package as.minecraft.worldrestoration.utils;

import static org.junit.Assert.assertEquals;

import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TestName;

public class utilsTest {
	@Rule public TestName name = new TestName();
	
	@Test
	public void testgetSecondsFromTimeUnit() {
		assertEquals(Utils.getSecondsFromTimeUnit("10 seconds"), 10);
		assertEquals(Utils.getSecondsFromTimeUnit("1 second"), 1);
		assertEquals(Utils.getSecondsFromTimeUnit("10 s"), 10);
		assertEquals(Utils.getSecondsFromTimeUnit("10s"), 10);
		assertEquals(Utils.getSecondsFromTimeUnit("10 minutes"), 600);
		assertEquals(Utils.getSecondsFromTimeUnit("1 minute"), 60);
		assertEquals(Utils.getSecondsFromTimeUnit("10 m"), 600);
		assertEquals(Utils.getSecondsFromTimeUnit("10m"), 600);
		assertEquals(Utils.getSecondsFromTimeUnit("10 hours"), 10*60*60);
		assertEquals(Utils.getSecondsFromTimeUnit("1 hour"), 60*60);
		assertEquals(Utils.getSecondsFromTimeUnit("10 h"), 10*60*60);
		assertEquals(Utils.getSecondsFromTimeUnit("10h"), 10*60*60);
		assertEquals(Utils.getSecondsFromTimeUnit("10 days"), 10*60*60*24);
		assertEquals(Utils.getSecondsFromTimeUnit("1 day"), 60*60*24);
		assertEquals(Utils.getSecondsFromTimeUnit("10 d"), 10*60*60*24);
		assertEquals(Utils.getSecondsFromTimeUnit("10d"), 10*60*60*24);
		
		assertEquals(Utils.getSecondsFromTimeUnit("10 fishes"), 10);
		assertEquals(Utils.getSecondsFromTimeUnit("fishes 10 fishes"), 60*60*24*1000);
		assertEquals(Utils.getSecondsFromTimeUnit("10 d 1002"), 10);
		assertEquals(Utils.getSecondsFromTimeUnit("10 d 1002 seconds"), 10);
		assertEquals(Utils.getSecondsFromTimeUnit("10 day 1002 seconds"), 10*60*60*24);
		assertEquals(Utils.getSecondsFromTimeUnit("10 day 1002 minute"), 10*60);
		assertEquals(Utils.getSecondsFromTimeUnit("ShouldReturnThousandDaysWithoutNumbers"), 60*60*24*1000);
		assertEquals(Utils.getSecondsFromTimeUnit(""), 60*60*24*1000);
		
		assertEquals(Utils.getTicksFromTimeUnit("10 fishes"), 10*20L);
		
	}
}
