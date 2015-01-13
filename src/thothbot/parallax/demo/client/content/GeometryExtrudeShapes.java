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

package thothbot.parallax.demo.client.content;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import thothbot.parallax.core.client.RenderingPanel;
import thothbot.parallax.core.shared.cameras.PerspectiveCamera;
import thothbot.parallax.core.shared.core.DimensionalObject;
import thothbot.parallax.core.shared.core.Geometry;
import thothbot.parallax.core.shared.core.Object3D;
import thothbot.parallax.core.shared.curves.SplineCurve3;
import thothbot.parallax.core.shared.curves.SplineCurve3Closed;
import thothbot.parallax.core.shared.curves.Path;
import thothbot.parallax.core.shared.curves.Shape;
import thothbot.parallax.core.shared.lights.DirectionalLight;
import thothbot.parallax.core.shared.materials.Material;
import thothbot.parallax.core.shared.materials.MeshBasicMaterial;
import thothbot.parallax.core.shared.materials.MeshLambertMaterial;
import thothbot.parallax.core.shared.math.Color;
import thothbot.parallax.core.shared.math.Mathematics;
import thothbot.parallax.core.shared.math.Vector2;
import thothbot.parallax.core.shared.math.Vector3;
import thothbot.parallax.core.shared.utils.SceneUtils;
import thothbot.parallax.demo.client.ContentWidget;
import thothbot.parallax.demo.client.Demo;
import thothbot.parallax.demo.client.DemoAnnotations.DemoSource;

import com.google.gwt.core.client.GWT;
import com.google.gwt.core.client.RunAsyncCallback;
import com.google.gwt.resources.client.ImageResource;
import com.google.gwt.user.client.rpc.AsyncCallback;

public class GeometryExtrudeShapes extends ContentWidget
{
	/*
	 * Prepare Rendering Scene
	 */
	@DemoSource
	class DemoScene extends DemoAnimatedScene 
	{
		private static final String texture = "./static/textures/crate.gif";
		
		PerspectiveCamera camera;
		Object3D parentObject;
        
