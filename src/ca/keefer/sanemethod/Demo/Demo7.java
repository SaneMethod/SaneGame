package ca.keefer.sanemethod.Demo;

import net.phys2d.raw.Body;
import net.phys2d.raw.StaticBody;
import net.phys2d.raw.World;
import net.phys2d.raw.shapes.Box;
import net.phys2d.raw.shapes.Circle;
import net.phys2d.raw.shapes.Line;

/**
 * Lines terrain
 * 
 * @author Kevin Glass
 */
public class Demo7 extends AbstractDemo {
        /** The box falling into the simulation */
        private Body box;
        
        /**
         * Create the demo
         */
        public Demo7() {
                super("Slick Phys2d Demo 7 - Lines Terrain");
        }

        /**
         * @see net.phys2d.raw.test.AbstractDemo#init(net.phys2d.raw.World)
         */
        protected void init(World world) {
                Body land = new StaticBody("Line1", new Line(130,30));
                land.setPosition(-30,200);
                world.add(land);
                Body land2 = new StaticBody("Line2", new Line(50,50));
                land2.setPosition(100,230);
                world.add(land2);
                Body land3 = new StaticBody("Line3", new Line(100,20));
                land3.setPosition(150,280);
                world.add(land3);
                Body land4 = new StaticBody("Line4", new Line(100,80));
                land4.setPosition(250,300);
                world.add(land4);
                Body land5 = new StaticBody("Line5", new Line(100,0));
                land5.setPosition(350,380);
                world.add(land5);
                Body land6 = new StaticBody("Line6", new Line(10,-200));
                land6.setPosition(450,380);
                world.add(land6);
                Body land7 = new StaticBody("Line7", new Line(80,-10));
                land7.setPosition(460,180);
                world.add(land7);
                
                box = new Body("Faller", new Box(50,50), 1);
                box.setPosition(50,50);
                box.setRotation(-0.5f);
                world.add(box);
                Body other = new Body("Faller", new Circle(10), 1);
                other.setPosition(200,50);
                other.setRotation(-0.5f);
                world.add(other);
                other = new Body("Faller", new Circle(10), 1);
                other.setPosition(225,50);
                other.setRotation(-0.5f);
                world.add(other);
                other = new Body("Faller", new Circle(10), 1);
                other.setPosition(250,50);
                other.setRotation(-0.5f);
                world.add(other);
                other = new Body("Faller", new Circle(10), 1);
                other.setPosition(275,50);
                other.setRotation(-0.5f);
                world.add(other);
                other = new Body("Faller", new Circle(10), 1);
                other.setPosition(300,50);
                other.setRotation(-0.5f);
                world.add(other);
        }
        
        @Override
        protected void update(int delta) {
        	super.update(delta);
    }
}
