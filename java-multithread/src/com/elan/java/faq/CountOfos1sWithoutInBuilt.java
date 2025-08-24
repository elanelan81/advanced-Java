package com.elan.java.faq;

import java.util.Arrays;

public class CountOfos1sWithoutInBuilt {
    public static void main(String[] args) {
        int[] arr = new int[]{1, 1, 0, 1, 0, 1, 1, 0};
        int sum = 0;
        for (int i : arr) {
            sum += i;
        }
        System.out.println("count of 1's::" + sum);
        System.out.println("count of 0's::" + (arr.length - sum));
        
        int[] arrStream = {1, 1, 0, 1, 0, 1, 1, 0};

        
        
        long countOnes = Arrays.stream(arrStream)
                               .filter(i -> i == 1)
                               .count();

        long countZeros = arr.length - countOnes;

        System.out.println("Count of 1's: " + countOnes);
        System.out.println("Count of 0's: " + countZeros);
    }
}
