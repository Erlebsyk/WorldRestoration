package as.minecraft.geometry;

import java.util.Random;

public class RandomPointGenerator {
	public Random random;
	public RandomPointGenerator(int seed) {
		random = new Random(seed);
	}
	
	public Point randomPoint(Point minPoint, Point maxPoint) {
		int xRange = maxPoint.x - minPoint.x;
		int yRange = maxPoint.y - minPoint.y;
		int xStart = minPoint.x;
		int yStart = minPoint.y;
		
		int x = xStart + random.nextInt(xRange);
		int y = yStart + random.nextInt(yRange);
		
		return new Point(x, y);
	}
}