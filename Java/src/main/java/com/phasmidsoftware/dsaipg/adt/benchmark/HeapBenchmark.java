package com.phasmidsoftware.dsaipg.adt.benchmark;

import com.phasmidsoftware.dsaipg.adt.pq.*;
import com.phasmidsoftware.dsaipg.adt.pq.PriorityQueue;
import com.phasmidsoftware.dsaipg.util.benchmark.Benchmark_Timer;

import java.util.*;

public class HeapBenchmark {
    private static final int M = 4095; // Max heap size
    private static final int INSERTIONS = 16000;
    private static final int REMOVALS = 4000;
    private static final List<Integer> testData = new ArrayList<>();

    static {
        Random rand = new Random(42); // Fixed seed for consistency
        for (int i = 0; i < INSERTIONS; i++) {
            testData.add(rand.nextInt(100000));
        }
    }

    public static void main(String[] args) {
        Comparator<Integer> comparator = Integer::compare;

        benchmarkBinaryHeap("Binary Heap", false, comparator);
        benchmarkBinaryHeap("Binary Heap (Floyd's Trick)", true, comparator);
        benchmarkFourAryHeap("4-Ary Heap", false, comparator);
        benchmarkFourAryHeap("4-Ary Heap (Floyd's Trick)", true, comparator);
        benchmarkFibonacciHeap("Fibonacci Heap", comparator);

//        Collections.sort(testData, Collections.reverseOrder());
//        System.out.println(testData);

    }

    private static void benchmarkBinaryHeap(String name, boolean floyd, Comparator<Integer> comparator) {
        Benchmark_Timer<PriorityQueue<Integer>> timer = new Benchmark_Timer<>(
                name,
                heap -> {
                    for (int num : testData) {
                        heap.give(num);
                    }
//                    System.out.println(heap.getOverflowElements());

                    int highestPriority = Integer.MIN_VALUE;
                    for (int i = 0; i < REMOVALS; i++) {
                        if (!heap.isEmpty()) {
                            int removed = 0;
                            try {
                                removed = heap.take();
                            } catch (PQException e) {
                                throw new RuntimeException(e);
                            }
                            highestPriority = Math.max(highestPriority, removed);
                        }
                    }
                    //System.out.println(name + " highest priority spilled: " + highestPriority+"; highest priority overflowed:"+heap.getHighestOverflowElement());
                }
        );

        double time = timer.runFromSupplier(() -> new PriorityQueue_BinaryHeap<>(M, true, comparator, floyd), 5);
        System.out.println(name + " average time: " + time + " ms");
    }

    private static void benchmarkFourAryHeap(String name, boolean floyd, Comparator<Integer> comparator) {
        Benchmark_Timer<FourAryHeap<Integer>> timer = new Benchmark_Timer<>(
                name,
                heap -> {
                    for (int num : testData) {
                        heap.give(num);
                    }
 //                   System.out.println(heap.getOverflowElements());

                    int highestPriority = Integer.MIN_VALUE;
                    for (int i = 0; i < REMOVALS; i++) {
                        if (!heap.isEmpty()) {
                            int removed = heap.take();
                            highestPriority = Math.max(highestPriority, removed);
                        }
                    }
                    System.out.println(name + " highest priority spilled: " + highestPriority+"; highest priority overflowed:"+heap.getHighestOverflowElement());
                }
        );

        double time = timer.runFromSupplier(() -> new FourAryHeap<>(M, true, comparator, floyd), 5);
        System.out.println(name + " average time: " + time + " ms");
    }

    private static void benchmarkFibonacciHeap(String name, Comparator<Integer> comparator) {
        Benchmark_Timer<FibonacciHeap<Integer>> timer = new Benchmark_Timer<>(
                name,
                heap -> {
                    for (int num : testData) {
                        heap.insert(num);
                    }
 //                   System.out.println(heap.getOverflowElements());

                    int highestPriority = Integer.MIN_VALUE;
                    for (int i = 0; i < REMOVALS; i++) {
                        if (!heap.isEmpty()) {
                            int removed = heap.deleteMin();
                            highestPriority = Math.max(highestPriority, removed);
                        }
                    }
                    System.out.println(name + " highest priority spilled: " + highestPriority+"; highest priority overflowed:"+heap.getHighestOverflowElement());
                }
        );

        double time = timer.runFromSupplier(() -> new FibonacciHeap<>(true, comparator,M), 5);
        System.out.println(name + " average time: " + time + " ms");
    }

}
