package ca.keefer.sanemethod.Demo;

import net.phys2d.math.Vector2f;
import net.phys2d.raw.Body;
import net.phys2d.raw.SpringJoint;
import net.phys2d.raw.StaticBody;
import net.phys2d.raw.World;
import net.phys2d.raw.shapes.Box;

/**
 * Demo for the spring joint.
 * 
 * @author Gideon Smeding
 */
public class Demo8 extends AbstractDemo {


        /**
         * Create the demo
         */
        public Demo8() {
                super("Slick Phys2d Demo 8 - Elastic Joint");
        }
        
        @Override
        protected void init(World world) {
                Body body1 = new StaticBody("Ground1", new Box(400.0f, 20.0f));
                body1.setPosition(250.0f, 50);
                world.add(body1);
                
                Body body2 = new Body("Mover1", new Box(20.0f, 20.0f), 100.0f);
                body2.setPosition(100.0f, 100f);
                world.add(body2);
                
                Body body3 = new Body("Mover2", new Box(20.0f, 20.0f), 100.0f);
                body3.setPosition(150.0f, 150f);
                world.add(body3);
                
                Body body4 = new Body("Mover3", new Box(20.0f, 20.0f), 50.0f);
                body4.setPosition(200f, 200f);
                world.add(body4);
                
                SpringJoint joint1 = new SpringJoint(body1, body2, new Vector2f(150,60), new Vector2f(100, 90));
                joint1.setBrokenSpringConst(10);
                joint1.setCompressedSpringConst(0);
                joint1.setStretchedSpringConst(0);
                joint1.setMinSpringSize(0);
                joint1.setMaxSpringSize(joint1.getSpringSize());
                world.add(joint1);
                
                SpringJoint joint2 = new SpringJoint(body2, body3, new Vector2f(100,110), new Vector2f(160, 150));
                joint2.setBrokenSpringConst(10);
                joint2.setCompressedSpringConst(0);
                joint2.setStretchedSpringConst(0);

                joint2.setMinSpringSize(0);
                joint2.setMaxSpringSize(joint2.getSpringSize());
                world.add(joint2);

                SpringJoint joint3 = new SpringJoint(body3, body4, new Vector2f(150,160), new Vector2f(200, 210));
                joint3.setBrokenSpringConst(100);
                joint3.setCompressedSpringConst(0);
                joint3.setStretchedSpringConst(0);
                joint3.setMinSpringSize(0);
                joint3.setMaxSpringSize(joint3.getSpringSize());
                world.add(joint3);
                
                SpringJoint joint5 = new SpringJoint(body4, body1, new Vector2f(190, 200), new Vector2f(400, 60));
                joint5.setBrokenSpringConst(1);
                joint5.setCompressedSpringConst(0.5f);
                joint5.setStretchedSpringConst(0.5f);
                joint5.setSpringSize(100);
                joint5.setMinSpringSize(50);
                joint5.setMaxSpringSize(200);
                world.add(joint5);
                
        }
}
