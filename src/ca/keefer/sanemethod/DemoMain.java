package ca.keefer.sanemethod;

import javax.swing.JOptionPane;

import org.newdawn.slick.SlickException;
/**
 * This class serves simply as the entrypoint of execution for the program
 * @author Christopher J Keefer
 * @version 1.1
 * 
 */
public class DemoMain {
	
	/**
	 * main method
	 * @param args String array of command-line arguments
	 */
	public static void main(String[] args){
		int runTime=14;
    	try{
    		runTime = Integer.parseInt(args[0]);
    	}catch(NumberFormatException e){
    		System.out.println("Argument must be an integer.");
    		System.exit(-1);
    	}
    	
    	// Call init to make a new game
    	DemoInit newGame = new DemoInit(runTime);
    	
    	// Start the game
    	try {
        	newGame.getContainer().start();
		} catch (SlickException e) {
			JOptionPane.showMessageDialog(null, "Error in DemoMain:"+e.getMessage(), 
					"Slick Exception", JOptionPane.ERROR_MESSAGE);
			e.printStackTrace();
			System.exit(-1);
		}
		
	}
}
