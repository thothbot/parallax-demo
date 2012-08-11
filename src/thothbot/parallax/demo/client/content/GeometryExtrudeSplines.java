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
 * Squirrel. If not, see http://www.gnu.org/licenses/.
 */

package thothbot.parallax.demo.client.content;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import thothbot.parallax.core.client.AnimationReadyEvent;
import thothbot.parallax.core.client.widget.Debugger;
import thothbot.parallax.core.shared.cameras.PerspectiveCamera;
import thothbot.parallax.core.shared.core.Vector3;
import thothbot.parallax.core.shared.curves.Curve;
import thothbot.parallax.core.shared.curves.CurveSpline3D;
import thothbot.parallax.core.shared.curves.CurveSplineClosed3D;
import thothbot.parallax.core.shared.curves.parametric.*;
import thothbot.parallax.demo.client.ContentWidget;
import thothbot.parallax.demo.client.Demo;
import thothbot.parallax.demo.client.DemoAnnotations.DemoSource;
import thothbot.parallax.demo.client.content.GeometryColors.DemoScene;

import com.google.gwt.core.client.GWT;
import com.google.gwt.core.client.RunAsyncCallback;
import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.event.dom.client.MouseMoveEvent;
import com.google.gwt.event.dom.client.MouseMoveHandler;
import com.google.gwt.resources.client.ImageResource;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.CheckBox;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.InlineLabel;
import com.google.gwt.user.client.ui.ListBox;

public final class GeometryExtrudeSplines extends ContentWidget 
{

	/*
	 * Prepare Rendering Scene
	 */
	@DemoSource
	class DemoScene extends DemoAnimatedScene 
	{

		@Override
		protected void loadCamera()
		{
			setCamera(
					new PerspectiveCamera(
							70, // fov
							getRenderer().getCanvas().getAspectRation(), // aspect 
							1, // near
							1000 // far 
					)); 
		}

		@Override
		protected void onStart()
		{
			getCamera().getPosition().setZ(400);
			getScene().addChild(getCamera());
		}
		
		public Map<String, Curve> splines()
		{
			Map<String, Curve> retval = new HashMap<String, Curve>();
			retval.put("GrannyKnot", new CurveGrannyKnot());
			retval.put("HeartCurve", new CurveHeart(3.5));
			retval.put("VivianiCurve", new CurveViviani(70));
			retval.put("KnotCurve", new CurveKnot());
			retval.put("HelixCurve", new CurveHelix());
			retval.put("TrefoilKnot", new CurveTrefoilKnot());
			retval.put("TorusKnot", new CurveTorusKnot(20));
			retval.put("CinquefoilKnot", new CurveCinquefoilKnot(20));
			retval.put("TrefoilPolynomialKnot", new CurveTrefoilPolynomialKnot(14));
			retval.put("FigureEightPolynomialKnot", new CurveFigureEightPolynomialKnot());
			retval.put("DecoratedTorusKnot4a", new CurveDecoratedTorusKnot4a());
			retval.put("DecoratedTorusKnot4b", new CurveDecoratedTorusKnot4b());
		    retval.put("DecoratedTorusKnot5a", new CurveDecoratedTorusKnot5a());
		    retval.put("DecoratedTorusKnot5c", new CurveDecoratedTorusKnot5c());
		    retval.put("PipeSpline", new CurveSpline3D(Arrays.asList(
		             new Vector3(0, 10, -10), new Vector3(10, 0, -10), new Vector3(20, 0, 0), 
		             new Vector3(30, 0, 10), new Vector3(30, 0, 20), new Vector3(20, 0, 30), 
		             new Vector3(10, 0, 30), new Vector3(0, 0, 30), new Vector3(-10, 10, 30), 
		             new Vector3(-10, 20, 30), new Vector3(0, 30, 30), new Vector3(10, 30, 30), 
		             new Vector3(20, 30, 15), new Vector3(10, 30, 10), new Vector3(0, 30, 10), 
		             new Vector3(-10, 20, 10), new Vector3(-10, 10, 10), new Vector3(0, 0, 10), 
		             new Vector3(10, -10, 10), new Vector3(20, -15, 10), new Vector3(30, -15, 10), 
		             new Vector3(40, -15, 10), new Vector3(50, -15, 10), new Vector3(60, 0, 10), 
		             new Vector3(70, 0, 0), new Vector3(80, 0, 0), new Vector3(90, 0, 0), 
		             new Vector3(100, 0, 0))));
		    retval.put("SampleClosedSpline",  new CurveSplineClosed3D(Arrays.asList(
		             new Vector3(0, -40, -40), new Vector3(0, 40, -40), new Vector3(0, 140, -40),
		             new Vector3(0, 40, 40), new Vector3(0, -40, 40) )));
			      
		    return retval;
		}
				
