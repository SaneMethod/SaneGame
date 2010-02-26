package ca.keefer.sanemethod.Environment;

/**
 * Contains the resources and methods for constructing a new tiledEnvironment from
 * a shapelist and tmx map file, as defined in the loading list
 * @author Christopher Keefer
 *
 */
public class MapObject {
	String shapeList;
	String mapFile;
	
	public MapObject(){
		shapeList=null;
		mapFile=null;
	}
	
	public MapObject(String s, String t){
		shapeList=s;
		mapFile=t;
	}
	
	public void setShapeList(String s){
		shapeList=s;
	}
	public void setMapFile(String m){
		mapFile=m;
	}
	
	public String getShapeList(){
		return shapeList;
	}
	
	public String getMapFile(){
		return mapFile;
	}
	
}
