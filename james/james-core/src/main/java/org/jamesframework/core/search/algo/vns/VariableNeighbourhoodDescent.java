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

package org.jamesframework.core.search.algo.vns;

import java.util.List;
import org.jamesframework.core.exceptions.JamesRuntimeException;
import org.jamesframework.core.problems.Problem;
import org.jamesframework.core.problems.solutions.Solution;
import org.jamesframework.core.search.MultiNeighbourhoodSearch;
import org.jamesframework.core.search.neigh.Move;
import org.jamesframework.core.search.neigh.Neighbourhood;

/**
 * <p>
 * Variable neighbourhood descent algorithm. In every search step, all neighbours of the current solution are generated
 * using the k-th neighbourhood (initially, k = 0). The best neighbour is identified and accepted as the new current
 * solution if it is an improvement. Else, k is increased by 1 so that the next neighbourhood will be used in the next
 * step. Whenever an improvement is found, k is reset to 0. When k becomes equal to the number of available neighbourhoods,
 * the search stops, as none of the neighbourhoods contains an improvement.
 * </p>
 * <p>
 * Good results are often obtained by providing (preferably disjoint) neighbourhoods ordered by increasing size. Then,
 * only the smallest neighbourhood is extensively used, which will reduce computational costs, while larger neighbourhoods
 * offer ways to escape from local optima where necessary.
 * </p>
 * 
 * @param <SolutionType> solution type of the problems that may be solved using this search, required to extend {@link Solution}
 * @author <a href="mailto:herman.debeukelaer@ugent.be">Herman De Beukelaer</a>
 */
public class VariableNeighbourhoodDescent<SolutionType extends Solution> extends MultiNeighbourhoodSearch<SolutionType> {

    // index of currently used neighbourhood
    private int k;
    
    /**
     * Creates a new variable neighbourhood descent search, specifying the problem to solve and the neighbourhoods
     * used to modify the current solution. Neither arguments can be <code>null</code> and the list of neighbourhoods
     * can not be empty and can not contain any <code>null</code> elements. The search name defaults to
     * "VariableNeighbourhoodDescent".
     * 
     * @throws NullPointerException if <code>problem</code> or <code>neighs</code> are <code>null</code>, or if
     *                              <code>neighs</code> contains a <code>null</code> element
     * @throws IllegalArgumentException if <code>neighs</code> is empty
     * @param problem problem to solve
     * @param neighs list of neighbourhoods used to create neighbouring solutions
     */
    public VariableNeighbourhoodDescent(Problem<SolutionType> problem, List<? extends Neighbourhood<? super SolutionType>> neighs){
        this(null, problem, neighs);
    }
    
    /**
     * Creates a new variable neighbourhood descent search, specifying the problem to solve, the neighbourhoods used
     * to modify the current solution, and a custom search name. The problem and list of neighbourhoods can not be
     * <code>null</code>, and the list of neighbourhoods can not be empty and can not contain any <code>null</code>
     * elements. The search name can be <code>null</code> in which case the default name "VariableNeighbourhoodDescent"
     * is assigned.
     * 
     * @throws NullPointerException if <code>problem</code> or <code>neighs</code> are <code>null</code>, or if
     *                              <code>neighs</code> contains a <code>null</code> element
     * @throws IllegalArgumentException if <code>neighs</code> is empty
     * @param problem problem to solve
     * @param neighs list of neighbourhoods used to create neighbouring solutions
     * @param name custom search name
     */
    public VariableNeighbourhoodDescent(String name, Problem<SolutionType> problem,
                                            List<? extends Neighbourhood<? super SolutionType>> neighs){
        super(name != null ? name : "VariableNeighbourhoodDescent", problem, neighs);
        // start with 0th neighbourhood
        k = 0;
    }

    /**
     * Investigates all neighbours of the current solution, using the k-th neighbourhood, and adopts the best one
     * as the new current solution, if it is an improvement. If no improvement is found, k is increased. Upon each
     * improvement, k is reset to 0, and when k has reached the number of available neighbourhoods, the search stops.
     * 
     * @throws JamesRuntimeException if depending on malfunctioning components (problem, neighbourhood, ...)
     */
    @Override
    protected void searchStep() {
        // stop if no more neighbourhoods available
        if(k >= getNeighbourhoods().size()){
            stop();
        } else {
            // use k-th neighbourhood to get best valid move with positive delta, if any
            Neighbourhood<? super SolutionType> neigh = getNeighbourhoods().get(k);
            Move<? super SolutionType> move = getMoveWithLargestDelta(
                                                neigh.getAllMoves(getCurrentSolution()),    // generate all moves
                                                true);                                      // only improvements
            // found improvement ?
            if(move != null){
                // improvement: accept move and reset k
                acceptMove(move);
                k = 0;
            } else {
                rejectMove();
                // switch to next neighbourhood (to be used in next step)
                k++;
            }
        }
    }
    
}
