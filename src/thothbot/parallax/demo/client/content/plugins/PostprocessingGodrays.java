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

import thothbot.parallax.core.client.RenderingPanel;
import thothbot.parallax.core.client.events.AnimationReadyEvent;
import thothbot.parallax.core.client.gl2.enums.PixelFormat;
import thothbot.parallax.core.client.gl2.enums.TextureMagFilter;
import thothbot.parallax.core.client.gl2.enums.TextureMinFilter;
import thothbot.parallax.core.client.textures.RenderTargetTexture;
import thothbot.parallax.core.shared.cameras.OrthographicCamera;
import thothbot.parallax.core.shared.cameras.PerspectiveCamera;
import thothbot.parallax.core.shared.core.AbstractGeometry;
import thothbot.parallax.core.shared.geometries.SphereGeometry;
import thothbot.parallax.core.shared.materials.Material;
import thothbot.parallax.core.shared.materials.MeshBasicMaterial;
import thothbot.parallax.core.shared.materials.MeshDepthMaterial;
import thothbot.parallax.core.shared.materials.ShaderMaterial;
import thothbot.parallax.core.shared.math.Color;
import thothbot.parallax.core.shared.math.Vector2;
import thothbot.parallax.core.shared.math.Vector3;
import thothbot.parallax.core.shared.objects.Mesh;
import thothbot.parallax.core.shared.scenes.Scene;
import thothbot.parallax.demo.client.ContentWidget;
import thothbot.parallax.demo.client.Demo;
import thothbot.parallax.demo.client.DemoAnnotations.DemoSource;
import thothbot.parallax.demo.resources.GodRaysCombineShader;
import thothbot.parallax.demo.resources.GodRaysGenerateShader;
import thothbot.parallax.demo.resources.GodraysFakeSunShader;
import thothbot.parallax.loader.shared.JsonLoader;
import thothbot.parallax.loader.shared.XHRLoader;
import thothbot.parallax.plugins.postprocessing.Postprocessing;
import thothbot.parallax.plugins.postprocessing.RenderPass;
import thothbot.parallax.plugins.postprocessing.ShaderPass;

import com.google.gwt.core.client.GWT;
import com.google.gwt.core.client.RunAsyncCallback;
import com.google.gwt.event.dom.client.MouseMoveEvent;
import com.google.gwt.event.dom.client.MouseMoveHandler;
import com.google.gwt.resources.client.ImageResource;
import com.google.gwt.user.client.rpc.AsyncCallback;

public final class PostprocessingGodrays extends ContentWidget 
{
	/*
	 * Prepare Rendering Scene
	 */
	@DemoSource
	class DemoScene extends DemoAnimatedScene 
	{

		private static final String model = "./static/models/obj/tree/tree.js";
		private static final double orbitRadius = 200.0;
		
		private static final int bgColor = 0x000511;
		private static final int sunColor = 0xffee00;
		
		PerspectiveCamera camera;
		
		public int mouseX;
		public int mouseY;
		
		Mesh sphereMesh;
		
		Vector3 sunPosition = new Vector3( 0, 1000, -1000 );
		Vector3 screenSpacePosition = new Vector3();
		
		ShaderPass godraysGenerate, godraysCombine, godraysFakeSun;
		
		Scene postprocessingScene;
		OrthographicCamera postprocessingCamera;
		RenderTargetTexture rtTextureColors, rtTextureDepth;
		
		ShaderMaterial materialGodraysFakeSun;
		
		MeshDepthMaterial materialDepth;
		
