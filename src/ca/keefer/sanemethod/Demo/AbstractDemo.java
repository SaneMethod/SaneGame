package ca.keefer.sanemethod.Demo;

import org.newdawn.slick.Color;
import org.newdawn.slick.Graphics;
import org.newdawn.slick.Input;
import org.newdawn.slick.util.Log;

import ca.keefer.sanemethod.Constants;
import net.phys2d.math.MathUtil;
import net.phys2d.math.Matrix2f;
import net.phys2d.math.ROVector2f;
import net.phys2d.math.Vector2f;
import net.phys2d.raw.AngleJoint;
import net.phys2d.raw.Arbiter;
import net.phys2d.raw.ArbiterList;
import net.phys2d.raw.BasicJoint;
import net.phys2d.raw.Body;
import net.phys2d.raw.BodyList;
import net.phys2d.raw.Contact;
import net.phys2d.raw.DistanceJoint;
import net.phys2d.raw.FixedJoint;
import net.phys2d.raw.Joint;
import net.phys2d.raw.JointList;
import net.phys2d.raw.SlideJoint;
import net.phys2d.raw.SpringJoint;
import net.phys2d.raw.World;
import net.phys2d.raw.shapes.Box;
import net.phys2d.raw.shapes.Circle;
import net.phys2d.raw.shapes.Line;
import net.phys2d.raw.shapes.Polygon;
import net.phys2d.raw.strategies.QuadSpaceStrategy;

/**
 * A super class containing common elements for all demos
 * @author Christopher Keefer
 *
 */
public abstract class AbstractDemo {

	/** name of the currently running demo */
	protected String demoName;
	/** Shared physics world */
	protected World world;
	/** True if the sim is running */
	protected boolean running=true;
	/** True if we should reset the demo on the next step */
	protected boolean resetRequested=false;
	/** True if we should render normals */
    protected boolean renderNormals = true;
    /** True if we should render contact points */
    protected boolean renderContacts = true;
    /** Gravity for this demo */
    protected Vector2f gravity;
    /** The amount time in ms passed since last update */
	private int totalDelta;
	/** The amount of time to pass before updating the physics world */
	private int stepSize = 1000/60;
    
    /**
     * Default constructor - creates new world with default parameters.
     * @param demoName String name of demo
     */
    public AbstractDemo(String demoName){
    	this.demoName = demoName;
    	this.gravity = Constants.GRAVITY;
    	this.world = new World(Constants.GRAVITY,Constants.ITERATIONS, new QuadSpaceStrategy(25,10));
    	initDemo();
    }
    
    /**
     * Detailed constructor, allowing demo to specify all qualities of the world to be used.
     * @param demoName
     * @param gravity
     * @param iterations
     * @param bodies
     * @param spaces
     */
    public AbstractDemo (String demoName, Vector2f gravity, int iterations, int bodies, int spaces){
    	this.demoName = demoName;
    	this.gravity = gravity;
    	this.world = new World(gravity,iterations,new QuadSpaceStrategy(bodies,spaces));
    	initDemo();
    }
    
    /** Gets the name of this demo */
    public String getDemoName(){
    	return demoName;
    }
    
    /**
     * Notification that a key was pressed - passed from the DemoState
     * @param keyPressed int
     * @param keyChar char
     */
    protected void receiveKeyPress(int keyPressed, char keyChar){
    	switch(keyPressed){
    	case Input.KEY_ESCAPE:
    		System.exit(0);
    		break;
    	case Input.KEY_R:
    		initDemo();
    		break;
    	}
    }
    
    /**
     * Draw the contents of this demo and all relevant strings, etc.
     * @param g Slick Graphics object
     */
    protected void render(Graphics g){
    	g.setColor(Color.white);
    	g.drawString("Arbiters: "+world.getArbiters().size(),10,30);
        g.drawString("Bodies: "+world.getBodies().size(),10,50);
        g.drawString("Energy: "+world.getTotalEnergy(),10,70);
    	
    	BodyList bodies = world.getBodies();
        
        for (int i=0;i<bodies.size();i++) {
                Body body = bodies.get(i);
                
                drawBody(g, body);
        }
        
        JointList joints = world.getJoints();
        
        for (int i=0;i<joints.size();i++) {
                Joint joint = joints.get(i);
                
                drawJoint(g, joint);
        }
        
        ArbiterList arbs = world.getArbiters();
        
        for (int i=0;i<arbs.size();i++) {
                Arbiter arb = arbs.get(i);
                
                Contact[] contacts = arb.getContacts();
                int numContacts = arb.getNumContacts();
                
                for (int j=0;j<numContacts;j++) {
                        drawContact(g, contacts[j]);
                }
        }
    }
    
    protected void update (int delta){
    	totalDelta += delta;
		while (totalDelta > stepSize) {
			world.step(stepSize * 0.01f);
			totalDelta -= stepSize;
		}
    	if (resetRequested){
    		resetRequested=false;
    		initDemo();
    	}
    }
    
