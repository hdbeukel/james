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

package org.jamesframework.core.subset.neigh;

import org.jamesframework.core.subset.neigh.moves.SubsetMove;
import java.util.Collections;
import java.util.Random;
import java.util.Set;
import java.util.concurrent.ThreadLocalRandom;
import java.util.stream.Collectors;
import org.jamesframework.core.subset.SubsetSolution;
import org.jamesframework.core.subset.neigh.moves.DeletionMove;
import org.jamesframework.core.util.SetUtilities;

/**
 * <p>
 * A subset neighbourhood that generates deletion moves only (see {@link DeletionMove}). A deletion move is a subtype
 * of {@link SubsetMove} that removes a single ID from the selection of a subset solution. If desired, a set of fixed
 * IDs can be provided which are not allowed to be removed from the selection. Also, a size limit can be imposed so that
 * no moves are generated when the current solution has minimum size.
 * </p>
 * <p>
 * Note that this neighbourhood is thread-safe: it can be safely used to concurrently generate moves in different
 * searches running in separate threads.
 * </p>
 * 
 * @author <a href="mailto:herman.debeukelaer@ugent.be">Herman De Beukelaer</a>
 */
public class SingleDeletionNeighbourhood extends SubsetNeighbourhood {
    
    // minimum subset size
    private final int minSubsetSize;
    
    /**
     * Create a single deletion neighbourhood without minimum subset size.
     * All items are candidates to be deselected.
     */
    public SingleDeletionNeighbourhood(){
        this(0);
    }
    
    /**
     * Create a single deletion neighbourhood with a given minimum subset size.
     * No moves will be generated if the current solution has minimum size.
     * All items are candidates to be deselected.
     * 
     * @param minSubsetSize minimum subset size (&ge; 0)
     * @throws IllegalArgumentException if minimum subset size is negative
     */
    public SingleDeletionNeighbourhood(int minSubsetSize){
        this(minSubsetSize, null);
    }
    
    /**
     * Create a single deletion neighbourhood with a given minimum subset size
     * and a set of fixed IDs which are not allowed to be deselected. None of
     * the generated deletion moves will remove any of these IDs from the selection.
     * 
     * @param minSubsetSize minimum subset size (&ge; 0)
     * @param fixedIDs set of fixed IDs which are not allowed to be removed from the selection
     * @throws IllegalArgumentException if minimum subset size is negative
     */
    public SingleDeletionNeighbourhood(int minSubsetSize, Set<Integer> fixedIDs){
        super(fixedIDs);
        // check maximum subset size
        if(minSubsetSize < 0){
            throw new IllegalArgumentException("Error while creating single deletion neighbourhood: minimum subset size should be non-negative.");
        }
        this.minSubsetSize = minSubsetSize;
    }
    
    /**
     * Get the minimum subset size.
     * If no size limit has been applied this method returns 0.
     * 
     * @return minimum subset size
     */
    public int getMinSubsetSize() {
        return minSubsetSize;
    }
    
    /**
     * Generates a random deletion move for the given subset solution that removes a single ID from the selection.
     * Possible fixed IDs are not considered to be removed and the minimum subset size is taken into account.
     * If no deletion move can be generated, <code>null</code> is returned.
     * 
     * @param solution solution for which a random deletion move is generated
     * @return random deletion move, <code>null</code> if no move can be generated
     */
    @Override
    public SubsetMove getRandomMove(SubsetSolution solution) {
        // check minimum size
        if(minSizeReached(solution)){
            return null;
        }
        // get set of candidate IDs for deletion (possibly fixed IDs are discarded)
        Set<Integer> removeCandidates = getRemoveCandidates(solution);
        // check if removal is possible
        if(removeCandidates.isEmpty()){
            return null;
        }
        // use thread local random for better concurrent performance
        Random rg = ThreadLocalRandom.current();
        // select random ID to remove from selection
        int del = SetUtilities.getRandomElement(removeCandidates, rg);
        // create and return deletion move
        return new DeletionMove(del);
    }

    /**
     * Generates a set of all possible deletion moves that remove a single ID from the selection of a given
     * subset solution. Possible fixed IDs are not considered to be removed and the minimum subset size
     * is taken into account. May return an empty set if no deletion moves can be generated.
     * 
     * @param solution solution for which all possible deletion moves are generated
     * @return set of all deletion moves, may be empty
     */
    @Override
    public Set<SubsetMove> getAllMoves(SubsetSolution solution) {
        // check minimum size
        if(minSizeReached(solution)){
            return Collections.emptySet();
        }
        // get set of candidate IDs for deletion (possibly fixed IDs are discarded)
        Set<Integer> removeCandidates = getRemoveCandidates(solution);
        // check if there are any candidates to be removed
        if(removeCandidates.isEmpty()){
            return Collections.emptySet();
        }
        // create deletion move for all candidates
        return removeCandidates.stream()
                               .map(del -> new DeletionMove(del))
                               .collect(Collectors.toSet());
    }
    
    /**
     * Check whether the minimum subset size has been reached (or exceeded).
     * 
     * @param sol subset solution
     * @return <code>true</code> if the minimum size has been reached
     */
    private boolean minSizeReached(SubsetSolution sol){
        return sol.getNumSelectedIDs() <= minSubsetSize;
    }

}
