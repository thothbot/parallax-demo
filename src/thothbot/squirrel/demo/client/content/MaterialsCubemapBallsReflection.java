/*
 * Copyright 2012 Alex Usachev, thothbot@gmail.com
 * 
 * This file is part of Squirrel project.
 * 
 * Squirrel is free software: you can redistribute it and/or modify it 
 * under the terms of the GNU General Public License as published by the 
 * Free Software Foundation, either version 3 of the License, or (at your 
 * option) any later version.
 * 
 * Squirrel is distributed in the hope that it will be useful, but 
 * WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY 
 * or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License 
 * for more details.
 * 
 * You should have received a copy of the GNU General Public License along with 
 * Squirrel. If not, see http://www.gnu.org/licenses/.
 */

package thothbot.squirrel.demo.client.content;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import thothbot.squirrel.core.client.shader.ShaderCubeMap;
import thothbot.squirrel.core.client.textures.CubeTexture;
import thothbot.squirrel.core.shared.Log;
import thothbot.squirrel.core.shared.cameras.PerspectiveCamera;
import thothbot.squirrel.core.shared.core.Color3f;
import thothbot.squirrel.core.shared.core.Vector3f;
import thothbot.squirrel.core.shared.geometries.Cube;
import thothbot.squirrel.core.shared.geometries.Sphere;
import thothbot.squirrel.core.shared.materials.MeshBasicMaterial;
import thothbot.squirrel.core.shared.materials.ShaderMaterial;
import thothbot.squirrel.core.shared.objects.Mesh;
import thothbot.squirrel.core.shared.scenes.Scene;
import thothbot.squirrel.core.shared.utils.ImageUtils;
import thothbot.squirrel.demo.client.ContentWidget;
import thothbot.squirrel.demo.client.Demo;
import thothbot.squirrel.demo.client.DemoAnnotations.DemoSource;
import thothbot.squirrel.demo.client.content.GeometryCube.Resources;

import com.google.gwt.core.client.GWT;
import com.google.gwt.core.client.RunAsyncCallback;
import com.google.gwt.resources.client.ClientBundle;
import com.google.gwt.resources.client.ImageResource;
import com.google.gwt.resources.client.ClientBundle.Source;
import com.google.gwt.user.client.rpc.AsyncCallback;

public final class MaterialsCubemapBallsReflection extends ContentWidget 
{

	/*
	 * Load texture
	 */
	@DemoSource
	public interface Resources extends ClientBundle
	{
		Resources INSTANCE = GWT.create(Resources.class);

		@Source("../../resources/textures/cube/pisa/nx.png")
		ImageResource nx();
		
		@Source("../../resources/textures/cube/pisa/ny.png")
		ImageResource ny();
		
		@Source("../../resources/textures/cube/pisa/nz.png")
		ImageResource nz();
		
		@Source("../../resources/textures/cube/pisa/px.png")
		ImageResource px();
		
		@Source("../../resources/textures/cube/pisa/py.png")
		ImageResource py();
		
		@Source("../../resources/textures/cube/pisa/pz.png")
		ImageResource pz();
	}

	/*
	 * Prepare Rendering Scene
	 */
	@DemoSource
	class DemoScene extends DemoRenderingScene 
	{
		public int mouseX = 0;
		public int mouseY = 0;
		
		private List<Mesh> speres;
		
		private Scene sceneCube;
		private PerspectiveCamera cameraCube;
		
		@Override
		protected void loadCamera()
		{
			setCamera(
					new PerspectiveCamera(
							60, // fov
							getRenderer().getCanvas().getAspectRation(), // aspect 
							1, // near
							100000 // far 
					)); 
		}

		@Override
		protected void onStart()
		{
			getCamera().getPosition().setZ(3200);
			getScene().addChild(getCamera());
			
			this.sceneCube = new Scene();
			this.cameraCube = new PerspectiveCamera( 60, getRenderer().getCanvas().getAspectRation(), 1, 100000 );

			sceneCube.addChild( cameraCube );

			Sphere geometry = new Sphere( 100, 32, 16 );


			Resources r = Resources.INSTANCE;
			CubeTexture textureCube = ImageUtils.loadTextureCube( Arrays.asList(r.nx(), r.ny(), r.nz(), r.px(), r.py(), r.pz()), null, null );
			
			MeshBasicMaterial.MeshBasicMaterialOptions mbOpt = new MeshBasicMaterial.MeshBasicMaterialOptions();
			mbOpt.color = new Color3f(0xffffff);
			mbOpt.envMap = textureCube;
			
			MeshBasicMaterial material = new MeshBasicMaterial( mbOpt );

			this.speres = new ArrayList<Mesh>();
			
			for ( int i = 0; i < 500; i ++ ) 
			{
				Mesh mesh = new Mesh( geometry, material );

				mesh.getPosition().setX( (float) (Math.random() * 10000.0 - 5000.0) );
				mesh.getPosition().setY( (float) (Math.random() * 10000.0 - 5000.0) );
				mesh.getPosition().setZ( (float) (Math.random() * 10000.0 - 5000.0) );

				float scale = (float) (Math.random() * 3.0 + 1.0);
				mesh.getScale().set(scale, scale, scale);

				getScene().addChild( mesh );

				this.speres.add( mesh );
			}

			// Skybox

			ShaderCubeMap shader = new ShaderCubeMap();
			shader.getUniforms().get("tCube").texture = textureCube; 

			ShaderMaterial.ShaderMaterialOptions sOpt = new ShaderMaterial.ShaderMaterialOptions();
			sOpt.fragmentShader = shader.getFragmentSource();
			sOpt.vertexShader = shader.getVertexSource();
			sOpt.uniforms = shader.getUniforms();
			sOpt.depthWrite = false;
			
			ShaderMaterial sMaterial = new ShaderMaterial( sOpt );
			Mesh mesh = new Mesh( new Cube( 100, 100, 100 ), sMaterial );
			mesh.setFlipSided(true);
			sceneCube.addChild( mesh );
			
			getRenderer().autoClear = false;
		}
		
		@Override
		protected void onStop()
		{			
		}
		
		@Override
		protected void onUpdate(double duration)
		{
			double timer = 0.0001 * duration;

			for ( int i = 0, il = this.speres.size(); i < il; i ++ ) 
			{
				this.speres.get(i).getPosition().setX( (float) (5000.0 * Math.cos( timer + i )) );
				this.speres.get(i).getPosition().setY( (float) (5000.0 * Math.sin( timer + i * 1.1 )) );
			}

			getCamera().getPosition().addX((float) (( mouseX - getCamera().getPosition().getX() ) * 0.05) );
			getCamera().getPosition().addY((float) (( - mouseY - getCamera().getPosition().getY() ) * 0.05) );

			getCamera().lookAt( getScene().getPosition() );
			this.cameraCube.getRotation().copy( getCamera().getRotation() );

			getRenderer().render( sceneCube, cameraCube );
			
			super.onUpdate(duration);
		}
	}
		
	public MaterialsCubemapBallsReflection() 
	{
		super("Cube reflection", "Drag mouse to move. This example based on the three.js example.");
	}
	
	@Override
	public DemoScene onInitialize()
	{
		Log.debug("Called onInitialize() class=" + this.getClass().getName());

		return new DemoScene();
	}

	@Override
	public ImageResource getIcon()
	{
		return Demo.resources.exampleMaterialsCubemapBallsReflection();
	}
	
	@Override
	protected void asyncOnInitialize(final AsyncCallback<DemoRenderingScene> callback)
	{
		GWT.runAsync(MaterialsCubemapBallsReflection.class, new RunAsyncCallback() 
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
