package as.minecraft.worldrestoration.utils;

import org.bukkit.ChatColor;

public class Utils {
	
	//Translate standard Bukkit "&" color coding to actual colors
	public static String chat (String s) {
		return ChatColor.translateAlternateColorCodes('&', s);
	}
}

