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

import java.util.Collection;
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
    private final FastLimitedQueue<Integer> memory;
    
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
        memory = new FastLimitedQueue<>(size);
    }
    
    /**
     * A move is considered tabu if any involved ID (added or deleted) is currently contained in the tabu memory.
     * If not, the move is allowed. It is required that the given move is of type {@link SubsetMove}, else an
     * {@link IncompatibleTabuMemoryException} will be thrown. Note that the argument <code>currentSolution</code>
     * is not used here, because the move itself contains all necessary information, and may be <code>null</code>.
     * 
     * @param move subset move to be applied to the current solution (required to be of type {@link SubsetMove})
     * @param currentSolution current solution (not used here, may be <code>null</code>)
     * @return <code>true</code> if the current memory contains any ID which is added or deleted by the given move
     * @throws IncompatibleTabuMemoryException if the given move is not of type {@link SubsetMove}
     */
    @Override
    public boolean isTabu(Move<? super SubsetSolution> move, SubsetSolution currentSolution) {
        // check move type
        if(move instanceof SubsetMove){
            // cast
            SubsetMove sMove = (SubsetMove) move;
            // check if any involved ID is tabu
            return containsTabuID(sMove.getAddedIDs()) || containsTabuID(sMove.getDeletedIDs());
        } else {
            // wrong move type
            throw new IncompatibleTabuMemoryException("ID based subset tabu memory can only be used in combination with "
                                                    + "neighbourhoods that generate moves of type SubsetMove. Received: "
                                                    + move.getClass().getName());
        }
    }
    
    /**
     * Checks whether the given collection of IDs contains any ID which is currently tabu.
     * 
     * @param ids collection of ids
     * @return <code>true</code> if <code>ids</code> contains any ID which is currently tabu
     */
    private boolean containsTabuID(Collection<Integer> ids){
        for(int  ID : ids){
            if(memory.contains(ID)){
                return true;
            }
        }
        return false;
    }

    /**
     * Registers an applied subset move by storing all involved IDs (added or deleted) in the tabu memory. It is required
     * that the given move is of type {@link SubsetMove}, else an {@link IncompatibleTabuMemoryException} will be thrown.
     * The argument <code>visitedSolution</code> is ignored, as the applied move contains all necessary information, and
     * may be <code>null</code>. If <code>appliedMove</code> is <code>null</code>, calling this method does not have any
     * effect.
     * 
     * @param visitedSolution newly visited solution (not used here, may be <code>null</code>)
     * @param appliedMove applied move of which all involved IDs are stored in the tabu memory
     * @throws IncompatibleTabuMemoryException if the given move is not of type {@link SubsetMove}
     */
    @Override
    public void registerVisitedSolution(SubsetSolution visitedSolution, Move<? super SubsetSolution> appliedMove) {
        // don't do anything if move is null
        if(appliedMove != null){
            // check move type
            if(appliedMove instanceof SubsetMove){
                // cast
                SubsetMove sMove = (SubsetMove) appliedMove;
                // store involved IDs
                memory.addAll(sMove.getAddedIDs());
                memory.addAll(sMove.getDeletedIDs());
            } else {
                // wrong move type
                throw new IncompatibleTabuMemoryException("ID based subset tabu memory can only be used in combination with "
                                                        + "neighbourhoods that generate moves of type SubsetMove. Received: "
                                                        + appliedMove.getClass().getName());
            }
        }
    }
    
    /**
     * Clear the tabu memory.
     */
    @Override
    public void clear() {
        memory.clear();
    }

}
