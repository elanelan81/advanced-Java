package com.elan.java.faq;

public class ReverseANumber {
    public static void main(String[] args) {
        int num=123;
        int reverse = 0;
        System.out.println("Before Reversal of a number::"+ num);
        while (num !=0) {
            int remainder = num % 10;
            reverse = reverse * 10 + remainder;
            num = num / 10;
        }
        System.out.println("After Reversal of a number::"+ reverse);
    }
}
