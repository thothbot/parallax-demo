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

package thothbot.parallax.demo.client.content.materials;

import java.util.ArrayList;
import java.util.List;

import thothbot.parallax.core.client.context.Canvas3d;
import thothbot.parallax.core.client.events.AnimationReadyEvent;
import thothbot.parallax.core.shared.cameras.PerspectiveCamera;
import thothbot.parallax.core.shared.core.Geometry;
import thothbot.parallax.core.shared.materials.ParticleBasicMaterial;
import thothbot.parallax.core.shared.math.Vector3;
import thothbot.parallax.core.shared.objects.DimensionalObject;
import thothbot.parallax.core.shared.objects.ParticleSystem;
import thothbot.parallax.core.shared.scenes.FogExp2;
import thothbot.parallax.core.shared.utils.ColorUtils;
import thothbot.parallax.demo.client.ContentWidget;
import thothbot.parallax.demo.client.Demo;
import thothbot.parallax.demo.client.DemoAnnotations.DemoSource;

import com.google.gwt.core.client.GWT;
import com.google.gwt.core.client.RunAsyncCallback;
import com.google.gwt.event.dom.client.MouseMoveEvent;
import com.google.gwt.event.dom.client.MouseMoveHandler;
import com.google.gwt.resources.client.ImageResource;
import com.google.gwt.user.client.rpc.AsyncCallback;

public final class ParticlesRandom extends ContentWidget 
{

	/*
	 * Prepare Rendering Scene
	 */
	@DemoSource
	class DemoScene extends DemoAnimatedScene 
	{

		PerspectiveCamera camera;
		
		List<ParticleBasicMaterial> materials;
		
		int mouseX = 0, mouseY = 0;

		@Override
		protected void onStart()
		{
			camera = new PerspectiveCamera(
					75, // fov
					getRenderer().getAbsoluteAspectRation(), // aspect 
					1, // near
					3000 // far 
			); 
			
			camera.getPosition().setZ(1000);
			
			getScene().setFog( new FogExp2( 0x000000, 0.0007 ));
			
			Geometry geometry = new Geometry();

			for ( int i = 0; i < 20000; i ++ ) 
			{
				Vector3 vertex = new Vector3();
				vertex.setX( Math.random() * 2000 - 1000 );
				vertex.setY( Math.random() * 2000 - 1000 );
				vertex.setZ( Math.random() * 2000 - 1000 );

				geometry.getVertices().add( vertex );

			}

			materials = new ArrayList<ParticleBasicMaterial>();
			int max = 5;

			for ( int i = 0; i < max; i ++ ) 
			{
				ParticleBasicMaterial material = new ParticleBasicMaterial();
				material.setSize( 5 - i );
				material.getColor().setHSV( 1.0 - i * 0.05, 1.0, 1.0 );
				materials.add(material);

				ParticleSystem particles = new ParticleSystem( geometry, material );

				particles.getRotation().setX( Math.random() * 6 );
				particles.getRotation().setY( Math.random() * 6 );
				particles.getRotation().setZ( Math.random() * 6 );

				getScene().add( particles );
			}
		}
		
		@Override
		protected void onUpdate(double duration)
		{
			double time = duration * 0.00005;

			camera.getPosition().addX( ( mouseX - camera.getPosition().getX() ) * 0.05 );
			camera.getPosition().addY( ( - mouseY - camera.getPosition().getY() ) * 0.05 );

			camera.lookAt( getScene().getPosition() );

			for ( int i = 0; i < getScene().getChildren().size(); i ++ ) 
			{
				DimensionalObject object = getScene().getChildren().get(i);

				if ( object instanceof ParticleSystem ) 
				{
					object.getRotation().setY( time * ( i < 4 ? i + 1 : - ( i + 1 ) ) );
				}
			}

			for ( int i = 0; i < materials.size(); i ++ ) 
			{
				ParticleBasicMaterial material = materials.get(i);
				ColorUtils.HSV hsv = ColorUtils.rgbToHsv( material.getColor() );
				material.getColor().setHSV( Math.abs(Math.sin( hsv.hue + time )), hsv.saturation, hsv.value );
			}
			
			getRenderer().render(getScene(), camera);
		}
	}
		
	public ParticlesRandom() 
	{
		super("Random particles", "This example based on the three.js example.");
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

		    	  	rs.mouseX = event.getX() - renderingPanel.getRenderer().getAbsoluteWidth() / 2; 
		    	  	rs.mouseY = event.getY() - renderingPanel.getRenderer().getAbsoluteHeight() / 2;
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
		return Demo.resources.exampleParticlesRandom();
	}
	
	@Override
	protected void asyncOnInitialize(final AsyncCallback<DemoAnimatedScene> callback)
	{
		GWT.runAsync(ParticlesRandom.class, new RunAsyncCallback() 
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
