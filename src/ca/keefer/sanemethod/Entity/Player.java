package ca.keefer.sanemethod.Entity;

import java.util.Hashtable;

import net.phys2d.math.Vector2f;
import net.phys2d.raw.CollisionEvent;
import net.phys2d.raw.FixedJoint;

import org.newdawn.slick.Animation;
import org.newdawn.slick.Graphics;
import org.newdawn.slick.SlickException;
import org.newdawn.slick.SpriteSheet;
import org.newdawn.slick.util.Log;

import ca.keefer.sanemethod.Constants;

/**
 * This class extends Platformer to allow for an animated character, with specific 
 * actions/animations mapped to user input, with Platformer dynamics and physical
 * body representation
 * @author Christopher Keefer
 * @version 1.5
 */
public class Player extends Platformer{

	/** Contains the sprites for this Player */
	SpriteSheet spriteSheet;
	/** Hashtable containing this Player's animations, keyed to strings which name the animation */
	Hashtable<String,Animation> animTable;
	/** The animation currently selected for drawing */
	String currentAnim;
	/** Idle timer - times how long its been since last input */
	int idleTimer=0;
	/** Are we trying to grab something */
	boolean grabbing=false;
	/** Are we kicking at something */
	boolean kicking;
	/** FixedJoint for establishing a grabbed relationship */
	FixedJoint grabJoint;
	/** The last key pressed */
	int lastKey;
	/** yOffset for image display */
	int yOffset;
	/** how many coins the player has */
	int coins;
	/** whether we're currently hurting */
	boolean hurt;
	/** Whether we need the current environment to reset */
	boolean requestReset=false;
	/** Whether the player is currently invulnerable (usually due to being hit recently) */
	boolean mercy = false;
	/** How long mercy has lasted for */
	int mercyTimer=0;
	/** how long we've been dying for */
	int dyingTimer=0;
	/** whether we're currently dying */
	boolean dying;
	/** whether to lock out control of this entity or not */
	boolean lockOut=false;
	/** how long we've been jumping while holding something - if too long, remove fixedJoint */
	int jumpGripTimer=0;
	/** how long we've been falling for */
	int fallTimer;
	/** how long we can fall before we take damage (loss of 1 coin) when we land */
	private final int FALL_PERIOD = 6000;
	/** waypoint position */
	float wayX, wayY;
	/** whether we've hit any waypoints */
	boolean hitWaypoint;
	/** whether we've reached the end of the stage */
	boolean finishedStage;
	
	/** How long mercy time should last for */
	private final int MERCY_PERIOD = 5000;
	
	boolean sliding=false;
	
	/** Constructs a Platformer based on the passed spriteSheet and pre-set expectations about
	 * where specific frames of animation will be found, and what they should be named
	 * @param x
	 * @param y
	 * @param shapeType
	 * @param dimensions
	 * @param mass
	 * @param restitution
	 * @param friction
	 * @param maxVelocity
	 * @param rotatable
	 * @param zOrder
	 * @param spriteSheet
	 */
	public Player(float x, float y, int shapeType, Vector2f dimensions,
			float mass, float restitution, float friction,
			Vector2f maxVelocity, boolean rotatable, int zOrder, SpriteSheet spriteSheet) {
		super(x, y, shapeType, dimensions, mass, restitution, friction, maxVelocity,
				rotatable, zOrder);
		this.spriteSheet = spriteSheet;
		yOffset = 58;
		animTable = new Hashtable<String,Animation>();
		currentAnim = "Run";
		coins=0;
		buildExpectedPlayerTable();
	}
	
	/**
	 * Builds a Platformer with the passed spriteSheet, and the animTable passed; animTable must
	 * contain, at minimum: Run, Stand, Hurt, StrongStrike, Crawl, Leap, InAir, Falling, Landed, 
	 * WeakStrike, Climb, Fallen, Slide, Idle, Kick, Stopping
	 * @param x
	 * @param y
	 * @param shapeType
	 * @param dimensions
	 * @param mass
	 * @param restitution
	 * @param friction
	 * @param maxVelocity
	 * @param rotatable
	 * @param zOrder
	 * @param spriteSheet
	 * @param animTable
	 */
	public Player(float x, float y, int shapeType, Vector2f dimensions,
			float mass, float restitution, float friction,
			Vector2f maxVelocity, boolean rotatable, int zOrder, SpriteSheet spriteSheet,
			Hashtable<String,Animation> animTable) {
		super(x, y, shapeType, dimensions, mass, restitution, friction, maxVelocity,
				rotatable, zOrder);
		this.spriteSheet = spriteSheet;
		currentAnim = "Run";
		coins=0;
		this.animTable = animTable;
	}
	
