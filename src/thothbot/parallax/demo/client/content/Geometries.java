/*
 * Copyright 2012 Alex Usachev, thothbot@gmail.com
 * 
 * This file based on the JavaScript source file of the THREE.JS project, 
 * licensed under MIT License.
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

package thothbot.parallax.demo.client.content;

import java.util.ArrayList;
import java.util.List;

import thothbot.parallax.core.client.RenderingPanel;
import thothbot.parallax.core.shared.cameras.PerspectiveCamera;
import thothbot.parallax.core.shared.core.Color3f;
import thothbot.parallax.core.shared.core.Vector3f;
import thothbot.parallax.core.shared.geometries.Cube;
import thothbot.parallax.core.shared.geometries.Cylinder;
import thothbot.parallax.core.shared.geometries.Icosahedron;
import thothbot.parallax.core.shared.geometries.Lathe;
import thothbot.parallax.core.shared.geometries.Octahedron;
import thothbot.parallax.core.shared.geometries.Plane;
import thothbot.parallax.core.shared.geometries.Sphere;
import thothbot.parallax.core.shared.geometries.Tetrahedron;
import thothbot.parallax.core.shared.geometries.Torus;
import thothbot.parallax.core.shared.geometries.TorusKnot;
import thothbot.parallax.core.shared.helpers.ArrowHelper;
import thothbot.parallax.core.shared.helpers.AxisHelper;
import thothbot.parallax.core.shared.lights.AmbientLight;
import thothbot.parallax.core.shared.lights.DirectionalLight;
import thothbot.parallax.core.shared.materials.Material;
import thothbot.parallax.core.shared.materials.MeshBasicMaterial;
import thothbot.parallax.core.shared.materials.MeshLambertMaterial;
import thothbot.parallax.core.shared.objects.DimensionalObject;
import thothbot.parallax.core.shared.objects.Mesh;
import thothbot.parallax.core.shared.utils.ImageUtils;
import thothbot.parallax.core.shared.utils.SceneUtils;
import thothbot.parallax.demo.client.ContentWidget;
import thothbot.parallax.demo.client.Demo;
import thothbot.parallax.demo.client.DemoAnnotations.DemoSource;

import com.google.gwt.core.client.GWT;
import com.google.gwt.core.client.RunAsyncCallback;
import com.google.gwt.resources.client.ClientBundle;
import com.google.gwt.resources.client.ImageResource;
import com.google.gwt.user.client.rpc.AsyncCallback;

public class Geometries extends ContentWidget
{

	/*
	 * Load texture
	 */
	@DemoSource
	public interface Resources extends ClientBundle
	{
		Resources INSTANCE = GWT.create(Resources.class);

		@Source("../../resources/textures/ash_uvgrid01.jpg")
		ImageResource texture();
	}

	/*
	 * Prepare Rendering Scene
	 */
	@DemoSource
	class DemoScene extends DemoAnimatedScene 
	{
		@Override
		protected void loadCamera()
		{
			setCamera(
					new PerspectiveCamera( 45,
							getRenderer().getCanvas().getAspectRation(), 
							1, 
							2000 
					)); 
		}

		@Override
		protected void onStart()
		{
			getCamera().getPosition().setY(400);
			getScene().addChild(getCamera());
			
			getScene().addChild( new AmbientLight( 0x404040 ) );
	
			DirectionalLight light = new DirectionalLight( 0xffffff );
			light.getPosition().set( 0, 1f, 0 );
			getScene().addChild( light );
			
			List<Material> materials = new ArrayList<Material>();
			MeshLambertMaterial lmaterial = new MeshLambertMaterial();
			lmaterial.setMap( ImageUtils.loadTexture(Resources.INSTANCE.texture()) );
			lmaterial.setAmbient( new Color3f(0xbbbbbb) );
			materials.add(lmaterial);	
			
			MeshBasicMaterial bmaterial = new MeshBasicMaterial();
			bmaterial.setColor( new Color3f(0xffffff) );
			bmaterial.setWireframe(true);
			bmaterial.setTransparent(true);
			bmaterial.setOpacity( 0.1f );
			materials.add(bmaterial);
	
			DimensionalObject object1 = SceneUtils.createMultiMaterialObject( new Cube( 100, 100, 100, 4, 4, 4 ), materials );
			object1.getPosition().set( -200, 0, 400 );
			getScene().addChild( object1 );
			
			DimensionalObject object2 = SceneUtils.createMultiMaterialObject( new Cylinder( 25, 75, 100, 40, 5 ), materials );
			object2.getPosition().set( 0, 0, 400 );
			getScene().addChild( object2 );
			
			DimensionalObject object3 = SceneUtils.createMultiMaterialObject( new Icosahedron( 75, 1 ), materials );
			object3.getPosition().set( -200, 0, 200 );
			getScene().addChild( object3 );
			
			DimensionalObject object4 = SceneUtils.createMultiMaterialObject( new Octahedron( 75, 2 ), materials );
			object4.getPosition().set( 0, 0, 200 );
			getScene().addChild( object4 );
			
			DimensionalObject object5 = SceneUtils.createMultiMaterialObject( new Tetrahedron( 75, 0 ), materials );
			object5.getPosition().set( 200, 0, 200 );
			getScene().addChild( object5 );
			
			DimensionalObject object6 = SceneUtils.createMultiMaterialObject( new Plane( 100, 100, 4, 4 ), materials );
			Mesh Meshobject6 = (Mesh) object6.getChildren().get(0);
			Meshobject6.setDoubleSided(true);
			object6.getPosition().set( -200, 0, 0 );
			getScene().addChild( object6 );
			
			DimensionalObject object7 = SceneUtils.createMultiMaterialObject( new Sphere( 75, 20, 10 ), materials );
			object7.getPosition().set( 0, 0, 0 );
			getScene().addChild( object7 );
		
			List<Vector3f> points = new ArrayList<Vector3f>();
	
			for ( int i = 0; i < 50; i ++ )
				points.add( new Vector3f( (float) (Math.sin( i * 0.2 ) * 15f + 50f), 0f, (float)(( i - 5f ) * 2f )) );
	
			DimensionalObject object8 = SceneUtils.createMultiMaterialObject( new Lathe( points, 20 ), materials );
			Mesh Meshobject8 = (Mesh) object8.getChildren().get(0);
			Meshobject8.setDoubleSided(true);
			object8.getPosition().set( 200, 0, 0 );
			getScene().addChild( object8 );
			
			DimensionalObject object9 = SceneUtils.createMultiMaterialObject( new Torus( 50, 20, 20, 20 ), materials );
			object9.getPosition().set( -200, 0, -200 );
			getScene().addChild( object9 );

			DimensionalObject object10 = SceneUtils.createMultiMaterialObject( new TorusKnot( 50, 10, 50, 20 ), materials );
			object10.getPosition().set( 0, 0, -200 );
			getScene().addChild( object10 );
	
			AxisHelper object11 = new AxisHelper();
			object11.getPosition().set( 200, 0, -200 );
			object11.getScale().set(0.5f);
			getScene().addChild( object11 );
			
			ArrowHelper object12 = new ArrowHelper( new Vector3f( 0f, 1f, 0f ), new Vector3f( 0f, 0f, 0f ), 50 );
			object12.getPosition().set( 300, 0, 300 );
			getScene().addChild( object12 );
		}
		
		@Override
		protected void onStop()
		{			
		}
		
		@Override
		protected void onUpdate(double duration)
		{
			getCamera().getPosition().setX((float) (Math.cos( duration * 0.0001 ) * 800.0f));
			getCamera().getPosition().setZ((float) (Math.sin( duration * 0.0001 ) * 800.0f));

			getCamera().lookAt( getScene().getPosition() );

			for ( int i = 0, l = getScene().getChildren().size(); i < l; i ++ ) 
			{
				DimensionalObject object = getScene().getChildren().get( i );

				object.getRotation().addX(0.01f);
				object.getRotation().addY(0.005f);
			}
		}
	}
	
	RenderingPanel renderingPanel;

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
