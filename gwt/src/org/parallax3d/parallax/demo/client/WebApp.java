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

import com.google.gwt.core.client.GWT;
import com.google.gwt.core.client.prefetch.Prefetcher;
import com.google.gwt.dom.client.Style.Visibility;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.logical.shared.OpenEvent;
import com.google.gwt.event.logical.shared.OpenHandler;
import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.event.logical.shared.ValueChangeHandler;
import com.google.gwt.safehtml.shared.SafeHtmlBuilder;
import com.google.gwt.safehtml.shared.SafeHtmlUtils;
import com.google.gwt.user.cellview.client.CellTree;
import com.google.gwt.user.cellview.client.TreeNode;
import com.google.gwt.user.client.DOM;
import com.google.gwt.user.client.History;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.DialogBox;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.RootLayoutPanel;
import com.google.gwt.user.client.ui.RootPanel;
import com.google.gwt.view.client.SelectionChangeEvent;
import com.google.gwt.view.client.SingleSelectionModel;
import org.parallax3d.parallax.App;
import org.parallax3d.parallax.demo.generator.FacebookGenerator;
import org.parallax3d.parallax.demo.generator.SourceGenerator;
import org.parallax3d.parallax.demo.resources.DemoResources;
import org.parallax3d.parallax.platforms.gwt.GwtApp;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

public class WebApp extends GwtApp
{
	/**
	 * The type passed into the
	 * {@link SourceGenerator}.
	 */
	private static final class GenerateSourceSignal 
	{}

	/**
	 * The type passed into the
	 * {@link FacebookGenerator}.
	 */
	private static final class GenerateFacebookSignal 
	{}

	/**
	 * The static resources used throughout the Demo.
	 */
	public static final DemoResources resources = GWT.create(DemoResources.class);

	/**
	 * The main application shell.
	 */
	private DemoShell shell;
	
	/**
	 * The index widget.
	 */
	private IndexWidget indexWidget;
	
	/**
	 * The main application.
	 */
	private Index index;

