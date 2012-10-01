/*
 * Copyright 2012 Alex Usachev, thothbot@gmail.com
 * 
 * This file based on the JavaScript source file of the THREE.JS project, 
 * licensed under MIT License.
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

package thothbot.parallax.demo.client.content;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import thothbot.parallax.core.client.RenderingPanel;
import thothbot.parallax.core.client.gl2.enums.TextureWrapMode;
import thothbot.parallax.core.client.shaders.Attribute;
import thothbot.parallax.core.client.shaders.Shader;
import thothbot.parallax.core.client.shaders.Uniform;
import thothbot.parallax.core.client.textures.Texture;
import thothbot.parallax.core.shared.cameras.PerspectiveCamera;
import thothbot.parallax.core.shared.core.Color;
import thothbot.parallax.core.shared.core.Vector3;
import thothbot.parallax.core.shared.geometries.CubeGeometry;
import thothbot.parallax.core.shared.geometries.SphereGeometry;
import thothbot.parallax.core.shared.materials.ShaderMaterial;
import thothbot.parallax.core.shared.objects.ParticleSystem;
import thothbot.parallax.core.shared.utils.GeometryUtils;
import thothbot.parallax.demo.client.ContentWidget;
import thothbot.parallax.demo.client.Demo;
import thothbot.parallax.demo.client.DemoAnnotations.DemoSource;

import com.google.gwt.core.client.GWT;
import com.google.gwt.core.client.RunAsyncCallback;
import com.google.gwt.resources.client.ImageResource;
import com.google.gwt.resources.client.TextResource;
import com.google.gwt.user.client.rpc.AsyncCallback;

public class CustomAttributesParticles2 extends ContentWidget
{
	/*
	 * Load shaders
	 */
	@DemoSource
	public interface Resources extends Shader.DefaultResources
	{
		Resources INSTANCE = GWT.create(Resources.class);

		@Source("../../resources/shaders/custom_attributes_particles.fs")
		TextResource getFragmentShader();
		
		@Source("../../resources/shaders/custom_attributes_particles.vs")
		TextResource getVertexShader();
	}

	/*
	 * Prepare Rendering Scene
	 */
	@SuppressWarnings("unchecked")
	@DemoSource
	class DemoScene extends DemoAnimatedScene 
	{	
		private static final String image = "./static/textures/sprites/disc.png";
		
		Map<String, Attribute> attributes;
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
			getScene().add(getCamera());

			this.attributes = new HashMap<String, Attribute>();
			attributes.put("size", new Attribute(Attribute.TYPE.F, new ArrayList<Integer>()));
			attributes.put("customColor", new Attribute(Attribute.TYPE.C, new ArrayList<Color>()));
	
			Map <String, Uniform> uniforms = new HashMap<String, Uniform>();
			uniforms.put("amplitude", new Uniform(Uniform.TYPE.F, 1.0));
			uniforms.put("color", new Uniform(Uniform.TYPE.C, new Color( 0xffffff )));
			
			Texture texture = new Texture(image);
			texture.setWrapS(TextureWrapMode.REPEAT);
			texture.setWrapT(TextureWrapMode.REPEAT);

			uniforms.put("texture", new Uniform(Uniform.TYPE.T, texture));
		
			ShaderMaterial shaderMaterial = new ShaderMaterial(	Resources.INSTANCE );
			shaderMaterial.getShader().setAttributes(attributes);
			shaderMaterial.getShader().setUniforms(uniforms);
			
			int radius = 100, segments = 68, rings = 38;
			
			SphereGeometry geometry = new SphereGeometry( radius, segments, rings );
	
			this.vc1 = geometry.getVertices().size();
	
			CubeGeometry geometry2 = new CubeGeometry( 0.8 * radius, 0.8 * radius, 0.8 * radius, 10, 10, 10 );
	
			GeometryUtils.merge( geometry, geometry2 );
	
			this.sphere = new ParticleSystem( geometry, shaderMaterial );
	
			sphere.setDynamic(true);
			// TODO: Fix this
//			sphere.sortParticles = true;
	
			List<Vector3> vertices = sphere.getGeometry().getVertices();
			List<Double> values_size = (List<Double>) attributes.get("size").getValue();
			List<Color> values_color = (List<Color>) attributes.get("customColor").getValue();
		
			for( int v = 0; v < vertices.size(); v++ ) 
			{
				values_color.add( v, new Color( 0xffffff ));
	
				if ( v < vc1 ) 
				{	
					values_size.add( v, 10.0);
					values_color.get( v ).setHSV( 0.01 + 0.1 * ( v / (double)vc1 ), 0.99, ( vertices.get( v ).getY() + radius ) / ( 2.0 *radius ) );
				} 
				else 
				{
					values_size.add( v, 40.0);
					values_color.get( v ).setHSV( 0.6, 0.75, 0.5 + vertices.get( v ).getY() / ( 0.8 * radius ) );
				}
	
			}
	
			getScene().add( sphere );

		}
				
		@Override
		protected void onUpdate(double duration)
		{
			double time = duration * 0.005;

			sphere.getRotation().setY(0.02 * time);
			sphere.getRotation().setZ(0.02 * time);
			
			for( int i = 0; i < attributes.get("size").getValue().size(); i++ ) 
			{
				List<Double> value = (List<Double>) attributes.get("size").getValue(); 
				if(i < vc1 )
					value.set( i, 16.0 + 12.0 * Math.sin( 0.1 * i + time ));
			}
			
			attributes.get("size").needsUpdate = true;
		}
	}

	RenderingPanel renderingPanel;

	public CustomAttributesParticles2() 
	{
		super("Sphere and cube", "Here are used custom shaders and sprites. This example based on the three.js example.");
	}

	@Override
	public DemoScene onInitialize()
	{
		return new DemoScene();
	}
	
	@Override
	public ImageResource getIcon()
	{
		return Demo.resources.exampleCustomAttributesParticles2();
	}
	
	@Override
	protected void asyncOnInitialize(final AsyncCallback<DemoAnimatedScene> callback)
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
