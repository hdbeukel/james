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

package org.jamesframework.core.subset.neigh.adv;

import java.util.ArrayList;
import java.util.Collections;
import org.jamesframework.core.subset.neigh.moves.GeneralSubsetMove;
import java.util.List;
import java.util.Random;
import java.util.Set;
import java.util.concurrent.ThreadLocalRandom;
import java.util.stream.IntStream;
import org.jamesframework.core.subset.SubsetSolution;
import org.jamesframework.core.subset.neigh.SingleAdditionNeighbourhood;
import org.jamesframework.core.subset.neigh.moves.SubsetMove;
import org.jamesframework.core.subset.neigh.SubsetNeighbourhood;
import org.jamesframework.core.util.SetUtilities;
import org.jamesframework.core.util.SubsetIterator;

/**
 * <p>
 * A subset neighbourhood that generates moves which simultaneously add up to \(k\) items to the selection,
 * where \(k\) is specified at construction (or unlimited). Generated moves are of type {@link GeneralSubsetMove},
 * which is a subtype of {@link SubsetMove}, and always only add IDs to the selection. If desired, a set of
 * fixed IDs can be provided which are not allowed to be added. Also, a size limit can be imposed so that
 * it is guaranteed that the subset solution will not exceed this size after application of a move generated
 * by this neighbourhood.
 * </p>
 * <p>
 * Note that a very large amount of moves may be generated when the dataset size increases. Therefore, this
 * advanced neighbourhood should be used with care, especially in combination with searches that inspect
 * all moves in every step. Furthermore, searches that inspect random moves may have few chances to find an
 * improvement in case of a huge amount of possible neighbours.
 * </p>
 * <p>
 * This neighbourhood is thread-safe: it can be safely used to concurrently generate moves in different searches
 * running in separate threads.
 * </p>
 * 
 * @author <a href="mailto:herman.debeukelaer@ugent.be">Herman De Beukelaer</a>
 */
public class MultiAdditionNeighbourhood extends SubsetNeighbourhood {

    // maximum number of simultaneous additions
    private final int maxAdditions;
    // maximum subset size
    private final int maxSubsetSize;
    
    /**
     * Creates a multi addition neighbourhood that may add an unlimited number of IDs to the selection.
     * The actual number of performed additions is then only limited by the number of unselected items,
     * as it is impossible to add more items.
     */
    public MultiAdditionNeighbourhood(){
        this(Integer.MAX_VALUE);
    }
    
    /**
     * Creates a multi addition neighbourhood that may simultaneously add up to the specified number of
     * IDs to the selection. If <code>maxAdditions</code> is 1, this neighbourhood generates exactly the
     * same moves as the {@link SingleAdditionNeighbourhood} so in such case it is advised to use the latter
     * neighbourhood which has been optimized for this specific scenario.
     * 
     * @param maxAdditions maximum number of added IDs (&gt; 0)
     * @throws IllegalArgumentException if <code>maxAdditions</code> is not strictly positive
     */
    public MultiAdditionNeighbourhood(int maxAdditions){
        this(maxAdditions, Integer.MAX_VALUE);
    }
    
    /**
     * Creates a multi addition neighbourhood that may simultaneously add up to the specified number of
     * IDs to the selection, taking into account that the given maximum subset size can not be exceeded.
     * If <code>maxAdditions</code> is 1, this neighbourhood generates exactly the same moves as the
     * {@link SingleAdditionNeighbourhood} so in such case it is advised to use the latter neighbourhood
     * which has been optimized for this specific scenario.
     * 
     * @param maxAdditions maximum number of added IDs (&gt; 0)
     * @param maxSubsetSize maximum subset size (&gt; 0)
     * @throws IllegalArgumentException if <code>maxAdditions</code> or <code>maxSubsetSize</code>
     *                                  are not strictly positive
     */
    public MultiAdditionNeighbourhood(int maxAdditions, int maxSubsetSize){
        this(maxAdditions, maxSubsetSize, null);
    }
    
    /**
     * Creates a multi addition neighbourhood that may simultaneously add up to the specified number of
     * IDs to the selection, taking into account that the given maximum subset size can not be exceeded
     * and that the given set of fixed IDs are not allowed to be selected. If <code>maxAdditions</code>
     * is 1, this neighbourhood generates exactly the same moves as the {@link SingleAdditionNeighbourhood}
     * so in such case it is advised to use the latter neighbourhood which has been optimized for this
     * specific scenario.
     * 
     * @param maxAdditions maximum number of added IDs (&gt; 0)
     * @param maxSubsetSize maximum subset size (&gt; 0)
     * @param fixedIDs set of fixed IDs which are not allowed to be added to the selection
     * @throws IllegalArgumentException if <code>maxAdditions</code> or <code>maxSubsetSize</code>
     *                                  are not strictly positive
     */
    public MultiAdditionNeighbourhood(int maxAdditions, int maxSubsetSize, Set<Integer> fixedIDs){
        super(fixedIDs);
        // check maximum number of additions
        if(maxAdditions <= 0){
            throw new IllegalArgumentException("The maximum number of additions should be strictly positive.");
        }
        if(maxSubsetSize <= 0){
            throw new IllegalArgumentException("The maximum subset size should be strictly positive.");
        }
        this.maxAdditions = maxAdditions;
        this.maxSubsetSize = maxSubsetSize;
    }

