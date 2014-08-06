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

package org.jamesframework.examples.clique;

import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.ThreadLocalRandom;
import org.jamesframework.core.subset.SubsetSolution;
import org.jamesframework.core.search.neigh.Move;
import org.jamesframework.core.search.neigh.Neighbourhood;
import org.jamesframework.core.subset.neigh.AdditionMove;
import org.jamesframework.core.util.SetUtilities;

/**
 * Alternative implementation of greedy clique neighbourhood that uses the optimized clique solution type
 * 
 * @author <a href="mailto:herman.debeukelaer@ugent.be">Herman De Beukelaer</a>
 */
public class GreedyCliqueNeighbourhood2 implements Neighbourhood<CliqueSolution> {
    
    // clique data (graph)
    private final CliqueData data;

    public GreedyCliqueNeighbourhood2(CliqueData data) {
        this.data = data;
    }
    
    @Override
    public Move<SubsetSolution> getRandomMove(CliqueSolution clique) {
        Set<Move<SubsetSolution>> allMoves = getAllMoves(clique);
        if(allMoves.isEmpty()){
            return null;
        } else {
            return SetUtilities.getRandomElement(allMoves, ThreadLocalRandom.current());
        }
    }

    @Override
    public Set<Move<SubsetSolution>> getAllMoves(CliqueSolution clique) {
        // get possible adds (constant time!)
        Set<Integer> possibleAdds = clique.getPossibleAdds();
        // retain only additions of candidate vertices
        // with maximum degree within induced subgraph
        Set<Move<SubsetSolution>> moves = new HashSet<>();
        int degree, maxDegree = -1;
        for(int v : possibleAdds){
            // get degree within subgraph
            degree = data.degree(v, possibleAdds);
            if(degree > maxDegree){
                // higher degree
                maxDegree = degree;
                moves.clear();
                moves.add(new AdditionMove(v));
            } else if (degree == maxDegree){
                // same degree
                moves.add(new AdditionMove(v));
            }
        }
        return moves;
    }

}
