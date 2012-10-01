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

package thothbot.parallax.demo.client.content.materials;

import thothbot.parallax.core.client.AnimationReadyEvent;
import thothbot.parallax.core.client.context.Canvas3d;
import thothbot.parallax.core.client.gl2.enums.TextureWrapMode;
import thothbot.parallax.core.client.textures.Texture;
import thothbot.parallax.core.shared.Log;
import thothbot.parallax.core.shared.cameras.PerspectiveCamera;
import thothbot.parallax.core.shared.core.Color;
import thothbot.parallax.core.shared.core.Mathematics;
import thothbot.parallax.core.shared.geometries.PlaneGeometry;
import thothbot.parallax.core.shared.lights.AmbientLight;
import thothbot.parallax.core.shared.lights.DirectionalLight;
import thothbot.parallax.core.shared.materials.MeshPhongMaterial;
import thothbot.parallax.core.shared.objects.Mesh;
import thothbot.parallax.core.shared.scenes.FogSimple;
import thothbot.parallax.core.shared.scenes.Scene;
import thothbot.parallax.demo.client.ContentWidget;
import thothbot.parallax.demo.client.Demo;
import thothbot.parallax.demo.client.DemoAnnotations.DemoSource;

import com.google.gwt.core.client.GWT;
import com.google.gwt.core.client.RunAsyncCallback;
import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.event.dom.client.MouseMoveEvent;
import com.google.gwt.event.dom.client.MouseMoveHandler;
import com.google.gwt.resources.client.ImageResource;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.Label;

public final class MaterialsTextureAnisotropy extends ContentWidget 
{
	/*
	 * Prepare Rendering Scene
	 */
	@DemoSource
	class DemoScene extends DemoAnimatedScene 
	{
		private static final String texture = "./static/textures/crate.gif";
		
		int mouseX = 0, mouseY = 0;
		
		Scene sceneMaxAnisotropy;
		

		@Override
		protected void loadCamera()
		{
			setCamera(
					new PerspectiveCamera(
							35, // fov
							getRenderer().getCanvas().getAspectRation(), // aspect 
							1, // near
							25000 // far 
					)); 
		}

		@Override
		protected void onStart()
		{
			getCamera().getPosition().setZ(1500);
			getScene().add(getCamera());

			sceneMaxAnisotropy = new Scene();

			sceneMaxAnisotropy.setFog( new FogSimple( 0xffffff, 1, 25000 ) );
			sceneMaxAnisotropy.getFog().getColor().setHSV( 0.6, 0.05, 1 );
			getScene().setFog(sceneMaxAnisotropy.getFog());

			sceneMaxAnisotropy.add( new AmbientLight( 0xeef0ff ) );
			getScene().add( new AmbientLight( 0xeef0ff ) );

			DirectionalLight light1 = new DirectionalLight( 0xffffff, 2 );
			light1.getPosition().set( 1 );
			sceneMaxAnisotropy.add( light1 );

			DirectionalLight light2 = new DirectionalLight( 0xffffff, 2 );
			light2.setPosition( light1.getPosition() );
			getScene().add( light2 );

			// GROUND

			Texture texture1 = new Texture(texture);
			MeshPhongMaterial material1 = new MeshPhongMaterial();
			material1.setColor(new Color(0xffffff));
			material1.setMap(texture1);

			texture1.setAnisotropy( getRenderer().getGPUmaxAnisotropy() );
			texture1.setWrapS(TextureWrapMode.REPEAT);
			texture1.setWrapT(TextureWrapMode.REPEAT);
			texture1.getRepeat().set( 512, 512 );

			Texture texture2 = new Texture(texture);
			MeshPhongMaterial material2 = new MeshPhongMaterial();
			material2.setColor(new Color(0xffffff));
			material2.setMap(texture2);

			texture2.setAnisotropy( 1 );
			texture2.setWrapS(TextureWrapMode.REPEAT);
			texture2.setWrapT(TextureWrapMode.REPEAT);
			texture2.getRepeat().set( 512, 512 );

			//

			PlaneGeometry geometry = new PlaneGeometry( 100, 100 );

			Mesh mesh1 = new Mesh( geometry, material1 );
			mesh1.getRotation().setX( - Math.PI / 2 );
			mesh1.getScale().set( 1000 );

			Mesh mesh2 = new Mesh( geometry, material2 );
			mesh2.getRotation().setX( - Math.PI / 2 );
			mesh2.getScale().set( 1000 );

			sceneMaxAnisotropy.add( mesh1 );
			getScene().add( mesh2 );

			// RENDERER

			getRenderer().setClearColor( sceneMaxAnisotropy.getFog().getColor(), 1 );
			getRenderer().setAutoClear(false);
		}
		
