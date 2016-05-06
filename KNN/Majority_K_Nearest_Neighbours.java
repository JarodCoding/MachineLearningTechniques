import lib.jadsl.collections.data.vector.DataVectorType;
import java.util.ArrayList;

/**
 * Created by Pascal Jarod Kuthe on 06/05/2016.
 * Copyright (c) <2016> <Pascal Jarod Kuthe>
 * Permission is hereby granted, free of charge, to any person obtaining a copy of this software and associated documentation files (the "Software"), to deal in the Software without restriction, including without limitation the rights to use, copy, modify, merge, publish, distribute, sublicense, and/or sell copies of the Software, and to permit persons to whom the Software is furnished to do so, subject to the following conditions:
 * The above copyright notice and this permission notice shall be included in all copies or substantial portions of the Software.
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
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