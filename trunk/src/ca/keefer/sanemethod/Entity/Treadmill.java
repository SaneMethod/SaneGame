package ca.keefer.sanemethod.Entity;

import net.phys2d.raw.Body;
import net.phys2d.raw.World;
import net.phys2d.raw.forcesource.WindSource;

import org.newdawn.slick.Animation;
import org.newdawn.slick.Graphics;
import org.newdawn.slick.geom.Rectangle;

/**
 * Extends AbstractEntity. 
 * FIXME: Only methods for making this work (checking each update cycle for whether a body is still touching this one, resting body detection) carry problems or prohibitive performance costs. Is this workable?
 * @author Christopher Keefer
 *
 */
public class Treadmill extends AbstractEntity{
	
	/** The world in which this entity can be found */
	protected World world;
	/** The dimensions of this entity */
	int height, width;
	/** Animation for this object */
	Animation treadAnim;
	/** zOrder controls the depth of this entity */
	private int zOrder;
	/** Direction of this treadmill */
	boolean direction;
	/** Windsource for the treadmill effect */
	WindSource windSource;
	
	public static boolean RIGHT = true;
	public static boolean LEFT = false;
	
	public Treadmill(float x, float y, int speed, boolean direction, int zOrder){
		active=true;
		this.zOrder=zOrder;
		this.direction = direction;
		if (direction == LEFT){
			windSource = new WindSource(x,y,-100);
		}else{
			windSource = new WindSource(x,y,100);
		}
	}

	@Override
	public Body getBody() {
		return body;
	}

	@Override
	public Rectangle getBoundingBox() {
		return new Rectangle(this.getX(),this.getY(),this.getBody().getShape().getBounds().getWidth(),
				this.getBody().getShape().getBounds().getHeight());
	}

	@Override
	public World getWorld() {
		return world;
	}

	@Override
	public int getZOrder() {
		return zOrder;
	}

	@Override
	public void setWorld(World world) {
		this.world=world;
	}

	@Override
	public void setZOrder(int z) {
		this.zOrder=z;
	}
	
	@Override
	public void preUpdate(int delta) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void update(int delta) {
		// TODO Auto-generated method stub
		
	}
	
	@Override
	public void render(Graphics g) {
		// TODO Auto-generated method stub
		
	}

}
