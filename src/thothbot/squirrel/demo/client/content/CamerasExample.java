/*
 * Copyright 2012 Alex Usachev, thothbot@gmail.com
 * 
 * This file based on the JavaScript source file of the THREE.JS project, 
 * licensed under MIT License.
 * 
 * This file is part of Squirrel project.
 * 
 * Squirrel is free software: you can redistribute it and/or modify it 
 * under the terms of the GNU General Public License as published by the 
 * Free Software Foundation, either version 3 of the License, or (at your 
 * option) any later version.
 * 
 * Squirrel is distributed in the hope that it will be useful, but 
 * WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY 
 * or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License 
 * for more details.
 * 
 * You should have received a copy of the GNU General Public License along with 
 * Squirrel. If not, see http://www.gnu.org/licenses/.
 */


package thothbot.squirrel.demo.client.content;

import thothbot.squirrel.core.client.RenderingPanel;
import thothbot.squirrel.core.client.RenderingReadyEvent;
import thothbot.squirrel.core.client.context.Canvas3d;
import thothbot.squirrel.core.shared.cameras.Camera;
import thothbot.squirrel.core.shared.cameras.OrthographicCamera;
import thothbot.squirrel.core.shared.cameras.PerspectiveCamera;
import thothbot.squirrel.core.shared.core.Color3f;
import thothbot.squirrel.core.shared.core.Geometry;
import thothbot.squirrel.core.shared.core.Mathematics;
import thothbot.squirrel.core.shared.core.Vector3f;
import thothbot.squirrel.core.shared.geometries.Sphere;
import thothbot.squirrel.core.shared.helpers.CameraHelper;
import thothbot.squirrel.core.shared.materials.MeshBasicMaterial;
import thothbot.squirrel.core.shared.materials.ParticleBasicMaterial;
import thothbot.squirrel.core.shared.objects.Mesh;
import thothbot.squirrel.core.shared.objects.Object3D;
import thothbot.squirrel.core.shared.objects.ParticleSystem;
import thothbot.squirrel.demo.client.ContentWidget;
import thothbot.squirrel.demo.client.Demo;
import thothbot.squirrel.demo.client.DemoAnnotations.DemoSource;

import com.google.gwt.core.client.GWT;
import com.google.gwt.core.client.RunAsyncCallback;
import com.google.gwt.event.dom.client.KeyDownEvent;
import com.google.gwt.event.dom.client.KeyDownHandler;
import com.google.gwt.resources.client.ImageResource;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.RootPanel;

public class CamerasExample extends ContentWidget
{
	 
	/*
	 * Prepare Rendering Scene
	 */
	@DemoSource
	class DemoScene extends DemoRenderingScene 
	{	
		public Camera activeCamera;

		public PerspectiveCamera cameraPerspective;
		private CameraHelper cameraPerspectiveHelper;
		
		public OrthographicCamera cameraOrtho;
		private CameraHelper cameraOrthoHelper;
		
		private Object3D cameraRig;
		
		private Mesh mesh;
		
		private float r = 0.0f;

		@Override
		protected void loadCamera()
		{
			setCamera(new PerspectiveCamera( 50f, 0.5f * getRenderer().getCanvas().getAspectRation(), 1f, 10000f ));
		}

