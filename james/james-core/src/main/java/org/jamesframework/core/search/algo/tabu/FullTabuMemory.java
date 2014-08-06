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

package org.jamesframework.core.search.algo.tabu;

import org.jamesframework.core.util.FastLimitedQueue;
import org.jamesframework.core.problems.Solution;
import org.jamesframework.core.search.neigh.Move;

/**
 * Full tabu memory that stores deeps copies of recently visited solutions and declares a move tabu if
 * applying it to the current solution yields a neighbouring solution which is currently contained in
 * the memory. A full tabu memory has a single parameter controlling the size of the memory, i.e. the
 * number of recently visited solutions which are simultaneously stored. If the size is exceeded, the
 * least recently visited solution is discarded from the memory (FIFO).
 * 
 * @param <SolutionType> solution type of the tabu memory, required to extend
 *                       {@link Solution}; should match with the solution type
 *                       of the tabu search that uses this memory
 * @author <a href="mailto:herman.debeukelaer@ugent.be">Herman De Beukelaer</a>
 */
public class FullTabuMemory<SolutionType extends Solution> implements TabuMemory<SolutionType> {
    
    // limited size queue containing recently visited solutions (deep copies)
    private final FastLimitedQueue<SolutionType> memory;
    
    /**
     * Creates a full tabu memory with specified size. This memory stores deep copies of recently
     * visited solutions, where the least recently visited solution is discarded if the memory
     * size is exceeded.
     * 
     * @param size memory size (strictly positive)
     * @throws IllegalArgumentException if <code>size</code> is not strictly positive
     */
    public FullTabuMemory(int size){
        // verify size
        if(size <= 0){
            throw new IllegalArgumentException("Tabu memory size should be > 0.");
        }
        // create memory (limited size queue)
        memory = new FastLimitedQueue<>(size);
    }
    
    /**
     * Verifies whether the given move is tabu by applying it to the current solution and checking if the obtained
     * neighbour is currently contained in the tabu memory. If not, the move is allowed. Before returning, the move
     * is undone to restore the original state of the current solution.
     * 
     * @param move move to be applied to the current solution
     * @param currentSolution current solution
     * @return <code>true</code> if the neighbour obtained by applying the given move to the current solution is
     *         currently already contained in the tabu memory
     */
    @Override
    public boolean isTabu(Move<? super SolutionType> move, SolutionType currentSolution) {
        // apply move
        move.apply(currentSolution);
        // check: contained in tabu memory?
        boolean tabu = memory.contains(currentSolution);
        // undo move
        move.undo(currentSolution);
        // return result
        return tabu;
    }

    /**
     * A newly visited solution is registered by storing a deep copy of this solution in the full tabu memory.
     * 
     * @param visitedSolution newly visited solution (copied to memory)
     * @param appliedMove applied move (not used here, can be <code>null</code>)
     */
    @Override
    public void registerVisitedSolution(SolutionType visitedSolution, Move<? super SolutionType> appliedMove) {
        // store deep copy of newly visited solution
        memory.add(Solution.checkedCopy(visitedSolution));
    }

    /**
     * Clear the tabu memory.
     */
    @Override
    public void clear() {
        memory.clear();
    }

}
