package ca.keefer.sanemethod;

import org.newdawn.slick.Input;

/**
 * This class contains configurable constants for a number of internal functions,
 * including game states, control bindings, and tile/sprite sizes.
 * It is entirely static.
 * @author Christopher Keefer
 * @version 1.3
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
	public static String mapName = "name";
	public static String mapType = "type";
	public static String mapTypeStory = "story";
	public static String mapTypeStrategy = "strategy";
	
	// Layer Property Definitions
	public static String layerName = "name";
	public static String layerType = "type";
	public static String layerLevel = "level";
	public static String layerTypeCollision = "collision";
	public static String layerTypeCost = "cost";
	public static String layerTypeObject = "object";
	public static String layerLevelBelow = "below";
	public static String layerLevelParity = "parity"; 
	/* 
	 * Note: Parity layers need to be drawn line-by-line
	 * So they can appear above/in front players (if the player is on a line behind them) or
	 * behind/below of players (if player is on a line in front of them) 
	 */
	public static String layerLevelAbove = "above";
	
	// Object Property Definitions
	public static String objectName = "name";
	public static String objectType = "type";
	public static String objectTypeEvent = "event";
	public static String objectTypeEntity = "entity";
	public static String objectTypeParticle = "particle";
	public static String objectScript = "script"; // This object references the script name (Lua?)
	// Which contains the commands/data for the event
	public static String objectXML = "XML"; // In the case of the object possessing an XML 
	// descriptor, such as with Particles
	
	// Tile Properties
	public static String PASS = "passable";
	public static String COST = "cost";
	public static int TILEWIDTH=64;
	public static int TILEHEIGHT=64;
	public static int NOTPASSABLE=0;
	public static int PASSABLE=1;
	public static int PASSABLEBYSWIMMERSORFLYERS=2;
	public static int PASSABLEBYFLYERS=3;
	
	// Sprite defaults
	public static int SPRITE_UPDATE_SPEED=5; // Sprite moves SPRITE_SPEED_PIXELS_PER_STEP pixels toward its destination every 5 milliseconds
	public static int SPRITE_SPEED_SLOW=4; // Sprite moves one pixel toward its destination every 20 milliseconds
	public static int SPRITE_SPEED_MEDIUM=8; // Sprite moves one pixel toward its destination every 10 milliseconds
	public static int SPRITE_SPEED_FAST=16; // Sprite moves one pixel toward its destination every 5 milliseconds
	public static int SPRITE_SPEED_PIXELS_PER_STEP = SPRITE_SPEED_MEDIUM; // default
	public static int WALK=1;
	public static int SWIM=2;
	public static int FLY=3;
	
	// Character Constants
	public static final short CHARACTER_PLAYER=0;
	public static final short CHARACTER_ENEMY=1;
	public static final short CHARACTER_OTHER=2;
	
	// Key Defaults
	public static int KEY_ACCEPT = Input.KEY_A;
	public static int KEY_CANCEL = Input.KEY_C;
	public static int KEY_INFO = Input.KEY_I;
	public static int KEY_UP = Input.KEY_UP;
	public static int KEY_DOWN = Input.KEY_DOWN;
	public static int KEY_LEFT = Input.KEY_LEFT;
	public static int KEY_RIGHT = Input.KEY_RIGHT;
	
	// Font Constants
	public static final String FONT_INTERFACE = "interfaceFont";
	public static final String FONT_DATA = "dataFont";
	public static final String FONT_STANDARD = "standardFont";
	public static final String FONT_SCARY = "scaryFont";
	public static final String FONT_KINGDOM = "kingdomFont";
	public static final String FONT_ELVEN = "elvenFont";
	
	// Font Line Width constants - based on width of capital M
	public static final int FONT_INTERFACE_MAX_WIDTH = 48; // 16 pixels indent either side
	public static final int FONT_DATA_MAX_WIDTH = 30; // 25 pixels AA
	public static final int FONT_STANDARD_MAX_WIDTH = 59; //Approx. 13 pixels AA
	public static final int FONT_SCARY_MAX_WIDTH = 45; //Approx. 17 pixels AA
	public static final int FONT_KINGDOM_MAX_WIDTH = 12; //Approx. 63 pixels AA
	public static final int FONT_EVLEN_MAX_WIDTH = 130; //Approx. 6 pixels AA
	
}
