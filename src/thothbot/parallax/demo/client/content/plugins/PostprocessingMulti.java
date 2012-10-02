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

package thothbot.parallax.demo.client.content.plugins;

import thothbot.parallax.core.shared.cameras.OrthographicCamera;
import thothbot.parallax.core.shared.cameras.PerspectiveCamera;
import thothbot.parallax.core.shared.core.Geometry;
import thothbot.parallax.core.shared.scenes.Scene;
import thothbot.parallax.demo.client.ContentWidget;
import thothbot.parallax.demo.client.Demo;
import thothbot.parallax.demo.client.DemoAnnotations.DemoSource;

import com.google.gwt.core.client.GWT;
import com.google.gwt.core.client.RunAsyncCallback;
import com.google.gwt.resources.client.ImageResource;
import com.google.gwt.user.client.rpc.AsyncCallback;

public final class PostprocessingMulti extends ContentWidget
{

	/*
	 * Prepare Rendering Scene
	 */
	@DemoSource
	class DemoScene extends DemoAnimatedScene 
	{

		OrthographicCamera cameraOrtho;

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
						
//			cameraOrtho = new OrthographicCamera( -halfWidth, halfWidth, halfHeight, -halfHeight, -10000, 10000 );
		}

		@Override
		protected void onResize() 
		{
			super.onResize();

//			cameraOrtho.left = -halfWidth;
//			cameraOrtho.right = halfWidth;
//			cameraOrtho.top = halfHeight;
//			cameraOrtho.bottom = -halfHeight;
//
//			cameraOrtho.updateProjectionMatrix();
//
//			composerScene.reset( new WebGLRenderTarget( halfWidth * 2, halfHeight * 2, rtParameters ) );
//
//			composer1.reset( new WebGLRenderTarget( halfWidth, halfHeight, rtParameters ) );
//			composer2.reset( new WebGLRenderTarget( halfWidth, halfHeight, rtParameters ) );
//			composer3.reset( new WebGLRenderTarget( halfWidth, halfHeight, rtParameters ) );
//			composer4.reset( new WebGLRenderTarget( halfWidth, halfHeight, rtParameters ) );
//
//			renderScene.uniforms[ "tDiffuse" ].value = composerScene.renderTarget2;
//
//			quadBG.scale.set( window.innerWidth, 1, window.innerHeight );
//			quadMask.scale.set( window.innerWidth / 2, 1, window.innerHeight / 2 );
		}

