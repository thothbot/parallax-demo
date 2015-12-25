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

package org.parallax3d.parallax.demo.content;

import thothbot.parallax.core.shared.cameras.PerspectiveCamera;
import thothbot.parallax.core.shared.core.AbstractGeometry;
import thothbot.parallax.core.shared.geometries.PlaneBufferGeometry;
import thothbot.parallax.core.shared.lights.AmbientLight;
import thothbot.parallax.core.shared.lights.DirectionalLight;
import thothbot.parallax.core.shared.materials.Material;
import thothbot.parallax.core.shared.materials.MeshPhongMaterial;
import thothbot.parallax.core.shared.math.Color;
import thothbot.parallax.core.shared.math.Vector3;
import thothbot.parallax.core.shared.objects.Mesh;
import thothbot.parallax.core.shared.scenes.Fog;
import org.parallax3d.parallax.demo.client.ContentWidget;
import org.parallax3d.parallax.demo.client.DemoAnnotations.DemoSource;
import thothbot.parallax.loader.shared.STLLoader;
import thothbot.parallax.loader.shared.XHRLoader;

import com.google.gwt.core.client.GWT;
import com.google.gwt.core.client.RunAsyncCallback;
import com.google.gwt.user.client.rpc.AsyncCallback;

public class LoaderSTL extends ContentWidget 
{
	private static final String slotted_disk = "./static/models/stl/ascii/slotted_disk.stl";
	private static final String pr2_head_pan = "./static/models/stl/binary/pr2_head_pan.stl";
	private static final String pr2_head_tilt = "./static/models/stl/binary/pr2_head_tilt.stl";
	private static final String colored = "./static/models/stl/binary/colored.stl";
	
	/*
	 * Prepare Rendering Scene
	 */
	@DemoSource
	class DemoScene extends DemoAnimatedScene 
	{
		PerspectiveCamera camera;
		Vector3 cameraTarget;

