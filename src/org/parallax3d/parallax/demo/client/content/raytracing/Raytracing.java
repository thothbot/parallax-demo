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

package org.parallax3d.parallax.demo.client.content.raytracing;

import org.parallax3d.parallax.core.shared.cameras.PerspectiveCamera;
import org.parallax3d.parallax.demo.client.ContentWidget;
import org.parallax3d.parallax.demo.client.DemoAnnotations;
import org.parallax3d.parallax.core.client.events.SceneLoadingEvent;
import org.parallax3d.parallax.core.client.renderers.RaytracingRenderer;
import org.parallax3d.parallax.core.shared.geometries.BoxGeometry;
import org.parallax3d.parallax.core.shared.geometries.SphereGeometry;
import org.parallax3d.parallax.core.shared.geometries.TorusKnotGeometry;
import org.parallax3d.parallax.core.shared.lights.PointLight;
import org.parallax3d.parallax.core.shared.materials.Material.COLORS;
import org.parallax3d.parallax.core.shared.materials.Material.SHADING;
import org.parallax3d.parallax.core.shared.materials.MeshPhongMaterial;
import org.parallax3d.parallax.core.shared.math.Color;
import org.parallax3d.parallax.core.shared.objects.Mesh;

import com.google.gwt.core.client.GWT;
import com.google.gwt.core.client.RunAsyncCallback;
import com.google.gwt.user.client.rpc.AsyncCallback;

public class Raytracing extends ContentWidget
{
	/*
	 * Prepare Rendering Scene
	 */
	@DemoAnnotations.DemoSource
	class DemoScene extends DemoAnimatedScene 
	{
		PerspectiveCamera camera;
		RaytracingRenderer r;
		
