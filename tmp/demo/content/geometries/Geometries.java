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

package org.parallax3d.parallax.demo.content.geometries;

import org.parallax3d.parallax.graphics.core.Object3D;
import org.parallax3d.parallax.graphics.extras.geometries.*;
import org.parallax3d.parallax.graphics.extras.helpers.ArrowHelper;
import org.parallax3d.parallax.graphics.extras.helpers.AxisHelper;
import org.parallax3d.parallax.graphics.lights.DirectionalLight;
import org.parallax3d.parallax.graphics.materials.Material;
import org.parallax3d.parallax.graphics.materials.MeshLambertMaterial;
import org.parallax3d.parallax.graphics.objects.Mesh;
import org.parallax3d.parallax.graphics.textures.Texture;
import org.parallax3d.parallax.math.Color;
import org.parallax3d.parallax.math.Vector3;
import org.parallax3d.parallax.system.gl.enums.TextureWrapMode;

public class Geometries extends ContentWidget
{
	/*
	 * Prepare Rendering Scene
	 */
	@DemoSource
	class DemoScene extends DemoAnimatedScene 
	{
		private static final String image = "./static/textures/UV_Grid_Sm.jpg";

		PerspectiveCamera camera;
		
		@Override
		protected void onStart()
		{
			camera = new PerspectiveCamera( 45,
					getRenderer().getAbsoluteAspectRation(), 
					1, 
					2000 
			);
			camera.getPosition().setY(400);
			
			getScene().add( new AmbientLight( 0x404040 ) );
	
			DirectionalLight light = new DirectionalLight( 0xffffff );
			light.getPosition().set( 0, 1, 0 );
			getScene().add( light );
			
			Texture texture = new Texture(image);
			texture.setWrapS(TextureWrapMode.REPEAT);
			texture.setWrapT(TextureWrapMode.REPEAT);
			texture.setAnisotropy(16);

			MeshLambertMaterial material = new MeshLambertMaterial();
			material.setMap( texture );
			material.setAmbient( new Color(0xbbbbbb) );
			material.setSide(Material.SIDE.DOUBLE);
				
			Object3D object1 = new Mesh( new SphereGeometry( 75, 20, 10 ), material );
			object1.getPosition().set( -400, 0, 200 );
			getScene().add( object1 );
			
			Object3D object2 = new Mesh(  new IcosahedronGeometry( 75, 1 ), material );
			object2.getPosition().set( -200, 0, 200 );
			getScene().add( object2 );
			
			Object3D object3 = new Mesh( new OctahedronGeometry( 75, 2 ), material );
			object3.getPosition().set( 0, 0, 200 );
			getScene().add( object3 );
			
			Object3D object4 = new Mesh( new TetrahedronGeometry( 75, 0 ), material );
			object4.getPosition().set( 200, 0, 200 );
			getScene().add( object4 );
			
			//
			
			Object3D object5 = new Mesh( new PlaneGeometry( 100, 100, 4, 4 ), material );
			object5.getPosition().set( -400, 0, 0 );
			getScene().add( object5 );
			
			Object3D object6 = new Mesh( new BoxGeometry( 100, 100, 100, 4, 4, 4 ), material );
			object6.getPosition().set( -200, 0, 0 );
			getScene().add( object6 );
			
			Object3D object7 = new Mesh( new CircleGeometry( 50, 20, 0, Math.PI * 2 ), material );
			object7.getPosition().set( 0, 0, 0 );
			getScene().add( object7 );
			
			Object3D object8 = new Mesh( new RingGeometry( 10, 50, 20, 5, 0, Math.PI * 2 ), material );
			object8.getPosition().set( 200, 0, 0 );
			getScene().add( object8 );
			
			Object3D object9 = new Mesh( new CylinderGeometry( 25, 75, 100, 40, 5 ), material );
			object9.getPosition().set( 400, 0, 0 );
			getScene().add( object9 );
		
			List<Vector3> points = new ArrayList<Vector3>();
	
			for ( int i = 0; i < 50; i ++ )
			{
				points.add( new Vector3( Math.sin( i * 0.2 ) * Math.sin( i * 0.1 ) * 15.0 + 50.0, 0.0, ( i - 5.0 ) * 2.0 )  );
			}
	
			Object3D object10 = new Mesh( new LatheGeometry( points, 20 ), material );
			object10.getPosition().set( -400, 0, -200 );
			getScene().add( object10 );
			
			Object3D object11 = new Mesh( new TorusGeometry( 50, 20, 20, 20 ), material );
			object11.getPosition().set( -200, 0, -200 );
			getScene().add( object11 );

			Object3D object12 = new Mesh( new TorusKnotGeometry( 50, 10, 50, 20 ), material );
			object12.getPosition().set( 0, 0, -200 );
			getScene().add( object12 );
	
			AxisHelper object13 = new AxisHelper();
			object13.getPosition().set( 200, 0, -200 );
			getScene().add( object13 );
			
			ArrowHelper object14 = new ArrowHelper( new Vector3( 0, 1, 0 ), new Vector3( 0, 0, 0 ), 50 );
			object14.getPosition().set( 400, 0, -200 );
			getScene().add( object14 );
		}
				
		@Override
		protected void onUpdate(double duration)
		{
			camera.getPosition().setX(Math.cos( duration * 0.0001 ) * 800.0);
			camera.getPosition().setZ(Math.sin( duration * 0.0001 ) * 800.0);

			camera.lookAt( getScene().getPosition() );

			for ( int i = 0, l = getScene().getChildren().size(); i < l; i ++ ) 
			{
				Object3D object = getScene().getChildren().get( i );

				object.getRotation().addX(0.01);
				object.getRotation().addY(0.005);
			}
			
			getRenderer().render(getScene(), camera);
		}
	}

	public Geometries() 
	{
		super("Different geometries", "Here are used pull of some geometric objects and two materials: mesh basic and lambert. This example based on the three.js example.");
	}

	@Override
	public DemoScene onInitialize()
	{
		return new DemoScene();
	}
	
	@Override
	protected void asyncOnInitialize(final AsyncCallback<DemoAnimatedScene> callback)
	{
		GWT.runAsync(Geometries.class, new RunAsyncCallback() 
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
