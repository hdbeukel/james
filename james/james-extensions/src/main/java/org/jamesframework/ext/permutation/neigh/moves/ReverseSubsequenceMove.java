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
 * A move that reverses a subsequence of a permutation.
 * 
 * @author <a href="mailto:herman.debeukelaer@ugent.be">Herman De Beukelaer</a>
 */
public class ReverseSubsequenceMove implements Move<PermutationSolution>{

    // first and last position of subsequence that will be reversed (cyclically)
    private final int from, to;

    /**
     * Create a move that will reverse the subsequence from one up to another position in the permutation.
     * It is allowed that <code>from</code> is larger than <code>to</code> as the first item is cyclically
     * defined as the successor of the last item. Yet, both positions should be different.
     * 
     * @param from first position of reversed subsequence
     * @param to last position of reversed subsequence
     * @throws IllegalArgumentException if <code>from</code> and <code>to</code> are equal
     */
    public ReverseSubsequenceMove(int from, int to) {
        // check
        if(from == to){
            throw new IllegalArgumentException("Error while creating move: first and last position of "
                                             + "reversed subsequence can not be equal.");
        }
        // store
        this.from = from;
        this.to = to;
    }
    
    /**
     * Get the first position in the reversed subsequence.
     * 
     * @return initial position in the reversed subsequence
     */
    public int getFrom(){
        return from;
    }
    
    /**
     * Get the last position in the reversed subsequence.
     * 
     * @return final position in the reversed subsequence
     */
    public int getTo(){
        return to;
    }
    
    /**
     * Reverse the subsequence by performing a series of swaps in the given permutation solution.
     * 
     * @param solution permutation solution to which the move is to be applied
     */
    @Override
    public void apply(PermutationSolution solution) {
        int start = from;
        int stop = to;
        int n = solution.size();
        // reverse subsequence by performing a series of swaps
        // (works cyclically when start > stop)
        int reversedLength;
        if(start < stop){
            reversedLength = stop-start+1;
        } else {
            reversedLength = n - (start-stop-1);
        }
        int numSwaps = reversedLength/2;
        for(int k=0; k<numSwaps; k++){
            solution.swap(start, stop);
            start = (start+1) % n;
            stop = (stop-1+n) % n;
        }
    }

    /**
     * Undo move by reverting the order of the reversed subsequence.
     * 
     * @param solution permutation solution to which the move had been applied
     */
    @Override
    public void undo(PermutationSolution solution) {
        // undo by reversing again
        apply(solution);
    }

}
