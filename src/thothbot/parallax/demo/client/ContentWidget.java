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
import thothbot.parallax.core.client.AnimationReadyEvent;
import thothbot.parallax.core.client.AnimationReadyHandler;
import thothbot.parallax.core.client.AnimatedScene;
import thothbot.parallax.core.client.RenderingPanel.RenderPanelAttributes;
import thothbot.parallax.demo.resources.DemoResources;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.http.client.Request;
import com.google.gwt.http.client.RequestBuilder;
import com.google.gwt.http.client.RequestCallback;
import com.google.gwt.http.client.RequestException;
import com.google.gwt.http.client.Response;
import com.google.gwt.resources.client.ImageResource;
import com.google.gwt.safehtml.shared.SafeHtml;
import com.google.gwt.safehtml.shared.SafeHtmlUtils;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.SimpleLayoutPanel;

/**
 * A widget used to show Parallax examples.
 */
public abstract class ContentWidget extends SimpleLayoutPanel implements AnimationReadyHandler
{

	/**
	 * Generic callback used for asynchronously loaded data.
	 * 
	 * @param <T> the data type
	 */
	public static interface Callback<T>
	{
		void onError();
		void onSuccess(T value);
	}

	/**
	 * {@link RenderingPanel} where example will be shown
	 */
	protected RenderingPanel renderingPanel;
	
	/**
	 * A description of an example.
	 */
	private final SafeHtml description;

	/**
	 * The name of the example.
	 */
	private final String name;

	/**
	 * The source code associated with an example.
	 */
	private String sourceCode;

	/**
	 * The view that holds the name, description, and example.
	 */
	private ContentWidgetView view;

	/**
	 * Whether the demo widget has been initialized.
	 */
	private boolean widgetInitialized;

	/**
	 * Whether the demo widget is (asynchronously) initializing.
	 */
	private boolean widgetInitializing;
	

	/**
	 * Default constructor should be called in an example (daughter) class
	 * 
	 * @param name
	 *            a text name of an example
	 * @param description
	 *            a text description of an example
	 */
	public ContentWidget(String name, String description) 
	{
		this.name = name;
		this.description = SafeHtmlUtils.fromString(description);
	}

	/**
	 * This is basic preparation for Demo Scene
	 */
	protected abstract class DemoAnimatedScene extends AnimatedScene{};
	
	/**
	 * This is called when the example is first initialized.
	 * 
	 * @return a {@link DemoAnimatedScene} to add to the {@link RenderingPanel}
	 */
	protected abstract DemoAnimatedScene onInitialize();

	protected abstract void asyncOnInitialize(final AsyncCallback<DemoAnimatedScene> callback);

	/**
	 * Get the simple filename of a class (name without dots).
	 * 
	 * @param c
	 *            the class
	 */
	protected static String getSimpleName(Class<?> c)
	{
		String name = c.getName();
		return name.substring(name.lastIndexOf(".") + 1);
	}
	
	/**
	 * Get the token for a given content widget.
	 * 
	 * @return the content widget token.
	 */
	public String getContentWidgetToken()
	{
		return getSimpleName(this.getClass());
	}
	
	/**
	 * Get a name of an example to use as a title.
	 * 
	 * @return a name for this example
	 */
	public final String getName()
	{
		return name;
	}

	/**
	 * Get a description of an example.
	 * 
	 * @return a description for an example
	 */
	public final SafeHtml getDescription()
	{
		return description;
	}

	/**
	 * Get an image of an example to show on the index page
	 *  
	 * @return ImageResource
	 */
	public abstract ImageResource getIcon();

	/**
	 * Request the source code associated with an example.
	 * 
	 * @param callback
	 *            the callback used when the source become available
	 */
	public void getSource(final Callback<String> callback)
	{
		if (sourceCode != null) 
		{
			callback.onSuccess(sourceCode);
		} 
		else 
		{
			RequestCallback rc = new RequestCallback() 
			{
				public void onError(Request request, Throwable exception)
				{
					callback.onError();
				}

				public void onResponseReceived(Request request, Response response)
				{
					sourceCode = response.getText();
					callback.onSuccess(sourceCode);
				}
			};

			String className = this.getClass().getName();
			className = className.substring(className.lastIndexOf(".") + 1);
			sendSourceRequest(rc, DemoResources.DST_SOURCE_EXAMPLE + className + ".html");
		}
	}
	
	/**
	 * Initialize {@link RenderingPanel} attributes. This method can be
	 * overridden by an example. For example to change clearColor or other
	 * options. For more information see {@link RenderingPanel.RenderPanelAttributes}
	 * 
	 * @return {@link RenderingPanel.RenderPanelAttributes}
	 */
	public RenderPanelAttributes getRenderPanelAttributes()
	{
		RenderPanelAttributes att = new RenderPanelAttributes();
		att.clearColor         = 0x111111;
		att.clearAlpha         = 1.0f;
		att.isDebugEnabled     = true;
		
		return att;
	}
	
	/**
	 * This event called when {@link RenderingPanel} is ready to animate a 
	 * {@link AnimatedScene} in loaded example.
	 */
	public void onAnimationReady(AnimationReadyEvent event)
	{
    	view.getAnimationSwitch().setEnabled(true);
    	view.getAnimationSwitch().setDown(true);
    	view.getAnimationSwitch().addClickHandler(new ClickHandler() {
    		public void onClick(ClickEvent event) {
    			if (view.getAnimationSwitch().isDown())
    				ContentWidget.this.renderingPanel.getRenderingScene().run();
    			else
    				ContentWidget.this.renderingPanel.getRenderingScene().stop();
    		}
    	});
	}

	/**
	 * Called when an example attached with parent Widget.
	 */
	@Override
	protected void onLoad()
	{
		if (view == null) 
		{		
			view = new ContentWidgetView();
			view.setName(getName());
			view.setDescription(getDescription());
			setWidget(view);
		}

		ensureWidgetInitialized();
		super.onLoad();
	}
	
	/**
	 * Ensure that an example has been initialized. Note that
	 * initialization can fail if there is a network failure.
	 */
	private void ensureWidgetInitialized()
	{
		if (widgetInitializing || widgetInitialized)
			return;

		widgetInitializing = true;

		asyncOnInitialize(new AsyncCallback<DemoAnimatedScene>() {
			public void onFailure(Throwable reason)
			{
				widgetInitializing = false;
				Window.alert("Failed to download code for this widget (" + reason + ")");
			}

			public void onSuccess(DemoAnimatedScene demoAnimatedScene)
			{
				widgetInitializing = false;
				widgetInitialized = true;

				// Finally setup RenderingPanel attached to the loaded example
		        if (demoAnimatedScene != null)
		        {
		    		final RenderingPanel renderingPanel = new RenderingPanel(getRenderPanelAttributes());
		    		renderingPanel.setRenderingScene(demoAnimatedScene);
		    		renderingPanel.addAnimationReadyEventHandler(ContentWidget.this);
		        	ContentWidget.this.renderingPanel = renderingPanel;

		        	view.setRenderingPanel(renderingPanel);
		        }
			}
		});
	}

	/**
	 * Send a request for source code.
	 * 
	 * @param callback
	 *            the {@link RequestCallback} to send
	 * @param url
	 *            the URL to target
	 */
	private void sendSourceRequest(RequestCallback callback, String url)
	{
		RequestBuilder builder = new RequestBuilder(RequestBuilder.GET, GWT.getModuleBaseURL() + url);
		builder.setCallback(callback);
		try 
		{
			builder.send();
		} 
		catch (RequestException e) 
		{
			callback.onError(null, e);
		}
	}
}
