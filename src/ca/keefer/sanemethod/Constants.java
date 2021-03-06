package ca.keefer.sanemethod;

import net.phys2d.math.Vector2f;

import org.newdawn.slick.Input;

import ca.keefer.sanemethod.Interface.SaneSystem;
import ca.keefer.sanemethod.Interface.TextHandler;

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
	public static final int STATE_INTRO = 0;
	public static final int STATE_MAIN_MENU = 1;
	public static final int STATE_IN_GAME = 2;
	public static final int STATE_IN_GAME_MENU = 3;
	public static final int STATE_CONFIG_MENU = 4;
	public static final int STATE_SAVE_LOAD_MENU = 5;
	public static final int STATE_CREDITS = 6;
	public static final int STATE_TEST = 12;
	public static final int STATE_SHAPE_BUILDER = 13;
	public static final int STATE_DEMO = 14;
	public static final int STATE_TEXT_DEMO = 15;
	
	// Animation direction constants
	public static final int DIR_UP=0;
	public static final int DIR_DOWN=1;
	public static final int DIR_LEFT=2;
	public static final int DIR_RIGHT=3;
	
	
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
	public static int TILE_WIDTH=64;
	public static int TILE_HEIGHT=64;
	public static int NOT_PASSABLE=0;
	public static int PASSABLE=1;
	public static int PASSABLE_BY_SWIMMERS_OR_FLYERS=2;
	public static int PASSABLE_BY_FLYERS=3;
	
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
	public static int KEY_JUMP = Input.KEY_C;
	public static int KEY_HIDE_HUD = Input.KEY_H;
	public static int KEY_TOGGLE_GRAVITY = Input.KEY_G;
	public static int KEY_SUMMON_BALL = Input.KEY_S;
	public static int KEY_PUNCH = Input.KEY_Z;
	public static int KEY_KICK = Input.KEY_X;
	public static int KEY_PICK_UP = Input.KEY_V;
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
	
	// Text Display Speed Constants
	public static final int TEXT_SPEED_FAST = 10;
	public static final int TEXT_SPEED_MEDIUM = 50;
	public static final int TEXT_SPEED_SLOW = 100;
	public static int TEXT_SPEED = TEXT_SPEED_MEDIUM;
	
	// Physics System Constants
	public static float FRICTION = 0.045f;
	public static float ACCELERATION = 0.9f;
	public static float MAX_ACCELERATION = 6.5f;
	public static Vector2f GRAVITY = new Vector2f(0f,15f); // 0 pull on x axis, 10 pull down on y axis
	public static int ITERATIONS = 20; // Number of iterations the phys engine should go through per update cycle
	
	// Body Shape Constants
	public static int SHAPE_TYPE_BOX=0;
	public static int SHAPE_TYPE_CIRCLE=1;
	public static int SHAPE_TYPE_POLYGON=2;
	
	// Layer Constants
	public static int LAYER_ENTITY = 1;
	public static int LAYER_TILE = 2;
	public static int LAYER_PARTICLE = 3;
	public static int LAYER_BACKGROUND = 4;
	public static int LAYER_HUD = 5;
	public static int LAYER_EVENT = 6;
	
	// ObjectType Constants
	public static String OBJECT_SPRING = "Spring";
	public static String OBJECT_SWITCH = "Switch";
	public static String OBJECT_DOOR = "Door";
	public static String OBJECT_PLAYER = "Player";
	public static String OBJECT_BALL = "Ball";
	public static String OBJECT_CRATE = "Crate";
	public static String OBJECT_COIN = "Coin";
	public static String OBJECT_JMOOK = "jMook";
	public static String OBJECT_CMOOK = "cMook";
	public static String OBJECT_FALLING_BLOCK = "FallingBlock";
	public static String OBJECT_SPIKE = "Spike";
	
	// EventType Constants
	public static String EVENT_WAYPOINT = "Waypoint";
	public static String EVENT_EXIT = "Exit";
	public static String EVENT_TALKABLE = "Talkable";
	
	// SaneSystem for system-wide access
	public static SaneSystem saneSystem = new SaneSystem();
	
	// TextHandler for common access
	public static TextHandler textHandler;
}
