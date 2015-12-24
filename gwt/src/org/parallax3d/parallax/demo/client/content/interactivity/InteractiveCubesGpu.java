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

package org.parallax3d.parallax.demo.client.content.interactivity;

import java.util.ArrayList;
import java.util.List;

import thothbot.parallax.core.client.RenderingPanel;
import thothbot.parallax.core.client.controls.TrackballControls;
import thothbot.parallax.core.client.events.AnimationReadyEvent;
import thothbot.parallax.core.client.events.HasEventBus;
import thothbot.parallax.core.client.events.ViewportResizeEvent;
import thothbot.parallax.core.client.events.ViewportResizeHandler;
import thothbot.parallax.core.client.gl2.WebGLRenderingContext;
import thothbot.parallax.core.client.gl2.arrays.Uint8Array;
import thothbot.parallax.core.client.gl2.enums.PixelFormat;
import thothbot.parallax.core.client.gl2.enums.PixelType;
import thothbot.parallax.core.client.textures.RenderTargetTexture;
import thothbot.parallax.core.shared.cameras.PerspectiveCamera;
import thothbot.parallax.core.shared.core.Face3;
import thothbot.parallax.core.shared.core.Geometry;
import thothbot.parallax.core.shared.geometries.BoxGeometry;
import thothbot.parallax.core.shared.lights.AmbientLight;
import thothbot.parallax.core.shared.lights.SpotLight;
import thothbot.parallax.core.shared.materials.Material.COLORS;
import thothbot.parallax.core.shared.materials.Material.SHADING;
import thothbot.parallax.core.shared.materials.MeshBasicMaterial;
import thothbot.parallax.core.shared.materials.MeshLambertMaterial;
import thothbot.parallax.core.shared.math.Color;
import thothbot.parallax.core.shared.math.Euler;
import thothbot.parallax.core.shared.math.Matrix4;
import thothbot.parallax.core.shared.math.Quaternion;
import thothbot.parallax.core.shared.math.Vector3;
import thothbot.parallax.core.shared.objects.Mesh;
import thothbot.parallax.core.shared.scenes.Scene;
import org.parallax3d.parallax.demo.client.ContentWidget;
import org.parallax3d.parallax.demo.client.DemoAnnotations.DemoSource;

import com.google.gwt.core.client.GWT;
import com.google.gwt.core.client.RunAsyncCallback;
import com.google.gwt.event.dom.client.MouseMoveEvent;
import com.google.gwt.event.dom.client.MouseMoveHandler;
import com.google.gwt.user.client.rpc.AsyncCallback;

public final class InteractiveCubesGpu extends ContentWidget
{
	class Picking
	{
		public Vector3 position;
		public Euler rotation;
		public Vector3 scale;
	}
	
	/*
	 * Prepare Rendering Scene
	 */
	@DemoSource
	class DemoScene extends DemoAnimatedScene implements HasEventBus, ViewportResizeHandler
	{
		PerspectiveCamera camera;
		
		Vector3 offset = new Vector3(10, 10, 10);
		int mouseX = 0, mouseY = 0;

		Scene pickingScene;
		TrackballControls controls;
		RenderTargetTexture pickingTexture;
		
		Mesh highlightBox;
		List<Picking> pickingData;
		
		@Override
		public void onResize(ViewportResizeEvent event) 
		{
			pickingTexture.setWidth(event.getRenderer().getAbsoluteWidth());
			pickingTexture.setHeight(event.getRenderer().getAbsoluteHeight());
		}

