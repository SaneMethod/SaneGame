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
 * This class extends Platformer to allow for a animated character, with specific 
 * actions/animations mapped to user input, with Platformer dynamics and physical
 * body representation
 * @author Christopher Keefer
 *
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
	/** FixedJoint for establishing a grabbed relationship */
	FixedJoint grabJoint;
	/** The last key pressed */
	int lastKey;
	/** yOffset for image display */
	int yOffset;
	
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
	public Player(float x, float y, int shapeType, Vector2f[] dimensions,
			float mass, float restitution, float friction,
			Vector2f maxVelocity, boolean rotatable, int zOrder, SpriteSheet spriteSheet) {
		super(x, y, shapeType, dimensions, mass, restitution, friction, maxVelocity,
				rotatable, zOrder);
		this.spriteSheet = spriteSheet;
		yOffset = 58;
		animTable = new Hashtable<String,Animation>();
		currentAnim = "Run";
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
	public Player(float x, float y, int shapeType, Vector2f[] dimensions,
			float mass, float restitution, float friction,
			Vector2f maxVelocity, boolean rotatable, int zOrder, SpriteSheet spriteSheet,
			Hashtable<String,Animation> animTable) {
		super(x, y, shapeType, dimensions, mass, restitution, friction, maxVelocity,
				rotatable, zOrder);
		this.spriteSheet = spriteSheet;
		currentAnim = "Run";
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
		thisAnim = new Animation(spriteSheet, 1,0,1,3,false,200,true);
		animTable.put("Stand", thisAnim);
		thisAnim = new Animation(spriteSheet, 1, 4, 1, 9, false, 100, true);
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
		thisAnim = new Animation(spriteSheet, 5, 4, 5, 4, false, 500, true);
		thisAnim.setLooping(false);
		animTable.put("Fallen", thisAnim);
		thisAnim = new Animation(spriteSheet, 5, 5, 5, 6, false, 200, true);
		animTable.put("Slide", thisAnim);
		thisAnim = new Animation(spriteSheet, 5,7,5,9,false,250,true);
		animTable.put("Idle", thisAnim);
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
	
	public void setAnimToDraw(String animToDraw){
		currentAnim = animToDraw;
	}
	
	public int getLastKey(){
		return lastKey;
	}
	
	
	@SuppressWarnings("deprecation")
	@Override
	/** Draw a specific animation at this  player's current position */
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
		if (!sliding){
			super.preUpdate(delta);
		}else{
			// different slow down as though half time has passed
			super.preUpdate(delta);
		}
		if (grabJoint != null){
			grabJoint.preStep(delta);
		}
	}
	
	@Override
	public void update (int delta){
		super.update(delta);
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
		if (idleTimer >= 20000){
			setAnimToDraw("Stand");
		} else if (isReversing() && !sliding){
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
			//yOffset = 50;
			setAnimToDraw("Idle");
		}
		
		// check to see if certain animations have reached a point where they should be swapped out
		if (currentAnim == "Slide"){
			if (!(isMoving() || isReversing()) && sliding){
				sliding = false;
			}
		}
	}
	
	@Override
	public void receiveKeyPress(int keyPressed){
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
		super.receiveKeyPress(keyPressed);
	}
	
	public void receiveKeyRelease(int keyReleased){
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
	
	public boolean isSliding(){
		return sliding;
	}
	
	public void setSliding(boolean b){
		sliding=b;
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
			}
		}
		
	}
	
}