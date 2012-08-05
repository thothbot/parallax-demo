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

import thothbot.parallax.core.client.textures.Texture;
import thothbot.parallax.core.shared.cameras.PerspectiveCamera;
import thothbot.parallax.core.shared.core.Color;
import thothbot.parallax.core.shared.objects.Object3D;
import thothbot.parallax.core.shared.objects.Sprite;
import thothbot.parallax.core.shared.objects.Sprite.ALIGNMENT;
import thothbot.parallax.core.shared.utils.ImageUtils;
import thothbot.parallax.demo.client.ContentWidget;
import thothbot.parallax.demo.client.Demo;
import thothbot.parallax.demo.client.DemoAnnotations.DemoSource;

import com.google.gwt.core.client.GWT;
import com.google.gwt.core.client.RunAsyncCallback;
import com.google.gwt.resources.client.ImageResource;
import com.google.gwt.user.client.rpc.AsyncCallback;

public final class EffectsSprites extends ContentWidget 
{

	/*
	 * Prepare Rendering Scene
	 */
	@DemoSource
	class DemoScene extends DemoAnimatedScene 
	{
		Object3D group;
		
		Texture mapA = ImageUtils.loadTexture( "./static/textures/sprite0.png" );
		Texture mapB = ImageUtils.loadTexture( "./static/textures/sprite1.png" );
		Texture mapC = ImageUtils.loadTexture( "./static/textures/sprite2.png" );
		
		@Override
		protected void loadCamera()
		{
			setCamera(
					new PerspectiveCamera(
							60, // fov
							getRenderer().getCanvas().getAspectRation(), // aspect 
							1, // near
							5000 // far 
					)); 
		}

		@Override
		protected void onStart()
		{
			getCamera().getPosition().setZ(1500);
			getScene().addChild(getCamera());
			
			int amount = 200;
			int radius = 500;
			
			group = new Object3D();

			for( int a = 0; a < amount; a ++ ) 
			{ 
				Sprite sprite = new Sprite();
				sprite.setMap(mapC);
				sprite.setUseScreenCoordinates(false);
				sprite.setColor(new Color(0xffffff));

				sprite.getPosition().set( Math.random() - 0.5,
						Math.random() - 0.5,
						Math.random() - 0.5 );

				if( sprite.getPosition().getZ() < 0 ) 
				{
					sprite.setMap(mapB);
				} 
				else 
				{
					sprite.getColor().setHSV(0.5 * Math.random(), 0.8, 0.9 );
					sprite.getUvScale().set( 2, 2 );
					sprite.getUvOffset().set( -0.5, -0.5 );
				}

				sprite.getPosition().normalize();
				sprite.getPosition().multiply( radius );

				group.addChild( sprite );

			}

			getScene().addChild( group );

			// add 2d-sprites 
			Sprite sprite1 = new Sprite();
			sprite1.setMap(mapA);
			sprite1.setAlignment(ALIGNMENT.TOP_LEFT);
			sprite1.getPosition().set( 100, 100, 0 );
			sprite1.setOpacity( 0.25 );
			getScene().addChild( sprite1 );

			Sprite sprite2 = new Sprite();
			sprite2.setMap(mapA);
			sprite2.setAlignment(ALIGNMENT.TOP_LEFT);
			sprite2.getPosition().set( 150, 150, 2 );
			sprite2.setOpacity( 0.5 );
			getScene().addChild( sprite2 );

			Sprite sprite3 = new Sprite();
			sprite3.setMap(mapA);
			sprite3.setAlignment(ALIGNMENT.TOP_LEFT);
			sprite3.getPosition().set( 200, 200, 3 );
			sprite3.setOpacity(1);
			getScene().addChild( sprite3 );
		}
		
		@Override
		protected void onStop()
		{			
		}
		
		@Override
		protected void onUpdate(double duration)
		{
			double time = duration * .001;
			
			for ( int c = 0; c < group.getChildren().size(); c ++ ) 
			{
				Sprite sprite = (Sprite) group.getChildren().get(c);
				double scale = Math.sin( time + sprite.getPosition().getX() * 0.01 ) * 0.3 + 0.5;

				sprite.setRotationFactor(sprite.getRotationFactor() +  0.1 * ( c / (double)group.getChildren().size() ) );
				sprite.getScale().set( scale, scale, 1.0 );

				if ( !sprite.getMap().equals( mapC ) )
					sprite.setOpacity( Math.sin( time + sprite.getPosition().getX() * 0.01 ) * 0.4 + 0.6 );
			}

			group.getRotation().setX( time * 0.5 );
			group.getRotation().setY( time * 0.75);
			group.getRotation().setZ( time * 1.0 );
		}
	}
		
	public EffectsSprites() 
	{
		super("Sprites", "This example based on the three.js example.");
	}
	
	@Override
	public DemoScene onInitialize()
	{
		return new DemoScene();
	}

	@Override
	public ImageResource getIcon()
	{
		return Demo.resources.exampleSprites();
	}
	
	@Override
	protected void asyncOnInitialize(final AsyncCallback<DemoAnimatedScene> callback)
	{
		GWT.runAsync(EffectsSprites.class, new RunAsyncCallback() 
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