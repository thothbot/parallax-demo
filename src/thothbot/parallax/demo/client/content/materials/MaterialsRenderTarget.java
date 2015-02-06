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

package thothbot.parallax.demo.client.content.materials;

import thothbot.parallax.core.client.events.AnimationReadyEvent;
import thothbot.parallax.core.client.gl2.enums.PixelFormat;
import thothbot.parallax.core.client.gl2.enums.TextureMagFilter;
import thothbot.parallax.core.client.gl2.enums.TextureMinFilter;
import thothbot.parallax.core.client.shaders.Shader.DefaultResources;
import thothbot.parallax.core.client.shaders.Uniform;
import thothbot.parallax.core.client.textures.RenderTargetTexture;
import thothbot.parallax.core.shared.cameras.OrthographicCamera;
import thothbot.parallax.core.shared.cameras.PerspectiveCamera;
import thothbot.parallax.core.shared.geometries.PlaneBufferGeometry;
import thothbot.parallax.core.shared.geometries.SphereGeometry;
import thothbot.parallax.core.shared.geometries.TorusGeometry;
import thothbot.parallax.core.shared.lights.DirectionalLight;
import thothbot.parallax.core.shared.materials.MeshBasicMaterial;
import thothbot.parallax.core.shared.materials.MeshPhongMaterial;
import thothbot.parallax.core.shared.materials.ShaderMaterial;
import thothbot.parallax.core.shared.math.Color;
import thothbot.parallax.core.shared.objects.Mesh;
import thothbot.parallax.core.shared.scenes.Scene;
import thothbot.parallax.demo.client.ContentWidget;
import thothbot.parallax.demo.client.Demo;
import thothbot.parallax.demo.client.DemoAnnotations.DemoSource;

import com.google.gwt.core.client.GWT;
import com.google.gwt.core.client.RunAsyncCallback;
import com.google.gwt.event.dom.client.MouseMoveEvent;
import com.google.gwt.event.dom.client.MouseMoveHandler;
import com.google.gwt.resources.client.ImageResource;
import com.google.gwt.resources.client.TextResource;
import com.google.gwt.user.client.rpc.AsyncCallback;

public class MaterialsRenderTarget extends ContentWidget
{
	interface ResourcesPass1 extends DefaultResources
	{
		ResourcesPass1 INSTANCE = GWT.create(ResourcesPass1.class);

		@Source("../../../resources/shaders/rtt.vs")
		TextResource getVertexShader();

		@Source("../../../resources/shaders/rtt_pass_1.fs")
		TextResource getFragmentShader();
	}
	
	interface ResourcesScreen extends DefaultResources
	{
		ResourcesScreen INSTANCE = GWT.create(ResourcesScreen.class);

		@Source("../../../resources/shaders/rtt.vs")
		TextResource getVertexShader();

		@Source("../../../resources/shaders/rtt_screen.fs")
		TextResource getFragmentShader();
	}
	
	/*
	 * Prepare Rendering Scene
	 */
	@DemoSource
	class DemoScene extends DemoAnimatedScene 
	{
		private static final String image = "./static/textures/UV_Grid_Sm.jpg";

		PerspectiveCamera camera;
		OrthographicCamera cameraRTT;
		Scene sceneRTT, sceneScreen;
		
		RenderTargetTexture rtTexture;
		
		ShaderMaterial material;
		
		Mesh zmesh1, zmesh2;
		
		public int mouseX;
		public int mouseY;
		
		double delta = 0.01;
		
		ShaderMaterial materialScreen;
		
