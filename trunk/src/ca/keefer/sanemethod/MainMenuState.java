package ca.keefer.sanemethod;

import org.newdawn.slick.GameContainer;
import org.newdawn.slick.Graphics;
import org.newdawn.slick.SlickException;
import org.newdawn.slick.state.BasicGameState;
import org.newdawn.slick.state.StateBasedGame;

/**
 * This class contains the resources and logic for the introductory sequence
 * @author Christopher Keefer
 * @version 1.1
 *
 */
public class MainMenuState extends BasicGameState {
	
	// Class Variables
	int stateID = -1;
	
	// Constructor receives stateID
	public MainMenuState(int stateID){
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
		// TODO Auto-generated method stub
		
	}

	@Override
	// Drawing tasks to be carried out by this game state
	public void render(GameContainer container, StateBasedGame game, Graphics g)
			throws SlickException {
		// TODO Auto-generated method stub
		
	}

	@Override
	// Logic update done by this game state
	public void update(GameContainer container, StateBasedGame game, int delta)
			throws SlickException {
		// TODO Auto-generated method stub
		
	}

}
