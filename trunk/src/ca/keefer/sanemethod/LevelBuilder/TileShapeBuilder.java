package ca.keefer.sanemethod.LevelBuilder;

import java.util.ArrayList;

import org.newdawn.slick.Color;
import org.newdawn.slick.GameContainer;
import org.newdawn.slick.Graphics;
import org.newdawn.slick.Input;
import org.newdawn.slick.SlickException;
import org.newdawn.slick.SpriteSheet;
import org.newdawn.slick.geom.Circle;
import org.newdawn.slick.geom.Path;
import org.newdawn.slick.geom.Polygon;
import org.newdawn.slick.geom.Rectangle;
import org.newdawn.slick.geom.Transform;
import org.newdawn.slick.gui.TextField;
import org.newdawn.slick.state.BasicGameState;
import org.newdawn.slick.state.StateBasedGame;
import org.newdawn.slick.util.Log;
import org.newdawn.slick.util.ResourceLoader;

/**
 * This class allows me to draw paths/polygons overlaying individual elements of, 
 *  a tile map, and save the points of the polygon as a text representation of an array of floats in an xml
 * file which contains information on the shape, and the location of the element within the context
 * of the tile map. <br /> This then allows me, when parsing in the tile map, to create static bodies
 * for phys2D to deal with representing the shapes of the map elements, allowing for proper collision
 * handling. The file name of the output xml file will be the name of the tile map tmx with an
 * '.xml' tacked on the end.
 * @author Christopher Keefer
 * @version 1.1
 */
/*
 * Format of the xml file:
 * <MapShapes name="name" map="tileMap.tmx" tileSizeX="##" tileSizeY="##">
 * 	<!-- Point count refers to how many pairs of x,y float values there are to parse for a given Map Element -->
 *  <!-- TileMapX/Y refer to the upper left hand corner of the shape of this element -->a	
 * 	<Element name="name" tileMapX="x" tileMapY="y" pointCount="###" point0X="x" point0Y="y" point1X="x" ... />
 * 	...
 * </MapShapes>
 */
/* Interface appearance:
 * ++++    ++++  Left rectangle contains finished tiles with their shapes drawn over them - can be reloaded and
 * |  |    |  |  shape refined this way.
 * |  |    |  |  Right rectangle contains unshaped tiles.
 * ++++    ++++  Path drawing done with mouse. 
 * Hot keys: s = save the xml file; l = load (pops up dialog box to type string for next tilesheet to load;
 * d = delete current shape/path; 1 = square; 2 = circle; 3 = Path/polygon;
 */
public class TileShapeBuilder extends BasicGameState{

	int stateID;
	
	// Global variables for editor environment
	
	// Tilesheet
	SpriteSheet tileSheet;
	
	// Self-referencing
	StateBasedGame game;
	GameContainer container;
	
	// Simple OPENGL GUI for text fields
	boolean showTileNameField;
	TextField tileNameField;
	
	String ref;
	int cellWidth;
	int cellHeight;
	
	// File type constants
	final int FILE_TYPE_PNG = 0;
	final int FILE_TYPE_XML = 1;
	
	// Temporary holder shapes for any drawn shapes
	Rectangle tempRect;
	Circle tempCircle;
	Path tempPath;
	
	int mouseX;
	int mouseY;
	int drawState;
	boolean drawing;
	int startPointX;
	int startPointY;
	
	final int DRAW_STATE_NONE = 0;
	final int DRAW_STATE_DRAWING = 1;
	final int DRAW_STATE_DRAWN = 2;
	
	// The array list holding all completed shapes
	ArrayList<TileShape> shapeList;
	int currentShape;
	final int SHAPE_SQUARE = 0;
	final int SHAPE_CIRCLE = 1;
	final int SHAPE_PATH = 2;
	int highlightArrow;
	final int HIGH_UP_RIGHT = 0;
	final int HIGH_UP_LEFT = 1;
	final int HIGH_DOWN_RIGHT = 2;
	final int HIGH_DOWN_LEFT = 3;
	TileShape currentTile;
	
	ArrayList<TileShape> rawTiles;
	ArrayList<TileShape> finishedTiles;
	
	int rawTilesOffset;
	int finishedTilesOffset;
	int rawTilesX;
	int rawTilesY;
	int finishedTilesX;
	int finishedTilesY;
	
	Polygon leftDownArrow;
	Polygon rightDownArrow;
	Polygon leftUpArrow;
	Polygon rightUpArrow;
	
	public TileShapeBuilder(int id){
		stateID = id;
	}
	
