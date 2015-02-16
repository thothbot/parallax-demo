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

import thothbot.parallax.core.client.events.HasEventBus;
import thothbot.parallax.core.client.events.ViewportResizeEvent;
import thothbot.parallax.core.client.events.ViewportResizeHandler;
import thothbot.parallax.core.client.textures.Texture;
import thothbot.parallax.core.shared.Log;
import thothbot.parallax.core.shared.cameras.OrthographicCamera;
import thothbot.parallax.core.shared.cameras.PerspectiveCamera;
import thothbot.parallax.core.shared.core.Object3D;
import thothbot.parallax.core.shared.math.Color;
import thothbot.parallax.core.shared.scenes.Fog;
import thothbot.parallax.core.shared.scenes.Scene;
import thothbot.parallax.demo.client.ContentWidget;
import thothbot.parallax.demo.client.Demo;
import thothbot.parallax.demo.client.DemoAnnotations.DemoSource;
import thothbot.parallax.plugins.sprite.Sprite;
import thothbot.parallax.plugins.sprite.SpriteMaterial;
import thothbot.parallax.plugins.sprite.SpritePlugin;

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
	class DemoScene extends DemoAnimatedScene implements HasEventBus, ViewportResizeHandler
	{
		PerspectiveCamera camera;
		OrthographicCamera cameraOrtho;
		
		Object3D group;
		
		Scene sceneOrtho;
		
		Sprite spriteTL, spriteTR, spriteBL, spriteBR, spriteC;
		
		Texture mapB = new Texture( "./static/textures/sprite1.png" );
		Texture mapC = new Texture( "./static/textures/sprite2.png" );
		
		@Override
		public void onResize(ViewportResizeEvent event) 
		{
			updateHUDSprites();
		}
		
		@Override
		protected void onStart()
		{
			EVENT_BUS.addHandler(ViewportResizeEvent.TYPE, this);
			
			camera = new PerspectiveCamera(
					60, // fov
					getRenderer().getAbsoluteAspectRation(), // aspect 
					1, // near
					2100 // far 
			);
			
			camera.getPosition().setZ(1500);
			
			cameraOrtho = new OrthographicCamera( getRenderer().getAbsoluteWidth(), getRenderer().getAbsoluteHeight(), 1, 10 );
			cameraOrtho.getPosition().setZ( 10 );

			getScene().setFog( new Fog( 0x000000, 1500, 2100 ) );
			
			sceneOrtho = new Scene();
			
			int amount = 200;
			int radius = 500;
			
			new SpritePlugin(getRenderer(), getScene());
			new SpritePlugin(getRenderer(), sceneOrtho);
			
			new Texture( "./static/textures/sprite0.png", new Texture.ImageLoadHandler() {
				
				@Override
				public void onImageLoad(Texture texture) {
					SpriteMaterial material = new SpriteMaterial();
					material.setMap(texture);

					int width = material.getMap().getImage().getOffsetWidth();
					int height = material.getMap().getImage().getOffsetHeight();

					spriteTL = new Sprite( material );
					spriteTL.getScale().set( width, height, 1 );
					sceneOrtho.add( spriteTL );

					spriteTR = new Sprite( material );
					spriteTR.getScale().set( width, height, 1 );
					sceneOrtho.add( spriteTR );

					spriteBL = new Sprite( material );
					spriteBL.getScale().set( width, height, 1 );
					sceneOrtho.add( spriteBL );

					spriteBR = new Sprite( material );
					spriteBR.getScale().set( width, height, 1 );
					sceneOrtho.add( spriteBR );
					
					spriteC = new Sprite( material );
					spriteC.getScale().set( width, height, 1 );
					sceneOrtho.add( spriteC );
					
					updateHUDSprites();
				}
			} );

			SpriteMaterial materialC = new SpriteMaterial();
			materialC.setMap(mapC);
			materialC.setColor(new Color(0xffffff));
			materialC.setFog(true);

			SpriteMaterial materialB = new SpriteMaterial();
			materialB.setMap(mapB);
			materialB.setColor(new Color(0xffffff));
			materialB.setFog(true);

			group = new Object3D();
			
			for ( int a = 0; a < amount; a ++ ) {

				double x = Math.random() - 0.5;
				double y = Math.random() - 0.5;
				double z = Math.random() - 0.5;

				SpriteMaterial material;
				
				if ( z < 0 ) {

					material = materialB.clone();

				} else {

					material = materialC.clone();
					material.getColor().setHSL( 0.5 * Math.random(), 0.75, 0.5 );
					material.getMap().getOffset().set( -0.5, -0.5 );
					material.getMap().getRepeat().set( 2, 2 );
				}

				Sprite sprite = new Sprite( material );

				sprite.getPosition().set( x, y, z );
				sprite.getPosition().normalize();
				sprite.getPosition().multiply( radius );

				group.add( sprite );

			}

			getScene().add( group );

			// To allow render overlay on top of sprited sphere
			getRenderer().setAutoClear(false); 
		}
		
		private void updateHUDSprites () {

			int width = getRenderer().getAbsoluteWidth() / 2;
			int height = getRenderer().getAbsoluteHeight() / 2;

			SpriteMaterial material = (SpriteMaterial) spriteTL.getMaterial();

			int imageWidth =  material.getMap().getImage().getOffsetWidth() / 2;
			int imageHeight =  material.getMap().getImage().getOffsetHeight() / 2;

			spriteTL.getPosition().set( - width + imageWidth,   height - imageHeight, 1 ); // top left
			spriteTR.getPosition().set(   width - imageWidth,   height - imageHeight, 1 ); // top right
			spriteBL.getPosition().set( - width + imageWidth, - height + imageHeight, 1 ); // bottom left
			spriteBR.getPosition().set(   width - imageWidth, - height + imageHeight, 1 ); // bottom right
			spriteC.getPosition().set( 0, 0, 1 ); // center

		};
		
		@Override
		protected void onUpdate(double duration)
		{
			double time = duration * .001;
			
			for ( int i = 0, l = group.getChildren().size(); i < l; i ++ ) 
			{
				Sprite sprite = (Sprite) group.getChildren().get(i);
				SpriteMaterial material = (SpriteMaterial) sprite.getMaterial();
				double scale = Math.sin( time + sprite.getPosition().getX() * 0.01 ) * 0.3 + 1.0;

				int imageWidth = 1;
				int imageHeight = 1;

				if ( material.getMap() != null 
						&& material.getMap().getImage() != null 
						&& material.getMap().getImage().getOffsetWidth() > 0 ) {

					imageWidth = material.getMap().getImage().getOffsetWidth();
					imageHeight = material.getMap().getImage().getOffsetHeight();

				}

				material.setRotation(material.getRotation() + 0.1 * ( (double)i / l ) );
				sprite.getScale().set( scale * imageWidth, scale * imageHeight, 1.0 );

				if ( !material.getMap().equals( mapC ) )
					material.setOpacity( Math.sin( time + sprite.getPosition().getX() * 0.01 ) * 0.4 + 0.6 );
			}
			
			group.getRotation().setX( time * 0.5 );
			group.getRotation().setY( time * 0.75);
			group.getRotation().setZ( time * 1.0 );
			
			getRenderer().clear();
			getRenderer().render( getScene(), camera );
			getRenderer().clearDepth();
			getRenderer().render( sceneOrtho, cameraOrtho );

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
