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

import thothbot.parallax.core.client.events.AnimationReadyEvent;
import thothbot.parallax.core.client.textures.Texture;
import thothbot.parallax.core.shared.cameras.PerspectiveCamera;
import thothbot.parallax.core.shared.core.Geometry;
import thothbot.parallax.core.shared.core.GeometryObject;
import thothbot.parallax.core.shared.core.Raycaster;
import thothbot.parallax.core.shared.core.Raycaster.Intersect;
import thothbot.parallax.core.shared.geometries.BoxGeometry;
import thothbot.parallax.core.shared.geometries.PlaneBufferGeometry;
import thothbot.parallax.core.shared.geometries.PlaneGeometry;
import thothbot.parallax.core.shared.lights.AmbientLight;
import thothbot.parallax.core.shared.lights.DirectionalLight;
import thothbot.parallax.core.shared.materials.LineBasicMaterial;
import thothbot.parallax.core.shared.materials.Material;
import thothbot.parallax.core.shared.materials.MeshBasicMaterial;
import thothbot.parallax.core.shared.materials.MeshLambertMaterial;
import thothbot.parallax.core.shared.math.Color;
import thothbot.parallax.core.shared.math.Matrix4;
import thothbot.parallax.core.shared.math.Vector3;
import thothbot.parallax.core.shared.objects.Line;
import thothbot.parallax.core.shared.objects.Mesh;
import thothbot.parallax.demo.client.ContentWidget;
import thothbot.parallax.demo.client.Demo;
import thothbot.parallax.demo.client.DemoAnnotations.DemoSource;

import com.google.gwt.core.client.GWT;
import com.google.gwt.core.client.RunAsyncCallback;
import com.google.gwt.event.dom.client.KeyCodes;
import com.google.gwt.event.dom.client.KeyDownEvent;
import com.google.gwt.event.dom.client.KeyDownHandler;
import com.google.gwt.event.dom.client.KeyUpEvent;
import com.google.gwt.event.dom.client.KeyUpHandler;
import com.google.gwt.event.dom.client.MouseDownEvent;
import com.google.gwt.event.dom.client.MouseDownHandler;
import com.google.gwt.event.dom.client.MouseMoveEvent;
import com.google.gwt.event.dom.client.MouseMoveHandler;
import com.google.gwt.resources.client.ImageResource;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.RootPanel;

public final class InteractiveVoxelPainter extends ContentWidget implements  MouseMoveHandler, MouseDownHandler, KeyDownHandler, KeyUpHandler
{
	/*
	 * Prepare Rendering Scene
	 */
	@DemoSource
	class DemoScene extends DemoAnimatedScene 
	{
	
		private static final String texture = "./static/textures/square-outline-textured.png";
		
		List<GeometryObject> objects = new ArrayList<GeometryObject>();
		
		PerspectiveCamera camera;
		
		Raycaster raycaster;
		
		Mesh rollOverMesh;
		Mesh plane;
		
		BoxGeometry cubeGeo;
		MeshLambertMaterial cubeMaterial;
		
		Vector3 mouse2D;
		Vector3 voxelPosition;
		Vector3 vector;
		
		boolean isShiftDown, isCtrlDown;
		
		double theta = 45;

		@Override
		protected void onStart()
		{
			camera = new PerspectiveCamera(
					45, // fov
					getRenderer().getAbsoluteAspectRation(), // aspect 
					1, // near
					10000 // far 
			);
			
			camera.getPosition().set(500, 800, 1300);
			camera.lookAt(new Vector3());
		
			// roll-over helpers

			BoxGeometry rollOverGeo = new BoxGeometry( 50, 50, 50 );
			MeshBasicMaterial rollOverMaterial = new MeshBasicMaterial();
			rollOverMaterial.setColor(new Color(0xff0000));
			rollOverMaterial.setOpacity(0.5);
			rollOverMaterial.setTransparent(true);
			rollOverMesh = new Mesh( rollOverGeo, rollOverMaterial );
			getScene().add( rollOverMesh );

			// cubes

			cubeGeo = new BoxGeometry( 50, 50, 50 );
			cubeMaterial = new MeshLambertMaterial();
			cubeMaterial.setColor(new Color(0xfeb74c));
			cubeMaterial.setAmbient(new Color(0x00ff80));
			cubeMaterial.setShading(Material.SHADING.FLAT);
			cubeMaterial.setMap(new Texture( texture ));
			cubeMaterial.setAmbient(cubeMaterial.getColor());

			// grid

			int size = 500, step = 50;

			Geometry geometry = new Geometry();

			for ( int i = - size; i <= size; i += step ) {

				geometry.getVertices().add( new Vector3( - size, 0, i ) );
				geometry.getVertices().add( new Vector3(   size, 0, i ) );

				geometry.getVertices().add( new Vector3( i, 0, - size ) );
				geometry.getVertices().add( new Vector3( i, 0,   size ) );

			}

			LineBasicMaterial material = new LineBasicMaterial();
			material.setColor(new Color(0x000000));
			material.setOpacity(0.2);
			material.setTransparent(true);
			
			Line line = new Line( geometry, material, Line.MODE.PIECES );
			getScene().add( line );
			
			//

			vector = new Vector3();
			raycaster = new Raycaster();

			PlaneBufferGeometry geometry2 = new PlaneBufferGeometry( 1000, 1000 );
			geometry2.applyMatrix( new Matrix4().makeRotationX( - Math.PI / 2 ) );

			plane = new Mesh( geometry2 );
			plane.setVisible( false );
			getScene().add( plane );

			objects.add( plane );


			// Lights

			getScene().add( new AmbientLight( 0x606060 ) );

			DirectionalLight directionalLight = new DirectionalLight( 0xffffff );
			directionalLight.getPosition().set( 1, 0.75, 0.5 ).normalize();
			getScene().add( directionalLight );

			getRenderer().setClearColor(0xf0f0f0);
			
			getRenderer().render(getScene(), camera);
		}
		
