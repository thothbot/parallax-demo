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

package thothbot.parallax.demo.client.content.geometries;

import thothbot.parallax.core.client.events.AnimationReadyEvent;
import thothbot.parallax.core.client.events.ViewportResizeEvent;
import thothbot.parallax.core.client.events.ViewportResizeHandler;
import thothbot.parallax.core.shared.cameras.Camera;
import thothbot.parallax.core.shared.cameras.OrthographicCamera;
import thothbot.parallax.core.shared.cameras.PerspectiveCamera;
import thothbot.parallax.core.shared.core.Color;
import thothbot.parallax.core.shared.core.Geometry;
import thothbot.parallax.core.shared.core.Mathematics;
import thothbot.parallax.core.shared.core.Vector3;
import thothbot.parallax.core.shared.geometries.SphereGeometry;
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

public class Cameras extends ContentWidget implements RequiresResize
{
	 
	/*
	 * Prepare Rendering Scene
	 */
	@DemoSource
	class DemoScene extends DemoAnimatedScene 
	{	
		Camera activeCamera;
		CameraHelper activeHelper;

		PerspectiveCamera camera;
		PerspectiveCamera cameraPerspective;
		CameraHelper cameraPerspectiveHelper;
		
		OrthographicCamera cameraOrtho;
		CameraHelper cameraOrthoHelper;
		
		Object3D cameraRig;
		
		Mesh mesh;

		@Override
		protected void onStart()
		{
			camera = new PerspectiveCamera( 
					50, 
					0.5 * getRenderer().getAbsoluteAspectRation(), 
					1, 
					10000 );
			
			camera.getPosition().setZ(2500);
			camera.addWebGlResizeEventHandler(new ViewportResizeHandler() {
				
				@Override
				public void onResize(ViewportResizeEvent event) {
					camera.setAspectRatio(0.5 * event.getRenderer().getAbsoluteAspectRation());
					
				}
			});

			cameraPerspective = new PerspectiveCamera( 
					50, 
					getRenderer().getAbsoluteAspectRation() * 0.5, 
					150, 
					1000 );
			cameraPerspective.addWebGlResizeEventHandler(new ViewportResizeHandler() {
				
				@Override
				public void onResize(ViewportResizeEvent event) {
					cameraPerspective.setAspectRatio(0.5 * event.getRenderer().getAbsoluteAspectRation());
					
				}
			});
			
			
			cameraOrtho = new OrthographicCamera( 0.5 * getRenderer().getAbsoluteWidth(), getRenderer().getAbsoluteHeight(), 150, 1000 );
			cameraOrtho.addWebGlResizeEventHandler(new ViewportResizeHandler() {
				
				@Override
				public void onResize(ViewportResizeEvent event) {
					cameraOrtho.setSize(0.5 * event.getRenderer().getAbsoluteWidth(), event.getRenderer().getAbsoluteHeight() );
					
				}
			});
			
			this.cameraPerspectiveHelper = new CameraHelper( this.cameraPerspective );
			getScene().add( this.cameraPerspectiveHelper );

			this.cameraOrthoHelper = new CameraHelper( this.cameraOrtho );
			getScene().add( this.cameraOrthoHelper );

			//
			
			this.activeCamera = this.cameraPerspective;
			this.activeHelper = this.cameraPerspectiveHelper;
			
			// counteract different front orientation of cameras vs rig
	
			this.cameraOrtho.getRotation().setY(Math.PI);
			this.cameraPerspective.getRotation().setY(Math.PI);
	
			this.cameraRig = new Object3D();
	
			this.cameraRig.add( this.cameraPerspective );
			this.cameraRig.add( this.cameraOrtho );
	
			getScene().add( this.cameraRig );
			
			//
	
			MeshBasicMaterial  bopt0 = new MeshBasicMaterial();
			bopt0.setColor( new Color(0xffffff) );
			bopt0.setWireframe(true);
			
			this.mesh = new Mesh( new SphereGeometry( 100, 16, 8 ), bopt0);
			getScene().add( mesh );
	
			MeshBasicMaterial  bopt1 = new MeshBasicMaterial();
			bopt1.setColor( new Color(0x00ff00) );
			bopt1.setWireframe(true);
			Mesh mesh2 = new Mesh( new SphereGeometry( 50, 16, 8 ), bopt1);
			mesh2.getPosition().setY(150);
			mesh.add( mesh2 );
	
			MeshBasicMaterial  bopt2 = new MeshBasicMaterial();
			bopt2.setColor( new Color(0x0000ff) );
			Mesh mesh3 = new Mesh( new SphereGeometry( 5, 16, 8 ), bopt2);
			mesh3.getPosition().setZ(150);
			cameraRig.add( mesh3 );
	
			//
			
			Geometry geometry = new Geometry();
	
			for ( int i = 0; i < 10000; i ++ ) 
			{
				Vector3 vertex = new Vector3();
				vertex.setX(Mathematics.randFloatSpread( 2000.0 ));
				vertex.setY(Mathematics.randFloatSpread( 2000.0 ));
				vertex.setZ(Mathematics.randFloatSpread( 2000.0 ));
	
				geometry.getVertices().add( vertex );
			}
			
			ParticleBasicMaterial popt = new ParticleBasicMaterial();
			popt.setColor( new Color(0x888888) );
	
			ParticleSystem particles = new ParticleSystem( geometry, popt );
			getScene().add( particles );
	
			//
	
			getRenderer().setAutoClear(false);
		}
		
