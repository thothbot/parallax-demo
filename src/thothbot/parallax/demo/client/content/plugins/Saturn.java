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

package thothbot.parallax.demo.client.content.plugins;

import thothbot.parallax.core.client.controls.TrackballControls;
import thothbot.parallax.core.client.events.AnimationReadyEvent;
import thothbot.parallax.core.client.gl2.enums.PixelFormat;
import thothbot.parallax.core.client.shaders.CubeShader;
import thothbot.parallax.core.client.textures.CubeTexture;
import thothbot.parallax.core.client.textures.Texture;
import thothbot.parallax.core.shared.cameras.PerspectiveCamera;
import thothbot.parallax.core.shared.geometries.BoxGeometry;
import thothbot.parallax.core.shared.geometries.RingGeometry;
import thothbot.parallax.core.shared.geometries.SphereGeometry;
import thothbot.parallax.core.shared.lights.DirectionalLight;
import thothbot.parallax.core.shared.materials.Material;
import thothbot.parallax.core.shared.materials.MeshLambertMaterial;
import thothbot.parallax.core.shared.materials.MeshPhongMaterial;
import thothbot.parallax.core.shared.materials.ShaderMaterial;
import thothbot.parallax.core.shared.math.Mathematics;
import thothbot.parallax.core.shared.objects.Mesh;
import thothbot.parallax.core.shared.scenes.Scene;
import thothbot.parallax.demo.client.ContentWidget;
import thothbot.parallax.demo.client.Demo;
import thothbot.parallax.demo.client.DemoAnnotations.DemoSource;

import com.google.gwt.core.client.Duration;
import com.google.gwt.core.client.GWT;
import com.google.gwt.core.client.RunAsyncCallback;
import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.event.dom.client.MouseDownEvent;
import com.google.gwt.event.dom.client.MouseDownHandler;
import com.google.gwt.event.dom.client.MouseMoveEvent;
import com.google.gwt.event.dom.client.MouseMoveHandler;
import com.google.gwt.resources.client.ImageResource;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.InlineLabel;

public class Saturn extends ContentWidget
{
	/*
	 * Prepare Rendering Scene
	 */
	@DemoSource
	class DemoScene extends DemoAnimatedScene 
	{
		private static final String skyboxTextures = "./static/textures/cube/milkyway/*.jpg";
		private static final String saturnTextures = "./static/textures/planets/saturn.jpg";
		private static final String saturnRingsTextures = "./static/textures/planets/saturnRings.png";
		private static final String saturnCloudsTextures = "./static/textures/planets/saturnClouds.png";
		
		private static final double saturnRadius = 120.536;
		private static final double saturnRotationSpeed = 0.02;
		private static final double cloudsScale = 1.005;
		private static final double titanScale = 0.23;
		private static final double dioneScale = 0.13;
		private static final double rheaScale = 0.15;
		
		PerspectiveCamera camera;
		PerspectiveCamera cameraCube;

		Scene sceneCube;
		
		Mesh meshSaturn, meshClouds;
		
		private TrackballControls control;
		private double oldTime;
		
