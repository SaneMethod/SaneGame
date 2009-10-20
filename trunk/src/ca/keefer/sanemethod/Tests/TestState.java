package ca.keefer.sanemethod.Tests;

import java.util.ArrayList;

import org.newdawn.slick.GameContainer;
import org.newdawn.slick.Graphics;
import org.newdawn.slick.Image;
import org.newdawn.slick.SlickException;
import org.newdawn.slick.state.BasicGameState;
import org.newdawn.slick.state.StateBasedGame;
import org.newdawn.slick.util.ResourceLoader;

import ca.keefer.sanemethod.Constants;
import ca.keefer.sanemethod.Entity.SAT;
import ca.keefer.sanemethod.Entity.Sprite;
import ca.keefer.sanemethod.Interface.SaneSystem;
import ca.keefer.sanemethod.Interface.Text;
import ca.keefer.sanemethod.Interface.TextHandler;
import ca.keefer.sanemethod.Tools.TextXMLPullParser;

/**
 * This class is just a test-bed state for me to experiment in
 * @author Christopher Keefer
 *
 */
public class TestState extends BasicGameState {

	// Global variables
	int stateID=0;
	
	// Test variables
	SaneSystem saneSystem;
	Text thisText;
	ArrayList<Text> thisDialog;
	TextHandler tHandle;
	Sprite testSprite;
	Sprite tSprite2;
	SAT sat;
	
	public TestState(int stateID){
		this.stateID = stateID;
	}
	
	@Override
	public int getID() {
		// TODO Auto-generated method stub
		return stateID;
	}

	@Override
	public void init(GameContainer container, StateBasedGame game)
			throws SlickException {
		saneSystem = Constants.saneSystem;
		Constants.TEXT_SPEED = Constants.TEXT_SPEED_FAST;
					
		TextXMLPullParser testPull = new TextXMLPullParser(ResourceLoader.getResourceAsStream("res/Dialogs/testBook.xml"));
		thisDialog = testPull.processDialog();
		
		//tHandle = new TextHandler(thisDialog, 40, Text.BOTTOM, 740);
		
		// Oooh... testSprite!
		testSprite = new Sprite(10,500,new Image("/res/ball.png"));
		tSprite2 = new Sprite(200,500,new Image("/res/ball.png"));
		sat = new SAT();
		sat.addEntity(testSprite);
		sat.addEntity(tSprite2);
		
	}

	@Override
	public void render(GameContainer container, StateBasedGame game, Graphics g)
			throws SlickException {
		//g.setBackground(Color.decode("52326"));
		//tHandle.display(g);
		
		tSprite2.render(g);
		testSprite.render(g);
		sat.testForCollision(g);
		
		

	}

	@Override
	public void update(GameContainer container, StateBasedGame game, int delta)
			throws SlickException {
			
		//tHandle.update(delta);
		
		testSprite.update(delta);
		tSprite2.update(delta);

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
		// TODO: On key press, exit this state for now
		//System.exit(0);
		
		//tHandle.acceptInput(keyPressed);
		
		if (keyPressed == Constants.KEY_LEFT){
			testSprite.setMovingDir(true);
			testSprite.setMoving(true);
			
			tSprite2.setMovingDir(false);
			tSprite2.setMoving(true);
		}else if (keyPressed == Constants.KEY_RIGHT){
			testSprite.setMovingDir(false);
			testSprite.setMoving(true);
			
			tSprite2.setMovingDir(true);
			tSprite2.setMoving(true);
		}else if (keyPressed == Constants.KEY_CANCEL){
			testSprite.setJumping(true);
			testSprite.setVelY(45);
		}
	}
	
	public void keyReleased(int keyReleased, char keyChar){
		testSprite.setMoving(false);
		tSprite2.setMoving(false);
	}

}
