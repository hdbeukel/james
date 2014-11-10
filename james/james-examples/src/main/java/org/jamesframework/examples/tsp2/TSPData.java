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

package org.jamesframework.examples.tsp2;

import java.util.HashSet;
import java.util.Set;
import org.jamesframework.core.problems.datatypes.IntegerIdentifiedData;

/**
 * TSP data consisting of a distance matrix with travel distance between each pair of cities.
 * The IDs assigned to the cities correspond to the row and column indices in the distance matrix.
 * 
 * @author <a href="mailto:herman.debeukelaer@ugent.be">Herman De Beukelaer</a>
 */
public class TSPData implements IntegerIdentifiedData {

    // travel distance matrix
    private final double[][] dist;
    
    // set of IDs
    private final Set<Integer> ids;

    public TSPData(double[][] dist) {
        // infer IDs
        ids = new HashSet<>();
        for(int i=0; i<dist.length; i++){
            ids.add(i);
        }
        // store distance matrix
        this.dist = dist;
    }

    @Override
    public Set<Integer> getIDs() {
        return ids;
    }
    
    // get travel distance from the given city to the given other city
    public double getDistance(int from, int to){
        return dist[from][to];
    }
    
}
