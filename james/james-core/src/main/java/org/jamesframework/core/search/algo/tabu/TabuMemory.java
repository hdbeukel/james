/*
 * Copyright 2014 Herman De Beukelaer
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

package org.jamesframework.core.search.algo.tabu;

import org.jamesframework.core.exceptions.IncompatibleTabuMemoryException;
import org.jamesframework.core.problems.solutions.Solution;
import org.jamesframework.core.search.neigh.Move;

/**
 * Interface of tabu memory which is used by tabu search and keeps track of
 * recently visited solutions, properties of those solutions and/or recent
 * modifications made to the current solution. The tabu memory indicates
 * whether a move is tabu, given the current solution to which it will be
 * applied, and given that every newly visited solution is registered in
 * the tabu memory.
 * 
 * @param <SolutionType> solution type of the tabu memory, required to extend
 *                       {@link Solution}; should match with the solution type
 *                       of the tabu search that uses this memory
 * @author <a href="mailto:herman.debeukelaer@ugent.be">Herman De Beukelaer</a>
 */
public interface TabuMemory<SolutionType extends Solution> {
    
    /**
     * Indicates whether the given move is tabu, considering the current solution to which it will be applied
     * and the current state of the tabu memory. If this method returns <code>true</code>, the respective move
     * should not be considered by the search. If desired, the given move may be cast to a specific move type.
     * If this cast fails, an {@link IncompatibleTabuMemoryException} might be thrown.
     * <p>
     * Note that if the specified current solution is modified by this method, it should be restored to its
     * original state before returning.
     * 
     * @param move move that will be applied to the given current solution
     * @param currentSolution current solution
     * @return <code>true</code> if the given move is tabu and should not be considered
     */
    public boolean isTabu(Move<SolutionType> move, SolutionType currentSolution);
    
    /**
     * Register a newly visited solution in the tabu memory. This method should be called whenever the
     * current solution has been updated, i.e. whenever a new solution is accepted, so that the tabu
     * memory is updated accordingly.
     * <p>
     * Note that if the specified newly visited solution is modified by this method, it should be restored
     * to its original state before returning.
     * 
     * @param visitedSolution newly visited solution (accepted new current solution)
     * @param appliedMove move that has been applied to obtain the new solution, if any; can be <code>null</code>
     *                    otherwise, e.g. when updating the memory after setting a custom initial solution
     */
    public void registerVisitedSolution(SolutionType visitedSolution, Move<SolutionType> appliedMove);
    
}
