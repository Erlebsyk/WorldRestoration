package as.minecraft.worldrestoration.dependencies;

import java.util.ArrayList;
import java.util.Set;

import org.bukkit.World;

import com.flowpowered.math.vector.Vector3i;
import com.griefdefender.api.GriefDefender;
import com.griefdefender.api.claim.Claim;
import com.griefdefender.api.claim.ClaimManager;

import as.minecraft.worldrestoration.data.DataStore;

public class ClaimHandler{
	
	private World world;
	private int padding;
	
	public ClaimHandler(World world) {
		this.world = world;
		this.padding = DataStore.getInt("claim-settings.claim-padding");
	}
	public ArrayList<Vector3i[]> getClaimBounds(){
		ClaimManager claimManager = GriefDefender.getCore().getClaimManager(world.getUID());
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
