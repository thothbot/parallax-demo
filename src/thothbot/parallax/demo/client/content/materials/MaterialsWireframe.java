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

package thothbot.parallax.demo.client.content.materials;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import thothbot.parallax.core.client.shaders.Attribute;
import thothbot.parallax.core.client.shaders.Shader;
import thothbot.parallax.core.shared.Log;
import thothbot.parallax.core.shared.cameras.PerspectiveCamera;
import thothbot.parallax.core.shared.core.Face3;
import thothbot.parallax.core.shared.core.Geometry;
import thothbot.parallax.core.shared.core.Vector4;
import thothbot.parallax.core.shared.geometries.CubeGeometry;
import thothbot.parallax.core.shared.geometries.SphereGeometry;
import thothbot.parallax.core.shared.materials.MeshBasicMaterial;
import thothbot.parallax.core.shared.materials.ShaderMaterial;
import thothbot.parallax.core.shared.objects.Mesh;
import thothbot.parallax.core.shared.utils.GeometryUtils;
import thothbot.parallax.demo.client.ContentWidget;
import thothbot.parallax.demo.client.Demo;
import thothbot.parallax.demo.client.DemoAnnotations.DemoSource;

import com.google.gwt.core.client.GWT;
import com.google.gwt.core.client.RunAsyncCallback;
import com.google.gwt.resources.client.ImageResource;
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
		Mesh meshLines;
		Mesh meshQuads;
		Mesh meshTris;
		Mesh meshMixed;
		
		@Override
		protected void loadCamera()
		{
			setCamera(
					new PerspectiveCamera(
							40, // fov
							getRenderer().getCanvas().getAspectRation(), // aspect 
							1, // near
							2000 // far 
					)); 
		}

		@Override
		protected void onStart()
		{
			getCamera().getPosition().setZ(800);
			getScene().add(getCamera());

			double size = 150;

			CubeGeometry geometryLines = new CubeGeometry( size, size, size );
			CubeGeometry geometryQuads = new CubeGeometry( size, size, size );
			CubeGeometry geometryTris = new CubeGeometry( size, size, size );

			GeometryUtils.triangulateQuads( geometryTris );

			// wireframe using gl.LINES

			MeshBasicMaterial materialLines = new MeshBasicMaterial();
			materialLines.setWireframe(true);

			meshLines = new Mesh( geometryLines, materialLines );
			meshLines.getPosition().setX(0);
			getScene().add( meshLines );

			// wireframe using gl.TRIANGLES (interpreted as quads)

			Attribute attributesQuads = new Attribute(Attribute.TYPE.V4, setupAttributes( geometryQuads ));
			attributesQuads.setBoundTo( Attribute.BOUND_TO.FACE_VERTICES );

			ShaderMaterial materialQuads = new ShaderMaterial( Resources.INSTANCE );
			materialQuads.getShader().addAttributes("center", attributesQuads);

			meshQuads = new Mesh( geometryQuads, materialQuads );
			meshQuads.getPosition().setX(300);
			getScene().add( meshQuads );

			// wireframe using gl.TRIANGLES (interpreted as triangles)

			Attribute attributesTris = new Attribute(Attribute.TYPE.V4, setupAttributes( geometryTris ));
			attributesTris.setBoundTo( Attribute.BOUND_TO.FACE_VERTICES );
			
			ShaderMaterial materialTris = new ShaderMaterial( Resources.INSTANCE );
			materialTris.getShader().addAttributes("center", attributesTris);

			meshTris = new Mesh( geometryTris, materialTris );
			meshTris.getPosition().setX(-300);
			getScene().add( meshTris );

			// wireframe using gl.TRIANGLES (mixed triangles and quads)

			SphereGeometry mixedGeometry = new SphereGeometry( size / 2.0, 32, 16 );

			Attribute attributesMixed = new Attribute(Attribute.TYPE.V4, setupAttributes( mixedGeometry ));
			attributesMixed.setBoundTo( Attribute.BOUND_TO.FACE_VERTICES );

			ShaderMaterial materialMixed = new ShaderMaterial( Resources.INSTANCE );
			materialMixed.getShader().addAttributes("center", attributesMixed);

			meshMixed = new Mesh( mixedGeometry, materialMixed );
			meshMixed.getPosition().setX(0);
			getScene().add( meshMixed );

		}
		
		private List<List<Vector4>> setupAttributes( Geometry geometry) 
		{
			List<List<Vector4>> values = new ArrayList<List<Vector4>>();
			
			for( int f = 0; f < geometry.getFaces().size(); f ++ ) 
			{
				Face3 face = geometry.getFaces().get( f );

				if ( face.getClass() == Face3.class ) 
				{
					values.add(f, Arrays.asList(
							new Vector4( 1, 0, 0, 0 ), 
							new Vector4( 0, 1, 0, 0 ), 
							new Vector4( 0, 0, 1, 0 ) ));
				} 
				else 
				{
					values.add(f, Arrays.asList( 
							new Vector4( 1, 0, 0, 1 ), 
							new Vector4( 1, 1, 0, 1 ), 
							new Vector4( 0, 1, 0, 1 ), 
							new Vector4( 0, 0, 0, 1 ) ));
				}
			}

			return values;
		}
		
		@Override
		protected void onUpdate(double duration)
		{
			meshLines.getRotation().addX(0.005);
			meshLines.getRotation().addY(0.01);

			meshQuads.getRotation().addX(0.005);
			meshQuads.getRotation().addY(0.01);

			meshTris.getRotation().addX(0.005);
			meshTris.getRotation().addY(0.01);

			if ( meshMixed != null) 
			{
				meshMixed.getRotation().addX(0.005);
				meshMixed.getRotation().addY(0.01);
			}
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
	public ImageResource getIcon()
	{
		return Demo.resources.exampleMaterialsWireframe();
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
