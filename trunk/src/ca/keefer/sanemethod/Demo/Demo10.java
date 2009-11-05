package ca.keefer.sanemethod.Demo;

import net.phys2d.math.Vector2f;
import net.phys2d.raw.Body;
import net.phys2d.raw.DistanceJoint;
import net.phys2d.raw.SpringJoint;
import net.phys2d.raw.SpringyAngleJoint;
import net.phys2d.raw.StaticBody;
import net.phys2d.raw.World;
import net.phys2d.raw.shapes.Circle;

/**
 * A test to demonstrate the SpringAngleJoint which can be used to produce free
 * standing springs.
 * 
 * @author guRuQu
 */
public class Demo10 extends AbstractDemo {
        /**
         * Create a new test
         */
        public Demo10(){
                super("Slick Phys2d Demo 10 - Springy Test");
        }
        
       @Override
        protected void init(World world) {
                Body knot = new StaticBody(new Circle(10));
                knot.setPosition(100,400);
                knot.setRotation(0.5f);
                world.add(knot);
                int N=8;
                Body balls[] = new Body[N];
                for(int i=0;i<N;i++){
                        Body ball = new Body(new Circle(5),10);
                        ball.setPosition(100,400-(i+1)*50);
                        ball.setDamping(0.01f);
                        world.add(ball);
                        balls[i]=ball;
                }
                
                for(int i=0;i<N;i++){
                        if(i==0){
                                SpringyAngleJoint saj1 = new SpringyAngleJoint(knot,balls[i],new Vector2f(),new Vector2f(),1e6f,-(float)Math.PI/2.0f-0.15f);
                                SpringyAngleJoint saj2 = new SpringyAngleJoint(balls[i],knot,new Vector2f(),new Vector2f(),1e6f,(float)Math.PI/2.0f);
                                //DistantConstraint daj = new DistantConstraint(knot,balls[i],new Vector2f(),new Vector2f(),50);
                                SpringJoint daj = new SpringJoint(knot,balls[i],new Vector2f(100,400),new Vector2f(balls[i].getPosition()));
                                daj.setCompressedSpringConst(100);
                                daj.setStretchedSpringConst(100);
                                daj.setSpringSize(30);
                                world.add(daj);
                                world.add(saj1);
                                world.add(saj2);
                        }else{
                                SpringyAngleJoint saj1 = new SpringyAngleJoint(balls[i-1],balls[i],new Vector2f(),new Vector2f(),1e6f,-(float)Math.PI/2.0f);
                                SpringyAngleJoint saj2 = new SpringyAngleJoint(balls[i],balls[i-1],new Vector2f(),new Vector2f(),1e6f,(float)Math.PI/2.0f);
                                //DistantConstraint daj = new DistantConstraint(balls[i-1],balls[i],new Vector2f(),new Vector2f(),50);
                                SpringJoint daj = new SpringJoint(balls[i-1],balls[i],new Vector2f(balls[i-1].getPosition()),new Vector2f(balls[i].getPosition()));
                                daj.setCompressedSpringConst(100);
                                daj.setStretchedSpringConst(100);
                                daj.setSpringSize(50);
                                world.add(daj);
                                world.add(saj1);
                                world.add(saj2);
                        }
                }
                Body balls1[] = new Body[N];
                for(int i=0;i<N;i++){
                        Body ball = new Body(new Circle(5),10);
                        ball.setPosition(100,400-(i+1)*50);
                        ball.setDamping(0.01f);
                        world.add(ball);
                        balls1[i]=ball;
                }
                for(int i=0;i<N;i++){
                        if(i==0){
                                SpringyAngleJoint saj1 = new SpringyAngleJoint(knot,balls1[i],new Vector2f(),new Vector2f(),1e6f,-(float)Math.PI/2.0f+0.45f);
                                SpringyAngleJoint saj2 = new SpringyAngleJoint(balls1[i],knot,new Vector2f(),new Vector2f(),1e6f,(float)Math.PI/2.0f);
                                DistanceJoint daj = new DistanceJoint(knot,balls1[i],new Vector2f(),new Vector2f(),50);


                                world.add(daj);
                                world.add(saj1);
                                world.add(saj2);
                        }else{
                                SpringyAngleJoint saj1 = new SpringyAngleJoint(balls1[i-1],balls1[i],new Vector2f(),new Vector2f(),1e6f,-(float)Math.PI/2.0f);
                                SpringyAngleJoint saj2 = new SpringyAngleJoint(balls1[i],balls1[i-1],new Vector2f(),new Vector2f(),1e6f,(float)Math.PI/2.0f);
                                DistanceJoint daj = new DistanceJoint(balls1[i-1],balls1[i],new Vector2f(),new Vector2f(),50);


                                world.add(daj);
                                world.add(saj1);
                                world.add(saj2);
                        }
                }
        }
       
       @Override
       public void update(int delta){
    	   world.step(delta*0.001f);
       }
}
