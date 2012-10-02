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

package thothbot.parallax.demo.client.content.misc;

import thothbot.parallax.core.client.textures.Texture;
import thothbot.parallax.core.shared.cameras.PerspectiveCamera;
import thothbot.parallax.core.shared.geometries.SphereGeometry;
import thothbot.parallax.core.shared.materials.MeshBasicMaterial;
import thothbot.parallax.core.shared.objects.Mesh;
import thothbot.parallax.demo.client.ContentWidget;
import thothbot.parallax.demo.client.Demo;
import thothbot.parallax.demo.client.DemoAnnotations.DemoSource;

import com.google.gwt.canvas.dom.client.Context2d;
import com.google.gwt.canvas.dom.client.ImageData;
import com.google.gwt.core.client.GWT;
import com.google.gwt.core.client.RunAsyncCallback;
import com.google.gwt.dom.client.CanvasElement;
import com.google.gwt.dom.client.Document;
import com.google.gwt.resources.client.ImageResource;
import com.google.gwt.user.client.rpc.AsyncCallback;

public final class MiscMemoryTestGeometries extends ContentWidget 
{

	
	/*
	 * Prepare Rendering Scene
	 */
	@DemoSource
	class DemoScene extends DemoAnimatedScene 
	{

		PerspectiveCamera camera;
		Mesh mesh;
		Texture texture;
		
		@Override
		protected void onStart()
		{
			camera = new PerspectiveCamera(
					60, // fov
					getRenderer().getCanvas().getAspectRation(), // aspect 
					1, // near
					10000 // far 
			); 
			
			camera.getPosition().setZ(200);
		}
		
		@Override
		protected void onUpdate(double duration)
		{
			if(mesh != null)
			{
				getScene().remove( mesh );
				getRenderer().deallocateObject( mesh );
			}

			if(texture != null)
				getRenderer().deallocateTexture( texture );
			
			SphereGeometry geometry = new SphereGeometry( 50, (int)(Math.random() * 64), (int)(Math.random() * 32) );

			texture = new Texture( generateTexture() );
			texture.setNeedsUpdate(true);

			MeshBasicMaterial material = new MeshBasicMaterial();
			material.setMap(texture);
			material.setWireframe(true);

			mesh = new Mesh( geometry, material );

			getScene().add( mesh );
			
			getRenderer().render(getScene(), camera);
		}
		
		private CanvasElement generateTexture() 
		{
			CanvasElement canvas = Document.get().createElement("canvas").cast();
			canvas.setWidth(256);
			canvas.setHeight(256);

			Context2d context = canvas.getContext2d();
			context.setFillStyle("rgb(" + Math.floor( Math.random() * 256 ) 
					+ "," + Math.floor( Math.random() * 256 ) 
					+ "," + Math.floor( Math.random() * 256 ) + ")");
			context.fillRect( 0, 0, 256, 256 );

			return canvas;
		}
	}
		
	public MiscMemoryTestGeometries() 
	{
		super("Memory test: geometries", "This example based on the three.js example.");
	}
	
	@Override
	public DemoScene onInitialize()
	{
		return new DemoScene();
	}

	@Override
	public ImageResource getIcon()
	{
		return Demo.resources.exampleMiscMemoryTestGeometry();
	}
	
	@Override
	protected void asyncOnInitialize(final AsyncCallback<DemoAnimatedScene> callback)
	{
		GWT.runAsync(MiscMemoryTestGeometries.class, new RunAsyncCallback() 
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
