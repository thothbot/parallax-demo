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

import thothbot.parallax.core.client.gl2.enums.TextureMagFilter;
import thothbot.parallax.core.client.gl2.enums.TextureMinFilter;
import thothbot.parallax.core.client.textures.CompressedTexture;
import thothbot.parallax.core.shared.cameras.PerspectiveCamera;
import thothbot.parallax.core.shared.core.Geometry;
import thothbot.parallax.core.shared.geometries.BoxGeometry;
import thothbot.parallax.core.shared.materials.Material.BLENDING;
import thothbot.parallax.core.shared.materials.Material.SIDE;
import thothbot.parallax.core.shared.materials.MeshBasicMaterial;
import thothbot.parallax.core.shared.objects.Mesh;
import org.parallax3d.parallax.demo.client.ContentWidget;
import org.parallax3d.parallax.demo.client.DemoAnnotations.DemoSource;

import com.google.gwt.core.client.GWT;
import com.google.gwt.core.client.RunAsyncCallback;
import com.google.gwt.user.client.rpc.AsyncCallback;

public final class MaterialsTextureCompressed extends ContentWidget 
{
	/*
	 * Prepare Rendering Scene
	 */
	@DemoSource
	class DemoScene extends DemoAnimatedScene 
	{

		private static final String dxt1_nomip = "./static/textures/compressed/disturb_dxt1_nomip.dds";
		private static final String dxt1_mip = "./static/textures/compressed/disturb_dxt1_mip.dds";
		private static final String dxt3_mip = "./static/textures/compressed/hepatica_dxt3_mip.dds";
		private static final String dxt5_mip = "./static/textures/compressed/explosion_dxt5_mip.dds";

		PerspectiveCamera camera;
		List<Mesh> meshes;

		@Override
		protected void onStart()
		{
			camera = new PerspectiveCamera(
					50, // fov
					getRenderer().getAbsoluteAspectRation(), // aspect 
					1, // near
					2000 // far 
			); 
			
			camera.getPosition().setZ(1000);
			
			Geometry geometry = new BoxGeometry( 200, 200, 200 );

			/*
			This is how compressed textures are supposed to be used:

			DXT1 - RGB - opaque textures
			DXT3 - RGBA - transparent textures with sharp alpha transitions
			DXT5 - RGBA - transparent textures with full alpha range
			*/

			CompressedTexture map1 = new CompressedTexture( dxt1_nomip );
			map1.setMinFilter(TextureMinFilter.LINEAR);
			map1.setMagFilter(TextureMagFilter.LINEAR);
			map1.setAnisotropy(4);

			CompressedTexture map2 = new CompressedTexture( dxt1_mip );
			map2.setAnisotropy(4);

			CompressedTexture map3 = new CompressedTexture( dxt3_mip );
			map3.setAnisotropy(4);

			CompressedTexture map4 = new CompressedTexture( dxt5_mip );
			map4.setAnisotropy(4);

			MeshBasicMaterial material1 = new MeshBasicMaterial();
			material1.setMap(map1);
			MeshBasicMaterial material2 = new MeshBasicMaterial();
			material2.setMap(map2);
			MeshBasicMaterial material3 = new MeshBasicMaterial();
			material3.setMap(map3);
			material3.setAlphaTest(0.3);
			material3.setSide(SIDE.DOUBLE);
			MeshBasicMaterial material4 = new MeshBasicMaterial();
			material4.setMap(map4);
			material4.setSide(SIDE.DOUBLE);
			material4.setBlending(BLENDING.ADDITIVE);
			material4.setDepthTest(false);
			material4.setTransparent(true);

			meshes = new ArrayList<Mesh>();
			Mesh mesh1 = new Mesh( geometry, material1 );
			mesh1.getPosition().setX( -200 );
			mesh1.getPosition().setY( -200 );
			getScene().add( mesh1 );
			meshes.add( mesh1 );

			Mesh mesh2 = new Mesh( geometry, material2 );
			mesh2.getPosition().setX( 200 );
			mesh2.getPosition().setY( -200 );
			getScene().add( mesh2 );
			meshes.add( mesh2 );

			Mesh mesh3 = new Mesh( geometry, material3 );
			mesh3.getPosition().setX( 200 );
			mesh3.getPosition().setY( 200 );
			getScene().add( mesh3 );
			meshes.add( mesh3 );

			Mesh mesh4 = new Mesh( geometry, material4 );
			mesh4.getPosition().setX( -200 );
			mesh4.getPosition().setY( 200 );
			getScene().add( mesh4 );
			meshes.add( mesh4 );

		}
		
		@Override
		protected void onUpdate(double duration)
		{
			double time = duration * 0.001;

			for ( int i = 0; i < meshes.size(); i ++ ) 
			{
				Mesh mesh = meshes.get( i );
				mesh.getRotation().setX( time );
				mesh.getRotation().setY( time );
			}
			
			getRenderer().render(getScene(), camera);
		}
	}
		
	public MaterialsTextureCompressed() 
	{
		super("Compressed textures", "This example based on the three.js example.");
	}
	
	@Override
	public DemoScene onInitialize()
	{
		return new DemoScene();
	}
	
	@Override
	protected void asyncOnInitialize(final AsyncCallback<DemoAnimatedScene> callback)
	{
		GWT.runAsync(MaterialsTextureCompressed.class, new RunAsyncCallback() 
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