    /**
     * Initialise the demo - clear the world
     */
    public final void initDemo() {
            world.clear();
            world.setGravity(gravity.x,gravity.y);
            
            Log.info("Initialising:" +getDemoName());
            init(world);
    }


    /**
     * Should be implemented by the demo, add the bodies/joints
     * to the world.
     * 
     * @param world The world in which the simulation is going to run
     */
    protected abstract void init(World world);
   
    /**
     * Draw a specific contact point determined from the simulation
     * 
     * @param g The graphics context on which to draw
     * @param contact The contact to draw
     */
    protected void drawContact(Graphics g, Contact contact){
    	int x = (int) contact.getPosition().getX();
        int y = (int) contact.getPosition().getY();
        
        if (renderContacts) {
            g.setColor(Color.blue);
            g.fillOval(x-3,y-3,6,6);
        }
    
	    if (renderNormals) {
	            int dx = (int) (contact.getNormal().getX() * 10);
	            int dy = (int) (contact.getNormal().getY() * 10);
	            g.setColor(Color.darkGray);
	            g.drawLine(x,y,x+dx,y+dy);
	    }
    }
    
    /**
     * Draw a body 
     * 
     * @param g The graphics context in which to draw
     * @param body The body to be drawn
     */
    protected void drawBody(Graphics g, Body body) {
            if (body.getShape() instanceof Box) {
                    drawBoxBody(g,body,(Box) body.getShape());
            }
            if (body.getShape() instanceof Circle) {
                    drawCircleBody(g,body,(Circle) body.getShape());
            }
            if (body.getShape() instanceof Line) {
                    drawLineBody(g,body,(Line) body.getShape());
            }
            if (body.getShape() instanceof Polygon) {
                    drawPolygonBody(g,body,(Polygon) body.getShape());
            }
    }
    
    protected void drawBoxBody(Graphics g, Body body, Box box) {
        Vector2f[] pts = box.getPoints(body.getPosition(), body.getRotation());
        
        Vector2f v1 = pts[0];
        Vector2f v2 = pts[1];
        Vector2f v3 = pts[2];
        Vector2f v4 = pts[3];
        
        g.setColor(Color.white);
        g.drawLine((int) v1.x,(int) v1.y,(int) v2.x,(int) v2.y);
        g.drawLine((int) v2.x,(int) v2.y,(int) v3.x,(int) v3.y);
        g.drawLine((int) v3.x,(int) v3.y,(int) v4.x,(int) v4.y);
        g.drawLine((int) v4.x,(int) v4.y,(int) v1.x,(int) v1.y);
    }
    
    protected void drawCircleBody(Graphics g, Body body, Circle circle) {
        g.setColor(Color.white);
        float x = body.getPosition().getX();
        float y = body.getPosition().getY();
        float r = circle.getRadius();
        float rot = body.getRotation();
        float xo = (float) (Math.cos(rot) * r);
        float yo = (float) (Math.sin(rot) * r);
        
        g.drawOval((int) (x-r),(int) (y-r),(int) (r*2),(int) (r*2));
        g.drawLine((int) x,(int) y,(int) (x+xo),(int) (y+yo));
    }
    
    protected void drawLineBody(Graphics g, Body body, Line line) {
        g.setColor(Color.white);
        Vector2f[] verts = line.getVertices(body.getPosition(), body.getRotation());
        g.drawLine(
                        (int) verts[0].getX(),
                        (int) verts[0].getY(), 
                        (int) verts[1].getX(),
                        (int) verts[1].getY());
    }
    
    protected void drawPolygonBody(Graphics g, Body body, Polygon poly) {
        g.setColor(Color.white);


        ROVector2f[] verts = poly.getVertices(body.getPosition(), body.getRotation());
        for ( int i = 0, j = verts.length-1; i < verts.length; j = i, i++ ) {                   
                g.drawLine(
                                (int) (0.5f + verts[i].getX()),
                                (int) (0.5f + verts[i].getY()), 
                                (int) (0.5f + verts[j].getX()),
                                (int) (0.5f + verts[j].getY()));
        }
    }
    
