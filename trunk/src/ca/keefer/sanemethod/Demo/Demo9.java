package ca.keefer.sanemethod.Demo;

import net.phys2d.math.Vector2f;
import net.phys2d.raw.BasicJoint;
import net.phys2d.raw.Body;
import net.phys2d.raw.StaticBody;
import net.phys2d.raw.World;
import net.phys2d.raw.shapes.ConvexPolygon;
import net.phys2d.raw.shapes.Polygon;

/**
 * A demo showing gears inteacting in a most un-believeable way
 * 
 * @author gideon
 */
public class Demo9 extends AbstractDemo {
        /** A wheel dropped into the scene */
        private Body wheel;
        
        /**
         * Create a new gears demo instance
         */
        public Demo9() {
                super("Slick Phys2d Demo 9 - Gears Demo");
        }


        /**
         * @see net.phys2d.raw.test.AbstractDemo#init(net.phys2d.raw.World)
         */
        protected void init(World world) {
                Vector2f[] groundVerts = {new Vector2f(-200, -10), new Vector2f(200,-10), new Vector2f(200,10), new Vector2f(-200,10)};
                ConvexPolygon groundBox = new ConvexPolygon(groundVerts);
                Body ground = new StaticBody("ground", groundBox);
                ground.setPosition(250, 50);
                world.add(ground);
                
                {
                        int noVerts = 40;
                        Vector2f[] circleVerts = new Vector2f[noVerts];
                        float[] radius = {50,42,42,50};
                        for( int i = 0; i < noVerts; i++ ) {
                                float angle = (float) (i* 2 * Math.PI/noVerts);
                                circleVerts[i] = new Vector2f(
                                                (float) (Math.cos(angle) * radius[i%radius.length]), 
                                                (float) (Math.sin(angle) * radius[i%radius.length]));
                        }
                        Polygon circlePolygon = new Polygon(circleVerts);
                        Body circle = new Body("circle", circlePolygon, 2);
                        circle.setPosition(250, 150);
                        world.add(circle);
                        
                        BasicJoint joint = new BasicJoint(ground, circle, new Vector2f(circle.getPosition()));
                        world.add(joint);
                }
                {
                        int outerCircleVerts = 30;
                        int noVerts = 120;
                        Vector2f[] circleVerts = new Vector2f[outerCircleVerts+1 + noVerts+1];
                        for( int i = 0; i <= outerCircleVerts; i++ ) {
                                float angle = (float) (i* 2 * Math.PI/outerCircleVerts);
                                circleVerts[i] = new Vector2f(
                                                (float) (Math.cos(angle) * 150), 
                                                (float) (Math.sin(angle) * 150));
                        }
                        float[] radius = {140, 133, 133, 140};
                        for( int i = 0; i <= noVerts; i++ ) {
                                float angle = (float) (i* 2 * Math.PI/noVerts);
                                circleVerts[outerCircleVerts+1 + noVerts-i] = new Vector2f(
                                                (float) (Math.cos(angle) * radius[i%radius.length]), 
                                                (float) (Math.sin(angle) * radius[i%radius.length]));
                        }
                        Polygon circlePolygon = new Polygon(circleVerts);
                        Body circle = new Body("circle", circlePolygon, 30);
                        circle.setPosition(250, 220);
                        world.add(circle);
                }
                
                {
                        int noVerts = 20;
                        Vector2f[] circleVerts = new Vector2f[noVerts];
                        float[] radius = {30,20,20,30};
                        for( int i = 0; i < noVerts; i++ ) {
                                float angle = (float) (i* 2 * Math.PI/noVerts);
                                circleVerts[i] = new Vector2f(
                                                (float) (Math.cos(angle) * radius[i%radius.length]), 
                                                (float) (Math.sin(angle) * radius[i%radius.length]));
                        }
                        Polygon circlePolygon = new Polygon(circleVerts);
                        Body circle = new Body("circle", circlePolygon, 2);
                        circle.setPosition(250, 300);
                        world.add(circle);
                        
                        Vector2f[] nonConvexPoly = {new Vector2f(-20,-10), new Vector2f(20,-10), new Vector2f(10,0), new Vector2f(20,10), new Vector2f(-20,10), new Vector2f(-10,0)};
                        Polygon poly = new Polygon(nonConvexPoly);
                        Body nonConvexBody = new Body("poly", poly, 35);
                        nonConvexBody.setPosition(250, 400);
                        world.add(nonConvexBody);
                        
                        BasicJoint joint = new BasicJoint(circle,nonConvexBody, new Vector2f(circle.getPosition()));
                        world.add(joint);
                        
                        wheel = circle;
                        circle.setRotDamping(5);
                }


        }


        /**
         * @see net.phys2d.raw.test.AbstractDemo#update()
         */
        protected void update(int delta) {
        	super.update(delta);
        	//world.step(delta*0.001f);
            wheel.setTorque(3000);
        }
}
