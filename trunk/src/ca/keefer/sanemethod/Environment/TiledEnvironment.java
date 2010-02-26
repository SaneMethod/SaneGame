package ca.keefer.sanemethod.Environment;

import java.util.ArrayList;

import org.newdawn.slick.Color;
import org.newdawn.slick.Graphics;
import org.newdawn.slick.Image;
import org.newdawn.slick.SlickException;
import org.newdawn.slick.SpriteSheet;
import org.newdawn.slick.geom.Path;
import org.newdawn.slick.geom.Rectangle;
import org.newdawn.slick.geom.Shape;
import org.newdawn.slick.tiled.TiledMap;
import org.newdawn.slick.util.Log;
import org.newdawn.slick.util.ResourceLoader;

import net.phys2d.math.ROVector2f;
import net.phys2d.math.Vector2f;
import net.phys2d.raw.Body;
import net.phys2d.raw.BodyList;
import net.phys2d.raw.CollisionEvent;
import net.phys2d.raw.CollisionListener;
import net.phys2d.raw.StaticBody;
import net.phys2d.raw.shapes.Polygon;

import ca.keefer.sanemethod.Constants;
import ca.keefer.sanemethod.Entity.ClamMook;
import ca.keefer.sanemethod.Entity.Coin;
import ca.keefer.sanemethod.Entity.Door;
import ca.keefer.sanemethod.Entity.ExitBubble;
import ca.keefer.sanemethod.Entity.FallingBlock;
import ca.keefer.sanemethod.Entity.JumpingMook;
import ca.keefer.sanemethod.Entity.Platformer;
import ca.keefer.sanemethod.Entity.Player;
import ca.keefer.sanemethod.Entity.Spike;
import ca.keefer.sanemethod.Entity.Spring;
import ca.keefer.sanemethod.Entity.Switch;
import ca.keefer.sanemethod.Entity.Talkable;
import ca.keefer.sanemethod.Entity.Waypoint;
import ca.keefer.sanemethod.LevelBuilder.MapShape;
import ca.keefer.sanemethod.LevelBuilder.XMLShapePullParser;
import ca.keefer.sanemethod.Tools.TextXMLPullParser;

/**
 * This class helps bridge the gap between the Phys2D static bodies and the TilEd tmx maps. 
 * It relies on the MapShape class to provide a shape for a staticbody object.
 * @author Christopher Keefer, Kevin Glass
 * @version 1.3
 * @see ca.keefer.sanemethod.XMLShapePullParser
 * @see ca.keefer.sanemethod.MapShape
 */

public class TiledEnvironment extends AbstractEnvironment{

