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

import org.mortbay.util.Loader;

import thothbot.parallax.core.shared.Log;
import thothbot.parallax.core.shared.cameras.PerspectiveCamera;
import thothbot.parallax.core.shared.core.Color3f;
import thothbot.parallax.core.shared.core.Geometry;
import thothbot.parallax.core.shared.core.Vector3f;
import thothbot.parallax.core.shared.lights.DirectionalLight;
import thothbot.parallax.core.shared.materials.Material;
import thothbot.parallax.core.shared.materials.MeshBasicMaterial;
import thothbot.parallax.core.shared.materials.MeshLambertMaterial;
import thothbot.parallax.core.shared.objects.Mesh;
import thothbot.parallax.core.shared.scenes.Scene;
import thothbot.parallax.demo.client.ContentWidget;
import thothbot.parallax.demo.client.Demo;
import thothbot.parallax.demo.client.DemoAnnotations.DemoSource;
import thothbot.parallax.loader.shared.Json;

import com.google.gwt.core.client.GWT;
import com.google.gwt.core.client.RunAsyncCallback;
import com.google.gwt.http.client.RequestException;
import com.google.gwt.resources.client.ImageResource;
import com.google.gwt.user.client.rpc.AsyncCallback;

public final class MorphNormalsFlamingo extends ContentWidget 
{
	/*
	 * Prepare Rendering Scene
	 */
	@DemoSource
	class DemoScene extends DemoAnimatedScene 
	{
		static final int radius = 600;
		static final String model = "./models/animated/flamingo.js";
		Scene scene2;

		@Override
		protected void loadCamera()
		{
			setCamera(
					new PerspectiveCamera(
							40, // fov
							getRenderer().getCanvas().getAspectRation(), // aspect 
							1, // near
							10000 // far 
					)); 
		}

		@Override
		protected void onStart()
		{
			getCamera().getPosition().setY(200);
			getScene().addChild(getCamera());
			
			scene2 = new Scene();
			
			DirectionalLight light = new DirectionalLight( 0xffffff, 1.3f );
			light.getPosition().set( 1, 1, 1 );
			getScene().addChild( light );
			scene2.addChild( light );

			DirectionalLight light2 = new DirectionalLight( 0xffffff, 0.1f );
			light2.getPosition().set( 0.25f, -1, 0 );
			getScene().addChild( light2 );
			scene2.addChild( light2 );

			final Json json = new Json();
			try
			{
				json.load(model, new Json.Callback() {

					@Override
					public void onLoaded() {
						Geometry geometry = json.getGeometry();
//						morphColorsToFaceColors( geometry );
						geometry.computeMorphNormals();

//						MeshLambertMaterial material = new MeshLambertMaterial();
//						material.setColor(new Color3f(0xffffff));
//						material.setWireframe(true);
////						material.setMorphTargets(true);
////						material.setMorphNormals(true);
//						material.setVertexColors(Material.COLORS.FACE);
//						material.setShading(Material.SHADING.FLAT);
						MeshBasicMaterial material = new MeshBasicMaterial();
						material.setColor( new Color3f(0xFF0000) );
						material.setWireframe( true );
						
						Mesh mesh = new Mesh(geometry, material);
						mesh.getScale().set(2f);
						mesh.getPosition().set(0);
						getScene().addChild(mesh);
//						var meshAnim = new MorphAnimMesh( geometry, material );

//						meshAnim.duration = 5000;

//						meshAnim.scale.set( 1.5, 1.5, 1.5 );
//						meshAnim.position.y = 150;

//						getScene().add( meshAnim );
//						morphs.push( meshAnim );
					}
				});
			}
			catch (RequestException exception) 
			{
				Log.error("Error while loading JSON file.");
			}
			
			getRenderer().setSortObjects(false);
			getRenderer().setAutoClear(false);
		}
		
		@Override
		protected void onStop()
		{			
		}
		
		@Override
		protected void onUpdate(double duration)
		{
			double theta = duration * 0.01;

			getCamera().getPosition().setX( (float) (radius * Math.sin( theta * Math.PI / 360.0 )) );
			getCamera().getPosition().setZ( (float) (radius * Math.cos( theta * Math.PI / 360.0 )) );

			getCamera().lookAt( getScene().getPosition() );

//			var delta = clock.getDelta();
//
//			for ( var i = 0; i < morphs.length; i ++ ) {
//
//				morph = morphs[ i ];
//				morph.updateAnimation( 1000 * delta );
//
//			}

//			getRenderer().clear(false, false, false);
		}
	}
		
	public MorphNormalsFlamingo() 
	{
		super("Morph normals: flamingo", "This example based on the three.js example.");
	}
	
	@Override
	public DemoScene onInitialize()
	{
		return new DemoScene();
	}

	@Override
	public ImageResource getIcon()
	{
		return Demo.resources.exampleMorphNormalsFlamingo();
	}
	
	@Override
	protected void asyncOnInitialize(final AsyncCallback<DemoAnimatedScene> callback)
	{
		GWT.runAsync(MorphNormalsFlamingo.class, new RunAsyncCallback() 
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
