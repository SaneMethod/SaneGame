package ca.keefer.sanemethod.Entity;

import net.phys2d.math.Vector2f;
import net.phys2d.raw.Body;
import net.phys2d.raw.CollisionEvent;
import net.phys2d.raw.FixedJoint;
import net.phys2d.raw.World;
import net.phys2d.raw.shapes.Box;
import net.phys2d.raw.shapes.Circle;
import net.phys2d.raw.shapes.Polygon;

import org.newdawn.slick.Graphics;
import org.newdawn.slick.Image;
import org.newdawn.slick.SpriteSheet;
import org.newdawn.slick.geom.Line;
import org.newdawn.slick.geom.Rectangle;

import ca.keefer.sanemethod.Constants;

public class Elevator extends AbstractEntity{
	/** The world in which this entity can be found */
	protected World world;
	/** Dimensions of the elevator shape */
	int height, width;
	/** shapeType controls which kind of shape we'll assign to the bottom of this Elevator */
	int shapeType;
	/** zOrder controls the depth of this entity */
	private int zOrder;
	/** direction controls the obvious */
	private short direction;
	/** moving references the obvious */
	private boolean moving;
	/** Images for the various directions */
	Image eLeft, eRight, eUp, eDown, eStop;
	/** auto determines whether the platform moves on its own following a simple line path */
	private boolean auto;
	/** line controls the movement of simple automated platform */
	private Line line;
	/** speed controls how fast the elevator moves */
	private int speed;
	/** spriteSheet for animation of this elevator */
	SpriteSheet spriteSheet;
	/** fixedJoint applies movement of this Elevator to whatever is riding it */
	FixedJoint fixedJoint;
	/** attachedBody is the body attached to this elevator by the fixedJoint */
	Body attachedBody;
	
	public static final short UP = 1;
	public static final short DOWN = 2;
	public static final short LEFT = 3;
	public static final short RIGHT = 4;
	public static final short STOP = 0;
	

	public Elevator(float x, float y, int shapeType, Vector2f[] dimensions, float mass, float restitution, 
			float friction, int zOrder, short direction, boolean auto, int speed, Line line, Vector2f maxVelocity,
			SpriteSheet spriteSheet){
		
		this.shapeType=shapeType;
		if (shapeType == Constants.SHAPE_TYPE_CIRCLE){
			this.body = new Body(new Circle(dimensions[0].x/2),mass);
			width = (int) dimensions[0].x*2;
			height = (int) dimensions[0].x;
		}else if (shapeType  == Constants.SHAPE_TYPE_BOX){
			this.body = new Body(new Box(dimensions[0].x,dimensions[0].y),mass);
		}else if (shapeType == Constants.SHAPE_TYPE_POLYGON){
			this.body = new Body(new Polygon(dimensions),mass);
			height = (int)body.getShape().getBounds().getHeight()+1;
			width = (int)body.getShape().getBounds().getWidth()+1;
		}
		
		this.auto = auto;
		if (auto){
			this.line=line;
		}
		
		body.setUserData(this);
		body.setRestitution(restitution);
		body.setFriction(friction);
		body.setMoveable(true);
		body.setGravityEffected(false);
		body.setRotatable(false);
		body.setMaxVelocity(maxVelocity.getX(), maxVelocity.getY());
		
		if (auto){
			setPosition(line.getStart().getX(),line.getStart().getY());
		}else{
			setPosition(x,y);
		}
		
		this.zOrder=zOrder;
		this.direction=direction;
		this.moving=false;
		this.speed = speed;
		this.spriteSheet = spriteSheet;
		fixedJoint=null;
		buildAnimTable();
	}
	
	public void buildAnimTable(){
		eUp = spriteSheet.getSubImage(0, 0);
		eDown = spriteSheet.getSubImage(0, 1);
		eRight = spriteSheet.getSubImage(0, 2);
		eLeft = spriteSheet.getSubImage(0, 3);
		eStop = spriteSheet.getSubImage(0,4);
	}
	
	public short getDirection(){
		return direction;
	}
	public void setDirection(short i){
		direction=i;
	}
	
	public boolean isMoving(){
		return moving;
	}
	public void setMoving(boolean b){
		moving=b;
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
		this.zOrder=z;
	}
	
