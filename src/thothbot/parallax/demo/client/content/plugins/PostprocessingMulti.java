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

package thothbot.parallax.demo.client.content.plugins;

import java.util.Map;

import thothbot.parallax.core.client.events.HasEventBus;
import thothbot.parallax.core.client.events.ViewportResizeEvent;
import thothbot.parallax.core.client.events.ViewportResizeHandler;
import thothbot.parallax.core.client.gl2.enums.PixelFormat;
import thothbot.parallax.core.client.gl2.enums.TextureMagFilter;
import thothbot.parallax.core.client.gl2.enums.TextureMinFilter;
import thothbot.parallax.core.client.shaders.NormalMapShader;
import thothbot.parallax.core.client.shaders.Uniform;
import thothbot.parallax.core.client.textures.RenderTargetTexture;
import thothbot.parallax.core.client.textures.Texture;
import thothbot.parallax.core.shared.Log;
import thothbot.parallax.core.shared.cameras.OrthographicCamera;
import thothbot.parallax.core.shared.cameras.PerspectiveCamera;
import thothbot.parallax.core.shared.core.Geometry;
import thothbot.parallax.core.shared.geometries.PlaneGeometry;
import thothbot.parallax.core.shared.lights.DirectionalLight;
import thothbot.parallax.core.shared.materials.MeshBasicMaterial;
import thothbot.parallax.core.shared.materials.ShaderMaterial;
import thothbot.parallax.core.shared.math.Color;
import thothbot.parallax.core.shared.math.Vector2;
import thothbot.parallax.core.shared.objects.Mesh;
import thothbot.parallax.core.shared.scenes.Scene;
import thothbot.parallax.demo.client.ContentWidget;
import thothbot.parallax.demo.client.Demo;
import thothbot.parallax.demo.client.DemoAnnotations.DemoSource;
import thothbot.parallax.loader.shared.JsonLoader;
import thothbot.parallax.plugins.postprocessing.BloomPass;
import thothbot.parallax.plugins.postprocessing.ClearMaskPass;
import thothbot.parallax.plugins.postprocessing.DotScreenPass;
import thothbot.parallax.plugins.postprocessing.FilmPass;
import thothbot.parallax.plugins.postprocessing.MaskPass;
import thothbot.parallax.plugins.postprocessing.Postprocessing;
import thothbot.parallax.plugins.postprocessing.RenderPass;
import thothbot.parallax.plugins.postprocessing.ShaderPass;
import thothbot.parallax.plugins.postprocessing.TexturePass;
import thothbot.parallax.plugins.postprocessing.shaders.BleachBypassShader;
import thothbot.parallax.plugins.postprocessing.shaders.ColorifyShader;
import thothbot.parallax.plugins.postprocessing.shaders.CopyShader;
import thothbot.parallax.plugins.postprocessing.shaders.HorizontalBlurShader;
import thothbot.parallax.plugins.postprocessing.shaders.SepiaShader;
import thothbot.parallax.plugins.postprocessing.shaders.VerticalBlurShader;
import thothbot.parallax.plugins.postprocessing.shaders.VignetteShader;

import com.google.gwt.core.client.GWT;
import com.google.gwt.core.client.RunAsyncCallback;
import com.google.gwt.http.client.RequestException;
import com.google.gwt.resources.client.ImageResource;
import com.google.gwt.user.client.rpc.AsyncCallback;

public final class PostprocessingMulti extends ContentWidget
{

	/*
	 * Prepare Rendering Scene
	 */
	@DemoSource
	class DemoScene extends DemoAnimatedScene implements HasEventBus, ViewportResizeHandler
	{
		private static final String model = "./static/models/obj/leeperrysmith/LeePerrySmith.js";
		private static final String texture = "./static/models/obj/leeperrysmith/Infinite-Level_02_Disp_NoSmoothUV-4096.jpg";
		private static final String textureCol = "./static/models/obj/leeperrysmith/Map-COL.jpg";
		private static final String texturebg = "./static/textures/cube/swedishRoyalCastle/pz.jpg";
		
		PerspectiveCamera cameraPerspective;
		OrthographicCamera cameraOrtho;
		
		Scene sceneModel, sceneBG;
		
		Mesh mesh, quadBG, quadMask;
		
