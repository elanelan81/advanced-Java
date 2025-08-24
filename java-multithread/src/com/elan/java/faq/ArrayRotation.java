package com.elan.java.faq;

import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class ArrayRotation {
    public static void main(String[] args) {
        List<Integer> array = Arrays.asList(1, 2, 3, 4, 5, 6, 7);
        int k = 10; // Number of positions to rotate

        System.out.println("Original Array: " + array);

        List<Integer> clockwiseRotated = rotateClockwise(array, k);
        System.out.println("Clockwise Rotated by " + k + ": " + clockwiseRotated);

        List<Integer> antiClockwiseRotated = rotateAnticlockwise(array, k);
        System.out.println("Anticlockwise Rotated by " + k + ": " + antiClockwiseRotated);
    }

    // Rotate clockwise using streams
    public static List<Integer> rotateClockwise(List<Integer> list, int k) {
        int size = list.size();
        k = k % size; // Handle overflow
        int temp = size - k; 
        System.out.println("K rotate: "+k+"  value - "+temp);
        System.out.println("K skip: "+list.stream().skip(size - k).count());
        System.out.println("K limit: "+list.stream().limit(size - k).count());
        
        return Stream.concat(list.stream().skip(size - k), list.stream().limit(size - k))
                     .collect(Collectors.toList());
    }

    // Rotate anticlockwise using streams
    public static List<Integer> rotateAnticlockwise(List<Integer> list, int k) {
        int size = list.size();
        k = k % size; // Handle overflow
        return Stream.concat(list.stream().skip(k), list.stream().limit(k))
                     .collect(Collectors.toList());
    }
}
