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

package org.parallax3d.parallax.demo.content.geometries;

import org.parallax3d.parallax.graphics.cameras.PerspectiveCamera;
import org.parallax3d.parallax.graphics.core.Geometry;
import org.parallax3d.parallax.graphics.core.Object3D;
import org.parallax3d.parallax.graphics.materials.LineBasicMaterial;
import org.parallax3d.parallax.graphics.objects.Line;
import org.parallax3d.parallax.math.Color;
import org.parallax3d.parallax.math.Vector3;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public final class LinesSphere extends ContentWidget
{

	class ExampleData 
	{
		double scale;
		Color color;
		double opasity;
		double lineWidth;
		
		public ExampleData(double scale, int color, double opasity, double lineWidth)
		{
			this.scale = scale;
			this.color = new Color(color);
			this.opasity = opasity;
			this.lineWidth = lineWidth;
		}
	}

	/*
	 * Prepare Rendering Scene
	 */
	@DemoSource
	class DemoScene extends DemoAnimatedScene 
	{

		PerspectiveCamera camera;
		
		public int mouseX;
		public int mouseY;
		
		Map<Line, Double> originalScale;
		
		@Override
		protected void onStart()
		{
			camera = new PerspectiveCamera(
					80, // fov
					getRenderer().getAbsoluteAspectRation(), // aspect 
					1, // near
					3000 // far 
			);
			
			camera.getPosition().setZ(1000);

			List<ExampleData> parameters = new ArrayList<ExampleData>();
			parameters.add(new ExampleData( 0.25, 0xff7700, 1.00, 2));
			parameters.add(new ExampleData( 0.50, 0xff9900, 1.00, 1));
			parameters.add(new ExampleData( 0.75, 0xffaa00, 0.75, 1));
			parameters.add(new ExampleData( 1.00, 0xffaa00, 0.50, 1));
			parameters.add(new ExampleData( 1.25, 0x000833, 0.80, 1));
			parameters.add(new ExampleData( 3.00, 0xaaaaaa, 0.75, 2));
			parameters.add(new ExampleData( 3.50, 0xffffff, 0.50, 1));
			parameters.add(new ExampleData( 4.50, 0xffffff, 0.25, 1));
			parameters.add(new ExampleData( 5.50, 0xffffff, 0.125, 1 ));

			Geometry geometry = new Geometry();


			for ( int i = 0; i < 1500; i ++ ) 
			{
				Vector3 vertex1 = new Vector3();
				vertex1.setX(Math.random() * 2.0 - 1.0);
				vertex1.setY(Math.random() * 2.0 - 1.0);
				vertex1.setZ(Math.random() * 2.0 - 1.0);
				vertex1.normalize();
				vertex1.multiply( 450 );

				Vector3 vertex2 = vertex1.clone();
				vertex2.multiply( Math.random() * 0.09 + 1.0 );

				geometry.getVertices().add( vertex1 );
				geometry.getVertices().add( vertex2 );
			}

			this.originalScale = new HashMap<Line, Double>();
			
			for( int i = 0; i < parameters.size(); ++i ) 
			{
				ExampleData p = parameters.get(i);

				LineBasicMaterial material = new LineBasicMaterial();
				material.setColor(p.color);
				material.setOpacity(p.opasity);
				material.setLinewidth(p.lineWidth);

				Line line = new Line( geometry, material, Line.MODE.PIECES );
				line.getScale().set( p.scale );
				this.originalScale.put(line, p.scale);

				line.getRotation().setY( Math.random() * Math.PI );
				line.updateMatrix();
				getScene().add( line );
			}
		}
		
		@Override
		protected void onUpdate(double duration)
		{
			camera.getPosition().addY( ( - mouseY + 200.0 - camera.getPosition().getY() ) * .05 );
			camera.lookAt( getScene().getPosition() );

			double time = duration * 0.0001;

			for ( int i = 0; i < getScene().getChildren().size(); i ++ ) 
			{
				Object3D object = getScene().getChildren().get(i);

				if ( object instanceof Line ) 
				{
					object.getRotation().setY( time * ( i < 4 ? ( i + 1.0 ) : - ( i + 1.0 ) ) );

					if ( i < 5 ) 
						object.getScale().set(originalScale.get(object) * (i / 5.0 + 1.0) * (1.0 + 0.5 * Math.sin( 7.0 * time ) ));
				}
			}
			
			getRenderer().render(getScene(), camera);
		}
	}
		
	public LinesSphere() 
	{
		super("Spheres in lines", "Drag mouse to move. This example based on the three.js example.");
	}
	
	@Override
	public void onAnimationReady(AnimationReadyEvent event)
	{
		super.onAnimationReady(event);

		this.renderingPanel.getCanvas().addMouseMoveHandler(new MouseMoveHandler() {
		      @Override
		      public void onMouseMove(MouseMoveEvent event)
		      {
		    	  	DemoScene rs = (DemoScene) renderingPanel.getAnimatedScene();
		    	  	rs.mouseX = (event.getX() - renderingPanel.getRenderer().getAbsoluteWidth() / 2 ); 
		    	  	rs.mouseY = (event.getY() - renderingPanel.getRenderer().getAbsoluteHeight() / 2);
		      }
		});
	}
	
	@Override
	public DemoScene onInitialize()
	{
		return new DemoScene();
	}
	
	@Override
	protected void asyncOnInitialize(final AsyncCallback<DemoAnimatedScene> callback)
	{
		GWT.runAsync(LinesSphere.class, new RunAsyncCallback() 
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
