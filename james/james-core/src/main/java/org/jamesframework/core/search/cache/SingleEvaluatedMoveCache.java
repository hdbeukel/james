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

package org.jamesframework.core.search.cache;

import org.jamesframework.core.problems.constraints.Validation;
import org.jamesframework.core.problems.objectives.Evaluation;
import org.jamesframework.core.search.neigh.Move;

/**
 * A single move cache only stores the last evaluation and validation that were offered to the cache.
 * Any previously cached value is immediately discarded and replaced with the new value. In general,
 * most neighbourhood searches select a single candidate move (random, best, ...) from the neighbourhood
 * in every step and then just decide whether or not to apply it to the current solution. Therefore, a
 * single move cache can be used to effectively cache the evaluation and validation of this candidate
 * move with minor memory overhead.
 * <p>
 * This is the default cache of any neighbourhood search.
 * 
 * @author <a href="mailto:herman.debeukelaer@ugent.be">Herman De Beukelaer</a>
 */
public class SingleEvaluatedMoveCache implements EvaluatedMoveCache {
    
    // single cached move evaluation
    private Move<?> evaluatedMove;
    private Evaluation evaluation;
    
    // single cached move validation
    private Move<?> validatedMove;
    private Validation validation;
    
    /**
     * Create an empty single evaluated move cache.
     */
    public SingleEvaluatedMoveCache(){
        clear();
    }

    /**
     * Cache the given evaluation, discarding any previously cached evaluations.
     * 
     * @param move move applied to the current solution
     * @param evaluation evaluation of obtained neighbour
     */
    @Override
    public final void cacheMoveEvaluation(Move<?> move, Evaluation evaluation) {
        evaluatedMove = move;
        this.evaluation = evaluation;
    }

    /**
     * Retrieve a cached evaluation, if still available. If the evaluation of any
     * other move has been cached at a later point in time, the value for this move
     * will have been overwritten.
     * 
     * @param move move applied to the current solution
     * @return cached evaluation of the obtained neighbour, if available, <code>null</code> if not
     */
    @Override
    public final Evaluation getCachedMoveEvaluation(Move<?> move) {
        if(evaluatedMove == null || !evaluatedMove.equals(move)){
            // cache miss
            return null;
        } else {
            // cache hit
            return evaluation;
        }
    }

    /**
     * Cache validation of the given move, discarding any previously cached value.
     * 
     * @param move move applied to the current solution
     * @param validation validation of obtained neighbour
     */
    @Override
    public final void cacheMoveValidation(Move<?> move, Validation validation) {
        validatedMove = move;
        this.validation = validation;
    }

    /**
     * Retrieve a cached validation, if still available. If the validation of any other move has
     * been cached at a later point in time, the value for this move will have been overwritten.
     * 
     * @param move move applied to the current solution
     * @return cached validation of the obtained neighbour, if available, <code>null</code> if not
     */
    @Override
    public final Validation getCachedMoveValidation(Move<?> move) {
        if(validatedMove == null || !validatedMove.equals(move)){
            // cache miss
            return null;
        } else {
            // cache hit
            return validation;
        }
    }

    /**
     * Clear all cached values.
     */
    @Override
    public final void clear() {
        evaluatedMove = null;
        evaluation = null;
        validatedMove = null;
        validation = null;
    }

}
