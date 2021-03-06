package as.minecraft.worldrestoration.dependencies;

import org.bukkit.Bukkit;
import org.bukkit.plugin.Plugin;

import as.minecraft.worldrestoration.WorldRestoration;
import net.coreprotect.CoreProtect;
import net.coreprotect.CoreProtectAPI;

public class CoreProtectHook {
	public static CoreProtectAPI coreProtect;
	private WorldRestoration plugin;
	
	public CoreProtectHook(WorldRestoration plugin){
		CoreProtectHook.coreProtect = this.fetchCoreProtect();
		this.plugin = plugin;
	}
	
	public CoreProtectAPI getCoreProtect() {
		return CoreProtectHook.coreProtect;
	}
	
	private CoreProtectAPI fetchCoreProtect() {
	    Plugin coreProtectPlugin = Bukkit.getServer().getPluginManager().getPlugin("CoreProtect");
	    // Check that CoreProtect is loaded
	    if (coreProtectPlugin == null || !(coreProtectPlugin instanceof CoreProtect)) {
	    	plugin.getLogger().severe("Could not load CoreProtect, make sure that you have the latest version of CoreProtect installed!");
	    	throw new RuntimeException();
	    }

    	// Check that the API is enabled
	    CoreProtectAPI CoreProtect = ((CoreProtect) coreProtectPlugin).getAPI();
	    if (CoreProtect.isEnabled() == false) {
	    	System.err.println("[WorldRestoration] CoreProtect api not enabled!");
	    	throw new RuntimeException();
	    }

    	// Check that a compatible version of the API is loaded
    	if (CoreProtect.APIVersion() < 6) {
    		System.err.println("[WorldRestoration] Outdated CoreProtect API!");
    		throw new IllegalStateException();
    	}
    	return CoreProtect;
	}
}
