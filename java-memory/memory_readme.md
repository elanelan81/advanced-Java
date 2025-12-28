**Java Memory**
What is the JMM (in plain English)
Jai: In one line: what is the Java Memory Model?

Joe: It’s the rulebook for how threads see each other’s reads and writes. It tells us when a write in one thread is guaranteed to be visible in another, and what reorderings the JVM/CPU are allowed to do.

Jai: And “happens-before”?

Joe: If A happens-before B, B must see A’s effects and treat A as if it ran earlier. Think of it as a visibility-and-ordering promise.

What guarantees do you get?
Jai: What does volatile actually give me?

Joe: Fresh reads and ordering. A volatile write makes earlier writes visible; a volatile read sees the latest write. But it doesn’t make multi-step operations atomic.

Jai: Are 64-bit fields special?

Joe: Non-volatile long/double are not guaranteed atomic by the spec. To be safe and portable, use volatile or atomics if you need atomicity.

Jai: What’s special about final fields?

Joe: If an object is constructed correctly and published safely, other threads will see its final fields initialized. That’s why immutable objects are so reliable across threads.

Happens-before in one picture
Press enter or click to view image in full size

Tools that create order and visibility
Jai: What are the main tools to create happens-before edges?

Joe: Three everyday ones:

synchronized/locks: unlock happens-before a later lock on the same monitor.
volatile: write happens-before a later read of the same variable.
Lifecycle edges: Thread.start() and Thread.join(), task completion and Future.get().
Jai: So where does println or sleep fit?

Joe: They don’t create happens-before. Don’t rely on them for visibility.

Real-life patterns you actually use
1) Configuration Reloader (Visibility Only)
public final class ConfigHolder {
    private static volatile Config current = new Config("v1");

public static Config get() { return current; }
    // Hot-swap from a watcher thread
    public static void reload(Config newConfig) {
        current = newConfig; // volatile write: safely publishes
    }
}
Usage: Readers call ConfigHolder.get() without locks; the volatile write publishes the new instance. Keep Config immutable.

2) Broken Double-Checked Locking (DCL)
class BrokenSingleton {
    private static BrokenSingleton instance; // NOT volatile
    private BrokenSingleton() {}

static BrokenSingleton get() {
        if (instance == null) {
            synchronized (BrokenSingleton.class) {
                if (instance == null) {
                    instance = new BrokenSingleton(); // reordering risk
                }
            }
        }
        return instance; // could see a partially constructed object
    }
}
Fix by making instance volatile:

class SafeSingleton {
    private static volatile SafeSingleton instance;
    private SafeSingleton() {}
    static SafeSingleton get() {
        SafeSingleton local = instance;
        if (local == null) {
            synchronized (SafeSingleton.class) {
                if ((local = instance) == null) {
                    local = instance = new SafeSingleton();
                }
            }
        }
        return local;
    }
}
Why DCL needs volatile
Press enter or click to view image in full size

3) volatile is not atomic for compound ops
class Counter {
    private volatile int count;
    void increment() { count++; } // read-modify-write race
}
Fix with atomics:

import java.util.concurrent.atomic.AtomicInteger;
class AtomicCounter {
    private final AtomicInteger count = new AtomicInteger();
    void increment() { count.incrementAndGet(); }
}
4) Safe Publication via Future
var executor = java.util.concurrent.Executors.newSingleThreadExecutor();
var future = executor.submit(() -> {
    return new ExpensiveImmutable("ready");
});
var obj = future.get(); // completion happens-before get() returns
5) Stop flag for graceful shutdown
class Workers {
    private static volatile boolean running = true; // visibility flag
    static void stop() { running = false; }
    static void workLoop() {
        while (running) {
            // do work; periodically checks 'running'
        }
    }
}
Jai: Is this enough if workers also read shared, non-volatile config?

Joe: Use immutable snapshots for config (replace the whole object via a volatile or atomic reference). The flag alone doesn’t make other fields consistent.

