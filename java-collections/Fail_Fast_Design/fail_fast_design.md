# Fail-Fast vs Fail-Safe Design

## Fail-Fast

Fail-fast is a design principle that emphasizes detecting and reporting errors as soon as they occur. The idea is to halt the system’s operation or throw an exception immediately upon detecting an error, preventing the error from propagating further.

**Purpose.** Fail-fast aims to minimize the impact of errors by stopping execution as soon as an issue is detected. This helps in identifying and fixing problems early in the development process, reducing the likelihood of hidden bugs causing more significant issues later on.
**Example.** Checking input parameters for validity at the beginning of a method and throwing an exception if they are incorrect is a common implementation of fail-fast.

### Advantages

- Early error detection: Fail-fast systems identify errors as soon as they occur, making it easier to diagnose and fix issues.
- Prevents cascading failures: By stopping execution upon detecting an error, fail-fast systems prevent further damage or incorrect behavior.

### Disadvantages

- Potential for disruption: Fail-fast systems can interrupt the normal flow of execution, leading to a less seamless user experience.
- May require additional error handling: When errors are detected early, the system needs mechanisms to handle and report them effectively.

### Example: Fail-Fast Iterator in Collections

```java
import java.util.HashMap;
import java.util.Map;

public class FailFastExample {

    public static void main(String[] args) {
        // Creating a map with some initial data
        Map<String, Integer> map = new HashMap<>();
        map.put("One", 1);
        map.put("Two", 2);

        // Iterating over the map and trying to modify it concurrently
        for (String key : map.keySet()) {
            if (key.equals("Two")) {
                // Modifying the map while iterating (fail-fast behavior)
                map.remove(key); // This will throw ConcurrentModificationException
            }
        }
    }
}
```

## Fail-Safe

Fail-safe is a foundational design principle in engineering and software architecture, focusing on the creation of systems that can continue to operate safely even in the presence of component failures or unforeseen errors. Fail-safe mechanisms prioritize system stability and resilience, ensuring that critical functions remain operational under adverse conditions.

**Purpose.** Fail-safe strategies aim to mitigate the impact of failures by implementing redundancy, backup systems, or graceful degradation mechanisms. These measures safeguard against catastrophic system failures, maintaining essential functionalities and preventing disruptions to services or operations.
**Example.** Employing redundant hardware components, fault-tolerant algorithms, or failover systems to seamlessly transition operations to backup resources in the event of primary system failures demonstrates the fail-safe principle’s application.

### Advantages

- Safety and reliability: Fail-safe systems prioritize safety by ensuring that failures do not lead to dangerous situations.
- Graceful degradation: The system can continue to operate at a reduced capacity, minimizing disruptions.

### Disadvantages

- Complex design: Implementing fail-safe mechanisms can add complexity to the system, increasing development and maintenance costs.
- Performance impact: Fail-safe systems may sacrifice performance in favor of safety and reliability.

### Example: Fail-Safe Queue (BlockingQueue)

```java
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;

public class FailSafeQueueExample {
    public static void main(String[] args) {
        // Creating a blocking queue with a capacity of 2
        BlockingQueue<Integer> queue = new ArrayBlockingQueue<>(2);

        // Adding elements to the queue
        queue.offer(1);
        queue.offer(2);

        // Trying to add a third element, which would normally block in a full queue.
        // In a fail-safe approach, this operation returns false instead of throwing an exception.
        boolean offered = queue.offer(3);
        System.out.println("Offer result: " + offered);

        // Printing the contents of the queue
        System.out.println("Elements in the queue: " + queue);
    }
}
```

### Example: Fail-Safe Iteration (ConcurrentHashMap)

```java
import java.util.concurrent.ConcurrentHashMap;

public class FailSafeMapExample {
    public static void main(String[] args) {
        ConcurrentHashMap<String, Integer> map = new ConcurrentHashMap<>();
        map.put("One", 1);
        map.put("Two", 2);

        // Iterating over the map using keySet while modifying the map.
        // The iterator is fail-safe (weakly consistent) and does not throw ConcurrentModificationException.
        for (String key : map.keySet()) {
            if (key.equals("Two")) {
                map.remove(key);
            }
        }

        System.out.println("Map after removal: " + map);
    }
}
```

