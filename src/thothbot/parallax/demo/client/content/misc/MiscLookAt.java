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

package thothbot.parallax.demo.client.content.misc;

import thothbot.parallax.core.client.events.AnimationReadyEvent;
import thothbot.parallax.core.shared.cameras.PerspectiveCamera;
import thothbot.parallax.core.shared.geometries.CylinderGeometry;
import thothbot.parallax.core.shared.geometries.SphereGeometry;
import thothbot.parallax.core.shared.materials.Material;
import thothbot.parallax.core.shared.materials.MeshNormalMaterial;
import thothbot.parallax.core.shared.math.Euler;
import thothbot.parallax.core.shared.math.Matrix4;
import thothbot.parallax.core.shared.objects.Mesh;
import thothbot.parallax.demo.client.ContentWidget;
import thothbot.parallax.demo.client.Demo;
import thothbot.parallax.demo.client.DemoAnnotations.DemoSource;

import com.google.gwt.core.client.GWT;
import com.google.gwt.core.client.RunAsyncCallback;
import com.google.gwt.event.dom.client.MouseMoveEvent;
import com.google.gwt.event.dom.client.MouseMoveHandler;
import com.google.gwt.resources.client.ImageResource;
import com.google.gwt.user.client.rpc.AsyncCallback;

public final class MiscLookAt extends ContentWidget 
{
	/*
	 * Prepare Rendering Scene
	 */
	@DemoSource
	class DemoScene extends DemoAnimatedScene 
	{

		PerspectiveCamera camera;
		Mesh sphere;
		
		int mouseX = 0, mouseY = 0;

		@Override
		protected void onStart()
		{
			camera = new PerspectiveCamera(
					50, // fov
					getRenderer().getAbsoluteAspectRation(), // aspect 
					1, // near
					15000 // far 
			); 
			
			camera.getPosition().setZ(3200);
			
			MeshNormalMaterial material = new MeshNormalMaterial();
			material.setShading(Material.SHADING.SMOOTH);
			sphere = new Mesh( new SphereGeometry( 100, 20, 20 ), material );
			getScene().add( sphere );

			CylinderGeometry geometry = new CylinderGeometry( 0, 10, 100, 3, 1 );
			geometry.applyMatrix( new Matrix4().makeRotationFromEuler( new Euler( Math.PI / 2.0, Math.PI, 0.0 ) ) );

			MeshNormalMaterial material2 = new MeshNormalMaterial();

			for ( int i = 0; i < 1000; i ++ ) 
			{
				Mesh mesh2 = new Mesh( geometry, material2 );
				mesh2.getPosition().setX( Math.random() * 4000 - 2000 );
				mesh2.getPosition().setY( Math.random() * 4000 - 2000 );
				mesh2.getPosition().setZ( Math.random() * 4000 - 2000 );
				mesh2.getScale().set( Math.random() * 4.0 + 2.0 );
				getScene().add( mesh2 );
			}

			getScene().setMatrixAutoUpdate(false);
			getRenderer().setSortObjects(false);
			getRenderer().setClearColor(0xeeeeee);
		}
		
		@Override
		protected void onUpdate(double duration)
		{
			double time = duration * 0.0005;
			sphere.getPosition().setX( Math.sin( time * 0.7 ) * 2000 );
			sphere.getPosition().setY( Math.cos( time * 0.5 ) * 2000 );
			sphere.getPosition().setZ( Math.cos( time * 0.3 ) * 2000 );

			for ( int i = 1, l = getScene().getChildren().size(); i < l; i ++ ) 
			{
				getScene().getChildren().get(i).lookAt( sphere.getPosition() );
			}

			camera.getPosition().addX( ( mouseX - camera.getPosition().getX() ) * .05 );
			camera.getPosition().addY( ( - mouseY - camera.getPosition().getY() ) * .05 );
			camera.lookAt( getScene().getPosition() );
			
			getRenderer().render(getScene(), camera);
		}
	}
		
	public MiscLookAt() 
	{
		super("Object3D.lookAt()", "This example based on the three.js example.");
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
	public ImageResource getIcon()
	{
		return Demo.resources.exampleMiscLookAt();
	}
	
	@Override
	protected void asyncOnInitialize(final AsyncCallback<DemoAnimatedScene> callback)
	{
		GWT.runAsync(MiscLookAt.class, new RunAsyncCallback() 
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
