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

/**
 * A subset solution iterator generates all possible subsets within a given size range, given a
 * set of IDs from which to select. It can be plugged into an exhaustive search algorithm
 * to generate and evaluate all possible subset solutions.
 * <p>
 * The implemented generation algorithm is the revolving door algorithm from
 * "Combinatorial Algorithms: Generation, Enumeration and Search", Donald Kreher
 * and Douglas Stinson, CRC Press, 1999 (chapter 2, p. 43-52). This algorithm generates
 * k-subsets in a specific minimal change ordering called the revolving door ordering.
 * 
 * @author <a href="mailto:herman.debeukelaer@ugent.be">Herman De Beukelaer</a>
 */
public class SubsetSolutionIterator implements SolutionIterator<SubsetSolution> {

    // maximum subset size
    private final int maxSubsetSize;
    
    // collection of IDs to select from
    private final Set<Integer> IDs;
    // array representation (arbitrary order)
    private final Integer[] IDsArray;
    
    // indices (in IDs array) of IDs selected in next generated subset solution
    // NOTE: last element of t is a dummy element set to |IDs|
    private int[] t;
    
    /**
     * Create a subset solution iterator that generates all subsets within the given size range,
     * sampled from the given set of IDs.
     * 
     * @param IDs set of IDs to select from 
     * @param minSubsetSize minimum subset size
     * @param maxSubsetSize maximum subset size
     * @throws NullPointerException if <code>IDs</code> is <code>null</code>
     * @throws IllegalArgumentException if <code>IDs</code> is empty,
     *                                     <code>minSubsetSize &le; 0</code>,
     *                                     <code>minSubsetSize &gt; |IDs|</code>,
     *                                     or <code>minSubsetSize &gt; maxSubsetSize</code>
     */
    public SubsetSolutionIterator(Set<Integer> IDs, int minSubsetSize, int maxSubsetSize){
        // check collection of IDs
        if(IDs.isEmpty()){
            throw new IllegalArgumentException("Error while creating subset solution iterator: collection of IDs to select from "
                                                + "can not be empty.");
        }
        // store set of IDs
        this.IDs = IDs;
        // convert set of IDs to array (impose arbitrary order)
        this.IDsArray = IDs.toArray(new Integer[0]);
        // check minimum/maximum size
        if(minSubsetSize <= 0){
            throw new IllegalArgumentException("Error while creating subset solution iterator: minimum subset size should be "
                                                + "strictly positive.");
        }
        if(minSubsetSize > IDs.size()){
            throw new IllegalArgumentException("Error while creating subset solution iterator: minimum subset size can not be "
                                                + "larger than number of IDs to select from.");
        }
        if(minSubsetSize > maxSubsetSize){
            throw new IllegalArgumentException("Error while creating subset solution iterator: minimum subset size can not be "
                                                + "larger than maximum subset size.");
        }
        // set indices of selected IDs in first generated solution (t = {0,1,...,k-1}, with k = minimum subset size)
        t = new int[minSubsetSize+1];
        for(int i=0; i<minSubsetSize; i++){
            t[i] = i;
        }
        // set dummy element
        t[minSubsetSize] = IDs.size();
        // store maximum size
        this.maxSubsetSize = maxSubsetSize;
    }
    
    /**
     * Create a subset solution iterator that generates all subsets with a fixed size,
     * sampled from the given set of IDs.
     * 
     * @param IDs set of IDs to select from 
     * @param fixedSubsetSize  fixed subset size
     * @throws NullPointerException if <code>IDs</code> is <code>null</code>
     * @throws IllegalArgumentException if <code>IDs</code> is empty,
     *                                     <code>fixedSubsetSize &le; 0</code>,
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
        return t != null;
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
        // check if there is a next solution to generate
        if(!hasNext()){
            throw new NoSuchElementException("No more subset solutions to be generated.");
        }
        // create subset solution based on currently selected indices (returned at the end of the method)
        SubsetSolution sol = new SubsetSolution(IDs);
        for(int i=0; i<t.length-1; i++){ // skip last element (= dummy)
            sol.select(IDsArray[t[i]]);
        }
        
        // set indices of IDs selected in next solution, if any, according to kSubsetRevDoorSuccessor
        // algorithm by Kreher and Stinson (p. 52), modified so that
        //  - it is detected when all subsets of the current size have been generated
        //  - in the latter case, the generation continues with the next size, if still valid
        //  - indices and values in t are counted from 0 to k-1 instead of 1 to k
        //  - base cases (size 1 and full size) also work
        
        // k indicates current subset size (account for dummy element!)
        int k = t.length-1;
        
        // search for first index j where t[j] is different from j
        int j=0;
        while(j < k && t[j] == j){
            j++;
        }
        
        // if j = k-1 and t[j] = |IDs|-1, or k = |IDs|, all subsets of the current size have been generated
        if (j == k-1 && t[j] == IDs.size()-1 || k == IDs.size()){
            // go to next size, if still within bounds
            int nextSize = k+1;
            if(nextSize <= maxSubsetSize && nextSize <= IDs.size()){
                // set first solution of next size (t = {0,1,...,nextSize-1})
                t = new int[nextSize+1];
                for(int i=0; i<nextSize; i++){
                    t[i] = i;
                }
                // set dummy
                t[nextSize] = IDs.size();
            } else {
                // next size is no longer within bounds
                t = null;
            }
        } else {
            // generate next solution of current size
            // (according to revolving door successor algorithm)
            if((k - (j+1)) % 2 != 0){
                if(j == 0){
                    t[0] = t[0]-1;
                } else {
                    t[j-1] = j;
                    if(j-2 >= 0){
                        t[j-2] = j-1;
                    }
                }
            } else {
                if(t[j+1] != t[j]+1){
                    if(j-1 >= 0){
                        t[j-1] = t[j];
                    }
                    t[j] = t[j] + 1;
                } else {
                    t[j+1] = t[j];
                    t[j] = j;
                }
            }
        }
        
        // return current solution
        return sol;
    }

}
