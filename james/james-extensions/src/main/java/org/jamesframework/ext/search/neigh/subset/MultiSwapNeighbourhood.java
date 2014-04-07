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

package org.jamesframework.ext.search.neigh.subset;

import java.util.HashSet;
import java.util.Random;
import java.util.Set;
import java.util.concurrent.ThreadLocalRandom;
import org.jamesframework.core.problems.solutions.SubsetSolution;
import org.jamesframework.core.search.algo.exh.SubsetSolutionIterator;
import org.jamesframework.core.search.neigh.Move;
import org.jamesframework.core.search.neigh.Neighbourhood;
import org.jamesframework.core.search.neigh.subset.SingleSwapNeighbourhood;
import org.jamesframework.core.util.SetUtilities;

/**
 * <p>
 * A subset neighbourhood that generates moves performing up to \(k\) multiple simultaneous swaps of selected and
 * unselected IDs, where \(k\) is specified when creating the neighbourhood. When applying moves generated by this
 * neighbourhood to a given subset solution, the set of selected IDs will always remain of the same size. Therefore,
 * this neighbourhood is only suited for fixed size subset selection problems. If desired, a set of fixed IDs can be
 * provided which are not allowed to be swapped.
 * </p>
 * <p>
 * Note that the number of possible moves quickly becomes very large when the size of the full set and/or selected
 * subset increase. For example, generating all combinations of 1 or 2 simultaneous swaps already yields
 * \[
 *  s*(n-s) + \frac{s(s-1)}{2} \times \frac{(n-s)(n-s-1)}{2}
 * \]
 * possibilities, where \(n\) is the size of the full set and \(s\) is the desired subset size. When selecting e.g.
 * 30 out of 100 items, this value already exceeds one million. Because of the large number of possible moves,
 * this extended neighbourhood should be used with care, especially in combination with searches that generate
 * all moves in every step. Furthermore, searches that generate random moves may have few chances to find an
 * improvement in case of a huge amount of possible neighbours.
 * </p>
 * <p>
 * This neighbourhood is thread-safe: it can be safely used to concurrently generate moves in different searches
 * running in separate threads.
 * </p>
 * 
 * @author <a href="mailto:herman.debeukelaer@ugent.be">Herman De Beukelaer</a>
 */
public class MultiSwapNeighbourhood implements Neighbourhood<SubsetSolution> {

    // maximum number of simultaneous swaps
    private final int maxSwaps;
    
    // set of fixed IDs (may be null or empty if no IDs are fixed)
    private final Set<Integer> fixedIDs;
    
    /**
     * Creates a multi swap neighbourhood without fixed IDs, indicating the desired maximum number
     * of (simultaneous) swaps performed by any generated move. If <code>maxSwaps</code> is 1, this
     * neighbourhood generates exactly the same moves as the {@link SingleSwapNeighbourhood} so in
     * such case it is advised to use the latter neighbourhood which has been optimized for this
     * specific scenario.
     * 
     * @param maxSwaps maximum number of swaps performed by any generated move (&gt; 0)
     * @throws IllegalArgumentException if <code>maxSwaps</code> is not strictly positive
     */
    public MultiSwapNeighbourhood(int maxSwaps){
        this(maxSwaps, null);
    }
    
    /**
     * Creates a multi swap neighbourhood with a given set of fixed IDs which are not allowed to be swapped.
     * None of the generated moves will add nor remove any of these fixed IDs. The generated moves will swap
     * no more than <code>maxSwaps</code> pairs of IDs. If <code>maxSwaps</code> is 1, this neighbourhood
     * generates exactly the same moves as the {@link SingleSwapNeighbourhood} so in such case it is advised
     * to use the latter neighbourhood which has been optimized for this specific scenario.
     * 
     * @param maxSwaps maximum number of swaps performed by any generated move (&gt; 0)
     * @param fixedIDs set of fixed IDs which are not allowed to be swapped
     * @throws IllegalArgumentException if <code>maxSwaps</code> is not strictly positive
     */
    public MultiSwapNeighbourhood(int maxSwaps, Set<Integer> fixedIDs){
        if(maxSwaps <= 0){
            throw new IllegalArgumentException("The maximum number of swaps should be strictly positive.");
        }
        this.maxSwaps = maxSwaps;
        this.fixedIDs = fixedIDs;
    }
    
