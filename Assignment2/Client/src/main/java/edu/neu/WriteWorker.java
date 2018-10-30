package edu.neu;

import java.io.*;
import java.util.concurrent.BlockingQueue;

public class WriteWorker implements Runnable {

    protected BlockingQueue queue;
    private String filePath;


    public WriteWorker (BlockingQueue queue, String filePath) {
        this.queue = queue;
        this.filePath = filePath;
    }

    @Override
    public void run() {
        Writer wr = null;

        try {
            wr = new FileWriter(filePath);

            while (true) {
                if (queue.isEmpty()) {
                    continue;
                }
                long[] vals = (long[]) queue.take();
                String str1 = String.valueOf(vals[0]);
                String str2 = String.valueOf(vals[1]);
                //System.out.println(str);
                wr.write(str1 + "\t" + str2 + "\n");
                wr.flush();
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                wr.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }


        /*
        OutputStream os = null;
        try {
            os = new FileOutputStream(filePath, true);
            String str = String.valueOf(queue.take());
            os.write(str.getBytes(), 0, str.length());
            os.write('\n');
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                os.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        */
    }

}
