package edu.neu;

import javax.ws.rs.client.*;
import javax.ws.rs.core.Response;

import java.util.List;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ThreadLocalRandom;

public class Worker implements Runnable {

    private BlockingQueue queue;
    private String phase;
    private String base_url;
    private int iterNum;
    private int userBound;
    private int day;
    private int timeInterval_start;
    private int timeInterval_end;
    private int stepCountBound;
    private List<Double> latency;
    private int successReq;
    private int req;


    public Worker (BlockingQueue queue, String base_url, int num, int userBound, int day, int stepCountBound,
                   int timeInterval_start, int timeInterval_end) {
        this.queue = queue;
        this.phase = phase;
        iterNum = num;
        this.base_url = base_url;
        this.userBound = userBound;
        this.day = day;
        this.timeInterval_start = timeInterval_start;
        this.timeInterval_end = timeInterval_end;
        this.stepCountBound = stepCountBound;
    }
    @Override
    public void run() {
        Client c = ClientBuilder.newClient();
        WebTarget webTarget = c.target(base_url);
        //BlockingQueue queue = new ArrayBlockingQueue(2014);
        //CollectWorker collectWorker = new CollectWorker(queue, phase + ".txt");
        //Thread t1 = new Thread(collectWorker);
        //t1.start();

        for (int i = 0; i < iterNum; i++) {

            /*
            Random rand = new Random();
            int userID1 = rand.nextInt(userBound);
            int userID2 = rand.nextInt(userBound);
            int userID3 = rand.nextInt(userBound);

            int delta_timeInterval = timeInterval_end - timeInterval_start + 1;

            int timeInterval1 = rand.nextInt(delta_timeInterval) + timeInterval_start;
            int timeInterval2 = rand.nextInt(delta_timeInterval) + timeInterval_start;
            int timeInterval3 = rand.nextInt(delta_timeInterval) + timeInterval_start;

            int stepCount1 = rand.nextInt(stepCountBound);
            int stepCount2 = rand.nextInt(stepCountBound);
            int stepCount3 = rand.nextInt(stepCountBound);
            */


            int userID1 = ThreadLocalRandom.current().nextInt(userBound);
            int userID2 = ThreadLocalRandom.current().nextInt(userBound);
            int userID3 = ThreadLocalRandom.current().nextInt(userBound);

            int delta_timeInterval = timeInterval_end - timeInterval_start + 1;

            int timeInterval1 = ThreadLocalRandom.current().nextInt(timeInterval_start, timeInterval_end+1);
            int timeInterval2 = ThreadLocalRandom.current().nextInt(timeInterval_start, timeInterval_end+1);
            int timeInterval3 = ThreadLocalRandom.current().nextInt(timeInterval_start, timeInterval_end+1);

            int stepCount1 = ThreadLocalRandom.current().nextInt(stepCountBound);
            int stepCount2 = ThreadLocalRandom.current().nextInt(stepCountBound);
            int stepCount3 = ThreadLocalRandom.current().nextInt(stepCountBound);

            try {
                long[] latency1 = post(webTarget, userID1, day, timeInterval1, stepCount1);
                queue.put(latency1);
                long[] latency2 = post(webTarget, userID2, day, timeInterval2, stepCount2);
                queue.put(latency2);
                long[] latency3 = get1(webTarget, userID1);
                queue.put(latency3);
                long[] latency4 = get2(webTarget, userID2, day);
                queue.put(latency4);
                long[] latency5 = post(webTarget, userID3, day, timeInterval3, stepCount3);
                queue.put(latency5);

                //System.out.println(latency1);
                //System.out.println(latency2);
                //System.out.println(latency3);
                //System.out.println(latency4);
                //System.out.println(latency5);

                if (req != successReq) System.out.println("there is unsuccessful request");
            } catch (Exception e) {
                e.printStackTrace();
            }



            /*
            long startTime = System.currentTimeMillis();
            Response resp = target.path("server").request().get();
            long endTime = System.currentTimeMillis();
            */
        }
        //System.out.println(req);
        //System.out.println(successReq);
        c.close();
    }

    public long[] post(WebTarget webTarget, int userID, int day, int timeInterval, int stepCount) {
        long startTime = System.currentTimeMillis();
        Response resp = webTarget.path("/{userID}/{day}/{timeInterval}/{stepCount}")
                .resolveTemplate("userID", userID)
                .resolveTemplate("day", day)
                .resolveTemplate("timeInterval", timeInterval)
                .resolveTemplate("stepCount", stepCount)
                .request().post(Entity.text(""));
        long endTime = System.currentTimeMillis();
        req++;
        if (resp.getStatus() == 200) successReq++;
        //else System.out.println(resp.getStatus());
        resp.close();
        return new long[]{startTime, endTime - startTime};
    }

    public long[] get1(WebTarget webTarget, int userID) {
        long startTime = System.currentTimeMillis();
        Response resp = webTarget.path("/current/{userID}")
                .resolveTemplate("userID", userID)
                .request().get();
        long endTime = System.currentTimeMillis();
        req++;
        if (resp.getStatus() == 200) successReq++;
        //else System.out.println(resp.getStatus());
        resp.close();
        return new long[]{startTime, endTime - startTime};
    }

    public long[] get2(WebTarget webTarget, int userID, int day) {
        long startTime = System.currentTimeMillis();
        Response resp = webTarget.path("/single/{userID}/{day}")
                .resolveTemplate("userID", userID)
                .resolveTemplate("day", day)
                .request().get();
        long endTime = System.currentTimeMillis();
        req++;
        if (resp.getStatus() == 200) successReq++;
        //else System.out.println(resp.getStatus());
        resp.close();
        return new long[]{startTime, endTime - startTime};
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
