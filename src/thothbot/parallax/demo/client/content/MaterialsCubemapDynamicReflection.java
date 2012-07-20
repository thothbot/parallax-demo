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
import thothbot.parallax.core.client.gl2.enums.TextureMinFilter;
import thothbot.parallax.core.client.textures.Texture;
import thothbot.parallax.core.shared.cameras.CubeCamera;
import thothbot.parallax.core.shared.cameras.PerspectiveCamera;
import thothbot.parallax.core.shared.core.Vector3f;
import thothbot.parallax.core.shared.geometries.Cube;
import thothbot.parallax.core.shared.geometries.Sphere;
import thothbot.parallax.core.shared.geometries.TorusKnot;
import thothbot.parallax.core.shared.materials.MeshBasicMaterial;
import thothbot.parallax.core.shared.objects.Mesh;
import thothbot.parallax.core.shared.utils.ImageUtils;
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
import com.google.gwt.resources.client.ClientBundle;
import com.google.gwt.resources.client.ImageResource;
import com.google.gwt.user.client.rpc.AsyncCallback;

public final class MaterialsCubemapDynamicReflection extends ContentWidget 
{
	/*
	 * Load texture
	 */
	@DemoSource
	public interface Resources extends ClientBundle
	{
		Resources INSTANCE = GWT.create(Resources.class);

		@Source("../../resources/textures/ruins.jpg")
		ImageResource texture();
	}

	/*
	 * Prepare Rendering Scene
	 */
	@DemoSource
	class DemoScene extends DemoAnimatedScene 
	{
		public int onMouseDownMouseX = 0;
		public int onMouseDownMouseY = 0;
		
		public boolean onMouseDown = false;
		
		public float fov = 70f;

		public double lat = 0; 
		public double lon = 0;
		public double phi = 0; 
		public double theta = 0;
		
		private Mesh sphere;
		private Mesh cube;
		private Mesh torus;
		
		private CubeCamera cubeCamera;

		@Override
		protected void loadCamera()
		{
			setCamera(
					new PerspectiveCamera(
							this.fov, // fov
							getRenderer().getCanvas().getAspectRation(), // aspect 
							1, // near
							1000 // far 
					)); 
		}

		@Override
		protected void onStart()
		{
			getCamera().getPosition().setZ(400);
			getScene().addChild(getCamera());

			Texture texture = ImageUtils.loadTexture( Resources.INSTANCE.texture(),  Texture.MAPPING_MODE.UV);
			MeshBasicMaterial mbOpt = new MeshBasicMaterial();
			mbOpt.setMap( texture );
			
			Mesh mesh = new Mesh( new Sphere( 500, 60, 40 ), mbOpt );
			mesh.getScale().setX( -1 );
			getScene().addChild( mesh );

			this.cubeCamera = new CubeCamera( 1f, 1000f, 256 );
			this.cubeCamera.getRenderTarget().setMinFilter( TextureMinFilter.LINEAR_MIPMAP_LINEAR );
			getScene().addChild( cubeCamera );

			MeshBasicMaterial material = new MeshBasicMaterial(); 
			material.setEnvMap( cubeCamera.getRenderTarget() );
			
			sphere = new Mesh( new Sphere( 20, 60, 40 ), material );
			getScene().addChild( sphere );

			cube = new Mesh( new Cube( 20, 20, 20 ), material );
			getScene().addChild( cube );

			torus = new Mesh( new TorusKnot( 20, 5, 100, 100 ), material );
			getScene().addChild( torus );
		}
		
		@Override
		protected void onStop()
		{			
		}
		
