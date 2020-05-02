package as.minecraft.worldrestoration.dependencies;

/* For additional lookup after rollback:
import java.util.List;
import java.util.Arrays;
import net.coreprotect.CoreProtectAPI.ParseResult;
*/

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;

public class CoreProtectRollback{
	//Method to fetch CoreProtect
	
	public void run(double[] pos, int radius) {
		try {
			World world = Bukkit.getWorld("world"); //Replace to get right world!
			Location rollbackLocation = new Location(world, pos[0], pos[1], pos[2]);
			
			if (CoreProtectHook.coreProtect != null){ //Ensure access to the API
				CoreProtectHook.coreProtect.performRollback(60000, null, null, null, null, null, radius, rollbackLocation);
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