		@Override
		protected void onStart()
		{
			camera = new PerspectiveCamera( 70,
					getRenderer().getAbsoluteAspectRation(), 
					1, 
					3000 
				);
			camera.getPosition().setZ(200);
			
			materialDepth = new MeshDepthMaterial();

			final MeshBasicMaterial materialScene = new MeshBasicMaterial();
			materialScene.setColor(new Color(0x000000));
			materialScene.setShading(Material.SHADING.FLAT);

			// tree

			new JsonLoader(model, new XHRLoader.ModelLoadHandler() {

				@Override
				public void onModelLoaded(XHRLoader loader, AbstractGeometry geometry) {																					
					Mesh treeMesh = new Mesh( geometry, materialScene );
					treeMesh.getPosition().set( 0, -150, -150 );

					treeMesh.getScale().set( 400 );

					treeMesh.setMatrixAutoUpdate(false);
					treeMesh.updateMatrix();

					getScene().add( treeMesh );
				}
			});

			// sphere

			SphereGeometry geo = new SphereGeometry( 1, 20, 10 );
			this.sphereMesh = new Mesh( geo, materialScene );
			this.sphereMesh.getScale().set( 20 );

			getScene().add( this.sphereMesh );

			//
					
			getRenderer().setSortObjects(false);
			getRenderer().setAutoClear(false);
			getRenderer().setClearColor( bgColor, 1 );
			
			//  Postprocessing
			
			postprocessingScene = new Scene();

			postprocessingCamera = new OrthographicCamera(getRenderer().getAbsoluteWidth(), getRenderer().getAbsoluteHeight(), -10000, 10000 );
			postprocessingCamera.getPosition().setZ( 100 );

			postprocessingScene.add( postprocessingCamera );

			rtTextureColors = new RenderTargetTexture( getRenderer().getAbsoluteWidth(), getRenderer().getAbsoluteHeight() );
			rtTextureColors.setMinFilter(TextureMinFilter.LINEAR);
			rtTextureColors.setMagFilter(TextureMagFilter.LINEAR);
			rtTextureColors.setFormat(PixelFormat.RGBA);

			// Switching the depth formats to luminance from rgb doesn't seem to work. I didn't
			// investigate further for now.
			// pars.format = THREE.LuminanceFormat;

			// I would have this quarter size and use it as one of the ping-pong render
			// targets but the aliasing causes some temporal flickering

			rtTextureDepth = new RenderTargetTexture(  getRenderer().getAbsoluteWidth(), getRenderer().getAbsoluteHeight()  );
			rtTextureDepth.setMinFilter(TextureMinFilter.LINEAR);
			rtTextureDepth.setMagFilter(TextureMagFilter.LINEAR);
			rtTextureDepth.setFormat(PixelFormat.RGBA);

			Postprocessing composer = new Postprocessing( getRenderer(), getScene() );
			RenderPass renderModel = new RenderPass( getScene(), camera );
			composer.addPass( renderModel );
			
			godraysGenerate = new ShaderPass( new GodRaysGenerateShader() );
			composer.addPass( godraysGenerate );
			
			godraysCombine = new ShaderPass( new GodRaysCombineShader() );
			godraysCombine.getUniforms().get("fGodRayIntensity").setValue( 0.75 );
			composer.addPass( godraysCombine );
			
			materialGodraysFakeSun = new ShaderMaterial ( new GodraysFakeSunShader() );
			godraysFakeSun = new ShaderPass( materialGodraysFakeSun.getShader() );
			((Color)godraysFakeSun.getUniforms().get("bgColor").getValue()).setHex( bgColor );
			((Color)godraysFakeSun.getUniforms().get("sunColor").getValue()).setHex( sunColor );
			composer.addPass( godraysFakeSun );
		}
				