		@Override
		protected void onStart()
		{
			camera = new PerspectiveCamera( 30,
					getRenderer().getAbsoluteAspectRation(), 
					1, 
					10000 
			);
			camera.getPosition().setZ(100);
			
			cameraRTT = new OrthographicCamera( getRenderer().getAbsoluteWidth(), getRenderer().getAbsoluteHeight() , -10000, 10000 );
			cameraRTT.getPosition().setZ(100);

			sceneRTT = new Scene();
			sceneScreen = new Scene();

			DirectionalLight light1 = new DirectionalLight( 0xffffff );
			light1.getPosition().set( 0, 0, 1 ).normalize();
			sceneRTT.add( light1 );

			DirectionalLight light2 = new DirectionalLight( 0xffaaaa, 1.5 );
			light2.getPosition().set( 0, 0, -1 ).normalize();
			sceneRTT.add( light2 );

			rtTexture = new RenderTargetTexture( getRenderer().getAbsoluteWidth(), getRenderer().getAbsoluteHeight() );
			rtTexture.setMinFilter(TextureMinFilter.LINEAR);
			rtTexture.setMagFilter(TextureMagFilter.NEAREST);
			rtTexture.setFormat(PixelFormat.RGBA);

			material = new ShaderMaterial(ResourcesPass1.INSTANCE);
			material.getShader().addUniform("time", new Uniform(Uniform.TYPE.F, 0.0 ));

			materialScreen = new ShaderMaterial(ResourcesScreen.INSTANCE);
			materialScreen.getShader().addUniform("tDiffuse", new Uniform(Uniform.TYPE.T, rtTexture ));
			materialScreen.setDepthWrite(false);
						
			PlaneBufferGeometry plane = new PlaneBufferGeometry(getRenderer().getAbsoluteWidth()+1000, getRenderer().getAbsoluteHeight() );
			Mesh quad1 = new Mesh( plane, material );
			quad1.getPosition().setZ( -100 );
			sceneRTT.add( quad1 );

			MeshPhongMaterial mat2 = new MeshPhongMaterial();
			mat2.setColor(new Color(0x550000));
			mat2.setSpecular(new Color(0xff2200));
			mat2.setShininess(5.0);
			
			MeshPhongMaterial mat1 = new MeshPhongMaterial();
			mat1.setColor(new Color(0x555555));
			mat1.setSpecular(new Color(0xffaa00));
			mat1.setShininess(5.0);

			TorusGeometry geometry = new TorusGeometry( 100, 25, 15, 30 );

			zmesh1 = new Mesh( geometry, mat1 );
			zmesh1.getPosition().set( 0, 0, 100 );
			zmesh1.getScale().set( 1.5, 1.5, 1.5 );
			sceneRTT.add( zmesh1 );
			
			zmesh2 = new Mesh( geometry, mat2 );
			zmesh2.getPosition().set( 0, 150, 100 );
			zmesh2.getScale().set( 0.75, 0.75, 0.75 );
			sceneRTT.add( zmesh2 );

			Mesh quad2 = new Mesh( plane, materialScreen );
			quad2.getPosition().setZ( -100 );
			sceneScreen.add( quad2 );

			int n = 5;
			SphereGeometry geometry2 = new SphereGeometry( 10, 64, 32 );
			MeshBasicMaterial material2 = new MeshBasicMaterial();
			material2.setColor(new Color(0xffffff));
			material2.setMap(rtTexture);

			for( int j = 0; j < n; j ++ ) {

				for( int i = 0; i < n; i ++ ) {

					Mesh mesh = new Mesh( geometry2, material2 );

					mesh.getPosition().setX(  ( i - ( n - 1.0 ) / 2.0 ) * 20.0 );
					mesh.getPosition().setY( ( j - ( n - 1.0 ) / 2.0 ) * 20.0 );
					mesh.getPosition().setZ( 0 );

					mesh.getRotation().setY( - Math.PI / 2 );

					getScene().add( mesh );

				}

			}

			getRenderer().setAutoClear(false);
		}
				
		@Override
		protected void onUpdate(double duration)
		{

			camera.getPosition().addX( ( mouseX - camera.getPosition().getX() ) * 0.05 );
			camera.getPosition().addY( ( - mouseY - camera.getPosition().getY() ) * 0.05 );

			camera.lookAt( getScene().getPosition() );

			if ( zmesh1 != null && zmesh2 != null ) {

				zmesh1.getRotation().setY( - duration * 0.0015 );
				zmesh2.getRotation().setY(- duration * 0.0015 + Math.PI / 2 );

			}

			if ( (Double)material.getShader().getUniforms().get("time").getValue() > 1.0 
					|| (Double)material.getShader().getUniforms().get("time").getValue() < 0.0 ) {

				delta *= -1;

			}

			material.getShader().getUniforms().get("time").setValue((Double)material.getShader().getUniforms().get("time").getValue() + delta);

			getRenderer().clear();

			// Render first scene into texture

			getRenderer().render( sceneRTT, cameraRTT, rtTexture, true );

			// Render full screen quad with generated texture

			getRenderer().render( sceneScreen, cameraRTT );

			// Render second scene to screen
			// (using first scene as regular texture)

			getRenderer().render( getScene(), camera );
		}
	}

	public MaterialsRenderTarget() 
	{
		super("Render to texture", "This example based on the three.js example.");
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
		return Demo.resources.exampleMaterialsRtt();
	}
	
	@Override
	protected boolean isEnabledEffectSwitch() {
		return false;
	}
	
	@Override
	protected void asyncOnInitialize(final AsyncCallback<DemoAnimatedScene> callback)
	{
		GWT.runAsync(MaterialsRenderTarget.class, new RunAsyncCallback() 
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

