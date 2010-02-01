package ca.keefer.sanemethod.Tests;

import java.util.ArrayList;

import org.newdawn.slick.Color;
import org.newdawn.slick.GameContainer;
import org.newdawn.slick.Graphics;
import org.newdawn.slick.Image;
import org.newdawn.slick.Input;
import org.newdawn.slick.SlickException;
import org.newdawn.slick.SpriteSheet;
import org.newdawn.slick.geom.Path;
import org.newdawn.slick.geom.Polygon;
import org.newdawn.slick.geom.Vector2f;
import org.newdawn.slick.state.BasicGameState;
import org.newdawn.slick.state.StateBasedGame;
import org.newdawn.slick.util.Log;
import org.newdawn.slick.util.ResourceLoader;

import ca.keefer.sanemethod.Constants;
import ca.keefer.sanemethod.Entity.Platformer;
import ca.keefer.sanemethod.Entity.Player;
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
		//testSprite = new Platformer(40,-30,Platformer.SHAPE_TYPE_CIRCLE,dimensions,5,0,0,new net.phys2d.math.Vector2f(30,50),true,1, new Image("/res/ball.png"));
		SpriteSheet spriteSheet = new SpriteSheet("res/Sprites/Player.png",96,96);
		testSprite = new Player(40,-30,Platformer.SHAPE_TYPE_CIRCLE,dimensions,5,0,0,new net.phys2d.math.Vector2f(30,50),true,1,spriteSheet);
		
		// TestSprite2
		dimensions = new net.phys2d.math.Vector2f[1];
		dimensions[0]= new net.phys2d.math.Vector2f();
		dimensions[0].x=48; dimensions[0].y=48;
		testSprite2 = new Platformer(300,-30,Platformer.SHAPE_TYPE_CIRCLE,dimensions,1,0,0,new net.phys2d.math.Vector2f(100,50),true,2, new Image("/res/ball.png"));
		
		
		XMLShapePullParser x = new XMLShapePullParser(ResourceLoader.getResourceAsStream("res/Tiles/testMap3.tmx.xml"));
		tileList = x.processXML();
		
		viewPort = new ViewPort(game);
		environment = new TiledEnvironment("res/Tiles/testMap3.tmx",tileList,viewPort);
		//environment = new TiledEnvironment("res/Tiles/testMap3.tmx",null,viewPort);
		environment.addEntity(testSprite);
		environment.addEntity(testSprite2);
		
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
