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

import java.util.HashMap;
import java.util.Map;

import thothbot.parallax.core.client.context.Canvas3d;
import thothbot.parallax.core.client.controls.TrackballControls;
import thothbot.parallax.core.client.gl2.enums.PixelFormat;
import thothbot.parallax.core.client.gl2.enums.TextureMagFilter;
import thothbot.parallax.core.client.gl2.enums.TextureMinFilter;
import thothbot.parallax.core.client.gl2.enums.TextureWrapMode;
import thothbot.parallax.core.client.shaders.NormalMapShader;
import thothbot.parallax.core.client.shaders.Shader;
import thothbot.parallax.core.client.shaders.Uniform;
import thothbot.parallax.core.client.textures.RenderTargetTexture;
import thothbot.parallax.core.client.textures.Texture;
import thothbot.parallax.core.shared.Log;
import thothbot.parallax.core.shared.cameras.OrthographicCamera;
import thothbot.parallax.core.shared.cameras.PerspectiveCamera;
import thothbot.parallax.core.shared.core.Color;
import thothbot.parallax.core.shared.core.Geometry;
import thothbot.parallax.core.shared.core.Mathematics;
import thothbot.parallax.core.shared.core.Vector2;
import thothbot.parallax.core.shared.geometries.PlaneGeometry;
import thothbot.parallax.core.shared.lights.AmbientLight;
import thothbot.parallax.core.shared.lights.DirectionalLight;
import thothbot.parallax.core.shared.lights.PointLight;
import thothbot.parallax.core.shared.materials.Material;
import thothbot.parallax.core.shared.materials.MeshBasicMaterial;
import thothbot.parallax.core.shared.materials.MeshLambertMaterial;
import thothbot.parallax.core.shared.materials.ShaderMaterial;
import thothbot.parallax.core.shared.objects.Mesh;
import thothbot.parallax.core.shared.scenes.FogSimple;
import thothbot.parallax.core.shared.scenes.Scene;
import thothbot.parallax.core.shared.utils.ImageUtils;
import thothbot.parallax.core.shared.utils.UniformsUtils;
import thothbot.parallax.demo.client.ContentWidget;
import thothbot.parallax.demo.client.Demo;
import thothbot.parallax.demo.client.DemoAnnotations.DemoSource;
import thothbot.parallax.demo.client.content.CustomAttributesParticles2.Resources;
import thothbot.parallax.demo.resources.TerrainShader;
import thothbot.parallax.loader.shared.JsonLoader;
import thothbot.parallax.loader.shared.MorphAnimation;
import thothbot.parallax.plugin.postprocessing.client.Postprocessing;
import thothbot.parallax.plugin.postprocessing.client.shaders.LuminosityShader;

import com.google.gwt.core.client.GWT;
import com.google.gwt.core.client.RunAsyncCallback;
import com.google.gwt.http.client.RequestException;
import com.google.gwt.resources.client.ClientBundle;
import com.google.gwt.resources.client.ImageResource;
import com.google.gwt.resources.client.TextResource;
import com.google.gwt.resources.client.ClientBundle.Source;
import com.google.gwt.user.client.rpc.AsyncCallback;

public final class TerrainDynamic extends ContentWidget 
{
	
	/*
	 * Load shaders
	 */
	@DemoSource
	public interface Resources extends Shader.DefaultResources
	{
		Resources INSTANCE = GWT.create(Resources.class);

		@Source("../../../resources/shaders/terrain_dynamic_noise.fs")
		TextResource fragmetShader();
		
		@Source("../../../resources/shaders/terrain_dynamic_noise.vs")
		TextResource vertexShader();
	}

	/*
	 * Prepare Rendering Scene
	 */
	@DemoSource
	class DemoScene extends DemoAnimatedScene implements ImageUtils.ImageLoadHandler
	{
		private static final String diffuseImage1 = "./static/textures/terrain/grasslight-big.jpg";
		private static final String diffuseImage2 = "./static/textures/terrain/backgrounddetailed6.jpg";
		private static final String detailImage = "./static/textures/terrain/grasslight-big-nm.jpg";
		