6) Immutable snapshot cache/map
import java.util.Map;
import java.util.concurrent.atomic.AtomicReference;

class ProductCache {
    private static final AtomicReference<Map<String, Price>> snapshot = new AtomicReference<>(Map.of());
    static Map<String, Price> getAll() { return snapshot.get(); } // readers use immutable snapshot
    static void reload(Map<String, Price> fresh) { snapshot.set(Map.copyOf(fresh)); } // safe publication
}
Alternative: use ConcurrentHashMap for mutable, per-key updates, but avoid iteration + modification races.

5) Using VarHandles for Fine-Grained Memory Semantics
import java.lang.invoke.MethodHandles;
import java.lang.invoke.VarHandle;

class Channel {
    private volatile int state; // 0=init,1=open
    private static final VarHandle STATE;
    static {
        try {
            STATE = MethodHandles.lookup().findVarHandle(Channel.class, "state", int.class);
        } catch (ReflectiveOperationException e) { throw new ExceptionInInitializerError(e); }
    }
    void open() { STATE.setRelease(this, 1); }  // release semantics
    boolean isOpen() { return (int) STATE.getAcquire(this) == 1; } // acquire semantics
}
More JMM essentials (balanced depth)
Jai: If my program has no data races, what guarantee do I get?

Joe: Data-Race Freedom ⇒ Sequential Consistency (DRF-SC). If every shared access is ordered by happens-before (via locks, volatiles, etc.), your program behaves like a simple interleaving of operations that respects each thread’s order.

Jai: Do volatile writes have a global order?

Joe: Yes. Writes to a volatile variable form a single total order seen by all threads. That’s why message passing with a volatile flag works.

// Store-buffering example
// T1: x = 1; r1 = y;   T2: y = 1; r2 = x;
// Without HB, (r1=0, r2=0) is allowed; with volatile, it’s ruled out.
static volatile int x, y;
Jai: What about “out-of-thin-air” values?

Joe: Java forbids them: compilers/JITs cannot conjure values not causally produced by any execution that follows JMM rules.

Jai: Can class initialization help with safe publication?

Joe: Yes. Successful class initialization happens-before any use of that class.

final class HolderSingleton {
  private HolderSingleton() {}
  private static class Holder { static final HolderSingleton I = new HolderSingleton(); }
  static HolderSingleton get() { return Holder.I; }
}
Jai: How do wait/notify affect visibility?

Joe: They rely on synchronized. The releasing thread’s monitorexit happens-before a waiting thread’s monitorenter upon notification.

class Mailbox {
  private String msg;
  synchronized void put(String m) { msg = m; notifyAll(); }
  synchronized String take() throws InterruptedException {
    while (msg == null) wait();
    String m = msg; msg = null; return m;
  }
}
Jai: VarHandle/Atomic modes and fences — how to choose?

Joe: Use the weakest that’s correct:

acquire/release for message passing (getAcquire, setRelease)
volatile for strongest per-access ordering (getVolatile, setVolatile)
fences only for rare cross-location ordering (acquireFence, releaseFence, fullFence)
STATE.setRelease(this, 1);        // publish with release
java.lang.invoke.VarHandle.fullFence(); // prevent all reorders if truly needed
Jai: Arrays with volatile?

Joe: volatile int[] a makes the reference volatile, not each element. For per-element atomicity/visibility, use AtomicIntegerArray or a VarHandle to array elements.

Jai: Easy ways to get HB “for free”?

Joe: Use libraries that embed HB: BlockingQueue.put/take, Future.get()/CompletableFuture completion, ConcurrentHashMap.compute for compound actions.

