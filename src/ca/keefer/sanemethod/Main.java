package ca.keefer.sanemethod;

import javax.swing.JOptionPane;

import org.newdawn.slick.SlickException;
/**
 * This class serves simply as the entrypoint of execution for the program
 * @author Christopher J Keefer
 * @version 1.1
 * 
 */
public class Main {
	
	/**
	 * main method
	 * @param args String array of command-line arguments
	 */
	public static void main(String[] args){
		// Call init to make a new game
    	Init newGame = new Init();
    	
    	// Start the game
    	try {
        	newGame.getContainer().start();
		} catch (SlickException e) {
			JOptionPane.showMessageDialog(null, "Error in Main:"+e.getMessage(), 
					"Slick Exception", JOptionPane.ERROR_MESSAGE);
			e.printStackTrace();
			System.exit(-1);
		}
		
	}
}
