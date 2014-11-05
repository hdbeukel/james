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

import org.jamesframework.examples.knapsack.*;
import org.jamesframework.core.problems.constraints.PenalizingConstraint;
import org.jamesframework.core.problems.constraints.validations.PenalizingValidation;
import org.jamesframework.core.problems.constraints.validations.Validation;
import org.jamesframework.core.search.neigh.Move;
import org.jamesframework.core.subset.SubsetSolution;

/**
 * A penalizing constraint that assings penalties when the knapsack capacity is exceeded.
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
        // TODO
        return null;
    }
    
    @Override
    public PenalizingValidation validate(Move move, SubsetSolution curSolution, Validation curValidation, KnapsackData data) {
        // TODO
        return null;
    }
    
}