Modern Java (9–25) in brief
VarHandles (JEP 193) give you explicit acquire/release/full-fence ops without Unsafe.
Future.get()/CompletableFuture completions add clean happens-before edges.
Virtual Threads change scheduling, not the JMM rules; visibility rules stay the same.
Practical Checklist
Prefer immutability. If mutable, define clear ownership or synchronization strategy.
For visibility-only control, use volatile on flags and immutable snapshots.
For compound actions, use atomics with CAS or locks; never rely on volatile alone.
Establish explicit publication edges (volatile write→read, lock release→acquire, task completion→get/join).
Be wary of subtle refactors that disable escape analysis or safe publication.
Interview Key Takeaways (Short)
Happens-before = visibility + ordering; it’s a contract, not a speed hint.
volatile is not atomic for multi-step operations; use atomics/locks.
Safe publication matters as much as thread safety; prefer immutable data.
Understand final-field semantics and why DCL needs volatile.
Modern tools: VarHandles for acquire/release/full fences where needed.
References (short list)
Java Language Specification, Chapter 17: Threads and Locks (JLS 17).
JSR-133 (Java Memory Model) FAQ — Pugh, Manson.
JEP 193: Variable Handles (VarHandle).
Note: Where the JLS permits but implementations rarely do (e.g., non-atomic 64-bit non-volatile accesses), write code that remains correct per spec:
use volatile or atomics if atomicity is required.



======

The Java Memory Model (JMM) explains how threads in Java communicate through memory to ensure data consistency and visibility. Understanding the JMM is key to writing efficient and correct concurrent programs in Java.

Java memory management is the process where the Java Virtual Machine (JVM) automatically handles memory allocation and cleanup. It uses a garbage collector to remove unused objects, so you don’t have to manage memory manually.

Java Memory Model
Java Memory Model (JMM) defines how threads in Java interact with memory in multithreaded programs and ensures that variables are visible across threads. It sets rules for reading and writing shared variables, helping to avoid issues like data races. One important concept is the “happens-before” relationship, which ensures that actions in the program happen in the correct order, making sure one thread sees the results of another thread’s work. The JMM balances performance with data consistency, allowing developers to understand and manage thread interactions in Java.

Key aspects of the Java Memory Model include:

Main Memory (Heap): All Java objects are stored in the heap, a shared memory accessible by all threads.
Thread Interaction: The JMM sets rules for how threads read and write shared variables to maintain data consistency.
Happens-Before Relationship: This rule guarantees that actions performed by one thread are visible to another if they are ordered properly, ensuring the correct sequence of actions.
Synchronization: The JMM supports synchronization tools like synchronized blocks and the volatile keyword to manage thread visibility and ordering.
Atomicity: Operations on volatile variables are atomic, meaning they are completed in one step without interruptions.
Volatile Keyword: Declaring a variable volatile makes sure that changes to it are immediately visible to all threads, preventing each thread from using its cached copy.
Memory Barriers: The JMM uses memory barriers to ensure that memory operations occur in the right order.
Thread-Local Variables: Variables declared inside a method are local to a thread and are not shared, avoiding visibility issues.
Cache Coherency: Modern CPUs ensure that changes made by one thread are visible to others, and the JMM defines when this happens.
Reordering: To optimize performance, the JMM allows instruction reordering by the compiler or processor so long as the program appears to execute in a consistent order when viewed by each thread.
Locks and Conditions: Java provides advanced synchronization tools like Lock and Condition interfaces to handle complex thread interactions.
Final Fields: Java guarantees that once a constructor completes, the final fields of an object are visible to all threads correctly.
Deadlocks: Understanding the JMM helps avoid deadlocks, which occur when threads wait indefinitely for resources held by each other.
Thread Safety: To achieve thread safety, developers must follow JMM guidelines when accessing shared data.
Press enter or click to view image in full size

JVM Memory Structure
The JVM uses different memory areas during program execution. Some are created by the JVM, and some are created by individual threads. The JVM-created memory is only removed when the JVM exits, while thread-specific memory is removed when the thread finishes. These areas include:

Heap Area
Method Area
JVM Stacks
Native Method Stacks
Program Counter (PC) Registers
1. Heap Area

The heap is a shared memory space where objects and arrays are stored. It is created when the JVM starts, and its size can be adjusted. When you create a new object using new, it’s placed in the heap, and its reference is stored in the stack.

