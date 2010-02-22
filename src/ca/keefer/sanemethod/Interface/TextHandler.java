package ca.keefer.sanemethod.Interface;

import java.util.ArrayList;

import org.newdawn.slick.Graphics;
import org.newdawn.slick.util.Log;

import ca.keefer.sanemethod.Constants;

/**
 * This class enables the abstraction of the inner workings of the Text objects when calling
 * such functions from the States - rather, we provide TextHandler with an ArrayList of Text objects,
 * and allow it to handle the mess - boiling down the calls needed from the state to ones like 
 * TextHandler.update or TextHandler.acceptInput.
 * @author Christopher Keefer
 * @version 1.2
 *
 */
public class TextHandler {

	ArrayList<Text> thisDialog;
	int dialogToDisplay;
	int queuedDialog;
	short proceed;
	boolean done;
	String optionSelection;
	
	/**
	 * This minimal constructor prepares all the text objects in the ArrayList with the default
	 * values of x=50, position = Text.BOTTOM, and boxWidth=750
	 * @param thisDialog The text object ArrayList
	 */
	public TextHandler(ArrayList<Text> thisDialog){
		this.thisDialog = thisDialog;
		optionSelection = null;
		dialogToDisplay=0;
		queuedDialog=0;
		done=false;
		for (int i=0;i<thisDialog.size();i++){
			if (thisDialog.get(i).isOption()){
				Option tOption = (Option) thisDialog.get(i);
				tOption.parse(tOption.getValueTemp(), tOption.getOptionTemp(),50,750);
			}
			thisDialog.get(i).prepare(50,0,Text.BOTTOM,750);	
		}
	}
	
	/**
	 * This full constructor allows the specification of both the Text object arraylist, and all
	 * the parameters which will be used to initialize each object. To specify different positions,
	 * x values or boxWidths for individual Text objects, either set them before passing them to
	 * TextHandler, or use the TextHandler.getByName or getByIndex functions to get and alter
	 * the individual Text objects.
	 * @param thisDialog The text object ArrayList
	 * @param x float for position of text along the left margin
	 * @param position short for position of text vertically on screen (Text.BOTTOM, MIDDLE or TOP)
	 * @param boxWidth width of the text box versus the right margin
	 */
	public TextHandler(ArrayList<Text> thisDialog, float x, float y, short position, int boxWidth){
		this.thisDialog = thisDialog;
		dialogToDisplay=0;
		queuedDialog=0;
		done=false;
		for (int i=0;i<thisDialog.size();i++){
			if (thisDialog.get(i).isOption()){
				Option tOption = (Option) thisDialog.get(i);
				tOption.parse(tOption.getValueTemp(), tOption.getOptionTemp(),x,boxWidth);
			}
			thisDialog.get(i).prepare(x,y,position,boxWidth);
			
		}
	}
	
	/**
	 * This is called to draw the text to the screen, as well as the text box
	 * @param g Graphics object for screen context
	 */
	public void display(Graphics g){
		if (!done){
			proceed = thisDialog.get(dialogToDisplay).drawTextBox();
			if (proceed == Text.OPEN){
				if (thisDialog.get(dialogToDisplay).isOption()){
					Option yada = (Option) thisDialog.get(dialogToDisplay);
					yada.draw(g,"9857534");
				}else{
					thisDialog.get(dialogToDisplay).drawTextLetterByLetter();
				}
			}else if (proceed == Text.CLOSED){
				// Assume we've closed because of considerQueue(), and set the dialog to Display to the
				// queued value, and open the TextBox back up.
				dialogToDisplay = queuedDialog;
				thisDialog.get(dialogToDisplay).switchBoxState();
			}
		}
	}
	
	/**
	 * Called to update the letter-by-letter display, as well as any other update requirements.
	 * Generally not required for Option Text Objects.
	 * @param delta int Time passed since last update call
	 */
	public void update(int delta){
		if (!done){
			if (thisDialog.get(dialogToDisplay).isOption() == false){
				thisDialog.get(dialogToDisplay).updateTextLetterByLetter(delta);
			}
		}
	}
	
	/**
	 * Return the index of the currently displaying dialog
	 * @return integer of the index of the currently displaying dialog
	 */
	public int getCurrent(){
		return dialogToDisplay;
	}
	
