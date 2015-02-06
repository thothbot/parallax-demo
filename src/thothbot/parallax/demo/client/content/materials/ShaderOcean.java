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

import thothbot.parallax.core.client.controls.FirstPersonControls;
import thothbot.parallax.core.client.events.HasEventBus;
import thothbot.parallax.core.client.events.ViewportResizeEvent;
import thothbot.parallax.core.client.events.ViewportResizeHandler;
import thothbot.parallax.core.client.gl2.enums.TextureWrapMode;
import thothbot.parallax.core.client.shaders.CubeShader;
import thothbot.parallax.core.client.textures.CubeTexture;
import thothbot.parallax.core.client.textures.Texture;
import thothbot.parallax.core.shared.Log;
import thothbot.parallax.core.shared.cameras.PerspectiveCamera;
import thothbot.parallax.core.shared.geometries.BoxGeometry;
import thothbot.parallax.core.shared.geometries.IcosahedronGeometry;
import thothbot.parallax.core.shared.geometries.PlaneBufferGeometry;
import thothbot.parallax.core.shared.lights.HemisphereLight;
import thothbot.parallax.core.shared.materials.Material;
import thothbot.parallax.core.shared.materials.Material.COLORS;
import thothbot.parallax.core.shared.materials.MeshBasicMaterial;
import thothbot.parallax.core.shared.materials.MeshPhongMaterial;
import thothbot.parallax.core.shared.materials.ShaderMaterial;
import thothbot.parallax.core.shared.math.Color;
import thothbot.parallax.core.shared.math.Mathematics;
import thothbot.parallax.core.shared.objects.Mesh;
import thothbot.parallax.demo.client.ContentWidget;
import thothbot.parallax.demo.client.Demo;
import thothbot.parallax.demo.client.DemoAnnotations.DemoSource;
import thothbot.parallax.demo.resources.Water;

import com.google.gwt.core.client.Duration;
import com.google.gwt.core.client.GWT;
import com.google.gwt.core.client.RunAsyncCallback;
import com.google.gwt.resources.client.ImageResource;
import com.google.gwt.user.client.rpc.AsyncCallback;

public final class ShaderOcean extends ContentWidget {

	/*
	 * Prepare Rendering Scene
	 */
	@DemoSource
	class DemoScene extends DemoAnimatedScene implements HasEventBus, ViewportResizeHandler
	{
		private static final String waternormals = "./static/textures/waternormals.jpg";
		private static final String textures = "./static/textures/cube/skybox/*.jpg";
		
		public int width = 2000;
		public int height = 2000;
		public int widthSegments = 250;
		public int heightSegments = 250;
		public int depth = 1500;
		public int param = 4;
		public int filterparam = 1;

		PerspectiveCamera camera;
		
		FirstPersonControls controls;
		
		Water water;
		Mesh sphere;
		
		private double oldTime;
		
		@Override
		public void onResize(ViewportResizeEvent event) 
		{		
		}
		
		@Override
		protected void onStart()
		{
			EVENT_BUS.addHandler(ViewportResizeEvent.TYPE, this);
			
			camera = new PerspectiveCamera(
					55, // fov
					getRenderer().getAbsoluteAspectRation(), // aspect 
					0.5, // near
					3000000 // far 
			); 
			
			camera.getPosition().set( 2000, 750, 2000 );
			
			this.controls = new FirstPersonControls( camera, getCanvas() );
			controls.setMovementSpeed(500);
			controls.setLookSpeed(0.1);
			
			HemisphereLight light = new HemisphereLight( 0xffffbb, 0x080820, 1 );
			light.getPosition().set( - 1, 1, - 1 );
			getScene().add( light );
			
			water = new Water( getRenderer(), camera, getScene());
			water.width = 512;
			water.height = 512;
			water.alpha = 1.0;
			water.sunDirection = light.getPosition().clone().normalize();
			water.sunColor = new Color(0xffffff);
			water.waterColor = new Color(0x001e0f);
			
			Texture waterNormals = new Texture( waternormals, new Texture.ImageLoadHandler() {
				
				@Override
				public void onImageLoad(Texture texture) {
					water.normalSampler = texture;
					water.updateUniforms();
				}
			});
			
			waterNormals.setWrapS(TextureWrapMode.REPEAT);
			waterNormals.setWrapT(TextureWrapMode.REPEAT);

			Mesh mirrorMesh = new Mesh(
					new PlaneBufferGeometry( this.width * 500, this.height * 500 ),
					water.material
			);
						
			mirrorMesh.add( water );
			mirrorMesh.getRotation().setX( - Math.PI * 0.5 );
			getScene().add( mirrorMesh );
			
			CubeTexture textureCube = new CubeTexture( textures );
			
			MeshBasicMaterial material = new MeshBasicMaterial();
			material.setColor( new Color(0xffffff) );
			material.setEnvMap( textureCube );
			
			// Skybox

			ShaderMaterial sMaterial = new ShaderMaterial( new CubeShader() );
			sMaterial.getShader().getUniforms().get("tCube").setValue( textureCube ); 
			sMaterial.setDepthWrite( false );
			sMaterial.setSide(Material.SIDE.BACK);
			
			Mesh mesh = new Mesh( new BoxGeometry( 1000000, 1000000, 1000000 ), sMaterial );
			getScene().add( mesh );

			// Sphere
			IcosahedronGeometry geometry = new IcosahedronGeometry( 400, 4 );

			for ( int i = 0, j = geometry.getFaces().size(); i < j; i ++ ) {

				geometry.getFaces().get(i).getColor().setHex( Mathematics.randInt(0x111111, 0xffffff) );

			}

			MeshPhongMaterial sphereMaterial = new MeshPhongMaterial();
			sphereMaterial.setVertexColors(COLORS.FACE);
			sphereMaterial.setEnvMap(textureCube);
			sphereMaterial.setShininess(100.0);
			
			sphere = new Mesh( geometry, sphereMaterial );
			getScene().add( sphere );			
		}
		
		@Override
		protected void onUpdate(double duration)
		{
			double time = Duration.currentTimeMillis() * 0.001;

			sphere.getPosition().setY( Math.sin( time ) * 500 + 250 );
			sphere.getRotation().setY( time * 0.5 );
			sphere.getRotation().setZ( time * 0.51 );

			water.material.getShader().getUniforms().get("time").setValue(  (Double)water.material.getShader().getUniforms().get("time").getValue() + 1.0 / 60.0 );
			water.render();
			controls.update((Duration.currentTimeMillis() - this.oldTime) * 0.001);
			this.oldTime = Duration.currentTimeMillis();
			getRenderer().render(getScene(), camera);
			Log.error(camera.getPosition(), camera.getRotation(), camera.getMatrix());

		}
	}
	
	public ShaderOcean() 
	{
		super("Ocean shader", "This example based on the three.js example.");
	}
	
	@Override
	protected DemoScene onInitialize() {
		return new DemoScene();
	}
	
	@Override
	public ImageResource getIcon() {
		return Demo.resources.exampleMaterialsShaderOcean();
	}
	
	@Override
	protected boolean isEnabledEffectSwitch() {
		return false;
	}

	@Override
	protected void asyncOnInitialize(final AsyncCallback<DemoAnimatedScene> callback)
	{
		GWT.runAsync(ShaderOcean.class, new RunAsyncCallback() 
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
