//Regeneration task called regularly with the interval defined under regen-delay in config.yml
package as.minecraft.worldrestoration.tasks;

import org.bukkit.Bukkit;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitScheduler;

import as.minecraft.worldrestoration.WorldRestoration;
import as.minecraft.worldrestoration.data.DataStore;
import as.minecraft.worldrestoration.utils.Utils;

public class RegenTask extends BukkitRunnable{
	
	WorldRestoration plugin;
	
	public RegenTask(WorldRestoration plugin) {
		this.plugin = plugin;
	}
	
	@Override
	public void run() {
		
		if(DataStore.isRunning()) {
			Bukkit.getLogger().warning("A restoration task was cancelled as there was already one running, consider increasing the regen-delay in config-yml.");
			return;
		}
		
		String regenWarnMessage = plugin.getConfig().getString("regen-warn-message");
		regenWarnMessage = regenWarnMessage.replace("<delay>", plugin.getConfig().getString("warning-delay").replaceAll("(\\d+).+", "$1"));
		Bukkit.broadcastMessage(Utils.chat(regenWarnMessage));
		
		//Run regeneration in new thread for better performance
		Runnable performRegen = new PerformRegen();
		Thread performRegenThread = new Thread(performRegen);
		BukkitScheduler regenScheduler = plugin.getServer().getScheduler();
		regenScheduler.runTaskLater(plugin, new Runnable(){ //Wait <delay>@config.yml seconds before regeneration starts
			@Override
			public void run() {
				//Run regeneration task in separate thread
				performRegenThread.start();
				DataStore.startRunning();
				
				//Check every 5 second (20L*5) whether or not the task is done.
				final BukkitRunnable regenChecker = new BukkitRunnable() {
					@Override
					public void run() {
						if(!performRegenThread.isAlive()) { //Close task if finished
							Bukkit.broadcastMessage(Utils.chat(plugin.getConfig().getString("regen-done-message")));
							DataStore.stopRunning();
							this.cancel();
						}
						
					}
				};
				//Check if the task is done every 5 second
				regenChecker.runTaskTimerAsynchronously(plugin, 20L*5, 20L*5);
			}
			
		}, Utils.getTicksFromTimeUnit(plugin.getConfig().getString("warning-delay")));
	}
}

