package ca.keefer.sanemethod.Demo;

import net.phys2d.raw.Body;
import net.phys2d.raw.StaticBody;
import net.phys2d.raw.World;
import net.phys2d.raw.shapes.Box;
import net.phys2d.raw.shapes.Circle;

/**
 * Splitting a set of balls
 * 
 * @author Kevin Glass
 */
public class Demo2 extends AbstractDemo {
        /**
         * Create the demo
         */
        public Demo2() {
                super("Slick Phys2D Demo 2");
        }


        /**
         * @see net.phys2d.raw.test.AbstractDemo#init(net.phys2d.raw.World)
         */
        protected void init(World world) {
                Body body1 = new StaticBody("Ground1", new Box(400.0f, 20.0f));
                body1.setPosition(250.0f, 400);
                world.add(body1);
                Body body1b = new StaticBody("Ground1", new Box(20.0f, 400.0f));
                body1b.setPosition(20.0f, 200);
                world.add(body1b);


                Body body3 = new Body("Mover2", new Circle(25), 50.0f);
                body3.setPosition(225.0f, 365);
                world.add(body3);
                Body body2 = new Body("Mover1", new Circle(25), 50.0f);
                body2.setPosition(275.0f, 365);
                world.add(body2);
                Body body3a = new Body("Mover2", new Circle(25), 50.0f);
                body3a.setPosition(175.0f, 365);
                world.add(body3a);
                Body body2a = new Body("Mover1", new Circle(25), 50.0f);
                body2a.setPosition(325.0f, 365);
                world.add(body2a);
                
                Body faller = new Body("Faller", new Circle(25), 200.0f);
                faller.setPosition(250.0f, -20f);
                world.add(faller);
        }
}
