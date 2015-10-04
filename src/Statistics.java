import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map.Entry;

public class Statistics {

	public static double mean(ArrayList<Double> arr) {
		int sum = 0;
		for (Double d : arr) {
			sum += d;
		}
		return sum / arr.size();
	}

	static double jointVariance(ArrayList<Double> arr1,
			ArrayList<Double> arr2) {
		double x1 = mean(arr1), x2 = mean(arr2);
		int sum1 = 0, sum2 = 0;
		for (Double d : arr1) {
			sum1 += (d - x1) * (d - x1);
		}
		for (Double d : arr2) {
			sum2 += (d - x2) * (d - x2);
		}
		return (sum1 + sum2) / ((double) (arr1.size() + arr2.size() - 2));
	}

	public static double tTest(ArrayList<Double> arr1,
			ArrayList<Double> arr2) {
		double x1 = mean(arr1), x2 = mean(arr2);
		double var = jointVariance(arr1, arr2);
		double num = x1 - x2;
		double dn1 = 1 / (double) arr1.size();
		double dn2 = 1 / (double) arr2.size();
		double denom = Math.sqrt(var * (dn1 + dn2));
		double t = num / denom;
		return t;
	}

	public static double fTest(HashMap<String, ArrayList<Double>> groupMap) {

		HashMap<String, Double> groupMean = new HashMap<String, Double>();
		for (Entry<String, ArrayList<Double>> e : groupMap.entrySet()) {
			groupMean.put(e.getKey(), mean(e.getValue()));
		}

		ArrayList<Double> unionAllGroups = new ArrayList<>();
		for (Entry<String, ArrayList<Double>> e : groupMap.entrySet()) {
			unionAllGroups.addAll(e.getValue());
		}

		double grandMean = mean(unionAllGroups);

		double SSW = sumOfSquaresWithin(groupMap, groupMean);
		double SSB = sumOfSquaresBetween(groupMean, grandMean);

		int N = allN(groupMap) - 1;
		int m = groupMap.size() - 1;
		int mn = N - m;
		double f = (SSB / (double) m) / (SSW / (double) mn);
		return f;
	}

	static double sumOfSquaresBetween(HashMap<String, Double> groupMean,
			double grandMean) {
		double sum = 0;
		for (Entry<String, Double> e : groupMean.entrySet()) {
			sum += (e.getValue() - grandMean) * (e.getValue() - grandMean);
		}

		return sum;
	}

	static double sumOfSquaresWithin(
			HashMap<String, ArrayList<Double>> groupMap,
			HashMap<String, Double> groupMean) {
		double sum = 0;
		for (Entry<String, ArrayList<Double>> e : groupMap.entrySet()) {
			for (Double xi : e.getValue()) {
				sum += xi - groupMean.get(e.getKey());
			}
		}
		return sum;
	}

	static int allN(HashMap<String, ArrayList<Double>> groupMap) {
		int sum = 0;
		for (ArrayList<Double> v : groupMap.values()) {
			sum += v.size();
		}
		return sum;
	}

}
