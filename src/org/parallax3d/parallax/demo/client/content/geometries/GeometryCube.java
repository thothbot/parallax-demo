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

package org.parallax3d.parallax.demo.client.content.geometries;

import org.parallax3d.parallax.core.shared.cameras.PerspectiveCamera;
import org.parallax3d.parallax.demo.client.ContentWidget;
import org.parallax3d.parallax.demo.client.DemoAnnotations;
import org.parallax3d.parallax.core.client.textures.Texture;
import org.parallax3d.parallax.core.shared.geometries.BoxGeometry;
import org.parallax3d.parallax.core.shared.materials.MeshBasicMaterial;
import org.parallax3d.parallax.core.shared.objects.Mesh;

import com.google.gwt.core.client.GWT;
import com.google.gwt.core.client.RunAsyncCallback;
import com.google.gwt.user.client.rpc.AsyncCallback;

public class GeometryCube extends ContentWidget
{
	/*
	 * Prepare Rendering Scene
	 */
	@DemoAnnotations.DemoSource
	class DemoScene extends DemoAnimatedScene 
	{
		private static final String texture = "./static/textures/crate.gif";
		PerspectiveCamera camera;
		
		private Mesh mesh;

		@Override
		protected void onStart()
		{
			camera = new PerspectiveCamera(
					70, // fov
					getRenderer().getAbsoluteAspectRation(), // aspect 
					1, // near
					1000 // far 
			);
			camera.getPosition().setZ(400);

			BoxGeometry geometry = new BoxGeometry( 200, 200, 200 );

			MeshBasicMaterial material = new MeshBasicMaterial();
			material.setMap( new Texture(texture) );

			this.mesh = new Mesh(geometry, material);
			getScene().add(mesh);			
		}
		
		@Override
		protected void onUpdate(double duration)
		{
			this.mesh.getRotation().addX(0.005);
			this.mesh.getRotation().addY(0.01);

			getRenderer().render(getScene(), camera);
		}
	}
		
	public GeometryCube() 
	{
		super("Cube and texture", "Here are used cube geometry and mesh basic material with simple texture. This example based on the three.js example.");
	}
	
	@Override
	public DemoScene onInitialize()
	{
		return new DemoScene();
	}
	
	@Override
	protected void asyncOnInitialize(final AsyncCallback<DemoAnimatedScene> callback)
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
