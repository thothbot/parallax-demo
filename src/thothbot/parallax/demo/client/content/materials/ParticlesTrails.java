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

import thothbot.parallax.core.client.RenderingPanel;
import thothbot.parallax.core.client.events.AnimationReadyEvent;
import thothbot.parallax.core.shared.cameras.PerspectiveCamera;
import thothbot.parallax.core.shared.core.Geometry;
import thothbot.parallax.core.shared.materials.Material;
import thothbot.parallax.core.shared.materials.PointCloudMaterial;
import thothbot.parallax.core.shared.math.Color;
import thothbot.parallax.core.shared.math.Vector3;
import thothbot.parallax.core.shared.objects.PointCloud;
import thothbot.parallax.demo.client.ContentWidget;
import thothbot.parallax.demo.client.Demo;
import thothbot.parallax.demo.client.DemoAnnotations.DemoSource;

import com.google.gwt.core.client.GWT;
import com.google.gwt.core.client.RunAsyncCallback;
import com.google.gwt.event.dom.client.MouseMoveEvent;
import com.google.gwt.event.dom.client.MouseMoveHandler;
import com.google.gwt.resources.client.ImageResource;
import com.google.gwt.user.client.rpc.AsyncCallback;

public final class ParticlesTrails extends ContentWidget 
{

	/*
	 * Prepare Rendering Scene
	 */
	@DemoSource
	class DemoScene extends DemoAnimatedScene 
	{
		PerspectiveCamera camera;
		
		public int mouseX;
		public int mouseY;

		@Override
		protected void onStart()
		{
			camera = new PerspectiveCamera(
					60, // fov
					getRenderer().getAbsoluteAspectRation(), // aspect 
					1, // near
					10000 // far 
			); 
			
			camera.getPosition().set(100000, 0, 3200 );

			int[] colors = {0x000000, 0xff0080, 0x8000ff, 0xffffff};
			Geometry geometry = new Geometry();

			for ( int i = 0; i < 2000; i ++ ) 
			{
				Vector3 vertex = new Vector3();
				vertex.setX(Math.random() * 4000.0 - 2000.0);
				vertex.setY(Math.random() * 4000.0 - 2000.0);
				vertex.setZ(Math.random() * 4000.0 - 2000.0);
				geometry.getVertices().add( vertex );

				geometry.getColors().add( new Color( colors[ (int) Math.floor( Math.random() * colors.length ) ] ) );

			}

			PointCloudMaterial material = new PointCloudMaterial();
			material.setSize( 1.0 );
			material.setVertexColors(  Material.COLORS.VERTEX );
			material.setDepthTest( false );
			material.setOpacity( 0.5 );
			material.setSizeAttenuation(false);
			material.setTransparent(true);

			PointCloud mesh = new PointCloud( geometry, material );
			getScene().add( mesh );

			getRenderer().setSortObjects(false);
			getRenderer().setAutoClear(false);
		}
		
		@Override
		protected void onUpdate(double duration)
		{
			camera.getPosition().addX( ( mouseX - camera.getPosition().getX() ) * .05 );
			camera.getPosition().addY( ( - mouseY - camera.getPosition().getY() ) * .05 );

			camera.lookAt( getScene().getPosition() );
			getRenderer().render(getScene(), camera);
		}
	}

	public ParticlesTrails() 
	{
		super("Particle Trails", "Use mouse. This example based on the three.js example.");
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
	protected void loadRenderingPanelAttributes(RenderingPanel renderingPanel) 
	{
		super.loadRenderingPanelAttributes(renderingPanel);
		renderingPanel.getCanvas3dAttributes().setPreserveDrawingBufferEnable(true);
	}

	@Override
	public DemoScene onInitialize()
	{
		return new DemoScene();
	}

	@Override
	public ImageResource getIcon()
	{
		return Demo.resources.exampleParticleTrails();
	}
	
	@Override
	protected boolean isEnabledEffectSwitch() {
		return false;
	}
	
	@Override
	protected void asyncOnInitialize(final AsyncCallback<DemoAnimatedScene> callback)
	{
		GWT.runAsync(ParticlesTrails.class, new RunAsyncCallback() 
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
