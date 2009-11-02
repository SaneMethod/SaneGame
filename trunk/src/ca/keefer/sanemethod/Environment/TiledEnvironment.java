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

import ca.keefer.sanemethod.LevelBuilder.TileShape;
import ca.keefer.sanemethod.LevelBuilder.XMLShapePullParser;

/**
 * This class helps bridge the gap between the Phys2D static bodies and the TilEd tmx maps
 * (thus the moniker). It relies on the TileShape class to provide a shape for a staticbody object, 
 * which can be built from the TileShape object as soon as we know where the appropriate tile is going 
 * on the map. It's a little convoluted, but it should work *CrossFingersPrayToGod* 
 * @author Christopher Keefer, Kevin Glass
 * @version 1.3
 * @see ca.keefer.sanemethod.XMLShapePullParser
 * @see ca.keefer.sanemethod.TileShape
 */
/*
 *  HOW THIS WORKS: Each TileShape contains a name, supplied upon shape defining in the TileShapeBuilder state,
 *  which must be EXACTLY duplicated as a property for each tile of a tileSet in the TilEd editor.
 *  Format: Name = name Value = the name of the tile as entered in the TileShapeBuilder class (also,
 *  see the produced XML for reference - it SHOULD be in order, but we don't want to have to rely on that).
*/
public class TiledEnvironment extends AbstractEnvironment{

	/** The tmx file which we'll reference for tile information */
	TiledMap tiledMap;
	/** The SAX Pull Parser for tileShape information */
	XMLShapePullParser xmlShapePullParser;
	/** Gkobal Ids of all tiles in the map, on all layers */
	int[][][] GIDS;
	/** Images for each tile on the map, on all layers */
	Image[][][] images;
	/** The shapes to use in the physical world - limited to a single layer (layer 1) 
	 * as layer 0 and 2 are below and above the character respectively, and thus will not physically 
	 * be interacted with.  */
	Shape[][] shapes;
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
	
	
	public TiledEnvironment(String mapFile, ArrayList<TileShape> tileList, boolean buildStatic) {
		
		if (buildStatic){
			try {
				buildStatic(mapFile, tileList);
			} catch (SlickException e) {
				Log.error(e.getMessage());
			}
		}
	}
	
	public void buildStatic(String mapFile, ArrayList<TileShape> tileList)throws SlickException{
		// Get the specified tiledMap
		tiledMap = new TiledMap(mapFile, true);
		this.width = tiledMap.getWidth();
		this.height = tiledMap.getHeight();
		this.layers = tiledMap.getLayerCount();
		this.tileWidth = tiledMap.getTileWidth();
		this.tileHeight = tiledMap.getTileHeight();
		
		// init arrays
		images = new Image[layers][width][height];
		shapes = new Shape[width][height];
		
		// Get the Global ids of all the tiles defined in the map, on every layer
		GIDS = new int[tiledMap.getWidth()][tiledMap.getHeight()][tiledMap.getLayerCount()];
		for (int l=0;l<tiledMap.getLayerCount();l++){
			for (int x=0;x<tiledMap.getWidth();x++){
				for (int y=0;y<tiledMap.getHeight();y++){
					GIDS[x][y][l] = tiledMap.getTileId(x, y, l);
					
				/*
				 *  Now, the fun/SLOW part - get the names of each tile, and match it up with a 
				 *  TileShape object.
				 */
					String name = tiledMap.getTileProperty(GIDS[x][y][l], "name", "NULL");
					if (name.equals("NULL")){
						/*
						 * throw new RuntimeException("Tile name at Layer:"+l+", TileX:"+x+", TileY:"+y+
						 * " name is NULL//Not Defined.");
						 */
								
					}else{
						for (int i=0; i < tileList.size(); i++){
							if (tileList.get(i).getTileName().equals(name)){
								Log.debug("Found:"+tileList.get(i).getTileName());
								// OKAY! We've found a TileShape with the same name as this tile
								this.setTile(l, x, y, tileList.get(i));
								// That's it, folks!
								break;
							}
						}
					}
				}
			}
		}
		this.init();
		Log.debug(tiledMap.getTileProperty(GIDS[1][0][0], "name", "Not Defined"));
	}
	
	
	/**
	 * Set the tile at a given location
	 * Note: bounds are not checked.
	 * 
	 * @param x The x coordinate of the tile to set
	 * @param y The y coordinate of the tile to set
	 * @param tile The tile to apply at the given location
	 */
	public void setTile(int l, int x, int y, TileShape tile) {
		images[l][x][y] = tile.getImage();
		// SetCenterX/Y matches irregular shapes better than setX/Y
		tile.getShape().setCenterX((x*tileWidth)+(tileWidth/2));
		tile.getShape().setCenterY((y*tileHeight)+(tileHeight/2));
		shapes[x][y] = tile.getPolygon().copy();
	}
	