	/**
	 * Set the spriteSheet of this Player to another image
	 * @param spriteSheet String Location of the image file
	 * @param tw Width of the sprites on the sheet
	 * @param th Height of the sprites on the sheet
	 */
	public void setSpriteSheet(String spriteSheet, int tw, int th){
		try{
			this.spriteSheet = new SpriteSheet(spriteSheet, tw, th);
		}catch (SlickException e){
			Log.error("Error Loading SpriteSheet:"+e.getMessage());
		}
	}
	
	/** Build the animTable based on the standard sprite sheet, Player.png */
	private void buildExpectedPlayerTable(){
		Animation thisAnim = new Animation(spriteSheet, 0, 0, 0, 9, false, 50, true);
		animTable.put("Run", thisAnim);
		thisAnim = new Animation(spriteSheet, 1,0,1,3,false,300,true);
		animTable.put("Idle", thisAnim);
		thisAnim = new Animation(spriteSheet, 1, 4, 1, 8, false, 150, true);
		thisAnim.addFrame(spriteSheet.getSubImage(1,9),500);
		thisAnim.setLooping(false);
		animTable.put("Hurt",thisAnim);
		thisAnim = new Animation(spriteSheet, 2,0,2,7,false,150,true);
		animTable.put("StrongStrike", thisAnim);
		thisAnim = new Animation(spriteSheet,3,2,3,3,false,100,true);
		animTable.put("Leap",thisAnim);
		thisAnim = new Animation(spriteSheet,3,4,3,4,false,150,true);
		animTable.put("InAir",thisAnim);
		thisAnim.setLooping(false);
		thisAnim = new Animation(spriteSheet,3,5,3,6,false,150,true);
		animTable.put("Falling",thisAnim);
		thisAnim = new Animation(spriteSheet,1,8,1,9,false,150,true);
		thisAnim.setLooping(false);
		animTable.put("Landed",thisAnim);
		thisAnim = new Animation(spriteSheet, 2, 8, 2, 9, false, 150, true);
		thisAnim.addFrame(spriteSheet.getSubImage(3, 8), 150);
		thisAnim.addFrame(spriteSheet.getSubImage(3, 9), 150);
		animTable.put("Crawl", thisAnim);
		thisAnim = new Animation(spriteSheet, 5, 0, 5, 3, false, 150, true);
		animTable.put("Climb", thisAnim);
		thisAnim = new Animation(spriteSheet, 1, 9, 1, 9, false, 500, true);
		thisAnim.addFrame(spriteSheet.getSubImage(5,4),500);
		thisAnim.setLooping(false);
		animTable.put("Dying", thisAnim);
		thisAnim = new Animation(spriteSheet, 5, 5, 5, 6, false, 200, true);
		animTable.put("Slide", thisAnim);
		thisAnim = new Animation(spriteSheet, 5,7,5,9,false,250,true);
		animTable.put("Stand", thisAnim);
		thisAnim = new Animation();
		thisAnim.addFrame(spriteSheet.getSubImage(1,0),150);
		thisAnim.addFrame(spriteSheet.getSubImage(2,7),150);
		thisAnim.addFrame(spriteSheet.getSubImage(6,2), 150);
		thisAnim.addFrame(spriteSheet.getSubImage(6,1), 150);
		thisAnim.addFrame(spriteSheet.getSubImage(6,0), 150);
		thisAnim.addFrame(spriteSheet.getSubImage(6,3), 250);
		animTable.put("Kick",thisAnim);
		thisAnim = new Animation(spriteSheet, 1,6,1,7,false,200,true);
		animTable.put("Stopping", thisAnim);
	}
	
	/**
	 * Set The animation to draw from the Hashtable
	 * @param animToDraw
	 */
	public void setAnimToDraw(String animToDraw){
		currentAnim = animToDraw;
	}
	
	/**
	 * Get the last key pressed
	 * @return an integer representing the last key pressed by the player
	 */
	public int getLastKey(){
		return lastKey;
	}
	
