package ca.keefer.sanemethod.Interface;

import java.util.Hashtable;

import org.newdawn.slick.AngelCodeFont;
import org.newdawn.slick.Image;
import org.newdawn.slick.util.ResourceLoader;

import ca.keefer.sanemethod.Tools.MenuItemsXMLPullParser;

/**
 * This class is a container class for the various system values and resources required globally - so all the
 * fonts, standard windows, arrows, etc.
 * Uses ca.keefer.sanemethod.Tools.MenuItemsXMLPullParser to obtain said resources' location from an XML file
 * in the res/UI/ directory
 * @author Christopher Keefer
 * @version 1.1
 * @see ca.keefer.sanemethod.Tools.MenuItemsXMLPullParser
 *
 */
public class SaneSystem {
	
	/*
	 * Defined Resources:
	 * -Fonts-
	 * see Constants class for font definitions
	 */

	// Hashtable for the fonts
	Hashtable<String,AngelCodeFont> systemFonts; 
	
	// Hashtable for the menu system images
	Hashtable<String,Image> systemImages;
	
	
	// Constructor - calls the xml pull parser to load values into passed variables from
	// the xml file and locations
	public SaneSystem(){
		// Initialize Hashtables
		systemFonts = new Hashtable<String,AngelCodeFont>();
		systemImages = new Hashtable<String,Image>();
		// Load xml file as input stream, and pass it and all hashtables to the parser
		new MenuItemsXMLPullParser(
				ResourceLoader.getResourceAsStream("res/UI/menuDef.xml"),systemImages,systemFonts);
	}
	
	public Hashtable<String,AngelCodeFont> getFonts(){
		return systemFonts;
	}
	
	public Hashtable<String,Image> getImages(){
		return systemImages;
	}
}
