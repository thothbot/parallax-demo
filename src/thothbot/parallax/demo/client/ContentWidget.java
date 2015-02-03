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

import thothbot.parallax.core.client.AnimatedScene;
import thothbot.parallax.core.client.RenderingPanel;
import thothbot.parallax.core.client.context.Canvas3d;
import thothbot.parallax.core.client.events.AnimationReadyEvent;
import thothbot.parallax.core.client.events.AnimationReadyHandler;
import thothbot.parallax.core.client.events.Context3dErrorEvent;
import thothbot.parallax.core.client.events.Context3dErrorHandler;
import thothbot.parallax.core.client.events.SceneLoadingEvent;
import thothbot.parallax.core.client.events.SceneLoadingHandler;
import thothbot.parallax.core.client.renderers.Plugin;
import thothbot.parallax.core.shared.Log;
import thothbot.parallax.demo.resources.DemoResources;
import thothbot.parallax.plugins.effects.Anaglyph;
import thothbot.parallax.plugins.effects.ParallaxBarrier;
import thothbot.parallax.plugins.effects.Stereo;

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
public abstract class ContentWidget extends SimpleLayoutPanel 
	implements AnimationReadyHandler, SceneLoadingHandler, Context3dErrorHandler
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
	
	private LoadingPanel loadingPanel;
	
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
	
	private Plugin effectPlugin;
	
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
	 * This event called when {@link RenderingPanel} is ready to animate a 
	 * {@link AnimatedScene} in loaded example.
	 */
	public void onAnimationReady(AnimationReadyEvent event)
	{
		view.setDebugger(this.renderingPanel.getRenderer());
		
		this.renderingPanel.setAnimationUpdateHandler(new RenderingPanel.AnimationUpdateHandler() {
			
			@Override
			public void onUpdate(double duration) {
				view.getDebugger().update();
			}
		});

    	view.getAnimationSwitch().setEnabled(true);
    	view.getAnimationSwitch().setDown(true);
    	view.getAnimationSwitch().addClickHandler(new ClickHandler() {
    		public void onClick(ClickEvent event) {
    			if (view.getAnimationSwitch().isDown())
    				ContentWidget.this.renderingPanel.getAnimatedScene().run();
    			else
    				ContentWidget.this.renderingPanel.getAnimatedScene().stop();
    		}
    	});
    	
    	view.getFullscreenSwitch().setEnabled(this.renderingPanel.isSupportFullScreen());
    	view.getFullscreenSwitch().addClickHandler(new ClickHandler() {
    		public void onClick(ClickEvent event) {
    			ContentWidget.this.renderingPanel.toFullScreen();
    		}
    	});
    	
    	view.effectAnaglyphSwitch.addClickHandler(new ClickHandler() {
    		public void onClick(ClickEvent event) {
    			if (view.effectAnaglyphSwitch.isDown())
    				ContentWidget.this.effectPlugin = new Anaglyph(
    						ContentWidget.this.renderingPanel.getRenderer(), 
    						ContentWidget.this.renderingPanel.getAnimatedScene().getScene());
    		}
    	});
    	
    	view.effectStereoSwitch.addClickHandler(new ClickHandler() {
    		public void onClick(ClickEvent event) {
    			if (view.effectStereoSwitch.isDown())
    				ContentWidget.this.effectPlugin = new Stereo(
    						ContentWidget.this.renderingPanel.getRenderer(), 
    						ContentWidget.this.renderingPanel.getAnimatedScene().getScene());
    		}
    	});
    	
    	view.effectC3d.addClickHandler(new ClickHandler() {
    		public void onClick(ClickEvent event) {
    			if (view.effectC3d.isDown())
    				ContentWidget.this.effectPlugin = new ParallaxBarrier(
    						ContentWidget.this.renderingPanel.getRenderer(), 
    						ContentWidget.this.renderingPanel.getAnimatedScene().getScene());
    		}
    	});

    	view.effectNoneSwitch.addClickHandler(new ClickHandler() {
    		public void onClick(ClickEvent event) {
    			if (view.effectNoneSwitch.isDown())
    				ContentWidget.this.renderingPanel.getRenderer().deletePlugin(ContentWidget.this.effectPlugin);
    		}
    	});

	}
	
	@Override
	public void onSceneLoading(SceneLoadingEvent event) 
	{
		Log.error(event.isLoaded());
		if(event.isLoaded() && loadingPanel != null) 
		{
			loadingPanel.hide();
		}
		else if(this.loadingPanel == null)
		{
			this.loadingPanel = new LoadingPanel();
			this.loadingPanel.show();
			this.renderingPanel.add(this.loadingPanel);
		}
	}
	
	@Override
	public void onContextError(Context3dErrorEvent event) 
	{
		if(this.loadingPanel != null && this.loadingPanel.isVisible())
		{
			this.loadingPanel.hide();
		}
		
		this.renderingPanel.add(new BadCanvasPanel(event.getMessage()));
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
	
	@Override
	protected void onUnload() 
	{
		view = null;
		renderingPanel = null;
		widgetInitializing = widgetInitialized = false;
		super.onUnload();
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
		        	RenderingPanel renderingPanel = new RenderingPanel();
		    		ContentWidget.this.renderingPanel = renderingPanel;
		    		renderingPanel.addSceneLoadingHandler(ContentWidget.this);
		    		renderingPanel.addCanvas3dErrorHandler(ContentWidget.this);
		    		renderingPanel.addAnimationReadyHandler(ContentWidget.this);

		    		ContentWidget.this.loadRenderingPanelAttributes(renderingPanel);
		    		
		    		renderingPanel.setAnimatedScene(demoAnimatedScene);
		    		
		        	view.setRenderingPanel(renderingPanel);
		        }
			}
		});
	}
	
	protected void loadRenderingPanelAttributes(RenderingPanel renderingPanel) 
	{
		/* Empty */
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
