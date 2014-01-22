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

import java.util.Collections;
import java.util.Iterator;
import java.util.TreeSet;
import org.jamesframework.core.problems.constraints.PenalizingConstraint;
import org.jamesframework.core.problems.solutions.SubsetSolution;

/**
 * Fake subset constraint based on fake subset data. Only accepts solutions where the minimum difference
 * in score of selected entities is larger than a given value. The penalty assigned to a solution violating
 * the constraint corresponds to the number of score differences smaller than the desired minimum.
 * Used for testing only.
 * 
 * @author Herman De Beukelaer <herman.debeukelaer@ugent.be>
 */
public class FakeSubsetPenalizingConstraint extends FakeSubsetConstraint implements PenalizingConstraint<SubsetSolution, FakeSubsetData> {

    public FakeSubsetPenalizingConstraint(double minDiff) {
        super(minDiff);
    }

    /**
     * Compute penalty of a solution. The penalty corresponds to the number of differences between scores of selected entities which
     * are smaller than the required minimum difference.
     * 
     * @param solution solution to compute penalty for
     * @param data underlying (fake) subset data
     * @return penalty of solution
     */
    @Override
    public double computePenalty(SubsetSolution solution, FakeSubsetData data) {
        // store scores in sorted set
        TreeSet<Double> scores  = new TreeSet<>();
        for(int ID : solution.getSelectedIDs()){
            scores.add(data.getScore(ID));
        }
        // go through sorted scores, keeping track of set of previous scores,
        // sorted in reversed order to enable early breaks
        TreeSet<Double> prevScores = new TreeSet<>(Collections.reverseOrder());
        int numTooSmall = 0;
        for(double score : scores){
            // go through previous scores, in reverse order, and compute diffs
            boolean tooSmallDiff = true;
            Iterator<Double> prevIt = prevScores.iterator();
            while(tooSmallDiff && prevIt.hasNext()){
                // check diff
                double diff = score - prevIt.next();
                if(diff < getMinDiff()){
                    // found too small diff, adjust counter
                    numTooSmall++;
                } else {
                    // diff large enough, can skip other previous scores as they
                    // will always yield higher diffs because of the reversed sorting
                    tooSmallDiff = false;
                }
            }
            // add to previous scores
            prevScores.add(score);
        }
        // return penalty (will be zero if all diffs are large enough)
        return (double) numTooSmall;
    }
}
