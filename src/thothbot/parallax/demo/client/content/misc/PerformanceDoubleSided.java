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

import thothbot.parallax.core.client.RenderingPanel;
import thothbot.parallax.core.client.events.AnimationReadyEvent;
import thothbot.parallax.core.client.gl2.enums.PixelFormat;
import thothbot.parallax.core.client.textures.CubeTexture;
import thothbot.parallax.core.client.textures.Texture;
import thothbot.parallax.core.shared.cameras.PerspectiveCamera;
import thothbot.parallax.core.shared.geometries.SphereGeometry;
import thothbot.parallax.core.shared.lights.PointLight;
import thothbot.parallax.core.shared.materials.Material;
import thothbot.parallax.core.shared.materials.MeshPhongMaterial;
import thothbot.parallax.core.shared.math.Color;
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

public final class PerformanceDoubleSided extends ContentWidget 
{
	/*
	 * Prepare Rendering Scene
	 */
	@DemoSource
	class DemoScene extends DemoAnimatedScene 
	{
		private static final String textures = "./static/textures/cube/swedishRoyalCastle/*.jpg";
		
		PerspectiveCamera camera;
		
		public int mouseX;
		public int mouseY;

		@Override
		protected void onStart()
		{
			camera = new PerspectiveCamera(
					50, // fov
					getRenderer().getAbsoluteAspectRation(), // aspect 
					1, // near
					20000 // far 
			); 
			
			camera.getPosition().setZ(3200);
			
			PointLight light1 = new PointLight( 0x0011ff, 1, 5500 );
			light1.getPosition().set( 4000, 0, 0 );
			getScene().add( light1 );

			PointLight light2 = new PointLight( 0xff1100, 1, 5500 );
			light2.getPosition().set( -4000, 0, 0 );
			getScene().add( light2 );

			PointLight light3 = new PointLight( 0xffaa00, 2, 3000 );
			light3.getPosition().set( 0, 0, 0 );
			getScene().add( light3 );

			CubeTexture reflectionCube = new CubeTexture( textures );
			reflectionCube.setFormat(PixelFormat.RGB);

			MeshPhongMaterial material = new MeshPhongMaterial();
			material.setSpecular( new Color(0xffffff) );
			material.setShininess( 100 );
			material.setEnvMap( reflectionCube );
			material.setCombine( Texture.OPERATIONS.MIX );
			material.setReflectivity( 0.1 );
//			material.setPerPixel(true);
			material.setWrapAround(true); 
			material.getWrapRGB().set( 0.5, 0.5, 0.5 );
			material.setSide(Material.SIDE.DOUBLE);

			SphereGeometry geometry = new SphereGeometry( 1, 32, 16, 0, (double)Math.PI );

			for ( int i = 0; i < 5000; i ++ ) 
			{
				Mesh mesh = new Mesh( geometry, material );

				mesh.getPosition().setX( Math.random() * 10000.0 - 5000.0 );
				mesh.getPosition().setY( Math.random() * 10000.0 - 5000.0 );
				mesh.getPosition().setZ( Math.random() * 10000.0 - 5000.0 );

				mesh.getRotation().setX( Math.random() * 360.0 * ( Math.PI / 180.0 ) );
				mesh.getRotation().setY( Math.random() * 360.0 * ( Math.PI / 180.0 ) );
				
				double scale =  Math.random() * 50.0 + 100.0;
				mesh.getScale().set( scale );

				mesh.setMatrixAutoUpdate(false);
				mesh.updateMatrix();

				getScene().add( mesh );
			}

			getRenderer().setGammaInput(true);
			getRenderer().setGammaOutput(true);
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
		
	public PerformanceDoubleSided() 
	{
		super("Performance: Double sided", "This example based on the three.js example.");
	}
	
	@Override
	protected void loadRenderingPanelAttributes(RenderingPanel renderingPanel) 
	{
		super.loadRenderingPanelAttributes(renderingPanel);
		renderingPanel.setBackground(0x050505);
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
		GWT.runAsync(PerformanceDoubleSided.class, new RunAsyncCallback() 
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
