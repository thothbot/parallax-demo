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

import thothbot.parallax.core.client.context.Canvas3d;
import thothbot.parallax.core.client.controls.TrackballControls;
import thothbot.parallax.core.client.shaders.NormalMapShader;
import thothbot.parallax.core.client.shaders.Shader;
import thothbot.parallax.core.client.textures.RenderTargetTexture;
import thothbot.parallax.core.client.textures.Texture;
import thothbot.parallax.core.shared.cameras.OrthographicCamera;
import thothbot.parallax.core.shared.cameras.PerspectiveCamera;
import thothbot.parallax.core.shared.core.Color;
import thothbot.parallax.core.shared.core.Geometry;
import thothbot.parallax.core.shared.geometries.PlaneGeometry;
import thothbot.parallax.core.shared.lights.AmbientLight;
import thothbot.parallax.core.shared.lights.DirectionalLight;
import thothbot.parallax.core.shared.lights.PointLight;
import thothbot.parallax.core.shared.materials.Material;
import thothbot.parallax.core.shared.materials.MeshLambertMaterial;
import thothbot.parallax.core.shared.materials.ShaderMaterial;
import thothbot.parallax.core.shared.objects.Mesh;
import thothbot.parallax.core.shared.scenes.FogSimple;
import thothbot.parallax.core.shared.scenes.Scene;
import thothbot.parallax.demo.client.ContentWidget;
import thothbot.parallax.demo.client.Demo;
import thothbot.parallax.demo.client.DemoAnnotations.DemoSource;
import thothbot.parallax.loader.shared.JsonLoader;
import thothbot.parallax.loader.shared.MorphAnimation;

import com.google.gwt.core.client.GWT;
import com.google.gwt.core.client.RunAsyncCallback;
import com.google.gwt.resources.client.ImageResource;
import com.google.gwt.user.client.rpc.AsyncCallback;

public final class TerrainDynamic extends ContentWidget 
{
	/*
	 * Prepare Rendering Scene
	 */
	@DemoSource
	class DemoScene extends DemoAnimatedScene 
	{

		OrthographicCamera cameraOrtho;
		Scene sceneRenderTarget;
		
		TrackballControls controls;
		
		@Override
		protected void loadCamera()
		{
			setCamera(
					new PerspectiveCamera(
							40, // fov
							getRenderer().getCanvas().getAspectRation(), // aspect 
							2, // near
							4000 // far 
					)); 
			
			Canvas3d canvas = getRenderer().getCanvas();
			
			cameraOrtho = new OrthographicCamera( 
					canvas.getWidth() / - 2.0, 
					canvas.getWidth() / 2.0, 
					canvas.getHeight() / 2.0, 
					canvas.getHeight() / - 2.0, 
					-10000, 10000);
		}

