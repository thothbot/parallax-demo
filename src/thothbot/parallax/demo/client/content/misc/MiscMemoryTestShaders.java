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

package thothbot.parallax.demo.client.content.misc;

import java.util.ArrayList;
import java.util.List;

import thothbot.parallax.core.client.shaders.Shader;
import thothbot.parallax.core.shared.cameras.PerspectiveCamera;
import thothbot.parallax.core.shared.geometries.SphereGeometry;
import thothbot.parallax.core.shared.materials.ShaderMaterial;
import thothbot.parallax.core.shared.objects.Mesh;
import thothbot.parallax.demo.client.ContentWidget;
import thothbot.parallax.demo.client.DemoAnnotations.DemoSource;

import com.google.gwt.core.client.GWT;
import com.google.gwt.core.client.RunAsyncCallback;
import com.google.gwt.resources.client.TextResource;
import com.google.gwt.user.client.rpc.AsyncCallback;

public final class MiscMemoryTestShaders extends ContentWidget
{
	/*
	 * Load shaders
	 */
	@DemoSource
	public interface Resources extends Shader.DefaultResources
	{
		Resources INSTANCE = GWT.create(Resources.class);

		@Source("../../../resources/shaders/misc_memory_test.fs")
		TextResource getFragmentShader();
		
		@Source("../../../resources/shaders/misc_memory_test.vs")
		TextResource getVertexShader();
	}

	/*
	 * Prepare Rendering Scene
	 */
	@DemoSource
	class DemoScene extends DemoAnimatedScene 
	{
		private static final int N = 100;
		
		PerspectiveCamera camera;
		List<Mesh> meshes;

		@Override
		protected void onStart()
		{
			camera = new PerspectiveCamera(
					40, // fov
					getRenderer().getAbsoluteAspectRation(), // aspect 
					1, // near
					10000 // far 
			); 
			
			camera.getPosition().setZ(2000);

			SphereGeometry geometry = new SphereGeometry( 15, 64, 32 );
			meshes = new ArrayList<Mesh>();

			for ( int i = 0; i < N; i ++ ) 
			{
				ShaderMaterial material = new ShaderMaterial(Resources.INSTANCE);
				generateFragmentShader(material.getShader());

				Mesh mesh = new Mesh( geometry, material );

				mesh.getPosition().setX( ( 0.5 - Math.random() ) * 1000 );
				mesh.getPosition().setY( ( 0.5 - Math.random() ) * 1000 );
				mesh.getPosition().setZ( ( 0.5 - Math.random() ) * 1000 );

				getScene().add( mesh );

				meshes.add( mesh );
			}

			getRenderer().setClearColor(0xeeeeee);
		}

		private void generateFragmentShader(Shader shader) 
		{
			String vector = Math.random() + "," + Math.random() + "," + Math.random();
			shader.setFragmentSource(Shader.updateShaderSource(shader.getFragmentSource(), vector));
		}
		
		@Override
		protected void onUpdate(double duration)
		{
			for ( int i = 0; i < N; i ++ ) 
			{
				Mesh mesh = meshes.get( i );
				 mesh.getMaterial().deallocate( getRenderer() );
			}
			
			for ( int i = 0; i < N; i ++ ) 
			{
				Mesh mesh = meshes.get( i );
				ShaderMaterial material = new ShaderMaterial(Resources.INSTANCE);
				generateFragmentShader(material.getShader());
				mesh.setMaterial(material);
			}
			
			getRenderer().render(getScene(), camera);
		}
	}
		
	public MiscMemoryTestShaders() 
	{
		super("Memory test: shaders", "This example based on the three.js example.");
	}
	
	@Override
	public DemoScene onInitialize()
	{
		return new DemoScene();
	}
	
	@Override
	protected void asyncOnInitialize(final AsyncCallback<DemoAnimatedScene> callback)
	{
		GWT.runAsync(MiscMemoryTestShaders.class, new RunAsyncCallback() 
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
