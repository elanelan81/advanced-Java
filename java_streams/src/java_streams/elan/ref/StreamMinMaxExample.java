package java_streams.elan.ref;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

public class StreamMinMaxExample {

	public static void main(String[] args) {
		List<Integer> numbers = Arrays.asList(10,20,5,45,2,100,23);
		//Max
		Optional<Integer> max = numbers.stream().max(Integer::compareTo);
		//Min
		Optional<Integer> min = numbers.stream().min(Integer::compareTo);
		
		max.ifPresent(m -> System.out.println("Maximum: "+m));
		min.ifPresent(m -> System.out.println("Minimum: "+m));

		/*
		 * 
Steps
1. create a list of integers using Arrays.asList(....)
2. Convert the list to a stream with numbers.stream().
3. Find the Maximum value using .max(Integer::CompareTo) which returns an Optional<Integer>
4. Print Max and Min values using ifPresent() to avoid null or empty list of issues.		 
5. Output		 
Maximum: 100
Minimum: 2
 
		 */

	}

}
