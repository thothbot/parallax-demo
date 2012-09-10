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

package thothbot.parallax.demo.client.content;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import thothbot.parallax.core.client.controls.TrackballControls;
import thothbot.parallax.core.client.shader.ShaderNormalMap;
import thothbot.parallax.core.client.shader.Uniform;
import thothbot.parallax.core.client.textures.Texture;
import thothbot.parallax.core.shared.cameras.PerspectiveCamera;
import thothbot.parallax.core.shared.core.Color;
import thothbot.parallax.core.shared.core.Geometry;
import thothbot.parallax.core.shared.core.Vector3;
import thothbot.parallax.core.shared.geometries.Sphere;
import thothbot.parallax.core.shared.lights.DirectionalLight;
import thothbot.parallax.core.shared.materials.MeshLambertMaterial;
import thothbot.parallax.core.shared.materials.MeshPhongMaterial;
import thothbot.parallax.core.shared.materials.ParticleBasicMaterial;
import thothbot.parallax.core.shared.materials.ShaderMaterial;
import thothbot.parallax.core.shared.objects.Mesh;
import thothbot.parallax.core.shared.objects.ParticleSystem;
import thothbot.parallax.core.shared.utils.ImageUtils;
import thothbot.parallax.demo.client.ContentWidget;
import thothbot.parallax.demo.client.Demo;
import thothbot.parallax.demo.client.DemoAnnotations.DemoSource;

import com.google.gwt.core.client.Duration;
import com.google.gwt.core.client.GWT;
import com.google.gwt.core.client.RunAsyncCallback;
import com.google.gwt.resources.client.ImageResource;
import com.google.gwt.user.client.rpc.AsyncCallback;

public final class TrackballEarth extends ContentWidget 
{
	/*
	 * Prepare Rendering Scene
	 */
	@DemoSource
	class DemoScene extends DemoAnimatedScene 
	{
		private static final String earthAtmos    = "./static/textures/planets/earth_atmos_2048.jpg";
		private static final String earthClouds   = "./static/textures/planets/earth_clouds_1024.png";
		private static final String earthNormal   = "./static/textures/planets/earth_normal_2048.jpg";
		private static final String earthSpecular = "./static/textures/planets/earth_specular_2048.jpg";
		private static final String moon          = "./static/textures/planets/moon_1024.jpg";
		
		static final int radius = 6371;
		static final double tilt = 0.41;
		static final double rotationSpeed = 0.1;

		static final double cloudsScale = 1.005;
		static final double moonScale = 0.23;
		
		Mesh meshPlanet;
		Mesh meshClouds;
		Mesh meshMoon;
		
		private TrackballControls control;
		private double oldTime;
		
		@Override
		protected void loadCamera()
		{
			setCamera(
					new PerspectiveCamera(
							25, // fov
							getRenderer().getCanvas().getAspectRation(), // aspect 
							50, // near
							1e7f // far 
					)); 
		}

