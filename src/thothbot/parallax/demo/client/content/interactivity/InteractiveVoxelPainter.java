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

import java.util.List;

import thothbot.parallax.core.client.context.Canvas3d;
import thothbot.parallax.core.client.events.AnimationReadyEvent;
import thothbot.parallax.core.client.textures.Texture;
import thothbot.parallax.core.shared.cameras.PerspectiveCamera;
import thothbot.parallax.core.shared.core.Projector;
import thothbot.parallax.core.shared.core.Raycaster;
import thothbot.parallax.core.shared.geometries.CubeGeometry;
import thothbot.parallax.core.shared.geometries.PlaneGeometry;
import thothbot.parallax.core.shared.lights.AmbientLight;
import thothbot.parallax.core.shared.lights.DirectionalLight;
import thothbot.parallax.core.shared.materials.Material.SHADING;
import thothbot.parallax.core.shared.materials.MeshBasicMaterial;
import thothbot.parallax.core.shared.materials.MeshLambertMaterial;
import thothbot.parallax.core.shared.math.Color;
import thothbot.parallax.core.shared.math.Mathematics;
import thothbot.parallax.core.shared.math.Ray;
import thothbot.parallax.core.shared.math.Vector3;
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
		
		PerspectiveCamera camera;
		
		Projector projector;
		Raycaster raycaster;
		
		Mesh rollOverMesh;
		Mesh plane;
		
		CubeGeometry cubeGeo;
		MeshLambertMaterial cubeMaterial;
		
		Vector3 mouse2D;
		Vector3 voxelPosition;
		Vector3 tmpVec;
		
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
			
			camera.getPosition().set(1000, 800, 1000);

			projector = new Projector();
			mouse2D = new Vector3( 0, 10000, 0.5 );
			voxelPosition = new Vector3();
			tmpVec = new Vector3();
			
			// roll-over helpers

			CubeGeometry rollOverGeo = new CubeGeometry( 50, 50, 50 );
			MeshBasicMaterial rollOverMaterial = new MeshBasicMaterial();
			rollOverMaterial.setColor(new Color(0xff0000));
			rollOverMaterial.setOpacity(0.5);
			rollOverMaterial.setTransparent(true);
			rollOverMesh = new Mesh( rollOverGeo, rollOverMaterial );
			getScene().add( rollOverMesh );

			// cubes

			cubeGeo = new CubeGeometry( 50, 50, 50 );
			cubeMaterial = new MeshLambertMaterial();
			cubeMaterial.setShading(SHADING.FLAT);
			cubeMaterial.setMap(new Texture( texture ));
			cubeMaterial.getColor().setHSV( 0.1, 0.7, 1.0 );
			cubeMaterial.setAmbient(cubeMaterial.getColor());			

			// grid

			MeshBasicMaterial planeMaterial = new MeshBasicMaterial();
			planeMaterial.setColor(new Color(0x555555));
			planeMaterial.setWireframe(true);
			plane = new Mesh( new PlaneGeometry( 1000, 1000, 20, 20 ), planeMaterial );
			plane.getRotation().setX( - Math.PI / 2 );
			getScene().add( plane );	

			// Lights

			getScene().add( new AmbientLight( 0x606060 ) );

			DirectionalLight directionalLight = new DirectionalLight( 0xffffff );
			directionalLight.getPosition().set( 1, 0.75, 0.5 ).normalize();
			getScene().add( directionalLight );

			getRenderer().setClearColorHex(0xeeeeee);
		}
		
		@Override
		protected void onUpdate(double duration)
		{
			if ( isShiftDown ) 
			{
				theta += mouse2D.getX() * 3;
			}

			raycaster = projector.pickingRay( mouse2D.clone(), camera );

			List<Raycaster.Intersect> intersects = raycaster.intersectObjects( getScene().getChildren() );

			if ( intersects.size() > 0 ) 
			{

				Raycaster.Intersect intersector = getRealIntersector( intersects );
				if ( intersector != null ) 
				{
					setVoxelPosition( intersector );
					rollOverMesh.setPosition( voxelPosition );
				}
			}

			camera.getPosition().setX( 1400 * Math.sin( Mathematics.degToRad( theta ) ) );
			camera.getPosition().setZ( 1400 * Math.cos( Mathematics.degToRad( theta ) ) );

			camera.lookAt( getScene().getPosition() );
			
			getRenderer().render(getScene(), camera);
		}
		
		public Raycaster.Intersect getRealIntersector( List<Raycaster.Intersect> intersects ) 
		{
			for( int i = 0; i < intersects.size(); i++ ) 
			{
				Raycaster.Intersect intersector = intersects.get( i );

				if ( intersector.object != rollOverMesh ) 
				{
					return intersector;
				}
			}

			return null;
		}

		public void  setVoxelPosition( Raycaster.Intersect intersector ) 
		{
			tmpVec.copy( intersector.face.getNormal() );
			tmpVec.apply( intersector.object.getMatrixRotationWorld() );

			voxelPosition.add( intersector.point, intersector.object.getMatrixRotationWorld().multiplyVector3( tmpVec ) );

			voxelPosition.setX( Math.floor( voxelPosition.getX() / 50 ) * 50 + 25 );
			voxelPosition.setY( Math.floor( voxelPosition.getY() / 50 ) * 50 + 25 );
			voxelPosition.setZ( Math.floor( voxelPosition.getZ() / 50 ) * 50 + 25 );
		}
	}
		
	public InteractiveVoxelPainter() 
	{
		super("Voxel painter", "Add voxel: [click]; Remove voxel: [control + click]; Rotate: [shift + mouse]. This example based on the three.js example.");
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
		
		List<Raycaster.Intersect> intersects = rs.raycaster.intersectObjects( rs.getScene().getChildren() );

		if ( intersects.size() > 0 ) 
		{
			Raycaster.Intersect intersector = rs.getRealIntersector( intersects );

			// delete cube
			if ( rs.isCtrlDown ) 
			{
				if ( intersector.object != rs.plane ) 
				{
					rs.getScene().remove( intersector.object );
				}
			}
			// create cube
			else 
			{
				rs.setVoxelPosition( intersector );

				Mesh voxel = new Mesh( rs.cubeGeo, rs.cubeMaterial );
				voxel.getPosition().copy( rs.voxelPosition );
				voxel.setMatrixAutoUpdate(false);
				voxel.updateMatrix();
				rs.getScene().add( voxel );
			}
		}
	}

	@Override
	public void onMouseMove(MouseMoveEvent event) 
	{
		event.preventDefault();
		
		DemoScene rs = (DemoScene) renderingPanel.getAnimatedScene();
	  	
	  	rs.mouse2D.setX( (event.getX() /(double) renderingPanel.getRenderer().getAbsoluteWidth() ) * 2.0 - 1.0 ); 
	  	rs.mouse2D.setY( - (event.getY() /(double) renderingPanel.getRenderer().getAbsoluteHeight() ) * 2.0 + 1.0 );	
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
