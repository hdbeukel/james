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

package org.jamesframework.core.subset.algo.exh;

import java.util.NoSuchElementException;
import java.util.Set;
import org.jamesframework.core.search.algo.exh.SolutionIterator;
import org.jamesframework.core.subset.SubsetSolution;
import org.jamesframework.core.util.SubsetIterator;

/**
 * A subset solution iterator generates all possible subsets within a given size range, given a
 * set of IDs from which to select. It can be plugged into an exhaustive search algorithm
 * to generate and evaluate all possible subset solutions.
 * <p>
 * A {@link SubsetIterator} is used internally and selected items are wrapped in a subset solution.
 * 
 * @author <a href="mailto:herman.debeukelaer@ugent.be">Herman De Beukelaer</a>
 */
public class SubsetSolutionIterator implements SolutionIterator<SubsetSolution> {

    // collection of IDs to select from
    private final Set<Integer> IDs;
    
    // subset iterator
    private final SubsetIterator<Integer> subsetIterator;
    
    /**
     * Create a subset solution iterator that generates all subsets within the given size range,
     * sampled from the given set of IDs.
     * 
     * @param IDs set of IDs to select from 
     * @param minSubsetSize minimum subset size
     * @param maxSubsetSize maximum subset size
     * @throws NullPointerException if <code>IDs</code> is <code>null</code>
     * @throws IllegalArgumentException if <code>IDs</code> is empty,
     *                                     <code>minSubsetSize &lt; 0</code>,
     *                                     <code>minSubsetSize &gt; |IDs|</code>,
     *                                     or <code>minSubsetSize &gt; maxSubsetSize</code>
     */
    public SubsetSolutionIterator(Set<Integer> IDs, int minSubsetSize, int maxSubsetSize){
        // create subset iterator (throws errors in case of invalid arguments)
        this.subsetIterator = new SubsetIterator<>(IDs, minSubsetSize, maxSubsetSize);
        // store set of IDs
        this.IDs = IDs;
    }
    
    /**
     * Create a subset solution iterator that generates all subsets with a fixed size,
     * sampled from the given set of IDs.
     * 
     * @param IDs set of IDs to select from 
     * @param fixedSubsetSize  fixed subset size
     * @throws NullPointerException if <code>IDs</code> is <code>null</code>
     * @throws IllegalArgumentException if <code>IDs</code> is empty,
     *                                     <code>fixedSubsetSize &lt; 0</code>,
     *                                     or <code>fixedSubsetSize &gt; |IDs|</code>
     */
    public SubsetSolutionIterator(Set<Integer> IDs, int fixedSubsetSize){
        this(IDs, fixedSubsetSize, fixedSubsetSize);
    }
    
    /**
     * Checks whether more subset solutions are to be generated.
     * 
     * @return <code>true</code> if not all possible subsets of all valid size have already been generated
     */
    @Override
    public boolean hasNext() {
        return subsetIterator.hasNext();
    }

    /**
     * Generate the next subset solution. The returned subset will either have the same size as the previously generated solution,
     * if any, or it will be a larger subset.
     * 
     * @return next subset solution within the size bounds
     * @throws NoSuchElementException if there is no next solution to be generated
     */
    @Override
    public SubsetSolution next() {
        // get next subset
        Set<Integer> subset = subsetIterator.next();
        // wrap in subset solution
        return new SubsetSolution(IDs, subset);
    }

}
