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

package thothbot.parallax.demo.client.content.animation;

import thothbot.parallax.core.client.gl2.enums.TextureWrapMode;
import thothbot.parallax.core.client.shaders.Shader;
import thothbot.parallax.core.client.shaders.Uniform;
import thothbot.parallax.core.client.textures.DataTexture;
import thothbot.parallax.core.client.textures.Texture;
import thothbot.parallax.core.shared.Log;
import thothbot.parallax.core.shared.cameras.PerspectiveCamera;
import thothbot.parallax.core.shared.core.Color;
import thothbot.parallax.core.shared.core.Vector3;
import thothbot.parallax.core.shared.geometries.CubeGeometry;
import thothbot.parallax.core.shared.geometries.PlaneGeometry;
import thothbot.parallax.core.shared.geometries.SphereGeometry;
import thothbot.parallax.core.shared.geometries.parametric.PlaneParametricGeometry;
import thothbot.parallax.core.shared.helpers.ArrowHelper;
import thothbot.parallax.core.shared.helpers.AxisHelper;
import thothbot.parallax.core.shared.lights.AmbientLight;
import thothbot.parallax.core.shared.lights.DirectionalLight;
import thothbot.parallax.core.shared.materials.Material.SIDE;
import thothbot.parallax.core.shared.materials.MeshPhongMaterial;
import thothbot.parallax.core.shared.materials.ShaderMaterial;
import thothbot.parallax.core.shared.objects.Mesh;
import thothbot.parallax.core.shared.scenes.Fog;
import thothbot.parallax.demo.client.ContentWidget;
import thothbot.parallax.demo.client.Demo;
import thothbot.parallax.demo.client.DemoAnnotations.DemoSource;
import thothbot.parallax.demo.resources.Cloth;

import com.google.gwt.core.client.GWT;
import com.google.gwt.core.client.RunAsyncCallback;
import com.google.gwt.resources.client.ImageResource;
import com.google.gwt.resources.client.TextResource;
import com.google.gwt.user.client.rpc.AsyncCallback;

public final class ClothSimulation extends ContentWidget 
{

	/*
	 * Load shaders
	 */
	@DemoSource
	public interface Resources extends Shader.DefaultResources
	{
		Resources INSTANCE = GWT.create(Resources.class);

		@Source("../../../resources/shaders/cloth_depth.fs")
		TextResource getFragmentShader();
		
		@Source("../../../resources/shaders/cloth_depth.vs")
		TextResource getVertexShader();
	}

	/*
	 * Prepare Rendering Scene
	 */
	@DemoSource
	class DemoScene extends DemoAnimatedScene 
	{
	
		private static final String circuitPattern = "./static/textures/patterns/circuit_pattern.png";
		private static final String grasslight = "./static/textures/terrain/grasslight-big.jpg";
			
		PerspectiveCamera camera;
		Cloth cloth;
		ArrowHelper arrow;
		
		boolean isRotate = true;

