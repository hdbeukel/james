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

import java.util.Collections;
import org.jamesframework.core.subset.neigh.moves.GeneralSubsetMove;
import java.util.HashSet;
import java.util.Random;
import java.util.Set;
import java.util.concurrent.ThreadLocalRandom;
import java.util.stream.IntStream;
import org.jamesframework.core.subset.SubsetSolution;
import org.jamesframework.core.subset.algo.exh.SubsetSolutionIterator;
import org.jamesframework.core.subset.neigh.SingleDeletionNeighbourhood;
import org.jamesframework.core.subset.neigh.moves.SubsetMove;
import org.jamesframework.core.subset.neigh.SubsetNeighbourhood;
import org.jamesframework.core.util.SetUtilities;

/**
 * <p>
 * A subset neighbourhood that generates moves which simultaneously remove up to \(k\) items from the selection,
 * where \(k\) is specified at construction (or unlimited). Generated moves are of type {@link GeneralSubsetMove},
 * which is a subtype of {@link SubsetMove}, and always only remove IDs from the selection. If desired, a set of
 * fixed IDs can be provided which are not allowed to be removed. Also, a minimum size can be imposed so that
 * it is guaranteed that the subset size will not drop below this minimum after application of a move generated
 * by this neighbourhood.
 * </p>
 * <p>
 * Note that a very large amount of moves may be generated when the subset size increases. Therefore, this
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
public class MultiDeletionNeighbourhood extends SubsetNeighbourhood {

    // maximum number of simultaneous removals
    private final int maxDeletions;
    // minimum subset size
    private final int minSubsetSize;
    
    /**
     * Creates a multi deletion neighbourhood that may remove an unlimited number of IDs from the selection.
     * The actual number of performed removals is then only limited by the number of selected items, as it
     * is impossible to remove more items.
     */
    public MultiDeletionNeighbourhood(){
        this(Integer.MAX_VALUE);
    }
    
    /**
     * Creates a multi deletion neighbourhood that may simultaneously remove up to the specified number of
     * IDs from the selection. If <code>maxDeletions</code> is 1, this neighbourhood generates exactly the
     * same moves as the {@link SingleDeletionNeighbourhood} so in such case it is advised to use the latter
     * neighbourhood which has been optimized for this specific scenario.
     * 
     * @param maxDeletions maximum number of removed IDs (&gt; 0)
     * @throws IllegalArgumentException if <code>maxDeletions</code> is not strictly positive
     */
    public MultiDeletionNeighbourhood(int maxDeletions){
        this(maxDeletions, 0);
    }
    
    /**
     * Creates a multi deletion neighbourhood that may simultaneously remove up to the specified number of
     * IDs from the selection, taking into account that the subset size may not drop below the given minimum.
     * If <code>maxDeletions</code> is 1, this neighbourhood generates exactly the same moves as the
     * {@link SingleDeletionNeighbourhood} so in such case it is advised to use the latter neighbourhood
     * which has been optimized for this specific scenario.
     * 
     * @param maxDeletions maximum number of removed IDs (&gt; 0)
     * @param minSubsetSize minimum subset size (&ge; 0)
     * @throws IllegalArgumentException if <code>maxDeletions</code> is not strictly positive
     *                                  or <code>minSubsetSize</code> is negative
     */
    public MultiDeletionNeighbourhood(int maxDeletions, int minSubsetSize){
        this(maxDeletions, minSubsetSize, null);
    }
    
    /**
     * Creates a multi deletion neighbourhood that may simultaneously remove up to the specified number of
     * IDs from the selection, taking into account that the subset size may not drop below the given minimum
     * and that the given set of fixed IDs are not allowed to be deselected. If <code>maxDeletions</code>
     * is 1, this neighbourhood generates exactly the same moves as the {@link SingleDeletionNeighbourhood}
     * so in such case it is advised to use the latter neighbourhood which has been optimized for this
     * specific scenario.
     * 
     * @param maxDeletions maximum number of removed IDs (&gt; 0)
     * @param minSubsetSize minimum subset size (&ge; 0)
     * @param fixedIDs set of fixed IDs which are not allowed to be removed from the selection
     * @throws IllegalArgumentException if <code>maxDeletions</code> is not strictly positive
     *                                  or <code>minSubsetSize</code> is negative
     */
    public MultiDeletionNeighbourhood(int maxDeletions, int minSubsetSize, Set<Integer> fixedIDs){
        super(fixedIDs);
        // check maximum number of deletions
        if(maxDeletions <= 0){
            throw new IllegalArgumentException("The maximum number of deletions should be strictly positive.");
        }
        if(minSubsetSize < 0){
            throw new IllegalArgumentException("The minimum subset size should be non-negative.");
        }
        this.maxDeletions = maxDeletions;
        this.minSubsetSize = minSubsetSize;
    }

    /**
     * Get the maximum number of deletions performed by generated moves.
     * If no limit has been set this method returns {@link Integer#MAX_VALUE}.
     * 
     * @return maximum number of deletions
     */
    public int getMaxDeletions() {
        return maxDeletions;
    }
    
    /**
     * Get the minimum subset size specified at construction.
     * If no minimum size has been set this method returns 0.
     * 
     * @return minimum subset size
     */
    public int getMinSubsetSize() {
        return minSubsetSize;
    }
    
    /**
     * <p>
     * Generates a move for the given subset solution that deselects a random subset of currently selected IDs.
     * Possible fixed IDs are not considered to be deselected. The maximum number of deletions \(k\) and minimum
     * allowed subset size are respected. If no items can be removed, <code>null</code> is returned.
     * </p>
     * <p>
     * Note that first, a random number of deletions is picked (uniformly distributed) from the valid range and then,
     * a random subset of this size is sampled from the currently selected IDs, to be removed (again, all possible
     * subsets are uniformly distributed, within the fixed size). Because the amount of possible moves increases with
     * the number of performed deletions, the probability of generating each specific move thus decreases with the
     * number of deletions. In other words, randomly generated moves are <b>not</b> uniformly distributed across
     * different numbers of performed deletions, but each specific move performing fewer deletions is more likely
     * to be selected than each specific move performing more deletions.
     * </p>
     * 
     * @param solution solution for which a random multi deletion move is generated
     * @return random multi deletion move, <code>null</code> if no items can be removed
     */
    @Override
    public SubsetMove getRandomMove(SubsetSolution solution) {
        // get set of candidate IDs for deletion (fixed IDs are discarded)
        Set<Integer> delCandidates = getRemoveCandidates(solution);
        // compute maximum number of deletions
        int curMaxDel = maxDeletions(delCandidates, solution);
        // return null if no removals are possible
        if(curMaxDel == 0){
            return null;
        }
        // use thread local random for better concurrent performance
        Random rg = ThreadLocalRandom.current();
        // pick number of deletions (in [1, curMaxDel])
        int numDel = rg.nextInt(curMaxDel) + 1;
        // pick random IDs to remove from selection
        Set<Integer> del = SetUtilities.getRandomSubset(delCandidates, numDel, rg);
        // create and return move
        return new GeneralSubsetMove(Collections.emptySet(), del);
    }

    /**
     * <p>
     * Generates the set of all possible moves that perform 1 up to \(k\) deletions, where \(k\) is the maximum number
     * of deletions specified at construction. Possible fixed IDs are not considered to be removed and the minimum
     * allowed subset size is respected.
     * </p>
     * <p>
     * May return an empty set if no moves can be generated.
     * </p>
     * 
     * @param solution solution for which all possible multi deletion moves are generated
     * @return set of all multi deletion moves, may be empty
     */
    @Override
    public Set<SubsetMove> getAllMoves(SubsetSolution solution) {
        // create empty set to store generated moves
        Set<SubsetMove> moves = new HashSet<>();
        // get set of candidate IDs for removal (fixed IDs are discarded)
        Set<Integer> delCandidates = getRemoveCandidates(solution);
        // compute maximum number of deletions
        int curMaxDel = maxDeletions(delCandidates, solution);
        // create all moves for each considered amount of deletions (in [1,curMaxDel])
        SubsetSolutionIterator itDel;
        Set<Integer> del;
        for(int d=1; d <= curMaxDel; d++){
            // create all moves that perform d deletions
            itDel = new SubsetSolutionIterator(delCandidates, d);
            while(itDel.hasNext()){
                del = itDel.next().getSelectedIDs();
                // create and add move
                moves.add(new GeneralSubsetMove(Collections.emptySet(), del));
            }
        }
        // return all moves
        return moves;
    }
    
    /**
     * Computes the maximum number of deletions that can be performed, given the set of remove candidates
     * and the current subset solution. Takes into account the desired maximum number of deletions \(k\)
     * specified at construction (if set) and the minimum allowed subset size (if any).
     * 
     * @param remCandidates candidate IDs to be removed from the selection
     * @param sol subset solution for which moves are being generated
     * @return maximum number of deletions to be performed
     */
    private int maxDeletions(Set<Integer> remCandidates, SubsetSolution sol){
        int d = IntStream.of(maxDeletions, remCandidates.size(), sol.getNumSelectedIDs()-minSubsetSize).min().getAsInt();
        return Math.max(d, 0);
    }

}
