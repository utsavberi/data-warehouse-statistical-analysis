import static org.junit.Assert.*;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;

import org.junit.Test;


public class StatisticsTest {

	@Test
	public void testPearsonCorrelation() {
		fail("Not yet implemented");
	}

	@Test
	public void testCovariance() {
		fail("Not yet implemented");
	}

	@Test
	public void testVariance() {
		fail("Not yet implemented");
	}

	@Test
	public void testMean() {
		ArrayList<Double> test1 = new ArrayList<Double>(Arrays.asList(1.0,2.0,3.0,4.0,5.0,5.0));
		assertEquals(Statistics.mean(test1),3.0,.001);
//		fail("Not yet implemented");
	}

	@Test
	public void testJointVariance() {
		fail("Not yet implemented");
	}

	@Test
	public void testTTest() {
		ArrayList<Double> arr1 = new ArrayList<Double>(Arrays.asList(13.0,43.0,76.0,3.0,56.0,67.0,32.0,9.0,37.0,85.0));
		ArrayList<Double> arr2 = new ArrayList<Double>(Arrays.asList(37.0,42.0,48.0,42.0,62.0,69.0,42.0,984.0,5.0,72.0));
		assertEquals(-1.04,Statistics.tTest(arr1, arr2),.01);
	}

	
	@Test
	public void testFTest() {
		ArrayList<Double> arr1 = new ArrayList<Double>(Arrays.asList(13.0,43.0,76.0,3.0,56.0,67.0,32.0,9.0,37.0,85.0));
		ArrayList<Double> arr2 = new ArrayList<Double>(Arrays.asList(37.0,42.0,48.0,42.0,62.0,69.0,42.0,984.0,5.0,72.0));
		HashMap<String,ArrayList<Double>> map = new HashMap<>();
		map.put("group1", arr1);
		map.put("group2", arr2);
		assertEquals(0.009321272973445663,Statistics.fTest(map),.01);
	}

}