	/** The tmx file which we'll reference for tile information */
	TiledMap tiledMap;
	/** The SAX Pull Parser for tileShape information */
	XMLShapePullParser xmlShapePullParser;
	/** The shapes to use in the physical world - limited to a single layer (layer 2) 
	 * as layer 0 and 1 are below the character, and 3+ are above the character, and thus will not physically 
	 * be interacted with.  */
	Shape[] shapes;
	/** The width in tiles of the environment */
	private int width;
	/** The height in tiles of the environment */
	private int height;
	/** The number of layers in the tile environment */
	private int layers;
	/** The width in pixels of each tile - set from the TilEd map */
	private int tileWidth;
	/** The height in pixels of each tile - set from the TilEd map */
	private int tileHeight;
	/** The bounds of the entire environment - set from the TilEd map */
	private Rectangle bounds;
	/** ViewPort for this game */
	ViewPort viewPort;
	/** SpriteSheets for the various possible map-defined entities */
	SpriteSheet springSheet;
	SpriteSheet switchSheet;
	SpriteSheet doorSheet;
	SpriteSheet playerSheet;
	Image ballImage;
	Image blockImage;
	Image spikeImage;
	SpriteSheet talkableSheet;
	SpriteSheet jMookSheet;
	SpriteSheet cMookSheet;
	SpriteSheet waySheet;
	SpriteSheet exitSheet;
	/** Player entity created set by this map */
	Player thePlayer;
	/** HudLayer created for this map if map property HUD == true */
	HudLayer hudLayer;
	/** ids for identifying the upper and lower boundaries of the map */
	int lowerID, upperID;
	/** whether the upper and lower boundaries should be considered lethal */
	boolean boundariesLethal;
	/** the music for this map */
	String theMusic;
	
	
	public TiledEnvironment(String mapFile, ArrayList<MapShape> tileList, ViewPort viewPort) {
		this.viewPort=viewPort;
		world.clear();
		try {
			springSheet = new SpriteSheet("res/Sprites/Jellyfish.png",128,128);
			doorSheet = switchSheet = new SpriteSheet("res/Tiles/Blocks.png",64,64);
			playerSheet = new SpriteSheet("res/Sprites/Player.png",96,96);
			ballImage = new Image("/res/ball.png");
			spikeImage = new Image("/res/Tiles/Spike.png");
			talkableSheet = new SpriteSheet("res/Sprites/WilloWisp.png",64,64);
			exitSheet = new SpriteSheet("res/Sprites/ExitBubble.png",128,128);
			jMookSheet = new SpriteSheet("res/Sprites/MobA.png",96,112);
			cMookSheet = new SpriteSheet("res/Sprites/Reserve_MobC.png",128,128);
			waySheet = new SpriteSheet("res/Sprites/waypoint.png",96,89);
		} catch (SlickException e1) {
			Log.error("Failed to load sprite sheet:"+e1.getMessage());
		}
			if (tileList != null){
				try {
					buildStatic(mapFile, tileList);
				} catch (SlickException e) {
					Log.error(e.getMessage());
				}
			}else{
				try {
					buildOnlyBorders(mapFile);
				}catch (SlickException e){
					Log.error(e.getMessage());
				}
			}
	}
	
	/**
	 * Build the physical shapes of the map based on a static XML file
	 * @param mapFile
	 * @param tileList
	 * @throws SlickException
	 */
	public void buildStatic(String mapFile, ArrayList<MapShape> tileList)throws SlickException{
		// Get the specified tiledMap
		tiledMap = new TiledMap(mapFile, true);
		this.width = tiledMap.getWidth();
		this.height = tiledMap.getHeight();
		this.layers = tiledMap.getLayerCount();
		this.tileWidth = tiledMap.getTileWidth();
		this.tileHeight = tiledMap.getTileHeight();
		
		// init arrays
		shapes = new Shape[tileList.size()];
		for (int i=0;i<tileList.size();i++){
			shapes[i] = tileList.get(i).getShape();
		}
		
		this.init();
	}
	
	/**
	 * Builds a map with only the borders filled in with static bodies
	 * @param mapFile
	 * @throws SlickException
	 */
	public void buildOnlyBorders(String mapFile) throws SlickException{
		tiledMap = new TiledMap(mapFile,true);
		this.width = tiledMap.getWidth();
		this.height = tiledMap.getHeight();
		this.layers = tiledMap.getLayerCount();
		this.tileWidth = tiledMap.getTileWidth();
		this.tileHeight = tiledMap.getTileHeight();
		
		// init arrays
		shapes = new Shape[0];
		
		this.init();
	}
	
	
	/**
	 * Initialise the tile map
	 */
	public void init() {
		world.addListener(new CollisionEcho());
		viewPort.setTiledDimensions(this.width*Constants.TILE_WIDTH, this.height*Constants.TILE_HEIGHT);
		buildBorders();
		buildSimpleSection();
		buildLayers();
		
	}
	
