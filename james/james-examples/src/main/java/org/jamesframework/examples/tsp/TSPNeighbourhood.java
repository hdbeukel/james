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

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.concurrent.ThreadLocalRandom;
import org.jamesframework.core.search.neigh.Move;
import org.jamesframework.core.search.neigh.Neighbourhood;

/**
 * Neighbourhood for the TSP problem.
 * 
 * @author <a href="mailto:herman.debeukelaer@ugent.be">Herman De Beukelaer</a>
 */
public class TSPNeighbourhood implements Neighbourhood<TSPSolution>{
    
    @Override
    public Move<? super TSPSolution> getRandomMove(TSPSolution solution) {
        // pick two distinct random positions in the round trip
        int n = solution.getCities().size();
        Random rg = ThreadLocalRandom.current();
        int i = rg.nextInt(n);
        int j = rg.nextInt(n-1);
        if(j >= i){
            j++;
        }
        // return TSP move that swaps cities at these positions
        return new TSPMove(i, j);
    }

    @Override
    public List<? extends Move<? super TSPSolution>> getAllMoves(TSPSolution solution) {
        // generate a TSP move for every pair of distinct positions in the round trip
        int n = solution.getCities().size();
        List<TSPMove> moves = new ArrayList<>();
        for(int i=0; i<n; i++){
            for(int j=i+1; j<n; j++){
                moves.add(new TSPMove(i, j));
            }
        }
        return moves;
    }

}
