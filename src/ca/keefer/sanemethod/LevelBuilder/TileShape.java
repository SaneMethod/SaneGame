package ca.keefer.sanemethod.LevelBuilder;

import org.newdawn.slick.Image;
import org.newdawn.slick.geom.Polygon;
import org.newdawn.slick.geom.Shape;
import org.newdawn.slick.util.Log;

import net.phys2d.math.Vector2f;

/**
 * This class contains a Shape, and holds a variety of additional information pertinent to the association
 * of this shape with a tile from a tileSheet.
 * @author Christopher Keefer
 * @version 1.1
 */
public class TileShape {

	Shape shape;
	Polygon poly;
	/** The name of this Tile - set to its complement in TilEd for identification */
	String tileName; 
	Image image;
	int tileSheetX;
	int tileSheetY;

	public TileShape(String tileName, Shape shape, Image image, int tileSheetX, int tileSheetY){
		this.tileName = tileName;
		this.shape = shape;
		this.image = image;
		this.tileSheetX = tileSheetX;
		this.tileSheetY = tileSheetY;
	}
	
	/**
	 * Note that shapes will need to be transformed/translated to their appropriate pixel
	 * positions on the map corresponding with their tile at runtime, at which point the
	 * collision information will need to be rebuilt.
	 * @param shape
	 * @param image
	 * @param tileSheetX
	 * @param tileSheetY
	 * @param loaded
	 */
	public TileShape(String tileName, Shape shape, Image image, int tileSheetX, int tileSheetY, boolean loaded){
		this.tileName = tileName;
		this.shape = shape;
		this.image = image;
		this.tileSheetX = tileSheetX;
		this.tileSheetY = tileSheetY;
		
	}
	
	/*
	public void createCollisionPolygonAndTileFromShape(){
		float[] points = shape.getPoints();
		int iterations = points.length/2;
		Vector2f[] vector = new Vector2f[iterations];
		for (int i=0;i<iterations;i++){
			vector[i] = new Vector2f(points[i*2], points[(i*2)+1]);
		}
		poly = new Polygon(vector);
		
		 // Debug Output
		pointCount=0;
		ROVector2f[] polyV;
		polyV = poly.getVertices();
		
		for (int z=0;z<vector.length;z++){
			Log.debug("Vertex"+z+"X:"+vector[z].getX()+" Vertex"+z+"Y:"+vector[z].getY());
			Log.debug("Point"+z+"X:"+points[pointCount]+" Point"+z+"Y:"+points[pointCount+1]);
			Log.debug("PolygonVertex"+z+"X:"+polyV[z].getX()+" PolygonVertex"+z+"Y:"+polyV[z].getY());
			pointCount +=2;
		}
		Log.debug("Shape:"+shape.getHeight());
		Log.debug("Poly:"+poly.getBounds().getHeight());
		
	}
	*/
	
	public String getTileName(){
		return tileName;
	}
	
	public void setTileName(String tileName){
		this.tileName = tileName;
	}
	
	public Shape getShape(){
		return shape;
	}
	public void setShape(Shape shape){
		this.shape=shape;
	}
	
	public Image getImage(){
		return image;
	}
	public void setImage(Image image){
		this.image = image;
	}
	
	public int getTileSheetX(){
		return tileSheetX;
	}
	public void setTileSheetX(int tileSheetX){
		this.tileSheetX = tileSheetX;
	}
	
	public int getTileSheetY(){
		return tileSheetY;
	}
	public void setTileSheetY(int tileSheetY){
		this.tileSheetY = tileSheetY;
	}
	
	public Polygon getPolygon(){
		return (Polygon) shape;
	}
	public void setPolygon(Polygon poly){
		this.shape = poly;
	}

}
