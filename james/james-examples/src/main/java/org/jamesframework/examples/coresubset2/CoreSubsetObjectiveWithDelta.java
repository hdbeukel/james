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

package org.jamesframework.examples.coresubset2;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import org.jamesframework.core.exceptions.IncompatibleDeltaEvaluationException;
import org.jamesframework.core.problems.objectives.evaluations.Evaluation;
import org.jamesframework.core.problems.objectives.evaluations.SimpleEvaluation;
import org.jamesframework.core.search.neigh.Move;
import org.jamesframework.core.subset.SubsetSolution;
import org.jamesframework.core.subset.neigh.moves.SubsetMove;
import org.jamesframework.examples.coresubset.CoreSubsetData;
import org.jamesframework.examples.coresubset.CoreSubsetObjective;

/**
 * Implements the core subset selection objective: maximizing the average distance between all pairs of selected items.
 * 
 * @author <a href="mailto:herman.debeukelaer@ugent.be">Herman De Beukelaer</a>
 */
public class CoreSubsetObjectiveWithDelta extends CoreSubsetObjective{

    /**
     * <p>
     * Computes the updated evaluation obtained when applying a given move to the current solution of a local search.
     * This so-called "delta evaluation" is much more efficient compared to re-evaluating the entire modified solution.
     * It is assumed that the received move is a {@link SubsetMove}, if not an exception is thrown. Therefore this
     * objective can only be used in combination with a neighbourhood that generates moves of this type.
     * </p>
     * <p>
     * The new evaluation is obtained by inspecting the currently selected and added/removed IDs, accounting for
     * the respective changes in the average of all pairwise distances.
     * </p>
     * 
     * @param move subset move
     * @param curSolution current subset solution
     * @param curEvaluation evaluation of the given solution
     * @param data core subset data
     * @throws IncompatibleDeltaEvaluationException if the received move is not a {@link SubsetMove}
     * @return evaluation of modified solution
     */
    @Override
    public Evaluation evaluate(Move move, SubsetSolution curSolution, Evaluation curEvaluation, CoreSubsetData data) {
        // check move type
        if(!(move instanceof SubsetMove)){
            throw new IncompatibleDeltaEvaluationException("Core subset objective should be used in combination "
                                                + "with neighbourhoods that generate moves of type SubsetMove.");
        }
        // cast move
        SubsetMove subsetMove = (SubsetMove) move;
        
        // get current evaluation
        double curEval = curEvaluation.getValue();
        // undo average to get sum of distances
        int numSelected = curSolution.getNumSelectedIDs();
        int numDistances = numSelected * (numSelected-1) / 2;
        double sumDist = curEval * numDistances;
        
        // get set of added and removed IDs
        Set<Integer> added = subsetMove.getAddedIDs();
        Set<Integer> removed = subsetMove.getDeletedIDs();
        // infer set of retained IDs
        List<Integer> retained = new ArrayList<>(curSolution.getSelectedIDs());
        retained.removeAll(removed);
        
        // subtract distances from removed items to retained items
        for(int rem : removed){
            for(int ret : retained){
                sumDist -= data.getDistance(rem, ret);
                numDistances--;
            }
        }
        
        // subtract distances from removed to other removed items
        for(int rem1 : removed){
            for(int rem2 : removed){
                // account for each distinct pair only once
                if(rem1 < rem2){
                    sumDist -= data.getDistance(rem1, rem2);
                    numDistances--;
                }
            }
        }
        
        // add distances from new items to retained items
        for(int add : added){
            for(int ret : retained){
                sumDist += data.getDistance(add, ret);
                numDistances++;
            }
        }
        
        // add distances from new items to other new items
        for(int add1 : added){
            for(int add2 : added){
                // account for each distinct pair only once
                if(add1 < add2){
                    sumDist += data.getDistance(add1, add2);
                    numDistances++;
                }
            }
        }
        
        double newEval;
        if(numDistances > 0){
            // take average based on updated number of distances
            newEval = sumDist / numDistances;
        } else {
            // no distances (less than two items remain selected)
            newEval = 0.0;
        }
        
        // return new evaluation
        return new SimpleEvaluation(newEval);
        
    }
    
}
