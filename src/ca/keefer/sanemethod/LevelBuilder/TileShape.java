package ca.keefer.sanemethod.LevelBuilder;

import org.newdawn.slick.Image;
import org.newdawn.slick.geom.Shape;

/**
 * This class contains a Shape, and holds a variety of additional information pertinent to the association
 * of this shape with a tile from a tileSheet.
 * @author Christopher Keefer
 * @version 1.1
 */
public class TileShape {

	Shape shape;
	Image image;
	int tileSheetX;
	int tileSheetY;

	public TileShape(Shape shape, Image image, int tileSheetX, int tileSheetY){
		this.shape = shape;
		this.image = image;
		this.tileSheetX = tileSheetX;
		this.tileSheetY = tileSheetY;
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

}
