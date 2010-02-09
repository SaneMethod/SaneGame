package ca.keefer.sanemethod.CodeExamples;

import java.util.ArrayList;

import org.newdawn.slick.Color; 
import org.newdawn.slick.GameContainer;
import org.newdawn.slick.Graphics;
import org.newdawn.slick.Input;
import org.newdawn.slick.SlickException;
import org.newdawn.slick.state.BasicGameState;
import org.newdawn.slick.state.StateBasedGame;
import org.newdawn.slick.util.Log;
import org.newdawn.slick.util.ResourceLoader;

import ca.keefer.sanemethod.Constants;
import ca.keefer.sanemethod.Interface.SaneSystem;
import ca.keefer.sanemethod.Interface.Text;
import ca.keefer.sanemethod.Interface.TextHandler;
import ca.keefer.sanemethod.Tools.TextXMLPullParser;

/**
 * This code example illustrates two different ways to setup, update and draw text boxes to the screen
 * @author Christopher Keefer
 *
 */
public class TextBoxExampleState extends BasicGameState {

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
	
	public TextBoxExampleState(int stateID){
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
		
		tHandle = new TextHandler(thisDialog, 40,0, Text.BOTTOM, 740);
	}

	@Override
	public void render(GameContainer container, StateBasedGame game, Graphics g)
			throws SlickException {
		g.setBackground(Color.black);
		tHandle.display(g);
	}

	@Override
	public void update(GameContainer container, StateBasedGame game, int delta)
			throws SlickException {
		tHandle.update(delta);

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
		tHandle.acceptInput(keyPressed);
		if (keyPressed == Input.KEY_ESCAPE){
			container.exit();
		}
	}

}
