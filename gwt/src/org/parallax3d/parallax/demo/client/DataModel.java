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

package org.parallax3d.parallax.demo.client;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.parallax3d.parallax.demo.client.ContentWidget;
import org.parallax3d.parallax.demo.client.content.CustomAttributesParticles;
import org.parallax3d.parallax.demo.client.content.CustomAttributesParticles2;
import org.parallax3d.parallax.demo.client.content.LoaderSTL;
import org.parallax3d.parallax.demo.client.content.animation.MorphNormalsFlamingo;
import org.parallax3d.parallax.demo.client.content.animation.MorphTargetsHorse;
import org.parallax3d.parallax.demo.client.content.geometries.BufferGeometryDemo;
import org.parallax3d.parallax.demo.client.content.geometries.BufferGeometryParticles;
import org.parallax3d.parallax.demo.client.content.geometries.Cameras;
import org.parallax3d.parallax.demo.client.content.geometries.Geometries;
import org.parallax3d.parallax.demo.client.content.geometries.GeometriesParametric;
import org.parallax3d.parallax.demo.client.content.geometries.GeometryColors;
import org.parallax3d.parallax.demo.client.content.geometries.GeometryCube;
import org.parallax3d.parallax.demo.client.content.geometries.GeometryDynamic;
import org.parallax3d.parallax.demo.client.content.geometries.GeometryExtrudeSplines;
import org.parallax3d.parallax.demo.client.content.geometries.GeometryHierarchy;
import org.parallax3d.parallax.demo.client.content.geometries.GeometryNormals;
import org.parallax3d.parallax.demo.client.content.geometries.GeometryShapes;
import org.parallax3d.parallax.demo.client.content.geometries.LinesSphere;
import org.parallax3d.parallax.demo.client.content.interactivity.InteractiveCubes;
import org.parallax3d.parallax.demo.client.content.interactivity.InteractiveDraggableCubes;
import org.parallax3d.parallax.demo.client.content.interactivity.InteractiveVoxelPainter;
import org.parallax3d.parallax.demo.client.content.materials.MaterialsBumpmap;
import org.parallax3d.parallax.demo.client.content.materials.MaterialsBumpmapSkin;
import org.parallax3d.parallax.demo.client.content.materials.MaterialsCubemapBallsReflection;
import org.parallax3d.parallax.demo.client.content.materials.MaterialsCubemapBallsRefraction;
import org.parallax3d.parallax.demo.client.content.materials.MaterialsCubemapDynamicReflection;
import org.parallax3d.parallax.demo.client.content.materials.MaterialsCubemapFresnel;
import org.parallax3d.parallax.demo.client.content.materials.MaterialsRenderTarget;
import org.parallax3d.parallax.demo.client.content.materials.MaterialsShaderLava;
import org.parallax3d.parallax.demo.client.content.materials.MaterialsShaderMonjori;
import org.parallax3d.parallax.demo.client.content.materials.MaterialsTextureAnisotropy;
import org.parallax3d.parallax.demo.client.content.materials.MaterialsTextureFilter;
import org.parallax3d.parallax.demo.client.content.materials.MaterialsTextures;
import org.parallax3d.parallax.demo.client.content.materials.MaterialsWireframe;
import org.parallax3d.parallax.demo.client.content.materials.ParticlesRandom;
import org.parallax3d.parallax.demo.client.content.materials.ParticlesTrails;
import org.parallax3d.parallax.demo.client.content.materials.ShaderOcean;
import org.parallax3d.parallax.demo.client.content.materials.TrackballEarth;
import org.parallax3d.parallax.demo.client.content.misc.Helpers;
import org.parallax3d.parallax.demo.client.content.misc.MiscLookAt;
import org.parallax3d.parallax.demo.client.content.misc.MiscMemoryTestGeometries;
import org.parallax3d.parallax.demo.client.content.misc.MiscMemoryTestShaders;
import org.parallax3d.parallax.demo.client.content.misc.PerformanceDoubleSided;
import org.parallax3d.parallax.demo.client.content.plugins.EffectsLensFlares;
import org.parallax3d.parallax.demo.client.content.plugins.EffectsSprites;
import org.parallax3d.parallax.demo.client.content.plugins.HilbertCurves;
import org.parallax3d.parallax.demo.client.content.plugins.PostprocessingGodrays;
import org.parallax3d.parallax.demo.client.content.plugins.Saturn;
import org.parallax3d.parallax.demo.client.content.plugins.TerrainDynamic;
import org.parallax3d.parallax.demo.client.content.raytracing.Raytracing;

