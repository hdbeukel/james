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

package org.jamesframework.examples.knapsack2;

import java.util.Iterator;
import org.jamesframework.examples.knapsack.*;
import org.jamesframework.core.problems.constraints.PenalizingConstraint;
import org.jamesframework.core.problems.constraints.validations.PenalizingValidation;
import org.jamesframework.core.problems.constraints.validations.SimplePenalizingValidation;
import org.jamesframework.core.subset.SubsetSolution;

/**
 * A penalizing constraint that assigns penalties when the knapsack capacity is exceeded.
 * IMPORTANT: requires that IDs are ordered based on the weight of the corresponding
 *            item (descending); if not, the result of validation is arbitrary.
 * 
 * @author <a href="mailto:herman.debeukelaer@ugent.be">Herman De Beukelaer</a>
 */
public class PenalizingKnapsackConstraint implements PenalizingConstraint<SubsetSolution, KnapsackData> {
    
    // maximum total weight
    private final double maxWeight;
    // highest profit (of all items in the data set)
    private final double highestProfitPerItem;
    
    public PenalizingKnapsackConstraint(double maxWeight, double highestProfitPerItem){
        this.maxWeight = maxWeight;
        this.highestProfitPerItem = highestProfitPerItem;
    }

    @Override
    public PenalizingValidation validate(SubsetSolution solution, KnapsackData data) {
        
        // step 1: compute total weight of selected items
        double curWeight = solution.getSelectedIDs().stream().mapToDouble(data::getWeight).sum();
        
        // step 2: compute minimum number of items to remove from selection to satisfy constraint
        int minRemove = 0;
        Iterator<Integer> it = solution.getSelectedIDs().iterator();
        // continue until capacity is not exceeded
        while(it.hasNext() && curWeight > maxWeight){
            // subtract weight of next most heavy item to be removed
            curWeight -= data.getWeight(it.next());
            // update counter
            minRemove++;
        }
        
        // step 3: produce penalizing validation
        if(minRemove > 0){
            // assign penalty (capacity exceeded)
            double penalty = minRemove * (highestProfitPerItem + 1);
            return SimplePenalizingValidation.FAILED(penalty);
        } else {
            // constraint satisfied
            return SimplePenalizingValidation.PASSED;
        }
        
    }
    
}
