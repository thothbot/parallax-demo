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

package thothbot.parallax.demo.client.content.materials;

import java.util.Map;

import thothbot.parallax.core.client.AnimationReadyEvent;
import thothbot.parallax.core.client.context.Canvas3d;
import thothbot.parallax.core.client.gl2.enums.PixelFormat;
import thothbot.parallax.core.client.gl2.enums.TextureMagFilter;
import thothbot.parallax.core.client.gl2.enums.TextureMinFilter;
import thothbot.parallax.core.client.gl2.enums.TextureWrapMode;
import thothbot.parallax.core.client.renderers.ShadowMap;
import thothbot.parallax.core.client.shaders.Uniform;
import thothbot.parallax.core.client.textures.RenderTargetTexture;
import thothbot.parallax.core.client.textures.Texture;
import thothbot.parallax.core.shared.Log;
import thothbot.parallax.core.shared.cameras.PerspectiveCamera;
import thothbot.parallax.core.shared.core.Color;
import thothbot.parallax.core.shared.core.Geometry;
import thothbot.parallax.core.shared.core.Vector4;
import thothbot.parallax.core.shared.lights.AmbientLight;
import thothbot.parallax.core.shared.lights.DirectionalLight;
import thothbot.parallax.core.shared.lights.PointLight;
import thothbot.parallax.core.shared.lights.SpotLight;
import thothbot.parallax.core.shared.materials.ShaderMaterial;
import thothbot.parallax.core.shared.objects.Mesh;
import thothbot.parallax.demo.client.ContentWidget;
import thothbot.parallax.demo.client.Demo;
import thothbot.parallax.demo.client.DemoAnnotations.DemoSource;
import thothbot.parallax.demo.resources.BeckmannShader;
import thothbot.parallax.demo.resources.SkinSimpleShader;
import thothbot.parallax.loader.shared.JsonLoader;
import thothbot.parallax.plugins.postprocessing.client.Postprocessing;
import thothbot.parallax.plugins.postprocessing.client.ShaderPass;
import thothbot.parallax.plugins.postprocessing.client.shaders.ScreenShader;

import com.google.gwt.core.client.GWT;
import com.google.gwt.core.client.RunAsyncCallback;
import com.google.gwt.event.dom.client.MouseMoveEvent;
import com.google.gwt.event.dom.client.MouseMoveHandler;
import com.google.gwt.http.client.RequestException;
import com.google.gwt.resources.client.ImageResource;
import com.google.gwt.user.client.rpc.AsyncCallback;

public final class MaterialsBumpmapSkin extends ContentWidget 
{

	/*
	 * Prepare Rendering Scene
	 */
	@DemoSource
	class DemoScene extends DemoAnimatedScene 
	{

		private static final String texture = "./static/models/obj/leeperrysmith/Infinite-Level_02_Disp_NoSmoothUV-4096.jpg";
		private static final String textureSpec = "./static/models/obj/leeperrysmith/Map-SPEC.jpg";
		private static final String textureCol = "./static/models/obj/leeperrysmith/Map-COL.jpg";
		private static final String model = "./static/models/obj/leeperrysmith/LeePerrySmith.js";
		
		PerspectiveCamera camera;
		
		Mesh mesh;
		
		Postprocessing composerBeckmann;
		
		int mouseX = 0, mouseY = 0;
		
		boolean firstPass = true;

		@Override
		protected void onStart()
		{
			camera = new PerspectiveCamera(
					27, // fov
					getRenderer().getCanvas().getAspectRation(), // aspect 
					1, // near
					10000 // far 
			);
			
			camera.getPosition().setZ(1200);

			// LIGHTS

			getScene().add( new AmbientLight( 0x555555 ) );

			PointLight pointLight = new PointLight( 0xffffff, 1.5, 1000 );
			pointLight.getPosition().set( 0, 0, 600 );

			getScene().add( pointLight );

			// shadow for PointLight

			SpotLight spotLight = new SpotLight( 0xffffff, 1 );
			spotLight.getPosition().set( 0.05, 0.05, 1 );
			getScene().add( spotLight );
			
			spotLight.getPosition().multiply( 700 );

			spotLight.setCastShadow(true);
			spotLight.setOnlyShadow(true);
			//spotLight.shadowCameraVisible = true;

			spotLight.setShadowMapWidth( 2048 );
			spotLight.setShadowMapHeight( 2048 );

			spotLight.setShadowCameraNear( 200 );
			spotLight.setShadowCameraFar( 1500 );

			spotLight.setShadowCameraFov( 40 );

			spotLight.setShadowBias( -0.005 );
			spotLight.setShadowDarkness( 0.15 );

			//

			DirectionalLight directionalLight = new DirectionalLight( 0xffffff, 0.85 );
			directionalLight.getPosition().set( 1, -0.5, 1 );
			directionalLight.getColor().setHSV( 0.6, 0.3, 1 );
			getScene().add( directionalLight );

			directionalLight.getPosition().multiply( 500 );
			
			directionalLight.setCastShadow(true);
			//directionalLight.shadowCameraVisible = true;

			directionalLight.setShadowMapWidth( 2048 );
			directionalLight.setShadowMapHeight( 2048 );

			directionalLight.setShadowCameraNear( 200 );
			directionalLight.setShadowCameraFar( 1500 );

			directionalLight.setShadowCameraLeft( -500 );
			directionalLight.setShadowCameraRight( 500 );
			directionalLight.setShadowCameraTop( 500 );
			directionalLight.setShadowCameraBottom( -500 );

			directionalLight.setShadowBias( -0.005 );
			directionalLight.setShadowDarkness( 0.15 );

			//

			DirectionalLight directionalLight2 = new DirectionalLight( 0xffffff, 0.85 );
			directionalLight2.getPosition().set( 1, -0.5, -1 );
			getScene().add( directionalLight2 );
			
			// COMPOSER BECKMANN

			ShaderPass effectBeckmann = new ShaderPass( new BeckmannShader() );
			ShaderPass effectScreen = new ShaderPass( new ScreenShader() );

			effectScreen.setRenderToScreen(true);

			RenderTargetTexture target = new RenderTargetTexture( 512, 512 );
			target.setMinFilter(TextureMinFilter.LINEAR);
			target.setMagFilter(TextureMagFilter.LINEAR);
			target.setFormat(PixelFormat.RGB);
			target.setStencilBuffer(false);
			composerBeckmann = new Postprocessing( getRenderer(), getScene(), target );
//			composerBeckmann.addPass( effectBeckmann );
//			composerBeckmann.addPass( effectScreen );

			//
			
			final JsonLoader jsonLoader = new JsonLoader();
			try
			{
				jsonLoader.load(model, new JsonLoader.ModelLoadHandler() {

					@Override
					public void onModeLoad() {		
						createScene( jsonLoader.getGeometry(), 100 );
					}
				});
			}
			catch (RequestException exception) 
			{
				Log.error("Error while loading JSON file.");
			}

			//

			getRenderer().setClearColorHex(0x4c5159);

			ShadowMap shadowMap = new ShadowMap(getRenderer(), getScene());
			shadowMap.setCullFrontFaces(false);

			getRenderer().setAutoClear(false);
			getRenderer().setGammaInput(true);
			getRenderer().setGammaOutput(true);
			getRenderer().setPhysicallyBasedShading(true);
		}
		
