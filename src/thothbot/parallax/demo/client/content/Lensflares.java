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
 * Squirrel. If not, see http://www.gnu.org/licenses/.
 */

package thothbot.parallax.demo.client.content;

import thothbot.parallax.core.client.textures.Texture;
import thothbot.parallax.core.shared.cameras.PerspectiveCamera;
import thothbot.parallax.core.shared.core.Color3f;
import thothbot.parallax.core.shared.geometries.Cube;
import thothbot.parallax.core.shared.lights.AmbientLight;
import thothbot.parallax.core.shared.lights.DirectionalLight;
import thothbot.parallax.core.shared.lights.LensFlare;
import thothbot.parallax.core.shared.lights.PointLight;
import thothbot.parallax.core.shared.materials.Material;
import thothbot.parallax.core.shared.materials.MeshPhongMaterial;
import thothbot.parallax.core.shared.objects.Mesh;
import thothbot.parallax.core.shared.scenes.FogSimple;
import thothbot.parallax.core.shared.utils.ColorUtils;
import thothbot.parallax.core.shared.utils.ImageUtils;
import thothbot.parallax.demo.client.ContentWidget;
import thothbot.parallax.demo.client.Demo;
import thothbot.parallax.demo.client.DemoAnnotations.DemoSource;

import com.google.gwt.core.client.GWT;
import com.google.gwt.core.client.RunAsyncCallback;
import com.google.gwt.resources.client.ImageResource;
import com.google.gwt.user.client.rpc.AsyncCallback;

public final class Lensflares extends ContentWidget 
{
	/*
	 * Prepare Rendering Scene
	 */
	@DemoSource
	class DemoScene extends DemoAnimatedScene 
	{
		
		private Texture textureFlare0 = ImageUtils.loadTexture( "./static/textures/lensflare/lensflare0.png" );
		private Texture textureFlare2 = ImageUtils.loadTexture( "./static/textures/lensflare/lensflare2.png" );
		private Texture textureFlare3 = ImageUtils.loadTexture( "./static/textures/lensflare/lensflare3.png" );

		@Override
		protected void loadCamera()
		{
			setCamera(
					new PerspectiveCamera(
							40, // fov
							getRenderer().getCanvas().getAspectRation(), // aspect 
							1, // near
							15000 // far 
					)); 
		}

		@Override
		protected void onStart()
		{
			getCamera().getPosition().setZ(250);
			getScene().addChild(getCamera());
			
			FogSimple fog = new FogSimple( 0x000000, 3500, 15000 );
			fog.getColor().setHSV( 0.51f, 0.6f, 0.025f );
			getScene().setFog(fog);

			// world

			int s = 250;
			Cube cube = new Cube( s, s, s );
			MeshPhongMaterial material = new MeshPhongMaterial();
			material.setColor(new Color3f(0xffffff));
			material.setAmbient(new Color3f(0xffffff));
			material.setSpecular(new Color3f(0xffffff));
			material.setShininess(50);
			material.setPerPixel(true);
			
			for ( int i = 0; i < 3000; i ++ ) 
			{
				Mesh mesh = new Mesh( cube, material );

				mesh.getPosition().setX( (float) (8000 * ( 2.0 * Math.random() - 1.0 )) );
				mesh.getPosition().setY( (float) (8000 * ( 2.0 * Math.random() - 1.0 )) );
				mesh.getPosition().setZ( (float) (8000 * ( 2.0 * Math.random() - 1.0 )) );

				mesh.getRotation().setX( (float) (Math.random() * Math.PI) );
				mesh.getRotation().setY( (float) (Math.random() * Math.PI) );
				mesh.getRotation().setZ( (float) (Math.random() * Math.PI) );

				mesh.setMatrixAutoUpdate(false);
				mesh.updateMatrix();

				getScene().addChild( mesh );

			}


			// lights

			AmbientLight ambient = new AmbientLight( 0xffffff );
			ambient.getColor().setHSV( 0.1f, 0.5f, 0.3f );
			getScene().addChild( ambient );


			DirectionalLight dirLight = new DirectionalLight( 0xffffff, 0.125f );
			dirLight.getPosition().set( 0, -1, 0 ).normalize();
			getScene().addChild( dirLight );

			dirLight.getColor().setHSV( 0.1f, 0.725f, 0.9f );

			// lens flares

			addLight( 0.55f, 0.825f, 0.99f, 5000, 0, -1000 );
			addLight( 0.08f, 0.825f, 0.99f,    0, 0, -1000 );
			addLight( 0.995f, 0.025f, 0.99f, 5000, 5000, -1000 );

			// renderer
			getRenderer().setMaxLights(8);
			getRenderer().setClearColor( getScene().getFog().getColor(), 1 );
			getRenderer().setGammaInput(true);
			getRenderer().setGammaOutput(true);
			getRenderer().setPhysicallyBasedShading(true);
		}
		
		private void addLight( float h, float s, float v, float x, float y, float z ) 
		{
			PointLight light = new PointLight( 0xffffff, 1.5f, 4500 );
			light.getPosition().set( x, y, z );
			getScene().addChild( light );

			light.getColor().setHSV( h, s, v );

			Color3f flareColor = new Color3f( 0xffffff );
			flareColor.copy( light.getColor() );
			ColorUtils.adjustHSV( flareColor, 0, -0.5f, 0.5f );

//			LensFlare lensFlare = new LensFlare( textureFlare0, 700, 0.0f, Material.BLENDING.ADDITIVE, flareColor );
			LensFlare lensFlare = new LensFlare( textureFlare0, 700, 0.0f, Material.BLENDING.ADDITIVE );

			lensFlare.add( textureFlare2, 512, 0.0f, Material.BLENDING.ADDITIVE );
			lensFlare.add( textureFlare2, 512, 0.0f, Material.BLENDING.ADDITIVE );
			lensFlare.add( textureFlare2, 512, 0.0f, Material.BLENDING.ADDITIVE );

			lensFlare.add( textureFlare3, 60, 0.6f, Material.BLENDING.ADDITIVE );
			lensFlare.add( textureFlare3, 70, 0.7f, Material.BLENDING.ADDITIVE );
			lensFlare.add( textureFlare3, 120, 0.9f, Material.BLENDING.ADDITIVE );
			lensFlare.add( textureFlare3, 70, 1.0f, Material.BLENDING.ADDITIVE );

//			lensFlare.customUpdateCallback = lensFlareUpdateCallback;
			lensFlare.setPosition(light.getPosition());

			getScene().addChild( lensFlare );
		}
		
		@Override
		protected void onStop()
		{			
		}
		
		@Override
		protected void onUpdate(double duration)
		{
		}
	}
		
	public Lensflares() 
	{
		super("Lensflares", "Fly with WASD/RF/QE + mouse. This example based on the three.js example.");
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
		GWT.runAsync(Lensflares.class, new RunAsyncCallback() 
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
