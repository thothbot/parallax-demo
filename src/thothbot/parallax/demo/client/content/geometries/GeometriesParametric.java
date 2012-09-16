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

package thothbot.parallax.demo.client.content.geometries;

import java.util.ArrayList;
import java.util.List;

import thothbot.parallax.core.client.RenderingPanel;
import thothbot.parallax.core.client.gl2.enums.TextureWrapMode;
import thothbot.parallax.core.client.textures.Texture;
import thothbot.parallax.core.shared.cameras.PerspectiveCamera;
import thothbot.parallax.core.shared.core.Color;
import thothbot.parallax.core.shared.geometries.parametric.KleinParametricGeometry;
import thothbot.parallax.core.shared.geometries.parametric.MobiusParametricGeometry;
import thothbot.parallax.core.shared.geometries.parametric.Mobius3dParametricGeometry;
import thothbot.parallax.core.shared.geometries.parametric.PlaneParametricGeometry;
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
import com.google.gwt.resources.client.ImageResource;
import com.google.gwt.user.client.rpc.AsyncCallback;

public class GeometriesParametric extends ContentWidget
{
	/*
	 * Prepare Rendering Scene
	 */
	@DemoSource
	class DemoScene extends DemoAnimatedScene 
	{
		private static final String image = "./static/textures/ash_uvgrid01.jpg";
		
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
			getScene().add(getCamera());
			
			getScene().add( new AmbientLight( 0x404040 ) );
	
			DirectionalLight light = new DirectionalLight( 0xffffff );
			light.getPosition().set( 0, 0, 1 );
			getScene().add( light );
			
			Texture texture = ImageUtils.loadTexture(image);
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
			materials.add(bmaterial);
			
			// KleinParametricGeometry Bottle
			DimensionalObject object1 = SceneUtils.createMultiMaterialObject(new KleinParametricGeometry(20, 20), materials );
			object1.getPosition().set( 0, 0, 0 );
			object1.getScale().multiply(20);
			getScene().add( object1 );
			
			// MobiusParametricGeometry Strip
			DimensionalObject object2 = SceneUtils.createMultiMaterialObject( new MobiusParametricGeometry(20, 20), materials );
			object2.getPosition().set( 10, 0, 0 );
			object2.getScale().multiply(100);
			getScene().add( object2 );
			
			DimensionalObject object3 = SceneUtils.createMultiMaterialObject( new PlaneParametricGeometry(200, 200, 10, 20), materials );
			object3.getPosition().set( 20, 0, 0 );
			getScene().add( object3 );
			
//			DimensionalObject object4 = SceneUtils.createMultiMaterialObject( new Mobius3dParametricGeometry(20,20), materials );
//			object4.getPosition().set( 10, 0, 0 );
//			object4.getScale().multiply(100);
//			getScene().addChild( object4 );
		}
		
		@Override
		protected void onStop()
		{			
		}
		
		@Override
		protected void onUpdate(double duration)
		{
			getCamera().getPosition().setX(Math.cos( duration * 0.0001 ) * 800.0);
			getCamera().getPosition().setZ(Math.sin( duration * 0.0001 ) * 800.0);

			getCamera().lookAt( getScene().getPosition() );

			for ( int i = 0, l = getScene().getChildren().size(); i < l; i ++ ) 
			{
				DimensionalObject object = getScene().getChildren().get( i );

				object.getRotation().addX(0.01);
				object.getRotation().addY(0.005);
			}
		}
	}
	
	RenderingPanel renderingPanel;
	
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
	public ImageResource getIcon()
	{
		return Demo.resources.exampleGeometriesParametric();
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
