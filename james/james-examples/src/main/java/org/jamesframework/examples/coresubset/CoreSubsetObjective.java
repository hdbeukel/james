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

package org.jamesframework.examples.coresubset;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import org.jamesframework.core.exceptions.IncompatibleDeltaEvaluationException;
import org.jamesframework.core.problems.objectives.Evaluation;
import org.jamesframework.core.problems.objectives.Objective;
import org.jamesframework.core.problems.objectives.evaluations.SimpleEvaluation;
import org.jamesframework.core.search.neigh.Move;
import org.jamesframework.core.subset.SubsetSolution;
import org.jamesframework.core.subset.neigh.moves.SubsetMove;

/**
 * Implements the core subset selection objective: maximizing the average distance between all pairs of selected items.
 * 
 * @author <a href="mailto:herman.debeukelaer@ugent.be">Herman De Beukelaer</a>
 */
public class CoreSubsetObjective implements Objective<SubsetSolution, CoreSubsetData>{

    /**
     * Evaluates the given subset solution using the given data, by computing the average
     * distance between all pairs of selected items. If less than two items are selected,
     * this method always returns 0.
     * 
     * @param solution subset solution
     * @param data core subset data
     * @return average distance between all pairs of selected items; 0 if less than 2 items are selected
     */
    @Override
    public Evaluation evaluate(SubsetSolution solution, CoreSubsetData data) {
        double value = 0.0;
        if(solution.getNumSelectedIDs() >= 2){
            // at least two items selected: compute average distance
            List<Integer> ids = new ArrayList<>(solution.getSelectedIDs());
            int id1, id2, numDist = 0;
            double sumDist = 0.0;
            for(int i=0; i<ids.size(); i++){
                id1 = ids.get(i);
                for(int j=i+1; j<ids.size(); j++){
                    id2 = ids.get(j);
                    sumDist += data.getDistance(id1, id2);
                    numDist++;
                }
            }
            value = sumDist/numDist;
        }
        return new SimpleEvaluation(value);
    }

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
        
        // infer IDs of retained, removed and added items
        List<Integer> added = new ArrayList<>(subsetMove.getAddedIDs());
        List<Integer> removed = new ArrayList<>(subsetMove.getDeletedIDs());
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
        for(int i=0; i<removed.size(); i++){
            for(int j=i+1; j<removed.size(); j++){
                sumDist -= data.getDistance(removed.get(i), removed.get(j));
                numDistances--;
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
        for(int i=0; i<added.size(); i++){
            for(int j=i+1; j<added.size(); j++){
                sumDist += data.getDistance(added.get(i), added.get(j));
                numDistances++;
            }
        }
        
        // take average based on updated number of distances
        double newEval = sumDist / numDistances;
        
        // return new evaluation
        return new SimpleEvaluation(newEval);
        
    }
    
    /**
     * Always returns <code>false</code> as this objective has to be maximized.
     * 
     * @return <code>false</code>
     */
    @Override
    public boolean isMinimizing() {
        return false;
    }

}