	@Override
	public int getID() {
		return stateID;
	}

	@Override
	public void init(GameContainer container, StateBasedGame game)
			throws SlickException {
		this.container = container;
		this.game = game;
		
		// Textfield for spritesheet name
		showTileNameField = false;
		tileNameField = new TextField(container,container.getDefaultFont(),200,200,100,30);
		
		ref = null;
		cellWidth=0;
		cellHeight=0;
		
		currentShape = SHAPE_SQUARE;
		currentTile = null;
		drawing=false;
		drawState = DRAW_STATE_NONE;
		highlightArrow = -1;
		rawTiles = new ArrayList<TileShape>();
		finishedTiles = new ArrayList<TileShape>();
		
		tempRect = new Rectangle(0,0,0,0);
		tempCircle = new Circle(0,0,0);
		tempPath = new Path(0, 0);
		
		rawTilesOffset = 0;
		finishedTilesOffset = 0;
		rawTilesX = 670;
		rawTilesY = 100;
		finishedTilesX = 20;
		finishedTilesY = 100;
		
		// Set left and right down arrow shape and position
		leftDownArrow = new Polygon();
		leftDownArrow.addPoint(0, 0);
		leftDownArrow.addPoint(25, 25);
		leftDownArrow.addPoint(50, 0);
		leftDownArrow = (Polygon) leftDownArrow.transform(Transform.createTranslateTransform(50, 540));
		rightDownArrow = (Polygon) leftDownArrow.transform(Transform.createTranslateTransform(650, 0));
		// Set left and right up arrow shape and position
		leftUpArrow = new Polygon();
		leftUpArrow.addPoint(0, 0);
		leftUpArrow.addPoint(25, -25);
		leftUpArrow.addPoint(50, 0);
		leftUpArrow = (Polygon) leftUpArrow.transform(Transform.createTranslateTransform(50, 50));
		rightUpArrow = (Polygon) leftUpArrow.transform(Transform.createTranslateTransform(650, 0));
		
		
	}

	@Override
	public void render(GameContainer container, StateBasedGame game, Graphics g)
			throws SlickException {
		g.setColor(Color.white);
		// draw left and right containing rectangles
		g.drawRect(0, 0, 150, 600);
		g.drawRect(650,0,150,600);
		// Draw arrows for moving raw or selected tile display up or down
		drawArrows(g);
		
		if (showTileNameField){
			g.setColor(Color.red);
			g.drawString("SpriteSheet Name:", 200, 170);
			tileNameField.render(container, g);
			g.setColor(Color.white);
		}
		
		// Draw raw tiles on right side of screen, four at a time, 
		// starting at the current offset as determined rawOffset, which is set with the arrows
		
		for (int i=0; (i+rawTilesOffset < rawTiles.size()) && (i<4);i++){
			g.drawImage(rawTiles.get(i+rawTilesOffset).getImage(),rawTilesX,rawTilesY+(i*100));
		}
		
		for (int i=0; (i+finishedTilesOffset < finishedTiles.size()) && (i<4);i++){
			g.drawImage(finishedTiles.get(i+finishedTilesOffset).getImage(),finishedTilesX,finishedTilesY+(i*100));
		}
		
		// draw the currently select tile in the middle of the screen
		if (currentTile != null && drawState != DRAW_STATE_DRAWN){
			g.drawImage(currentTile.getImage(),400-(currentTile.getImage().getWidth()/2),
					300-(currentTile.getImage().getHeight()/2));
		}
		
		if (currentTile != null){
			if (currentTile.getShape() != null){
				g.setColor(Color.red);
				g.draw(currentTile.getShape());
				g.setColor(Color.white);
			}
		}
		
		// Draw current temp shape while we work on currentTile
		if (drawState == DRAW_STATE_DRAWING){
			switch (currentShape){
			case SHAPE_SQUARE:
				g.setColor(Color.red);
				g.draw(tempRect);
				g.setColor(Color.white);
				break;
			case SHAPE_CIRCLE:
				g.setColor(Color.red);
				g.draw(tempCircle);
				g.setColor(Color.white);
				break;
			case SHAPE_PATH:
				g.setColor(Color.red);
				g.draw(tempPath);
              	g.setColor(Color.white);
				break;
			}
		}else if (drawState == DRAW_STATE_DRAWN){
			g.setColor(Color.red);
			g.draw(currentTile.getShape());
			g.setColor(Color.white);
		}
		
		
	}

