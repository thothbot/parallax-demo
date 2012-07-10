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

package thothbot.squirrel.demo.client;

import thothbot.squirrel.demo.resources.DemoResources;

import com.google.gwt.core.client.GWT;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.Anchor;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.ResizeComposite;
import com.google.gwt.user.client.ui.SimpleLayoutPanel;
import com.google.gwt.user.client.ui.SimplePanel;
import com.google.gwt.user.client.ui.Widget;

/**
 * Main view of the application
 */
public class Index extends ResizeComposite
{
	private static IndexUiBinder uiBinder = GWT.create(IndexUiBinder.class);

	interface IndexUiBinder extends UiBinder<Widget, Index> {
	}

	/**
	 * The button used to show index widget.
	 */
	@UiField
	Anchor linkIndex;
	
	@UiField(provided=true)
	FlowPanel bottonPanel;
	
	/**
	 * The page content
	 */
	@UiField(provided=true)
	SimpleLayoutPanel contentWidget;
	
	/**
	 * See {@linkDemoShell}
	 */
	@UiField
	Anchor linkExample;
	
	/**
	 * See {@linkDemoShell}
	 */
	@UiField
	Anchor linkSource;
		
	public Index() 
	{
		bottonPanel = new FlowPanel();
		contentWidget = new SimpleLayoutPanel();
		
		initWidget(uiBinder.createAndBindUi(this));
	}

	public Anchor getTabIndex()
	{
		return this.linkIndex;
	}
	
	public void setContentWidget(SimplePanel content)
	{
		linkIndex.getElement().getStyle().setColor(DemoResources.SELECTED_TAB_COLOR);
		bottonPanel.setVisible(false);
		
		this.contentWidget.clear();
		this.contentWidget.setWidget(content);
	}

	public void setContentWidget(DemoShell content)
	{
		linkIndex.getElement().getStyle().clearColor();
		bottonPanel.setVisible(true);

		this.contentWidget.clear();
		this.contentWidget.setWidget(content);	
	}	
}
