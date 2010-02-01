package ca.keefer.sanemethod.Entity;

import net.phys2d.math.Vector2f;
import net.phys2d.raw.Body;
import net.phys2d.raw.CollisionEvent;
import net.phys2d.raw.World;
import net.phys2d.raw.shapes.AABox;
import net.phys2d.raw.shapes.Box;
import net.phys2d.raw.shapes.Circle;
import net.phys2d.raw.shapes.Polygon;

import org.newdawn.slick.Animation;
import org.newdawn.slick.Graphics;
import org.newdawn.slick.Image;
import org.newdawn.slick.geom.Rectangle;
import org.newdawn.slick.util.Log;

import ca.keefer.sanemethod.Constants;
import ca.keefer.sanemethod.Tools.Utility;

/**
 * A special type of entity that implements platformer-style physical dynamics
 * @author Christopher Keefer
 * @version 1.1
 *
 */
public class Platformer extends AbstractEntity{

	/** The world in which this entity can be found */
	protected World world;
	/** Dimensions of the Platformer */
	private int width;
	protected int height;
	/** Image of the platformer */
	private Image image;
	/** Whether this entity is moving */
	private boolean moving;
	/** Whether this entity is jumping */
	private boolean jumping;
	/** Whether this entity is falling */
	private boolean falling;
	/** Which direction the entity is facing */
	private boolean direction;
	/** Whether the character is reversing direction */
	private boolean reversing;
	/** Local velocity vector */
	private Vector2f velocity;
	/** Local scalable acceleration */
	float accel;
	/** Whether this entity is currently in contact with a 'ground' */
	boolean onGround;
	/** shapeType controls which kind of shape we'll assign to this Entity for collision detection */
	int shapeType;
	/** zOrder controls the depth of this entity */
	private int zOrder;
	
	private boolean activeJump;
	private int jumpTimer=0;
	
	public static int SHAPE_TYPE_BOX=0;
	public static int SHAPE_TYPE_CIRCLE=1;
	public static int SHAPE_TYPE_POLYGON=2;
	
	public static boolean DIR_LEFT = false;
	public static boolean DIR_RIGHT = true;
	
	public static int MAX_JUMP = 500;
	public static int REV_VELOCITY = 20;
	
	
	/**
	 * Create a platformer with a single static image representation
	 * Note that the dimensions must be provided as an array of Vector2f - this is to allow for
	 * the creation of complex polygons for the body's shape representation in the physical world;
	 * for a simple box or circle type, just the first element of the array shall be used.
	 * @param x Position on the horiztonal plane
	 * @param y Position on the vertical plane
	 * @param shapeType One of either SHAPE_TYPE_CIRCLE, SHAPE_TYPE_BOX or SHAPE_TYPE_POLYGON
	 * @param dimensions Vector2f array, containing the points of a complex shape
	 * @param mass Mass of the object - default is 1.0
	 * @param restitution The degree to which this body transfers momentum - default is 1.0f
	 * @param friction The friction on the surface of this body
	 * @param MaxVelocity Vector2f the maximum velocities this body can reach in the x and y ranges
	 * @param rotatable Whether this body is rotatable - default false
	 * @param zOrder Controls the sorting of entities - not necessary if each entity is added to a seperate layer
	 * @param image Static image for this Platformer
	 */
	public Platformer(float x, float y,int shapeType, Vector2f[] dimensions, float mass, float restitution, 
			float friction, Vector2f maxVelocity, boolean rotatable, int zOrder, Image image){
		
		this.shapeType=shapeType;
		if (shapeType == SHAPE_TYPE_CIRCLE){
			this.body = new Body(new Circle(dimensions[0].x/2),mass);
		}else if (shapeType  == SHAPE_TYPE_BOX){
			this.body = new Body(new Box(dimensions[0].x,dimensions[0].y),mass);
		}else if (shapeType == SHAPE_TYPE_POLYGON){
			this.body = new Body(new Polygon(dimensions),mass);
		}
		body.setUserData(this);
		body.setRestitution(restitution);
		body.setFriction(friction);
		body.setMaxVelocity(maxVelocity.x,maxVelocity.y);
		body.setRotatable(rotatable);
		this.image = image;
		setPosition(x,y);
		
		this.zOrder = zOrder;
		velocity = new Vector2f(0,0);
		accel = Constants.ACCELERATION;
		onGround=false;
		jumping=false;
		falling=false;
		moving=false;
		direction = DIR_LEFT;
	}
	
