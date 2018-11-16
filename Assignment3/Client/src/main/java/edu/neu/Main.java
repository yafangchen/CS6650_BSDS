package edu.neu;

import java.io.IOException;
import java.util.*;

import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.apache.commons.math3.stat.descriptive.moment.Mean;
import org.apache.commons.math3.stat.descriptive.rank.*;


/**
 * Main class.
 *
 */
public class Main {

    public static void main(String[] args) throws IOException {
        int threadNum = Integer.parseInt(args[0]);

        // EC2: http://52.12.19.161:8080/Server2
        // larger EC2: http://54.212.78.157:8080/Server2
        // load balancer: http://bsds-a2-1100734282.us-west-2.elb.amazonaws.com/Server2
        // http://localhost:8080
        // lambda: https://2jz3moy4x5.execute-api.us-west-2.amazonaws.com/Prod
        // gcp instance: http://35.230.53.82:8080/Server2
        String base_url = args[1];
        int day = Integer.parseInt(args[2]);
        int userBound = Integer.parseInt(args[3]);
        int tests = Integer.parseInt(args[4]);
        int stepCountBound = Integer.parseInt(args[5]);

        System.out.println("max_thread_Num: " + threadNum);
        System.out.println("base_url: " + base_url);
        System.out.println("day: " + day);
        System.out.println("userBound: " + userBound);
        System.out.println("tests/phase: " + tests);
        System.out.println("stepCountBound: " + stepCountBound);

        long startTime = System.currentTimeMillis();
        System.out.println("Client starting ....Time: " + startTime);
        runPhase("warmup", base_url, threadNum, tests, userBound, day, stepCountBound);
        runPhase("loading", base_url, threadNum, tests, userBound, day, stepCountBound);
        runPhase("peak", base_url, threadNum, tests, userBound, day, stepCountBound);
        runPhase("cooldown", base_url, threadNum, tests, userBound, day, stepCountBound);
        long endTime = System.currentTimeMillis();
        System.out.println("Client stopping ....Time: " + endTime);
        double wallTime = (endTime - startTime) / 1000.0;

        System.out.println("==================================");
        System.out.println("Test wall time: " + wallTime + " seconds");

        /*
        int totalReq = 0, totalSuccessReq = 0;
        List<Double> allLatencies = new ArrayList<>();

        List<Worker> allWorkers = new ArrayList<>();
        allWorkers.addAll(Arrays.asList(wl1));
        allWorkers.addAll(Arrays.asList(wl2));
        allWorkers.addAll(Arrays.asList(wl3));
        allWorkers.addAll(Arrays.asList(wl4));
        for (Worker w : allWorkers) {
            totalReq += w.getReq();
            totalSuccessReq += w.getSuccessReq();
            allLatencies.addAll(w.getLatency());
        }

        System.out.println("Total number of requests sent: " + totalReq);
        System.out.println("Total number of successful responses: " + totalSuccessReq);
        System.out.println("Test wall time: " + wallTime + " seconds");
        System.out.println("Overall throughput: " + Math.round(totalReq/wallTime) + " per second");
        System.out.println();
        processLatency(allLatencies);
        */

    }

    public static void runPhase(String phase, String base_url, int threadNum, int tests,
                                int userBound, int day, int stepCountBound) {
        int timeInterval1 = 0, timeInterval2 = 0;
        double pct = 0;

        if (phase.equals("warmup")) {
            pct = 0.1;
            timeInterval1 = 0;
            timeInterval2 = 2;
        } else if (phase.equals("loading")) {
            pct = 0.5;
            timeInterval1 = 3;
            timeInterval2 = 7;
        } else if (phase.equals("peak")) {
            pct = 1.0;
            timeInterval1 = 8;
            timeInterval2 = 18;
        } else if (phase.equals("cooldown")) {
            pct = 0.25;
            timeInterval1 = 19;
            timeInterval2 = 23;
        }
        int iterNum = tests * (timeInterval2 - timeInterval1 + 1);
        threadNum = (int)(pct * threadNum);
        System.out.println();
        System.out.println(phase + " phase");
        System.out.println("iterNum: " + iterNum);
        System.out.println("threadNum: " + threadNum);
        ExecutorService executor = Executors.newFixedThreadPool(threadNum);
        long startTime = System.currentTimeMillis();
        System.out.println(phase + " phase: All threads running ....");
        int req = 0, successReq = 0;
        BlockingQueue queue = new ArrayBlockingQueue(2014);
        WriteWorker writeWorker = new WriteWorker(queue, phase + ".txt");
        Thread t1 = new Thread(writeWorker);
        t1.start();

        for (int i = 0; i < threadNum; i++) {
            Worker worker = new Worker(queue, base_url, iterNum, userBound, day, stepCountBound,
                    timeInterval1, timeInterval2);
            executor.execute(worker);

            //req += worker.getReq();
            //successReq += worker.getSuccessReq();
        }
        executor.shutdown();
        while (!executor.isTerminated()) {
        }
        long endTime = System.currentTimeMillis();
        System.out.println(phase + " phase complete: Time " + (endTime - startTime) / 1000.0 + " seconds");

        //System.out.println("req: " + req);
        //System.out.println("successReq: " + successReq);

    }

    /*
    public static void processLatency(List<Double> list) {
        double[] values = new double[list.size()];
        int idx = 0;
        for (Double x : list) {
            values[idx] = x;
            idx++;
        }

        Mean meanClass = new Mean();
        double mean = meanClass.evaluate(values);
        System.out.println("Mean latencies: " + Math.round(mean));

        Median medianClass = new Median();
        double median = medianClass.evaluate(values);
        System.out.println("Median latencies: " + median);

        Percentile percentileClass95 = new Percentile(95);
        double percentile95 = percentileClass95.evaluate(values);
        System.out.println("95th percentile latencies: " + Math.round(percentile95));

        Percentile percentileClass99 = new Percentile(99);
        double percentile99 = percentileClass99.evaluate(values);
        System.out.println("99th percentile latencies: " + Math.round(percentile99));

        System.out.println();
    }
    */
}


