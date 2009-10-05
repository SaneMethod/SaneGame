package ca.keefer.sanemethod;

import ca.keefer.sanemethod.Interface.SaneSystem;

/**
 * This class controls the display of text on screen - including conversation between characters,
 * and whatever else may pop up, allowing for the abstraction of the process away from loading graphics
 * explicitly, displaying the text over them, etc. All such text is derived from XML files, for increased
 * seperation between content and form, and for easy update without mucking through code.
 * @author Christopher Keefer
 * @version 1.1
 *
 */
public class TextBox {

	SaneSystem saneSystem;
	
	// Constructor - requires SaneSystem for access to fonts and window graphics
	public TextBox (SaneSystem saneSystem){
		this.saneSystem = saneSystem;
	}
	
	
	/**
	 * readXML calls TextXMLPullParser to get us the specified text snippet
	 * @param input XML inputstream to read the information from
	 */
	public void draw(){
		
	}
}
