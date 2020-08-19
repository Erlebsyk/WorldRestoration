package as.minecraft.geometry;

import static as.minecraft.geometry.RectangleTestUtils.assertAreaEquals;
import static as.minecraft.geometry.RectangleTestUtils.assertInside;
import static as.minecraft.geometry.RectangleTestUtils.assertInsideSquares;
import static as.minecraft.geometry.RectangleTestUtils.assertNoOverlap;
import static as.minecraft.geometry.RectangleTestUtils.assertNoOverlapSquaresList;
import static as.minecraft.geometry.RectangleTestUtils.assertNoOverlapSquaresIterable;
import static org.junit.Assert.assertEquals;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TestName;

public class RectangleTest {
	@Rule public TestName name = new TestName();
	
	@Test(expected = IllegalArgumentException.class)
	public void topLeftXLargerThanBottomRightXThrows() {
		new Rectangle(new Point(-2, -3), new Point(-4, 20));
	}
	
	@Test(expected = IllegalArgumentException.class)
	public void topLeftYLargerThanBottomRightYThrows() {
		new Rectangle(new Point(2, -3), new Point(10, -20));
	}
	
	@Test(expected = IllegalArgumentException.class)
	public void topLeftXYLargerThanBottomRightXYThrows() {
		new Rectangle(new Point(2, 2), new Point(1, 1));
	}
	
	@Test
	public void testArea() {
		Rectangle rect = new Rectangle(new Point(4, 5), new Point(6, 8));
		assertEquals(rect.area(), 6);
	}
	
	@Test
	public void testZeroArea() {
		Rectangle rect = new Rectangle(new Point(2, 2), new Point(2, 2));
		assertEquals(rect.area(), 0);
	}

	@Test
	public void subtractionTopRightCornerInside() {
		Rectangle baseRectangle = new Rectangle(new Point(0, 0), new Point(10, 10));
		Rectangle subtractionRectangle = new Rectangle(new Point(-5, -5), new Point(5, 5));
		Rectangle overlapRectangle = new Rectangle(new Point(0, 0), new Point(5, 5));
		
		subtractionTestBase(baseRectangle, subtractionRectangle, overlapRectangle);
	}
	
	@Test
	public void subtractionTopLeftCornerInside() {
		Rectangle baseRectangle = new Rectangle(new Point(-3, -5), new Point(6, 4));
		Rectangle subtractionRectangle = new Rectangle(new Point(2, -7), new Point(8, -1));
		Rectangle overlapRectangle = new Rectangle(new Point(2, -5), new Point(6, -1));
		
		subtractionTestBase(baseRectangle, subtractionRectangle, overlapRectangle);
	}
	
	@Test
	public void subtractionBottomRightCornerInside() {
		Rectangle baseRectangle = new Rectangle(new Point(-7, -9), new Point(-2, 0));
		Rectangle subtractionRectangle = new Rectangle(new Point(-8, -5), new Point(-5, 3));
		Rectangle overlapRectangle = new Rectangle(new Point(-7, -5), new Point(-5, 0));
		
		subtractionTestBase(baseRectangle, subtractionRectangle, overlapRectangle);
	}
	
	@Test
	public void subtractionBottomLeftCornerInside() {
		Rectangle baseRectangle = new Rectangle(new Point(-4, -5), new Point(7, 5));
		Rectangle subtractionRectangle = new Rectangle(new Point(6, 4), new Point(8, 6));
		Rectangle overlapRectangle = new Rectangle(new Point(6, 4), new Point(7, 5));
		
		subtractionTestBase(baseRectangle, subtractionRectangle, overlapRectangle);
	}
	
	@Test
	public void subtractionTopCornersInside() {
		Rectangle baseRectangle = new Rectangle(new Point(1, 2), new Point(7, 5));
		Rectangle subtractionRectangle = new Rectangle(new Point(3, 1), new Point(6, 4));
		Rectangle overlapRectangle = new Rectangle(new Point(3, 2), new Point(6, 4));
		
		subtractionTestBase(baseRectangle, subtractionRectangle, overlapRectangle);
	}
	
	@Test
	public void subtractionLeftCornersInside() {
		Rectangle baseRectangle = new Rectangle(new Point(-1, -1), new Point(1, 8));
		Rectangle subtractionRectangle = new Rectangle(new Point(0, 3), new Point(5, 4));
		Rectangle overlapRectangle = new Rectangle(new Point(0, 3), new Point(1, 4));
		
		subtractionTestBase(baseRectangle, subtractionRectangle, overlapRectangle);
	}
	
