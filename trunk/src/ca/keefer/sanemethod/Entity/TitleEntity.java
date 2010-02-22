package ca.keefer.sanemethod.Entity;

import net.phys2d.math.Vector2f;
import net.phys2d.raw.Body;
import net.phys2d.raw.CollisionEvent;
import net.phys2d.raw.World;
import net.phys2d.raw.shapes.Box;
import net.phys2d.raw.shapes.Circle;
import net.phys2d.raw.shapes.Polygon;

import org.newdawn.slick.AngelCodeFont;
import org.newdawn.slick.Color;
import org.newdawn.slick.Graphics;
import org.newdawn.slick.geom.Path;
import org.newdawn.slick.geom.Rectangle;
import org.newdawn.slick.util.Log;

/**
 * This class instantiates AbstractEntity, and is based on the dimensions of a string,
 * to provide a physical body therefore - intended for use in the title sequence,
 * hence the name
 * @author Christopher Keefer
 *
 */
public class TitleEntity extends AbstractEntity{
	
	/** The world in which this entity can be found */
	protected World world;
	/** Dimensions of this entity */
	int height, width;
	/** Position of this entity */
	float x, y;
	/** number of times this entity has bounced (and thus, whether we should turn off resititution) */
	int bounces;
	/** zOrder controls the depth of this entity */
	private int zOrder;
	/** String controlling the contents of this title entity */
	String text;
	/** AngelCodeFont we're drawing the text with, supplied by the Constants class */
	AngelCodeFont agf;
	/** Colour of the string */
	Color color;
	/** body we need to move out of the way of this entity */
	Body tBody;
	/** Controls whether we're fading in, out, or neither */
	short fadeState;
	
	public static final short FADE_IN = 0;
	public static final short FADE_OUT = 1;
	public static final short FADE_NONE = 2;
	
	public TitleEntity(float x, float y, int zOrder, String text, AngelCodeFont agf, Color color){
		this.active=true;
		this.x=x;
		this.y=y;
		this.agf=agf;
		this.text=text;
		this.color = color;
		this.width = agf.getWidth(text)-20;
		this.height= agf.getHeight(text)-10;
		
		// Dimensions of this door
		Path doorPath = new Path(x,y);
		doorPath.lineTo(x+(width), y);
		doorPath.lineTo(x+(width), y+(height));
		doorPath.lineTo(x,y+(height));
		doorPath.lineTo(x, y);
		doorPath.close();
		
		float[] pts = doorPath.getPoints();
		Vector2f[] vecs = new Vector2f[(pts.length / 2)];
		for (int j=0;j<vecs.length;j++) {
			vecs[j] = new Vector2f(pts[j*2],pts[(j*2)+1]);
		}
		
		//this.body = new Body(new Circle(width/2),10); // Why do only circles work for moveable bodies? Its strange.
		//this.body = new Body(new Box(width,height),10);
		this.body = new Body(new Polygon(vecs),100);
		body.setUserData(this);
		body.setRestitution(1f);
		body.setFriction(0f);
		body.setMoveable(false);
		body.setRotatable(false);
		//body.setPosition(x, y);
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
	
	public void setFadeState (short fadeState){
		this.fadeState = fadeState;
	}
	
	@Override
	public void preUpdate(int delta) {
		// move all non-static objects out of the area occupied by this entity, to a safe place
		if ((tBody = checkForCollision()) != null){
			//tBody.setPosition(70, 70);
		}
		
	}

	@Override
	public void update(int delta) {
		if (fadeState == FADE_IN){
			if (this.color.getAlpha() < 1.0f){
				this.color.a = this.color.getAlpha()+(0.001f*delta);
			}else {
				fadeState = FADE_NONE;
			}
		}else if (fadeState == FADE_OUT){
			if (this.color.getAlpha() > 0){
				this.color.a = this.color.getAlpha()-(0.001f*delta);
			}else {
				fadeState = FADE_NONE;
			}
		}
		
	}
	
	@Override
	public void render(Graphics g) {
		agf.drawString(x, y-10, this.text, this.color);
	}
	
	protected Body checkForCollision(){
		if (world == null) {
			Log.error("no world found in TitleEntity");
			return null;
		}
		// Get all the collision events involving this object
		CollisionEvent[] events = world.getContacts(this.body);
		
		for (int i=0;i<events.length;i++){
			if (events[i].getBodyB() == this.body){
				if (!events[i].getBodyA().isStatic()){
					return events[i].getBodyA();
				}
			}else if (events[i].getBodyA() == this.body){
				if (!events[i].getBodyB().isStatic()){
					return events[i].getBodyB();
				}
			}
		}
		return null;
	}

}