	private void buildBorders(){
		// Left Border
		Path border = new Path(-2,-2);
		border.lineTo(-2, this.height*Constants.TILE_HEIGHT);
		border.lineTo(0, this.height*Constants.TILE_HEIGHT);
		border.lineTo(0, -2);
		border.lineTo(-2, -2);
		border.close();
		
		float[] pts = border.getPoints();
		Vector2f[] vecs = new Vector2f[(pts.length / 2)];
		for (int j=0;j<vecs.length;j++) {
			vecs[j] = new Vector2f(pts[j*2],pts[(j*2)+1]);
		}
		StaticBody body = new StaticBody("leftBorder",new Polygon(vecs));
		body.setFriction(0f);
		body.setRestitution(1f);
		world.add(body);
		
		// Right Border
		border = new Path((this.width*Constants.TILE_WIDTH)+2,-2);
		border.lineTo((this.width*Constants.TILE_WIDTH)+2, this.height*Constants.TILE_HEIGHT);
		border.lineTo((this.width*Constants.TILE_WIDTH), this.height*Constants.TILE_HEIGHT);
		border.lineTo((this.width*Constants.TILE_WIDTH), -2);
		border.lineTo((this.width*Constants.TILE_WIDTH)+2, -2);
		border.close();
		
		pts = border.getPoints();
		vecs = new Vector2f[(pts.length / 2)];
		for (int j=0;j<vecs.length;j++) {
			vecs[j] = new Vector2f(pts[j*2],pts[(j*2)+1]);
		}
		body = new StaticBody("rightBorder",new Polygon(vecs));
		body.setFriction(0f);
		body.setRestitution(1f);
		world.add(body);
		
		// Upper Border
		border = new Path(0,-2);
		border.lineTo((this.width*Constants.TILE_WIDTH), -2);
		border.lineTo((this.width*Constants.TILE_WIDTH), 0);
		border.lineTo(0, 0);
		border.lineTo(0, -2);
		border.close();
		
		pts = border.getPoints();
		vecs = new Vector2f[(pts.length / 2)];
		for (int j=0;j<vecs.length;j++) {
			vecs[j] = new Vector2f(pts[j*2],pts[(j*2)+1]);
		}
		body = new StaticBody("upperBorder",new Polygon(vecs));
		upperID = body.getID();
		body.setFriction(0f);
		body.setRestitution(1f);
		world.add(body);
		
		// Lower Border
		border = new Path(0,(this.height*Constants.TILE_HEIGHT)+2);
		border.lineTo((this.width*Constants.TILE_WIDTH), (this.height*Constants.TILE_HEIGHT)+2);
		border.lineTo((this.width*Constants.TILE_WIDTH), (this.height*Constants.TILE_HEIGHT));
		border.lineTo(0, (this.height*Constants.TILE_HEIGHT));
		border.lineTo(0, (this.height*Constants.TILE_HEIGHT)+2);
		border.close();
		
		pts = border.getPoints();
		vecs = new Vector2f[(pts.length / 2)];
		for (int j=0;j<vecs.length;j++) {
			vecs[j] = new Vector2f(pts[j*2],pts[(j*2)+1]);
		}
		body = new StaticBody("lowerBorder",new Polygon(vecs));
		lowerID = body.getID();
		body.setFriction(0f);
		body.setRestitution(1f);
		world.add(body);
		
	}
	
	private void buildSimpleSection(){
		for (int i=0;i<shapes.length;i++){
			Shape shape = shapes[i];
			if (shape != null){
				float[] pts = shape.getPoints();
				Vector2f[] vecs = new Vector2f[(pts.length / 2)];
				for (int j=0;j<vecs.length;j++) {
					vecs[j] = new Vector2f(pts[j*2],pts[(j*2)+1]);
				}
					
				Polygon poly = new Polygon(vecs);
				StaticBody body = new StaticBody(poly);
				Log.debug("Adding Body:"+body.getID());
				Log.debug("Body X:"+body.getPosition().getX()+" Body Width:"+body.getShape().getBounds().getWidth());
				body.setFriction(0f);
				body.setRestitution(1f);
				world.add(body);
			}
		}
	}
	
	public Player getPlayer(){
		return thePlayer;
	}
	
	public String getMusic(){
		return theMusic;
	}
	
