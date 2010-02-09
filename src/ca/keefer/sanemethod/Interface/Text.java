package ca.keefer.sanemethod.Interface;

import org.newdawn.slick.AngelCodeFont;
import org.newdawn.slick.Color;
import org.newdawn.slick.Image;
import org.newdawn.slick.SlickException;
import org.newdawn.slick.geom.Rectangle;
import org.newdawn.slick.geom.ShapeRenderer;
import org.newdawn.slick.util.Log;

import ca.keefer.sanemethod.Constants;

import java.util.Scanner;

/**
 * This class defines a Text object, with the following properties:
 * name String of the object name derived from xml or direct input
 * font The font to use to print this Text object
 * colour The colour to print the string in
 * text The string to print
 * boxed boolean determining whether to draw a text box around the string or not
 * resString the parsed string to print
 * textTex Texture for text box
 * position short determining vertical screen position
 * x float x position
 * y float y position
 * prepared whether this text object is ready to drawn or not 
 * @author Christopher Keefer
 * @version 1.5
 *
 */
public class Text {
	
	// Object global variables
	String name;
	AngelCodeFont font;
	Color colour;
	String text;
	boolean boxed;
	String proceed; // Stores information on which (if any) Text object from the current dialog should be displayed
	
	String resString;
	Image textTex;
	short position;
	float x;
	float y;
	int boxWidth;
	boolean prepared=false;
	public boolean option=false; // Boolean for the child Option class to set, to prevent word wrapping of options
	boolean finished=false; // Boolean for whether the text is finished displaying letter-by-letter
	boolean skippable=true; // Boolean determining whether this text can be fast-forwarded through by the user
	
	// Text box global variables
	short boxState=0;
	public static final short OPENING=0;
	public static final short OPEN=1;
	public static final short CLOSING=2;
	public static final short CLOSED=3;
	int boxHeight=0;
	
	// Letter-By-Letter drawing vars
	int cOffset;
	int accumulator;
	
	// proceed string statics
	final public static String PROCEED_NEXT = "++";
	final public static String PROCEED_LAST = "--";
	final public static String PROCEED_END = "@@";
	
	// Position statics
	final public static short BOTTOM = 0;
	final public static short MIDDLE = 1;
	final public static short TOP = 2;
	
	int height; // This is known only after parsing the text
	int width; // This is known only after parsing the text
	
	// Minimal constructor
	public Text(AngelCodeFont font, String text, String proceed){
		this.name="Unknown";
		this.font=font;
		colour=Color.white;
		this.proceed=proceed;
		this.text=text;
		this.boxed=true;
		try {
			textTex = new Image("res/UI/texWin.png");
		} catch (SlickException e) {
			Log.error("Failure to load image in Text:"+e.getMessage());
		}
	}
	
	// Recommended Constructor
	public Text(String name, AngelCodeFont font, Color colour, boolean boxed, String text, String proceed){
		this.name = name;
		this.font=font;
		this.colour=colour;
		this.text=text;
		this.boxed=boxed;
		this.proceed=proceed;
		try {
			textTex = new Image("res/UI/texWin.png");
		} catch (SlickException e) {
			Log.error("Failure to load image in Text:"+e.getMessage());
		}
	}
	
	// Full Constructor
	public Text(String name, AngelCodeFont font, Color colour, boolean boxed, String text, Image textTex, 
			String proceed){
		this.name=name;
		this.font=font;
		this.colour=colour;
		this.boxed=boxed;
		this.text=text;
		this.textTex=textTex;
		this.proceed=proceed;
	}
	
	// returns name
	public String getName(){
		return this.name;
	}
	
	// This method ensures the horizontal centering of the text box
	public float centerWidth(float width){
		float x = (Constants.SCREENWIDTH - width) / 2;
		return x;
	}
	
	// This method allows for the easy vertical centering of the text box
	public float centerHeight(float height){
		float y = (Constants.SCREENHEIGHT - height) / 2;
		return y;
	}
	