		@Override
		protected void onStart()
		{
			camera = new PerspectiveCamera(
					30, // fov
					getRenderer().getAbsoluteAspectRation(), // aspect 
					1, // near
					10000 // far 
			); 
			
			camera.getPosition().setZ(1500);
			camera.getPosition().setY(50);

			getScene().setFog( new Fog( 0x000000, 500, 10000 ) );
			getScene().getFog().getColor().setHSV( 0.6, 0.2, 1 );

			// lights

			getScene().add( new AmbientLight( 0x666666 ) );

			DirectionalLight light = new DirectionalLight( 0xffffff, 1.75 );
			light.getColor().setHSV( 0.6, 0.125, 1 );
			light.getPosition().set( 50, 200, 100 );
			light.getPosition().multiply( 1.3 );

//			light.setCastShadow(true);
//			//light.shadowCameraVisible = true;
//
//			light.setShadowMapWidth( 2048 );
//			light.setShadowMapHeight( 2048 );
//
//			int d = 300;
//
//			light.setShadowCameraLeft( -d );
//			light.setShadowCameraRight( d );
//			light.setShadowCameraTop( d );
//			light.setShadowCameraBottom( -d );
//
//			light.setShadowCameraFar( 1000 );
//			light.setShadowDarkness( 0.5 );

			getScene().add( light );

			DirectionalLight directionalLight = new DirectionalLight( 0xffffff, 0.35 );
			directionalLight.getColor().setHSV( 0.3, 0.95, 1 );
			directionalLight.getPosition().set( 0, -1, 0 );
			getScene().add( directionalLight );

			// cloth geometry

			cloth = new Cloth();
			Cloth.ClothPlane clothGeometry = cloth.new ClothPlane(Cloth.restDistance * Cloth.xSegs, 
					Cloth.restDistance * Cloth.ySegs, cloth.getWidth(), cloth.getHeight());

			clothGeometry.setDynamic(true);
			clothGeometry.computeFaceNormals();
			cloth.setGeometry(clothGeometry);

			// cloth material
			
			Texture clothTexture = new Texture( circuitPattern );
			clothTexture.setWrapS(TextureWrapMode.REPEAT);
			clothTexture.setWrapT(TextureWrapMode.REPEAT);
			clothTexture.setAnisotropy( 16 );

			MeshPhongMaterial material = new MeshPhongMaterial();
			material.setAlphaTest(0.5);
			material.setAmbient(new Color(0xffffff));
			material.setColor(new Color(0xffffff));
			material.setSpecular(new Color(0x030303));
			material.setEmissive(new Color(0x111111));
			material.setShininess(10);
			material.setPerPixel(true);
			material.setMetal(false);
			material.setMap(clothTexture);
			material.setSide(SIDE.DOUBLE);

			// cloth mesh

			Mesh object = new Mesh( cloth.getGeometry(), material );
			object.getPosition().set( 0 );
			object.setCastShadow(true);
			object.setReceiveShadow(true);
			getScene().add( object );

			object.setCustomDepthMaterial( new ShaderMaterial(Resources.INSTANCE) );
			object.getCustomDepthMaterial().getShader().addUniform("texture", new Uniform( Uniform.TYPE.T ));

			// sphere

			SphereGeometry ballGeo = new SphereGeometry( cloth.getBallSize(), 20, 20 );
			MeshPhongMaterial ballMaterial = new MeshPhongMaterial();
			ballMaterial.setColor(new Color(0xffffff));

			Mesh sphere = new Mesh( ballGeo, ballMaterial );
			cloth.setBall(sphere);
			sphere.setCastShadow(true);
			sphere.setReceiveShadow(true);
			getScene().add( sphere );

			// arrow

			arrow = new ArrowHelper( new Vector3( 0, 1, 0 ), new Vector3( 0, 0, 0 ), 50, 0xff0000 );
			arrow.getPosition().set( -200, 0, -200 );
			getScene().add( arrow );

			AxisHelper axis = new AxisHelper();
			axis.getPosition().set( 200, 0, -200 );
			axis.getScale().set( 0.5 );
			getScene().add( axis );

			// ground

			Color initColor = new Color( 0x00ff00 );
			initColor.setHSV( 0.25, 0.85, 0.5 );

			final MeshPhongMaterial groundMaterial = new MeshPhongMaterial();
			groundMaterial.setColor(new Color(0xffffff));
			groundMaterial.setSpecular(new Color(0x111111));
			groundMaterial.setMap(new DataTexture(1, 1, initColor));
			groundMaterial.setPerPixel(true);

			Texture groundTexture = new Texture( grasslight, new Texture.ImageLoadHandler() {
				
				@Override
				public void onImageLoad(Texture texture) {
					groundMaterial.setMap(texture);
				}
			});

			groundTexture.setWrapS(TextureWrapMode.REPEAT);
			groundTexture.setWrapT(TextureWrapMode.REPEAT);
			groundTexture.getRepeat().set( 25, 25 );
			groundTexture.setAnisotropy( 16 );

			Mesh mesh1 = new Mesh( new PlaneGeometry( 20000, 20000 ), groundMaterial );
			mesh1.getPosition().setY( -250 );
			mesh1.getRotation().setX( - Math.PI / 2 );
			mesh1.setReceiveShadow(true);
			getScene().add( mesh1 );

			// poles

			CubeGeometry poleGeo = new CubeGeometry( 5, 750, 5 );
			MeshPhongMaterial poleMat = new MeshPhongMaterial();
			poleMat.setColor(new Color(0xffffff));
			poleMat.setSpecular(new Color(0x111111));
			poleMat.setShininess(100);
			poleMat.setPerPixel(true);

			Mesh mesh2 = new Mesh( poleGeo, poleMat );
			mesh2.getPosition().setY( -250 );
			mesh2.getPosition().setX( -125 );
			mesh2.setReceiveShadow(true);
			mesh2.setCastShadow(true);
			getScene().add( mesh2 );

			Mesh mesh3 = new Mesh( poleGeo, poleMat );
			mesh3.getPosition().setY( -250 );
			mesh3.getPosition().setX( 125 );
			mesh3.setReceiveShadow(true);
			mesh3.setCastShadow(true);
			getScene().add( mesh3 );

			Mesh mesh4 = new Mesh( new CubeGeometry( 255, 5, 5 ), poleMat );
			mesh4.getPosition().setY( -250 + 750/2 );
			mesh4.getPosition().setX( 0 );
			mesh4.setReceiveShadow(true);
			mesh4.setCastShadow(true);
			getScene().add( mesh4 );

			CubeGeometry gg = new CubeGeometry( 10, 10, 10 );
			Mesh mesh5 = new Mesh( gg, poleMat );
			mesh5.getPosition().setY( -250 );
			mesh5.getPosition().setX( 125 );
			mesh5.setReceiveShadow(true);
			mesh5.setCastShadow(true);
			getScene().add( mesh5 );

			Mesh mesh6 = new Mesh( gg, poleMat );
			mesh6.getPosition().setY( -250 );
			mesh6.getPosition().setX( -125 );
			mesh6.setReceiveShadow(true);
			mesh6.setCastShadow(true);
			getScene().add( mesh6 );

			//

			getRenderer().setClearColor( getScene().getFog().getColor(), 1.0 );
			getRenderer().setGammaInput(true);
			getRenderer().setGammaOutput(true);
			getRenderer().setPhysicallyBasedShading(true);
//			getRenderer().setShadowMapEnabled(true);

			sphere.setVisible(false);
		}
		
