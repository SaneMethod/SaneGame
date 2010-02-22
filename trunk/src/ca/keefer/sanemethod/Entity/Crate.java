package ca.keefer.sanemethod.Entity;

import net.phys2d.raw.Body;
import net.phys2d.raw.World;
import net.phys2d.raw.shapes.Box;
import net.phys2d.raw.shapes.Circle;

import org.newdawn.slick.Graphics;
import org.newdawn.slick.Image;
import org.newdawn.slick.SpriteSheet;
import org.newdawn.slick.geom.Rectangle;

public class Crate extends AbstractEntity{
	/** The world in which this entity can be found */
	protected World world;
	/** Dimensions of this entity, as a multiple of the tile width or height */
	int height, width;
	/** zOrder controls the depth of this entity */
	private int zOrder;
	/** spriteSheet for animation of this entity */
	SpriteSheet spriteSheet;
	/** Image of this Entity */
	Image crateImage;

	public Crate(float x, float y, int width, int height, int zOrder, int mass, SpriteSheet spriteSheet){
		active=true;
		this.body = new Body(new Box(width,height), mass);
		this.body.setFriction(0.6f);
		this.body.setMaxVelocity(30, 50);
		this.body.setUserData(this);
		this.zOrder=zOrder;
		this.height=height;
		this.width=width;
		crateImage = spriteSheet.getSubImage(0, 1);
		setPosition(x,y);
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
		zOrder=z;
	}
	
	@Override
	public void preUpdate(int delta) {
		
	}

	@Override
	public void update(int delta) {
		// TODO Auto-generated method stub
		
	}
	
	@Override
	public void render(Graphics g) {
		g.translate(getX(), getY());
		g.rotate(0,0,(float) Math.toDegrees(body.getRotation()));
		crateImage.draw(-width/2,-height/2,width,height);
		g.rotate(0,0,(float) -Math.toDegrees(body.getRotation()));
		g.translate(-getX(), -getY());
	}

}
