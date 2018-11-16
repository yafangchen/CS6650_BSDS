package edu.neu;

import java.io.*;
import java.util.*;

import org.apache.commons.math3.stat.descriptive.moment.Mean;
import org.apache.commons.math3.stat.descriptive.rank.Median;
import org.apache.commons.math3.stat.descriptive.rank.Percentile;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartUtilities;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.data.xy.XYSeries;
import org.jfree.data.xy.XYSeriesCollection;

public class DataProcess {

    double totalTime;

    public static void main(String[] args) throws Exception {

        DataProcess obj = new DataProcess();

        List<Long> timeStamps = new ArrayList<>();
        List<Double> times = new ArrayList<>();
        List<Double> latencies = new ArrayList<>();

        BufferedReader[] brs = new BufferedReader[4];

        File file = new File("./a3/gcp/256/warmup.txt");
        brs[0] = new BufferedReader(new FileReader(file));

        file = new File("./a3/gcp/256/loading.txt");
        brs[1] = new BufferedReader(new FileReader(file));

        file = new File("./a3/gcp/256/peak.txt");
        brs[2] = new BufferedReader(new FileReader(file));

        file = new File("./a3/gcp/256/cooldown.txt");
        brs[3] = new BufferedReader(new FileReader(file));

        String str;
        for (BufferedReader br : brs) {
            while ((str = br.readLine()) != null) {
                String[] arr = str.split("\\s+");
                timeStamps.add(Long.parseLong(arr[0]));
                latencies.add(Double.parseDouble(arr[1]));
            }
        }

        long timeStamp0 = timeStamps.get(0);
        double max = 0;
        for (int i = 0; i < timeStamps.size(); i++) {
            double curr = (double) (timeStamps.get(i) - timeStamp0)/1000;
            times.add(curr);
            if (curr > max) max = curr;
        }

        //System.out.println(times.size());
        //System.out.println(latencies.size());

        int maxBucket = (int) Math.ceil(max);
        int[] counts = new int[maxBucket];
        //System.out.println(maxBucket);


        for (double time : times) {
            if (time < 0.0) continue;
            int bucket = (int) Math.floor(time);
            counts[bucket]++;
        }

        obj.statistics(latencies, maxBucket);
        obj.plot(counts);

    }

    public void statistics(List<Double> list, int maxBucket) {
        double[] values = new double[list.size()];
        int idx = 0;
        for (Double x : list) {
            values[idx] = x;
            idx++;
        }

        double throughput = list.size()/maxBucket;

        System.out.println("max 256 threads with gcp");
        System.out.println("=================================");
        System.out.println("total requests: " + list.size());
        System.out.println("wall time: " + maxBucket + " seconds");
        System.out.println("throughput: " + Math.round(throughput) + " per second");
        System.out.println();
        System.out.println("=====Statistics of latencies=====");
        Mean meanClass = new Mean();
        double mean = meanClass.evaluate(values);
        System.out.println("Mean latencies: " + Math.round(mean) + " ms");

        Median medianClass = new Median();
        double median = medianClass.evaluate(values);
        System.out.println("Median latencies: " + median + " ms");

        Percentile percentileClass95 = new Percentile(95);
        double percentile95 = percentileClass95.evaluate(values);
        System.out.println("95th percentile latencies: " + Math.round(percentile95) + " ms");

        Percentile percentileClass99 = new Percentile(99);
        double percentile99 = percentileClass99.evaluate(values);
        System.out.println("99th percentile latencies: " + Math.round(percentile99) + " ms");
    }

    public void plot(int[] counts) throws Exception {
        String plotTitle = "max 256 threads with gcp";
        String plotImage = "256_gcp.png";

        int maxBucket = counts.length;

        XYSeries series1 = new XYSeries(plotTitle);
        for (int i = 0; i < maxBucket; i++) {
            series1.add(i, counts[i]);
        }

        XYSeriesCollection dataset = new XYSeriesCollection();
        dataset.addSeries(series1);

        JFreeChart chart = ChartFactory.createXYLineChart(
                plotTitle,
                "Second bucket",
                "Number of requests fired",
                dataset,
                PlotOrientation.VERTICAL,
                false,
                true,
                false
        );
        ChartUtilities.saveChartAsPNG(new File(plotImage), chart, 1000, 600);
        //ChartUtilities.saveChartAsPNG(new File(plotImage), chart, 800, 500);
    }


}