		@Override
		protected void onStart()
		{
			camera = new PerspectiveCamera( 50,
					getRenderer().getAbsoluteAspectRation(), 
					1, 
					1000 
				);
			camera.getPosition().set(0, 150, 150);
			
			DirectionalLight light = new DirectionalLight( 0xffffff );
			light.getPosition().set( 0, 0, 1 );
			getScene().add( light );

			this.parentObject = new Object3D();
			this.parentObject.getPosition().setY(50);
			getScene().add( this.parentObject );
			
			//Closed
			SplineCurve3 extrudeBend = new SplineCurve3(Arrays.asList( 
					new Vector3( 30, 12, 83),
					new Vector3( 40, 20, 67),
					new Vector3( 60, 40, 99),
					new Vector3( 10, 60, 49),
					new Vector3( 25, 80, 40)));

			SplineCurve3 pipeSpline = new SplineCurve3(Arrays.asList( 
					new Vector3(0, 10, -10), 
					new Vector3(10, 0, -10), 
					new Vector3(20, 0, 0), 
					new Vector3(30, 0, 10), 
					new Vector3(30, 0, 20), 
					new Vector3(20, 0, 30), 
					new Vector3(10, 0, 30), 
					new Vector3(0, 0, 30), 
					new Vector3(-10, 10, 30), 
					new Vector3(-10, 20, 30), 
					new Vector3(0, 30, 30), 
					new Vector3(10, 30, 30), 
					new Vector3(20, 30, 15), 
					new Vector3(10, 30, 10), 
					new Vector3(0, 30, 10), 
					new Vector3(-10, 20, 10), 
					new Vector3(-10, 10, 10), 
					new Vector3(0, 0, 10), 
					new Vector3(10, -10, 10), 
					new Vector3(20, -15, 10), 
					new Vector3(30, -15, 10), 
					new Vector3(40, -15, 10), 
					new Vector3(50, -15, 10), 
					new Vector3(60, 0, 10), 
					new Vector3(70, 0, 0), 
					new Vector3(80, 0, 0), 
					new Vector3(90, 0, 0),
					new Vector3(100, 0, 0)));

			SplineCurve3Closed sampleClosedSpline = new SplineCurve3Closed(Arrays.asList( 
					new Vector3(0, -40, -40),
					new Vector3(0, 40, -40),
					new Vector3(0, 140, -40),
					new Vector3(0, 40, 40),
					new Vector3(0, -40, 40)));

//			List<Vector3> randomPoints = new ArrayList<Vector3>();
//
//			for (int i=0; i<10; i++)
//				randomPoints.add(new Vector3((double)Math.random() * 200.0f, (double)Math.random() * 200.0f, (double)Math.random() * 200.0f ));
//
//			SplineCurve3 randomSpline =  new SplineCurve3(randomPoints);
			
			SplineCurve3 randomSpline = new SplineCurve3(Arrays.asList( 
					new Vector3(-40, -40, 0),
					new Vector3(40, -40, 0),
					new Vector3( 140, -40, 0),
					new Vector3(40, 40, 0),
					new Vector3(-40, 40, 20)));

//			ExtrudeGeometry.ExtrudeGeometryParameters extrudeParameters = new ExtrudeGeometry.ExtrudeGeometryParameters();
//			extrudeParameters.amount = 200;
//			extrudeParameters.bevelEnabled = true;
//			extrudeParameters.bevelSegments = 2;
//			extrudeParameters.steps = 150;
//			extrudeParameters.extrudePath = randomSpline;

			// CircleGeometry

			double circleRadius = 4.0;
			Shape circleShape = new Shape();
			circleShape.moveTo( 0, circleRadius );
			circleShape.quadraticCurveTo( circleRadius, circleRadius, circleRadius, 0 );
			circleShape.quadraticCurveTo( circleRadius, -circleRadius, 0, -circleRadius );
			circleShape.quadraticCurveTo( -circleRadius, -circleRadius, -circleRadius, 0 );
			circleShape.quadraticCurveTo( -circleRadius, circleRadius, 0, circleRadius);

			double rectLength = 12.0;
			double rectWidth = 4.0;

			Shape rectShape = new Shape();

			rectShape.moveTo( -rectLength/2, -rectWidth/2 );
			rectShape.lineTo( -rectLength/2, rectWidth/2 );
			rectShape.lineTo( rectLength/2, rectWidth/2 );
			rectShape.lineTo( rectLength/2, -rectLength/2 );
			rectShape.lineTo( -rectLength/2, -rectLength/2 );

			// Smiley

			Shape smileyShape = new Shape();
			smileyShape.moveTo( 80, 40 );
			smileyShape.arc( 40, 40, 40, 0.0, Math.PI * 2.0, false );

			Path smileyEye1Path = new Path();
			smileyEye1Path.moveTo( 35, 20 );
			smileyEye1Path.arc( 25, 20, 10, 0.0, Math.PI * 2.0, true );
			smileyShape.getHoles().add( smileyEye1Path );

			Path smileyEye2Path = new Path();
			smileyEye2Path.moveTo( 65, 20 );
			smileyEye2Path.arc( 55, 20, 10, 0.0, Math.PI * 2.0, true );
			smileyShape.getHoles().add( smileyEye2Path );

			Path smileyMouthPath = new Path();

			smileyMouthPath.moveTo( 20, 40 );
			smileyMouthPath.quadraticCurveTo( 40, 60, 60, 40 );
			smileyMouthPath.bezierCurveTo( 70, 45, 70, 50, 60, 60 );
			smileyMouthPath.quadraticCurveTo( 40, 80, 20, 60 );
			smileyMouthPath.quadraticCurveTo( 5, 50, 20, 40 );

			smileyShape.getHoles().add( smileyMouthPath );

			List<Vector2> pts = new ArrayList<Vector2>();
			int starPoints = 5;
			double l;
			for (int i = 0; i < starPoints * 2; i++) 
			{
				l = (Mathematics.isEven(i)) ? 5.0 : 10.0; 
				double a = i / starPoints * Math.PI;

				pts.add(new Vector2(Math.cos(a) * l, Math.sin(a) * l ));
			}

			Shape starShape = new Shape(pts);
//			ExtrudeGeometry circle3d = starShape.extrude( extrudeParameters ); //circleShape rectShape smileyShape starShape

//			TubeGeometry tubeGeometry = new TubeGeometry((CurvePath) extrudeParameters.extrudePath, 150, 4.0, 5, false, true);     

//			addGeometry( circle3d, new Color(0xff1111),  
//					-100f, 0, 0,     
//					0, 0, 0, 
//					1);

//			addGeometry( tubeGeometry, new Color(0x00ff11),  
//					0, 0, 0,     
//					0, 0, 0, 
//					1);  
		}
		
		private void addGeometry( Geometry geometry, Color color, double x, double y, double z, double rx, double ry, double rz, double s ) 
		{
			// 3d shape
			MeshLambertMaterial ml = new MeshLambertMaterial();
			ml.setColor(color);
			ml.setOpacity( 0.2 );
			ml.setTransparent(true);

			MeshBasicMaterial mb = new MeshBasicMaterial();
			mb.setColor( new Color(0x000000) );
			mb.setWireframe( true );
			mb.setOpacity( 0.3 );

			List<Material> materials= new ArrayList<Material>();
			materials.add(ml);
			materials.add(mb);
			Object3D mesh = SceneUtils.createMultiMaterialObject( geometry, materials );     

			mesh.getPosition().set( x, y, z - 75.0 );

			mesh.getScale().set( s );

//			if (geometry.debug) 
//				mesh.add(geometry.debug);

			this.parentObject.add( mesh );
			getRenderer().render(getScene(), camera);
		}
		
		@Override
		protected void onUpdate(double duration)
		{
		}
	}

	public GeometryExtrudeShapes() 
	{
		super("Geometry - extrude shapes", "Shapes Extrusion via Spline path. (Drag to spin)");
	}
	
	@Override
	protected void loadRenderingPanelAttributes(RenderingPanel renderingPanel) 
	{
		super.loadRenderingPanelAttributes(renderingPanel);
		renderingPanel.setBackground(0xCCCCCC);
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
		GWT.runAsync(GeometryExtrudeShapes.class, new RunAsyncCallback() 
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
