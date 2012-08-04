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

import thothbot.parallax.core.client.AnimationReadyEvent;
import thothbot.parallax.core.client.RenderingPanel;
import thothbot.parallax.core.client.context.Canvas3d;
import thothbot.parallax.core.shared.cameras.PerspectiveCamera;
import thothbot.parallax.core.shared.core.Color3;
import thothbot.parallax.core.shared.core.Geometry;
import thothbot.parallax.core.shared.core.Vector3;
import thothbot.parallax.core.shared.materials.Material;
import thothbot.parallax.core.shared.materials.ParticleBasicMaterial;
import thothbot.parallax.core.shared.objects.ParticleSystem;
import thothbot.parallax.demo.client.ContentWidget;
import thothbot.parallax.demo.client.Demo;
import thothbot.parallax.demo.client.DemoAnnotations.DemoSource;

import com.google.gwt.core.client.GWT;
import com.google.gwt.core.client.RunAsyncCallback;
import com.google.gwt.event.dom.client.MouseMoveEvent;
import com.google.gwt.event.dom.client.MouseMoveHandler;
import com.google.gwt.resources.client.ImageResource;
import com.google.gwt.user.client.rpc.AsyncCallback;

public final class ParticleTrails extends ContentWidget 
{

	/*
	 * Prepare Rendering Scene
	 */
	@DemoSource
	class DemoScene extends DemoAnimatedScene 
	{
		public int mouseX;
		public int mouseY;

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
			getCamera().getPosition().set(100000, 0, 3200 );
			getScene().addChild(getCamera());

			int[] colors = {0x000000, 0xff0080, 0x8000ff, 0xffffff};
			Geometry geometry = new Geometry();

			for ( int i = 0; i < 2000; i ++ ) 
			{
				Vector3 vertex = new Vector3();
				vertex.setX(Math.random() * 4000.0 - 2000.0);
				vertex.setY(Math.random() * 4000.0 - 2000.0);
				vertex.setZ(Math.random() * 4000.0 - 2000.0);
				geometry.getVertices().add( vertex );

				geometry.getColors().add( new Color3( colors[ (int) Math.floor( Math.random() * colors.length ) ] ) );

			}

			ParticleBasicMaterial material = new ParticleBasicMaterial();
			material.setSize( 1f );
			material.setVertexColors(  Material.COLORS.VERTEX );
			material.setDepthTest( false );
			material.setOpacity( 0.5f );
			material.setSizeAttenuation(false);

			ParticleSystem mesh = new ParticleSystem( geometry, material );
			getScene().addChild( mesh );

			getRenderer().setSortObjects(false);
			getRenderer().setAutoClear(false);
		}
		
		@Override
		protected void onStop()
		{			
		}
		
		@Override
		protected void onUpdate(double duration)
		{
			getCamera().getPosition().addX( ( mouseX - getCamera().getPosition().getX() ) * .05 );
			getCamera().getPosition().addY( ( - mouseY - getCamera().getPosition().getY() ) * .05 );

			getCamera().lookAt( getScene().getPosition() );
		}
	}

	public ParticleTrails() 
	{
		super("Particle Trails", "Use mouse. This example based on the three.js example.");
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
	protected void asyncOnInitialize(final AsyncCallback<DemoAnimatedScene> callback)
	{
		GWT.runAsync(ParticleTrails.class, new RunAsyncCallback() 
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
