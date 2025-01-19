package com.sample.multithread;

/*
 * Overview: Instead of extending the Thread class, you can implement the Runnable interface, 
 * which provides more flexibility as your class can extend another class while still implementing Runnable.

Steps:

Create a class that implements the Runnable interface.
Override the run() method.
Create an object of the class and pass it to the Thread class constructor.
Call the start() method on the Thread object to begin execution.

Key Points:

This approach is preferred when you want your class to extend another class.
Runnable makes your code more reusable and flexible.
Recommended for situations where tasks need to be shared among multiple threads.

 */
public class RunnableInterfaceSample {

	public static void main(String[] args) {
		RunnableSample runnableThread1 = new RunnableSample();
		Thread thread1 = new Thread(runnableThread1);
		thread1.start();
		
		Thread thread2 = new Thread(new RunnableSample());
		thread2.start();
		

	}

}

class RunnableSample implements Runnable {

	@Override
	public void run() {
		System.out.println("Thread is running: " + Thread.currentThread().getName());
	}
}