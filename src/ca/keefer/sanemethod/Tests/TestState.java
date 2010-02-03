package ca.keefer.sanemethod.Tests;

import java.util.ArrayList;

import org.newdawn.slick.Color;
import org.newdawn.slick.GameContainer;
import org.newdawn.slick.Graphics;
import org.newdawn.slick.Image;
import org.newdawn.slick.Input;
import org.newdawn.slick.SlickException;
import org.newdawn.slick.SpriteSheet;
import org.newdawn.slick.geom.Line;
import org.newdawn.slick.geom.Path;
import org.newdawn.slick.geom.Polygon;
import org.newdawn.slick.geom.Vector2f;
import org.newdawn.slick.state.BasicGameState;
import org.newdawn.slick.state.StateBasedGame;
import org.newdawn.slick.util.Log;
import org.newdawn.slick.util.ResourceLoader;

import ca.keefer.sanemethod.Constants;
import ca.keefer.sanemethod.Entity.Elevator;
import ca.keefer.sanemethod.Entity.Platformer;
import ca.keefer.sanemethod.Entity.Player;
import ca.keefer.sanemethod.Entity.Spring;
import ca.keefer.sanemethod.Entity.Switch;
import ca.keefer.sanemethod.Environment.TiledEnvironment;
import ca.keefer.sanemethod.Environment.ViewPort;
import ca.keefer.sanemethod.Interface.SaneSystem;
import ca.keefer.sanemethod.Interface.Text;
import ca.keefer.sanemethod.Interface.TextHandler;
import ca.keefer.sanemethod.LevelBuilder.MapShape;
import ca.keefer.sanemethod.LevelBuilder.XMLShapePullParser;
import ca.keefer.sanemethod.Tools.TSXGen;
import ca.keefer.sanemethod.Tools.TextXMLPullParser;

/**
 * This class is just a test-bed state for me to experiment in.
 * @author Christopher Keefer
 *
 */
public class TestState extends BasicGameState {

	// Global variables
	int stateID=0;
	
	// Self-reference
	GameContainer container;
	StateBasedGame game;
	
	// Test variables
	SaneSystem saneSystem;
	Text thisText;
	ArrayList<Text> thisDialog;
	TextHandler tHandle;
	Player testSprite;
	Platformer testSprite2;
	Player tSprite2;
	ArrayList<MapShape> tileList;
	
	TiledEnvironment environment;
	ViewPort viewPort;
	
	int xT=0;
	int yT=0;
	boolean goRight = true;
	boolean goDown = true;
	
	
	public TestState(int stateID){
		this.stateID = stateID;
	}
	
	@Override
	public int getID() {
		return stateID;
	}

