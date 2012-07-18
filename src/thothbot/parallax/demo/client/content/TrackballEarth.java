/*
 * Copyright 2012 Alex Usachev, thothbot@gmail.com
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

package thothbot.parallax.demo.client.content;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import thothbot.parallax.core.client.controls.TrackballControl;
import thothbot.parallax.core.client.shader.ShaderNormalMap;
import thothbot.parallax.core.client.shader.Uniform;
import thothbot.parallax.core.client.textures.Texture;
import thothbot.parallax.core.shared.cameras.PerspectiveCamera;
import thothbot.parallax.core.shared.core.Color3f;
import thothbot.parallax.core.shared.core.Geometry;
import thothbot.parallax.core.shared.core.Vector3f;
import thothbot.parallax.core.shared.geometries.Sphere;
import thothbot.parallax.core.shared.lights.DirectionalLight;
import thothbot.parallax.core.shared.materials.MeshLambertMaterial;
import thothbot.parallax.core.shared.materials.MeshPhongMaterial;
import thothbot.parallax.core.shared.materials.ParticleBasicMaterial;
import thothbot.parallax.core.shared.materials.ShaderMaterial;
import thothbot.parallax.core.shared.objects.Mesh;
import thothbot.parallax.core.shared.objects.ParticleSystem;
import thothbot.parallax.core.shared.utils.ImageUtils;
import thothbot.parallax.core.shared.utils.UniformsUtils;
import thothbot.parallax.demo.client.ContentWidget;
import thothbot.parallax.demo.client.Demo;
import thothbot.parallax.demo.client.DemoAnnotations.DemoSource;

import com.google.gwt.core.client.Duration;
import com.google.gwt.core.client.GWT;
import com.google.gwt.core.client.RunAsyncCallback;
import com.google.gwt.resources.client.ClientBundle;
import com.google.gwt.resources.client.ImageResource;
import com.google.gwt.user.client.rpc.AsyncCallback;

public final class TrackballEarth extends ContentWidget 
{

	/*
	 * Load textures
	 */
	@DemoSource
	public interface Resources extends ClientBundle
	{
		Resources INSTANCE = GWT.create(Resources.class);

		@Source("../../resources/textures/planets/earth_atmos_2048.jpg")
		ImageResource earthAtmos();
		
		@Source("../../resources/textures/planets/earth_clouds_1024.png")
		ImageResource earthClouds();
		
		@Source("../../resources/textures/planets/earth_normal_2048.jpg")
		ImageResource earthNormal();
		
		@Source("../../resources/textures/planets/earth_specular_2048.jpg")
		ImageResource earthSpecular();
		
		@Source("../../resources/textures/planets/moon_1024.jpg")
		ImageResource moon();
	}
	
	/*
	 * Prepare Rendering Scene
	 */
	@DemoSource
	class DemoScene extends DemoRenderingScene 
	{
		static final int radius = 6371;
		static final float tilt = 0.41f;
		static final float rotationSpeed = 0.1f;

		static final float cloudsScale = 1.005f;
		static final float moonScale = 0.23f;
		
		Mesh meshPlanet;
		Mesh meshClouds;
		Mesh meshMoon;
		
		private TrackballControl control;
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

			this.control = new TrackballControl( getCamera(), getRenderer().getCanvas() );
			this.control.setPanSpeed(0.2f);
			this.control.setDynamicDampingFactor(0.3f);
			this.control.setMinDistance(radius * 1.1f);
			this.control.setMaxDistance(radius * 100f);

			DirectionalLight dirLight = new DirectionalLight( 0xFFFFFF );
			dirLight.getPosition().set( -1f, 0f, 1f ).normalize();
			getScene().addChild( dirLight );

			Texture planetTexture   = ImageUtils.loadTexture( Resources.INSTANCE.earthAtmos(), null, null );
			Texture cloudsTexture   = ImageUtils.loadTexture( Resources.INSTANCE.earthClouds(), null, null );
			Texture normalTexture   = ImageUtils.loadTexture( Resources.INSTANCE.earthNormal(), null, null );
			Texture specularTexture = ImageUtils.loadTexture( Resources.INSTANCE.earthSpecular(), null, null );
			Texture moonTexture     = ImageUtils.loadTexture( Resources.INSTANCE.moon(), null, null );

			ShaderNormalMap shader = new ShaderNormalMap();
			Map<String, Uniform> uniforms = UniformsUtils.clone( shader.getUniforms() );

			uniforms.get("tNormal").texture = normalTexture;
			uniforms.get("uNormalScale").value = 0.85f;

			uniforms.get("tDiffuse").texture = planetTexture;
			uniforms.get("tSpecular").texture = specularTexture;

			uniforms.get("enableAO").value = 0;
			uniforms.get("enableDiffuse").value = 1;
			uniforms.get("enableSpecular").value = 1;

			((Color3f)uniforms.get("uDiffuseColor").value).setHex( 0xffffff );
			((Color3f)uniforms.get("uSpecularColor").value).setHex( 0x666666 );
			((Color3f)uniforms.get("uAmbientColor").value).setHex( 0x000000 );

			uniforms.get("uShininess").value = 20f;

			((Color3f)uniforms.get("uDiffuseColor").value).convertGammaToLinear();
			((Color3f)uniforms.get("uSpecularColor").value).convertGammaToLinear();
			((Color3f)uniforms.get("uAmbientColor").value).convertGammaToLinear();

			ShaderMaterial.ShaderMaterialOptions sOpt = new ShaderMaterial.ShaderMaterialOptions(); 
			sOpt.fragmentShader = shader.getFragmentSource();
			sOpt.vertexShader = shader.getVertexSource();
			sOpt.uniforms = uniforms;
			sOpt.lights = true;
			ShaderMaterial materialNormalMap = new ShaderMaterial(sOpt);

			// planet

			Sphere geometry = new Sphere( radius, 100, 50 );
			geometry.computeTangents();

			this.meshPlanet = new Mesh( geometry, materialNormalMap );
			meshPlanet.getRotation().setY( 0 );
			meshPlanet.getRotation().setZ( tilt );
			getScene().addChild( meshPlanet );


			// clouds
			MeshLambertMaterial.MeshLambertMaterialOptions mlOpt = new MeshLambertMaterial.MeshLambertMaterialOptions();
			mlOpt.color = new Color3f(0xffffff);
			mlOpt.map = cloudsTexture;
			mlOpt.transparent = true;
			MeshLambertMaterial materialClouds = new MeshLambertMaterial( mlOpt );

			this.meshClouds = new Mesh( geometry, materialClouds );
			meshClouds.getScale().set( cloudsScale, cloudsScale, cloudsScale );
			meshClouds.getRotation().setZ( tilt );
			getScene().addChild( meshClouds );


			// moon
			MeshPhongMaterial.MeshPhongMaterialOptions mpOpt = new MeshPhongMaterial.MeshPhongMaterialOptions();
			mpOpt.color = new Color3f(0xffffff);
			mpOpt.map = moonTexture;
			MeshPhongMaterial materialMoon = new MeshPhongMaterial( mpOpt );

			this.meshMoon = new Mesh( geometry, materialMoon );
			meshMoon.getPosition().set( radius * 5.0f, 0, 0 );
			meshMoon.getScale().set( moonScale, moonScale, moonScale );
			getScene().addChild( meshMoon );


			// stars

			Geometry starsGeometry = new Geometry();

			for ( int i = 0; i < 1500; i ++ ) 
			{

				Vector3f vertex = new Vector3f();
				vertex.setX( (float) (Math.random() * 2.0 - 1.0) );
				vertex.setY( (float) (Math.random() * 2.0 - 1.0) );
				vertex.setZ( (float) (Math.random() * 2.0 - 1.0) );
				vertex.multiply( radius );

				starsGeometry.getVertices().add( vertex );

			}

			ParticleBasicMaterial.ParticleBasicMaterialOptions pbOpt = new ParticleBasicMaterial.ParticleBasicMaterialOptions();
			pbOpt.color = new Color3f(0x555555);
			pbOpt.size = 2;
			pbOpt.sizeAttenuation = false;
			
			List<ParticleBasicMaterial> starsMaterials = new ArrayList<ParticleBasicMaterial>();
			starsMaterials.add(new ParticleBasicMaterial( pbOpt ));
			
			pbOpt.size = 1;
			starsMaterials.add(new ParticleBasicMaterial( pbOpt ));
			
			pbOpt.color = new Color3f(0x333333);
			pbOpt.size = 2;
			starsMaterials.add(new ParticleBasicMaterial( pbOpt ));
			
			pbOpt.color = new Color3f(0x3a3a3a);
			pbOpt.size = 1;
			starsMaterials.add(new ParticleBasicMaterial( pbOpt ));
			
			pbOpt.color = new Color3f(0x1a1a1a);
			pbOpt.size = 2;
			starsMaterials.add(new ParticleBasicMaterial( pbOpt ));
			
			pbOpt.color = new Color3f(0x1a1a1a);
			pbOpt.size = 1;
			starsMaterials.add(new ParticleBasicMaterial( pbOpt ));				

			for ( int i = 10; i < 30; i ++ ) 
			{
				ParticleSystem stars = new ParticleSystem( starsGeometry, starsMaterials.get( i % 6 ) );

				stars.getRotation().setX( (float) (Math.random() * 6.0) );
				stars.getRotation().setY( (float) (Math.random() * 6.0) );
				stars.getRotation().setZ( (float) (Math.random() * 6.0) );

				float s = i * 10.0f;
				stars.getScale().set( s, s, s );

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
			float delta = (float) ((Duration.currentTimeMillis() - this.oldTime) * 0.001);

			meshPlanet.getRotation().addY( rotationSpeed * delta );
			meshClouds.getRotation().addY( 1.25f * rotationSpeed * delta );

			float angle = delta * rotationSpeed;

			meshMoon.setPosition( new Vector3f(
				(float)(Math.cos( angle ) * meshMoon.getPosition().getX() - Math.sin( angle ) * meshMoon.getPosition().getZ()),
				0,
				(float)(Math.sin( angle ) * meshMoon.getPosition().getX() + Math.cos( angle ) * meshMoon.getPosition().getZ())
			));
			meshMoon.getRotation().addY( - angle );

			this.control.update();

			getRenderer().clear(false, false, false);
			
			this.oldTime = Duration.currentTimeMillis();
			super.onUpdate(duration);
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
	protected void asyncOnInitialize(final AsyncCallback<DemoRenderingScene> callback)
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
