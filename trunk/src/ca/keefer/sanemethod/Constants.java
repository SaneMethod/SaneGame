package ca.keefer.sanemethod;

import org.newdawn.slick.Input;

/**
 * This class contains configurable constants for a number of internal functions,
 * including game states, control bindings, and tile/sprite sizes.
 * It is entirely static.
 * @author Christopher Keefer
 * @version 1.2
 *
 */
public class Constants {

	// Game Container Definitions
	public static int SCREENWIDTH = 800;
	public static int SCREENHEIGHT = 600;
	public static int FRAMERATE = 60;
	
	// Game State Definitions
	public static final int INTROSTATE = 0;
	public static final int MAINMENUSTATE = 1;
	public static final int INGAMESTATE = 2;
	public static final int INGAMEMENUSTATE = 3;
	public static final int CONFIGMENUSTATE = 4;
	public static final int SAVELOADMENUSTATE = 5;
	public static final int CREDITSTATE = 6;
	
	// Animation direction constants
	public static final int DIR_UP=0;
	public static final int DIR_DOWN=1;
	public static final int DIR_LEFT=2;
	public static final int DIR_RIGHT=3;
	
	// Map Property Definitions
	// Use these to search the property hashtables
	static String mapName = "name";
	static String mapType = "type";
	static String mapTypeStory = "story";
	static String mapTypeStrategy = "strategy";
	
	// Layer Property Definitions
	static String layerName = "name";
	static String layerType = "type";
	static String layerLevel = "level";
	static String layerTypeCollision = "collision";
	static String layerTypeCost = "cost";
	static String layerTypeObject = "object";
	static String layerLevelBelow = "below";
	static String layerLevelParity = "parity"; 
	/* 
	 * Note: Parity layers need to be drawn line-by-line
	 * So they can appear above/in front players (if the player is on a line behind them) or
	 * behind/below of players (if player is on a line in front of them) 
	 */
	static String layerLevelAbove = "above";
	
	// Object Property Definitions
	static String objectName = "name";
	static String objectType = "type";
	static String objectTypeEvent = "event";
	static String objectTypeEntity = "entity";
	static String objectTypeParticle = "particle";
	static String objectScript = "script"; // This object references the script name (Lua?)
	// Which contains the commands/data for the event
	static String objectXML = "XML"; // In the case of the object possessing an XML 
	// descriptor, such as with Particles
	
	// Tile Properties
	static String PASS = "passable";
	static String COST = "cost";
	static int TILEWIDTH=64;
	static int TILEHEIGHT=64;
	static int NOTPASSABLE=0;
	static int PASSABLE=1;
	static int PASSABLEBYSWIMMERSORFLYERS=2;
	static int PASSABLEBYFLYERS=3;
	
	// Sprite defaults
	static int SPRITE_UPDATE_SPEED=5; // Sprite moves SPRITE_SPEED_PIXELS_PER_STEP pixels toward its destination every 5 milliseconds
	static int SPRITE_SPEED_SLOW=4; // Sprite moves one pixel toward its destination every 20 milliseconds
	static int SPRITE_SPEED_MEDIUM=8; // Sprite moves one pixel toward its destination every 10 milliseconds
	static int SPRITE_SPEED_FAST=16; // Sprite moves one pixel toward its destination every 5 milliseconds
	static int SPRITE_SPEED_PIXELS_PER_STEP = SPRITE_SPEED_MEDIUM; // default
	static int WALK=1;
	static int SWIM=2;
	static int FLY=3;
	
	// Character Constants
	static final short CHARACTER_PLAYER=0;
	static final short CHARACTER_ENEMY=1;
	static final short CHARACTER_OTHER=2;
	
	// Key Defaults
	static int KEY_ACCEPT = Input.KEY_A;
	static int KEY_CANCEL = Input.KEY_C;
	static int KEY_INFO = Input.KEY_I;
	static int KEY_UP = Input.KEY_UP;
	static int KEY_DOWN = Input.KEY_DOWN;
	static int KEY_LEFT = Input.KEY_LEFT;
	static int KEY_RIGHT = Input.KEY_RIGHT;
}
