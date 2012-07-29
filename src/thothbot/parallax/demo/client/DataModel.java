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

package thothbot.parallax.demo.client;

import com.google.gwt.cell.client.AbstractCell;
import com.google.gwt.core.client.prefetch.RunAsyncCode;
import com.google.gwt.safehtml.shared.SafeHtmlBuilder;
import com.google.gwt.view.client.ListDataProvider;
import com.google.gwt.view.client.SelectionModel;
import com.google.gwt.view.client.TreeViewModel;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import thothbot.parallax.demo.client.content.*;

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
				sb.appendEscaped(value.getName());
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
	Set<ContentWidget> getAllContentWidgets() 
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

		// Geometry.
		{
			Category category = new Category("Geometry");
			categoriesList.add(category);
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
			category.addExample(new CamerasExample(),
					RunAsyncCode.runAsyncCode(CamerasExample.class));
			category.addExample(new GeometryLinesColors(),
					RunAsyncCode.runAsyncCode(GeometryLinesColors.class));
		}
		
		// Materials
		{
			Category category = new Category("Materials");
			categoriesList.add(category);
			category.addExample(new MaterialsCanvas2D(),
					RunAsyncCode.runAsyncCode(MaterialsCanvas2D.class));
			category.addExample(new MaterialsCubemapBallsReflection(),
					RunAsyncCode.runAsyncCode(MaterialsCubemapBallsReflection.class));
			category.addExample(new MaterialsCubemapBallsRefraction(),
					RunAsyncCode.runAsyncCode(MaterialsCubemapBallsRefraction.class));
			category.addExample(new MaterialsCubemapDynamicReflection(),
					RunAsyncCode.runAsyncCode(MaterialsCubemapDynamicReflection.class));
			category.addExample(new MaterialsTextureFilter(),
					RunAsyncCode.runAsyncCode(MaterialsTextureFilter.class));
			category.addExample(new ParticleTrails(),
					RunAsyncCode.runAsyncCode(ParticleTrails.class));
			category.addExample(new TrackballEarth(),
					RunAsyncCode.runAsyncCode(TrackballEarth.class));
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
		
		// Morphing
		{
			Category category = new Category("Morphing");
			categoriesList.add(category);
			category.addExample(new MorphNormalsFlamingo(),
					RunAsyncCode.runAsyncCode(MorphNormalsFlamingo.class));
			category.addExample(new MorphTargetsHorse(),
					RunAsyncCode.runAsyncCode(MorphTargetsHorse.class));
		}
		
		// Loaders
		{
			Category category = new Category("Loaders");
			categoriesList.add(category);
			category.addExample(new LoaderCollada(),
					RunAsyncCode.runAsyncCode(LoaderCollada.class));
		}
		
		// Performance
		{
			Category category = new Category("Performance");
			categoriesList.add(category);
			category.addExample(new PerformanceDoubleSided(),
					RunAsyncCode.runAsyncCode(PerformanceDoubleSided.class));
		}
	}
}