	/**
	 * Create a platformer with no graphical representation - this is so a further class can
	 * extend the platformer class and replace the image with a series of animations
	 * Note that the dimensions must be provided as an array of Vector2f - this is to allow for
	 * the creation of complex polygons for the body's shape representation in the physical world;
	 * for a simple box or circle type, just the first element of the array shall be used.
	 * @param x Position on the horiztonal plane
	 * @param y Position on the vertical plane
	 * @param shapeType One of either SHAPE_TYPE_CIRCLE, SHAPE_TYPE_BOX or SHAPE_TYPE_POLYGON
	 * @param dimensions Vector2f array, containing the points of a complex shape
	 * @param mass Mass of the object - default is 1.0
	 * @param restitution The degree to which this body transfers momentum - default is 1.0f
	 * @param friction The friction on the surface of this body
	 * @param MaxVelocity Vector2f the maximum velocities this body can reach in the x and y ranges
	 * @param rotatable Whether this body is rotatable - default false
	 * @param zOrder Controls the sorting of entities - not necessary if each entity is added to a seperate layer
	 */
	public Platformer(float x, float y,int shapeType, Vector2f[] dimensions, float mass, float restitution, 
			float friction, Vector2f maxVelocity, boolean rotatable, int zOrder){
		
		this.shapeType=shapeType;
		if (shapeType == SHAPE_TYPE_CIRCLE){
			this.body = new Body(new Circle(dimensions[0].x/2),mass);
		}else if (shapeType  == SHAPE_TYPE_BOX){
			this.body = new Body(new Box(dimensions[0].x,dimensions[0].y),mass);
		}else if (shapeType == SHAPE_TYPE_POLYGON){
			this.body = new Body(new Polygon(dimensions),mass);
		}
		body.setUserData(this);
		body.setRestitution(restitution);
		body.setFriction(friction);
		body.setMaxVelocity(maxVelocity.x,maxVelocity.y);
		body.setRotatable(rotatable);
		setPosition(x,y);
		
		this.zOrder = zOrder;
		velocity = new Vector2f(0,0);
		accel = Constants.ACCELERATION;
		onGround=false;
		jumping=false;
		falling=false;
		moving=false;
		direction = DIR_LEFT;
	}
	
	public void setMoving(boolean moving){
		this.moving=moving;
	}
	public boolean isMoving(){
		return moving;
	}

	public void setDirection(boolean direction){
		this.direction=direction;
	}
	public boolean getDirection(){
		return direction;
	}
	
	public void setJumping(boolean jumping){
		this.jumping=jumping;
	}
	public boolean isJumping(){
		return jumping;
	}
	
	public void setFalling(boolean falling){
		this.falling=falling;
	}
	public boolean isFalling(){
		return falling;
	}
	
	public void setReversing(boolean reversing){
		this.reversing=reversing;
	}
	public boolean isReversing(){
		return reversing;
	}
	
	public void setOnGround(boolean onGround){
		this.onGround=onGround;
	}
	public boolean isOnGround(){
		return onGround;
	}
	
	public void setGravityEffected(boolean g){
		body.setGravityEffected(g);
	}
	
	public void setDimensions(int width, int height){
		this.width=width;
		this.height=height;
	}
	public int getWidth(){
		return width;
	}
	public int getHeight(){
		return height;
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
		return this.world;
	}

	@Override
	public void preUpdate(int delta) {
		// If the character is not currently moving, then decrease the x velocity
		// according to the Friction constant times their current velocity, eventually
		// clamping the value to 0 when it reaches a certain low point.
		if (isMoving() == false){
			accel = Constants.ACCELERATION;
			if (velocity.getX() > 0.001){
				velocity.x -= Constants.FRICTION * velocity.getX() * 2;
				if (isReversing()){
					// add additional slow down from dug in heels effect
					//velocity.x -= (Constants.FRICTION * velocity.getX());
				}
				if (((velocity.getX() < 4 && getDirection() == DIR_LEFT)
						|| (velocity.getX() < 4 && getDirection() == DIR_RIGHT))
						&& isReversing()){
					setReversing(false);
				}
			}else{
				velocity.x=0;
			}
			float velX;
			
			// Invert velocity.x based on direction
			if (direction == DIR_LEFT){
				velX = -velocity.x;
			}else{
				velX = velocity.x;
			}
			setVelocity(velX,getVelocityY());
		}
		// Check to see if we're falling by determing whether the y velocity is greater
		// than 0, and the onGround flag is not set
		if (getVelocityY() >= 0 && onGround == false){
			jumping = false;
			falling = true;
		}
		
		if (falling == true){
			onGround = onGroundImpl(body);
			if (onGround){
				falling = false;
			}
		}
		// FIXME: Kill the restitution velocity on the y if below a certain threshold
		/*
		if (this.body.getRestitution() > 0){
			if (getVelocityY() < 0.00001){
				setVelocity(getVelocityX(),0);
			}
		
		}
		*/
		
	}
	
