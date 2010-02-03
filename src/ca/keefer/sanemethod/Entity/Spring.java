package ca.keefer.sanemethod.Entity;

import java.util.Hashtable;

import net.phys2d.math.Vector2f;
import net.phys2d.raw.Body;
import net.phys2d.raw.CollisionEvent;
import net.phys2d.raw.World;
import net.phys2d.raw.shapes.Box;
import net.phys2d.raw.shapes.Circle;
import net.phys2d.raw.shapes.Polygon;

import org.newdawn.slick.Animation;
import org.newdawn.slick.Graphics;
import org.newdawn.slick.SpriteSheet;
import org.newdawn.slick.geom.Rectangle;
import org.newdawn.slick.util.Log;

import ca.keefer.sanemethod.Constants;

/**
 * This class contains a physical object (the spring) and a springJoint, which applies the spring forces to
 * the Platformer/Player, and to the spring, compressing and stretching it as desired
 * @author Christopher Keefer
 *
 */
public class Spring extends AbstractEntity {

	/** The world in which this entity can be found */
	protected World world;
	/** Dimensions of this spring */
	int height, width;
	/** Hashtable containing the spring's animations, keyed to strings which name the animation */
	Hashtable<String,Animation> animTable;
	/** shapeType controls which kind of shape we'll assign to this Entity for collision detection */
	int shapeType;
	/** zOrder controls the depth of this entity */
	private int zOrder;
	/* The variables which control the desired physical interactions */
	private boolean bounceAffect;
	private int bounceTimer;
	private Body bounceBody;
	private int springAffectDuration;
	/** spriteSheet for animation of this spring */
	SpriteSheet spriteSheet;
	/** The animation tied to this string to draw from the animTable */
	String currentAnim;
	
	/**
	 * Create a spring object
	 * @param x
	 * @param y
	 * @param shapeType
	 * @param dimensions
	 * @param mass
	 * @param restitution
	 * @param friction
	 * @param zOrder
	 * @param springAffectDuration
	 * @param spriteSheet
	 */
	public Spring (float x, float y, int shapeType, Vector2f[] dimensions, float mass, float restitution, 
			float friction, int zOrder, int springAffectDuration, SpriteSheet spriteSheet){
		
		this.shapeType=shapeType;
		if (shapeType == Constants.SHAPE_TYPE_CIRCLE){
			this.body = new Body(new Circle(dimensions[0].x/2),mass);
			width = (int) dimensions[0].x*2;
			height = (int) dimensions[0].x;
		}else if (shapeType  == Constants.SHAPE_TYPE_BOX){
			this.body = new Body(new Box(dimensions[0].x,dimensions[0].y),mass);
		}else if (shapeType == Constants.SHAPE_TYPE_POLYGON){
			this.body = new Body(new Polygon(dimensions),mass);
		}
		
		body.setUserData(this);
		body.setRestitution(restitution);
		body.setFriction(friction);
		body.setMoveable(false);
		body.setRotatable(false);
		setPosition(x,y);
		
		this.springAffectDuration = springAffectDuration;
		bounceAffect = false;
		bounceTimer = 0;
		this.zOrder = zOrder;
		this.spriteSheet=spriteSheet;
		animTable = new Hashtable<String,Animation>();
		buildAnimTable();
	}
	
	protected void buildAnimTable(){
		/*
		 * Scaling Routine
		int vCount = spriteSheet.getVerticalCount();
		int hCount = spriteSheet.getHorizontalCount();
		Image sheetToScale = spriteSheet.getScaledCopy(hCount*width,vCount*height);
		spriteSheet = new SpriteSheet(sheetToScale,sheetToScale.getWidth()/hCount,sheetToScale.getHeight()/vCount);
		*/
		
		Animation thisAnim = new Animation();
		thisAnim.addFrame(spriteSheet.getSubImage(1,3), 1);
		thisAnim.setAutoUpdate(false);
		animTable.put("Base", thisAnim);
		thisAnim = new Animation(spriteSheet,0,0,1,3,false,50,true);
		thisAnim.setLooping(false);
		animTable.put("Bounce", thisAnim);
		currentAnim = "Base";
	}
	
	public void setAnimToDraw(String animToDraw){
		currentAnim = animToDraw;
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
		if (bounceAffect && bounceTimer < springAffectDuration){
			bounceBody.addForce(new Vector2f(0,-1000));
			bounceTimer += delta;
		}else if (bounceTimer >= 500){
			bounceBody = null;
			bounceAffect = false;
			bounceTimer = 0;
		}else if (bounceAffect == false){
			SpringOnCollide();
		}
		animTable.get(currentAnim).update(delta);
	}

	@Override
	public void update(int delta) {

	}
	
	@Override
	public void render(Graphics g) {
		g.drawAnimation(animTable.get(currentAnim), body.getPosition().getX()-width/2, body.getPosition().getY()-height);
		if (currentAnim == "Bounce"){
			if (animTable.get(currentAnim).getFrame() == animTable.get(currentAnim).getFrameCount()-1){
				animTable.get(currentAnim).restart();
				currentAnim = "Base";
			}
		}
		//Log.debug("Spring Speed:"+animTable.get(currentAnim).getSpeed());
	}
	
	protected void SpringOnCollide(){
		if (world == null) {
			Log.error("no world found in Spring");
			return;
		}

		// Get all the collision events involving this object
		CollisionEvent[] events = world.getContacts(this.body);
		for (int i=0;i<events.length;i++){
			// check that this collision occurred near the top of the spring
			//if (events[i].getPoint().getY() > getY()+((height/4)*3)){
				if (events[i].getBodyB() == this.body){
					if (!(events[i].getBodyA().isStatic())){
						//Log.debug("Detected Collision on Spring");
						events[i].getBodyA().addForce(new Vector2f(0,-1000));
						bounceAffect = true;
						bounceBody = events[i].getBodyA();
						currentAnim = "Bounce";
						/* Swaps out the standing animation for jumping,
						 * and prevents the user from double-jumping from
						 * a spring - worthwhile? It can cause errors.
						 */
						if (bounceBody.getUserData().getClass() == Player.class){
							Player setJump = (Player) bounceBody.getUserData();
							setJump.setJumping(true);
							setJump.onGround = false;
						}
					}
					
				}
			}
		}

}
