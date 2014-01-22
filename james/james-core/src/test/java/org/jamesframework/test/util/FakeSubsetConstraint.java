//  Copyright 2014 Herman De Beukelaer
//
//  Licensed under the Apache License, Version 2.0 (the "License");
//  you may not use this file except in compliance with the License.
//  You may obtain a copy of the License at
//
//      http://www.apache.org/licenses/LICENSE-2.0
//
//  Unless required by applicable law or agreed to in writing, software
//  distributed under the License is distributed on an "AS IS" BASIS,
//  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
//  See the License for the specific language governing permissions and
//  limitations under the License.

package org.jamesframework.test.util;

import java.util.TreeSet;
import org.jamesframework.core.problems.constraints.Constraint;
import org.jamesframework.core.problems.solutions.SubsetSolution;

/**
 * Fake subset constraint based on fake subset data. Only accepts solutions where the minimum difference
 * in score of selected entities is larger than a given value. Used for testing only.
 * 
 * @author Herman De Beukelaer <herman.debeukelaer@ugent.be>
 */
public class FakeSubsetConstraint implements Constraint<SubsetSolution, FakeSubsetData>{

    // minimum required difference in score of selected entities
    private final double minDiff;
    
    /**
     * Create a fake subset constraint with given minimum difference in scores of selected entities.
     * 
     * @param minDiff minimum difference in scores of selected entities.
     */
    public FakeSubsetConstraint(double minDiff){
        this.minDiff = minDiff;
    }

    public double getMinDiff() {
        return minDiff;
    }
    
    /**
     * Check whether no entities are selected with a smaller difference in score than the required minimum difference.
     * 
     * @param solution solution to verify
     * @param data underlying (fake) subset data
     * @return true if minimum difference is satisfied
     */
    @Override
    public boolean isSatisfied(SubsetSolution solution, FakeSubsetData data) {
        // store score in sorted set
        TreeSet<Double> scores  = new TreeSet<>();
        for(int ID : solution.getSelectedIDs()){
            scores.add(data.getScore(ID));
        }
        // compute difference between consecutive sorted scores, break if minimum violated
        Double prevScore = null;
        for(double score : scores){
            if(prevScore != null && score-prevScore < minDiff){
                return false;
            }
            prevScore = score;
        }
        // all ok
        return true;
    }

}
