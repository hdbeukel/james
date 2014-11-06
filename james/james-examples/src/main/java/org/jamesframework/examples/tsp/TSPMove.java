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

package org.jamesframework.examples.tsp;

import org.jamesframework.core.search.neigh.Move;

/**
 * A move that swaps two cities in the round trip.
 * 
 * @author <a href="mailto:herman.debeukelaer@ugent.be">Herman De Beukelaer</a>
 */
public class TSPMove implements Move<TSPSolution> {

    // positions of cities to swap
    private int i, j;

    public TSPMove(int i, int j) {
        this.i = i;
        this.j = j;
    }
    
    @Override
    public void apply(TSPSolution solution) {
        // swap i-th and j-th city in the given TSP solution
        solution.swapCities(i, j);
    }

    @Override
    public void undo(TSPSolution solution) {
        // undo by swapping again
        apply(solution);
    }

}
