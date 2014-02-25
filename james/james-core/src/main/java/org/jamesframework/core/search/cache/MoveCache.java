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

import org.jamesframework.core.problems.Problem;
import org.jamesframework.core.search.neigh.Move;

/**
 * Interface of a cache that records validity (see {@link Problem#rejectSolution(Solution)}) and
 * evaluations (see {@link Problem#evaluate(Solution)}) of neighbours obtained by applying moves
 * to the current solution in a neighbourhood search. Whenever a move is validated and evaluated, the
 * computed values may be offered to the cache. They can be retrieved later, when the same move is
 * validated or evaluated again for the same current solution, if these value are still contained
 * in the cache at that time.
 * <p>
 * In general, there is no guarantee that cached values will still be available at any later
 * point in time. Every cache implementation may handle cache requests in a specific way, and may
 * store a different number and/or specific selection of values. The only guarantee is that if a
 * value is retrieved from the cache, it will be correct.
 * <p>
 * When the current solution of a neighbourhood search is modified, any move cache used by this
 * search should be cleared using {@link #clear()}, as then it is no longer valid.
 * <p>
 * Note that when using a cache implementation that stores multiple values, it may be beneficial
 * to override {@link Object#equals(Object)} and {@link Object#hashCode()} in the moves generated
 * by the applied neighbourhood, to increase the number of cache hits.
 * 
 * @author Herman De Beukelaer <herman.debeukelaer@ugent.be>
 */
public interface MoveCache {

    /**
     * Request to cache the evaluation (see {@link Problem#evaluate(Solution)}) of the neighbouring
     * solution which is obtained by applying the given move to the current solution. The specific cache
     * implementation may decide whether to store the given value and/or discard previously cached values.
     * 
     * @param move move applied to the current solution
     * @param evaluation evaluation of the obtained neighbour
     */
    public void cacheMoveEvaluation(Move<?> move, double evaluation);
    
    /**
     * Retrieve the cached evaluation (see {@link Problem#evaluate(Solution)}) of the neighbouring
     * solution which is obtained by applying the given move to the current solution, if available.
     * If this evaluation is not (or no longer) available in the cache, <code>null</code> is returned.
     * Else, the returned value is guaranteed to be correct.
     * 
     * @param move move applied to the current solution
     * @return evaluation of the obtained neighbour, <code>null</code> if not available in the cache
     */
    public Double getCachedMoveEvaluation(Move<?> move);
    
    /**
     * Request to cache rejection (see {@link Problem#rejectSolution(Solution)}) of the neighbouring
     * solution which is obtained by applying the given move to the current solution. The specific cache
     * implementation may decide whether to store the given value and/or discard previously cached values.
     * 
     * @param move move applied to the current solution
     * @param isRejected indicates whether the obtained neighbour is rejected
     */
    public void cacheMoveRejection(Move<?> move, boolean isRejected);
    
    /**
     * Retrieve cached rejection (see {@link Problem#evaluate(Solution)}) of the neighbouring
     * solution which is obtained by applying the given move to the current solution, if available.
     * If this evaluation is not (or no longer) available in the cache, <code>null</code> is returned.
     * Else, <code>true</code> is returned if the obtained neighbour is rejected, and <code>false</code>
     * is returned in case of a valid neighbour.
     * 
     * @param move move applied to the current solution
     * @return <code>true</code> if obtained neighbour is rejected, <code>null</code> if not available in the cache
     */
    public Boolean getCachedMoveRejection(Move<?> move);
    
    /**
     * Clears all cached values. This method should at least be called whenever the current solution
     * has been modified, as then the cached values are no longer valid.
     */
    public void clear();
    
}