		@Override
		protected void onUpdate(double duration)
		{
			this.lon += .15;

			this.lat = Math.max( - 85.0, Math.min( 85.0, this.lat ) );
			this.phi = ( 90 - lat ) * Math.PI / 180.0;
			this.theta = this.lon * Math.PI / 180.0;

			this.sphere.getPosition().setX( (float) (Math.sin( duration * 0.001 ) * 30.0) );
			this.sphere.getPosition().setY( (float) (Math.sin( duration * 0.0011 ) * 30.0) );
			this.sphere.getPosition().setZ( (float) (Math.sin( duration * 0.0012 ) * 30.0) );

			this.sphere.getRotation().addX( 0.02f );
			this.sphere.getRotation().addY( 0.03f );

			this.cube.getPosition().setX( (float) (Math.sin( duration * 0.001 + 2.0 ) * 30.0) );
			this.cube.getPosition().setY( (float) (Math.sin( duration * 0.0011 + 2.0 ) * 30.0) );
			this.cube.getPosition().setZ( (float) (Math.sin( duration * 0.0012 + 2.0 ) * 30.0) );

			this.cube.getRotation().addX( 0.02f );
			this.cube.getRotation().addY( 0.03f );

			this.torus.getPosition().setX( (float) (Math.sin( duration * 0.001 + 4.0 ) * 30.0) );
			this.torus.getPosition().setY( (float) (Math.sin( duration * 0.0011 + 4.0 ) * 30.0) );
			this.torus.getPosition().setZ( (float) (Math.sin( duration * 0.0012 + 4.0 ) * 30.0) );

			this.torus.getRotation().addX( 0.02f );
			this.torus.getRotation().addY( 0.03f );

			getCamera().getPosition().setX( (float) (100.0 * Math.sin( phi ) * Math.cos( theta )) );
			getCamera().getPosition().setY( (float) (100.0 * Math.cos( phi )) );
			getCamera().getPosition().setZ( (float) (100.0 * Math.sin( phi ) * Math.sin( theta )) );

			getCamera().lookAt( new Vector3f( 0.0f, 0.0f, 0.0f ) );

			this.sphere.setVisible(false); // *cough*

			cubeCamera.updateCubeMap( getRenderer(), getScene() );

			this.sphere.setVisible(true); // *cough*
			
			super.onUpdate(duration);
		}
	}
		
	public MaterialsCubemapDynamicReflection() 
	{
		super("Dynamic cube reflection", "Use mouse to move and zoom. This example based on the three.js example.");
	}
	
	@Override
	public void onAnimationReady(AnimationReadyEvent event)
	{
		super.onAnimationReady(event);

		this.renderingPanel.getRenderer().getCanvas().addMouseWheelHandler(new MouseWheelHandler() {
			
			@Override
			public void onMouseWheel(MouseWheelEvent event) {
				DemoScene rs = (DemoScene) renderingPanel.getRenderingScene();
				rs.fov -= event.getDeltaY() * 1.0f;
				rs.getCamera().getProjectionMatrix().makePerspective(rs.fov, rs.getRenderer().getCanvas().getAspectRation(), 1, 1100);
			}
		});
		
		this.renderingPanel.getRenderer().getCanvas().addMouseDownHandler(new MouseDownHandler() {
			
			@Override
			public void onMouseDown(MouseDownEvent event) {
				event.preventDefault();

				DemoScene rs = (DemoScene) renderingPanel.getRenderingScene();
				rs.onMouseDownMouseX = event.getX();
				rs.onMouseDownMouseY = event.getY();
				
				rs.onMouseDown = true;
			}
		});
		
		this.renderingPanel.getRenderer().getCanvas().addMouseUpHandler(new MouseUpHandler() {
			
			@Override
			public void onMouseUp(MouseUpEvent event) {
				DemoScene rs = (DemoScene) renderingPanel.getRenderingScene();
				rs.onMouseDown = false;
			}
		});
				
		this.renderingPanel.getRenderer().getCanvas().addMouseMoveHandler(new MouseMoveHandler() {
			
		      @Override
		      public void onMouseMove(MouseMoveEvent event)
		      {
		    	  	DemoScene rs = (DemoScene) renderingPanel.getRenderingScene();
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
	public ImageResource getIcon()
	{
		return Demo.resources.exampleMaterialsCubemapDynamicReflection();
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