		@Override
		protected void onUpdate(double duration)
		{
			cloth.setWindStrength( Math.cos( duration / 7000 ) * 20 + 40 );
			cloth.getWindForce().set( 
					Math.sin( duration / 2000 ), 
					Math.cos( duration / 3000 ), 
					Math.sin( duration / 1000 ) ).normalize().multiply( cloth.getWindStrength() );

			cloth.simulate();

			arrow.setLength( cloth.getWindStrength() );
			arrow.setDirection( cloth.getWindForce() );
			
			double timer = duration * 0.0002;
			
			if ( isRotate ) 
			{
				camera.getPosition().setX( Math.cos( timer ) * 1500 );
				camera.getPosition().setZ( Math.sin( timer ) * 1500 );
			}

			camera.lookAt( getScene().getPosition() );
			getRenderer().render(getScene(), camera);
		}
	}
		
	public ClothSimulation() 
	{
		super("Cloth Simulation", "This example based on the three.js example.");
	}
	
	@Override
	public DemoScene onInitialize()
	{
		return new DemoScene();
	}

	@Override
	public ImageResource getIcon()
	{
		return Demo.resources.exampleClothSimulation();
	}
	
	@Override
	protected void asyncOnInitialize(final AsyncCallback<DemoAnimatedScene> callback)
	{
		GWT.runAsync(ClothSimulation.class, new RunAsyncCallback() 
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
