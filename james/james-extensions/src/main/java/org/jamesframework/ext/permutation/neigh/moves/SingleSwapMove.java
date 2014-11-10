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
     * Get the position of the first swapped item.
     * 
     * @return first position
     */
    public int getI(){
        return i;
    }
    
    /**
     * Get the position of the second swapped item.
     * 
     * @return second position
     */
    public int getJ(){
        return j;
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

    /**
     * Two single swap moves are considered equal if they swap the same pair of items.
     * The order of <code>i</code> and <code>j</code> is arbitrary.
     * 
     * @param obj object to compare for equality
     * @return <code>true</code> if the given object is also a single swap move that
     *         swaps the same pair of items
     */
    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final SingleSwapMove other = (SingleSwapMove) obj;
        return (this.i == other.i && this.j == other.j)
                || (this.i == other.j && this.j == other.i);
    }
    
    /**
     * Hash code computation corresponding to the implementation of {@link #equals(Object)}.
     * 
     * @return hash code
     */
    @Override
    public int hashCode() {
        int hash = 7;
        hash = 71 * hash + this.i + this.j;
        return hash;
    }

}