	@Override
	public void update(GameContainer container, StateBasedGame game, int delta)
			throws SlickException {
		// change shape based on whether or not we're drawing, the position/clicks of the mouse,
		// and the currently selected shape to draw. 
		// NOTE: Remember, one shape/path per tile.
		if (drawState == DRAW_STATE_DRAWING){
			if (currentShape == this.SHAPE_SQUARE){
				tempRect = new Rectangle (startPointX, startPointY, mouseX-startPointX, mouseY-startPointY);
			}else if (currentShape == SHAPE_CIRCLE){
				tempCircle = new Circle(startPointX, startPointY, mouseX-startPointX);
			}
		}
		
	}
	
	@Override
	// Allows this state to accept input as though it were an instance of GameController
	public boolean isAcceptingInput(){
		return true;
	}
	
	@Override
	// Controls key press response
	public void keyPressed(int keyPressed, char keyChar){
		switch(keyPressed){
		case Input.KEY_L:
			TileSheetLoadDialog dialog = new TileSheetLoadDialog(null);
			dialog.showDialog();
			ref = dialog.getReference();
			cellWidth = dialog.getCellWidth();
			cellHeight = dialog.getCellHeight();
			int fileType = checkFileType(ref);
			if (fileType == FILE_TYPE_PNG){
				loadTileSheet(ref,cellWidth,cellHeight);
			}else if (fileType == FILE_TYPE_XML){
				loadXML(ref,cellWidth,cellHeight);
			}
			break;
		case Input.KEY_1:
			currentShape = SHAPE_SQUARE;
			break;
		case Input.KEY_2:
			currentShape = SHAPE_CIRCLE;
			break;
		case Input.KEY_3:
			currentShape = SHAPE_PATH;
			break;
		case Input.KEY_RETURN:
			commitShape();
			// show the tile name Field so we can enter a name for this newly made TileShape object
			showTileNameField = !showTileNameField;
			break;
		case Input.KEY_SPACE:
			currentTile.setTileName(tileNameField.getText());
			finishedTiles.add(currentTile);
			Log.info("CurrentTileName:"+currentTile.getTileName());
			currentTile = null;
			drawState = DRAW_STATE_NONE;
			// setTile field name to blank and turn off until next shape
			showTileNameField = !showTileNameField;
			tileNameField.setText("");
			tileNameField.deactivate();
			break;
		case Input.KEY_S:
			if (ref != null){
				fileType = checkFileType(ref);
				if (fileType == FILE_TYPE_PNG){
					XMLShapeOutput xso = new XMLShapeOutput(ref, cellWidth, cellHeight,finishedTiles.toArray(new TileShape[0]));
					xso.createOutputFile();
					Log.info("File:"+ref+".xml has been created successfully.");
				}else{
					String thisRef = ref.substring(0, ref.length()-4);
					XMLShapeOutput xso = new XMLShapeOutput(thisRef, cellWidth, cellHeight,finishedTiles.toArray(new TileShape[0]));
					xso.createOutputFile();
					Log.info("File:"+thisRef+".xml has been created successfully.");
				}
			}
			break;
		case Input.KEY_ESCAPE:
			this.container.exit();
			break;
		}
	}
	
	public int checkFileType(String fileName){
		String ext = fileName.substring(fileName.length()-3,fileName.length());
		Log.debug("Extension:"+ext);
		if (ext.equalsIgnoreCase("PNG")){
			return FILE_TYPE_PNG;
		}else if (ext.equalsIgnoreCase("XML")){
			return FILE_TYPE_XML;
		}
		return -1;
	}
	
	public void loadXML(String ref, int cellWidth, int cellHeight){
		String pngFileName = ref.substring(0, ref.length()-4);
		Log.debug("PngFileName:"+pngFileName);
		XMLShapePullParser x=null;
		try {
			x = new XMLShapePullParser(ResourceLoader.getResourceAsStream(ref),
					new SpriteSheet(pngFileName,cellWidth,cellHeight));
		} catch (SlickException e) {
			Log.error(e.getMessage());
		}
		finishedTiles = x.processXML();
		loadTileSheet(pngFileName,cellWidth,cellHeight);
		
		//Check to see which tiles are already in the finishedTiles list by comparing
		//tilesheet values, and remove any matches from the raw list
		for (int i=0;i<finishedTiles.size();i++){
			for (int z=0;z<rawTiles.size();z++){
				if (finishedTiles.get(i).getTileSheetX() == rawTiles.get(z).getTileSheetX() &&
						finishedTiles.get(i).getTileSheetY() == rawTiles.get(z).getTileSheetY()){
					rawTiles.remove(z);
				}
			}
		}
	}
	
	
	@Override
	public void mouseMoved(int oldx, int oldy, int newx, int newy) {
		// set mouse position
		mouseX = newx;
		mouseY = newy;
		// check for arrow highlighting
		highlightArrow = checkArrows(newx,newy);
	}
	
