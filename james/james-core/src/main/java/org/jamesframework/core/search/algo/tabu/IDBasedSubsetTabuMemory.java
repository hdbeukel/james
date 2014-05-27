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

package org.jamesframework.core.search.algo.tabu;

import java.util.Queue;
import org.apache.commons.collections4.queue.CircularFifoQueue;
import org.jamesframework.core.exceptions.IncompatibleTabuMemoryException;
import org.jamesframework.core.problems.solutions.SubsetSolution;
import org.jamesframework.core.search.neigh.Move;
import org.jamesframework.core.search.neigh.subset.SubsetMove;

/**
 * An ID based subset tabu memory keeps track of recently added and deleted IDs (recently applied moves) and does not
 * allow that these IDs are added nor deleted to/from the current solution as long as they are contained in the limited
 * size tabu memory. This tabu memory can only be used for subset selection, in combination with neighbourhoods that
 * generate moves of type {@link SubsetMove}. The tabu memory casts all received moves to this specific type and throws
 * an {@link IncompatibleTabuMemoryException} if the cast fails.
 *
 * @author <a href="mailto:herman.debeukelaer@ugent.be">Herman De Beukelaer</a>
 */
public class IDBasedSubsetTabuMemory implements TabuMemory<SubsetSolution> {

    // limited size queue containing recently touched IDs
    private final Queue<Integer> memory;
    
    /**
     * Creates an ID based subset tabu memory with specified size. This memory stores recently added and deleted
     * IDs, inferred from recently applied subset moves. As long as an ID is contained in the limited size memory,
     * it can not be touched (not added nor deleted).
     * 
     * @param size memory size (strictly positive)
     * @throws IllegalArgumentException if <code>size</code> is not strictly positive
     */
    public IDBasedSubsetTabuMemory(int size){
        // verify size
        if(size <= 0){
            throw new IllegalArgumentException("Tabu memory size should be > 0.");
        }
        // create memory (limited size queue)
        memory = new CircularFifoQueue<>(size);
    }
    
    /**
     * A move is considered tabu if any involved ID (added or deleted) is currently contained in the tabu memory.
     * If not, the move is allowed. It is required that the given move is of type {@link SubsetMove}, else an
     * {@link IncompatibleTabuMemoryException} will be thrown. Note that the argument <code>currentSolution</code>
     * is not used here because the move itself contains all necessary information.
     * 
     * @param move subset move to be applied to the current solution (required to be of type {@link SubsetMove})
     * @param currentSolution current solution (not used here)
     * @return <code>true</code> if the current memory contains any ID which is added or deleted by the given move
     * @throws IncompatibleTabuMemoryException if the given move is not of type {@link SubsetMove}
     */
    @Override
    public boolean isTabu(Move<SubsetSolution> move, SubsetSolution currentSolution) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void registerVisitedSolution(SubsetSolution visitedSolution, Move<SubsetSolution> appliedMove) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

}
