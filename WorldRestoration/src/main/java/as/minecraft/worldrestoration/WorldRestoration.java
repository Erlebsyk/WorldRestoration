package as.minecraft.worldrestoration;

import org.bukkit.Bukkit;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitTask;

import as.minecraft.worldrestoration.data.DataStore;
import as.minecraft.worldrestoration.tasks.RegenTask;


public class WorldRestoration extends JavaPlugin{
	
	@Override
	public void onEnable() {
		saveDefaultConfig();
		DataStore dataStore = new DataStore(this);
		BukkitTask regenTask = new RegenTask(this).runTaskTimer(this, 100L, this.getConfig().getInt("regen-delay")*60*20L);
	}

}


