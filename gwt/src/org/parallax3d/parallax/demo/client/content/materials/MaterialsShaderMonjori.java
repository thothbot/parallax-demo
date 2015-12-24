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

package org.parallax3d.parallax.demo.client.content.materials;

import java.util.Map;

import thothbot.parallax.core.client.events.HasEventBus;
import thothbot.parallax.core.client.events.ViewportResizeEvent;
import thothbot.parallax.core.client.events.ViewportResizeHandler;
import thothbot.parallax.core.client.shaders.Uniform;
import thothbot.parallax.core.shared.cameras.Camera;
import thothbot.parallax.core.shared.geometries.PlaneBufferGeometry;
import thothbot.parallax.core.shared.materials.ShaderMaterial;
import thothbot.parallax.core.shared.math.Vector2;
import thothbot.parallax.core.shared.objects.Mesh;
import org.parallax3d.parallax.demo.client.ContentWidget;
import org.parallax3d.parallax.demo.client.DemoAnnotations.DemoSource;
import org.parallax3d.parallax.demo.resources.MonjoriShader;

import com.google.gwt.core.client.GWT;
import com.google.gwt.core.client.RunAsyncCallback;
import com.google.gwt.user.client.rpc.AsyncCallback;

public final class MaterialsShaderMonjori extends ContentWidget 
{

	/*
	 * Prepare Rendering Scene
	 */
	@DemoSource
	class DemoScene extends DemoAnimatedScene implements HasEventBus, ViewportResizeHandler
	{

		Camera camera;
		
		Map<String, Uniform> uniforms;
		
		@Override
		public void onResize(ViewportResizeEvent event) 
		{		
			((Vector2)uniforms.get("resolution").getValue()).setX( event.getRenderer().getAbsoluteWidth() );
			((Vector2)uniforms.get("resolution").getValue()).setY( event.getRenderer().getAbsoluteHeight() );
		}

		@Override
		protected void onStart()
		{
			EVENT_BUS.addHandler(ViewportResizeEvent.TYPE, this);
			camera = new Camera(); 
			
			camera.getPosition().setZ(1);

			ShaderMaterial material = new ShaderMaterial(new MonjoriShader());
			uniforms = material.getShader().getUniforms();
			
			Mesh mesh = new Mesh( new PlaneBufferGeometry( 2, 2 ), material );
			getScene().add( mesh );
			
			((Vector2)uniforms.get("resolution").getValue()).setX( getRenderer().getAbsoluteWidth() );
			((Vector2)uniforms.get("resolution").getValue()).setY( getRenderer().getAbsoluteHeight() );
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
	protected boolean isEnabledEffectSwitch() {
		return false;
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
