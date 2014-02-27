//  Copyright 2014 Herman De Beukelaer
//
//  Licensed under the Apache License, Version 2.0 (the "License");
//  you may not use this file except in compliance with the License.
//  You may obtain a copy of the License at
//
//      http://www.apache.org/licenses/LICENSE-2.0
//
//  Unless required by applicable law or agreed to in writing, software
//  distributed under the License is distributed on an "AS IS" BASIS,
//  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
//  See the License for the specific language governing permissions and
//  limitations under the License.

package org.jamesframework.core.search.algo;

import java.util.Set;
import org.jamesframework.core.exceptions.JamesRuntimeException;
import org.jamesframework.core.problems.Problem;
import org.jamesframework.core.problems.solutions.Solution;
import org.jamesframework.core.search.SingleNeighbourhoodSearch;
import org.jamesframework.core.search.neigh.Move;
import org.jamesframework.core.search.neigh.Neighbourhood;

/**
 * Steepest descent algorithm. In every search step, all neighbours of the current solution are evaluated and the best one is
 * adopted as the new current solution, given that it is an improvement over the current solution. If the best neighbour is no
 * improvement over the current solution, the search stops.
 * 
 * @param <SolutionType> solution type of the problems that may be solved using this search, required to extend {@link Solution}
 * @author Herman De Beukelaer <herman.debeukelaer@ugent.be>
 */
public class SteepestDescent<SolutionType extends Solution> extends SingleNeighbourhoodSearch<SolutionType> {

    /**
     * Creates a new steepest descent search, specifying the problem to solve and the neighbourhood used to
     * modify the current solution. Neither arguments can be <code>null</code>. The search name defaults
     * to "SteepestDescent".
     * 
     * @throws NullPointerException if <code>problem</code> or <code>neighbourhood</code> are <code>null</code>
     * @param problem problem to solve
     * @param neighbourhood neighbourhood used to create neighbouring solutions
     */
    public SteepestDescent(Problem<SolutionType> problem, Neighbourhood<? super SolutionType> neighbourhood){
        this(null, problem, neighbourhood);
    }
    
    /**
     * Creates a new steepest descent search, specifying the problem to solve, the neighbourhood used to
     * modify the current solution, and a custom search name. The problem and neighbourhood can not be
     * <code>null</code>. The search name can be <code>null</code> in which case the default name
     * "SteepestDescent" is assigned.
     * 
     * @throws NullPointerException if <code>problem</code> or <code>neighbourhood</code> are <code>null</code>
     * @param problem problem to solve
     * @param neighbourhood neighbourhood used to create neighbouring solutions
     * @param name custom search name
     */
    public SteepestDescent(String name, Problem<SolutionType> problem, Neighbourhood<? super SolutionType> neighbourhood){
        super(name != null ? name : "SteepestDescent", problem, neighbourhood);
    }

    /**
     * Investigates all neighbours of the current solution and adopts the best one as the new current solution,
     * if it is an improvement. If no improvement is found, the search is requested to stop and no further steps
     * will be performed.
     * 
     * @throws JamesRuntimeException if depending on malfunctioning components (problem, neighbourhood, ...)
     */
    @Override
    protected void searchStep() {
        // get best valid move with positive delta
        Move<? super SolutionType> move = getMoveWithLargestDelta(
                                            getNeighbourhood().getAllMoves(getCurrentSolution()),   // generate all moves
                                            true);                                                  // only improvements
        // found improvement ?
        if(move != null){
            // accept move
            acceptMove(move);
        } else {
            // no improvement found
            stop();
        }
    }
    
}
