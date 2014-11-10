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
 * A standard 2-opt TSP move.
 * 
 * @author <a href="mailto:herman.debeukelaer@ugent.be">Herman De Beukelaer</a>
 */
public class TSP2OptMove implements Move<TSPSolution> {

    // city positions i,j between which the path is reversed as follows:
    // ... - (a) - (c_1) - (c_2)   - ... - (c_n-1) - (c_n) - (b) - ... 
    //               i                                 j
    //  =>
    // ... - (a) - (c_n) - (c_n-1) - ... - (c_2)   - (c_1) - (b) - ...
    //               i                                 j
    private final int i, j;

    public TSP2OptMove(int i, int j) {
        // check
        if(i == j){
            throw new IllegalArgumentException("Error: i and j should be distinct positions.");
        }
        // store
        this.i = i;
        this.j = j;
    }
    
    public int getI(){
        return i;
    }
    
    public int getJ(){
        return j;
    }
    
    @Override
    public void apply(TSPSolution solution) {
        // reverse subpath
        int start = i;
        int stop = j;
        int n = solution.getCities().size();
        int reversedLength;
        if(i < j){
            reversedLength = j-i+1;
        } else {
            reversedLength = n - (i-j-1);
        }
        int numSwaps = reversedLength/2;
        for(int k=0; k<numSwaps; k++){
            solution.swapCities(start, stop);
            start = (start+1) % n;
            stop = (stop-1+n) % n;
        }
    }

    @Override
    public void undo(TSPSolution solution) {
        // undo by reversing again
        apply(solution);
    }

}
