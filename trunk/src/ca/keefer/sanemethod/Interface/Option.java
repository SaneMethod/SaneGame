package ca.keefer.sanemethod.Interface;

import org.newdawn.slick.AngelCodeFont;
import org.newdawn.slick.Color;
import org.newdawn.slick.Graphics;
import org.newdawn.slick.Image;
import org.newdawn.slick.geom.Rectangle;
import org.newdawn.slick.util.Log;

import java.util.Scanner;

/**
 * This class extends the Text class, and functions relatively similarly, with a few major
 * differences: 
 * One, it seperates the given text based on a delimiter (the '|' character) and
 * displays each string thus seperated on a seperate line;
 * Two, it allows for interaction with the text box contents by drawing a highlight
 * rectangle around one line at a time, to show which will be selected should the player
 * press the accept key;
 * Three, when the player chooses one of the options, the value of the option chose is
 * returned - this value should be equal to the name value of another Text or Option
 * field within the same Dialog file, and will allow for branching conversation trees.
 * @author Christopher Keefer
 * @version 1.1
 * @see ca.keefer.sanemethod.Interface.Text
 *
 */
public class Option extends Text{
	
	String[] options; // can get rectangle size from height of each string
	String[] values;
	float[] optionHeight;
	int optionCount;
	int selected=1;
	Rectangle selectRect;
	float selectX, selectY;
	String valueTemp;
	String optionTemp;
	

	// Recommended Constructor
	public Option (String name, AngelCodeFont font, Color colour, boolean boxed, String options,
			String caption, String values){
		super (name, font, colour, boxed, caption,Text.PROCEED_END);
		this.option = true;
		this.finished = true;
		valueTemp = values;
		optionTemp = options;
	}
	
	// Full Constructor
	public Option(String name, AngelCodeFont font, Color colour, boolean boxed,
			String options, Image textTex, String caption, String values) {
		super(name, font, colour, boxed, caption, textTex, Text.PROCEED_END);
		this.option = true;
		this.finished = true;
		valueTemp = values;
		optionTemp = options;
	}
	
	public void parse(String values, String options, float xOffset, int boxWidth){
		// Set variable to prevent word-wrapping of options
		this.option=true;
		this.finished=true;
		// break up the text and values strings into new lines based on the delimiter '|'
		optionCount=0;
		text+="\n";
		Log.info("Parsing:"+this.name);
		Log.info("Parse String:"+options);
		Scanner thisScan = new Scanner(options);
		thisScan.useDelimiter("\\s*@@\\s*");
		Log.info("scanner delimiter:"+thisScan.delimiter());
		while(thisScan.hasNext()){
			optionCount++;
			thisScan.next();
		}
		// now that we know how many options (and thus, how many values there are) we can split
		// up the passed strings accordingly, ang get the heights of each
		this.optionHeight = new float[optionCount+1];
		// Get Height of caption
		optionHeight[0]=this.font.getHeight(this.text); // Remember, options have height of optionHeight[this-1]
		this.options = new String[optionCount];
		this.values = new String[optionCount];
		thisScan = new Scanner(options);
		thisScan.useDelimiter("\\s*@@\\s*");
		for (int i=0; i<optionCount;i++){
			this.options[i] = thisScan.next();
			// Word wrap the option, just in case it spans multiple lines
			// TODO: This uses default values - WILL BREAK if the full constructor is used for TextHandler 
			// and different values are specified
			this.options[i] = this.wordWrap(boxWidth, (int)xOffset, this.options[i]);
			this.text += this.options[i]+"\n";
			optionHeight[i+1]=this.font.getHeight(this.options[i]);
		}
		thisScan = new Scanner(values);
		thisScan.useDelimiter("\\s*@@\\s*");
		for (int i=0; i<optionCount;i++){
			this.values[i] = thisScan.next();
		}
	}
	
	public void draw(Graphics g, String colour){
		
		selectX=x;
		selectY=y;
		for (int i=0;i<selected;i++){
			selectY+=optionHeight[i];
		}
		// Draw the selection rectangle around the current selected item
		selectRect = new Rectangle(selectX-2,selectY,width+2,optionHeight[selected]);
		Color tColor = g.getColor(); // preserve current color in temp var
		Color thisColor = Color.decode(colour); // get color to be displayed
		thisColor.a = 0.5f; // set this colours transparency
		g.setColor(thisColor); // set color to our select bar color as defined in the XML
		g.fill(selectRect); // draw the select rectangle
		g.setColor(tColor); // set color back to original
		super.draw();
		
	}
	
	public void moveSelectRect(boolean dir){
		// dir is true if moving up, false if moving down
		if (selected > 1 && dir == true){
			selected--;
		}else if (selected < optionHeight.length-1 && dir == false){
			selected++;
		}
		Log.info("Selected:"+selected);
	}
	
	// This method returns that string value of the selected option so that
	// the Dialog ArrayList can be searched for the appropriately named Text or Option
	// Allowing for branching conversation trees
	public String getValue(){
		// return string value of the option the selected var is currently pointing at
		return values[selected-1];
	}
	
	public String getValueTemp(){
		return valueTemp;
	}
	public String getOptionTemp(){
		return optionTemp;
	}

}
