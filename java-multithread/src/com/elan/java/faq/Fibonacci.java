package com.elan.java.faq;

public class Fibonacci {
    //recursion
    public static int fibonaci(int num) {
        if (num < 0) return 0;
        if (num == 1 || num == 2) return 1;
        else
            return fibonaci(num - 2) + fibonaci(num - 1);
    }

    public static void main(String[] args) {

        //iterative way
        int num = 5;
        int n1;
        int n2 = 0, n3 = 1;
        System.out.println("Fibonaci series using iteration::");
        for (int i = 0; i < num; i++) {
            n1 = n2;
            n2 = n3;
            n3 = n1 + n2;
            System.out.print(n1 + " ");
        }
        System.out.println("\nFibonaci series using recursion::");
        for (int i = 0; i < num; i++) {
            System.out.print(fibonaci(i) + " ");
        }
        
        //
        int n = 10; // position in Fibonacci sequence
        System.out.println("Fibonacci number at position " + n + " is " + FibonacciRecursive(n));
        
        
        System.out.println("Fibonacci number at position " + n + " is " + FibonacciIterative(n));
    }
    public static long FibonacciRecursive(int n) {
        if (n <= 1) return n;
        return FibonacciRecursive(n - 1) + FibonacciRecursive(n - 2);
    }
    
    public static long FibonacciIterative(int n) {
        if (n <= 1) return n;

        long a = 0, b = 1;
        long fib = 1;

        for (int i = 2; i <= n; i++) {
            fib = a + b;
            a = b;
            b = fib;
        }

        return fib;
    }
}
