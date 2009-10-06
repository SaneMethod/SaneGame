package ca.keefer.sanemethod.Tools;

import org.newdawn.slick.AngelCodeFont;
import org.newdawn.slick.Color;
import org.newdawn.slick.geom.*;

import java.util.Scanner;

/**
 * This class defines a Text object, with the following properties:
 * Font The font to use to print this Text object
 * Colour The colour to print the string in
 * Text The string to print
 * @author Christopher Keefer
 *
 */
public class Text {
	
	String name;
	AngelCodeFont font;
	Color colour;
	String text;
	boolean boxed;
	final short BOTTOM = 0;
	final short MIDDLE = 1;
	final short TOP = 2;
	int height; // This is known only after drawing the text
	
	// These variables are used for the morphable text box
	Circle startShape;
	RoundedRectangle finishShape;
	MorphShape morphShape;
	
	// Minimal constructor
	public Text(AngelCodeFont font, String text){
		this.name="Unknown";
		this.font=font;
		colour=Color.black;
		this.text=text;
		this.boxed=true;
	}
	
	// Recommended Constructor
	public Text(String name, AngelCodeFont font, Color colour, String text){
		this.name = name;
		this.font=font;
		this.colour=colour;
		this.text=text;
		this.boxed=true;
	}
	
	// Full Constructor
	public Text(String name, AngelCodeFont font, Color colour, boolean boxed, String text){
		this.name=name;
		this.font=font;
		this.colour=colour;
		this.boxed=boxed;
		this.text=text;
	}
	
	// This method implements the Greedy Algorithm to determine word placement on a line
	public String wordWrap(int boxWidth, int xOffset){
		int textWidth=xOffset;
		String token="";
		String temp="";
		String product="";
		// tokenize and split the text
		Scanner thisScan = new Scanner(this.text);
		while(thisScan.hasNext()){
			token = thisScan.next()+" ";
			// Add the width of this token to the currently tested width
			textWidth += this.font.getWidth(token);
			// if this width is greater than the boxWidth, add a \n to the current temp string
			// and append it to the product string, clear the temp string and add this text to
			// it - if it isn't greater, than add this token to the temp string and move on
			if (textWidth > boxWidth){
				temp+="\n";
				product += temp;
				temp=token;
				textWidth=xOffset+this.font.getWidth(token);
			}else{
				temp+=token;
			}
		}
		product+=temp;
		return product;
	}
	
	// This method draws the text, wrapping it according to my implementation of the
	// Greedy Algorithm - not optimal, perhaps, but efficient - see method wordWrap, above
	// This requires that the text to print be split into words (searching for blank space
	// as the token) and each word be considered as to whether it will fit in the remaining
	// width or not - if not, it is placed on the next line, and the process continues
	
	// Also, depending on the boxed variable, a morphable shape may be drawn in and around the
	// the text at the specified x and y positions, plus a ten(?) pixel margin
	public void draw(float x, float y, int boxWidth){
		
		// Get word-wrapped version of text
		this.text= wordWrap(boxWidth, (int) x);
		
		if (boxed){
			
		}
		
		// draw this text to the screen at given position
		this.font.drawString(x, y, this.text, this.colour);
	}
	
}
