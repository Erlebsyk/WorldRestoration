package as.minecraft.worldrestoration;

import org.bukkit.Bukkit;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.plugin.java.JavaPlugin;

import as.minecraft.worldrestoration.data.ConfigHandler;
import as.minecraft.worldrestoration.data.DataStore;
import as.minecraft.worldrestoration.dependencies.CoreProtectHook;
import as.minecraft.worldrestoration.tasks.RegenTask;
import as.minecraft.worldrestoration.utils.Utils;

import org.bukkit.plugin.PluginDescriptionFile;
import org.bukkit.plugin.java.JavaPluginLoader;
import java.io.File;

public class WorldRestoration extends JavaPlugin{

	public WorldRestoration()
	{
		super();
	}

	protected WorldRestoration(JavaPluginLoader loader, PluginDescriptionFile description, File dataFolder, File file)
	{
		super(loader, description, dataFolder, file);
	}

	@Override
	public void onEnable() {
		
		//----Setup-----
		try {
			getLogger().info("Loading configurations...");
			ConfigHandler configHandler = new ConfigHandler(this);
			FileConfiguration config = configHandler.getConfig();
			
			config = configHandler.getConfig();
			
			new DataStore(this, config);
			
			long regenDelay = Utils.getTicksFromTimeUnit(DataStore.getString("regen-delay"));
			long waitBeforeStart = regenDelay;
		
			getLogger().info("Hooking into CoreProtect...");
			new CoreProtectHook(this);
			if(CoreProtectHook.coreProtect.isEnabled()) {
				CoreProtectHook.coreProtect.testAPI();
			}
			else {
				getLogger().severe("Could not load CoreProtect!");
				this.getPluginLoader().disablePlugin(this);
			}
			//------||------
			
			//Start main task
			getLogger().info("Successfully enabled.");
			new RegenTask(this).runTaskTimer(this, waitBeforeStart, regenDelay);
		}
		catch(RuntimeException e) {
			getLogger().severe("Plugin encountered a problem and will shut down! See error log for more information\n" + "Error: " + e);
			getPluginLoader().disablePlugin(this);
		}
	}
	@Override
	public void onDisable() {
		Bukkit.getScheduler().cancelTasks(this);
		getLogger().info("Plugin disabled.");
	}
}


