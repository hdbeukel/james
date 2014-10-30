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

package org.jamesframework.core.util;

import java.util.Collection;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.NoSuchElementException;
import java.util.Set;

/**
 * <p>
 * A subset iterator generates all possible subsets of a given set, within the imposed size range.
 * </p>
 * <p>
 * The implemented generation algorithm is the revolving door algorithm from
 * "Combinatorial Algorithms: Generation, Enumeration and Search", Donald Kreher
 * and Douglas Stinson, CRC Press, 1999 (chapter 2, p. 43-52). This algorithm generates
 * k-subsets in a specific minimal change ordering called the revolving door ordering.
 * </p>
 * 
 * @param <T> type of elements in set and subsets
 * @author <a href="mailto:herman.debeukelaer@ugent.be">Herman De Beukelaer</a>
 */
public class SubsetIterator<T> implements Iterator<Set<T>>{

    // maximum subset size
    private final int maxSubsetSize;

    // array containing all items to select from
    private final T[] items;
    
    // indices (in item array) of items selected in next generated subset
    // NOTE: last element of t is a dummy element set to |items|
    private int[] t;
    
    /**
     * Create a subset iterator that generates all subsets of the given set, within the imposed size range.
     * 
     * @param items set of items to select from 
     * @param minSubsetSize minimum subset size
     * @param maxSubsetSize maximum subset size
     * @throws NullPointerException if <code>items</code> is <code>null</code>
     * @throws IllegalArgumentException if <code>items</code> is empty,
     *                                     <code>minSubsetSize &le; 0</code>,
     *                                     <code>minSubsetSize &gt; |IDs|</code>,
     *                                     or <code>minSubsetSize &gt; maxSubsetSize</code>
     */
    @SuppressWarnings("unchecked")
    public SubsetIterator(Set<T> items, int minSubsetSize, int maxSubsetSize){
        // check collection of IDs
        if(items.isEmpty()){
            throw new IllegalArgumentException("Error while creating subset iterator: no items to select from.");
        }
        // convert set of items to array (impose arbitrary order)
        T[] itemArray = (T[]) new Object[items.size()];
        this.items = items.toArray(itemArray);
        // check minimum/maximum size
        if(minSubsetSize <= 0){
            throw new IllegalArgumentException("Error while creating subset iterator: minimum subset size should be "
                                                + "strictly positive.");
        }
        if(minSubsetSize > items.size()){
            throw new IllegalArgumentException("Error while creating subset iterator: minimum subset size can not be "
                                                + "larger than number of items to select from.");
        }
        if(minSubsetSize > maxSubsetSize){
            throw new IllegalArgumentException("Error while creating subset iterator: minimum subset size can not be "
                                                + "larger than maximum subset size.");
        }
        // set indices of selected items in first generated subset (t = {0,1,...,k-1}, with k = minimum subset size)
        t = new int[minSubsetSize+1];
        for(int i=0; i<minSubsetSize; i++){
            t[i] = i;
        }
        // set dummy element
        t[minSubsetSize] = items.size();
        // store maximum size
        this.maxSubsetSize = maxSubsetSize;
    }
    
    /**
     * Create a subset iterator that generates all subsets of the given set, with a fixed size.
     * 
     * @param items set of items to select from 
     * @param fixedSubsetSize  fixed subset size
     * @throws NullPointerException if <code>items</code> is <code>null</code>
     * @throws IllegalArgumentException if <code>items</code> is empty,
     *                                     <code>fixedSubsetSize &le; 0</code>,
     *                                     or <code>fixedSubsetSize &gt; |items|</code>
     */
    public SubsetIterator(Set<T> items, int fixedSubsetSize){
        this(items, fixedSubsetSize, fixedSubsetSize);
    }
    
    /**
     * Checks whether more subsets are to be generated.
     * 
     * @return <code>false</code> if all possible subsets of all valid
     *         sizes have already been generated, else <code>true</code>
     */
    @Override
    public boolean hasNext() {
        return t != null;
    }

    /**
     * <p>
     * Fill the given collection with the items from the next subset. The collection is <b>not</b>
     * cleared so already contained items will be retained. A reference to this same collection
     * is returned after it has been modified.
     * </p>
     * <p>
     * To store the next subset in a newly allocated {@link LinkedHashSet} the alternative method
     * {@link #next()} may also be used.
     * </p>
     * 
     * @param subset collection to fill with items from next generated subset
     * @return reference to given collection, after it has been filled with the items from the next subset
     * @throws NoSuchElementException if there is no next subset to be generated
     */
    public Collection<T> next(Collection<T> subset){
        // check if there is a next subset to generate
        if(!hasNext()){
            throw new NoSuchElementException("No more subsets to be generated.");
        }
        // fill collection with currently selected items (returned at the end of the method)
        for(int i=0; i<t.length-1; i++){ // skip last element (= dummy)
            subset.add(items[t[i]]);
        }
        
        // set indices of items to be selected in next subset, if any, according to kSubsetRevDoorSuccessor
        // algorithm by Kreher and Stinson (p. 52), modified so that
        //  - it is detected when all subsets of the current size have been generated
        //  - in the latter case, the generation continues with the next size, if still valid
        //  - indices and values in t are counted from 0 to k-1 instead of 1 to k
        //  - special cases (size 1 and full size) also work
        
        // k indicates current subset size (account for dummy element!)
        int k = t.length-1;
        
        // search for first index j where t[j] is different from j
        int j=0;
        while(j < k && t[j] == j){
            j++;
        }
        
        // if j = k-1 and t[j] = |items|-1, or k = |items|, all subsets of the current size have been generated
        if (j == k-1 && t[j] == items.length-1 || k == items.length){
            // go to next size, if still within bounds
            int nextSize = k+1;
            if(nextSize <= maxSubsetSize && nextSize <= items.length){
                // set first subset of next size (t = {0,1,...,nextSize-1})
                t = new int[nextSize+1];
                for(int i=0; i<nextSize; i++){
                    t[i] = i;
                }
                // set dummy
                t[nextSize] = items.length;
            } else {
                // next size is no longer within bounds
                t = null;
            }
        } else {
            // generate next subset of current size
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
        
        // return current subset
        return subset;
    }
    
    /**
     * Generate the next subset in a newly allocated {@link LinkedHashSet}.
     * 
     * @return next subset stored in newly allocated {@link LinkedHashSet}.
     * @throws NoSuchElementException if there is no next subset to be generated
     */
    @Override
    public Set<T> next() {
        Set<T> subset = new LinkedHashSet<>();
        next(subset);
        return subset;
    }

}
