package ca.keefer.sanemethod.Entity;

import java.util.ArrayList;

import org.newdawn.slick.Animation;
import org.newdawn.slick.Graphics;
import org.newdawn.slick.Image;
import org.newdawn.slick.SpriteSheet;
import org.newdawn.slick.geom.Rectangle;
import org.newdawn.slick.util.Log;

import ca.keefer.sanemethod.Constants;
import ca.keefer.sanemethod.Interface.Text;
import ca.keefer.sanemethod.Interface.TextHandler;

import net.phys2d.raw.Body;
import net.phys2d.raw.StaticBody;
import net.phys2d.raw.CollisionEvent;
import net.phys2d.raw.World;
import net.phys2d.raw.shapes.Circle;

/**
 * This entity links into the common TextHandler object, and displays the contents on collision with this entity,
 * enabling a player to view and choose text and options.
 * @author Christopher Keefer
 *
 */
public class Talkable extends AbstractEntity{

	/** The world in which this entity can be found */
	protected World world;
	/** Position of this entity */
	float x, y;
	/** zOrder controls the depth of this entity */
	private int zOrder;
	/** Animation representing this entity */
	Image def;
	Animation talkAnim;
	/** How long its been since we last updated this entities status */
	int checkTimer;
	/** How long between updating whether or not this block has collided with the player */
	private final int CHECK_PERIOD = 50;
	/** The textHandler containing the dialog for this entity */
	TextHandler textHandler;
	/** whether we're currently displaying the text */
	boolean displayText;
	
	public Talkable(float x, float y, int zOrder, ArrayList<Text> dialog, short textPosition, SpriteSheet spriteSheet){
		this.active=true;
		this.body = new StaticBody(new Circle(16));
		body.setPosition(x, y);
		this.zOrder=zOrder;
		def = spriteSheet.getSubImage(0, 0);
		talkAnim= new Animation(spriteSheet,1,0,1,4,false,200,true);
		this.textHandler = new TextHandler(dialog, 10,10,textPosition,740);
		displayText=false;
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
		checkTimer+=delta;
		if (checkTimer > CHECK_PERIOD && !displayText){
			checkTimer=0;
			displayText = checkForCollision();
			if (displayText){
				// hook into common textHandler
				Constants.textHandler = this.textHandler;
			}
		}
	}

	@Override
	public void update(int delta) {
		if (displayText){
			textHandler.update(delta);
			if (textHandler.getDone()){
				displayText = false;
				Constants.textHandler = null;
				this.textHandler.reset();
			}
		}
		
	}
	
	@Override
	public void render(Graphics g) {
		if (displayText){
			g.drawAnimation(talkAnim, this.getX()-32,this.getY()-36);
		}else{
			g.drawImage(def,this.getX()-32,this.getY()-36);
		}
	}
	
	protected boolean checkForCollision(){
		if (world == null) {
			Log.error("no world found in Talkable");
			return false;
		}
		// Get all the collision events involving this object
		CollisionEvent[] events = world.getContacts(this.body);
		
		for (int i=0;i<events.length;i++){
			if (events[i].getBodyB() == this.body){
				if ((!(events[i].getBodyA().isStatic()) && (events[i].getBodyA().getUserData().getClass() == Player.class))){
					return true;
				}
			}else if (events[i].getBodyA() == this.body){
				if ((!(events[i].getBodyB().isStatic()) && (events[i].getBodyB().getUserData().getClass() == Player.class))){
					return true;
				}
			}
		}
		return false;
	}
}