	@Override
	public void preUpdate(int delta) {
		if (fixedJoint != null){
			fixedJoint.preStep(delta);
		}
		if (fixedJoint == null){
			if (considerCollision()){
			//Log.debug("Collision on top of elevator");
				if (attachedBody.getUserData().getClass() == Player.class){
					fixedJoint = new FixedJoint(this.body,attachedBody);
					world.add(fixedJoint);
				}
			}
		}
		
	}

	@Override
	public void update(int delta) {
		switch(direction){
		case UP:
			body.addForce(new Vector2f(0,-speed));
			if (auto && body.getPosition().getY() <= line.getEnd().getY()){
				setVelocity(0,0);
				direction = DOWN;
			}
			break;
		case DOWN:
			body.addForce(new Vector2f(0,speed));
			if (auto && body.getPosition().getY() >= line.getStart().getY()){
				setVelocity(0,0);
				direction = UP;
			}
			break;
		case LEFT:
			body.addForce(new Vector2f(-speed,0));
			if (auto && body.getPosition().getX() <= line.getStart().getX()){
				setVelocity(0,0);
				direction = RIGHT;
			}
			break;
		case RIGHT:
			body.addForce(new Vector2f(speed,0));
			if (auto && body.getPosition().getX() >= line.getEnd().getX()){
				setVelocity(0,0);
				direction = LEFT;
			}
			break;
		case STOP:
			setVelocity(0,0);
		}
		
		if (fixedJoint != null){
			fixedJoint.applyImpulse();
			Player yada = (Player) attachedBody.getUserData();
			if (yada.getLastKey() == Constants.KEY_JUMP){
				world.remove(fixedJoint);
				fixedJoint = null;
			}else if (yada.getLastKey() == Constants.KEY_UP && !auto){
				this.direction = UP;
			}else if (yada.getLastKey() == Constants.KEY_DOWN && !auto){
				this.direction = DOWN;
			}else if (yada.getLastKey() == Constants.KEY_LEFT && !auto){
				this.direction = LEFT;
			}else if (yada.getLastKey() == Constants.KEY_RIGHT && !auto){
				this.direction = RIGHT;
			}
		}
		
	}
	
	@Override
	public void render(Graphics g) {
		switch(direction){
		case UP:
			g.drawImage(eUp, body.getPosition().getX()-width/6, 
					body.getPosition().getY()-eUp.getHeight()+height/4);
			break;
		case DOWN:
			g.drawImage(eDown, body.getPosition().getX()-width/6, 
					body.getPosition().getY()-eUp.getHeight()+height/4);
			break;
		case LEFT:
			g.drawImage(eLeft, body.getPosition().getX()-width/6, 
					body.getPosition().getY()-eUp.getHeight()+height/4);
			break;
		case RIGHT:
			g.drawImage(eRight, body.getPosition().getX()-width/6, 
					body.getPosition().getY()-eUp.getHeight()+height/4);
			break;
		case STOP:
			g.drawImage(eStop, body.getPosition().getX()-width/6, 
					body.getPosition().getY()-eUp.getHeight()+height/4);
			break;
		}
		
	}
	
	public boolean considerCollision(){
		if (world == null) {
			return false;
		}
		
		// loop through the collision events that have occured in the world
		CollisionEvent[] events = world.getContacts(body);
		
		for (int i=0;i<events.length;i++) {
			//Log.debug("CollisionEvent:"+events[i].getPenetrationDepth());
			
			// if the point of collision was above the centre of the elevator
			if (events[i].getPoint().getY() < getY()+(height/4)) {
				// check the normal to work out which body we care about
				// if the right body is involved and a collision has happened
				// above it then we're on the elevator platform
				if (events[i].getNormal().getY() == 1.0) {
					if (events[i].getBodyB() == body) {
						//Log.debug(events[i].getPoint()+","+events[i].getNormal());
						attachedBody = events[i].getBodyA();
						return true;
					}
				}
				if (events[i].getNormal().getY() == 1.0) {
					if (events[i].getBodyA() == body) {
						//Log.debug(events[i].getPoint()+","+events[i].getNormal());
						attachedBody = events[i].getBodyB();
						return true;
					}
				}
			}
		}
		return false;
	}

}