		@Override
		protected void onStart()
		{
			getCamera().getPosition().set( -1200, 800, 1200 );
			cameraOrtho.getPosition().setZ( 100 );
//			getScene().add(getCamera());

//			container = document.getElementById( 'container' );
//			soundtrack = document.getElementById( "soundtrack" );

			// SCENE (RENDER TARGET)

			sceneRenderTarget = new Scene();
			sceneRenderTarget.add( cameraOrtho );

			// CAMERA

			controls = new TrackballControls( getCamera(), getRenderer().getCanvas() );
			controls.getTarget().set( 0 );

			controls.setRotateSpeed(1.0);
			controls.setZoomSpeed(1.2);
			controls.setPanSpeed(0.8);

			controls.setZoom(true);
			controls.setPan(true);

			controls.setStaticMoving(true);
			controls.setDynamicDampingFactor(0.15);

			// SCENE (FINAL)

			getScene().setFog( new FogSimple( 0x050505, 2000, 4000 ) );
			getScene().getFog().getColor().setHSV( 0.102, 0.9, 0.825 );

			// LIGHTS

			getScene().add( new AmbientLight( 0x111111 ) );

			DirectionalLight directionalLight = new DirectionalLight( 0xffffff, 1.15 );
			directionalLight.getPosition().set( 500, 2000, 0 );
			getScene().add( directionalLight );

			PointLight pointLight = new PointLight( 0xff4400, 1.5, 0 );
			pointLight.getPosition().set( 0 );
			getScene().add( pointLight );

			// HEIGHT + NORMAL MAPS

			NormalMapShader normalShader = new NormalMapShader();

			var rx = 256, ry = 256;
			var pars = { minFilter: THREE.LinearMipmapLinearFilter, magFilter: THREE.LinearFilter, format: THREE.RGBFormat };

			heightMap  = new THREE.WebGLRenderTarget( rx, ry, pars );
			normalMap = new THREE.WebGLRenderTarget( rx, ry, pars );

			uniformsNoise = {

				time:   { type: "f", value: 1.0 },
				scale:  { type: "v2", value: new THREE.Vector2( 1.5, 1.5 ) },
				offset: { type: "v2", value: new THREE.Vector2( 0, 0 ) }

			};

			uniformsNormal = THREE.UniformsUtils.clone( normalShader.uniforms );

			uniformsNormal.height.value = 0.05;
			uniformsNormal.resolution.value.set( rx, ry );
			uniformsNormal.heightMap.texture = heightMap;

			var vertexShader = document.getElementById( 'vertexShader' ).textContent;

			// TEXTURES

			RenderTargetTexture specularMap = new RenderTargetTexture( 2048, 2048, pars );

			Texture diffuseTexture1 = ImageUtils.loadTexture( "textures/terrain/grasslight-big.jpg", null, function () {

				loadTextures();
				applyShader( THREE.ShaderExtras[ 'luminosity' ], diffuseTexture1, specularMap );

			} );

			var diffuseTexture2 = THREE.ImageUtils.loadTexture( "textures/terrain/backgrounddetailed6.jpg", null, loadTextures );
			var detailTexture = THREE.ImageUtils.loadTexture( "textures/terrain/grasslight-big-nm.jpg", null, loadTextures );

			diffuseTexture1.wrapS = diffuseTexture1.wrapT = THREE.RepeatWrapping;
			diffuseTexture2.wrapS = diffuseTexture2.wrapT = THREE.RepeatWrapping;
			detailTexture.wrapS = detailTexture.wrapT = THREE.RepeatWrapping;
			specularMap.wrapS = specularMap.wrapT = THREE.RepeatWrapping;

			// TERRAIN SHADER

			var terrainShader = THREE.ShaderTerrain[ "terrain" ];

			uniformsTerrain = THREE.UniformsUtils.clone( terrainShader.uniforms );

			uniformsTerrain[ "tNormal" ].texture = normalMap;
			uniformsTerrain[ "uNormalScale" ].value = 3.5;

			uniformsTerrain[ "tDisplacement" ].texture = heightMap;

			uniformsTerrain[ "tDiffuse1" ].texture = diffuseTexture1;
			uniformsTerrain[ "tDiffuse2" ].texture = diffuseTexture2;
			uniformsTerrain[ "tSpecular" ].texture = specularMap;
			uniformsTerrain[ "tDetail" ].texture = detailTexture;

			uniformsTerrain[ "enableDiffuse1" ].value = true;
			uniformsTerrain[ "enableDiffuse2" ].value = true;
			uniformsTerrain[ "enableSpecular" ].value = true;

			uniformsTerrain[ "uDiffuseColor" ].value.setHex( 0xffffff );
			uniformsTerrain[ "uSpecularColor" ].value.setHex( 0xffffff );
			uniformsTerrain[ "uAmbientColor" ].value.setHex( 0x111111 );

			uniformsTerrain[ "uShininess" ].value = 30;

			uniformsTerrain[ "uDisplacementScale" ].value = 375;

			uniformsTerrain[ "uRepeatOverlay" ].value.set( 6, 6 );

			var params = [
							[ 'heightmap', 	document.getElementById( 'fragmentShaderNoise' ).textContent, 	vertexShader, uniformsNoise, false ],
							[ 'normal', 	normalShader.fragmentShader,  normalShader.vertexShader, uniformsNormal, false ],
							[ 'terrain', 	terrainShader.fragmentShader, terrainShader.vertexShader, uniformsTerrain, true ]
						 ];

			for( int i = 0; i < params.length; i ++ ) 
			{
				material = new ShaderMaterial( {

					uniforms: 		params[ i ][ 3 ],
					vertexShader: 	params[ i ][ 2 ],
					fragmentShader: params[ i ][ 1 ],
					lights: 		params[ i ][ 4 ],
					fog: 			true
					} );

				mlib[ params[ i ][ 0 ] ] = material;
			}


			var plane = new THREE.PlaneGeometry( SCREEN_WIDTH, SCREEN_HEIGHT );

			quadTarget = new THREE.Mesh( plane, new THREE.MeshBasicMaterial( { color: 0x000000 } ) );
			quadTarget.position.z = -500;
			sceneRenderTarget.add( quadTarget );

			// TERRAIN MESH

			var geometryTerrain = new THREE.PlaneGeometry( 6000, 6000, 256, 256 );

			geometryTerrain.computeFaceNormals();
			geometryTerrain.computeVertexNormals();
			geometryTerrain.computeTangents();

			terrain = new THREE.Mesh( geometryTerrain, mlib[ "terrain" ] );
			terrain.position.set( 0, -125, 0 );
			terrain.rotation.x = -Math.PI / 2;
			terrain.visible = false;
			scene.add( terrain );

			// RENDERER

			getRenderer().setClearColor(getScene().getFog().getColor());
			getRenderer().setGammaInput(true);
			getRenderer().setGammaOutput(true);

			// EVENTS

			onWindowResize();

			window.addEventListener( 'resize', onWindowResize, false );

			document.addEventListener( 'keydown', onKeyDown, false );

			// COMPOSER

			renderer.autoClear = false;

			renderTargetParameters = { minFilter: THREE.LinearFilter, magFilter: THREE.LinearFilter, format: THREE.RGBFormat, stencilBuffer: false };
			renderTarget = new THREE.WebGLRenderTarget( SCREEN_WIDTH, SCREEN_HEIGHT, renderTargetParameters );

			effectBloom = new THREE.BloomPass( 0.6 );
			var effectBleach = new THREE.ShaderPass( THREE.ShaderExtras[ "bleachbypass" ] );

			hblur = new THREE.ShaderPass( THREE.ShaderExtras[ "horizontalTiltShift" ] );
			vblur = new THREE.ShaderPass( THREE.ShaderExtras[ "verticalTiltShift" ] );

			var bluriness = 6;

			hblur.uniforms[ 'h' ].value = bluriness / SCREEN_WIDTH;
			vblur.uniforms[ 'v' ].value = bluriness / SCREEN_HEIGHT;

			hblur.uniforms[ 'r' ].value = vblur.uniforms[ 'r' ].value = 0.5;

			effectBleach.uniforms[ 'opacity' ].value = 0.65;

			composer = new THREE.EffectComposer( renderer, renderTarget );

			var renderModel = new THREE.RenderPass( scene, camera );

			vblur.renderToScreen = true;

			composer = new THREE.EffectComposer( renderer, renderTarget );

			composer.addPass( renderModel );

			composer.addPass( effectBloom );
			//composer.addPass( effectBleach );

			composer.addPass( hblur );
			composer.addPass( vblur );

		}
		
