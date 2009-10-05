package ca.keefer.sanemethod;

import org.newdawn.slick.geom.Shape;

/**
 * This interface is applicable to all objects that can cast shadows - those that do not
 * return null instead of a z-index
 * Thanks to Another Early Morning 
 * (http://www.anotherearlymorning.com/2008/09/physical-queries-for-the-math-challenged-real-time-shadows/)
 * for the idea and most of the implementation
 * All instances of the Object class return shadows - this includes all Placeables and all Sprites
 * @author Christopher Keefer
 *
 */
public interface ShadowCaster {

	/* 
	 * Returns an object's assigned depth
	 * Note: As only 3d shapes really cast shadows, a depth is simply assigned to object's
	 * which we want to cast shadows, and the depth (z-index) assigned to them determines
	 * the length of their shadow as well.
	*/
	public int getZIndex();
	
	// Call calculating routines and return the objects shadow as a Slick Shape
	public Shape getShadow(float direction);
}
