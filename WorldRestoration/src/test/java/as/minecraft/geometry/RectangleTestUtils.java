package as.minecraft.geometry;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.util.List;
import java.util.Optional;

public class RectangleTestUtils {
	public static void assertInside(Iterable<Rectangle> rectangles, Rectangle container) {
		for(Rectangle rect : rectangles) {
			assertTrue(rect.minCoordinate.x >= container.minCoordinate.x);
			assertTrue(rect.minCoordinate.y >= container.minCoordinate.y);
			assertTrue(rect.maxCoordinate.x <= container.maxCoordinate.x);
			assertTrue(rect.maxCoordinate.y <= container.maxCoordinate.y);
		}
	}
	
	public static void assertInsideSquares(Iterable<Square> squares, Rectangle container) {
		for(Square square : squares) {
			Rectangle rect = square.toRectangle();
			assertTrue(rect.minCoordinate.x >= container.minCoordinate.x);
			assertTrue(rect.minCoordinate.y >= container.minCoordinate.y);
			assertTrue(rect.maxCoordinate.x <= container.maxCoordinate.x);
			assertTrue(rect.maxCoordinate.y <= container.maxCoordinate.y);
		}
	}
	
	public static void assertNoOverlap(Iterable<Rectangle> rectangles) {
		for(Rectangle firstRect : rectangles) {
			for (Rectangle secondRect : rectangles) {
				if(firstRect != secondRect) {
					assertNoOverlap(firstRect, secondRect);
				}
			}
		}
	}
	
	public static void assertNoOverlapSquaresList(List<Square> squares) {
		for(Square firstSquare : squares) {
			for (Square secondSquare : squares) {
				if(firstSquare != secondSquare) {
					assertNoOverlap(firstSquare.toRectangle(), secondSquare.toRectangle());
				}
			}
		}
	}
	
	public static void assertNoOverlapSquaresIterable(Iterable<Square> squares) {
		for(Square firstSquare : squares) {
			for (Square secondSquare : squares) {
				if(!firstSquare.equals(secondSquare)) {
					assertNoOverlap(firstSquare.toRectangle(), secondSquare.toRectangle());
				}
			}
		}
	}
	
	public static void assertNoOverlap(Rectangle first, Rectangle second) {
		assertFalse(overlaps(first, second));
	}
	
	private static boolean overlaps(Rectangle first, Rectangle second) {
		// If one rectangle is on left side of other 
		if(first.minCoordinate.x >= second.maxCoordinate.x) {
			return false;
		}
		if(second.minCoordinate.x >= first.maxCoordinate.x) {
			return false;
		}
		// If one rectangle is above other 
		if(first.minCoordinate.y >= second.maxCoordinate.y) {
			return false;
		}
		if(second.minCoordinate.y >= first.maxCoordinate.y) {
			return false;
		}
		
		// Else, there must be an overlap
		return true;
	}
	
	public static void assertAreaEquals(
			Rectangle baseRect,
			Iterable<Rectangle> subRectangles,
			Optional<Rectangle> overlapRectangle) {
		int areaSum = 0;
		for(Rectangle subRectangle : subRectangles) {
			areaSum += subRectangle.area();
		}
		
		int subtractionArea = overlapRectangle.isPresent() ? overlapRectangle.get().area() : 0;
		assertEquals(areaSum, baseRect.area() - subtractionArea);
	}
	
	public static void assertAreaEquals(
			Rectangle baseRect,
			Iterable<Square> squares) {
		int areaSum = 0;
		for(Square subRectangle : squares) {
			areaSum += subRectangle.area();
		}
		
		assertEquals(areaSum, baseRect.area());
	}
	

	public static void assertAreaEquals(
			Rectangle baseRectangle,
			Iterable<Rectangle> newRects,
			Iterable<Rectangle> overlapRegions) {
		int areaSum = 0;
		for (Rectangle rectangle : newRects) {
			areaSum += rectangle.area();
		}
		
		int subtractionArea = 0;
		for(Rectangle rectangle : overlapRegions) {
			subtractionArea += rectangle.area();
		}
		
		assertEquals(areaSum, baseRectangle.area() - subtractionArea);
	}
}
