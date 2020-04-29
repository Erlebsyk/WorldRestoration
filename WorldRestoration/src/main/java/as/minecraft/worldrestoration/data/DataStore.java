//Class for accessing data from external threads, where bukkit-functions are not available

package as.minecraft.worldrestoration.data;

import java.util.HashMap;
import java.util.Set;

import as.minecraft.worldrestoration.WorldRestoration;

public class DataStore {
	private static Set<String> keys;
	private static HashMap<String, String> configStrings;
	private static HashMap<String, Integer> configInts;
	
	public DataStore(WorldRestoration plugin) {
		DataStore.keys = plugin.getConfig().getKeys(true);
		DataStore.configStrings = new HashMap<String, String>();
		DataStore.configInts = new HashMap<String, Integer>();
		
		for(String k: keys) {
			String entry = plugin.getConfig().getString(k);
			try {
		        int num = Integer.parseInt(entry);
		        DataStore.configInts.put(k, num);
		    } catch (NumberFormatException nfe) {
		    	DataStore.configStrings.put(k, entry);
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
	
}
