package ca.keefer.sanemethod.Entity;

import net.phys2d.raw.Body;
import net.phys2d.raw.CollisionEvent;
import net.phys2d.raw.World;
import net.phys2d.raw.shapes.Circle;

import ca.keefer.sanemethod.Environment.TiledEnvironment;

import org.newdawn.slick.Animation;
import org.newdawn.slick.Graphics;
import org.newdawn.slick.SlickException;
import org.newdawn.slick.SpriteSheet;
import org.newdawn.slick.geom.Rectangle;
import org.newdawn.slick.util.Log;

/**
 * This class extends abstractEntity and represents a collectable coin in the physical world.
 * @author Christopher Keefer
 *
 */
public class Coin extends AbstractEntity{
	
	/** The world in which this entity can be found */
	protected World world;
	/** Dimensions of this entity, as a multiple of the tile width or height */
	int height, width;
	/** Position of this entity */
	float x, y;
	/** zOrder controls the depth of this entity */
	private int zOrder;
	/** spriteSheet for animation of this entity */
	SpriteSheet spriteSheet;
	/** Animation of this entity */
	Animation coinAnim;

	public Coin(float x, float y, int zOrder){
		this.x=x;
		this.y=y;
		this.zOrder=zOrder;
		active=true;
		this.body = new Body(new Circle(16),1);
		this.body.setUserData(this);
		height=32;
		width=32;
		this.body.setMoveable(false);
		try {
			this.spriteSheet = new SpriteSheet("res/Sprites/Coins.png",64,64);
		}catch (SlickException e){
			Log.error("Failure to load Coin sprites:"+e.getMessage());
			this.spriteSheet = null;
		}
		
		if (spriteSheet != null){
			coinAnim = new Animation(spriteSheet,1,0,1,5,false,150,true);
		}
		setPosition(x, y);
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
		if (active){
			collectOnCollision();
		}
		
	}

	@Override
	public void update(int delta) {
		// TODO Auto-generated method stub
		
	}
	
	@Override
	public void render(Graphics g) {
		if(active){
			coinAnim.draw(this.body.getPosition().getX()-width, this.body.getPosition().getY()-height);
		}
	}
	
	private void collectOnCollision(){
		if (world == null) {
			Log.error("no world found in Coin");
			return;
		}
		// Get all the collision events involving this object
		CollisionEvent[] events = world.getContacts(this.body);
		
		for (int i=0;i<events.length;i++){
			if (events[i].getBodyB() == this.body){
				if (!(events[i].getBodyA().isStatic())){
					TiledEnvironment tE = (TiledEnvironment)this.getEnvironment();
					tE.getPlayer().addCoins(10);
					this.world.remove(this.body);
					this.active = false;
				}
			}else if (events[i].getBodyA() == this.body){
				if (!(events[i].getBodyB().isStatic())){
					TiledEnvironment tE = (TiledEnvironment)this.getEnvironment();
					tE.getPlayer().addCoins(10);
					this.world.remove(this.body);
					this.active = false;
				}
			}
		}
	}

	
}
