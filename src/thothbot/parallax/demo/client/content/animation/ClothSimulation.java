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

package thothbot.parallax.demo.client.content.animation;

import thothbot.parallax.core.client.gl2.enums.TextureWrapMode;
import thothbot.parallax.core.client.textures.Texture;
import thothbot.parallax.core.shared.cameras.PerspectiveCamera;
import thothbot.parallax.core.shared.core.Color;
import thothbot.parallax.core.shared.geometries.parametric.PlaneParametricGeometry;
import thothbot.parallax.core.shared.helpers.AxisHelper;
import thothbot.parallax.core.shared.lights.AmbientLight;
import thothbot.parallax.core.shared.lights.DirectionalLight;
import thothbot.parallax.core.shared.materials.MeshPhongMaterial;
import thothbot.parallax.core.shared.objects.Mesh;
import thothbot.parallax.core.shared.scenes.FogSimple;
import thothbot.parallax.core.shared.utils.ImageUtils;
import thothbot.parallax.demo.client.ContentWidget;
import thothbot.parallax.demo.client.Demo;
import thothbot.parallax.demo.client.DemoAnnotations.DemoSource;

import com.google.gwt.core.client.GWT;
import com.google.gwt.core.client.RunAsyncCallback;
import com.google.gwt.resources.client.ImageResource;
import com.google.gwt.user.client.rpc.AsyncCallback;

public final class ClothSimulation extends ContentWidget 
{

	/*
	 * Prepare Rendering Scene
	 */
	@DemoSource
	class DemoScene extends DemoAnimatedScene 
	{
	
		private static final String circuitPattern = "./static/textures/patterns/circuit_pattern.png";
		private static final String grasslight = "./static/textures/terrain/grasslight-big.jpg";
		
		PlaneParametricGeometry clothGeometry;
		
		Mesh object;
		Mesh sphere;

		@Override
		protected void loadCamera()
		{
			setCamera(
					new PerspectiveCamera(
							30, // fov
							getRenderer().getCanvas().getAspectRation(), // aspect 
							1, // near
							10000 // far 
					)); 
		}

