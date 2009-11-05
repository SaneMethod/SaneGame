package ca.keefer.sanemethod.Demo;

import net.phys2d.math.Vector2f;
import net.phys2d.raw.Body;
import net.phys2d.raw.StaticBody;
import net.phys2d.raw.World;
import net.phys2d.raw.shapes.Box;
import net.phys2d.raw.shapes.Circle;
import net.phys2d.raw.shapes.DynamicShape;

import org.newdawn.slick.Color;
import org.newdawn.slick.Input;
import org.newdawn.slick.Graphics;

public class Demo3 extends AbstractDemo {
    /** The world in which the simulation takes place */
    private World world;
    
    /**
     * Create the demo
     */
    public Demo3() {
            super("Slick Phys2d Demo 3 - Ball/Stack Demo");
    }


    @Override
    protected void receiveKeyPress(int keyPressed, char keyChar) {
           	super.receiveKeyPress(keyPressed, keyChar);
    		if (keyPressed == Input.KEY_SPACE) {
                    Body body2 = new Body("Mover1", new Circle(20), 300.0f);
                    body2.setPosition(-50, (float) (((Math.random() * 50) + 150)));
                    world.add(body2);
                    body2.adjustAngularVelocity(1);
                    body2.adjustVelocity(new Vector2f(200, (float) (Math.random() * 200)));
            }
    }
    
    @Override
    public void render(Graphics g){
    	g.setColor(Color.white);
    	g.drawString("Press Space", 450, 90);
    	super.render(g);
    }
    
    @Override
    protected void init(World world) {
            this.world = world;
            
            Body body1 = new StaticBody("Ground1", new Box(400.0f, 20.0f));
            body1.setPosition(250.0f, 400);
            body1.setFriction(1);
            world.add(body1);
            
            for (int y=0;y<5;y++) {
                    int xbase = 250 - (y * 21);
                    for (int x=0;x<y+1;x++) {
                            DynamicShape shape = new Box(40,40);
                            if ((x == 1) && (y == 2)) {
                                    shape = new Circle(19);
                            }
                            if ((x == 1) && (y == 4)) {
                                    shape = new Circle(19);
                            }
                            if ((x == 3) && (y == 4)) {
                                    shape = new Circle(19);
                            }
                            Body body2 = new Body("Mover1", shape, 100.0f);
                            body2.setPosition(xbase + (x * 42), y*45);
                            world.add(body2);
                    }
            }
    }
}