		@Override
		protected void onStart()
		{
			camera = new PerspectiveCamera(
					35, // fov
					getRenderer().getAbsoluteAspectRation(), // aspect 
					1, // near
					15 // far 
			);
			
			camera.getPosition().set( 3, 0.15, 3 );
			
			cameraTarget = new Vector3( 0, -0.25, 0 );
			
			getScene().setFog(new Fog( 0x72645b, 2, 15 ));

			// Ground

			MeshPhongMaterial planeMaterial = new MeshPhongMaterial();
			planeMaterial.setColor(new Color(0x999999));
			planeMaterial.setAmbient(new Color(0x999999));
			planeMaterial.setSpecular(new Color(0x101010));

			Mesh plane = new Mesh( new PlaneBufferGeometry( 40, 40 ), planeMaterial );
			plane.getRotation().setX( -Math.PI/2 );
			plane.getPosition().setY( -0.5 );
			getScene().add( plane );

			plane.setReceiveShadow(true);
			
			// Binary files
			new STLLoader(slotted_disk, new XHRLoader.ModelLoadHandler() {

				@Override
				public void onModelLoaded(XHRLoader loader, AbstractGeometry geometry) {																					

					MeshPhongMaterial material = new MeshPhongMaterial();
					material.setAmbient( new Color(0xff5533) );
					material.setColor( new Color(0xff5533) );
					material.setSpecular( new Color(0x111111) );
					material.setShininess(200.0);
					
					Mesh mesh = new Mesh( geometry, material );

					mesh.getPosition().set( 0, - 0.25, 0.6 );
					mesh.getRotation().set( 0, - Math.PI / 2, 0 );
					mesh.getScale().set( 0.5, 0.5, 0.5 );

					mesh.setCastShadow(true);
					mesh.setReceiveShadow(true);

					getScene().add( mesh );

				}
			});

			
			final MeshPhongMaterial material = new MeshPhongMaterial();
			material.setAmbient( new Color(0x555555) );
			material.setColor( new Color(0xAAAAAA) );
			material.setSpecular( new Color(0x111111) );
			material.setShininess(200.0);

			new STLLoader(pr2_head_pan, new XHRLoader.ModelLoadHandler() {

				@Override
				public void onModelLoaded(XHRLoader loader, AbstractGeometry geometry) {																					

					Mesh mesh = new Mesh( geometry, material );

					mesh.getPosition().set( 0, - 0.37, - 0.6 );
					mesh.getRotation().set( - Math.PI / 2, 0, 0 );
					mesh.getScale().set( 2, 2, 2 );

					mesh.setCastShadow(true);
					mesh.setReceiveShadow(true);

					getScene().add( mesh );

				}
			});
			
			
			new STLLoader(pr2_head_tilt, new XHRLoader.ModelLoadHandler() {

				@Override
				public void onModelLoaded(XHRLoader loader, AbstractGeometry geometry) {																					
					
					Mesh mesh = new Mesh( geometry, material );

					mesh.getPosition().set( 0.136, - 0.37, - 0.6 );
					mesh.getRotation().set( - Math.PI / 2, 0.3, 0 );
					mesh.getScale().set( 2, 2, 2 );

					mesh.setCastShadow(true);
					mesh.setReceiveShadow(true);

					getScene().add( mesh );

				}
			});
			
			new STLLoader(colored, new XHRLoader.ModelLoadHandler() {

				@Override
				public void onModelLoaded(XHRLoader loader, AbstractGeometry geometry) {
					
					final MeshPhongMaterial material = new MeshPhongMaterial();
					material.setOpacity(((STLLoader)loader).getAlpha());
					material.setVertexColors( Material.COLORS.VERTEX );

					Mesh mesh = new Mesh( geometry, material );

					mesh.getPosition().set( 0.5, 0.2, 0 );
					mesh.getRotation().set( - Math.PI / 2, Math.PI / 2, 0 );
					mesh.getScale().set( 0.3, 0.3, 0.3 );

					mesh.setCastShadow(true);
					mesh.setReceiveShadow(true);

					getScene().add( mesh );

				}
			});
			
			
			getScene().add( new AmbientLight( 0x777777 ) );
			
			addShadowedLight( 1, 1, 1, 0xffffff, 1.35 );
			addShadowedLight( 0.5, 1, -1, 0xffaa00, 1 );

//			ShadowMap shadowMap = new ShadowMap(getRenderer(), getScene());
//			shadowMap.setCullFrontFaces(false);
			
			getRenderer().setClearColor( getScene().getFog().getColor() );
			getRenderer().setGammaInput(true);
			getRenderer().setGammaOutput(true);
		}
		
		private void addShadowedLight( double x, double y, double z, int color, double intensity ) {

			DirectionalLight directionalLight = new DirectionalLight( color, intensity );
			directionalLight.getPosition().set( x, y, z );
			getScene().add( directionalLight );

			directionalLight.setCastShadow(true);
			// directionalLight.shadowCameraVisible = true;

			int d = 1;
			directionalLight.setShadowCameraLeft( -d );
			directionalLight.setShadowCameraRight( d );
			directionalLight.setShadowCameraTop( d );
			directionalLight.setShadowCameraBottom( -d );

			directionalLight.setShadowCameraNear( 1 );
			directionalLight.setShadowCameraFar( 4 );

			directionalLight.setShadowMapWidth( 1024 );
			directionalLight.setShadowMapHeight( 1024 );

			directionalLight.setShadowBias( -0.005 );
			directionalLight.setShadowDarkness( 0.15 );
		}
		
		@Override
		protected void onUpdate(double duration)
		{

			camera.getPosition().setX( Math.cos( duration * 0.0005 ) * 3 );
			camera.getPosition().setZ( Math.sin( duration * 0.0005) * 3 );

			camera.lookAt( cameraTarget );

			getRenderer().render(getScene(), camera);
		}
	}
		
	public LoaderSTL() 
	{
		super("STL loader", "This example based on the three.js example.");
	}
	
	@Override
	public DemoScene onInitialize()
	{
		return new DemoScene();
	}
	
	@Override
	protected boolean isEnabledEffectSwitch() {
		return false;
	}
	
	@Override
	protected void asyncOnInitialize(final AsyncCallback<DemoAnimatedScene> callback)
	{
		GWT.runAsync(LoaderSTL.class, new RunAsyncCallback() 
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
