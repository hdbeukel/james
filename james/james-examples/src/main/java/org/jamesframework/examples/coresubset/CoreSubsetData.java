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

package org.jamesframework.examples.coresubset;

import java.util.HashSet;
import java.util.Set;
import org.jamesframework.core.problems.datatypes.IntegerIdentifiedData;

/**
 * Provides the distance matrix which is used for the core subset selection problem.
 * 
 * @author <a href="mailto:herman.debeukelaer@ugent.be">Herman De Beukelaer</a>
 */
public class CoreSubsetData implements IntegerIdentifiedData {

    // item names
    private final String[] names;
    // distance matrix
    private final double[][] dist;
    // IDs
    private final Set<Integer> ids;
    
    /**
     * Create core subset data given an array of N item names and an (N x N) symmetric distance matrix.
     * 
     * @param names item names
     * @param dist distance matrix
     */
    public CoreSubsetData(String[] names, double[][] dist){
        // store data
        this.names = names;
        this.dist = dist;
        // infer IDs: 0..N-1 in case of N items (indices in distance matrix and name array)
        ids = new HashSet<>();
        for(int i=0; i<names.length; i++){
            ids.add(i);
        }
    }
    
    /**
     * Get the set of IDs assigned to the items. These IDs correspond to indices in the distance matrix and
     * name array that have been set at construction.
     * 
     * @return set of IDs of all items
     */
    @Override
    public Set<Integer> getIDs() {
        return ids;
    }
    
    /**
     * Get the name of the item with the given ID.
     * 
     * @param id ID of an item
     * @return name of item with given ID
     */
    public String getName(int id){
        return names[id];
    }
    
    /**
     * Get the distance between the items with the given IDs.
     * 
     * @param id1 ID of item 1
     * @param id2 ID of item 2
     * @return distance between the two items
     */
    public double getDistance(int id1, int id2){
        return dist[id1][id2];
    }

}
