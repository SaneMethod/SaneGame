package ca.keefer.sanemethod;

import java.util.ArrayList;

import org.newdawn.slick.Color;
import org.newdawn.slick.GameContainer;
import org.newdawn.slick.Graphics;
import org.newdawn.slick.Input;
import org.newdawn.slick.SlickException;
import org.newdawn.slick.state.BasicGameState;
import org.newdawn.slick.state.StateBasedGame;
import org.newdawn.slick.state.transition.EmptyTransition;
import org.newdawn.slick.state.transition.FadeOutTransition;
import org.newdawn.slick.state.transition.HorizontalSplitTransition;
import org.newdawn.slick.state.transition.RotateTransition;
import org.newdawn.slick.state.transition.Transition;
import org.newdawn.slick.state.transition.VerticalSplitTransition;
import org.newdawn.slick.util.Log;
import org.newdawn.slick.util.ResourceLoader;

import ca.keefer.sanemethod.Entity.Player;
import ca.keefer.sanemethod.Entity.TitleEntity;
import ca.keefer.sanemethod.Environment.TiledEnvironment;
import ca.keefer.sanemethod.Environment.ViewPort;
import ca.keefer.sanemethod.Interface.SaneSystem;
import ca.keefer.sanemethod.Interface.Text;
import ca.keefer.sanemethod.Interface.TextHandler;
import ca.keefer.sanemethod.LevelBuilder.MapShape;
import ca.keefer.sanemethod.LevelBuilder.XMLShapePullParser;
import ca.keefer.sanemethod.Tests.TestState;
import ca.keefer.sanemethod.Tools.TextXMLPullParser;

/**
 * This class contains the resources and logic for the introductory sequence
 * @author Christopher Keefer
 * @version 1.1
 *
 */
public class IntroState extends BasicGameState {
	
	// Class Variables
	int stateID = -1;
	StateBasedGame thisGame;
	GameContainer thisContainer;
	
	SaneSystem systemTest;
	TiledEnvironment environment;
	ViewPort viewPort;
	
	TextHandler textHandler;
	
	Player thePlayer;
	
	float timer;
	boolean atMenu;
	String optionSelection;
	
	// Constructor receives stateID
	public IntroState(int stateID){
		this.stateID = stateID;
	}

	@Override
	// Returns the state ID of this game state
	public int getID() {
		return stateID;
	}

	@Override
	// Resources to be loaded or processes carried out upon initialization of this state
	public void init(GameContainer container, StateBasedGame game)
			throws SlickException {
		// Access System-wide resources through Constants.saneSystem
		// Set up environment
		viewPort = new ViewPort(game);
		XMLShapePullParser x = new XMLShapePullParser(ResourceLoader.getResourceAsStream("res/Tiles/titleMap.tmx.xml"));
		ArrayList<MapShape> tileList = x.processXML();
		environment = new TiledEnvironment("res/Tiles/titleMap.tmx",tileList,viewPort);
		thePlayer = environment.getPlayer();
		viewPort.trackEntity(thePlayer,ViewPort.TRACK_MODE_CENTER);
		
		craftIntroText();
		atMenu=false;
		
		// System reference variables
		// Useful for making alterations to setup when outside of the supplying methods
		thisGame=game;
		thisContainer=container;
	}

	@Override
	// Drawing tasks to be carried out by this game state
	public void render(GameContainer container, StateBasedGame game, Graphics g)
			throws SlickException {
		g.setBackground(Color.black);
		
		viewPort.render(g);
		textHandler.display(g);
	}

