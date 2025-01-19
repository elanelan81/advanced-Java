package com.sample.multithread;


/*
 * A daemon thread is a low-priority thread in Java that runs in the background to perform tasks like garbage collection,
 *  monitoring, or housekeeping. It does not prevent the JVM from exiting when all user threads finish their execution.

Key Points:
Created by setting the setDaemon(true) method before calling start().
Automatically terminates when all user threads finish.

The daemon thread keeps running as long as the main thread is alive, but it stops automatically once the main thread finishes.



 */
public class DaemonExample {

	public static void main(String[] args) {
		Thread daemonThread = new Thread( () -> {
			 while(true) {
				 System.out.println("Daemon Thread is running");
				 try {
					 Thread.sleep(50);
					 System.out.println("Daemon Thread is after sleep");
				 }catch(InterruptedException e) {
					 System.out.println("Daemon Thread is after sleep");
					 e.printStackTrace();
					 
				 }
			 }
		});
		daemonThread.setDaemon(true);
		daemonThread.start();
		System.out.println("Main Thread Finished");

	}

}
