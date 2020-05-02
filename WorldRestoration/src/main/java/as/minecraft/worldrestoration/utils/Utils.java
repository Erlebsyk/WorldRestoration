package as.minecraft.worldrestoration.utils;


import org.bukkit.ChatColor;

public class Utils {
	//Translate standard Bukkit "&" color coding to actual colors
	public static String chat (String s) {
		return ChatColor.translateAlternateColorCodes('&', s);
	}
	
	//translate time units to number
	public static int getSecondsFromTimeUnit(String inputTimeString) {
		int inputNumber;
		try {
			inputNumber = Integer.parseInt(inputTimeString.replaceAll("(\\d+).+", "$1"));
		}
		catch(NumberFormatException e) {
			inputTimeString = "1000d";
			inputNumber = 1000;
		}
		
		int timeInSecondsToReturn = inputNumber;
        
        String timeString = inputTimeString.toLowerCase().replaceAll("[^a-zA-Z0-9]+","");
		if(timeString.contains("minute") || timeString.replace(Integer.toString(inputNumber), "").equals("m"))
			timeInSecondsToReturn *= 60;
		else if (timeString.contains("hour") || timeString.replace(Integer.toString(inputNumber), "").equals("h"))
			timeInSecondsToReturn *= 60*60;
		else if (timeString.contains("day") || timeString.replace(Integer.toString(inputNumber), "").equals("d"))
			timeInSecondsToReturn *= 60*60*24;
		
		return timeInSecondsToReturn;
	}
	
	public static long getTicksFromTimeUnit(String inputTimeString) {
		int seconds = Utils.getSecondsFromTimeUnit(inputTimeString);
		return seconds * 20L;
	}
}

