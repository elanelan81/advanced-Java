package com.elan.java.faq;

import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

public class FindPairsOfGivenSum {
    public static void main(String[] args) {
        int[] nums = new int[]{15, 12, 4, 16, 9, 8, 24, 0};
        int sum = 24;
        Map<Integer, Integer> map = new HashMap<>();
        for (int i = 0; i < nums.length - 1; i++) {
            if (map.containsKey(sum - nums[i])) {
                System.out.println("pair found::" + (sum - nums[i]) + " " + nums[i]);
            }
            map.put(nums[i], i);
        }
        
       
        int[] arr = {2, 4, 3, 5, 7, -1, 8, 9};
        int targetSum = 7;

        findPairs(arr, targetSum);

    }
    public static void findPairs(int[] arr, int targetSum) {
        Set<Integer> seen = new HashSet<>();
        Set<String> output = new HashSet<>(); // To avoid duplicate pairs

        Arrays.stream(arr)
              .forEach(n -> {
                  int complement = targetSum - n;
                  if (seen.contains(complement)) {
                      // Sort pair elements to avoid duplicates like (2,5) and (5,2)
                      int first = Math.min(n, complement);
                      int second = Math.max(n, complement);
                      output.add("(" + first + ", " + second + ")");
                  }
                  seen.add(n);
              });

        output.forEach(System.out::println);
    }

}