		@Override
		protected void onStart()
		{
			getCamera().getPosition().setZ(900);
			getScene().add(getCamera());
			
			cameraOrtho.getPosition().setZ( 100 );

			//

//			sceneModel = new THREE.Scene();
//			sceneBG = new THREE.Scene();
//
//			//
//
//			directionalLight = new DirectionalLight( 0xffffff );
//			directionalLight.position.set( 0, -0.1, 1 ).normalize();
//			sceneModel.add( directionalLight );
//
//			loader = new THREE.JSONLoader( true );
//			document.body.appendChild( loader.statusDomElement );
//			loader.load( "obj/leeperrysmith/LeePerrySmith.js", function( geometry ) { createMesh( geometry, sceneModel, 100 ) } );
//
//			//
//
//			var materialColor = new THREE.MeshBasicMaterial( { map: THREE.ImageUtils.loadTexture( "textures/cube/SwedishRoyalCastle/pz.jpg" ), depthTest: false } );
//
//			quadBG = new THREE.Mesh( new THREE.PlaneGeometry( 1, 1 ), materialColor );
//			quadBG.position.z = -500;
//			quadBG.scale.set( width, height, 1 );
//			sceneBG.add( quadBG );
//
//			//
//
//			var sceneMask = new THREE.Scene();
//
//			quadMask = new THREE.Mesh( new THREE.PlaneGeometry( 1, 1 ), new THREE.MeshBasicMaterial( { color: 0xffaa00 } )  );
//			quadMask.position.z = -300;
//			quadMask.scale.set( width / 2, height / 2, 1 );
//			sceneMask.add( quadMask );
//
//			//
//
//			renderer = new THREE.WebGLRenderer( { antialias: false } );
//			renderer.setSize( width, height );
//			renderer.setClearColorHex( 0x000000, 1 );
//			renderer.autoClear = false;
//
//			//
//
//			renderer.gammaInput = true;
//			renderer.gammaOutput = true;
//
//			//
//
//			container.appendChild( renderer.domElement );
//
//			//
//
//			stats = new Stats();
//			stats.domElement.style.position = 'absolute';
//			stats.domElement.style.top = '0px';
//			//container.appendChild( stats.domElement );
//
//			//
//
//			var shaderBleach = THREE.ShaderExtras[ "bleachbypass" ];
//			var shaderSepia = THREE.ShaderExtras[ "sepia" ];
//			var shaderVignette = THREE.ShaderExtras[ "vignette" ];
//			var shaderScreen = THREE.ShaderExtras[ "screen" ];
//
//			var effectBleach = new THREE.ShaderPass( shaderBleach );
//			var effectSepia = new THREE.ShaderPass( shaderSepia );
//			var effectVignette = new THREE.ShaderPass( shaderVignette );
//			var effectScreen = new THREE.ShaderPass( shaderScreen );
//
//			effectBleach.uniforms[ "opacity" ].value = 0.95;
//
//			effectSepia.uniforms[ "amount" ].value = 0.9;
//
//			effectVignette.uniforms[ "offset" ].value = 0.95;
//			effectVignette.uniforms[ "darkness" ].value = 1.6;
//
//			var effectBloom = new THREE.BloomPass( 0.5 );
//			var effectFilm = new THREE.FilmPass( 0.35, 0.025, 648, false );
//			var effectFilmBW = new THREE.FilmPass( 0.35, 0.5, 2048, true );
//			var effectDotScreen = new THREE.DotScreenPass( new THREE.Vector2( 0, 0 ), 0.5, 0.8 );
//
//			var effectHBlur = new THREE.ShaderPass( THREE.ShaderExtras[ "horizontalBlur" ] );
//			var effectVBlur = new THREE.ShaderPass( THREE.ShaderExtras[ "verticalBlur" ] );
//			effectHBlur.uniforms[ 'h' ].value = 2 / ( width/2 );
//			effectVBlur.uniforms[ 'v' ].value = 2 / ( height/2 );
//
//			var effectColorify1 = new THREE.ShaderPass( THREE.ShaderExtras[ "colorify" ] );
//			var effectColorify2 = new THREE.ShaderPass( THREE.ShaderExtras[ "colorify" ] );
//			effectColorify1.uniforms[ 'color' ].value.setRGB( 1, 0.8, 0.8 );
//			effectColorify2.uniforms[ 'color' ].value.setRGB( 1, 0.75, 0.5 );
//
//			var clearMask = new THREE.ClearMaskPass();
//			var renderMask = new THREE.MaskPass( sceneModel, cameraPerspective );
//			var renderMaskInverse = new THREE.MaskPass( sceneModel, cameraPerspective );
//
//			renderMaskInverse.inverse = true;
//
//			//effectFilm.renderToScreen = true;
//			//effectFilmBW.renderToScreen = true;
//			//effectDotScreen.renderToScreen = true;
//			//effectBleach.renderToScreen = true;
//			effectVignette.renderToScreen = true;
//			//effectScreen.renderToScreen = true;
//
//			//
//
//			rtParameters = { minFilter: THREE.LinearFilter, magFilter: THREE.LinearFilter, format: THREE.RGBFormat, stencilBuffer: true };
//
//			var rtWidth  = width / 2;
//			var rtHeight = height / 2;
//
//			//
//
//			var renderBackground = new THREE.RenderPass( sceneBG, cameraOrtho );
//			var renderModel = new THREE.RenderPass( sceneModel, cameraPerspective );
//
//			renderModel.clear = false;
//
//			composerScene = new THREE.EffectComposer( renderer, new THREE.WebGLRenderTarget( rtWidth * 2, rtHeight * 2, rtParameters ) );
//
//			composerScene.addPass( renderBackground );
//			composerScene.addPass( renderModel );
//			composerScene.addPass( renderMaskInverse );
//			composerScene.addPass( effectHBlur );
//			composerScene.addPass( effectVBlur );
//			composerScene.addPass( clearMask );
//
//			//
//
//			renderScene = new THREE.TexturePass( composerScene.renderTarget2 );
//
//			//
//
//			composer1 = new THREE.EffectComposer( renderer, new THREE.WebGLRenderTarget( rtWidth, rtHeight, rtParameters ) );
//
//			composer1.addPass( renderScene );
//			//composer1.addPass( renderMask );
//			composer1.addPass( effectFilmBW );
//			//composer1.addPass( clearMask );
//			composer1.addPass( effectVignette );
//
//			//
//
//			composer2 = new THREE.EffectComposer( renderer, new THREE.WebGLRenderTarget( rtWidth, rtHeight, rtParameters ) );
//
//			composer2.addPass( renderScene );
//			composer2.addPass( effectDotScreen );
//			composer2.addPass( renderMask );
//			composer2.addPass( effectColorify1 );
//			composer2.addPass( clearMask );
//			composer2.addPass( renderMaskInverse );
//			composer2.addPass( effectColorify2 );
//			composer2.addPass( clearMask );
//			composer2.addPass( effectVignette );
//
//			//
//
//			composer3 = new THREE.EffectComposer( renderer, new THREE.WebGLRenderTarget( rtWidth, rtHeight, rtParameters ) );
//
//			composer3.addPass( renderScene );
//			//composer3.addPass( renderMask );
//			composer3.addPass( effectSepia );
//			composer3.addPass( effectFilm );
//			//composer3.addPass( clearMask );
//			composer3.addPass( effectVignette );
//
//			//
//
//			composer4 = new THREE.EffectComposer( renderer, new THREE.WebGLRenderTarget( rtWidth, rtHeight, rtParameters ) );
//
//			composer4.addPass( renderScene );
//			//composer4.addPass( renderMask );
//			composer4.addPass( effectBloom );
//			composer4.addPass( effectFilm );
//			composer4.addPass( effectBleach );
//			//composer4.addPass( clearMask );
//			composer4.addPass( effectVignette );
//
//			//
//
//			//onWindowResize();
//
//			renderScene.uniforms[ "tDiffuse" ].value = composerScene.renderTarget2;
//
//			window.addEventListener( 'resize', onWindowResize, false );
		}
		
