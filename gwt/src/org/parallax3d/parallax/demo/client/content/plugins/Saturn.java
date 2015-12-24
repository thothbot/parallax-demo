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

package org.parallax3d.parallax.demo.client.content.plugins;

import thothbot.parallax.core.client.events.AnimationReadyEvent;
import thothbot.parallax.core.client.gl2.enums.TextureMinFilter;
import thothbot.parallax.core.client.renderers.ShadowMap;
import thothbot.parallax.core.client.textures.Texture;
import thothbot.parallax.core.shared.cameras.CubeCamera;
import thothbot.parallax.core.shared.cameras.PerspectiveCamera;
import thothbot.parallax.core.shared.geometries.RingGeometry;
import thothbot.parallax.core.shared.geometries.SphereGeometry;
import thothbot.parallax.core.shared.lights.DirectionalLight;
import thothbot.parallax.core.shared.materials.Material;
import thothbot.parallax.core.shared.materials.MeshBasicMaterial;
import thothbot.parallax.core.shared.materials.MeshLambertMaterial;
import thothbot.parallax.core.shared.materials.MeshPhongMaterial;
import thothbot.parallax.core.shared.math.Color;
import thothbot.parallax.core.shared.math.Mathematics;
import thothbot.parallax.core.shared.objects.Group;
import thothbot.parallax.core.shared.objects.Mesh;
import org.parallax3d.parallax.demo.client.ContentWidget;
import org.parallax3d.parallax.demo.client.DemoAnnotations.DemoSource;
import thothbot.parallax.plugins.lensflare.LensFlare;
import thothbot.parallax.plugins.lensflare.LensFlare.LensSprite;
import thothbot.parallax.plugins.lensflare.LensFlarePlugin;

import com.google.gwt.core.client.Duration;
import com.google.gwt.core.client.GWT;
import com.google.gwt.core.client.RunAsyncCallback;
import com.google.gwt.dom.client.AudioElement;
import com.google.gwt.media.client.Audio;
import com.google.gwt.user.client.rpc.AsyncCallback;

public class Saturn extends ContentWidget
{
	/*
	 * Prepare Rendering Scene
	 */
	@DemoSource
	class DemoScene extends DemoAnimatedScene 
	{
		private static final String skyboxTextures = "./static/textures/stars_skybox.jpg";
		
		private static final String saturnTextures = "./static/textures/planets/saturn.jpg";
		private static final String saturnRingsTextures = "./static/textures/planets/saturnRings.png";
		private static final String saturnCloudsTextures = "./static/textures/planets/saturnClouds.png";
		
		private Texture titanTexture = new Texture( "./static/textures/planets/titan.jpg" );
		private Texture moonTexture = new Texture( "./static/textures/planets/moon_1024.jpg" );
		
		private Texture textureFlare0 = new Texture( "./static/textures/lensflare/lensflare0.png" );
		private Texture textureFlare2 = new Texture( "./static/textures/lensflare/lensflare2.png" );
		private Texture textureFlare3 = new Texture( "./static/textures/lensflare/lensflare3.png" );
		
		private static final double saturnRadius = 120.536;
		private static final double saturnRingsRadius = 265.882;
		private static final double saturnTitle = 5.51 * -5;
		private static final double saturnRotationSpeed = 0.02;
		private static final double cloudsScale = 1.005;
		private static final double titanScale = 0.23;
		private static final double dioneScale = 0.13;
		private static final double rheaScale = 0.15;
		
		PerspectiveCamera camera, cameraRings, cameraMain;
		CubeCamera cubeCamera;
		
		Mesh meshSaturn, meshClouds, meshTitan, meshDione, meshRhea;
		
		double oldTime;
		double theta = 0;
		
		Audio audio = Audio.createIfSupported();
		
