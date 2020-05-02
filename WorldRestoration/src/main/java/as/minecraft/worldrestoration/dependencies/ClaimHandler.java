package as.minecraft.worldrestoration.dependencies;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import org.bukkit.World;

import com.flowpowered.math.vector.Vector3i;
import com.griefdefender.api.GriefDefender;
import com.griefdefender.api.claim.Claim;
import com.griefdefender.api.claim.ClaimManager;

import as.minecraft.geometry.Point;
import as.minecraft.geometry.Rectangle;
import as.minecraft.worldrestoration.data.DataStore;

public class ClaimHandler{
	
	private World world;
	private int padding;
	
	public ClaimHandler(World world) {
		this.world = world;
		this.padding = DataStore.getInt("claim-settings.claim-padding");
	}
	
	public Set<Claim> getWorldClaims(){
		ClaimManager claimManager = GriefDefender.getCore().getClaimManager(world.getUID());
		return claimManager.getWorldClaims();
		
	}
	
	public ArrayList<Vector3i[]> getWorldClaimBounds(){
		Set<Claim> allClaims = this.getWorldClaims();
		
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
	
	public List<Rectangle> getWorldClaimRectangles(){
		ArrayList<Rectangle> worldRectangles = new ArrayList<Rectangle>();
		ArrayList<Vector3i[]> claimBounds = this.getWorldClaimBounds();
		for(Vector3i[] vec: claimBounds) {
			
			worldRectangles.add(this.vectorToRectangle(vec));
		}
		return worldRectangles;
	}
	
	public Rectangle vectorToRectangle(Vector3i[] vec) {
		Point lesserBound = new Point(vec[0].getX(), vec[0].getZ());
		Point greaterBound = new Point(vec[1].getX(), vec[1].getZ());
		return new Rectangle(lesserBound, greaterBound);
	}
}
