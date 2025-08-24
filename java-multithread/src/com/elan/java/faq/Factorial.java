package com.elan.java.faq;

public class Factorial {

    // Recursion way
    static int fact(int num) {
        if (num < 0) return -1;
        if (num == 0) return 1;
        else
            return (num * fact(num - 1));

    }

    public static void main(String[] args) {
        //Iterative way
        int fact = 1;
        int num = 5;
        if (num < 0) System.out.println("Factorial cant be calculated for  negative no");
        if (num == 0) System.out.println("Factorial of 0 is::" + 1);
        for (int i = 1; i <= num; i++) {
            fact = fact * i;
        }
        System.out.println("Factorial of " + num + " using iterative way " 
        + fact);

        System.out.println("Factorial of " + num + " using recursive way " 
        + fact(num));
        
        int number = 5;
        
        long result = factorialRecursive(number);
        System.out.println("Factorial of factorialRecursive " + number + " is: " + result);
        
        long result1 = factorialIterative(number);
        System.out.println("Factorial of factorialIterative " + number + " is: " + result1);
    }
    
    public static long factorialRecursive(int n) {
        if (n == 0 || n == 1)
            return 1;
        return n * factorialRecursive(n - 1);
    }
    public static long factorialIterative(int n) {
        long result = 1;
        for (int i = 2; i <= n; i++) {
            result *= i;
        }
        return result;
    }
}