	@Test
	public void subtractionBottomCornersInside() {
		Rectangle baseRectangle = new Rectangle(new Point(-5, -8), new Point(-1, -2));
		Rectangle subtractionRectangle = new Rectangle(new Point(-4, -3), new Point(-2, 2));
		Rectangle overlapRectangle = new Rectangle(new Point(-4, -3), new Point(-2, -2));
		
		subtractionTestBase(baseRectangle, subtractionRectangle, overlapRectangle);
	}
	
	@Test
	public void subtractionRightCornersInside() {
		Rectangle baseRectangle = new Rectangle(new Point(-4, -5), new Point(3, -1));
		Rectangle subtractionRectangle = new Rectangle(new Point(-7, -4), new Point(-2, -3));
		Rectangle overlapRectangle = new Rectangle(new Point(-4, -4), new Point(-2, -3));
		
		subtractionTestBase(baseRectangle, subtractionRectangle, overlapRectangle);
	}
	
	@Test
	public void subtractionBaseTopCornersInsideSubtractionRectangle() {
		Rectangle baseRectangle = new Rectangle(new Point(-3, -3), new Point(2, 2));
		Rectangle subtractionRectangle = new Rectangle(new Point(-4, 1), new Point(3, 5));
		Rectangle overlapRectangle = new Rectangle(new Point(-3, 1), new Point(2, 2));
		
		subtractionTestBase(baseRectangle, subtractionRectangle, overlapRectangle);
	}
	
	@Test
	public void subtractionBaseLeftCornersInsideSubtractionRectangle() {
		Rectangle baseRectangle = new Rectangle(new Point(-5, -3), new Point(0, -1));
		Rectangle subtractionRectangle = new Rectangle(new Point(-8, -4), new Point(-2, 0));
		Rectangle overlapRectangle = new Rectangle(new Point(-5, -3), new Point(-2, -1));
		
		subtractionTestBase(baseRectangle, subtractionRectangle, overlapRectangle);
	}
	
	@Test
	public void subtractionBaseBottomCornersInsideSubtractionRectangle() {
		Rectangle baseRectangle = new Rectangle(new Point(0, -3), new Point(1, 6));
		Rectangle subtractionRectangle = new Rectangle(new Point(-1, -4), new Point(2, 5));
		Rectangle overlapRectangle = new Rectangle(new Point(0, -3), new Point(1, 5));
		
		subtractionTestBase(baseRectangle, subtractionRectangle, overlapRectangle);
	}
	
	@Test
	public void subtractionBaseRightCornersInsideSubtractionRectangle() {
		Rectangle baseRectangle = new Rectangle(new Point(-11, 2), new Point(-3, 4));
		Rectangle subtractionRectangle = new Rectangle(new Point(-6, -3), new Point(10, 5));
		Rectangle overlapRectangle = new Rectangle(new Point(-6, 2), new Point(-3, 4));
		
		subtractionTestBase(baseRectangle, subtractionRectangle, overlapRectangle);
	}
	
	@Test
	public void subtractionNoneOverlappingLeft() {
		Rectangle baseRectangle = new Rectangle(new Point(-11, 2), new Point(-3, 4));
		Rectangle subtractionRectangle = new Rectangle(new Point(-15, -6), new Point(-12, 5));
		Rectangle overlapRectangle = new Rectangle(new Point(0, 0), new Point(0, 0));
		
		subtractionTestBase(baseRectangle, subtractionRectangle, overlapRectangle);
	}
	
	@Test
	public void subtractionNoneOverlappingTop() {
		Rectangle baseRectangle = new Rectangle(new Point(1, -5), new Point(6, -1));
		Rectangle subtractionRectangle = new Rectangle(new Point(-15, 1), new Point(8, 9));
		Rectangle overlapRectangle = new Rectangle(new Point(0, 0), new Point(0, 0));
		
		subtractionTestBase(baseRectangle, subtractionRectangle, overlapRectangle);
	}
	
	@Test
	public void subtractionNoneOverlappingRight() {
		Rectangle baseRectangle = new Rectangle(new Point(2, -5), new Point(9, 2));
		Rectangle subtractionRectangle = new Rectangle(new Point(10, -7), new Point(14, 1));
		Rectangle overlapRectangle = new Rectangle(new Point(0, 0), new Point(0, 0));
		
		subtractionTestBase(baseRectangle, subtractionRectangle, overlapRectangle);
	}
	
