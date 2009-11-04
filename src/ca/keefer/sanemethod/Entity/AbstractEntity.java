package ca.keefer.sanemethod.Entity;

import net.phys2d.math.Vector2f;
import net.phys2d.raw.Body;
import net.phys2d.raw.World;

/**
 * The common bits of all entities. A bunch of utility methods
 * and a physical body.
 * 
 * @author Kevin Glass, Christopher Keefer
 */
public abstract class AbstractEntity implements Entity {
	/** The physical body representing the entity */
	protected Body body;
	
	/**
	 * Set the velocity of the entity
	 * 
	 * @param x The x component of the velocity to apply
	 * @param y The y component of the velocity to apply
	 */
	public void setVelocity(float x, float y) {
		Vector2f vec = new Vector2f(body.getVelocity());
		vec.scale(-1f);
		body.adjustVelocity(vec);
		body.adjustVelocity(new Vector2f(x,y));
	}
	
	/**
	 * Set the x coordinate of this entities position
	 * 
	 * @param x The new x coordinate 
	 */
	public void setX(float x) {
		body.setPosition(x, getY());
	}

	/**
	 * Set the y coordinate of this entities position
	 * 
	 * @param y The new y coordinate 
	 */
	public void setY(float y) {
		body.setPosition(getX(), y);
	}
	
	/**
	 * Set the position of this entity
	 * 
	 * @param x The new x coordinate 
	 * @param y The new y coordinate
	 */
	public void setPosition(float x, float y) {
		body.setPosition(x,y);
	}
	
	/**
	 * Get the x coordinate of this entity's position 
	 * 
	 * @return The x coordinate of this entity
	 */
	public float getX() {
		return body.getPosition().getX();
	}

	/**
	 * Get the y coordinate of this entity's position 
	 * 
	 * @return The y coordinate of this entity
	 */
	public float getY() {
		return body.getPosition().getY();
	}

	/**
	 * Get the x component of the velocity of this entity
	 * 
	 * @return The x component of the velocity of this entity
	 */
	public float getVelocityX() {
		return body.getVelocity().getX();
	}
	public void setVelocityY(float y){
		body.adjustVelocity(new Vector2f(body.getVelocity().getX(),y));
	}

	/**
	 * Get the y component of the velocity of this entity
	 * 
	 * @return The y component of the velocity of this entity
	 */
	public float getVelocityY() {
		return body.getVelocity().getY();
	}
	public void setVelocityX(float x){
		body.adjustVelocity(new Vector2f(x,body.getVelocity().getY()));
	}
	
	public void setVelocity(Vector2f v){
		// Kill the current velocity, and then apply the new velocity
		Vector2f vec = new Vector2f(body.getVelocity());
		vec.negate();
		body.adjustVelocity(vec);
		body.adjustVelocity(v);
	}
	
	
	
	
	
}
