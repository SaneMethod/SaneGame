package ca.keefer.sanemethod.Entity;

import org.newdawn.slick.Graphics;
import org.newdawn.slick.Image;
import org.newdawn.slick.geom.Rectangle;

import ca.keefer.sanemethod.Constants;
import ca.keefer.sanemethod.Environment.TiledEnvironment;

import net.phys2d.math.Vector2f;
import net.phys2d.raw.Body;
import net.phys2d.raw.World;
import net.phys2d.raw.shapes.Circle;

public class Ball extends AbstractEntity{
	
	/** The world in which this entity can be found */
	protected World world;
	/** Dimensions of this entity */
	int dimensions;
	/** zOrder controls the depth of this entity */
	private int zOrder;
	/** Image for drawing of this entity */
	Image ballImage;
	/** If evaporate is true, this ball costs the player one coin for every second its present, until the player runs out */
	boolean evaporate;
	int evaporateTimer;
	/** Local velocity vector */
	private Vector2f velocity;
	
	public Ball(float x, float y, int zOrder, boolean evaporate, Image ballImage){
		this.active=true;
		this.evaporate=evaporate;
		evaporateTimer=0;
		this.ballImage=ballImage;
		this.dimensions=24;
		this.body = new Body(new Circle(dimensions),5);
		this.body.setFriction(0.1f);
		this.body.setRestitution(0f);
		this.body.setMaxVelocity(30, 50);
		this.body.setUserData(this);
		this.body.setRotatable(false);
		this.body.setPosition(x, y);
		this.velocity = new Vector2f();
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
		
		velocity.x = this.getVelocityX();
		if (velocity.getX() > 0.001){
			velocity.x -= (Constants.FRICTION/2) * velocity.getX();
		}else if (velocity.getX() < -0.001){
			velocity.x -= (Constants.FRICTION/2) * velocity.getX();
		}else{
			velocity.x=0;
		}
		setVelocity(velocity.x,getVelocityY());
	}
	
	@Override
	public void update(int delta) {
		if (evaporate){
			evaporateTimer += delta;
			if (evaporateTimer > 1000){
				evaporateTimer = 0;
				TiledEnvironment tE = (TiledEnvironment)this.getEnvironment();
				if (tE.getPlayer().getCoins() >= 1){
					tE.getPlayer().removeCoins(1);
				}else{
					this.remove();
				}
			}
		}
	}
	
	public void remove(){
		this.world.remove(this.body);
		this.active=false;
	}

	@Override
	public void render(Graphics g) {
		if (body.isRotatable()){
			g.rotate(body.getPosition().getX(),body.getPosition().getY(),(float) Math.toDegrees(body.getRotation()));
			
			g.drawImage(ballImage,body.getPosition().getX()-body.getShape().getBounds().getWidth()/2
					,body.getPosition().getY()-body.getShape().getBounds().getHeight()/2);
			g.rotate(body.getPosition().getX(),body.getPosition().getY(),(float) -Math.toDegrees(body.getRotation()));
		}else{
			g.drawImage(ballImage,(body.getPosition().getX()-body.getShape().getBounds().getWidth()/2)
					,(body.getPosition().getY()-body.getShape().getBounds().getHeight()/2));
		}
	}
	
}
