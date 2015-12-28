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

package org.parallax3d.parallax.demo.content.materials;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import thothbot.parallax.core.client.shaders.Attribute;
import thothbot.parallax.core.client.shaders.Shader;
import thothbot.parallax.core.shared.cameras.PerspectiveCamera;
import thothbot.parallax.core.shared.core.Face3;
import thothbot.parallax.core.shared.core.Geometry;
import thothbot.parallax.core.shared.geometries.BoxGeometry;
import thothbot.parallax.core.shared.geometries.SphereGeometry;
import thothbot.parallax.core.shared.materials.MeshBasicMaterial;
import thothbot.parallax.core.shared.materials.ShaderMaterial;
import thothbot.parallax.core.shared.math.Vector3;
import thothbot.parallax.core.shared.objects.Mesh;
import org.parallax3d.parallax.demo.client.ContentWidget;
import org.parallax3d.parallax.demo.client.DemoAnnotations.DemoSource;

import com.google.gwt.core.client.GWT;
import com.google.gwt.core.client.RunAsyncCallback;
import com.google.gwt.resources.client.TextResource;
import com.google.gwt.user.client.rpc.AsyncCallback;

public final class MaterialsWireframe extends ContentWidget 
{

	/*
	 * Load shaders
	 */
	@DemoSource
	public interface Resources extends Shader.DefaultResources
	{
		Resources INSTANCE = GWT.create(Resources.class);

		@Source("../../../resources/shaders/materials_wireframe.fs")
		TextResource getFragmentShader();
		
		@Source("../../../resources/shaders/materials_wireframe.vs")
		TextResource getVertexShader();
	}

	/*
	 * Prepare Rendering Scene
	 */
	@DemoSource
	class DemoScene extends DemoAnimatedScene 
	{
		PerspectiveCamera camera;
		
		Mesh meshLines;
		Mesh meshTris;
		Mesh meshMixed;

		@Override
		protected void onStart()
		{
			camera = new PerspectiveCamera(
					40, // fov
					getRenderer().getAbsoluteAspectRation(), // aspect 
					1, // near
					2000 // far 
			);
			
			camera.getPosition().setZ(800);
			
			double size = 150;

			BoxGeometry geometryLines = new BoxGeometry( size, size, size );
			BoxGeometry geometryTris = new BoxGeometry( size, size, size );

			// wireframe using gl.LINES

			MeshBasicMaterial materialLines = new MeshBasicMaterial();
			materialLines.setWireframe(true);

			meshLines = new Mesh( geometryLines, materialLines );
			meshLines.getPosition().setX(-150);
			getScene().add( meshLines );

			// wireframe using gl.TRIANGLES (interpreted as triangles)

			Attribute attributesTris = new Attribute(Attribute.TYPE.V3, setupAttributes( geometryTris ));
			attributesTris.setBoundTo( Attribute.BOUND_TO.FACE_VERTICES );
			
			ShaderMaterial materialTris = new ShaderMaterial( Resources.INSTANCE );
			materialTris.getShader().addAttributes("center", attributesTris);

			meshTris = new Mesh( geometryTris, materialTris );
			meshTris.getPosition().setX(150);
			getScene().add( meshTris );

			// wireframe using gl.TRIANGLES (mixed triangles and quads)

			SphereGeometry mixedGeometry = new SphereGeometry( size / 2.0, 32, 16 );

			Attribute attributesMixed = new Attribute(Attribute.TYPE.V3, setupAttributes( mixedGeometry ));
			attributesMixed.setBoundTo( Attribute.BOUND_TO.FACE_VERTICES );

			ShaderMaterial materialMixed = new ShaderMaterial( Resources.INSTANCE );
			materialMixed.getShader().addAttributes("center", attributesMixed);

			meshMixed = new Mesh( mixedGeometry, materialMixed );
			meshMixed.getPosition().setX(-150);
			getScene().add( meshMixed );

		}
		
		private List<List<Vector3>> setupAttributes( Geometry geometry) 
		{
			List<List<Vector3>> values = new ArrayList<List<Vector3>>();
			
			for( int f = 0; f < geometry.getFaces().size(); f ++ ) 
			{
				Face3 face = geometry.getFaces().get( f );
				values.add(f, Arrays.asList(
						new Vector3( 1, 0, 0 ), 
						new Vector3( 0, 1, 0 ), 
						new Vector3( 0, 0, 1 ) ));
			}

			return values;
		}
		
		@Override
		protected void onUpdate(double duration)
		{
			meshLines.getRotation().addX(0.005);
			meshLines.getRotation().addY(0.01);

			meshTris.getRotation().addX(0.005);
			meshTris.getRotation().addY(0.01);

			if ( meshMixed != null) 
			{
				meshMixed.getRotation().addX(0.005);
				meshMixed.getRotation().addY(0.01);
			}
			
			getRenderer().render(getScene(), camera);
		}
	}
		
	public MaterialsWireframe() 
	{
		super("Wireframe material", "This example based on the three.js example.");
	}
	
	@Override
	public DemoScene onInitialize()
	{
		return new DemoScene();
	}
	
	@Override
	protected void asyncOnInitialize(final AsyncCallback<DemoAnimatedScene> callback)
	{
		GWT.runAsync(MaterialsWireframe.class, new RunAsyncCallback() 
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