		private static final String parrotModel = "./static/models/animated/parrot.js";
		private static final String flamingoModel = "./static/models/animated/flamingo.js";
		private static final String storkModel = "./static/models/animated/stork.js";
		
		OrthographicCamera cameraOrtho;
		Scene sceneRenderTarget;
		
		Map<String, ShaderMaterial> mlib;
		
		TrackballControls controls;
		Postprocessing composer;
		
		Mesh terrain;
		Mesh quadTarget;
		
		int textureCounter;
		
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

			int rx = 256, ry = 256;

			RenderTargetTexture heightMap  = new RenderTargetTexture( rx, ry );
			heightMap.setMinFilter(TextureMinFilter.LINEAR_MIPMAP_LINEAR);
			heightMap.setMagFilter(TextureMagFilter.LINEAR);
			heightMap.setFormat(PixelFormat.RGB);
			
			RenderTargetTexture normalMap = new RenderTargetTexture( rx, ry );
			normalMap.setMinFilter(TextureMinFilter.LINEAR_MIPMAP_LINEAR);
			normalMap.setMagFilter(TextureMagFilter.LINEAR);
			normalMap.setFormat(PixelFormat.RGB);

			NormalMapShader normalShader = new NormalMapShader();

			Map<String, Uniform> uniformsNormal = UniformsUtils.clone( normalShader.getUniforms() );

			uniformsNormal.get("height").setValue( 0.05 );
			((Vector2)uniformsNormal.get("resolution").getValue()).set( rx, ry );
			uniformsNormal.get("heightMap").setTexture( heightMap );

			// TEXTURES

			RenderTargetTexture specularMap = new RenderTargetTexture( 2048, 2048 );
			specularMap.setMinFilter(TextureMinFilter.LINEAR_MIPMAP_LINEAR);
			specularMap.setMagFilter(TextureMagFilter.LINEAR);
			specularMap.setFormat(PixelFormat.RGB);
			specularMap.setWrapS(TextureWrapMode.REPEAT);
			specularMap.setWrapT(TextureWrapMode.REPEAT);

			Texture diffuseTexture1 = ImageUtils.loadTexture( diffuseImage1, null, new ImageUtils.ImageLoadHandler() {
				
				@Override
				public void onImageLoad(Texture texture) {
					DemoScene.this.onImageLoad(texture);
					DemoScene.this.applyShader( new LuminosityShader(), diffuseTexture1, specularMap );
				}
			});

			diffuseTexture1.setWrapS(TextureWrapMode.REPEAT);
			diffuseTexture1.setWrapT(TextureWrapMode.REPEAT);

			Texture diffuseTexture2 = ImageUtils.loadTexture( diffuseImage2, null, this);

			diffuseTexture2.setWrapS(TextureWrapMode.REPEAT);
			diffuseTexture2.setWrapT(TextureWrapMode.REPEAT);

			Texture detailTexture = ImageUtils.loadTexture( detailImage, null, this );
			detailTexture.setWrapS(TextureWrapMode.REPEAT);
			detailTexture.setWrapT(TextureWrapMode.REPEAT);

			// TERRAIN SHADER

			TerrainShader terrainShader = new TerrainShader();

			Map<String, Uniform> uniformsTerrain = UniformsUtils.clone( terrainShader.getUniforms() );

			uniformsTerrain.get( "tNormal" ).setTexture( normalMap );
			uniformsTerrain.get( "uNormalScale" ).setValue( 3.5 );

			uniformsTerrain.get( "tDisplacement" ).setTexture( heightMap );

			uniformsTerrain.get( "tDiffuse1" ).setTexture( diffuseTexture1 );
			uniformsTerrain.get( "tDiffuse2" ).setTexture( diffuseTexture2 );
			uniformsTerrain.get( "tSpecular" ).setTexture( specularMap );
			uniformsTerrain.get( "tDetail" ).setTexture( detailTexture );

			uniformsTerrain.get( "enableDiffuse1" ).setValue( 1 );
			uniformsTerrain.get( "enableDiffuse2" ).setValue( 1 );
			uniformsTerrain.get( "enableSpecular" ).setValue( 1 );

