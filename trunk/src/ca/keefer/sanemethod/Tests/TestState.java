package ca.keefer.sanemethod.Tests;

import java.util.ArrayList;

import org.newdawn.slick.Color; 
import org.newdawn.slick.GameContainer;
import org.newdawn.slick.Graphics;
import org.newdawn.slick.SlickException;
import org.newdawn.slick.state.BasicGameState;
import org.newdawn.slick.state.StateBasedGame;
import org.newdawn.slick.util.Log;
import org.newdawn.slick.util.ResourceLoader;

import ca.keefer.sanemethod.Constants;
import ca.keefer.sanemethod.Interface.Option;
import ca.keefer.sanemethod.Interface.SaneSystem;
import ca.keefer.sanemethod.Interface.Text;
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
	short proceed;
	
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
		//Constants.TEXT_SPEED = Constants.TEXT_SPEED_SLOW;
		/*
		thisText = new Text("testText",saneSystem.getFonts().get("kingdomFont"),Color.decode("16777215"),false,
				"This is a really, truly, extraordinarily long string, especially considering " +
				"that this will be printed in the huge kindgom font.");
				*/
				
		
		TextXMLPullParser testPull = new TextXMLPullParser(ResourceLoader.getResourceAsStream("res/Dialogs/testBook.xml"));
		thisDialog = testPull.processDialog();
		
	}

	@Override
	public void render(GameContainer container, StateBasedGame game, Graphics g)
			throws SlickException {
		//g.setBackground(Color.decode("52326"));
		/* 
		proceed = thisText.drawTextBox();
		 if (proceed == Text.OPEN){
			 thisText.drawTextLetterByLetter();
		 }
		 */
		
		if (thisDialog.get(1).isPrepared()){
			proceed = thisDialog.get(1).drawTextBox();
			if (proceed == Text.OPEN){
				//thisDialog.get(1).drawTextLetterByLetter();
				Option yada = (Option) thisDialog.get(1);
				yada.draw(g,"39168");
			}
		}
		
	}

	@Override
	public void update(GameContainer container, StateBasedGame game, int delta)
			throws SlickException {
		/*
		if (thisText.isPrepared() != true){
			thisText.prepare(20,Text.TOP,750);
		}
		if (proceed == Text.OPEN){
			thisText.updateTextLetterByLetter(delta);
		}
		*/
		
		
		if (thisDialog.get(1).isPrepared() != true){
			thisDialog.get(1).prepare(50, Text.BOTTOM, 750);
		}
			//thisDialog.get(1).updateTextLetterByLetter(delta);
			

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
		//thisText.switchBoxState();
		//thisText.prepare(20, Text.BOTTOM, 750);
		//thisDialog.get(1).switchBoxState();
		//thisDialog.get(1).prepare(10, Text.BOTTOM, 750);
		
		if (keyPressed == Constants.KEY_UP){
			Option yada = (Option)thisDialog.get(1);
			yada.moveSelectRect(true);
		}else if (keyPressed == Constants.KEY_DOWN){
			Option yada = (Option)thisDialog.get(1);
			yada.moveSelectRect(false);
		}
	}

}