	@Override
	// Logic update done by this game state
	public void update(GameContainer container, StateBasedGame game, int delta)
			throws SlickException {
		environment.update(delta);
		viewPort.update(delta);
		
		textHandler.update(delta);
		if (!atMenu){
			
			titleSequence(delta);
			
		}else{
			if (optionSelection != null){
				if (optionSelection.equals("newGame")){
					this.leave(container, game);
				}else if (optionSelection.equals("loadGame")){
					
				}else if (optionSelection.equals("options")){
					
				}else if (optionSelection.equals("exit")){
					thisContainer.exit();
				}
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
	public void keyPressed(int keyPressed, char keyChar){
		if (keyPressed == Input.KEY_ESCAPE || keyPressed == Input.KEY_ENTER || keyPressed == Input.KEY_SPACE){
			if (!atMenu){
				TextXMLPullParser textPull = new TextXMLPullParser(ResourceLoader.getResourceAsStream("res/Dialogs/titleDialog.xml"));
				ArrayList<Text> thisDialog = textPull.processDialog();
				textPull = null;
				textHandler = new TextHandler(thisDialog, viewPort.getPosition().getX(), viewPort.getPosition().getY(), Text.MIDDLE, 750);
				atMenu=true;
			}else{
				textHandler.acceptInput(Constants.KEY_ACCEPT);
				optionSelection = textHandler.getOptionSelection();
			}
		}else{
			if (atMenu){
				textHandler.acceptInput(keyPressed);
				optionSelection = textHandler.getOptionSelection();
			}
			thePlayer.receiveKeyPress(keyPressed);
		}
	}
	@Override
	public void keyReleased(int keyReleased, char keyChar){
		thePlayer.receiveKeyRelease(keyReleased);
	}
	
	/**
	 * Responsible for displaying titleEntities (credits, acknowledgements, etc) over-top of the scene
	 * @param delta
	 */
	public void titleSequence(int delta){
		//titleEntity = new TitleEntity(350,280,1,"SANE",Constants.saneSystem.getFonts().get("kingdomFont"),Color.red);	
		if (textHandler.getByIndex(textHandler.getCurrent()).isFinished()){
			timer+=delta;
			if (timer > 1500){
				textHandler.acceptInput(Constants.KEY_ACCEPT);
				timer = 0;
			}
		}
	}
	
	/**
	 * Responsible for crafting the text objects that will be displayed during this intro sequence<br />
	 * Done in code to prevent malicious alteration of the xml file - I don't really want my name
	 * edited out of the intro sequence!
	 */
	public void craftIntroText(){
		ArrayList<Text> thisDialog  = new ArrayList<Text>();
		// gameName - SANE
		Text thisText = new Text("gameName",Constants.saneSystem.getFonts().get("creditFont"),
				Color.red,true,"SANE alpha v0.1","++");
		thisText.setSkippable(false);
		thisDialog.add(thisText);
		// designBy - Christopher J. Keefer
		thisText = new Text("designBy",Constants.saneSystem.getFonts().get("creditFont"),
				Color.white,true,"Game Design and Programming By Christopher J. Keefer.","++");
		thisText.setSkippable(false);
		thisDialog.add(thisText);
		// music: A Morning Song - SupraDarky
		thisText = new Text("musicMorningSongBy",Constants.saneSystem.getFonts().get("creditFont"),
				Color.red,true,"Music: 'A Morning Song' By SupraDarky.","++");
		thisText.setSkippable(false);
		thisDialog.add(thisText);
		// graphicsBy - Enterbrain
		thisText = new Text("graphicsBy",Constants.saneSystem.getFonts().get("creditFont"),
				Color.white,true,"Thanks to Enterbrain Inc. for the majority of free graphical resources used in this game.",
				"++");
		thisText.setSkippable(false);
		thisDialog.add(thisText);
		// music: Isotope - NightHawk22
		thisText = new Text("musicIsotopeBy",Constants.saneSystem.getFonts().get("creditFont"),
				Color.white,true,"Music: 'Isotope' By Nighthawk22.","++");
		thisDialog.add(thisText);
		// builtOn - Slick,phys2d and lwjgl
		thisText = new Text("builtOn",Constants.saneSystem.getFonts().get("creditFont"),
				Color.red,true,"This game built on the Slick, Phys2D and LWJGL libraries." +
						" Big thanks to Kevin Glass and the LWJGL team.",
				"++");
		thisDialog.add(thisText);
		// Music: AreYouSleepyYet - BlueSpiderEyes
		thisText = new Text("musicSleepyYetBy",Constants.saneSystem.getFonts().get("creditFont"),
				Color.white,true,"Music: 'Are You Sleepy Yet' By BlueSpiderEyes.","++");
		thisDialog.add(thisText);
		// thanksInAdvance - alpha testers
		thisText = new Text("thanksInAdvance",Constants.saneSystem.getFonts().get("creditFont"),
				Color.red,true,"Thanks in advance to my alpha testers - let's make this thing unbreakable (and earn" +
						" yourself a spot in the credits)!","++");
		thisDialog.add(thisText);
		// littleBroAndFamily
		thisText = new Text("littleBroAndFamily",Constants.saneSystem.getFonts().get("creditFont"),
				Color.white,true,"Hey Nathaniel, Mom, Dad. Was it worth the wait? Love ya.","++");
		thisDialog.add(thisText);
		// toJesus
		thisText = new Text("toJesus",Constants.saneSystem.getFonts().get("creditFont"),
				Color.red,true,"My Lord Jesus. I put my best effort into this. Hope it pleases you.","++");
		thisDialog.add(thisText);
		//legal
		thisText = new Text("legal",Constants.saneSystem.getFonts().get("creditFont"),
				Color.white,true,"This game and all rights, code, binaries, resources, and documentation not explicitly" +
						" reserved by another or in the public domain belongs to Christopher J. Keefer, all" +
						" rights reserved.","++");
		thisDialog.add(thisText);
		//thanksForWaiting
		thisText = new Text("thanksForWaiting",Constants.saneSystem.getFonts().get("creditFont"),
				Color.red,true,"Hey, thanks for sitting through the credits. Here's a hint: Press F10 on any map" +
						" to enable infinite jumping. Enjoy!","gameName");
		thisDialog.add(thisText);
		
		textHandler = new TextHandler(thisDialog, viewPort.getPosition().getX(), viewPort.getPosition().getY(), Text.MIDDLE, 740);
	}
	
	@Override
	public void leave(GameContainer container, StateBasedGame game){
		game.addState(new TestState(Constants.STATE_TEST));
		//game.getState(Constants.STATE_TEST).init(container, game);
		game.enterState(Constants.STATE_TEST, new EmptyTransition(), new HorizontalSplitTransition());
	}
	
	@Override
	public void enter(GameContainer container, StateBasedGame game){
		
	}

}
