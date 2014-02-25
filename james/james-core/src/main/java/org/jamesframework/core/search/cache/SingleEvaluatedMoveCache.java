//  Copyright 2014 Herman De Beukelaer
//
//  Licensed under the Apache License, Version 2.0 (the "License");
//  you may not use this file except in compliance with the License.
//  You may obtain a copy of the License at
//
//      http://www.apache.org/licenses/LICENSE-2.0
//
//  Unless required by applicable law or agreed to in writing, software
//  distributed under the License is distributed on an "AS IS" BASIS,
//  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
//  See the License for the specific language governing permissions and
//  limitations under the License.

package org.jamesframework.core.search.cache;

import org.jamesframework.core.search.neigh.Move;

/**
 * A single move cache only stores the last evaluation and validity that were offered to the cache.
 * Any previously cached value is immediately discarded and replaced with the new value. In general,
 * most neighbourhood searches select a single candidate move (random, best, ...) from the neighbourhood
 * in every step and then merely decide whether or not to apply it to the current solution. Therefore, a
 * single move cache can be used to appropriately cache the validity and evaluation of this candidate move,
 * with minor memory overhead.
 * 
 * @author Herman De Beukelaer <herman.debeukelaer@ugent.be>
 */
public class SingleEvaluatedMoveCache implements EvaluatedMoveCache {
    
    // single cached move evaluation
    private Move<?> evaluatedMove;
    private Double evaluation;
    
    // single cached move validity
    private Move<?> validatedMove;
    private Boolean rejected;
    
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
    public final void cacheMoveEvaluation(Move<?> move, double evaluation) {
        evaluatedMove = move;
        this.evaluation = evaluation;
    }

    /**
     * Retrieve a cached evaluation, if still available. If the evaluation of any
     * other move has been cached at a later point in time, it will have overwritten
     * the evaluation of this move.
     * 
     * @param move move applied to the current solution
     * @return cached evaluation of the obtained neighbour, if available, <code>null</code> if not
     */
    @Override
    public final Double getCachedMoveEvaluation(Move<?> move) {
        if(evaluatedMove == null || !evaluatedMove.equals(move)){
            // cache miss
            return null;
        } else {
            // cache hit
            return evaluation;
        }
    }

    /**
     * Cache rejection of the given move, discarding any previously cached value.
     * 
     * @param move move applied to the current solution
     * @param isRejected indicates whether the obtained neighbour is rejected
     */
    @Override
    public final void cacheMoveRejection(Move<?> move, boolean isRejected) {
        validatedMove = move;
        rejected = isRejected;
    }

    /**
     * Retrieve cached rejection, if still available. If rejection of any other move has
     * been cached at a later point in time, the value for this move will have been overwritten.
     * 
     * @param move move applied to the current solution
     * @return <code>true</code> if the obtained neighbour is known to be rejected,
     *         <code>null</code> if the requested value is not available in the cache
     */
    @Override
    public final Boolean getCachedMoveRejection(Move<?> move) {
        if(validatedMove == null || !validatedMove.equals(move)){
            // cache miss
            return null;
        } else {
            // cache hit
            return rejected;
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
        rejected = null;
    }

}
