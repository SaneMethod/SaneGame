package ca.keefer.sanemethod.Environment;

import org.newdawn.slick.util.Log;

import ca.keefer.sanemethod.Constants;
import ca.keefer.sanemethod.Entity.Entity;

import net.phys2d.raw.World;
import net.phys2d.raw.strategies.QuadSpaceStrategy;

/**
 * The common bits of all environments. Holds a physics world, allows addition
 * of entities and their phyiscal representation.
 * 
 * @author Kevin Glass, Christopher Keefer
 */
public abstract class AbstractEnvironment implements Environment {
	/** The physical world the environment provides to it's entities */
	protected World world = new World(Constants.GRAVITY, Constants.ITERATIONS, new QuadSpaceStrategy(15,50)); 

	/** The Entity Layer */
	EntityLayer eLayer;
	/** The amount time in ms passed since last update */
	private int totalDelta;
	/** The amount of time to pass before updating the physics world */
	private int stepSize = 1000/60;

	/**
	 * Add an entity to the environment. This will include it's physical
	 * body in the world.
	 * 
	 * @param entity The entity to be added
	 */
	public void addEntity(Entity entity) {
		if (entity.getBody() != null) {
			world.add(entity.getBody());
		}
		
		entity.setWorld(world);
		
		eLayer.addEntity(entity);
	}
	
	/** Get the physics world encapsulated by this environment */
	public World getWorld(){
		return world;
	}
	
	@Override
	public void update(int delta) {
		boolean first = true;
		
		totalDelta += delta;
		while (totalDelta > stepSize) {
			world.step(stepSize * 0.01f);
			totalDelta -= stepSize;

			if (first) {
				first = false;
				for (int i=0;i<eLayer.getEntityList().size();i++) {
					eLayer.getEntity(i).preUpdate(stepSize);
				}
			}
			
			for (int i=0;i<eLayer.getEntityList().size();i++) {
				eLayer.getEntity(i).update(stepSize);
			}
		}
	}
}