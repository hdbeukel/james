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
import org.jamesframework.core.problems.objectives.Evaluation;
import org.jamesframework.core.search.neigh.Move;
import org.jamesframework.ext.problems.objectives.evaluations.WeightedIndexEvaluation;

/**
 * An index that is composed as a weighted sum of several objectives. A weighted index
 * is always maximizing, scores for contained minimizing objectives are negated so that
 * maximization of the weighted sum still corresponds to minimization of these objectives.
 * 
 * @author <a href="mailto:herman.debeukelaer@ugent.be">Herman De Beukelaer</a>
 * @param <SolutionType> solution type to be evaluated, required to extend {@link Solution}
 * @param <DataType> underlying data type
 */
public class WeightedIndex<SolutionType extends Solution, DataType> implements Objective<SolutionType, DataType>{

    // map containing underlying objectives and their weights
    private final Map<Objective<? super SolutionType, ? super DataType>, Double> weights;

    /**
     * Create an empty weighted index.
     */
    public WeightedIndex() {
        weights = new HashMap<>();
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
            weights.put(objective, weight);
        } else {
            throw new IllegalArgumentException("Error in weighted index: each objective's weight should be strictly positive.");
        }
    }
    
    /**
     * Remove an objective, if present. Returns <code>true</code> if the objective has been successfully
     * removed, <code>false</code> if it was not contained in the index.
     * 
     * @param objective objective to remove
     * @return <code>true</code> if the objective has been successfully removed
     */
    public boolean removeObjective(Objective<? super SolutionType, ? super DataType> objective){
        if(weights.containsKey(objective)){
            weights.remove(objective);
            return true;
        } else {
            return false;
        }
    }
    
    /**
     * Weighted index is always maximizing, so this method always returns <code>false</code>.
     * Scores for contained minimizing objectives are negated, i.e. their sign is flipped.
     * 
     * @return <code>false</code>
     */
    @Override
    public boolean isMinimizing(){
        return false;
    }

    /**
     * Produces an evaluation object that reflects the weighted sum of evaluations of all underlying objectives.
     * 
     * @param solution solution to evaluate
     * @param data data to be used for evaluation
     * @return weighted index evaluation
     */
    @Override
    public WeightedIndexEvaluation evaluate(SolutionType solution, DataType data) {
        // initialize evaluation object
        WeightedIndexEvaluation eval = new WeightedIndexEvaluation();
        // add evaluations produced by contained objectives
        weights.keySet().forEach(obj -> {
            // evaluate solution using objective
            Evaluation objEval = obj.evaluate(solution, data);
            // flip weight sign if minimizing
            double w = weights.get(obj);
            if(obj.isMinimizing()){
                w = -w;
            }
            // register in weighted index evaluation
            eval.addEvaluation(obj, objEval, w);
        });
        // return weighted index evaluation
        return eval;
    }

    /**
     * Delta evaluation. Computes a delta evaluation for each contained objective and wraps
     * the obtained modified evaluations in a new weighted index evaluation.
     * 
     * @param move move to be evaluated
     * @param curSolution current solution of a local search
     * @param curEvaluation evaluation of current solution
     * @param data data to be used for evaluation
     * @param <ActualSolutionType> the actual solution type of the problem that is being solved;
     *                             a subtype of the solution types of both the objective and the applied move
     * @return weighted index evaluation of obtained neighbour when applying the move
     */
    @Override
    public <ActualSolutionType extends SolutionType> WeightedIndexEvaluation evaluate(Move<? super ActualSolutionType> move,
                                                                                      ActualSolutionType curSolution,
                                                                                      Evaluation curEvaluation,
                                                                                      DataType data) {
        // cast current evaluation object
        WeightedIndexEvaluation curEval = (WeightedIndexEvaluation) curEvaluation;
        // initialize new evaluation object
        WeightedIndexEvaluation newEval = new WeightedIndexEvaluation();
        // compute delta evaluation for each contained objective
        weights.keySet().forEach(obj -> {
            // extract current evaluation
            Evaluation objCurEval = curEval.getEvaluation(obj);
            // delta evaluation of contained objective
            Evaluation objNewEval = obj.evaluate(move, curSolution, objCurEval, data);
            // flip weight sign if minimizing
            double w = weights.get(obj);
            if(obj.isMinimizing()){
                w = -w;
            }
            // register in new weighted index evaluation
            newEval.addEvaluation(obj, objNewEval, w);
        });
        // return new evaluation
        return newEval;
    }
    
    

}
