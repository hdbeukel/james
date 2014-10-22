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

import org.jamesframework.core.problems.Problem;
import org.jamesframework.core.problems.Solution;
import org.jamesframework.core.problems.constraints.validations.Validation;
import org.jamesframework.core.problems.objectives.evaluations.Evaluation;
import org.jamesframework.core.search.neigh.Move;

/**
 * Interface of a cache that stores validations (see {@link Problem#validate(Solution)}) and
 * evaluations (see {@link Problem#evaluate(Solution)}) of neighbours obtained by applying moves
 * to the current solution in a neighbourhood search. Whenever a move is validated and evaluated, the
 * computed values may be presented to the cache. They can be retrieved later, when the same move is
 * validated or evaluated again for the same current solution, if these value are still contained
 * in the cache at that time.
 * <p>
 * In general, there is no guarantee that cached values will still be available at any later
 * point in time. Every cache implementation may handle cache requests in a specific way, and may
 * store a different number and/or specific selection of values. The only guarantee is that if a
 * value is retrieved from the cache, it will be correct.
 * <p>
 * When the current solution of a neighbourhood search is modified, any move cache used by this
 * search should be cleared using {@link #clear()} as then the cache is no longer valid.
 * <p>
 * Note that when using a cache implementation that stores multiple values, it may be beneficial
 * to override {@link Object#equals(Object)} and {@link Object#hashCode()} in the moves generated
 * by the applied neighbourhood, to increase the probability of a cache hit.
 * 
 * @author <a href="mailto:herman.debeukelaer@ugent.be">Herman De Beukelaer</a>
 */
public interface EvaluatedMoveCache {

    /**
     * Request to cache the evaluation (see {@link Problem#evaluate(Solution)}) of the neighbouring
     * solution which is obtained by applying the given move to the current solution. The specific cache
     * implementation may decide whether to store the given value and/or discard previously cached values.
     * 
     * @param move move applied to the current solution
     * @param evaluation evaluation of the obtained neighbour
     */
    public void cacheMoveEvaluation(Move<?> move, Evaluation evaluation);
    
    /**
     * Retrieve the cached evaluation (see {@link Problem#evaluate(Solution)}) of the neighbouring
     * solution which is obtained by applying the given move to the current solution, if available.
     * If this evaluation is not (or no longer) available in the cache, <code>null</code> is returned.
     * 
     * @param move move applied to the current solution
     * @return evaluation of the obtained neighbour, <code>null</code> if not available in the cache
     */
    public Evaluation getCachedMoveEvaluation(Move<?> move);
    
    /**
     * Request to cache the validation (see {@link Problem#validate(Solution)}) of the neighbouring
     * solution which is obtained by applying the given move to the current solution. The specific cache
     * implementation may decide whether to store the given value and/or discard previously cached values.
     * 
     * @param move move applied to the current solution
     * @param validation validation of the obtained neighbour
     */
    public void cacheMoveValidation(Move<?> move, Validation validation);
    
    /**
     * Retrieve the cached validation (see {@link Problem#validate(Solution)}) of the neighbouring
     * solution which is obtained by applying the given move to the current solution, if available.
     * If this validation is not (or no longer) available in the cache, <code>null</code> is returned.
     * 
     * @param move move applied to the current solution
     * @return validation of the obtained neighbour, <code>null</code> if not available in the cache
     */
    public Validation getCachedMoveValidation(Move<?> move);
    
    /**
     * Clears all cached values.
     */
    public void clear();
    
}
