package ca.keefer.sanemethod.LevelBuilder;

import java.util.ArrayList;

import org.newdawn.slick.Color;
import org.newdawn.slick.GameContainer;
import org.newdawn.slick.Graphics;
import org.newdawn.slick.Input;
import org.newdawn.slick.SlickException;
import org.newdawn.slick.geom.Circle;
import org.newdawn.slick.geom.Path;
import org.newdawn.slick.geom.Polygon;
import org.newdawn.slick.geom.Rectangle;
import org.newdawn.slick.gui.TextField;
import org.newdawn.slick.state.BasicGameState;
import org.newdawn.slick.state.StateBasedGame;
import org.newdawn.slick.tiled.TiledMap;
import org.newdawn.slick.util.Log;
import org.newdawn.slick.util.ResourceLoader;

import ca.keefer.sanemethod.Constants;

/**
 * This class allows me to draw paths/polygons overlaying individual elements of a tile map,
 * and save the points of the polygon as a text representation of an array of floats in an xml
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
 * <MapShapes name="name" map="tileMap.tmx">
 * 	<!-- Point count refers to how many pairs of x,y float values there are to parse for a given Map Element -->
 *  <!-- mapX/Y refer to the upper left hand corner of the shape of this element -->a	
 * 	<Element mapX="x" mapY="y" pointCount="###" point0X="x" point0Y="y" point1X="x" ... />
 * 	...
 * </MapShapes>
 */
/* Interface Behaviour:
 * General: Load a tile map, and draw the outline of the desired shapes around map elements, taking care not
 * to overlap them. There are no moving elements in a map - all such elements are Sprites, not tiles, and
 * are handled seperately.
 * Hot keys: s = save the xml file; l = load (pops up dialog box to type string for next tilemap to load; 
 * 1 = square; 2 = circle; 3 = Path/polygon; 
 * right key = scroll map right; left key = scroll map left; up key = scroll map up; down key = scroll map down;
 * enter = commit current shape to list; , = increase map scroll speed; . = decrease map scroll speed;
 * F1->F10 = toggle layers 0-9 on/off;
 */
public class LevelShapeBuilder extends BasicGameState{

	int stateID;
	
	// Global variables for editor environment
	
	// TMX Map
	TiledMap tiledMap;
	// Boolean array determining whether layers are drawn or not
	boolean[] displayLayer;
	
	// Self-referencing
	StateBasedGame game;
	GameContainer container;
	// Input object
	Input input;
	
	// Simple OPENGL GUI for text fields
	boolean showFileNameField;
	Rectangle fileNameRectangle;
	TextField fileNameField;
	
	String ref;
	
	// File type constants
	final int FILE_TYPE_TMX = 0;
	final int FILE_TYPE_XML = 1;
	
	// Temporary holder shapes for any drawn shapes
	Rectangle tempRect;
	Circle tempCircle;
	Path tempPath;
	
	// Viewpoint translation and scroll variables
	int viewOffsetX;
	int viewOffsetY;
	int scrollSpeed;
	
	// Mouse position and shape drawing variables
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
	ArrayList<MapShape> shapeList;
	int currentShape;
	final int SHAPE_SQUARE = 0;
	final int SHAPE_CIRCLE = 1;
	final int SHAPE_PATH = 2;
	
	public LevelShapeBuilder(int id){
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
		input = new Input(Constants.SCREENHEIGHT);
		this.setInput(input);
		input.enableKeyRepeat();
		tiledMap=null;
		scrollSpeed=10;
		shapeList = new ArrayList<MapShape>();
		
		// Viewpoint translation variables
		viewOffsetX=0;
		viewOffsetY=0;
		
		// Textfield for spritesheet name
		showFileNameField = false;
		fileNameRectangle = new Rectangle(0+viewOffsetX,230+viewOffsetY,800,100);
		fileNameField = new TextField(container,container.getDefaultFont(),200+viewOffsetX,260+viewOffsetY,200,30);
		
		ref = null;
		
		currentShape = SHAPE_SQUARE;
		drawing=false;
		drawState = DRAW_STATE_NONE;
		
		tempRect = new Rectangle(0,0,0,0);
		tempCircle = new Circle(0,0,0);
		tempPath = new Path(0, 0);
		
		
	}

