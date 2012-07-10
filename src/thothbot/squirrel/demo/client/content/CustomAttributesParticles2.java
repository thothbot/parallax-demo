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
import thothbot.squirrel.core.shared.core.Vector3f;
import thothbot.squirrel.core.shared.core.WebGLCustomAttribute;
import thothbot.squirrel.core.shared.geometries.Cube;
import thothbot.squirrel.core.shared.geometries.Sphere;
import thothbot.squirrel.core.shared.materials.ShaderMaterial;
import thothbot.squirrel.core.shared.objects.ParticleSystem;
import thothbot.squirrel.core.shared.textures.Texture;
import thothbot.squirrel.core.shared.utils.GeometryUtils;
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

public class CustomAttributesParticles2 extends ContentWidget
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
		
		@Source("../../resources/textures/sprites/disc.png")
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
		int vc1;

		@Override
		protected void loadCamera()
		{
			setCamera(
				new PerspectiveCamera( 45,
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
			attributes.put("size", new WebGLCustomAttribute(WebGLCustomAttribute.TYPE.F, new ArrayList<Integer>()));
			attributes.put("customColor", new WebGLCustomAttribute(WebGLCustomAttribute.TYPE.C, new ArrayList<Color3f>()));
	
			Map <String, Uniform> uniforms = new HashMap<String, Uniform>();
			uniforms.put("amplitude", new Uniform(Uniform.TYPE.F, 1.0f));
			uniforms.put("color", new Uniform(Uniform.TYPE.C, new Color3f( 0xffffff )));
			uniforms.put("texture", new Uniform(Uniform.TYPE.T, 0, ImageUtils.loadTexture(Resources.INSTANCE.texture(), null, null )));
	
			uniforms.get("texture").texture.setWrapS(Texture.WRAPPING_MODE.REPEAT);
			uniforms.get("texture").texture.setWrapT(Texture.WRAPPING_MODE.REPEAT);
	
			ShaderMaterial.ShaderMaterialOptions opt = new ShaderMaterial.ShaderMaterialOptions();
			opt.uniforms = uniforms;
			opt.attributes = attributes;
			opt.vertexShader = Resources.INSTANCE.vertexShader().getText();
			opt.fragmentShader = Resources.INSTANCE.fragmetShader().getText();
			
			ShaderMaterial shaderMaterial = new ShaderMaterial(opt);
			
			int radius = 100, segments = 68, rings = 38;
			
			Sphere geometry = new Sphere( radius, segments, rings );
	
			this.vc1 = geometry.getVertices().size();
	
			Cube geometry2 = new Cube( 0.8f * radius, 0.8f * radius, 0.8f * radius, 10, 10, 10 );
	
			GeometryUtils.merge( geometry, geometry2 );
	
			this.sphere = new ParticleSystem( geometry, shaderMaterial );
	
			sphere.setDynamic(true);
			// TODO: Fix this
//			sphere.sortParticles = true;
	
			List<Vector3f> vertices = sphere.getGeometry().getVertices();
			List<Float> values_size = (List<Float>) attributes.get("size").getValue();
			List<Color3f> values_color = (List<Color3f>) attributes.get("customColor").getValue();
		
			for( int v = 0; v < vertices.size(); v++ ) 
			{
				values_color.add( v, new Color3f( 0xffffff ));
	
				if ( v < vc1 ) 
				{	
					values_size.add( v, 10f);
					values_color.get( v ).setHSV( 0.01f + 0.1f * ( v / (float)vc1 ), 0.99f, ( vertices.get( v ).getY() + radius ) / ( 2.0f *radius ) );
				} 
				else 
				{
					values_size.add( v, 40f);
					values_color.get( v ).setHSV( 0.6f, 0.75f, 0.5f + vertices.get( v ).getY() / ( 0.8f * radius ) );
				}
	
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

			sphere.getRotation().setY((float) (0.02 * time));
			sphere.getRotation().setZ((float) (0.02 * time));
			
			for( int i = 0; i < attributes.get("size").getValue().size(); i++ ) 
			{
				List<Float> value = (List<Float>) attributes.get("size").getValue(); 
				if(i < vc1 )
					value.set( i, (float)(16f + 12f * Math.sin( 0.1 * i + time )));
			}
			
			attributes.get("size").needsUpdate = true;
			super.onUpdate(duration);
		}
	}

	RenderingPanel renderingPanel;

	public CustomAttributesParticles2() 
	{
		super("Sphere and cube", "Here are used custom shaders and sprites. This example bases on the three.js example.");
	}

	@Override
	public DemoScene onInitialize()
	{
		return new DemoScene();
	}
	
	@Override
	public ImageResource getIcon()
	{
		return Demo.resources.example_custom_attributes_particles2();
	}
	
	@Override
	protected void asyncOnInitialize(final AsyncCallback<DemoRenderingScene> callback)
	{
		GWT.runAsync(CustomAttributesParticles2.class, new RunAsyncCallback() 
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
