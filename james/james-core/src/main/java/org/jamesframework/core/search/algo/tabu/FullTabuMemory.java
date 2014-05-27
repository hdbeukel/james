/*
 * Copyright 2014 Herman De Beukelaer
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

import org.jamesframework.core.problems.solutions.Solution;
import org.jamesframework.core.search.neigh.Move;

/**
 * Full tabu memory that stores deeps copies of recently visited solutions and declares a move tabu if
 * applying it to the current solution yields a neighbouring solution which is currently contained in
 * the memory. A full tabu memory has a single parameter controlling the size of the memory, i.e. the
 * number of recently visited solutions which are simultaneously stored. If the size is exceeded, the
 * least recently visited solution is discarded from the memory (FIFO).
 * 
 * @param <SolutionType> solution type of the tabu memory, required to extend
 *                       {@link Solution}; should match with the solution type
 *                       of the tabu search that uses this memory
 * @author <a href="mailto:herman.debeukelaer@ugent.be">Herman De Beukelaer</a>
 */
public class FullTabuMemory<SolutionType extends Solution> implements TabuMemory<SolutionType> {
    
    @Override
    public boolean isTabu(Move<SolutionType> move, SolutionType currentSolution) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void registerVisitedSolution(SolutionType visitedSolution, Move<SolutionType> appliedMove) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

}
