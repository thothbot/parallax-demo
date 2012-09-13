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

package thothbot.parallax.demo.client.content.geometries;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import thothbot.parallax.core.client.AnimationReadyEvent;
import thothbot.parallax.core.client.context.Canvas3d;
import thothbot.parallax.core.shared.cameras.PerspectiveCamera;
import thothbot.parallax.core.shared.core.Color;
import thothbot.parallax.core.shared.core.Geometry;
import thothbot.parallax.core.shared.core.Vector3;
import thothbot.parallax.core.shared.materials.LineBasicMaterial;
import thothbot.parallax.core.shared.objects.DimensionalObject;
import thothbot.parallax.core.shared.objects.Line;
import thothbot.parallax.demo.client.ContentWidget;
import thothbot.parallax.demo.client.Demo;
import thothbot.parallax.demo.client.DemoAnnotations.DemoSource;

import com.google.gwt.core.client.GWT;
import com.google.gwt.core.client.RunAsyncCallback;
import com.google.gwt.event.dom.client.MouseMoveEvent;
import com.google.gwt.event.dom.client.MouseMoveHandler;
import com.google.gwt.resources.client.ImageResource;
import com.google.gwt.user.client.rpc.AsyncCallback;

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

		public int mouseX;
		public int mouseY;
		
		Map<Line, Double> originalScale;
		
		@Override
		protected void loadCamera()
		{
			setCamera(
					new PerspectiveCamera(
							80, // fov
							getRenderer().getCanvas().getAspectRation(), // aspect 
							1, // near
							3000 // far 
					)); 
		}

		@Override
		protected void onStart()
		{
			getCamera().getPosition().setZ(1000);
			getScene().add(getCamera());

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

				Line line = new Line( geometry, material, Line.TYPE.PIECES );
				line.getScale().set( p.scale );
				this.originalScale.put(line, p.scale);

				line.getRotation().setY( Math.random() * Math.PI );
				line.updateMatrix();
				getScene().add( line );
			}
		}
		
		@Override
		protected void onStop()
		{			
		}
		
		@Override
		protected void onUpdate(double duration)
		{
			getCamera().getPosition().addY( ( - mouseY + 200.0 - getCamera().getPosition().getY() ) * .05 );
			getCamera().lookAt( getScene().getPosition() );

			double time = duration * 0.0001;

			for ( int i = 0; i < getScene().getChildren().size(); i ++ ) 
			{
				DimensionalObject object = getScene().getChildren().get(i);

				if ( object instanceof Line ) 
				{
					object.getRotation().setY( time * ( i < 4 ? ( i + 1.0 ) : - ( i + 1.0 ) ) );

					if ( i < 5 ) 
						object.getScale().set(originalScale.get(object) * (i / 5.0 + 1.0) * (1.0 + 0.5 * Math.sin( 7.0 * time ) ));
				}
			}
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

		this.renderingPanel.getRenderer().getCanvas().addMouseMoveHandler(new MouseMoveHandler() {
		      @Override
		      public void onMouseMove(MouseMoveEvent event)
		      {
		    	  	DemoScene rs = (DemoScene) renderingPanel.getAnimatedScene();
		    	  	Canvas3d canvas = renderingPanel.getRenderer().getCanvas();
		    	  	rs.mouseX = (event.getX() - canvas.getWidth() / 2 ); 
		    	  	rs.mouseY = (event.getY() - canvas.getHeight() / 2);
		      }
		});
	}
	
	@Override
	public DemoScene onInitialize()
	{
		return new DemoScene();
	}

	@Override
	public ImageResource getIcon()
	{
		return Demo.resources.exampleLinesSphere();
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
