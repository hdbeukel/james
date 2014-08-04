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

package org.jamesframework.core.search.algo.tabu;

import java.util.Collection;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.Queue;

/**
 * A fast queue with limited size that replaces the least recently added items if it is full, and which never contains
 * duplicates of the same item. The implementation uses a combination of a queue (to maintain addition order) and a hash
 * set (for fast lookup of elements).
 * 
 * @param <E> type of contained elements
 * @author <a href="mailto:herman.debeukelaer@ugent.be">Herman De Beukelaer</a>
 */
public class FastLimitedQueue<E> {

    // queue and hash set
    private final Queue<E> queue;
    private final HashSet<E> set;
    
    // size limit
    private final int sizeLimit;
    
    /**
     * Creat a new instance with given size limit. The specified size limit is required to be strictly positive.
     * 
     * @param sizeLimit size limit of the queue, required to be strictly positive
     * @throws IllegalArgumentException if <code>size</code> is not strictly positive
     */
    public FastLimitedQueue(int sizeLimit){
        // verify size
        if(sizeLimit <= 0){
            throw new IllegalArgumentException("Queue size should be > 0.");
        }
        // store size
        this.sizeLimit = sizeLimit;
        // create underlying collections
        queue = new LinkedList<>();
        set = new HashSet<>();
    }
    
    /**
     * Add an element at the tail of the queue, replacing the least recently added item if the queue is full.
     * The item is only added to the queue if it is not already contained. Runs in constant time, assuming the
     * hash function disperses the elements properly among the buckets of the underlying hash set.
     * 
     * @param e element to add at the tail of the queue, if not already contained in the queue
     * @return <code>true</code> if the given item was not yet contained in the queue
     */
    public boolean add(E e){
        // not yet contained?
        if(set.contains(e)){
            return false;
        }
        // add new element
        queue.add(e);
        set.add(e);
        // maintain size limit
        while(queue.size() > sizeLimit){
            remove();
        }
        return true;
    }
    
    /**
     * Add the given collection of items. Only those items which are not yet contained in the queue are added, in the
     * order of iteration through the given collection. Runs in linear time, assuming the hash function disperses the
     * elements properly among the buckets of the underlying hash set.
     * 
     * @param items items to add to the queue
     * @return <code>true</code> if the queue is modified, i.e. if at least one new item has been added
     */
    public boolean addAll(Collection<E> items){
        boolean modified = false;
        for(E e : items){
            modified = add(e) || modified;
        }
        return modified;
    }
    
    /**
     * Retrieves and removes the head of the queue, or returns <code>null</code> if the queue is empty.
     * 
     * @return head of the queue, <code>null</code> if queue is empty
     */
    public E remove(){
        E head = queue.poll();      // remove head from queue
        if(head != null){
            set.remove(head);       // also remove this element from the hash set
        }
        return head;
    }
    
    /**
     * Checks whether the given item is contained in the queue. Runs in constant time, assuming the
     * hash function disperses the elements properly among the buckets of the underlying hash set.
     * 
     * @param e given item
     * @return <code>true</code> if <code>e</code> is contained in the queue
     */
    public boolean contains(E e){
        return set.contains(e);
    }
    
    /**
     * Get the number of items contained in the queue.
     * 
     * @return queue size
     */
    public int size(){
        return set.size();
    }
    
    /**
     * Get the size limit of the queue.
     * 
     * @return queue size limit
     */
    public int sizeLimit(){
        return sizeLimit;
    }
    
    /**
     * Clear the queue.
     */
    public void clear(){
        queue.clear();
        set.clear();
    }
    
}
