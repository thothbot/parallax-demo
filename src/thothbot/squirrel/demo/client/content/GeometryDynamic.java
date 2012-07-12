/*
 * Copyright 2012 Alex Usachev, thothbot@gmail.com
 * 
 * This file based on the JavaScript source file of the THREE.JS project, 
 * licensed under MIT License.
 * 
 * This file is part of Squirrel project.
 * 
 * Squirrel is free software: you can redistribute it and/or modify it 
 * under the terms of the GNU General Public License as published by the 
 * Free Software Foundation, either version 3 of the License, or (at your 
 * option) any later version.
 * 
 * Squirrel is distributed in the hope that it will be useful, but 
 * WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY 
 * or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License 
 * for more details.
 * 
 * You should have received a copy of the GNU General Public License along with 
 * Squirrel. If not, see http://www.gnu.org/licenses/.
 */

package thothbot.squirrel.demo.client.content;

import thothbot.squirrel.core.client.RenderingPanel;
import thothbot.squirrel.core.client.RenderingPanel.RenderPanelAttributes;
import thothbot.squirrel.core.client.controls.FirstPersonControl;
import thothbot.squirrel.core.client.gl2.enums.TextureWrapMode;
import thothbot.squirrel.core.client.textures.Texture;
import thothbot.squirrel.core.shared.cameras.PerspectiveCamera;
import thothbot.squirrel.core.shared.core.Color3f;
import thothbot.squirrel.core.shared.geometries.Plane;
import thothbot.squirrel.core.shared.materials.MeshBasicMaterial;
import thothbot.squirrel.core.shared.objects.Mesh;
import thothbot.squirrel.core.shared.scenes.FogExp2;
import thothbot.squirrel.core.shared.utils.ImageUtils;
import thothbot.squirrel.demo.client.ContentWidget;
import thothbot.squirrel.demo.client.Demo;
import thothbot.squirrel.demo.client.DemoAnnotations.DemoSource;

import com.google.gwt.core.client.Duration;
import com.google.gwt.core.client.GWT;
import com.google.gwt.core.client.RunAsyncCallback;
import com.google.gwt.resources.client.ClientBundle;
import com.google.gwt.resources.client.ImageResource;
import com.google.gwt.user.client.rpc.AsyncCallback;

public class GeometryDynamic extends ContentWidget
{

	/*
	 * Load texture
	 */
	@DemoSource
	public interface Resources extends ClientBundle
	{
		Resources INSTANCE = GWT.create(Resources.class);

		@Source("../../resources/textures/water.jpg")
		ImageResource texture();
	}

	/*
	 * Prepare Rendering Scene
	 */
	@DemoSource
	class DemoScene extends DemoRenderingScene 
	{
		FirstPersonControl controls;
		Plane geometry;
		Mesh mesh;
		
		int worldWidth = 128;
		int worldDepth = 128;
		int worldHalfWidth = worldWidth / 2;
		int worldHalfDepth = worldDepth / 2;
		
		private double oldTime;
		
		@Override
		protected void loadCamera()
		{
			setCamera(
					new PerspectiveCamera(
							60, // fov
							getRenderer().getCanvas().getAspectRation(), // aspect 
							1, // near
							20000 // far 
					)); 
		}

		@Override
		protected void onStart()
		{
			getCamera().getPosition().setY(200);
			getScene().addChild(getCamera());

			getScene().setFog(new FogExp2( 0xAACCFF, 0.0007f ));

			this.controls = new FirstPersonControl( getCamera(), getRenderer().getCanvas() );
			controls.setMovementSpeed(500f);
			controls.setLookSpeed(0.1f);

			this.geometry = new Plane( 20000, 20000, worldWidth - 1, worldDepth - 1 );
			this.geometry.dynamic = true;

			for ( int i = 0, il = this.geometry.getVertices().size(); i < il; i ++ )
				this.geometry.getVertices().get( i ).setY((float) (35.0 * Math.sin( i/2.0 )));

			this.geometry.computeFaceNormals(false);
			this.geometry.computeVertexNormals();

			Texture texture = ImageUtils.loadTexture(Resources.INSTANCE.texture(), null, null);
			texture.setWrapS(TextureWrapMode.REPEAT); 
			texture.setWrapT(TextureWrapMode.REPEAT);
			texture.repeat.set( 5.0f, 5.0f );

			MeshBasicMaterial.MeshBasicMaterialOptions options = new MeshBasicMaterial.MeshBasicMaterialOptions();
			options.color = new Color3f(0x0044ff);
			options.map = texture;
			MeshBasicMaterial material = new MeshBasicMaterial( options );

			this.mesh = new Mesh( this.geometry, material );
			getScene().addChild( this.mesh );
			
			this.oldTime = Duration.currentTimeMillis();
		}
		
		@Override
		protected void onStop()
		{			
		}
		
		@Override
		protected void onUpdate(double duration)
		{			
			for ( int i = 0, l = this.geometry.getVertices().size(); i < l; i ++ )
				this.geometry.getVertices().get( i ).setY((float) (35.0 * Math.sin( i / 5.0 + ( duration * 0.01 + i ) / 7.0 )));
		
			this.mesh.getGeometry().verticesNeedUpdate = true;
			
			this.controls.update( (float) ((Duration.currentTimeMillis() - this.oldTime) * 0.001) );

			this.oldTime = Duration.currentTimeMillis();
			super.onUpdate(duration);
		}
	}
		
	RenderingPanel renderingPanel;

	public GeometryDynamic()
	{
		super("Vertices moving", "Here are shown vertices moving on single surface and using dense fog. (left click: forward, right click: backward). This example bases on the three.js example.");
	}
		
	@Override
	public RenderPanelAttributes getRenderPanelAttributes()
	{
		RenderPanelAttributes att = super.getRenderPanelAttributes();
		att.clearColor         = 0xaaccff;
		
		return att;
	}
	
	@Override
	public DemoScene onInitialize()
	{
		return new DemoScene();
	}

	@Override
	public ImageResource getIcon()
	{
		return Demo.resources.exampleGeometryDinamic();
	}
	
	@Override
	protected void asyncOnInitialize(final AsyncCallback<DemoRenderingScene> callback)
	{
		GWT.runAsync(GeometryDynamic.class, new RunAsyncCallback() 
		{
			public void onFailure(Throwable caught)
			{
				callback.onFailure(caught);
			}

			public void onSuccess()
			{
				callback.onSuccess(onInitialize());
			}
		});
	}

}
