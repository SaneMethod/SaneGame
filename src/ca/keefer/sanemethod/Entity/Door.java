package ca.keefer.sanemethod.Entity;

import net.phys2d.math.Vector2f;
import net.phys2d.raw.Body;
import net.phys2d.raw.World;
import net.phys2d.raw.shapes.Box;
import net.phys2d.raw.shapes.ConvexPolygon;
import net.phys2d.raw.shapes.Polygon;

import org.newdawn.slick.Graphics;
import org.newdawn.slick.Image;
import org.newdawn.slick.SpriteSheet;
import org.newdawn.slick.geom.Path;
import org.newdawn.slick.geom.Rectangle;

/**
 * This class extends AbstractEntity, and implements a 'door' which can be controlled by
 * a switch entity. This door is actually composed of a physical body whose dimensions are
 * defined at run-time, and a number of static (?) images. The door must be a multiple of
 * 64 pixels wide and high.
 * @author Christopher Keefer
 *
 */
public class Door extends AbstractEntity{

	/** The world in which this entity can be found */
	protected World world;
	/** Dimensions of this door, as a multiple of the tile width or height */
	int height, width;
	/** Position of this door */
	float x, y;
	/** zOrder controls the depth of this entity */
	private int zOrder;
	/** spriteSheet for animation of this door */
	SpriteSheet spriteSheet;
	/** Image containing the 64x64 block that makes up the door */
	Image blockImage;
	/** Whether this door is currently in operation */
	boolean enabled;
	/** The switch to which this door is linked */
	Switch linkedSwitch;
	/** boolean on whether to fade these blocks away when this body is not active */
	boolean fadeAway;
	
	public Door(float x, float y, int width, int height, int zOrder, boolean enabled, Switch linkedSwitch, 
			SpriteSheet spriteSheet){
		this.active=true;
		this.width=width;
		this.height=height;
		this.zOrder=zOrder;
		this.spriteSheet=spriteSheet;
		this.enabled=enabled;
		this.linkedSwitch=linkedSwitch;
		fadeAway=false;
		
		// Dimensions of this door
		Path doorPath = new Path(x,y);
		doorPath.lineTo(x+(width*64), y);
		doorPath.lineTo(x+(width*64), y+(height*64));
		doorPath.lineTo(x,y+(height*64));
		doorPath.lineTo(x, y);
		doorPath.close();
		
		float[] pts = doorPath.getPoints();
		Vector2f[] vecs = new Vector2f[(pts.length / 2)];
		for (int j=0;j<vecs.length;j++) {
			vecs[j] = new Vector2f(pts[j*2],pts[(j*2)+1]);
		}
		
		// create body based on dimensions and width, height
		this.body = new Body(new Polygon(vecs),100);
		body.setUserData(this);
		body.setRestitution(1f);
		body.setFriction(0f);
		body.setMoveable(false);
		body.setRotatable(false);
		this.x=x;
		this.y=y;
		
		blockImage = spriteSheet.getSubImage(3, 2);
	}
	
	/** Set this door as active in the physics world */
	public void setEnabled(boolean b){
		enabled=b;
		if (enabled == true){
			this.world.add(this.body);
		}else{
			this.world.remove(this.body);
		}
	}
	/** Get whether this body is active in the physics world or not */
	public boolean getEnabled(){
		return enabled;
	}
	
	
	@Override
	public Body getBody() {
		return this.body;
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
		if (linkedSwitch.getSwitchState() == Switch.DOWN){
			fadeAway=true;
		}else{
			fadeAway=false;
		}
	}
	
	@Override
	public void update(int delta) {
		
		if (fadeAway){
			if (blockImage.getAlpha() > 0){
				blockImage.setAlpha(blockImage.getAlpha()-(0.001f*delta));
			}else if (enabled){
				this.setEnabled(false);
			}
		}else{
			if (!enabled){
				this.setEnabled(true);
			}
			if (blockImage.getAlpha() < 1.0f){
				blockImage.setAlpha(blockImage.getAlpha()+(0.001f*delta));
			}
		}
		
	}
	
	@Override
	public void render(Graphics g) {
		for (int x=0;x<this.width;x++){
			for (int y=0;y<this.height;y++){
				g.drawImage(blockImage, this.x+(x*64), this.y+(y*64));
			}
		}
	}
}