		private void addMorph( Geometry geometry, double speed, double duration, double x, double y, double z ) 
		{
			MeshLambertMaterial material = new MeshLambertMaterial();
			material.setColor(new Color(0xffaa55));
			material.setMorphTargets(true);
			material.setVertexColors(Material.COLORS.FACE);

			MorphAnimation meshAnim = new MorphAnimation( geometry, material );

			meshAnim.speed = speed;
			meshAnim.duration = duration;
			meshAnim.time = 600 * Math.random();

			meshAnim.position.set( x, y, z );
			meshAnim.rotation.y = Math.PI/2;

			meshAnim.castShadow = true;
			meshAnim.receiveShadow = false;

			getScene().add( meshAnim );

			morphs.push( meshAnim );

			renderer.initWebGLObjects( scene );

			JsonLoader loader = new JsonLoader();

			var startX = -3000;

			loader.load( "models/animated/parrot.js", function( geometry ) {

				morphColorsToFaceColors( geometry );
				addMorph( geometry, 250, 500, startX -500, 500, 700 );
				addMorph( geometry, 250, 500, startX - Math.random() * 500, 500, -200 );
				addMorph( geometry, 250, 500, startX - Math.random() * 500, 500, 200 );
				addMorph( geometry, 250, 500, startX - Math.random() * 500, 500, 1000 );

			} );

			loader.load( "models/animated/flamingo.js", function( geometry ) {

				morphColorsToFaceColors( geometry );
				addMorph( geometry, 500, 1000, startX - Math.random() * 500, 350, 40 );

			} );

			loader.load( "models/animated/stork.js", function( geometry ) {

				morphColorsToFaceColors( geometry );
				addMorph( geometry, 350, 1000, startX - Math.random() * 500, 350, 340 );

			} );

			// PRE-INIT

			renderer.initWebGLObjects( scene );
		}

