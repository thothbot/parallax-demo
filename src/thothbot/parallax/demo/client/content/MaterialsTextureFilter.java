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

package thothbot.parallax.demo.client.content;

import com.google.gwt.canvas.dom.client.Context2d;
import com.google.gwt.core.client.GWT;
import com.google.gwt.core.client.RunAsyncCallback;
import com.google.gwt.dom.client.CanvasElement;
import com.google.gwt.dom.client.Document;
import com.google.gwt.dom.client.Element;
import com.google.gwt.event.dom.client.MouseMoveEvent;
import com.google.gwt.event.dom.client.MouseMoveHandler;
import com.google.gwt.resources.client.ClientBundle;
import com.google.gwt.resources.client.ImageResource;
import com.google.gwt.resources.client.ClientBundle.Source;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.Image;

import thothbot.parallax.core.client.AnimationReadyEvent;
import thothbot.parallax.core.client.context.Canvas3d;
import thothbot.parallax.core.client.gl2.enums.TextureMagFilter;
import thothbot.parallax.core.client.gl2.enums.TextureMinFilter;
import thothbot.parallax.core.client.gl2.enums.TextureWrapMode;
import thothbot.parallax.core.client.textures.Texture;
import thothbot.parallax.core.shared.Log;
import thothbot.parallax.core.shared.cameras.PerspectiveCamera;
import thothbot.parallax.core.shared.core.Color3f;
import thothbot.parallax.core.shared.core.Vector2f;
import thothbot.parallax.core.shared.geometries.Cube;
import thothbot.parallax.core.shared.geometries.Plane;
import thothbot.parallax.core.shared.materials.Material;
import thothbot.parallax.core.shared.materials.MeshBasicMaterial;
import thothbot.parallax.core.shared.objects.Mesh;
import thothbot.parallax.core.shared.scenes.Fog;
import thothbot.parallax.core.shared.scenes.FogSimple;
import thothbot.parallax.core.shared.scenes.Scene;
import thothbot.parallax.core.shared.utils.ImageUtils;
import thothbot.parallax.demo.client.ContentWidget;
import thothbot.parallax.demo.client.Demo;
import thothbot.parallax.demo.client.DemoAnnotations.DemoSource;
import thothbot.parallax.demo.client.content.GeometryCube.DemoScene;
import thothbot.parallax.demo.client.content.GeometryCube.Resources;

public final class MaterialsTextureFilter extends ContentWidget 
{

	/*
	 * Load texture
	 */
	@DemoSource
	public interface Resources extends ClientBundle
	{
		Resources INSTANCE = GWT.create(Resources.class);

		@Source("../../resources/textures/caravaggio.jpg")
		ImageResource texture();
	}

	/*
	 * Prepare Rendering Scene
	 */
	@DemoSource
	class DemoScene extends DemoAnimatedScene 
	{
		int mouseX = 0, mouseY = 0;
		
		Scene scene2;
		
		Plane geometry;
		
		Texture texturePainting2;
		Mesh meshCanvas;
		Mesh meshCanvas2;
		
		MeshBasicMaterial materialPainting;
		MeshBasicMaterial materialPainting2;

