package com.elan.java.faq;

public class PrimeNo {
    public static void main(String[] args) {
        int first = 10;
        int second = 50;
        for (int i = first; i < second; i++) {
            boolean value = isPrime(i);
            if (value) System.out.println("Prime no::" + i);

        }
        
        
        int start = 10;
        int end = 50;

        System.out.println("Prime numbers between " + start + " and " + end + " are:");
        for (int i = start; i <= end; i++) {
            if (isPrime1(i)) {
                System.out.print(i + " ");
            }
        }

    }

    private static boolean isPrime(int num) {
        if (num <= 1) return false;
        for (int i = 2; i < num; i++) {
            if (num % i == 0) return false;
        }

        return true;
    }
    
    public static boolean isPrime1(int num) {
        if (num <= 1) return false;
        if (num == 2) return true;

        for (int i = 2; i <= Math.sqrt(num); i++) {
            if (num % i == 0)
                return false;
        }
        return true;
    }
}