	@Override
	public void init(GameContainer container, StateBasedGame game)
			throws SlickException {
		this.container = container;
		this.game = game;
		
		saneSystem = Constants.saneSystem;
		Constants.TEXT_SPEED = Constants.TEXT_SPEED_FAST;
					
		TextXMLPullParser testPull = new TextXMLPullParser(ResourceLoader.getResourceAsStream("res/Dialogs/testBook.xml"));
		thisDialog = testPull.processDialog();
		testPull = null;
		
		//tHandle = new TextHandler(thisDialog, 40, Text.BOTTOM, 740);
		
		// Oooh... testSprite!
		net.phys2d.math.Vector2f[] dimensions = new net.phys2d.math.Vector2f[1];
		dimensions[0]= new net.phys2d.math.Vector2f();
		dimensions[0].x=50; dimensions[0].y=64;
		SpriteSheet spriteSheet = new SpriteSheet("res/Sprites/Player.png",96,96);
		testSprite = new Player(540,-30,Constants.SHAPE_TYPE_CIRCLE,dimensions,5,0,0,new net.phys2d.math.Vector2f(30,50),true,4,spriteSheet);
		//testSprite.getBody().setRotation(-90);
		// TestSprite2
		dimensions = new net.phys2d.math.Vector2f[1];
		dimensions[0]= new net.phys2d.math.Vector2f();
		dimensions[0].x=48; dimensions[0].y=48;
		testSprite2 = new Platformer(300,-30,Constants.SHAPE_TYPE_CIRCLE,dimensions,1,0,0,new net.phys2d.math.Vector2f(100,50),false,3, new Image("/res/ball.png"));
		Platformer testSprite3 = new Platformer(400,-30,Constants.SHAPE_TYPE_CIRCLE,dimensions,1,0,0,new net.phys2d.math.Vector2f(100,50),true,2, new Image("/res/ball.png"));
		
		dimensions[0].x=64;
		Spring testSpring = new Spring(500,400,Constants.SHAPE_TYPE_CIRCLE,dimensions,10,0,0,1,1000,new SpriteSheet("res/Sprites/Jellyfish.png",128,128));
		
		dimensions = new net.phys2d.math.Vector2f[4];
		dimensions[0] = new net.phys2d.math.Vector2f();
		dimensions[1] = new net.phys2d.math.Vector2f();
		dimensions[2] = new net.phys2d.math.Vector2f();
		dimensions[3] = new net.phys2d.math.Vector2f();
		dimensions[0].x=0; dimensions[0].y=0;
		dimensions[1].x=0; dimensions[1].y=64;
		dimensions[2].x=64; dimensions[2].y=64;
		dimensions[3].x=64; dimensions[0].y=0;
		Switch testSwitch = new Switch(200,370,Constants.SHAPE_TYPE_POLYGON,dimensions,10,0,0,1,Switch.UP,false,new SpriteSheet("res/Tiles/Blocks.png",64,64));
		
		dimensions = new net.phys2d.math.Vector2f[4];
		dimensions[0] = new net.phys2d.math.Vector2f();
		dimensions[1] = new net.phys2d.math.Vector2f();
		dimensions[2] = new net.phys2d.math.Vector2f();
		dimensions[3] = new net.phys2d.math.Vector2f();
		dimensions[0].x=0; dimensions[0].y=0;
		dimensions[1].x=0; dimensions[1].y=60;
		dimensions[2].x=90; dimensions[2].y=60;
		dimensions[3].x=90; dimensions[0].y=0;
		Elevator testElevator = new Elevator (600f,200f,Constants.SHAPE_TYPE_POLYGON,dimensions,100f,0f,0f,1,Elevator.UP,true,100,new Line(600,200,600,-100),new net.phys2d.math.Vector2f(50,100),new SpriteSheet("res/Sprites/Elevator.png",136,192));
		
		XMLShapePullParser x = new XMLShapePullParser(ResourceLoader.getResourceAsStream("res/Tiles/testMap3.tmx.xml"));
		tileList = x.processXML();
		
		viewPort = new ViewPort(game);
		environment = new TiledEnvironment("res/Tiles/testMap3.tmx",tileList,viewPort);
		//environment = new TiledEnvironment("res/Tiles/testMap3.tmx",null,viewPort);
		environment.addEntity(testSprite);
		environment.addEntity(testSprite2);
		environment.addEntity(testSprite3);
		environment.addEntity(testSpring);
		environment.addEntity(testSwitch);
		environment.addEntity(testElevator);
		
		viewPort.trackEntity(testSprite,ViewPort.TRACK_MODE_CENTER);
		// Mwa ha ha... XMLShapePullParser...
		/*
		XMLShapePullParser x = new XMLShapePullParser(ResourceLoader.getResourceAsStream("res/Tiles/tilesheettest.png.xml"),
				new SpriteSheet("res/Tiles/TileSheetTest.png",64,64));
		tileList = x.processXML();
			
		
		
		thisBridge = new TiledEnvironment("res/Tiles/testMap.tmx",tileList,true);
		
		//TSXGen.makeTSX(0, 1023, "Tiles", 64, 64);
		*/
		
		
	}

	@Override
	public void render(GameContainer container, StateBasedGame game, Graphics g)
			throws SlickException {
		g.setBackground(Color.black);
		//tHandle.display(g);
		
		//Entity rendering is now handled by the environment
		//testSprite.render(g);
		
		viewPort.render(g);
		
		//environment.render(g);
		environment.renderBounds(g);
		
		
		

	}

	@Override
	public void update(GameContainer container, StateBasedGame game, int delta)
			throws SlickException {
			
		//tHandle.update(delta);
		//viewPort.centerOn(testSprite);
		
		/*
		if (xT > 400){
			goRight = false;
		}else if (xT < 0){
			goRight = true;
		}
		if (yT > 500){
			goDown = false;
		}else if (yT < 0){
			goDown = true;
		}
		if (goRight){
			xT +=5;
		}else{
			xT -=5;
		}
		if (goDown){
			yT +=5;
		}else{
			yT -=5;
		}
		viewPort.setPosition(xT, yT);
		*/
		
		viewPort.update(delta);
		environment.update(delta);

	}
	
	@Override
	// Allows this state to accept input as though it were an instance of GameController
	public boolean isAcceptingInput(){
		return true;
	}
	
	@Override
	// Controls key press response
	/**
	 * @param keypressed ascii integer
	 * @param keyChar ascii char
	 */
	public void keyPressed(int keyPressed, char keyChar){
		if (keyPressed == Input.KEY_ESCAPE){
			this.container.exit();
		}else if (keyPressed == Input.KEY_1){
			viewPort.trackEntity(testSprite,ViewPort.TRACK_MODE_CENTER);
		}else if (keyPressed == Input.KEY_2){
			viewPort.trackEntity(testSprite2,ViewPort.TRACK_MODE_CENTER);
		}
		
		//tHandle.acceptInput(keyPressed);
		
		testSprite.receiveKeyPress(keyPressed);
	}
	
	public void keyReleased(int keyReleased, char keyChar){
		testSprite.receiveKeyRelease(keyReleased);
	}

}
