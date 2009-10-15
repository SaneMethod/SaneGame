package ca.keefer.sanemethod.Tools;

import ca.keefer.sanemethod.Constants;
import ca.keefer.sanemethod.Interface.Option;
import ca.keefer.sanemethod.Interface.SaneSystem;
import ca.keefer.sanemethod.Interface.Text;

import org.newdawn.slick.Color;
import org.newdawn.slick.Image;
import org.newdawn.slick.SlickException;
import org.newdawn.slick.util.Log;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;
import org.xmlpull.v1.XmlPullParserFactory;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;

/**
 * This class processes a given xml file and returns a Text object which can then be called
 * to draw itself to the screen, before the state calls this routine again to fetch the next
 * part of the conversation
 * @author Christopher Keefer
 * @version 1.1
 * @see ca.keefer.sanemethod.Tools.Text
 *
 */
public class TextXMLPullParser {
	
	//Global variables
	SaneSystem saneSystem;
	XmlPullParser xpp;
	ArrayList<Text> thisDialog;

	public TextXMLPullParser(InputStream xmlFile){
		this.saneSystem = Constants.saneSystem;
		XmlPullParserFactory factory = null;
		thisDialog = new ArrayList<Text>();
		
		try{
			// Init the factory for a new pull parser
			factory = XmlPullParserFactory.newInstance(
					System.getProperty(XmlPullParserFactory.PROPERTY_NAME), 
					Thread.currentThread().getContextClassLoader().getClass());
			factory.setNamespaceAware(false);
			xpp = factory.newPullParser();
			xpp.setInput(xmlFile, null);

		}catch (XmlPullParserException e){
			Log.error("Error parsing Dialog:"+e.getMessage());
		}
	}
	
	public ArrayList<Text> processDialog(){
		// Get the event type of the first encountered event
		int eventType=0;
		try {
			eventType = xpp.getEventType();
		} catch (XmlPullParserException e) {
			Log.error("Error parsing Dialog:"+e.getMessage());
		}
		do{
			if(eventType == XmlPullParser.START_DOCUMENT) {
				Log.info("Dialog Parsing Begun: "+Utility.getDateTime());
			} else if(eventType == XmlPullParser.START_TAG) {
				processElement();
			}
			try {
				eventType = xpp.next();
			} catch (XmlPullParserException e) {
				Log.error("Error parsing Dialog:"+e.getMessage());
			} catch (IOException e) {
				Log.error("Error parsing Dialog:"+e.getMessage());
			}
		}while(eventType != XmlPullParser.END_DOCUMENT);
		Log.info("Dialog Parsing Finished: "+Utility.getDateTime());
		
		return thisDialog;
	}
	
	// Depending on the element encountered, process its various attributes and store
	// them as appropriate
	public void processElement(){
		if (xpp.getName().equals("Dialog")){
			// Just output the dialog name as an info message for now
			Log.info("Dialog Name:"+xpp.getAttributeValue(0));
		}else if (xpp.getName().equals("Text")){
			// Index: 0 = type; 1 = name; 2 = AngelCodeFont name; 3 = Colour decimal value;
			// 4 = whether to display the text box; 5 = text content; 6 = Image ref for textbox
			// 7 = proceed
			
			//Check to see what type (either full, rec or min) of text tag we're dealing with
			if (xpp.getAttributeValue(0).equals("full")){
				Log.info("Adding Text:"+xpp.getAttributeValue(1));
				try {
					thisDialog.add(new Text(xpp.getAttributeValue(1),saneSystem.getFonts().get(xpp.getAttributeValue(2)),
							Color.decode(xpp.getAttributeValue(3)),Boolean.parseBoolean(xpp.getAttributeValue(4)),
							xpp.getAttributeValue(5), new Image(xpp.getAttributeValue(6)),xpp.getAttributeValue(7)));
				} catch (SlickException e) {
					Log.error("Failed to load texture image in Text:"+e.getMessage());
				}
			}else if (xpp.getAttributeValue(0).equals("rec")){
				Log.info("Adding Text:"+xpp.getAttributeValue(1));
				thisDialog.add(new Text(xpp.getAttributeValue(1),saneSystem.getFonts().get(xpp.getAttributeValue(2)),
						Color.decode(xpp.getAttributeValue(3)),Boolean.parseBoolean(xpp.getAttributeValue(4)),
						xpp.getAttributeValue(5), xpp.getAttributeValue(7)));
			}else if (xpp.getAttributeValue(0).equals("min")){
				Log.info("Adding Text:"+xpp.getAttributeValue(1));
				thisDialog.add(new Text(saneSystem.getFonts().get(xpp.getAttributeValue(2)), xpp.getAttributeValue(5),
						xpp.getAttributeValue(7)));
			}
		}else if (xpp.getName().equals("Option")){
			// create an object of type Option, with an optional caption that will be displayed at the top
			// of the text box, above the options
			Log.info("Adding Option Text:"+xpp.getAttributeValue(0));
			// Index: 0 = name; 1 = font; 2 = colour; 3 = boxed; 4 = options; 5 = caption; 6 = values;
			thisDialog.add(new Option(xpp.getAttributeValue(0), saneSystem.getFonts().get(xpp.getAttributeValue(1)),
					Color.decode(xpp.getAttributeValue(2)), Boolean.parseBoolean(xpp.getAttributeValue(3)), 
					xpp.getAttributeValue(4), xpp.getAttributeValue(5), xpp.getAttributeValue(6)));
		}
	}
	
}
