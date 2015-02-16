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

package thothbot.parallax.demo.client.content.materials;

import java.util.ArrayList;
import java.util.List;

import thothbot.parallax.core.client.gl2.enums.TextureWrapMode;
import thothbot.parallax.core.client.textures.Texture;
import thothbot.parallax.core.shared.cameras.PerspectiveCamera;
import thothbot.parallax.core.shared.geometries.SphereGeometry;
import thothbot.parallax.core.shared.lights.AmbientLight;
import thothbot.parallax.core.shared.lights.DirectionalLight;
import thothbot.parallax.core.shared.lights.PointLight;
import thothbot.parallax.core.shared.materials.HasShading;
import thothbot.parallax.core.shared.materials.Material;
import thothbot.parallax.core.shared.materials.MeshBasicMaterial;
import thothbot.parallax.core.shared.materials.MeshLambertMaterial;
import thothbot.parallax.core.shared.materials.MeshPhongMaterial;
import thothbot.parallax.core.shared.math.Color;
import thothbot.parallax.core.shared.math.Vector2;
import thothbot.parallax.core.shared.objects.Mesh;
import thothbot.parallax.demo.client.ContentWidget;
import thothbot.parallax.demo.client.Demo;
import thothbot.parallax.demo.client.DemoAnnotations.DemoSource;

import com.google.gwt.core.client.GWT;
import com.google.gwt.core.client.RunAsyncCallback;
import com.google.gwt.resources.client.ImageResource;
import com.google.gwt.user.client.rpc.AsyncCallback;

public final class MaterialsTextures extends ContentWidget 
{
	
	/*
	 * Prepare Rendering Scene
	 */
	@DemoSource
	class DemoScene extends DemoAnimatedScene 
	{
		private static final String img = "./static/textures/lava/lavatile.jpg";
		private static final String img2 = "./static/textures/planets/moon_1024.jpg";

		PerspectiveCamera camera;
		List<Mesh> objects;
		Mesh particleLight;

