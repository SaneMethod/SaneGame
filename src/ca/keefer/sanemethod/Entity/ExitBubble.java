package ca.keefer.sanemethod.Entity;

import net.phys2d.raw.Body;
import net.phys2d.raw.CollisionEvent;
import net.phys2d.raw.World;
import net.phys2d.raw.shapes.Circle;
import net.phys2d.raw.StaticBody;

import org.newdawn.slick.Animation;
import org.newdawn.slick.Graphics;
import org.newdawn.slick.Image;
import org.newdawn.slick.SpriteSheet;
import org.newdawn.slick.geom.Rectangle;
import org.newdawn.slick.util.Log;

public class ExitBubble extends AbstractEntity {

	int zOrder;
	/** The world in which this entity can be found */
	protected World world;
	Image def;
	Animation triggered;
	boolean state;
	final boolean STATE_DEFAULT = false;
	final boolean STATE_TRIGGERED = true;
	int checkTimer;
	final int CHECK_PERIOD = 250;
	
	public ExitBubble(float x, float y, int zOrder, SpriteSheet spriteSheet){
		this.active=true;
		body = new StaticBody(new Circle(32));
		this.setPosition(x, y);
		body.setUserData(this);
		body.setRestitution(1f);
		body.setFriction(0f);
		def = spriteSheet.getSubImage(0, 0);
		triggered = new Animation(spriteSheet, 1,0,4,0,true,200,true);
		triggered.addFrame(spriteSheet.getSubImage(0,1), 200);
		triggered.addFrame(spriteSheet.getSubImage(1,1), 200);
		triggered.setLooping(false);
		state=STATE_DEFAULT;
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
			g.drawImage(def, this.body.getPosition().getX()-64, this.body.getPosition().getY()-64);
		}else{
			if (!triggered.isStopped()){
				g.drawAnimation(triggered, this.body.getPosition().getX()-64, this.body.getPosition().getY()-64);
			}
		}
		
	}
	
	protected boolean triggerOnCollision(){
		if (world == null) {
			Log.error("no world found in exit");
			return false;
		}
		// Get all the collision events involving this object
		CollisionEvent[] events = world.getContacts(this.body);
		
		for (int i=0;i<events.length;i++){
			if (events[i].getBodyB() == this.body){
				if (!(events[i].getBodyA().isStatic())){
					if (events[i].getBodyA().getUserData().getClass() == Player.class){
						Player p = (Player) events[i].getBodyA().getUserData();
						p.setFinishedStage(true);
						this.state = STATE_TRIGGERED;
						this.world.remove(this.body);
					}
				}
			}else if (events[i].getBodyA() == this.body){
				if (!(events[i].getBodyB().isStatic())){
					if (events[i].getBodyB().getUserData().getClass() == Player.class){
						Player p = (Player) events[i].getBodyB().getUserData();
						this.state = STATE_TRIGGERED;
						p.setFinishedStage(true);
						this.world.remove(this.body);
					}
				}
			}
		}
		return false;
	}
}
