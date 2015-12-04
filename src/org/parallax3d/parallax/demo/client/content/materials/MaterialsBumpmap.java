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

import org.parallax3d.parallax.core.client.events.AnimationReadyEvent;
import org.parallax3d.parallax.core.client.gl2.enums.PixelFormat;
import org.parallax3d.parallax.core.client.renderers.ShadowMap;
import org.parallax3d.parallax.core.client.textures.Texture;
import org.parallax3d.parallax.core.shared.cameras.PerspectiveCamera;
import org.parallax3d.parallax.core.shared.core.AbstractGeometry;
import org.parallax3d.parallax.core.shared.lights.*;
import org.parallax3d.parallax.core.shared.materials.Material;
import org.parallax3d.parallax.core.shared.materials.MeshPhongMaterial;
import org.parallax3d.parallax.core.shared.math.Color;
import org.parallax3d.parallax.core.shared.objects.Mesh;
import org.parallax3d.parallax.demo.client.ContentWidget;
import org.parallax3d.parallax.demo.client.DemoAnnotations;
import org.parallax3d.parallax.loader.shared.XHRLoader;
import org.parallax3d.parallax.core.client.gl2.enums.TextureWrapMode;
import org.parallax3d.parallax.core.shared.core.Geometry;
import org.parallax3d.parallax.core.shared.lights.DirectionalLight;
import org.parallax3d.parallax.core.shared.lights.PointLight;
import org.parallax3d.parallax.core.shared.lights.SpotLight;
import org.parallax3d.parallax.loader.shared.JsonLoader;

import com.google.gwt.core.client.GWT;
import com.google.gwt.core.client.RunAsyncCallback;
import com.google.gwt.event.dom.client.MouseMoveEvent;
import com.google.gwt.event.dom.client.MouseMoveHandler;
import com.google.gwt.user.client.rpc.AsyncCallback;

public final class MaterialsBumpmap extends ContentWidget
{

	/*
	 * Prepare Rendering Scene
	 */
	@DemoAnnotations.DemoSource
	class DemoScene extends DemoAnimatedScene 
	{
		private static final String texture = "./static/models/obj/leeperrysmith/Infinite-Level_02_Disp_NoSmoothUV-4096.jpg";
		private static final String model = "./static/models/obj/leeperrysmith/LeePerrySmith.js";
		
		PerspectiveCamera camera;
		
		Mesh mesh;
		
		int mouseX = 0, mouseY = 0;

