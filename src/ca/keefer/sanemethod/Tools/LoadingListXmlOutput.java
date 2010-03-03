package ca.keefer.sanemethod.Tools;

import java.util.ArrayList;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

import org.newdawn.slick.util.Log;

import ca.keefer.sanemethod.Environment.MapObject;

/**
 * This class is responsible for automatically creating a new loadingList to replace the previous,
 * which will be renamed to loadingList.xml.bak, based on MapObjects
 * @author Christopher Keefer
 * @see ca.keefer.sanemethod.Environment.MapObject
 *
 */
public class LoadingListXmlOutput {

	public static void makeLoadingList(String ref, File savedFile, ArrayList<MapObject> loadList){
		File oldFile = new File("res/LoadingList.xml");
		renameOldList(oldFile);
		oldFile = null;
		File newFile = new File("res/LoadingList.xml");
		try {
			FileWriter fw = new FileWriter(newFile);
			fw.write("<?xml version=\"1.0\" encoding=\"utf-8\"?>\n"+
					"<!DOCTYPE Menu System \"kLoadingList.dtd\">"+
					"<!-- Loading List for Christopher Keefer's Sane Method\n" +
					"generated on "+Utility.getDateTime()+"  -->\n");
			fw.write("<LoadingList name=\"generated\" listSize=\""+loadList.size()+1+"\">");
			// loop through all current items that need to be outputted, save the last, which conclude the list
			for (int i=0;i<loadList.size()-1;i++){
				if (loadList.get(i).getShapeList() == null){
					fw.write("<Map name=\""+loadList.get(i).getMapFile().substring(0, loadList.get(i).getMapFile().length()-4)+
						"\" mapTMX=\""+loadList.get(i).getMapFile()+"\" />");
				}else{
					fw.write("<Map name=\""+loadList.get(i).getMapFile().substring(0, loadList.get(i).getMapFile().length()-4)+
							"\" mapTMX=\""+loadList.get(i).getMapFile()+"\" shapeXML=\""+loadList.get(i).getShapeList()+
							"\" />");
				}
			}
			fw.write("<Map name=\""+ref.substring(0, ref.length()-4)+"\" mapTMX=\""+ref+
					"\" shapeXML=\""+savedFile.getPath()+savedFile.getName()+
					"\" />");
			if (loadList.get(loadList.size()-1).getShapeList() == null){
				fw.write("<Map name=\""+loadList.get(loadList.size()-1).getMapFile().substring(0, loadList.get(loadList.size()-1).getMapFile().length()-4)+
					"\" mapTMX=\""+loadList.get(loadList.size()-1).getMapFile()+"\" />");
			}else{
				fw.write("<Map name=\""+loadList.get(loadList.size()-1).getMapFile().substring(0, loadList.get(loadList.size()-1).getMapFile().length()-4)+
						"\" mapTMX=\""+loadList.get(loadList.size()-1).getMapFile()+"\" shapeXML=\""+loadList.get(loadList.size()-1).getShapeList()+
						"\" />");
			}
			fw.write("</LoadingList>");
		} catch (IOException e) {
			Log.debug(e.getMessage());
		}
	}
	
	private static void renameOldList(File oldFile){
		File originFile = oldFile;
		File bakFile = new File(oldFile.getName()+".bak");
		boolean success = originFile.renameTo(bakFile);
		if (!success){
			Log.error("Failed to create backup of original loading list file");
		}
	}
}
