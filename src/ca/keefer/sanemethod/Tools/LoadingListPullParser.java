package ca.keefer.sanemethod.Tools;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;

import org.newdawn.slick.util.Log;
import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;
import org.xmlpull.v1.XmlPullParserFactory;

import ca.keefer.sanemethod.Environment.MapObject;

public class LoadingListPullParser {
	ArrayList<MapObject> mapObjects;
	XmlPullParser xpp;
	
	public LoadingListPullParser(InputStream res){
		mapObjects = new ArrayList<MapObject>(5);
		XmlPullParserFactory factory = null;
		xpp = null;
		try{
			// DemoInit the factory for a new pull parser
			factory = XmlPullParserFactory.newInstance(
					System.getProperty(XmlPullParserFactory.PROPERTY_NAME), 
					Thread.currentThread().getContextClassLoader().getClass());
			factory.setNamespaceAware(false);
			xpp = factory.newPullParser();
			xpp.setInput(res, null);
		}catch (XmlPullParserException e){
			Log.debug(e.getMessage());
		}
	}
	
	public ArrayList<MapObject> processLoadingList(){
		int eventType;
		try {
				eventType = xpp.getEventType();
			do{
				if(eventType == XmlPullParser.START_DOCUMENT) {
					Log.info("XML Parsing Begun: "+Utility.getDateTime());
				} else if(eventType == XmlPullParser.START_TAG) {
					processElement(xpp);
				}
				eventType = xpp.next();
				
			}while(eventType != XmlPullParser.END_DOCUMENT);
			
		}catch (IOException e){
			Log.debug(e.getMessage());
		} catch (XmlPullParserException ex) {
			Log.debug(ex.getMessage());
		}
		Log.info("XML Parsing Finished: "+Utility.getDateTime());
		return mapObjects;
	}
	
	public void processElement(XmlPullParser xpp){
		if (xpp.getName().equalsIgnoreCase("LoadingList")){
			// the name and size of the list are available here - do something with them?
			Log.info("LoadingList: "+xpp.getAttributeValue(null, "name")+" with a size of:"
					+xpp.getAttributeValue(null, "listSize"));
		}else if (xpp.getName().equalsIgnoreCase("Map")){
			MapObject temp = new MapObject(xpp.getAttributeValue(null, "shapeXML"),
					xpp.getAttributeValue(null, "mapTMX"));
			mapObjects.add(temp);
		}
	}

}
