package ca.keefer.sanemethod.Demo;

import net.phys2d.math.Vector2f;
import net.phys2d.raw.Body;
import net.phys2d.raw.World;
import net.phys2d.raw.shapes.Circle;

/**
 * Back and forth
 * 
 * @author Kevin Glass
 */
public class Demo6 extends AbstractDemo {
        /** The first circle in the simulation */
        private Body circle1;
        /** The second circle in the simulation */
        private Body circle2;
        
        /**
         * Create the demo
         */
        public Demo6() {
                super("Slick Phys2d Demo 6");
        }


        /**
         * @see net.phys2d.raw.test.AbstractDemo#init(net.phys2d.raw.World)
         */
        protected void init(World world) {
                //world.setGravity(0,0);
                
                circle1 = new Body("Circle 1", new Circle(20.0f), 1);
                circle1.setPosition(150,100);
                circle1.setRestitution(1.0f);
                circle1.setFriction(0.0f);
                circle1.adjustVelocity(new Vector2f(100,0));
                world.add(circle1);
                circle2 = new Body("Circle 2", new Circle(20.0f), 1);
                circle2.setPosition(250,100);
                circle2.setRestitution(1.0f);
                circle2.setFriction(0.0f);
                world.add(circle2);
        }
}