		@Override
		protected void onStart()
		{
			cameraMain = new PerspectiveCamera(
					50, // fov
					getRenderer().getAbsoluteAspectRation(), // aspect 
					50, // near
					1e7 // far 
			);

			cameraMain.getPosition().setY(500);
			
			cameraRings = cameraMain.clone();
			cameraRings.getPosition().setX(50);
			
			// Sky box
			MeshBasicMaterial sMaterial = new MeshBasicMaterial();
			sMaterial.setMap(new Texture(skyboxTextures));

			Mesh mesh = new Mesh( new SphereGeometry( 900, 60, 40 ), sMaterial );
			mesh.getScale().setX( -1 );
			getScene().add( mesh );
			
			cubeCamera = new CubeCamera( 1, 1000, 256 );
			cubeCamera.getRenderTarget().setMinFilter(TextureMinFilter.LINEAR_MIPMAP_LINEAR);
			getScene().add( cubeCamera );
			
			Group saturn = new Group();

			// Saturn
			MeshPhongMaterial materialSaturn = new MeshPhongMaterial();
			Texture saturnTexture = new Texture(saturnTextures);
			materialSaturn.setMap(saturnTexture);
			materialSaturn.setShininess(15.0);
			materialSaturn.setAmbient(new Color(0x000000));
			materialSaturn.setSpecular(new Color(0x333333));
			
			SphereGeometry saturnGeometry = new SphereGeometry( saturnRadius, 100, 50 );
			meshSaturn = new Mesh( saturnGeometry, materialSaturn );
			meshSaturn.setCastShadow(true);	
			meshSaturn.setReceiveShadow(true);	
			saturn.add( meshSaturn );
			
			// Clouds
			MeshLambertMaterial materialClouds = new MeshLambertMaterial();
			materialClouds.setTransparent(true);
			materialClouds.setMap(new Texture(saturnCloudsTextures));
	        
			meshClouds = new Mesh( saturnGeometry, materialClouds );
			meshClouds.getScale().set( cloudsScale );
			meshClouds.getRotation().setZ( Mathematics.degToRad(180) );
			saturn.add( meshClouds );
			
			// Saturn Rings
			MeshLambertMaterial materialRings = new MeshLambertMaterial();
			materialRings.setTransparent(true);
			materialRings.setMap(new Texture(saturnRingsTextures));
			materialRings.setSide(Material.SIDE.DOUBLE);

			Mesh saturnRings = new Mesh( new RingGeometry( saturnRadius, saturnRingsRadius , 20, 5, 0, Math.PI * 2 ), materialRings );
			saturnRings.getRotation().setX( Mathematics.degToRad(90) );
			saturnRings.setCastShadow(true);	
			saturnRings.setReceiveShadow(true);
			saturn.add(saturnRings);

			saturn.getRotation().setZ( Mathematics.degToRad(saturnTitle) ) ;
			getScene().add(saturn);

			// Moons
			
			MeshPhongMaterial materialTitan = new MeshPhongMaterial();
			materialTitan.setMap(titanTexture);
			
			meshTitan = new Mesh( saturnGeometry, materialTitan );
			meshTitan.getPosition().set( saturnRadius * 10, 0, 0 );
			meshTitan.getScale().set( titanScale );
			getScene().add( meshTitan );

			MeshPhongMaterial materialDione = new MeshPhongMaterial();
			materialDione.setMap(moonTexture);
			
			meshDione = new Mesh( saturnGeometry, materialDione );
			meshDione.getPosition().set( saturnRadius * 2.5, 0, 0 );
			meshDione.getScale().set( dioneScale );
			meshDione.setCastShadow(true);
			getScene().add( meshDione );
			
			MeshPhongMaterial materialRhea = new MeshPhongMaterial();
			materialRhea.setMap(moonTexture);

			meshRhea = new Mesh( saturnGeometry, materialRhea );
			meshRhea.getPosition().set( saturnRadius * 5, 0, 0 );
			meshRhea.getScale().set( rheaScale );
			meshRhea.setCastShadow(true);
			getScene().add( meshRhea );

		    // Sun
		    new LensFlarePlugin(getRenderer(), getScene());
		    addSun( 0.985, 0.5, 0.850, 800, - saturnRadius * 0.8, 0 );
		    	    
		    new ShadowMap(getRenderer(), getScene());
						
			getRenderer().setGammaInput(true);
			getRenderer().setGammaOutput(true);
			getRenderer().setClearColor(0xffffff);

		    this.oldTime = Duration.currentTimeMillis();

		}
		
