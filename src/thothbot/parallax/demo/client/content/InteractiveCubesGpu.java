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

import java.util.ArrayList;
import java.util.List;

import thothbot.parallax.core.client.AnimationReadyEvent;
import thothbot.parallax.core.client.context.Canvas3d;
import thothbot.parallax.core.client.controls.TrackballControls;
import thothbot.parallax.core.client.gl2.WebGLRenderingContext;
import thothbot.parallax.core.client.gl2.arrays.Uint8Array;
import thothbot.parallax.core.client.gl2.enums.DataType;
import thothbot.parallax.core.client.gl2.enums.GLenum;
import thothbot.parallax.core.client.gl2.enums.PixelFormat;
import thothbot.parallax.core.client.textures.RenderTargetTexture;
import thothbot.parallax.core.shared.cameras.PerspectiveCamera;
import thothbot.parallax.core.shared.core.Color;
import thothbot.parallax.core.shared.core.Face3;
import thothbot.parallax.core.shared.core.Geometry;
import thothbot.parallax.core.shared.core.Projector;
import thothbot.parallax.core.shared.core.Vector3;
import thothbot.parallax.core.shared.geometries.Cube;
import thothbot.parallax.core.shared.lights.AmbientLight;
import thothbot.parallax.core.shared.lights.SpotLight;
import thothbot.parallax.core.shared.materials.Material.COLORS;
import thothbot.parallax.core.shared.materials.Material.SHADING;
import thothbot.parallax.core.shared.materials.MeshBasicMaterial;
import thothbot.parallax.core.shared.materials.MeshLambertMaterial;
import thothbot.parallax.core.shared.objects.Mesh;
import thothbot.parallax.core.shared.scenes.Scene;
import thothbot.parallax.core.shared.utils.GeometryUtils;
import thothbot.parallax.demo.client.ContentWidget;
import thothbot.parallax.demo.client.Demo;
import thothbot.parallax.demo.client.DemoAnnotations.DemoSource;
import thothbot.parallax.demo.client.content.InteractiveCubes.DemoScene;

import com.google.gwt.core.client.GWT;
import com.google.gwt.core.client.RunAsyncCallback;
import com.google.gwt.event.dom.client.MouseMoveEvent;
import com.google.gwt.event.dom.client.MouseMoveHandler;
import com.google.gwt.resources.client.ImageResource;
import com.google.gwt.user.client.rpc.AsyncCallback;

public final class InteractiveCubesGpu extends ContentWidget
{
	class Picking
	{
		public Vector3 position;
		public Vector3 rotation;
		public Vector3 scale;
	}
	
	/*
	 * Prepare Rendering Scene
	 */
	@DemoSource
	class DemoScene extends DemoAnimatedScene 
	{
		Vector3 offset = new Vector3(10, 10, 10);
		int mouseX = 0, mouseY = 0;

		Scene pickingScene;
		TrackballControls controls;
		RenderTargetTexture pickingTexture;
		
		Mesh highlightBox;
		List<Picking> pickingData;
		
		Projector projector;

		@Override
		protected void loadCamera()
		{
			setCamera(
					new PerspectiveCamera(
							70, // fov
							getRenderer().getCanvas().getAspectRation(), // aspect 
							1, // near
							10000 // far 
					)); 
		}

		@Override
		protected void onResize() 
		{
			super.onResize();
			Canvas3d canvas = getRenderer().getCanvas();
			pickingTexture.setWidth(canvas.getWidth());
			pickingTexture.setHeight(canvas.getHeight());
		}

