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
	String SELECTED_TAB_COLOR = "#EEEEEE";
	
	/**
	 * The path to source code for examples.
	 */
	String DST_SOURCE = "demoSource/";

	/**
	 * The destination folder for parsed source code from examples.
	 */
	String DST_SOURCE_EXAMPLE = DST_SOURCE + "java/";

	@Source("images/logo.png")
	ImageResource logo();
	
	@Source("images/loading.gif")
	ImageResource loading();
	
	@Source("css/demoView.css")
	@CssResource.NotStrict
	CssResource css();
	
	@Source("images/example_default.jpg")
	ImageResource exampleDefault();
	
	@Source("images/example_cameras.jpg")
	ImageResource exampleCameras();
	
	@Source("images/example_colors.jpg")
	ImageResource exampleColors();
	
	@Source("images/example_cube.jpg")
	ImageResource exampleCube();
	
	@Source("images/example_hilbert_curves.jpg")
	ImageResource exampleHilbertCurves();
	
	@Source("images/example_postprocessing_godrays.jpg")
	ImageResource examplePostprocessingGodrays();
	
	@Source("images/example_custom_attributes_particles.jpg")
	ImageResource exampleCustomAttributesParticles();
	
	@Source("images/example_custom_attributes_particles2.jpg")
	ImageResource exampleCustomAttributesParticles2();
	
	@Source("images/example_geometries_parametric.jpg")
	ImageResource exampleGeometriesParametric();
	
	@Source("images/example_geometries.jpg")
	ImageResource exampleGeometries();
	
	@Source("images/example_geometry_dynamic.jpg")
	ImageResource exampleGeometryDinamic();
	
	@Source("images/example_geometry_hierarchy.jpg")
	ImageResource exampleGeometryHierarchy();
	
	@Source("images/example_geometry_shapes.jpg")
	ImageResource exampleGeometryShapes();
	
	@Source("images/example_geometry_extrude_splines.jpg")
	ImageResource exampleGeometryExtrudeSplines();
	
	@Source("images/example_interactive_cubes.jpg")
	ImageResource exampleInteractiveCubes();
	
	@Source("images/example_interactive_cubes_gpu.jpg")
	ImageResource exampleInteractiveCubesGpu();
	
	@Source("images/example_interactive_draggable_cubes.jpg")
	ImageResource exampleInteractiveDraggableCubes();
	
	@Source("images/example_interactive_voxel_painter.jpg")
	ImageResource exampleInteractiveVoxelPainter();
	
	@Source("images/example_lines_sphere.jpg")
	ImageResource exampleLinesSphere();
	
	@Source("images/example_materials_bumpmap.jpg")
	ImageResource exampleMaterialsBumpmap();
	
	@Source("images/example_materials_bumpmap_skin.jpg")
	ImageResource exampleMaterialsBumpmapSkin();
	
	@Source("images/example_materials_lightmap.jpg")
	ImageResource exampleMaterialsLightmap();
	
	@Source("images/example_materials_wireframe.jpg")
	ImageResource exampleMaterialsWireframe();
	
	@Source("images/example_materials_canvas2d.jpg")
	ImageResource exampleMaterialsCanvas2D();
	
	@Source("images/example_materials_textures.jpg")
	ImageResource exampleMaterialsTextures();
	
	@Source("images/example_materials_cubemap_balls_reflection.jpg")
	ImageResource exampleMaterialsCubemapBallsReflection();
	
	@Source("images/example_materials_cubemap_balls_refraction.jpg")
	ImageResource exampleMaterialsCubemapBallsRefraction();
	
	@Source("images/example_materials_cubemap_dynamic_reflection.jpg")
	ImageResource exampleMaterialsCubemapDynamicReflection();
	
	@Source("images/example_materials_texture_filter.jpg")
	ImageResource exampleMaterialsTextureFilter();
	
	@Source("images/example_materials_cubemap_fresnel.jpg")
	ImageResource exampleMaterialsCubemapFresnel();
	
	@Source("images/example_particle_trails.jpg")
	ImageResource exampleParticleTrails();
	
	@Source("images/example_particles_random.jpg")
	ImageResource exampleParticlesRandom();
	
	@Source("images/example_trackball_earth.jpg")
	ImageResource exampleTrackballEarth();
	
	@Source("images/example_morph_normals_flamingo.jpg")
	ImageResource exampleMorphNormalsFlamingo();
	
	@Source("images/example_morph_targets_horse.jpg")
	ImageResource exampleMorphTargetsHorse();
	
	@Source("images/example_cloth_simulation.jpg")
	ImageResource exampleClothSimulation();
	
	@Source("images/example_loader_collada.jpg")
	ImageResource exampleLoaderCollada();
	
	@Source("images/example_performance_double_sided.jpg")
	ImageResource examplePerformanceDoubleSided();
	
	@Source("images/example_terrain_dynamic.jpg")
	ImageResource exampleTerrainDynamic();
	
	@Source("images/example_lensflares.jpg")
	ImageResource exampleLensflares();
	
	@Source("images/example_sprites.jpg")
	ImageResource exampleSprites();
	
	@Source("images/example_misc_lookat.jpg")
	ImageResource exampleMiscLookAt();
	
	@Source("images/example_misc_memory_test_geometry.jpg")
	ImageResource exampleMiscMemoryTestGeometry();
	
	@Source("images/example_misc_memory_test_shaders.jpg")
	ImageResource exampleMiscMemoryTestShaders();
}