Scanner sc = new Scanner(System.in);
Here, the Scanner object is in the heap, and the reference sc is in the stack.

2. Method Area

The method area is part of the heap, created when the JVM starts. It stores class-level information like:

Class structures (metadata)
Method bytecode
Static variables
Constant pool
Interfaces
It can have a fixed or dynamic size, depending on system configuration. Static variables in Java are stored here. However, garbage collection for the method area isn’t guaranteed and depends on the JVM.

import java.io.*;

class User {
  
    // Static variables are stored in the Method Area
    static String company = "SunilCorp";

    // Instance variables are stored in the Heap
    String name;

    public User(String name) {
        this.name = name;
    }

    public void display() {
        // Local variables are stored in the Stack
        int age = 25;

        System.out.println("Name: " + name);
        System.out.println("Age: " + age);
        System.out.println("Company: " + company);  // Accessing static variable from Method Area
    }
}

public class Main {
    public static void main(String[] args) {
        // Creating an instance of the User class (Heap)
        User user1 = new User("Rishi");

        // Calling the display method (Stack)
        user1.display();
    }
}
3. JVM Stacks

Each thread gets its own stack when it’s created. The stack stores:

Local variables (like those inside methods)
Method arguments
Return addresses
Information about the currently running method.
Each thread’s stack is separate, which ensures thread safety. The stack size can be either fixed or dynamic and is set when the thread is created.

The stack memory doesn’t need to be contiguous. Once a method finishes, its stack frame is removed automatically.

4. Native Method Stacks

Native method stacks (also called C stacks) handle native methods that interact with Java code (methods not written in Java).
Each thread gets its own native method stack when created.
The size can be fixed or dynamic.
These stacks are used for executing non-Java (native) code, such as C or C++ code.
5. Program Counter (PC) Registers

Each JVM thread has a Program Counter (PC) register.
For non-native methods, the PC stores the address of the current JVM instruction being executed.
For native methods, the PC value is undefined.
On some platforms, the PC may also store a return address or native pointer.
Garbage Collector in Java
The JVM runs a Garbage Collector (GC) in the background to automatically clean up unused objects and free heap memory. The GC reduces manual memory management, preventing memory leaks. Java 8 includes advanced garbage collection algorithms such as G1 GC, designed to minimize application pause times especially in large heaps.

It finds objects that aren’t being used and removes them to make space for new ones. You can request garbage collection by calling System.gc(), but the JVM ultimately decides the best time to run it.

Java 8 Features Affecting Memory and Concurrency
MetaSpace replacing PermGen: Offers more flexible memory allocation for class metadata.
Streams and Lambda Expressions: Facilitate functional-style programming and can help write cleaner concurrent code.
CompletableFuture: Provides a flexible way to write asynchronous and concurrent programs.
Improved Garbage Collection: Java 8 brought enhancements to garbage collection, improving performance and scalability.
Best Practices for Learning and Using the Java Memory Model
Use happens-before relationships to reason about when changes in one thread become visible to others.
Apply volatile to simple flags to ensure immediate visibility while avoiding full synchronization overhead.
Use synchronized or Lock to coordinate complex interactions and guarantee atomicity.
Prefer immutable objects or thread-local variables when possible to reduce synchronization needs.
Be mindful of instruction reordering; always use synchronization constructs to enforce order when necessary.
Avoid potential deadlocks by careful lock ordering and timeouts.
Monitor memory and threads using tools like VisualVM or Java Flight Recorder to analyze performance issues.
In this article, we examined the structure and behavior of the Java Memory Model, covering key memory areas such as the heap, stack, Metaspace, and native memory. We also explored how garbage collection works in Java, including the different types of collectors and their operation. Furthermore, we discussed how to monitor and tune memory usage effectively using JVM tools, as well as how to identify and resolve common memory issues like memory leaks and OutOfMemoryError. Understanding these memory types is important for ensuring the performance and stability of our applications.


====

