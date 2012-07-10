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

import thothbot.squirrel.demo.client.DataModel.Category;

import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.Event;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.HasAlignment;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.ScrollPanel;
import com.google.gwt.user.client.ui.VerticalPanel;

/**
 * This widget used to show all available examples
 */
public class IndexWidget extends ScrollPanel
{
	
	/**
	 * Used to show content of the all tree categories
	 */
	VerticalPanel categoriesInfo;
	
	/**
	 * Three model, which data used to generate the page
	 */
	DataModel treeModel;
		
	public IndexWidget(DataModel treeModel)
	{
		this.treeModel = treeModel;
		
		categoriesInfo = new VerticalPanel();
		categoriesInfo.getElement().getStyle().setMarginLeft(10.0, Unit.PX);
		categoriesInfo.getElement().getStyle().setMarginRight(10.0, Unit.PX);
		this.add(categoriesInfo);

		for (Category category : treeModel.getCategories()) 
		{
			addCategory(category);
		}
	}
	
	/**
	 * Generate the view of the top level Category
	 * 
	 * @param category top-level category
	 */
	public void addCategory(Category category)
	{
		Label name = new Label(category.getName());
		name.setStyleName("indexGroupName");
		
		this.categoriesInfo.add(name);
		FlowPanel examplesInfo = new FlowPanel();
		examplesInfo.ensureDebugId("examplesInfo");

		this.categoriesInfo.add(examplesInfo);

		for (ContentWidget example : category.getExamples().getList())
			addItem(example, examplesInfo);
	}
	
	/**
	 * Used to generate view of the examples. On click will be updated
	 * selection model.
	 * 
	 * @param example content Widget
	 * @param examplesInfo panel where render this view 
	 */
	public void addItem(final ContentWidget example, FlowPanel examplesInfo) 
	{

		final FlowPanel examplePanel = new FlowPanel();
		examplePanel.setStyleName("indexExamplePanel");
		examplePanel.ensureDebugId("examplePanel");
		examplePanel.add(new Image(example.getIcon()));
		
		Label name = new Label(example.getName());
		name.setHorizontalAlignment(HasAlignment.ALIGN_CENTER);  
		examplePanel.add(name);

		examplesInfo.add(examplePanel);
		examplePanel.sinkEvents(Event.ONCLICK);
		
		examplePanel.addHandler(new ClickHandler() {
			
			@Override
			public void onClick(ClickEvent event)
			{
				treeModel.getSelectionModel().setSelected(example, true);
				
			}
		}, ClickEvent.getType());
	}
}
