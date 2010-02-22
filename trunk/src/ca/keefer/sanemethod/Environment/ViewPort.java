package ca.keefer.sanemethod.Environment;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Enumeration;
import java.util.Hashtable;

import org.newdawn.slick.Graphics;
import org.newdawn.slick.geom.Rectangle;
import org.newdawn.slick.geom.Shape;
import org.newdawn.slick.geom.Vector2f;
import org.newdawn.slick.state.StateBasedGame;

import ca.keefer.sanemethod.Constants;
import ca.keefer.sanemethod.Entity.Entity;

/**
 * Controls whats viewable on-screen at any one time - the 'camera' through which the user sees
 * the virtual world. It also controls what is actually drawn to the screen, as all layers must
 * relay their render requests to their assigned viewport. Only one viewport object should be
 * instantiated at a time - however, multiple 'camera' objects can be attached to any one
 * viewport, allowing for multiple scenes. Only one 'camera' can be active at a time.
 * @author Christopher Keefer
 * @version 1.1
 *
 */
public class ViewPort {
	
	/** Definitions for the dimensions of the viewport and its boundaries */
	private int x,y,minX,minY,width,height,maxX,maxY,tileMapWidth,tileMapHeight;
	/** Layer hashTable object for layers attached to this viewport for rendering */
	private ArrayList<Layer> layerList;
	/** Clipping shape for mask if defined viewport size is smaller than the screenheight and width */
	private Shape clip;
	/** Entity this viewpoint is following */
	Entity entityToTrack;
	/** 
	 * int determining whether to center the entityToTrack, or to follow the entity to track;
	 * if both are false, then we are not tracking an entity. Center means to keep that entity in the
	 * center of the screen (roughly); follow means to scroll the camera only when the entityToTrack goes
	 * beyond a certain bounds in any direction */
	int trackMode;
	public static final int TRACK_MODE_NONE=0;
	public static final int TRACK_MODE_CENTER=1;
	public static final int TRACK_MODE_FOLLOW=2;
	
	/**
	 * Create a default Viewport and Camera
	 * @param game The game this viewport is attached to.
	 */
	public ViewPort(StateBasedGame game){
		//assign default values to this viewport and create/add a default camera
		x=0;
		y=0;
		minX=0;
		minY=0;
		width=Constants.SCREENWIDTH;
		height=Constants.SCREENHEIGHT;
		maxX=game.getContainer().getWidth();
		maxY=game.getContainer().getHeight();
		
		// Define clipping shape
		clip = new Rectangle(x,y,width,height);
		
		layerList=new ArrayList<Layer>();
		
		entityToTrack=null;
		trackMode=TRACK_MODE_NONE;
	}
	
	/**
	 * Create a fully defined viewport and attach a Camera object
	 * @param x
	 * @param y
	 * @param minX
	 * @param minY
	 * @param width
	 * @param height
	 * @param maxX
	 * @param maxY
	 * @param camera
	 */
	public ViewPort(int x,int y,int minX,int minY,int width,int height,int maxX,int maxY){
		this.x=x;
		this.y=y;
		this.minX=minX;
		this.minY=minY;
		this.width=width;
		this.height=height;
		this.maxX=maxX;
		this.maxY=maxY;
		
		// Define clipping shape
		clip = new Rectangle(x,y,width,height);
		
		layerList=new ArrayList<Layer>();
		
		entityToTrack=null;
		trackMode=TRACK_MODE_NONE;
	}
	
	/**
	 * Define the clipping Shape with another shape
	 * @param shape
	 */
	public void defineClip(Shape shape){
		clip = shape;
	}
	
	/**
	 * Attach a layer to this viewport
	 * @param layer
	 */
	public void attachLayer(Layer layer){
		layer.setViewPort(this);
		//layer.setActive(true);
		layerList.add(layer);
	}
	
	/**
	 * Get a layer by its index from this viewport
	 * @param index
	 * @return The layer with the given index
	 */
	public Layer getLayer(int index){
		return layerList.get(index);
	}
	
	/**
	 * Get a layer by its id from this viewport
	 * @param id
	 * @return The layer with a matching id, or null if none match
	 */
	public Layer getLayerById(int id){
		for (int i=0;i<layerList.size();i++){
			if (layerList.get(i).getId() == id){
				return layerList.get(i);
			}
		}
		return null;
	}
	
