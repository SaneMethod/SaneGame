package ca.keefer.sanemethod.Entity;


import org.newdawn.slick.Color;
import org.newdawn.slick.Graphics;
import org.newdawn.slick.Image;
import org.newdawn.slick.geom.Rectangle;
import org.newdawn.slick.geom.Vector2f;
import org.newdawn.slick.util.Log;

import ca.keefer.sanemethod.Constants;

public class Player implements Entity{

	float xPos; // x position of sprite
	float yPos; // y position of sprite
	int height; // height of the sprite
	int width; // width of the sprite
	Vector2f velocity;
	float mass; // mass of the sprite
	Image thisImage; // temporary image for testing movement
	double velocityX;
	double velocityY;
	boolean direction;
	boolean moving;
	boolean jumping;
	float accel = Constants.ACCELERATION;
	
	// Constants for Direction
	public static final boolean DIR_LEFT = true;
	public static final boolean DIR_RIGHT = false;
	
	public Player (float x, float y, Image image){
		this.xPos=x;
		this.yPos=y;
		this.mass = 1;
		this.velocity = new Vector2f(0,0);
		this.thisImage=image;
		this.velocityX=0;
		this.velocityY=0;
		this.direction = DIR_LEFT;
		this.moving=false;
		this.jumping=false;
	}
	
	@Override
	public void render(Graphics g) {
		// TODO Auto-generated method stub
		g.drawImage(thisImage, xPos, yPos);
	}
	
	public void setDirection(boolean t){
		direction = t;
	}
	public void setMoving(boolean t){
		moving =t;
	}
	
	public void setJumping (boolean t){
		jumping=t;
	}
	public void setX(float x){
		this.xPos = x;
	}
	public void setY(float y){
		this.yPos = y;
	}
	
	public void setVelocityY(float velY){
		velocity.y = velY;
	}
	public void setVelocityX(float velX){
		velocity.x = velX;
	}
	public void setVelocity(float x, float y){
		velocity = new Vector2f(x,y);
	}
	
	public float getVelocityX(){
		return velocity.getX();
	}
	
	public float getVelocityY(){
		return velocity.getY();
	}
	
	public Vector2f getVelocity(){
		return velocity;
	}
	
	public boolean getDirection(){
		return direction;
	}

	
	@Override
	public void update(int delta) {
		// Call movement calculation
		move(delta);
	}
	
	public void move(int delta){
		if (jumping){
			if (this.yPos <= 500){
			//this.yPos -= velocityY;
				this.yPos -= velocity.getY();
			}else {
				this.yPos = 500;
				jumping =false;
			}
		
		}
		
		
		if (velocity.getY() > -500){
			velocity.y -= Constants.GRAVITY.y + Constants.FRICTION * velocity.getY();
		}else{
			velocity.y = 0;
		}
		
		if (moving){
			//Modeling velocity as an implementation of Stokes' Drag (v = (a/f) - (a/f)*exp(-f*t))
			velocity.x = (float) ((accel/Constants.FRICTION) - (accel/Constants.FRICTION)*Math.exp(-Constants.FRICTION*delta));

			// scale velocity
			if (accel < Constants.MAX_ACCELERATION){
				accel += 0.01;
			}
		}else{
			accel = Constants.ACCELERATION;
			if (velocity.getX() > 0.0001){
				velocity.x -= Constants.FRICTION * velocity.getX();
			}else{
				velocity.x=0;
			}
		}
		if (direction == DIR_LEFT){
			this.xPos -= velocity.getX();
		}else{
			this.xPos += velocity.getX();
		}
	}

	/**
	 * This method takes a keypress event from the state and interprets it into action
	 * for this Entity.
	 * @param keyPressed
	 */
	public void receiveKeyPress(int keyPressed){
		if (keyPressed == Constants.KEY_LEFT){
			this.setDirection(DIR_LEFT);
			this.setMoving(true);
		}else if (keyPressed == Constants.KEY_RIGHT){
			this.setDirection(DIR_RIGHT);
			this.setMoving(true);
		}else if (keyPressed == Constants.KEY_CANCEL){
			this.setJumping(true);
			this.setVelocityY(45);
		}
	}
	
	/**
	 * This method takes a keyRelease event from the state and interprets it into action
	 * or cessation thereof for this Entity.
	 * @param keyReleased
	 */
	public void receiveKeyRelease(int keyReleased){
		if (keyReleased == Constants.KEY_LEFT){
			this.setMoving(false);
		}else if (keyReleased == Constants.KEY_RIGHT){
			this.setMoving(false);
		}
	}

	@Override
	public Rectangle getBoundingBox() {
		return new Rectangle(this.xPos,this.yPos,this.thisImage.getWidth(),this.thisImage.getHeight());
	}
	


}
