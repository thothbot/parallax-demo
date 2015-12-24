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

package org.parallax3d.parallax.demo.client.content.geometries;

import thothbot.parallax.core.client.events.AnimationReadyEvent;
import thothbot.parallax.core.shared.cameras.PerspectiveCamera;
import thothbot.parallax.core.shared.core.Object3D;
import thothbot.parallax.core.shared.geometries.BoxGeometry;
import thothbot.parallax.core.shared.materials.MeshNormalMaterial;
import thothbot.parallax.core.shared.objects.Mesh;
import thothbot.parallax.core.shared.scenes.Fog;
import org.parallax3d.parallax.demo.client.ContentWidget;
import org.parallax3d.parallax.demo.client.DemoAnnotations.DemoSource;

import com.google.gwt.core.client.GWT;
import com.google.gwt.core.client.RunAsyncCallback;
import com.google.gwt.event.dom.client.MouseMoveEvent;
import com.google.gwt.event.dom.client.MouseMoveHandler;
import com.google.gwt.user.client.rpc.AsyncCallback;

public final class GeometryHierarchy extends ContentWidget 
{

	/*
	 * Prepare Rendering Scene
	 */
	@DemoSource
	class DemoScene extends DemoAnimatedScene 
	{
		PerspectiveCamera camera;
		
		Object3D group;

		int mouseX = 0, mouseY = 0;

		@Override
		protected void onStart()
		{
			camera = new PerspectiveCamera(
					60, // fov
					getRenderer().getAbsoluteAspectRation(), // aspect 
					1, // near
					10000 // far 
			);
			
			camera.getPosition().setZ(500);

			getScene().setFog(new Fog( 0xffffff, 1, 10000));

			BoxGeometry geometry = new BoxGeometry( 100, 100, 100 );
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

				group.add( mesh );
			}

			getScene().add( group );

			getRenderer().setSortObjects(false);
			getRenderer().setClearColor(0xeeeeee);
		}
		
		@Override
		protected void onUpdate(double duration)
		{
			double time = duration * 0.001;

			double rx = Math.sin( time * 0.7 ) * 0.5;
			double ry = Math.sin( time * 0.3 ) * 0.5;
			double rz = Math.sin( time * 0.2 ) * 0.5;

			camera.getPosition().addX(( mouseX - camera.getPosition().getX() ) * .05);
			camera.getPosition().addY(( - mouseY - camera.getPosition().getY() ) * .05);

			camera.lookAt( getScene().getPosition() );

			this.group.getRotation().setX( rx );
			this.group.getRotation().setY( ry );
			this.group.getRotation().setZ( rz );
			
			getRenderer().render(getScene(), camera);
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

		this.renderingPanel.getCanvas().addMouseMoveHandler(new MouseMoveHandler() {
		      @Override
		      public void onMouseMove(MouseMoveEvent event)
		      {
		    	  	DemoScene rs = (DemoScene) renderingPanel.getAnimatedScene();
		    	  	rs.mouseX = (event.getX() - renderingPanel.getRenderer().getAbsoluteWidth() / 2 ) * 10; 
		    	  	rs.mouseY = (event.getY() - renderingPanel.getRenderer().getAbsoluteHeight() / 2) * 10;
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
