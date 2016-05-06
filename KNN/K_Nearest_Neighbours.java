import lib.jadsl.collections.data.vector.DataVector;
import lib.jadsl.collections.data.vector.DataVectorType;
import lib.jadsl.collections.list.DataVectorArrayList;
import lib.jadsl.collections.list.StaticSizeArrayList;

import java.util.*;

/**
 * Created by Pascal Jarod Kuthe on 28/04/2016.
 * Copyright (c) <2016> <Pascal Jarod Kuthe>
 * Permission is hereby granted, free of charge, to any person obtaining a copy of this software and associated documentation files (the "Software"), to deal in the Software without restriction, including without limitation the rights to use, copy, modify, merge, publish, distribute, sublicense, and/or sell copies of the Software, and to permit persons to whom the Software is furnished to do so, subject to the following conditions:
 * The above copyright notice and this permission notice shall be included in all copies or substantial portions of the Software.
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 */
public abstract class K_Nearest_Neighbours {

    private int dimension;
    private DataVectorType type;
    private boolean running = false;
    private int totalSize = 0;

    public K_Nearest_Neighbors(int dimension,DataVectorType type){
        this.dimension = dimension;
        this.type = type;
    }


    private ArrayList<DataVectorArrayList> trainedData = new ArrayList<>();

        public void train(int classification,DataVector... xn){
            if(classification < trainedData.size())throw new IllegalArgumentException("Classifications need to be registered before they are used! Highest registered classification is: \"+NextClassification+\" given was: \"+classification");
            if(running)throw new IllegalAccessError("Training phase is over!");
            trainedData.get(classification).addAll(Arrays.asList(xn));
            totalSize += xn.length;
        }
        public void train(int classification, Collection<DataVector> xn){
            if(classification < trainedData.size())throw new IllegalArgumentException("Classifications need to be registered before they are used! Highest registered classification is: \"+NextClassification+\" given was: \"+classification");
            if(running)throw new IllegalAccessError("Training phase is over!");
            trainedData.get(classification).addAll(xn);
            totalSize += xn.size();
        }

        public int getHighestClassification(){
            return trainedData.size()-1;
        }
        // Registers the given classifications and all beneath not yet registered
        public void registerClassification(int classification){
            if(classification < trainedData.size())throw new IllegalArgumentException("Classification already registered! Highest registered classification is: \"+NextClassification+\" given was: \"+classification");
            while(trainedData.size()<=classification){
                trainedData.add(new DataVectorArrayList(dimension,type));
            }
        }

    public int classify(DataVector x,int k){
        running = true;
        //check if enough training data is present
            if(totalSize<k){
                running = false;
                throw new IllegalStateException("There need to be at least k training data available!");
            }
            if(trainedData.size()<2){
                running = false;
                throw new IllegalStateException("There need to be at least 2 classifications!");
            }
        Integer[] closestClassifications = select_K_closestClassifications(x,k);
        int res = selectClassification(closestClassifications);
        running = false;
        return res;
    }
    public int classify(DataVector x){
        return classify(x,(int)Math.round(Math.sqrt(totalSize)));
    }
    protected abstract int selectClassification(Integer[] closestClassifications);
    private Integer[] select_K_closestClassifications(DataVector x,int k){
        StaticSizeArrayList<Integer>    closestDataVectorClassifications  = new StaticSizeArrayList<>(k);
        closestDataVectorClassifications.setMaxSize(k);
        closestDataVectorClassifications.ensureCapacity(k);
        StaticSizeArrayList<Double>     smallestDistance                   = new StaticSizeArrayList<>(k);
        smallestDistance.setMaxSize(k);
        smallestDistance.ensureCapacity(k);
        //determine the k closestDataVectors
        double distance;
        int j;
        int l;
        for(int i = 0;i < trainedData.size();i++){
            for(j = 0;j < trainedData.get(i).size();j++)
                if((distance = x.distance(trainedData.get(i).get(j)))>smallestDistance.get(k-1)){
                    for(l = 1;l <= k&&distance>smallestDistance.get(k-l);l++){}
                    smallestDistance.remove(k-1);
                    closestDataVectorClassifications.remove(k-1);
                    smallestDistance.add(k-l+1,distance);
                    closestDataVectorClassifications.add(k-l+1,i);
                }
        }
        return (Integer[])closestDataVectorClassifications.toArray();
    }


}