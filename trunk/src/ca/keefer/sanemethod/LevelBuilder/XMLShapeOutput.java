package ca.keefer.sanemethod.LevelBuilder;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

import org.newdawn.slick.util.Log;


public class XMLShapeOutput {
	
	File toSaveTo;
	String name;
	String fileName;
	MapShape[] shapeList;

	public XMLShapeOutput(String fileName, MapShape[] shapeList){
		this.fileName = fileName;
		this.shapeList = shapeList;
		toSaveTo = new File(fileName+".xml");
		name = fileName;
	}
	
	public void createOutputFile(){
		FileWriter fw=null;
		try {
			fw = new FileWriter(toSaveTo);
			
			// Begin writing the file with the xml tag
			fw.write("<?xml version=\"1.0\" encoding=\"utf-8\"?>\n");
			fw.write("<!DOCTYPE TileShapes SYSTEM \"kShape.dtd\">\n");
			fw.write("<!--Format 1.0 for kShape XML generation\n"+ 
					"by Christopher Keefer\n"+
					"Last Updated 2009-10-24\n" +
					"See www.sanemethod.com for more -->\n");
			fw.write("<MapShapes name=\""+name+"\" map=\""+fileName+"\">\n");
			
			// Now we enter the for loops where we write Tile information
			for (int i=0; i< shapeList.length;i++){
				fw.write("<Element pointCount=\""+
						shapeList[i].getShape().getPointCount()+"\" \n");
				// output the various points to this tag
				float[] points = shapeList[i].getShape().getPoints();
				int pointCounter =0;
				for (int z=0; z< points.length;z+=2){
					fw.write("point"+pointCounter+"X=\""+points[z]+"\" point"+pointCounter+"Y=\""+points[z+1]+"\" \n");
					pointCounter++;
				}
				pointCounter=0;
				// close this tile tag
				fw.write("/>\n");
			}
			// write close TileShapes tag
			fw.write("</MapShapes>");
			
			//Done.
			fw.close();
			toSaveTo = null;
			
		} catch (IOException e) {
			Log.error(e.getMessage());
		}
		
	}
}
