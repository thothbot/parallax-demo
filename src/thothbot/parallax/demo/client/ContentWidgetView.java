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

import thothbot.parallax.core.client.RenderingPanel;

import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.Element;
import com.google.gwt.safehtml.shared.SafeHtml;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.LayoutPanel;
import com.google.gwt.user.client.ui.ResizeComposite;
import com.google.gwt.user.client.ui.SimpleLayoutPanel;
import com.google.gwt.user.client.ui.ToggleButton;
import com.google.gwt.user.client.ui.Widget;

/**
 * A view of a {@link ContentWidget}.
 */
public class ContentWidgetView extends ResizeComposite
{

	interface ContentWidgetViewUiBinder extends UiBinder<Widget, ContentWidgetView>{
	}

	private static ContentWidgetViewUiBinder uiBinder = GWT.create(ContentWidgetViewUiBinder.class);

	/**
	 * Used to show description of an example
	 */
	@UiField
	Element descriptionField;

	/**
	 * Main panel where will be {@link RenderingPanel} located
	 */
	@UiField(provided = true)
	SimpleLayoutPanel examplePanel;

	/**
	 * Used to show a name of an example
	 */
	@UiField
	Element nameField;
	
	/**
	 * Toggle button to on/off animation. Just for fun
	 */
	@UiField
	ToggleButton animationSwitch;
	 
	public ContentWidgetView()
	{
		this.examplePanel = new SimpleLayoutPanel();
		initWidget(uiBinder.createAndBindUi(this));
	}

	public ToggleButton getAnimationSwitch()
	{
		return this.animationSwitch;
	}

	public void setDescription(SafeHtml html)
	{
		this.descriptionField.setInnerHTML(html.asString());
	}

	public void setName(String text)
	{
		this.nameField.setInnerText(text);
	}
	
	public void setRenderingPanel(RenderingPanel renderingPanel) 
	{
		this.examplePanel.setWidget(renderingPanel);
	}
}
