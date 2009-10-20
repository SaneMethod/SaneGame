package ca.keefer.sanemethod.Entity;

import org.newdawn.slick.Color;
import org.newdawn.slick.Graphics;
import org.newdawn.slick.Image;
import org.newdawn.slick.geom.Rectangle;
import org.newdawn.slick.util.Log;

import ca.keefer.sanemethod.Constants;

public class Sprite implements Entity{

	float xPos; // x position of sprite
	float yPos; // y position of sprite
	int height; // height of the sprite
	int width; // width of the sprite
	float mass; // mass of the sprite
	AABB boundingBox;
	Image thisImage; // temporary image for testing movement
	double velocityX;
	double velocityY;
	boolean movingLeft;
	boolean moving;
	boolean jumping;
	float accel = Constants.ACCELERATION;
	
	public Sprite (float x, float y, Image image){
		this.xPos=x;
		this.yPos=y;
		this.mass = 1;
		this.thisImage=image;
		boundingBox = new AABB(this.xPos,this.yPos,this.thisImage.getWidth(),this.thisImage.getHeight());
		this.velocityX=0;
		this.velocityY=0;
		this.movingLeft=true;
		this.moving=false;
		this.jumping=false;
	}
	
	@Override
	public float getXPosition() {
		// TODO Auto-generated method stub
		return xPos;
	}

	@Override
	public float getYPosition() {
		// TODO Auto-generated method stub
		return yPos;
	}

	@Override
	public void setXPosition(float x) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void setYPosition(float y) {
		// TODO Auto-generated method stub
		
	}
	
	@Override
	public void render(Graphics g) {
		// TODO Auto-generated method stub
		g.drawImage(thisImage, xPos, yPos);
		g.setColor(Color.red);
		g.draw(this.boundingBox);
	}
	
	public void setMovingDir(boolean t){
		movingLeft = t;
	}
	public void setMoving(boolean t){
		moving =t;
	}
	
	public void setJumping (boolean t){
		jumping=t;
	}
	
	public void setVelY(double velY){
		velocityY = velY;
	}

	
	@Override
	public void update(int delta) {
		// Call horizontal and vertical movement calculation
		move(delta);
		
		this.boundingBox.setLocation(this.xPos, this.yPos);
	}
	
	public void move(int delta){
		if (jumping){
			if (this.yPos <= 500){
			this.yPos -= velocityY;
			}else {
				this.yPos = 500;
				jumping =false;
			}
		
		}
		
		if (velocityY > -500){
			velocityY -= Constants.ACCELERATION*4 + Constants.FRICTION * velocityY;
		}else{
			velocityY = 0;
		}
		if (moving){
			//Modeling velocity as an implementation of Stokes' Drag (v = (a/f) - (a/f)*exp(-f*t))
			velocityX = (accel/Constants.FRICTION) - (accel/Constants.FRICTION)*Math.exp(-Constants.FRICTION*delta);
			// scale velocity
			if (accel < 1.5){
				accel += 0.01;
			}
		}else{
			accel = Constants.ACCELERATION;
			if (velocityX > 0.0001){
			velocityX -= Constants.FRICTION * velocityX;
			}else{
				velocityX=0;
			}
		}
		if (movingLeft){
			this.xPos -= velocityX;
		}else{
			this.xPos += velocityX;
		}
	}

	@Override
	public Rectangle getBoundingRectangle() {
		return this.boundingBox;
	}

	@Override
	public boolean collides(Entity e) {	
		// render for one frame ahead
		this.boundingBox.setLocation(this.xPos, this.yPos);
		if (this.boundingBox.intersects(e.getBoundingRectangle())){
			return true;
		}
		return false;
	}
	
	@Override
	public void impulse(Entity e){
		
		if (movingLeft){
			this.xPos += velocityX;
		}else {
			this.xPos -= velocityX;
		}
		if (jumping){
			this.yPos += velocityY;
		}
		
	}

}
