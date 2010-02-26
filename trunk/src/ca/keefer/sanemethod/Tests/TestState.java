package ca.keefer.sanemethod.Tests;

import java.io.IOException;
import java.util.ArrayList;

import org.newdawn.slick.Color;
import org.newdawn.slick.GameContainer;
import org.newdawn.slick.Graphics;
import org.newdawn.slick.Image;
import org.newdawn.slick.Input;
import org.newdawn.slick.Music;
import org.newdawn.slick.SlickException;
import org.newdawn.slick.SpriteSheet;
import org.newdawn.slick.geom.Line;
import org.newdawn.slick.geom.Path;
import org.newdawn.slick.geom.Polygon;
import org.newdawn.slick.geom.Rectangle;
import org.newdawn.slick.geom.Vector2f;
import org.newdawn.slick.particles.ConfigurableEmitter;
import org.newdawn.slick.particles.ParticleIO;
import org.newdawn.slick.particles.ParticleSystem;
import org.newdawn.slick.state.BasicGameState;
import org.newdawn.slick.state.StateBasedGame;
import org.newdawn.slick.util.Log;
import org.newdawn.slick.util.ResourceLoader;

import ca.keefer.sanemethod.Constants;
import ca.keefer.sanemethod.Entity.ClamMook;
import ca.keefer.sanemethod.Entity.Coin;
import ca.keefer.sanemethod.Entity.Crate;
import ca.keefer.sanemethod.Entity.Door;
import ca.keefer.sanemethod.Entity.Elevator;
import ca.keefer.sanemethod.Entity.JumpingMook;
import ca.keefer.sanemethod.Entity.Platformer;
import ca.keefer.sanemethod.Entity.Player;
import ca.keefer.sanemethod.Entity.Spring;
import ca.keefer.sanemethod.Entity.Switch;
import ca.keefer.sanemethod.Environment.BackgroundLayer;
import ca.keefer.sanemethod.Environment.MapObject;
import ca.keefer.sanemethod.Environment.ParticleLayer;
import ca.keefer.sanemethod.Environment.TiledEnvironment;
import ca.keefer.sanemethod.Environment.ViewPort;
import ca.keefer.sanemethod.Interface.SaneSystem;
import ca.keefer.sanemethod.Interface.Text;
import ca.keefer.sanemethod.Interface.TextHandler;
import ca.keefer.sanemethod.LevelBuilder.MapShape;
import ca.keefer.sanemethod.LevelBuilder.XMLShapePullParser;
import ca.keefer.sanemethod.Tools.LoadingListPullParser;
import ca.keefer.sanemethod.Tools.TSXGen;
import ca.keefer.sanemethod.Tools.TextXMLPullParser;

/**
 * This class is just a test-bed state for me to experiment in.
 * @author Christopher Keefer
 *
 */
public class TestState extends BasicGameState {

	// Global variables
	int stateID=-1;
	
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
	int mapOffset;
	
	TiledEnvironment environment;
	ViewPort viewPort;
	ArrayList<MapObject> loadingList;
	Music theMusic;
	
	ConfigurableEmitter conEmitter;
	ParticleSystem particleSystem;
	
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
		
		loadingList = new LoadingListPullParser(
				ResourceLoader.getResourceAsStream("res/loadingList.xml")).processLoadingList();
		mapOffset=0;
		
		if (loadingList.get(mapOffset).getShapeList() != null){
			XMLShapePullParser x = new XMLShapePullParser(ResourceLoader.getResourceAsStream(
					loadingList.get(mapOffset).getShapeList()));
			tileList = x.processXML();
		}else{
			tileList = null;
		}
		viewPort = new ViewPort(game);
		environment = new TiledEnvironment(loadingList.get(mapOffset).getMapFile(),tileList,viewPort);
		// check to see if we're resetting the player's position back to a waypoint
		if (testSprite != null){
			if (testSprite.getHitWaypoint()){
				float wx = testSprite.getWayX();
				float wy = testSprite.getWayY();
				testSprite = environment.getPlayer();
				testSprite.setPosition(wx, wy);
			}else{
				testSprite = environment.getPlayer();
			}
		}else{
			testSprite = environment.getPlayer();
		}
		theMusic = new Music(environment.getMusic(),true);
		theMusic.loop();
		viewPort.trackEntity(testSprite,ViewPort.TRACK_MODE_CENTER);
		
		/*
		
		XMLShapePullParser x = new XMLShapePullParser(ResourceLoader.getResourceAsStream("res/Tiles/SaneMap1.tmx.xml"));
		tileList = x.processXML();
		
		viewPort = new ViewPort(game);
		
		environment = new TiledEnvironment("res/Tiles/SaneMap1.tmx",tileList,viewPort);
				
		// Define an environment that creates only map-defined borders (as opposed to hand-drawn XML-defined shapes):
		//environment = new TiledEnvironment("res/Tiles/testMap3.tmx",null,viewPort);
	
		testSprite = environment.getPlayer();
		
		viewPort.trackEntity(testSprite,ViewPort.TRACK_MODE_CENTER);
		
		// ParticleLayer
		/*
		ParticleLayer pLayer = new ParticleLayer(15,true);
		int psRef = pLayer.addParticleSystem("res/Particle/GoingViral.xml", testSprite);
		Log.debug("psRef:"+psRef);
		viewPort.attachLayer(pLayer);
		*/
		
