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
import org.jamesframework.ext.permutation.neigh.moves.SingleSwapMove;

/**
 * Permutation neighbourhood that generates single swap moves.
 * 
 * @author <a href="mailto:herman.debeukelaer@ugent.be">Herman De Beukelaer</a>
 */
public class SingleSwapNeighbourhood implements Neighbourhood<PermutationSolution>{

    /**
     * Create a random single swap move.
     * 
     * @param solution permutation solution to which the move is to be applied
     * @return random swap move
     */
    @Override
    public SingleSwapMove getRandomMove(PermutationSolution solution) {
        // pick two random, distinct positions to swap
        Random rg = ThreadLocalRandom.current();
        int n = solution.size();
        int i = rg.nextInt(n);
        int j = rg.nextInt(n-1);
        if(j >= i){
            j++;
        }
        // generate swap move
        return new SingleSwapMove(i, j);
    }

    /**
     * Create a list of all possible single swap moves. A move is generated
     * for each pair of distinct positions i,j (i&lt;j) in the given permutation.
     * 
     * @param solution permutation solution to which the move is to be applied
     * @return list of all possible single swap moves
     */
    @Override
    public List<SingleSwapMove> getAllMoves(PermutationSolution solution) {
        // initialize list
        List<SingleSwapMove> moves = new ArrayList<>();
        int n = solution.size();
        for(int i=0; i<n; i++){
            for(int j=i+1; j<n; j++){
                moves.add(new SingleSwapMove(i, j));
            }
        }
        return moves;
    }

}
