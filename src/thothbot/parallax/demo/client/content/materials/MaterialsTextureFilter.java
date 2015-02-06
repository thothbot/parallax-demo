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

import thothbot.parallax.core.client.events.AnimationReadyEvent;
import thothbot.parallax.core.client.gl2.enums.TextureMagFilter;
import thothbot.parallax.core.client.gl2.enums.TextureMinFilter;
import thothbot.parallax.core.client.gl2.enums.TextureWrapMode;
import thothbot.parallax.core.client.textures.Texture;
import thothbot.parallax.core.shared.cameras.PerspectiveCamera;
import thothbot.parallax.core.shared.geometries.PlaneGeometry;
import thothbot.parallax.core.shared.materials.MeshBasicMaterial;
import thothbot.parallax.core.shared.math.Color;
import thothbot.parallax.core.shared.math.Vector2;
import thothbot.parallax.core.shared.objects.Mesh;
import thothbot.parallax.core.shared.scenes.Fog;
import thothbot.parallax.core.shared.scenes.Scene;
import thothbot.parallax.demo.client.ContentWidget;
import thothbot.parallax.demo.client.Demo;
import thothbot.parallax.demo.client.DemoAnnotations.DemoSource;

import com.google.gwt.canvas.dom.client.Context2d;
import com.google.gwt.core.client.GWT;
import com.google.gwt.core.client.RunAsyncCallback;
import com.google.gwt.dom.client.CanvasElement;
import com.google.gwt.dom.client.Document;
import com.google.gwt.dom.client.Element;
import com.google.gwt.event.dom.client.MouseMoveEvent;
import com.google.gwt.event.dom.client.MouseMoveHandler;
import com.google.gwt.resources.client.ImageResource;
import com.google.gwt.user.client.rpc.AsyncCallback;

public final class MaterialsTextureFilter extends ContentWidget 
{
	/*
	 * Prepare Rendering Scene
	 */
	@DemoSource
	class DemoScene extends DemoAnimatedScene 
	{
		private static final String texture = "./static/textures/caravaggio.jpg";
		
		PerspectiveCamera camera;
		int mouseX = 0, mouseY = 0;
		
		Scene scene2;
		
		PlaneGeometry geometry;
		
		Texture texturePainting2;
		Mesh meshCanvas;
		Mesh meshCanvas2;
		
		MeshBasicMaterial materialPainting;
		MeshBasicMaterial materialPainting2;

		@Override
		protected void onStart()
		{
			camera = new PerspectiveCamera(
					35, // fov
					getRenderer().getAbsoluteAspectRation(), // aspect 
					1, // near
					5000 // far 
			); 
			
			camera.getPosition().setZ(1500);
			
			this.scene2 = new Scene();
			
			getScene().setFog( new Fog( 0x000000, 1500, 4000 ));
			scene2.setFog( getScene().getFog() );

			// GROUND

			CanvasElement canvas = Document.get().createElement("canvas").cast();
			canvas.setWidth(128);
			canvas.setHeight(128);
			Context2d context = canvas.getContext2d();

			context.setFillStyle( "#444" );
			context.fillRect( 0, 0, 128, 128 );

			context.setFillStyle( "#fff" );
			context.fillRect( 0, 0, 64, 64);
			context.fillRect( 64, 64, 64, 64 );

			Texture textureCanvas = new Texture( canvas );
			textureCanvas.setWrapS(TextureWrapMode.REPEAT);
			textureCanvas.setWrapT(TextureWrapMode.REPEAT);
			MeshBasicMaterial materialCanvas = new MeshBasicMaterial();
			materialCanvas.setMap(textureCanvas);

			textureCanvas.setNeedsUpdate(true);
			textureCanvas.setRepeat(new Vector2(1000, 1000));

			Texture textureCanvas2 = new Texture( canvas );
			textureCanvas2.setWrapS(TextureWrapMode.REPEAT);
			textureCanvas2.setWrapT(TextureWrapMode.REPEAT);
			textureCanvas2.setMagFilter(TextureMagFilter.NEAREST);
			textureCanvas2.setMinFilter(TextureMinFilter.NEAREST);
			MeshBasicMaterial materialCanvas2 = new MeshBasicMaterial();
			materialCanvas2.setColor(new Color(0xffccaa));
			materialCanvas2.setMap(textureCanvas2);

			textureCanvas2.setNeedsUpdate(true);
			textureCanvas2.setRepeat(new Vector2(1000, 1000));

			geometry = new PlaneGeometry( 100, 100 );

			meshCanvas = new Mesh( geometry, materialCanvas );
			meshCanvas.getRotation().setX( - Math.PI / 2.0 );
			meshCanvas.getScale().set( 1000 );

			meshCanvas2 = new Mesh( geometry, materialCanvas2 );
			meshCanvas2.getRotation().setX( - Math.PI / 2.0 );
			meshCanvas2.getScale().set( 1000 );

			// PAINTING
			Texture texturePainting = new Texture(texture, new Texture.ImageLoadHandler() {
				
				@Override
				public void onImageLoad(Texture texture) {
					callbackPainting(texture);
				}
			} );
			
			materialPainting = new MeshBasicMaterial();
			materialPainting.setColor(new Color(0xffffff));
			materialPainting.setMap(texturePainting);
			
			texturePainting2 = new Texture();
			materialPainting2 = new MeshBasicMaterial();
			materialPainting2.setColor(new Color(0xffccaa));
			materialPainting2.setMap(texturePainting2);

			texturePainting2.setMinFilter(TextureMinFilter.NEAREST);
			texturePainting2.setMagFilter(TextureMagFilter.NEAREST);

			texturePainting.setMinFilter(TextureMinFilter.LINEAR);
			texturePainting.setMagFilter(TextureMagFilter.LINEAR);

			getRenderer().setClearColor( getScene().getFog().getColor(), 1 );
			getRenderer().setAutoClear(false);
		}
		