		private void createMesh( Geometry geometry, Scene scene, double scale ) 
		{

//			geometry.computeTangents();
//
//			var ambient = 0x444444, diffuse = 0x999999, specular = 0x080808, shininess = 20;
//
//			var shader = THREE.ShaderUtils.lib[ "normal" ];
//			var uniforms = THREE.UniformsUtils.clone( shader.uniforms );
//
//			uniforms[ "tNormal" ].value = THREE.ImageUtils.loadTexture( "obj/leeperrysmith/Infinite-Level_02_Tangent_SmoothUV.jpg" );
//			uniforms[ "uNormalScale" ].value.set( 0.75, 0.75 );
//
//			uniforms[ "tDiffuse" ].value = THREE.ImageUtils.loadTexture( "obj/leeperrysmith/Map-COL.jpg" );
//
//			uniforms[ "enableAO" ].value = false;
//			uniforms[ "enableDiffuse" ].value = true;
//
//			uniforms[ "uDiffuseColor" ].value.setHex( diffuse );
//			uniforms[ "uSpecularColor" ].value.setHex( specular );
//			uniforms[ "uAmbientColor" ].value.setHex( ambient );
//
//			uniforms[ "uShininess" ].value = shininess;
//
//			uniforms[ "uDiffuseColor" ].value.convertGammaToLinear();
//			uniforms[ "uSpecularColor" ].value.convertGammaToLinear();
//			uniforms[ "uAmbientColor" ].value.convertGammaToLinear();
//
//			var parameters = { fragmentShader: shader.fragmentShader, vertexShader: shader.vertexShader, uniforms: uniforms, lights: true };
//			var mat2 = new THREE.ShaderMaterial( parameters );
//
//			mesh = new THREE.Mesh( geometry, mat2 );
//			mesh.position.set( 0, -50, 0 );
//			mesh.scale.set( scale, scale, scale );
//
//			scene.add( mesh );
//
//			loader.statusDomElement.style.display = "none";

		}
		
		@Override
		protected void onUpdate(double duration)
		{
//			var time = Date.now() * 0.0004;
//
//			if ( mesh ) mesh.rotation.y = -time;
//
//			renderer.setViewport( 0, 0, 2 * halfWidth, 2 * halfHeight );
//
//			renderer.clear();
//			composerScene.render( delta );
//
//			renderer.setViewport( 0, 0, halfWidth, halfHeight );
//			composer1.render( delta );
//
//			renderer.setViewport( halfWidth, 0, halfWidth, halfHeight );
//			composer2.render( delta );
//
//			renderer.setViewport( 0, halfHeight, halfWidth, halfHeight );
//			composer3.render( delta );
//
//			renderer.setViewport( halfWidth, halfHeight, halfWidth, halfHeight );
//			composer4.render( delta );
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