	@Test
	public void subtractionNoneOverlappingBottom() {
		Rectangle baseRectangle = new Rectangle(new Point(-8, -6), new Point(7, 2));
		Rectangle subtractionRectangle = new Rectangle(new Point(-7, -11), new Point(-5, -7));
		Rectangle overlapRectangle = new Rectangle(new Point(0, 0), new Point(0, 0));
		
		subtractionTestBase(baseRectangle, subtractionRectangle, overlapRectangle);
	}
	
	@Test
	public void subtractionNoneOverlappingTopLeft() {
		Rectangle baseRectangle = new Rectangle(new Point(-7, 0), new Point(9, 1));
		Rectangle subtractionRectangle = new Rectangle(new Point(-8, 2), new Point(-7, 5));
		Rectangle overlapRectangle = new Rectangle(new Point(0, 0), new Point(0, 0));
		
		subtractionTestBase(baseRectangle, subtractionRectangle, overlapRectangle);
	}
	
	@Test
	public void subtractionNoneOverlappingTopRight() {
		Rectangle baseRectangle = new Rectangle(new Point(-7, 0), new Point(-6, 1));
		Rectangle subtractionRectangle = new Rectangle(new Point(-5, 1), new Point(-4, 7));
		Rectangle overlapRectangle = new Rectangle(new Point(0, 0), new Point(0, 0));
		
		subtractionTestBase(baseRectangle, subtractionRectangle, overlapRectangle);
	}
	
	@Test
	public void subtractionNoneOverlappingBottomRight() {
		Rectangle baseRectangle = new Rectangle(new Point(-7, -4), new Point(6, 10));
		Rectangle subtractionRectangle = new Rectangle(new Point(8, -7), new Point(12, -5));
		Rectangle overlapRectangle = new Rectangle(new Point(0, 0), new Point(0, 0));
		
		subtractionTestBase(baseRectangle, subtractionRectangle, overlapRectangle);
	}
	
	@Test
	public void subtractionNoneOverlappingBottomLeft() {
		Rectangle baseRectangle = new Rectangle(new Point(-1, -5), new Point(2, 8));
		Rectangle subtractionRectangle = new Rectangle(new Point(-5, -8), new Point(-2, -7));
		Rectangle overlapRectangle = new Rectangle(new Point(0, 0), new Point(0, 0));
		
		subtractionTestBase(baseRectangle, subtractionRectangle, overlapRectangle);
	}
	
	@Test
	public void subtractionNoneOverlappingTopLeftIntersects() {
		Rectangle baseRectangle = new Rectangle(new Point(0, 4), new Point(2, 7));
		Rectangle subtractionRectangle = new Rectangle(new Point(-5, 7), new Point(0, 10));
		Rectangle overlapRectangle = new Rectangle(new Point(0, 0), new Point(0, 0));
		
		subtractionTestBase(baseRectangle, subtractionRectangle, overlapRectangle);
	}
	
	@Test
	public void subtractionNoneOverlappingTopRightIntersects() {
		Rectangle baseRectangle = new Rectangle(new Point(-3, 2), new Point(-2, 5));
		Rectangle subtractionRectangle = new Rectangle(new Point(-2, 5), new Point(1, 9));
		Rectangle overlapRectangle = new Rectangle(new Point(0, 0), new Point(0, 0));
		
		subtractionTestBase(baseRectangle, subtractionRectangle, overlapRectangle);
	}
	
	@Test
	public void subtractionNoneOverlappingBottomRightIntersects() {
		Rectangle baseRectangle = new Rectangle(new Point(-7, -5), new Point(-2, -1));
		Rectangle subtractionRectangle = new Rectangle(new Point(-2, -8), new Point(3, -5));
		Rectangle overlapRectangle = new Rectangle(new Point(0, 0), new Point(0, 0));
		
		subtractionTestBase(baseRectangle, subtractionRectangle, overlapRectangle);
	}
	
	@Test
	public void subtractionNoneOverlappingBottomLeftIntersects() {
		Rectangle baseRectangle = new Rectangle(new Point(-4, -3), new Point(1, 2));
		Rectangle subtractionRectangle = new Rectangle(new Point(-8, -5), new Point(-4, -3));
		Rectangle overlapRectangle = new Rectangle(new Point(0, 0), new Point(0, 0));
		
		subtractionTestBase(baseRectangle, subtractionRectangle, overlapRectangle);
	}
	
