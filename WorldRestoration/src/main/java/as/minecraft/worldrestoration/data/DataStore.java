//Class for accessing data from external threads, where bukkit-functions are not available

package as.minecraft.worldrestoration.data;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;

import org.apache.commons.lang.StringUtils;
import org.bukkit.Bukkit;
import org.bukkit.configuration.file.FileConfiguration;

import as.minecraft.worldrestoration.WorldRestoration;

public class DataStore {
	//Static variables accessible from external threads
	private static Set<String> keys;
	private static HashMap<String, String> configStrings;
	private static HashMap<String, Integer> configInts;
	private static Set<String> worldNames;
	private static boolean running;
	
	//Non-static variables that should never be accessed from external threads
	private WorldRestoration plugin;
	
	public DataStore(WorldRestoration plugin, FileConfiguration config) {
		DataStore.configStrings = new HashMap<String, String>();
		DataStore.configInts = new HashMap<String, Integer>();
		DataStore.worldNames = new HashSet<String>();
		DataStore.running = false;
		this.plugin = plugin;
		
		storeDataFromConfig(config);
	}
	
	private void storeDataFromConfig(FileConfiguration config) {
		DataStore.keys = config.getKeys(true);
		for(String k: keys) {
			String entry = config.getString(k);
			//If entry is a valid integer, store it as an integer
			if(entry.matches("\\d+")) {
				try {
					int num = Integer.parseInt(entry);
					DataStore.configInts.put(k, num);
				}
				catch(NumberFormatException e) { //Entry is a positive number but not a valid int
					plugin.getLogger().severe("Input: \"" + entry + "\" in config.yml at setting \"" + k + "\" is not a valid integer. Maximum allowed value is: 2 147 483 647.");
					throw new RuntimeException();
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
		    			plugin.getLogger().warning("World with name: \"" + worldName + "\" is not found, and will not be restored!");
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
