/*
 * Copyright 2014 Ghent University, Bayer CropScience.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.jamesframework.test.fakes;

import org.jamesframework.core.exceptions.IncompatibleDeltaValidationException;
import org.jamesframework.core.problems.constraints.PenalizingConstraint;
import org.jamesframework.core.problems.constraints.PenalizingValidation;
import org.jamesframework.core.problems.constraints.Validation;
import org.jamesframework.core.problems.constraints.validations.SimplePenalizingValidation;
import org.jamesframework.core.search.neigh.Move;
import org.jamesframework.core.subset.SubsetSolution;
import org.jamesframework.core.subset.neigh.SubsetMove;

/**
 * Fake subset constraint based on fake subset data. Only accepts solutions where the minimum difference
 * in score of selected entities is larger than a given value. Used for testing only.
 * 
 * @author <a href="mailto:herman.debeukelaer@ugent.be">Herman De Beukelaer</a>
 */
public class MinDiffFakeSubsetConstraint implements PenalizingConstraint<SubsetSolution, ScoredFakeSubsetData>{
    
    // minimum required difference in score of selected entities
    private final double minDiff;
    
    /**
     * Create a fake subset constraint with given minimum difference in scores of selected entities.
     * 
     * @param minDiff minimum difference in scores of selected entities.
     */
    public MinDiffFakeSubsetConstraint(double minDiff){
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
    public PenalizingValidation validate(SubsetSolution solution, ScoredFakeSubsetData data) {
        // check all pairs of elements
        int numTooClose = 0;
        for(int id1 : solution.getSelectedIDs()){
            for(int id2 : solution.getSelectedIDs()){
                // careful: DO NOT count all pairs twice!
                if(id2 > id1 && Math.abs(data.getScore(id1) - data.getScore(id2)) < minDiff){
                    numTooClose++;
                }
            }
        }
        return new SimplePenalizingValidation(numTooClose == 0, numTooClose);
    }

    /**
     * Delta validation
     * 
     * @param move move to be applied to the current solution
     * @param curSolution current solution
     * @param curValidation current solution validation
     * @param data underlying data
     * @return validation of modified solution (neighbour)
     */
    @Override
    public PenalizingValidation validate(Move move, SubsetSolution curSolution, Validation curValidation, ScoredFakeSubsetData data) {
        if(!(move instanceof SubsetMove)){
            throw new IncompatibleDeltaValidationException("Expected move of type SubsetMove.");
        }
        SubsetMove subsetMove = (SubsetMove) move;
        PenalizingValidation val = (PenalizingValidation) curValidation;
        int n = (int) val.getPenalty();
        
        // account for removed elements
        for(int del : subsetMove.getDeletedIDs()){
            for(int curSel : curSolution.getSelectedIDs()){
                if(!subsetMove.getDeletedIDs().contains(curSel)){
                    // distance to retained item
                    if(Math.abs(data.getScore(del) - data.getScore(curSel)) < minDiff){
                        n--;
                    }
                } else {
                    // distance to other discarded item
                    // careful: DO NOT remove these twice!
                    if(curSel > del && Math.abs(data.getScore(del) - data.getScore(curSel)) < minDiff){
                        n--;
                    }
                }
            }
        }
        
        // account for added elements
        for(int add : subsetMove.getAddedIDs()){
            // check distances to currently selected elements
            for(int curSel : curSolution.getSelectedIDs()){
                // check: retained
                if(!subsetMove.getDeletedIDs().contains(curSel)){
                    // check distance
                    if(Math.abs(data.getScore(add) - data.getScore(curSel)) < minDiff){
                        n++;
                    }
                }
            }
            // check distances within added elements
            for(int add2 : subsetMove.getAddedIDs()){
                // careful: DO NOT account for each pair twice!
                if(add2 > add && Math.abs(data.getScore(add) - data.getScore(add2)) < minDiff){
                    n++;
                }
            }
        }
        
        return new SimplePenalizingValidation(n == 0, n);
    }

}
