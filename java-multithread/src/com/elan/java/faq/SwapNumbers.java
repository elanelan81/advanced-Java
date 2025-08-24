package com.elan.java.faq;

public class SwapNumbers {

    public static void main(String[] args) {
        int x = 10;
        int y = 5;
        System.out.println("Before swapping:"  + " x = " + x + ", y = " + y);
        x = x + y;
        y = x - y;
        x = x - y;
        System.out.println("After swapping:" + " x = " + x + ", y = " + y);
        
        int a = 10;
        int b = 20;

        System.out.println("Before swap: a = " + a + ", b = " + b);

        // Swap logic without using temp or inbuilt functions
        a = a + b; // a = 30
        b = a - b; // b = 10
        a = a - b; // a = 20

        System.out.println("After swap: a = " + a + ", b = " + b);
    }
}