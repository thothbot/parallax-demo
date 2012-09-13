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

import thothbot.parallax.core.client.controls.TrackballControls;
import thothbot.parallax.core.shared.cameras.PerspectiveCamera;
import thothbot.parallax.core.shared.lights.AmbientLight;
import thothbot.parallax.core.shared.lights.SpotLight;
import thothbot.parallax.demo.client.ContentWidget;
import thothbot.parallax.demo.client.Demo;
import thothbot.parallax.demo.client.DemoAnnotations.DemoSource;

import com.google.gwt.core.client.GWT;
import com.google.gwt.core.client.RunAsyncCallback;
import com.google.gwt.resources.client.ImageResource;
import com.google.gwt.user.client.rpc.AsyncCallback;

public final class InteractiveDraggableCubes extends ContentWidget 
{
	/*
	 * Prepare Rendering Scene
	 */
	@DemoSource
	class DemoScene extends DemoAnimatedScene 
	{

		TrackballControls controls;
		
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

			var geometry = new THREE.CubeGeometry( 40, 40, 40 );

			for ( var i = 0; i < 200; i ++ ) {

				var object = new THREE.Mesh( geometry, new THREE.MeshLambertMaterial( { color: Math.random() * 0xffffff } ) );

				object.material.ambient = object.material.color;

				object.position.x = Math.random() * 1000 - 500;
				object.position.y = Math.random() * 600 - 300;
				object.position.z = Math.random() * 800 - 400;

				object.rotation.x = ( Math.random() * 360 ) * Math.PI / 180;
				object.rotation.y = ( Math.random() * 360 ) * Math.PI / 180;
				object.rotation.z = ( Math.random() * 360 ) * Math.PI / 180;

				object.scale.x = Math.random() * 2 + 1;
				object.scale.y = Math.random() * 2 + 1;
				object.scale.z = Math.random() * 2 + 1;

				object.castShadow = true;
				object.receiveShadow = true;

				scene.add( object );

				objects.push( object );

			}

			plane = new THREE.Mesh( new THREE.PlaneGeometry( 2000, 2000, 8, 8 ), new THREE.MeshBasicMaterial( { color: 0x000000, opacity: 0.25, transparent: true, wireframe: true } ) );
			plane.visible = false;
			scene.add( plane );

			projector = new THREE.Projector();

			renderer = new THREE.WebGLRenderer( { antialias: true } );
			renderer.sortObjects = false;
			renderer.setSize( window.innerWidth, window.innerHeight );

			renderer.shadowMapEnabled = true;
			renderer.shadowMapSoft = true;

			container.appendChild( renderer.domElement );

			var info = document.createElement( 'div' );
			info.style.position = 'absolute';
			info.style.top = '10px';
			info.style.width = '100%';
			info.style.textAlign = 'center';
			info.innerHTML = '<a href="http://github.com/mrdoob/three.js" target="_blank">three.js</a> webgl - draggable cubes';
			container.appendChild( info );

			stats = new Stats();
			stats.domElement.style.position = 'absolute';
			stats.domElement.style.top = '0px';
			container.appendChild( stats.domElement );

			renderer.domElement.addEventListener( 'mousemove', onDocumentMouseMove, false );
			renderer.domElement.addEventListener( 'mousedown', onDocumentMouseDown, false );
			renderer.domElement.addEventListener( 'mouseup', onDocumentMouseUp, false );

			//

			window.addEventListener( 'resize', onWindowResize, false );

		}
		
		@Override
		protected void onStop()
		{			
		}
		
		@Override
		protected void onUpdate(double duration)
		{

		}
	}
		
	public InteractiveDraggableCubes() 
	{
		super("Draggable cubes", "This example based on the three.js example.");
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
