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

package org.parallax3d.parallax.demo.client.content.materials;

import java.util.ArrayList;
import java.util.List;

import thothbot.parallax.core.client.events.AnimationReadyEvent;
import thothbot.parallax.core.client.shaders.CubeShader;
import thothbot.parallax.core.client.textures.CubeTexture;
import thothbot.parallax.core.client.textures.Texture.MAPPING_MODE;
import thothbot.parallax.core.shared.Log;
import thothbot.parallax.core.shared.cameras.PerspectiveCamera;
import thothbot.parallax.core.shared.geometries.BoxGeometry;
import thothbot.parallax.core.shared.geometries.SphereGeometry;
import thothbot.parallax.core.shared.materials.Material;
import thothbot.parallax.core.shared.materials.MeshBasicMaterial;
import thothbot.parallax.core.shared.materials.ShaderMaterial;
import thothbot.parallax.core.shared.math.Color;
import thothbot.parallax.core.shared.objects.Mesh;
import thothbot.parallax.core.shared.scenes.Scene;
import org.parallax3d.parallax.demo.client.ContentWidget;
import org.parallax3d.parallax.demo.client.DemoAnnotations.DemoSource;

import com.google.gwt.core.client.GWT;
import com.google.gwt.core.client.RunAsyncCallback;
import com.google.gwt.event.dom.client.MouseMoveEvent;
import com.google.gwt.event.dom.client.MouseMoveHandler;
import com.google.gwt.user.client.rpc.AsyncCallback;

public final class MaterialsCubemapBallsRefraction extends ContentWidget 
{
	/*
	 * Prepare Rendering Scene
	 */
	@DemoSource
	class DemoScene extends DemoAnimatedScene 
	{
		private static final String textures = "./static/textures/cube/skybox/*.jpg";
		
		PerspectiveCamera camera;
		
		public int mouseX = 0;
		public int mouseY = 0;
		
		private List<Mesh> speres;
		
		private Scene sceneCube;
		private PerspectiveCamera cameraCube;

		@Override
		protected void onStart()
		{
			camera = new PerspectiveCamera(
					60, // fov
					getRenderer().getAbsoluteAspectRation(), // aspect 
					1, // near
					100000 // far 
			); 
			
			camera.getPosition().setZ(3200);
			
			this.cameraCube = new PerspectiveCamera(
					60, // fov
					getRenderer().getAbsoluteAspectRation(), // aspect 
					1, // near
					100000 // far 
			);
			
			this.sceneCube = new Scene();

			SphereGeometry geometry = new SphereGeometry( 100, 32, 16 );

			CubeTexture textureCube = new CubeTexture( textures );
			textureCube.setMapping(MAPPING_MODE.CUBE_REFRACTION);
//			
			MeshBasicMaterial material = new MeshBasicMaterial();
			material.setColor( new Color(0xffffff) );
			material.setEnvMap( textureCube );
			material.setRefractionRatio( 0.95 );

			this.speres = new ArrayList<Mesh>();
			
			for ( int i = 0; i < 500; i ++ ) 
			{
				Mesh mesh = new Mesh( geometry, material );

				mesh.getPosition().setX( Math.random() * 10000.0 - 5000.0 );
				mesh.getPosition().setY( Math.random() * 10000.0 - 5000.0 );
				mesh.getPosition().setZ( Math.random() * 10000.0 - 5000.0 );

				double scale = Math.random() * 3.0 + 1.0;
				mesh.getScale().set(scale);

				getScene().add( mesh );

				this.speres.add( mesh );
			}

			// Skybox
			ShaderMaterial sMaterial = new ShaderMaterial(new CubeShader());
			sMaterial.getShader().getUniforms().get("tCube").setValue( textureCube ); 
			sMaterial.setDepthWrite( false );
			sMaterial.setSide( Material.SIDE.BACK );
			
			Mesh mesh = new Mesh( new BoxGeometry( 100, 100, 100 ), sMaterial );
			sceneCube.add( mesh );
			
			getRenderer().setAutoClear(false);
		}
		
		@Override
		protected void onUpdate(double duration)
		{
			double timer = 0.0001 * duration;

			for ( int i = 0, il = this.speres.size(); i < il; i ++ ) 
			{
				this.speres.get(i).getPosition().setX(5000.0 * Math.cos( timer + i ) );
				this.speres.get(i).getPosition().setY(5000.0 * Math.sin( timer + i * 1.1 ) );
			}

			camera.getPosition().addX(( mouseX - camera.getPosition().getX() ) * 0.05 );
			camera.getPosition().addY(( - mouseY - camera.getPosition().getY() ) * 0.05 );

			camera.lookAt( getScene().getPosition() );
			this.cameraCube.getRotation().copy( camera.getRotation() );

			getRenderer().render( sceneCube, cameraCube );
			getRenderer().render(getScene(), camera);
		}
	}
		
	public MaterialsCubemapBallsRefraction() 
	{
		super("Cube map refraction", "Drag mouse to move. This example based on the three.js example.");
	}
	
	@Override
	public void onAnimationReady(AnimationReadyEvent event)
	{
		super.onAnimationReady(event);

		this.renderingPanel.getCanvas().addMouseMoveHandler(new MouseMoveHandler() {
			@Override
			public void onMouseMove(MouseMoveEvent event)
			{
				DemoScene rs = (DemoScene) renderingPanel.getAnimatedScene();

				rs.mouseX = (event.getX() - renderingPanel.getRenderer().getAbsoluteWidth() / 2 ) * 10; 
				rs.mouseY = (event.getY() - renderingPanel.getRenderer().getAbsoluteHeight() / 2) * 10;
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
