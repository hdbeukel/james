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

package org.jamesframework.ext.permutation.neigh;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.concurrent.ThreadLocalRandom;
import org.jamesframework.core.search.neigh.Neighbourhood;
import org.jamesframework.ext.permutation.PermutationSolution;
import org.jamesframework.ext.permutation.neigh.moves.ReverseSubsequenceMove;
import org.jamesframework.ext.permutation.neigh.moves.SingleSwapMove;

/**
 * Permutation neighbourhood which generates moves that reverse a subsequence of the permutation.
 * 
 * @author <a href="mailto:herman.debeukelaer@ugent.be">Herman De Beukelaer</a>
 */
public class ReverseSubsequenceNeighbourhood implements Neighbourhood<PermutationSolution>{

    /**
     * Create a random move that reverses a subsequence of the permutation.
     * 
     * @param solution permutation solution to which the move is to be applied
     * @return random move, <code>null</code> if the permutation contains less than 2 items
     */
    @Override
    public ReverseSubsequenceMove getRandomMove(PermutationSolution solution) {
        int n = solution.size();
        // check: move possible
        if(n < 2){
            return null;
        }
        // pick two random, distinct positions
        Random rg = ThreadLocalRandom.current();
        int i = rg.nextInt(n);
        int j = rg.nextInt(n-1);
        if(j >= i){
            j++;
        }
        // generate move
        return new ReverseSubsequenceMove(i, j);
    }

    /**
     * Create a list of all possible moves that reverse a subsequence of the permutation.
     * A move is generated for each pair of distinct positions i,j in the given permutation.
     * The returned list may be empty if the permutation contains less than 2 items.
     * 
     * @param solution permutation solution to which the move is to be applied
     * @return list of all possible moves
     */
    @Override
    public List<ReverseSubsequenceMove> getAllMoves(PermutationSolution solution) {
        // initialize list
        List<ReverseSubsequenceMove> moves = new ArrayList<>();
        int n = solution.size();
        for(int i=0; i<n; i++){
            for(int j=0; j<n; j++){
                if(i != j){
                    moves.add(new ReverseSubsequenceMove(i, j));
                }
            }
        }
        return moves;
    }

}