	/** Toggle the world's gravity, at the cost of one coin */
	public void toggleGravity(){
		if (coins > 0){
			coins -= 1;
			if (Constants.GRAVITY.getY() > 0){
				Constants.GRAVITY = new net.phys2d.math.Vector2f(0,-15f);
				this.getWorld().setGravity(Constants.GRAVITY.getX(), Constants.GRAVITY.getY());
			}else{
				Constants.GRAVITY = new net.phys2d.math.Vector2f(0,15f);
				this.getWorld().setGravity(Constants.GRAVITY.getX(), Constants.GRAVITY.getY());
			}
		}
	}
	
	
	@SuppressWarnings("deprecation")
	@Override
	/** Draw a specific animation at this player's current position */
	public void render (Graphics g){
		boolean yInverse;
		if (Constants.GRAVITY.getY() > 0){
			yInverse = false;
		}else{
			yInverse = true;
		}
		
		if (this.getDirection() == DIR_LEFT){
			animTable.get(currentAnim).updateNoDraw();
			if (yInverse){
					g.drawImage(animTable.get(currentAnim).getCurrentFrame().getFlippedCopy(true, true),
						super.getX()-50, super.getY()-yOffset+16);
			}else{
					g.drawImage(animTable.get(currentAnim).getCurrentFrame().getFlippedCopy(true, false),
						super.getX()-50, super.getY()-yOffset);
			}
		}else{
			if (yInverse){
				animTable.get(currentAnim).updateNoDraw();
					g.drawImage(animTable.get(currentAnim).getCurrentFrame().getFlippedCopy(false, true),
						super.getX()-50, super.getY()-yOffset+16);
			}else{
					g.drawAnimation(animTable.get(currentAnim), super.getX()-50, super.getY()-yOffset);
			}
		}
	}
	
	@Override 
	public void preUpdate (int delta){
		super.preUpdate(delta);
		if (grabJoint != null){
			grabJoint.preStep(delta);
		}
	}
	
	@Override
	public void update (int delta){
		super.update(delta);
		// check for invulnerability due to collision, and whether it should be turned off
		mercyTimer += delta;
		if (mercyTimer > MERCY_PERIOD){
			mercyTimer = 0;
			mercy = false;
		}
		if (this.isFalling()){
			fallTimer += delta;
		}else{
			if (fallTimer > FALL_PERIOD){
				hurtPlayer();
			}
			fallTimer = 0;
		}
		if (grabbing){
			if (grabJoint == null){
				pickUpOnCollide();
			}
			else if (grabJoint != null){
				grabJoint.applyImpulse();
			}
		}
		if (idleTimer < 20000){
		idleTimer += delta;
		}
		if (idleTimer >= 20000 && !this.isMoving()){
			setAnimToDraw("Idle");
		}else if (isHurt()){
			setAnimToDraw("Hurt");
		}else if (isDying()){
			setAnimToDraw("Dying");
		}else if (isReversing() && !sliding){
			setAnimToDraw("Stopping");
		}else if (this.isMoving() && onGround){
			setAnimToDraw("Run");
		}else if (this.isJumping()){
			setAnimToDraw("Leap");
		}else if (this.isFalling()){
			setAnimToDraw("Falling");
		}else if (this.isSliding()){
			setAnimToDraw("Slide");
		}
		
		else{
			setAnimToDraw("Stand");
		}
		
		// check to see if certain animations have reached a point where they should be swapped out
		if (currentAnim.equalsIgnoreCase("Slide")){
			if (!(isMoving() || isReversing()) && sliding){
				sliding = false;
			}
		}
		if (currentAnim.equals("Hurt")){
			if (animTable.get(currentAnim).isStopped()){
				hurt = false;
				animTable.get(currentAnim).restart();
				currentAnim = "Stand";
			}
		}
		if (currentAnim.equals("Dying")){
			// if we're dying, and the animation is finished, give the player a second to realize they're dead,
			// then request a reset
			if(animTable.get(currentAnim).isStopped()){
				dyingTimer += delta;
				if (dyingTimer > 1000){
					dyingTimer = 0;
					animTable.get(currentAnim).restart();
					currentAnim="Stand";
					lockOut=false;
					this.requestReset = true;
				}
			}
		}
		
	}
	