	/**
	 * Get a tile's image at the specified location
	 * @return Image The tile image
	 */
	public Image getTileImage(int l, int x, int y){
		return images[l][x][y];
	}
	
	/**
	 * Get a tile's shape from the specified part of the grid
	 * @ return shape The Shape of the tile
	 */
	public Shape getTileShape(int x, int y){
		return shapes[x][y];
	}
	
	
	/**
	 * Initialise the tile map
	 */
	public void init() {
		// Section the tile environment up into quads to improve performance. 
		// Currently using 4x4 quads.
		
		int sectionX = 4;
		int sectionY = 4;
		
		buildSimpleSection();
		
		/* FIXME: Currently doesn't produce expected result
		for (int x=0;x<width;x+=sectionX) {
			Log.debug("buildSectionX:"+x+" width:"+width);
			for (int y=0;y<height;y+=sectionY) {
				Log.debug("buildSectiony:"+y);
				buildSection(x,y,sectionX,sectionY);
			}
		}
		*/
		
	}
	
	private void buildSimpleSection(){
		for (int x=0;x<width;x++){
			for(int y=0;y<height;y++){
				Shape shape = shapes[x][y];
				if (shape != null){
					float[] pts = shape.getPoints();
					Vector2f[] vecs = new Vector2f[(pts.length / 2)];
					for (int j=0;j<vecs.length;j++) {
						vecs[j] = new Vector2f(pts[j*2],pts[(j*2)+1]);
					}
					
					Polygon poly = new Polygon(vecs);
					StaticBody body = new StaticBody(poly);
					Log.debug("Adding Body:"+body.getID());
					Log.debug("Body X:"+body.getShape().getBounds().getOffsetX()+" Body Width:"+body.getShape().getBounds().getWidth());
					body.setFriction(1f);
					body.setRestitution(1f);
					world.add(body);
				}
			}
		}
	}
			
	
	
	/**
	 * Build a section of the map by combining the tile shapes 
	 * 
	 * @param xp The x position of the section to build
	 * @param yp The y position of the section to build 
	 * @param width The width of the section to build
	 * @param height The height of the section to build
	 */
	private void buildSection(int xp, int yp, int width, int height) {
		// go through all the shapes in the section building a list
		ArrayList<Shape> shapes = new ArrayList<Shape>();
		for (int x=0;x<width;x++) {
			for (int y=0;y<height;y++) {
				if (xp+x >= this.width) {
					continue;
				}
				if (yp+y >= this.height) {
					continue;
				}
				
				if (this.shapes[xp+x][yp+y] != null) {
					shapes.add(this.shapes[xp+x][yp+y]);
				}
			}
		}
		
		// combine the shapes together then build a static 
		// body for resulting shape
		ArrayList<Shape> combines = combine(shapes);
		for (int i=0;i<combines.size();i++) {
			Shape shape = combines.get(i);
			float[] pts = shape.getPoints();
			Vector2f[] vecs = new Vector2f[(pts.length / 2)];
			for (int j=0;j<vecs.length;j++) {
				vecs[j] = new Vector2f(pts[j*2],pts[(j*2)+1]);
			}
			
			Polygon poly = new Polygon(vecs);
			StaticBody body = new StaticBody(poly);
			Log.debug("Adding Body:"+body.getID());
			Log.debug("Body Width:"+body.getShape().getBounds().getWidth());
			body.setFriction(1f);
			body.setRestitution(1f);
			world.add(body);
		}
	}

