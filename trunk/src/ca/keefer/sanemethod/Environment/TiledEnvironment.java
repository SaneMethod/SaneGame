package ca.keefer.sanemethod.Environment;

import java.util.ArrayList;

import org.newdawn.slick.Color;
import org.newdawn.slick.Graphics;
import org.newdawn.slick.Image;
import org.newdawn.slick.SlickException;
import org.newdawn.slick.geom.GeomUtil;
import org.newdawn.slick.geom.Rectangle;
import org.newdawn.slick.geom.Shape;
import org.newdawn.slick.tiled.TiledMap;
import org.newdawn.slick.util.Log;

import net.phys2d.math.ROVector2f;
import net.phys2d.math.Vector2f;
import net.phys2d.raw.Body;
import net.phys2d.raw.BodyList;
import net.phys2d.raw.CollisionEvent;
import net.phys2d.raw.CollisionListener;
import net.phys2d.raw.StaticBody;
import net.phys2d.raw.shapes.Polygon;

import ca.keefer.sanemethod.Constants;
import ca.keefer.sanemethod.LevelBuilder.MapShape;
import ca.keefer.sanemethod.LevelBuilder.XMLShapePullParser;

/**
 * This class helps bridge the gap between the Phys2D static bodies and the TilEd tmx maps
 * (thus the moniker). It relies on the MapShape class to provide a shape for a staticbody object.
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
	/** The utility used to combine tile shapes */
	private GeomUtil util = new GeomUtil();
	/** The width in pixels of each tile - set from the TilEd map */
	private int tileWidth;
	/** The height in pixels of each tile - set from the TilEd map */
	private int tileHeight;
	/** The bounds of the entire environment - set from the TilEd map */
	private Rectangle bounds;
	/** ViewPort for this game */
	ViewPort viewPort;
	
	
	public TiledEnvironment(String mapFile, ArrayList<MapShape> tileList, ViewPort viewPort) {
		this.viewPort=viewPort;
			if (tileList != null){
				try {
					buildStatic(mapFile, tileList);
				} catch (SlickException e) {
					Log.error(e.getMessage());
				}
			}else{
				try {
					buildDynamic(mapFile);
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
	 * Dynamically populate the shapes array with polygons based on
	 * the presence of tiles on the collision layer (named "Collision")<br>
	 * TODO:For now, makes only boxes - consider enabling this to handle 
	 * more complex shapes as well
	 * @param mapFile
	 * @throws SlickException
	 */
	public void buildDynamic(String mapFile) throws SlickException{
		tiledMap = new TiledMap(mapFile,true);
		this.width = tiledMap.getWidth();
		this.height = tiledMap.getHeight();
		this.layers = tiledMap.getLayerCount();
		this.tileWidth = tiledMap.getTileWidth();
		this.tileHeight = tiledMap.getTileHeight();
		ArrayList<Shape> shapeList = new ArrayList<Shape>();
		
		// Populate shapes array with polygons based on tile presence
		// on Collision Layer
		int layerID = tiledMap.getLayerIndex("Collision");
		for (int x=0;x<this.width;x++){
			for (int y=0;y<this.height;y++){
				if (tiledMap.getTileImage(x, y, layerID) != null){
					shapeList.add(new Rectangle(x*Constants.TILE_WIDTH,y*Constants.TILE_HEIGHT,
							Constants.TILE_WIDTH, Constants.TILE_HEIGHT));
				}
			}
		}
		
		// init arrays
		shapes = new Shape[shapeList.size()];
		for (int i=0;i<shapeList.size();i++){
			shapes[i] = shapeList.get(i);
		}
		
		this.init();
	}
	
	
	/**
	 * Initialise the tile map
	 */
	public void init() {
		world.addListener(new CollisionEcho());
		viewPort.setTiledDimensions(this.width*Constants.TILE_WIDTH, this.height*Constants.TILE_HEIGHT);
		buildSimpleSection();
		buildLayers();
		
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
		
		// Build Tile Layers 4+
		for (int i=3;i<layers;i++){
			if (i != colLayerID){
				TileLayer tLayer = new TileLayer(tiledMap,i,i+layerOffset, true);
				viewPort.attachLayer(tLayer);
				layerOffset +=1;
			}
		}
		
		//create any foreground effect/image/whatever layers
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
				g.translate(-list.get(i).getPosition().getX(), 
						-list.get(i).getPosition().getY());
				g.draw(p);
				g.translate(list.get(i).getPosition().getX(), 
						list.get(i).getPosition().getY());
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
			}else if (eLayer.getEntityList().get(i).getBody().getShape() instanceof Polygon) {
				Polygon poly = (Polygon) eLayer.getEntityList().get(i).getBody().getShape();
				org.newdawn.slick.geom.Polygon p = new org.newdawn.slick.geom.Polygon();
				ROVector2f[] verts = poly.getVertices();
				for (int k=0;k<verts.length;k++) {
					p.addPoint(verts[k].getX(), verts[k].getY());
				}
				g.translate(-list.get(i).getPosition().getX(), 
						-list.get(i).getPosition().getY());
				g.draw(p);
				g.translate(list.get(i).getPosition().getX(), 
						list.get(i).getPosition().getY());
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
		}
		
	}
	
}