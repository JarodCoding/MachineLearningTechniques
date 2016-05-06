
import lib.jadsl.collections.list.*;
import lib.jadsl.collections.data.vector.*;
import java.util.ArrayList;
import java.util.Random;

/**
 * Created by Pascal Jarod Kuthe on 28/04/2016.
 * Copyright (c) <2016> <Pascal Jarod Kuthe>
 * Permission is hereby granted, free of charge, to any person obtaining a copy of this software and associated documentation files (the "Software"), to deal in the Software without restriction, including without limitation the rights to use, copy, modify, merge, publish, distribute, sublicense, and/or sell copies of the Software, and to permit persons to whom the Software is furnished to do so, subject to the following conditions:
 * The above copyright notice and this permission notice shall be included in all copies or substantial portions of the Software.
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 */
public class K_Means<T> {
    private final int k;
    private final double threshold;
    private int dimension;
    private StaticSizeDataVectorArrayList observations;
    private StaticSizeDataVectorArrayList means;
    private DataVectorArrayList[] clusters;
    private boolean running = false;

    public K_Means(int k,int threshold){
        this.k = k;
        this.threshold = threshold;
        if(k <= 1)throw new IllegalArgumentException("k needs to be > 1");
    }

    public DataVectorArrayList[] getClusters(){
        if(running)throw new IllegalStateException("Data Clustering is still in progress");
        return clusters;
    }

    public StaticSizeDataVectorArrayList getMeans(){
        if(running)throw new IllegalStateException("Data Clustering is still in progress");
        return means;
    }

    public void supplyData(StaticSizeDataVectorArrayList observations){
        if(k > observations.size())throw new IllegalArgumentException("there need to be more oberservations the clusters to be calculated");
        this.observations = observations;
        this.dimension = observations.getDimension();
        observations.makeContentStrictlyStatic();
        means = new StaticSizeDataVectorArrayList(dimension,k,observations.getType());
        clusters = new DataVectorArrayList[k];
        for(int i = 0;i<k;i++){
            clusters[i] = new DataVectorArrayList(dimension,observations.getType());
        }
    }
    //starts the alogrythem with supplied data and infinite cycles
    public int start(StaticSizeDataVectorArrayList observations){
        return start(observations,-1);
    }
    //starts the alogrythem with previously supplied data and infinite cycles
    public int start(){
        return  start(-1);
    }
    public int start(StaticSizeDataVectorArrayList observations, int maxCycels){
        supplyData(observations);
        start(maxCycels);
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
    private void initalize(){
        DataVector totalMean = observations.calculateMeanDataVector();
        double average_euclidean_distance = 0;
        for(DataVector x:observations){
            average_euclidean_distance += x.distance(totalMean);
        }
        average_euclidean_distance /= observations.size();
        //try to create point system
        boolean sucess = false;
        double min_euclidean_distance;
        double max_euclidean_distance;
        int offset = 1;
        Random rand = new Random();
        ArrayList<Integer> failedIndecies = new ArrayList<>();
        ArrayList<Integer> usedIndecies = new ArrayList<>();
        while (!sucess) {
            min_euclidean_distance = Math.max(average_euclidean_distance / (1 + offset * 0.1),average_euclidean_distance / 6);
            max_euclidean_distance = Math.min(average_euclidean_distance * (1 + offset * 0.1),average_euclidean_distance * 2);
            for (int sum = 0; sum < k; sum++) {
                int index = rand.nextInt(observations.size());
                if (min_euclidean_distance < totalMean.distance(observations.get(index)) && totalMean.distance(observations.get(index)) < max_euclidean_distance) {
                    boolean failed = false;
                    if (!failedIndecies.contains(index) && !usedIndecies.contains(index)) {
                        for (int comparisonIndex = 0; comparisonIndex < usedIndecies.size() && !failed; comparisonIndex++) {
                            if (observations.get(comparisonIndex).distance( observations.get(index)) > min_euclidean_distance * 0.9) {
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
    private int run(int max){
        int finished = 0;
        int i;
        for (i = 0;(i < max||max == -1)&&finished<10;i++){
            if(update()< threshold)finished++;
            else finished = 0;
        }
        return i;
    }
    private double update(){
        for(DataVectorArrayList cluster:clusters){
            cluster.clear();
        }
        int res;
        for(DataVector x:observations){
            res = x.determineClosestDataVectorIndex(means);
            clusters[res].add(x);
        }
        double meanchange = 0;
        DataVector newMean;
        for(int i = 0;i < clusters.length;i++){
            newMean = clusters[i].calculateMeanDataVector();
            meanchange = Math.max(meanchange,newMean.distance(means.get(i)));
            means.set(i,newMean);
        }
        return meanchange;
    }


}
