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

package thothbot.parallax.demo.resources;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import thothbot.parallax.core.shared.core.Face3;
import thothbot.parallax.core.shared.core.Vector3;

/**
 * Suggested Readings
 * <p>
 * Advanced Character Physics by Thomas Jakobsen Character<br>
 * <a href="http://freespace.virgin.net/hugo.elias/models/m_cloth.htm">freespace.virgin.net</a><br>
 * <a href="http://en.wikipedia.org/wiki/Cloth_modeling">wikipedia.org</a><br>
 * <a href="http://cg.alexandra.dk/tag/spring-mass-system/">cg.alexandra.dk</a><br>
 * Real-time Cloth Animation <a href="http://www.darwin3d.com/gamedev/articles/col0599.pdf">www.darwin3d.com</a>
 * <p>
 * Based on three.js code
 * 
 * @author thothbot
 *
 */
public class Cloth 
{

	static final double damping = 0.01;
	static final double drag = 1 - damping;
	static final double mass = 0.1;

	class Particle
	{
		private Vector3 position;
		private Vector3 previous;
		private Vector3 a;
		private double invMass;
		
		private Vector3 tmp;
		private Vector3 tmp2;
		
		public Particle(double x, double y, double z)
		{
			this.position = new Vector3(x, y, z); // position
			this.previous = new Vector3(x, y, z); // previous
			this.a = new Vector3(0, 0, 0); // acceleration
			this.invMass = 1 / mass;
			this.tmp = new Vector3();
			this.tmp2 = new Vector3();
		}
		
		/**
		 * Force -> Acceleration
		 */
		public void addForce(Vector3 force) 
		{
			this.a.add(
				this.tmp2.copy(force).multiply(this.invMass)
			);
		}
		
		/**
		 * Performs verlet integration
		 */
		public void integrate(double timesq) 
		{
			Vector3 newPos = this.tmp.sub(this.position, this.previous);
			newPos.multiply(drag).add(this.position);
			newPos.add(this.a.multiply(timesq));
			
			this.tmp = this.previous;
			this.previous = this.position;
			this.position = newPos;

			this.a.set(0, 0, 0);
		}
	}

	private static final double restDistance = 25;
	
	int GRAVITY = 981; // 
	Vector3 gravity = new Vector3( 0, -GRAVITY, 0 ).multiply(Cloth.Particle.mass);


	double TIMESTEP = 14 / 1000;
	double TIMESTEP_SQ = TIMESTEP * TIMESTEP;

	var pins = [true];
	pins[cloth.w] = true;


	boolean wind = true;
	int windStrength = 2;
	Vector3 windForce = new Vector3(0,0,0);

	Vector3 ballPosition = new Vector3(0, -45, 0);
	int ballSize = 60; //40

	Vector3 tmpForce = new Vector3();
	
	private int width;
	private int height;
	List<Cloth.Particle> particles;
	List<List<Cloth.Particle>> constrains;
	
	public Cloth()
	{
		this(10, 10);
	}

	public Cloth(int width, int height)
	{	
		this.width = width;
		this.height = height;
		
		particles = new ArrayList<Cloth.Particle>();
		constrains = new ArrayList<List<Particle>>();

		// Create particles
		for (int v = 0; v <= height; v++) 
		{
			for (int u = 0; u <= width; u++) 
			{
				particles.add(
					new Particle((u - width/2) * restDistance, (v - height/2) * -restDistance, 0)
				);
			}
		}

		// Structural

		for (int v = 0; v < height; v++) 
		{
			for (int u = 0; u < width; u++) 
			{
				constrains.add(Arrays.asList(
					particles.get( index(u, v, width) ),
					particles.get( index(u, v + 1, width) )
				));

				constrains.add(Arrays.asList(
					particles.get( index(u, v, width) ),
					particles.get( index(u + 1, v, width) )
				));

			}
		}

		for (int u = width, v=0; v < height; v++) 
		{
			constrains.add(Arrays.asList(
				particles.get( index(u, v, width) ),
				particles.get( index(u, v + 1, width) )
			));
		}

		for (int v = height, u = 0; u < width; u++) 
		{
			constrains.add(Arrays.asList(
				particles.get( index(u, v, width) ),
				particles.get( index(u + 1, v, width) )
			));
		}
	}
	
	public void simulate() 
	{
		// Aerodynamics forces
		if (wind)
		{
			faces = clothGeometry.faces;

			for (int i = 0, il = faces.size(); i < il; i++)
			{
				Face3 face = faces.get(i);
				Vector3 normal = face.getNormal();

				tmpForce.copy(normal).normalize().multiply(normal.dot(windForce));
				particles.get(face.getA()).addForce(tmpForce);
				particles.get(face.getB()).addForce(tmpForce);
				particles.get(face.getC()).addForce(tmpForce);
			}
		}
		
		for (int i = 0, il = particles.size(); i < il; i++) 
		{
			Cloth.Particle particle = particles.get(i);
			particle.addForce(gravity);
			// particle.addForce(windForce);
			particle.integrate(TIMESTEP_SQ);
		}

		// Start Constrains
		for (int i = 0, il = constrains.size(); i < il; i++) 
		{
			List<Cloth.Particle> constrain = constrains.get(i);
			satisifyConstrains(constrain.get(0), constrain.get(1));
		}

		// Ball Constrains

		ballPosition.z = -Math.sin(Date.now()/300) * 90 ; //+ 40;
		ballPosition.x = Math.cos(Date.now()/200) * 70;

		if (sphere.isVisible())
		{
			for (int i = 0, il = particles.size(); i < il; i++) 
			{
				Cloth.Particle particle = particles.get(i);
				Vector3 pos = particle.position;
				Vector3 diff = new Vector3();
				diff.sub(pos, ballPosition);
				if (diff.length() < ballSize) 
				{
					// collided
					diff.normalize().multiply(ballSize);
					pos.copy(ballPosition).add(diff);
				}
			}
		}

		// Pin Constrains
		for (int i = 0, il = cloth.w; i <= il; i++) 
		{
			if (pins[i])
			{
				particle = particles[i];
				particle.previous.set((i - cloth.w/2) * restDistance,  -cloth.h/2 * -restDistance, 0);
				particle.position.copy(particle.previous);
			}
		}
	}
	
	private void satisifyConstrains(Cloth.Particle p1, Cloth.Particle p2) 
	{
		Vector3 diff = new Vector3();
		
		diff.sub(p2.position, p1.position);
		double currentDist = diff.length();
		
		if (currentDist == 0) 
			return; // prevents division by 0
		
		Vector3 correction = diff.multiply(1 - restDistance/currentDist);
		Vector3 correctionHalf = correction.multiply(0.5);
		p1.position.add(correctionHalf);
		p2.position.sub(correctionHalf);
	}
	
	private int index(int u, int v, int w) 
	{
		return u + v * (w + 1);
	}
}
