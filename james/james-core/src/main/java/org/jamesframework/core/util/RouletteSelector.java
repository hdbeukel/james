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

import java.util.List;
import java.util.Random;

/**
 * <p>
 * A selection object which simulates a roulette wheel where all items have a weight expressing the
 * size of their section of the wheel. The size of the wheel is the sum of all item weights. During
 * selection a random number in (0, wheel size) is picked and then the item corresponding to this
 * section of the wheel is selected.
 * </p>
 * <p>
 * For a set of items with equal weight they will all have the same probability of being selected.
 * Items which are assigned higher weights will have a higher likelihood of being selected (and the
 * opposite holds for lower weights).
 * </p>
 * <p>
 * The implementation is heavily inspired by original code written by the Apache Software Foundation
 * (<a href="http://www.apache.org/" target="_blank">http://www.apache.org</a>) for the Hadoop MapReduce
 * project (<a href="http://hadoop.apache.org" target="_blank">http://hadoop.apache.org</a>).
 * </p>
 * 
 * @param <E> type of items from which one is to be selected
 * @author <a href="mailto:herman.debeukelaer@ugent.be">Herman De Beukelaer</a>
 */
public class RouletteSelector<E> {
    
    // applied random generator
    private final Random picker;

    /**
     * Create a roulette selector.
     * 
     * @param rnd applied random generator
     */
    public RouletteSelector(Random rnd) {
        this.picker = rnd;
    }
    
    /**
     * Select an item from a given list by roulette selection, where each item has a weight expressing
     * the size of its share of the roulette wheel. The total size of the wheel is the sum of all item
     * weights. The list of items and weights should be of the same size and all weights should be
     * positive. A weight of zero is allowed, in which case the respective item will never be selected.
     * One item is always selected, except when the item list is empty or when all weights are zero in
     * which case <code>null</code> is returned.
     * 
     * @param items items from which one is to be selected
     * @param weights item weights (same order as items)
     * @return item selected by roulette selection, <code>null</code> if all weights are zero
     *              or the item list is empty
     * @throws IllegalArgumentException if both lists are not of the same size or if any of the
     *                                  given weights is &lt; 0
     * @throws NullPointerException if <code>items</code> or <code>weights</code> are <code>null</code>
     *                              or if <code>weights</code> contains any <code>null</code> elements
     */
    public E select(List<E> items, List<Double> weights){
        // check null
        if(items == null){
            throw new NullPointerException("List of items can not be null.");
        }
        if(weights == null){
            throw new NullPointerException("List of weights can not be null.");
        }
        // check list sizes
        if(items.size() != weights.size()){
            throw new IllegalArgumentException("Item and weight lists should be of the same size.");
        }
        // return null if no items given
        if(items.isEmpty()){
            return null;
        }
        // at least one item: compute total weight
        double totalWeight = 0;
        for(Double w : weights){
            // check not null
            if(w == null){
                throw new NullPointerException("Null elements not allowed in weights list.");
            }
            // check positive
            if(w < 0){
                throw new IllegalArgumentException("Negative weights not allowed.");
            }
            // increase sum
            totalWeight += w;
        }
        if(totalWeight > 0){
            // roulette wheel selection
            double r = picker.nextDouble() * totalWeight;
            int i=0;
            while(i < items.size() && r > 0){
                r -= weights.get(i);
                i++;
            }
            return items.get(i-1);
        } else {
            // all items have weight zero
            return null;
        }
    }

}
