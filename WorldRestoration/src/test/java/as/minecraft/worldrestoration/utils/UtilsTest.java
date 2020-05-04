package as.minecraft.worldrestoration.utils;

import static org.junit.Assert.assertEquals;

import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TestName;

public class UtilsTest {
	@Rule public TestName name = new TestName();
	
	@Test
	public void secondsAreConvertedToSeconds(){
		assertEquals(10, Utils.getSecondsFromTimeUnit("10 seconds"));
		assertEquals(10, Utils.getSecondsFromTimeUnit("10 second"));
		assertEquals(10, Utils.getSecondsFromTimeUnit(" 10 seconds  "));
		assertEquals(1, Utils.getSecondsFromTimeUnit("1 second"));
		assertEquals(1, Utils.getSecondsFromTimeUnit("1 seconds"));
		assertEquals(1, Utils.getSecondsFromTimeUnit("1 WEIRD SeCoNdS-@WEIRD"));
	}
	@Test
	public void sIsConvertedToSeconds(){
		assertEquals(10, Utils.getSecondsFromTimeUnit("10 s"));
		assertEquals(10, Utils.getSecondsFromTimeUnit("10s"));
		assertEquals(10, Utils.getSecondsFromTimeUnit(" 10 s  "));
	}
	
	@Test
	public void minutesAreConvertedToSeconds(){
		assertEquals(10*60, Utils.getSecondsFromTimeUnit("10 minutes"));
		assertEquals(10*60, Utils.getSecondsFromTimeUnit("10 minute"));
		assertEquals(10*60, Utils.getSecondsFromTimeUnit(" 10 minutes  "));
		assertEquals(1*60, Utils.getSecondsFromTimeUnit("1 minute"));
		assertEquals(1*60, Utils.getSecondsFromTimeUnit("1 minutes"));
		assertEquals(1*60, Utils.getSecondsFromTimeUnit("1 WEIRD MiNuTeS-@WEIRD"));
	}
	@Test
	public void mIsConvertedToSeconds(){
		assertEquals(10*60, Utils.getSecondsFromTimeUnit("10 m"));
		assertEquals(10*60, Utils.getSecondsFromTimeUnit("10m"));
		assertEquals(10*60, Utils.getSecondsFromTimeUnit(" 10 m  "));
	}
	
	@Test
	public void hoursAreConvertedToSeconds(){
		assertEquals(10*60*60, Utils.getSecondsFromTimeUnit("10 hours"));
		assertEquals(10*60*60, Utils.getSecondsFromTimeUnit("10 hour"));
		assertEquals(10*60*60, Utils.getSecondsFromTimeUnit(" 10 hours  "));
		assertEquals(1*60*60, Utils.getSecondsFromTimeUnit("1 hour"));
		assertEquals(1*60*60, Utils.getSecondsFromTimeUnit("1 hours"));
		assertEquals(1*60*60, Utils.getSecondsFromTimeUnit("1 WEIRD HoUrS-@WEIRD"));
	}
	@Test
	public void hIsConvertedToSeconds(){
		assertEquals(10*60*60, Utils.getSecondsFromTimeUnit("10 h"));
		assertEquals(10*60*60, Utils.getSecondsFromTimeUnit("10h"));
		assertEquals(10*60*60, Utils.getSecondsFromTimeUnit(" 10 h  "));
	}
	
	@Test
	public void daysAreConvertedToSeconds(){
		assertEquals(10*60*60*24, Utils.getSecondsFromTimeUnit("10 days"));
		assertEquals(10*60*60*24, Utils.getSecondsFromTimeUnit("10 day"));
		assertEquals(10*60*60*24, Utils.getSecondsFromTimeUnit(" 10 days  "));
		assertEquals(1*60*60*24, Utils.getSecondsFromTimeUnit("1 day"));
		assertEquals(1*60*60*24, Utils.getSecondsFromTimeUnit("1 days"));
		assertEquals(1*60*60*24, Utils.getSecondsFromTimeUnit("1 WEIRD DaYs-@WEIRD"));
	}
	@Test
	public void dIsConvertedToSeconds(){
		assertEquals(10*60*60*24, Utils.getSecondsFromTimeUnit("10 d"));
		assertEquals(10*60*60*24, Utils.getSecondsFromTimeUnit("10d"));
		assertEquals(10*60*60*24, Utils.getSecondsFromTimeUnit(" 10 d  "));
	}
	
	@Test
	public void unknownTagIsConvertedToSecondsWithWarning(){
		System.out.println("2 Warnings expected!");
		assertEquals(10, Utils.getSecondsFromTimeUnit("10 fishes"));
		assertEquals(10, Utils.getSecondsFromTimeUnit("10 d 1002"));
	}
	
	@Test
	public void multipleTagsAssumesLowestTimeUnitAndIsConvertedToSeconds(){
		assertEquals(10, Utils.getSecondsFromTimeUnit("10 days hours minutes second"));
		assertEquals(10, Utils.getSecondsFromTimeUnit("10 seconds minute hours"));
		assertEquals(10*60, Utils.getSecondsFromTimeUnit("10 hour minutes"));
		assertEquals(10*60*60, Utils.getSecondsFromTimeUnit("10 hours days"));
	}
	
	@Test
	public void weirdShortTagsAreConvertedToSeconds(){ //Time tags are not recognized and assumed to be seconds, and warns user
		System.out.println("4 Warnings expected!");
		assertEquals(10, Utils.getSecondsFromTimeUnit("10 WEIRD d-@WeIrD"));
		assertEquals(10, Utils.getSecondsFromTimeUnit("10 WEIRD m-@WeIrD"));
		assertEquals(10, Utils.getSecondsFromTimeUnit("10 WEIRD h-@WeIrD"));
		assertEquals(10, Utils.getSecondsFromTimeUnit("10 WEIRD s-@WeIrD"));
	}
	
	@Test
	public void getTicksFromTimeUnit() {
		//This calls getSecondsFromTimeUnit(), so testing all tags are not required
		assertEquals(10*60*60*24*20L, Utils.getTicksFromTimeUnit("10 days"));
		assertEquals(10*60*60*20L, Utils.getTicksFromTimeUnit("10 hour"));
		assertEquals(10*60*20L, Utils.getTicksFromTimeUnit("10 m"));
		assertEquals(10*20L, Utils.getTicksFromTimeUnit("10s"));
	}
	
	@Test(expected=NumberFormatException.class)
	public void expectedNumberFormatFailure() {
		Utils.getSecondsFromTimeUnit("ShouldFailWithoutNumbers");
		Utils.getSecondsFromTimeUnit("");
		Utils.getSecondsFromTimeUnit("fishes 10 fishes");
	}
}
