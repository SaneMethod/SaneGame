package ca.keefer.sanemethod.LevelBuilder;

import org.newdawn.slick.geom.Polygon;
import org.newdawn.slick.geom.Shape;

/**
 * This class contains a Shape, and holds a variety of additional information pertinent to the association
 * of this shape with an element from a TiledMap.
 * @author Christopher Keefer
 * @version 1.1
 */
public class MapShape {

	Shape shape;
	Polygon poly;

	public MapShape(Shape shape){
		this.shape = shape;
	}
	
	public Shape getShape(){
		return shape;
	}
	public void setShape(Shape shape){
		this.shape=shape;
	}
	
	public Polygon getPolygon(){
		return (Polygon) shape;
	}
	public void setPolygon(Polygon poly){
		this.shape = poly;
	}

}