		@Override
		protected void onStop()
		{			
		}
		
		@Override
		protected void onUpdate(double duration)
		{
		}
	}
		
	public GeometryExtrudeSplines() 
	{
		super("Spline Extrusion", "This example based on the three.js example.");
	}
	
	@Override
	public void onAnimationReady(AnimationReadyEvent event)
	{
		super.onAnimationReady(event);

		DemoScene rs = (DemoScene) renderingPanel.getAnimatedScene();
		
		FlowPanel panel = new FlowPanel();
		panel.setStyleName("common-panel", true);
		panel.setStyleName("corner-panel", true);
		this.renderingPanel.add(panel);
		this.renderingPanel.setWidgetLeftWidth(panel, 1, Unit.PX, 25, Unit.EM);
		this.renderingPanel.setWidgetTopHeight(panel, 1, Unit.PX, 10, Unit.EM);
		
		// Splines
		panel.add(new InlineLabel("Spline:"));
		
		ListBox splines = new ListBox();
		for(String key: rs.splines().keySet())
			splines.addItem(key, key);
		
		panel.add(splines);
		
		// Scale
		panel.add(new InlineLabel("Scale:"));

		ListBox scale = new ListBox();
		for(String key: Arrays.asList("1", "2", "4", "6", "10"))
			scale.addItem(key, key);

		panel.add(scale);
		
		// Extrusion Segments
		panel.add(new InlineLabel("Extrusion Segments:"));
		
		ListBox extrusionSegments = new ListBox();
		for(String key: Arrays.asList("50", "100", "200", "400"))
			extrusionSegments.addItem(key, key);
		
		panel.add(extrusionSegments);
		
		// Radius Segments
		panel.add(new InlineLabel("Radius Segments:"));
		
		ListBox radiusSegments = new ListBox();
		for(String key: Arrays.asList("1", "2", "3", "4", "5", "6", "8", "12"))
			radiusSegments.addItem(key, key);
		
		panel.add(radiusSegments);
		
		// Debug normals
		panel.add(new InlineLabel("Debug normals:"));
		CheckBox isDebugNormals = new CheckBox(); 
		panel.add(isDebugNormals);
		
		// Closed
		panel.add(new InlineLabel("Closed:"));
		CheckBox isClosed = new CheckBox(); 
		panel.add(isClosed);
		
		// Camera Spline Animation View
		panel.add(new Button("Camera Spline Animation View: OFF"));
		
		// Look Ahead
		panel.add(new InlineLabel("Look Ahead:"));
		CheckBox isLookAhead = new CheckBox(); 
		panel.add(isLookAhead);
		
		// Camera Helper
		panel.add(new InlineLabel("Camera Helper:"));
		CheckBox isCameraHelper = new CheckBox(); 
		panel.add(isCameraHelper);
	}
	
	@Override
	public DemoScene onInitialize()
	{
		return new DemoScene();
	}

	@Override
	public ImageResource getIcon()
	{
		return Demo.resources.exampleDefault();
	}
	
	@Override
	protected void asyncOnInitialize(final AsyncCallback<DemoAnimatedScene> callback)
	{
		GWT.runAsync(GeometryExtrudeSplines.class, new RunAsyncCallback() 
		{
			public void onFailure(Throwable caught)
			{
				callback.onFailure(caught);
			}

			public void onSuccess()
			{
				callback.onSuccess(onInitialize());
			}
		});
	}
}
