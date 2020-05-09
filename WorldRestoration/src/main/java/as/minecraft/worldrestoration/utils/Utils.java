package as.minecraft.worldrestoration.utils;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;

public class Utils {
	//Translate standard Bukkit "&" color coding to actual colors
	public static String chat (String s) {
		return ChatColor.translateAlternateColorCodes('&', s);
	}
	
	//translate time units to number
	public static int getSecondsFromTimeUnit(String inputTimeString) {
		int inputNumber = Integer.parseInt(inputTimeString.trim().replaceAll("(\\d+).+", "$1"));
		
		int timeInSecondsToReturn = inputNumber;
        
        String timeString = inputTimeString.toLowerCase().replaceAll("[^a-zA-Z0-9]+","");
        if(timeString.contains("second") || timeString.replace(Integer.toString(inputNumber), "").equals("s"))
			timeInSecondsToReturn = inputNumber;
        else if(timeString.contains("minute") || timeString.replace(Integer.toString(inputNumber), "").equals("m"))
			timeInSecondsToReturn *= 60;
		else if (timeString.contains("hour") || timeString.replace(Integer.toString(inputNumber), "").equals("h"))
			timeInSecondsToReturn *= 60*60;
		else if (timeString.contains("day") || timeString.replace(Integer.toString(inputNumber), "").equals("d"))
			timeInSecondsToReturn *= 60*60*24;
		else {
			Bukkit.getLogger().warning("[WorldRestoration] time tag not recognized for input: \"" + inputTimeString + "\". Assuming \"" + inputNumber + "\" seconds!");
		}
		
		return timeInSecondsToReturn;
	}
	
	public static long getTicksFromTimeUnit(String inputTimeString) {
		int seconds = Utils.getSecondsFromTimeUnit(inputTimeString);
		return seconds * 20L;
	}
}

