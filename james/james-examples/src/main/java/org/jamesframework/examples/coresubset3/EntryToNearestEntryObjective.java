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

package org.jamesframework.examples.coresubset3;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Set;
import org.jamesframework.core.exceptions.IncompatibleDeltaEvaluationException;
import org.jamesframework.core.problems.objectives.Objective;
import org.jamesframework.core.problems.objectives.evaluations.Evaluation;
import org.jamesframework.core.search.neigh.Move;
import org.jamesframework.core.subset.SubsetSolution;
import org.jamesframework.core.subset.neigh.moves.SubsetMove;
import org.jamesframework.examples.coresubset.CoreSubsetData;

/**
 * Evaluates a subset solution by computing the average distance from each selected item
 * (entry) to the closest other selected item (nearest entry). This value is to be maximized.
 */
public class EntryToNearestEntryObjective implements Objective<SubsetSolution, CoreSubsetData>{

    @Override
    public Evaluation evaluate(SubsetSolution solution, CoreSubsetData data) {
        // initialize evaluation object
        EntryToNearestEntryEvaluation eval = new EntryToNearestEntryEvaluation();
        // find closest neighbour of each item in the selection
        Set<Integer> selected = solution.getSelectedIDs();
        for(int sel : selected){
            // find closest other selected item
            Integer closestOther = findClosest(sel, selected, data);
            // register closest item in evaluation object (if any)
            if(closestOther != null){
                eval.add(sel, closestOther, data.getDistance(sel, closestOther));
            }
        }
        return eval;
    }
    
    // finds the item in the given group that is closest to the given item;
    // null if the given group does not contain any items different from the given item
    private Integer findClosest(int item, Collection<Integer> group, CoreSubsetData data){
        double dist;
        Double minDist = null;
        Integer closestOther = null;
        for(int other : group){
            if(other != item){
                dist = data.getDistance(item, other);
                if(minDist == null || dist < minDist){
                    minDist = dist;
                    closestOther = other;
                }
            }
        }
        return closestOther;
    }
    
    @Override
    public Evaluation evaluate(Move move, SubsetSolution curSolution, Evaluation curEvaluation, CoreSubsetData data){
        // check move type
        if(!(move instanceof SubsetMove)){
            throw new IncompatibleDeltaEvaluationException("Entry to nearest entry objective should be used in "
                                     + "combination with neighbourhoods that generate moves of type SubsetMove.");
        }
        // cast move
        SubsetMove subsetMove = (SubsetMove) move;
        
        // cast evaluation (cannot fail as both evaluate methods return such evaluation object)
        EntryToNearestEntryEvaluation eval = (EntryToNearestEntryEvaluation) curEvaluation;
        // copy to initialize new evaluation
        EntryToNearestEntryEvaluation newEval = new EntryToNearestEntryEvaluation(eval);
        
        // get added and deleted IDs from move
        Set<Integer> added = subsetMove.getAddedIDs();
        Set<Integer> deleted = subsetMove.getDeletedIDs();
        // get current selection from solution
        Set<Integer> curSelection = curSolution.getSelectedIDs();
        // infer new selection
        List<Integer> newSelection = new ArrayList<>(curSelection);
        newSelection.addAll(added);
        newSelection.removeAll(deleted);

        // discard contribution of removed items
        for(int item : deleted){
            newEval.remove(item);
        }
        
        // update closest items in new selection
        for(int item : newSelection){
            Integer curClosest = newEval.getClosest(item);
            if(curClosest == null){
                // case 1: previously unselected or no closest item set (less than two items selected);
                //         search for closest item in new selection
                Integer newClosest = findClosest(item, newSelection, data);
                // register, if any
                if(newClosest != null){
                    newEval.add(item, newClosest, data.getDistance(item, newClosest));
                }
            } else {
                // case 2: current closest item needs to be updated
                if(deleted.contains(curClosest)){
                    // case 2A: current closest item removed, rescan entire new selection
                    Integer newClosest = findClosest(item, newSelection, data);
                    // update, if any
                    if(newClosest != null){
                        newEval.update(item, newClosest, data.getDistance(item, newClosest));
                    } else {
                        // no closest item left (less than two items selected); discard
                        newEval.remove(item);
                    }
                } else {
                    // case 2B: current closest item retained; only check if any newly
                    //          added item is closer
                    Integer closestAddedItem = findClosest(item, added, data);
                    if(closestAddedItem != null
                            && data.getDistance(item, closestAddedItem) < data.getDistance(item, curClosest)){
                        // update closest item
                        newEval.update(item, closestAddedItem, data.getDistance(item, closestAddedItem));
                    }
                }
            }
        }
        
        return newEval;
    }

    @Override
    public boolean isMinimizing() {
        return false;
    }

}
