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

import java.util.Set;
import org.jamesframework.core.problems.objectives.Objective;
import org.jamesframework.core.problems.objectives.evaluations.Evaluation;
import org.jamesframework.core.subset.SubsetSolution;
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
            double dist;
            Double minDist = null;
            Integer closestOther = null;
            for(int other : selected){
                if(other != sel){
                    dist = data.getDistance(sel, other);
                    if(minDist == null || dist < minDist){
                        minDist = dist;
                        closestOther = other;
                    }
                }
            }
            // register closest item in evaluation object (if any)
            if(closestOther != null){
                eval.add(sel, closestOther, minDist);
            }
        }
        return eval;
    }

    @Override
    public boolean isMinimizing() {
        return false;
    }

}
