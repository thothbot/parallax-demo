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

import thothbot.parallax.core.client.RenderingPanel;
import thothbot.parallax.core.shared.Log;
import thothbot.parallax.core.shared.cameras.PerspectiveCamera;
import thothbot.parallax.core.shared.lights.DirectionalLight;
import thothbot.parallax.core.shared.objects.Mesh;
import thothbot.parallax.demo.client.ContentWidget;
import thothbot.parallax.demo.client.Demo;
import thothbot.parallax.demo.client.DemoAnnotations.DemoSource;
import thothbot.parallax.loader.shared.Json;

import com.google.gwt.core.client.Duration;
import com.google.gwt.core.client.GWT;
import com.google.gwt.core.client.RunAsyncCallback;
import com.google.gwt.http.client.RequestException;
import com.google.gwt.resources.client.ImageResource;
import com.google.gwt.user.client.rpc.AsyncCallback;

public final class MorphNormalsFlamingo extends ContentWidget 
{
	/*
	 * Prepare Rendering Scene
	 */
	@DemoSource
	class DemoScene extends DemoAnimatedScene 
	{
		private static final String model = "./static/models/animated/flamingo.js";
		
		static final int radius = 600;
		
		Json json;
		
		private double oldTime;

		@Override
		protected void loadCamera()
		{
			setCamera(
					new PerspectiveCamera(
							40, // fov
							getRenderer().getCanvas().getAspectRation(), // aspect 
							1, // near
							10000 // far 
					)); 
		}

		@Override
		protected void onStart()
		{
			getCamera().getPosition().setY(200);
			getScene().add(getCamera());
			
			DirectionalLight light = new DirectionalLight( 0xffffff, 1.3 );
			light.getPosition().set( 1, 1, 1 );
			getScene().add( light );

			this.json = new Json();
			try
			{
				this.json.load(model, new Json.Callback() {

					@Override
					public void onLoaded() {																					
						json.getAnimation().setDuration(3000);

						Mesh mesh = json.getMesh();
						mesh.getScale().set(2);
						mesh.getPosition().set(0);

						getScene().add(mesh);
					}
				});
			}
			catch (RequestException exception) 
			{
				Log.error("Error while loading JSON file.");
			}
			
			getRenderer().setGammaInput(true);
			getRenderer().setGammaOutput(true);
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
			this.oldTime = Duration.currentTimeMillis();
			double theta = duration * 0.01;

			getCamera().getPosition().setX( radius * Math.sin( theta * Math.PI / 360.0 ) );
			getCamera().getPosition().setZ( radius * Math.cos( theta * Math.PI / 360.0 ) );

			getCamera().lookAt( getScene().getPosition() );

			this.json.getAnimation().updateAnimation( (int) (Duration.currentTimeMillis() - this.oldTime)  );
			getRenderer().clear(false, false, false);
		}
	}
		
	public MorphNormalsFlamingo() 
	{
		super("Morph normals: flamingo", "This example based on the three.js example.");
	}
	
	@Override
	protected void loadRenderingPanelAttributes(RenderingPanel renderingPanel) 
	{
		super.loadRenderingPanelAttributes(renderingPanel);
		renderingPanel.setBackground(0x222222);
	}
	
	@Override
	public DemoScene onInitialize()
	{
		return new DemoScene();
	}

	@Override
	public ImageResource getIcon()
	{
		return Demo.resources.exampleMorphNormalsFlamingo();
	}
	
	@Override
	protected void asyncOnInitialize(final AsyncCallback<DemoAnimatedScene> callback)
	{
		GWT.runAsync(MorphNormalsFlamingo.class, new RunAsyncCallback() 
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
