package ca.keefer.sanemethod.CodeExamples;

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
import ca.keefer.sanemethod.Interface.SaneSystem;
import ca.keefer.sanemethod.Interface.Text;
import ca.keefer.sanemethod.Tools.TextXMLPullParser;

/**
 * This code example illustrates two different ways to setup, update and draw text boxes to the screen
 * @author Christopher Keefer
 *
 */
public class TextBoxExampleState extends BasicGameState {

	// Global variables
	int stateID=0;
	
	// Test variables
	SaneSystem saneSystem;
	Text thisText;
	ArrayList<Text> thisDialog;
	short proceed;
	
	public TextBoxExampleState(int stateID){
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
		
		thisText = new Text("testText",saneSystem.getFonts().get("kingdomFont"),Color.decode("16777215"),true,
				"This is a really, truly, extraordinarily long string, especially considering " +
				"that this will be printed in the huge kindgom font.");
				
		/*
		TextXMLPullParser testPull = new TextXMLPullParser(ResourceLoader.getResourceAsStream("res/Dialogs/testBook.xml"));
		thisDialog = testPull.processDialog();
		*/
	}

	@Override
	public void render(GameContainer container, StateBasedGame game, Graphics g)
			throws SlickException {
		//g.setBackground(Color.decode("52326"));
		 proceed = thisText.drawTextBox();
		 if (proceed == Text.OPEN){
			 thisText.drawTextLetterByLetter();
		 }
		
		/*
		proceed = thisDialog.get(0).drawTextBox();
		if (proceed == Text.OPEN){
			thisDialog.get(0).drawTextLetterByLetter();
		}
		*/
	}

	@Override
	public void update(GameContainer container, StateBasedGame game, int delta)
			throws SlickException {
		// 
		if (thisText.isPrepared() != true){
			thisText.prepare(20,Text.TOP,750);
		}
		if (proceed == Text.OPEN){
			thisText.updateTextLetterByLetter(delta);
		}
		
		/*
		if (thisDialog.get(0).isPrepared() != true){
			thisDialog.get(0).prepare(50f, Text.BOTTOM, 750);
		}
			thisDialog.get(0).updateTextLetterByLetter(delta);
			*/

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
		thisText.switchBoxState();
		thisText.prepare(20, Text.BOTTOM, 750);
		//thisDialog.get(0).switchBoxState();
		//thisDialog.get(0).prepare(50f, Text.BOTTOM, 750);
	}

}
