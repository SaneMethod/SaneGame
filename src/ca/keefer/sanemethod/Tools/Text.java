package ca.keefer.sanemethod.Tools;

import org.newdawn.slick.AngelCodeFont;
import org.newdawn.slick.Color;
import org.newdawn.slick.util.Log;

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
	
	AngelCodeFont font;
	Color colour;
	String text;
	int height; // This is known only after drawing the text
	
	// Minimal constructor
	public Text(AngelCodeFont font, String text){
		this.font=font;
		colour=Color.black;
		this.text=text;
	}
	
	// Recommended Constructor
	public Text(AngelCodeFont font, Color colour, String text){
		this.font=font;
		this.colour=colour;
		this.text=text;
	}
	
	public String wordWrap(int boxWidth){
		int textWidth=0;
		String token="";
		String temp="";
		String product="";
		// tokenize and split the text
		Scanner thisScan = new Scanner(this.text);
		while(thisScan.hasNext()){
			token = thisScan.next()+" ";
			//Log.info("Token:"+token);
			// Add the width of this token to the currently tested width
			textWidth += this.font.getWidth(token);
			//Log.info("Width:"+textWidth);
			// if this width is greater than the boxWidth, add a \n to the current temp string
			// and append it to the product string, clear the temp string and add this text to
			// it - if it isn't greater, than add this token to the temp string and move on
			if (textWidth > boxWidth){
				temp+="\n";
				product += temp;
				temp=token;
				textWidth=this.font.getWidth(token);
				//Log.info("Product:"+product);
			}else{
				temp+=token;
				//Log.info("Collating Token:"+temp);
			}
		}
		product+=temp;
		//product.concat(temp);
		return product;
	}
	
	// This method draws the text, wrapping it according to my implementation of the
	// Greedy Algorithm - not optimal, perhaps, but efficient - see method wordWrap, above
	// This requires that the text to print be split into words (searching for blank space
	// as the token) and each word be considered as to whether it will fit in the remaining
	// width or not - if not, it is placed on the next line, and the process continues
	public void draw(float x, float y, int boxWidth){
		
		// Get word-wrapped version of text
		this.text= wordWrap(boxWidth);
		// draw this text to the screen at given position
		this.font.drawString(x, y, this.text, this.colour);
	}
	
}