# Java Collections – Advanced Notes

This document summarizes important concepts, internals, and interview-style questions around the Java Collections Framework.

---

## 1. Linear Collections (List): Arrays vs. Linked Lists

Lists are ordered collections that allow duplicate elements. The two primary implementations, `ArrayList` and `LinkedList`, offer a classic trade-off between fast access and efficient modification.

### 1.1 ArrayList (Dynamic Array)

- **Underlying structure**: Backed by a dynamic array (`Object[] elementData`). Modifications are tracked using a `modCount` field, which underpins the **fail-fast** behavior of iterators.
- **Expansion strategy**: When the array runs out of space, `ArrayList` grows automatically. The new capacity is typically **1.5x the old capacity**, implemented via `Arrays.copyOf`, which creates a larger array and copies elements.
- **Performance**:
  - Random access (`get(int index)`): O(1) – direct index access.
  - Append (`add(E e)`): Amortized O(1), but O(n) when resizing occurs.
  - Insert/delete in the middle: O(n) due to array element shifting.

### 1.2 LinkedList (Doubly Linked List)

- **Underlying structure**: Composed of `Node<E>` objects, each holding `item`, `prev`, and `next` references. Maintains `first` and `last` pointers for efficient boundary operations.
- **Performance**:
  - Insert/delete at head or tail: O(1).
  - Insert/delete in the middle: O(n) to traverse to the position.
  - Random access (`get(int index)`): O(n), as it must traverse from one end.
- **Best for**: Frequent insertions/deletions at arbitrary positions and queue/deque-like usage.

### 1.3 ArrayList vs. LinkedList – When to Use Which?

- **ArrayList**:
  - Prefer when you need fast random access and the size is relatively stable.
  - Great for read-heavy scenarios (e.g., report generation, lookups).
- **LinkedList**:
  - Prefer when you have many insertions/deletions, especially near the ends.
  - Useful as a `Queue`/`Deque` implementation.

---

## 2. Sets: Enforcing Uniqueness and Optional Ordering

Sets do not allow duplicate elements. Implementation details determine ordering behavior.

### 2.1 HashSet (Hash Table)

- **Underlying structure**: Backed by a `HashMap`. Elements are stored as keys; the values are a constant dummy object.
- **Uniqueness**: Enforced via `hashCode()` and `equals()` on keys.
- **Order**: Not guaranteed; depends on hash distribution.

### 2.2 TreeSet (Sorted Set)

- **Underlying structure**: Backed by a `TreeMap`, which uses a red-black tree (self-balancing BST).
- **Order**: Maintains elements in sorted order via natural ordering (`Comparable`) or a custom `Comparator`.
- **Uniqueness**: No duplicates; enforced by tree properties.
- **Performance**: `add`, `remove`, and `contains` are **O(log n)**.

---

## 3. Maps – Key–Value Pairs

Maps store data as key–value pairs; keys must be unique.

### 3.1 HashMap

- **Underlying structure (JDK 8+)**: An array of buckets (`Node<K,V>[] table`). Each bucket holds a linked list or a red-black tree of entries.
- **How `put(K key, V value)` works**:
  1. Compute a hash from `key.hashCode()` (possibly mixing bits).
  2. Determine the bucket index using `(n - 1) & hash`, where `n` is the table length.
  3. If the bucket is empty, insert the new node.
  4. If not, traverse the bucket:
     - If an entry with the same key exists, update its value.
     - Otherwise, append the new node to the list.
  5. If a bucket’s list becomes too long (≥ 8) and the table is large enough (≥ 64), convert the list into a red-black tree to improve worst-case lookup from O(n) to O(log n).
- **Thread-safety**: **Not thread-safe**. Use `ConcurrentHashMap` in concurrent environments instead of manual synchronization on `HashMap`.

### 3.2 TreeMap

