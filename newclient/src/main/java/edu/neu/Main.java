package edu.neu;

import java.io.IOException;
import java.util.*;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.apache.commons.httpclient.*;
import org.apache.commons.httpclient.methods.*;
import org.apache.commons.httpclient.params.HttpMethodParams;

import org.apache.commons.math3.stat.descriptive.moment.Mean;
import org.apache.commons.math3.stat.descriptive.rank.*;


/**
 * Main class.
 *
 */
public class Main {
    // Base URI the Grizzly HTTP server will listen on
    //public static String BASE_URI = "http://52.12.19.161:8080/server";
    public static String BASE_URI = "https://jg30uwsnl0.execute-api.us-east-1.amazonaws.com/Prod/server";

    public static class Worker implements Runnable {

      private int iterNum;
      private List<Double> latency;
      private int successReq;
      private int req;

      public Worker (int num) {
        iterNum = num;
        latency = new ArrayList<>();
      }
      @Override
      public void run() {
	    long startTime, endTime;
	    HttpClient client = new HttpClient();
	    int statusCode;
	    GetMethod getMethod = new GetMethod(Main.BASE_URI);
	    getMethod.getParams().setParameter(HttpMethodParams.RETRY_HANDLER,
    		new DefaultHttpMethodRetryHandler());
	PostMethod postMethod = new PostMethod(Main.BASE_URI);
	    postMethod.getParams().setParameter(HttpMethodParams.RETRY_HANDLER,
    		new DefaultHttpMethodRetryHandler());
        for (int i = 0; i < iterNum; i++) {
            startTime = System.currentTimeMillis();
		try {
		    statusCode = client.executeMethod(getMethod);
              if (statusCode == HttpStatus.SC_OK) successReq++;
              //System.out.println("Get Code: " + statusCode);
		} catch (HttpException e) {
              System.err.println("Fatal protocol violation: " + e.getMessage());
              e.printStackTrace();
            } catch (IOException e) {
              System.err.println("Fatal transport error: " + e.getMessage());
              e.printStackTrace();
		}
            endTime = System.currentTimeMillis();
            req++;
            latency.add((double)(endTime - startTime));

            startTime = System.currentTimeMillis();
		try {
		    statusCode = client.executeMethod(postMethod);
              if (statusCode == HttpStatus.SC_OK) successReq++;
              //System.out.println("Post Code: " + statusCode);
		} catch (HttpException e) {
              System.err.println("Fatal protocol violation: " + e.getMessage());
              e.printStackTrace();
            } catch (IOException e) {
              System.err.println("Fatal transport error: " + e.getMessage());
              e.printStackTrace();
            }
            endTime = System.currentTimeMillis();
            req++;
            latency.add((double)(endTime - startTime));
        }
	    getMethod.releaseConnection();
	    postMethod.releaseConnection();
      }

      public List<Double> getLatency() {
        return latency;
      }

      public int getReq() {
        return req;
      }

      public int getSuccessReq() {
        return successReq;
      }
    }

    /**
     * Main method.
     * @param args
     * @throws IOException
     */
    public static void main(String[] args) throws IOException {
      int threadNum = Integer.parseInt(args[0]);
      int iterNum = Integer.parseInt(args[1]);
	String prefix = "http://";
	if (args[2].contains("execute-api")) prefix = "https://";
      Main.BASE_URI = prefix + args[2] + ":" + args[3] + "/Prod/server";

      System.out.println("URL: " + Main.BASE_URI);
      System.out.println(args[0] + " " + args[1] + " " + args[2] + " " + args[3]);

      long startTime = System.currentTimeMillis();
      System.out.println("Client starting ....Time: " + startTime);
      Worker[] wl1 = runPhase("Warmup", 0.1, threadNum, iterNum);
      Worker[] wl2 = runPhase("Loading", 0.5, threadNum, iterNum);
      Worker[] wl3 = runPhase("Peak", 1.0, threadNum, iterNum);
      Worker[] wl4 = runPhase("Cooldown", 0.25, threadNum, iterNum);
      long endTime = System.currentTimeMillis();
      double wallTime = (endTime - startTime) / 1000.0;

      System.out.println("==================================");

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

    }

    public static Worker[] runPhase(String phase, double pct, int threadNum, int iterNum) {
      int tNum = (int)(pct * threadNum);
      ExecutorService executor = Executors.newFixedThreadPool(tNum);
      long startTime = System.currentTimeMillis();
      System.out.println(phase + " phase: All threads running ....");
      Worker[] wl = new Worker[tNum];
      for (int i = 0; i < tNum; i++) {
        wl[i] = new Worker(iterNum);
        Runnable w = wl[i];
        executor.execute(w);
      }
      executor.shutdown();
      while (!executor.isTerminated()) {
      }
      long endTime = System.currentTimeMillis();
      System.out.println(phase + " phase complete: Time " + (endTime - startTime) / 1000.0 + " seconds");
      return wl;
    }

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
}


