package ca.keefer.sanemethod;

import org.newdawn.slick.*;
import org.newdawn.slick.state.StateBasedGame;
import org.newdawn.slick.util.Log;

import ca.keefer.sanemethod.LevelBuilder.TileShapeBuilder;
import ca.keefer.sanemethod.Tests.TestState;

/**
 * This class instantiates an override of the StateBasedGame class, and initializes the various
 * game states.
 * @author Christopher Keefer
 * @version 1.1
 * @see org.newdawn.StateBasedGame
 */
public class Init extends StateBasedGame {
	
	// Container object for the game
	private AppGameContainer thisGameContainer;
	
	public Init(){
		// create the StateBasedGame and assign it to a gamecontainer
		super("Sane Method v0.1");
		
		try {
			// Set default width/height to 800x600 and fullscreen
			thisGameContainer = new AppGameContainer(this,800,600,false);
			// Make sure the game logic doesn't update less than 60 times per second
			thisGameContainer.setMinimumLogicUpdateInterval(1000/60);
			//thisGameContainer.setMaximumLogicUpdateInterval(1000/60);
			thisGameContainer.setTargetFrameRate(60);
			//Set console output to verbose to help diagnose problems
			thisGameContainer.setVerbose(true);
			
			
			// Add states to this game container
			this.addState(new MainMenuState(Constants.STATE_MAIN_MENU));
			this.addState(new IntroState(Constants.STATE_INTRO));
			this.addState(new TileShapeBuilder(Constants.STATE_SHAPE_BUILDER));
			//this.addState(new TestState(Constants.STATE_TEST));
			
			// Enter first game state and begin render/update loop
			this.enterState(Constants.STATE_SHAPE_BUILDER);
			
			
		}catch (SlickException e){
			Log.error("Error in Init:"+e.getMessage());
			System.exit(-1);
		}
		
	}
	
	public StateBasedGame getThisGame(){
		return this;
	}
	
	public AppGameContainer getContainer(){
		return thisGameContainer;
	}
	
	@Override
	// Call the init routines of the various states of the game
	public void initStatesList(GameContainer container) throws SlickException {
		// Called when the entering the first game state
		// Use to call any procedures needed at that time
		
	}
	
}