	@Override
	public void mousePressed(int button, int x, int y) {
		// Check for arrow clicks, and pass value to function controlling offset
		int arrowCheck = checkArrows(x,y);
		controlOffset(arrowCheck);
		
		// Check for raw image selection for shape building
		int rawCheck = rawImageCheck(x,y);
		//Log.debug("rCheck:"+rawCheck);
		setCurrentTile(rawCheck, true);
		
		// Check for finished image selection for shape refinement
		int finishCheck = finishedImageCheck(x,y);
		setCurrentTile(finishCheck,false);
		
		// Check for click within 50 pixels of currentTile to determine whether
		// we're trying to draw something or not
		boolean drawCheck = drawShapeCheck(x,y);
		if (drawCheck && currentShape == SHAPE_SQUARE && drawState == DRAW_STATE_NONE){
			drawState = DRAW_STATE_DRAWING;
			startPointX = x;
			startPointY = y;
		}else if (drawCheck && currentShape == SHAPE_CIRCLE && drawState == DRAW_STATE_NONE){
			drawState = DRAW_STATE_DRAWING;
			startPointX = x;
			startPointY = y;
			// Special efforts must be taken with the path tool, since it can draw multiple
			// lines while attempting to outline a polygon
		}else if (drawCheck && currentShape == SHAPE_PATH && drawState == DRAW_STATE_NONE){
			tempPath = new Path(x,y);
			drawState = DRAW_STATE_DRAWING;
			startPointX = x;
			startPointY = y;
		}else if (drawCheck && currentShape == SHAPE_PATH && drawState == DRAW_STATE_DRAWING){
			// add a new point
			this.tempPath.lineTo(mouseX, mouseY);
		}else{
			drawState = DRAW_STATE_NONE;
		}
	}
	
	/** Set the currentTile's shape to the shape we've been drawing */
	public void commitShape(){
		if (currentShape == SHAPE_SQUARE && drawState == DRAW_STATE_DRAWING){
			currentTile.setShape(tempRect);
			drawState = DRAW_STATE_DRAWN;
		}else if (currentShape == SHAPE_CIRCLE && drawState == DRAW_STATE_DRAWING){
			currentTile.setShape(tempCircle);
			drawState = DRAW_STATE_DRAWN;
		}else if (currentShape == SHAPE_PATH && drawState == DRAW_STATE_DRAWING){
			tempPath.close();
			Polygon thisPoly = new Polygon();
			float[] thisFloat = tempPath.getPoints();
			for (int i=0; i<thisFloat.length; i+=2){
				thisPoly.addPoint(thisFloat[i],thisFloat[i+1]);
			}
			currentTile.setShape(thisPoly);
			drawState = DRAW_STATE_DRAWN;
		}
	}
	
	public boolean drawShapeCheck(int x, int y){
		if (currentTile != null){
			int startX = 350;
			int endX = startX + currentTile.getImage().getWidth()+50;
			int startY = 250;
			int endY = startY + currentTile.getImage().getHeight()+50;
			if (x >= startX && x <= endX && y >= startY && y <= endY){
				return true;
			}
		}
		return false;
	}
	
	public void setCurrentTile(int i, boolean raw){
		if (i != -1){
			if (raw == true){
				currentTile = rawTiles.get(i+rawTilesOffset);
				rawTiles.remove(i+rawTilesOffset);
				//Log.debug("current Tile:"+(i+rawTilesOffset));
			}else{
				currentTile = finishedTiles.get(i+finishedTilesOffset);
				finishedTiles.remove(i+finishedTilesOffset);
			}
		}
	}
	
	public int finishedImageCheck(int x, int y){
		for (int i=0; (i+finishedTilesOffset < finishedTiles.size()) && (i<4);i++){
			int startX;
			int endX;
			int startY;
			int endY;
			startX = finishedTilesX;
			endX = startX + finishedTiles.get(i+finishedTilesOffset).getImage().getWidth();
			startY = finishedTilesY+(i*100);
			endY = startY + finishedTiles.get(i+finishedTilesOffset).getImage().getHeight();
			//Log.debug("sx:"+startX+" ex:"+endX+" sy:"+startY+" ey:"+endY);
			if (x >= startX && x <= endX && y >= startY && y <= endY){
				return i;
			}
		}
		return -1;
	}
	