		private void createScene( Geometry geometry, double scale ) 
		{
			Texture mapHeight = new Texture( texture );

			mapHeight.setAnisotropy(4);
			mapHeight.getRepeat().set( 0.998, 0.998 );
			mapHeight.getOffset().set( 0.001, 0.001 );
			mapHeight.setWrapS(TextureWrapMode.REPEAT);
			mapHeight.setWrapT(TextureWrapMode.REPEAT);
			mapHeight.setFormat(PixelFormat.RGB);

			Texture mapSpecular = new Texture( textureSpec );
			mapSpecular.getRepeat().set( 0.998, 0.998 );
			mapSpecular.getOffset().set( 0.001, 0.001 );
			mapSpecular.setWrapS(TextureWrapMode.REPEAT);
			mapSpecular.setWrapT(TextureWrapMode.REPEAT);
			mapSpecular.setFormat(PixelFormat.RGB);

			Texture mapColor = new Texture( textureCol );
			mapColor.getRepeat().set( 0.998, 0.998 );
			mapColor.getOffset().set( 0.001, 0.001 );
			mapColor.setWrapS(TextureWrapMode.REPEAT);
			mapColor.setWrapT(TextureWrapMode.REPEAT);
			mapColor.setFormat(PixelFormat.RGB);

			SkinSimpleShader shader = new SkinSimpleShader();

			Map<String, Uniform> uniforms = shader.getUniforms();
			
			uniforms.get( "enableBump" ).setValue( true );
			uniforms.get( "enableSpecular" ).setValue( true );

			uniforms.get( "tBeckmann" ).setValue( composerBeckmann.getRenderTarget1() );
			uniforms.get( "tDiffuse" ).setValue( mapColor );

			uniforms.get( "bumpMap" ).setValue( mapHeight );
			uniforms.get( "specularMap" ).setValue( mapSpecular );

			((Color)uniforms.get( "uAmbientColor" ).getValue()).setHex( 0xa0a0a0 );
			((Color)uniforms.get( "uDiffuseColor" ).getValue()).setHex( 0xa0a0a0 );
			((Color)uniforms.get( "uSpecularColor" ).getValue()).setHex( 0xa0a0a0 );

			uniforms.get( "uRoughness" ).setValue( 0.145 );
			uniforms.get( "uSpecularBrightness" ).setValue( 0.75 );

			uniforms.get( "bumpScale" ).setValue( 16.0 );

			((Vector4)uniforms.get( "offsetRepeat" ).getValue()).set( 0.001, 0.001, 0.998, 0.998 );

			ShaderMaterial material = new ShaderMaterial( shader );
			material.setLights(true);

			mesh = new Mesh( geometry, material );

			mesh.getPosition().setY(- 50 );
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
			
//			if ( firstPass ) 
//			{
//				composerBeckmann.render();
//				firstPass = false;
//			}

			getRenderer().clear();
			getRenderer().render(getScene(), camera);
		}
	}
		
	public MaterialsBumpmapSkin() 
	{
		super("Single-pass skin material", "This example based on the three.js example.");
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
		    	  	rs.mouseX = event.getX() - canvas.getWidth() / 2 ; 
		    	  	rs.mouseY = event.getY() - canvas.getHeight() / 2;
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
		return Demo.resources.exampleMaterialsBumpmapSkin();
	}
	
	@Override
	protected void asyncOnInitialize(final AsyncCallback<DemoAnimatedScene> callback)
	{
		GWT.runAsync(MaterialsBumpmapSkin.class, new RunAsyncCallback() 
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
