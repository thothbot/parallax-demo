/*
 * Copyright 2012 Alex Usachev, thothbot@gmail.com
 * 
 * This file based on the JavaScript source file of the THREE.JS project, 
 * licensed under MIT License.
 * 
 * This file is part of Squirrel project.
 * 
 * Squirrel is free software: you can redistribute it and/or modify it 
 * under the terms of the GNU General Public License as published by the 
 * Free Software Foundation, either version 3 of the License, or (at your 
 * option) any later version.
 * 
 * Squirrel is distributed in the hope that it will be useful, but 
 * WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY 
 * or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License 
 * for more details.
 * 
 * You should have received a copy of the GNU General Public License along with 
 * Squirrel. If not, see http://www.gnu.org/licenses/.
 */

package thothbot.squirrel.demo.client.content;

import thothbot.squirrel.demo.client.ContentWidget;
import thothbot.squirrel.demo.client.Demo;
import thothbot.squirrel.demo.client.DemoAnnotations.DemoSource;

import thothbot.squirrel.lib.shared.Log;
import thothbot.squirrel.lib.shared.cameras.PerspectiveCamera;
import thothbot.squirrel.lib.shared.geometries.Cube;
import thothbot.squirrel.lib.shared.materials.MeshBasicMaterial;
import thothbot.squirrel.lib.shared.objects.Mesh;
import thothbot.squirrel.lib.shared.utils.ImageUtils;
import com.google.gwt.core.client.GWT;
import com.google.gwt.core.client.RunAsyncCallback;
import com.google.gwt.resources.client.ClientBundle;
import com.google.gwt.resources.client.ImageResource;
import com.google.gwt.user.client.rpc.AsyncCallback;

public class GeometryCube extends ContentWidget
{
	/*
	 * Load texture
	 */
	@DemoSource
	public interface Resources extends ClientBundle
	{
		Resources INSTANCE = GWT.create(Resources.class);

		@Source("../../resources/textures/crate.gif")
		ImageResource texture();
	}

	/*
	 * Prepare Rendering Scene
	 */
	@DemoSource
	class DemoScene extends DemoRenderingScene 
	{
		private Mesh mesh;

		@Override
		protected void loadCamera()
		{
			setCamera(
					new PerspectiveCamera(
							70, // fov
							getRenderer().getCanvas().getAspectRation(), // aspect 
							1, // near
							1000 // far 
					)); 
		}

		@Override
		protected void onStart()
		{
			getCamera().getPosition().setZ(400);
			getScene().addChild(getCamera());

			Cube geometry = new Cube( 200, 200, 200 );

			MeshBasicMaterial.MeshBasicMaterialOptions options = new MeshBasicMaterial.MeshBasicMaterialOptions();
			options.map = ImageUtils.loadTexture(Resources.INSTANCE.texture(), null, null);
			MeshBasicMaterial material = new MeshBasicMaterial(options);

			this.mesh = new Mesh(geometry, material);
			getScene().addChild(mesh);
		}
		
		@Override
		protected void onStop()
		{			
		}
		
		@Override
		protected void onUpdate(double duration)
		{
			this.mesh.getRotation().setX(this.mesh.getRotation().getX() + 0.005f);
			this.mesh.getRotation().setY(this.mesh.getRotation().getY() + 0.01f);

			super.onUpdate(duration);
		}
	}
		
	public GeometryCube() 
	{
		super("Cube and texture", "Here are used cube geometry and mesh basic material with simple texture. This example bases on the three.js example.");
	}
	
	@Override
	public DemoScene onInitialize()
	{
		Log.debug("Called onInitialize() class=" + this.getClass().getName());

		return new DemoScene();
	}

	@Override
	public ImageResource getIcon()
	{
		return Demo.resources.example_cube();
	}
	
	@Override
	protected void asyncOnInitialize(final AsyncCallback<DemoRenderingScene> callback)
	{
		GWT.runAsync(GeometryCube.class, new RunAsyncCallback() 
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