		@Override
		protected void onUpdate(double duration)
		{
			double r = duration * 0.0005;
			
			mesh.getPosition().setX(700 * Math.cos( r ));
			mesh.getPosition().setZ(700 * Math.sin( r ));
			mesh.getPosition().setY(700 * Math.sin( r ));

			mesh.getChildren().get( 0 ).getPosition().setX(70.0 * Math.cos( 2.0 * r ));
			mesh.getChildren().get( 0 ).getPosition().setZ(70.0 * Math.sin( r ));

			if ( activeCamera.equals(cameraPerspective) ) 
			{
				cameraPerspective.setFieldOfView(35.0 + 30.0 * Math.sin( 0.5 * r ));
				cameraPerspective.setFar(mesh.getPosition().length());
				cameraPerspective.updateProjectionMatrix();

				cameraPerspectiveHelper.update();
				cameraPerspectiveHelper.setVisible(true);

				cameraOrthoHelper.setVisible(false);
			} 
			else 
			{
				cameraOrtho.setFar(mesh.getPosition().length());
				cameraOrtho.updateProjectionMatrix();

				cameraOrthoHelper.update();
				cameraOrthoHelper.setVisible(true);

				cameraPerspectiveHelper.setVisible(false);
			}

			cameraRig.lookAt( mesh.getPosition() );

			getRenderer().clear();

			activeHelper.setVisible(false);
			
			getRenderer().setViewport( 0, 0, getRenderer().getAbsoluteWidth() / 2, getRenderer().getAbsoluteHeight() );

			getRenderer().render( getScene(), activeCamera );

			activeHelper.setVisible(true);
			
			getRenderer().setViewport( getRenderer().getAbsoluteWidth() / 2, 0, getRenderer().getAbsoluteWidth() / 2, getRenderer().getAbsoluteHeight() );
			getRenderer().render(getScene(), camera);
		}
	}
	
	public Cameras() 
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
				case 79: case 111:/*O*/	
					rs.activeCamera = rs.cameraOrtho;
					rs.activeHelper = rs.cameraOrthoHelper;
					break;
				case 80: case 112:/*P*/ 
					rs.activeCamera = rs.cameraPerspective;
					rs.activeHelper = rs.cameraPerspectiveHelper;
					break;
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
		GWT.runAsync(Cameras.class, new RunAsyncCallback() 
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