		@Override
		protected void loadCamera()
		{
			setCamera(
					new PerspectiveCamera(
							35, // fov
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

			this.scene2 = new Scene();

			getScene().setFog( new FogSimple( 0x000000, 1500, 4000 ));
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

			Texture textureCanvas = new Texture( canvas, Texture.MAPPING_MODE.UV, TextureWrapMode.REPEAT, TextureWrapMode.REPEAT );
			MeshBasicMaterial materialCanvas = new MeshBasicMaterial();
			materialCanvas.setMap(textureCanvas);

			textureCanvas.setNeedsUpdate(true);
			textureCanvas.setRepeat(new Vector2f(1000, 1000));

			Texture textureCanvas2 = new Texture( canvas, Texture.MAPPING_MODE.UV, TextureWrapMode.REPEAT, TextureWrapMode.REPEAT, TextureMagFilter.NEAREST, TextureMinFilter.NEAREST);
			MeshBasicMaterial materialCanvas2 = new MeshBasicMaterial();
			materialCanvas2.setColor(new Color3f(0xffccaa));
			materialCanvas2.setMap(textureCanvas2);

			textureCanvas2.setNeedsUpdate(true);
			textureCanvas2.setRepeat(new Vector2f(1000, 1000));

			geometry = new Plane( 100, 100 );

			meshCanvas = new Mesh( geometry, materialCanvas );
			meshCanvas.getScale().set( 1000 );

			meshCanvas2 = new Mesh( geometry, materialCanvas2 );
			meshCanvas2.getScale().set( 1000 );

			// PAINTING
			Texture texturePainting = ImageUtils.loadTexture(Resources.INSTANCE.texture(), Texture.MAPPING_MODE.UV, new ImageUtils.Callback() {
				
				@Override
				public void run(Texture texture) {
					callbackPainting(texture);
				}
			} );
			
			materialPainting = new MeshBasicMaterial();
			materialPainting.setColor(new Color3f(0xffffff));
			materialPainting.setMap(texturePainting);
			
			texturePainting2 = new Texture();
			materialPainting2 = new MeshBasicMaterial();
			materialPainting2.setColor(new Color3f(0xffccaa));
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

			getScene().addChild( meshCanvas );
			scene2.addChild( meshCanvas2 );

			Plane geometry = new Plane( 100, 100 );
			Mesh mesh = new Mesh( geometry, materialPainting );
			Mesh mesh2 = new Mesh( geometry, materialPainting2 );

			mesh.getRotation().setX( (float) (Math.PI / 2.0) );
			mesh2.getRotation().setX( (float) (Math.PI / 2.0) );

			addPainting( texture.getImage(), getScene(), mesh );
			addPainting( texture.getImage(), scene2, mesh2 );
		}
		
		private void addPainting( Element image, Scene zscene, Mesh zmesh ) 
		{
			zmesh.getScale().setX( image.getOffsetWidth() / 100.0f ) ;
			zmesh.getScale().setZ( image.getOffsetHeight() / 100.0f );

			zscene.addChild( zmesh );

			MeshBasicMaterial mb = new MeshBasicMaterial();
			mb.setColor(new Color3f(0x000000));
			mb.setPolygonOffset(true);
			mb.setPolygonOffsetFactor(1);
			mb.setPolygonOffsetUnits(5);
			
			Mesh meshFrame = new Mesh( geometry,  mb);
			meshFrame.getRotation().setX( (float) (Math.PI / 2.0) );
			meshFrame.getScale().setX( 1.1f * image.getOffsetWidth() / 100 );
			meshFrame.getScale().setZ( 1.1f * image.getOffsetHeight() / 100 );

			zscene.addChild( meshFrame );

			MeshBasicMaterial mb2 = new MeshBasicMaterial();
			mb2.setColor(new Color3f(0x000000));
			mb2.setOpacity(0.75f);
			mb2.setTransparent(true);
			Mesh meshShadow = new Mesh( geometry, mb2 );

			meshShadow.getPosition().setZ( - 1.1f * image.getOffsetHeight()/ 2 );
			meshShadow.getScale().setX( 1.1f * image.getOffsetWidth() / 100 );
			meshShadow.getScale().setZ( 1.1f * image.getOffsetHeight() / 100 );
			zscene.addChild( meshShadow );

			meshShadow.getPosition().setY( - 1.1f * image.getOffsetHeight()/2 );

			float floorHeight = - 1.117f * image.getOffsetHeight()/2;
			meshCanvas.getPosition().setY( floorHeight ); 
			meshCanvas2.getPosition().setY( floorHeight );
		}
		
		@Override
		protected void onStop()
		{			
		}
		
		@Override
		protected void onUpdate(double duration)
		{
			getCamera().getPosition().addX( ( mouseX - getCamera().getPosition().getX() ) * .05f );
			getCamera().getPosition().addY( ( - ( mouseY - 200) - getCamera().getPosition().getY() ) * .05f );

			getCamera().lookAt( getScene().getPosition() );

			getRenderer().enableScissorTest( false );
			getRenderer().clear(false, false, false);
			getRenderer().enableScissorTest( true );

			Canvas3d canvas = getRenderer().getCanvas();
			getRenderer().setScissor( canvas.getOffsetWidth()/2, 0, canvas.getOffsetWidth()/2 - 2, canvas.getOffsetHeight()  );
			getRenderer().render( this.scene2, getCamera() );

			getRenderer().setScissor( 0, 0, canvas.getOffsetWidth()/2 - 2, canvas.getOffsetHeight() );
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
