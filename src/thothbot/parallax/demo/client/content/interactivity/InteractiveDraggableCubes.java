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
import thothbot.parallax.core.client.controls.TrackballControls;
import thothbot.parallax.core.shared.cameras.PerspectiveCamera;
import thothbot.parallax.core.shared.core.Color;
import thothbot.parallax.core.shared.core.Projector;
import thothbot.parallax.core.shared.core.Ray;
import thothbot.parallax.core.shared.core.Vector3;
import thothbot.parallax.core.shared.geometries.CubeGeometry;
import thothbot.parallax.core.shared.geometries.PlaneGeometry;
import thothbot.parallax.core.shared.lights.AmbientLight;
import thothbot.parallax.core.shared.lights.SpotLight;
import thothbot.parallax.core.shared.materials.MeshBasicMaterial;
import thothbot.parallax.core.shared.materials.MeshLambertMaterial;
import thothbot.parallax.core.shared.objects.GeometryObject;
import thothbot.parallax.core.shared.objects.Mesh;
import thothbot.parallax.core.shared.objects.Object3D;
import thothbot.parallax.demo.client.ContentWidget;
import thothbot.parallax.demo.client.Demo;
import thothbot.parallax.demo.client.DemoAnnotations.DemoSource;
import thothbot.parallax.demo.client.content.interactivity.InteractiveCubes.Intersect;
import thothbot.parallax.demo.client.content.interactivity.InteractiveCubesGpu.DemoScene;

import com.google.gwt.core.client.GWT;
import com.google.gwt.core.client.RunAsyncCallback;
import com.google.gwt.dom.client.Style.Cursor;
import com.google.gwt.event.dom.client.MouseMoveEvent;
import com.google.gwt.event.dom.client.MouseMoveHandler;
import com.google.gwt.resources.client.ImageResource;
import com.google.gwt.user.client.rpc.AsyncCallback;

public final class InteractiveDraggableCubes extends ContentWidget 
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

		Vector3 offset = new Vector3(10, 10, 10);
		int mouseX = 0, mouseY = 0;
		
		List<Mesh> objects;
		Mesh plane;
		
		TrackballControls controls;
		Projector projector;
		
		Intersect intersected;
		Mesh selected;
		
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
			getCamera().getPosition().setZ(1000);
			getScene().add(getCamera());
			
			controls = new TrackballControls( getCamera(), getRenderer().getCanvas() );
			controls.setRotateSpeed(1.0);
			controls.setZoomSpeed(1.2);
			controls.setPanSpeed(0.8);
			controls.setZoom(true);
			controls.setPan(true);
			controls.setStaticMoving(true);
			controls.setDynamicDampingFactor(0.3);

			getScene().add( new AmbientLight( 0x505050 ) );

			SpotLight light = new SpotLight( 0xffffff, 1.5 );
			light.getPosition().set( 0, 500, 2000 );
			light.setCastShadow(true);

			light.setShadowCameraNear(200);
			light.setShadowCameraFar(((PerspectiveCamera)getCamera()).getFar());
			light.setShadowCameraFar(50);

			light.setShadowBias(-0.00022);
			light.setShadowDarkness(0.5);

			light.setShadowMapWidth(2048);
			light.setShadowMapHeight(2048);

			getScene().add( light );

			CubeGeometry geometry = new CubeGeometry( 40, 40, 40 );

			objects = new ArrayList<Mesh>();
			for ( int i = 0; i < 200; i ++ ) 
			{
				MeshLambertMaterial material1 = new MeshLambertMaterial();
				material1.setColor(new Color((int)(Math.random() * 0xfffff)));
				material1.setAmbient(material1.getColor());
				Mesh object = new Mesh( geometry, material1 );

				object.getPosition().setX( Math.random() * 1000 - 500 );
				object.getPosition().setY( Math.random() * 600 - 300 );
				object.getPosition().setZ( Math.random() * 800 - 400 );

				object.getRotation().setX( ( Math.random() * 360 ) * Math.PI / 180 );
				object.getRotation().setY( ( Math.random() * 360 ) * Math.PI / 180 ); 
				object.getRotation().setZ( ( Math.random() * 360 ) * Math.PI / 180 );

				object.getScale().setX( Math.random() * 2 + 1 );
				object.getScale().setY( Math.random() * 2 + 1 );
				object.getScale().setZ( Math.random() * 2 + 1 );

//				object.castShadow = true;
//				object.receiveShadow = true;

				getScene().add( object );

				objects.add( object );
			}

			MeshBasicMaterial material2 = new MeshBasicMaterial();
			material2.setColor(new Color(0x000000));
			material2.setOpacity(0.25);
			material2.setTransparent(true);
			material2.setWireframe(true);
			plane = new Mesh( new PlaneGeometry( 2000, 2000, 8, 8 ), material2 );
			plane.setVisible(false);
			getScene().add( plane );

			projector = new Projector();

			getRenderer().setClearColorHex(0xeeeeee);
			getRenderer().setSortObjects(false);
			getRenderer().setShadowMapEnabled(true);
			getRenderer().setShadowMapSoft(true);
		}
		
		@Override
		protected void onStop()
		{			
		}
		
		@Override
		protected void onUpdate(double duration)
		{
			Vector3 vector = new Vector3( mouseX, mouseY, 0.5 );
			projector.unprojectVector( vector, getCamera() );

			Ray ray = new Ray( getCamera().getPosition(), vector.sub( getCamera().getPosition() ).normalize() );


			if ( selected != null ) 
			{
				List<Ray.Intersect> intersects = ray.intersectObject( plane );
				selected.getPosition().copy( intersects.get( 0 ).point.sub( offset ) );
				return;
			}

			List<Ray.Intersect> intersects = ray.intersectObjects( objects );

			if ( intersects.size() > 0 ) 
			{
				if ( intersected.object != intersects.get(0).object ) 
				{
					if ( intersected != null ) 
						((MeshLambertMaterial)intersected.object.getMaterial()).getColor().setHex( intersected.currentHex );

					intersected = new Intersect();
					intersected.object = (GeometryObject) intersects.get(0).object;
					intersected.currentHex = ((MeshLambertMaterial)intersected.object.getMaterial()).getColor().getHex();

					plane.getPosition().copy( intersected.object.getPosition() );
					plane.lookAt( getCamera().getPosition() );
				}
				getWidget().getElement().getStyle().setCursor(Cursor.POINTER);
			} 
			else 
			{
				if ( intersected != null) 
					((MeshLambertMaterial)intersected.object.getMaterial()).getColor().setHex( intersected.currentHex );

				intersected = null;

				getWidget().getElement().getStyle().setCursor(Cursor.AUTO);
			}
		}
	}
		
	public InteractiveDraggableCubes() 
	{
		super("Draggable cubes", "This example based on the three.js example.");
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
		    	  	rs.mouseY = - (event.getY() / canvas.getHeight() ) * 2 + 1;
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
		return Demo.resources.exampleInteractiveDraggableCubes();
	}
	
	@Override
	protected void asyncOnInitialize(final AsyncCallback<DemoAnimatedScene> callback)
	{
		GWT.runAsync(InteractiveDraggableCubes.class, new RunAsyncCallback() 
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
