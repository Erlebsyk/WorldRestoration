package as.minecraft.geometry;

public class Square {
	public Point minPoint;
	public int size;
	public Square(Point minPoint, int size) {
		this.minPoint = minPoint;
		this.size = size;
	}
	
	public int area() {
		return size * size;
	}
	
	Rectangle toRectangle() {
		return new Rectangle(
				new Point(this.minPoint.x, this.minPoint.y),
				new Point(this.minPoint.x + size, this.minPoint.y + size));
	}
	
	boolean equals(Square other) {
		return minPoint.equals(other.minPoint) && size == other.size;
	}
}
