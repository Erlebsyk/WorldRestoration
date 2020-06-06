package as.minecraft.geometry;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;

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

	public List<Square> convertToSquares() {
		if(width() == 0 || height() == 0) {
			throw new IllegalStateException("Cannot convert rectangle with width or height equal to zero");
		}
		
		ArrayList<Square> returnList = new ArrayList<Square>();
		for(Square square : getSquaresIterable()) {
			returnList.add(square);
		}
		
		return returnList;
	}
	
	public RectangleToSquareConversionIterable getSquaresIterable() {
		return new RectangleToSquareConversionIterable(this);
	}
	
	public List<Rectangle> subtractRectangles(List<Rectangle> subtractionRectangles) {
		ArrayList<Rectangle> subtractedRectangles = new ArrayList<>();
		LinkedList<Rectangle> rectanglesToSubtractFrom = new LinkedList<>();
		rectanglesToSubtractFrom.add(this);
		
		while(!rectanglesToSubtractFrom.isEmpty()) {
			Rectangle currentRectangle = rectanglesToSubtractFrom.pop();
			boolean wasSubtracted = false;
			
			for(Rectangle subtractionRectangle : subtractionRectangles) {
				List<Rectangle> newRectangles = currentRectangle.subtractRectangle(subtractionRectangle);
				if(newRectangles.size() == 0 || (newRectangles.size() >= 1 && newRectangles.get(0).area() != currentRectangle.area())) {
					rectanglesToSubtractFrom.addAll(newRectangles);
					wasSubtracted = true;
					break;
				}
			}
			
			if(!wasSubtracted) {
				subtractedRectangles.add(currentRectangle);
			}
		}
		
		return subtractedRectangles;
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
	
	public class RectangleToSquareConversionIterable implements Iterable<Square> {
		private Rectangle rectangle;
		public RectangleToSquareConversionIterable(Rectangle rectangle) {
			this.rectangle = rectangle;
		}
		
		@Override
		public RectangleToSquareConversionIterator iterator() {
			return new RectangleToSquareConversionIterator(rectangle);
		}
		
	}
	
	public class RectangleToSquareConversionIterator implements Iterator<Square> {
		private Optional<Rectangle> currentRectangle;
		public RectangleToSquareConversionIterator(Rectangle rectangle) {
			if(rectangle.width() == 0 || rectangle.height() == 0) {
				currentRectangle = Optional.empty();
			}
			else {
				currentRectangle = Optional.of(new Rectangle(
						rectangle.minCoordinate.clone(), rectangle.maxCoordinate.clone()));
			}
		}
		
		@Override
		public boolean hasNext() {
			return currentRectangle.isPresent();
		}

		@Override
		public Square next() {
			if(!hasNext()) {
				throw new NoSuchElementException();
			}
			
			Rectangle rect = currentRectangle.get();
			if(rect.width() == rect.height()) {
				Square square = new Square(rect.minCoordinate.clone(), rect.width());
				currentRectangle = Optional.empty();
				return square;
			}
			else if(rect.width() < rect.height()) {
				Square square = new Square(rect.minCoordinate.clone(), rect.width());
				rect.minCoordinate.y += rect.width();
				return square;
			}
			else {
				Square square = new Square(rect.minCoordinate.clone(), rect.height());
				rect.minCoordinate.x += rect.height();
				return square;
			}
		}
	}
}
