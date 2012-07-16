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
import thothbot.parallax.core.shared.geometries.parametric.Klein;
import thothbot.parallax.core.shared.geometries.parametric.Mobius;
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

public class GeometriesParametric extends ContentWidget
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
	class DemoScene extends DemoRenderingScene 
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
			light.getPosition().set( 0, 0, 1f );
			getScene().addChild( light );
			
			List<Material> materials = new ArrayList<Material>();
			MeshLambertMaterial.MeshLambertMaterialOptions lopt = new MeshLambertMaterial.MeshLambertMaterialOptions();
			lopt.map = ImageUtils.loadTexture(Resources.INSTANCE.texture(), null, null);
			lopt.ambient = new Color3f(0xbbbbbb);
			MeshLambertMaterial lmaterial = new MeshLambertMaterial(lopt);
	
			MeshBasicMaterial.MeshBasicMaterialOptions bopt = new MeshBasicMaterial.MeshBasicMaterialOptions();
			bopt.color = new Color3f(0xffffff);
			bopt.wireframe = true;
			bopt.transparent = true;
			bopt.opacity = 0.1f;
			MeshBasicMaterial bmaterial = new MeshBasicMaterial(bopt);
	
			materials.add(lmaterial);
			materials.add(bmaterial);
			
			// Klein Bottle
			DimensionalObject object1 = SceneUtils.createMultiMaterialObject(new Klein(20, 20), materials );
			Mesh Meshobject1 = (Mesh) object1.getChildren().get(0);
			Meshobject1.setDoubleSided(true);
			object1.getPosition().set( 0, 0, 0 );
			object1.getScale().multiply(20);
			getScene().addChild( object1 );
			
			// Mobius Strip
			DimensionalObject object2 = SceneUtils.createMultiMaterialObject( new Mobius(20,20), materials );
			object2.getPosition().set( 10, 0, 0 );
			object2.getScale().multiply(100);
			getScene().addChild( object2 );
			
//			DimensionalObject object3 = SceneUtils.createMultiMaterialObject( new com.alexusachev.lib.geometries.parametric.Plane(200, 200, 10,10), materials );
//			object3.getPosition().set( 10, 0, 0 );
//			object3.getScale().multiply(100);
//			scene.addChild( object3 );
//			
//			DimensionalObject object4 = SceneUtils.createMultiMaterialObject( new Mobius3d(20,20), materials );
//			object4.getPosition().set( 10, 0, 0 );
//			object4.getScale().multiply(100);
//			scene.addChild( object4 );
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

			super.onUpdate(duration);
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
	protected void asyncOnInitialize(final AsyncCallback<DemoRenderingScene> callback)
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