		@Override
		protected void onUpdate(double duration)
		{
		}
	}
		
	public InteractiveVoxelPainter() 
	{
		super("Voxel painter", "<strong>click</strong>: add voxel, <strong>shift + click</strong>: remove voxel");
	}
	
	@Override
	public void onAnimationReady(AnimationReadyEvent event)
	{
		super.onAnimationReady(event);
  	
	  	RootPanel.get().addDomHandler(this, KeyDownEvent.getType());
		RootPanel.get().addDomHandler(this, KeyUpEvent.getType());
		getWidget().addDomHandler(this, MouseMoveEvent.getType());
		getWidget().addDomHandler(this, MouseDownEvent.getType());
	}
	
	@Override
	public void onMouseDown(MouseDownEvent event) 
	{
		event.preventDefault();

		DemoScene rs = (DemoScene) renderingPanel.getAnimatedScene();
		
		rs.vector.set( ( event.getX() / (double) renderingPanel.getRenderer().getAbsoluteWidth() ) * 2.0 - 1.0, 
				- ( event.getY() / (double) renderingPanel.getRenderer().getAbsoluteHeight() ) * 2.0 + 1.0, 0.5 );
		
		rs.vector.unproject( rs.camera );

		rs.raycaster.getRay().set( rs.camera.getPosition(), rs.vector.sub( rs.camera.getPosition() ).normalize() );

		List<Intersect> intersects = rs.raycaster.intersectObjects( rs.objects, false );

		if ( intersects.size() > 0 ) {

			Intersect intersect = intersects.get(0);

			// delete cube

			if ( rs.isShiftDown ) {

				if ( intersect.object != rs.plane ) {

					rs.getScene().remove( intersect.object );

					rs.objects.remove( rs.objects.indexOf( intersect.object ) );

				}

			// create cube

			} else {

				Mesh voxel = new Mesh( rs.cubeGeo, rs.cubeMaterial );
				voxel.getPosition().copy( intersect.point ).add( intersect.face.getNormal() );
				voxel.getPosition().divide( 50.0 ).floor().multiply( 50.0 ).add( 25.0 );
				rs.getScene().add( voxel );

				rs.objects.add( voxel );

			}

		}
		
		rs.getRenderer().render(rs.getScene(), rs.camera);
	}

	@Override
	public void onMouseMove(MouseMoveEvent event) 
	{
		event.preventDefault();
		
		DemoScene rs = (DemoScene) renderingPanel.getAnimatedScene();
		
		rs.vector.set( ( event.getX() /(double) renderingPanel.getRenderer().getAbsoluteWidth() ) * 2.0 - 1.0, 
				- ( event.getY() /(double) renderingPanel.getRenderer().getAbsoluteHeight() ) * 2.0 + 1.0, 0.5 );
		rs.vector.unproject( rs.camera );

		rs.raycaster.getRay().set( rs.camera.getPosition(), rs.vector.sub( rs.camera.getPosition() ).normalize() );

		List<Intersect> intersects = rs.raycaster.intersectObjects( rs.objects, false );

		if ( intersects.size() > 0 ) {

			Intersect intersect = intersects.get( 0 );

			rs.rollOverMesh.getPosition().copy( intersect.point ).add( intersect.face.getNormal() );
			rs.rollOverMesh.getPosition().divide( 50.0 ).floor().multiply( 50.0 ).add( 25.0 );

		}
		
		rs.getRenderer().render(rs.getScene(), rs.camera);
	}

	@Override
	public void onKeyUp(KeyUpEvent event) 
	{
		DemoScene rs = (DemoScene) renderingPanel.getAnimatedScene();
		
		if ( event.getNativeKeyCode() == KeyCodes.KEY_SHIFT ) 
		{
			rs.isShiftDown = false;
		} 
		else if ( event.getNativeKeyCode() == KeyCodes.KEY_CTRL ) 
		{
			rs.isCtrlDown = false;
		} 
	}

	@Override
	public void onKeyDown(KeyDownEvent event) 
	{
		DemoScene rs = (DemoScene) renderingPanel.getAnimatedScene();
		
		if ( event.getNativeKeyCode() == KeyCodes.KEY_SHIFT ) 
		{
			rs.isShiftDown = true;
		} 
		else if ( event.getNativeKeyCode() == KeyCodes.KEY_CTRL ) 
		{
			rs.isCtrlDown = true;
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
		return Demo.resources.exampleInteractiveVoxelPainter();
	}
	
	@Override
	protected void asyncOnInitialize(final AsyncCallback<DemoAnimatedScene> callback)
	{
		GWT.runAsync(InteractiveVoxelPainter.class, new RunAsyncCallback() 
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
