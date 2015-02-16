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

package thothbot.parallax.demo.client.content.materials;

import thothbot.parallax.core.client.events.AnimationReadyEvent;
import thothbot.parallax.core.client.gl2.enums.TextureMinFilter;
import thothbot.parallax.core.client.textures.Texture;
import thothbot.parallax.core.shared.cameras.CubeCamera;
import thothbot.parallax.core.shared.cameras.PerspectiveCamera;
import thothbot.parallax.core.shared.geometries.BoxGeometry;
import thothbot.parallax.core.shared.geometries.SphereGeometry;
import thothbot.parallax.core.shared.geometries.TorusKnotGeometry;
import thothbot.parallax.core.shared.materials.MeshBasicMaterial;
import thothbot.parallax.core.shared.math.Mathematics;
import thothbot.parallax.core.shared.objects.Mesh;
import thothbot.parallax.demo.client.ContentWidget;
import thothbot.parallax.demo.client.Demo;
import thothbot.parallax.demo.client.DemoAnnotations.DemoSource;

import com.google.gwt.core.client.GWT;
import com.google.gwt.core.client.RunAsyncCallback;
import com.google.gwt.event.dom.client.MouseDownEvent;
import com.google.gwt.event.dom.client.MouseDownHandler;
import com.google.gwt.event.dom.client.MouseMoveEvent;
import com.google.gwt.event.dom.client.MouseMoveHandler;
import com.google.gwt.event.dom.client.MouseUpEvent;
import com.google.gwt.event.dom.client.MouseUpHandler;
import com.google.gwt.event.dom.client.MouseWheelEvent;
import com.google.gwt.event.dom.client.MouseWheelHandler;
import com.google.gwt.resources.client.ImageResource;
import com.google.gwt.user.client.rpc.AsyncCallback;

public final class MaterialsCubemapDynamicReflection extends ContentWidget 
{
	/*
	 * Prepare Rendering Scene
	 */
	@DemoSource
	class DemoScene extends DemoAnimatedScene 
	{
		private static final String texture = "./static/textures/ruins.jpg";
		
		PerspectiveCamera camera; 
		
		public int onMouseDownMouseX = 0;
		public int onMouseDownMouseY = 0;
		
		public boolean onMouseDown = false;
		
		public double fov = 70;

		public double lat = 0; 
		public double lon = 0;
		public double phi = 0; 
		public double theta = 0;
		
		private Mesh sphere;
		private Mesh cube;
		private Mesh torus;
		
		private CubeCamera cubeCamera;

		@Override
		protected void onStart()
		{
			camera = new PerspectiveCamera(
					this.fov, // fov
					getRenderer().getAbsoluteAspectRation(), // aspect 
					1, // near
					1000 // far 
			); 
			
			Texture texture = new Texture(DemoScene.texture);
			MeshBasicMaterial mbOpt = new MeshBasicMaterial();
			mbOpt.setMap( texture );
			
			Mesh mesh = new Mesh( new SphereGeometry( 500, 60, 40 ), mbOpt );
			mesh.getScale().setX( -1 );
			getScene().add( mesh );

			this.cubeCamera = new CubeCamera( 1, 1000, 256 );
			this.cubeCamera.getRenderTarget().setMinFilter( TextureMinFilter.LINEAR_MIPMAP_LINEAR );
			getScene().add( cubeCamera );

			MeshBasicMaterial material = new MeshBasicMaterial(); 
			material.setEnvMap( cubeCamera.getRenderTarget() );
			
			sphere = new Mesh( new SphereGeometry( 20, 30, 15 ), material );
			getScene().add( sphere );

			cube = new Mesh( new BoxGeometry( 20, 20, 20 ), material );
			getScene().add( cube );

			torus = new Mesh( new TorusKnotGeometry( 20, 5, 100, 25 ), material );
			getScene().add( torus );
			
			getRenderer().render( getScene(), camera );
		}
		
