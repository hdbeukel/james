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
import org.jamesframework.core.problems.objectives.Objective;
import org.jamesframework.core.subset.SubsetSolution;

/**
 * Implements the core subset selection objective: maximizing the average distance between all pairs of selected items.
 * 
 * @author <a href="mailto:herman.debeukelaer@ugent.be">Herman De Beukelaer</a>
 */
public class CoreSubsetObjective implements Objective<SubsetSolution, CoreSubsetData>{

    /**
     * Evaluates the given subset solution using the given data, by computing the average distance between all pairs of
     * selected items. If less than two items are selected, this method always returns 0.
     * 
     * @param solution subset solution
     * @param data core subset data
     * @return average distance between all pairs of selected items; 0 if less than 2 items are selected
     */
    @Override
    public double evaluate(SubsetSolution solution, CoreSubsetData data) {
        if(solution.getNumSelectedIDs() < 2){
            return 0.0;
        } else {
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
            return sumDist/numDist;
        }
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