	@Test
	public void subtractionNoneOverlappingLeftIntersects() {
		Rectangle baseRectangle = new Rectangle(new Point(0, -1), new Point(7, 5));
		Rectangle subtractionRectangle = new Rectangle(new Point(-7, 1), new Point(0, 4));
		Rectangle overlapRectangle = new Rectangle(new Point(0, 0), new Point(0, 0));
		
		subtractionTestBase(baseRectangle, subtractionRectangle, overlapRectangle);
	}
	
	@Test
	public void subtractionNoneOverlappingTopIntersects() {
		Rectangle baseRectangle = new Rectangle(new Point(2, -1), new Point(7, 2));
		Rectangle subtractionRectangle = new Rectangle(new Point(4, 2), new Point(6, 4));
		Rectangle overlapRectangle = new Rectangle(new Point(0, 0), new Point(0, 0));
		
		subtractionTestBase(baseRectangle, subtractionRectangle, overlapRectangle);
	}
	
	@Test
	public void subtractionNoneOverlappingRightIntersects() {
		Rectangle baseRectangle = new Rectangle(new Point(-2, -4), new Point(3, 2));
		Rectangle subtractionRectangle = new Rectangle(new Point(3, -2), new Point(6, 1));
		Rectangle overlapRectangle = new Rectangle(new Point(0, 0), new Point(0, 0));
		
		subtractionTestBase(baseRectangle, subtractionRectangle, overlapRectangle);
	}
	
	@Test
	public void subtractionNoneOverlappingBottomIntersects() {
		Rectangle baseRectangle = new Rectangle(new Point(-4, 1), new Point(3, 2));
		Rectangle subtractionRectangle = new Rectangle(new Point(-2, -3), new Point(5, 1));
		Rectangle overlapRectangle = new Rectangle(new Point(0, 0), new Point(0, 0));
		
		subtractionTestBase(baseRectangle, subtractionRectangle, overlapRectangle);
	}
	
	@Test
	public void subtractionVerticalIntersectionNoCornersInside() {
		Rectangle baseRectangle = new Rectangle(new Point(1, -1), new Point(8, 2));
		Rectangle subtractionRectangle = new Rectangle(new Point(3, -2), new Point(5, 3));
		Rectangle overlapRectangle = new Rectangle(new Point(3, -1), new Point(5, 2));
		
		subtractionTestBase(baseRectangle, subtractionRectangle, overlapRectangle);
	}
	
	@Test
	public void subtractionHorizontalIntersectionNoCornersInside() {
		Rectangle baseRectangle = new Rectangle(new Point(0, -1), new Point(8, 4));
		Rectangle subtractionRectangle = new Rectangle(new Point(-3, 1), new Point(10, 3));
		Rectangle overlapRectangle = new Rectangle(new Point(0, 1), new Point(8, 3));
		
		subtractionTestBase(baseRectangle, subtractionRectangle, overlapRectangle);
	}
	
	@Test
	public void subtractionFullyEnclosed() {
		Rectangle baseRectangle = new Rectangle(new Point(4, 0), new Point(8, 6));
		Rectangle subtractionRectangle = new Rectangle(new Point(5, 1), new Point(7, 3));
		Rectangle overlapRectangle = new Rectangle(new Point(5, 1), new Point(7, 3));
		
		subtractionTestBase(baseRectangle, subtractionRectangle, overlapRectangle);
	}
	
	@Test
	public void subtractionFullyWrapped() {
		Rectangle baseRectangle = new Rectangle(new Point(1, -2), new Point(5, 1));
		Rectangle subtractionRectangle = new Rectangle(new Point(0, -5), new Point(10, 8));
		Rectangle overlapRectangle = new Rectangle(new Point(1, -2), new Point(5, 1));
		
		subtractionTestBase(baseRectangle, subtractionRectangle, overlapRectangle);
	}
	
	@Test
	public void subtractionEnclosedIntersectingTopLeft() {
		Rectangle baseRectangle = new Rectangle(new Point(-3, -4), new Point(1, -1));
		Rectangle subtractionRectangle = new Rectangle(new Point(-3, -3), new Point(0, -1));
		Rectangle overlapRectangle = new Rectangle(new Point(-3, -3), new Point(0, -1));
		
		subtractionTestBase(baseRectangle, subtractionRectangle, overlapRectangle);
	}
	
	@Test
	public void subtractionEnclosedIntersectingTopRight() {
		Rectangle baseRectangle = new Rectangle(new Point(-8, -11), new Point(-1, -4));
		Rectangle subtractionRectangle = new Rectangle(new Point(-4, -7), new Point(-1, -4));
		Rectangle overlapRectangle = new Rectangle(new Point(-4, -7), new Point(-1, -4));
		
		subtractionTestBase(baseRectangle, subtractionRectangle, overlapRectangle);
	}
	
