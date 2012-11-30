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

package thothbot.parallax.demo.client.content.interactivity;

import java.util.ArrayList;
import java.util.List;

import thothbot.parallax.core.client.context.Canvas3d;
import thothbot.parallax.core.client.controls.TrackballControls;
import thothbot.parallax.core.client.events.AnimationReadyEvent;
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
import thothbot.parallax.core.shared.objects.DimensionalObject;
import thothbot.parallax.core.shared.objects.GeometryObject;
import thothbot.parallax.core.shared.objects.Mesh;
import thothbot.parallax.demo.client.ContentWidget;
import thothbot.parallax.demo.client.Demo;
import thothbot.parallax.demo.client.DemoAnnotations.DemoSource;

import com.google.gwt.core.client.GWT;
import com.google.gwt.core.client.RunAsyncCallback;
import com.google.gwt.dom.client.Style.Cursor;
import com.google.gwt.event.dom.client.KeyDownEvent;
import com.google.gwt.event.dom.client.KeyUpEvent;
import com.google.gwt.event.dom.client.MouseDownEvent;
import com.google.gwt.event.dom.client.MouseDownHandler;
import com.google.gwt.event.dom.client.MouseMoveEvent;
import com.google.gwt.event.dom.client.MouseMoveHandler;
import com.google.gwt.event.dom.client.MouseUpEvent;
import com.google.gwt.event.dom.client.MouseUpHandler;
import com.google.gwt.resources.client.ImageResource;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.RootPanel;

public final class InteractiveDraggableCubes extends ContentWidget implements  MouseMoveHandler, MouseDownHandler, MouseUpHandler
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
		PerspectiveCamera camera;
		
		Vector3 offset = new Vector3(10, 10, 10);
		double mouseDeltaX = 0, mouseDeltaY = 0;
		
		List<Mesh> objects;
		Mesh plane;
		
		TrackballControls controls;
		Projector projector;
		
		Intersect intersected;
		DimensionalObject selected;

		@Override
		protected void onStart()
		{
			camera = new PerspectiveCamera(
					70, // fov
					getRenderer().getAbsoluteAspectRation(), // aspect 
					1, // near
					10000 // far 
			);
			camera.getPosition().setZ(1000);
			
			controls = new TrackballControls( camera, renderingPanel.getCanvas() );
			controls.setRotateSpeed(1.0);
			controls.setZoomSpeed(1.2);
			controls.setPanSpeed(0.8);
			controls.setZoom(true);
			controls.setPan(true);
			controls.setStaticMoving(true);
			controls.setDynamicDampingFactor(0.3);
			controls.setEnabled(false);

			getScene().add( new AmbientLight( 0x505050 ) );

			SpotLight light = new SpotLight( 0xffffff, 1.5 );
			light.getPosition().set( 0, 500, 2000 );
			light.setCastShadow(true);

			light.setShadowCameraNear(200);
			light.setShadowCameraFar(((PerspectiveCamera)camera).getFar());
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
				material1.setColor(new Color( (int)(Math.random() * 0xffffff) ));
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

				object.setCastShadow(true);
				object.setReceiveShadow(true);

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
//			getRenderer().setShadowMapEnabled(true);
//			getRenderer().setShadowMapSoft(true);
		}
		
		@Override
		protected void onUpdate(double duration)
		{
			controls.update();
			getRenderer().render(getScene(), camera);
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

		getWidget().addDomHandler(this, MouseMoveEvent.getType());
		getWidget().addDomHandler(this, MouseDownEvent.getType());
		getWidget().addDomHandler(this, MouseUpEvent.getType());
	}
	
	@Override
	public void onMouseUp(MouseUpEvent event) 
	{
		event.preventDefault();

		DemoScene rs = (DemoScene) renderingPanel.getAnimatedScene();
		
		rs.controls.setEnabled(true);

		if ( rs.intersected != null ) 
		{
			rs.plane.getPosition().copy( rs.intersected.object.getPosition() );

			rs.selected = null;
		}

		getWidget().getElement().getStyle().setCursor(Cursor.AUTO);	
	}

	@Override
	public void onMouseDown(MouseDownEvent event) 
	{
		event.preventDefault();

		DemoScene rs = (DemoScene) renderingPanel.getAnimatedScene();
		
		Vector3 vector = new Vector3( rs.mouseDeltaX, rs.mouseDeltaY, 0.5 );
		rs.projector.unprojectVector( vector, rs.camera );

		Ray ray = new Ray( rs.camera.getPosition(), vector.sub( rs.camera.getPosition() ).normalize() );
		List<Ray.Intersect> intersects = ray.intersectObjects( rs.objects );

		if ( intersects.size() > 0 ) 
		{
			rs.controls.setEnabled(false);

			rs.selected = intersects.get( 0 ).object; 

			List<Ray.Intersect> intersects2 = ray.intersectObject( rs.plane );
			rs.offset.copy( intersects2.get( 0 ).point ).sub( rs.plane.getPosition() );

			getWidget().getElement().getStyle().setCursor(Cursor.MOVE);	
		}
	}

	@Override
	public void onMouseMove(MouseMoveEvent event) 
	{
		event.preventDefault();

		DemoScene rs = (DemoScene) renderingPanel.getAnimatedScene();

		rs.mouseDeltaX = (event.getX() / (double)renderingPanel.getRenderer().getAbsoluteWidth() ) * 2.0 - 1.0; 
		rs.mouseDeltaX = - (event.getY() / (double)renderingPanel.getRenderer().getAbsoluteHeight() ) * 2.0 + 1.0;

		//

		Vector3 vector = new Vector3( rs.mouseDeltaX, rs.mouseDeltaX, 0.5 );
		rs.projector.unprojectVector( vector, rs.camera );

		Ray ray = new Ray( rs.camera.getPosition(), vector.sub( rs.camera.getPosition() ).normalize() );

		if ( rs.selected != null ) 
		{
			List<Ray.Intersect> intersects = ray.intersectObject( rs.plane );
			rs.selected.getPosition().copy( intersects.get( 0 ).point.sub( rs.offset ) );
			return;
		}

		List<Ray.Intersect> intersects = ray.intersectObjects( rs.objects );

		if ( intersects.size() > 0 ) 
		{
//			if ( rs.intersected == null || rs.intersected.object != intersects.get(0).object ) 
			if ( rs.intersected != intersects.get(0).object )
			{
				if ( rs.intersected != null )
				{
					((MeshLambertMaterial)rs.intersected.object.getMaterial()).getColor().setHex( rs.intersected.currentHex );
				}

				rs.intersected = new Intersect();
				rs.intersected.object = (GeometryObject) intersects.get(0).object;
				rs.intersected.currentHex = ((MeshLambertMaterial)rs.intersected.object.getMaterial()).getColor().getHex();

				rs.plane.getPosition().copy( rs.intersected.object.getPosition() );
				rs.plane.lookAt( rs.camera.getPosition() );
			}

			getWidget().getElement().getStyle().setCursor(Cursor.POINTER);

		} else {

			if ( rs.intersected != null ) 
				((MeshLambertMaterial)rs.intersected.object.getMaterial()).getColor().setHex( rs.intersected.currentHex );

			rs.intersected = null;

			getWidget().getElement().getStyle().setCursor(Cursor.AUTO);

		}
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
