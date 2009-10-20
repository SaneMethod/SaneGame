package ca.keefer.sanemethod.Entity;

import org.newdawn.slick.geom.Rectangle;
import org.newdawn.slick.geom.Vector2f;

/**
 * This class defines an Axis-Aligned Bounding Box, as derived from the Rectangle class,
 * with the properties of a position, and two half-wdith vectors.
 * @author Christopher Keefer
 *
 */
public class AABB extends Rectangle{

	private static final long serialVersionUID = 1L;
	
	private Vector2f xw; // x half-width vector
	private Vector2f yw; // y half-width vector
	
	public AABB(float x, float y, float width, float height) {
		super(x, y, width, height);
		xw = new Vector2f(x/2,0);
		yw = new Vector2f(0,y/2);
	}
	
	public Vector2f getXVector(){
		return xw;
	}
	public void setXVector(Vector2f xw){
		this.xw = xw;
	}
	
	public Vector2f getYVector(){
		return yw;
	}
	public void setYVector(Vector2f yw){
		this.yw = yw;
	}
	
}
