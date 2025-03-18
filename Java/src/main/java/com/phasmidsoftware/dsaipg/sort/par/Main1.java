package com.phasmidsoftware.dsaipg.sort.par;

import java.io.BufferedWriter;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.util.*;
import java.util.concurrent.ForkJoinPool;

public class Main1 {

    public static void main(String[] args) {
        System.out.println("Degree of parallelism: " + ForkJoinPool.getCommonPoolParallelism());
        Random random = new Random();
        int[] array = new int[2_000_000];
        
        try (BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(new FileOutputStream("result.csv")))) {
            bw.write("cutoff_ratio,maxDepth,avg_time_ms\n");

            for (int j = 50; j < 100; j++) {
                ParSort1.cutoff = 10_000 * (j + 1);
                double cutoffRatio = (double) ParSort1.cutoff / array.length;

                for (int depth = 1; depth <= 6; depth++) {
                    ParSort1.maxDepth = depth;
                    long totalTime = 0;

                    for (int trial = 0; trial < 10; trial++) {
                        for (int i = 0; i < array.length; i++) array[i] = random.nextInt(10_000_000);
                        long startTime = System.currentTimeMillis();
                        ParSort1.sort(array, 0, array.length);
                        long endTime = System.currentTimeMillis();
                        totalTime += (endTime - startTime);
                    }

                    double avgTime = totalTime / 10.0;


                    String line = cutoffRatio + "," + depth + "," + avgTime + "\n";
                    bw.write(line);
                    bw.flush(); 

                    System.out.printf("cutoff: %d (%.4f), depth: %d -> avg time: %.2fms\n",
                            ParSort1.cutoff, cutoffRatio, depth, avgTime);
                }
            }
            System.out.println("Results written to result.csv in current directory.");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