Today’s Java apps work with tons of objects in memory, things like user logins, live data updates, or saved information to make things faster. Luckily, Java automatically cleans up the memory you’re not using anymore (garbage collection or GC). But there’s a problem: this cleanup costs something. It uses up processing power and can freeze your app for a split second. These tiny freezes add up and make your app slower while costing you more money in the cloud.

Not a Member -> Read here.

For example, if your GC throughput is 98%, it means your application is actively processing requests for 98% of the time, while the remaining 2% is spent in GC pauses. During the pause time, the application is essentially “frozen”-users can’t interact with it, but you’re still paying your cloud provider (AWS, GCP, etc.) for 100% of the compute time.

Now scale this across dozens or hundreds of containers, and suddenly you’re burning thousands of dollars on compute time where your app isn’t even live.

This is where JVM tuning comes in, not just for performance, but for cost efficiency and stability at scale.

The JVM offers several tuning flags to help control memory usage and performance. Let’s break down four of the most critical ones.

Press enter or click to view image in full size

Photo by Vardan Papikyan on Unsplash
Key JVM Flags for Memory Management
1. -Xmx (Maximum Heap Size)
It defines the maximum amount of heap memory the JVM is allowed to use. This controls the upper limit of how much memory your Java process can consume for object allocation.

On a 16GB server, it might give your small API service 4GB when it only needs 1GB. That’s 3GB of wasted money every month.

How to choose the right size:

Too small: Your app crashes with “OutOfMemoryError” during traffic spikes
Too big: You waste money on unused memory and get longer garbage collection pauses
Just right: Your app has room to grow without waste
Start here: Monitor your app’s actual memory usage for a few days, then set -Xmx to 150% of your peak usage

Example: -Xmx2g sets the max heap size to 2 GB.

2. -Xms (Initial Heap Size)
It sets the starting size of the heap when the JVM starts. By pre-allocating memory upfront, you can avoid dynamic heap resizing during runtime, which reduces GC activity and improves startup performance.

Tip: It’s often recommended to set -Xms equal to -Xmx to avoid resizing delays.

3. -Xss (Thread Stack Size)
It sets how much memory each thread gets for its stack (local variables, method calls, etc.). A lower value allows more threads, but risks stack overflows. A higher value supports deep recursion, but reduces the number of threads you can spawn.

Modern applications create lots of threads. If your web service handles 500 concurrent requests (500 threads) and each thread uses 1MB stack space, that’s 500MB just for thread overhead

Real impact: In a Kubernetes cluster running 100 containers, optimising thread stack size can save significant memory costs.

Example: -Xss512k gives each thread a 512 KB stack.

4. -Xlog:gc* or -XX:+PrintGCDetails (Garbage Collection Logging)
It controls garbage collection behaviour and creates logs, so you can see what’s happening.

# Modern logging (Java 9+)
java -Xlog:gc*:gc.log:time,tags MyApp

# Choose your garbage collector
java -XX:+UseG1GC MyApp         # Balanced performance
java -XX:+UseZGC MyApp           # Ultra-low pause times
java -XX:+UseParallelGC MyApp    # High throughput
Garbage collection pauses happen silently. Your users experience slow responses, but you don’t know why because you can’t see when or how long GC runs.

What GC logs reveal:

# Good performance
[gc] GC(23) Pause Young 256M->89M(1024M) 15ms

# Problem that needs fixing  
[gc] GC(24) Pause Young 512M->201M(1024M) 2300ms
The second log shows a 2.3-second pause- your users just waited over 2 seconds for a page to load.

Pair these flags based on your use case.

For example, for a high-throughput API service:

-Xms2g -Xmx2g -Xss1m -XX:+UseG1GC -Xlog:gc*:file=gc.log
This ensures consistent memory allocation, safe thread stack size, and full GC visibility.

JVM Profiling Tools
Tuning is only half the story. Sometimes the real problem lies not in the GC settings, but in how your application behaves.
That’s where profiling tools come in - they give you visibility into what your JVM is actually doing at runtime.

