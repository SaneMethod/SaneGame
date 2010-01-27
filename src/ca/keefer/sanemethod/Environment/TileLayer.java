package ca.keefer.sanemethod.Environment;

import org.newdawn.slick.Graphics;
import org.newdawn.slick.tiled.TiledMap;

import ca.keefer.sanemethod.Constants;

public class TileLayer implements Layer{
	
	private int id;
	private int layer;
	private boolean active;
	private ViewPort viewport;
	private TiledMap tiledMap;
	
	public TileLayer(TiledMap tm, int layer, int id, boolean active){
		this.tiledMap=tm;
		this.id=id;
		this.layer=layer;
		this.active=active;
	}

	@Override
	public int getId() {
		return id;
	}
	
	@Override
	public void setId(int id) {
		this.id=id;
	}
	
	@Override
	public int getType(){
		return Constants.LAYER_TILE;
	}

	@Override
	public ViewPort getViewPort() {
		return viewport;
	}

	@Override
	public boolean isActive() {
		return active;
	}

	@Override
	public void render(Graphics g) {
		tiledMap.render(0, 0, layer);
	}
	
	public void render(Graphics g, int xOffset, int yOffset, int xLimit, int yLimit){
		// calculate the offset to the next tile
	    xOffset = 0;
	    yOffset = 0;
		int tileOffsetX = (int) - (xOffset % Constants.TILE_WIDTH); 
	    int tileOffsetY = (int) - (yOffset % Constants.TILE_HEIGHT);    
	    // calculate the index of the leftmost tile that is being displayed 
	    int tileIndexX = (int) (xOffset / Constants.TILE_WIDTH); 
	    int tileIndexY = (int) (yOffset / Constants.TILE_HEIGHT); 
		tiledMap.render(tileOffsetX + xOffset, tileOffsetY + yOffset, tileIndexX, tileIndexY, 
				(Constants.SCREENWIDTH-tileOffsetX), (Constants.SCREENHEIGHT-tileOffsetY), layer, false);
	}

	@Override
	public void setActive(boolean t) {
		active=t;
	}

	@Override
	public void setViewPort(ViewPort v) {
		viewport=v;	
	}

	@Override
	public void update(int delta) {
		
	}

}