			((Color)uniformsTerrain.get( "uDiffuseColor" ).getValue()).setHex( 0xffffff );
			((Color)uniformsTerrain.get( "uSpecularColor").getValue()).setHex( 0xffffff );
			((Color)uniformsTerrain.get( "uAmbientColor" ).getValue()).setHex( 0x111111 );

			uniformsTerrain.get( "uShininess" ).setValue( 30.0 );

			uniformsTerrain.get( "uDisplacementScale" ).setValue( 375 );

			((Vector2)uniformsTerrain.get( "uRepeatOverlay" ).getValue()).set( 6, 6 );

//			uniformsNoise = {
//
//				time:   { type: "f", value: 1.0 },
//				scale:  { type: "v2", value: new Vector2( 1.5, 1.5 ) },
//				offset: { type: "v2", value: new Vector2( 0, 0 ) }
//
//			};
			
			mlib = new HashMap<String, ShaderMaterial>();
			
			ShaderMaterial material1 = new ShaderMaterial(Resources.INSTANCE);
			material1.getShader().setUniforms(uniformsNoise);
			material1.setLights(false);
			material1.setFog(true);
			mlib.put("heightmap", material1);
			
			ShaderMaterial material2 = new ShaderMaterial(normalShader);
			material2.getShader().setUniforms(uniformsNormal);
			material2.setLights(false);
			material2.setFog(true);
			mlib.put("normal", material2);
			
			ShaderMaterial material3 = new ShaderMaterial(terrainShader);
			material3.getShader().setUniforms(uniformsTerrain);
			material3.setLights(true);
			material3.setFog(true);
			mlib.put("terrain", material3);

			PlaneGeometry plane = new PlaneGeometry( SCREEN_WIDTH, SCREEN_HEIGHT );
			MeshBasicMaterial planeMaterial = new MeshBasicMaterial();
			planeMaterial.setColor(new Color(0x000000));
			quadTarget = new Mesh( plane, planeMaterial );
			quadTarget.getPosition().setZ( -500 );
			sceneRenderTarget.add( quadTarget );

			// TERRAIN MESH

			PlaneGeometry geometryTerrain = new PlaneGeometry( 6000, 6000, 256, 256 );

			geometryTerrain.computeFaceNormals();
			geometryTerrain.computeVertexNormals();
			geometryTerrain.computeTangents();

			terrain = new Mesh( geometryTerrain, mlib[ "terrain" ] );
			terrain.getPosition().set( 0, -125, 0 );
			terrain.getRotation().setX( -Math.PI / 2 );
			terrain.setVisible(false);
			getScene().add( terrain );

			// RENDERER

			getRenderer().setClearColor(getScene().getFog().getColor());
			getRenderer().setGammaInput(true);
			getRenderer().setGammaOutput(true);

			// EVENTS

			onWindowResize();

			document.addEventListener( 'keydown', onKeyDown, false );

			// COMPOSER

			getRenderer().setAutoClear(false);

			renderTargetParameters = { minFilter: THREE.LinearFilter, magFilter: THREE.LinearFilter, format: THREE.RGBFormat, stencilBuffer: false };
			renderTarget = new THREE.WebGLRenderTarget( SCREEN_WIDTH, SCREEN_HEIGHT, renderTargetParameters );

			effectBloom = new BloomPass( 0.6 );
			var effectBleach = new ShaderPass( THREE.ShaderExtras[ "bleachbypass" ] );

			hblur = new ShaderPass( THREE.ShaderExtras[ "horizontalTiltShift" ] );
			vblur = new ShaderPass( THREE.ShaderExtras[ "verticalTiltShift" ] );

			int bluriness = 6;

			hblur.uniforms[ 'h' ].value = bluriness / SCREEN_WIDTH;
			vblur.uniforms[ 'v' ].value = bluriness / SCREEN_HEIGHT;
			hblur.uniforms[ 'r' ].value = vblur.uniforms[ 'r' ].value = 0.5;

			effectBleach.uniforms[ 'opacity' ].value = 0.65;

			composer = new Postprocessing( getRenderer(), renderTarget );

			var renderModel = new RenderPass( getScene(), getCamera() );

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
			meshAnim.setDuration(duration);
			meshAnim.time = 600 * Math.random();

			meshAnim.position.set( x, y, z );
			meshAnim.rotation.y = Math.PI/2;