Here are the warning signs that tuning alone won’t solve your problems:

Memory keeps growing despite GC
Short GC pauses but terrible performance
Frequent OutOfMemoryErrors despite a large heap
These problems require detective work that JVM flags can’t provide. You need to see what’s actually happening inside your application.

Here’s a breakdown of some of the most important JVM profiling and diagnostic tools every Java engineer should know:

1. Java Flight Recorder (JFR) + JDK Mission Control (JMC)
What it is: A lightweight, production-ready profiler built into the JVM.

What it helps you do:

Record application events (GC, thread states, memory allocation, method profiling).
Identify hot methods, lock contention, and GC bottlenecks.
Visualise performance patterns over time.
Enable it via:-XX:StartFlightRecording=duration=5m,filename=recording.jfr

2. VisualVM
What it is: A visual profiler that comes with the JDK or can be downloaded separately.

What it helps you do:

Monitor heap and GC live.
Analyse CPU and memory usage.
Take heap dumps and thread dumps.
Identify memory leaks, high object allocations, and deadlocks.
Best for: Local dev environments and staging systems.

3. jstat (Java Statistics Monitoring Tool)
What it is: A lightweight CLI tool that prints real-time JVM statistics.

What it helps you do:

Monitor GC activity, heap/PermGen/Metaspace usage, and class loading without a GUI.
Command example: jstat -gc <pid> 1000

This shows GC data every second.

4. Eclipse MAT (Memory Analyzer Tool)
What it is: An advanced tool for analysing heap dumps and identifying memory leaks.

What it helps you do:

Find the largest objects in memory (dominators).
Generate leak suspect reports.
Best used when: You suspect a memory leak or unbounded object retention.

5. jstack
What it is: A CLI tool to capture thread dumps of a running JVM.

What it helps you do:

Detect deadlocks, thread contention, and blocked threads.
Understand thread states (RUNNABLE, BLOCKED, WAITING).
Command example : jstack <pid>

6. Third-Party Application Performance Monitoring (APM) Tools
Tools like New Relic, AppDynamics, or Datadog provide continuous profiling with minimal overhead.

Dynatrace - Smart alerts, GC/memory leaks detection, CPU hotspots

New Relic - Distributed tracing, throughput, and latency monitoring

AppDynamics - Transaction snapshots, heap analysis, database visibility

Prometheus + Grafana (with JVM Exporter) - Open-source dashboards showing heap, GC, CPU, threads, class loading

These are essential in microservices and containerized environments (K8s, ECS, etc.) for observability at scale.

Over the last four parts of this series, we’ve journeyed through the depths of Java memory management, from understanding the JVM’s memory layout, to demystifying garbage collection algorithms, to identifying memory leaks, and finally, to tuning and profiling like a pro.

Whether you’re managing a high-traffic e-commerce app, building microservices in Kubernetes, or optimising for serverless functions, these tuning techniques can help you deliver a smoother, faster, and more stable experience.

Many people think performance tuning is like magic, you try random things and hope something works. But it’s not that hard.

When you understand how Java works under the hood, know how to find problems, and use the right fixes, you can make your app run much better. It’s not just about tiny improvements, it’s about making your users happier, spending less money on servers, and building apps that work well for years.

This is the last part of my Java Memory Management Demystified series. We’ve covered everything from how Java stores data to how it cleans up memory, and how objects live and die. I hope this series helped you understand what’s really happening inside Java when your code runs.

If you missed the earlier parts of this series, here’s your chance to go back and build a strong foundation:

Part 1 — JVM Memory Layout Explained
Understand the Heap, Stack, Metaspace, and how memory is allocated behind the scenes.
Part 2 — Object Lifecycle & GC Roots
Dive into object allocation, reference types, and how the JVM determines what’s “garbage.”
Part 3 — GC Algorithms Deep Dive
Compare Serial, Parallel, CMS, G1, ZGC, and Shenandoah with use cases and tuning flags.