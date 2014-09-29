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

package org.jamesframework.core.problems.objectives;

import org.jamesframework.core.problems.Solution;
import org.jamesframework.core.search.neigh.Move;

/**
 * Interface of an objective function that evaluates solutions using underlying data.
 * An objective can be either maximizing or minimizing; in the former case increasing scores
 * indicate improvement, while in the latter case decreasing scores indicate improvement.
 * 
 * !!! TODO !!! --> explain full/delta evaluations
 * 
 * @author <a href="mailto:herman.debeukelaer@ugent.be">Herman De Beukelaer</a>
 * @param <SolutionType> solution type to be evaluated, required to extend {@link Solution}
 * @param <DataType> underlying data type
 */
public interface Objective<SolutionType extends Solution, DataType> {
    
    /**
     * <p>
     * Evaluates a given solution using the given data. Returns an object of type {@link Evaluation}.
     * The corresponding double value can be obtained by calling {@link Evaluation#getValue()} on the
     * evaluation object.
     * </p>
     * <p>
     * Used for full evaluation of solutions during execution of a search algorithm.
     * </p>
     * 
     * @param solution solution to evaluate
     * @param data underlying data used for evaluation
     * @return evaluation of the given solution
     */
    public Evaluation evaluate(SolutionType solution, DataType data);
    
    /**
     * <p>
     * Evaluates a move to be applied to the current solution of a local search. The result
     * corresponds to the evaluation of the modified solution that would be obtained by applying
     * the given move to the current solution. A default implementation is provided that (1) applies
     * the move to the current solution, (2) computes a full evaluation of the modified solution by
     * calling {@link #evaluate(Solution, Object)} and (3) undoes the applied move.
     * </p>
     * <p>
     * It is often possible to provide a custom, much more efficient delta evaluation that computes
     * the modified evaluation based on the current evaluation and the changes that will be made
     * when applying the move to the current solution. This can be done by overriding this method.
     * The given <code>move</code> and <code>curEvaluation</code> may be cast to a specific move
     * and evaluation type, respectively, where an appropriate error should be thrown when receiving
     * an incompatible move or evaluation type. Given that both this method and the full evaluation
     * return evaluations of the same type, it is guaranteed that <code>curEvaluation</code> will also
     * be of this specific type.
     * </p>
     * 
     * @param move move to evaluate
     * @param curSolution current solution
     * @param curEvaluation evaluation of current solution
     * @param data underlying data used for evaluation
     * @return evaluation of modified solution obtained when applying the move to the current solution
     * @throws ... !!! TODO !!!
     */
    default public Evaluation evaluate(Move<? super SolutionType> move,
                                       SolutionType curSolution,
                                       Evaluation curEvaluation,
                                       DataType data){
        // apply move
        move.apply(curSolution);
        // full evaluation
        Evaluation e = evaluate(curSolution, data);
        // undo move
        move.undo(curSolution);
        // return evaluation
        return e;
    }
    
    /**
     * Check whether the produced evaluations are to be minimized.
     * 
     * @return <code>true</code> if evaluations are to be minimized,
     *         <code>false</code> if they are to be maximized
     */
    public boolean isMinimizing();

}
