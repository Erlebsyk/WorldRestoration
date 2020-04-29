package as.minecraft.worldrestoration.dependencies;

/* For additional lookup after rollback:
import java.util.List;
import java.util.Arrays;
import net.coreprotect.CoreProtectAPI.ParseResult;
*/

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.plugin.Plugin;

import net.coreprotect.CoreProtect;
import net.coreprotect.CoreProtectAPI;

public class CoreProtectRollback{
	//Method to fetch CoreProtect
	
	private CoreProtectAPI getCoreProtect() {
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
	
	public void run(double[] pos, int radius) {
		try {
			CoreProtectAPI CoreProtect = getCoreProtect();
			World world = Bukkit.getWorld("world"); //Replace to get right world!
			Location rollbackLocation = new Location(world, pos[0], pos[1], pos[2]);
			
			if (CoreProtect!=null){ //Ensure access to the API
				CoreProtect.performRollback(60000, null, null, null, null, null, radius, rollbackLocation);
				/*//Rollback method with lookup value
				List<String[]> lookup = CoreProtect.performRollback(60000, null, null, null, null, null, 20, rollbackLocation);
				if (lookup!=null){
					for (String[] value : lookup){
						ParseResult result = CoreProtect.parseResult(value);
						int x = result.getX();
						int y = result.getY();
						int z = result.getZ();
	          }
	        }*/
	      }
	    }
	    catch (Exception e){
	    	System.out.println("Error: Could not perform rollback: \n" + e);
	    	e.printStackTrace(); 
	    }
	  }
}