		private void callbackPainting( Texture texture ) 
		{
			texturePainting2.setImage(texture.getImage());
			texturePainting2.setNeedsUpdate(true);

			getScene().add( meshCanvas );
			scene2.add( meshCanvas2 );

			PlaneGeometry geometry = new PlaneGeometry( 100, 100 );
			Mesh mesh = new Mesh( geometry, materialPainting );
			Mesh mesh2 = new Mesh( geometry, materialPainting2 );

			addPainting( texture.getImage(), getScene(), mesh );
			addPainting( texture.getImage(), scene2, mesh2 );
		}
		
		private void addPainting( Element image, Scene zscene, Mesh zmesh ) 
		{
			zmesh.getScale().setX( image.getOffsetWidth() / 100.0 ) ;
			zmesh.getScale().setY( image.getOffsetHeight() / 100.0 );

			zscene.add( zmesh );

			MeshBasicMaterial mb = new MeshBasicMaterial();
			mb.setColor(new Color(0x000000));
			mb.setPolygonOffset(true);
			mb.setPolygonOffsetFactor(1);
			mb.setPolygonOffsetUnits(5);
			
			Mesh meshFrame = new Mesh( geometry,  mb);

			meshFrame.getScale().setX( 1.1 * image.getOffsetWidth() / 100 );
			meshFrame.getScale().setY( 1.1 * image.getOffsetHeight() / 100 );

			zscene.add( meshFrame );

			MeshBasicMaterial mb2 = new MeshBasicMaterial();
			mb2.setColor(new Color(0x000000));
			mb2.setOpacity(0.75);
			mb2.setTransparent(true);

			Mesh meshShadow = new Mesh( geometry, mb2 );
			meshShadow.getPosition().setY( - 1.1 * image.getOffsetHeight()/ 2.0 );
			meshShadow.getPosition().setZ( - 1.1 * image.getOffsetHeight()/ 2.0 );
			meshShadow.getRotation().setX( - Math.PI / 2 );
			meshShadow.getScale().setX( 1.1 * image.getOffsetWidth() / 100.0 );
			meshShadow.getScale().setY( 1.1 * image.getOffsetHeight() / 100.0 );
			zscene.add( meshShadow );

			meshShadow.getPosition().setY( - 1.1 * image.getOffsetHeight() / 2.0 );

			double floorHeight = - 1.117 * image.getOffsetHeight() / 2.0;
			meshCanvas.getPosition().setY( floorHeight ); 
			meshCanvas2.getPosition().setY( floorHeight );
		}
		
		@Override
		protected void onUpdate(double duration)
		{
			camera.getPosition().addX( ( mouseX - camera.getPosition().getX() ) * .05 );
			camera.getPosition().addY( ( - ( mouseY - 200) - camera.getPosition().getY() ) * .05 );

			camera.lookAt( getScene().getPosition() );

			getRenderer().enableScissorTest( false );
			getRenderer().clear();
			getRenderer().enableScissorTest( true );

			getRenderer().setScissor( getRenderer().getAbsoluteWidth()/2, 0, getRenderer().getAbsoluteWidth()/2 - 2, getRenderer().getAbsoluteHeight()  );
			getRenderer().render( this.scene2, camera );

			getRenderer().setScissor( 0, 0, getRenderer().getAbsoluteWidth()/2 - 2, getRenderer().getAbsoluteHeight() );
			getRenderer().render(getScene(), camera);
		}
	}
		
	public MaterialsTextureFilter() 
	{
		super("Texture filtering", "This example based on the three.js example.");
	}
	
	@Override
	public DemoScene onInitialize()
	{
		return new DemoScene();
	}
	
	@Override
	protected boolean isEnabledEffectSwitch() {
		return false;
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
	public ImageResource getIcon()
	{
		return Demo.resources.exampleMaterialsTextureFilter();
	}
	
	@Override
	protected void asyncOnInitialize(final AsyncCallback<DemoAnimatedScene> callback)
	{
		GWT.runAsync(MaterialsTextureFilter.class, new RunAsyncCallback() 
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
