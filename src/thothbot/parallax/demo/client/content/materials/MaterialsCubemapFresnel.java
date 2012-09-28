/*
 * Copyright 2012 Alex Usachev, thothbot@gmail.com
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

package thothbot.parallax.demo.client.content.materials;

import java.util.ArrayList;
import java.util.List;

import thothbot.parallax.core.client.AnimationReadyEvent;
import thothbot.parallax.core.client.context.Canvas3d;
import thothbot.parallax.core.client.gl2.enums.PixelFormat;
import thothbot.parallax.core.client.shaders.CubeMapShader;
import thothbot.parallax.core.client.shaders.FresnelShader;
import thothbot.parallax.core.client.textures.CubeTexture;
import thothbot.parallax.core.shared.cameras.PerspectiveCamera;
import thothbot.parallax.core.shared.geometries.CubeGeometry;
import thothbot.parallax.core.shared.geometries.SphereGeometry;
import thothbot.parallax.core.shared.materials.Material;
import thothbot.parallax.core.shared.materials.ShaderMaterial;
import thothbot.parallax.core.shared.objects.Mesh;
import thothbot.parallax.core.shared.scenes.Scene;
import thothbot.parallax.demo.client.ContentWidget;
import thothbot.parallax.demo.client.Demo;
import thothbot.parallax.demo.client.DemoAnnotations.DemoSource;

import com.google.gwt.core.client.GWT;
import com.google.gwt.core.client.RunAsyncCallback;
import com.google.gwt.event.dom.client.MouseMoveEvent;
import com.google.gwt.event.dom.client.MouseMoveHandler;
import com.google.gwt.resources.client.ImageResource;
import com.google.gwt.user.client.rpc.AsyncCallback;

public final class MaterialsCubemapFresnel extends ContentWidget 
{

	/*
	 * Prepare Rendering Scene
	 */
	@DemoSource
	class DemoScene extends DemoAnimatedScene 
	{
		private static final String textures = "./static/textures/cube/park2/*.jpg";
		
		private Scene sceneCube;
		private PerspectiveCamera cameraCube;
		
		List<Mesh> spheres;
		
		int mouseX = 0, mouseY = 0;
		
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
			
			this.cameraCube = new PerspectiveCamera(
					60, // fov
					getRenderer().getCanvas().getAspectRation(), // aspect 
					1, // near
					100000 // far
				);
		}
		
		@Override
		protected void onResize()
		{
			super.onResize();
			this.cameraCube.setAspectRatio(getRenderer().getCanvas().getAspectRation());
		}

		@Override
		protected void onStart()
		{
			getCamera().getPosition().setZ(3200);
			getScene().add(getCamera());

			this.sceneCube = new Scene();
			sceneCube.add( cameraCube );

			SphereGeometry geometry = new SphereGeometry( 100, 32, 16 );

			CubeTexture textureCube = new CubeTexture( textures );
			textureCube.setFormat(PixelFormat.RGB);

			FresnelShader shader = new FresnelShader();
			shader.getUniforms().get("tCube").setValue(textureCube);

			ShaderMaterial material = new ShaderMaterial( shader );

			spheres = new ArrayList<Mesh>();
			for ( int i = 0; i < 500; i ++ ) 
			{
				Mesh mesh = new Mesh( geometry, material );

				mesh.getPosition().setX( Math.random() * 10000 - 5000 );
				mesh.getPosition().setY( Math.random() * 10000 - 5000 );
				mesh.getPosition().setZ( Math.random() * 10000 - 5000 );

				mesh.getScale().set( Math.random() * 3 + 1 );

				getScene().add( mesh );

				spheres.add( mesh );
			}

			getScene().setMatrixAutoUpdate(false);

			// Skybox
			CubeMapShader shaderCube = new CubeMapShader();
			shaderCube.getUniforms().get("tCube").setValue(textureCube);

			ShaderMaterial sMaterial = new ShaderMaterial(shaderCube);
			sMaterial.setSide(Material.SIDE.BACK);

			Mesh mesh = new Mesh( new CubeGeometry( 100000, 100000, 100000 ), sMaterial );
			sceneCube.add( mesh );

			//

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

			getCamera().getPosition().addX( ( mouseX - getCamera().getPosition().getX() ) * .05 );
			getCamera().getPosition().addY( ( - mouseY - getCamera().getPosition().getY() ) * .05 );

			getCamera().lookAt( getScene().getPosition() );

			cameraCube.getRotation().copy( getCamera().getRotation() );

			for ( int i = 0, il = spheres.size(); i < il; i ++ ) 
			{
				Mesh sphere = spheres.get( i );

				sphere.getPosition().setX( 5000 * Math.cos( timer + i ) );
				sphere.getPosition().setY( 5000 * Math.sin( timer + i * 1.1 ) );
			}

			getRenderer().clear();
			getRenderer().render( sceneCube, cameraCube );
		}
	}
		
	public MaterialsCubemapFresnel() 
	{
		super("Cube map Fresnel shader", "This example based on the three.js example.");
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
		return new DemoScene();
	}

	@Override
	public ImageResource getIcon()
	{
		return Demo.resources.exampleMaterialsCubemapFresnel();
	}
	
	@Override
	protected void asyncOnInitialize(final AsyncCallback<DemoAnimatedScene> callback)
	{
		GWT.runAsync(MaterialsCubemapFresnel.class, new RunAsyncCallback() 
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
