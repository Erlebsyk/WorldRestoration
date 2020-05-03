package as.minecraft.worldrestoration.tasks;

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
import as.minecraft.worldrestoration.utils.Utils;
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
				int oldTime = Utils.getSecondsFromTimeUnit(DataStore.getString("world-settings." + worldName + ".considered-old-after").trim());
				ClaimHandler claimHandler = new ClaimHandler(world);
				List<Rectangle> claimRectangles = claimHandler.getWorldClaimRectangles();
				
				Rectangle worldRectangle = new Rectangle(new Point(800, 800), new Point(1200, 1200));
				List<Rectangle> rollbackRectangles = worldRectangle.subtractRectangles(claimRectangles);
				
				for(Rectangle rectangle: rollbackRectangles) {
					List<Square> rollbackSquares = rectangle.convertToSquares();
					for(Square square: rollbackSquares) {
						
						Location squareCentre = new Location(world, (double) (square.minPoint.x) + (double)(square.size)/2.0, 62.0, (double) (square.minPoint.y) + (double)(square.size)/2.0);
						int squareRadius = (int) (Math.ceil((double)(square.size) / 2.0));
						if(square.size % 2 == 0) squareRadius += 1;
						
						List<String[]> lookup = CoreProtectHook.coreProtect.performLookup(2147483600, null, null, null, null /*exclude-blocks*/, null /*actions*/, squareRadius, squareCentre);
						if(checkListForOldEntry(oldTime, lookup)) {
							performRestoration(oldTime, square, squareRadius, squareCentre);
						}
					}
				}
			}
		}
		
		//TESTS!:
		
		//System.out.println("_____________Claims:________________");
		//for(Rectangle claimRect: claimRectangles) {
		//	System.out.println("min: " + claimRect.minCoordinate.x + ", " + claimRect.minCoordinate.y + ", max: " + claimRect.maxCoordinate.x + ", " + claimRect.maxCoordinate.y);
		//}
		
		//Single lookup in the whole area (800, 800 to 1200, 1200)
		//
		//List<String[]> looked = CoreProtectHook.coreProtect.performLookup(60*3/*2147483647*/, named, null, null, null /*exclude-blocks*/, null /*actions*/, 200, new Location(world, 1000, 62, 1000));
		//System.out.println("All results: " + looked.toString());
		//System.out.println("_____________________________");
		
		//System.out.println("In sqare: (" + square.minPoint.x + ", " + square.minPoint.y + ") - (" + (square.minPoint.x + square.size) + ", " + (square.minPoint.y + square.size) + ")");
		
		//System.out.println("Block: " + result.getType().toString() + " changed " + (result.getTime() - (int) (System.currentTimeMillis() / 1000L)) + " seconds ago.");
		
		//System.out.println("x: "+ result.getX() + ", y: " +  result.getY() + ", z: " + result.getZ() + ". Time: " + result.getTime() + " | Data: [" + result.getPlayer() +", r:" + lookupRadius + ", sqSize:" + square.size + ", " + result.getActionString() + ", " + world.getName() + "]");
		//System.out.println("_______________________________________________________");
		//---------------------------------
		
		
				
		//Lookup across all rectangles within WorldRectangle, after subtracting claimRectangles
		/*
		//Regen test task at block 1000 1000
		CoreProtectRollback runnable = new CoreProtectRollback();
		runnable.run(new double[] {1000, 72, 1000}, 20);
		//
		*/
	}
	
	private boolean checkListForOldEntry(int oldTime, List<String[]> lookup) {
		if (lookup!=null && !lookup.isEmpty()) {
			int currentTime = (int)(System.currentTimeMillis() / 1000L);
			for(String[] entry: lookup) {
				ParseResult result = CoreProtectHook.coreProtect.parseResult(entry);
				//System.out.println("Time ago: " + (currentTime - result.getTime()) + ", rolled back: " + result.isRolledBack());
				if(currentTime - result.getTime() > oldTime && !result.isRolledBack()) {
					return true;
				}
			}
		}
		return false;
	}
	
	private void performRestoration(int oldTime, Square square, int radius, Location centre) {
		CoreProtectHook.coreProtect.performRollback(2147483600, null, null, null, null /*exclude-blocks*/, null /*actions*/, radius, centre);
		CoreProtectHook.coreProtect.performRestore(oldTime, null, null, null, null /*exclude-blocks*/, null /*actions*/, radius, centre);
	}
	
}

