package ca.keefer.sanemethod.Entity;

import net.phys2d.math.Vector2f;
import net.phys2d.raw.Body;
import net.phys2d.raw.CollisionEvent;
import net.phys2d.raw.World;
import net.phys2d.raw.shapes.Polygon;

import org.newdawn.slick.Animation;
import org.newdawn.slick.Graphics;
import org.newdawn.slick.SpriteSheet;
import org.newdawn.slick.geom.Path;
import org.newdawn.slick.geom.Rectangle;
import org.newdawn.slick.util.Log;

public class ClamMook extends AbstractEntity{

	/** The world in which this entity can be found */
	protected World world;
	/** Dimensions of this entity */
	int height, width;
	/** Animations for this entity */
	Animation idleAnim, attackAnim;
	/** The state of this entity */
	short state;
	private final short STATE_IDLE = 0;
	private final short STATE_ATTACK = 1;
	/** zOrder controls the depth of this entity */
	private int zOrder;
	/** How long its been since we last updated this entities status */
	int checkTimer;
	/** How long we've been attacking for, and how long we should */
	int attackTimer;
	private final int ATTACK_PERIOD = 3000;
	/** How long between updating whether or not this mook has collided with the player */
	private final int CHECK_PERIOD = 200;
	/** Direction of this mook */
	boolean direction;
	private final boolean DIR_LEFT = false;
	float x,y;
	
	public ClamMook (float x, float y, int zOrder, boolean active, SpriteSheet spriteSheet){
		this.zOrder=zOrder;
		this.active=active;
		this.x=x;
		this.y=y;
		this.height=32;
		this.width=64;
		// Dimensions of this mook
		Path clamPath = new Path(x,y);
		clamPath.lineTo(x+(width), y);
		clamPath.lineTo(x+(width), y+(height));
		clamPath.lineTo(x,y+(height));
		clamPath.lineTo(x, y);
		clamPath.close();
		
		float[] pts = clamPath.getPoints();
		Vector2f[] vecs = new Vector2f[(pts.length / 2)];
		for (int j=0;j<vecs.length;j++) {
			vecs[j] = new Vector2f(pts[j*2],pts[(j*2)+1]);
		}
		this.body = new Body(new Polygon(vecs),100);
		
		body.setUserData(this);
		body.setRestitution(1f);
		body.setFriction(0f);
		body.setMoveable(false);
		body.setRotatable(false);
		
		// set up animations
		if (spriteSheet != null){
			idleAnim = new Animation(spriteSheet,0,0,0,0,false,250,true);
			attackAnim = new Animation(spriteSheet,0,0,0,2,false,100,true);
			attackAnim.addFrame(spriteSheet.getSubImage(1,0),100);
			attackAnim.setPingPong(true);
		}
		state = STATE_IDLE;
		direction = DIR_LEFT;
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
		// TODO Auto-generated method stub
		
	}

	@Override
	public void update(int delta) {
		checkTimer += delta;
		if (checkTimer > CHECK_PERIOD){
			considerCollision();
			checkTimer = 0;
		}
		if (state == STATE_ATTACK){
			attackTimer += delta;
			if (attackTimer > ATTACK_PERIOD){
				attackTimer = 0;
				state = STATE_IDLE;
			}
		}
	}

	@Override
	public void render(Graphics g) {
		switch(state){
		case STATE_IDLE:
			
			if (this.direction == DIR_LEFT){
				idleAnim.draw(x-width/2, y-height*3);
			}else{
				idleAnim.updateNoDraw();
				idleAnim.getCurrentFrame().getFlippedCopy(true, false).draw(x-width/2, y-height*3);
			}
			break;
		case STATE_ATTACK:
			if (this.direction == DIR_LEFT){
				attackAnim.draw(x-width/2, y-height*3);
			}else{
				attackAnim.updateNoDraw();
				attackAnim.getCurrentFrame().getFlippedCopy(true, false).draw(x-width/2, y-height*3);
			}
			break;
		}
		
	}
	
	protected void considerCollision(){
		if (world == null) {
			Log.error("no world found in clamMook");
			return;
		}
		// Get all the collision events involving this object
		CollisionEvent[] events = world.getContacts(this.body);
		
		for (int i=0;i<events.length;i++){
			if (events[i].getBodyB() == this.body && !events[i].getBodyA().isStatic()){
				if ((events[i].getBodyA().getUserData().getClass() == Player.class)){
					Player pHurt = (Player) events[i].getBodyA().getUserData();
					this.direction = !pHurt.getDirection();
					this.state=STATE_ATTACK;
					pHurt.hurtPlayer();
				}else if (events[i].getBodyA().getUserData().getClass() == JumpingMook.class){
					JumpingMook jMook = (JumpingMook) events[i].getBodyA().getUserData();
					this.direction = !jMook.direction;
					this.state = STATE_ATTACK;
					jMook.setState(jMook.STATE_DYING);
				}
			}else if (events[i].getBodyA() == this.body && !events[i].getBodyB().isStatic()){
				if ((events[i].getBodyA().getUserData().getClass() == Player.class)){
					Player pHurt = (Player) events[i].getBodyA().getUserData();
					this.direction = !pHurt.getDirection();
					this.state=STATE_ATTACK;
					pHurt.hurtPlayer();
				}else if (events[i].getBodyA().getUserData().getClass() == JumpingMook.class){
					JumpingMook jMook = (JumpingMook) events[i].getBodyA().getUserData();
					this.direction = !jMook.direction;
					this.state = STATE_ATTACK;
					jMook.setState(jMook.STATE_DYING);
				}
			}
		}
	}

}
