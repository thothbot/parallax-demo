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
import thothbot.parallax.core.shared.Log;
import thothbot.parallax.core.shared.cameras.PerspectiveCamera;
import thothbot.parallax.core.shared.core.Color;
import thothbot.parallax.core.shared.geometries.SphereGeometry;
import thothbot.parallax.core.shared.materials.Material;
import thothbot.parallax.core.shared.materials.MeshBasicMaterial;
import thothbot.parallax.core.shared.materials.MeshDepthMaterial;
import thothbot.parallax.core.shared.objects.Mesh;
import thothbot.parallax.demo.client.ContentWidget;
import thothbot.parallax.demo.client.Demo;
import thothbot.parallax.demo.client.DemoAnnotations.DemoSource;
import thothbot.parallax.loader.shared.JsonLoader;
import thothbot.parallax.plugin.postprocessing.client.Postprocessing;

import com.google.gwt.core.client.GWT;
import com.google.gwt.core.client.RunAsyncCallback;
import com.google.gwt.event.dom.client.MouseMoveEvent;
import com.google.gwt.event.dom.client.MouseMoveHandler;
import com.google.gwt.http.client.RequestException;
import com.google.gwt.resources.client.ImageResource;
import com.google.gwt.user.client.rpc.AsyncCallback;

public final class PostprocessingGodrays extends ContentWidget 
{
	/*
	 * Prepare Rendering Scene
	 */
	@DemoSource
	class DemoScene extends DemoAnimatedScene 
	{

		private static final String model = "./static/models/obj/tree/tree.js";
		private static final double orbitRadius = 200.0;
		
		private static final int bgColor = 0x000511;
		private static final int sunColor = 0xffee00;
		
		public int mouseX;
		public int mouseY;
		
		Mesh sphereMesh;
		
		private Postprocessing composer;
		
		@Override
		protected void loadCamera()
		{
			setCamera(
					new PerspectiveCamera(
							70, // fov
							getRenderer().getCanvas().getAspectRation(), // aspect 
							1, // near
							3000 // far 
					)); 
		}

		@Override
		protected void onStart()
		{
			getCamera().getPosition().setZ(200);
			getScene().add(getCamera());
			
			MeshDepthMaterial materialDepth = new MeshDepthMaterial();

			MeshBasicMaterial materialScene = new MeshBasicMaterial();
			materialScene.setColor(new Color(0x000000));
			materialScene.setShading(Material.SHADING.FLAT);

			// tree

			final JsonLoader loader = new JsonLoader();
			loader.setMaterial(materialScene);

			try
			{
				loader.load(model, new JsonLoader.ModelLoadHandler() {

					@Override
					public void onModeLoad() {																					
						Mesh mesh = loader.getMesh();
						mesh.getPosition().set(0, -150, -150);
						mesh.getScale().set(400);
						mesh.setMatrixAutoUpdate(false);
						mesh.updateMatrix();

						getScene().add(mesh);
					}
				});
			}
			catch (RequestException exception) 
			{
				Log.error("Error while loading JSON file.");
			}

			// sphere

			SphereGeometry geo = new SphereGeometry( 1, 20, 10 );
			this.sphereMesh = new Mesh( geo, materialScene );
			this.sphereMesh.getScale().set( 20 );

			getScene().add( this.sphereMesh );

			//
					
			getRenderer().setSortObjects(false);
			getRenderer().setAutoClear(false);
			getRenderer().setClearColorHex( bgColor, 1 );
		}
		
		@Override
		protected void onStop()
		{			
		}
		
		@Override
		protected void onUpdate(double duration)
		{
			double time = duration / 4000.0;

			this.sphereMesh.getPosition().setX( orbitRadius * Math.cos( time ) );
			this.sphereMesh.getPosition().setZ( orbitRadius * Math.sin( time ) - 100.0 );

			getCamera().getPosition().addX( ( mouseX - getCamera().getPosition().getX() ) * 0.036 );
			getCamera().getPosition().addY( ( - ( mouseY ) - getCamera().getPosition().getY() ) * 0.036 );

			getCamera().lookAt( getScene().getPosition() );
			
			getRenderer().clear();
		}
	}
		
	public PostprocessingGodrays() 
	{
		super("God-rays", "Drag mouse to move. This example based on the three.js example.");
	}
	
	@Override
	protected void loadRenderingPanelAttributes(RenderingPanel renderingPanel) 
	{
		super.loadRenderingPanelAttributes(renderingPanel);
		renderingPanel.getCanvas3dAttributes().setAntialiasEnable(false);
		renderingPanel.setBackground(0x000511);
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
		    	  	rs.mouseX = (event.getX() - canvas.getWidth() / 2 ); 
		    	  	rs.mouseY = (event.getY() - canvas.getHeight() / 2);
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
		return Demo.resources.examplePostprocessingGodrays();
	}
	
	@Override
	protected void asyncOnInitialize(final AsyncCallback<DemoAnimatedScene> callback)
	{
		GWT.runAsync(PostprocessingGodrays.class, new RunAsyncCallback() 
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