- **Underlying structure**: **Red-black tree**.
- **Order**: Entries are sorted by key (natural or via `Comparator`).
- **Performance**: `get`, `put`, and `remove` are **O(log n)**.
- **Extras**: Supports range views like `subMap`, `headMap`, `tailMap` efficiently.

### 3.3 ConcurrentHashMap

- **JDK 7**: Used segmented locks (map divided into segments with separate locks).
- **JDK 8+**: Moved to bucket-level synchronization and CAS-based operations:
  - Uses `synchronized` on individual buckets for writes.
  - Uses **CAS (Compare-And-Swap)** and `volatile` for non-blocking reads.
- **Benefit**: Much finer-grained locking and improved concurrency; multiple threads can operate on different buckets with minimal contention.

---

## 4. Queues: FIFO and Priority Access

Queues are used to hold elements prior to processing.

### 4.1 LinkedList as Queue/Deque

Because `LinkedList` implements `Deque`, it works well as a FIFO queue:

- `offer()` (add to tail): O(1).
- `poll()` (remove from head): O(1).
- Also supports stack-like operations (`push`, `pop`).

### 4.2 PriorityQueue

- **Underlying structure**: **Binary heap** (min-heap by default).
- **Behavior**: The head is always the smallest element according to natural ordering or a `Comparator`.
- **Performance**: `offer` and `poll` are **O(log n)** due to heap sift-up/sift-down operations.
- **Best for**: Task scheduling, top-N problems, and any scenario where you need efficient access to the “next” most important element.

---

## 5. Important Interview Questions (with Short Answers)

**Q1: ArrayList vs. LinkedList – when to use which?**

- Use **ArrayList** when you need fast random reads and relatively infrequent insertions/deletions in the middle.
- Use **LinkedList** when you have frequent insertions/deletions, especially at the ends, and random access performance is less critical.

**Q2: HashMap vs. Hashtable – what’s the difference?**

- `HashMap` is **not synchronized**, allows one `null` key and multiple `null` values, and is generally faster.
- `Hashtable` is **synchronized** (thread-safe but with coarse-grained locking), does **not** allow `null` keys or values, and is considered legacy.
- For modern concurrent code, prefer `ConcurrentHashMap` instead of `Hashtable`.

**Q3: Why did ConcurrentHashMap abandon segment locks in JDK 8?**

- The segment lock in JDK 1.7 had a fixed concurrency level (16 by default), which was a bottleneck.
- The JDK 1.8 approach of using CAS and synchronized on individual hash buckets provides much **finer-grained locking**. This dramatically improves concurrency, as different threads can operate on different buckets simultaneously without contention.

---

## 6. Summary: Choosing the Right Data Structure

Selecting the right collection is a balance between **functional needs**, **performance characteristics**, and **engineering best practices**.

### 6.1 Functional Needs

- **Order**: Need sorting? Use `TreeSet` or `TreeMap`.
- **Uniqueness**: Need to prevent duplicates? Use a `Set` (`HashSet`, `LinkedHashSet`, `TreeSet`).
- **Concurrency**: Need thread-safety with good performance? Use `ConcurrentHashMap` or other concurrent collections.

### 6.2 Performance Characteristics

- **Access**: For fast random access, `ArrayList` is ideal (O(1)).
- **Modification**: For frequent insertions/deletions, especially near the ends, `LinkedList` excels.
- **Search**: `HashMap` gives amortized O(1) lookups; `TreeMap` offers O(log n) with sorted order.

### 6.3 Engineering Best Practices

- **Initialize capacity**: If you know the approximate number of elements, initialize your `ArrayList` or `HashMap` with a specific capacity to avoid costly resizing, e.g. `List<String> list = new ArrayList<>(1000);`.
- **Program to interfaces**: Declare variables using interface types (`List`, `Map`, `Set`) rather than implementation types (`ArrayList`, `HashMap`) to keep code flexible.
- **Beware of fail-fast behavior**: Modifying a collection while iterating with a standard iterator can throw `ConcurrentModificationException`. Use iterator methods (`remove`) or concurrent collections where appropriate.
