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

package org.parallax3d.parallax.demo.content.interactivity;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import thothbot.parallax.core.client.controls.TrackballControls;
import thothbot.parallax.core.client.events.AnimationReadyEvent;
import thothbot.parallax.core.shared.cameras.PerspectiveCamera;
import thothbot.parallax.core.shared.core.FastMap;
import thothbot.parallax.core.shared.core.GeometryObject;
import thothbot.parallax.core.shared.core.Raycaster;
import thothbot.parallax.core.shared.geometries.BoxGeometry;
import thothbot.parallax.core.shared.geometries.PlaneBufferGeometry;
import thothbot.parallax.core.shared.lights.AmbientLight;
import thothbot.parallax.core.shared.lights.SpotLight;
import thothbot.parallax.core.shared.materials.MeshBasicMaterial;
import thothbot.parallax.core.shared.materials.MeshLambertMaterial;
import thothbot.parallax.core.shared.math.Color;
import thothbot.parallax.core.shared.math.Vector3;
import thothbot.parallax.core.shared.objects.Mesh;
import org.parallax3d.parallax.demo.client.ContentWidget;
import org.parallax3d.parallax.demo.client.DemoAnnotations.DemoSource;

import com.google.gwt.core.client.GWT;
import com.google.gwt.core.client.RunAsyncCallback;
import com.google.gwt.dom.client.Style.Cursor;
import com.google.gwt.event.dom.client.MouseDownEvent;
import com.google.gwt.event.dom.client.MouseDownHandler;
import com.google.gwt.event.dom.client.MouseMoveEvent;
import com.google.gwt.event.dom.client.MouseMoveHandler;
import com.google.gwt.event.dom.client.MouseUpEvent;
import com.google.gwt.event.dom.client.MouseUpHandler;
import com.google.gwt.user.client.rpc.AsyncCallback;

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
		
		List<GeometryObject> objects;
		Mesh plane;
		
		TrackballControls controls;
		
		GeometryObject intersected;
		GeometryObject selected;
		
		Map<String, Integer> currentHex = GWT.isScript() ? 
				new FastMap<Integer>() : new HashMap<String, Integer>();

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

			BoxGeometry geometry = new BoxGeometry( 40, 40, 40 );

			objects = new ArrayList<GeometryObject>();
			for ( int i = 0; i < 200; i ++ ) 
			{
				MeshLambertMaterial material1 = new MeshLambertMaterial();
				material1.setColor(new Color( (int)(Math.random() * 0xffffff) ));
				material1.setAmbient(material1.getColor());
				Mesh object = new Mesh( geometry, material1 );

				object.getPosition().setX( Math.random() * 1000 - 500 );
				object.getPosition().setY( Math.random() * 600 - 300 );
				object.getPosition().setZ( Math.random() * 800 - 400 );

				object.getRotation().setX( Math.random() * 2 * Math.PI );
				object.getRotation().setY( Math.random() * 2 * Math.PI ); 
				object.getRotation().setZ( Math.random() * 2 * Math.PI );

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
			plane = new Mesh( new PlaneBufferGeometry( 2000, 2000, 8, 8 ), material2 );
			plane.setVisible(false);
			getScene().add( plane );

			getRenderer().setClearColor(0xeeeeee);
			getRenderer().setSortObjects(false);
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

		renderingPanel.getCanvas().addDomHandler(this, MouseMoveEvent.getType());
		renderingPanel.getCanvas().addDomHandler(this, MouseDownEvent.getType());
		renderingPanel.getCanvas().addDomHandler(this, MouseUpEvent.getType());
	}
	
	@Override
	public void onMouseUp(MouseUpEvent event) 
	{
		event.preventDefault();

		DemoScene rs = (DemoScene) renderingPanel.getAnimatedScene();
		
		rs.controls.setEnabled(true);

		if ( rs.intersected != null ) 
		{
			rs.plane.getPosition().copy( rs.intersected.getPosition() );

			rs.selected = null;
		}

		getWidget().getElement().getStyle().setCursor(Cursor.AUTO);	
	}

	@Override
	public void onMouseDown(MouseDownEvent event) 
	{
		event.preventDefault();

		DemoScene rs = (DemoScene) renderingPanel.getAnimatedScene();
		
		Vector3 vector = new Vector3( rs.mouseDeltaX, rs.mouseDeltaY, 0.5 ).unproject(rs.camera);

		Raycaster raycaster = new Raycaster( rs.camera.getPosition(), vector.sub( rs.camera.getPosition() ).normalize() );
		
		List<Raycaster.Intersect> intersects = raycaster.intersectObjects( rs.objects, false );

		if ( intersects.size() > 0 ) 
		{
			rs.controls.setEnabled(false);

			rs.selected = intersects.get( 0 ).object; 

			List<Raycaster.Intersect>  intersects2 = raycaster.intersectObject( rs.plane, false );
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
		rs.mouseDeltaY = - (event.getY() / (double)renderingPanel.getRenderer().getAbsoluteHeight() ) * 2.0 + 1.0;

		//

		Vector3 vector = new Vector3( rs.mouseDeltaX, rs.mouseDeltaY, 0.5 ).unproject( rs.camera );

		Raycaster raycaster = new Raycaster( rs.camera.getPosition(), vector.sub( rs.camera.getPosition() ).normalize() );

		if ( rs.selected != null ) 
		{
			List<Raycaster.Intersect> intersects = raycaster.intersectObject( rs.plane, false );
			rs.selected.getPosition().copy( intersects.get( 0 ).point.sub( rs.offset ) );
			return;
		}

		List<Raycaster.Intersect> intersects = raycaster.intersectObjects( rs.objects, false );

		if ( intersects.size() > 0 ) 
		{
			if ( rs.intersected != intersects.get(0).object )
			{
				if ( rs.intersected != null )
				{
					((MeshLambertMaterial)rs.intersected.getMaterial()).getColor().setHex( rs.currentHex.get(rs.intersected.getId() + "") );
				}

				rs.intersected = intersects.get(0).object;
				rs.currentHex.put(rs.intersected.getId() + "", ((MeshLambertMaterial)rs.intersected.getMaterial()).getColor().getHex());

				rs.plane.getPosition().copy( rs.intersected.getPosition() );
				rs.plane.lookAt( rs.camera.getPosition() );
			}

			getWidget().getElement().getStyle().setCursor(Cursor.POINTER);

		} else {

			if ( rs.intersected != null ) 
				((MeshLambertMaterial)rs.intersected.getMaterial()).getColor().setHex(  rs.currentHex.get(rs.intersected.getId() + "") );

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
