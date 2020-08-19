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
		
		Set<String> worldNames = DataStore.getWorldNames();
		for(String worldName: worldNames) {
			String worldRegenEnabledProperty = DataStore.getString("world-settings." + worldName + ".regen-enabled").trim().toLowerCase();
			boolean regenEnabledForWorld = worldRegenEnabledProperty.equals("true");
			if(regenEnabledForWorld) {
				World world = Bukkit.getWorld(worldName);
				int oldTime = Utils.getSecondsFromTimeUnit(DataStore.getString("world-settings." + worldName + ".considered-old-after").trim());
				int restoreRadius = DataStore.getInt("world-settings." + worldName + ".radius");
				ClaimHandler claimHandler = new ClaimHandler(world);
				List<Rectangle> claimRectangles = claimHandler.getWorldClaimRectangles();
				
				Rectangle worldRectangle = new Rectangle(new Point(-restoreRadius, -restoreRadius), new Point(restoreRadius, restoreRadius));
				List<Rectangle> rollbackRectangles = worldRectangle.subtractRectangles(claimRectangles);
				
				for(Rectangle rectangle: rollbackRectangles) {
					for(Square square: rectangle.getSquaresIterable()) {
						
						Location squareCentre = new Location(world, (double) (square.minPoint.x) + (double)(square.size)/2.0, 62.0, (double) (square.minPoint.y) + (double)(square.size)/2.0);
						int squareRadius = (int) (Math.ceil((double)(square.size) / 2.0));
						if(square.size % 2 == 0) squareRadius += 1;
						
						int lookupTime = Integer.MAX_VALUE;
						List<String[]> lookup = CoreProtectHook.coreProtect.performLookup(lookupTime, null /*restrict_users*/, null /*exclude_users*/, null /*restrict_blocks*/, null /*exclude-blocks*/, null /*actions*/, squareRadius, squareCentre);
						if(checkListForOldEntry(oldTime, lookup)) {
							performRestoration(oldTime, squareRadius, squareCentre);
						}
					}
				}
			}
		}
	}
	
	private boolean checkListForOldEntry(int oldTime, List<String[]> lookup) {
		if (lookup!=null && !lookup.isEmpty()) {
			int currentTimeSeconds = (int)(System.currentTimeMillis() / 1000L);
			for(String[] entry: lookup) {
				ParseResult result = CoreProtectHook.coreProtect.parseResult(entry);
				if(currentTimeSeconds - result.getTime() > oldTime && !result.isRolledBack()) {
					return true;
				}
			}
		}
		return false;
	}
	
	private void performRestoration(int oldTime, int radius, Location centre) {
		int rollbackTime = Integer.MAX_VALUE;
		CoreProtectHook.coreProtect.performRollback(rollbackTime, null /*restrict_users*/, null /*exclude_users*/, null /*restrict_blocks*/, null /*exclude-blocks*/, null /*actions*/, radius, centre);
		CoreProtectHook.coreProtect.performRestore(oldTime, null /*restrict_users*/, null /*exclude_users*/, null /*restrict_blocks*/, null /*exclude-blocks*/, null /*actions*/, radius, centre);
		
		//Re-update database as a temporary fix for blocks not getting their "rollbacked"-state updated after first restoration
		CoreProtectHook.coreProtect.performRestore(oldTime, null /*restrict_users*/, null /*exclude_users*/, null /*restrict_blocks*/, null /*exclude-blocks*/, null /*actions*/, radius, centre);
	}
	
}

