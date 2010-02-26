package ca.keefer.sanemethod.Entity;

import net.phys2d.math.Vector2f;
import net.phys2d.raw.Body;
import net.phys2d.raw.CollisionEvent;
import net.phys2d.raw.StaticBody;
import net.phys2d.raw.World;
import net.phys2d.raw.shapes.Polygon;

import org.newdawn.slick.Graphics;
import org.newdawn.slick.Image;
import org.newdawn.slick.geom.Path;
import org.newdawn.slick.geom.Rectangle;
import org.newdawn.slick.util.Log;

public class Spike extends AbstractEntity{

	/** The world in which this entity can be found */
	protected World world;
	/** Position of this entity */
	float x, y;
	/** zOrder controls the depth of this entity */
	private int zOrder;
	/** Image representing this entity */
	Image spikeImage;
	/** whether the image is inverted */
	boolean inverted;
	/** How long its been since we last updated this entities status */
	int checkTimer;
	/** How long between updating whether or not this block has collided with the player */
	private final int CHECK_PERIOD = 200;
	
	public Spike(float x, float y, int zOrder, boolean inverted, Image spikeImage){
		this.zOrder=zOrder;
		this.active=true;
		this.inverted=inverted;
		this.spikeImage=spikeImage;
		this.x=x;
		this.y=y;
		// Dimensions of this spike
		Path spikePath = new Path(x,y);
		spikePath.lineTo(x+(64), y);
		spikePath.lineTo(x+(64), y+(64));
		spikePath.lineTo(x,y+(64));
		spikePath.lineTo(x, y);
		spikePath.close();
		
		float[] pts = spikePath.getPoints();
		Vector2f[] vecs = new Vector2f[(pts.length / 2)];
		for (int j=0;j<vecs.length;j++) {
			vecs[j] = new Vector2f(pts[j*2],pts[(j*2)+1]);
		}
		this.x=x;
		this.y=y;
		
		this.body = new StaticBody(new Polygon(vecs));
		body.setUserData(this);
		body.setRestitution(1f);
		body.setFriction(0f);
		body.setMoveable(false);
		body.setRotatable(false);
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
		checkTimer+=delta;
		if (checkTimer > CHECK_PERIOD){
			checkTimer=0;
			checkForCollision();
		}
	}

	@Override
	public void update(int delta) {
		// TODO Auto-generated method stub
		
	}
	
	@Override
	public void render(Graphics g) {
		if (inverted){
			g.drawImage(spikeImage.getFlippedCopy(false, true),x,y);
		}else{
			g.drawImage(spikeImage,x,y);
		}
	}

	protected void checkForCollision(){
		if (world == null) {
			Log.error("no world found in Spike");
			return;
		}
		// Get all the collision events involving this object
		CollisionEvent[] events = world.getContacts(this.body);
		
		for (int i=0;i<events.length;i++){
			if (events[i].getBodyB() == this.body){
				if ((!(events[i].getBodyA().isStatic()) && (events[i].getBodyA().getUserData().getClass() == Player.class))){
					Player pHurt = (Player) events[i].getBodyA().getUserData();
					pHurt.setDying(true);
					pHurt.setLockOut(true);
					return;
				}
			}else if (events[i].getBodyA() == this.body){
				if ((!(events[i].getBodyB().isStatic()) && (events[i].getBodyB().getUserData().getClass() == Player.class))){
					Player pHurt = (Player) events[i].getBodyB().getUserData();
					pHurt.setDying(true);
					pHurt.setLockOut(true);
					return;
				}
			}
		}
	}

}
