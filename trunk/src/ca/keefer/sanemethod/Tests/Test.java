package ca.keefer.sanemethod.Tests;

import org.newdawn.slick.Color;

import ca.keefer.sanemethod.Interface.SaneSystem;

public class Test {

	public static void drawStringTest(SaneSystem systemTest){
		String testString = "";
		systemTest.getFonts().get("interfaceFont").drawString(10, 50, testString,Color.white);
		systemTest.getFonts().get("dataFont").drawString(10, 100, testString,Color.white);
		systemTest.getFonts().get("standardFont").drawString(10, 150, testString,Color.white);
		systemTest.getFonts().get("scaryFont").drawString(10, 200, testString,Color.white);
		systemTest.getFonts().get("kingdomFont").drawString(10, 250, testString,Color.white);
		systemTest.getFonts().get("elvenFont").drawString(10, 350, testString,Color.white);
	}
}