		Postprocessing composerScene, composer1, composer2, composer3, composer4;
		TexturePass renderScene;

		@Override
		public void onResize(ViewportResizeEvent event) 
		{
/*
			Canvas3d canvas = getRenderer().getCanvas();
			int halfWidth = getRenderer().getAbsoluteWidth() / 2;
			int halfHeight = getRenderer().getAbsoluteHeight() / 2;
			
			cameraPerspective.setAspectRatio( getRenderer().getAbsoluteAspectRation() );

			cameraOrtho.setLeft( -halfWidth );
			cameraOrtho.setRight( halfWidth );
			cameraOrtho.setTop( halfHeight );
			cameraOrtho.setBottom( -halfHeight );

			cameraOrtho.updateProjectionMatrix();

			composerScene.reset( new WebGLRenderTarget( halfWidth * 2, halfHeight * 2, rtParameters ) );

			composer1.reset( new WebGLRenderTarget( halfWidth, halfHeight, rtParameters ) );
			composer2.reset( new WebGLRenderTarget( halfWidth, halfHeight, rtParameters ) );
			composer3.reset( new WebGLRenderTarget( halfWidth, halfHeight, rtParameters ) );
			composer4.reset( new WebGLRenderTarget( halfWidth, halfHeight, rtParameters ) );

			renderScene.getMaterial().getShader().getUniforms().get("tDiffuse").setValue( composerScene.getRenderTarget2() );

			quadBG.getScale().set( getRenderer().getAbsoluteWidth(), 1, getRenderer().getAbsoluteHeight() );
			quadMask.getScale().set( halfWidth, 1, halfHeight );
			*/
		}

