package ca.keefer.sanemethod.Environment;

import java.io.IOException;
import java.util.Enumeration;
import java.util.Hashtable;

import org.newdawn.slick.Graphics;
import org.newdawn.slick.SlickException;
import org.newdawn.slick.geom.Vector2f;
import org.newdawn.slick.particles.ParticleSystem;
import org.newdawn.slick.particles.ParticleIO;

import ca.keefer.sanemethod.Constants;
import ca.keefer.sanemethod.Entity.Entity;

/**
 * Particle Layer implements the Layer interface, for the sake of being rendered, updated and translated
 * in time with the other display layers.
 * @author Christopher Keefer
 *
 */
public class ParticleLayer implements Layer{
	/** The particle systems operating in this layer */
	Hashtable<Integer, ParticleSystem> psList;
	Hashtable<Integer, Entity> eTrackList;
	private int id;
	private boolean active;
	private ViewPort viewPort;
	/** The internal counter for the hashtables */
	int ic;
	
	public ParticleLayer(int id, boolean active){
		this.id=id;
		this.active=active;
		psList = new Hashtable<Integer, ParticleSystem>();
		eTrackList = new Hashtable<Integer,Entity>();
		ic = -1;
	}

	@Override
	public int getId() {
		return id;
	}

	@Override
	public int getType() {
		return Constants.LAYER_PARTICLE;
	}

	@Override
	public ViewPort getViewPort() {
		return viewPort;
	}
	
	public ParticleSystem getParticleSystem(int index){
		if (psList.containsKey(index)){
			return psList.get(index);
		}
		return null;
	}
	
	public int addParticleSystem(String fileRef, Vector2f position) throws SlickException{
		ParticleSystem particleSystem;
		try {
			ParticleSystem.setRelativePath("res/Particle/");
			particleSystem = ParticleIO.loadConfiguredSystem(fileRef);
		} catch (IOException e) {
			throw new SlickException("Cannot Load pre-Configured Particle System.",e);
		}
		particleSystem.setPosition(position.getX(), position.getY());
		ic++;
		psList.put(ic,particleSystem);
		return ic;
	}
	
	public int addParticleSystem(String fileRef, Entity entityToTrack) throws SlickException{
		ParticleSystem particleSystem;
		try {
			ParticleSystem.setRelativePath("res/Particle/");
			particleSystem = ParticleIO.loadConfiguredSystem(fileRef);
		} catch (IOException e) {
			throw new SlickException("Cannot Load pre-Configured Particle System.",e);
		}
		particleSystem.setPosition(entityToTrack.getBody().getPosition().getX(), 
				entityToTrack.getBody().getPosition().getY());
		ic++;
		psList.put(ic,particleSystem);
		eTrackList.put(ic, entityToTrack);
		return ic;
	}
	
	public void removeParticleSystem(int id){
		if (psList.containsKey(id)){
			psList.remove(id);
			if (eTrackList.containsKey(id)){
				eTrackList.remove(id);
			}
		}
	}

	@Override
	public boolean isActive() {
		return active;
	}

	@Override
	public void setActive(boolean t) {
		active = t;
	}

	@Override
	public void setId(int Id) {
		id = Id;
	}

	@Override
	public void setViewPort(ViewPort v) {
		viewPort = v;
	}

	@Override
	public void update(int delta) {
		for (int i=0;i==ic;i++){
			if (eTrackList.containsKey(i)){
				psList.get(i).setPosition(eTrackList.get(i).getBody().getPosition().getX(), 
						eTrackList.get(i).getBody().getPosition().getY());
			}
			if (psList.containsKey(i)){
				psList.get(i).update(delta);
			}
		}
	}
	
	@Override
	public void render(Graphics g) {
		Enumeration<ParticleSystem> thisEnum = psList.elements();
		while(thisEnum.hasMoreElements()){
			thisEnum.nextElement().render();
		}
	}

	@Override
	public void render(Graphics g, int xOffset, int yOffset, int xLimit,
			int yLimit) {
		// Make sure particle effects don't stop getting drawn until they're definitely offscreen
		xOffset -= Constants.TILE_WIDTH;
		yOffset -= Constants.TILE_HEIGHT;
		xLimit += Constants.TILE_WIDTH;
		yLimit += Constants.TILE_HEIGHT;
		// check for particle position, and render those within the defined limits
		Enumeration<ParticleSystem> thisEnum = psList.elements();
		while(thisEnum.hasMoreElements()){
			ParticleSystem thisElement = thisEnum.nextElement();
			float x = thisElement.getPositionX();
			float y = thisElement.getPositionY();
			if (x >= xOffset && y >= yOffset && x <= xLimit && y <= yLimit){
				thisElement.render();
			}
		}
	}

}
