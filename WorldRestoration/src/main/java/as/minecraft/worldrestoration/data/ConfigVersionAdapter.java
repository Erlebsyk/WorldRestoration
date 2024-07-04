package as.minecraft.worldrestoration.data;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Scanner;
import java.util.Set;

import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

import as.minecraft.worldrestoration.WorldRestoration;

public class ConfigVersionAdapter {
	
	private WorldRestoration plugin;
	private String configSection;
	private String pluginVersion;
	private String configVersion;
	
	public ConfigVersionAdapter(WorldRestoration plugin, String pluginVersion, String configVersion) {
		this.plugin = plugin;
		this.configSection = "";
		this.pluginVersion = pluginVersion;
		this.configVersion = configVersion;
	}
	
	public FileConfiguration updateConfig(File configFile) {
		int configVersionValue = getVersionValue(configVersion);
    	int pluginVersionValue = getVersionValue(pluginVersion);
		if(pluginVersionValue != configVersionValue){
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
		
		File fileToImport = new File(plugin.getDataFolder(), backupFileName);
		
		importConfig(fileToImport);
		
        plugin.reloadConfig();
        return plugin.getConfig();
	}
	
	public void importConfig(File fileToImport) {
		try {
			File configFile = new File(plugin.getDataFolder(), "config.yml");
		
			FileConfiguration configToImport = new YamlConfiguration();
			configToImport.load(fileToImport);
		
			Set<String> importKeys = configToImport.getKeys(true);
			Set<String> importKeysNoPath = getKeysWithoutPath(importKeys);

			ArrayList<String> originalConfigLines = getConfigLines(configFile);
		
			ArrayList<String> newLines = new ArrayList<String>();
			String newOptions = "";
			int sectionSpaces = 0;
		
			for(String line: originalConfigLines) {
				String newLine = line;
				sectionSpaces = getSpaces(sectionSpaces, line);
				boolean foundKey = false;
            	for(String key: importKeysNoPath) { //Check if the line has a corresponding key in the backup
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
            	if(!foundKey && !line.trim().startsWith("#") && !newLine.contains("version"))
            		newOptions += newLine.split("\\:")[0].trim() + " ";
            	newLines.add(newLine);
        	}
			if(!newOptions.trim().equals(""))
				plugin.getLogger().warning("The following options were added to config: " + newOptions.trim());
			
			writeLinesToConfig(newLines, originalConfigLines, configFile);
		}
		catch (IOException | InvalidConfigurationException e) {
        	plugin.getLogger().severe("Something went wrong importing settings from the old config...");
            e.printStackTrace();
        }
		
	}
	
	private Set<String> getKeysWithoutPath(Set<String> keys){
		Set<String> keysNoPath = new HashSet<String>();
		for(String key: keys) {
			if(key.contains(".")) {
				String[] cutKey = key.split("\\.");
				int cutKeyLen = cutKey.length;
				keysNoPath.add(cutKey[cutKeyLen-1]);
			}
			else {
				keysNoPath.add(key);
			}
		}
		return keysNoPath;
	}
	
	private ArrayList<String> getConfigLines(File configFile) {
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
		return newConfigLines;
	}
	
	private int getSpaces(int sectionSpaces, String line) {
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
		return sectionSpaces;
	}
	
	private void writeLinesToConfig(ArrayList<String> linesToWrite, ArrayList<String> originalConfigLines, File targetFile) {
		FileWriter fw;
		String[] linesArray = linesToWrite.toArray(new String[originalConfigLines.size()]);
		try {
			fw = new FileWriter(targetFile);
			for (int i = 0; i < linesArray.length; i++) {
				fw.write(linesArray[i] + "\n");
			}
			fw.close();
		} catch (IOException e) {
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
}