		@Override
		protected void onUpdate(double duration)
		{
			this.lon += .15;

			this.lat = Math.max( - 85.0, Math.min( 85.0, this.lat ) );
			this.phi = Mathematics.degToRad( 90 - lat ) ;
			this.theta = Mathematics.degToRad( this.lon );

			this.sphere.getPosition().setX(Math.sin( duration * 0.001 ) * 30.0 );
			this.sphere.getPosition().setY(Math.sin( duration * 0.0011 ) * 30.0 );
			this.sphere.getPosition().setZ(Math.sin( duration * 0.0012 ) * 30.0 );

			this.sphere.getRotation().addX( 0.02 );
			this.sphere.getRotation().addY( 0.03 );

			this.cube.getPosition().setX(Math.sin( duration * 0.001 + 2.0 ) * 30.0 );
			this.cube.getPosition().setY(Math.sin( duration * 0.0011 + 2.0 ) * 30.0 );
			this.cube.getPosition().setZ(Math.sin( duration * 0.0012 + 2.0 ) * 30.0 );

			this.cube.getRotation().addX( 0.02 );
			this.cube.getRotation().addY( 0.03 );

			this.torus.getPosition().setX(Math.sin( duration * 0.001 + 4.0 ) * 30.0 );
			this.torus.getPosition().setY(Math.sin( duration * 0.0011 + 4.0 ) * 30.0 );
			this.torus.getPosition().setZ(Math.sin( duration * 0.0012 + 4.0 ) * 30.0 );

			this.torus.getRotation().addX( 0.02 );
			this.torus.getRotation().addY( 0.03 );

			camera.getPosition().setX(100.0 * Math.sin( phi ) * Math.cos( theta ) );
			camera.getPosition().setY(100.0 * Math.cos( phi ) );
			camera.getPosition().setZ(100.0 * Math.sin( phi ) * Math.sin( theta ) );

			camera.lookAt( getScene().getPosition() );

			this.sphere.setVisible(false); // *cough*

			cubeCamera.updateCubeMap( getRenderer(), getScene() );

			this.sphere.setVisible(true); // *cough*
			
			getRenderer().render(getScene(), camera);
		}
	}
		
	public MaterialsCubemapDynamicReflection() 
	{
		super("Dynamic map cube reflection", "Use mouse to move and zoom. This example based on the three.js example.");
	}
	
	@Override
	public void onAnimationReady(AnimationReadyEvent event)
	{
		super.onAnimationReady(event);

		this.renderingPanel.getCanvas().addMouseWheelHandler(new MouseWheelHandler() {
			
			@Override
			public void onMouseWheel(MouseWheelEvent event) {
				DemoScene rs = (DemoScene) renderingPanel.getAnimatedScene();
				rs.fov -= event.getDeltaY() * 1.0;
				rs.camera.getProjectionMatrix().makePerspective(rs.fov, rs.getRenderer().getAbsoluteAspectRation(), 1, 1100);
			}
		});
		
		this.renderingPanel.getCanvas().addMouseDownHandler(new MouseDownHandler() {
			
			@Override
			public void onMouseDown(MouseDownEvent event) {
				event.preventDefault();

				DemoScene rs = (DemoScene) renderingPanel.getAnimatedScene();
				rs.onMouseDownMouseX = event.getX();
				rs.onMouseDownMouseY = event.getY();
				
				rs.onMouseDown = true;
			}
		});
		
		this.renderingPanel.getCanvas().addMouseUpHandler(new MouseUpHandler() {
			
			@Override
			public void onMouseUp(MouseUpEvent event) {
				DemoScene rs = (DemoScene) renderingPanel.getAnimatedScene();
				rs.onMouseDown = false;
			}
		});
				
		this.renderingPanel.getCanvas().addMouseMoveHandler(new MouseMoveHandler() {
			
		      @Override
		      public void onMouseMove(MouseMoveEvent event)
		      {
		    	  	DemoScene rs = (DemoScene) renderingPanel.getAnimatedScene();
		    	  	if(rs.onMouseDown)
		    	  	{
		    	  		rs.lon += ( event.getX() - rs.onMouseDownMouseX ) * 0.01; 
		    	  		rs.lat += ( event.getY() - rs.onMouseDownMouseY ) * 0.01;
		    	  	}
		      }
		});
	}
	
	@Override
	public DemoScene onInitialize()
	{
		return new DemoScene();
	}
	
	@Override
	protected void asyncOnInitialize(final AsyncCallback<DemoAnimatedScene> callback)
	{
		GWT.runAsync(MaterialsCubemapDynamicReflection.class, new RunAsyncCallback() 
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