	/**
	 * Lengthy function which accepts input from the state (in the form of an integer detailing
	 * which key has been pressed, and compared against the KEY Constants), and performs the necessary
	 * functions on the Text objects.
	 * This function also controls the progression of the text objects to be displayed, and the summary
	 * ending of display once the end display ('@@') characters have been reached.
	 * @param keyPressed
	 */
	public void acceptInput(int keyPressed){
		if (!done){
			if (keyPressed == Constants.KEY_UP){
				if (thisDialog.get(dialogToDisplay).isOption()){
					Option yada = (Option)thisDialog.get(dialogToDisplay);
					yada.moveSelectRect(true);
				}
			}else if (keyPressed == Constants.KEY_DOWN){
				if (thisDialog.get(dialogToDisplay).isOption()){
					Option yada = (Option)thisDialog.get(dialogToDisplay);
					yada.moveSelectRect(false);
				}
			}else if (keyPressed == Constants.KEY_ACCEPT){
				if (thisDialog.get(dialogToDisplay).isOption()){
					Option yada = (Option)thisDialog.get(dialogToDisplay);
					optionSelection = yada.getValue();
					Log.info("Value:"+optionSelection);
					for (int i=0; i< thisDialog.size();i++){
						if (thisDialog.get(i).getName().equals(optionSelection)){
							queuedDialog = i;
							break;
						}
					}
				}else{
					//thisDialog.get(dialogToDisplay).switchBoxState();
					if (thisDialog.get(dialogToDisplay).getProceed().equals(Text.PROCEED_NEXT)){
						// if there are any values left in the arrayList, then increment the 
						// queuedDialog
						if ((dialogToDisplay + 1) < thisDialog.size()){
							queuedDialog=dialogToDisplay +1;
						}
					}else if (thisDialog.get(dialogToDisplay).getProceed().equals(Text.PROCEED_LAST)){
						if ((dialogToDisplay - 1) > 0){
							queuedDialog=dialogToDisplay -1;
						}
					}else if (thisDialog.get(dialogToDisplay).getProceed().equals(Text.PROCEED_END)){
						this.done = true;
					}else {
						for (int i=0; i<thisDialog.size();i++){
							if (thisDialog.get(i).getName().equals(thisDialog.get(dialogToDisplay).getProceed())){
								queuedDialog = i;
								break;
							}
						}
					}
				}
				// check to see if we've done something to change the queue
				considerQueue();
			}
		}
	}
	/**
	 * If we want the selected optionfor seom purpose external to this dialog
	 * @return a String containing the selected option value
	 */
	public String getOptionSelection(){
		return optionSelection;
	}
	
	/**
	 * This method checks to see if there's a difference between the currently displayed
	 * Text and the queued text, and if so, sets in motion the closing of the current Textbox and
	 * the opening of the next, along with the change of the dialogToDisplay to the new dialog
	 */
	private void considerQueue(){
		if (queuedDialog != dialogToDisplay){
			// check to see if is skippable and if is finished
			if (thisDialog.get(dialogToDisplay).isSkippable()){
			// if isSkippable(), then it doesn't matter whether its done yet or not - we go on
				thisDialog.get(dialogToDisplay).switchBoxState();
			}else {
				// if its not skippable, check to see if its done yet.
				if (thisDialog.get(dialogToDisplay).isFinished()){
					// if so, go on
					thisDialog.get(dialogToDisplay).switchBoxState();
				}
			}
		}
	}
	
	public void setDone(boolean t){
		done = t;
	}
	/**
	 * Get whether this textHandler is finished displaying its contents
	 * @return true if all contents have been displayed
	 */
	public boolean getDone(){
		return done;
	}
	
	/**
	 * If the Text object with the given name (case-sensitive) exists, then find and return it.
	 * @param name String name of Text object as defined by the XML.
	 * @return Text Object
	 */
	public Text getByName(String name){
		for (int i=0;i<thisDialog.size();i++){
			if (thisDialog.get(i).getName().equals(name)){
				return thisDialog.get(i);
			}
		}
		return null;
	}
	
	/**
	 * Returns the Text object at the given index. If index is out of bounds, returns null.
	 * @param index int position of Text object in the ArrayList.
	 * @return Text Object
	 */
	public Text getByIndex(int index){
		if (index >=0 && index < thisDialog.size()){
			return thisDialog.get(index);
		}
		return null;
	}
}