	public int rawImageCheck(int x, int y){
		for (int i=0; (i+rawTilesOffset < rawTiles.size()) && (i<4);i++){
			int startX;
			int endX;
			int startY;
			int endY;
			startX = rawTilesX;
			endX = startX + rawTiles.get(i+rawTilesOffset).getImage().getWidth();
			startY = rawTilesY+(i*100);
			endY = startY + rawTiles.get(i+rawTilesOffset).getImage().getHeight();
			//Log.debug("sx:"+startX+" ex:"+endX+" sy:"+startY+" ey:"+endY);
			if (x >= startX && x <= endX && y >= startY && y <= endY){
				return i;
			}
		}
		return -1;
	}
	
	public void controlOffset(int arrowCheck){
		// depending on whats returned, change offset
		if (arrowCheck != -1){
			switch(arrowCheck){
			case HIGH_DOWN_LEFT:
				finishedTilesOffset += 1;
				break;
			case HIGH_UP_LEFT:
				if (finishedTilesOffset > 0){
					finishedTilesOffset -= 1;
				}
				break;
			case HIGH_DOWN_RIGHT:
				rawTilesOffset += 1;
				break;
			case HIGH_UP_RIGHT:
				if (rawTilesOffset > 0){
					rawTilesOffset -= 1;
				}
				break;
			}
		}
	}
	
	public int checkArrows(int x, int y){
		if (leftDownArrow.contains(x, y)){
			return HIGH_DOWN_LEFT;
		}else if (rightDownArrow.contains(x, y)){
			return HIGH_DOWN_RIGHT;
		}else if (leftUpArrow.contains(x, y)){
			return HIGH_UP_LEFT;
		}else if (rightUpArrow.contains(x, y)){
			return HIGH_UP_RIGHT;
		}
		return -1;
	}
	
	public void drawArrows(Graphics g){
		
		switch(highlightArrow){
		case HIGH_DOWN_LEFT:
			if (finishedTilesOffset < finishedTiles.size()){
				g.setColor(Color.red);
				g.fill(leftDownArrow);
			}else{
				g.setColor(Color.blue);
				g.fill(leftDownArrow);
			}
			g.setColor(Color.white);
			g.draw(rightDownArrow);
			g.draw(leftUpArrow);
			g.draw(rightUpArrow);
			break;
		case HIGH_DOWN_RIGHT:
			if (rawTilesOffset < rawTiles.size()){
				g.setColor(Color.red);
				g.fill(rightDownArrow);
			}else{
				g.setColor(Color.blue);
				g.fill(rightDownArrow);
			}
			g.setColor(Color.white);
			g.draw(leftDownArrow);
			g.draw(leftUpArrow);
			g.draw(rightUpArrow);
			break;
		case HIGH_UP_LEFT:
			if (finishedTilesOffset > 0){
				g.setColor(Color.red);
				g.fill(leftUpArrow);
			}else{
				g.setColor(Color.blue);
				g.fill(leftUpArrow);
			}
			g.setColor(Color.white);
			g.draw(leftDownArrow);
			g.draw(rightDownArrow);
			g.draw(rightUpArrow);
			break;
		case HIGH_UP_RIGHT:
			if (rawTilesOffset > 0){
				g.setColor(Color.red);
				g.fill(rightUpArrow);
			}else{
				g.setColor(Color.blue);
				g.fill(rightUpArrow);
			}
			g.setColor(Color.white);
			g.draw(leftUpArrow);
			g.draw(leftDownArrow);
			g.draw(rightDownArrow);
			break;
		default:
			g.draw(rightUpArrow);
			g.draw(leftUpArrow);
			g.draw(leftDownArrow);
			g.draw(rightDownArrow);
			break;
		}
			
	}
	
	public void loadTileSheet(String ref, int cellWidth, int cellHeight){
		// Try loading the tileSheet file
		try {
			tileSheet = new SpriteSheet(ref, cellWidth, cellHeight);
		} catch (SlickException e) {
			Log.error(e.getMessage());
			tileSheet = null;
		} catch (RuntimeException ex){
			Log.error(ex.getMessage());
			tileSheet = null;
		}
		if (tileSheet != null){
			//if loaded successfully, get the images of each tile on each layer,
			for (int yi=0;yi<tileSheet.getVerticalCount();yi++){	
				for (int xi=0;xi<tileSheet.getHorizontalCount();xi++){
					rawTiles.add(new TileShape("",null,tileSheet.getSprite(xi, yi),xi,yi));
				}
			}
		}
	}

	
}
