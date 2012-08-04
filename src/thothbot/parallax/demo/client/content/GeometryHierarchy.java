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
import thothbot.parallax.core.shared.cameras.PerspectiveCamera;
import thothbot.parallax.core.shared.geometries.Cube;
import thothbot.parallax.core.shared.materials.MeshNormalMaterial;
import thothbot.parallax.core.shared.objects.Mesh;
import thothbot.parallax.core.shared.objects.Object3D;
import thothbot.parallax.core.shared.scenes.FogSimple;
import thothbot.parallax.demo.client.ContentWidget;
import thothbot.parallax.demo.client.Demo;
import thothbot.parallax.demo.client.DemoAnnotations.DemoSource;

import com.google.gwt.core.client.GWT;
import com.google.gwt.core.client.RunAsyncCallback;
import com.google.gwt.event.dom.client.MouseMoveEvent;
import com.google.gwt.event.dom.client.MouseMoveHandler;
import com.google.gwt.resources.client.ImageResource;
import com.google.gwt.user.client.rpc.AsyncCallback;

public final class GeometryHierarchy extends ContentWidget 
{

	/*
	 * Prepare Rendering Scene
	 */
	@DemoSource
	class DemoScene extends DemoAnimatedScene 
	{
		Object3D group;

		int mouseX = 0, mouseY = 0;

		@Override
		protected void loadCamera()
		{
			setCamera(
					new PerspectiveCamera(
							60, // fov
							getRenderer().getCanvas().getAspectRation(), // aspect 
							1, // near
							10000 // far 
					)); 
		}

		@Override
		protected void onStart()
		{
			getCamera().getPosition().setZ(500);
			getScene().addChild(getCamera());
			getScene().setFog(new FogSimple( 0xffffff, 1, 10000));

			Cube geometry = new Cube( 100, 100, 100 );
			MeshNormalMaterial material = new MeshNormalMaterial();

			this.group = new Object3D();

			for ( int i = 0; i < 1000; i ++ ) 
			{
				Mesh mesh = new Mesh( geometry, material );
				mesh.getPosition().setX( Math.random() * 2000.0 - 1000.0 );
				mesh.getPosition().setY( Math.random() * 2000.0 - 1000.0 );
				mesh.getPosition().setZ( Math.random() * 2000.0 - 1000.0 );

				mesh.getRotation().setX( Math.random() * 360.0 * ( Math.PI / 180.0 ) );
				mesh.getRotation().setY( Math.random() * 360.0 * ( Math.PI / 180.0 ) );

				mesh.setMatrixAutoUpdate(false);
				mesh.updateMatrix();

				group.addChild( mesh );
			}

			getScene().addChild( group );

			getRenderer().setSortObjects(false);
			getRenderer().setClearColorHex(0xeeeeee);
		}
		
		@Override
		protected void onStop()
		{			
		}
		
		@Override
		protected void onUpdate(double duration)
		{
			double time = duration * 0.001;

			double rx = Math.sin( time * 0.7 ) * 0.5;
			double ry = Math.sin( time * 0.3 ) * 0.5;
			double rz = Math.sin( time * 0.2 ) * 0.5;

			getCamera().getPosition().addX(( mouseX - getCamera().getPosition().getX() ) * .05);
			getCamera().getPosition().addY(( - mouseY - getCamera().getPosition().getY() ) * .05);

			getCamera().lookAt( getScene().getPosition() );

			this.group.getRotation().setX( rx );
			this.group.getRotation().setY( ry );
			this.group.getRotation().setZ( rz );
		}
	}
		
	public GeometryHierarchy() 
	{
		super("Geometry hierarchy", "Drag mouse to move. This example based on the three.js example.");
	}
	
	@Override
	public void onAnimationReady(AnimationReadyEvent event)
	{
		super.onAnimationReady(event);

		this.renderingPanel.getRenderer().getCanvas().addMouseMoveHandler(new MouseMoveHandler() {
		      @Override
		      public void onMouseMove(MouseMoveEvent event)
		      {
		    	  	DemoScene rs = (DemoScene) renderingPanel.getAnimatedScene();
		    	  	Canvas3d canvas = renderingPanel.getRenderer().getCanvas();
		    	  	rs.mouseX = (event.getX() - canvas.getWidth() / 2 ) * 10; 
		    	  	rs.mouseY = (event.getY() - canvas.getHeight() / 2) * 10;
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
		return Demo.resources.exampleGeometryHierarchy();
	}
	
	@Override
	protected void asyncOnInitialize(final AsyncCallback<DemoAnimatedScene> callback)
	{
		GWT.runAsync(GeometryHierarchy.class, new RunAsyncCallback() 
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
