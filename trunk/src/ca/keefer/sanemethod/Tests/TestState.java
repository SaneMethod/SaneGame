package ca.keefer.sanemethod.Tests;

import org.newdawn.slick.Color;
import org.newdawn.slick.GameContainer;
import org.newdawn.slick.Graphics;
import org.newdawn.slick.SlickException;
import org.newdawn.slick.state.BasicGameState;
import org.newdawn.slick.state.StateBasedGame;

import ca.keefer.sanemethod.Interface.SaneSystem;
import ca.keefer.sanemethod.Tools.Text;

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
		saneSystem = new SaneSystem();
		 thisText = new Text(saneSystem.getFonts().get("interfaceFont"),Color.white,
				"This is a really, truly, extraordinarily long string, especially considering " +
				"that this will be printed in the huge kindgom font.");

	}

	@Override
	public void render(GameContainer container, StateBasedGame game, Graphics g)
			throws SlickException {
		// TODO Auto-generated method stub
		thisText.draw(10, 10, 700);

	}

	@Override
	public void update(GameContainer container, StateBasedGame game, int delta)
			throws SlickException {
		// TODO Auto-generated method stub

	}

}
