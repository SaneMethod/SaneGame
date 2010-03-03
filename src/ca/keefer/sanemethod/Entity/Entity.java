package ca.keefer.sanemethod.Entity;


import net.phys2d.raw.Body;
import net.phys2d.raw.World;

import org.newdawn.slick.Graphics;
import org.newdawn.slick.geom.Rectangle;

import ca.keefer.sanemethod.Environment.Environment;

/**
 * This interface defines the methods all classes implementing the Entity interface must possess.
 * @author Christopher Keefer, Kevin Glass
 * @version 1.2
 * @see ca.keefer.sanemethod.PhysObject
 * @see ca.keefer.sanemethod.Sprite
 * @see ca.keefer.sanemethod.Player
 *
 */

public interface Entity {
	
	/**
	 * Get the bounding box for this Entity based on its current position
	 * and the currently displaying animation image 
	 * @return
	 */
	public Rectangle getBoundingBox();
	
	/**
	 * Get the zOrder (depth) of an Entity - an Entity with a higher zOrder will
	 * be drawn after/over an Entity with a lower zOrder.
	 * @return zOrder/depth of this enttiy
	 */
	public int getZOrder();
	
	/**
	 * Get the zOrder (depth) of an Entity - an Entity with a higher zOrder will
	 * be drawn after/over an Entity with a lower zOrder.
	 * @param z the zOrder to be set for this Entity.
	 */
	public void setZOrder(int z);
	
	/**
	 * Update this entity. This method is called once before physical world is updated
	 * each cycle. It is only ever called once per update.
	 * 
	 * @param delta The amount of time passed since last update
	 */
	public void preUpdate(int delta);
	
	/**
	 * Update this entity's position, state, etc. during the physical loop.
	 * Called each time the World is updated.
	 * @param delta int Time elapsed since last update
	 */
	public void update(int delta);
	
	/**
	 * Draw this entity at its given Body position
	 * @param g Graphics context to draw to
	 */
	public void render(Graphics g);
	
	/**
	 * Get the physical body of this Entity for interaction
	 * with the Phys2D World
	 */
	public Body getBody();
	
	/**
	 * Set the Phys2D world this Entity belongs to
	 * @param world
	 */
	public void setWorld(World world);
	
	/**
	 * Get the phys2D World this Entity belongs to
	 * @return
	 */
	public World getWorld();
	
	/** Get whether this entity is currently active, or should be removed from the entityLayer */
	public boolean isActive();
	
	/** assign an TiledEnvironment to this entity */
	public void setEnvironment(Environment environment);
	
	public Environment getEnvironment();
	
	public void receiveKeyPress(int keyPressed);
	public void receiveKeyRelease(int keyReleased);
}
