/*
 * Copyright 2012 Alex Usachev, thothbot@gmail.com
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

package thothbot.squirrel.demo.resources;

import com.google.gwt.resources.client.ClientBundle;
import com.google.gwt.resources.client.CssResource;
import com.google.gwt.resources.client.ImageResource;

/**
 * The resources and styles used throughout the Demo.
 */
public interface DemoResources extends ClientBundle
{
	/**
	 * The text color of the selected link.
	 */
	String SELECTED_TAB_COLOR = "#333333";
	
	/**
	 * The path to source code for examples.
	 */
	String DST_SOURCE = "demoSource/";

	/**
	 * The destination folder for parsed source code from examples.
	 */
	String DST_SOURCE_EXAMPLE = DST_SOURCE + "java/";

	@Source("images/loading.gif")
	ImageResource loading();
	
	@Source("css/demoView.css")
	@CssResource.NotStrict
	CssResource css();
	
	@Source("images/example_default.jpg")
	ImageResource example_default();
	
	@Source("images/example_cameras.jpg")
	ImageResource example_cameras();
	
	@Source("images/example_colors.jpg")
	ImageResource example_colors();
	
	@Source("images/example_cube.jpg")
	ImageResource example_cube();
	
	@Source("images/example_custom_attributes_particles.jpg")
	ImageResource example_custom_attributes_particles();
	
	@Source("images/example_custom_attributes_particles2.jpg")
	ImageResource example_custom_attributes_particles2();
	
	@Source("images/example_geometries_parametric.jpg")
	ImageResource example_geometries_parametric();
	
	@Source("images/example_geometries.jpg")
	ImageResource example_geometries();
	
	@Source("images/example_geometry_dynamic.jpg")
	ImageResource example_geometry_dinamic();
}
