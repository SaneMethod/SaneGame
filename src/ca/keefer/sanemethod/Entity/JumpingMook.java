package ca.keefer.sanemethod.Entity;

import net.phys2d.math.Vector2f;
import net.phys2d.raw.Body;
import net.phys2d.raw.CollisionEvent;
import net.phys2d.raw.World;
import net.phys2d.raw.shapes.Circle;

import org.newdawn.slick.Animation;
import org.newdawn.slick.Graphics;
import org.newdawn.slick.SlickException;
import org.newdawn.slick.SpriteSheet;
import org.newdawn.slick.geom.Rectangle;
import org.newdawn.slick.util.Log;

import ca.keefer.sanemethod.Constants;

/**
 * Extends AbstractEntity. Contains animation and body for an enemy character that will jump around.
 * @author Christopher Keefer
 *
 */
public class JumpingMook extends AbstractEntity{
	
	/** The world in which this entity can be found */
	protected World world;
	/** Dimensions of this entity */
	int diameter;
	/** zOrder controls the depth of this entity */
	private int zOrder;
	/** Animation sequences for this enemy */
	Animation animBlink, animJump, animFall, animDie;
	/** How long this mook remains in the air before falling */
	int jumpTimer;
	/** How long we've been idle */
	int idleTimer;
	/** How long we've been falling */
	int fallingTimer;
	/** whether this mook is currently idle, jumping, dying, or dead */
	short state;
	/** direction of this mook */
	boolean direction;
	/** How long its been since we last updated this entities status */
	int checkTimer;
	/** Limits on this entities movement on the x axis, in tiles */
	int xLower; int xUpper;

	/** How long we should be idle */
	private final int IDLE_PERIOD = 1000;
	/** How long between updating whether or not this mook has collided with the player or been stomped on */
	private final int CHECK_PERIOD = 200;
	// Entity States
	private final short STATE_IDLE = 0;
	private final short STATE_JUMPING = 1;
	private final short STATE_FALLING = 2;
	private final short STATE_RECOVER = 3;
	public final short STATE_DYING = 4;
	private final short STATE_DEAD = 5;
	// Directional constants
	private final int MAX_JUMP = 100;
	private final boolean DIR_LEFT = false;
	private final boolean DIR_RIGHT = true;
	// Collision constants
	private final short NONE = 0;
	private final short HEAD_STOMP = 1;
	private final short HURT_PLAYER = 2;
	