		@Override
		protected void onStart()
		{
			EVENT_BUS.addHandler(ViewportResizeEvent.TYPE, this);

			int width = getRenderer().getAbsoluteWidth();
			int height = getRenderer().getAbsoluteHeight();
			
			cameraOrtho = new OrthographicCamera( width, height, -10000, 10000 );
			cameraOrtho.getPosition().setZ( 100 );

			cameraPerspective = new PerspectiveCamera( 50, getRenderer().getAbsoluteAspectRation(), 1, 10000 );
			cameraPerspective.getPosition().setZ( 900 );

			//

			sceneModel = new Scene();
			sceneBG = new Scene();

			//

			DirectionalLight directionalLight = new DirectionalLight( 0xffffff );
			directionalLight.getPosition().set( 0, -0.1, 1 ).normalize();
			sceneModel.add( directionalLight );

			final JsonLoader jsonLoader = new JsonLoader();
			try
			{
				jsonLoader.load(model, new JsonLoader.ModelLoadHandler() {

					@Override
					public void onModelLoaded() {		
						createMesh( jsonLoader.getGeometry(), 100 );
					}
				});
			}
			catch (RequestException exception) 
			{
				Log.error("Error while loading JSON file.");
			}
			
			//

			MeshBasicMaterial materialColor = new MeshBasicMaterial();
			materialColor.setMap(new Texture(texturebg));
			materialColor.setDepthTest(false);

			quadBG = new Mesh( new PlaneGeometry( 1, 1 ), materialColor );
			quadBG.getPosition().setZ( -500 );
			quadBG.getScale().set( width, height, 1 );
			sceneBG.add( quadBG );

			//

			Scene sceneMask = new Scene();
			MeshBasicMaterial maskMaterial = new MeshBasicMaterial();
			maskMaterial.setColor(new Color(0xffaa00));

			quadMask = new Mesh( new PlaneGeometry( 1, 1 ), maskMaterial );
			quadMask.getPosition().setZ( -300 );
			quadMask.getScale().set( width / 2, height / 2, 1 );
			sceneMask.add( quadMask );

			//

			getRenderer().setClearColor( 0x000000, 1 );
			getRenderer().setAutoClear(false);
			getRenderer().setGammaInput(true);
			getRenderer().setGammaOutput(true);

			BleachBypassShader shaderBleach = new BleachBypassShader();
			SepiaShader shaderSepia = new SepiaShader();
			VignetteShader shaderVignette = new VignetteShader();
			CopyShader shaderCopy = new CopyShader();

			ShaderPass effectBleach = new ShaderPass( shaderBleach );
			ShaderPass effectSepia = new ShaderPass( shaderSepia );
			ShaderPass effectVignette = new ShaderPass( shaderVignette );
			ShaderPass effectScreen = new ShaderPass( shaderCopy );

			effectBleach.getUniforms().get("opacity").setValue( 0.95 );
			effectSepia.getUniforms().get("amount").setValue( 0.9 );
			effectVignette.getUniforms().get("offset").setValue( 0.95 );
			effectVignette.getUniforms().get("darkness").setValue( 1.6 );

			BloomPass effectBloom = new BloomPass( 0.5 );
			FilmPass effectFilm = new FilmPass( 0.35, 0.025, 648, false );
			FilmPass effectFilmBW = new FilmPass( 0.35, 0.5, 2048, true );
			DotScreenPass effectDotScreen = new DotScreenPass( new Vector2( 0, 0 ), 0.5, 0.8 );

			ShaderPass effectHBlur = new ShaderPass( new HorizontalBlurShader() );
			ShaderPass effectVBlur = new ShaderPass( new VerticalBlurShader() );
			effectHBlur.getUniforms().get("h").setValue( 2.0 / ( width / 2.0 ) );
			effectVBlur.getUniforms().get("v").setValue( 2.0 / ( height / 2.0 ) );

			ShaderPass effectColorify1 = new ShaderPass( new ColorifyShader() );
			ShaderPass effectColorify2 = new ShaderPass( new ColorifyShader() );
			((Color)effectColorify1.getUniforms().get("color").getValue()).setRGB( 1, 0.8, 0.8 );
			((Color)effectColorify2.getUniforms().get("color").getValue()).setRGB( 1, 0.75, 0.5 );

			ClearMaskPass clearMask = new ClearMaskPass();
			MaskPass renderMask = new MaskPass( sceneModel, cameraPerspective );
			MaskPass renderMaskInverse = new MaskPass( sceneModel, cameraPerspective );

			renderMaskInverse.setInverse(true);

			//effectFilm.renderToScreen = true;
			//effectFilmBW.renderToScreen = true;
			//effectDotScreen.renderToScreen = true;
			//effectBleach.renderToScreen = true;
			effectVignette.setRenderToScreen(true);
			//effectScreen.renderToScreen = true;

			//

			RenderPass renderBackground = new RenderPass( sceneBG, cameraOrtho );
			RenderPass renderModel = new RenderPass( sceneModel, cameraPerspective );

			renderModel.setClear(false);
			
			RenderTargetTexture rt = new RenderTargetTexture(width, height);
			rt.setMinFilter(TextureMinFilter.LINEAR);
			rt.setMagFilter(TextureMagFilter.LINEAR);
			rt.setFormat(PixelFormat.RGB);
			rt.setStencilBuffer(true);

			composerScene = new Postprocessing( getRenderer(), getScene(), rt);

			composerScene.addPass( renderBackground );
			composerScene.addPass( renderModel );
			composerScene.addPass( renderMaskInverse );
			composerScene.addPass( effectHBlur );
			composerScene.addPass( effectVBlur );
			composerScene.addPass( clearMask );

			//

			renderScene = new TexturePass( composerScene.getRenderTarget2() );

			//

			composer1 = new Postprocessing( getRenderer(), getScene(), rt.clone() );
			composer1.setEnabled(false);

			composer1.addPass( renderScene );
			//composer1.addPass( renderMask );
			composer1.addPass( effectFilmBW );
			//composer1.addPass( clearMask );
			composer1.addPass( effectVignette );

			//

			composer2 = new Postprocessing( getRenderer(), getScene(), rt.clone() );
			composer2.setEnabled(false);

			composer2.addPass( renderScene );
			composer2.addPass( effectDotScreen );
			composer2.addPass( renderMask );
			composer2.addPass( effectColorify1 );
			composer2.addPass( clearMask );
			composer2.addPass( renderMaskInverse );
			composer2.addPass( effectColorify2 );
			composer2.addPass( clearMask );
			composer2.addPass( effectVignette );

			//

			composer3 = new Postprocessing( getRenderer(), getScene(), rt.clone() );
			composer3.setEnabled(false);

			composer3.addPass( renderScene );
			//composer3.addPass( renderMask );
			composer3.addPass( effectSepia );
			composer3.addPass( effectFilm );
			//composer3.addPass( clearMask );
			composer3.addPass( effectVignette );

			//

			composer4 = new Postprocessing( getRenderer(), getScene(), rt.clone() );
			composer4.setEnabled(false);

			composer4.addPass( renderScene );
			//composer4.addPass( renderMask );
			composer4.addPass( effectBloom );
			composer4.addPass( effectFilm );
			composer4.addPass( effectBleach );
			//composer4.addPass( clearMask );
			composer4.addPass( effectVignette );

			renderScene.getMaterial().getShader().getUniforms().get("tDiffuse").setValue( composerScene.getRenderTarget2() );
		}
		