    public void drawJoint(Graphics g, Joint j) {
        if (j instanceof FixedJoint) {
                FixedJoint joint = (FixedJoint) j;
                
                g.setColor(Color.red);
                float x1 = joint.getBody1().getPosition().getX();
                float x2 = joint.getBody2().getPosition().getX();
                float y1 = joint.getBody1().getPosition().getY();
                float y2 = joint.getBody2().getPosition().getY();
                
                g.drawLine((int) x1,(int) y1,(int) x2,(int) y2);
        }
        if(j instanceof SlideJoint){
                SlideJoint joint = (SlideJoint) j;
                
                Body b1 = joint.getBody1();
                Body b2 = joint.getBody2();

                Matrix2f R1 = new Matrix2f(b1.getRotation());
                Matrix2f R2 = new Matrix2f(b2.getRotation());

                ROVector2f x1 = b1.getPosition();
                Vector2f p1 = MathUtil.mul(R1,joint.getAnchor1());
                p1.add(x1);

                ROVector2f x2 = b2.getPosition();
                Vector2f p2 = MathUtil.mul(R2,joint.getAnchor2());
                p2.add(x2);
                
                Vector2f im = new Vector2f(p2);
                im.sub(p1);
                im.normalise();
                
                
                
                g.setColor(Color.red);
                g.drawLine((int)p1.x,(int)p1.y,(int)(p1.x+im.x*joint.getMinDistance()),(int)(p1.y+im.y*joint.getMinDistance()));
                g.setColor(Color.blue);
                g.drawLine((int)(p1.x+im.x*joint.getMinDistance()),(int)(p1.y+im.y*joint.getMinDistance()),(int)(p1.x+im.x*joint.getMaxDistance()),(int)(p1.y+im.y*joint.getMaxDistance()));
        }
        if(j instanceof AngleJoint){
                AngleJoint angleJoint = (AngleJoint)j;
                Body b1 = angleJoint.getBody1();
                Body b2 = angleJoint.getBody2();
                float RA = j.getBody1().getRotation() + angleJoint.getRotateA();
                float RB = j.getBody1().getRotation() + angleJoint.getRotateB();
                
                Vector2f VA = new Vector2f((float) Math.cos(RA), (float) Math.sin(RA));
                Vector2f VB = new Vector2f((float) Math.cos(RB), (float) Math.sin(RB));
                
                Matrix2f R1 = new Matrix2f(b1.getRotation());
                Matrix2f R2 = new Matrix2f(b2.getRotation());
                
                ROVector2f x1 = b1.getPosition();
                Vector2f p1 = MathUtil.mul(R1,angleJoint.getAnchor1());
                p1.add(x1);

                ROVector2f x2 = b2.getPosition();
                Vector2f p2 = MathUtil.mul(R2,angleJoint.getAnchor2());
                p2.add(x2);
                
                g.setColor(Color.red);
                g.drawLine((int)p1.x,(int)p1.y,(int)(p1.x+VA.x*20),(int)(p1.y+VA.y*20));
                g.drawLine((int)p1.x,(int)p1.y,(int)(p1.x+VB.x*20),(int)(p1.y+VB.y*20));
        }
        if (j instanceof BasicJoint) {
                BasicJoint joint = (BasicJoint) j;
                
                Body b1 = joint.getBody1();
                Body b2 = joint.getBody2();

                Matrix2f R1 = new Matrix2f(b1.getRotation());
                Matrix2f R2 = new Matrix2f(b2.getRotation());

                ROVector2f x1 = b1.getPosition();
                Vector2f p1 = MathUtil.mul(R1,joint.getLocalAnchor1());
                p1.add(x1);

                ROVector2f x2 = b2.getPosition();
                Vector2f p2 = MathUtil.mul(R2,joint.getLocalAnchor2());
                p2.add(x2);

                g.setColor(Color.red);
                g.drawLine((int) x1.getX(), (int) x1.getY(), (int) p1.x, (int) p1.y);
                g.drawLine((int) p1.x, (int) p1.y, (int) x2.getX(), (int) x2.getY());
                g.drawLine((int) x2.getX(), (int) x2.getY(), (int) p2.x, (int) p2.y);
                g.drawLine((int) p2.x, (int) p2.y, (int) x1.getX(), (int) x1.getY());
        }
        if(j instanceof DistanceJoint){
                DistanceJoint joint = (DistanceJoint) j;
                
                Body b1 = joint.getBody1();
                Body b2 = joint.getBody2();

                Matrix2f R1 = new Matrix2f(b1.getRotation());
                Matrix2f R2 = new Matrix2f(b2.getRotation());

                ROVector2f x1 = b1.getPosition();
                Vector2f p1 = MathUtil.mul(R1,joint.getAnchor1());
                p1.add(x1);

                ROVector2f x2 = b2.getPosition();
                Vector2f p2 = MathUtil.mul(R2,joint.getAnchor2());
                p2.add(x2);
                
                g.setColor(Color.red);
                g.drawLine((int) p1.getX(), (int) p1.getY(), (int) p2.x, (int) p2.y);
        }
        if (j instanceof SpringJoint) {
                SpringJoint joint = (SpringJoint) j;
                
                Body b1 = joint.getBody1();
                Body b2 = joint.getBody2();

                Matrix2f R1 = new Matrix2f(b1.getRotation());
                Matrix2f R2 = new Matrix2f(b2.getRotation());

                ROVector2f x1 = b1.getPosition();
                Vector2f p1 = MathUtil.mul(R1,joint.getLocalAnchor1());
                p1.add(x1);

                ROVector2f x2 = b2.getPosition();
                Vector2f p2 = MathUtil.mul(R2,joint.getLocalAnchor2());
                p2.add(x2);
                
                g.setColor(Color.red);
                g.drawLine((int) x1.getX(), (int) x1.getY(), (int) p1.x, (int) p1.y);
                g.drawLine((int) p1.x, (int) p1.y, (int) p2.getX(), (int) p2.getY());
                g.drawLine((int) p2.getX(), (int) p2.getY(), (int) x2.getX(), (int) x2.getY());
        }
    }
    
}
