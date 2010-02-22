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
import org.newdawn.slick.geom.Path;
import org.newdawn.slick.geom.Rectangle;
import org.newdawn.slick.util.Log;

import ca.keefer.sanemethod.Constants;

public class Switch extends AbstractEntity{
	
	/** The world in which this entity can be found */
	protected World world;
	/** Dimensions of this switch */
	int height, width;
	/** Position of this switch */
	float x, y;
	/** Images containing the two possible switch states */
	Image buttonDefault;
	Image buttonPressed;
	/** zOrder controls the depth of this entity */
	private int zOrder;
	/** boolean controlling this switch's state */
	private boolean state;
	/** boolean controlling whether this switch stays down after being pressed, or needs to be kept down */
	private boolean staysDown;
	/** spriteSheet for animation of this spring */
	SpriteSheet spriteSheet;
	/** The id that any door objects defined on the map will use to reference this switch - must be unique */
	int refID;
	
	public static boolean UP = true;
	public static boolean DOWN = false;
	
	public Switch(int refID, float x, float y, float mass, int zOrder, boolean state, boolean staysDown, SpriteSheet spriteSheet){
		this.active=true;
		// Dimensions of this switch
		Path switchPath = new Path(x,y);
		switchPath.lineTo(x+(64), y);
		switchPath.lineTo(x+(64), y+(64));
		switchPath.lineTo(x,y+(64));
		switchPath.lineTo(x, y);
		switchPath.close();
		
		float[] pts = switchPath.getPoints();
		Vector2f[] vecs = new Vector2f[(pts.length / 2)];
		for (int j=0;j<vecs.length;j++) {
			vecs[j] = new Vector2f(pts[j*2],pts[(j*2)+1]);
		}
		this.x=x;
		this.y=y;
		
		this.body = new Body(new Polygon(vecs),100);
		body.setUserData(this);
		body.setRestitution(1f);
		body.setFriction(0f);
		body.setMoveable(false);
		body.setRotatable(false);
		
		this.state=state;
		this.refID=refID;
		this.staysDown=staysDown;
		this.zOrder = zOrder;
		this.spriteSheet=spriteSheet;
		buildAnimTable();
	}
	
	public int getRefID(){
		return refID;
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
			g.drawImage(buttonDefault, x, y);
		}else{
			g.drawImage(buttonPressed, x, y);
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
			}else if (events[i].getBodyA() == this.body){
				if (!(events[i].getBodyB().isStatic())){
					return true;
				}
			}
		}
		return false;
	}

}