		@Override
		protected void onStart()
		{
			getCamera().getPosition().setZ(2500);
			getScene().addChild( getCamera() );
			
			Canvas3d canvas = getRenderer().getCanvas();
	
			this.cameraPerspective = new PerspectiveCamera( 50f, canvas.getAspectRation() * 0.5f, 150f, 1000f );
	
			this.cameraPerspectiveHelper = new CameraHelper( this.cameraPerspective );
			this.cameraPerspective.addChild( this.cameraPerspectiveHelper );
			
			//
	
			this.cameraOrtho = new OrthographicCamera( 0.5f * canvas.getWidth() / - 2f, 
					0.5f * canvas.getWidth() / 2f, 
					canvas.getHeight() / 2f, 
					canvas.getHeight() / - 2f, 
					150f, 
					1000f );
	
			this.cameraOrthoHelper = new CameraHelper( this.cameraOrtho );
			this.cameraOrtho.addChild( this.cameraOrthoHelper );
	
			//
			
			this.activeCamera = this.cameraPerspective;
			
			// counteract different front orientation of cameras vs rig
	
			cameraOrtho.getRotation().setY((float) Math.PI);
			cameraPerspective.getRotation().setY((float) Math.PI);
	
			this.cameraRig = new Object3D();
	
			cameraRig.addChild( cameraPerspective );
			cameraRig.addChild( cameraOrtho );
	
			getScene().addChild( cameraRig );
			
			//
	
			MeshBasicMaterial.MeshBasicMaterialOptions bopt0 = new MeshBasicMaterial.MeshBasicMaterialOptions();
			bopt0.color = new Color3f(0xffffff);
			bopt0.wireframe = true;
			
			this.mesh = new Mesh( new Sphere( 100, 16, 8 ), new MeshBasicMaterial( bopt0 ) );
			getScene().addChild( mesh );
	
			bopt0.color = new Color3f(0x00ff00);
			Mesh mesh2 = new Mesh( new Sphere( 50, 16, 8 ), new MeshBasicMaterial( bopt0 ) );
			mesh2.getPosition().setY(150);
			mesh.addChild( mesh2 );
	
			bopt0.color = new Color3f(0x0000ff);
			Mesh mesh3 = new Mesh( new Sphere( 5, 16, 8 ), new MeshBasicMaterial( bopt0 ) );
			mesh3.getPosition().setZ(150);
			cameraRig.addChild( mesh3 );
	
			//
			
			Geometry geometry = new Geometry();
	
			for ( int i = 0; i < 10000; i ++ ) 
			{
				Vector3f vertex = new Vector3f();
				vertex.setX(Mathematics.randFloatSpread( 2000f ));
				vertex.setY(Mathematics.randFloatSpread( 2000f ));
				vertex.setZ(Mathematics.randFloatSpread( 2000f ));
	
				geometry.getVertices().add( vertex );
			}
			
			ParticleBasicMaterial.ParticleBasicMaterialOptions popt = new ParticleBasicMaterial.ParticleBasicMaterialOptions();
			popt.color = new Color3f(0xDDDDDD);
	
			ParticleSystem particles = new ParticleSystem( geometry, new ParticleBasicMaterial( popt ) );
			getScene().addChild( particles );
	
			//
	
			getRenderer().autoClear = false;
		}
		
		@Override
		protected void onStop()
		{			
		}
		
		@Override
		protected void onUpdate(double duration)
		{
			mesh.getPosition().setX((float) (700f * Math.cos( r )));
			mesh.getPosition().setZ((float) (700f * Math.sin( r )));
			mesh.getPosition().setY((float) (700f * Math.sin( r )));

			mesh.getChildren().get( 0 ).getPosition().setX((float) (70f * Math.cos( 2.0 * r )));
			mesh.getChildren().get( 0 ).getPosition().setZ((float) (70f * Math.sin( r )));

			if ( activeCamera.equals(cameraPerspective) ) 
			{
				
				cameraPerspective.setFieldOfView((float) (35.0 + 30.0 * Math.sin( 0.5 * r )));
				cameraPerspective.setFar(mesh.getPosition().length());
				cameraPerspective.updateProjectionMatrix();

				cameraPerspectiveHelper.update();
				cameraPerspectiveHelper.getLine().setVisible(true);

				cameraOrthoHelper.getLine().setVisible(false);

			} else {

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

			super.onUpdate(duration);

			r += 0.01f;
		}
	}

	RenderingPanel renderingPanel;
	
	public CamerasExample() 
	{
		super("Cameras", "Here is show how to split viewport to two and use different cameras for each. Use: [O] - orthographic camera,  [P] - perspective camera. This example bases on the three.js example.");
	}

	@Override
	public void onAnimationReady(RenderingReadyEvent event)
	{
		super.onAnimationReady(event);
		
		RootPanel.get().addDomHandler(new KeyDownHandler() { 
			
			@Override
			public void onKeyDown(KeyDownEvent event) 
			{
				DemoScene rs = (DemoScene) renderingPanel.getRenderingScene();
				switch(event.getNativeEvent().getKeyCode())
				{
				case 79: case 111:/*O*/	rs.activeCamera = rs.cameraOrtho; break;
				case 80: case 112:/*P*/ 	rs.activeCamera = rs.cameraPerspective; break;
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
		return Demo.resources.example_cameras();
	}
	
	@Override
	protected void asyncOnInitialize(final AsyncCallback<DemoRenderingScene> callback)
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
