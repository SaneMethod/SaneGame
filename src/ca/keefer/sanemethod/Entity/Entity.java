package ca.keefer.sanemethod.Entity;

import org.newdawn.slick.Graphics;
import org.newdawn.slick.geom.Rectangle;

/**
 * This interface defines some of the attributes and methods all classes implementing the Entity
 * interface must possess.
 * @author Christopher Keefer
 * @version 1.1
 * @see ca.keefer.sanemethod.SomeObject
 * @see ca.keefer.sanemethod.Sprite
 * @see ca.keefer.sanemethod.Player
 *
 */
public interface Entity {

	/**
	 * Get the x position of this Entity 
	 * @return float x position
	 */
	public float getXPosition();
	
	/**
	 * Get the y position of this Entity
	 * @return float y position
	 */
	public float getYPosition();
	
	/**
	 * Set the x position of this Entity
	 * @param x float x position to set
	 */
	public void setXPosition(float x);
	
	/**
	 * Set the y position of this Entity
	 * @param y float y position to set
	 */
	public void setYPosition(float y);
	
	/**
	 * Draw this entity at its given x,y position
	 * @param g Graphics context to draw to
	 */
	public void render(Graphics g);
	
	/**
	 * Update this entity's position, state, etc.
	 * @param delta int Time elapsed since last update
	 */
	public void update(int delta);
	
	public Rectangle getBoundingRectangle();
	
	public boolean collides(Entity e);
	
	public void impulse(Entity e);
}
