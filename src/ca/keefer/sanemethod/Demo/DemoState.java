package ca.keefer.sanemethod.Demo;

import org.newdawn.slick.Color;
import org.newdawn.slick.GameContainer;
import org.newdawn.slick.Graphics;
import org.newdawn.slick.Input;
import org.newdawn.slick.SlickException;
import org.newdawn.slick.state.BasicGameState;
import org.newdawn.slick.state.StateBasedGame;

/**
 * This class instantiates a state in which all demos take place and can be interacted with.
 * @author Christopher Keefer
 * @version 1.1
 * @since 11/05/09
 *
 */
public class DemoState extends BasicGameState{

	int stateID;
	/** The current demo we want to look at - ranges from 1 - 13 */
	int demoOffset = 1;
	AbstractDemo currentDemo;
	
	public DemoState(int stateID){
		this.stateID=stateID;
	}
	
	@Override
	public int getID() {
		return stateID;
	}

	@Override
	public void init(GameContainer container, StateBasedGame game)
			throws SlickException {
		// TODO Auto-generated method stub
		currentDemo =new Demo1();
		
	}

	@Override
	public void render(GameContainer container, StateBasedGame game, Graphics g)
			throws SlickException {
		g.setBackground(Color.black);
		g.setColor(Color.white);
		g.drawString("R = Reset Demo", 450, 10);
		g.drawString("Right Key = Next Demo", 450, 30);
		g.drawString("Left Key = Previous Demo",450,50);
		g.drawString("Escape = Exit Demo",450,70);
		currentDemo.render(g);
		
	}

	@Override
	public void update(GameContainer container, StateBasedGame game, int delta)
			throws SlickException {
		// TODO Auto-generated method stub
		currentDemo.update(delta);
		
	}
	
	@Override
	public boolean isAcceptingInput(){
		return true;
	}
	
	@Override
	public void keyPressed(int keyPressed, char keyChar){
		if (keyPressed == Input.KEY_RIGHT){
			if (demoOffset < 10){
				demoOffset += 1;
				nextDemo();
			}
		}else if (keyPressed == Input.KEY_LEFT){
			if (demoOffset > 1){
				demoOffset -= 1;
				nextDemo();
			}
		}else{
			currentDemo.receiveKeyPress(keyPressed, keyChar);
		}
	}
	
	/** set current demo to the demo pointed to by demoOffset */
	public void nextDemo(){
		switch (demoOffset){
		case 1:
			currentDemo = new Demo1();
			break;
		case 2:
			currentDemo = new Demo2();
			break;
		case 3:
			currentDemo = new Demo3();
			break;
		case 4:
			currentDemo = new Demo4();
			break;
		case 5:
			currentDemo = new Demo5();
			break;
		case 6:
			currentDemo = new Demo6();
			break;
		case 7:
			currentDemo = new Demo7();
			break;
		case 8:
			currentDemo = new Demo8();
			break;
		case 9:
			currentDemo = new Demo9();
			break;
		case 10:
			currentDemo = new Demo10();
			break;
		}
	}

}
