package ca.keefer.sanemethod.Demo;

import java.util.Random;

import org.newdawn.slick.Color;
import org.newdawn.slick.Graphics;
import org.newdawn.slick.Input;

import net.phys2d.math.Vector2f;
import net.phys2d.raw.Body;
import net.phys2d.raw.StaticBody;
import net.phys2d.raw.World;
import net.phys2d.raw.shapes.Box;
import net.phys2d.raw.shapes.Circle;
import net.phys2d.raw.shapes.ConvexPolygon;
import net.phys2d.raw.shapes.Line;
import net.phys2d.raw.shapes.Polygon;

/**
 * A test to show odd shapes interacting
 * 
 * @author gideon
 */
public class Demo4 extends AbstractDemo  {
        /** The world in which the simulation takes place */
        private World world;
        
        /**
         * Create a new demo instance
         */
        public Demo4() {
                super("Slick Phys2d Demo 4 - All Shapes Demo");
        }
        
        /** A local random number generator */
        private Random random = new Random();
        
        @Override
        protected void receiveKeyPress(int keyPressed, char keyChar) {
                super.receiveKeyPress(keyPressed, keyChar);
                
                Body newBody = null;
                
                if (keyPressed == Input.KEY_S) {         
                        Vector2f[] circleVerts = new Vector2f[30];
                        float[] radius = {20,10};
                        for( int i = 0; i < 30; i++ ) {
                                float angle = (float) (3*4 * i * Math.PI/180);
                                circleVerts[i] = new Vector2f(
                                                (float) (Math.cos(angle) * radius[i%2]), 
                                                (float) (Math.sin(angle) * radius[i%2]));
                        }
                        Polygon circlePolygon = new Polygon(circleVerts);
                        newBody = new Body(circlePolygon, 4);
                } else if ( keyPressed == Input.KEY_W ) {
                        newBody = new Body(new Circle(15), 2);
                } else if ( keyPressed == Input.KEY_T ) {
                        Vector2f[] triangleVerts = {new Vector2f(-20, -20), new Vector2f(20,-20), new Vector2f(20,20)};
                        ConvexPolygon trianglePolygon = new ConvexPolygon(triangleVerts);
                        newBody = new Body(trianglePolygon, 3);
                } else if ( keyPressed == Input.KEY_B ) {
                        newBody = new Body(new Box(20,30), 3);
                } else {
                        return;
                }
                
                newBody.setPosition(250, 150);
                newBody.setRotation((float) (random.nextFloat() * 2 * Math.PI));
                world.add(newBody);
                
        }
        
        @Override
        public void render(Graphics g){
        	g.setColor(Color.white);
        	g.drawString("S - Drop a star",450,90);
            g.drawString("T - Drop a triangle",450,110);
            g.drawString("W - Drop a wheel",450,130);
            g.drawString("B - Drop a box",450, 150);
            super.render(g);
        }
        
        protected void init(World world) {
            this.world = world;
            Line line1 = new Line(300, 0);
            Body ground1 = new StaticBody("line", line1);
            ground1.setPosition(100, 400);
            world.add(ground1);
            
            Line line2 = new Line(-100, -100);
            Body ground2 = new StaticBody("line", line2);
            ground2.setPosition(140, 420);
            world.add(ground2);
            
            Line line3 = new Line(100, -100);
            Body ground3 = new StaticBody("line", line3);
            ground3.setPosition(360, 420);
            world.add(ground3);
        }
}
