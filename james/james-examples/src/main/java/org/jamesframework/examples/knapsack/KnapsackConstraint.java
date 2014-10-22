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

import org.jamesframework.core.exceptions.IncompatibleDeltaEvaluationException;
import org.jamesframework.core.problems.constraints.Constraint;
import org.jamesframework.core.problems.constraints.validations.Validation;
import org.jamesframework.core.search.neigh.Move;
import org.jamesframework.core.subset.SubsetSolution;
import org.jamesframework.core.subset.neigh.moves.SubsetMove;

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
    public Validation validate(SubsetSolution solution, KnapsackData data) {
        // compute sum of weights of selected items
        double weight = solution.getSelectedIDs().stream().mapToDouble(data::getWeight).sum();
        // return custom validation object
        return new KnapsackValidation(weight);
    }
    
    @Override
    public Validation validate(Move move, SubsetSolution curSolution, Validation curValidation, KnapsackData data) {
        // check move type
        if(!(move instanceof SubsetMove)){
            throw new IncompatibleDeltaEvaluationException("Knapsack constraint should be used in combination "
                                                + "with neighbourhoods that generate moves of type SubsetMove.");
        }
        // cast move
        SubsetMove subsetMove = (SubsetMove) move;
        // cast current validation object (known to be of the required type as both 'validate'-methods return such object)
        KnapsackValidation kVal = (KnapsackValidation) curValidation;
        // extract current sum of weights
        double weight = kVal.getCurWeight();
        // account for added items
        weight += subsetMove.getAddedIDs().stream().mapToDouble(data::getWeight).sum();
        // account for removed items
        weight -= subsetMove.getDeletedIDs().stream().mapToDouble(data::getWeight).sum();
        // return updated validation
        return new KnapsackValidation(weight);
    }
    
    /**
     * Private class implementing custom validation object.
     */
    private class KnapsackValidation implements Validation {

        private final double curWeight;

        public KnapsackValidation(double curWeight) {
            this.curWeight = curWeight;
        }
        
        @Override
        public boolean passed() {
            return curWeight <= maxWeight;
        }

        public double getCurWeight() {
            return curWeight;
        }
        
    }
    
}
