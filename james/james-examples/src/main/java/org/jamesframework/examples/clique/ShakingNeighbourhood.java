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

import java.util.Collections;
import java.util.Set;
import java.util.concurrent.ThreadLocalRandom;
import org.jamesframework.core.subset.SubsetSolution;
import org.jamesframework.core.search.neigh.Move;
import org.jamesframework.core.search.neigh.Neighbourhood;
import org.jamesframework.core.subset.neigh.adv.GeneralSubsetMove;
import org.jamesframework.core.util.SetUtilities;

/**
 * Shaking neighbourhood applied in variable neighbourhood search for the maximum clique problem.
 * 
 * @author <a href="mailto:herman.debeukelaer@ugent.be">Herman De Beukelaer</a>
 */
public class ShakingNeighbourhood implements Neighbourhood<SubsetSolution>{

    // number of vertices which are removed from the clique
    private final int numRemovals;

    public ShakingNeighbourhood(int numRemovals) {
        this.numRemovals = numRemovals;
    }
    
    @Override
    public Move<SubsetSolution> getRandomMove(SubsetSolution solution) {
        Set<Integer> remove = SetUtilities.getRandomSubset(
                                    solution.getSelectedIDs(),
                                    Math.min(numRemovals, solution.getNumSelectedIDs()),
                                    ThreadLocalRandom.current()
                              );
        Set<Integer> add = Collections.emptySet();
        return new GeneralSubsetMove(add, remove);
    }

    @Override
    public Set<Move<SubsetSolution>> getAllMoves(SubsetSolution solution) {
        throw new UnsupportedOperationException("Neighbourhood only supports generation of random moves.");
    }

}
