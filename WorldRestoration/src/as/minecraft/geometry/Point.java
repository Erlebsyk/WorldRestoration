package as.minecraft.geometry;

public class Point {
	public int x;
	public int y;
	public Point(int x, int y){
		this.x = x;
		this.y = y;
	}
	
	public Point clone() {
		return new Point(x, y);
	}
}
