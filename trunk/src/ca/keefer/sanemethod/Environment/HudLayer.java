package ca.keefer.sanemethod.Environment;

import org.newdawn.slick.Color;
import org.newdawn.slick.Graphics;
import org.newdawn.slick.SlickException;
import org.newdawn.slick.SpriteSheet;
import org.newdawn.slick.util.Log;

import ca.keefer.sanemethod.Constants;
import ca.keefer.sanemethod.Entity.Player;

public class HudLayer implements Layer{
	
	private int id;
	private boolean active;
	private Player thePlayer;
	private ViewPort viewPort;
	int coins;
	SpriteSheet hudSheet;
	
	public HudLayer(Player thePlayer){
		this.thePlayer=thePlayer;
		this.active=true;
		try{
			hudSheet = new SpriteSheet("res/UI/LifeCoinPanel.png",64,64);
		}catch(SlickException e){
			Log.debug("Error loading Hud Sheet:"+e.getMessage());
		}
		coins = thePlayer.getCoins();
	}

	@Override
	public int getId() {
		return id;
	}

	@Override
	public int getType() {
		return Constants.LAYER_HUD;
	}
	
	public void setPlayer(Player p){
		thePlayer = p;
	}

	@Override
	public ViewPort getViewPort() {
		return viewPort;
	}

	@Override
	public boolean isActive() {
		return active;
	}

	@Override
	public void setActive(boolean t) {
		this.active=t;
		
	}

	@Override
	public void setId(int Id) {
		this.id=Id;
	}

	@Override
	public void setViewPort(ViewPort v) {
		viewPort=v;
	}

	@Override
	public void update(int delta) {
		coins = thePlayer.getCoins();
	}
	
	@Override
	public void render(Graphics g) {
		g.translate(viewPort.getPosition().getX(),viewPort.getPosition().getY());
		/*
		hudSheet.getSubImage(0, 0, 98, 52).draw(10, 10);
		for (int i=0;i<=lifePoints;i++){
			hudSheet.getSubImage(98, 0, 30, 52).draw(108+(30*i), 10);
		}
		*/
		hudSheet.getSubImage(0, 64, 40, 44).draw(10, 20);
		Constants.saneSystem.getFonts().get("interfaceFont").drawString(50, 24, ""+coins, Color.white);
		g.translate(-viewPort.getPosition().getX(),-viewPort.getPosition().getY());
	}

	@Override
	public void render(Graphics g, int xOffset, int yOffset, int xLimit,
			int yLimit) {
		render(g);
	}

}
