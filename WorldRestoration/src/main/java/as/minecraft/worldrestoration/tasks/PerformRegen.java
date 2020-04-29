package as.minecraft.worldrestoration.tasks;

import java.util.ArrayList;

import org.bukkit.Bukkit;
import org.bukkit.World;

import com.flowpowered.math.vector.Vector3i;

import as.minecraft.worldrestoration.WorldRestoration;
import as.minecraft.worldrestoration.dependencies.ClaimHandler;
import as.minecraft.worldrestoration.dependencies.CoreProtectRollback;

public class PerformRegen implements Runnable{
	WorldRestoration plugin;
	
	@Override
	public void run() {
		
		//Test code to get and display coords of claim list
		World world = Bukkit.getWorld("world");
		ClaimHandler claimHandler = new ClaimHandler(world);
		ArrayList<Vector3i[]> claimBounds = claimHandler.getClaimBounds();
		
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
	}
	
}

