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
 * Squirrel. If not, see http://www.gnu.org/licenses/.
 */

package thothbot.parallax.demo.client.content;

import thothbot.parallax.core.client.RenderingPanel;
import thothbot.parallax.core.shared.Log;
import thothbot.parallax.core.shared.cameras.PerspectiveCamera;
import thothbot.parallax.core.shared.core.Vector3f;
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

public final class MorphTargetsHorse extends ContentWidget 
{
	/*
	 * Prepare Rendering Scene
	 */
	@DemoSource
	class DemoScene extends DemoAnimatedScene 
	{
		private static final String model = "./static/models/animated/horse.js";
		
		static final int radius = 600;
		
		Mesh mesh;
		Vector3f target = new Vector3f(0, 150, 0);
		
		static final int aminationDuration = 1000;
		static final int keyframes = 15;
		static final double interpolation = (double)aminationDuration / keyframes;
		
		int lastKeyframe = 0;
		int currentKeyframe = 0;

		@Override
		protected void loadCamera()
		{
			setCamera(
					new PerspectiveCamera(
							50, // fov
							getRenderer().getCanvas().getAspectRation(), // aspect 
							1, // near
							10000 // far 
					)); 
		}

		@Override
		protected void onStart()
		{
			getCamera().getPosition().setY(300);
			getScene().addChild(getCamera());
			
			DirectionalLight light = new DirectionalLight( 0xefefff, 2 );
			light.getPosition().set( 1, 1, 1 ).normalize();
			getScene().addChild( light );

			DirectionalLight light1 = new DirectionalLight( 0xffefef, 2 );
			light1.getPosition().set( -1, -1, -1 ).normalize();
			getScene().addChild( light1 );

			final Json json = new Json();
			try
			{
				json.load(model, new Json.Callback() {

					@Override
					public void onLoaded() {																					
						json.getAnimation().setDuration(3000);

						mesh = json.getMesh();
						mesh.getScale().set(1.5);

						getScene().addChild(mesh);
					}
				});
			}
			catch (RequestException exception) 
			{
				Log.error("Error while loading JSON file.");
			}
		}
		
		@Override
		protected void onStop()
		{			
		}
		
		@Override
		protected void onUpdate(double duration)
		{
			double theta = duration * 0.02;

			getCamera().getPosition().setX( radius * Math.sin( theta * Math.PI / 360.0 ) );
			getCamera().getPosition().setZ( radius * Math.cos( theta * Math.PI / 360.0 ) );

			getCamera().lookAt( target );

			if ( mesh != null ) 
			{
				// Alternate morph targets
				double time = Duration.currentTimeMillis() % aminationDuration;

				int keyframe = (int)Math.floor( time / interpolation );

				if ( keyframe != currentKeyframe ) 
				{
					mesh.getMorphTargetInfluences().set( lastKeyframe, 0.0 );
					mesh.getMorphTargetInfluences().set( currentKeyframe, 1.0 );
					mesh.getMorphTargetInfluences().set( keyframe, 0.0 );

					lastKeyframe = currentKeyframe;
					currentKeyframe = keyframe;
				}

				mesh.getMorphTargetInfluences().set( keyframe, 
						(double)( time % interpolation ) / interpolation);
				mesh.getMorphTargetInfluences().set( lastKeyframe,
						1.0 - mesh.getMorphTargetInfluences().get( keyframe ));
			}
		}
	}
		
	public MorphTargetsHorse() 
	{
		super("Morph targets: horse", "This example based on the three.js example.");
	}
	
	@Override
	protected void loadRenderingPanelAttributes(RenderingPanel renderingPanel) 
	{
		super.loadRenderingPanelAttributes(renderingPanel);
		renderingPanel.setBackground(0xf0f0f0);
	}
	
	@Override
	public DemoScene onInitialize()
	{
		return new DemoScene();
	}

	@Override
	public ImageResource getIcon()
	{
		return Demo.resources.exampleMorphTargetsHorse();
	}
	
	@Override
	protected void asyncOnInitialize(final AsyncCallback<DemoAnimatedScene> callback)
	{
		GWT.runAsync(MorphTargetsHorse.class, new RunAsyncCallback() 
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
