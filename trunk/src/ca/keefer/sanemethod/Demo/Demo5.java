package ca.keefer.sanemethod.Demo;

import org.newdawn.slick.Color;
import org.newdawn.slick.Graphics;
import org.newdawn.slick.Input;
import org.newdawn.slick.util.Log;

import net.phys2d.math.Vector2f;
import net.phys2d.raw.BasicJoint;
import net.phys2d.raw.Body;
import net.phys2d.raw.StaticBody;
import net.phys2d.raw.World;
import net.phys2d.raw.shapes.Box;

/**
 * Rope bridge demo
 * 
 * @author Kevin Glass
 */
public class Demo5 extends AbstractDemo {
        /** The joint to break */
        private BasicJoint joint;
        /** The world in which the demo takes place */
        private World world;
        
        /**
         * Create a new demo
         */
        public Demo5() {
                super("Slick Phys2d Demo 5 - Bridge Demo");
        }
        
        @Override
        protected void receiveKeyPress(int keyPressed, char keyChar) {
            super.receiveKeyPress(keyPressed, keyChar);
        	if (keyPressed == Input.KEY_SPACE) {
                        Log.info("Removing joint");
                        world.remove(joint);
                }
        }
        
        @Override
        public void render(Graphics g){
        	g.setColor(Color.white);
        	g.drawString("Press Space", 450, 90);
        	super.render(g);
        }
        
        /**
         * @see net.phys2d.raw.test.AbstractDemo#init(net.phys2d.raw.World)
         */
        protected void init(World world) {
                this.world = world;
                float relax = 0.8f;
                
                Body body1 = new StaticBody("Ground1", new Box(500.0f, 20.0f));
                body1.setPosition(250.0f, 400);
                world.add(body1);
                
                Body body2 = new Body("First", new Box(40.0f, 10.0f), 500);
                body2.setFriction(0.2f);
                body2.setPosition(80.0f, 300);
                world.add(body2);
                
                BasicJoint j = new BasicJoint(body1,body2,new Vector2f(40,300));
                j.setRelaxation(relax);
                world.add(j);
                
                int i;
                for (i=1;i<8;i++) {
                        Body body3 = new Body("Teeter",new Box(40.0f, 10.0f), 500);
                        body3.setFriction(0.2f);
                        body3.setPosition(80.0f+(i*45), 300);
                        world.add(body3);
                        
                        BasicJoint j2 = new BasicJoint(body2,body3,new Vector2f(65+(i*45),300));
                        j2.setRelaxation(relax);
                        world.add(j2);
                        if (i == 4) {
                                joint = j2;
                        }
                        
                        body2 = body3;
                }


                BasicJoint j3 = new BasicJoint(body1,body2,new Vector2f(80+(i*45),300));
                j3.setRelaxation(relax);
                world.add(j3);
        }
}