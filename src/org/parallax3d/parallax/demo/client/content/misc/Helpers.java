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

package org.parallax3d.parallax.demo.client.content.misc;

import org.parallax3d.parallax.core.shared.cameras.PerspectiveCamera;
import org.parallax3d.parallax.core.shared.core.AbstractGeometry;
import org.parallax3d.parallax.core.shared.lights.PointLight;
import org.parallax3d.parallax.core.shared.materials.MeshLambertMaterial;
import org.parallax3d.parallax.core.shared.objects.Mesh;
import org.parallax3d.parallax.demo.client.ContentWidget;
import org.parallax3d.parallax.demo.client.DemoAnnotations;
import org.parallax3d.parallax.loader.shared.JsonLoader;
import org.parallax3d.parallax.core.shared.helpers.BoxHelper;
import org.parallax3d.parallax.core.shared.helpers.FaceNormalsHelper;
import org.parallax3d.parallax.core.shared.helpers.GridHelper;
import org.parallax3d.parallax.core.shared.helpers.PointLightHelper;
import org.parallax3d.parallax.core.shared.helpers.VertexNormalsHelper;
import org.parallax3d.parallax.core.shared.helpers.WireframeHelper;
import org.parallax3d.parallax.loader.shared.XHRLoader;

import com.google.gwt.core.client.GWT;
import com.google.gwt.core.client.RunAsyncCallback;
import com.google.gwt.user.client.rpc.AsyncCallback;

public final class Helpers extends ContentWidget
{
	/*
	 * Prepare Rendering Scene
	 */
	@DemoAnnotations.DemoSource
	class DemoScene extends DemoAnimatedScene 
	{

		private static final String model = "./static/models/obj/leeperrysmith/LeePerrySmith.js";
		
		PerspectiveCamera camera;
		PointLight light;
		
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
			
			light = new PointLight(0xffffff);
			light.getPosition().set( 200, 100, 150 );
			getScene().add( light );

			getScene().add( new PointLightHelper( light, 5.0 ) );

			GridHelper helper = new GridHelper( 200, 10 );
			helper.setColors( 0x0000ff, 0x808080 );
			helper.getPosition().setY( - 150 );
			getScene().add( helper );
			
			new JsonLoader(model, new XHRLoader.ModelLoadHandler() {

				@Override
				public void onModelLoaded(XHRLoader loader, AbstractGeometry geometry) {
					MeshLambertMaterial material = new MeshLambertMaterial();

					Mesh mesh = new Mesh( geometry, material );
					mesh.getScale().multiply( 50 );
					getScene().add( mesh );

					getScene().add( new FaceNormalsHelper( mesh, 10 ) );
					getScene().add( new VertexNormalsHelper( mesh, 10 ) );
					
					WireframeHelper helper = new WireframeHelper( mesh );
					helper.getMaterial().setDepthTest(false);
					helper.getMaterial().setOpacity( 0.25 );
					helper.getMaterial().setTransparent( true );
					getScene().add( helper );

					getScene().add( new BoxHelper( mesh ) );

				}
			});
			
		}
		
		@Override
		protected void onUpdate(double duration)
		{
			
			double time = duration * 0.0003;
			camera.getPosition().setX( 400 * Math.cos( time ) );
			camera.getPosition().setZ( 400 * Math.sin( time ) );
			camera.lookAt( getScene().getPosition() );

			light.getPosition().setX( Math.sin( time * 1.7 ) * 300 );
			light.getPosition().setY( Math.cos( time * 1.5 ) * 400 );
			light.getPosition().setZ( Math.cos( time * 1.3 ) * 300 );
			
			getRenderer().render(getScene(), camera);
		}
	}
		
	public Helpers() 
	{
		super("Helpers", "This example based on the three.js example.");
	}
		
	@Override
	public DemoScene onInitialize()
	{
		return new DemoScene();
	}
	
	@Override
	protected void asyncOnInitialize(final AsyncCallback<DemoAnimatedScene> callback)
	{
		GWT.runAsync(Helpers.class, new RunAsyncCallback() 
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
