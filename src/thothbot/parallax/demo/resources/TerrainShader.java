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
import thothbot.parallax.core.shared.core.Vector2;
import thothbot.parallax.core.shared.core.Vector3;

import com.google.gwt.core.client.GWT;
import com.google.gwt.resources.client.TextResource;

/**
 * Dynamic terrain shader<br>
 * - Blinn-Phong<br>
 * - height + normal + diffuse1 + diffuse2 + specular + detail maps<br>
 * - point and directional lights (use with "lights: true" material option)
 * <p>
 * Based on three.js code
 * 
 * @author thothbot
 *
 */
public final class TerrainShader extends Shader 
{

	interface Resources extends DefaultResources
	{
		Resources INSTANCE = GWT.create(Resources.class);
		
		@Source("shaders/terrain.vs")
		TextResource getVertexShader();

		@Source("shaders/terrain.fs")
		TextResource getFragmentShader();
	}

	public TerrainShader() 
	{
		super(Resources.INSTANCE);
	}

	@Override
	protected void initUniforms()
	{
		this.setUniforms(UniformsLib.getFog());
		this.setUniforms(UniformsLib.getLights());
		this.addUniform("enableDiffuse1", new Uniform(Uniform.TYPE.I, 0 ));
		this.addUniform("enableDiffuse2", new Uniform(Uniform.TYPE.I, 0 ));
		this.addUniform("enableSpecular", new Uniform(Uniform.TYPE.I, 0 ));
		this.addUniform("enableReflection", new Uniform(Uniform.TYPE.I, 0 ));

		this.addUniform("tDiffuse1", new Uniform(Uniform.TYPE.T, 0 ));
		this.addUniform("tDiffuse2", new Uniform(Uniform.TYPE.T, 1 ));
		this.addUniform("tDetail", new Uniform(Uniform.TYPE.T, 2 ));
		this.addUniform("tNormal", new Uniform(Uniform.TYPE.T, 3 ));
		this.addUniform("tSpecular", new Uniform(Uniform.TYPE.T, 4 ));
		this.addUniform("tDisplacement", new Uniform(Uniform.TYPE.T, 5 ));
		
		this.addUniform("uNormalScale", new Uniform(Uniform.TYPE.F, 1.0 ));
		
		this.addUniform("uDisplacementBias", new Uniform(Uniform.TYPE.F, 0.0 ));
		this.addUniform("uDisplacementScale", new Uniform(Uniform.TYPE.F, 1.0 ));

		this.addUniform("uDiffuseColor", new Uniform(Uniform.TYPE.C, new Color(0xeeeeee) ));
		this.addUniform("uSpecularColor", new Uniform(Uniform.TYPE.C, new Color(0x111111) ));
		this.addUniform("uAmbientColor", new Uniform(Uniform.TYPE.C, new Color(0x050505) ));

		this.addUniform("uShininess", new Uniform(Uniform.TYPE.F, 30.0 ));
		this.addUniform("uOpacity", new Uniform(Uniform.TYPE.F, 1.0 ));
		
		this.addUniform("uRepeatBase", new Uniform(Uniform.TYPE.V2, new Vector2(1, 1) ));
		this.addUniform("uRepeatOverlay", new Uniform(Uniform.TYPE.V2, new Vector2(1, 1) ));

		this.addUniform("uOffset", new Uniform(Uniform.TYPE.V2, new Vector2(0, 0) ));
	}
	
	@Override
	protected void updateFragmentSource(String src)
	{
		List<String> vars = Arrays.asList(
			ChunksFragmentShader.FOG_PARS
		);
		
		List<String> main = Arrays.asList(
			ChunksFragmentShader.LENEAR_TO_GAMMA,
			ChunksFragmentShader.FOG
		);
		
		super.updateFragmentSource(Shader.updateShaderSource(src, vars, main));		
	}
}
