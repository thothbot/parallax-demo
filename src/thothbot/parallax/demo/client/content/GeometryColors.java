/*
 * Copyright 2012 Alex Usachev, thothbot@gmail.com
 * 
 * This file based on the JavaScript source file of the THREE.JS project, 
 * licensed under MIT License.
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

import java.util.ArrayList;
import java.util.List;

import thothbot.parallax.core.client.RenderingPanel;
import thothbot.parallax.core.client.RenderingReadyEvent;
import thothbot.parallax.core.client.RenderingPanel.RenderPanelAttributes;
import thothbot.parallax.core.shared.Log;
import thothbot.parallax.core.shared.cameras.PerspectiveCamera;
import thothbot.parallax.core.shared.core.Color3f;
import thothbot.parallax.core.shared.core.Face3;
import thothbot.parallax.core.shared.core.Face4;
import thothbot.parallax.core.shared.core.Geometry;
import thothbot.parallax.core.shared.core.Vector3f;
import thothbot.parallax.core.shared.geometries.Icosahedron;
import thothbot.parallax.core.shared.geometries.Plane;
import thothbot.parallax.core.shared.lights.DirectionalLight;
import thothbot.parallax.core.shared.materials.Material;
import thothbot.parallax.core.shared.materials.MeshBasicMaterial;
import thothbot.parallax.core.shared.materials.MeshLambertMaterial;
import thothbot.parallax.core.shared.objects.DimensionalObject;
import thothbot.parallax.core.shared.objects.Mesh;
import thothbot.parallax.core.shared.utils.ImageUtils;
import thothbot.parallax.core.shared.utils.SceneUtils;
import thothbot.parallax.demo.client.ContentWidget;
import thothbot.parallax.demo.client.Demo;
import thothbot.parallax.demo.client.DemoAnnotations.DemoSource;

import com.google.gwt.core.client.GWT;
import com.google.gwt.core.client.RunAsyncCallback;
import com.google.gwt.event.dom.client.MouseMoveEvent;
import com.google.gwt.event.dom.client.MouseMoveHandler;
import com.google.gwt.resources.client.ClientBundle;
import com.google.gwt.resources.client.ImageResource;
import com.google.gwt.user.client.rpc.AsyncCallback;

public class GeometryColors extends ContentWidget
{
	/*
	 * Load texture
	 */
	@DemoSource
	public interface Resources extends ClientBundle
	{
		Resources INSTANCE = GWT.create(Resources.class);

		@Source("../../resources/textures/shadow.png")
		ImageResource texture();
	}
	
	/*
	 * Prepare Rendering Scene
	 */
	@DemoSource
	class DemoScene extends DemoRenderingScene 
	{	
		public int mouseX;
		public int mouseY;

		@Override
		protected void loadCamera()
		{
			setCamera(
					new PerspectiveCamera( 20,
							getRenderer().getCanvas().getAspectRation(), 
							1, 
							10000 
					)); 
		}

		@Override
		protected void onStart()
		{
			getCamera().getPosition().setZ(2000);
			getScene().addChild(getCamera());
			
			DirectionalLight light = new DirectionalLight( 0xffffff );
			light.getPosition().set( 0, 0, 1 );
			getScene().addChild( light );
			
			MeshBasicMaterial shadowMaterial = new MeshBasicMaterial();
			shadowMaterial.setMap( ImageUtils.loadTexture(Resources.INSTANCE.texture()) );
			Geometry shadowGeo = new Plane( 300, 300, 1, 1 );
			
			Mesh mesh1 = new Mesh( shadowGeo, shadowMaterial );
			mesh1.getPosition().setY(-250);
			getScene().addChild( mesh1 );

			Mesh mesh2 = new Mesh( shadowGeo, shadowMaterial );
			mesh2.getPosition().setY(-250);
			mesh2.getPosition().setX(-400);
			getScene().addChild( mesh2 );

			Mesh mesh3 = new Mesh( shadowGeo, shadowMaterial );
			mesh3.getPosition().setY(-250);
			mesh3.getPosition().setX(400);
			getScene().addChild( mesh3 );
			
			int radius = 200;
			
			Geometry geometry  = new Icosahedron( radius, 1 );
			Geometry geometry2 = new Icosahedron( radius, 1 );
			Geometry geometry3 = new Icosahedron( radius, 1 );
			
			for ( int i = 0; i < geometry.getFaces().size(); i ++ ) 
			{
				Face3 f  = geometry.getFaces().get( i );

				int n = ( f.getClass() == Face3.class ) ? 3 : 4;

				for( int j = 0; j < n; j++ ) 
				{
					int vertexIndex = (j == 0 ) ? f.getA()
							: (j == 1 ) ? f.getB()
							: (j == 2 ) ? f.getC() : 0;

					if(j == 3)
					{
						Face4 f14 = (Face4) f;
						vertexIndex = f14.getD();
					}

					Vector3f p = geometry.getVertices().get( vertexIndex );

					Color3f color = new Color3f( 0xffffff );
					color.setHSV( ( p.getY() / radius + 1.0f ) / 2.0f, 1.0f, 1.0f );

					geometry.getFaces().get( i ).getVertexColors().add(color);

					Color3f color2 = new Color3f( 0xffffff );
					color2.setHSV( 0.0f, ( (float)p.getY() / radius + 1.0f ) / 2.0f, 1.0f );

					geometry2.getFaces().get( i ).getVertexColors().add(color);

					Color3f color3 = new Color3f( 0xffffff );
					color3.setHSV( (0.125f * (float)vertexIndex/geometry.getVertices().size()), 1.0f, 1.0f );

					geometry3.getFaces().get( i ).getVertexColors().add(color3);
				}
			}

			List<Material> materials = new ArrayList<Material>();
			MeshLambertMaterial lmaterial = new MeshLambertMaterial();
			lmaterial.setColor( new Color3f(0xffffff) );
			lmaterial.setShading( Material.SHADING.FLAT );
			lmaterial.setVertexColors( Material.COLORS.VERTEX );
			materials.add(lmaterial);

			MeshBasicMaterial bmaterial = new MeshBasicMaterial();
			bmaterial.setColor( new Color3f(0x000000) );
			bmaterial.setShading( Material.SHADING.FLAT );
			bmaterial.setWireframe(true);
			bmaterial.setTransparent( true );
			materials.add(bmaterial);

			DimensionalObject group1 = SceneUtils.createMultiMaterialObject( geometry, materials );
			group1.getPosition().setX(-400);
			group1.getRotation().setX(-1.87f);
			getScene().addChild( group1 );

			DimensionalObject group2 = SceneUtils.createMultiMaterialObject( geometry2, materials );
			group2.getPosition().setX(400);
			group2.getRotation().setX(0f);
			getScene().addChild( group2 );

			DimensionalObject group3 = SceneUtils.createMultiMaterialObject( geometry3, materials );
			group3.getPosition().setX(0);
			group3.getRotation().setX(0f);
			getScene().addChild( group3 );
		}
		
		@Override
		protected void onStop()
		{			
		}
		
		@Override
		protected void onUpdate(double duration)
		{
			getCamera().getPosition().addX(( - mouseX - getRenderer().getCanvas().getWidth()/2.5f - getCamera().getPosition().getX()) );
			getCamera().getPosition().addY(( mouseY - getRenderer().getCanvas().getHeight()/2.5f- getCamera().getPosition().getY()) );

			getCamera().lookAt( getScene().getPosition());
			super.onUpdate(duration);
		}
	}
		
	public GeometryColors()
	{
		super("Vertices colors", "Here are shown Icosahedrons and different vertex colors. At the bottom located shadow texture. Drag mouse to move. This example based on the three.js example.");
	}

	@Override
	public void onAnimationReady(RenderingReadyEvent event)
	{
		super.onAnimationReady(event);

		this.renderingPanel.getRenderer().getCanvas().addMouseMoveHandler(new MouseMoveHandler() {
		      @Override
		      public void onMouseMove(MouseMoveEvent event)
		      {
		    	  	DemoScene rs = (DemoScene) renderingPanel.getRenderingScene();
		    	  	rs.mouseX = event.getX(); 
		    	  	rs.mouseY = event.getY();
		      }
		});
	}

	@Override
	public RenderPanelAttributes getRenderPanelAttributes()
	{
		RenderPanelAttributes att = super.getRenderPanelAttributes();
		att.clearColor         = 0xDDDDDD;
		
		return att;
	}

	@Override
	public DemoScene onInitialize()
	{
		return new DemoScene();
	}
	
	@Override
	public ImageResource getIcon()
	{
		return Demo.resources.exampleColors();
	}
	
	@Override
	protected void asyncOnInitialize(final AsyncCallback<DemoRenderingScene> callback)
	{
		GWT.runAsync(GeometryColors.class, new RunAsyncCallback() 
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
