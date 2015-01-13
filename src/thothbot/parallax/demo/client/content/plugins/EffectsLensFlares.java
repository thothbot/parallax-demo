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

import thothbot.parallax.core.client.controls.FlyControls;
import thothbot.parallax.core.client.textures.Texture;
import thothbot.parallax.core.shared.cameras.PerspectiveCamera;
import thothbot.parallax.core.shared.geometries.BoxGeometry;
import thothbot.parallax.core.shared.lights.AmbientLight;
import thothbot.parallax.core.shared.lights.DirectionalLight;
import thothbot.parallax.core.shared.lights.PointLight;
import thothbot.parallax.core.shared.materials.Material;
import thothbot.parallax.core.shared.materials.MeshPhongMaterial;
import thothbot.parallax.core.shared.math.Color;
import thothbot.parallax.core.shared.objects.Mesh;
import thothbot.parallax.core.shared.scenes.Fog;
import thothbot.parallax.demo.client.ContentWidget;
import thothbot.parallax.demo.client.Demo;
import thothbot.parallax.demo.client.DemoAnnotations.DemoSource;
import thothbot.parallax.plugins.lensflare.LensFlare;
import thothbot.parallax.plugins.lensflare.LensFlare.LensSprite;
import thothbot.parallax.plugins.lensflare.LensFlarePlugin;

import com.google.gwt.core.client.Duration;
import com.google.gwt.core.client.GWT;
import com.google.gwt.core.client.RunAsyncCallback;
import com.google.gwt.resources.client.ImageResource;
import com.google.gwt.user.client.rpc.AsyncCallback;

public final class EffectsLensFlares extends ContentWidget 
{
	/*
	 * Prepare Rendering Scene
	 */
	@DemoSource
	class DemoScene extends DemoAnimatedScene 
	{
		
		private Texture textureFlare0 = new Texture( "./static/textures/lensflare/lensflare0.png" );
		private Texture textureFlare2 = new Texture( "./static/textures/lensflare/lensflare2.png" );
		private Texture textureFlare3 = new Texture( "./static/textures/lensflare/lensflare3.png" );
		
		PerspectiveCamera camera;
		
		private FlyControls controls;
		
		private double oldTime;

		@Override
		protected void onStart()
		{
			camera = new PerspectiveCamera(
					40, // fov
					getRenderer().getAbsoluteAspectRation(), // aspect 
					1, // near
					15000 // far 
			);
			
			camera.getPosition().setZ(250);
			
			new LensFlarePlugin(getRenderer(), getScene());
			controls = new FlyControls( camera, getCanvas() );

			controls.setMovementSpeed( 2500 );
			controls.setRollSpeed( Math.PI / 6.0 );
			controls.setAutoForward( false );
			controls.setDragToLook( false );
			
			Fog fog = new Fog( 0x000000, 3500, 15000 );
			fog.getColor().setHSV( 0.51, 0.6, 0.025 );
			getScene().setFog(fog);

			// world

			int s = 250;
			BoxGeometry cubeGeometry = new BoxGeometry( s, s, s );
			MeshPhongMaterial material = new MeshPhongMaterial();
			material.setColor(new Color(0xffffff));
			material.setAmbient(new Color(0xffffff));
			material.setSpecular(new Color(0xffffff));
			material.setShininess(50);
//			material.setPerPixel(true);
			
			for ( int i = 0; i < 3000; i ++ ) 
			{
				Mesh mesh = new Mesh( cubeGeometry, material );

				mesh.getPosition().setX( 8000 * ( 2.0 * Math.random() - 1.0 ) );
				mesh.getPosition().setY( 8000 * ( 2.0 * Math.random() - 1.0 ) );
				mesh.getPosition().setZ( 8000 * ( 2.0 * Math.random() - 1.0 ) );

				mesh.getRotation().setX( Math.random() * Math.PI );
				mesh.getRotation().setY( Math.random() * Math.PI );
				mesh.getRotation().setZ( Math.random() * Math.PI );

				mesh.setMatrixAutoUpdate(false);
				mesh.updateMatrix();

				getScene().add( mesh );
			}

			// lights

			AmbientLight ambient = new AmbientLight( 0xffffff );
			ambient.getColor().setHSV( 0.1, 0.5, 0.3 );
			getScene().add( ambient );


			DirectionalLight dirLight = new DirectionalLight( 0xffffff, 0.125 );
			dirLight.getPosition().set( 0, -1, 0 ).normalize();
			getScene().add( dirLight );

			dirLight.getColor().setHSV( 0.1, 0.725, 0.9 );

			// lens flares

			addLight( 0.55, 0.825, 0.99, 5000, 0, -1000 );
			addLight( 0.08, 0.825, 0.99,    0, 0, -1000 );
			addLight( 0.995, 0.025, 0.99, 5000, 5000, -1000 );

			// renderer
			getRenderer().setMaxLights(8);
			getRenderer().setClearColor( getScene().getFog().getColor(), 1 );
			getRenderer().setGammaInput(true);
			getRenderer().setGammaOutput(true);
			getRenderer().setPhysicallyBasedShading(true);
			
			this.oldTime = Duration.currentTimeMillis();
		}
		
		private void addLight( double h, double s, double v, double x, double y, double z ) 
		{
			PointLight light = new PointLight( 0xffffff, 1.5, 4500 );
			light.getPosition().set( x, y, z );
			getScene().add( light );

			light.getColor().setHSV( h, s, v );

			Color flareColor = new Color( 0xffffff );
			flareColor.setHSV( h, s - 0.5, v + 0.5 );
			
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
					lensFlare.getLensFlares().get( 3 ).rotation = lensFlare.getPositionScreen().getX() * 0.5 + 45.0 * Math.PI / 180.0;
				}
			});

			lensFlare.setPosition(light.getPosition());

			getScene().add( lensFlare );
		}
		
		@Override
		protected void onUpdate(double duration)
		{
			controls.update( (Duration.currentTimeMillis() - this.oldTime) * 0.001 );
			this.oldTime = Duration.currentTimeMillis();
			getRenderer().render(getScene(), camera);
		}
	}
		
	public EffectsLensFlares() 
	{
		super("Lens Flares", "Fly with WASD/RF/QE + mouse. This example based on the three.js example.");
	}
	
	@Override
	public DemoScene onInitialize()
	{
		return new DemoScene();
	}

	@Override
	public ImageResource getIcon()
	{
		return Demo.resources.exampleLensflares();
	}
	
	@Override
	protected void asyncOnInitialize(final AsyncCallback<DemoAnimatedScene> callback)
	{
		GWT.runAsync(EffectsLensFlares.class, new RunAsyncCallback() 
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
