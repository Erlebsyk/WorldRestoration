//Regeneration task called regularly with the interval defined under regen-delay in config.yml
package as.minecraft.worldrestoration.tasks;

import org.bukkit.Bukkit;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitScheduler;

import as.minecraft.worldrestoration.WorldRestoration;
import as.minecraft.worldrestoration.utils.Utils;

public class RegenTask extends BukkitRunnable{
	
	WorldRestoration plugin;
	
	public RegenTask(WorldRestoration plugin) {
		this.plugin = plugin;
	}
	
	@Override
	public void run() {
		Bukkit.broadcastMessage(Utils.chat(plugin.getConfig().getString("regen-message").replace("<delay>", Integer.toString(plugin.getConfig().getInt("warning-delay")))));
		//BukkitTask performRegen = new PerformRegen(plugin).runTaskLater(plugin, plugin.getConfig().getInt("warning-delay")*20L);
		
		//Run regeneration in new thread for better performance
		Runnable performRegen = new PerformRegen();
		Thread performRegenThread = new Thread(performRegen);
		BukkitScheduler regenScheduler = Bukkit.getServer().getScheduler();
		regenScheduler.runTaskLater(plugin, new Runnable(){ //Wait <delay>@config.yml seconds before regeneration starts
			@Override
			public void run() {
				//Run regeneration task in separate thread
				performRegenThread.start();
				
				//Check every 5 second (20L*5) whether or not the task is done.
				final BukkitRunnable regenChecker = new BukkitRunnable() {
					@Override
					public void run() {
						if(!performRegenThread.isAlive()) { //Close task if finished
							Bukkit.broadcastMessage(Utils.chat(plugin.getConfig().getString("regen-done-message")));
							this.cancel();
						}
						
					}
				};
				regenChecker.runTaskTimerAsynchronously(plugin, 20L*5, 20L*5);
			}
			
		}, plugin.getConfig().getInt("warning-delay")*20L);
	}
}

