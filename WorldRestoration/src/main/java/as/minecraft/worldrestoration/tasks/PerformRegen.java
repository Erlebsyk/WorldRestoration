package as.minecraft.worldrestoration.tasks;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;

import as.minecraft.geometry.Point;
import as.minecraft.geometry.Rectangle;
import as.minecraft.geometry.Square;
import as.minecraft.worldrestoration.WorldRestoration;
import as.minecraft.worldrestoration.data.DataStore;
import as.minecraft.worldrestoration.dependencies.ClaimHandler;
import as.minecraft.worldrestoration.dependencies.CoreProtectHook;
import net.coreprotect.CoreProtectAPI.ParseResult;

public class PerformRegen implements Runnable{
	WorldRestoration plugin;
	
	@Override
	public void run() {
		
		//Test code to get and display coords of claim list
		Set<String> worldNames = DataStore.getWorldNames();
		for(String worldName: worldNames) {
			if(DataStore.getString("world-settings." + worldName + ".regen-enabled").trim().equals("true")) {
				World world = Bukkit.getWorld(worldName);
				ClaimHandler claimHandler = new ClaimHandler(world);
				List<Rectangle> claimRectangles = claimHandler.getWorldClaimRectangles();
				System.out.println("_____________Claims:________________");
				for(Rectangle claimRect: claimRectangles) {
					System.out.println("min: " + claimRect.minCoordinate.x + ", " + claimRect.minCoordinate.y + ", max: " + claimRect.maxCoordinate.x + ", " + claimRect.maxCoordinate.y);
				}
				
				//Single lookup in the whole area (800, 800 to 1200, 1200)
				List<String> named = new ArrayList<String>();
				named.add("Linoko");
				List<String[]> looked = CoreProtectHook.coreProtect.performLookup(60*3/*2147483647*/, named, null, null, null /*exclude-blocks*/, null /*actions*/, 200, new Location(world, 1000, 62, 1000));
				System.out.println(looked.toString());
				System.out.println("_____________________________");
				//---------------------------------
				
				//Lookup across all rectangles within WorldRectangle, after subtracting claimRectangles
				Rectangle worldRectangle = new Rectangle(new Point(800, 800), new Point(1200, 1200));
				List<Rectangle> rollbackRectangles = worldRectangle.subtractRectangles(claimRectangles);
				
				for(Rectangle rectangle: rollbackRectangles) {
					List<Square> rollbackSquares = rectangle.convertToSquares();
					for(Square square: rollbackSquares) {
						Location centre = new Location(world, square.centre().x, 62, square.centre().y);
						List<String> names = new ArrayList<String>();
						names.add("Linoko");
						List<String[]> lookup = CoreProtectHook.coreProtect.performLookup(60*3/*2147483647*/, names, null, null, null /*exclude-blocks*/, null /*actions*/, square.size/2, centre);
						int counter = 0;
						if (lookup!=null && !lookup.isEmpty()){
							System.out.println("Checking square: (" + square.minPoint.x + ", " + square.minPoint.y + ") - (" + square.minPoint.x + square.size + ", " + square.minPoint.y + square.size + ")");
							for(String[] entry: lookup) {
								ParseResult result = CoreProtectHook.coreProtect.parseResult(entry);
								if(counter < 10) {
									System.out.println("x: "+ result.getX() + ", y: " +  result.getY() + ", z: " + result.getZ() + ". Time: " + result.getTime() + " | Data: [" + result.getPlayer() +", " + result.getBlockData() + ", " + result.getActionString() + ", " + world.getName() + "]");
									counter++;
								}
							}
							System.out.println("_______________________________________________________");
						}
					}
				}
			}
		}
		//TESTS!:
		/*
		for(Vector3i[] vec: claimBounds) {
			Bukkit.broadcastMessage("Lesser Bound - X: " + Integer.toString(vec[0].getX()) + ", Z: " + Integer.toString(vec[0].getZ()));
			Bukkit.broadcastMessage("Greater Bound - X: " + Integer.toString(vec[1].getX()) + ", Z: " + Integer.toString(vec[1].getZ()));
			Bukkit.broadcastMessage("____________________________________");
		}
		//
		
		
		//Regen test task at block 1000 1000
		CoreProtectRollback runnable = new CoreProtectRollback();
		runnable.run(new double[] {1000, 72, 1000}, 20);
		//
		*/
	}
	
}