	@Test
	public void subtractionEnclosedIntersectingBottomRight() {
		Rectangle baseRectangle = new Rectangle(new Point(-5, -7), new Point(-2, 4));
		Rectangle subtractionRectangle = new Rectangle(new Point(-4, -7), new Point(-2, -4));
		Rectangle overlapRectangle = new Rectangle(new Point(-4, -7), new Point(-2, -4));
		
		subtractionTestBase(baseRectangle, subtractionRectangle, overlapRectangle);
	}
	
	@Test
	public void subtractionEnclosedIntersectingBottomLeft() {
		Rectangle baseRectangle = new Rectangle(new Point(-7, -10), new Point(10, 7));
		Rectangle subtractionRectangle = new Rectangle(new Point(-7, -10), new Point(1, -9));
		Rectangle overlapRectangle = new Rectangle(new Point(-7, -10), new Point(1, -9));
		
		subtractionTestBase(baseRectangle, subtractionRectangle, overlapRectangle);
	}
	
	@Test
	public void subtractionEnclosedIntersectingTop() {
		Rectangle baseRectangle = new Rectangle(new Point(-4, -6), new Point(9, 5));
		Rectangle subtractionRectangle = new Rectangle(new Point(-2, -4), new Point(3, 5));
		Rectangle overlapRectangle = new Rectangle(new Point(-2, -4), new Point(3, 5));
		
		subtractionTestBase(baseRectangle, subtractionRectangle, overlapRectangle);
	}
	
	@Test
	public void subtractionEnclosedIntersectingRight() {
		Rectangle baseRectangle = new Rectangle(new Point(-6, -2), new Point(4, 5));
		Rectangle subtractionRectangle = new Rectangle(new Point(1, -1), new Point(4, 3));
		Rectangle overlapRectangle = new Rectangle(new Point(1, -1), new Point(4, 3));
		
		subtractionTestBase(baseRectangle, subtractionRectangle, overlapRectangle);
	}
	
	@Test
	public void subtractionEnclosedIntersectingBottom() {
		Rectangle baseRectangle = new Rectangle(new Point(-6, -2), new Point(4, 5));
		Rectangle subtractionRectangle = new Rectangle(new Point(-3, -2), new Point(2, 4));
		Rectangle overlapRectangle = new Rectangle(new Point(-3, -2), new Point(2, 4));
		
		subtractionTestBase(baseRectangle, subtractionRectangle, overlapRectangle);
	}
	
	@Test
	public void subtractionEnclosedIntersectingLeft() {
		Rectangle baseRectangle = new Rectangle(new Point(0, 2), new Point(7, 15));
		Rectangle subtractionRectangle = new Rectangle(new Point(0, 3), new Point(4, 6));
		Rectangle overlapRectangle = new Rectangle(new Point(0, 3), new Point(4, 6));
		
		subtractionTestBase(baseRectangle, subtractionRectangle, overlapRectangle);
	}
	
	@Test
	public void subtractionEnclosedIntersectingLeftAndRight() {
		Rectangle baseRectangle = new Rectangle(new Point(-3, -2), new Point(11, 10));
		Rectangle subtractionRectangle = new Rectangle(new Point(-3, 1), new Point(11, 6));
		Rectangle overlapRectangle = new Rectangle(new Point(-3, 1), new Point(11, 6));
		
		subtractionTestBase(baseRectangle, subtractionRectangle, overlapRectangle);
	}
	
	@Test
	public void subtractionEnclosedIntersectingTopAndBottom() {
		Rectangle baseRectangle = new Rectangle(new Point(0, -12), new Point(7, 12));
		Rectangle subtractionRectangle = new Rectangle(new Point(1, -12), new Point(6, 12));
		Rectangle overlapRectangle = new Rectangle(new Point(1, -12), new Point(6, 12));
		
		subtractionTestBase(baseRectangle, subtractionRectangle, overlapRectangle);
	}
	
	@Test
	public void subtractionBaseRectangleEnclosedIntersectingTopLeft() {
		Rectangle baseRectangle = new Rectangle(new Point(-3, -3), new Point(0, -1));
		Rectangle subtractionRectangle = new Rectangle(new Point(-3, -4), new Point(1, -1));
		Rectangle overlapRectangle = new Rectangle(new Point(-3, -3), new Point(0, -1));
		
		subtractionTestBase(baseRectangle, subtractionRectangle, overlapRectangle);
	}
	
