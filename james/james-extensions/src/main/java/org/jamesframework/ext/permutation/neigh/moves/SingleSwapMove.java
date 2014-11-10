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

package org.jamesframework.ext.permutation.neigh.moves;

import org.jamesframework.core.search.neigh.Move;
import org.jamesframework.ext.permutation.PermutationSolution;

/**
 * A move that swaps two items at given positions in a permutation solution.
 * 
 * @author <a href="mailto:herman.debeukelaer@ugent.be">Herman De Beukelaer</a>
 */
public class SingleSwapMove implements Move<PermutationSolution>{

    // positions of items to swap
    private final int i, j;

    /**
     * Create a single swap move given the two positions in the permutation at which items are to be swapped.
     * 
     * @param i position of first item to be swapped
     * @param j position of second item to be swapped
     */
    public SingleSwapMove(int i, int j) {
        this.i = i;
        this.j = j;
    }
    
    /**
     * Apply the move by swapping the items at the positions specified at construction.
     * 
     * @param solution permutation solution to which the move is applied
     */
    @Override
    public void apply(PermutationSolution solution) {
        solution.swap(i, j);
    }

    /**
     * Undo a previously applied swap move.
     * 
     * @param solution permutation solution to which the move had been applied
     */
    @Override
    public void undo(PermutationSolution solution) {
        // apply swap again to undo
        apply(solution);
    }

}
