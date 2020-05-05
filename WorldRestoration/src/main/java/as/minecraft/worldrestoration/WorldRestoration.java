package as.minecraft.worldrestoration;

import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;

import as.minecraft.worldrestoration.data.DataStore;
import as.minecraft.worldrestoration.dependencies.CoreProtectHook;
import as.minecraft.worldrestoration.tasks.RegenTask;
import as.minecraft.worldrestoration.utils.Utils;


public class WorldRestoration extends JavaPlugin{
	
	@Override
	public void onEnable() {
		
		//----Setup-----
		try {
			getLogger().info("Loading configuration...");
			DataStore dataStore = new DataStore(this);
			dataStore.loadConfigFile();
			dataStore.storeDataFromConfig();
			long regenDelay = Utils.getTicksFromTimeUnit(DataStore.getString("regen-delay"));
			long waitBeforeStart = regenDelay;
		
			getLogger().info("Hooking into CoreProtect...");
			new CoreProtectHook(this);
			if(CoreProtectHook.coreProtect.isEnabled()) {
				CoreProtectHook.coreProtect.testAPI();
				getLogger().info("Successfully hooked into CoreProtect.");
			}
			else {
				getLogger().severe("Could not load CoreProtect!");
				this.getPluginLoader().disablePlugin(this);
			}
			//------||------
			
			//Start main task
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


