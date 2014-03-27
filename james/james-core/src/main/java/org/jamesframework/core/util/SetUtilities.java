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

package org.jamesframework.core.util;

import java.util.HashSet;
import java.util.Iterator;
import java.util.Random;
import java.util.Set;

/**
 * Contains some utility functions for manipulating sets.
 * 
 * @author <a href="mailto:herman.debeukelaer@ugent.be">Herman De Beukelaer</a>
 */
public class SetUtilities {
    
    /**
     * Select a random element from a given set (uniformly distributed). This implementation generates
     * a random number r in [0,|set|-1] and traverses the set using an iterator, where the element obtained
     * after r+1 applications of {@link Iterator#next()} is returned. In the worst case, this algorithm has
     * linear time complexity with respect to the size of the given set.
     * 
     * @param <T> type of randomly selected element
     * @param set set from which to select a random element
     * @param rg random generator
     * @return random element (uniformly distributed)
     */
    public static final <T> T getRandomElement(Set<? extends T> set, Random rg){
        Iterator<? extends T> it = set.iterator();
        int r = rg.nextInt(set.size());
        T selected = it.next();
        for(int i=0; i<r; i++){
            selected = it.next();
        }
        return selected;
    }
    
    /**
     * Selects a random subset of a specific size from a given set (uniformly distributed). This implementation
     * applies a full scan algorithm that iterates once through the given set and selects each item with probability
     * (#remaining to select)/(#remaining to scan). It can be proven that this algorithm creates uniformly distributed
     * random subsets (for example, a proof is given <a href="http://eyalsch.wordpress.com/2010/04/01/random-sample">here</a>).
     * In the worst case, this algorithm has linear time complexity with respect to the size of the given set.
     * 
     * @see <a href="http://eyalsch.wordpress.com/2010/04/01/random-sample">http://eyalsch.wordpress.com/2010/04/01/random-sample</a>
     * @param <T> type of elements in randomly selected subset
     * @param set set from which a random subset is to be selected
     * @param size desired subset size, should be a number in [0,|set|]
     * @param rg random generator
     * @throws IllegalArgumentException if an invalid subset size outside [0,|set|] is specified
     * @return random subset (uniformly distributed) 
     */
    public static final <T> Set<T> getRandomSubset(Set<? extends T> set, int size, Random rg) {
        // check size
        if(size < 0 || size > set.size()){
            throw new IllegalArgumentException("Error in SetUtilities: desired subset size should be a number in [0,|set|].");
        }
        Set<T> subset = new HashSet<>();
        // remaining number of items to select
        int remainingToSelect = size;
        // remaining number of candidates to consider
        int remainingToScan = set.size();
        Iterator<? extends T> it = set.iterator();
        // randomly add items until desired size is obtained
        while (remainingToSelect > 0){
            // get next element
            T item = it.next();
            // select item:
            //  1) always, if all remaining items have to be selected (#remaining to select == #remaining to scan)
            //  2) else, with probability (#remaining to select)/(#remaining to scan) < 1
            if (remainingToSelect == remainingToScan
                    || rg.nextDouble() < ((double)remainingToSelect) / remainingToScan){
                subset.add(item);
                remainingToSelect--;
            }
            remainingToScan--;
        }        
        // return selected subset
        return subset;
    }

}
