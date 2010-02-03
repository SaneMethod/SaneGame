package ca.keefer.sanemethod.Entity;

import net.phys2d.math.Vector2f;
import net.phys2d.raw.Body;
import net.phys2d.raw.CollisionEvent;
import net.phys2d.raw.World;
import net.phys2d.raw.shapes.Box;
import net.phys2d.raw.shapes.Circle;
import net.phys2d.raw.shapes.Polygon;

import org.newdawn.slick.Graphics;
import org.newdawn.slick.Image;
import org.newdawn.slick.SpriteSheet;
import org.newdawn.slick.geom.Rectangle;
import org.newdawn.slick.util.Log;

import ca.keefer.sanemethod.Constants;

public class Switch extends AbstractEntity{
	
	/** The world in which this entity can be found */
	protected World world;
	/** Dimensions of this switch */
	int height, width;
	/** Images containing the two possible switch states */
	Image buttonDefault;
	Image buttonPressed;
	/** shapeType controls which kind of shape we'll assign to this Entity for collision detection */
	int shapeType;
	/** zOrder controls the depth of this entity */
	private int zOrder;
	/** boolean controlling this switch's state */
	private boolean state;
	/** boolean controlling whether this switch stays down after being pressed, or needs to be kept down */
	private boolean staysDown;
	/** spriteSheet for animation of this spring */
	SpriteSheet spriteSheet;
	
	public static boolean UP = true;
	public static boolean DOWN = false;
	
	public Switch(float x, float y, int shapeType, Vector2f[] dimensions, float mass, float restitution, 
			float friction, int zOrder, boolean state, boolean staysDown, SpriteSheet spriteSheet){
		
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
		
		this.state=state;
		this.staysDown=staysDown;
		this.zOrder = zOrder;
		this.spriteSheet=spriteSheet;
		buildAnimTable();
	}
	
	public void buildAnimTable(){
		buttonDefault = spriteSheet.getSubImage(5, 3);
		buttonPressed = spriteSheet.getSubImage(6, 0);
	}
	
	public boolean getSwitchState(){
		return state;
	}
	public void setSwitchState(boolean b){
		state=b;
	}
	
	public boolean getStaysDown(){
		return staysDown;
	}
	public void setStaysDown(boolean b){
		staysDown=b;
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
		// If this button doesn't stay down, check if there's anything on it keeping it down
		if (state == DOWN && staysDown == false){
			if (!checkForCollision()){
				state = UP;
			}
		}
		if (checkForCollision()){
			state = DOWN;
		}
		
	}

	@Override
	public void update(int delta) {
		// TODO Auto-generated method stub
		
	}
	
	@Override
	public void render(Graphics g) {
		if (state == UP){
			g.drawImage(buttonDefault, body.getPosition().getX()-width/2, body.getPosition().getY()-height);
		}else{
			g.drawImage(buttonPressed, body.getPosition().getX()-width/2, body.getPosition().getY()-height);
		}	
	}
	
	protected boolean checkForCollision(){
		if (world == null) {
			Log.error("no world found in Switch");
			return false;
		}
		// Get all the collision events involving this object
		CollisionEvent[] events = world.getContacts(this.body);
		
		for (int i=0;i<events.length;i++){
			if (events[i].getBodyB() == this.body){
				if (!(events[i].getBodyA().isStatic())){
					return true;
				}
			}
		}
		return false;
	}

}
