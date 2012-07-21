/*
 * Copyright 2012 Alex Usachev, thothbot@gmail.com
 * 
 * This file based on the JavaScript source file of the THREE.JS project, 
 * licensed under MIT License.
 * 
 * This file is part of Parallax project.
 * 
 * Parallax is free software: you can redistribute it and/or modify it 
 * under the terms of the GNU General Public License as published by the 
 * Free Software Foundation, either version 3 of the License, or (at your 
 * option) any later version.
 * 
 * Parallax is distributed in the hope that it will be useful, but 
 * WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY 
 * or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License 
 * for more details.
 * 
 * You should have received a copy of the GNU General Public License along with 
 * Parallax. If not, see http://www.gnu.org/licenses/.
 */

package thothbot.parallax.demo.client.content;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import thothbot.parallax.core.client.AnimationReadyEvent;
import thothbot.parallax.core.client.context.Canvas3d;
import thothbot.parallax.core.client.shader.ShaderCubeMap;
import thothbot.parallax.core.client.textures.CubeTexture;
import thothbot.parallax.core.client.textures.Texture;
import thothbot.parallax.core.shared.Log;
import thothbot.parallax.core.shared.cameras.PerspectiveCamera;
import thothbot.parallax.core.shared.core.Color3f;
import thothbot.parallax.core.shared.geometries.Cube;
import thothbot.parallax.core.shared.geometries.Sphere;
import thothbot.parallax.core.shared.materials.MeshBasicMaterial;
import thothbot.parallax.core.shared.materials.ShaderMaterial;
import thothbot.parallax.core.shared.objects.Mesh;
import thothbot.parallax.core.shared.scenes.Scene;
import thothbot.parallax.core.shared.utils.ImageUtils;
import thothbot.parallax.demo.client.ContentWidget;
import thothbot.parallax.demo.client.Demo;
import thothbot.parallax.demo.client.DemoAnnotations.DemoSource;

import com.google.gwt.core.client.GWT;
import com.google.gwt.core.client.RunAsyncCallback;
import com.google.gwt.event.dom.client.MouseMoveEvent;
import com.google.gwt.event.dom.client.MouseMoveHandler;
import com.google.gwt.resources.client.ClientBundle;
import com.google.gwt.resources.client.ImageResource;
import com.google.gwt.user.client.rpc.AsyncCallback;

public final class MaterialsCubemapBallsRefraction extends ContentWidget 
{
	/*
	 * Load texture
	 */
	@DemoSource
	public interface Resources extends ClientBundle
	{
		Resources INSTANCE = GWT.create(Resources.class);
		
		@Source("../../resources/textures/cube/skybox/px.jpg")
		ImageResource px();
		
		@Source("../../resources/textures/cube/skybox/nx.jpg")
		ImageResource nx();
		
		@Source("../../resources/textures/cube/skybox/py.jpg")
		ImageResource py();
		
		@Source("../../resources/textures/cube/skybox/ny.jpg")
		ImageResource ny();
		
		@Source("../../resources/textures/cube/skybox/pz.jpg")
		ImageResource pz();
				
		@Source("../../resources/textures/cube/skybox/nz.jpg")
		ImageResource nz();
	}

	/*
	 * Prepare Rendering Scene
	 */
	@DemoSource
	class DemoScene extends DemoAnimatedScene 
	{
		public int mouseX = 0;
		public int mouseY = 0;
		
		private List<Mesh> speres;
		
		private Scene sceneCube;
		private PerspectiveCamera cameraCube;
		
		@Override
		protected void loadCamera()
		{
			setCamera(
					new PerspectiveCamera(
							60, // fov
							getRenderer().getCanvas().getAspectRation(), // aspect 
							1, // near
							100000 // far 
					)); 
		}

