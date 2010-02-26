package ca.keefer.sanemethod.Entity;

import net.phys2d.math.Vector2f;
import net.phys2d.raw.Body;
import net.phys2d.raw.CollisionEvent;
import net.phys2d.raw.StaticBody;
import net.phys2d.raw.World;
import net.phys2d.raw.shapes.Polygon;

import org.newdawn.slick.Graphics;
import org.newdawn.slick.Image;
import org.newdawn.slick.SpriteSheet;
import org.newdawn.slick.geom.Path;
import org.newdawn.slick.geom.Rectangle;
import org.newdawn.slick.util.Log;

public class Waypoint extends AbstractEntity{

	float x, y;
	int zOrder;
	/** The world in which this entity can be found */
	protected World world;
	Image def, triggered;
	boolean state;
	final boolean STATE_DEFAULT = false;
	final boolean STATE_TRIGGERED = true;
	int checkTimer;
	final int CHECK_PERIOD = 250;
	
	public Waypoint(float x, float y, int zOrder, SpriteSheet spriteSheet){
		this.active=true;
		// Dimensions of this switch
		Path wayPath = new Path(x,y);
		wayPath.lineTo(x+(20), y);
		wayPath.lineTo(x+(20), y+(64));
		wayPath.lineTo(x,y+(64));
		wayPath.lineTo(x, y);
		wayPath.close();
		
		float[] pts = wayPath.getPoints();
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
		this.def = spriteSheet.getSubImage(0, 0);
		this.triggered = spriteSheet.getSubImage(1,0);
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
		this.world = world;
	}

	@Override
	public void setZOrder(int z) {
		zOrder = z;
		
	}

	@Override
	public void preUpdate(int delta) {
		// TODO Auto-generated method stub
		
	}
	
	@Override
	public void update(int delta) {
		if (state == STATE_DEFAULT){
			checkTimer += delta;
			if (checkTimer > CHECK_PERIOD){
				checkTimer=0;
				triggerOnCollision();
			}
		}
		
	}

	@Override
	public void render(Graphics g) {
		if(this.state == STATE_DEFAULT){
			g.drawImage(def, x, y);
		}else{
			g.drawImage(triggered, x, y);
		}
		
	}
	
	protected boolean triggerOnCollision(){
		if (world == null) {
			Log.error("no world found in waypoint");
			return false;
		}
		// Get all the collision events involving this object
		CollisionEvent[] events = world.getContacts(this.body);
		
		for (int i=0;i<events.length;i++){
			if (events[i].getBodyB() == this.body){
				if (!(events[i].getBodyA().isStatic())){
					if (events[i].getBodyA().getUserData().getClass() == Player.class){
						Player p = (Player) events[i].getBodyA().getUserData();
						p.setWaypoint(x, y);
						this.state = STATE_TRIGGERED;
						this.world.remove(this.body);
					}
				}
			}else if (events[i].getBodyA() == this.body){
				if (!(events[i].getBodyB().isStatic())){
					if (events[i].getBodyB().getUserData().getClass() == Player.class){
						Player p = (Player) events[i].getBodyB().getUserData();
						p.setWaypoint(x, y);
						this.state = STATE_TRIGGERED;
						this.world.remove(this.body);
					}
				}
			}
		}
		return false;
	}
	

}