		private void morphColorsToFaceColors( Geometry geometry ) 
		{
			if ( geometry.getMorphColors() != null && geometry.getMorphColors().size() > 0 ) 
			{
				Geometry.MorphColor colorMap = geometry.getMorphColors().get(0);

				for ( int i = 0; i < colorMap.colors.size(); i ++ ) 
				{
					geometry.getFaces().get(i).setColor( colorMap.colors.get(i) );
				}
			}
		}
		
		private void applyShader( Shader shader, Texture texture, RenderTargetTexture target ) 
		{
			ShaderMaterial shaderMaterial = new ShaderMaterial(shader);

			shaderMaterial.getShader().getUniforms().get("tDiffuse").setTexture(texture);

			Scene sceneTmp = new Scene();

			Mesh meshTmp = new Mesh( new PlaneGeometry( SCREEN_WIDTH, SCREEN_HEIGHT ), shaderMaterial );
			meshTmp.getPosition().setZ( -500 );

			sceneTmp.add( meshTmp );

			getRenderer().render( sceneTmp, cameraOrtho, target, true );
		}
		
		private void loadTextures() 
		{
			textureCounter += 1;

			if ( textureCounter == 3 )	
			{
				terrain.visible = true;

				document.getElementById( "loading" ).style.display = "none";
			}
		}
		
		@Override
		protected void onStop()
		{			
		}
		
		@Override
		protected void onUpdate(double duration)
		{
			var delta = clock.getDelta();

			soundVal = THREE.Math.clamp( soundVal + delta * soundDir, 0, 1 );

			if ( soundVal !== oldSoundVal ) 
			{
				if ( soundtrack ) 
				{
					soundtrack.volume = soundVal;
					oldSoundVal = soundVal;
				}
			}

			if ( terrain.visible ) 
			{
				controls.update();

				var time = Date.now() * 0.001;

				var fLow = 0.4, fHigh = 0.825;

				lightVal = THREE.Math.clamp( lightVal + 0.5 * delta * lightDir, fLow, fHigh );

				var valNorm = ( lightVal - fLow ) / ( fHigh - fLow );

				var sat = THREE.Math.mapLinear( valNorm, 0, 1, 0.95, 0.25 );
				scene.fog.color.setHSV( 0.1, sat, lightVal );

				renderer.setClearColor( scene.fog.color, 1 );

				directionalLight.intensity = THREE.Math.mapLinear( valNorm, 0, 1, 0.1, 1.15 );
				pointLight.intensity = THREE.Math.mapLinear( valNorm, 0, 1, 0.9, 1.5 );

				uniformsTerrain[ "uNormalScale" ].value = THREE.Math.mapLinear( valNorm, 0, 1, 0.6, 3.5 );

				if ( updateNoise ) 
				{
					animDelta = THREE.Math.clamp( animDelta + 0.00075 * animDeltaDir, 0, 0.05 );
					uniformsNoise[ "time" ].value += delta * animDelta;

					uniformsNoise[ "offset" ].value.x += delta * 0.05;

					uniformsTerrain[ "uOffset" ].value.x = 4 * uniformsNoise[ "offset" ].value.x;

					quadTarget.material = mlib[ "heightmap" ];
					renderer.render( sceneRenderTarget, cameraOrtho, heightMap, true );

					quadTarget.material = mlib[ "normal" ];
					renderer.render( sceneRenderTarget, cameraOrtho, normalMap, true );

					//updateNoise = false;
				}

				for ( var i = 0; i < morphs.length; i ++ ) 
				{
					morph = morphs[ i ];

					morph.updateAnimation( 1000 * delta );

					morph.position.x += morph.speed * delta;

					if ( morph.position.x  > 2000 )  
					{
						morph.position.x = -1500 - Math.random() * 500;
					}
				}

				//renderer.render( scene, camera );
				composer.render( 0.1 );
		}
	}
		
	public TerrainDynamic() 
	{
		super("Dynamic procedural terrain", "Used 3d simplex noise. Options - day / night: [n]; animate terrain: [m]; toggle soundtrack: [b]. This example based on the three.js example.");
	}
	
	@Override
	public DemoScene onInitialize()
	{
		return new DemoScene();
	}

	@Override
	public ImageResource getIcon()
	{
		return Demo.resources.exampleCube();
	}
	
	@Override
	protected void asyncOnInitialize(final AsyncCallback<DemoAnimatedScene> callback)
	{
		GWT.runAsync(TerrainDynamic.class, new RunAsyncCallback() 
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