		@Override
		protected void onStart()
		{
			getCamera().getPosition().setZ(3200);
			getScene().addChild(getCamera());
			
			this.sceneCube = new Scene();
			this.cameraCube = new PerspectiveCamera( 60, getRenderer().getCanvas().getAspectRation(), 1, 100000 );

			sceneCube.addChild( cameraCube );

			Sphere geometry = new Sphere( 100, 32, 16 );


			Resources r = Resources.INSTANCE;
			CubeTexture textureCube = ImageUtils.loadTextureCube( Arrays.asList(r.px(), r.nx(), r.py(), r.ny(), r.pz(), r.nz()), Texture.MAPPING_MODE.CUBE_REFRACTION );
			
			MeshBasicMaterial material = new MeshBasicMaterial();
			material.setColor( new Color3f(0xffffff) );
			material.setEnvMap( textureCube );
			material.setRefractionRatio( 0.95f );

			this.speres = new ArrayList<Mesh>();
			
			for ( int i = 0; i < 500; i ++ ) 
			{
				Mesh mesh = new Mesh( geometry, material );

				mesh.getPosition().setX( (float) (Math.random() * 10000.0 - 5000.0) );
				mesh.getPosition().setY( (float) (Math.random() * 10000.0 - 5000.0) );
				mesh.getPosition().setZ( (float) (Math.random() * 10000.0 - 5000.0) );

				float scale = (float) (Math.random() * 3.0 + 1.0);
				mesh.getScale().set(scale);

				getScene().addChild( mesh );

				this.speres.add( mesh );
			}

			// Skybox

			ShaderCubeMap shader = new ShaderCubeMap();
			shader.getUniforms().get("tCube").texture = textureCube; 

			ShaderMaterial sMaterial = new ShaderMaterial();
			sMaterial.setFragmentShaderSource( shader.getFragmentSource() );
			sMaterial.setVertexShaderSource( shader.getVertexSource() );
			sMaterial.setUniforms( shader.getUniforms() );
			sMaterial.setDepthWrite( false );
			
			Mesh mesh = new Mesh( new Cube( 100, 100, 100 ), sMaterial );
			mesh.setFlipSided(true);
			sceneCube.addChild( mesh );
			
			getRenderer().setAutoClear(false);
		}
		
		@Override
		protected void onStop()
		{			
		}
		
		@Override
		protected void onUpdate(double duration)
		{
			double timer = 0.0001 * duration;

			for ( int i = 0, il = this.speres.size(); i < il; i ++ ) 
			{
				this.speres.get(i).getPosition().setX( (float) (5000.0 * Math.cos( timer + i )) );
				this.speres.get(i).getPosition().setY( (float) (5000.0 * Math.sin( timer + i * 1.1 )) );
			}

			getCamera().getPosition().addX((float) (( mouseX - getCamera().getPosition().getX() ) * 0.05) );
			getCamera().getPosition().addY((float) (( - mouseY - getCamera().getPosition().getY() ) * 0.05) );

			getCamera().lookAt( getScene().getPosition() );
			this.cameraCube.getRotation().copy( getCamera().getRotation() );

			getRenderer().render( sceneCube, cameraCube );
		}
	}
		
	public MaterialsCubemapBallsRefraction() 
	{
		super("Cube refraction", "Drag mouse to move. This example based on the three.js example.");
	}
	
	@Override
	public void onAnimationReady(AnimationReadyEvent event)
	{
		super.onAnimationReady(event);

		this.renderingPanel.getRenderer().getCanvas().addMouseMoveHandler(new MouseMoveHandler() {
			@Override
			public void onMouseMove(MouseMoveEvent event)
			{
				DemoScene rs = (DemoScene) renderingPanel.getAnimatedScene();
				Canvas3d canvas = renderingPanel.getRenderer().getCanvas();
				rs.mouseX = (event.getX() - canvas.getWidth() / 2 ) * 10; 
				rs.mouseY = (event.getY() - canvas.getHeight() / 2) * 10;
			}
		});
	}
	
	@Override
	public DemoScene onInitialize()
	{
		Log.debug("Called onInitialize() class=" + this.getClass().getName());

		return new DemoScene();
	}

	@Override
	public ImageResource getIcon()
	{
		return Demo.resources.exampleMaterialsCubemapBallsRefraction();
	}
	
	@Override
	protected void asyncOnInitialize(final AsyncCallback<DemoAnimatedScene> callback)
	{
		GWT.runAsync(MaterialsCubemapBallsRefraction.class, new RunAsyncCallback() 
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