		@Override
		protected void onStart()
		{
			camera = new PerspectiveCamera(
					27, // fov
					getRenderer().getAbsoluteAspectRation(), // aspect 
					1, // near
					10000 // far 
			);
			
			camera.getPosition().setZ(1200);

			// LIGHTS

			getScene().add( new AmbientLight( 0x444444 ) );

			//

			PointLight pointLight = new PointLight( 0xffffff, 1.5, 1000 );
			pointLight.getColor().setHSL( 0.05, 1.0, 0.95 );
			pointLight.getPosition().set( 0, 0, 600 );

			getScene().add( pointLight );

			// shadow for PointLight

			SpotLight spotLight = new SpotLight( 0xffffff, 1.5 );
			spotLight.getPosition().set( 0.05, 0.05, 1 );
			spotLight.getColor().setHSL( 0.6, 1.0, 0.95 );
			getScene().add( spotLight );

			spotLight.getPosition().multiply( 700 );

			spotLight.setCastShadow(true);
			spotLight.setOnlyShadow(true);
//			spotLight.setShadowCameraVisible(true);

			spotLight.setShadowMapWidth( 2048 );
			spotLight.setShadowMapHeight( 2048 );

			spotLight.setShadowCameraNear( 200 );
			spotLight.setShadowCameraFar( 1500 );

			spotLight.setShadowCameraFov( 40 );

			spotLight.setShadowBias( -0.005 );
			spotLight.setShadowDarkness( 0.35 );

			//

			DirectionalLight directionalLight = new DirectionalLight( 0xffffff, 1.5 );
			directionalLight.getPosition().set( 1, -0.5, 1 );
			directionalLight.getColor().setHSL( 0.6, 1, 0.95 );
			getScene().add( directionalLight );

			directionalLight.getPosition().multiply( 500 );

			directionalLight.setCastShadow( true );
//			directionalLight.setShadowCameraVisible(true);

			directionalLight.setShadowMapWidth( 2048 );
			directionalLight.setShadowMapHeight( 2048 );

			directionalLight.setShadowCameraNear( 200 );
			directionalLight.setShadowCameraFar( 1500 );

			directionalLight.setShadowCameraLeft( -500 );
			directionalLight.setShadowCameraRight( 500 );
			directionalLight.setShadowCameraTop( 500 );
			directionalLight.setShadowCameraBottom( -500 );

			directionalLight.setShadowBias( -0.005 );
			directionalLight.setShadowDarkness( 0.35 );

			//

			DirectionalLight directionalLight2 = new DirectionalLight( 0xffffff, 1.2 );
			directionalLight2.getPosition().set( 1, -0.5, -1 );
			directionalLight2.getColor().setHSL( 0.08, 1.0, 0.825 );
			getScene().add( directionalLight2 );

			Texture mapHeight = new Texture( texture );

			mapHeight.setAnisotropy(4);
			mapHeight.getRepeat().set( 0.998, 0.998 );
			mapHeight.getOffset().set( 0.001, 0.001 );
			mapHeight.setWrapS(TextureWrapMode.REPEAT);
			mapHeight.setWrapT(TextureWrapMode.REPEAT);
			mapHeight.setFormat(PixelFormat.RGB);

			final MeshPhongMaterial material = new MeshPhongMaterial();
			material.setAmbient(new Color(0x552811));
			material.setColor(new Color(0x552811));
			material.setSpecular(new Color(0x333333));
			material.setShininess(25);
			material.setBumpMap(mapHeight);
			material.setBumpScale(19);
			material.setMetal(false);
			
			new JsonLoader(model, new XHRLoader.ModelLoadHandler() {

				@Override
				public void onModelLoaded(XHRLoader loader, AbstractGeometry geometry) {
					createScene( (Geometry) geometry, 100, material );
				}
			});
			
			ShadowMap shadowMap = new ShadowMap(getRenderer(), getScene());
			shadowMap.setCullFrontFaces(false);

			//

			getRenderer().setClearColor(0x060708);
			getRenderer().setGammaInput(true);
			getRenderer().setGammaOutput(true);
		}
		
		private void createScene( Geometry geometry, double scale, Material material )
		{
			mesh = new Mesh( geometry, material );

			mesh.getPosition().setY( - 50 );
			mesh.getScale().set( scale );

			mesh.setCastShadow(true);
			mesh.setReceiveShadow(true);

			getScene().add( mesh );
		}
		
		@Override
		protected void onUpdate(double duration)
		{
			double targetX = mouseX * .001;
			double targetY = mouseY * .001;

			if ( mesh != null ) 
			{
				mesh.getRotation().addY( 0.05 * ( targetX - mesh.getRotation().getY() ) );
				mesh.getRotation().addX( 0.05 * ( targetY - mesh.getRotation().getX() ) );
			}
			
			getRenderer().render(getScene(), camera);
		}
	}
		
	public MaterialsBumpmap() 
	{
		super("Bump mapping", "This example based on the three.js example.");
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
		    	  	rs.mouseX = event.getX() - renderingPanel.getRenderer().getAbsoluteWidth() / 2 ; 
		    	  	rs.mouseY = event.getY() - renderingPanel.getRenderer().getAbsoluteHeight() / 2;
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
		GWT.runAsync(MaterialsBumpmap.class, new RunAsyncCallback() 
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