    /**
     * Generates a move for the given subset solution that removes a random subset of IDs from the current selection
     * and replaces them with an equally large random subset of the currently unselected IDs. The maximum number of
     * swaps specified at construction is respected, i.e. the returned move will swap at most this number of IDs.
     * Possible fixed IDs are not considered to be swapped. If no swaps can be performed, <code>null</code>
     * is returned.
     * 
     * @param solution solution for which a random multi swap move is generated
     * @return random multi swap move, <code>null</code> if no swaps can be performed
     */
    @Override
    public Move<SubsetSolution> getRandomMove(SubsetSolution solution) {
        // get set of candidate IDs for deletion and addition
        Set<Integer> deleteCandidates = solution.getSelectedIDs();
        Set<Integer> addCandidates = solution.getUnselectedIDs();
        // remove fixed IDs, if any, from candidates
        if(fixedIDs != null && !fixedIDs.isEmpty()){
            deleteCandidates = new HashSet<>(deleteCandidates);
            addCandidates = new HashSet<>(addCandidates);
            deleteCandidates.removeAll(fixedIDs);
            addCandidates.removeAll(fixedIDs);
        }
        // compute maximum number of swaps
        int curMaxSwaps = maxSwaps(addCandidates, deleteCandidates);
        // return null if no swaps are possible
        if(curMaxSwaps == 0){
            // impossible to perform a swap
            return null;
        }
        // use thread local random for better concurrent performance
        Random rg = ThreadLocalRandom.current();
        // pick number of swaps (at least 1)
        int numSwaps = rg.nextInt(curMaxSwaps) + 1;
        // pick random IDs to remove from selection
        Set<Integer> del = SetUtilities.getRandomSubset(deleteCandidates, numSwaps, rg);
        // pick random IDs to add to selection
        Set<Integer> add = SetUtilities.getRandomSubset(addCandidates, numSwaps, rg);
        // create and return move
        return new GeneralSubsetMove(add, del);
    }

    /**
     * <p>
     * Generates the set of all possible moves that perform 1 up to \(k\) swaps, where \(k\) is the maximum number
     * of swaps specified at construction. Possible fixed IDs are not considered to be swapped. If \(m &lt; k\)
     * IDs are currently selected or unselected (excluding any fixed IDs), generated moves will perform up to
     * \(m\) swaps only, as it is impossible to perform more than this amount of swaps.
     * </p>
     * <p>
     * May return an empty set if no swap moves can be generated.
     * </p>
     * 
     * @param solution solution for which all possible multi swap moves are generated
     * @return set of all multi swap moves, may be empty
     */
    @Override
    public Set<Move<SubsetSolution>> getAllMoves(SubsetSolution solution) {
        // create empty set to store generated moves
        Set<Move<SubsetSolution>> moves = new HashSet<>();
        // get set of candidate IDs for deletion and addition
        Set<Integer> deleteCandidates = solution.getSelectedIDs();
        Set<Integer> addCandidates = solution.getUnselectedIDs();
        // remove fixed IDs, if any, from candidates
        if(fixedIDs != null && !fixedIDs.isEmpty()){
            deleteCandidates = new HashSet<>(deleteCandidates);
            addCandidates = new HashSet<>(addCandidates);
            deleteCandidates.removeAll(fixedIDs);
            addCandidates.removeAll(fixedIDs);
        }
        // compute maximum number of swaps
        int curMaxSwaps = maxSwaps(addCandidates, deleteCandidates);
        // create all moves for each considered amount of swaps (1 up to curMaxSwaps)
        SubsetSolutionIterator itDel, itAdd;
        Set<Integer> del, add;
        for(int s=1; s <= curMaxSwaps; s++){
            // create all moves that perform s swaps
            itDel = new SubsetSolutionIterator(deleteCandidates, s);
            while(itDel.hasNext()){
                del = itDel.next().getSelectedIDs();
                itAdd = new SubsetSolutionIterator(addCandidates, s);
                while(itAdd.hasNext()){
                    add = itAdd.next().getSelectedIDs();
                    // create and add move
                    moves.add(new GeneralSubsetMove(add, del));
                }
            }
        }
        // return all moves
        return moves;
    }
    
    /**
     * Computes the maximum number of swaps that can be performed, given the set of candidate IDs
     * for addition and deletion. Takes into account the desired maximum number of swaps \(k\) specified
     * at construction. The maximum number of swaps is equal to the minimum of \(k\) and the size of both
     * candidate sets. Thus, if any of the given candidate sets is empty, zero is returned.
     * 
     * @param addCandidates candidate IDs to be added to the selection
     * @param deleteCandidates candidate IDs to be removed from the selection
     * @return maximum number of possible swaps
     */
    private int maxSwaps(Set<Integer> addCandidates, Set<Integer> deleteCandidates){
        return Math.min(maxSwaps, Math.min(addCandidates.size(), deleteCandidates.size()));
    }

}
