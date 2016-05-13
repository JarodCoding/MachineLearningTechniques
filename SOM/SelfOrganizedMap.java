import com.test.clustering.Majority_K_Nearest_Neighbours;
import lib.jadsl.collections.data.vector.DataVector;
import lib.jadsl.collections.data.vector.DataVectorType;
import lib.jadsl.collections.list.DataVectorArrayList;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Map;
import java.util.Random;

/**
 * Created by Pascal Jarod Kuthe on 10/05/2016.
 * Copyright (c) <2016> <Pascal Jarod Kuthe>
 * Permission is hereby granted, free of charge, to any person obtaining a copy of this software and associated documentation files (the "Software"), to deal in the Software without restriction, including without limitation the rights to use, copy, modify, merge, publish, distribute, sublicense, and/or sell copies of the Software, and to permit persons to whom the Software is furnished to do so, subject to the following conditions:
 * The above copyright notice and this permission notice shall be included in all copies or substantial portions of the Software.
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 */
public class SelfOrganizedMap {
    private DataVector[][] grid;

    private final DataVectorArrayList trainingData;
        private boolean trained;
        private final boolean shuffle;


    public SelfOrganizedMap(DataVector[][] initialGrid, double startLearningRate,DataVectorArrayList trainingData,boolean shuffleTrainingData){

        this.grid = initialGrid;
        int gridHeight = initialGrid[0].length;
        for(int i = 1;i <initialGrid.length;i++){
            if(initialGrid[i].length != gridHeight)throw new IllegalArgumentException("Length must be constant across all rows of the initial grid! The Width of the "+i+". row is "+initialGrid[i].length+" while the length of all rows before it is "+gridHeight);
        }
        this.Radius0 = Math.max(initialGrid.length,gridHeight)/2;
        this.startLearningRate = startLearningRate;
        this.trainingData = trainingData;
        this.shuffle = shuffleTrainingData;
    }

    public void startTraining(int iterations){
        if(trained)throw new IllegalStateException("SelfOrganizedMap is already trained!");
        trained = true;
        Random rand = new Random();
        for(int i = 0;i < iterations; i++){
            if(shuffle)Collections.shuffle(trainingData,rand);
            final double radius = calculateRadius(i,iterations);
            final double learningRate = calculateLearningRate(i,iterations);
            for(int j = 0;j < trainingData.size();j++){
                DataVector trainingVector = trainingData.get(j);
                int closestLatticeX = 0;
                int closestLatticeY = 0;
                double closestDistance = trainingVector.distance(grid[0][0]);
                double tmpDistance;
                for(int x = 0; x < grid.length;x++){
                    for(int y = 0; y < grid[x].length;y++){
                        if((tmpDistance = trainingVector.distance(grid[x][y]))<closestDistance){
                            closestLatticeX = x;
                            closestLatticeY = y;
                            closestDistance = tmpDistance;
                        }
                    }
                }
                int xEffektBlockEnd = (int) Math.min(Math.round(closestLatticeX+radius),grid.length);
                int yEffektBlockEnd = (int) Math.min(Math.round(closestLatticeY+radius),grid[0].length);
                for(int x = (int) Math.max(Math.round(closestLatticeX-radius),0); x < xEffektBlockEnd;x++){
                    for(int y = (int) Math.max(Math.round(closestLatticeY-radius),0); y < yEffektBlockEnd;y++){
                        grid[x][y] = grid[x][y].add(trainingVector.subtract(grid[x][y]).multiplyElements(calculateLatticeDistance(closestLatticeX,closestLatticeY,x,y,radius)*learningRate));
                    }
                }
            }
        }
    }



            private double calculateLatticeDistance(int x1,int y1,int x2,int y2,double Radius){
                int distanceSquared = (x1-x2)^2 + (y1-y2)^2;
                if(Math.sqrt(distanceSquared)>Radius)return 0;
                return Math.exp(-distanceSquared/(2* Radius*Radius));
            }
                private double calculateLatticeDistance(int x1,int y1,int x2,int y2,int currentIterations, int totalIterations){
                    return calculateLatticeDistance(x1,y1,x2,y2,calculateRadius(currentIterations,totalIterations));
                }
                private final double Radius0;
                private double calculateRadius(int currentIteration,int totalIterations){
                    return Math.round(Radius0 * Math.exp(-currentIteration/(totalIterations/Math.log(Radius0))));
                }
            private final double startLearningRate;
            private double calculateLearningRate(int currentIteration,int totalIterations){
                return startLearningRate*Math.exp(-currentIteration/totalIterations);
            }
    public DataVector getWeightVector(int x, int y){
        return grid[x][y];
    }

}
