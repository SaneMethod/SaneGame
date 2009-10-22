package ca.keefer.sanemethod.Environment;

import java.util.ArrayList;
import java.util.Properties;

import org.newdawn.slick.SlickException;
import org.newdawn.slick.tiled.TiledMap;
import org.newdawn.slick.util.Log;
import org.newdawn.slick.util.pathfinding.PathFindingContext;
import org.newdawn.slick.util.pathfinding.TileBasedMap;

import ca.keefer.sanemethod.Constants;
import ca.keefer.sanemethod.Entity.Entity;
import ca.keefer.sanemethod.Entity.Player;
import ca.keefer.sanemethod.Tools.Utility;

/**
 * This class loads a map created with TilEd, and puts together a blocking grid
 * (TODO: and a cost grid?)
 * and handles retrieving all desired properties from the map and its layers
 * (TODO: It also handles the assignation of placed Entities?)
 * @author Christopher Keefer
 *
 */
public class TiledHandler extends TiledMap implements TileBasedMap{
	
	// Global Variables for this Map
	/** Properties hashmap for this map */
	Properties mapProperties;
	/** Properties hashmap for a layer */
	Properties layerProperties;
	/** Properties arraylist for the layer hashmaps */
	ArrayList<Properties> layerPropertiesList;
	/** 2d array of Tile objects - [x][y] - all are assumed to be on layer 1 (the second layer) */
	Tile[][] tiles;
	/** 2d blocking grid for the map */
	int[][] blockGrid;
	
	public TiledHandler(String ref, boolean loadTileSets) throws SlickException{
		super(ref,loadTileSets);
		Log.info("Tiled Map Parsing Begun:"+Utility.getDateTime());
		// Init blockGrid to the size of this map in tiles [WidthInTiles][HeightInTiles] +1 for safety
		blockGrid = new int[this.getWidth()+1][this.getHeight()+1];
		// Get Map Properties
		mapProperties = new Properties();
		/* 
		 * Get the properties of name of map, useful for debugging, and type of map,
		 * useful for determining how to deal with this map in terms of physics handling, etc.
		 * Also get background image or overlay(s) (if any), and whether and how fast they
		 * should move relative to the 'camera', and if they should be scaled, clamped, etc.
		 */
		mapProperties.setProperty("name", this.getMapProperty("name","null"));
		mapProperties.setProperty("type", this.getMapProperty("type","null"));
		mapProperties.setProperty("backgroundImage", this.getMapProperty("backgroundImage","null"));
		if (!(mapProperties.get("backgroundImage").equals("null"))){
			mapProperties.setProperty("backgroundWidth", this.getMapProperty("backgroundWidth",null));
			mapProperties.setProperty("backgroundHeight", this.getMapProperty("backgroundHeight",null));
			mapProperties.setProperty("backgroundScale", this.getMapProperty("backgroundScale",null));
			mapProperties.setProperty("backgroundScroll", this.getMapProperty("backgroundScroll",null));
			mapProperties.setProperty("backgroundClamp", this.getMapProperty("backgroundClamp",null));
		}
		for (int i=0; i<5; i++){
			// define a loop of five iterations to get up to five overlay layers - any more
			// and we risk compromising graphical performance
			// Starts at index 0, ends at index 4
			mapProperties.setProperty("Overlay"+i, this.getMapProperty("Overlay"+i,"null"));
			if (!(mapProperties.get("Overlay"+i).equals("null"))){
				mapProperties.setProperty("Overlay"+i+"Scale", this.getMapProperty("Overlay"+i+"Scale", null));
				mapProperties.setProperty("Overlay"+i+"ScrollX", this.getMapProperty("Overlay"+i+"ScrollX", null));
				mapProperties.setProperty("Overlay"+i+"ScrollY", this.getMapProperty("Overlay"+i+"ScrollY", null));
				mapProperties.setProperty("Overlay"+i+"Parallax", this.getMapProperty("Overlay"+i+"Parallax", null));
			}
		}
		
		
		// Get Properties for each layer and store them in an appropriately numbered part
		// of the ArrayList<Properties> (ie. Layer 0 goes in layerPropertiesList[0])
		layerPropertiesList = new ArrayList<Properties>();
		int numberOfLayers = this.getLayerCount();
		for (int i=0;i < numberOfLayers; i++){
			layerProperties = new Properties();
			layerProperties.setProperty(Constants.layerName, 
					this.getLayerProperty(i, Constants.layerName, "Null"));
			layerProperties.setProperty(Constants.layerLevel, 
					this.getLayerProperty(i, Constants.layerLevel, Constants.layerLevelBelow));
			layerProperties.setProperty(Constants.layerType, 
					this.getLayerProperty(i, Constants.layerType, "Null"));
			layerPropertiesList.add(layerProperties);
		}
		assignBlockingFromCollisionLayer();
		
		tiles = new Tile[this.getWidth()][this.getHeight()];
		// Get tile properties and store in Tile objects
		// NOTE: we're only interested in layer 1 for now
			for (int x=0;x<this.getWidth();x++){
				for (int y=0;y<this.getHeight();y++){
					int tileId = this.getTileId(x, y, 1);
					boolean isWall = Boolean.parseBoolean(this.getTileProperty(tileId, "isWall", "false"));
					boolean isFloor = Boolean.parseBoolean(this.getTileProperty(tileId, "isFloor", "false"));
					boolean isLedge = Boolean.parseBoolean(this.getTileProperty(tileId, "isLedge", "false"));
					boolean isWalkable = Boolean.parseBoolean(this.getTileProperty(tileId, "isWalkable", "true"));
					int tileType = Integer.parseInt(this.getTileProperty(tileId, "tileType", "0"));
					Tile thisTile = new Tile(tileId,x,y,this.getTileWidth(),this.getTileHeight(), isWall, isFloor,
							isLedge, tileType, isWalkable);
					tiles[x][y] = thisTile;
				}
			}
		
		Log.info("Tiled Map parsing Finished:"+Utility.getDateTime());
		
	}
	
