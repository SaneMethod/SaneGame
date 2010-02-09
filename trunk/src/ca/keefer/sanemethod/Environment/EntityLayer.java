package ca.keefer.sanemethod.Environment;

import java.util.ArrayList;

import org.newdawn.slick.Graphics;

import ca.keefer.sanemethod.Constants;
import ca.keefer.sanemethod.Entity.Entity;

/**
 * The EntityLayer class implements the Layer interface,
 * and provides a common layer for entities on the same 
 * level to be drawn together. This layer is attached to
 * a specific ViewPort, to which a reference is maintained.
 * @author Christopher Keefer
 *
 */
public class EntityLayer implements Layer{
	
	private int id;
	private boolean active;
	private ArrayList<Entity> entityList;
	private ViewPort viewPort;
	
	public EntityLayer(int id, boolean active){
		this.id=id;
		this.active=active;
		entityList = new ArrayList<Entity>();
	}

	@Override
	public int getId() {
		return id;
	}
	
	@Override
	public int getType(){
		return Constants.LAYER_ENTITY;
	}

	@Override
	public ViewPort getViewPort() {
		return viewPort;
	}

	@Override
	public boolean isActive() {
		return active;
	}

	@Override
	public void render(Graphics g) {
		for (int i=0; i<entityList.size();i++){
			entityList.get(i).render(g);
		}
		
	}
	/**
	 * Render only those entities on-screen
	 */
	public void render(Graphics g, int xOffset, int yOffset, int xLimit, int yLimit){
		// Make sure entities don't stop getting drawn until they're
		// definitely off-screen
		xOffset -= Constants.TILE_WIDTH;
		yOffset -= Constants.TILE_HEIGHT;
		xLimit += Constants.TILE_WIDTH;
		yLimit += Constants.TILE_HEIGHT;
		for (int i=0; i<entityList.size();i++){
			float x = entityList.get(i).getBody().getPosition().getX();
			float y = entityList.get(i).getBody().getPosition().getY();
			if (x >= xOffset && x <= xLimit && y >= yOffset && y <= yLimit){
				entityList.get(i).render(g);
			}
		}
	}

	@Override
	public void setActive(boolean t) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void setId(int id) {
		this.id=id;
	}

	@Override
	public void setViewPort(ViewPort v) {
		this.viewPort=v;
	}

	@Override
	public void update(int delta) {
		for (int i=0;i<entityList.size();i++){
			entityList.get(i).update(delta);
		}
	}
	
	public void addEntity(Entity e){
		// add to an appropriate place in the arrayList based on zOrder
		for (int i=0; i< entityList.size(); i++){
			if (entityList.get(i).getZOrder() > e.getZOrder()){
				entityList.add(i,e);
				return;
			}
		}
		// this entity's zOrder is higher than all others in list - add it to the end
		entityList.add(e);
	}
	
	public ArrayList<Entity> getEntityList(){
		return entityList;
	}
	
	public Entity getEntity(int index){
		return entityList.get(index);
	}
	
	public Entity getEntity(Entity o){
		if (entityList.contains(o)){
			return entityList.get(entityList.indexOf(o));
		}
		return null;
	}
	
	public void removeEntity(int index){
		entityList.remove(index);
	}
	
	public void removeEntity(Entity e){
		entityList.remove(e);
	}

}
