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

package thothbot.parallax.demo.client.content.plugins;

import thothbot.parallax.core.client.textures.Texture;
import thothbot.parallax.core.shared.cameras.PerspectiveCamera;
import thothbot.parallax.core.shared.math.Color;
import thothbot.parallax.core.shared.objects.Object3D;
import thothbot.parallax.demo.client.ContentWidget;
import thothbot.parallax.demo.client.Demo;
import thothbot.parallax.demo.client.DemoAnnotations.DemoSource;
import thothbot.parallax.plugins.sprite.Sprite;
import thothbot.parallax.plugins.sprite.SpritePlugin;
import thothbot.parallax.plugins.sprite.Sprite.ALIGNMENT;

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
		PerspectiveCamera camera;
		Object3D group;
		
		Texture mapA = new Texture( "./static/textures/sprite0.png" );
		Texture mapB = new Texture( "./static/textures/sprite1.png" );
		Texture mapC = new Texture( "./static/textures/sprite2.png" );

		@Override
		protected void onStart()
		{
			camera = new PerspectiveCamera(
					60, // fov
					getRenderer().getAbsoluteAspectRation(), // aspect 
					1, // near
					5000 // far 
			);
			
			camera.getPosition().setZ(1500);
			
			int amount = 200;
			int radius = 500;
			
			new SpritePlugin(getRenderer(), getScene());
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

				group.add( sprite );
			}

			getScene().add( group );

			// add 2d-sprites 
			Sprite sprite1 = new Sprite();
			sprite1.setMap(mapA);
			sprite1.setAlignment(ALIGNMENT.TOP_LEFT);
			sprite1.getPosition().set( 100, 100, 0 );
			sprite1.setOpacity( 0.25 );
			getScene().add( sprite1 );

			Sprite sprite2 = new Sprite();
			sprite2.setMap(mapA);
			sprite2.setAlignment(ALIGNMENT.TOP_LEFT);
			sprite2.getPosition().set( 150, 150, 2 );
			sprite2.setOpacity( 0.5 );
			getScene().add( sprite2 );

			Sprite sprite3 = new Sprite();
			sprite3.setMap(mapA);
			sprite3.setAlignment(ALIGNMENT.TOP_LEFT);
			sprite3.getPosition().set( 200, 200, 3 );
			sprite3.setOpacity(1);
			getScene().add( sprite3 );
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
			
			getRenderer().render(getScene(), camera);
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
