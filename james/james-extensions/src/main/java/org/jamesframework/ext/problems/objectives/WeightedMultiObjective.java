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

package org.jamesframework.ext.problems.objectives;

import java.util.HashMap;
import java.util.Map;
import org.jamesframework.core.problems.objectives.Objective;
import org.jamesframework.core.problems.Solution;

/**
 * Implements an objective which calculates a weighted sum of other objectives. A weighted multi-objective
 * is always maximizing, scores for contained minimizing objectives are inverted so that maximization of
 * the weighted sum still corresponds to minimization of these objectives.
 * 
 * @author <a href="mailto:herman.debeukelaer@ugent.be">Herman De Beukelaer</a>
 * @param <SolutionType> solution type to be evaluated, required to extend {@link Solution}
 * @param <DataType> underlying data type
 */
public class WeightedMultiObjective<SolutionType extends Solution, DataType> implements Objective<SolutionType, DataType>{

    // map containing underlying objectives and their weights
    private final Map<Objective<? super SolutionType, ? super DataType>, Double> objectives;

    /**
     * Create an empty weighted multi-objective.
     */
    public WeightedMultiObjective() {
        objectives = new HashMap<>();
    }
    
    /**
     * Add an objective with corresponding weight. Any objective designed for the solution type and data type of this
     * multi-objective (or for more general solution or data types) can be added. The specified weight should be strictly
     * positive, if not, an exception will be thrown.
     * 
     * @param objective objective to be added
     * @param weight corresponding weight (strictly positive)
     * @throws IllegalArgumentException if the specified weight is not strictly positive
     */
    public void addObjective(Objective<? super SolutionType, ? super DataType> objective, double weight) {
        // check weight
        if(weight > 0.0){
            // add objective to map
            objectives.put(objective, weight);
        } else {
            throw new IllegalArgumentException("Error in weighted multi-objective: each objective's weight should be strictly positive.");
        }
    }
    
    /**
     * Remove an objective, if present. Returns true if the objective has been successfully removed, false
     * if it was not contained in this multi-objective.
     * 
     * @param objective objective to remove
     * @return true if the objective was successfully removed
     */
    public boolean removeObjective(Objective<? super SolutionType, ? super DataType> objective){
        if(objectives.containsKey(objective)){
            objectives.remove(objective);
            return true;
        } else {
            return false;
        }
    }
    
    /**
     * Weighted multi-objective is always maximizing, so this method always returns false. Scores for contained minimizing
     * objectives are inverted.
     * 
     * @return false
     */
    @Override
    public boolean isMinimizing(){
        return false;
    }

    /**
     * Computes the weighted sum of evaluations of all underlying objectives.
     * 
     * @param solution solution to evaluate
     * @param data data to be used for evaluation
     * @return weighted sum of evaluations of underlying objectives
     */
    @Override
    public double evaluate(SolutionType solution, DataType data) {
        double eval = 0.0, partial;
        // go through objectives
        for(Objective<? super SolutionType, ? super DataType> obj : objectives.keySet()){
            // evaluate solution using objective
            partial = obj.evaluate(solution, data);
            // multiply with weight
            partial *= objectives.get(obj);
            // update sum
            if(obj.isMinimizing()){
                // minimizing: subtract from sum
                eval -= partial;
            } else {
                // maximizing: add to sum
                eval += partial;
            }
        }
        // return weighted sum
        return eval;
    }

}
