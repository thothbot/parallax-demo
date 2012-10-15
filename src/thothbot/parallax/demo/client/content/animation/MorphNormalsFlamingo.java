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

package thothbot.parallax.demo.client.content.animation;

import java.util.ArrayList;
import java.util.List;

import thothbot.parallax.core.client.RenderingPanel;
import thothbot.parallax.core.client.events.ViewportResizeEvent;
import thothbot.parallax.core.client.events.ViewportResizeHandler;
import thothbot.parallax.core.shared.Log;
import thothbot.parallax.core.shared.cameras.PerspectiveCamera;
import thothbot.parallax.core.shared.core.Color;
import thothbot.parallax.core.shared.core.Vector3;
import thothbot.parallax.core.shared.lights.DirectionalLight;
import thothbot.parallax.core.shared.materials.Material;
import thothbot.parallax.core.shared.materials.MeshLambertMaterial;
import thothbot.parallax.core.shared.materials.MeshPhongMaterial;
import thothbot.parallax.core.shared.objects.MorphAnimMesh;
import thothbot.parallax.core.shared.scenes.Scene;
import thothbot.parallax.demo.client.ContentWidget;
import thothbot.parallax.demo.client.Demo;
import thothbot.parallax.demo.client.DemoAnnotations.DemoSource;
import thothbot.parallax.loader.shared.JsonLoader;

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
		
		PerspectiveCamera camera;
		Scene scene2;
		
		JsonLoader jsonLoader;
		
		List<MorphAnimMesh> morphs;
		
		private double oldTime;
		Vector3 target = new Vector3( 0, 150, 0 );

		@Override
		protected void onStart()
		{
			scene2 = new Scene();
			
			camera = new PerspectiveCamera(
					40, // fov
					0.5 * getRenderer().getAbsoluteAspectRation(), // aspect 
					1, // near
					10000 // far 
			);
			
			camera.getPosition().setY(300);
			camera.addViewportResizeHandler(new ViewportResizeHandler() {
				
				@Override
				public void onResize(ViewportResizeEvent event) {
					camera.setAspectRatio(0.5 * event.getRenderer().getAbsoluteAspectRation());
					
				}
			});
			
			DirectionalLight light = new DirectionalLight( 0xffffff, 1.3 );
			light.getPosition().set( 1, 1, 1 );
			getScene().add( light );

			
			DirectionalLight light2 = new DirectionalLight( 0xffffff, 0.1 );
			light2.getPosition().set( 0.25, -1, 0 );
			getScene().add( light2 );
			
			DirectionalLight light12 = new DirectionalLight( 0xffffff, 1.3 );
			light12.getPosition().set( 1, 1, 1 );
			scene2.add( light12 );

			
			DirectionalLight light22 = new DirectionalLight( 0xffffff, 0.1 );
			light22.getPosition().set( 0.25, -1, 0 );
			scene2.add( light22 );

			morphs = new ArrayList<MorphAnimMesh>();
			this.jsonLoader = new JsonLoader();
			try
			{
				this.jsonLoader.load(model, new JsonLoader.ModelLoadHandler() {

					@Override
					public void onModelLoaded() {	
						
						jsonLoader.morphColorsToFaceColors();

						MeshLambertMaterial material = new MeshLambertMaterial();
						material.setColor(new Color(0xffffff));
						material.setMorphTargets(true);
						material.setMorphNormals(true);
						material.setVertexColors(Material.COLORS.FACE);
						material.setShading(Material.SHADING.FLAT);
 
						MorphAnimMesh meshAnim = new MorphAnimMesh( jsonLoader.getGeometry(), material );

						meshAnim.setDuration(5000);

						meshAnim.getScale().set( 1.5 );
						meshAnim.getPosition().setY( 150 );

						getScene().add( meshAnim );
						morphs.add( meshAnim );
						
						// 

						MeshPhongMaterial material2 = new MeshPhongMaterial();
						material2.setColor(new Color(0xffffff));
						material2.setSpecular(new Color(0xffffff));
						material2.setShininess(20);
						material2.setMorphTargets(true);
						material2.setMorphNormals(true);
						material2.setVertexColors(Material.COLORS.FACE);
						material2.setShading(Material.SHADING.SMOOTH); 
						material2.setPerPixel(false);
 
						MorphAnimMesh meshAnim2 = new MorphAnimMesh( jsonLoader.getGeometry(), material2 );

						meshAnim2.setDuration(5000);

						meshAnim2.getScale().set( 1.5 );
						meshAnim2.getPosition().setY( 150 );

						scene2.add( meshAnim2 );
						morphs.add( meshAnim2 );
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
		protected void onUpdate(double duration)
		{
			this.oldTime = Duration.currentTimeMillis();
			double theta = duration * 0.01;

			camera.getPosition().setX( radius * Math.sin( theta * Math.PI / 360.0 ) );
			camera.getPosition().setZ( radius * Math.cos( theta * Math.PI / 360.0 ) );

			camera.lookAt( target );

			for ( int i = 0; i < morphs.size(); i ++ ) 
			{
				MorphAnimMesh morph = morphs.get( i );
				morph.updateAnimation( (int) (Duration.currentTimeMillis() - this.oldTime) );
			}

			getRenderer().clear();

			getRenderer().setViewport( 0, 0, getRenderer().getAbsoluteWidth()/2, getRenderer().getAbsoluteHeight() );
			getRenderer().render( getScene(), camera );

			getRenderer().setViewport( getRenderer().getAbsoluteWidth()/2, 0, getRenderer().getAbsoluteWidth()/2, getRenderer().getAbsoluteHeight() );
			getRenderer().render( scene2, camera );
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