	@Test
	public void subtractionBaseRectangleEnclosedIntersectingTopRight() {
		Rectangle baseRectangle = new Rectangle(new Point(-4, -7), new Point(-1, -4));
		Rectangle subtractionRectangle = new Rectangle(new Point(-8, -11), new Point(-1, -4));
		Rectangle overlapRectangle = new Rectangle(new Point(-4, -7), new Point(-1, -4));
		
		subtractionTestBase(baseRectangle, subtractionRectangle, overlapRectangle);
	}
	
	@Test
	public void subtractionBaseRectangleEnclosedIntersectingBottomRight() {
		Rectangle baseRectangle = new Rectangle(new Point(-4, -7), new Point(-2, -4));
		Rectangle subtractionRectangle = new Rectangle(new Point(-5, -7), new Point(-2, 4));
		Rectangle overlapRectangle = new Rectangle(new Point(-4, -7), new Point(-2, -4));
		
		subtractionTestBase(baseRectangle, subtractionRectangle, overlapRectangle);
	}
	
	@Test
	public void subtractionBaseRectangleEnclosedIntersectingBottomLeft() {
		Rectangle baseRectangle = new Rectangle(new Point(-7, -10), new Point(1, -9));
		Rectangle subtractionRectangle = new Rectangle(new Point(-7, -10), new Point(10, 7));
		Rectangle overlapRectangle = new Rectangle(new Point(-7, -10), new Point(1, -9));
		
		subtractionTestBase(baseRectangle, subtractionRectangle, overlapRectangle);
	}
	
	@Test
	public void subtractionBaseRectangleEnclosedIntersectingTop() {
		Rectangle baseRectangle = new Rectangle(new Point(-2, -4), new Point(3, 5));
		Rectangle subtractionRectangle = new Rectangle(new Point(-4, -6), new Point(9, 5));
		Rectangle overlapRectangle = new Rectangle(new Point(-2, -4), new Point(3, 5));
		
		subtractionTestBase(baseRectangle, subtractionRectangle, overlapRectangle);
	}
	
	@Test
	public void subtractionBaseRectangleEnclosedIntersectingRight() {
		Rectangle baseRectangle = new Rectangle(new Point(1, -1), new Point(4, 3));
		Rectangle subtractionRectangle = new Rectangle(new Point(-6, -2), new Point(4, 5));
		Rectangle overlapRectangle = new Rectangle(new Point(1, -1), new Point(4, 3));
		
		subtractionTestBase(baseRectangle, subtractionRectangle, overlapRectangle);
	}
	
	@Test
	public void subtractionBaseRectangleEnclosedIntersectingBottom() {
		Rectangle baseRectangle = new Rectangle(new Point(-3, -2), new Point(2, 4));
		Rectangle subtractionRectangle = new Rectangle(new Point(-6, -2), new Point(4, 5));
		Rectangle overlapRectangle = new Rectangle(new Point(-3, -2), new Point(2, 4));
		
		subtractionTestBase(baseRectangle, subtractionRectangle, overlapRectangle);
	}
	
	@Test
	public void subtractionBaseRectangleEnclosedIntersectingLeft() {
		Rectangle baseRectangle = new Rectangle(new Point(0, 3), new Point(4, 6));
		Rectangle subtractionRectangle = new Rectangle(new Point(0, 2), new Point(7, 15));
		Rectangle overlapRectangle = new Rectangle(new Point(0, 3), new Point(4, 6));
		
		subtractionTestBase(baseRectangle, subtractionRectangle, overlapRectangle);
	}
	
	@Test
	public void subtractionBaseRectangleEnclosedIntersectingLeftAndRight() {
		Rectangle baseRectangle = new Rectangle(new Point(-3, 1), new Point(11, 6));
		Rectangle subtractionRectangle = new Rectangle(new Point(-3, -2), new Point(11, 10));
		Rectangle overlapRectangle = new Rectangle(new Point(-3, 1), new Point(11, 6));
		
		subtractionTestBase(baseRectangle, subtractionRectangle, overlapRectangle);
	}
	
	@Test
	public void subtractionBaseRectangleEnclosedIntersectingTopAndBottom() {
		Rectangle baseRectangle = new Rectangle(new Point(1, -12), new Point(6, 12));
		Rectangle subtractionRectangle = new Rectangle(new Point(0, -12), new Point(7, 12));
		Rectangle overlapRectangle = new Rectangle(new Point(1, -12), new Point(6, 12));
		
		subtractionTestBase(baseRectangle, subtractionRectangle, overlapRectangle);
	}
	
