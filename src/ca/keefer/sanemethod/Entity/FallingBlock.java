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

/**
 * This class extends AbstractEntity to create platforms that only the Player can interact with,
 * and which will disintegrate shortly after the hero jumps on them (that is, their physical body
 * will be removed from the simulations, and they will be set to inactive).
 * @author Christopher Keefer
 *
 */
public class FallingBlock extends AbstractEntity{

	/** The world in which this entity can be found */
	protected World world;
	/** position of this entity */
	float x,y;
	/** zOrder controls the depth of this entity */
	private int zOrder;
	/** How long its been since we last updated this entities status */
	int checkTimer;
	/** Image for this block */
	Image blockImage;
	/** How long between updating whether or not this block has collided with the player */
	private final int CHECK_PERIOD = 200;
	/** whether or not this block should is disolving */
	boolean dissolve;
	/** How long we wait before dissolving */
	public int DISSOLVE_PERIOD;
	/** how long we've been waiting for this block to dissolve */
	public int dissolveTimer;
	
	public FallingBlock(float x, float y, int dissolvePeriod, int zOrder, Image image){
		this.active=true;
		blockImage=image;
		DISSOLVE_PERIOD = dissolvePeriod;
		// Dimensions of this block
		Path switchPath = new Path(x,y);
		switchPath.lineTo(x+(64), y);
		switchPath.lineTo(x+(64), y+(64));
		switchPath.lineTo(x,y+(64));
		switchPath.lineTo(x, y);
		switchPath.close();
		
		float[] pts = switchPath.getPoints();
		Vector2f[] vecs = new Vector2f[(pts.length / 2)];
		for (int j=0;j<vecs.length;j++) {
			vecs[j] = new Vector2f(pts[j*2],pts[(j*2)+1]);
		}
		this.x=x;this.y=y;
		this.body = new StaticBody(new Polygon(vecs));
		body.setUserData(this);
		body.setRestitution(1f);
		body.setFriction(0f);
		this.zOrder = zOrder;
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
		checkTimer +=delta;
		if(checkTimer > CHECK_PERIOD){
			checkTimer = 0;
			if(checkForCollision()){
				dissolve=true;
			}
		}
		if (dissolve){
			dissolveTimer +=delta;
			if (dissolveTimer > DISSOLVE_PERIOD){
				dissolveTimer=0;
				this.world.remove(this.body);
				this.active=false;
			}
		}
		
	}

	@Override
	public void update(int delta) {
		// TODO Auto-generated method stub
		
	}
	
	@Override
	public void render(Graphics g) {
		if (!dissolve){
		g.drawImage(blockImage,x,y);
		}else{
			float xRand = (float)(10 * Math.random());
			float yRand = (float)(10 * Math.random());
			g.drawImage(blockImage,x+xRand,y+yRand);
		}
		
	}
	
	protected boolean checkForCollision(){
		if (world == null) {
			Log.error("no world found in FallingBlock");
			return false;
		}
		// Get all the collision events involving this object
		CollisionEvent[] events = world.getContacts(this.body);
		
		for (int i=0;i<events.length;i++){
			if (events[i].getBodyB() == this.body){
				if (!(events[i].getBodyA().isStatic() && events[i].getBodyA().getUserData().getClass() == Player.class)){
					return true;
				}
			}else if (events[i].getBodyA() == this.body){
				if (!(events[i].getBodyB().isStatic() && events[i].getBodyB().getUserData().getClass() == Player.class)){
					return true;
				}
			}
		}
		return false;
	}

}
