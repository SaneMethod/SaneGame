package ca.keefer.sanemethod.Entity;


import org.newdawn.slick.Graphics;
import org.newdawn.slick.geom.Rectangle;

/**
 * This interface defines the methods all classes implementing the Entity interface must possess.
 * @author Christopher Keefer
 * @version 1.1
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
}
