/*
 * Copyright 2012 Alex Usachev, thothbot@gmail.com
 * 
 * This file is part of Parallax project.
 * 
 * Parallax is free software: you can redistribute it and/or modify it 
 * under the terms of the Creative Commons Attribution 3.0 Unported License.
 * 
 * Parallax is distributed in the hope that it will be useful, but 
 * WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY 
 * or FITNESS FOR A PARTICULAR PURPOSE. See the Creative Commons Attribution 
 * 3.0 Unported License. for more details.
 * 
 * You should have received a copy of the the Creative Commons Attribution 
 * 3.0 Unported License along with Parallax. 
 * If not, see http://creativecommons.org/licenses/by/3.0/.
 */

package org.parallax3d.parallax.demo.content.geometries;

import org.parallax3d.parallax.graphics.cameras.PerspectiveCamera;
import org.parallax3d.parallax.graphics.core.Geometry;
import org.parallax3d.parallax.graphics.extras.geometries.PlaneGeometry;
import org.parallax3d.parallax.graphics.materials.MeshBasicMaterial;
import org.parallax3d.parallax.graphics.objects.Mesh;
import org.parallax3d.parallax.graphics.scenes.FogExp2;
import org.parallax3d.parallax.graphics.textures.Texture;
import org.parallax3d.parallax.math.Matrix4;
import org.parallax3d.parallax.system.Duration;
import org.parallax3d.parallax.system.gl.enums.TextureWrapMode;

public class GeometryDynamic extends ContentWidget
{
	/*
	 * Prepare Rendering Scene
	 */
	@DemoSource
	class DemoScene extends DemoAnimatedScene 
	{
		private static final String texture = "./static/textures/water.jpg";
		
		PerspectiveCamera camera;
		
		FirstPersonControls controls;
		PlaneGeometry geometry;
		Mesh mesh;
		
		int worldWidth = 32;
		int worldDepth = 32;
	
		private double oldTime;
		
		@Override
		protected void onStart()
		{
			camera = new PerspectiveCamera(
					60, // fov
					getRenderer().getAbsoluteAspectRation(), // aspect 
					1, // near
					20000 // far 
			);
			
			camera.getPosition().setY(200);

			getScene().setFog(new FogExp2( 0xAACCFF, 0.0007 ));

			this.controls = new FirstPersonControls( camera, getCanvas() );
			controls.setMovementSpeed(500);
			controls.setLookSpeed(0.1);

			this.geometry = new PlaneGeometry( 20000, 20000, worldWidth - 1, worldDepth - 1 );
			this.geometry.applyMatrix(new Matrix4().makeRotationX( - Math.PI / 2.0 ));

			for ( int i = 0, il = this.geometry.getVertices().size(); i < il; i ++ )
				this.geometry.getVertices().get( i ).setY(35.0 * Math.sin( i/2.0 ));

			this.geometry.computeFaceNormals();
			this.geometry.computeVertexNormals();

			Texture texture = new Texture(DemoScene.texture);
			texture.setWrapS(TextureWrapMode.REPEAT);
			texture.setWrapT(TextureWrapMode.REPEAT);
			texture.getRepeat().set( 5.0, 5.0 );

			MeshBasicMaterial material = new MeshBasicMaterial();
			material.setColor( new Color(0x0044ff) );
			material.setMap( texture );

			this.mesh = new Mesh( this.geometry, material );
			getScene().add( this.mesh );
			
			this.oldTime = Duration.currentTimeMillis();
		}
		
		@Override
		protected void onUpdate(double duration)
		{			
			for ( int i = 0, l = this.geometry.getVertices().size(); i < l; i ++ )
				this.geometry.getVertices().get( i ).setY(35.0 * Math.sin( i / 5.0 + ( duration * 0.01 + i ) / 7.0 ));
		
			((Geometry)this.mesh.getGeometry()).setVerticesNeedUpdate( true );
			
			this.controls.update( (Duration.currentTimeMillis() - this.oldTime) * 0.001);

			this.oldTime = Duration.currentTimeMillis();
			
			getRenderer().render(getScene(), camera);
		}
	}

	public GeometryDynamic()
	{
		super("Vertices moving", "Here are shown vertices moving on single surface and using dense fog. (left click: forward, right click: backward). This example based on the three.js example.");
	}
	
	@Override
	protected void loadRenderingPanelAttributes(RenderingPanel renderingPanel) 
	{
		super.loadRenderingPanelAttributes(renderingPanel);
		renderingPanel.setBackground(0xaaccff);
	}

	@Override
	public DemoScene onInitialize()
	{
		return new DemoScene();
	}
	
	@Override
	protected void asyncOnInitialize(final AsyncCallback<DemoAnimatedScene> callback)
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
