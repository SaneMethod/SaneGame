package ca.keefer.sanemethod.Environment;

import org.newdawn.slick.Graphics;

/**
 * Interface for Layers - logically seperate aspects of the display that can be updated and rendered
 * on an individual basis. Layers should be attached to a Viewport to be rendered or updated.
 * @author Christopher Keefer
 * @version 1.1
 *
 */
public interface Layer {
	
	public void setId(int Id);
	
	public int getId();
	
	public int getType();
	
	/**
	 * Set whether this camera is the currently active one
	 * @param t
	 */
	public void setActive(boolean t);
	
	/**
	 * Return whether this state is active
	 * @return boolean active
	 */
	public boolean isActive();
	
	/**
	 * Set the viewport this layer belongs to
	 * @param v
	 */
	public void setViewPort(ViewPort v);
	
	/**
	 * Get the viewPort this layer belongs to (if any)
	 * @return The viewport this layer belongs to
	 */
	public ViewPort getViewPort();
	
	/**
	 * Contains any layer specific update information we might require
	 * @param delta
	 */
	public void update(int delta);
	
	/**
	 * Draw this layer to the given graphics context
	 * @param g
	 */
	public void render(Graphics g);
	
	/**
	 * Draw this layer on the given graphics context only within the given limits
	 * @param g
	 * @param xOffset
	 * @param yOffset
	 * @param xLimit
	 * @param yLimit
	 */
	public void render(Graphics g, int xOffset, int yOffset, int xLimit, int yLimit);
}