		private void createMesh( Geometry geometry, double scale ) 
		{

			geometry.computeTangents();

			int ambient = 0x444444, 
					diffuse = 0x999999, 
					specular = 0x080808;
			double shininess = 20;

			ShaderMaterial mat2 = new ShaderMaterial( new NormalMapShader() );
			mat2.setLights(true);
			Map<String, Uniform> uniforms = mat2.getShader().getUniforms();

			uniforms.get("tNormal").setValue( new Texture( texture ));
			((Vector2)uniforms.get("uNormalScale").getValue()).set( 0.75, 0.75 );

			uniforms.get("tDiffuse").setValue( new Texture( textureCol ));

			uniforms.get("enableAO").setValue( false );
			uniforms.get("enableDiffuse").setValue( true );

			((Color)uniforms.get("uDiffuseColor").getValue()).setHex( diffuse );
			((Color)uniforms.get("uSpecularColor").getValue()).setHex( specular );
			((Color)uniforms.get("uAmbientColor").getValue()).setHex( ambient );

			uniforms.get("uShininess").setValue( shininess );

			((Color)uniforms.get("uDiffuseColor").getValue()).convertGammaToLinear();
			((Color)uniforms.get("uSpecularColor").getValue()).convertGammaToLinear();
			((Color)uniforms.get("uAmbientColor").getValue()).convertGammaToLinear();

			mesh = new Mesh( geometry, mat2 );
			mesh.getPosition().set( 0, -50, 0 );
			mesh.getScale().set( scale );

			sceneModel.add( mesh );
		}
		
		@Override
		protected void onUpdate(double duration)
		{
			if ( mesh != null )
				mesh.getRotation().addY( -0.04 );

			int halfWidth = getRenderer().getAbsoluteWidth() / 2;
			int halfHeight = getRenderer().getAbsoluteHeight() / 2;
			
			getRenderer().setViewport( 0, 0, 2 * halfWidth, 2 * halfHeight );

			getRenderer().clear();
			getRenderer().render(getScene(), this.cameraPerspective);

			getRenderer().setViewport( 0, 0, halfWidth, halfHeight );
			composer1.setEnabled(true);
			getRenderer().render(getScene(), this.cameraPerspective);
			composer1.setEnabled(false);

			getRenderer().setViewport( halfWidth, 0, halfWidth, halfHeight );
			composer2.setEnabled(true);
			getRenderer().render(getScene(), this.cameraPerspective);
			composer2.setEnabled(false);

			getRenderer().setViewport( 0, halfHeight, halfWidth, halfHeight );
			composer3.setEnabled(true);
			getRenderer().render(getScene(), this.cameraPerspective);
			composer3.setEnabled(false);

			getRenderer().setViewport( halfWidth, halfHeight, halfWidth, halfHeight );
			composer4.setEnabled(true);
			getRenderer().render(getScene(), this.cameraPerspective);
			composer4.setEnabled(false);
		}
	}
		
	public PostprocessingMulti() 
	{
		super("Postprocessing", "This example based on the three.js example.");
	}
	
	@Override
	public DemoScene onInitialize()
	{
		return new DemoScene();
	}

	@Override
	public ImageResource getIcon()
	{
		return Demo.resources.examplePostprocessingMulti();
	}
	
	@Override
	protected void asyncOnInitialize(final AsyncCallback<DemoAnimatedScene> callback)
	{
		GWT.runAsync(PostprocessingMulti.class, new RunAsyncCallback() 
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
