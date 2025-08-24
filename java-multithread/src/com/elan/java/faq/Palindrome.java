package com.elan.java.faq;

public class Palindrome {
    public static void main(String[] args) {
        String original = "mom";
        String reverse = "";
        for (int i = original.length() - 1; i >= 0; i--) {
            reverse += original.charAt(i);
        }
        System.out.println("palindrome or not::" + reverse.equals(original));
        
        
        String str = "madam";
        System.out.println(str + " is palindrome? " + palindromeRecursive(str, 0, str.length() - 1));
        
        System.out.println(str + " is palindrome? " + palindromeIterative(str));
    }
    
    public static boolean palindromeIterative(String str) {
        int left = 0, right = str.length() - 1;

        while (left < right) {
            if (str.charAt(left) != str.charAt(right)) {
                return false;
            }
            left++;
            right--;
        }

        return true;
    }
    
    public static boolean palindromeRecursive(String str, int left, int right) {
        if (left >= right) {
            return true;
        }
        if (str.charAt(left) != str.charAt(right)) {
            return false;
        }
        return palindromeRecursive(str, left + 1, right - 1);
    }
}
