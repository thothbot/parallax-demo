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

import org.parallax3d.parallax.graphics.cameras.PerspectiveCamera;
import org.parallax3d.parallax.graphics.core.Object3D;
import org.parallax3d.parallax.graphics.extras.SceneUtils;
import org.parallax3d.parallax.graphics.extras.geometries.parametric.KleinParametricGeometry;
import org.parallax3d.parallax.graphics.extras.geometries.parametric.MobiusParametricGeometry;
import org.parallax3d.parallax.graphics.extras.geometries.parametric.PlaneParametricGeometry;
import org.parallax3d.parallax.graphics.lights.AmbientLight;
import org.parallax3d.parallax.graphics.lights.DirectionalLight;
import org.parallax3d.parallax.graphics.materials.Material;
import org.parallax3d.parallax.graphics.materials.MeshBasicMaterial;
import org.parallax3d.parallax.graphics.materials.MeshLambertMaterial;
import org.parallax3d.parallax.graphics.textures.Texture;
import org.parallax3d.parallax.math.Color;
import org.parallax3d.parallax.system.gl.enums.TextureWrapMode;

import java.util.ArrayList;
import java.util.List;


public class GeometriesParametric extends ContentWidget
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
			light.getPosition().set( 0, 0, 1 );
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
			bmaterial.setWireframe( true );
			bmaterial.setTransparent(true);
			bmaterial.setOpacity( 0.1 );
			bmaterial.setSide(Material.SIDE.DOUBLE);
			materials.add(bmaterial);
			
			// KleinParametricGeometry Bottle
			Object3D object1 = SceneUtils.createMultiMaterialObject(new KleinParametricGeometry(20, 20), materials);
			object1.getPosition().set( 0, 0, 0 );
			object1.getScale().multiply(20);
			getScene().add( object1 );
			
			// MobiusParametricGeometry Strip
			Object3D object2 = SceneUtils.createMultiMaterialObject( new MobiusParametricGeometry(20, 20), materials );
			object2.getPosition().set( 10, 0, 0 );
			object2.getScale().multiply(100);
			getScene().add( object2 );
			
			Object3D object3 = SceneUtils.createMultiMaterialObject( new PlaneParametricGeometry(200, 200, 10, 20), materials );
			object3.getPosition().set( 20, 0, 0 );
			getScene().add( object3 );
			
//			DimensionalObject object4 = SceneUtils.createMultiMaterialObject( new Mobius3dParametricGeometry(20,20), materials );
//			object4.getPosition().set( 10, 0, 0 );
//			object4.getScale().multiply(100);
//			getScene().addChild( object4 );
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
	
	public GeometriesParametric()
	{
		super("Parametric geometry", "Here are show how to generate geometric objects by custom function. This example based on the three.js example.");
	}

	@Override
	public DemoScene onInitialize()
	{
		return new DemoScene();
	}
	
	@Override
	protected void asyncOnInitialize(final AsyncCallback<DemoAnimatedScene> callback)
	{
		GWT.runAsync(GeometriesParametric.class, new RunAsyncCallback() 
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