	// This method parses the text, wrapping it according to my implementation of the
	// Greedy Algorithm - not optimal, perhaps, but efficient
	// This requires that the text to print be split into words (searching for blank space
	// as the token) and each word be considered as to whether it will fit in the remaining
	// width or not - if not, it is placed on the next line, and the process continues
	public String wordWrap(int boxWidth, int xOffset, String text){
		int textWidth=xOffset;
		String token="";
		String temp="";
		String product="";
		// tokenize and split the text
		Scanner thisScan = new Scanner(text);
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
	
	// This method draws the full text immediately to the screen
	public void draw(){
		// draw this text to the screen at given position
		this.font.drawString(x, y, resString, this.colour);
	}
	
	
	// This method draws the full text immediately to the screen with an immediate box
	public void drawWithBox(){
		// if boxed==true, then draw a text box around the text
		if (boxed){
			x = centerWidth(width);
			ShapeRenderer.textureFit(new Rectangle(x-10,y-5,width+20,height+15), textTex);
		}
		// draw this text to the screen at given position
		this.font.drawString(x, y, resString, this.colour);
	}
	
	public short drawTextBox(){
		if (boxed){
			switch(boxState){
			case OPENING:
				ShapeRenderer.textureFit(new Rectangle(x-15,y-5,width+30,boxHeight), textTex);
				if (boxHeight < height){
					boxHeight += 15;
				}else {
					boxState = OPEN;
				}
				break;
			case OPEN:
				ShapeRenderer.textureFit(new Rectangle(x-15,y-5,width+30,height+15), textTex);
				break;
			case CLOSING:
				ShapeRenderer.textureFit(new Rectangle(x-15,y-5,width+30,boxHeight), textTex);
				if (boxHeight > 0){
					boxHeight -= 15;
				}else {
					boxState = CLOSED;
				}
				break;
			case CLOSED:
				break;
			}
		}else {
			if (boxState == OPENING){
			boxState = OPEN;
			}else if (boxState == CLOSING){
				boxState = CLOSED;
			}
		}
		return boxState;
	}
	
	public void setBoxState (short s){
		boxState = s;
	}
	public void switchBoxState(){
		if (boxState == OPEN){
			boxState = CLOSING;
		}else{
			boxState = OPENING;
		}
	}
	
	public void updateTextLetterByLetter(int delta){
		// Update the offset for the text display based on how
		// much time has passed
		accumulator += delta;
		if (cOffset < resString.length() && accumulator >= Constants.TEXT_SPEED){
			cOffset++;
			accumulator = 0;
		}else{
			this.finished = true;
		}
	}
	
	// Draw the text letter by letter to the screen - must be called from render
	public void drawTextLetterByLetter(){
		this.font.drawString(x, y, resString.substring(0, cOffset), this.colour);
	}
	
	// Called before any output functions, to ensure text is properly
	// formatted and all text-dependant variables are set
	public void prepare(float x, float y, short pos, int boxWidth){
		// float for y position based on short
		this.y=y;
		this.x=x;
		this.boxWidth=boxWidth;
		
		if (!option){
			// Get word-wrapped version of text
			resString= wordWrap(this.boxWidth, (int) x, this.text);
		}else{
			resString=text;
		}
		// Set height and width
		height = this.font.getHeight(resString);
		width = this.font.getWidth(resString);
		// Center x position
		this.x = centerWidth(width);
		// Depending on position variable and height, determine where to place the box vertically
		if (pos == BOTTOM){
			this.y = Constants.SCREENHEIGHT - height - 20 + y;
		}else if (pos == MIDDLE){
			this.y = centerHeight(height) + y;
		}else if (pos == TOP){
			this.y = 20 + y;
		}
		cOffset=0;
		accumulator=0;
		this.prepared = true;
	}
	
	public boolean isPrepared(){
		return this.prepared;
	}
	
	public boolean isOption(){
		return this.option;
	}
	
	public String getProceed(){
		return proceed;
	}
	
	public boolean isSkippable(){
		return skippable;
	}
	
	public boolean isFinished(){
		return finished;
	}
	
	// Use this method when we just want the wordwrapped string returned
	public String getString(){
		// Get word-wrapped version of text
		resString= wordWrap(boxWidth, (int) x, this.text);
		// Set height
		height = this.font.getHeight(resString);
		
		return resString;
	}
	
	// use this to get the height of the word-wrapped string
	public int getHeight(String txt){
		String thisString= wordWrap(boxWidth, (int) x, txt);
		// Set height
		height = this.font.getHeight(thisString);
		
		return height;
	}
	
}
