package ca.keefer.sanemethod.Environment;

import java.util.Arrays;
import java.util.Hashtable;

import org.newdawn.slick.BigImage;
import org.newdawn.slick.Graphics;
import org.newdawn.slick.SlickException;

import ca.keefer.sanemethod.Constants;

/**
 * This class implements Layer, and is responsible for containing any background image,
 * and scrolling it, clamping it, etc., as desired
 * @author Christopher Keefer
 * @see ca.keefer.sanemethod.Environment.Layer
 *
 */
public class BackgroundLayer implements Layer{
	
	private int id;
	private boolean active;
	private ViewPort viewPort;
	private BigImage[] backImages;
	private int length;
	
	public BackgroundLayer(int id, boolean active){
		this.id=id;
		this.active=active;
		length=0;
	}
	
	/**
	 * Add a new BigImage to the background
	 * @param image
	 */
	public void add(BigImage image){
		if (length > 0){
			BigImage[] temp = new BigImage[length+1];
			for (int i=0;i<length;i++){
				temp[i] = backImages[i];
			}
			length++;
			temp[length]=image;
			backImages = temp;
			temp=null;
		}else{
			backImages = new BigImage[1];
			backImages[0] = image;
			length++;
		}
		
		sortByWidth();
	}
	
	/**
	 * Add a new BigImage to the background based on a String reference
	 * @param ref
	 * @throws SlickException
	 */
	public void add(String ref) throws SlickException{
		if (length > 0){
			BigImage[] temp = new BigImage[length+1];
			for (int i=0;i<length;i++){
				temp[i] = backImages[i];
			}
			temp[length]= new BigImage(ref);
			backImages = temp;
			temp=null;
			length++;
		}else{
			backImages = new BigImage[1];
			backImages[0] = new BigImage(ref);
			length++;
		}
	}
	
	public BigImage[] getBackImages(){
		return backImages;
	}
	
	public BigImage getBigImageAt(int index){
		if (index < length){
			return backImages[index];
		}
		return null;
	}
	
	public int getNumOfImages(){
		return this.backImages.length;
	}
	
	/** 
	 * Sort the given backImages according to their widths
	 */
	public void sortByWidth(){
		BigImage[] temp = new BigImage[length];
		Hashtable<Integer, Integer> hashIndex = new Hashtable<Integer, Integer>(length);
		int[] index = new int[length];
		for (int i=0;i<length;i++){
			hashIndex.put(backImages[i].getWidth(), i);
			index[i]=backImages[i].getWidth();
		}
		
		Arrays.sort(index);
		
		for (int i=0;i<length;i++){
			temp[i] = backImages[hashIndex.get(index[i])];
			hashIndex.remove(hashIndex.get(index[i]));
		}
		
		backImages=temp;
		temp=null;
		hashIndex=null;
		index=null;
	}

	@Override
	public int getId() {
		return id;
	}

	@Override
	public int getType() {
		return Constants.LAYER_BACKGROUND;
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
		active=t;
	}

	@Override
	public void setId(int Id) {
		id=Id;
	}

	@Override
	public void setViewPort(ViewPort v) {
		viewPort=v;
	}

	@Override
	public void update(int delta) {
		//TODO:Something?
	}
	
	@Override
	public void render(Graphics g) {
		for (int i=0; i<backImages.length;i++){
			float xOffset = (viewPort.getPosition().getX()+Constants.SCREENWIDTH)/backImages[i].getWidth();
			//Log.debug("xPos:"+viewPort.getPosition().getX()+"width:"+backImages[i].getWidth()+"xOffset:"+xOffset);
			if (xOffset >= 1){
				for (int j=(int) (xOffset-1);j<=xOffset;j++){
					backImages[i].draw(backImages[i].getWidth()*j,viewPort.getPosition().getY());
				}
			}else{
				backImages[i].draw(0, viewPort.getPosition().getY());
			}
		}
	}

	@Override
	public void render(Graphics g, int xOffset, int yOffset, int xLimit,
			int yLimit) {
		// TODO Auto-generated method stub
		
	}

}