	public void onInit()
	{
		GWT.setUncaughtExceptionHandler(new GWT.UncaughtExceptionHandler() {
			public void onUncaughtException(Throwable throwable)
			{
//				Log.error("Uncaught exception", throwable);
				if (!GWT.isScript()) 
				{
					String text = "Uncaught exception: ";
					while (throwable != null) 
					{
						StackTraceElement[] stackTraceElements = throwable.getStackTrace();
						text += throwable.toString() + "\n";

						for (int i = 0; i < stackTraceElements.length; i++)
							text += "    at " + stackTraceElements[i] + "\n";
						
						throwable = throwable.getCause();
						if (throwable != null)
							text += "Caused by: ";
					}

					DialogBox dialogBox = new DialogBox(true);
					DOM.setStyleAttribute(dialogBox.getElement(), "backgroundColor", "#ABCDEF");
					text = text.replaceAll(" ", "&nbsp;");
					dialogBox.setHTML("<pre>" + text + "</pre>");
					dialogBox.center();
				}
			}
		});

		// Generate the source code for examples
		GWT.create(GenerateSourceSignal.class);

		// Generate the demo file
		GWT.create(GenerateFacebookSignal.class);

		resources.css().ensureInjected();

		// Create the application shell.
		final SingleSelectionModel<ContentWidget> selectionModel = new SingleSelectionModel<ContentWidget>();
		final DataModel treeModel = new DataModel(selectionModel);
		Set<ContentWidget> contentWidgets = treeModel.getAllContentWidgets();
		
		index = new Index();
		// Hide loading panel
		RootPanel.get("loading").getElement().getStyle().setVisibility(Visibility.HIDDEN);
		// Attach index panel
		RootLayoutPanel.get().add(index);
		
		index.getTabIndex().addClickHandler(new ClickHandler() {
			public void onClick(ClickEvent event)
			{
				displayIndex();
			}
		});

		indexWidget = new IndexWidget(treeModel);
		shell = new DemoShell(treeModel, index);

		// Prefetch examples when opening the Category tree nodes.
		final List<DataModel.Category> prefetched = new ArrayList<DataModel.Category>();
		final CellTree mainMenu = shell.getMainMenu();
		
		mainMenu.addOpenHandler(new OpenHandler<TreeNode>() {
			public void onOpen(OpenEvent<TreeNode> event)
			{
				Object value = event.getTarget().getValue();
				if (!(value instanceof DataModel.Category))
					return;

				DataModel.Category category = (DataModel.Category) value;
				if (!prefetched.contains(category)) 
				{
					prefetched.add(category);
					Prefetcher.prefetch(category.getSplitPoints());
				}
			}
		});

		// Always prefetch.
		Prefetcher.start();

		// Change the history token when a main menu item is selected.
		selectionModel.addSelectionChangeHandler(new SelectionChangeEvent.Handler() {
			public void onSelectionChange(SelectionChangeEvent event)
			{
				ContentWidget selected = selectionModel.getSelectedObject();
				if (selected != null) 
				{
					index.setContentWidget(shell);
					History.newItem("!"+selected.getContentWidgetToken(), true);
				}
			}
		});
		
		// Setup a history handler to reselect the associate menu item.
		final ValueChangeHandler<String> historyHandler = new ValueChangeHandler<String>() {
			public void onValueChange(ValueChangeEvent<String> event)
			{
				// Get the content widget associated with the history token.
				ContentWidget contentWidget = treeModel.getContentWidgetForToken(event.getValue().replaceFirst("!", ""));

				if (contentWidget == null)
					return;

				// Expand the tree node associated with the content.
				DataModel.Category category = treeModel.getCategoryForContentWidget(contentWidget);
				TreeNode node = mainMenu.getRootTreeNode();
				int childCount = node.getChildCount();
				for (int i = 0; i < childCount; i++) 
				{
					if (node.getChildValue(i) == category) 
					{
						node.setChildOpen(i, true, true);
						break;
					}
				}
				
				// Display the content widget.
				displayContentWidget(contentWidget);
				
				//Add GA statistics
				trackPageview(Window.Location.getHref());

				// Select the node in the tree.
				selectionModel.setSelected(contentWidget, true);
			}
		};

		History.addValueChangeHandler(historyHandler);

		// Show the initial example.
		if (History.getToken().length() > 0) 
			History.fireCurrentHistoryState();

		// Use the first token available.
		else
			displayIndex();

		// Generate a site map.
		createSiteMap(contentWidgets);
	}

	/**
	 * Create a hidden site map for crawlability.
	 * 
	 * @param contentWidgets the {@link ContentWidget}s used in Demo
	 */
	private void createSiteMap(Set<ContentWidget> contentWidgets)
	{
		SafeHtmlBuilder sb = new SafeHtmlBuilder();
		for (ContentWidget cw : contentWidgets) 
		{
			String token = cw.getContentWidgetToken();
			sb.append(SafeHtmlUtils.fromTrustedString("<a href=\"#" + token + "\">" + token + "</a>"));
		}

		// Add the site map to the page.
		HTML siteMap = new HTML(sb.toSafeHtml());
		siteMap.setVisible(false);
		RootPanel.get().add(siteMap, 0, 0);
	}

	/**
	 * Set the content to the {@link ContentWidget}.
	 * 
	 * @param content
	 *            the {@link ContentWidget} to display
	 */
	private void displayContentWidget(final ContentWidget content)
	{
		if (content == null)
			return;
		
		shell.setContent(content);
		Window.setTitle("Parallax demo: " + content.getName());
	}
		
	private void displayIndex()
	{
		History.newItem("", true);
		index.setContentWidget(indexWidget);
		Window.setTitle("Parallax: All Examples");
	}
	
	private static native void trackPageview(String url) /*-{
		$wnd._gaq.push(['_trackPageview', url.replace(/^.*\/\/[^\/]+/, '')]);
	}-*/;

}
