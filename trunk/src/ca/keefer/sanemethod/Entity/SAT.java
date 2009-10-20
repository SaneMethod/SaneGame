package ca.keefer.sanemethod.Entity;

import java.util.ArrayList;

import org.newdawn.slick.Graphics;

/**
 * Implements the Separating Axis Thereom as expressed in: 
 * Gomez, Miguel. Simple Intersection Tests for Games.
 * Eberly, David. Intersection of Convex Objects: The Method of Separating Axes.
 * and: Metanet Software, N Tutorial A - Collision Detection and Response.
 * @author Christopher Keefer
 * @version 1.1
 * @see ca.keefer.sanemethod.Entity.AABB
 *
 */
public class SAT {

	ArrayList<Entity> collisionList; //expandable list of objects whose collisions we need to test
	
	public SAT(){
		collisionList = new ArrayList<Entity>();
	}
	
	public SAT(ArrayList<Entity> cl){
		collisionList = cl;
	}
	
	public void addEntity(Entity e){
		collisionList.add(e);
	}
	
	public void testForCollision(Graphics g){
		// for each object in the collision list, test for collision
		for (int i=0; i<collisionList.size();i++){
			// check on each axis - if not all overlapping, discard
			for (int x=0;x<collisionList.size();x++){
				if (i != x && collisionList.get(i).collides(collisionList.get(x))){
					collisionList.get(i).impulse(collisionList.get(x));
					g.drawString("Collision!",10,50);
				}
			}
		}
	}
}