	public void buildLayers(){
		// layerOffset controls how much to offset the layer ids, so they line up
		// with their render priorities
		int layerOffset = 0;
		// get the layer ID of the collision layer, so this won't be drawn
		int colLayerID = tiledMap.getLayerIndex("Collision");
		String backImage;
		// create any background image layer and increment layerOffset
		if ((backImage = tiledMap.getMapProperty("background", null)) != null){
			BackgroundLayer bg = new BackgroundLayer(layerOffset,true);
			try {
				bg.add(backImage);
			} catch (SlickException e) {
				Log.error("Failed to load background image");
			}
			viewPort.attachLayer(bg);
		}
		layerOffset+=1;
		// create any background effect layer and increment layerOffset
		layerOffset+=1;
		
		// Build Tile Layers 0->2 behind Entities
		for (int i=0; i<layers && i<3; i++){
			if (i != colLayerID){
				TileLayer tLayer = new TileLayer(tiledMap, i,i+layerOffset, true);
				viewPort.attachLayer(tLayer);
				layerOffset+=1;
			}
		}
		
		// Build Entity Layer 3
		eLayer = new EntityLayer(3+layerOffset,true);
		viewPort.attachLayer(eLayer);
		layerOffset+=1;
		
		// Add any Entities defined in the map on the objects layer
		for (int i=0;i<=tiledMap.getObjectGroupCount();i++){
			//Log.debug("Object groups found:"+tiledMap.getObjectGroupCount());
			for (int j=0;j<tiledMap.getObjectCount(i);j++){
				//Log.debug("Objects found:"+tiledMap.getObjectCount(i));
				//Log.debug("ObjectType:"+tiledMap.getObjectType(i, j));
			if (tiledMap.getObjectType(i, j).equals(Constants.OBJECT_PLAYER)){
				thePlayer = new Player(tiledMap.getObjectX(i, j), tiledMap.getObjectY(i,j),Constants.SHAPE_TYPE_CIRCLE,
						new Vector2f(50,50),5,0,0,new net.phys2d.math.Vector2f(30,50),true,4,playerSheet);
				this.addEntity(thePlayer);
			}else if (tiledMap.getObjectType(i,j).equals(Constants.OBJECT_BALL)){
				Platformer ball = new Platformer(tiledMap.getObjectX(i, j),tiledMap.getObjectY(i, j),
						Constants.SHAPE_TYPE_CIRCLE,new Vector2f(48,48),1,0,0,new net.phys2d.math.Vector2f(100,50),
						false,2,ballImage);
				this.addEntity(ball);
			}else if (tiledMap.getObjectType(i, j).equals(Constants.OBJECT_SPRING)){
					Spring thisSpring = new Spring(tiledMap.getObjectX(i, j),
							tiledMap.getObjectY(i, j)+19,10,1,1000,
							Boolean.parseBoolean(tiledMap.getObjectProperty(i, j, "Inverted", "false")),springSheet);
					this.addEntity(thisSpring);
				}else if (tiledMap.getObjectType(i, j).equals(Constants.OBJECT_SWITCH)){
					// FIXME: The way this is setup, the switch MUST appear first in the tmx file - it works, but is rough
					Switch thisSwitch = new Switch(Integer.parseInt(tiledMap.getObjectProperty(i, j, "refID", "-1")),
							tiledMap.getObjectX(i, j), tiledMap.getObjectY(i, j),1,
							Boolean.parseBoolean(tiledMap.getObjectProperty(i, j, "switchState", "true")),
							Boolean.parseBoolean(tiledMap.getObjectProperty(i, j, "staysDown", "true")),switchSheet);
					this.addEntity(thisSwitch);
				}else if (tiledMap.getObjectType(i, j).equals(Constants.OBJECT_DOOR)){
					// FIXME: The way this is setup, the switch MUST appear first in the tmx file - it works, but is rough
					// Get the switch connected to this door
					Switch switchRef=null;
					for (int x=0;x<eLayer.getEntityList().size();x++){
						if (eLayer.getEntity(x).getClass() == Switch.class){
							Switch tempSwitch = (Switch) eLayer.getEntity(x);
							if (tempSwitch.getRefID() == 
								Integer.parseInt(tiledMap.getObjectProperty(i, j, "switchRef", "-1"))){
								switchRef=tempSwitch;
								break;
							}
						}
					}
					
					Door thisDoor = new Door(tiledMap.getObjectX(i, j), tiledMap.getObjectY(i, j)-
							((tiledMap.getObjectHeight(i, j)-1)*64),
							tiledMap.getObjectWidth(i, j),tiledMap.getObjectHeight(i, j),1,switchRef.getSwitchState(),
							switchRef,doorSheet);
					this.addEntity(thisDoor);
				}else if (tiledMap.getObjectType(i, j).equals(Constants.OBJECT_COIN)){
					Coin thisCoin = new Coin(tiledMap.getObjectX(i, j), tiledMap.getObjectY(i, j),1);
					this.addEntity(thisCoin);
				}else if (tiledMap.getObjectType(i,j).equals(Constants.OBJECT_JMOOK)){
					//Log.debug("Making jMook");
					JumpingMook jMook= new JumpingMook(tiledMap.getObjectX(i, j), tiledMap.getObjectY(i, j),40,
							Integer.parseInt(tiledMap.getObjectProperty(i, j, "xLower", "-1")),
							Integer.parseInt(tiledMap.getObjectProperty(i, j, "xUpper", "-1")),1,true,jMookSheet);
					this.addEntity(jMook);
				}else if (tiledMap.getObjectType(i,j).equals(Constants.OBJECT_CMOOK)){
					ClamMook clamMook = new ClamMook(tiledMap.getObjectX(i, j), tiledMap.getObjectY(i, j),1,true,cMookSheet);
					this.addEntity(clamMook);
				}else if (tiledMap.getObjectType(i,j).equals(Constants.OBJECT_FALLING_BLOCK)){
					FallingBlock fallingBlock = new FallingBlock(tiledMap.getObjectX(i, j), tiledMap.getObjectY(i, j),
							Integer.parseInt(tiledMap.getObjectProperty(i, j, "dissolvePeriod", "1000")),1,doorSheet.getSubImage(3, 3));
					this.addEntity(fallingBlock);
				}else if (tiledMap.getObjectType(i,j).equals(Constants.OBJECT_SPIKE)){
					Spike spike = new Spike(tiledMap.getObjectX(i, j), tiledMap.getObjectY(i, j),
							1, Boolean.parseBoolean(tiledMap.getObjectProperty(i, j, "Inverted", "false")),spikeImage);
					this.addEntity(spike);				
				}else if (tiledMap.getObjectType(i,j).equals(Constants.EVENT_WAYPOINT)){
					Waypoint waypoint = new Waypoint(tiledMap.getObjectX(i, j),tiledMap.getObjectY(i, j),1,waySheet);
					this.addEntity(waypoint);
				}else if (tiledMap.getObjectType(i,j).equals(Constants.EVENT_EXIT)){
					ExitBubble exit = new ExitBubble(tiledMap.getObjectX(i, j),tiledMap.getObjectY(i, j),1,exitSheet);
					this.addEntity(exit);
				}else if (tiledMap.getObjectType(i,j).equals(Constants.EVENT_TALKABLE)){
					Talkable talkable = new Talkable(tiledMap.getObjectX(i, j),tiledMap.getObjectY(i, j),1,
							new TextXMLPullParser(ResourceLoader.getResourceAsStream(
									tiledMap.getObjectProperty(i,j,"dialogFile","res/Dialogs/errorText.xml"))).processDialog(),
							Short.parseShort(tiledMap.getObjectProperty(i,j,"position","0")),
									talkableSheet);
					this.addEntity(talkable);
				}
			}
		}
		
		// Build Tile Layers 4+
		for (int i=3;i<layers;i++){
			if (i != colLayerID){
				TileLayer tLayer = new TileLayer(tiledMap,i,i+layerOffset, true);
				viewPort.attachLayer(tLayer);
				layerOffset +=1;
			}
		}
		
		
		//create any foreground effect/image/whatever layers
		if (Boolean.parseBoolean(tiledMap.getMapProperty("HUD", "false"))){
			hudLayer = new HudLayer(thePlayer);
			viewPort.attachLayer(hudLayer);
		}
		boundariesLethal = Boolean.parseBoolean(tiledMap.getMapProperty("boundariesLethal","false"));
		theMusic = tiledMap.getMapProperty("music","res/Music/Isotope.mp3");
	}
	
