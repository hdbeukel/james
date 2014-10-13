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
import org.jamesframework.core.subset.neigh.moves.AdditionMove;
import org.jamesframework.core.util.SetUtilities;

/**
 * <p>
 * A subset neighbourhood that generates addition moves only (see {@link AdditionMove}). An addition move is a subtype
 * of {@link SubsetMove} that adds a single ID to the selection of a subset solution. If desired, a set of fixed IDs
 * can be provided which are not allowed to be added to the selection.
 * </p>
 * <p>
 * Note that this neighbourhood is thread-safe: it can be safely used to concurrently generate moves in different
 * searches running in separate threads.
 * </p>
 * 
 * @author <a href="mailto:herman.debeukelaer@ugent.be">Herman De Beukelaer</a>
 */
public class SingleAdditionNeighbourhood extends SubsetNeighbourhood {
    
    // maximum subset size
    private final int maxSubsetSize;
    
    /**
     * Create a single addition neighbourhood without any limit on the number of
     * selected items (subset size). All items are candidates to be selected.
     */
    public SingleAdditionNeighbourhood(){
        this(0);
    }
    
    /**
     * Create a single addition neighbourhood with a given limit on the number of
     * selected items (subset size). No moves will be generated if the maximum
     * subset size would be exceeded. All items are candidates to be selected.
     * 
     * @param maxSubsetSize maximum subset size
     */
    public SingleAdditionNeighbourhood(int maxSubsetSize){
        this(maxSubsetSize, null);
    }
    
    /**
     * Create a single addition neighbourhood with a limit on the number of selected
     * items (subset size) and a given set of fixed IDs which are not allowed to be
     * selected. None of the generated addition moves will add any of these IDs.
     * 
     * @param maxSubsetSize maximum subset size
     * @param fixedIDs set of fixed IDs which are not allowed to be added to the selection
     */
    public SingleAdditionNeighbourhood(int maxSubsetSize, Set<Integer> fixedIDs){
        super(fixedIDs);
        this.maxSubsetSize = maxSubsetSize;
    }
    
    /**
     * Get the maximum subset size.
     * 
     * @return maximum subset size
     */
    public int getMaxSubsetSize() {
        return maxSubsetSize;
    }
    
    /**
     * Generates a random addition move for the given subset solution that adds a single ID to the selection.
     * Possible fixed IDs are not considered to be added and the maximum subset size is taken into account.
     * If no addition move can be generated, <code>null</code> is returned.
     * 
     * @param solution solution for which a random addition move is generated
     * @return random addition move, <code>null</code> if no move can be generated
     */
    @Override
    public SubsetMove getRandomMove(SubsetSolution solution) {
        // check size limit
        if(solution.getNumSelectedIDs() >= maxSubsetSize){
            // size limit would be exceeded
            return null;
        }
        // get set of candidate IDs for addition (possibly fixed IDs are discarded)
        Set<Integer> addCandidates = getAddCandidates(solution);
        // check if addition is possible
        if(addCandidates.isEmpty()){
            // impossible to perform a swap
            return null;
        }
        // use thread local random for better concurrent performance
        Random rg = ThreadLocalRandom.current();
        // select random ID to add to selection
        int add = SetUtilities.getRandomElement(addCandidates, rg);
        // create and return addition move
        return new AdditionMove(add);
    }

    /**
     * Generates a set of all possible addition moves that add a single ID to the selection of a given
     * subset solution. Possible fixed IDs are not considered to be added and the maximum subset size
     * is taken into account. May return an empty set if no addition moves can be generated.
     * 
     * @param solution solution for which all possible addition moves are generated
     * @return set of all addition moves, may be empty
     */
    @Override
    public Set<SubsetMove> getAllMoves(SubsetSolution solution) {
        // check size limit
        if(solution.getNumSelectedIDs() >= maxSubsetSize){
            return Collections.emptySet();
        }
        // get set of candidate IDs for addition (possibly fixed IDs are discarded)
        Set<Integer> addCandidates = getAddCandidates(solution);
        // check if there are any candidates to be added
        if(addCandidates.isEmpty()){
            return Collections.emptySet();
        }
        // create addition move for all add candidates
        return addCandidates.stream()
                            .map(add -> new AdditionMove(add))
                            .collect(Collectors.toSet());
    }

}