	@Test
	public void subtractionEqualToBaseRectangle() {
		Rectangle baseRectangle = new Rectangle(new Point(1, -12), new Point(6, 12));
		Rectangle subtractionRectangle = new Rectangle(new Point(1, -12), new Point(6, 12));
		Rectangle overlapRectangle = new Rectangle(new Point(1, -12), new Point(6, 12));
		
		subtractionTestBase(baseRectangle, subtractionRectangle, overlapRectangle);
	}
	
	@Test
	public void subtractionRandomRuns() {
		RandomPointGenerator random = new RandomPointGenerator(12345);
		Point minPoint = new Point(-5000, -5000);
		Point maxPoint = new Point(5000, 5000);
		for(int i=0; i<100; i++) {
			Point baseRectangleMin = random.randomPoint(minPoint, maxPoint);
			Point baseRectangleMax = random.randomPoint(baseRectangleMin, maxPoint);
			Rectangle baseRectangle = new Rectangle(baseRectangleMin, baseRectangleMax);
			
			Point subtractionRectangleMin = random.randomPoint(minPoint, maxPoint);
			Point subtractionRectangleMax = random.randomPoint(subtractionRectangleMin, maxPoint);
			Rectangle subtractionRectangle = new Rectangle(subtractionRectangleMin, subtractionRectangleMax);
			
			List<Rectangle> newRects = baseRectangle.subtractRectangle(subtractionRectangle);
			
			assertInside(newRects, baseRectangle);
			assertNoOverlap(newRects);
		}
	}

	
	private void subtractionTestBase(
			Rectangle baseRectangle,
			Rectangle subtractionRectangle,
			Rectangle overlapRectangle) {
		List<Rectangle> newRects = baseRectangle.subtractRectangle(subtractionRectangle);
		
		assertInside(newRects, baseRectangle);
		assertNoOverlap(newRects);
		assertAreaEquals(baseRectangle, newRects, Optional.of(overlapRectangle));
	}
	
	@Test
	public void convertToSquareAlreadySquare() {
		Rectangle baseRectangle = new Rectangle(new Point(-3, -3), new Point(10, 10));
		squareConversionTestBase(baseRectangle);
	}
	
	@Test
	public void convertToSquareLongAndThin() {
		Rectangle baseRectangle = new Rectangle(new Point(1, -2), new Point(100, -1));
		squareConversionTestBase(baseRectangle);
	}
	
	@Test
	public void convertToSquareHighAndThin() {
		Rectangle baseRectangle = new Rectangle(new Point(1, -2), new Point(2, 200));
		squareConversionTestBase(baseRectangle);
	}
	
	@Test
	public void convertRectangle1ToSquare() {
		Rectangle baseRectangle = new Rectangle(new Point(-3, -3), new Point(5, 10));
		squareConversionTestBase(baseRectangle);
	}
	
	@Test
	public void convertRectangle2ToSquare() {
		Rectangle baseRectangle = new Rectangle(new Point(1, -20), new Point(100, -1));
		squareConversionTestBase(baseRectangle);
	}
	
	@Test
	public void convertRectangle3ToSquare() {
		Rectangle baseRectangle = new Rectangle(new Point(-100, -100), new Point(100, 1123));
		squareConversionTestBase(baseRectangle);
	}
	
	@Test
	public void convertManyRectanglesToSquare() {
		RandomPointGenerator random = new RandomPointGenerator(1234);
		Point minPoint = new Point(-5000, -5000);
		Point maxPoint = new Point(5000, 5000);
		for(int i=0; i<100; i++) {
			Point minCorner = random.randomPoint(minPoint, maxPoint);
			Point maxCorner = random.randomPoint(minCorner, maxPoint);
			
			Rectangle baseRectangle = new Rectangle(minCorner, maxCorner);
			squareConversionTestBase(baseRectangle);
		}
	}
	
	@Test
	public void convertUnitSquareToSquare() {
		Rectangle baseRectangle = new Rectangle(new Point(1, 1), new Point(2, 2));
		squareConversionTestBase(baseRectangle);
	}
	
	@Test(expected = IllegalStateException.class)
	public void convertLineToSquare() {
		Rectangle baseRectangle = new Rectangle(new Point(1, 1), new Point(1, 3));
		squareConversionTestBase(baseRectangle);
	}
	
