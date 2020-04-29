package as.minecraft.worldrestoration.dependencies;

import java.util.ArrayList;
import java.util.Set;

import org.bukkit.World;

import com.flowpowered.math.vector.Vector3i;
import com.griefdefender.GriefDefenderPlugin;
import com.griefdefender.api.claim.Claim;
import com.griefdefender.claim.GDClaimManager;

import as.minecraft.worldrestoration.WorldRestoration;

public class ClaimHandler{
	
	WorldRestoration plugin;
	private World world;
	private int padding;
	
	public ClaimHandler(WorldRestoration plugin, World world) {
		this.plugin = plugin;
		this.world = world;
		//this.padding = plugin.getConfig().getInt("claim-padding");
		//Find some way to do this ^ within the thread, perhaps store that variable
		this.padding = 5; //Set the padding manually for now
	}
	public ArrayList<Vector3i[]> getClaimBounds(){
		GDClaimManager claimManager = GriefDefenderPlugin.getInstance().dataStore.getClaimWorldManager(world.getUID());
		Set<Claim> allClaims = claimManager.getWorldClaims();
		
		ArrayList<Vector3i[]> result = new ArrayList<Vector3i[]>();
		
		for(Claim claim: allClaims) {
			if(padding == 0)
				result.add(new Vector3i[]{claim.getLesserBoundaryCorner(), claim.getGreaterBoundaryCorner()});
			else {
				Vector3i lesserBound = claim.getLesserBoundaryCorner(),
						greaterBound = claim.getGreaterBoundaryCorner(),
						paddedLesserBound = new Vector3i(lesserBound.getX() - padding, lesserBound.getY(), lesserBound.getZ() - padding),
						paddedGreaterBound = new Vector3i(greaterBound.getX() + padding, greaterBound.getY(), greaterBound.getZ() + padding);
				result.add(new Vector3i[]{paddedLesserBound, paddedGreaterBound});
			}
		}
		return result;
	}
}