	/**
	 * Combine the shapes given into optimal shapes
	 *  
	 * @param shapes The shapes to combine
	 * @return The list of combined shapes
	 */
	private ArrayList<Shape> combineImpl(ArrayList<Shape> shapes) {
		ArrayList<Shape> result = new ArrayList<Shape>(shapes);

		// combine each shape with each other one
		for (int i = 0; i < shapes.size(); i++) {
			Shape first = (Shape) shapes.get(i);
			for (int j = i + 1; j < shapes.size(); j++) {
				Shape second = (Shape) shapes.get(j);

				// if only one shape is returned then remove the combined pair
				// and add the new one, otherwise leave them where they are
				Shape[] joined = util.union(first, second);
				if (joined.length == 1) {
					result.remove(first);
					result.remove(second);
					result.add(joined[0]);
					return result;
				}
			}
		}

		return result;
	}
	
	/**
	 * Combine the shapes by looping combining until there
	 * are no removed shapes.
	 * 
	 * @param shapes The shapes to combine
	 * @return The new list of shapes cleaned up
	 */
	private ArrayList<Shape> combine(ArrayList<Shape> shapes) {
		ArrayList<Shape> last = shapes;
		ArrayList<Shape> current = shapes;
		boolean first = true;

		while ((current.size() != last.size()) || (first)) {
			first = false;
			last = current;
			current = combineImpl(current);
		}

		ArrayList<Shape> pruned = new ArrayList<Shape>();
		for (int i = 0; i < current.size(); i++) {
			pruned.add(current.get(i).prune());
		}
		return pruned;
	}

	@Override
	public Rectangle getBounds() {
		bounds = new Rectangle(0,0,width*tileWidth,height*tileHeight);
		return bounds;
	}

	@Override
	public void render(Graphics g) {
		// render the bottom and parity layers of tile images
		for (int l=0;l<layers && l<2;l++){
			for (int x=0;x<width;x++) {
				for (int y=0;y<height;y++) {
					if (images[l][x][y] != null) {
						Log.debug("Drawing Tile At X:"+x+" && Y:"+y);
						g.drawImage(images[l][x][y], x*tileWidth, y*tileHeight);
					}
				}
			}
		}
		
		// Render entities
		for (int i=0;i<entities.size();i++) {
			entities.get(i).render(g);
		}
		
		// Render the above layers(layers 2+) of tiles
		for (int l=2;l<layers;l++){
			for (int x=0;x<width;x++){
				for (int y=0;y<height;y++){
					if (images[l][x][y] != null) {
						g.drawImage(images[l][x][y], x*tileWidth, y*tileHeight);
					}
				}
			}
		}
	}

	@Override
	public void renderBounds(Graphics g) {

		g.setColor(Color.red);
		for (int x=0;x<width;x++) {
			for (int y=0;y<height;y++) {
				if (shapes[x][y] != null) {
					g.draw(shapes[x][y]);
				}
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
				
				g.draw(p);
			}
		}

		g.setLineWidth(1);
	}
	
	/**
	 * Inner class for responding to collisions on the map
	 * @author Christopher Keefer
	 *
	 */
	public class CollisionEcho implements CollisionListener{

		@Override
		public void collisionOccured(CollisionEvent event) {
			// TODO Do Something when we encounter a collision?
			
		}
		
	}
	
}
