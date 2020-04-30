package as.minecraft.geometry;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

public class Rectangle {
	public Point minCoordinate;
	public Point maxCoordinate;
	
	public Rectangle(Point minCoordinate, Point maxCoordinate) {
		if(minCoordinate.x > maxCoordinate.x || minCoordinate.y > maxCoordinate.y) {
			throw new IllegalArgumentException("minCoordinate must be larger than maxCoordinate");
		}
		this.minCoordinate = minCoordinate;
		this.maxCoordinate = maxCoordinate;
	}
	
	List<Rectangle> subtractRectangle(Rectangle other) {
		LinkedList<Rectangle> cutCandidates = new LinkedList<>();
		ArrayList<Rectangle> cutRectangles = new ArrayList<>();
		
		cutCandidates.add(this);
		
		while(!cutCandidates.isEmpty()) {
			Rectangle currentRectangle = cutCandidates.pop();
			
			List<Rectangle> minVerticalCut = currentRectangle.cutVertical(other.minCoordinate.x);
			List<Rectangle> maxVerticalCut = currentRectangle.cutVertical(other.maxCoordinate.x);
			List<Rectangle> minHorizontalCut = currentRectangle.cutHorizontal(other.minCoordinate.y);
			List<Rectangle> maxHorizontalCut = currentRectangle.cutHorizontal(other.maxCoordinate.y);
			
			if(other.encloses(currentRectangle)) {
				
			}
			else if(minVerticalCut.size() >= 2) {
				cutCandidates.addAll(minVerticalCut);
			}
			else if(maxVerticalCut.size() >= 2) {
				cutCandidates.addAll(maxVerticalCut);
			}
			else if(minHorizontalCut.size() >= 2) {
				cutCandidates.addAll(minHorizontalCut);
			}
			else if(maxHorizontalCut.size() >= 2) {
				cutCandidates.addAll(maxHorizontalCut);
			}
			else {
				cutRectangles.add(currentRectangle);
			}
		}
		
		return mergeOnEqualSides(cutRectangles);
	}

	private boolean encloses(Rectangle other) {
		if(minCoordinate.x <= other.minCoordinate.x &&
				minCoordinate.y <= other.minCoordinate.y &&
				maxCoordinate.x >= other.maxCoordinate.x &&
				maxCoordinate.y >= other.maxCoordinate.y) {
			return true;
		}
		return false;
	}

	private List<Rectangle> cutVertical(int x) {
		ArrayList<Rectangle> cutRectangles = new ArrayList<Rectangle>();
		
		if(minCoordinate.x < x && maxCoordinate.x > x) {
			Rectangle firstCut = new Rectangle(minCoordinate.clone(), new Point(x, maxCoordinate.y));
			Rectangle secondCut = new Rectangle(new Point(x, minCoordinate.y), maxCoordinate.clone());
			cutRectangles.add(firstCut);
			cutRectangles.add(secondCut);
		}
		
		return cutRectangles;
	}
	
	private List<Rectangle> cutHorizontal(int y) {
		ArrayList<Rectangle> cutRectangles = new ArrayList<Rectangle>();
		
		if(minCoordinate.y < y && maxCoordinate.y > y) {
			Rectangle firstCut = new Rectangle(minCoordinate.clone(), new Point(maxCoordinate.x, y));
			Rectangle secondCut = new Rectangle(new Point(minCoordinate.x, y), maxCoordinate.clone());
			cutRectangles.add(firstCut);
			cutRectangles.add(secondCut);
		}
		
		return cutRectangles;
	}
	
	private List<Rectangle> mergeOnEqualSides(List<Rectangle> rectangles) {
		LinkedList<Rectangle> rectanglesToMerge = new LinkedList<>(rectangles);
		
		boolean isMerging = true;
		while(isMerging) {
			isMerging = false;
			
			for(int i=0; i<rectanglesToMerge.size(); i++) {
				Rectangle rectangleToMerge = rectanglesToMerge.pop();
				for(int j=0; j<rectanglesToMerge.size(); j++) {
					Rectangle other = rectanglesToMerge.pop();
					
					if(rectangleToMerge.sharesSide(other)) {
						Rectangle newRectangle = rectangleToMerge.merge(other);
						rectanglesToMerge.add(newRectangle);
						
						isMerging = true;
						break;
					}
					else {
						rectanglesToMerge.add(other);
					}
				}
				if(isMerging) {
					break;
				}
				else {
					rectanglesToMerge.add(rectangleToMerge);
				}
			}
		}
		
		return rectanglesToMerge;
	}
	
	private Rectangle merge(Rectangle other) {
		Rectangle mergedRectangle = new Rectangle(
				new Point(Math.min(minCoordinate.x, other.minCoordinate.x),
						Math.min(minCoordinate.y, other.minCoordinate.y)),
				new Point(Math.max(maxCoordinate.x, other.maxCoordinate.x),
						Math.max(maxCoordinate.y, other.maxCoordinate.y)));
		
		return mergedRectangle;
	}

	private boolean sharesSide(Rectangle other) {
		return this.sharesRightSide(other) || this.sharesTopSide(other) || 
				other.sharesRightSide(this) || other.sharesTopSide(this);
	}
	
	private boolean sharesTopSide(Rectangle other) {
		if(minCoordinate.x == other.minCoordinate.x && 
				maxCoordinate.y == other.minCoordinate.y &&
				maxCoordinate.x == other.maxCoordinate.x) {
			return true;
		}
		return false;
	}
	
	private boolean sharesRightSide(Rectangle other) {
		if(minCoordinate.y == other.minCoordinate.y &&
				maxCoordinate.x == other.minCoordinate.x &&
				maxCoordinate.y == other.maxCoordinate.y) {
			return true;
		}
		return false;
	}

	List<Square> convertToSquares() {
		if(width() == 0 || height() == 0) {
			throw new IllegalStateException("Cannot convert rectangle with width or height equal to zero");
		}
		
		ArrayList<Square> returnList = new ArrayList<Square>();
		if(width() == height()) {
			returnList.add(new Square(minCoordinate, width()));
		}
		else if(width() < height()) {
			returnList.add(new Square(minCoordinate, width()));
			Point newMinCoord = new Point(minCoordinate.x, minCoordinate.y + width());
			returnList.addAll(new Rectangle(newMinCoord, maxCoordinate).convertToSquares());
		}
		else {
			returnList.add(new Square(minCoordinate, height()));
			Point newMinCoord = new Point(minCoordinate.x + height(), minCoordinate.y);
			returnList.addAll(new Rectangle(newMinCoord, maxCoordinate).convertToSquares());
		}
		
		return returnList;
	}
	
	public int area() {
		return width() * height();
	}
	
	public int width() {
		return maxCoordinate.x - minCoordinate.x;
	}
	
	public int height() {
		return maxCoordinate.y - minCoordinate.y;
	}

	public String toGeodrafterString() {
		return "rectangle(" + minCoordinate.x + "|" + minCoordinate.y +
				" " + width() + " " + height() + ")";
	}
}