import com.google.gwt.cell.client.AbstractCell;
import com.google.gwt.core.client.prefetch.RunAsyncCode;
import com.google.gwt.safehtml.shared.SafeHtmlBuilder;
import com.google.gwt.view.client.ListDataProvider;
import com.google.gwt.view.client.SelectionModel;
import com.google.gwt.view.client.TreeViewModel;

/**
 * The {@link TreeViewModel} used by the main menu.
 */
public class DataModel implements TreeViewModel 
{
	/**
	 * The cell used to render categories.
	 */
	private static class CategoryCell extends AbstractCell<Category> 
	{
		@Override
		public void render(Context context, Category value, SafeHtmlBuilder sb) 
		{
			if (value != null)
				sb.appendEscaped(value.getName());
		}
	}

	/**
	 * The cell used to render examples.
	 */
	private static class ContentWidgetCell extends AbstractCell<ContentWidget>
	{    
		@Override
		public void render(Context context, ContentWidget value, SafeHtmlBuilder sb) 
		{
			if (value != null) 
			{
				sb.appendHtmlConstant("<img class='menuIcon' src='" + value.getIconUrl() + "'/>");
				sb.appendHtmlConstant("<span>" + value.getName() + "</span>");
			}
		}
	}

	/**
	 * A top level category in the tree.
	 */
	public class Category 
	{
		private final ListDataProvider<ContentWidget> examples = new ListDataProvider<ContentWidget>();
		private final String name;
		private NodeInfo<ContentWidget> nodeInfo;
		private final List<RunAsyncCode> splitPoints = new ArrayList<RunAsyncCode>();

		public Category(String name) 
		{
			this.name = name;
		}

		public void addExample(ContentWidget example, RunAsyncCode splitPoint) 
		{
			examples.getList().add(example);
			
			if (splitPoint != null)
				splitPoints.add(splitPoint);

			contentCategory.put(example, this);
			contentToken.put(example.getContentWidgetToken(), example);
		}
		
		public ListDataProvider<ContentWidget> getExamples()
		{
			return this.examples;
		}

		public String getName() 
		{
			return name;
		}

		/**
		 * Get the node info for examples under this category.
		 * 
		 * @return the node info
		 */
		public NodeInfo<ContentWidget> getNodeInfo() 
		{
			if (nodeInfo == null)
				nodeInfo = new DefaultNodeInfo<ContentWidget>(getExamples(), contentWidgetCell, selectionModel, null);
			
			return nodeInfo;
		}

		/**
		 * Get the list of split points to prefetch for this category.
		 * 
		 * @return the list of classes in this category
		 */
		public Iterable<RunAsyncCode> getSplitPoints() 
		{
			return splitPoints;
		}
	}

	/**
	 * The top level categories.
	 */
	private final ListDataProvider<Category> categories = new ListDataProvider<Category>();

	/**
	 * A mapping of {@link ContentWidget}s to their associated categories.
	 */
	private final Map<ContentWidget, Category> contentCategory = new HashMap<ContentWidget, Category>();

	/**
	 * The cell used to render examples.
	 */
	private final ContentWidgetCell contentWidgetCell = new ContentWidgetCell();

	/**
	 * A mapping of history tokens to their associated {@link ContentWidget}.
	 */
	private final Map<String, ContentWidget> contentToken = new HashMap<String, ContentWidget>();

	/**
	 * The selection model used to select examples.
	 */
	private final SelectionModel<ContentWidget> selectionModel;

	public DataModel(SelectionModel<ContentWidget> selectionModel) 
	{
		this.selectionModel = selectionModel;
		initializeTree();
	}

	public  SelectionModel<ContentWidget> getSelectionModel()
	{
		return this.selectionModel;
	}

	/**
	 * Get the {@link Category} associated with a widget.
	 * 
	 * @param widget the {@link ContentWidget}
	 * @return the associated {@link Category}
	 */
	public Category getCategoryForContentWidget(ContentWidget widget) 
	{
		return contentCategory.get(widget);
	}

	/**
	 * Get the content widget associated with the specified history token.
	 * 
	 * @param token the history token
	 * @return the associated {@link ContentWidget}
	 */
	public ContentWidget getContentWidgetForToken(String token) 
	{
		return contentToken.get(token);
	}

	public <T> NodeInfo<?> getNodeInfo(T value) 
	{
		if (value == null) 
		{
			// Return the top level categories.
			return new DefaultNodeInfo<Category>(categories, new CategoryCell());
		} 
		else if (value instanceof Category) 
		{
			// Return the examples within the category.
			Category category = (Category) value;
			return category.getNodeInfo();
		}
		return null;
	}

	public boolean isLeaf(Object value) 
	{
		return value != null && !(value instanceof Category);
	}

