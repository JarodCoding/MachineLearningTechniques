import lib.jadsl.collections.data.vector.DataVectorType;

import java.util.ArrayList;

/**
 * Created by Pascal Jarod Kuthje on 06.05.2016.
 */
public class Majority_K_Nearest_Neighbours extends K_Nearest_Neighbors{
    Majority_K_Nearest_Neighbours(int dimension,DataVectorType type){
        super(dimension,type);
    }
    @Override
    protected int selectClassification(Integer[] closestClassifications) {
        int[] matchCount = new int[getHighestClassification()];
        for(int i:closestClassifications){
            matchCount[i]++;
        }
        int maxMatchCount = 0;
        ArrayList<Integer> maxMatchCountClassifications = new ArrayList<>();

        for(int i = 0;i < matchCount.length;i++){
            if(matchCount[i]>maxMatchCount){
                maxMatchCount=matchCount[i];
                maxMatchCountClassifications.clear();
                maxMatchCountClassifications.add(i);
            }else if(matchCount[i]==maxMatchCount){
                maxMatchCountClassifications.add(i);
            }
        }
        if(maxMatchCountClassifications.size() != 1)return -1;//tie
        return maxMatchCountClassifications.get(0);
    }
}