		//tHandle = new TextHandler(thisDialog, viewPort.getPosition().getX(), viewPort.getPosition().getY(), Text.BOTTOM, 740);
		
	}
	
	/** Reset the state of the current game map */
	public void reset(GameContainer container, StateBasedGame game){
		if (loadingList.get(mapOffset).getShapeList() != null){
			XMLShapePullParser x = new XMLShapePullParser(ResourceLoader.getResourceAsStream(
					loadingList.get(mapOffset).getShapeList()));
			tileList = x.processXML();
		}else{
			tileList = null;
		}
		Constants.GRAVITY = new net.phys2d.math.Vector2f(0f,15f);
		viewPort = new ViewPort(game);
		environment = new TiledEnvironment(loadingList.get(mapOffset).getMapFile(),tileList,viewPort);
		// check to see if we're resetting the player's position back to a waypoint
		if (testSprite != null){
			if (testSprite.getHitWaypoint()){
				float wx = testSprite.getWayX();
				float wy = testSprite.getWayY();
				testSprite = environment.getPlayer();
				testSprite.setPosition(wx, wy);
			}else{
				testSprite = environment.getPlayer();
			}
		}else{
			testSprite = environment.getPlayer();
		}
		if (theMusic.playing()){
			theMusic.stop();
		}
		try {
			theMusic = new Music(environment.getMusic(),true);
		} catch (SlickException e) {
			Log.debug("Error Loading Music in reset:"+e.getMessage());
		}
		theMusic.loop();
		viewPort.trackEntity(testSprite,ViewPort.TRACK_MODE_CENTER);
	}
	
	/** Select the next map via the mapOffset */
	public void setMapOffset(int offset){
		mapOffset = offset;
	}
	
	/** increment the map offset to go to the next map */
	public void nextMap(){
		if (mapOffset+1 < loadingList.size()){
			mapOffset++;
		}
	}
	
	/** decrement the map offset to go to the previous map */
	public void previousMap(){
		if (mapOffset-1 >= 0){
			mapOffset--;
		}else{
			mapOffset = 0;
		}
	}

	@Override
	public void render(GameContainer container, StateBasedGame game, Graphics g)
			throws SlickException {
		g.setBackground(Color.black);
		
		if (viewPort != null){
			//g.setColor(Color.white);
			viewPort.render(g);
			//environment.render(g);
			//environment.renderBounds(g);
			if (Constants.textHandler != null){
				g.translate(viewPort.getPosition().getX(), viewPort.getPosition().getY());
				Constants.textHandler.display(g);
				g.translate(-viewPort.getPosition().getX(), -viewPort.getPosition().getY());
			}
		}else{
			Color rg = Color.red;
			Color.red.a=0.5f;
			g.setColor(Color.red);
			g.fill(new Rectangle(-10,250,(Constants.SCREENWIDTH+10),100));
			Color.red.a = rg.a;
			Constants.saneSystem.getFonts().get("creditFont").drawString(
					(Constants.SCREENWIDTH/2)-Constants.saneSystem.getFonts().get("creditFont").getWidth("Loading Level")/2, 300, "Loading Level", Color.white);
		}

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
		
		if (viewPort != null){
		environment.update(delta);
		viewPort.update(delta);
		if (Constants.textHandler != null){
			Constants.textHandler.update(delta);
		}
			if (testSprite.resetRequested()){
				reset(container,game);
			}
			if (testSprite.isFinishedStage()){
				nextMap();
				// reset waypoint flag
				testSprite = null;
				reset(container,game);
			}
		}
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
		}else if (keyPressed == Input.KEY_3){
			Constants.GRAVITY = new net.phys2d.math.Vector2f(0,15f);
			environment.getWorld().setGravity(Constants.GRAVITY.getX(), Constants.GRAVITY.getY());
		}else if (keyPressed == Input.KEY_4){
			Constants.GRAVITY = new net.phys2d.math.Vector2f(0,-15f);
			environment.getWorld().setGravity(Constants.GRAVITY.getX(), Constants.GRAVITY.getY());
		}else if (keyPressed == Input.KEY_R){
			testSprite.setReset(true);
		}else if (keyPressed == Input.KEY_H){
			environment.toggleHudLayer();
		}else if (keyPressed == Input.KEY_M){
			testSprite.setCoins(5000);
		}
		
		//tHandle.acceptInput(keyPressed);
		
		testSprite.receiveKeyPress(keyPressed);
		if (Constants.textHandler != null){
			Constants.textHandler.acceptInput(keyPressed);
		}
	}
	
	public void keyReleased(int keyReleased, char keyChar){
		testSprite.receiveKeyRelease(keyReleased);
	}
	
	@Override
	public void enter(GameContainer container, StateBasedGame game){
		try {
			this.init(container,game);
		} catch (SlickException e) {
			Log.debug("Error initiating on entrance to state.");
		}
	}

}
