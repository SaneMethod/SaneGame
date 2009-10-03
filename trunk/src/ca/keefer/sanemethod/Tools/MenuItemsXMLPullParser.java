package ca.keefer.sanemethod.Tools;

import org.newdawn.slick.AngelCodeFont;
import org.newdawn.slick.Color;
import org.newdawn.slick.Image;
import org.newdawn.slick.SlickException;
import org.newdawn.slick.util.Log;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;
import org.xmlpull.v1.XmlPullParserFactory;

import java.io.IOException;
import java.io.InputStream;
import java.util.Hashtable;

public class MenuItemsXMLPullParser {
	Hashtable<String,Image> imageTable;
	Hashtable<String,AngelCodeFont> fontTable;
	
	public MenuItemsXMLPullParser(InputStream res, Hashtable<String,Image> thisImageHash, 
			Hashtable<String,AngelCodeFont> thisFontHash){
		this.imageTable = thisImageHash;
		this.fontTable = thisFontHash;
		XmlPullParserFactory factory = null;
		
		try{
			// Init the factory for a new pull parser
			factory = XmlPullParserFactory.newInstance(
					System.getProperty(XmlPullParserFactory.PROPERTY_NAME), 
					Thread.currentThread().getContextClassLoader().getClass());
			factory.setNamespaceAware(false);
			XmlPullParser xpp = factory.newPullParser();
			xpp.setInput(res, null);
			
			processDocument(xpp);
		}catch (XmlPullParserException e){
			e.printStackTrace();
		}catch (IOException ex){
			ex.printStackTrace();
		}catch (SlickException ez){
			ez.printStackTrace();
		}
	}
	
	public void processDocument(XmlPullParser xpp) 
    throws XmlPullParserException, IOException, SlickException
    {
		int eventType = xpp.getEventType();
		do{
			if(eventType == XmlPullParser.START_DOCUMENT) {
				Log.info("XML Parsing Begun: "+Utility.getDateTime());
			} else if(eventType == XmlPullParser.START_TAG) {
				processStartElement(xpp);
		}
			eventType = xpp.next();
		}while(eventType != XmlPullParser.END_DOCUMENT);
		Log.info("XML Parsing Finished: "+Utility.getDateTime());
    }
	
	public void processStartElement(XmlPullParser xpp) throws SlickException{
		// imageTable
		if (xpp.getName().equalsIgnoreCase("Menu")){
			// do nothing - the system name is not currently used for anything
			Log.info("Menu System: "+xpp.getAttributeValue(null, "systemName"));
		}else if (xpp.getName().equalsIgnoreCase("Item")){
			// If the image is intended to be subdivided, check for that
			// and then create sub-images based on the loaded file, and
			// add those to the imageTable.
			if (xpp.getAttributeValue(null, "type").equals("subImage")){
				Image temp = new Image(xpp.getAttributeValue(null, "location"));
				int subX=0,subY=0,subWidth=0,subHeight=0;
				try{
					subX=Integer.parseInt(xpp.getAttributeValue(null,"subImageX"));
					subY=Integer.parseInt(xpp.getAttributeValue(null,"subImageY"));
					subWidth=Integer.parseInt(xpp.getAttributeValue(null,"subImageWidth"));
					subHeight=Integer.parseInt(xpp.getAttributeValue(null,"subImageHeight"));
				}catch (NumberFormatException e){
					e.printStackTrace();
				}
				Image tempSub = temp.getSubImage(subX,subY,subWidth,subHeight);
				
				//imageTable.put("base_"+(xpp.getAttributeValue(null,"name")),temp);
				imageTable.put(xpp.getAttributeValue(null,"name"), tempSub);
			}else if (xpp.getAttributeValue(null,"type").equals("font")){
				// Parse the font location and image file and drop the new AngelCodeFont
				// into the appropriate hashtable
				AngelCodeFont thisFont = new AngelCodeFont(xpp.getAttributeValue(null,"fontLocation"),
						new Image(xpp.getAttributeValue(null,"location"),false,Image.FILTER_LINEAR,Color.black));
				fontTable.put(xpp.getAttributeValue(null,"name"), thisFont);
				Log.info("Added font:" + xpp.getAttributeValue(null,"name"));
			}else{
				// Otherwise, just add the whole image to the imageTable
				imageTable.put(xpp.getAttributeValue(null,"name"), 
						new Image(
						xpp.getAttributeValue(null, "location")));
			}
			
		}
	}
}