		@Override
		protected void onStart()
		{
			getCamera().getPosition().setZ(1500);
			getCamera().getPosition().setY(50);
			getScene().add(getCamera());

			getScene().setFog( new FogSimple( 0x000000, 500, 10000 ) );
			getScene().getFog().getColor().setHSV( 0.6, 0.2, 1 );

			// lights

			getScene().add( new AmbientLight( 0x666666 ) );

			DirectionalLight light = new DirectionalLight( 0xffffff, 1.75 );
			light.getColor().setHSV( 0.6, 0.125, 1 );
			light.getPosition().set( 50, 200, 100 );
			light.getPosition().multiply( 1.3 );

			light.setCastShadow(true);
			//light.shadowCameraVisible = true;

			light.shadowMapWidth = 2048;
			light.shadowMapHeight = 2048;

			int d = 300;

			light.shadowCameraLeft = -d;
			light.shadowCameraRight = d;
			light.shadowCameraTop = d;
			light.shadowCameraBottom = -d;

			light.shadowCameraFar = 1000;
			light.shadowDarkness = 0.5;

			getScene().add( light );

			DirectionalLight directionalLight = new DirectionalLight( 0xffffff, 0.35 );
			directionalLight.getColor().setHSV( 0.3, 0.95, 1 );
			directionalLight.getPosition().set( 0, -1, 0 );
			getScene().add( directionalLight );

			// cloth material
			
			Texture clothTexture = ImageUtils.loadTexture( circuitPattern );
			clothTexture.setWrapS(TextureWrapMode.REPEAT);
			clothTexture.setWrapT(TextureWrapMode.REPEAT);
			clothTexture.setAnisotropy( 16 );

			materials = [
				new MeshPhongMaterial( { alphaTest: 0.5, ambient: 0xffffff, color: 0xffffff, specular: 0x030303, emissive: 0x111111, shiness: 10, perPixel: true, metal: false, map: clothTexture, side: THREE.DoubleSide } ),
				new MeshBasicMaterial( { color: 0xff0000, wireframe: true, transparent: true, opacity: 0.9 } )
			];

			// cloth geometry

			clothGeometry = new PlaneParametricGeometry( 200, 200, cloth.w, cloth.h );
			clothGeometry.dynamic = true;
			clothGeometry.computeFaceNormals();

			var uniforms = { texture:  { type: "t", value: 0, texture: clothTexture } };

			// cloth mesh

			object = new Mesh( clothGeometry, materials[ 0 ] );
			object.getPosition().set( 0 );
			object.castShadow = true;
			object.receiveShadow = true;
			getScene().add( object );

			object.customDepthMaterial = new ShaderMaterial( { uniforms: uniforms, vertexShader: vertexShader, fragmentShader: fragmentShader } );

			// sphere

			SphereGeometry ballGeo = new SphereGeometry( ballSize, 20, 20 );
			MeshPhongMaterial ballMaterial = new MeshPhongMaterial();
			ballMaterial.setColor(new Color(0xffffff));

			sphere = new Mesh( ballGeo, ballMaterial );
			sphere.castShadow = true;
			sphere.receiveShadow = true;
			getScene().add( sphere );

			// arrow

			AxisHelper axis = new AxisHelper();
			axis.getPosition().set( 200, 0, -200 );
			axis.getScale().set( 0.5 );
			scene.add( axis );

			// ground

			Color initColor = new Color( 0x00ff00 );
			initColor.setHSV( 0.25, 0.85, 0.5 );
			Texture initTexture = ImageUtils.generateDataTexture( 1, 1, initColor );

			MeshPhongMaterial groundMaterial = new MeshPhongMaterial();
			groundMaterial.setColor(new Color(0xffffff));
			groundMaterial.setSpecular(new Color(0x111111));
			groundMaterial.setMap(initTexture);
			groundMaterial.setPerPixel(true);

			Texture groundTexture = ImageUtils.loadTexture( grasslight, undefined, function() { groundMaterial.map = groundTexture } );
			groundTexture.setWrapS(TextureWrapMode.REPEAT);
			groundTexture.setWrapT(TextureWrapMode.REPEAT);
			groundTexture.getRepeat().set( 25, 25 );
			groundTexture.setAnisotropy( 16 );

			Mesh mesh = new Mesh( new PlaneGeometry( 20000, 20000 ), groundMaterial );
			mesh.getPosition().setY( -250 );
			mesh.getRotation().setX( - Math.PI / 2 );
			mesh.receiveShadow = true;
			getScene().add( mesh );

			// poles

			CubeGeometry poleGeo = new CubeGeometry( 5, 750, 5 );
			MeshPhongMaterial poleMat = new MeshPhongMaterial();
			poleMat.setColor(new Color(0xffffff));
			poleMat.setSpecular(new Color(0x111111));
			poleMat.setShininess(100);
			poleMat.setPerPixel(true);

			Mesh mesh = new Mesh( poleGeo, poleMat );
			mesh.getPosition().setY( -250 );
			mesh.getPosition().setX( -125 );
			mesh.receiveShadow = true;
			mesh.castShadow = true;
			getScene().add( mesh );

			Mesh mesh = new Mesh( poleGeo, poleMat );
			mesh.getPosition().setY( -250 );
			mesh.getPosition().setX( 125 );
			mesh.receiveShadow = true;
			mesh.castShadow = true;
			getScene().add( mesh );

			Mesh mesh = new Mesh( new CubeGeometry( 255, 5, 5 ), poleMat );
			mesh.getPosition().setY( -250 + 750/2 );
			mesh.getPosition().setX( 0 );
			mesh.receiveShadow = true;
			mesh.castShadow = true;
			getScene().add( mesh );

			CubeGeometry gg = new CubeGeometry( 10, 10, 10 );
			Mesh mesh = new Mesh( gg, poleMat );
			mesh.position.setY( -250 );
			mesh.position.setX( 125 );
			mesh.receiveShadow = true;
			mesh.castShadow = true;
			getScene().add( mesh );

			var mesh = new THREE.Mesh( gg, poleMat );
			mesh.position.y = -250;
			mesh.position.x = -125;
			mesh.receiveShadow = true;
			mesh.castShadow = true;
			getScene().add( mesh );

			//

			getRenderer().setClearColor( getScene().getFog().getColor() );
			getRenderer().setGammaInput(true);
			getRenderer().setGammaOutput(true);
			getRenderer().setPhysicallyBasedShading(true);
			getRenderer().setShadowMapEnabled(true);

			sphere.setVisible(false);
		}
		
		@Override
		protected void onStop()
		{			
		}
		
		@Override
		protected void onUpdate(double duration)
		{
			windStrength = Math.cos( duration / 7000 ) * 20 + 40;
			windForce.set( Math.sin( duration / 2000 ), Math.cos( duration / 3000 ), Math.sin( duration / 1000 ) ).normalize().multiplyScalar( windStrength );
			arrow.setLength( windStrength );
			arrow.setDirection( windForce );

			simulate();

			double timer = duration * 0.0002;

			var p = cloth.particles;

			for ( int i = 0, il = p.length; i < il; i ++ ) 
			{
				clothGeometry.vertices[ i ].copy( p[ i ].position );
			}

			clothGeometry.computeFaceNormals();
			clothGeometry.computeVertexNormals();

			clothGeometry.normalsNeedUpdate = true;
			clothGeometry.verticesNeedUpdate = true;

			sphere.getPosition().copy( ballPosition );

			if ( rotate ) 
			{
				getCamera().getPosition().setX( Math.cos( timer ) * 1500 );
				getCamera().getPosition().setZ( Math.sin( timer ) * 1500 );
			}

			getCamera().lookAt( getScene().getPosition() );
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
