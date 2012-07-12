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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import thothbot.squirrel.core.client.RenderingPanel;
import thothbot.squirrel.core.client.shader.Uniform;
import thothbot.squirrel.core.shared.cameras.PerspectiveCamera;
import thothbot.squirrel.core.shared.core.Color3f;
import thothbot.squirrel.core.shared.core.Geometry;
import thothbot.squirrel.core.shared.core.Vector3f;
import thothbot.squirrel.core.shared.core.WebGLCustomAttribute;
import thothbot.squirrel.core.shared.materials.Material;
import thothbot.squirrel.core.shared.materials.ShaderMaterial;
import thothbot.squirrel.core.shared.objects.ParticleSystem;
import thothbot.squirrel.core.shared.utils.ImageUtils;
import thothbot.squirrel.demo.client.ContentWidget;
import thothbot.squirrel.demo.client.Demo;
import thothbot.squirrel.demo.client.DemoAnnotations.DemoSource;

import com.google.gwt.core.client.GWT;
import com.google.gwt.core.client.RunAsyncCallback;
import com.google.gwt.resources.client.ClientBundle;
import com.google.gwt.resources.client.ImageResource;
import com.google.gwt.resources.client.TextResource;
import com.google.gwt.user.client.rpc.AsyncCallback;

public class CustomAttributesParticles extends ContentWidget
{
	/*
	 * Load texture
	 */
	@DemoSource
	public interface Resources extends ClientBundle
	{
		Resources INSTANCE = GWT.create(Resources.class);

		@Source("../../resources/shaders/custom_attributes_particles.fs")
		TextResource fragmetShader();
		
		@Source("../../resources/shaders/custom_attributes_particles.vs")
		TextResource vertexShader();
		
		@Source("../../resources/textures/sprites/spark1.png")
		ImageResource texture();
	}

	/*
	 * Prepare Rendering Scene
	 */
	@SuppressWarnings("unchecked")
	@DemoSource
	class DemoScene extends DemoRenderingScene 
	{	
		Map<String, WebGLCustomAttribute> attributes;
		ParticleSystem sphere;

		@Override
		protected void loadCamera()
		{
			setCamera(
					new PerspectiveCamera( 40,
							getRenderer().getCanvas().getAspectRation(), 
							1, 
							10000 
					)); 
		}

		@Override
		protected void onStart()
		{
			getCamera().getPosition().setZ(300);
			getScene().addChild(getCamera());

			this.attributes = new HashMap<String, WebGLCustomAttribute>();
			this.attributes.put("size", new WebGLCustomAttribute(WebGLCustomAttribute.TYPE.F, new ArrayList<Integer>()));
			this.attributes.put("customColor", new WebGLCustomAttribute(WebGLCustomAttribute.TYPE.C, new ArrayList<Color3f>()));
	
			Map <String, Uniform> uniforms = new HashMap<String, Uniform>();
			uniforms.put("amplitude", new Uniform(Uniform.TYPE.F, 1.0f));
			uniforms.put("color", new Uniform(Uniform.TYPE.C, new Color3f( 0xffffff )));
			uniforms.put("texture", new Uniform(Uniform.TYPE.T, 0, ImageUtils.loadTexture( Resources.INSTANCE.texture(), null, null )));
			
			ShaderMaterial.ShaderMaterialOptions opt = new ShaderMaterial.ShaderMaterialOptions();
			opt.uniforms = uniforms;
			opt.attributes = attributes;
			opt.vertexShader =  Resources.INSTANCE.vertexShader().getText();
			opt.fragmentShader = Resources.INSTANCE.fragmetShader().getText();
			opt.blending = Material.BLENDING.ADDITIVE;
			opt.depthTest = false;
			opt.transparent = true;
			
			ShaderMaterial shaderMaterial = new ShaderMaterial(opt);
			
			float radius = 200f;
			Geometry geometry = new Geometry();
	
			for ( int i = 0; i < 10000; i++ ) 
			{
				Vector3f vertex = new Vector3f();
				vertex.setX((float) (Math.random() * 2.0 - 1.0));
				vertex.setY((float) (Math.random() * 2.0 - 1.0));
				vertex.setZ((float) (Math.random() * 2.0 - 1.0));
				vertex.multiply( radius );
	
				geometry.getVertices().add( vertex );
			}
	
			this.sphere = new ParticleSystem( geometry, shaderMaterial );
			this.sphere.setDynamic(true);
			
			List<Vector3f> vertices = sphere.getGeometry().getVertices();
			List<Float> values_size = (List<Float>) attributes.get("size").getValue();
			List<Color3f> values_color = (List<Color3f>) attributes.get("customColor").getValue();
	
			for( int v = 0; v < vertices.size(); v++ ) 
			{
				values_size.add( v, 10f);
				values_color.add( v, new Color3f( 0xffaa00 ));
	
				if ( vertices.get( v ).getX() < 0 )
					values_color.get( v ).setHSV( 0.5f + 0.1f * ( v / (float)vertices.size() ), 0.7f, 0.9f );
				else
					values_color.get( v ).setHSV( 0.0f + 0.1f * ( v / (float)vertices.size() ), 0.9f, 0.9f );
			}
	
			getScene().addChild( sphere );

		}
		
		@Override
		protected void onStop()
		{			
		}
		
		@Override
		protected void onUpdate(double duration)
		{
			double time = duration * 0.005;

			this.sphere.getRotation().setZ((float) (0.01 * time));

			for( int i = 0; i < this.attributes.get("size").getValue().size(); i++ ) 
			{
				List<Float> value = (List<Float>) this.attributes.get("size").getValue(); 
				value.set( i, (float)(14f + 13f * Math.sin( 0.1f * i + time )));
			}

			this.attributes.get("size").needsUpdate = true;
			super.onUpdate(duration);
		}
	}
	
	RenderingPanel renderingPanel;

	public CustomAttributesParticles() 
	{
		super("Particles", "Here are used custom shaders and sprites. This example bases on the three.js example.");
	}

	@Override
	public DemoScene onInitialize()
	{
		return new DemoScene();
	}
	
	@Override
	public ImageResource getIcon()
	{
		return Demo.resources.exampleCustomAttributesParticles();
	}
	
	@Override
	protected void asyncOnInitialize(final AsyncCallback<DemoRenderingScene> callback)
	{
		GWT.runAsync(CustomAttributesParticles.class, new RunAsyncCallback() 
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
