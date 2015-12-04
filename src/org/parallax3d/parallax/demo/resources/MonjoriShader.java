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

package org.parallax3d.parallax.demo.resources;

import org.parallax3d.parallax.core.client.shaders.Shader;
import org.parallax3d.parallax.core.client.shaders.Uniform;
import org.parallax3d.parallax.core.shared.math.Vector2;

import com.google.gwt.core.client.GWT;
import com.google.gwt.resources.client.TextResource;

public final class MonjoriShader extends Shader
{

	interface Resources extends DefaultResources
	{
		Resources INSTANCE = GWT.create(Resources.class);
		
		@Source("shaders/monjori.vs")
		TextResource getVertexShader();

		@Source("shaders/monjori.fs")
		TextResource getFragmentShader();
	}

	public MonjoriShader() 
	{
		super(Resources.INSTANCE);
	}
	
	@Override
	protected void initUniforms() 
	{

		this.addUniform("time", new Uniform(Uniform.TYPE.F, 1.0 ));
		this.addUniform("resolution", new Uniform(Uniform.TYPE.V2, new Vector2() ));
	}
}
