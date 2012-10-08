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

import thothbot.parallax.core.client.gl2.arrays.Float32Array;
import thothbot.parallax.core.shared.cameras.PerspectiveCamera;
import thothbot.parallax.core.shared.core.Color;
import thothbot.parallax.core.shared.core.GeometryBuffer;
import thothbot.parallax.core.shared.materials.Material.COLORS;
import thothbot.parallax.core.shared.materials.ParticleBasicMaterial;
import thothbot.parallax.core.shared.objects.ParticleSystem;
import thothbot.parallax.core.shared.scenes.Fog;
import thothbot.parallax.demo.client.ContentWidget;
import thothbot.parallax.demo.client.Demo;
import thothbot.parallax.demo.client.DemoAnnotations.DemoSource;

import com.google.gwt.core.client.GWT;
import com.google.gwt.core.client.RunAsyncCallback;
import com.google.gwt.resources.client.ImageResource;
import com.google.gwt.user.client.rpc.AsyncCallback;

public final class BufferGeometryParticles extends ContentWidget
{

	/*
	 * Prepare Rendering Scene
	 */
	@DemoSource
	class DemoScene extends DemoAnimatedScene 
	{
		PerspectiveCamera camera;
		ParticleSystem particleSystem;

		@Override
		protected void onStart()
		{
			camera = new PerspectiveCamera(
					27, // fov
					getRenderer().getCanvas().getAspectRation(), // aspect 
					5, // near
					35000 // far 
			);
			camera.getPosition().setZ(2750);

			
			getScene().setFog( new Fog( 0x050505, 2000, 3500 ) );

			//

			int particles = 500000;

			GeometryBuffer geometry = new GeometryBuffer();
			geometry.setVerticesNeedUpdate(true);
			geometry.setColorsNeedUpdate(true);
			geometry.setWebGlVertexArray(Float32Array.create(particles * 3 * 3));
			geometry.setWebGlColorArray(Float32Array.create(particles * 3 * 3));

			Float32Array positions = geometry.getWebGlVertexArray();
			Float32Array colors = geometry.getWebGlColorArray();
			
			Color color = new Color();
			double n = 1000, n2 = n/2;	// particles spread in the cube

			for ( int i = 0; i < positions.getLength(); i += 3 ) 
			{

				// positions
				double x = Math.random() * n - n2;
				double y = Math.random() * n - n2;
				double z = Math.random() * n - n2;

				positions.set( i, x );
				positions.set( i + 1, y );
				positions.set( i + 2, z );
				

				// colors
				double vx = ( x / n ) + 0.5;
				double vy = ( y / n ) + 0.5;
				double vz = ( z / n ) + 0.5;

				color.setRGB( vx, vy, vz );
				
				colors.set( i, color.getR() );
				colors.set( i + 1, color.getG() );
				colors.set( i + 2, color.getB() );
			}

			geometry.computeBoundingSphere();

			//
			ParticleBasicMaterial material = new ParticleBasicMaterial();
			material.setVertexColors(COLORS.VERTEX);
			material.setSize(15.0);

			particleSystem = new ParticleSystem( geometry, material );
			getScene().add( particleSystem );

			//

			getRenderer().setClearColor( getScene().getFog().getColor(), 1 );
			getRenderer().setAutoClear(false);
		}
		
		@Override
		protected void onUpdate(double duration)
		{
			double time = duration * 0.001;

			particleSystem.getRotation().setX( time * 0.25 );
			particleSystem.getRotation().setY( time * 0.5 );
			getRenderer().render(getScene(), camera);
		}
	}
		
	public BufferGeometryParticles() 
	{
		super("Buffered particles", "This example based on the three.js example.");
	}
	
	@Override
	public DemoScene onInitialize()
	{
		return new DemoScene();
	}

	@Override
	public ImageResource getIcon()
	{
		return Demo.resources.exampleCube();
	}
	
	@Override
	protected void asyncOnInitialize(final AsyncCallback<DemoAnimatedScene> callback)
	{
		GWT.runAsync(BufferGeometryParticles.class, new RunAsyncCallback() 
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
