package com.phasmidsoftware.dsaipg.sort.par;

import java.util.Arrays;
import java.util.concurrent.CompletableFuture;

final class ParSort1 {

    public static int cutoff = 1000;
    public static int maxDepth = 4;


    public static void sort(int[] array, int from, int to) {
        sort(array, from, to, 0);
    }


    private static void sort(int[] array, int from, int to, int depth) {
        if (to - from <= cutoff || depth >= maxDepth) {
            Arrays.sort(array, from, to);
        } else {
            int mid = (from + to) / 2;
            CompletableFuture<int[]> cf1 = asyncSort(array, from, mid, depth + 1);
            CompletableFuture<int[]> cf2 = asyncSort(array, mid, to, depth + 1);
            CompletableFuture<int[]> cf = cf1.thenCombine(cf2, ParSort::doMerge);
            cf.whenComplete((result, throwable) -> System.arraycopy(result, 0, array, from, result.length));
            cf.join();
        }
    }


    static CompletableFuture<int[]> asyncSort(int[] array, int from, int to) {
        return CompletableFuture.supplyAsync(() -> sortRecursive(array, from, to));
    }


    private static CompletableFuture<int[]> asyncSort(int[] array, int from, int to, int depth) {
        return CompletableFuture.supplyAsync(() -> {
            if (to - from <= cutoff || depth >= maxDepth) {
                return sortRecursive(array, from, to);
            } else {
                int mid = (from + to) / 2;
                CompletableFuture<int[]> cf1 = asyncSort(array, from, mid, depth + 1);
                CompletableFuture<int[]> cf2 = asyncSort(array, mid, to, depth + 1);
                return cf1.thenCombine(cf2, ParSort::doMerge).join();
            }
        });
    }


    static int[] sortRecursive(int[] array, int from, int to) {
        int[] result = new int[to - from];
        System.arraycopy(array, from, result, 0, result.length);
        Arrays.sort(result);
        return result;
    }


    static int[] doMerge(int[] xs1, int[] xs2) {
        int[] result = new int[xs1.length + xs2.length];
        int i = 0, j = 0;
        for (int k = 0; k < result.length; k++) {
            if (i >= xs1.length) result[k] = xs2[j++];
            else if (j >= xs2.length) result[k] = xs1[i++];
            else if (xs2[j] < xs1[i]) result[k] = xs2[j++];
            else result[k] = xs1[i++];
        }
        return result;
    }
}