	@Override
	public void update(int delta) {
		// update whether the body is currently on the ground
		//boolean on = onGroundImpl(body);
		// if we're standing on the ground negate gravity. This stops
		// some instability in physics 
		//body.setGravityEffected(!on);
		
		// Update speed throughout physics/frame updates
		
		// For jumping
		if (activeJump){
			jumpTimer += delta/2;
			if (jumpTimer < MAX_JUMP){
				this.setVelocityY(-jumpTimer);
			}else{
				activeJump = false;
				jumpTimer = 0;
			}
		}else {
			jumpTimer = 0;
		}
		
		// For moving left/right
		if (moving){
			//Modeling velocity as an implementation of Stokes' Drag (v = (a/f) - (a/f)*exp(-f*t))
			velocity.x = (float) ((accel/Constants.FRICTION) - (accel/Constants.FRICTION)*Math.exp(-Constants.FRICTION*delta));

			// scale velocity
			if (accel < Constants.MAX_ACCELERATION){
				accel += 0.01;
			}
		}
		
		float velX;
		
		// Invert velocity.x based on direction
		if (direction == DIR_LEFT){
			velX = -velocity.x;
		}else{
			velX = velocity.x;
		}
		setVelocity(velX,getVelocityY());
		
	}

	@Override
	public void render(Graphics g) {
		if (image !=null){
			if (shapeType==SHAPE_TYPE_CIRCLE){
				if (body.isRotatable()){
					g.rotate(body.getPosition().getX(),body.getPosition().getY(),(float) Math.toDegrees(body.getRotation()));
					
					g.drawImage(image,body.getPosition().getX()-body.getShape().getBounds().getWidth()/2
							,body.getPosition().getY()-body.getShape().getBounds().getHeight()/2);
					g.rotate(body.getPosition().getX(),body.getPosition().getY(),(float) -Math.toDegrees(body.getRotation()));
				}else{
					g.drawImage(image,(body.getPosition().getX()-body.getShape().getBounds().getWidth()/2)
							,(body.getPosition().getY()-body.getShape().getBounds().getHeight()/2));
				}
			}else if (shapeType==SHAPE_TYPE_BOX || shapeType==SHAPE_TYPE_POLYGON){
				g.drawImage(image,body.getPosition().getX(),body.getPosition().getY());
			}
		}
		
	}

	@Override
	public void setWorld(World world) {
		this.world=world;
		
	}
	
	/**
	 * This method takes a keypress event from the state and interprets it into action
	 * for this Entity.
	 * @param keyPressed
	 */
	public void receiveKeyPress(int keyPressed){
		if (keyPressed == Constants.KEY_LEFT && !isReversing()){
				this.setDirection(DIR_LEFT);
				this.setMoving(true);
		}else if (keyPressed == Constants.KEY_RIGHT && !isReversing()){
				this.setDirection(DIR_RIGHT);
				this.setMoving(true);
		}else if (keyPressed == Constants.KEY_JUMP){
			this.setJumping(true);
			this.onGround = false;
			//this.setVelocityY(-100);
			activeJump = true;
		}
	}
	
	/**
	 * This method takes a keyRelease event from the state and interprets it into action
	 * or cessation thereof for this Entity.
	 * @param keyReleased
	 */
	public void receiveKeyRelease(int keyReleased){
		if (keyReleased == Constants.KEY_LEFT || keyReleased == Constants.KEY_RIGHT){
			this.setMoving(false);
			if ((this.getVelocityX() > REV_VELOCITY && getDirection()==DIR_RIGHT) && onGround ||
					(-this.getVelocityX() > REV_VELOCITY && getDirection()==DIR_LEFT) && onGround){
				this.setReversing(true);
			}
		}
		if (keyReleased == Constants.KEY_JUMP){
			activeJump = false;
		}
	}
	
	/**
	 * Implementation on ground check. This can be expensive so best
	 * to try and limit its use by caching
	 * FIXED[more or less]: We check this only when we might suspect the body is not on the ground
	 * (ie. when falling) - this helps make the expense of this function less onerous
	 * @return True if the body is resting on the ground
	 */
	protected boolean onGroundImpl(Body body) {
		if (world == null) {
			return false;
		}
		
		// loop through the collision events that have occured in the
		// world
		CollisionEvent[] events = world.getContacts(body);
		
		for (int i=0;i<events.length;i++) {
			//Log.debug("CollisionEvent:"+events[i].getPenetrationDepth());
			
			// if the point of collision was below the centre of the actor
			// i.e. near the feet
			if (events[i].getPoint().getY() > getY()+(height/4)) {
				// check the normal to work out which body we care about
				// if the right body is involved and a collision has happened
				// below it then we're on the ground
				if (events[i].getNormal().getY() < -0.5) {
					if (events[i].getBodyB() == body) {
						//System.out.println(events[i].getPoint()+","+events[i].getNormal());
						return true;
					}
				}
				if (events[i].getNormal().getY() > 0.5) {
					if (events[i].getBodyA() == body) {
						//System.out.println(events[i].getPoint()+","+events[i].getNormal());
						return true;
					}
				}
			}
		}
		
		return false;
	}

	@Override
	public int getZOrder() {
		return zOrder;
	}

	@Override
	public void setZOrder(int z) {
		zOrder=z;
	}
	
}