		@Override
		protected void onStart()
		{
			getCamera().getPosition().setZ(radius * 7);
			getScene().addChild(getCamera());

			this.control = new TrackballControls( getCamera(), getRenderer().getCanvas() );
			this.control.setPanSpeed(0.2);
			this.control.setDynamicDampingFactor(0.3);
			this.control.setMinDistance(radius * 1.1);
			this.control.setMaxDistance(radius * 100);

			DirectionalLight dirLight = new DirectionalLight( 0xFFFFFF );
			dirLight.getPosition().set( -1, 0, 1 ).normalize();
			getScene().addChild( dirLight );

			Texture planetTexture   = ImageUtils.loadTexture( earthAtmos );
			Texture cloudsTexture   = ImageUtils.loadTexture( earthClouds );
			Texture normalTexture   = ImageUtils.loadTexture( earthNormal );
			Texture specularTexture = ImageUtils.loadTexture( earthSpecular );
			Texture moonTexture     = ImageUtils.loadTexture( moon );

			ShaderMaterial materialNormalMap = new ShaderMaterial(new ShaderNormalMap()); 
			materialNormalMap.setLights(true);
			
			Map<String, Uniform> uniforms = materialNormalMap.getShader().getUniforms();

			uniforms.get("tNormal").setTexture( normalTexture );
			uniforms.get("uNormalScale").setValue( 0.85 );

			uniforms.get("tDiffuse").setTexture( planetTexture );
			uniforms.get("tSpecular").setTexture( specularTexture );

			uniforms.get("enableAO").setValue( 0 );
			uniforms.get("enableDiffuse").setValue( 1 );
			uniforms.get("enableSpecular").setValue( 1 );

			((Color)uniforms.get("uDiffuseColor").getValue()).setHex( 0xffffff );
			((Color)uniforms.get("uSpecularColor").getValue()).setHex( 0x666666 );
			((Color)uniforms.get("uAmbientColor").getValue()).setHex( 0x000000 );

			uniforms.get("uShininess").setValue( 20.0 );

			((Color)uniforms.get("uDiffuseColor").getValue()).convertGammaToLinear();
			((Color)uniforms.get("uSpecularColor").getValue()).convertGammaToLinear();
			((Color)uniforms.get("uAmbientColor").getValue()).convertGammaToLinear();

			// planet

			Sphere geometry = new Sphere( radius, 100, 50 );
			geometry.computeTangents();

			this.meshPlanet = new Mesh( geometry, materialNormalMap );
			meshPlanet.getRotation().setY( 0 );
			meshPlanet.getRotation().setZ( tilt );
			getScene().addChild( meshPlanet );


			// clouds
			MeshLambertMaterial materialClouds = new MeshLambertMaterial();
			materialClouds.setColor( new Color(0xffffff) );
			materialClouds.setMap( cloudsTexture );
			materialClouds.setTransparent(true);

			this.meshClouds = new Mesh( geometry, materialClouds );
			meshClouds.getScale().set( cloudsScale );
			meshClouds.getRotation().setZ( tilt );
			getScene().addChild( meshClouds );


			// moon
			MeshPhongMaterial materialMoon = new MeshPhongMaterial();
			materialMoon.setColor( new Color(0xffffff) );
			materialMoon.setMap( moonTexture );
			

			this.meshMoon = new Mesh( geometry, materialMoon );
			meshMoon.getPosition().set( radius * 5.0, 0, 0 );
			meshMoon.getScale().set( moonScale );
			getScene().addChild( meshMoon );


			// stars

			Geometry starsGeometry = new Geometry();

			for ( int i = 0; i < 1500; i ++ ) 
			{

				Vector3 vertex = new Vector3();
				vertex.setX( Math.random() * 2.0 - 1.0 );
				vertex.setY( Math.random() * 2.0 - 1.0 );
				vertex.setZ( Math.random() * 2.0 - 1.0 );
				vertex.multiply( radius );

				starsGeometry.getVertices().add( vertex );

			}

			ParticleBasicMaterial pbOpt = new ParticleBasicMaterial();
			pbOpt.setColor( new Color(0x555555) );
			pbOpt.setSize( 2 );
			pbOpt.setSizeAttenuation(false);
			
			List<ParticleBasicMaterial> starsMaterials = new ArrayList<ParticleBasicMaterial>();
			starsMaterials.add(pbOpt);
			
			ParticleBasicMaterial pbOpt1 = new ParticleBasicMaterial();
			pbOpt1.setColor( new Color(0x555555) );
			pbOpt1.setSize( 1 );
			pbOpt1.setSizeAttenuation(false);
			starsMaterials.add(pbOpt1);
			
			ParticleBasicMaterial pbOpt2 = new ParticleBasicMaterial();
			pbOpt2.setColor( new Color(0x333333) );
			pbOpt2.setSize( 2 );
			pbOpt2.setSizeAttenuation(false);
			starsMaterials.add(pbOpt2);
			
			ParticleBasicMaterial pbOpt3 = new ParticleBasicMaterial();
			pbOpt3.setColor( new Color(0x3a3a3a) );
			pbOpt3.setSize( 1 );
			pbOpt3.setSizeAttenuation(false);
			starsMaterials.add(pbOpt3);
			
			ParticleBasicMaterial pbOpt4 = new ParticleBasicMaterial();
			pbOpt4.setColor( new Color(0x1a1a1a) );
			pbOpt4.setSize( 2 );
			pbOpt4.setSizeAttenuation(false);
			starsMaterials.add(pbOpt4);
			
			ParticleBasicMaterial pbOpt5 = new ParticleBasicMaterial();
			pbOpt5.setColor( new Color(0x1a1a1a) );
			pbOpt5.setSize( 1 );
			pbOpt5.setSizeAttenuation(false);
			starsMaterials.add(pbOpt5);				

			for ( int i = 10; i < 30; i ++ ) 
			{
				ParticleSystem stars = new ParticleSystem( starsGeometry, starsMaterials.get( i % 6 ) );

				stars.getRotation().setX( Math.random() * 6.0 );
				stars.getRotation().setY( Math.random() * 6.0 );
				stars.getRotation().setZ( Math.random() * 6.0 );

				double s = i * 10.0;
				stars.getScale().set( s );

				stars.setMatrixAutoUpdate(false);
				stars.updateMatrix();

				getScene().addChild( stars );
			}
			
			getRenderer().setSortObjects(false);
			getRenderer().setAutoClear(false);

//			renderer.gammaInput = true;
//			renderer.gammaOutput = true;
			
			this.oldTime = Duration.currentTimeMillis();
		}
		
		@Override
		protected void onStop()
		{			
		}
		
		@Override
		protected void onUpdate(double duration)
		{
			double delta = (Duration.currentTimeMillis() - this.oldTime) * 0.001;

			meshPlanet.getRotation().addY( rotationSpeed * delta );
			meshClouds.getRotation().addY( 1.25 * rotationSpeed * delta );

			double angle = delta * rotationSpeed;

			meshMoon.setPosition( new Vector3(
				Math.cos( angle ) * meshMoon.getPosition().getX() - Math.sin( angle ) * meshMoon.getPosition().getZ(),
				0,
				Math.sin( angle ) * meshMoon.getPosition().getX() + Math.cos( angle ) * meshMoon.getPosition().getZ()
			));
			meshMoon.getRotation().addY( - angle );

			this.control.update();

			getRenderer().clear(false, false, false);
			
			this.oldTime = Duration.currentTimeMillis();
		}
	}

	public TrackballEarth() 
	{
		super("Earth (trackball camera)", "MOVE: mouse and press, LEFT/A: rotate, MIDDLE/S: zoom, RIGHT/D: pan. This example based on the three.js example.");
	}
	
	@Override
	public DemoScene onInitialize()
	{
		return new DemoScene();
	}

	@Override
	public ImageResource getIcon()
	{
		return Demo.resources.exampleTrackballEarth();
	}
	
	@Override
	protected void asyncOnInitialize(final AsyncCallback<DemoAnimatedScene> callback)
	{
		GWT.runAsync(TrackballEarth.class, new RunAsyncCallback() 
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
