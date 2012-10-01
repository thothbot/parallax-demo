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

package thothbot.parallax.demo.resources;

import java.util.Arrays;
import java.util.List;

import thothbot.parallax.core.client.shaders.ChunksFragmentShader;
import thothbot.parallax.core.client.shaders.ChunksVertexShader;
import thothbot.parallax.core.client.shaders.Shader;
import thothbot.parallax.core.client.shaders.Uniform;
import thothbot.parallax.core.client.shaders.UniformsLib;
import thothbot.parallax.core.shared.core.Color;
import thothbot.parallax.core.shared.core.Vector3;
import thothbot.parallax.core.shared.core.Vector4;

import com.google.gwt.core.client.GWT;
import com.google.gwt.resources.client.TextResource;

/**
 * Simple skin shader
 * <p>
 * - per-pixel Blinn-Phong diffuse term mixed with half-Lambert wrap-around term (per color component)<br>
 * - physically based specular term (Kelemen/Szirmay-Kalos specular reflectance)<br>
 * - diffuse map<br>
 * - bump map<br>
 * - specular map<br>
 * - point, directional and hemisphere lights (use with "lights: true" material option)<br>
 * - fog (use with "fog: true" material option)<br>
 * - shadow maps
 * <p>
 * Based on three,js code.
 *  
 * @author thothbot
 *
 */
public final class SkinSimpleShader extends Shader 
{
	interface Resources extends DefaultResources
	{
		Resources INSTANCE = GWT.create(Resources.class);
		
		@Source("shaders/skin_simple.vs")
		TextResource getVertexShader();

		@Source("shaders/skin_simple.fs")
		TextResource getFragmentShader();
	}

	public SkinSimpleShader() 
	{
		super(Resources.INSTANCE);
	}
	
	@Override
	protected void initUniforms() 
	{
		this.setUniforms(UniformsLib.getFog());
		this.setUniforms(UniformsLib.getLights());
		this.setUniforms(UniformsLib.getShadowmap());
		
		this.addUniform("enableBump", new Uniform(Uniform.TYPE.I, 0 ));
		this.addUniform("enableSpecular", new Uniform(Uniform.TYPE.I, 0 ));
		
		this.addUniform("tDiffuse", new Uniform(Uniform.TYPE.T ));
		this.addUniform("tBeckmann", new Uniform(Uniform.TYPE.T ));

		this.addUniform("uDiffuseColor", new Uniform(Uniform.TYPE.C, new Color( 0xeeeeee ) ));
		this.addUniform("uSpecularColor", new Uniform(Uniform.TYPE.C, new Color( 0x111111 ) ));
		this.addUniform("uAmbientColor", new Uniform(Uniform.TYPE.C, new Color( 0x050505 ) ));
		
		this.addUniform("uOpacity", new Uniform(Uniform.TYPE.F, 1.0 ));
		
		this.addUniform("uRoughness", new Uniform(Uniform.TYPE.F, 0.15 ));
		this.addUniform("uSpecularBrightness", new Uniform(Uniform.TYPE.F, 0.75 ));

		this.addUniform("bumpMap", new Uniform(Uniform.TYPE.T ));
		this.addUniform("bumpScale", new Uniform(Uniform.TYPE.F, 1.0 ));
		
		this.addUniform("specularMap", new Uniform(Uniform.TYPE.T ));
		
		this.addUniform("offsetRepeat", new Uniform(Uniform.TYPE.V4, new Vector4( 0, 0, 1, 1 ) ));
		this.addUniform("uWrapRGB", new Uniform(Uniform.TYPE.V3, new Vector3( 0.75, 0.375, 0.1875 ) ));		
	}
	
	@Override
	protected void updateFragmentSource(String src)
	{
		List<String> vars = Arrays.asList(
				ChunksFragmentShader.SHADOWMAP_PARS,
				ChunksFragmentShader.FOG_PARS,
				ChunksFragmentShader.BUMPMAP_PARS
		);

		List<String> main = Arrays.asList(
				ChunksFragmentShader.SHADOWMAP,
				ChunksFragmentShader.LINEAR_TO_GAMMA,
				ChunksFragmentShader.FOG
		);

		super.updateFragmentSource(Shader.updateShaderSource(src, vars, main));		
	}
	
	@Override
	protected void updateVertexSource(String src) 
	{
		List<String> vars = Arrays.asList(
				ChunksVertexShader.SHADOWMAP_PARS
		);
				
		List<String> main = Arrays.asList(
				ChunksVertexShader.SHADOWMAP
		);

		// TODO Auto-generated method stub
		super.updateVertexSource(Shader.updateShaderSource(src, vars, main));
	}
}