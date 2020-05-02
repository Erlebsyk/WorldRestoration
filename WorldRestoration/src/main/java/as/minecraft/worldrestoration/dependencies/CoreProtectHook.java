package as.minecraft.worldrestoration.dependencies;

import org.bukkit.Bukkit;
import org.bukkit.plugin.Plugin;

import net.coreprotect.CoreProtect;
import net.coreprotect.CoreProtectAPI;

public class CoreProtectHook {
	public static CoreProtectAPI coreProtect;
	
	public CoreProtectHook(){
		CoreProtectHook.coreProtect = this.fetchCoreProtect();
	}
	
	public CoreProtectAPI getCoreProtect() {
		return CoreProtectHook.coreProtect;
	}
	
	private CoreProtectAPI fetchCoreProtect() {
	    Plugin plugin = Bukkit.getServer().getPluginManager().getPlugin("CoreProtect");
	 
	    // Check that CoreProtect is loaded
	    if (plugin == null || !(plugin instanceof CoreProtect)) {
	    	//Bukkit.broadcastMessage("CoreProtect not loaded!");
	        return null;
	    }

	    // Check that the API is enabled
	    CoreProtectAPI CoreProtect = ((CoreProtect) plugin).getAPI();
	    if (CoreProtect.isEnabled() == false) {
	    	//Bukkit.broadcastMessage("API not enabled");
	        return null;
	    }

	    // Check that a compatible version of the API is loaded
	    if (CoreProtect.APIVersion() < 6) {
	    	//Bukkit.broadcastMessage("WrongAPI");
	        return null;
	    }
	    //Bukkit.broadcastMessage("Returning CoreProtect...");
	    return CoreProtect;
	}
}