	@Override
	public void render(GameContainer container, StateBasedGame game, Graphics g)
			throws SlickException {
		g.translate(viewOffsetX, viewOffsetY);
		g.setColor(Color.white);
		
		if (tiledMap != null){
			for (int i=0;i<tiledMap.getLayerCount();i++){
				if (displayLayer[i]==true){
					tiledMap.render(0, 0, i);
				}
			}
		}
		
		if (showFileNameField){
			g.setColor(Color.white);
			g.fill(fileNameRectangle);
			g.setColor(Color.red);
			g.drawString("File Name:", 200+viewOffsetX, 240+viewOffsetY);
			fileNameField.render(container, g);
			g.setColor(Color.white);
		}
		
		// draw current shapes
		for (int i=0;i<shapeList.size();i++){
			g.draw(shapeList.get(i).getShape());
		}
		
		// Draw current temp shape while we work on the currentShape
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
		}
		
		
	}

	@Override
	public void update(GameContainer container, StateBasedGame game, int delta)
			throws SlickException {
		// change shape based on whether or not we're drawing, the position/clicks of the mouse,
		// and the currently selected shape to draw. 
		if (drawState == DRAW_STATE_DRAWING){
			if (currentShape == this.SHAPE_SQUARE){
				tempRect = new Rectangle (startPointX-viewOffsetX, startPointY-viewOffsetY, mouseX-startPointX, mouseY-startPointY);
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
		int fileType;
		switch(keyPressed){
		case Input.KEY_L:
			showFileNameField=true;
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
			if (showFileNameField == true){
				ref = fileNameField.getText();
				fileType = checkFileType(ref);
				if (fileType == FILE_TYPE_TMX){
					loadMap(ref);
				}else if (fileType == FILE_TYPE_XML){
					loadXML(ref);
				}
				showFileNameField=false;
			}else{
				commitShape();
			}
			break;
		case Input.KEY_S:
			if (ref != null){
				fileType = checkFileType(ref);
				if (fileType == FILE_TYPE_TMX){
					XMLShapeOutput xso = new XMLShapeOutput(ref,shapeList.toArray(new MapShape[0]));
					xso.createOutputFile();
					Log.info("File:"+ref+".xml has been created successfully.");
				}else{
					String thisRef = ref.substring(0, ref.length()-4);
					XMLShapeOutput xso = new XMLShapeOutput(thisRef,shapeList.toArray(new MapShape[0]));
					xso.createOutputFile();
					Log.info("File:"+thisRef+".xml has been created successfully.");
				}
			}
			break;
		case Input.KEY_LEFT:
			viewOffsetX += scrollSpeed;
			break;
		case Input.KEY_RIGHT:
			viewOffsetX -= scrollSpeed;
			break;
		case Input.KEY_UP:
			viewOffsetY += scrollSpeed;
			break;
		case Input.KEY_DOWN:
			viewOffsetY -= scrollSpeed;
			break;
		case Input.KEY_COMMA:
			scrollSpeed +=1;
			break;
		case Input.KEY_PERIOD:
			scrollSpeed -=1;
			break;
		case Input.KEY_F1:
			if (displayLayer !=null){
				if (displayLayer.length >=1){
					displayLayer[0]=!displayLayer[0];
				}
			}
			break;
		case Input.KEY_F2:
			if (displayLayer !=null){
				if (displayLayer.length >=2){
					displayLayer[1]=!displayLayer[1];
				}
			}
			break;
		case Input.KEY_F3:
			if (displayLayer !=null){
				if (displayLayer.length >=3){
					displayLayer[2]=!displayLayer[2];
				}
			}
			break;
		case Input.KEY_F4:
			if (displayLayer !=null){
				if (displayLayer.length >=4){
					displayLayer[3]=!displayLayer[3];
				}
			}
			break;
		case Input.KEY_F5:
			if (displayLayer !=null){
				if (displayLayer.length >=5){
					displayLayer[4]=!displayLayer[4];
				}
			}
			break;
		case Input.KEY_F6:
			if (displayLayer !=null){
				if (displayLayer.length >=6){
					displayLayer[5]=!displayLayer[5];
				}
			}
			break;
		case Input.KEY_F7:
			if (displayLayer !=null){
				if (displayLayer.length >=7){
					displayLayer[6]=!displayLayer[6];
				}
			}
			break;
		case Input.KEY_F8:
			if (displayLayer !=null){
				if (displayLayer.length >=8){
					displayLayer[7]=!displayLayer[7];
				}
			}
			break;
		case Input.KEY_F9:
			if (displayLayer !=null){
				if (displayLayer.length >=9){
					displayLayer[8]=!displayLayer[8];
				}
			}
			break;
		case Input.KEY_F10:
			if (displayLayer !=null){
				if (displayLayer.length >=10){
					displayLayer[9]=!displayLayer[9];
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
		if (ext.equalsIgnoreCase("TMX")){
			return FILE_TYPE_TMX;
		}else if (ext.equalsIgnoreCase("XML")){
			return FILE_TYPE_XML;
		}
		return -1;
	}
	
	public void loadMap(String ref){
		try {
			tiledMap = new TiledMap(ref,true);
			displayLayer = new boolean[tiledMap.getLayerCount()];
			for (int i=0;i<displayLayer.length;i++){
				displayLayer[i]=true;
			}
		} catch (SlickException e) {
			Log.error(e.getMessage());
		} catch (RuntimeException ex){
			Log.error("TiledMap failed to load:"+ex.getMessage());
		}
	}
	
	public void loadXML(String ref){
		String tmxFileName = ref.substring(0, ref.length()-4);
		Log.debug("TMXFileName:"+tmxFileName);
		XMLShapePullParser x=null;
		try {
			x = new XMLShapePullParser(ResourceLoader.getResourceAsStream(ref));
			tiledMap = new TiledMap(tmxFileName,true);
			displayLayer = new boolean[tiledMap.getLayerCount()];
			for (int i=0;i<displayLayer.length;i++){
				displayLayer[i]=true;
			}
		} catch (SlickException e) {
			Log.error(e.getMessage());
		}
		shapeList = x.processXML();
	}
	
	
	@Override
	public void mouseMoved(int oldx, int oldy, int newx, int newy) {
		// set mouse position
		mouseX = newx;
		mouseY = newy;
	}
	
	@Override
	public void mousePressed(int button, int x, int y) {
		
		if (!showFileNameField){
			if (currentShape == SHAPE_SQUARE && drawState == DRAW_STATE_NONE){
				drawState = DRAW_STATE_DRAWING;
				startPointX = x;
				startPointY = y;
			}else if (currentShape == SHAPE_CIRCLE && drawState == DRAW_STATE_NONE){
				drawState = DRAW_STATE_DRAWING;
				startPointX = x;
				startPointY = y;
				// Special efforts must be taken with the path tool, since it can draw multiple
				// lines while attempting to outline a polygon
			}else if (currentShape == SHAPE_PATH && drawState == DRAW_STATE_NONE){
				tempPath = new Path(x-viewOffsetX,y-viewOffsetY);
				drawState = DRAW_STATE_DRAWING;
				startPointX = x;
				startPointY = y;
			}else if (currentShape == SHAPE_PATH && drawState == DRAW_STATE_DRAWING){
				// add a new point
				this.tempPath.lineTo(mouseX-viewOffsetX, mouseY-viewOffsetY);
			}else{
				drawState = DRAW_STATE_NONE;
			}
		}
	}
	
	/** Add the drawn shape to the list and reset the drawing state */
	public void commitShape(){
		if (currentShape == SHAPE_SQUARE && drawState == DRAW_STATE_DRAWING){
			shapeList.add(new MapShape(tempRect));
			drawState = DRAW_STATE_NONE;
		}else if (currentShape == SHAPE_CIRCLE && drawState == DRAW_STATE_DRAWING){
			shapeList.add(new MapShape(tempCircle));
			drawState = DRAW_STATE_DRAWN;
		}else if (currentShape == SHAPE_PATH && drawState == DRAW_STATE_DRAWING){
			tempPath.close();
			Polygon thisPoly = new Polygon();
			float[] thisFloat = tempPath.getPoints();
			for (int i=0; i<thisFloat.length; i+=2){
				thisPoly.addPoint(thisFloat[i],thisFloat[i+1]);
			}
			shapeList.add(new MapShape(thisPoly));
			Log.debug("Xoffset:"+viewOffsetX+" Yoffset:"+viewOffsetY);
			Log.debug("PolyX:"+thisPoly.getX()+" PolyY:"+thisPoly.getY());
			drawState = DRAW_STATE_NONE;
		}
	}
	
}