		@Override
		protected void onStart()
		{
			getCamera().getPosition().setZ(1000);
			getScene().addChild(getCamera());
			
			controls = new TrackballControls( getCamera(), getRenderer().getCanvas()  );
			controls.setRotateSpeed(1.0);
			controls.setZoomSpeed(1.2);
			controls.setPanSpeed(0.8);
			controls.setZoom(true);
//			controls.noPan = false;
			controls.setStaticMoving(true);
			controls.setDynamicDampingFactor(0.3);

			pickingScene = new Scene();
			Canvas3d canvas = getRenderer().getCanvas();
			pickingTexture = new RenderTargetTexture(canvas.getWidth(), canvas.getHeight());
			pickingTexture.setGenerateMipmaps(false);

			getScene().addChild( new AmbientLight( 0x555555 ) );

			SpotLight light = new SpotLight( 0xffffff, 1.5 );
			
			light.getPosition().set( 0, 500, 2000 );
			light.castShadow = true;

			light.shadowCameraNear = 200;
			light.shadowCameraFar = ((PerspectiveCamera)getCamera()).getFar();
			light.shadowCameraFov = 50;

			light.shadowBias = -0.00022;
			light.shadowDarkness = 0.5;

			light.shadowMapWidth = 1024;
			light.shadowMapHeight = 1024;

			getScene().addChild( light );

			Geometry geometry = new Geometry();
			Geometry pickingGeometry = new Geometry();
			MeshBasicMaterial pickingMaterial = new MeshBasicMaterial();
			pickingMaterial.setVertexColors(COLORS.VERTEX);

			MeshLambertMaterial defaultMaterial = new MeshLambertMaterial();
			defaultMaterial.setColor(new Color(0xffffff));
			defaultMaterial.setShading(SHADING.FLAT);
			defaultMaterial.setVertexColors(COLORS.VERTEX);

			pickingData = new ArrayList<Picking>();
			
			for ( int i = 0; i < 5000; i ++ ) 
			{
				Vector3 position = new Vector3();
				position.setX( Math.random() * 10000 - 5000 );
				position.setY( Math.random() * 6000 - 3000 );
				position.setZ( Math.random() * 8000 - 4000 );

				Vector3 rotation = new Vector3();
				rotation.setX( ( Math.random() * 2 * Math.PI) );
				rotation.setY( ( Math.random() * 2 * Math.PI) );
				rotation.setZ( ( Math.random() * 2 * Math.PI) );

				Vector3 scale = new Vector3();
				scale.setX( Math.random() * 200 + 100 );
				scale.setY( Math.random() * 200 + 100 );
				scale.setZ( Math.random() * 200 + 100 );

				//give the geom's vertices a random color, to be displayed
				Cube geom = new Cube(1, 1, 1);
				Color color = new Color((int)(Math.random() * 0xffffff));
				applyVertexColors(geom, color);
				Mesh cube = new Mesh(geom);
				cube.getPosition().copy(position);
				cube.getRotation().copy(rotation);
				cube.getScale().copy(scale);
				GeometryUtils.merge(geometry, cube);

				//give the pickingGeom's vertices a color corresponding to the "id"
				Cube pickingGeom = new Cube(1, 1, 1);
				Color pickingColor = new Color(i);
				applyVertexColors(pickingGeom, pickingColor);
				Mesh pickingCube = new Mesh(pickingGeom);
				pickingCube.getPosition().copy(position);
				pickingCube.getRotation().copy(rotation);
				pickingCube.getScale().copy(scale);
				GeometryUtils.merge(pickingGeometry, pickingCube);

				Picking picking = new Picking();
				picking.position = position;
				picking.rotation = rotation;
				picking.scale = scale;

				pickingData.add(picking);
			}
			
			Mesh drawnObject = new Mesh(geometry, defaultMaterial);
			//drawnObject.castShadow = true;
			//drawnObject.receiveShadow = true;
			getScene().addChild(drawnObject);

			pickingScene.addChild(new Mesh(pickingGeometry, pickingMaterial));

			MeshLambertMaterial highlightBoxMaterial = new MeshLambertMaterial();
			highlightBoxMaterial.setColor(new Color(0xffff00));
			highlightBox = new Mesh( new Cube(1, 1, 1), highlightBoxMaterial );
			getScene().addChild( highlightBox );

			projector = new Projector();
			getRenderer().setClearColorHex(0xeeeeee);
			getRenderer().setSortObjects(false);
			getRenderer().setShadowMapEnabled(true);
			getRenderer().setShadowMapSoft(true);
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
		protected void onStop()
		{			
		}
		
		@Override
		protected void onUpdate(double duration)
		{
			controls.update();

			pick();
		}
		
		private void pick() 
		{
			//render the picking scene off-screen
			WebGLRenderingContext gl = getRenderer().getGL();
			getRenderer().render(pickingScene, getCamera(), pickingTexture);
			Uint8Array pixelBuffer = Uint8Array.create(4);
			
			//read the pixel under the mouse from the texture
			gl.readPixels(mouseX, pickingTexture.getHeight() - mouseY, 1, 1, PixelFormat.RGBA.getValue(), DataType.UNSIGNED_BYTE.getValue(), pixelBuffer);
			
			//interpret the pixel as an ID
			int id = (pixelBuffer.get(0) << 16) | (pixelBuffer.get(1) << 8) | (pixelBuffer.get(2));
			Picking data = pickingData.get(id);
			if(data != null)
			{
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
	public void onAnimationReady(AnimationReadyEvent event)
	{
		super.onAnimationReady(event);

		this.renderingPanel.getRenderer().getCanvas().addMouseMoveHandler(new MouseMoveHandler() {
		      @Override
		      public void onMouseMove(MouseMoveEvent event)
		      {
		    	  	DemoScene rs = (DemoScene) renderingPanel.getAnimatedScene();
		    	  	Canvas3d canvas = renderingPanel.getRenderer().getCanvas();
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
	public ImageResource getIcon()
	{
		return Demo.resources.exampleInteractiveCubesGpu();
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