	public List<Category> getCategories()
	{
		return categories.getList();
	}
	/**
	 * Get the set of all {@link ContentWidget}s used in the model.
	 * 
	 * @return the {@link ContentWidget}s
	 */
	public Set<ContentWidget> getAllContentWidgets() 
	{
		Set<ContentWidget> widgets = new HashSet<ContentWidget>();
		for (Category category : getCategories()) 
		{
			for (ContentWidget example : category.examples.getList())
				widgets.add(example);
		}

		return widgets;
	}

	/**
	 * Initialize the tree.
	 */
	private void initializeTree() 
	{
		List<Category> categoriesList = categories.getList();

		// Geometries.
		{
			Category category = new Category("Geometries");
			categoriesList.add(category);
//			category.addExample(new CopyOfGeometryCube(), 
//					RunAsyncCode.runAsyncCode(CopyOfGeometryCube.class));
			category.addExample(new GeometryCube(), 
					RunAsyncCode.runAsyncCode(GeometryCube.class));
			category.addExample(new GeometryColors(),
					RunAsyncCode.runAsyncCode(GeometryColors.class));
			category.addExample(new Geometries(),
					RunAsyncCode.runAsyncCode(Geometries.class));
			category.addExample(new GeometriesParametric(),
					RunAsyncCode.runAsyncCode(GeometriesParametric.class));
			category.addExample(new GeometryDynamic(),
					RunAsyncCode.runAsyncCode(GeometryDynamic.class));
			category.addExample(new GeometryHierarchy(),
					RunAsyncCode.runAsyncCode(GeometryHierarchy.class));
			category.addExample(new Cameras(),
					RunAsyncCode.runAsyncCode(Cameras.class));
			category.addExample(new LinesSphere(),
					RunAsyncCode.runAsyncCode(LinesSphere.class));
			category.addExample(new GeometryShapes(),
					RunAsyncCode.runAsyncCode(GeometryShapes.class));
			category.addExample(new GeometryExtrudeSplines(),
					RunAsyncCode.runAsyncCode(GeometryExtrudeSplines.class));
			category.addExample(new BufferGeometryDemo(),
					RunAsyncCode.runAsyncCode(BufferGeometryDemo.class));
			category.addExample(new BufferGeometryParticles(),
					RunAsyncCode.runAsyncCode(BufferGeometryParticles.class));
			category.addExample(new GeometryNormals(),
					RunAsyncCode.runAsyncCode(GeometryNormals.class));
		}
		
		// Interactivity 
		{
			Category category = new Category("Interactivity");
			categoriesList.add(category);
			category.addExample(new InteractiveCubes(),
					RunAsyncCode.runAsyncCode(InteractiveCubes.class));
//			category.addExample(new InteractiveCubesGpu(),
//					RunAsyncCode.runAsyncCode(InteractiveCubesGpu.class));
			category.addExample(new InteractiveDraggableCubes(),
					RunAsyncCode.runAsyncCode(InteractiveDraggableCubes.class));
			category.addExample(new InteractiveVoxelPainter(),
					RunAsyncCode.runAsyncCode(InteractiveVoxelPainter.class));
		}

		// Materials
		{
			Category category = new Category("Materials");
			categoriesList.add(category);
			category.addExample(new MaterialsBumpmap(),
					RunAsyncCode.runAsyncCode(MaterialsBumpmap.class));
			category.addExample(new MaterialsBumpmapSkin(),
					RunAsyncCode.runAsyncCode(MaterialsBumpmapSkin.class));
//			category.addExample(new MaterialsLightmap(),
//					RunAsyncCode.runAsyncCode(MaterialsLightmap.class));
			category.addExample(new MaterialsWireframe(),
					RunAsyncCode.runAsyncCode(MaterialsWireframe.class));
//			category.addExample(new MaterialsCanvas2D(),
//					RunAsyncCode.runAsyncCode(MaterialsCanvas2D.class));
			category.addExample(new MaterialsTextures(),
					RunAsyncCode.runAsyncCode(MaterialsTextures.class));
//			category.addExample(new MaterialsTextureCompressed(),
//					RunAsyncCode.runAsyncCode(MaterialsTextureCompressed.class));
			category.addExample(new MaterialsCubemapFresnel(),
					RunAsyncCode.runAsyncCode(MaterialsCubemapFresnel.class));
			category.addExample(new MaterialsCubemapBallsReflection(),
					RunAsyncCode.runAsyncCode(MaterialsCubemapBallsReflection.class));
			category.addExample(new MaterialsCubemapBallsRefraction(),
					RunAsyncCode.runAsyncCode(MaterialsCubemapBallsRefraction.class));
			category.addExample(new MaterialsCubemapDynamicReflection(),
					RunAsyncCode.runAsyncCode(MaterialsCubemapDynamicReflection.class));
			category.addExample(new MaterialsTextureFilter(),
					RunAsyncCode.runAsyncCode(MaterialsTextureFilter.class));
			category.addExample(new MaterialsTextureAnisotropy(),
					RunAsyncCode.runAsyncCode(MaterialsTextureAnisotropy.class));
			category.addExample(new ParticlesTrails(),
					RunAsyncCode.runAsyncCode(ParticlesTrails.class));
			category.addExample(new ParticlesRandom(),
					RunAsyncCode.runAsyncCode(ParticlesRandom.class));
			category.addExample(new TrackballEarth(),
					RunAsyncCode.runAsyncCode(TrackballEarth.class));
			category.addExample(new MaterialsShaderLava(),
					RunAsyncCode.runAsyncCode(MaterialsShaderLava.class));
			category.addExample(new MaterialsShaderMonjori(),
					RunAsyncCode.runAsyncCode(MaterialsShaderMonjori.class));
			category.addExample(new ShaderOcean(),
					RunAsyncCode.runAsyncCode(ShaderOcean.class));
			category.addExample(new MaterialsRenderTarget(),
					RunAsyncCode.runAsyncCode(MaterialsRenderTarget.class));
		}

		// Custom Attributes
		{
			Category category = new Category("Custom Attributes");
			categoriesList.add(category);
			category.addExample(new CustomAttributesParticles(),
					RunAsyncCode.runAsyncCode(CustomAttributesParticles.class));
			category.addExample(new CustomAttributesParticles2(),
					RunAsyncCode.runAsyncCode(CustomAttributesParticles2.class));
		}
		
		// Animation
		{
			Category category = new Category("Animation");
			categoriesList.add(category);
//			category.addExample(new ClothSimulation(),
//					RunAsyncCode.runAsyncCode(ClothSimulation.class));
			category.addExample(new MorphNormalsFlamingo(),
					RunAsyncCode.runAsyncCode(MorphNormalsFlamingo.class));
			category.addExample(new MorphTargetsHorse(),
					RunAsyncCode.runAsyncCode(MorphTargetsHorse.class));
		}
		
		// Loaders
//		{
//			Category category = new Category("Loaders");
//			categoriesList.add(category);
//			category.addExample(new LoaderCollada(),
//					RunAsyncCode.runAsyncCode(LoaderCollada.class));
//		}
		
		// Plugins
		{
			Category category = new Category("Plugins");
			categoriesList.add(category);
			category.addExample(new TerrainDynamic(),
					RunAsyncCode.runAsyncCode(TerrainDynamic.class));
			category.addExample(new HilbertCurves(),
					RunAsyncCode.runAsyncCode(HilbertCurves.class));
			category.addExample(new PostprocessingGodrays(),
					RunAsyncCode.runAsyncCode(PostprocessingGodrays.class));
//			category.addExample(new PostprocessingMulti(),
//					RunAsyncCode.runAsyncCode(PostprocessingMulti.class));
			category.addExample(new EffectsLensFlares(),
					RunAsyncCode.runAsyncCode(EffectsLensFlares.class));
			category.addExample(new EffectsSprites(),
					RunAsyncCode.runAsyncCode(EffectsSprites.class));
			category.addExample(new Saturn(),
					RunAsyncCode.runAsyncCode(Saturn.class));

		}

		// Miscellaneous
		{
			Category category = new Category("Miscellaneous");
			categoriesList.add(category);
			category.addExample(new PerformanceDoubleSided(),
					RunAsyncCode.runAsyncCode(PerformanceDoubleSided.class));
			category.addExample(new MiscLookAt(),
					RunAsyncCode.runAsyncCode(MiscLookAt.class));
			category.addExample(new MiscMemoryTestGeometries(),
					RunAsyncCode.runAsyncCode(MiscMemoryTestGeometries.class));
			category.addExample(new MiscMemoryTestShaders(),
					RunAsyncCode.runAsyncCode(MiscMemoryTestShaders.class));
			category.addExample(new LoaderSTL(), 
					RunAsyncCode.runAsyncCode(LoaderSTL.class));
			category.addExample(new Helpers(), 
					RunAsyncCode.runAsyncCode(Helpers.class));
		}
		
		// Raytracing Rendering
		{
			Category category = new Category("Raytracing Rendering");
			categoriesList.add(category);
			category.addExample(new Raytracing(),
					RunAsyncCode.runAsyncCode(Raytracing.class));
		}
	}
}