			meshAnim.castShadow = true;
			meshAnim.receiveShadow = false;

			getScene().add( meshAnim );

			morphs.add( meshAnim );

			getRenderer().initWebGLObjects( getScene() );

			final JsonLoader jsonLoader = new JsonLoader();

			final double startX = -3000;
			try
			{
				jsonLoader.load(parrotModel, new JsonLoader.ModelLoadHandler() {
					
					@Override
					public void onModeLoad() {
						Geometry geometry = jsonLoader.getGeometry();

						morphColorsToFaceColors( geometry );
						addMorph( geometry, 250, 500, startX -500, 500, 700 );
						addMorph( geometry, 250, 500, startX - Math.random() * 500, 500, -200 );
						addMorph( geometry, 250, 500, startX - Math.random() * 500, 500, 200 );
						addMorph( geometry, 250, 500, startX - Math.random() * 500, 500, 1000 );
						
					}
				});
				
				jsonLoader.load(flamingoModel, new JsonLoader.ModelLoadHandler() {
					
					@Override
					public void onModeLoad() {
						Geometry geometry = jsonLoader.getGeometry();

						morphColorsToFaceColors( geometry );
						addMorph( geometry, 500, 1000, startX - Math.random() * 500, 350, 40 );
					}
				});
				
				jsonLoader.load(storkModel, new JsonLoader.ModelLoadHandler() {

					@Override
					public void onModeLoad() {
						Geometry geometry = jsonLoader.getGeometry();

						morphColorsToFaceColors( geometry );
						addMorph( geometry, 350, 1000, startX - Math.random() * 500, 350, 340 );
					}
				});

			}
			catch (RequestException exception) 
			{
				Log.error("Error while loading JSON file.");
			}
			
			// PRE-INIT

			getRenderer().initWebGLObjects( getScene() );
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
		
		@Override
		public void onImageLoad(Texture texture)
		{
			textureCounter += 1;

			if ( textureCounter == 3 )	
			{
				terrain.setVisible(true);

//				document.getElementById( "loading" ).style.display = "none";
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

			//			soundVal = Mathematics.clamp( soundVal + delta * soundDir, 0, 1 );
			//
			//			if ( soundVal !== oldSoundVal ) 
			//			{
			//				if ( soundtrack ) 
			//				{
			//					soundtrack.volume = soundVal;
			//					oldSoundVal = soundVal;
			//				}
			//			}

			if ( terrain.isVisible() ) 
			{
				controls.update();

				double time = duration * 0.001;

				double fLow = 0.4, fHigh = 0.825;

				lightVal = Mathematics.clamp( lightVal + 0.5 * delta * lightDir, fLow, fHigh );

				var valNorm = ( lightVal - fLow ) / ( fHigh - fLow );

				var sat = Mathematics.mapLinear( valNorm, 0, 1, 0.95, 0.25 );
				scene.fog.color.setHSV( 0.1, sat, lightVal );

				getRenderer().setClearColor( getScene().getFog().getColor(), 1 );

				directionalLight.intensity = Mathematics.mapLinear( valNorm, 0, 1, 0.1, 1.15 );
				pointLight.intensity = Mathematics.mapLinear( valNorm, 0, 1, 0.9, 1.5 );

				uniformsTerrain[ "uNormalScale" ].value = Mathematics.mapLinear( valNorm, 0, 1, 0.6, 3.5 );

				if ( updateNoise ) 
				{
					animDelta = Mathematics.clamp( animDelta + 0.00075 * animDeltaDir, 0, 0.05 );
					uniformsNoise[ "time" ].value += delta * animDelta;

					uniformsNoise[ "offset" ].value.x += delta * 0.05;

					uniformsTerrain[ "uOffset" ].value.x = 4 * uniformsNoise[ "offset" ].value.x;

					quadTarget.material = mlib[ "heightmap" ];
					getRenderer().render( sceneRenderTarget, cameraOrtho, heightMap, true );

					quadTarget.material = mlib[ "normal" ];
					getRenderer().render( sceneRenderTarget, cameraOrtho, normalMap, true );

					//updateNoise = false;
				}

				for ( int i = 0; i < morphs.length; i ++ ) 
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
