package com.sample.multithread;

class MyThread extends Thread {
	@Override
	public void run() {
		System.out.println("Thread is running: " + Thread.currentThread().getName());
	}
}

public class ThreadExample1 {

	public static void main(String[] args) {
		MyThread thread1 = new MyThread();
        thread1.start(); // Start the thread

        MyThread thread2 = new MyThread();
        thread2.start(); // Start another thread

	}
/*
 * The Thread class in Java represents a thread of execution.
 * You can create a thread by extending the Thread class and overriding its run() method.

Steps:

Create a class that extends the Thread class.
Override the run() method, which contains the code that the thread will execute.
Create an object of the subclass and call the start() method to begin the thread execution.

Key Points:

start() method creates a new thread and invokes the run() method in that thread.
Extending Thread is simple but has limitations because Java doesn't support multiple inheritance.
 This means you cannot extend another class if you already extend Thread.
	 */

}
