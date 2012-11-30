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

package thothbot.parallax.demo.resources;

import thothbot.parallax.core.client.shaders.Shader;
import thothbot.parallax.core.client.shaders.Uniform;
import thothbot.parallax.core.shared.core.Vector2;
import thothbot.parallax.core.shared.core.Vector3;

import com.google.gwt.core.client.GWT;
import com.google.gwt.resources.client.TextResource;

public final class LavaShader extends Shader 
{

	interface Resources extends DefaultResources
	{
		Resources INSTANCE = GWT.create(Resources.class);
		
		@Source("shaders/lava.vs")
		TextResource getVertexShader();

		@Source("shaders/lava.fs")
		TextResource getFragmentShader();
	}

	public LavaShader() 
	{
		super(Resources.INSTANCE);
	}
	
	@Override
	protected void initUniforms() 
	{

		this.addUniform("fogDensity", new Uniform(Uniform.TYPE.F, 0.45 ));
		this.addUniform("fogColor", new Uniform(Uniform.TYPE.V3, new Vector3( 0, 0, 0 ) ));
		
		this.addUniform("time", new Uniform(Uniform.TYPE.F, 1.0 ));
		this.addUniform("resolution", new Uniform(Uniform.TYPE.V2, new Vector2() ));

		this.addUniform("uvScale", new Uniform(Uniform.TYPE.V2, new Vector2( 3.0, 1.0 ) ));
		this.addUniform("texture1", new Uniform(Uniform.TYPE.T ));
		this.addUniform("texture2", new Uniform(Uniform.TYPE.T ));
	}
}
