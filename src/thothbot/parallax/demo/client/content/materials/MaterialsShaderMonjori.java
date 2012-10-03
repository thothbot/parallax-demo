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

import java.util.Map;

import thothbot.parallax.core.client.context.Canvas3d;
import thothbot.parallax.core.client.shaders.Uniform;
import thothbot.parallax.core.shared.cameras.Camera;
import thothbot.parallax.core.shared.core.Vector2;
import thothbot.parallax.core.shared.geometries.PlaneGeometry;
import thothbot.parallax.core.shared.materials.ShaderMaterial;
import thothbot.parallax.core.shared.objects.Mesh;
import thothbot.parallax.demo.client.ContentWidget;
import thothbot.parallax.demo.client.Demo;
import thothbot.parallax.demo.client.DemoAnnotations.DemoSource;
import thothbot.parallax.demo.resources.MonjoriShader;

import com.google.gwt.core.client.GWT;
import com.google.gwt.core.client.RunAsyncCallback;
import com.google.gwt.resources.client.ImageResource;
import com.google.gwt.user.client.rpc.AsyncCallback;

public final class MaterialsShaderMonjori extends ContentWidget 
{

	/*
	 * Prepare Rendering Scene
	 */
	@DemoSource
	class DemoScene extends DemoAnimatedScene 
	{

		Camera camera;
		
		Map<String, Uniform> uniforms;
		
		@Override
		protected void onResize() 
		{
			super.onResize();
			
			Canvas3d canvas = getRenderer().getCanvas();
			
			((Vector2)uniforms.get("resolution").getValue()).setX( canvas.getWidth() );
			((Vector2)uniforms.get("resolution").getValue()).setY( canvas.getHeight() );
		}

		@Override
		protected void onStart()
		{
			camera = new Camera(); 
			
			camera.getPosition().setZ(1);

			ShaderMaterial material = new ShaderMaterial(new MonjoriShader());
			uniforms = material.getShader().getUniforms();
			
			Mesh mesh = new Mesh( new PlaneGeometry( 2, 2 ), material );
			getScene().add( mesh );
		}
		
		@Override
		protected void onUpdate(double duration)
		{
			uniforms.get("time").setValue((Double)uniforms.get("time").getValue() + 0.05);
			getRenderer().render(getScene(), camera);
		}
	}
		
	public MaterialsShaderMonjori() 
	{
		super("Monjori shader", "This example based on the three.js example.");
	}
	
	@Override
	public DemoScene onInitialize()
	{
		return new DemoScene();
	}

	@Override
	public ImageResource getIcon()
	{
		return Demo.resources.exampleMaterialsShaderMonjori();
	}
	
	@Override
	protected void asyncOnInitialize(final AsyncCallback<DemoAnimatedScene> callback)
	{
		GWT.runAsync(MaterialsShaderMonjori.class, new RunAsyncCallback() 
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