	/**
	 * Set the width and height of the tiled layers, if existant
	 */
	public void setTiledDimensions(int width, int height){
		tileMapWidth=width;
		tileMapHeight=height;
		maxX=width-Constants.SCREENWIDTH;
		maxY=height-Constants.SCREENHEIGHT;
	}
	/**
	 * Get the dimensions of the tiled map
	 * @return The dimensions of the tiled map
	 */
	public Vector2f getTiledDimensions(){
		return new Vector2f(tileMapWidth,tileMapHeight);
	}
	
	
	/**
	 * Update all the layers attached to this viewport
	 * @param delta
	 */
	public void update(int delta){
		// Set this viewport's position to track the set entity, if not null
		
		for (int i=0;i<layerList.size();i++){
			if (layerList.get(i).isActive()){
				layerList.get(i).update(delta);
			}
		}
	}
	
	public void render(Graphics g){
		if (trackMode == TRACK_MODE_CENTER){
			centerOn(entityToTrack);
		}
		if (trackMode == TRACK_MODE_FOLLOW){
			follow(entityToTrack);
		}
		// Translate the view according to this viewport's position
		g.translate(-x,-y);
		
		
		// define the screen mask based on the clipping rectangle
		/*
		MaskUtil.defineMask();
		g.fill(clip);
		MaskUtil.finishDefineMask();
		MaskUtil.drawOnMask();
		*/
		for (int i=0; i< layerList.size(); i++){
			if (layerList.get(i).isActive()){
				layerList.get(i).render(g);
				
				/*
				layerList.get(i).render(g, x, y, 
						x+(Constants.SCREENWIDTH), y+(Constants.SCREENWIDTH));
						*/
						
			}
		}
		
		//g.translate(x, y);
	}
	
	public void setPosition(int x, int y){
			this.x=x;
			this.y=y;
	}
	
	public Vector2f getPosition(){
		return new Vector2f(this.x,this.y);
	}
	
	/** Once this is called, if the entity is not set to null, the
	 * viewport will center on this entity every update cycle.
	 * @param e The Entity to track with this viewport
	 */
	public void trackEntity(Entity e, int trackMode){
		entityToTrack = e;
		this.trackMode=trackMode;
	}
	
	/**
	 * FIXME: This doesn't work as intended - needs to be fixed someday
	 * @param e Entity to follow
	 */
	public void follow(Entity e){
		// create a bounding rectangle around the current area to determine the
		// entity's position in relation to it
		Rectangle bounds = new Rectangle(x,y,width,height);
		
		// Check minX
		if (e.getBody().getPosition().getX() <= x+ (Constants.TILE_WIDTH*2)){
			if (x-(10) > minX){
				x -= 10;
			}else{
				x = minX;
			}
		}
		// check minY
		if (e.getBody().getPosition().getY() <= y+ (Constants.TILE_HEIGHT*2)){
			if (y-(10) > minY){
				y -= 10;
			}else{
				y = minY;
			}
		}
		//Check maxX
		if ((e.getBody().getPosition().getX() + e.getBody().getShape().getBounds().getWidth()) 
				>= bounds.getWidth() - (Constants.TILE_WIDTH*2)){
			if (x+(10) < maxX){
				x += 10;
			}else{
				x = maxX;
			}
		}
		//Check maxY
		if ((e.getBody().getPosition().getY() + e.getBody().getShape().getBounds().getHeight())  
				>= bounds.getHeight()- (Constants.TILE_HEIGHT*2)){
			if (y+(10) < maxY){
				y += 10;
			}else{
				y = maxY;
			}
		}
	}
	
	/**
	 * Centers the view on the center point of the specified entity, treated as a rectangle
	 * @param e
	 */
	public void centerOn(Entity e){
		centerOn(
				(int)e.getBody().getPosition().getX(),
				(int)e.getBody().getPosition().getY(),
				(int)e.getBody().getShape().getBounds().getWidth(),
				(int)e.getBody().getShape().getBounds().getHeight()
				);
	}
	
	/**
	 * Centers the view on the center point of the specified rectangle.
	 * 
	 * @param x x-position of rectangle
	 * @param y y-position of rectangle
	 * @param width rectangle width
	 * @param height rectangle height
	 */
	public void centerOn(int x, int y, int width, int height)
	{
		int xPos = (x + (width / 2) - (int)(clip.getWidth() / 2));
		int yPos = (y + (height / 2) - (int)(clip.getHeight() / 2));
		
		
		// Clamp screen scroll to limits of map
		if (xPos < minX){
			xPos = minX;
		}else if (xPos > maxX){
			xPos = maxX;
		}
		if (yPos < minY){
			yPos = minY;
		}else if (yPos > maxY){
			yPos = maxY;
		}
		
		
		setPosition (xPos, yPos);
	}
}
