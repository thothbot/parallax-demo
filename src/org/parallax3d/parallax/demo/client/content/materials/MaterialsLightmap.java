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

package org.parallax3d.parallax.demo.client.content.materials;

import org.parallax3d.parallax.core.client.controls.TrackballControls;
import org.parallax3d.parallax.core.client.shaders.Uniform;
import org.parallax3d.parallax.core.shared.cameras.PerspectiveCamera;
import org.parallax3d.parallax.core.shared.geometries.SphereGeometry;
import org.parallax3d.parallax.core.shared.lights.DirectionalLight;
import org.parallax3d.parallax.core.shared.objects.Mesh;
import org.parallax3d.parallax.core.shared.scenes.Fog;
import org.parallax3d.parallax.demo.client.ContentWidget;
import org.parallax3d.parallax.core.client.shaders.Shader;
import org.parallax3d.parallax.core.shared.core.AbstractGeometry;
import org.parallax3d.parallax.core.shared.lights.HemisphereLight;
import org.parallax3d.parallax.core.shared.materials.Material.SIDE;
import org.parallax3d.parallax.core.shared.materials.MeshFaceMaterial;
import org.parallax3d.parallax.core.shared.materials.ShaderMaterial;
import org.parallax3d.parallax.core.shared.math.Color;
import org.parallax3d.parallax.demo.client.DemoAnnotations.DemoSource;
import org.parallax3d.parallax.loader.shared.JsonLoader;
import org.parallax3d.parallax.loader.shared.XHRLoader;

import com.google.gwt.core.client.GWT;
import com.google.gwt.core.client.RunAsyncCallback;
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

			new JsonLoader(model, new XHRLoader.ModelLoadHandler() {

				@Override
				public void onModelLoaded(XHRLoader loader, AbstractGeometry geometry) {		
					Mesh mesh = new Mesh( geometry, new MeshFaceMaterial(((JsonLoader)loader).getMaterials()) );
					mesh.getPosition().set( 0 );
					mesh.getScale().set( 100 );
					getScene().add( mesh );
				}
			});
	
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
