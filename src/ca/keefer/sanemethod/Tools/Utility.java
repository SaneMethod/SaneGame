package ca.keefer.sanemethod.Tools;
import java.util.Date;
import java.text.DateFormat;
import java.text.SimpleDateFormat;    

public class Utility {

	public static String getDateTime(){
		DateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
	    Date date = new Date();
	    return dateFormat.format(date);
	}
}