	/**
	 * Instantiate JumpingMook.
	 * @param x
	 * @param y
	 * @param diameter
	 * @param xLower Lower bound (in tiles) for this entity's movement
	 * @param xUpper Upper bound (in tiles) for this entity's movement
	 * @param zOrder
	 * @param active
	 * @param spriteSheet
	 */
	public JumpingMook(float x, float y, int diameter, int xLower, int xUpper, int zOrder, boolean active,
			SpriteSheet spriteSheet){
		this.active=true;
		this.xUpper=xUpper*Constants.TILE_WIDTH;
		this.xLower=xLower*Constants.TILE_WIDTH;
		this.body = new Body(new Circle(diameter/2),10);
		this.diameter = diameter;
		this.body.setFriction(0.9f);
		this.body.setMaxVelocity(10, 50);
		this.zOrder=zOrder;
		state=STATE_IDLE;
		jumpTimer = 0;
		idleTimer = 0;
		fallingTimer = 0;
		direction = DIR_LEFT;
		if (spriteSheet != null){
			animBlink = new Animation(spriteSheet, 1,0,1,2,false,200,true);
			animBlink.setPingPong(true);
			animJump = new Animation(spriteSheet,0,5,0,8,false,50,true);
			animJump.setLooping(false);
			animFall = new Animation(spriteSheet,0,8,0,8,false,150,true);
			animFall.addFrame(spriteSheet.getSubImage(0,7), 150);
			animFall.addFrame(spriteSheet.getSubImage(0,6), 150);
			animFall.addFrame(spriteSheet.getSubImage(0,5), 150);
			animFall.setLooping(false);
			animDie = new Animation(spriteSheet,1,6,1,7,false,250,true);
			animDie.addFrame(spriteSheet.getSubImage(1,3),250);
			animDie.addFrame(spriteSheet.getSubImage(1,4),300);
			animDie.addFrame(spriteSheet.getSubImage(1,5),300);
			animDie.addFrame(spriteSheet.getSubImage(1,5),300);
			animDie.setLooping(false);
		}
		this.body.setUserData(this);
		this.setPosition(x, y);
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
	
	public void setState(short state){
		this.state=state;
	}
	
	@Override
	public void preUpdate(int delta) {
		// if this mook is currently idle, check to see if thePlayer is within a certain
		// range of it - if so, try and jump towards the player

		switch(state){
		case STATE_IDLE:
			idleTimer += delta;
			this.setVelocity(0,this.getVelocityY());
			if (idleTimer > IDLE_PERIOD){
				idleTimer = 0;
				state = STATE_JUMPING;
			}
			break;
		case STATE_JUMPING:
			if (this.body.getPosition().getX() >= (xUpper)){
				//Log.debug("Left Side:"+this.body.getPosition().getX()+" xUpper:"+xUpper);
				direction = DIR_LEFT;
				
			}else if (this.body.getPosition().getX() <= (xLower)) {
				//Log.debug("Right Side:"+this.body.getPosition().getX()+" xLower:"+xLower);
				direction = DIR_RIGHT;
				
			}
			// Set directional movement
			boolean yInverse;
			if (Constants.GRAVITY.getY() > 0){
				yInverse = false;
			}else{
				yInverse = true;
			}
			if (direction == DIR_LEFT){
				if (jumpTimer < MAX_JUMP){
					jumpTimer += delta/2;
					if (!yInverse){
						this.setVelocityY(-jumpTimer);
					}else{
						this.setVelocityY(jumpTimer);
					}
					this.setVelocityX(-5);
				}else{
					state = STATE_FALLING;
					jumpTimer = 0;
				}
			}else{
				if (jumpTimer < MAX_JUMP){
					jumpTimer += delta/2;
					if (!yInverse){
						this.setVelocityY(-jumpTimer);
					}else{
						this.setVelocityY(jumpTimer);
					}
					this.setVelocityX(5);
				}else{
					state = STATE_FALLING;
					jumpTimer = 0;
				}
			}
			
			break;
		case STATE_FALLING:
			fallingTimer += delta;
			boolean onGround = onGroundImpl(this.body);
			if (onGround){
				state = STATE_RECOVER;
				if (fallingTimer > 3000){
					state = STATE_DYING;
				}
				fallingTimer = 0;
			}
			break;
		case STATE_DYING:
			if (this.body.added()){
				world.remove(body);
			}
			break;
		case STATE_DEAD:
			if (this.active){
				this.active=false;
			}
			break;
		}
	}


	@Override
	public void update(int delta) {
		checkTimer += delta;
		if (checkTimer > CHECK_PERIOD){
			short cc = considerCollision();
			if (cc == HEAD_STOMP){
				//Log.debug("HEAD_STOMP");
				this.state=STATE_DYING;
			}else if (cc == HURT_PLAYER){
				//Log.debug("HURT_PLAYER");
				direction = !direction;
			}
			checkTimer = 0;
		}
		
	}
	
	@SuppressWarnings("deprecation")
	@Override
	public void render(Graphics g) {
		// Draw a specific animation based on the state of this mook
		boolean yInverse;
		if (Constants.GRAVITY.getY() > 0){
			yInverse = false;
		}else{
			yInverse = true;
		}
		switch(state){
		case STATE_IDLE:
			if (!yInverse){
				animBlink.draw(this.body.getPosition().getX()-(diameter+8), this.body.getPosition().getY()-(diameter+18));
			}else{
				animBlink.updateNoDraw();
				animBlink.getCurrentFrame().getFlippedCopy(false, true).draw(this.body.getPosition().getX()-(diameter+8), this.body.getPosition().getY()-(diameter+18));
			}
			break;
		case STATE_JUMPING:
			if (!yInverse){
				animJump.draw(this.body.getPosition().getX()-(diameter+8), this.body.getPosition().getY()-(diameter-30));
			}else{
				animJump.updateNoDraw();
				animJump.getCurrentFrame().getFlippedCopy(false, true).draw(this.body.getPosition().getX()-(diameter+8), this.body.getPosition().getY()-(diameter-30));
			}
			break;
		case STATE_FALLING:
			if (!yInverse){
				animFall.draw(this.body.getPosition().getX()-(diameter+8), this.body.getPosition().getY()-(diameter+18));
			}else{
				animFall.updateNoDraw();
				animFall.getCurrentFrame().getFlippedCopy(false, true).draw(this.body.getPosition().getX()-(diameter+8), this.body.getPosition().getY()-(diameter+18));
			}
			break;
		case STATE_RECOVER:
			animJump.restart();
			animFall.restart();
			state = STATE_IDLE;
			break;
		case STATE_DYING:
			if (!yInverse){
				animDie.draw(this.body.getPosition().getX()-(diameter+8), this.body.getPosition().getY()-(diameter+18));
			}else{
				animDie.updateNoDraw();
				animDie.getCurrentFrame().getFlippedCopy(false, true).draw(this.body.getPosition().getX()-(diameter+8), this.body.getPosition().getY()-(diameter+18));
			}
			if (animDie.getFrame() == animDie.getFrameCount()-1){
				state = STATE_DEAD;
			}
			break;
		}
	}
	
	/**
	 * Implementation on ground check. This can be expensive so best to try and limit its use by caching
	 * FIXED[more or less]: We check this only when we might suspect the body is not on the ground
	 * (ie. when falling) - this helps make the expense of this function less onerous
	 * @return True if the body is resting on the ground
	 */
	protected boolean onGroundImpl(Body body) {
		if (world == null) {
			return false;
		}
		
		boolean yInverse;
		if (Constants.GRAVITY.getY() > 0){
			yInverse = false;
		}else{
			yInverse = true;
		}
		
		// loop through the collision events that have occured in the world
		CollisionEvent[] events = world.getContacts(body);
		
		for (int i=0;i<events.length;i++) {
			//Log.debug("CollisionEvent:"+events[i].getNormal());
			
			// if the point of collision was below the centre of the actor
			// i.e. near the feet
			if ((events[i].getPoint().getY() > getY()+(diameter/4) && !yInverse) ||
					(events[i].getPoint().getY() < getY()+(diameter/4) && yInverse)) {
				// check the normal to work out which body we care about
				// if the right body is involved and a collision has happened
				// below it then we're on the ground
				if ((events[i].getNormal().getY() < -0.5 && !yInverse) || 
						(events[i].getNormal().getY() > 0.5 && yInverse)) {
					if (events[i].getBodyB() == body) {
						//Log.debug(events[i].getPoint()+","+events[i].getNormal());
						return true;
					}
				}
				if ((events[i].getNormal().getY() > 0.5 & !yInverse) || 
						(events[i].getNormal().getY() < -0.5 && yInverse)) {
					if (events[i].getBodyA() == body) {
						//Log.debug(events[i].getPoint()+","+events[i].getNormal());
						return true;
					}
				}
			}
		}
		
		return false;
	}
	
	public short considerCollision(){
		if (world == null) {
			return NONE;
		}
		
		// loop through the collision events that have occured in the world
		CollisionEvent[] events = world.getContacts(body);
		
		for (int i=0;i<events.length;i++) {
			if ((!events[i].getBodyA().isStatic() && !events[i].getBodyB().isStatic()) && 
					(events[i].getBodyA().getUserData().getClass() == Player.class || 
					events[i].getBodyB().getUserData().getClass() == Player.class)){
				boolean yInverse;
				if (Constants.GRAVITY.getY() > 0){
					yInverse = false;
				}else{
					yInverse = true;
				}
				
				/*
				Log.debug("CollisionEvent Point:"+events[i].getPoint().getX());
				Log.debug("CollisionEvent Y:"+getX()+(diameter/4));
				Log.debug("CollisionEvent Normal:"+events[i].getNormal().getX());
				 */


				// if the point of collision was above the centre of this entity
				if ((events[i].getPoint().getY() < getY()+(diameter/4) && !yInverse) ||
						(events[i].getPoint().getY() > getY()+(diameter/4) && yInverse)) {
					// check the normal to work out which body we care about
					// if the right body is involved and a collision has happened
					// above it then we've landed on this things head
					if ((events[i].getNormal().getY() > 0.5 & !yInverse) || 
							(events[i].getNormal().getY() < -0.5 && yInverse)) {
						if (events[i].getBodyB() == body) {
							//Log.debug("TRUE A");
							return HEAD_STOMP;
						}
					}else if ((events[i].getNormal().getY() < -0.5 && !yInverse) || 
							(events[i].getNormal().getY() > 0.5 && yInverse)) {
						if (events[i].getBodyA() == body) {
							//Log.debug("TRUE B");
							return HEAD_STOMP;
						}
					}else{
						if (events[i].getBodyA().getUserData().getClass() == Player.class){
							Player hPlayer = (Player) events[i].getBodyA().getUserData();
							hPlayer.hurtPlayer();
						}else{
							Player hPlayer = (Player) events[i].getBodyB().getUserData();
							hPlayer.hurtPlayer();
						}
						return HURT_PLAYER;
					}
				}else{
					if (events[i].getBodyA().getUserData().getClass() == Player.class){
						Player hPlayer = (Player) events[i].getBodyA().getUserData();
						hPlayer.hurtPlayer();
					}else{
						Player hPlayer = (Player) events[i].getBodyB().getUserData();
						hPlayer.hurtPlayer();
					}
					return HURT_PLAYER;
				}
			}
		}
		return NONE;
	}

}
