package com.elan.java.faq;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

public class ElanJavaFaq {

	public static void main(String[] args) {
		
		System.out.println("Print Even numbers : ");
		int arr[] = new int[]{9,10,15,8,49,25,98,32,10};
		Arrays.stream(arr).filter(temp -> temp %2==0).forEach(System.out::println);
		
		System.out.println("Print Even numbers case two : ");
		List<Integer> numbers = Arrays.asList(1, 2, 3, 4, 5, 6, 7, 8, 9, 10);

        // Filter even numbers and assign to a variable
        List<Integer> evenNumbers = numbers.stream()
                                           .filter(n -> n % 2 == 0)
                                           .collect(Collectors.toList());
        // Print the even numbers
        evenNumbers.forEach(System.out::println);

        System.out.println("Find First element in the list start : ");
        int arrFirst[] = new int[]{9,10,15,8,49,25,98,32,10}; 
        int firstElement = Arrays.stream(arrFirst).findFirst().getAsInt();
        System.out.println("Find First element in the list End : "+firstElement);
        
	}

}