	private void squareConversionTestBase(Rectangle rectangle) {
		List<Square> squares = rectangle.convertToSquares();
		
		assertInsideSquares(squares, rectangle);
		assertNoOverlapSquaresList(squares);
		assertAreaEquals(rectangle, squares);
		
		Iterable<Square> squareIterable = rectangle.getSquaresIterable();
		assertInsideSquares(squareIterable, rectangle);
		assertNoOverlapSquaresIterable(squareIterable);
		assertAreaEquals(rectangle, squareIterable);
	}
	
	@Test
	public void subtractRectanglesNoneOverlapping() {
		Rectangle baseRectangle = new Rectangle(new Point(-5, -5), new Point(0, -2));
		List<Rectangle> subtractionRectangles = Arrays.asList(
				new Rectangle(new Point(0, 0), new Point(5, 3)),
				new Rectangle(new Point(3, 3), new Point(6, 6)),
				new Rectangle(new Point(-10, -5), new Point(-5, -2)));
		List<Rectangle> overlapRegion = Arrays.asList();
		
		subtractRectanglesBaseTest(baseRectangle, subtractionRectangles, overlapRegion);
	}
	
	@Test
	public void subtractRectanglesFullyOverlapping() {
		Rectangle baseRectangle = new Rectangle(new Point(-5, -5), new Point(0, -2));
		List<Rectangle> subtractionRectangles = Arrays.asList(
				new Rectangle(new Point(-8, -5), new Point(-3, -2)),
				new Rectangle(new Point(-4, -4), new Point(0, -1)),
				new Rectangle(new Point(-4, -7), new Point(1, -4)));
		List<Rectangle> overlapRegion = Arrays.asList(
				new Rectangle(new Point(-5, -5), new Point(0, -2)));
		
		subtractRectanglesBaseTest(baseRectangle, subtractionRectangles, overlapRegion);
	}
	
	@Test
	public void subtractRectanglesPartiallyOverlapping() {
		Rectangle baseRectangle = new Rectangle(new Point(-5, -5), new Point(0, -2));
		List<Rectangle> subtractionRectangles = Arrays.asList(
				new Rectangle(new Point(-8, -5), new Point(-3, -2)),
				new Rectangle(new Point(-2, -3), new Point(2, 0)),
				new Rectangle(new Point(-4, -7), new Point(1, -4)));
		List<Rectangle> overlapRegion = Arrays.asList(
				new Rectangle(new Point(-5, -5), new Point(-3, -2)),
				new Rectangle(new Point(-3, -5), new Point(0, -4)),
				new Rectangle(new Point(-2, -3), new Point(0, -2)));
		
		subtractRectanglesBaseTest(baseRectangle, subtractionRectangles, overlapRegion);
	}
	
	@Test
	public void subtractRectanglesAllInside() {
		Rectangle baseRectangle = new Rectangle(new Point(-1000, -500), new Point(300, 20));
		List<Rectangle> subtractionRectangles = Arrays.asList(
				new Rectangle(new Point(-8, -5), new Point(-3, -2)),
				new Rectangle(new Point(-2, -3), new Point(2, 0)),
				new Rectangle(new Point(-4, -7), new Point(1, -4)));
		// Two of the subtraction rectangles are overlapping, so the
		// overlap region is not identical to subtraction rectangles
		List<Rectangle> overlapRegion = Arrays.asList(
				new Rectangle(new Point(-8, -5), new Point(-4, -2)),
				new Rectangle(new Point(-2, -3), new Point(2, 0)),
				new Rectangle(new Point(-4, -7), new Point(1, -4)),
				new Rectangle(new Point(-4, -4), new Point(-3, -2)));
		
		subtractRectanglesBaseTest(baseRectangle, subtractionRectangles, overlapRegion);
	}
	
	@Test
	public void subtractRectanglesEmptySubtractionList() {
		Rectangle baseRectangle = new Rectangle(new Point(-5, -5), new Point(0, -2));
		List<Rectangle> subtractionRectangles = Arrays.asList();
		List<Rectangle> overlapRegion = Arrays.asList();
		
		subtractRectanglesBaseTest(baseRectangle, subtractionRectangles, overlapRegion);
	}
	
	private void subtractRectanglesBaseTest(
			Rectangle baseRectangle,
			List<Rectangle> subtractionRectangles,
			List<Rectangle> overlapRegions) {
		List<Rectangle> newRects = baseRectangle.subtractRectangles(subtractionRectangles);
		
		assertInside(newRects, baseRectangle);
		assertNoOverlap(newRects);
		assertAreaEquals(baseRectangle, newRects, overlapRegions);
	}
}