		@Override
		protected void onStart()
		{
			EVENT_BUS.addHandler(ViewportResizeEvent.TYPE, this);

			camera = new PerspectiveCamera(
					70, // fov
					getRenderer().getAbsoluteAspectRation(), // aspect 
					1, // near
					10000 // far 
			);
			camera.getPosition().setZ(1000);
			
			controls = new TrackballControls( camera, getCanvas()  );
			controls.setRotateSpeed(1.0);
			controls.setZoomSpeed(1.2);
			controls.setPanSpeed(0.8);
			controls.setZoom(true);
			controls.setPan(true);
			controls.setStaticMoving(true);
			controls.setDynamicDampingFactor(0.3);

			pickingScene = new Scene();

			pickingTexture = new RenderTargetTexture(getRenderer().getAbsoluteWidth(), getRenderer().getAbsoluteHeight());
			pickingTexture.setGenerateMipmaps(false);

			getScene().add( new AmbientLight( 0x555555 ) );

			SpotLight light = new SpotLight( 0xffffff, 1.5 );
			light.getPosition().set( 0, 500, 2000 );
			getScene().add( light );

			Geometry geometry = new Geometry();
			Geometry pickingGeometry = new Geometry();
			MeshBasicMaterial pickingMaterial = new MeshBasicMaterial();
			pickingMaterial.setVertexColors(COLORS.VERTEX);

			MeshLambertMaterial defaultMaterial = new MeshLambertMaterial();
			defaultMaterial.setColor(new Color(0xffffff));
			defaultMaterial.setShading(SHADING.FLAT);
			defaultMaterial.setVertexColors(COLORS.VERTEX);

			pickingData = new ArrayList<Picking>();
			
			BoxGeometry geom = new BoxGeometry( 1, 1, 1 );
			Color color = new Color();

			Matrix4 matrix = new Matrix4();
			Quaternion quaternion = new Quaternion();

			for ( int i = 0; i < 500; i ++ ) 
			{
				Vector3 position = new Vector3();
				position.setX( Math.random() * 10000 - 5000 );
				position.setY( Math.random() * 6000 - 3000 );
				position.setZ( Math.random() * 8000 - 4000 );

				Euler rotation = new Euler();
				rotation.setX( ( Math.random() * 2 * Math.PI) );
				rotation.setY( ( Math.random() * 2 * Math.PI) );
				rotation.setZ( ( Math.random() * 2 * Math.PI) );

				Vector3 scale = new Vector3();
				scale.setX( Math.random() * 200 + 100 );
				scale.setY( Math.random() * 200 + 100 );
				scale.setZ( Math.random() * 200 + 100 );
				
				quaternion.setFromEuler( rotation, false );
				matrix.compose( position, quaternion, scale );


				//give the geom's vertices a random color, to be displayed
				applyVertexColors( geom, color.setHex( (int)Math.random() * 0xffffff ) );
				
				geometry.merge( geom, matrix );

				// give the geom's vertices a color corresponding to the "id"

				applyVertexColors( geom, color.setHex( i ) );

				pickingGeometry.merge( geom, matrix );
				
				Picking picking = new Picking();
				picking.position = position;
				picking.rotation = rotation;
				picking.scale = scale;

				pickingData.add(picking);
			}
			
			Mesh drawnObject = new Mesh(geometry, defaultMaterial);
			getScene().add(drawnObject);

			pickingScene.add(new Mesh(pickingGeometry, pickingMaterial));

			MeshLambertMaterial highlightBoxMaterial = new MeshLambertMaterial();
			highlightBoxMaterial.setColor(new Color(0xffff00));
			highlightBox = new Mesh( new BoxGeometry(1, 1, 1), highlightBoxMaterial );
			getScene().add( highlightBox );

			getRenderer().setSortObjects(false);
		}
		
		private void applyVertexColors(Geometry g, Color c) 
		{
			for(Face3 f: g.getFaces())
			{
				int n = (f.getClass() == Face3.class) ? 3 : 4;
				for(int j = 0; j < n; j++)
				{
					f.getVertexColors().add( c );
				}
			}
		}
		
		@Override
		protected void onUpdate(double duration)
		{
			controls.update();

			pick();
			
			getRenderer().render(getScene(), camera);
		}
		
		private void pick() 
		{
			//render the picking scene off-screen
			WebGLRenderingContext gl = getRenderer().getGL();
			getRenderer().render(pickingScene, camera, pickingTexture);
			Uint8Array pixelBuffer = Uint8Array.create(4);

			//read the pixel under the mouse from the texture
			gl.readPixels(mouseX, pickingTexture.getHeight() - mouseY, 1, 1, PixelFormat.RGBA, PixelType.UNSIGNED_BYTE, pixelBuffer);

			//interpret the pixel as an ID

			int id = ( pixelBuffer.get(0) << 16 ) | (  pixelBuffer.get(1) << 8 ) | ( pixelBuffer.get(2) );
			if( pickingData.size() > id && pickingData.get(id) != null )
			{
				Picking data = pickingData.get(id);
				//move our highlightBox so that it surrounds the picked object
				if(data.position != null && data.rotation != null && data.scale != null)
				{
					highlightBox.getPosition().copy(data.position);
					highlightBox.getRotation().copy(data.rotation);
					highlightBox.getScale().copy(data.scale).add(offset);
					highlightBox.setVisible(true);
				}
			} 
			else 
			{
				highlightBox.setVisible(false);
			}
		}
	}
		
	public InteractiveCubesGpu() 
	{
		super("GPU picking", "This example based on the three.js example.");
	}

	@Override
	protected void loadRenderingPanelAttributes(RenderingPanel renderingPanel) 
	{
		super.loadRenderingPanelAttributes(renderingPanel);
		renderingPanel.setBackground(0xffffff);
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
		    	  	rs.mouseX = event.getX(); 
		    	  	rs.mouseY = event.getY();
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
		GWT.runAsync(InteractiveCubesGpu.class, new RunAsyncCallback() 
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
