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

package thothbot.parallax.demo.client;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import thothbot.parallax.core.client.RenderingPanel;
import thothbot.parallax.core.client.debugger.Debugger;
import thothbot.parallax.core.client.renderers.WebGLRenderer;
import thothbot.parallax.demo.resources.DemoResources;

import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.Element;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.safehtml.shared.SafeHtml;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.Image;
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

	@UiField(provided = true)
	SimpleLayoutPanel debuggerPanel;

	/**
	 * Used to show a name of an example
	 */
	@UiField(provided = true)
	SimpleLayoutPanel nameField;
	
	/**
	 * Toggle button to on/off animation. Just for fun
	 */
	@UiField(provided = true)
	public ToggleButton switchAnimation;
	
	@UiField(provided = true)
	public ToggleButton switchFullScreen;

	@UiField(provided = true)
	public ToggleButton switchEffectNone;
	
	@UiField(provided = true)
	public ToggleButton switchEffectAnaglyph;
	
	@UiField(provided = true)
	public ToggleButton switchEffectStereo;
	
	@UiField(provided = true)
	public ToggleButton switchEffectParallaxBarrier;
	
	@UiField(provided = true)
	public ToggleButton switchEffectOculusRift;
	
	List<ToggleButton> effectButtons;
		
	private Debugger debugger;
	
	private ClickHandler handler = new ClickHandler(){
        @Override
        public void onClick(ClickEvent event) {

        	for(ToggleButton button: effectButtons) {
        		if(event.getSource().equals(button)) {
                    button.setDown(true);
                } else {
                	button.setDown(false);
                }
        	}
        }
    };

	public ContentWidgetView()
	{
		this.examplePanel = new SimpleLayoutPanel();
		this.debuggerPanel = new SimpleLayoutPanel();
		this.nameField = new SimpleLayoutPanel();
		
		switchAnimation = new ToggleButton(new Image(Demo.resources.switchAnimation()));
		switchFullScreen = new ToggleButton(new Image(Demo.resources.switchFullscreen()));
		
		switchEffectNone = new ToggleButton(new Image(Demo.resources.switchEffectNone()));
		switchEffectAnaglyph = new ToggleButton(new Image(Demo.resources.switchEAnaglyph()));
		switchEffectStereo = new ToggleButton(new Image(Demo.resources.switchEStereo()));
		switchEffectParallaxBarrier = new ToggleButton(new Image(Demo.resources.switchEParallaxBarrier()));
		switchEffectOculusRift = new ToggleButton(new Image(Demo.resources.switchEOculusRift()));
		
		initWidget(uiBinder.createAndBindUi(this));
		
		this.effectButtons = new ArrayList<ToggleButton>();
		for(ToggleButton button: Arrays.asList(switchEffectNone, switchEffectAnaglyph, switchEffectStereo, switchEffectParallaxBarrier, switchEffectOculusRift)) {
			button.addClickHandler(handler);
			this.effectButtons.add(button);
		}
	}

	public void setDescription(SafeHtml html)
	{
		this.descriptionField.setInnerHTML(html.asString());
	}

	public void setName(String text)
	{
		this.nameField.getElement().setInnerText(text);
	}
	
	public void setRenderingPanel(RenderingPanel renderingPanel) 
	{
		this.examplePanel.setWidget(renderingPanel);
	}
	
	public void setDebugger(WebGLRenderer renderer) 
	{
		this.debugger = new Debugger(renderer.getInfo());
		this.debuggerPanel.setWidget(this.debugger);		
	}
	
	public Debugger getDebugger() {
		return this.debugger;
	}
}