		@Override
		protected void onStart()
		{
			camera = new PerspectiveCamera(
					40, // fov
					getRenderer().getAbsoluteAspectRation(), // aspect 
					1, // near
					2000 // far 
			); 
			
			camera.getPosition().setY(200);
			
			objects = new ArrayList<Mesh>();

			Texture imgTexture2 = new Texture( img2 );
			imgTexture2.setWrapS(TextureWrapMode.REPEAT);
			imgTexture2.setWrapT(TextureWrapMode.REPEAT);
			imgTexture2.setAnisotropy(16);

			Texture imgTexture = new Texture( img );
			imgTexture.setRepeat(new Vector2(4, 2));
			imgTexture.setWrapS(TextureWrapMode.REPEAT);
			imgTexture.setWrapT(TextureWrapMode.REPEAT);
			imgTexture.setAnisotropy(16);

			double shininess = 50;
			Color specular = new Color(0x333333);
			int bumpScale = 1;
			Material.SHADING shading = Material.SHADING.SMOOTH;
			
			List<Material> materials = new ArrayList<Material>();
			
			MeshPhongMaterial mp1 = new MeshPhongMaterial();
			mp1.setMap(imgTexture);
			mp1.setBumpMap(imgTexture);
			mp1.setBumpScale(bumpScale);
			mp1.setColor(new Color(0xffffff));
			mp1.setAmbient(new Color(0x777777));
			mp1.setSpecular(specular);
			mp1.setShininess(shininess);
			mp1.setShading(shading);
//			mp1.setPerPixel(true);
			materials.add(mp1);

			MeshPhongMaterial mp2 = new MeshPhongMaterial();
			mp2.setMap(imgTexture);
			mp2.setBumpMap(imgTexture);
			mp2.setBumpScale(bumpScale);
			mp2.setColor(new Color(0x00ff00));
			mp2.setAmbient(new Color(0x777777));
			mp2.setSpecular(specular);
			mp2.setShininess(shininess);
			mp2.setShading(shading);
//			mp2.setPerPixel(true);
			materials.add(mp2);
			
			MeshPhongMaterial mp3 = new MeshPhongMaterial();
			mp3.setMap(imgTexture);
			mp3.setBumpMap(imgTexture);
			mp3.setBumpScale(bumpScale);
			mp3.setColor(new Color(0x00ff00));
			mp3.setAmbient(new Color(0x007700));
			mp3.setSpecular(specular);
			mp3.setShininess(shininess);
			mp3.setShading(shading);
//			mp3.setPerPixel(true);
			materials.add(mp3);
			
			MeshPhongMaterial mp4 = new MeshPhongMaterial();
			mp4.setMap(imgTexture);
			mp4.setBumpMap(imgTexture);
			mp4.setBumpScale(bumpScale);
			mp4.setColor(new Color(0x000000));
			mp4.setAmbient(new Color(0x00ff00));
			mp4.setSpecular(specular);
			mp4.setShininess(shininess);
			mp4.setShading(shading);
//			mp4.setPerPixel(true);
			materials.add(mp4);
			
			MeshLambertMaterial ml1 = new MeshLambertMaterial();
			ml1.setMap(imgTexture);
			ml1.setColor(new Color(0xffffff));
			ml1.setAmbient(new Color(0x777777));
			ml1.setShading(shading);
			materials.add(ml1);

			MeshLambertMaterial ml2 = new MeshLambertMaterial();
			ml2.setMap(imgTexture);
			ml2.setColor(new Color(0xff0000));
			ml2.setAmbient(new Color(0x777777));
			ml2.setShading(shading);
			materials.add(ml2);
			
			MeshLambertMaterial ml3 = new MeshLambertMaterial();
			ml3.setMap(imgTexture);
			ml3.setColor(new Color(0xff0000));
			ml3.setAmbient(new Color(0x770000));
			ml3.setShading(shading);
			materials.add(ml3);
			
			MeshLambertMaterial ml4 = new MeshLambertMaterial();
			ml4.setMap(imgTexture);
			ml4.setColor(new Color(0x000000));
			ml4.setAmbient(new Color(0xff0000));
			ml4.setShading(shading);
			materials.add(ml4);

			shininess = 15;
			
			MeshPhongMaterial mpb1 = new MeshPhongMaterial();
			mpb1.setMap(imgTexture2);
			mpb1.setBumpMap(imgTexture2);
			mpb1.setBumpScale(bumpScale);
			mpb1.setColor(new Color(0x000000));
			mpb1.setAmbient(new Color(0x000000));
			mpb1.setSpecular(new Color(0xffaa00));
			mpb1.setShininess(shininess);
			mpb1.setMetal(true);
			mpb1.setShading(shading);
//			mpb1.setPerPixel(true);
			materials.add(mpb1);
			
			MeshPhongMaterial mpb2 = new MeshPhongMaterial();
			mpb2.setMap(imgTexture2);
			mpb2.setBumpMap(imgTexture2);
			mpb2.setBumpScale(bumpScale);
			mpb2.setColor(new Color(0x000000));
			mpb2.setAmbient(new Color(0x000000));
			mpb2.setSpecular(new Color(0xaaff00));
			mpb2.setShininess(shininess);
			mpb2.setMetal(true);
			mpb2.setShading(shading);
//			mpb2.setPerPixel(true);
			materials.add(mpb2);

			MeshPhongMaterial mpb3 = new MeshPhongMaterial();
			mpb3.setMap(imgTexture2);
			mpb3.setBumpMap(imgTexture2);
			mpb3.setBumpScale(bumpScale);
			mpb3.setColor(new Color(0x000000));
			mpb3.setAmbient(new Color(0x000000));
			mpb3.setSpecular(new Color(0x00ffaa));
			mpb3.setShininess(shininess);
			mpb3.setMetal(true);
			mpb3.setShading(shading);
//			mpb3.setPerPixel(true);
			materials.add(mpb3);

			MeshPhongMaterial mpb4 = new MeshPhongMaterial();
			mpb4.setMap(imgTexture2);
			mpb4.setBumpMap(imgTexture2);
			mpb4.setBumpScale(bumpScale);
			mpb4.setColor(new Color(0x000000));
			mpb4.setAmbient(new Color(0x000000));
			mpb4.setSpecular(new Color(0x00aaff));
			mpb4.setShininess(shininess);
			mpb4.setMetal(true);
			mpb4.setShading(shading);
//			mpb4.setPerPixel(true);
			materials.add(mpb4);

			// Spheres geometry

			SphereGeometry geometry_smooth = new SphereGeometry( 70, 32, 16 );
			SphereGeometry geometry_flat = new SphereGeometry( 70, 32, 16 );

			for ( int i = 0, l = materials.size(); i < l; i ++ ) 
			{
				Material material = materials.get( i );
				SphereGeometry geometry = (((HasShading)material).getShading() == Material.SHADING.FLAT) ? geometry_flat : geometry_smooth;
				Mesh sphere = new Mesh( geometry, material );

				sphere.getPosition().setX( ( i % 4 ) * 200 - 200 );
				sphere.getPosition().setZ( Math.floor( i / 4 ) * 200 - 200 );

				objects.add( sphere );

				getScene().add( sphere );
			}

			MeshBasicMaterial particleLightMaterial = new MeshBasicMaterial();
			particleLightMaterial.setColor(new Color(0xffffff));
			particleLight = new Mesh( new SphereGeometry( 4, 8, 8 ), particleLightMaterial );
			getScene().add( particleLight );

			// Lights

			getScene().add( new AmbientLight( 0x444444 ) );

			DirectionalLight directionalLight = new DirectionalLight( 0xffffff, 1 );
			directionalLight.getPosition().set( 1 ).normalize();
			getScene().add( directionalLight );

			PointLight pointLight = new PointLight( 0xffffff, 2, 800 );
			getScene().add( pointLight );

			((MeshBasicMaterial)particleLight.getMaterial()).setColor( pointLight.getColor() );
			pointLight.setPosition( particleLight.getPosition() );

			//
			getRenderer().setClearColor(0x0a0a0a);
			getRenderer().setSortObjects(true);
			getRenderer().setGammaInput(true);
			getRenderer().setGammaOutput(true);
		}
		
		@Override
		protected void onUpdate(double duration)
		{
			double timer = duration * 0.00025;

			camera.getPosition().setX( Math.cos( timer ) * 800 );
			camera.getPosition().setZ( Math.sin( timer ) * 800 );

			camera.lookAt( getScene().getPosition() );

			for ( int i = 0, l = objects.size(); i < l; i ++ ) 
			{
				Mesh object = objects.get( i );

				object.getRotation().addY( 0.005 );
			}

			particleLight.getPosition().setX( Math.sin( timer * 7 ) * 300 );
			particleLight.getPosition().setY( Math.cos( timer * 5 ) * 400 );
			particleLight.getPosition().setZ( Math.cos( timer * 3 ) * 300 );
			
			getRenderer().render(getScene(), camera);
		}
	}
		
	public MaterialsTextures() 
	{
		super("Textures", "This example based on the three.js example.");
	}
	
	@Override
	public DemoScene onInitialize()
	{
		return new DemoScene();
	}
	
	@Override
	protected void asyncOnInitialize(final AsyncCallback<DemoAnimatedScene> callback)
	{
		GWT.runAsync(MaterialsTextures.class, new RunAsyncCallback() 
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
