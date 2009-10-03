package ca.keefer.sanemethod;

import javax.swing.JOptionPane;

import org.newdawn.slick.*;
import org.newdawn.slick.state.StateBasedGame;

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
			
			//Init Game States
			initStatesList(thisGameContainer);
			this.enterState(Constants.INTROSTATE);
			
			
		}catch (SlickException e){
			JOptionPane.showMessageDialog(null, "Error in Init:"+e.getMessage(), 
					"Slick Exception", JOptionPane.ERROR_MESSAGE);
			e.printStackTrace();
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
		this.addState(new MainMenuState(Constants.MAINMENUSTATE));
		this.addState(new IntroState(Constants.INTROSTATE));
	}
	
}