		@Override
		protected void onUpdate(double duration)
		{
			double time = duration / 4000.0;

			this.sphereMesh.getPosition().setX( orbitRadius * Math.cos( time ) );
			this.sphereMesh.getPosition().setZ( orbitRadius * Math.sin( time ) - 100.0 );

			camera.getPosition().addX( ( mouseX - camera.getPosition().getX() ) * 0.036 );
			camera.getPosition().addY( ( - ( mouseY ) - camera.getPosition().getY() ) * 0.036 );

			camera.lookAt( getScene().getPosition() );
			
			// Find the screenspace position of the sun
			screenSpacePosition.copy( sunPosition ).project( camera );

			screenSpacePosition.setX( ( screenSpacePosition.getX() + 1.0 ) / 2.0 );
			screenSpacePosition.setY( ( screenSpacePosition.getY() + 1.0 ) / 2.0 );
			
			// Give it to the god-ray and sun shaders

			((Vector2)godraysGenerate.getUniforms().get("vSunPositionScreenSpace").getValue()).set( screenSpacePosition.getX(), screenSpacePosition.getY() );
			((Vector2)godraysFakeSun.getUniforms().get("vSunPositionScreenSpace").getValue()).set( screenSpacePosition.getX(), screenSpacePosition.getY() );

			// -- Draw sky and sun --

			// Clear colors and depths, will clear to sky color

			getRenderer().clearTarget( rtTextureColors, true, true, false );

			// Sun render. Runs a shader that gives a brightness based on the screen
			// space distance to the sun. Not very efficient, so i make a scissor
			// rectangle around the suns position to avoid rendering surrounding pixels.
			
			int width = getRenderer().getAbsoluteWidth(); 
			int height = getRenderer().getAbsoluteHeight();

			int sunsqH = (int) (0.74 * height); // 0.74 depends on extent of sun from shader
			int sunsqW = (int) (0.74 * height); // both depend on height because sun is aspect-corrected

			screenSpacePosition.setX( screenSpacePosition.getX() * width );
			screenSpacePosition.setY( screenSpacePosition.getY() * height );

			getRenderer().setScissor( (int)(screenSpacePosition.getX() - sunsqW / 2), (int)(screenSpacePosition.getY() - sunsqH / 2), sunsqW, sunsqH );
			getRenderer().enableScissorTest( true );

			godraysFakeSun.getUniforms().get("fAspect").setValue( (double)width / height );

			postprocessingScene.overrideMaterial = materialGodraysFakeSun;
			getRenderer().render( postprocessingScene, postprocessingCamera, rtTextureColors );

			getRenderer().enableScissorTest( false );
			
			// Colors

			getScene().overrideMaterial = null;
			getRenderer().render( getScene(), camera, rtTextureColors );

			// Depth

			getScene().overrideMaterial = materialDepth;
			getRenderer().render( getScene(), camera, rtTextureDepth, true );
			
			// -- Render god-rays --

			// Maximum length of god-rays (in texture space [0,1]X[0,1])

			double filterLen = 1.0;

			// Samples taken by filter

			double TAPS_PER_PASS = 6.0;

			// Pass order could equivalently be 3,2,1 (instead of 1,2,3), which
			// would start with a small filter support and grow to large. however
			// the large-to-small order produces less objectionable aliasing artifacts that
			// appear as a glimmer along the length of the beams

			// pass 1 - render into first ping-pong target

			double pass = 1.0;
			double stepLen = filterLen * Math.pow( TAPS_PER_PASS, -pass );

			godraysGenerate.getUniforms().get("fStepSize" ).setValue( stepLen );
			godraysGenerate.getUniforms().get("tInput" ).setValue( rtTextureDepth );

			getRenderer().render( getScene(), camera);
		}
	}
		
	public PostprocessingGodrays() 
	{
		super("God-rays", "Drag mouse to move. This example based on the three.js example.");
	}
	
	@Override
	protected void loadRenderingPanelAttributes(RenderingPanel renderingPanel) 
	{
		super.loadRenderingPanelAttributes(renderingPanel);
		renderingPanel.getCanvas3dAttributes().setAntialiasEnable(false);
		renderingPanel.setBackground(0x000511);
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
		    	  	rs.mouseX = (event.getX() - renderingPanel.getRenderer().getAbsoluteWidth() / 2 ); 
		    	  	rs.mouseY = (event.getY() - renderingPanel.getRenderer().getAbsoluteHeight() / 2);
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
		return Demo.resources.examplePostprocessingGodrays();
	}
	
	@Override
	protected void asyncOnInitialize(final AsyncCallback<DemoAnimatedScene> callback)
	{
		GWT.runAsync(PostprocessingGodrays.class, new RunAsyncCallback() 
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