		@Override
		protected void onStart()
		{
			camera = new PerspectiveCamera(
					60, // fov
					getRenderer().getAbsoluteAspectRation(), // aspect 
					1, // near
					1000 // far 
			);		
			
			camera.getPosition().setZ( 600 );
			
			// materials

			MeshPhongMaterial phongMaterial = new MeshPhongMaterial();
			phongMaterial.setColor(new Color(0xffffff));
			phongMaterial.setSpecular(new Color(0x222222));
			phongMaterial.setShininess(150);
			phongMaterial.setVertexColors(COLORS.NO);
			phongMaterial.setShading(SHADING.SMOOTH);

			MeshPhongMaterial phongMaterialBox = new MeshPhongMaterial();
			phongMaterialBox.setColor(new Color(0xffffff));
			phongMaterialBox.setSpecular(new Color(0xffffff));
			phongMaterialBox.setShininess(100);
			phongMaterialBox.setVertexColors(COLORS.NO);
			phongMaterialBox.setShading(SHADING.FLAT);


			MeshPhongMaterial phongMaterialBoxBottom = new MeshPhongMaterial();
			phongMaterialBoxBottom.setColor(new Color(0x666666));
			phongMaterialBoxBottom.setSpecular(new Color(0x111111));
			phongMaterialBoxBottom.setShininess(100);
			phongMaterialBoxBottom.setVertexColors(COLORS.NO);
			phongMaterialBoxBottom.setShading(SHADING.FLAT);

			MeshPhongMaterial phongMaterialBoxLeft = new MeshPhongMaterial();
			phongMaterialBoxLeft.setColor(new Color(0x990000));
			phongMaterialBoxLeft.setSpecular(new Color(0x111111));
			phongMaterialBoxLeft.setShininess(100);
			phongMaterialBoxLeft.setVertexColors(COLORS.NO);
			phongMaterialBoxLeft.setShading(SHADING.FLAT);

			MeshPhongMaterial phongMaterialBoxRight = new MeshPhongMaterial();
			phongMaterialBoxRight.setColor(new Color(0x0066ff));
			phongMaterialBoxRight.setSpecular(new Color(0x111111));
			phongMaterialBoxRight.setShininess(100);
			phongMaterialBoxRight.setVertexColors(COLORS.NO);
			phongMaterialBoxRight.setShading(SHADING.FLAT);

			MeshPhongMaterial mirrorMaterialFlat = new MeshPhongMaterial();
			mirrorMaterialFlat.setColor(new Color(0x000000));
			mirrorMaterialFlat.setSpecular(new Color(0xff8888));
			mirrorMaterialFlat.setShininess(10000);
			mirrorMaterialFlat.setVertexColors(COLORS.NO);
			mirrorMaterialFlat.setShading(SHADING.FLAT);
			mirrorMaterialFlat.setMirror(true);
			mirrorMaterialFlat.setReflectivity(1.0);

			MeshPhongMaterial mirrorMaterialFlatDark = new MeshPhongMaterial();
			mirrorMaterialFlatDark.setColor(new Color(0x000000));
			mirrorMaterialFlatDark.setSpecular(new Color(0xaaaaaa));
			mirrorMaterialFlatDark.setShininess(10000);
			mirrorMaterialFlatDark.setVertexColors(COLORS.NO);
			mirrorMaterialFlatDark.setShading(SHADING.FLAT);
			mirrorMaterialFlatDark.setMirror(true);
			mirrorMaterialFlatDark.setReflectivity(1.0);
			
			MeshPhongMaterial mirrorMaterialSmooth = new MeshPhongMaterial();
			mirrorMaterialSmooth.setColor(new Color(0xffaa00));
			mirrorMaterialSmooth.setSpecular(new Color(0x222222));
			mirrorMaterialSmooth.setShininess(10000);
			mirrorMaterialSmooth.setVertexColors(COLORS.NO);
			mirrorMaterialSmooth.setShading(SHADING.SMOOTH);
			mirrorMaterialSmooth.setMirror(true);
			mirrorMaterialSmooth.setReflectivity(0.3);

			MeshPhongMaterial glassMaterialFlat = new MeshPhongMaterial();
			glassMaterialFlat.setColor(new Color(0x000000));
			glassMaterialFlat.setSpecular(new Color(0x00ff00));
			glassMaterialFlat.setShininess(10000);
			glassMaterialFlat.setVertexColors(COLORS.NO);
			glassMaterialFlat.setShading(SHADING.FLAT);
			glassMaterialFlat.setGlass(true);
			glassMaterialFlat.setReflectivity(0.5);

			MeshPhongMaterial glassMaterialSmooth = new MeshPhongMaterial();
			glassMaterialSmooth.setColor(new Color(0x000000));
			glassMaterialSmooth.setSpecular(new Color(0xffaa55));
			glassMaterialSmooth.setShininess(10000);
			glassMaterialSmooth.setVertexColors(COLORS.NO);
			glassMaterialSmooth.setShading(SHADING.SMOOTH);
			glassMaterialSmooth.setGlass(true);
			glassMaterialSmooth.setReflectivity(0.25);
			glassMaterialSmooth.setRefractionRatio(0.6);
			
			// geometries

			TorusKnotGeometry torusGeometry = new TorusKnotGeometry( 150 );
			SphereGeometry sphereGeometry = new SphereGeometry( 100, 16, 8 );
			BoxGeometry planeGeometry = new BoxGeometry( 600, 5, 600 );
			BoxGeometry boxGeometry = new BoxGeometry( 100, 100, 100 );

			// TorusKnot

			//torus = new THREE.Mesh( torusGeometry, phongMaterial );
			//scene.add( torus );

			// Sphere

			Mesh sphere = new Mesh( sphereGeometry, phongMaterial );
			sphere.getScale().multiply( 0.5 );
			sphere.getPosition().set( -50, -250+5, -50 );
			getScene().add( sphere );

			Mesh sphere2 = new Mesh( sphereGeometry, mirrorMaterialSmooth );
			sphere2.getScale().multiply( 0.5 );
			sphere2.getPosition().set( 175, -250+5, -150 );
			getScene().add( sphere2 );

			// Box

			Mesh box = new Mesh( boxGeometry, mirrorMaterialFlat );
			box.getPosition().set( -175, -250+2.5, -150 );
			box.getRotation().setY( 0.5 );
			getScene().add( box );

			// Glass

			Mesh glass = new Mesh( sphereGeometry, glassMaterialSmooth );
			glass.getScale().multiply( 0.5 );
			glass.getPosition().set( 75, -250+5, -75 );
			glass.getRotation().setY( 0.5 );
			getScene().add( glass );

			// bottom

			Mesh plane = new Mesh( planeGeometry, phongMaterialBoxBottom );
			plane.getPosition().set( 0, -300+2.5, -300 );
			getScene().add( plane );

			// top

			Mesh plane2 = new Mesh( planeGeometry, phongMaterialBox );
			plane2.getPosition().set( 0, 300-2.5, -300 );
			getScene().add( plane2 );

			// back

			Mesh plane3 = new Mesh( planeGeometry, phongMaterialBox );
			plane3.getRotation().setX( 1.57 );
			plane3.getPosition().set( 0, 0, -300 );
			getScene().add( plane3 );

			Mesh plane4 = new Mesh( planeGeometry, mirrorMaterialFlatDark );
			plane4.getRotation().setX( 1.57 );
			plane4.getPosition().set( 0, 0, -300+10 );
			plane4.getScale().multiply( 0.85 );
			getScene().add( plane4 );

			// left

			Mesh plane5 = new Mesh( planeGeometry, phongMaterialBoxLeft );
			plane5.getRotation().setZ( 1.57 );
			plane5.getPosition().set( -300, 0, -300 );
			getScene().add( plane5 );

			// right

			Mesh plane6 = new Mesh( planeGeometry, phongMaterialBoxRight );
			plane6.getRotation().setZ( 1.57 );
			plane6.getPosition().set( 300, 0, -300 );
			getScene().add( plane6 );

			// light

			double intensity = 70000;

			PointLight light = new PointLight( 0xffaa55, intensity );
			light.getPosition().set( -200, 100, 100 );
			light.setPhysicalAttenuation(true);
			getScene().add( light );

			PointLight light2 = new PointLight( 0x55aaff, intensity );
			light2.getPosition().set( 200, 100, 100 );
			light2.setPhysicalAttenuation(true);
			getScene().add( light2 );

			PointLight light3 = new PointLight( 0xffffff, intensity * 1.5 );
			light3.getPosition().set( 0, 0, 300 );
			light3.setPhysicalAttenuation(true);
			getScene().add( light3 );
			
		}
		
		@Override
		protected void onUpdate(double duration)
		{

		}
	}
	
	@Override
	public void onSceneLoading(SceneLoadingEvent event) {
		
		super.onSceneLoading(event);
		
		if(event.isLoaded())
		{
			final DemoScene rs = (DemoScene) this.renderingPanel.getAnimatedScene();
			
			rs.r = new RaytracingRenderer(rs.getRenderer().getAbsoluteWidth(), rs.getRenderer().getAbsoluteHeight());
			this.renderingPanel.add(rs.r.getCanvas());
			rs.r.render(rs.getScene(), rs.camera);
		}
	}
			
	public Raytracing() 
	{
		super("Raytracing sandbox", "This example based on the three.js example.");
	}
	
	@Override
	public DemoScene onInitialize()
	{
		return new DemoScene();
	}
	
	@Override
	protected void asyncOnInitialize(final AsyncCallback<DemoAnimatedScene> callback)
	{
		GWT.runAsync(Raytracing.class, new RunAsyncCallback() 
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

