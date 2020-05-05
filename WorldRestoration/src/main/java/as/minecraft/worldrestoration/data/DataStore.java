//Class for accessing data from external threads, where bukkit-functions are not available

package as.minecraft.worldrestoration.data;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;

import org.apache.commons.lang.StringUtils;
import org.bukkit.Bukkit;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.PluginDescriptionFile;

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
	private File configFile;
    private FileConfiguration config;
	
	public DataStore(WorldRestoration plugin) {
		DataStore.configStrings = new HashMap<String, String>();
		DataStore.configInts = new HashMap<String, Integer>();
		DataStore.worldNames = new HashSet<String>();
		DataStore.running = false;
		
		this.plugin = plugin;
	}
	
	public void loadConfigFile() {
        this.configFile = new File(plugin.getDataFolder(), "config.yml");
        if (!this.configFile.exists()) {
        	plugin.getLogger().info("Config not found, generating default config...");
            this.configFile.getParentFile().mkdirs();
            plugin.saveResource("config.yml", false);
         }

        this.config = new YamlConfiguration();
        try { 
            this.config.load(configFile);
            String configVersion = this.config.getString("version");
            PluginDescriptionFile pdf = plugin.getDescription();
            String pluginVersion = pdf.getVersion();
            if(!configVersion.equals(pluginVersion)) {
            	int configVersionValue = getVersionValue(configVersion);
            	int pluginVersionValue = getVersionValue(pluginVersion);
            	if(pluginVersionValue > configVersionValue) {
            		plugin.getLogger().warning("Config file outdated! A copy of the old is saved as \"config_backup.yml\", and a new one is created. "
            				+ "The old settings will be imported, but make sure to review the new settings and verify that "
            				+ "the old settings was imported correctly into the new \"config.yml\"!");
            	}
            	else if(pluginVersionValue < configVersionValue) {
            		plugin.getLogger().warning("A config file for a newer version of the plugin was detected,"
            				+ " if this is a mistake make sure to update the plugin to the latest version!"
            				+ " The unsupported config is stored as config_backup,"
            				+ " and the settings have been imported into the new \"config.yml\".");
            	}
            	else {
            		this.validateConfig();
            	}
            }
        }
        catch (IOException | InvalidConfigurationException e) {
            e.printStackTrace();
        }
    }
	
	private int getVersionValue(String versionString) { //version string: 1.0.1 and similar
		int versionValue = 0;
		try {
			for(int i = 0; i < 3; i++) {
	    		versionValue += (int) (Math.pow(10, (2-i)*3)) * Integer.parseInt(versionString.split("[\\p{Punct}\\s]+")[i]);
	    	}
	    	return versionValue;
		}
		catch(Exception e) {
			plugin.getLogger().severe("Something went wrong reading the plugin version from config, has it been manually changed?");
			throw e;
		}
    	
	}
	
	private void validateConfig() {
		
	}
	
	public void storeDataFromConfig() {
		DataStore.keys = this.config.getKeys(true);
		for(String k: keys) {
			String entry = this.config.getString(k);
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
