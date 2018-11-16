package edu.neu;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ArrayBlockingQueue;

public class CollectWorker implements Runnable {

    protected BlockingQueue inQueue;
    protected BlockingQueue outQueue;
    private String filePath;

    public CollectWorker (BlockingQueue inQueue, String filePath) {
        this.inQueue = inQueue;
        this.outQueue = new ArrayBlockingQueue(2014);
        this.filePath = filePath;
    }

    public void run() {
        try {
            WriteWorker writeWorker = new WriteWorker(outQueue, filePath);
            Thread t1 = new Thread(writeWorker);
            t1.start();
            while (true) {
                if (inQueue.isEmpty()) continue;
                outQueue.put(inQueue.take());
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

    }
}
