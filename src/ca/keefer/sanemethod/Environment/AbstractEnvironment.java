package ca.keefer.sanemethod.Environment;

import java.util.ArrayList;

import ca.keefer.sanemethod.Constants;
import ca.keefer.sanemethod.Entity.Entity;

import net.phys2d.raw.World;

/**
 * The common bits of all environments. Holds a physics world, allows addition
 * of entities and their phyiscal representation.
 * 
 * @author Kevin Glass, Christopher Keefer
 */
public abstract class AbstractEnvironment implements Environment {
	/** The physical world the environment provides to it's entities */
	protected World world = new World(Constants.GRAVITY, 20); 

	/** The entities list */
	protected ArrayList<Entity> entities = new ArrayList<Entity>();
	/** The amount time in ms passed since last update */
	private int totalDelta;
	/** The amount of time to pass before updating the physics world */
	private int stepSize = 10;

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
		
		entities.add(entity);
		entity.setWorld(world);
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
				for (int i=0;i<entities.size();i++) {
					entities.get(i).preUpdate(stepSize);
				}
			}
			
			for (int i=0;i<entities.size();i++) {
				entities.get(i).update(stepSize);
			}
		}
	}
}