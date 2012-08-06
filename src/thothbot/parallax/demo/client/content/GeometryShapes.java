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

import java.util.Arrays;

import thothbot.parallax.core.shared.cameras.PerspectiveCamera;
import thothbot.parallax.core.shared.core.Color;
import thothbot.parallax.core.shared.core.ExtrudeGeometry;
import thothbot.parallax.core.shared.core.Geometry;
import thothbot.parallax.core.shared.curves.Shape;
import thothbot.parallax.core.shared.lights.DirectionalLight;
import thothbot.parallax.core.shared.materials.LineBasicMaterial;
import thothbot.parallax.core.shared.materials.MeshBasicMaterial;
import thothbot.parallax.core.shared.materials.MeshLambertMaterial;
import thothbot.parallax.core.shared.materials.ParticleBasicMaterial;
import thothbot.parallax.core.shared.objects.DimensionalObject;
import thothbot.parallax.core.shared.objects.Line;
import thothbot.parallax.core.shared.objects.Object3D;
import thothbot.parallax.core.shared.objects.ParticleSystem;
import thothbot.parallax.core.shared.utils.SceneUtils;
import thothbot.parallax.demo.client.ContentWidget;
import thothbot.parallax.demo.client.Demo;
import thothbot.parallax.demo.client.DemoAnnotations.DemoSource;

import com.google.gwt.core.client.GWT;
import com.google.gwt.core.client.RunAsyncCallback;
import com.google.gwt.resources.client.ImageResource;
import com.google.gwt.user.client.rpc.AsyncCallback;

public final class GeometryShapes extends ContentWidget 
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
							50, // fov
							getRenderer().getCanvas().getAspectRation(), // aspect 
							1, // near
							1000 // far 
					)); 
		}

		@Override
		protected void onStart()
		{
			getCamera().getPosition().set( 0, 150, 500 );
			getScene().addChild(getCamera());
			
			DirectionalLight light = new DirectionalLight( 0xffffff );
			light.getPosition().set( 0, 0, 1 );
			getScene().addChild( light );

			Object3D parent = new Object3D();
			parent.getPosition().setY( 50 );
			getScene().addChild( parent );
			
			Shape triangleShape = new Shape();
			triangleShape.moveTo(  80, 20 );
			triangleShape.lineTo(  40, 80 );
			triangleShape.lineTo( 120, 80 );
			triangleShape.lineTo(  80, 20 ); // close path

//			var extrudeSettings = {	amount: 20,  bevelEnabled: true, bevelSegments: 2, steps: 2 };
			
			ExtrudeGeometry.ExtrudeGeometryParameters extrudeSettings = new ExtrudeGeometry.ExtrudeGeometryParameters();
			extrudeSettings.amount = 20;
			extrudeSettings.bevelEnabled = true;
			extrudeSettings.bevelSegments = 2;
			extrudeSettings.steps = 2;
			
			Geometry triangle3d = triangleShape.extrude( extrudeSettings );
			Geometry trianglePoints = triangleShape.createPointsGeometry();
			Geometry triangleSpacedPoints = triangleShape.createSpacedPointsGeometry();
			
			addGeometry( parent, triangle3d, trianglePoints, triangleSpacedPoints, new Color(0xffee00), -180, 0, 0, 0, 0, 0, 1 );
		}
		
		private void addGeometry(Object3D parent, Geometry geometry, Geometry points, Geometry spacedPoints, Color color, 
				double x, double y, double z, double rx, double ry, double rz, double s 
		) {

			// 3d shape

			MeshLambertMaterial meshMat1 = new MeshLambertMaterial();
			meshMat1.setColor( color );

			MeshBasicMaterial meshMat2 = new MeshBasicMaterial();
			meshMat2.setColor(color);
			meshMat2.setWireframe(true);
			meshMat2.setTransparent(true);
			
			DimensionalObject mesh = SceneUtils.createMultiMaterialObject( geometry, Arrays.asList( meshMat1, meshMat2 ) );
			mesh.getPosition().set( x, y, z - 75.0 );
			mesh.getRotation().set( rx, ry, rz );
			mesh.getScale().set( s );
			parent.addChild( mesh );

			// solid line

			LineBasicMaterial line1Mat = new LineBasicMaterial();
			line1Mat.setColor(color);
			line1Mat.setLinewidth(2);

			Line line1 = new Line( points, line1Mat );
			line1.getPosition().set( x, y, z + 25.0 );
			line1.getRotation().set( rx, ry, rz );
			line1.getScale().set( s );
			parent.addChild( line1 );

			// transparent line from real points

			LineBasicMaterial line2Mat = new LineBasicMaterial();
			line2Mat.setColor(color);
			line2Mat.setOpacity(0.5);
			
			Line line2 = new Line( points, line2Mat );
			line2.getPosition().set( x, y, z + 75.0 );
			line2.getRotation().set( rx, ry, rz );
			line2.getScale().set( s );
			parent.addChild( line2 );

			// vertices from real points

			ParticleBasicMaterial particleMat = new ParticleBasicMaterial();
			particleMat.setColor(color);
			particleMat.setSize(2);
			particleMat.setOpacity(0.75);
			
			Geometry pgeo1 = points.clone();
			ParticleSystem particles = new ParticleSystem( pgeo1, particleMat );
			particles.getPosition().set( x, y, z + 75.0 );
			particles.getRotation().set( rx, ry, rz );
			particles.getScale().set( s );
			parent.addChild( particles );

			// transparent line from equidistance sampled points

			LineBasicMaterial line3Mat = new LineBasicMaterial();
			line3Mat.setColor(color);
			line3Mat.setOpacity(0.2);
			
			Line line3 = new Line( spacedPoints, line3Mat );
			line3.getPosition().set( x, y, z + 100.0 );
			line3.getRotation().set( rx, ry, rz );
			line3.getScale().set( s );
			parent.addChild( line3 );

			// equidistance sampled points

			ParticleBasicMaterial particles2Mat = new ParticleBasicMaterial();
			particles2Mat.setColor(color);
			particles2Mat.setSize(2);
			particles2Mat.setOpacity(0.5);
			
			Geometry pgeo2 = spacedPoints.clone();
			ParticleSystem particles2 = new ParticleSystem( pgeo2, particles2Mat );
			particles2.getPosition().set( x, y, z + 100.0 );
			particles2.getRotation().set( rx, ry, rz );
			particles2.getScale().set( s );
			parent.addChild( particles2 );
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
		
	public GeometryShapes() 
	{
		super("Cube and texture", "Drag mouse to spin. This example based on the three.js example.");
	}
	
	@Override
	public DemoScene onInitialize()
	{
		return new DemoScene();
	}

	@Override
	public ImageResource getIcon()
	{
		return Demo.resources.exampleGeometryShapes();
	}
	
	@Override
	protected void asyncOnInitialize(final AsyncCallback<DemoAnimatedScene> callback)
	{
		GWT.runAsync(GeometryShapes.class, new RunAsyncCallback() 
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
