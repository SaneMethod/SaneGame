package ca.keefer.sanemethod.Tools;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

import org.newdawn.slick.util.Log;

/**
 * This creates a Properties tsx (xml) file for a TilEd TileSet
 * @author Christopher Keefer
 * @version 1.1
 *
 */
public class TSXGen {
	
	static File toSaveTo;

	public static void makeTSX(int start, int end, String fileName, int tileWidth, int tileHeight){
		toSaveTo = new File("res/Tiles/"+fileName+".tsx");
		FileWriter fw=null;
		try {
			fw = new FileWriter(toSaveTo);
			
			// Begin writing the file with the xml tag
			fw.write("<?xml version=\"1.0\" encoding=\"utf-8\"?>\n");
			fw.write("<tileset name=\""+fileName+"\" firstgid=\""+start+"\" tilewidth=\""+tileWidth+"\" tileheight=\""+tileHeight+"\">\n");
			fw.write("<image source=\""+fileName+".png\"/>\n");
			for (int i=start;i<=end;i++){
				fw.write("<tile id=\""+i+"\">\n");
				fw.write("<properties>\n");
				fw.write("<property name=\"name\" value=\"Tile"+i+"\"/>\n");
				fw.write("</properties>\n");
				fw.write("</tile>\n");
			}
			fw.write("</tileset>");
			
			//Done.
			fw.close();
			toSaveTo = null;
		} catch (IOException e) {
			Log.error(e.getMessage());
		}
	}
}