		private void addSun( double h, double s, double l, double x, double y, double z ) 
		{
			DirectionalLight dirLight = new DirectionalLight( 0xffffff );
			dirLight.getPosition().set(x, y, z );
			dirLight.setCastShadow(true);
			dirLight.setShadowMapWidth( 2048 );
			dirLight.setShadowMapHeight( 2048 );
			dirLight.setShadowCameraNear( 1 );
			dirLight.setShadowCameraFar( 1500 );
			dirLight.setShadowBias( -0.005 ); 
			dirLight.setShadowDarkness( 0.85 );		
//			dirLight.shadowCameraVisible = true;

		    getScene().add( dirLight );
		    
			Color flareColor = new Color( 0xffffff );
			flareColor.setHSL( h, s, l + 0.5 );
			
			final LensFlare lensFlare = new LensFlare( textureFlare0, 700, 0.0, Material.BLENDING.ADDITIVE, flareColor );

			lensFlare.add( textureFlare2, 512, 0.0, Material.BLENDING.ADDITIVE );
			lensFlare.add( textureFlare2, 512, 0.0, Material.BLENDING.ADDITIVE );
			lensFlare.add( textureFlare2, 512, 0.0, Material.BLENDING.ADDITIVE );

			lensFlare.add( textureFlare3, 60,  0.6, Material.BLENDING.ADDITIVE );
			lensFlare.add( textureFlare3, 70,  0.7, Material.BLENDING.ADDITIVE );
			lensFlare.add( textureFlare3, 120, 0.9, Material.BLENDING.ADDITIVE );
			lensFlare.add( textureFlare3, 70,  1.0, Material.BLENDING.ADDITIVE );

			lensFlare.setUpdateCallback(new LensFlare.Callback() {

				@Override
				public void update() {

					double vecX = -lensFlare.getPositionScreen().getX() * 2.0;
					double vecY = -lensFlare.getPositionScreen().getY() * 2.0;

					for( int f = 0; f < lensFlare.getLensFlares().size(); f++ ) 
					{
						LensSprite flare = lensFlare.getLensFlares().get( f );

						flare.x = lensFlare.getPositionScreen().getX() + vecX * flare.distance;
						flare.y = lensFlare.getPositionScreen().getY() + vecY * flare.distance;

						flare.rotation = 0;
					}

					lensFlare.getLensFlares().get( 2 ).y += 0.025;
					lensFlare.getLensFlares().get( 3 ).rotation = lensFlare.getPositionScreen().getX() * 0.5 + Mathematics.degToRad(45.0);
				}
			});

			lensFlare.getPosition().copy(dirLight.getPosition());

			getScene().add( lensFlare );
		}
		
		@Override
		protected void onUpdate(double duration)
		{
			if(audio.isPaused())
				audio.play();

			double delta = (Duration.currentTimeMillis() - this.oldTime) * 0.001;
			
			if(duration < 24.8 * 1000 || ( duration > 70 * 1000 && duration < 82 * 1000 ) ) 
			{
				theta += 0.07;

				cameraRings.getPosition().setY( (saturnRingsRadius + 10 ) * Math.cos( Mathematics.degToRad( theta ) ) );         
				cameraRings.getPosition().setZ( -saturnRingsRadius +(saturnRingsRadius + 10 ) * Math.sin( Mathematics.degToRad( theta ) ) );
				cameraRings.lookAt( meshSaturn.getPosition() );
				
				camera = cameraRings;
			} 
			else 
			{
				theta = 0;

				cameraMain.getPosition().setX( saturnRadius * 8 * Math.cos( 0.00005 * duration ) );         
				cameraMain.getPosition().setZ( saturnRadius * 9 * Math.sin( 0.00005 * duration ) );
				cameraMain.lookAt( meshSaturn.getPosition() );
				
				camera = cameraMain;
			}

			meshSaturn.getRotation().addY( saturnRotationSpeed * delta );
			meshClouds.getRotation().addY( -3.2 * saturnRotationSpeed * delta );
			
			meshDione.getPosition().setX( saturnRadius * 2.5 * Math.cos( 0.0003 * duration ) );         
			meshDione.getPosition().setZ( saturnRadius * 2.5 * Math.sin( 0.0003 * duration ) );
			
			meshRhea.getPosition().setX( saturnRadius * 5 * Math.cos( 0.0001 * duration ) );         
			meshRhea.getPosition().setZ( saturnRadius * 5 * Math.sin( 0.0001 * duration ) );
			
			meshTitan.getPosition().setX( saturnRadius * 7 * Math.cos( 0.00007 * duration ) );         
			meshTitan.getPosition().setZ( saturnRadius * 7 * Math.sin( 0.00007 * duration ) );
					
		    cubeCamera.updateCubeMap( getRenderer(), getScene() );

			getRenderer().render( getScene(), camera );
			
			this.oldTime = Duration.currentTimeMillis();

		}
		
		@Override
		protected void onStop() 
		{
			super.onStop();
			audio.pause();
		}
	}
		
	public Saturn() 
	{
		super("Saturn", "Design and development by <b>Dejan Ristic</b> (<a href=\'mailto:dejanristic85@gmail.com\'>dejanristic85@gmail.com</a>)<br/>Music by <b>Silver Screen</b> - Solar Winds (<a href=\'https://www.youtube.com/watch?v=sSAk4T6IaDs\'>https://www.youtube.com/watch?v=sSAk4T6IaDs</a>)");
	}
	
	@Override
	public void onAnimationReady(AnimationReadyEvent event)
	{
		super.onAnimationReady(event);
		
		final DemoScene rs = (DemoScene) this.renderingPanel.getAnimatedScene();
		
		rs.audio.setAutoplay(true);
		AudioElement element = rs.audio.getAudioElement();
		element.setSrc("./static/audio/Silver Screen - Solar Winds (Epic Beautiful Uplifting).mp3");
		
		this.renderingPanel.add(rs.audio);
	}
	
	@Override
	public DemoScene onInitialize()
	{
		return new DemoScene();
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
