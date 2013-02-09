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

package thothbot.parallax.demo.client.content.geometries;

import java.util.ArrayList;
import java.util.List;

import thothbot.parallax.core.client.gl2.enums.TextureWrapMode;
import thothbot.parallax.core.client.textures.Texture;
import thothbot.parallax.core.shared.cameras.PerspectiveCamera;
import thothbot.parallax.core.shared.geometries.CircleGeometry;
import thothbot.parallax.core.shared.geometries.CubeGeometry;
import thothbot.parallax.core.shared.geometries.CylinderGeometry;
import thothbot.parallax.core.shared.geometries.IcosahedronGeometry;
import thothbot.parallax.core.shared.geometries.LatheGeometry;
import thothbot.parallax.core.shared.geometries.OctahedronGeometry;
import thothbot.parallax.core.shared.geometries.PlaneGeometry;
import thothbot.parallax.core.shared.geometries.SphereGeometry;
import thothbot.parallax.core.shared.geometries.TetrahedronGeometry;
import thothbot.parallax.core.shared.geometries.TorusGeometry;
import thothbot.parallax.core.shared.geometries.TorusKnotGeometry;
import thothbot.parallax.core.shared.helpers.ArrowHelper;
import thothbot.parallax.core.shared.helpers.AxisHelper;
import thothbot.parallax.core.shared.lights.AmbientLight;
import thothbot.parallax.core.shared.lights.DirectionalLight;
import thothbot.parallax.core.shared.materials.Material;
import thothbot.parallax.core.shared.materials.MeshBasicMaterial;
import thothbot.parallax.core.shared.materials.MeshLambertMaterial;
import thothbot.parallax.core.shared.math.Color;
import thothbot.parallax.core.shared.math.Vector3;
import thothbot.parallax.core.shared.objects.DimensionalObject;
import thothbot.parallax.core.shared.utils.SceneUtils;
import thothbot.parallax.demo.client.ContentWidget;
import thothbot.parallax.demo.client.Demo;
import thothbot.parallax.demo.client.DemoAnnotations.DemoSource;

import com.google.gwt.core.client.GWT;
import com.google.gwt.core.client.RunAsyncCallback;
import com.google.gwt.resources.client.ImageResource;
import com.google.gwt.user.client.rpc.AsyncCallback;

public class Geometries extends ContentWidget
{
	/*
	 * Prepare Rendering Scene
	 */
	@DemoSource
	class DemoScene extends DemoAnimatedScene 
	{
		private static final String image = "./static/textures/ash_uvgrid01.jpg";

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

			List<Material> materials = new ArrayList<Material>();
			MeshLambertMaterial lmaterial = new MeshLambertMaterial();
			lmaterial.setMap( texture );
			lmaterial.setAmbient( new Color(0xbbbbbb) );
			lmaterial.setSide(Material.SIDE.DOUBLE);
			materials.add(lmaterial);	
			
			MeshBasicMaterial bmaterial = new MeshBasicMaterial();
			bmaterial.setColor( new Color(0xffffff) );
			bmaterial.setWireframe(true);
			bmaterial.setTransparent(true);
			bmaterial.setOpacity( 0.1 );
			materials.add(bmaterial);
	
			DimensionalObject object1 = SceneUtils.createMultiMaterialObject( new CubeGeometry( 100, 100, 100, 4, 4, 4 ), materials );
			object1.getPosition().set( -200, 0, 400 );
			getScene().add( object1 );
			
			DimensionalObject object2 = SceneUtils.createMultiMaterialObject( new CylinderGeometry( 25, 75, 100, 40, 5 ), materials );
			object2.getPosition().set( 0, 0, 400 );
			getScene().add( object2 );
			
			DimensionalObject object3 = SceneUtils.createMultiMaterialObject( new IcosahedronGeometry( 75, 1 ), materials );
			object3.getPosition().set( -200, 0, 200 );
			getScene().add( object3 );
			
			DimensionalObject object4 = SceneUtils.createMultiMaterialObject( new OctahedronGeometry( 75, 2 ), materials );
			object4.getPosition().set( 0, 0, 200 );
			getScene().add( object4 );
			
			DimensionalObject object5 = SceneUtils.createMultiMaterialObject( new TetrahedronGeometry( 75, 0 ), materials );
			object5.getPosition().set( 200, 0, 200 );
			getScene().add( object5 );
			
			DimensionalObject object6 = SceneUtils.createMultiMaterialObject( new PlaneGeometry( 100, 100, 4, 4 ), materials );
			object6.getPosition().set( -200, 0, 0 );
			getScene().add( object6 );
			
			DimensionalObject object6a = SceneUtils.createMultiMaterialObject( new CircleGeometry( 50, 10, 0, Math.PI ), materials );
			object6a.getRotation().setX( Math.PI / 2.0 );
			object6.add( object6a );
			
			DimensionalObject object7 = SceneUtils.createMultiMaterialObject( new SphereGeometry( 75, 20, 10 ), materials );
			object7.getPosition().set( 0, 0, 0 );
			getScene().add( object7 );
		
			List<Vector3> points = new ArrayList<Vector3>();
	
			for ( int i = 0; i < 50; i ++ )
				points.add( new Vector3( Math.sin( i * 0.2 ) * 15.0 + 50.0, 0.0, ( i - 5.0 ) * 2.0 ) );
	
			DimensionalObject object8 = SceneUtils.createMultiMaterialObject( new LatheGeometry( points, 20 ), materials );
			object8.getPosition().set( 200, 0, 0 );
			getScene().add( object8 );
			
			DimensionalObject object9 = SceneUtils.createMultiMaterialObject( new TorusGeometry( 50, 20, 20, 20 ), materials );
			object9.getPosition().set( -200, 0, -200 );
			getScene().add( object9 );

			DimensionalObject object10 = SceneUtils.createMultiMaterialObject( new TorusKnotGeometry( 50, 10, 50, 20 ), materials );
			object10.getPosition().set( 0, 0, -200 );
			getScene().add( object10 );
	
			AxisHelper object11 = new AxisHelper();
			object11.getPosition().set( 200, 0, -200 );
			object11.getScale().set(0.5);
			getScene().add( object11 );
			
			ArrowHelper object12 = new ArrowHelper( new Vector3( 0, 1, 0 ), new Vector3( 0, 0, 0 ), 50 );
			object12.getPosition().set( 300, 0, 300 );
			getScene().add( object12 );
		}
				
		@Override
		protected void onUpdate(double duration)
		{
			camera.getPosition().setX(Math.cos( duration * 0.0001 ) * 800.0);
			camera.getPosition().setZ(Math.sin( duration * 0.0001 ) * 800.0);

			camera.lookAt( getScene().getPosition() );

			for ( int i = 0, l = getScene().getChildren().size(); i < l; i ++ ) 
			{
				DimensionalObject object = getScene().getChildren().get( i );

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
	public ImageResource getIcon()
	{
		return Demo.resources.exampleGeometries();
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
