package mt.edu.um.util.math;

import java.util.Collection;

public class MathUtils {

	public static double max(double[] values) {
		double max = 0.0;
		
		for(int i = 0; i < values.length; i++) {
			if(values[i] > max) {
				max = values[i];
			}
		}
		
		return max;
	}
	
	public static double sum(Collection<Double> numbers) {
		double sum = 0.0;
		
		for(Double num: numbers) {
			sum += num;
		}
		
		return sum;
	}
	
	public static double multiply(Collection<Double> numbers) {
		double mult = 1.0;
		
		for(Double n: numbers) {
			mult *= n;
		}
		
		
		return mult;
	}
	
	public static double mean(Collection<Double> numbers) {
		return sum(numbers)/numbers.size();
	}
	
	public static double geometricMean(Collection<Double> numbers) {
		return Math.pow(multiply(numbers), 1.0/new Double(numbers.size()));
	}
	
}
