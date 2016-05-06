import lib.jadsl.collections.data.vector.DataVector;
import lib.jadsl.collections.data.vector.DataVectorType;
import lib.jadsl.collections.list.DataVectorArrayList;
import lib.jadsl.collections.list.StaticSizeArrayList;

import java.util.*;

/**
 * Created by Pascal Jarod Kuthje on 28.04.2016.
 */
public abstract class K_Nearest_Neighbors {

    private int dimension;
    private DataVectorType type;
    private boolean running = false;

    public K_Nearest_Neighbors(int dimension,DataVectorType type){
        this.dimension = dimension;
        this.type = type;
    }


    private ArrayList<DataVectorArrayList> trainedData = new ArrayList<>();

        public void train(int classification,DataVector... xn){
            if(classification < trainedData.size())throw new IllegalArgumentException("Classifications need to be registered before they are used! Highest registered classification is: \"+NextClassification+\" given was: \"+classification");
            if(running)throw new IllegalAccessError("Training phase is over!");
            trainedData.get(classification).addAll(Arrays.asList(xn));
        }
        public void train(int classification, Collection<DataVector> xn){
            if(classification < trainedData.size())throw new IllegalArgumentException("Classifications need to be registered before they are used! Highest registered classification is: \"+NextClassification+\" given was: \"+classification");
            if(running)throw new IllegalAccessError("Training phase is over!");
            trainedData.get(classification).addAll(xn);
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
            int totalSize = 0;
            for(Collection c:trainedData){
                totalSize += c.size();
            }
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
