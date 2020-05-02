package as.minecraft.worldrestoration;

import org.bukkit.plugin.java.JavaPlugin;
//import org.bukkit.scheduler.BukkitTask;

import as.minecraft.worldrestoration.data.DataStore;
import as.minecraft.worldrestoration.dependencies.CoreProtectHook;
import as.minecraft.worldrestoration.tasks.RegenTask;
import as.minecraft.worldrestoration.utils.Utils;


public class WorldRestoration extends JavaPlugin{
	
	@Override
	public void onEnable() {
		saveDefaultConfig();
		new DataStore(this);
		new CoreProtectHook();
		if(CoreProtectHook.coreProtect == null) {
			System.out.println("[WorldRestoration] CoreProtect found but not recognized. Please make sure you use the latest version of CoreProtect!");
		}
		else {
			new RegenTask(this).runTaskTimer(this, 100L, Utils.getTicksFromTimeUnit(this.getConfig().getString("regen-delay")));
			//BukkitTask regenTask = new RegenTask(this).runTaskTimer(this, 100L, this.getConfig().getInt("regen-delay")*60*20L);
		}
	}
}


