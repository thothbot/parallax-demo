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

package thothbot.parallax.demo.client.content.interactivity;

import java.util.ArrayList;
import java.util.List;

import thothbot.parallax.core.client.AnimationReadyEvent;
import thothbot.parallax.core.client.context.Canvas3d;
import thothbot.parallax.core.shared.cameras.PerspectiveCamera;
import thothbot.parallax.core.shared.core.Color;
import thothbot.parallax.core.shared.core.Projector;
import thothbot.parallax.core.shared.core.Ray;
import thothbot.parallax.core.shared.core.Vector3;
import thothbot.parallax.core.shared.geometries.CubeGeometry;
import thothbot.parallax.core.shared.lights.DirectionalLight;
import thothbot.parallax.core.shared.materials.MeshLambertMaterial;
import thothbot.parallax.core.shared.objects.GeometryObject;
import thothbot.parallax.core.shared.objects.Mesh;
import thothbot.parallax.core.shared.objects.Object3D;
import thothbot.parallax.demo.client.ContentWidget;
import thothbot.parallax.demo.client.Demo;
import thothbot.parallax.demo.client.DemoAnnotations.DemoSource;

import com.google.gwt.core.client.GWT;
import com.google.gwt.core.client.RunAsyncCallback;
import com.google.gwt.event.dom.client.MouseMoveEvent;
import com.google.gwt.event.dom.client.MouseMoveHandler;
import com.google.gwt.resources.client.ImageResource;
import com.google.gwt.user.client.rpc.AsyncCallback;

public final class InteractiveCubes extends ContentWidget 
{
	class Intersect
	{
		public GeometryObject object;
		public int currentHex;
	}

	/*
	 * Prepare Rendering Scene
	 */
	@DemoSource
	class DemoScene extends DemoAnimatedScene 
	{
		static final int radius = 100;
		
		Projector projector;
		
		int mouseX = 0, mouseY = 0;
		Intersect Intersected;
		
		@Override
		protected void loadCamera()
		{
			setCamera(
					new PerspectiveCamera(
							70, // fov
							getRenderer().getCanvas().getAspectRation(), // aspect 
							1, // near
							10000 // far 
					)); 
		}

		@Override
		protected void onStart()
		{
			getCamera().getPosition().set( 0, 300, 500 );
			getScene().add(getCamera());
			
			DirectionalLight light = new DirectionalLight( 0xffffff, 2 );
			light.getPosition().set( 1 ).normalize();
			getScene().add( light );

			DirectionalLight light2 = new DirectionalLight( 0xffffff );
			light2.getPosition().set( -1 ).normalize();
			getScene().add( light2 );

			CubeGeometry geometry = new CubeGeometry( 20, 20, 20 );

			for ( int i = 0; i < 500; i ++ ) 
			{
				MeshLambertMaterial material = new MeshLambertMaterial();
				material.setColor(new Color( (int)(Math.random() * 0xffffff) ));
				Mesh object = new Mesh( geometry, material );

				object.getPosition().setX( Math.random() * 800 - 400 );
				object.getPosition().setY( Math.random() * 800 - 400 );
				object.getPosition().setZ( Math.random() * 800 - 400 );

				object.getRotation().setX( ( Math.random() * 360 ) * Math.PI / 180 );
				object.getRotation().setY( ( Math.random() * 360 ) * Math.PI / 180 );
				object.getRotation().setZ( ( Math.random() * 360 ) * Math.PI / 180 );

				object.getScale().setX( Math.random() * 2 + 1 );
				object.getScale().setY( Math.random() * 2 + 1 );
				object.getScale().setZ( Math.random() * 2 + 1 );

				getScene().add( object );

			}

			projector = new Projector();
			getRenderer().setClearColorHex(0xeeeeee);
			getRenderer().setSortObjects(false);
		}
		
		@Override
		protected void onStop()
		{			
		}
		
		@Override
		protected void onUpdate(double duration)
		{
			getCamera().getPosition().setX( radius * Math.sin( duration / 100 * Math.PI / 360 ) );
			getCamera().getPosition().setY( radius * Math.sin( duration / 100 * Math.PI / 360 ) );
			getCamera().getPosition().setZ( radius * Math.cos( duration / 100 * Math.PI / 360 ) );

			getCamera().lookAt( getScene().getPosition() );

			// find intersections

			Vector3 vector = new Vector3( mouseX, mouseY, 1 );
			projector.unprojectVector( vector, getCamera() );

			Ray ray = new Ray( getCamera().getPosition(), vector.sub( getCamera().getPosition() ).normalize() );

			List<Ray.Intersect> intersects = ray.intersectObjects( getScene().getChildren() );

			if ( intersects.size() > 0 ) 
			{
				if ( Intersected != intersects.get( 0 ).object ) 
				{
					if ( Intersected != null ) 
						((MeshLambertMaterial)Intersected.object.getMaterial()).getColor().setHex( Intersected.currentHex );

					Intersected = new Intersect();
					Intersected.object = (GeometryObject) intersects.get( 0 ).object;
					Intersected.currentHex = ((MeshLambertMaterial)Intersected.object.getMaterial()).getColor().getHex();
					((MeshLambertMaterial)Intersected.object.getMaterial()).getColor().setHex( 0xff0000 );
				}
			}
			else 
			{
				if ( Intersected != null ) 
					((MeshLambertMaterial)Intersected.object.getMaterial()).getColor().setHex( Intersected.currentHex );

				Intersected = null;
			}
		}
	}
		
	public InteractiveCubes() 
	{
		super("Interactive cubes", "This example based on the three.js example.");
	}
	
	@Override
	public void onAnimationReady(AnimationReadyEvent event)
	{
		super.onAnimationReady(event);

		this.renderingPanel.getRenderer().getCanvas().addMouseMoveHandler(new MouseMoveHandler() {
		      @Override
		      public void onMouseMove(MouseMoveEvent event)
		      {
		    	  	DemoScene rs = (DemoScene) renderingPanel.getAnimatedScene();
		    	  	Canvas3d canvas = renderingPanel.getRenderer().getCanvas();
		    	  	rs.mouseX = (event.getX() / canvas.getWidth() ) * 2 - 1; 
		    	  	rs.mouseY = (event.getY() / canvas.getHeight() ) * 2 + 1;
		      }
		});
	}
	
	@Override
	public DemoScene onInitialize()
	{
		return new DemoScene();
	}

	@Override
	public ImageResource getIcon()
	{
		return Demo.resources.exampleInteractiveCubes();
	}
	
	@Override
	protected void asyncOnInitialize(final AsyncCallback<DemoAnimatedScene> callback)
	{
		GWT.runAsync(InteractiveCubes.class, new RunAsyncCallback() 
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