	// Assign the blocking 2d array mapGrid from the collision layer
	public void assignBlockingFromCollisionLayer(){
		for (int i=0; i< layerPropertiesList.size();i++){
			if (layerPropertiesList.get(i).getProperty(Constants.layerType).equals(Constants.layerTypeCollision)){
				for (int x=0; x < this.getWidth();x++){
					for (int y=0; y < this.getHeight();y++){
						int passable;
						// Convert property from tile on collision layer
						// to blocking property in mapGrid array
						// If conversion fails, mark the tile as
						// Passable (1)
						// Possible results are stored in the Constants class
						try{
						passable=Integer.parseInt(this.getTileProperty(
								this.getTileId(x, y, i), Constants.PASS, "1"));
						}catch (NumberFormatException e){
							passable = 1;
						}
						blockGrid[x][y] = passable;
						// Debug Output
						//Log.debug("Map Grid x:"+x+" y:"+y+" set to:"+mapGrid[x][y]);
					}
				}
			}
		}
	}
	
	public Properties getMapProperties(){
		return mapProperties;
	}
	
	public String getLayerNameByIndex(int index){
		if (index < layerPropertiesList.size()){
			return layerPropertiesList.get(index).getProperty(Constants.layerName);
		}
		return null;
	}
	
	public int getLayerIndexByName(String name){
		for (int i=0;i < layerPropertiesList.size();i++){
			if (layerPropertiesList.get(i).getProperty(Constants.layerName).equalsIgnoreCase(name)){
				return i;
			}
		}
		return -1;
	}
	
	public boolean collisionCheck(Player e, boolean direction, float velocity){
		/* 
		 * Get the player's direction, x and y position, the bounds of its displayed image
		 * and its current velocity, and use these to determine if, in the next update, it would
		 * collide with a non-walkable tile. If so, move it to the position immediately adjacent to
		 * that tile, and kill its velocity in that direction.
		 */
		if (direction == Player.DIR_LEFT){
			int tx = (int)(e.getBoundingBox().getMaxY()+e.getBoundingBox().getY());
			int ty = (int)(e.getBoundingBox().getMaxX()+e.getBoundingBox().getX()+velocity);
			if (blocked(null,tx,ty)){
				e.setX(tiles[tx][ty].getX()-1);
				return false;
			}
		}
		return true;
	}
	
	@Override
	public boolean blocked(PathFindingContext context, int tx, int ty){
		if (blockGrid[tx][ty]==Constants.NOT_PASSABLE){
			return true;
		}
		return false;
	}

	@Override
	public float getCost(PathFindingContext context, int tx, int ty) {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public int getHeightInTiles() {
		return this.getHeight();
	}

	@Override
	public int getWidthInTiles() {
		return this.getWidth();
	}

	@Override
	public void pathFinderVisited(int x, int y) {
		// Use for debugging purposes
		// Log.debug("VisitedX:"+x+" VisitedY:"+y);
		
	}

}
