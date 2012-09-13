/*
 * Copyright 2012 Alex Usachev, thothbot@gmail.com
 * 
 * This file based on the JavaScript source file of the THREE.JS project, 
 * licensed under MIT License.
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

import thothbot.parallax.core.client.AnimationReadyEvent;
import thothbot.parallax.core.client.context.Canvas3d;
import thothbot.parallax.core.shared.cameras.Camera;
import thothbot.parallax.core.shared.cameras.OrthographicCamera;
import thothbot.parallax.core.shared.cameras.PerspectiveCamera;
import thothbot.parallax.core.shared.core.Color;
import thothbot.parallax.core.shared.core.Geometry;
import thothbot.parallax.core.shared.core.Mathematics;
import thothbot.parallax.core.shared.core.Vector3;
import thothbot.parallax.core.shared.geometries.Sphere;
import thothbot.parallax.core.shared.helpers.CameraHelper;
import thothbot.parallax.core.shared.materials.MeshBasicMaterial;
import thothbot.parallax.core.shared.materials.ParticleBasicMaterial;
import thothbot.parallax.core.shared.objects.Mesh;
import thothbot.parallax.core.shared.objects.Object3D;
import thothbot.parallax.core.shared.objects.ParticleSystem;
import thothbot.parallax.demo.client.ContentWidget;
import thothbot.parallax.demo.client.Demo;
import thothbot.parallax.demo.client.DemoAnnotations.DemoSource;

import com.google.gwt.core.client.GWT;
import com.google.gwt.core.client.RunAsyncCallback;
import com.google.gwt.event.dom.client.KeyDownEvent;
import com.google.gwt.event.dom.client.KeyDownHandler;
import com.google.gwt.resources.client.ImageResource;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.RequiresResize;
import com.google.gwt.user.client.ui.RootPanel;

public class CamerasExample extends ContentWidget implements RequiresResize
{
	 
	/*
	 * Prepare Rendering Scene
	 */
	@DemoSource
	class DemoScene extends DemoAnimatedScene 
	{	
		public Camera activeCamera;

		public PerspectiveCamera cameraPerspective;
		private CameraHelper cameraPerspectiveHelper;
		
		public OrthographicCamera cameraOrtho;
		private CameraHelper cameraOrthoHelper;
		
		private Object3D cameraRig;
		
		private Mesh mesh;
		
		private double r = 0.0;

		@Override
		protected void loadCamera()
		{
			Canvas3d canvas = getRenderer().getCanvas();
			
			setCamera(new PerspectiveCamera( 
					50, 
					0.5 * canvas.getAspectRation(), 
					1, 
					10000 ));
			
			this.cameraPerspective = new PerspectiveCamera( 
					50, 
					canvas.getAspectRation() * 0.5, 
					150, 
					1000 );
			
			this.cameraOrtho = new OrthographicCamera( 0.5 * canvas.getWidth() / - 2.0, 
					0.5 * canvas.getWidth() / 2.0, 
					canvas.getHeight() / 2.0, 
					canvas.getHeight() / - 2.0, 
					150, 
					1000 );
		}
		
		@Override
		protected void onResize() 
		{
			super.onResize();
			Canvas3d canvas = getRenderer().getCanvas();

			cameraPerspective.setAspectRatio(0.5 * canvas.getAspectRation());

			cameraOrtho.setLeft(- 0.5 * canvas.getWidth() / 2.0 );
			cameraOrtho.setRight( 0.5 * canvas.getWidth() / 2.0 );
			cameraOrtho.setTop( canvas.getHeight() / 2.0 );
			cameraOrtho.setBottom(- canvas.getHeight() / 2.0 );
			cameraOrtho.updateProjectionMatrix();
		}

		@Override
		protected void onStart()
		{
			getCamera().getPosition().setZ(2500);
			getScene().add( getCamera() );
					
			this.cameraPerspectiveHelper = new CameraHelper( this.cameraPerspective );
			this.cameraPerspective.add( this.cameraPerspectiveHelper );

			this.cameraOrthoHelper = new CameraHelper( this.cameraOrtho );
			this.cameraOrtho.add( this.cameraOrthoHelper );
	
			//
			
			this.activeCamera = this.cameraPerspective;
			
			// counteract different front orientation of cameras vs rig
	
			cameraOrtho.getRotation().setY(Math.PI);
			cameraPerspective.getRotation().setY(Math.PI);
	
			this.cameraRig = new Object3D();
	
			cameraRig.add( cameraPerspective );
			cameraRig.add( cameraOrtho );
	
			getScene().add( cameraRig );
			
			//
	
			MeshBasicMaterial  bopt0 = new MeshBasicMaterial();
			bopt0.setColor( new Color(0xffffff) );
			bopt0.setWireframe(true);
			
			this.mesh = new Mesh( new Sphere( 100, 16, 8 ), bopt0);
			getScene().add( mesh );
	
			MeshBasicMaterial  bopt1 = new MeshBasicMaterial();
			bopt1.setColor( new Color(0x00ff00) );
			bopt1.setWireframe(true);
			Mesh mesh2 = new Mesh( new Sphere( 50, 16, 8 ), bopt1);
			mesh2.getPosition().setY(150);
			mesh.add( mesh2 );
	
			MeshBasicMaterial  bopt2 = new MeshBasicMaterial();
			bopt2.setColor( new Color(0x0000ff) );
			Mesh mesh3 = new Mesh( new Sphere( 5, 16, 8 ), bopt2);
			mesh3.getPosition().setZ(150);
			cameraRig.add( mesh3 );
	
			//
			
			Geometry geometry = new Geometry();
	
			for ( int i = 0; i < 10000; i ++ ) 
			{
				Vector3 vertex = new Vector3();
				vertex.setX(Mathematics.randFloatSpread( 3000.0 ));
				vertex.setY(Mathematics.randFloatSpread( 3000.0 ));
				vertex.setZ(Mathematics.randFloatSpread( 3000.0 ));
	
				geometry.getVertices().add( vertex );
			}
			
			ParticleBasicMaterial popt = new ParticleBasicMaterial();
			popt.setColor( new Color(0xDDDDDD) );
	
			ParticleSystem particles = new ParticleSystem( geometry, popt );
			getScene().add( particles );
	
			//
	
			getRenderer().setAutoClear(false);
		}
		
		@Override
		protected void onStop()
		{			
		}
		
		@Override
		protected void onUpdate(double duration)
		{
			mesh.getPosition().setX(700.0 * Math.cos( r ));
			mesh.getPosition().setZ(700.0 * Math.sin( r ));
			mesh.getPosition().setY(700.0 * Math.sin( r ));

			mesh.getChildren().get( 0 ).getPosition().setX(70.0 * Math.cos( 2.0 * r ));
			mesh.getChildren().get( 0 ).getPosition().setZ(70.0 * Math.sin( r ));

			if ( activeCamera.equals(cameraPerspective) ) 
			{
				cameraPerspective.setFieldOfView(35.0 + 30.0 * Math.sin( 0.5 * r ));
				cameraPerspective.setFar(mesh.getPosition().length());
				cameraPerspective.updateProjectionMatrix();

				cameraPerspectiveHelper.update();
				cameraPerspectiveHelper.getLine().setVisible(true);

				cameraOrthoHelper.getLine().setVisible(false);
			} 
			else 
			{
				cameraOrtho.setFar(mesh.getPosition().length());
				cameraOrtho.updateProjectionMatrix();

				cameraOrthoHelper.update();
				cameraOrthoHelper.getLine().setVisible(true);

				cameraPerspectiveHelper.getLine().setVisible(false);
			}

			cameraRig.lookAt( mesh.getPosition() );

			getRenderer().clear(false, false, false);

			getRenderer().setViewport( 0, 0, getRenderer().getCanvas().getWidth()/2, getRenderer().getCanvas().getHeight() );
			getRenderer().render( getScene(), activeCamera );

			getRenderer().setViewport( getRenderer().getCanvas().getWidth()/2, 0, 
					getRenderer().getCanvas().getWidth()/2, getRenderer().getCanvas().getWidth() );

			r += 0.01;
		}
	}
	
	public CamerasExample() 
	{
		super("Cameras", "Here is show how to split viewport to two and use different cameras for each. Use: [O] - orthographic camera,  [P] - perspective camera. This example based on the three.js example.");
	}

	@Override
	public void onAnimationReady(AnimationReadyEvent event)
	{
		super.onAnimationReady(event);
		
		RootPanel.get().addDomHandler(new KeyDownHandler() { 
			
			@Override
			public void onKeyDown(KeyDownEvent event) 
			{
				DemoScene rs = (DemoScene) renderingPanel.getAnimatedScene();
				switch(event.getNativeEvent().getKeyCode())
				{
				case 79: case 111:/*O*/	rs.activeCamera = rs.cameraOrtho; break;
				case 80: case 112:/*P*/ rs.activeCamera = rs.cameraPerspective; break;
				}		
				
			}
		}, KeyDownEvent.getType()); 
	}

	@Override
	public DemoScene onInitialize()
	{
		return new DemoScene();
	}
	
	@Override
	public ImageResource getIcon()
	{
		return Demo.resources.exampleCameras();
	}
	
	@Override
	protected void asyncOnInitialize(final AsyncCallback<DemoAnimatedScene> callback)
	{
		GWT.runAsync(CamerasExample.class, new RunAsyncCallback() 
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
