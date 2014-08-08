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

package org.jamesframework.examples.knapsack;

import org.jamesframework.core.problems.constraints.Constraint;
import org.jamesframework.core.subset.SubsetSolution;

/**
 * Knapsack constraint verifying that the maximum total weight is not exceeded.
 * 
 * @author <a href="mailto:herman.debeukelaer@ugent.be">Herman De Beukelaer</a>
 */
public class KnapsackConstraint implements Constraint<SubsetSolution, KnapsackData> {

    // maximum total weight
    private final double maxWeight;
    
    public KnapsackConstraint(double maxWeight){
        this.maxWeight = maxWeight;
    }

    @Override
    public boolean isSatisfied(SubsetSolution solution, KnapsackData data) {
        // check: maximum weight not exceeded
        double sum = 0.0;
        for(int id : solution.getSelectedIDs()){
            sum += data.getWeight(id);
            if(sum > maxWeight){
                return false;
            }
        }
        return true;
    }
    
}
