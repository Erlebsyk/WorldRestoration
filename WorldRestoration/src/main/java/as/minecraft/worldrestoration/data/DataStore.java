//Class for accessing data from external threads, where bukkit-functions are not available

package as.minecraft.worldrestoration.data;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;

import org.apache.commons.lang.StringUtils;
import org.bukkit.Bukkit;

import as.minecraft.worldrestoration.WorldRestoration;

public class DataStore {
	private static Set<String> keys;
	private static HashMap<String, String> configStrings;
	private static HashMap<String, Integer> configInts;
	private static Set<String> worldNames;
	private static boolean running;
	
	public DataStore(WorldRestoration plugin) {
		DataStore.keys = plugin.getConfig().getKeys(true);
		DataStore.configStrings = new HashMap<String, String>();
		DataStore.configInts = new HashMap<String, Integer>();
		DataStore.worldNames = new HashSet<String>();
		DataStore.running = false;
		
		for(String k: keys) {
			String entry = plugin.getConfig().getString(k);
			//If entry is a valid integer, store it as an integer
			if(entry.matches("\\d+")) {
				try {
					int num = Integer.parseInt(entry);
					DataStore.configInts.put(k, num);
				}
				catch(NumberFormatException e) { //Entry is a positive number but not a valid int
					Bukkit.getLogger().severe("Input: \"" + entry + "\" in config.yml at setting \"" + k + "\" is not a valid integer. Maximum allowed value is: 2 147 483 647.");
					plugin.getPluginLoader().disablePlugin(plugin);
				}
		    } 
			else{ //Store all non-integers as strings
		    	DataStore.configStrings.put(k, entry);
		    	
		    	//Store world names separately
		    	if(k.contains("world-settings.") && StringUtils.countMatches(k, ".") == 1) {
		    		String worldName = k.replace("world-settings.", "");
		    		if(Bukkit.getWorld(worldName) != null)
		    			worldNames.add(worldName);
		    		else
		    			Bukkit.getLogger().warning("World with name: \"" + worldName + "\" is not found, and will not be restored!");
		    	}
		    }
		}
	}
	
	public static Set<String> getKeys(){
		return DataStore.keys;
	}
	
	public static String getString(String key) {
		return DataStore.configStrings.get(key);
	}
	public static int getInt(String key) {
		return DataStore.configInts.get(key);
	}
	
	public static Set<String> getWorldNames(){
		return DataStore.worldNames;
	}
	public static boolean isRunning() {
		return DataStore.running;
	}
	public static void startRunning() {
		DataStore.running = true;
	}
	public static void stopRunning() {
		DataStore.running = false;
	}
}