    /**
     * Get the maximum number of additions performed by generated moves.
     * If no limit has been set this method returns {@link Integer#MAX_VALUE}.
     * 
     * @return maximum number of additions
     */
    public int getMaxAdditions() {
        return maxAdditions;
    }
    
    /**
     * Get the maximum subset size specified at construction.
     * If no maximum size has been set this method returns {@link Integer#MAX_VALUE}.
     * 
     * @return maximum subset size
     */
    public int getMaxSubsetSize() {
        return maxSubsetSize;
    }
    
    /**
     * <p>
     * Generates a move for the given subset solution that adds a random subset of currently unselected IDs to the
     * selection. Possible fixed IDs are not considered to be selected. The maximum number of additions \(k\) and
     * maximum allowed subset size are respected. If no items can be added, <code>null</code> is returned.
     * </p>
     * <p>
     * Note that first, a random number of additions is picked (uniformly distributed) from the valid range and then,
     * a random subset of this size is sampled from the currently unselected IDs, to be added (again, all possible
     * subsets are uniformly distributed, within the fixed size). Because the amount of possible moves increases with
     * the number of performed additions, the probability of generating each specific move thus decreases with the
     * number of additions. In other words, randomly generated moves are <b>not</b> uniformly distributed across
     * different numbers of performed additions, but each specific move performing fewer additions is more likely
     * to be selected than each specific move performing more additions.
     * </p>
     * 
     * @param solution solution for which a random multi addition move is generated
     * @return random multi addition move, <code>null</code> if no items can be added
     */
    @Override
    public SubsetMove getRandomMove(SubsetSolution solution) {
        // get set of candidate IDs for addition (fixed IDs are discarded)
        Set<Integer> addCandidates = getAddCandidates(solution);
        // compute maximum number of adds
        int curMaxAdds = maxAdditions(addCandidates, solution);
        // return null if no additions are possible
        if(curMaxAdds == 0){
            return null;
        }
        // use thread local random for better concurrent performance
        Random rg = ThreadLocalRandom.current();
        // pick number of additions (in [1, curMaxAdds])
        int numAdds = rg.nextInt(curMaxAdds) + 1;
        // pick random IDs to add to selection
        Set<Integer> add = SetUtilities.getRandomSubset(addCandidates, numAdds, rg);
        // create and return move
        return new GeneralSubsetMove(add, Collections.emptySet());
    }

    /**
     * <p>
     * Generates the list of all possible moves that perform 1 up to \(k\) additions, where \(k\) is the maximum number
     * of additions specified at construction. Possible fixed IDs are not considered to be added and the maximum
     * allowed subset size is respected.
     * </p>
     * <p>
     * May return an empty list if no moves can be generated.
     * </p>
     * 
     * @param solution solution for which all possible multi addition moves are generated
     * @return list of all multi addition moves, may be empty
     */
    @Override
    public List<SubsetMove> getAllMoves(SubsetSolution solution) {
        // create empty list to store generated moves
        List<SubsetMove> moves = new ArrayList<>();
        // get set of candidate IDs for addition (fixed IDs are discarded)
        Set<Integer> addCandidates = getAddCandidates(solution);
        // compute maximum number of additions
        int curMaxAdds = maxAdditions(addCandidates, solution);
        // create all moves for each considered amount of adds (in [1,curMaxAdds])
        SubsetIterator<Integer> itAdd;
        Set<Integer> add;
        for(int a=1; a <= curMaxAdds; a++){
            // create all moves that perform a additions
            itAdd = new SubsetIterator<>(addCandidates, a);
            while(itAdd.hasNext()){
                add = itAdd.next();
                // create and add move
                moves.add(new GeneralSubsetMove(add, Collections.emptySet()));
            }
        }
        // return all moves
        return moves;
    }
    
    /**
     * Computes the maximum number of additions that can be performed, given the set of add candidates
     * and the current subset solution. Takes into account the desired maximum number of additions \(k\)
     * specified at construction (if set) and the maximum allowed subset size (if any).
     * 
     * @param addCandidates candidate IDs to be added to the selection
     * @param sol subset solution for which moves are being generated
     * @return maximum number of additions to be performed
     */
    private int maxAdditions(Set<Integer> addCandidates, SubsetSolution sol){
        int a = IntStream.of(maxAdditions, addCandidates.size(), maxSubsetSize-sol.getNumSelectedIDs()).min().getAsInt();
        return Math.max(a, 0);
    }

}
