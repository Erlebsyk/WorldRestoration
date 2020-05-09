package as.minecraft.worldrestoration.data;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
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
            	ConfigVersionAdapter cfgVersionAdapter = new ConfigVersionAdapter(plugin, pluginVersion, configVersion);
            	this.config = cfgVersionAdapter.updateConfig(configFile);
            }
        	this.validateConfig();
        }
        catch (IOException | InvalidConfigurationException e) {
        	plugin.getLogger().severe("Something went wrong loading config...");
            e.printStackTrace();
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