		@Override
		protected void onUpdate(double duration)
		{
			getCamera().getPosition().addX( ( mouseX - getCamera().getPosition().getX() ) * .05 );
			getCamera().getPosition().setY( Mathematics.clamp( 
					getCamera().getPosition().getY() + ( - ( mouseY - 200 ) - getCamera().getPosition().getY() ) * .05, 50, 1000 ));

			getCamera().lookAt( sceneMaxAnisotropy.getPosition() );

			getRenderer().enableScissorTest( false );
			getRenderer().clear();
			getRenderer().enableScissorTest( true );

			Canvas3d canvas = getRenderer().getCanvas();
			getRenderer().setScissor( 0, 0, canvas.getWidth()/2 - 2, canvas.getHeight() );
			getRenderer().render( sceneMaxAnisotropy, getCamera() );

			getRenderer().setScissor( canvas.getWidth()/2, 0, canvas.getWidth()/2 - 2, canvas.getHeight()  );
		}
	}
		
	public MaterialsTextureAnisotropy() 
	{
		super("Anisotropic filtering", "This example based on the three.js example.");
	}
	
	@Override
	public void onAnimationReady(AnimationReadyEvent event)
	{
		super.onAnimationReady(event);
				
		FlowPanel panelLeft = new FlowPanel();
		panelLeft.setStyleName("common-panel", true);
		panelLeft.setStyleName("corner-panel", true);
		this.renderingPanel.add(panelLeft);
		this.renderingPanel.setWidgetLeftWidth(panelLeft, 1, Unit.PX, 80, Unit.PX);
		this.renderingPanel.setWidgetBottomHeight(panelLeft, 1, Unit.PX, 20, Unit.PX);
		
		FlowPanel panelRight = new FlowPanel();
		panelRight.setStyleName("common-panel", true);
		panelRight.setStyleName("corner-panel", true);
		this.renderingPanel.add(panelRight);
		this.renderingPanel.setWidgetRightWidth(panelRight, 1, Unit.PX, 80, Unit.PX);
		this.renderingPanel.setWidgetBottomHeight(panelRight, 1, Unit.PX, 20, Unit.PX);

		final DemoScene rs = (DemoScene) this.renderingPanel.getAnimatedScene();

		if ( this.renderingPanel.getRenderer().getGPUmaxAnisotropy() > 0 ) 
		{
			panelLeft.add(new Label("Anisotropy: " + this.renderingPanel.getRenderer().getGPUmaxAnisotropy()));
			panelRight.add(new Label("Anisotropy: " + 1));
		} 
		else
		{
			panelLeft.add(new Label("not supported"));
			panelRight.add(new Label("not supported"));
		}
		
		this.renderingPanel.getRenderer().getCanvas().addMouseMoveHandler(new MouseMoveHandler() {
		      @Override
		      public void onMouseMove(MouseMoveEvent event)
		      {
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
		return Demo.resources.exampleMaterialsTextureAnisotropy();
	}
	
	@Override
	protected void asyncOnInitialize(final AsyncCallback<DemoAnimatedScene> callback)
	{
		GWT.runAsync(MaterialsTextureAnisotropy.class, new RunAsyncCallback() 
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
