import lib.jadsl.list.MultiPropertyArrayList;
import lib.jadsl.list.StaticSizeArrayList;
import lib.jadsl.list.StaticSizeMultiPropertyArrayList;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Objects;
import java.util.Random;

/**
 * Created by Pascal Jarod Kuthe on 28/04/2016.
 * Copyright (c) <2016> <Pascal Jarod Kuthe>
 * Permission is hereby granted, free of charge, to any person obtaining a copy of this software and associated documentation files (the "Software"), to deal in the Software without restriction, including without limitation the rights to use, copy, modify, merge, publish, distribute, sublicense, and/or sell copies of the Software, and to permit persons to whom the Software is furnished to do so, subject to the following conditions:
 * The above copyright notice and this permission notice shall be included in all copies or substantial portions of the Software.
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 */
public class K_Means {
    private final int k;
    private final double threshhold;
    private StaticSizeMultiPropertyArrayList<Double> observations;
    private StaticSizeMultiPropertyArrayList<Double> means;
    private MultiPropertyArrayList<Double>[] clusters;
    private boolean running = false;

    public K_Means(int k,int threshhold){
        this.k = k;
        this.threshhold = threshhold;
        if(k == 1)throw new IllegalArgumentException("k needs to be > 1");
        this.means = new StaticSizeMultiPropertyArrayList<Double>(k);
        means.ensureCapacity(k);
        means.makeBoundriesStrictlyStatic();
        clusters = new MultiPropertyArrayList[k];
    }


    public void supplyData(StaticSizeMultiPropertyArrayList<Double> observations){
        this.observations = observations;
        if(k > observations.size())throw new IllegalArgumentException("there need to be more oberservations the clusters to be calculated");
        observations.makeContentStrictlyStatic();
        means.clear();
    }
    //starts the alogrythem with supplied data and infinite cycles
    public int start(StaticSizeMultiPropertyArrayList<Double> observations){
        return start(observations,-1);
    }
    //starts the alogrythem with previously supplied data and infinite cycles
    public int start(){
        return  start(-1);
    }
    public int start(StaticSizeMultiPropertyArrayList<Double> observations, int maxCycels){
        supplyData(observations);
        start();
        return 0;
    }
    //starts the alogrythem with previously supplied data
    public int start(int maxCycels){
        running = true;
        initalize();
        int result = run(maxCycels);
        running = false;
        return result;
    }
    public void initalize(){
        Double[] totalMean = calculateMean(observations);
        double average_euclidean_distance = 0;
        for(Double[] x:observations){
            average_euclidean_distance += euclidean_distance(totalMean,x);
        }
        average_euclidean_distance /= observations.size();
        //try to create point system
        boolean sucess = false;
        double min_euclidean_distance;
        double max_euclidean_distance;
        int offset = 1;
        Random rand = new Random();
        ArrayList<Integer> failedIndecies = new ArrayList<Integer>();
        ArrayList<Integer> usedIndecies = new ArrayList<Integer>();
        while (!sucess) {
            min_euclidean_distance = Math.max(average_euclidean_distance / (1 + offset * 0.1),average_euclidean_distance / 6);
            max_euclidean_distance = Math.min(average_euclidean_distance * (1 + offset * 0.1),average_euclidean_distance * 2);
            for (int sum = 0; sum < k; sum++) {
                int index = rand.nextInt(observations.size());
                if (min_euclidean_distance < euclidean_distance(totalMean, observations.get(index)) && euclidean_distance(totalMean, observations.get(index)) < max_euclidean_distance) {
                    boolean failed = false;
                    if (!failedIndecies.contains(index) && !usedIndecies.contains(index)) {
                        for (int comparisonIndex = 0; comparisonIndex < usedIndecies.size() && !failed; comparisonIndex++) {
                            if (euclidean_distance(observations.get(comparisonIndex), observations.get(index)) > min_euclidean_distance * 0.9) {
                                failed = true;

                            }
                        }
                        if (failed) failedIndecies.add(index);
                        usedIndecies.add(index);
                    } else {
                        failedIndecies.add(index);
                    }
                }
            }
            if(usedIndecies.size()==k)sucess = true;
            offset++;
        }
        for(int i = 0;i < k;i++){
            means.set(i,observations.get(i));
        }

    }
    public int run(int max){
        int finished = 0;
        int i;
        for (i = 0;(i < max||max == -1)&&finished<10;i++){
            if(update()< threshhold)finished++;
            else finished = 0;
        }
        return i;
    }
    public double update(){
        for(MultiPropertyArrayList<Double> cluster:clusters){
            cluster.clear();
        }
        double[] res;
        for(Double[] x:observations){
            res = determineClosestMean(means,x);
            clusters[(int)res[0]].add(x);
        }
        double meanchange = 0;
        Double[] newMean;
        for(int i = 0;i < clusters.length;i++){
            newMean = calculateMean(clusters[i]);
            meanchange = Math.max(meanchange,euclidean_distance(newMean,means.get(i)));
            means.set(i,newMean);
        }
        return meanchange;
    }
    //returns the index of the nearest mean and the distance to that mean
    public static double[] determineClosestMean(StaticSizeMultiPropertyArrayList<Double> means,Double[] x){
        double smallestDistance = euclidean_distance(means.get(0),x);
        int closestIndex = 0;
        for(int i = 1;i < means.size();i++){
            if(euclidean_distance(means.get(i),x) < smallestDistance){
                smallestDistance = euclidean_distance(means.get(i),x);
                closestIndex = i;
            }
        }
        return new double[]{closestIndex,smallestDistance};
    }
    public static Double[] calculateMean(MultiPropertyArrayList<Double> cluster){
        Double[] res = new Double[cluster.getDimension()];
        for(Double[] x:cluster){
            for(int i = 0;i<cluster.getDimension();i++){
                res[i] += x[i];
            }
        }
        for(int i = 0;i<cluster.getDimension();i++){
            res[i] /= cluster.size();
        }
        return res;
    }
    public static Double[] calculateMean(StaticSizeMultiPropertyArrayList<Double> cluster){
        Double[] res = new Double[cluster.getDimension()];
        for(Double[] x:cluster){
            for(int i = 0;i<cluster.getDimension();i++){
                res[i] += x[i];
            }
        }
        for(int i = 0;i<cluster.getDimension();i++){
            res[i] /= cluster.size();
        }
        return res;
    }
    public static double euclidean_distance(Double[] x1, Double[] x2){
        if(x1.length != x2.length)throw new IllegalArgumentException("Euclidean distance can only be calculated for vectors with equal dimensions!\n Dimension of "+ x1+" is "+x1.length+"\n Dimension of "+ x2+" is "+x2.length);
        double l = 0;
        for(int i = 0;i < x1.length;i++){
            l += Math.pow(x1[i].doubleValue()-x2[i].doubleValue(),2);
        }
        return Math.sqrt(l);
    }

}
