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

import org.jamesframework.core.problems.Solution;
import org.jamesframework.core.search.neigh.Move;

/**
 * Declares all moves tabu.
 * 
 * @author <a href="mailto:herman.debeukelaer@ugent.be">Herman De Beukelaer</a>
 */
public class RejectAllTabuMemory<SolutionType extends Solution> implements TabuMemory<SolutionType>{

    @Override
    public boolean isTabu(Move<? super SolutionType> move, SolutionType currentSolution) {
        return true;
    }

    @Override
    public void registerVisitedSolution(SolutionType visitedSolution, Move<? super SolutionType> appliedMove) {}

    @Override
    public void clear() {}

}
