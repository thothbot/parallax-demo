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

import thothbot.parallax.core.client.controls.TrackballControls;
import thothbot.parallax.core.client.shaders.Shader;
import thothbot.parallax.core.client.shaders.Uniform;
import thothbot.parallax.core.shared.Log;
import thothbot.parallax.core.shared.cameras.PerspectiveCamera;
import thothbot.parallax.core.shared.geometries.SphereGeometry;
import thothbot.parallax.core.shared.lights.DirectionalLight;
import thothbot.parallax.core.shared.lights.HemisphereLight;
import thothbot.parallax.core.shared.materials.Material.SIDE;
import thothbot.parallax.core.shared.materials.MeshFaceMaterial;
import thothbot.parallax.core.shared.materials.ShaderMaterial;
import thothbot.parallax.core.shared.math.Color;
import thothbot.parallax.core.shared.objects.Mesh;
import thothbot.parallax.core.shared.scenes.Fog;
import thothbot.parallax.demo.client.ContentWidget;
import thothbot.parallax.demo.client.Demo;
import thothbot.parallax.demo.client.DemoAnnotations.DemoSource;
import thothbot.parallax.loader.shared.JsonLoader;

import com.google.gwt.core.client.GWT;
import com.google.gwt.core.client.RunAsyncCallback;
import com.google.gwt.http.client.RequestException;
import com.google.gwt.resources.client.ImageResource;
import com.google.gwt.resources.client.TextResource;
import com.google.gwt.user.client.rpc.AsyncCallback;

public final class MaterialsLightmap extends ContentWidget 
{
	
	/*
	 * Load shaders
	 */
	@DemoSource
	public interface Resources extends Shader.DefaultResources
	{
		Resources INSTANCE = GWT.create(Resources.class);

		@Source("../../../resources/shaders/skydome.fs")
		TextResource getFragmentShader();
		
		@Source("../../../resources/shaders/skydome.vs")
		TextResource getVertexShader();
	}

	/*
	 * Prepare Rendering Scene
	 */
	@DemoSource
	class DemoScene extends DemoAnimatedScene 
	{
		private static final String model = "./static/models/obj/lightmap/lightmap.js";
		
		PerspectiveCamera camera;
		
		TrackballControls controls;

		@Override
		protected void onStart()
		{
			camera = new PerspectiveCamera(
					40, // fov
					getRenderer().getAbsoluteAspectRation(), // aspect 
					1, // near
					10000 // far 
			); 
			
			camera.getPosition().set( 700, 180, -500 );

			getScene().setFog( new Fog( 0xfafafa, 1000, 10000 ) );

			// CONTROLS

			controls = new TrackballControls( camera, getCanvas() );
			controls.getTarget().setZ( 150 );

			// LIGHTS

			DirectionalLight directionalLight = new DirectionalLight( 0xffffff, 1.475 );
			directionalLight.getPosition().set( 100, 100, -100 );
			getScene().add( directionalLight );


			HemisphereLight hemiLight = new HemisphereLight( 0xffffff, 0xffffff, 1.25 );
			hemiLight.getColor().setHSL( 0.6, 1.0, 0.75 );
			hemiLight.getGroundColor().setHSL( 0.1, 0.8, 0.7 );
			hemiLight.getPosition().setY( 500 );
			getScene().add( hemiLight );

			// SKYDOME

			ShaderMaterial skyMat = new ShaderMaterial(Resources.INSTANCE);
			skyMat.setSide(SIDE.BACK);

			skyMat.getShader().addUniform("topColor", new Uniform(Uniform.TYPE.C, new Color(0x0077ff)));
			skyMat.getShader().addUniform("bottomColor", new Uniform(Uniform.TYPE.C, new Color(0xffffff)));
			skyMat.getShader().addUniform("topColor", new Uniform(Uniform.TYPE.C, hemiLight.getColor().clone()));
			skyMat.getShader().addUniform("offset", new Uniform(Uniform.TYPE.F, 400.0 ));
			skyMat.getShader().addUniform("exponent", new Uniform(Uniform.TYPE.F, 0.6 ));

			getScene().getFog().setColor( ((Color)skyMat.getShader().getUniforms().get("bottomColor").getValue()).clone() );

			SphereGeometry skyGeo = new SphereGeometry( 4000, 32, 15 );
			Mesh sky = new Mesh( skyGeo, skyMat );
			getScene().add( sky );

			// RENDERER

			getRenderer().setClearColor( getScene().getFog().getColor(), 1 );
			getRenderer().setGammaInput(true);
			getRenderer().setGammaOutput(true);

			// MODEL

			final JsonLoader jsonLoader = new JsonLoader();
			try
			{
				jsonLoader.load(model, new JsonLoader.ModelLoadHandler() {

					@Override
					public void onModelLoaded() {		
						Log.error(jsonLoader.getMaterials());
						Mesh mesh = new Mesh( jsonLoader.getGeometry(), new MeshFaceMaterial(jsonLoader.getMaterials()) );
						mesh.getPosition().set( 0 );
						mesh.getScale().set( 100 );
						getScene().add( mesh );
					}
				});
			}
			catch (RequestException exception) 
			{
				Log.error("Error while loading JSON file.");
			}			
		}
		
		@Override
		protected void onUpdate(double duration)
		{
			controls.update();
			getRenderer().render(getScene(), camera);
		}
	}
		
	public MaterialsLightmap() 
	{
		super("Lightmap", "This example based on the three.js example.");
	}
	
	@Override
	public DemoScene onInitialize()
	{
		return new DemoScene();
	}

	@Override
	public ImageResource getIcon()
	{
		return Demo.resources.exampleMaterialsLightmap();
	}
	
	@Override
	protected void asyncOnInitialize(final AsyncCallback<DemoAnimatedScene> callback)
	{
		GWT.runAsync(MaterialsLightmap.class, new RunAsyncCallback() 
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
