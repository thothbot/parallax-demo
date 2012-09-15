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

import thothbot.parallax.core.shared.Log;
import thothbot.parallax.core.shared.cameras.PerspectiveCamera;
import thothbot.parallax.core.shared.core.Color;
import thothbot.parallax.core.shared.core.Geometry;
import thothbot.parallax.core.shared.core.Vector3;
import thothbot.parallax.core.shared.geometries.SphereGeometry;
import thothbot.parallax.core.shared.lights.AmbientLight;
import thothbot.parallax.core.shared.lights.DirectionalLight;
import thothbot.parallax.core.shared.lights.PointLight;
import thothbot.parallax.core.shared.materials.LineBasicMaterial;
import thothbot.parallax.core.shared.materials.MeshBasicMaterial;
import thothbot.parallax.core.shared.objects.Line;
import thothbot.parallax.core.shared.objects.Mesh;
import thothbot.parallax.demo.client.ContentWidget;
import thothbot.parallax.demo.client.Demo;
import thothbot.parallax.demo.client.DemoAnnotations.DemoSource;
import thothbot.parallax.loader.shared.ColladaLoader;

import com.google.gwt.core.client.GWT;
import com.google.gwt.core.client.RunAsyncCallback;
import com.google.gwt.http.client.RequestException;
import com.google.gwt.resources.client.ImageResource;
import com.google.gwt.user.client.rpc.AsyncCallback;

public final class LoaderCollada extends ContentWidget 
{
	/*
	 * Prepare Rendering Scene
	 */
	@DemoSource
	class DemoScene extends DemoAnimatedScene 
	{
		static final String model = "./models/collada/monster/monster.dae";
		
		Mesh particleLight;

		@Override
		protected void loadCamera()
		{
			setCamera(
					new PerspectiveCamera(
							45, // fov
							getRenderer().getCanvas().getAspectRation(), // aspect 
							1, // near
							2000 // far 
					)); 
		}

		@Override
		protected void onStart()
		{
			getCamera().getPosition().set(2, 2, 3);
			getScene().add(getCamera());

			ColladaLoader colladaLoader = new ColladaLoader();
			try
			{
				colladaLoader.load(model, new ColladaLoader.Callback() {

					@Override
					public void onLoaded() {
//						assert(false);
						// Add the COLLADA
//						getScene().addChild( dae );
					}
				});
			}
			catch (RequestException exception) 
			{
				Log.error("Error while loading COLLADA file.");
			}
			finally
			{
//				assert(false);
			}
			
			// Grid

			LineBasicMaterial line_material = new LineBasicMaterial();
			line_material.setColor(new Color(0xcccccc));
			line_material.setOpacity(0.2);
			
			Geometry geometry = new Geometry();
			double floor = -0.04, step = 1.0, size = 14.0;

			for ( int i = 0; i <= size / step * 2; i ++ ) 
			{

				geometry.getVertices().add( new Vector3( - size, floor, i * step - size ) );
				geometry.getVertices().add( new Vector3(   size, floor, i * step - size ) );

				geometry.getVertices().add( new Vector3( i * step - size, floor, -size ) );
				geometry.getVertices().add( new Vector3( i * step - size, floor,  size ) );

			}

			Line line = new Line( geometry, line_material, Line.TYPE.PIECES);
			getScene().add( line );

			MeshBasicMaterial sMaterial = new MeshBasicMaterial();
			sMaterial.setColor(new Color(0xffffff));
			
			this.particleLight = new Mesh( new SphereGeometry( 4, 8, 8 ), sMaterial );
			getScene().add( this.particleLight );

			// Lights

			getScene().add( new AmbientLight( 0xcccccc ) );

			DirectionalLight directionalLight = new DirectionalLight(0xeeeeee );
			directionalLight.getPosition().setX( Math.random() - 0.5 );
			directionalLight.getPosition().setY( Math.random() - 0.5 );
			directionalLight.getPosition().setZ( Math.random() - 0.5 );
			directionalLight.getPosition().normalize();
			getScene().add( directionalLight );

			PointLight pointLight = new PointLight( 0xffffff, 4, 0 );
			pointLight.setPosition( this.particleLight.getPosition() );
			getScene().add( pointLight );
		}
		
		@Override
		protected void onStop()
		{			
		}
		
		@Override
		protected void onUpdate(double duration)
		{
			double timer = duration * 0.0005;

			getCamera().getPosition().setX( Math.cos( timer ) * 10.0 );
			getCamera().getPosition().setY( 2.0 );
			getCamera().getPosition().setZ( Math.sin( timer ) * 10.0 );

			getCamera().lookAt( getScene().getPosition() );

			this.particleLight.getPosition().setX( Math.sin( timer * 4 ) * 3009.0 );
			this.particleLight.getPosition().setY( Math.cos( timer * 5 ) * 4000.0 );
			this.particleLight.getPosition().setZ( Math.cos( timer * 4 ) * 3009.0 );
		}
	}
		
	public LoaderCollada() 
	{
		super("ColladaLoader", "This example based on the three.js example.");
	}
	
	@Override
	public DemoScene onInitialize()
	{
		return new DemoScene();
	}

	@Override
	public ImageResource getIcon()
	{
		return Demo.resources.exampleLoaderCollada();
	}
	
	@Override
	protected void asyncOnInitialize(final AsyncCallback<DemoAnimatedScene> callback)
	{
		GWT.runAsync(LoaderCollada.class, new RunAsyncCallback() 
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
