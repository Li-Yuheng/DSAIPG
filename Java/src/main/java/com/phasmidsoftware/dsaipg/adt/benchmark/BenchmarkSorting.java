package com.phasmidsoftware.dsaipg.adt.benchmark;

import com.phasmidsoftware.dsaipg.sort.elementary.InsertionSortComparator;
import com.phasmidsoftware.dsaipg.util.Benchmark_Timer;

import java.util.Random;

public class BenchmarkSorting {
    public static void main(String[] args) {
        int[] sizes = {1000, 2000, 4000, 8000, 16000};
        int warmupRuns = 10;
        int benchmarkRuns = 20;

        for (int n : sizes) {
            Integer[] randomArray = generateRandomArray(n);
            Integer[] orderedArray = generateOrderedArray(n);
            Integer[] partiallyOrderedArray = generatePartiallyOrderedArray(n);
            Integer[] reverseOrderedArray = generateReverseOrderedArray(n);

            runBenchmark("Random Order", randomArray, n, warmupRuns, benchmarkRuns);
            runBenchmark("Ordered", orderedArray, n, warmupRuns, benchmarkRuns);
            runBenchmark("Partially Ordered", partiallyOrderedArray, n, warmupRuns, benchmarkRuns);
            runBenchmark("Reverse Ordered", reverseOrderedArray, n, warmupRuns, benchmarkRuns);
            System.out.println();
        }
    }

    private static void runBenchmark(String description, Integer[] array, int n, int warmupRuns, int benchmarkRuns) {
        Benchmark_Timer<Integer[]> benchmark = new Benchmark_Timer<>(
                "Insertion Sort - " + description,
                arr -> InsertionSortComparator.sort(arr.clone())
        );

        double time = benchmark.runFromSupplier(() -> array.clone(), benchmarkRuns);
        System.out.printf("%-20s (n=%d): %.5f ms\n", description, n, time);
    }

    private static Integer[] generateRandomArray(int n) {
        Random rand = new Random();
        Integer[] array = new Integer[n];
        for (int i = 0; i < n; i++) {
            array[i] = rand.nextInt(n * 10);
        }
        return array;
    }

    private static Integer[] generateOrderedArray(int n) {
        Integer[] array = new Integer[n];
        for (int i = 0; i < n; i++) {
            array[i] = i;
        }
        return array;
    }

    private static Integer[] generatePartiallyOrderedArray(int n) {
        Integer[] array = generateOrderedArray(n);
        Random rand = new Random();
        for (int i = 0; i < n / 10; i++) {
            int index1 = rand.nextInt(n);
            int index2 = rand.nextInt(n);
            int temp = array[index1];
            array[index1] = array[index2];
            array[index2] = temp;
        }
        return array;
    }

    private static Integer[] generateReverseOrderedArray(int n) {
        Integer[] array = new Integer[n];
        for (int i = 0; i < n; i++) {
            array[i] = n - i;
        }
        return array;
    }
}
