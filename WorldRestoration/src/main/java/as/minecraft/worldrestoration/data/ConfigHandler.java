package as.minecraft.worldrestoration.data;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Scanner;
import java.util.Set;

import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.PluginDescriptionFile;

import as.minecraft.worldrestoration.WorldRestoration;
import as.minecraft.worldrestoration.utils.Utils;

public class ConfigHandler {
	private File configFile;
    private FileConfiguration config;
    private WorldRestoration plugin;
    
    public ConfigHandler(WorldRestoration plugin){
    	this.plugin = plugin;
    	loadConfigFile();
    }
    
	public void loadConfigFile() {
        this.configFile = new File(plugin.getDataFolder(), "config.yml");
        if (!this.configFile.exists()) {
        	plugin.getLogger().info("Config not found, generating default config.");
            this.configFile.getParentFile().mkdirs();
            plugin.saveResource("config.yml", false);
         }

        this.config = new YamlConfiguration();
        try { 
            this.config.load(configFile);
            
            String configVersion = this.config.getString("version");
            PluginDescriptionFile pdf = plugin.getDescription();
            String pluginVersion = pdf.getVersion();
            
            //Version tracking. Add new row if the config is changed between versions.
            HashMap<String, String> versionSupportMap = new HashMap<String, String>();
            versionSupportMap.put("1.0", "1.0.1-alpha");
            
            boolean configUpToDate = false;
            for(String version: versionSupportMap.keySet()) {
            	String versionSupports = versionSupportMap.get(version);
            	if(versionSupports.contains(configVersion) && versionSupports.contains(pluginVersion)) configUpToDate = true;
            }
            
            if(!configUpToDate) {
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
            		plugin.getLogger().severe("Could not read config version! Please validate config.yml.");
            		throw new RuntimeException();
            	}
            	
            	int nameCounter = 1;
        		String backupFileName = "config_backup.yml";
        		
        		
        		File configBackup =  new File(plugin.getDataFolder(), backupFileName);
        		
        		//Make sure to not overwrite an existing backup.
        		while(configBackup.exists()) {
        			backupFileName = "config_backup_" + Integer.toString(nameCounter) + ".yml";
        			configBackup =  new File(plugin.getDataFolder(), backupFileName);
        			nameCounter++;
        		}
        		
        		boolean renamed = configFile.renameTo(configBackup);
        		if(renamed) {
        			plugin.saveDefaultConfig();
        		}
        		else {
        			plugin.getLogger().severe("Could not take backup of config. Try renaming it manually and re-run the plugin.");
        			throw new RuntimeException();
        		}
        		
        		this.configFile = new File(plugin.getDataFolder(), "config.yml");
        		
        		File fileToImport = new File(plugin.getDataFolder(), backupFileName);
        		FileConfiguration configToImport = new YamlConfiguration();;
        		configToImport.load(fileToImport);
        		Set<String> importKeys = configToImport.getKeys(true);
        		Set<String> importKeysNoPath = new HashSet<String>();
        		
        		for(String key: importKeys) {
        			if(key.contains(".")) {
        				String[] cutKey = key.split("\\.", 100);
        				int cutKeyLen = cutKey.length;
        				importKeysNoPath.add(cutKey[cutKeyLen-1]);
        			}
        			else {
        				importKeysNoPath.add(key);
        			}
        		}

        		ArrayList<String> newConfigLines = new ArrayList<String>();
        		
        		try {
        			Scanner newConfigScan = new Scanner(configFile);
        			while (newConfigScan.hasNextLine()) {
        				newConfigLines.add(newConfigScan.nextLine() + "");
        			}
        			newConfigScan.close();
        		} catch (FileNotFoundException e) {
        			e.printStackTrace();
        		}
        		
        		ArrayList<String> newLines = new ArrayList<String>();
        		String configSection = "";
        		int sectionSpaces = 0;
        		
        		String newOptions = "";
        		
        		for(String line: newConfigLines) {
        			String newLine = line;
        			int spaces = 0;
        			for(int i = 0; i < line.length(); i++) {//Check number of spaces at the beginning of the line to get configSection
        				char letter = line.charAt(i);
        				if(letter == ' ') spaces++;
        				else break;
        			}
        			if(spaces < sectionSpaces) { //If group is done, move to parent section
        				int levelsToGoUp = (sectionSpaces - spaces)/4;
        				while(levelsToGoUp > 0) {
        					int lastDotLocation = configSection.lastIndexOf('.');
        					if(lastDotLocation == -1) {
        						configSection = "";
        						levelsToGoUp = 0;
        					}
        					else {
            					configSection = configSection.substring(0, lastDotLocation);
            					levelsToGoUp--;
        					}
        				}
        				sectionSpaces = spaces;
        				
        			}
        			else if(spaces > sectionSpaces) {
        				sectionSpaces = spaces;
        			}
        			boolean foundKey = false;
                    for(String key: importKeysNoPath) {
                    	if(line.trim().equals(key + ":")) {
                    		if(configSection.equals("")) configSection += key;
                    		else configSection += "." + key;
                    		foundKey = true;
                    		break;
                    	}
                    	else if(line.trim().startsWith(key + ":") && !key.equals("version")) {
                    		String valueAtKey = configToImport.getString(configSection + "." + key);
                    		if(!valueAtKey.matches("\\d+") && !valueAtKey.equals("false") && !valueAtKey.equals("true")) {
                    			valueAtKey = "'" + valueAtKey + "'";
                    		}
                    		newLine = line.replace(line.trim(), key + ": " + valueAtKey);
                    		foundKey = true;
                    		break;
                    	}
                    }
                    if(!foundKey && !line.trim().startsWith("#") && !newLine.contains("version")) newOptions += newLine.split("\\:")[0].trim() + " ";
                    newLines.add(newLine);
                }
        		
        		plugin.getLogger().warning("The following options were added to config: " + newOptions.trim());
        		
        		FileWriter fw;
        		String[] linesArray = newLines.toArray(new String[newConfigLines.size()]);
        		try {
        			fw = new FileWriter(configFile);
        			for (int i = 0; i < linesArray.length; i++) {
        				fw.write(linesArray[i] + "\n");
        			}
        			fw.close();
        		} catch (IOException e) {
        			e.printStackTrace();
        		}
        		
                plugin.reloadConfig();
                this.config = plugin.getConfig();
            }
        	this.validateConfig();
        }
        catch (IOException | InvalidConfigurationException e) {
        	plugin.getLogger().severe("Something went wrong loading config...");
            e.printStackTrace();
        }
    }
	
	
	
	private int getVersionValue(String versionString) { //versionString = 1.0.1 and similar
		int versionValue = 0;
		String[] versionSplitByPunctuations = versionString.split("[\\p{Punct}\\s]+");
		try {
			for(int i = 0; i < 3; i++)
				versionValue += (int) (Math.pow(10, (2-i)*3)) * Integer.parseInt(versionSplitByPunctuations[i]);
	    	return versionValue;
		}
		catch(Exception e) {
			plugin.getLogger().severe("Something went wrong reading the plugin version from config, has it been manually changed?");
			throw e;
		}
    	
	}
	
	public FileConfiguration getConfig() {
		return this.config;
	}
	
	
	private void validateConfig() { //Tests if the config file is readable by the program
		
		//---Global settings---//
		int regenDelaySeconds;
		int warningDelaySeconds;
		try {
			String regenDelay = config.getString("regen-delay");
			if(regenDelay.isEmpty()) throw new NoSuchFieldException();
			regenDelaySeconds = Utils.getSecondsFromTimeUnit(regenDelay);
			if(regenDelaySeconds < 0) {
				throw new NumberFormatException();
			}
		}catch(NumberFormatException e) {
			plugin.getLogger().severe("Something went wrong getting \"regen-delay\" from config. Please check this setting in config.yml: the delay must be positive.");
			throw new RuntimeException();
		}
		catch(NoSuchFieldException e) {
			plugin.getLogger().severe("Could not find \"regen-delay\" setting in config.yml. Please validate config.yml.");
			throw new RuntimeException();
		}
		catch(Exception e) {
			plugin.getLogger().severe("Something went wrong getting \"regen-delay\" from config. Please check this setting in config.yml.");
			throw new RuntimeException();
		}
		try {
			String warningDelay = config.getString("warning-delay");
			if(warningDelay.isEmpty()) throw new NoSuchFieldException();
			warningDelaySeconds = Utils.getSecondsFromTimeUnit(warningDelay);
			if(warningDelaySeconds < 0) throw new NumberFormatException();
			else if(warningDelaySeconds > regenDelaySeconds) throw new RuntimeException();
		}catch(NumberFormatException e) {
			plugin.getLogger().severe("Something went wrong getting \"warning-delay\" from config. Please check this setting in config.yml: the delay must be positive.");
			throw new RuntimeException();
		}
		catch(RuntimeException e) {
			plugin.getLogger().severe("Something went wrong getting \"warning-delay\" from config. Please check this setting in config.yml: the warning delay must be smaller than regen-delay.");
			throw e;
		}
		catch(NoSuchFieldException e) {
			plugin.getLogger().severe("Could not find \"warning-delay\" setting in config.yml. Please validate config.yml.");
			throw new RuntimeException();
		}
		catch(Exception e) {
			plugin.getLogger().severe("Something went wrong getting \"warning-delay\" from config. Please check this setting in config.yml.");
			throw new RuntimeException();
		}
		try {
			String warnMessage = config.getString("regen-warn-message");
			if(warnMessage.isEmpty()) throw new NoSuchFieldException();
		}catch(NoSuchFieldException e) {
			plugin.getLogger().severe("Could not find \"regen-warn-message\" setting in config.yml. Please validate config.yml.");
			throw new RuntimeException();
		}catch(Exception e) {
			plugin.getLogger().severe("Something went wrong getting \"regen-warn-message\" from config."
					+ " Please evaluate this setting in config.yml.");
			throw new RuntimeException();
		}
		try {
			String doneMessage = config.getString("regen-done-message");
			if(doneMessage.isEmpty()) throw new NoSuchFieldException();
		}catch(NoSuchFieldException e) {
			plugin.getLogger().severe("Could not find \"regen-done-message\" setting in config.yml. Please validate config.yml.");
			throw new RuntimeException();
		}catch(Exception e) {
			plugin.getLogger().severe("Something went wrong getting \"regen-done-message\" from config."
					+ " Please evaluate this setting in config.yml.");
			throw new RuntimeException();
		}
		//----------------//
		//---World Settings---//
		ConfigurationSection allWorldConfigs = config.getConfigurationSection("world-settings");
		Set<String> worldNames = allWorldConfigs.getKeys(false);
		boolean isRegenEnabled = false;
		try {
			if(worldNames.isEmpty()) throw new NoSuchFieldException();
		}catch(NoSuchFieldException e) {
			plugin.getLogger().severe("Could not find any valid worlds in \"world-settings\" in config.yml. Please validate config.yml.");
			throw new RuntimeException();
		}
		for(String worldName: worldNames) {
			ConfigurationSection worldConfig = allWorldConfigs.getConfigurationSection(worldName);
			try {
				String regenEnabled = worldConfig.getString("regen-enabled").toLowerCase();
				if(regenEnabled.isEmpty()) throw new NoSuchFieldException();
				if(!(regenEnabled.equals("true") || regenEnabled.equals("false"))) throw new RuntimeException();
				if(regenEnabled.equals("true")) isRegenEnabled = true;
			}catch(RuntimeException e) {
				plugin.getLogger().severe("Something went wrong getting world setting \"regen-enabled\" for world: \""
						+ worldName + "\" from config. Please evaluate this setting in config.yml, this setting must be either true or false!");
				throw e;
			}catch(NoSuchFieldException e) {
				plugin.getLogger().severe("Could not find \"regen-enabled\" setting in config.yml for world \""+ worldName +"\". Please validate config.yml.");
				throw new RuntimeException();
			}catch(Exception e) {
				plugin.getLogger().severe("Something went wrong getting world setting \"regen-enabled\" for world: \""
						+ worldName + "\" from config. Please evaluate this setting in config.yml.");
				throw new RuntimeException();
			}
			try {
				String oldAfter = worldConfig.getString("considered-old-after");
				if(oldAfter.isEmpty()) throw new NoSuchFieldException();
				Utils.getSecondsFromTimeUnit(oldAfter);
			}catch(NoSuchFieldException e) {
				plugin.getLogger().severe("Could not find \"considered-old-after\" setting in config.yml for world \""+ worldName +"\". Please validate config.yml.");
				throw new RuntimeException();
			}catch(Exception e) {
				plugin.getLogger().severe("Something went wrong getting world setting \"considered-old-after\" for world: \""
						+ worldName + "\" from config. Please evaluate this setting in config.yml.");
				throw new RuntimeException();
			}
			try {
				int radius = worldConfig.getInt("radius");
				if(radius == 0) throw new NoSuchFieldException();
				if(radius < 0) throw new NumberFormatException();
				radius += 1; //Check if operators work on the int
			}catch(NumberFormatException e) {
				plugin.getLogger().severe("Something went wrong getting world setting \"radius\" for world: \""
						+ worldName + "\" from config. The radius needs to be positive!");
				throw e;
			}catch(NoSuchFieldException e) {
				plugin.getLogger().severe("Could not find \"radius\" setting in config.yml for world \""+ worldName +"\". Please validate config.yml.");
				throw new RuntimeException();
			}catch(Exception e) {
				plugin.getLogger().severe("Something went wrong getting world setting \"considered-old-after\" for world: \""
						+ worldName + "\" from config. Please evaluate this setting in config.yml.");
				throw new RuntimeException();
			}
			try {
				String respectClaims = worldConfig.getString("respect-claims");
				if(respectClaims.isEmpty()) throw new NoSuchFieldException();
				if(!(respectClaims.equals("true") || respectClaims.equals("false"))) throw new RuntimeException();
			}catch(NoSuchFieldException e) {
				plugin.getLogger().severe("Could not find \"respectClaims\" setting in config.yml for world \""+ worldName +"\". Please validate config.yml.");
				throw new RuntimeException();
			}catch(RuntimeException e) {
				plugin.getLogger().severe("Something went wrong getting world setting \"respect-claims\" for world: \""
						+ worldName + "\" from config. Please evaluate this setting in config.yml: Setting must be either true or false!");
				throw e;
			}catch(Exception e) {
				plugin.getLogger().severe("Something went wrong getting world setting \"respect-claims\" for world: \""
						+ worldName + "\" from config. Please evaluate this setting in config.yml.");
				throw new RuntimeException();
			}
		}
		if(!isRegenEnabled) plugin.getLogger().warning("No worlds are scheduled for restoration in config, as set by default."
				+ " Enable restoration by setting \"regen-enabled: true\" for at least one world in config.yml.");
		//---Protection settings---//
		try {
			String supportEnabled = config.getString("enable-griefdefender-support").toLowerCase();
			if(supportEnabled.isEmpty()) throw new NoSuchFieldException();
			if(!(supportEnabled.equals("true") || supportEnabled.equals("false"))) throw new RuntimeException();
		}catch(NoSuchFieldException e) {
			plugin.getLogger().severe("Could not find \"enable-griefdefender-support\" setting in config.yml. Please validate config.yml.");
			throw new RuntimeException();
		}catch(RuntimeException e) {
			plugin.getLogger().severe("Something went wrong getting setting \"enable-griefdefender-support\""
					+  " from config. Please evaluate this setting in config.yml, value must be either true or false!");
			throw e;
		}catch(Exception e) {
			plugin.getLogger().severe("Something went wrong getting setting \"enable-griefdefender-support\""
					+  " from config. Please evaluate this setting in config.yml!");
			throw new RuntimeException();
		}
		ConfigurationSection claimConfig = config.getConfigurationSection("claim-settings");
		try {
			int padding = claimConfig.getInt("claim-padding");
			if(padding == 0) throw new NoSuchFieldException();
			if(padding < 0) throw new NumberFormatException();
			padding += 1; //Check if operators work on the int
		}catch(NumberFormatException e) {
			plugin.getLogger().severe("Something went wrong getting claim setting \"claim-padding\""
					+ " from config. The radius needs to be positive!");
			throw e;
		}catch(NoSuchFieldException e) {
			plugin.getLogger().severe("Could not find \"claim-padding\" setting in config.yml. Please validate config.yml.");
			throw new RuntimeException();
		}catch(Exception e) {
			plugin.getLogger().severe("Something went wrong getting claim setting \"claim-padding\""
					+ " from config. Please evaluate this setting in config.yml, it must be a positive integer.");
			throw new RuntimeException();
		}
	}
	
}
