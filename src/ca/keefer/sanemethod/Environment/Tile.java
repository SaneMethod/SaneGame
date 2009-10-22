package ca.keefer.sanemethod.Environment;

/**
 * This class is responsible for holding information for individual tiles loaded from a TilEd tmx
 * file, including tile properties, such as:<br />
 * 1 - Whether it should be considered a Wall tile, a floor tile, or other;<br />
 * 2 - Whther it is a ledge tile (to allow ledge gripping);<br />
 * 3 - The type of tile (types include: normal, speed ramp (velocity increase in x vector),
 * spring (velocity increase in y vector), pain (damage done to player), death (instant kill),
 * sand (slowed movement) or water (much slower movement, less gravity);<br />
 * 4 - Blocking (whether the tile is walkable or not);<br />
 * 5 - Cost (--Not defined or used for now--)
 * @author Christopher Keefer
 *
 */
public class Tile {

	int x; // x position
	int y; // y position
	int width;
	int height;
	int tileId;
	boolean isWall;
	boolean isFloor;
	boolean isLedge;
	boolean isWalkable;
	int tileType; // Type of tile, as defined by the constants below
	
	// Constants for tileType
	public static final int TILE_TYPE_NORMAL = 0;
	/** Increases velocityX in a particular direction */
	public static final int TILE_TYPE_SPEED_RAMP = 1;
	/** Increases velocityY in a particular direction */
	public static final int TILE_TYPE_SPRING = 2;
	/** Deals damage to character */
	public static final int TILE_TYPE_PAIN = 3;
	/** Instantly kills character */
	public static final int TILE_TYPE_DEATH = 4;
	/** Slows movement speed, allows slow falling through */
	public static final int TILE_TYPE_SAND = 5;
	/** Slows movement speed, decreased gravity */
	public static final int TILE_TYPE_WATER = 6;
	
	public Tile(int tileId, int x, int y, int width, int height, boolean isWall, boolean isFloor, boolean isLedge, int tileType, boolean isWalkable){
		this.x = x;
		this.y = y;
		this.width = width;
		this.height = height;
		this.isWall = isWall;
		this.isFloor = isFloor;
		this.isLedge = isLedge;
		this.tileType = tileType;
		this.isWalkable = isWalkable;
	}
	
	public int getTileId(){
		return tileId;
	}
	
	public int getX(){
		return x;
	}
	
	public int getY(){
		return y;
	}
	
	public int getWidth(){
		return width;
	}
	
	public int getHeight(){
		return height;
	}
	
	public boolean isWall(){
		return isWall;
	}
	
	public boolean isFloor(){
		return isFloor;
	}
	
	public boolean isLedge(){
		return isLedge;
	}
	
	public boolean isWalkable(){
		return isWalkable;
	}
	
	public int getTileType(){
		return tileType;
	}
	
}
