/*
 * Copyright 2012 Alex Usachev, thothbot@gmail.com
 * 
 * This file based on the JavaScript source file of the THREE.JS project, 
 * licensed under MIT License.
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

package thothbot.squirrel.demo.client.content;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import thothbot.squirrel.core.client.RenderingPanel;
import thothbot.squirrel.core.client.RenderingPanel.RenderPanelAttributes;
import thothbot.squirrel.core.shared.cameras.PerspectiveCamera;
import thothbot.squirrel.core.shared.core.Color3f;
import thothbot.squirrel.core.shared.core.DimentionObject;
import thothbot.squirrel.core.shared.core.ExtrudeGeometry;
import thothbot.squirrel.core.shared.core.Geometry;
import thothbot.squirrel.core.shared.core.Mathematics;
import thothbot.squirrel.core.shared.core.Object3D;
import thothbot.squirrel.core.shared.core.Vector2f;
import thothbot.squirrel.core.shared.core.Vector3f;
import thothbot.squirrel.core.shared.curves.CurveSpline3D;
import thothbot.squirrel.core.shared.curves.CurveSplineClosed3D;
import thothbot.squirrel.core.shared.curves.Path;
import thothbot.squirrel.core.shared.curves.Shape;
import thothbot.squirrel.core.shared.lights.DirectionalLight;
import thothbot.squirrel.core.shared.materials.Material;
import thothbot.squirrel.core.shared.materials.MeshBasicMaterial;
import thothbot.squirrel.core.shared.materials.MeshLambertMaterial;
import thothbot.squirrel.core.shared.materials.MeshBasicMaterial.MeshBasicMaterialOptions;
import thothbot.squirrel.core.shared.materials.MeshLambertMaterial.MeshLambertMaterialOptions;
import thothbot.squirrel.core.shared.utils.SceneUtils;
import thothbot.squirrel.demo.client.ContentWidget;
import thothbot.squirrel.demo.client.Demo;
import thothbot.squirrel.demo.client.DemoAnnotations.DemoSource;

import com.google.gwt.core.client.GWT;
import com.google.gwt.core.client.RunAsyncCallback;
import com.google.gwt.resources.client.ClientBundle;
import com.google.gwt.resources.client.ImageResource;
import com.google.gwt.user.client.rpc.AsyncCallback;

public class GeometryExtrudeShapes extends ContentWidget
{

	/*
	 * Load texture
	 */
	@DemoSource
	public interface Resources extends ClientBundle
	{
		Resources INSTANCE = GWT.create(Resources.class);

		@Source("../../resources/textures/crate.gif")
		ImageResource texture();
	}

	/*
	 * Prepare Rendering Scene
	 */
	@DemoSource
	class DemoScene extends DemoRenderingScene 
	{
		Object3D parentObject;

		@Override
		protected void loadCamera()
		{
			setCamera(
					new PerspectiveCamera(
							50, // fov
							getRenderer().getCanvas().getAspectRation(), // aspect 
							1, // near
							1000 // far 
					)); 
		}
        
		@Override
		protected void onStart()
		{
			getCamera().getPosition().set(0, 150, 500);
			getScene().addChild(getCamera());

			DirectionalLight light = new DirectionalLight( 0xffffff );
			light.getPosition().set( 0, 0, 1 );
			getScene().addChild( light );

			this.parentObject = new Object3D();
			this.parentObject.getPosition().setY(50);
			getScene().addChild( this.parentObject );
			
			//Closed
			CurveSpline3D extrudeBend = new CurveSpline3D(Arrays.asList( 
					new Vector3f( 30, 12, 83),
					new Vector3f( 40, 20, 67),
					new Vector3f( 60, 40, 99),
					new Vector3f( 10, 60, 49),
					new Vector3f( 25, 80, 40)));

			CurveSpline3D pipeSpline = new CurveSpline3D(Arrays.asList( 
					new Vector3f(0, 10, -10), 
					new Vector3f(10, 0, -10), 
					new Vector3f(20, 0, 0), 
					new Vector3f(30, 0, 10), 
					new Vector3f(30, 0, 20), 
					new Vector3f(20, 0, 30), 
					new Vector3f(10, 0, 30), 
					new Vector3f(0, 0, 30), 
					new Vector3f(-10, 10, 30), 
					new Vector3f(-10, 20, 30), 
					new Vector3f(0, 30, 30), 
					new Vector3f(10, 30, 30), 
					new Vector3f(20, 30, 15), 
					new Vector3f(10, 30, 10), 
					new Vector3f(0, 30, 10), 
					new Vector3f(-10, 20, 10), 
					new Vector3f(-10, 10, 10), 
					new Vector3f(0, 0, 10), 
					new Vector3f(10, -10, 10), 
					new Vector3f(20, -15, 10), 
					new Vector3f(30, -15, 10), 
					new Vector3f(40, -15, 10), 
					new Vector3f(50, -15, 10), 
					new Vector3f(60, 0, 10), 
					new Vector3f(70, 0, 0), 
					new Vector3f(80, 0, 0), 
					new Vector3f(90, 0, 0),
					new Vector3f(100, 0, 0)));

			CurveSplineClosed3D sampleClosedSpline = new CurveSplineClosed3D(Arrays.asList( 
					new Vector3f(0, -40, -40),
					new Vector3f(0, 40, -40),
					new Vector3f(0, 140, -40),
					new Vector3f(0, 40, 40),
					new Vector3f(0, -40, 40)));

//			List<Vector3f> randomPoints = new ArrayList<Vector3f>();
//
//			for (int i=0; i<10; i++)
//				randomPoints.add(new Vector3f((float)Math.random() * 200.0f, (float)Math.random() * 200.0f, (float)Math.random() * 200.0f ));
//
//			CurveSpline3D randomSpline =  new CurveSpline3D(randomPoints);
			
			CurveSpline3D randomSpline = new CurveSpline3D(Arrays.asList( 
					new Vector3f(-40, -40, 0),
					new Vector3f(40, -40, 0),
					new Vector3f( 140, -40, 0),
					new Vector3f(40, 40, 0),
					new Vector3f(-40, 40, 20)));

			ExtrudeGeometry.ExtrudeGeometryParameters extrudeParameters = new ExtrudeGeometry.ExtrudeGeometryParameters();
			extrudeParameters.amount = 200;
			extrudeParameters.bevelEnabled = true;
			extrudeParameters.bevelSegments = 2;
			extrudeParameters.steps = 150;
			extrudeParameters.extrudePath = randomSpline;

			// Circle

			float circleRadius = 4f;
			Shape circleShape = new Shape();
			circleShape.moveTo( 0, circleRadius );
			circleShape.quadraticCurveTo( circleRadius, circleRadius, circleRadius, 0 );
			circleShape.quadraticCurveTo( circleRadius, -circleRadius, 0, -circleRadius );
			circleShape.quadraticCurveTo( -circleRadius, -circleRadius, -circleRadius, 0 );
			circleShape.quadraticCurveTo( -circleRadius, circleRadius, 0, circleRadius);

			float rectLength = 12f;
			float rectWidth = 4f;

			Shape rectShape = new Shape();

			rectShape.moveTo( -rectLength/2, -rectWidth/2 );
			rectShape.lineTo( -rectLength/2, rectWidth/2 );
			rectShape.lineTo( rectLength/2, rectWidth/2 );
			rectShape.lineTo( rectLength/2, -rectLength/2 );
			rectShape.lineTo( -rectLength/2, -rectLength/2 );

			// Smiley

			Shape smileyShape = new Shape();
			smileyShape.moveTo( 80f, 40f );
			smileyShape.arc( 40f, 40f, 40f, 0.0f, (float)(Math.PI * 2.0), false );

			Path smileyEye1Path = new Path();
			smileyEye1Path.moveTo( 35, 20 );
			smileyEye1Path.arc( 25f, 20f, 10f, 0.0f, (float)(Math.PI * 2.0), true );
			smileyShape.getHoles().add( smileyEye1Path );

			Path smileyEye2Path = new Path();
			smileyEye2Path.moveTo( 65, 20 );
			smileyEye2Path.arc( 55f, 20f, 10f, 0.0f, (float)(Math.PI * 2.0), true );
			smileyShape.getHoles().add( smileyEye2Path );

			Path smileyMouthPath = new Path();

			smileyMouthPath.moveTo( 20, 40 );
			smileyMouthPath.quadraticCurveTo( 40, 60, 60, 40 );
			smileyMouthPath.bezierCurveTo( 70, 45, 70, 50, 60, 60 );
			smileyMouthPath.quadraticCurveTo( 40, 80, 20, 60 );
			smileyMouthPath.quadraticCurveTo( 5, 50, 20, 40 );

			smileyShape.getHoles().add( smileyMouthPath );

			List<Vector2f> pts = new ArrayList<Vector2f>();
			int starPoints = 5;
			double l;
			for (int i = 0; i < starPoints * 2; i++) 
			{
				l = (Mathematics.isEven(i)) ? 5.0 : 10.0; 
				double a = i / (double)starPoints * Math.PI;

				pts.add(new Vector2f((float)(Math.cos(a) * l), (float)(Math.sin(a) * l )));
			}

			Shape starShape = new Shape(pts);
			ExtrudeGeometry circle3d = starShape.extrude( extrudeParameters ); //circleShape rectShape smileyShape starShape

//			Tube tube = new Tube((CurvePath) extrudeParameters.extrudePath, 150, 4.0f, 5, false, true);     

//			addGeometry( circle3d, new Color3f(0xff1111),  
//					-100f, 0f, 0f,     
//					0f, 0f, 0f, 
//					1f);

//			addGeometry( tube, new Color3f(0x00ff11),  
//					0f, 0f, 0f,     
//					0f, 0f, 0f, 
//					1f);  
		}
		
		private void addGeometry( Geometry geometry, Color3f color, float x, float y, float z, float rx, float ry, float rz, float s ) 
		{
			// 3d shape
			MeshLambertMaterialOptions mlOptions = new MeshLambertMaterialOptions();
			mlOptions.color = color;
			mlOptions.opacity = 0.2f;
			mlOptions.transparent = true;

			MeshBasicMaterialOptions mbOptions = new MeshBasicMaterialOptions();
			mbOptions.color = new Color3f(0x000000);
			mbOptions.wireframe = true;
			mbOptions.opacity = 0.3f;

			List<Material> materials= new ArrayList<Material>();
			materials.add(new MeshLambertMaterial( mlOptions ));
			materials.add(new MeshBasicMaterial( mbOptions ));
			DimentionObject mesh = SceneUtils.createMultiMaterialObject( geometry, materials );     

			mesh.getPosition().set( x, y, z - 75.0f );

			mesh.getScale().set( s, s, s );

//			if (geometry.debug) 
//				mesh.add(geometry.debug);

			this.parentObject.addChild( mesh );
		}
		
		@Override
		protected void onStop()
		{			
		}
		
		@Override
		protected void onUpdate(double duration)
		{
			super.onUpdate(duration);
		}
	}
		
	RenderingPanel renderingPanel;

	public GeometryExtrudeShapes() 
	{
		super("Geometry - extrude shapes", "Shapes Extrusion via Spline path. (Drag to spin)");
	}
	
	@Override
	public RenderPanelAttributes getRenderPanelAttributes()
	{
		RenderPanelAttributes att = super.getRenderPanelAttributes();
		att.clearColor         = 0xCCCCCC;
		
		return att;
	}
	
	@Override
	public DemoScene onInitialize()
	{
		return new DemoScene();
	}
	
	@Override
	public ImageResource getIcon()
	{
		return Demo.resources.example_default();
	}

	@Override
	protected void asyncOnInitialize(final AsyncCallback<DemoRenderingScene> callback)
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
