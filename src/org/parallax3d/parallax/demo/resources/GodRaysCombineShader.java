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


import org.parallax3d.parallax.core.client.shaders.Uniform;
import org.parallax3d.parallax.core.client.shaders.Shader;
import org.parallax3d.parallax.core.shared.math.Vector2;

import com.google.gwt.core.client.GWT;
import com.google.gwt.resources.client.TextResource;

/**
 * Additively applies god rays from texture tGodRays to a background (tColors).
 * fGodRayIntensity attenuates the god rays.
 * <p>
 * The code from three.js code
 */
public final class GodRaysCombineShader extends Shader 
{

	interface Resources extends DefaultResources
	{
		Resources INSTANCE = GWT.create(Resources.class);

		@Source("shaders/godrays.vs")
		TextResource getVertexShader();

		@Source("shaders/godraysCombine.fs")
		TextResource getFragmentShader();
	}
	
	public GodRaysCombineShader() 
	{
		super(Resources.INSTANCE);
	}

	@Override
	protected void initUniforms()
	{
		this.addUniform("tColors", new Uniform(Uniform.TYPE.T));
		this.addUniform("tGodRays", new Uniform(Uniform.TYPE.T));
		this.addUniform("fGodRayIntensity", new Uniform(Uniform.TYPE.F, 0.69));
		this.addUniform("vSunPositionScreenSpace", new Uniform(Uniform.TYPE.V2, new Vector2( 0.5, 0.5 )));
	}
}