	public void toggleHudLayer(){
		if (hudLayer != null){
			hudLayer.setActive(!hudLayer.isActive());
		}
	}
			

	@Override
	public Rectangle getBounds() {
		bounds = new Rectangle(0,0,width*tileWidth,height*tileHeight);
		return bounds;
	}

	/**
	 * @deprecated
	 * @see ca.keefer.sanemethod.Environment.ViewPort
	 */
	@Override
	public void render(Graphics g) {
		// render the bottom and parity layers of tile images
		for (int l=0;l<layers && l<3;l++){
			tiledMap.render(0, 0, l);
		}
		
		/*
		// Render entities
		for (int i=0;i<entities.size();i++) {
			entities.get(i).render(g);
		}
		*/
		
		// Render the above layers(layers 3+) of tiles
		for (int l=3;l<layers;l++){
			tiledMap.render(0,0,l);
		}
	}

	@Override
	public void renderBounds(Graphics g) {

		g.setColor(Color.red);
		for (int i=0;i<shapes.length;i++) {
				if (shapes[i] != null) {
					g.draw(shapes[i]);
				}
		}

		g.setColor(Color.yellow);
		g.setLineWidth(2);
		BodyList list = world.getBodies();
		for (int i=0;i<list.size();i++) {
			Body body = list.get(i);
			net.phys2d.raw.shapes.Shape shape = body.getShape();
			
			if (shape instanceof Polygon) {
				Polygon poly = (Polygon) shape;
				org.newdawn.slick.geom.Polygon p = new org.newdawn.slick.geom.Polygon();
				ROVector2f[] verts = poly.getVertices();
				for (int k=0;k<verts.length;k++) {
					p.addPoint(verts[k].getX(), verts[k].getY());
				}
				//g.translate(-list.get(i).getPosition().getX(), -list.get(i).getPosition().getY());
				g.draw(p);
				//g.translate(list.get(i).getPosition().getX(), list.get(i).getPosition().getY());
			}
		}

		g.setLineWidth(1);
		
		// Render entities
		
		for (int i=0;i<eLayer.getEntityList().size();i++) {
			if (eLayer.getEntityList().get(i).getBody().getShape() instanceof net.phys2d.raw.shapes.Circle){
				org.newdawn.slick.geom.Circle circle = new org.newdawn.slick.geom.Circle(eLayer.getEntityList().get(i).getBody().getPosition().getX(),
						eLayer.getEntityList().get(i).getBody().getPosition().getY(),eLayer.getEntityList().get(i).getBody().getShape().getBounds().getWidth()/2);
				g.draw(circle);
			}else if (eLayer.getEntityList().get(i).getBody().getShape() instanceof net.phys2d.raw.shapes.Box){
			org.newdawn.slick.geom.Rectangle box = new org.newdawn.slick.geom.Rectangle(eLayer.getEntityList().get(i).getBody().getPosition().getX(),
					eLayer.getEntityList().get(i).getBody().getPosition().getY(),eLayer.getEntityList().get(i).getBody().getShape().getBounds().getWidth(),
					eLayer.getEntityList().get(i).getBody().getShape().getBounds().getHeight());
			
				g.draw(box);
			}
		}
		
	}
	
	/**
	 * Inner class for responding to collisions on the map
	 * @author Christopher Keefer
	 *
	 */
	public class CollisionEcho implements CollisionListener{

		@Override
		public void collisionOccured(CollisionEvent event) {
			// TODO: Do Something when we encounter a collision?
			//Log.debug("Collision Occured:"+event.getTime());
			// base collision with upper and lower boundaries on their ids
			if ((event.getBodyA().getID() == lowerID || event.getBodyA().getID() == upperID)
					&& boundariesLethal){
				if (event.getBodyB().getUserData().getClass() == Player.class){
					Player p = (Player) event.getBodyB().getUserData();
					p.setLockOut(true);
					p.setDying(true);
				}
			}else if ((event.getBodyB().getID() == lowerID || event.getBodyB().getID() == upperID)
					&& boundariesLethal){
				if (event.getBodyA().getUserData().getClass() == Player.class){
					Player p = (Player) event.getBodyA().getUserData();
					p.setLockOut(true);
					p.setDying(true);
				}
			}
		}
		
	}
	
}