		@Override
		protected void onStart()
		{
			camera = new PerspectiveCamera(
					50, // fov
					getRenderer().getAbsoluteAspectRation(), // aspect 
					50, // near
					1e7 // far 
			);
			camera.getPosition().setZ(1000);
			
			this.control = new TrackballControls( camera, getCanvas() );
			this.control.setPanSpeed(0.2);
			this.control.setDynamicDampingFactor(0.3);
			
			// Sky box
			cameraCube = new PerspectiveCamera( 60, getRenderer().getAbsoluteWidth() / getRenderer().getAbsoluteHeight(), 1, 100000 );
			sceneCube = new Scene();
			
			CubeTexture textureCube = new CubeTexture( skyboxTextures );
			textureCube.setFormat(PixelFormat.RGB);
			
			CubeShader shaderCube = new CubeShader();
			shaderCube.getUniforms().get("tCube").setValue(textureCube);

			ShaderMaterial sMaterial = new ShaderMaterial(shaderCube);
			sMaterial.setSide(Material.SIDE.BACK);

			Mesh mesh = new Mesh( new BoxGeometry( 100000, 100000, 100000 ), sMaterial );
			sceneCube.add( mesh );
			
			// Saturn
			MeshPhongMaterial materialSaturn = new MeshPhongMaterial();
			materialSaturn.setMap(new Texture(saturnTextures));
			materialSaturn.setShininess(15.0);
//		    ambient: 0x000000,
//		    specular: 0x333333,

			SphereGeometry saturnGeometry = new SphereGeometry( saturnRadius, 100, 50 );
			meshSaturn = new Mesh( saturnGeometry, materialSaturn );
			meshSaturn.getRotation().setY( 0 );
			meshSaturn.getRotation().setZ( Mathematics.degToRad(-50.51) ) ;
			meshSaturn.setCastShadow(true);	
			meshSaturn.setReceiveShadow(true);	
			getScene().add( meshSaturn );
			
			// Clouds
			MeshLambertMaterial materialClouds = new MeshLambertMaterial();
			materialClouds.setTransparent(true);
			materialClouds.setMap(new Texture(saturnCloudsTextures));
	        
			meshClouds = new Mesh( saturnGeometry, materialClouds );
			meshClouds.getScale().set( cloudsScale );
			meshClouds.getRotation().setY( 0 );
			meshClouds.getRotation().setZ( Mathematics.degToRad(-50.51) ) ;
			getScene().add( meshClouds );
			
			// Saturn Rings
			MeshLambertMaterial materialRings = new MeshLambertMaterial();
			materialRings.setTransparent(true);
			materialRings.setMap(new Texture(saturnRingsTextures));
			materialRings.setSide(Material.SIDE.DOUBLE);

			Mesh saturnRings = new Mesh( new RingGeometry( saturnRadius, 265.882 , 20, 5, 0, Math.PI * 2 ), materialRings );
			saturnRings.getRotation().setX( Mathematics.degToRad(-70) );
			saturnRings.getRotation().setY( Mathematics.degToRad( 50.51 ));
			saturnRings.setCastShadow(true);	
			saturnRings.setReceiveShadow(true);	
			getScene().add(saturnRings);

			// Sun
			DirectionalLight dirLight = new DirectionalLight( 0xffffff );
			dirLight.getPosition().set(100 ,-200, 600 );
			dirLight.setCastShadow(true);
			dirLight.setShadowMapWidth( 2048 );
			dirLight.setShadowMapHeight( 2048 );
			dirLight.setShadowCameraNear( 1 );
			dirLight.setShadowCameraFar( 1500 );
			dirLight.setShadowBias( -0.005 ); 
			dirLight.setShadowDarkness( 0.35 );		
		//	dirLight.shadowCameraVisible = true;
			
		    getScene().add( dirLight );
		    
			getRenderer().setAutoClear(false);

		    this.oldTime = Duration.currentTimeMillis();

		}
		
		@Override
		protected void onUpdate(double duration)
		{
			double delta = (Duration.currentTimeMillis() - this.oldTime) * 0.001;
			
			meshSaturn.getRotation().addY( saturnRotationSpeed * delta );
			meshClouds.getRotation().addY( 1.25 * saturnRotationSpeed * delta );
			
			this.control.update();
			
			cameraCube.getRotation().copy( camera.getRotation() );

			getRenderer().clear();
			getRenderer().render( sceneCube, cameraCube );
			getRenderer().render( getScene(), camera );
			
			this.oldTime = Duration.currentTimeMillis();

		}
	}
		
	public Saturn() 
	{
		super("Saturn", "By Dejan Ristic (dejanristic@gmail.com)");
	}
	
	@Override
	public DemoScene onInitialize()
	{
		return new DemoScene();
	}

	@Override
	public ImageResource getIcon()
	{
		return Demo.resources.exampleCube();
	}
	
	@Override
	protected void asyncOnInitialize(final AsyncCallback<DemoAnimatedScene> callback)
	{
		GWT.runAsync(Saturn.class, new RunAsyncCallback() 
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