	@Override
	public void receiveKeyPress(int keyPressed){
		if (!lockOut){
			lastKey = keyPressed;
			// reset the idle timer
			idleTimer = 0;
			if (keyPressed == Constants.KEY_DOWN && onGround && isMoving()){
				sliding = true;
				setMoving(false);
				setReversing(true);
			}
			if (keyPressed == Constants.KEY_PICK_UP){
				grabbing = true;
			}
			if (keyPressed == Constants.KEY_TOGGLE_GRAVITY){
				toggleGravity();
			}
			super.receiveKeyPress(keyPressed);
		}
	}
	@Override
	public void receiveKeyRelease(int keyReleased){
		if (!lockOut){
			if (keyReleased == Constants.KEY_PICK_UP){
				grabbing = false;
				if (grabJoint != null){
					world.remove(grabJoint);
					grabJoint = null;
					//Log.debug("Pick Up Released.");
				}
			}
			super.receiveKeyRelease(keyReleased);
		}
	}
	
	public boolean isSliding(){
		return sliding;
	}
	
	public void setSliding(boolean b){
		sliding=b;
	}
	
	// Coin getter/setters
	public void addCoins(int amount){
		coins += amount;
	}
	public void removeCoins(int amount){
		coins -= amount;
	}
	public void setCoins(int amount){
		coins = amount;
	}
	public int getCoins(){
		return coins;
	}
	/**
	 * Get whether we're hurting
	 * @return
	 */
	public boolean isHurt(){
		return hurt;
	}
	/**
	 * Set the value of hurt, without subtracting any life
	 * Use this to display the hurt animation without changing any other variables
	 * @param b
	 */
	public void setHurt(boolean b){
		hurt=b;
	}
	/**
	 * Hurt the player (usually by collision with an enemy, or by falling too far)
	 */
	public void hurtPlayer(){
		if (!mercy && !dying){
			coins -= 1;
			// Check to see if the player is now 'dead' - at -1 coins. If so, set this player to dying, and lock
			// out control of this character
			if (coins <= -1){
				lockOut=true;
				setDying(true);
			}else{
				mercy = true;
				hurt = true;
			}
		}
	}
	
	/** Toggle Lock out control of this player */
	public void toggleLockOut(){
		lockOut = !lockOut;
	}
	/** set lock out control of this player */
	public void setLockOut(boolean b){
		lockOut=b;
	}
	/** get whether this player has control locked out */
	public boolean isLockedOut(){
		return lockOut;
	}
	
	/** Get whether the player is requesting reset of the scenario - usually due to dying */
	public boolean resetRequested(){
		return requestReset;
	}
	public void setReset(boolean b){
		requestReset = b;
	}
	
	/** whether this player is currently dying */
	public boolean isDying(){
		return dying;
	}
	/** set whether this player is currently dying */
	public void setDying(boolean b){
		dying=b;
	}
	
	/** get whether we've hit any waypoints */
	public boolean getHitWaypoint(){
		return hitWaypoint;
	}
	public float getWayX(){
		return wayX;
	}
	public float getWayY(){
		return wayY;
	}
	/** set a waypoint for this player */
	public void setWaypoint(float x, float y){
		this.hitWaypoint=true;
		this.wayX=x;
		this.wayY=y;
	}
	/** remove reference to waypoints */
	public void clearWaypointFlag(){
		hitWaypoint=false;
	}
	/** set whether we've finished the stage */
	public void setFinishedStage(boolean b){
		finishedStage = b;
	}
	public boolean isFinishedStage(){
		return finishedStage;
	}
	
	public void pickUpOnCollide(){
		if (world == null) {
			return;
		}
		// Get all the collision events involving this player
		CollisionEvent[] events = world.getContacts(body);
		for (int i=0;i<events.length;i++){
			// make sure this collision occurred by running into this object
			// and that its mass is not too great for our player to lift
			if (events[i].getBodyA()==body){
				if (events[i].getBodyB().getMass() < body.getMass()*4 && events[i].getBodyB().isMoveable()){
					if (grabJoint == null){
						grabJoint = new FixedJoint(events[i].getBodyB(),body);
						//events[i].getBodyB().setPosition(body.getPosition().getX(), body.getPosition().getY());
						grabJoint.setRelaxation(0.3f);
						world.add(grabJoint);
					}
				}
			}else if (events[i].getBodyB()==body){
				if (events[i].getBodyA().getMass() < body.getMass()*4 && events[i].getBodyA().isMoveable()){
					if (grabJoint == null){
						grabJoint = new FixedJoint(events[i].getBodyA(),body);
						grabJoint.setRelaxation(0.3f);
						world.add(grabJoint);
					}
				}
			}
		}
		
	}
	
}
