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
	
	public Point centre() {
		return new Point(minPoint.x + size/2, minPoint.y + size/2);
	}
	
	Rectangle toRectangle() {
		return new Rectangle(
				new Point(this.minPoint.x, this.minPoint.y),
				new Point(this.minPoint.x + size, this.minPoint.y + size));
	}
}
