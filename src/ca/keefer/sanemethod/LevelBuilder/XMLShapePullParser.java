package ca.keefer.sanemethod.LevelBuilder;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;

import org.newdawn.slick.geom.Polygon;
import org.newdawn.slick.util.Log;
import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;
import org.xmlpull.v1.XmlPullParserFactory;

import ca.keefer.sanemethod.Tools.Utility;

/**
 * This class implements an xml pull parser to fetch a specific tile shape definition whenever
 * a tile position on a tilesheet with a matching xml file is passed to this class object.
 * This information is then used to create a SlickSet Tile object, and added to the tile
 * layer in the appropriate place as determined by the TilEd tmx file.
 *<br />
 * NOTE: Remember that the tmx file holds a tileSet, which holds a spritesheet,
 * which will be assigned a name in the TilEd editor that MUST match up with the name given in
 * the TileShapes tag of the xml file for the program to match the two.
 * @author Christopher Keefer
 * @version 1.1
 *
 */
public class XMLShapePullParser {

	ArrayList<MapShape> tileList;
	XmlPullParser xpp;
	
	public XMLShapePullParser(InputStream xmlFile){
		XmlPullParserFactory factory = null;
		tileList = new ArrayList<MapShape>();
		
		try{
			// DemoInit the factory for a new pull parser
			factory = XmlPullParserFactory.newInstance(
					System.getProperty(XmlPullParserFactory.PROPERTY_NAME), 
					Thread.currentThread().getContextClassLoader().getClass());
			factory.setNamespaceAware(false);
			xpp = factory.newPullParser();
			xpp.setInput(xmlFile, null);

		}catch (XmlPullParserException e){
			Log.error("Error 1 parsing TileShapes:"+e.getMessage());
		}
	}
	
	public ArrayList<MapShape> processXML(){
		// Get the event type of the first encountered event
		int eventType=0;
		try {
			eventType = xpp.getEventType();
		} catch (XmlPullParserException e) {
			Log.error("Error 2 parsing TileShape:"+e.getMessage());
		}
		do{
			if(eventType == XmlPullParser.START_DOCUMENT) {
				Log.info("TileShape Parsing Begun: "+Utility.getDateTime());
			} else if(eventType == XmlPullParser.START_TAG) {
				processElement();
			}
			try {
				eventType = xpp.next();
			} catch (XmlPullParserException e) {
				Log.error("Error 3 parsing TileShape:"+e.getMessage());
			} catch (IOException e) {
				Log.error("Error 4 parsing TileShape:"+e.getMessage());
			}
		}while(eventType != XmlPullParser.END_DOCUMENT);
		Log.info("TileShape Parsing Finished: "+Utility.getDateTime());
		
		return tileList;
	}
	
	// Depending on the element encountered, process its various attributes and store
	// them as appropriate
	public void processElement(){
		if (xpp.getName().equals("MapShapes")){
			// Do Nothing much for now
			Log.info("TileShapes:"+xpp.getAttributeValue(0));
		}else if (xpp.getName().equals("Element")){
			// create a new polygon from the Tile point information, and add it to the TileShape, along
			// with the image information provided from the SpriteSheet - use the shape to make Tile with
			// TileShape
			
			// Attribute Index: 0 = pointCount;
			// 1 -> ? = Point0X,Point0Y,Point1X, etc. -- point count will tell us how many
			// iterations to do to get all the point information
			
			float[] points;
			int iterations = Integer.parseInt(xpp.getAttributeValue(0));
			int pointCount=1;
			points = new float[iterations*2];
			for (int i=0;i<iterations;i++){
				points[pointCount-1] = (Float.parseFloat(xpp.getAttributeValue(pointCount))); 
				points[(pointCount+1)-1]= (Float.parseFloat(xpp.getAttributeValue(pointCount+1)));
				pointCount +=2;
			}
			Polygon thisPoly = new Polygon(points);
			// And now lets slot it all into the MapShape
			MapShape mapShape = new MapShape(thisPoly);
			tileList.add(mapShape);
		}
	}
	
}
