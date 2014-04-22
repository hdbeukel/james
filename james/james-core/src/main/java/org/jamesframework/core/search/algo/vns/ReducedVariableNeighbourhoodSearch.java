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
 * Reduced variable neighbourhood search (RVNS) algorithm. In every search step, a random neighbour of the current
 * solution is sampled using the k-th neighbourhood (initially, k = 0). If this neighbour is a improvement, it is
 * accepted as the new current solution. Else, k is increased by 1 so that the next neighbourhood will be used in
 * the next step. Whenever an improvement is found, or whenever all neighbourhoods have been used, k is reset to 0.
 * Note that RVNS never terminates internally, but continues until a stop criterion is met.
 * </p>
 * <p>
 * The reduced variable neighbourhood search can be useful for larger problems, for which variable neighbourhood
 * descent is too costly because it generates and evaluates all neighbours in every step.
 * </p>
 * 
 * @param <SolutionType> solution type of the problems that may be solved using this search, required to extend {@link Solution}
 * @author <a href="mailto:herman.debeukelaer@ugent.be">Herman De Beukelaer</a>
 */
public class ReducedVariableNeighbourhoodSearch<SolutionType extends Solution> extends MultiNeighbourhoodSearch<SolutionType> {

    // index of currently used neighbourhood
    private int k;
    
    /**
     * Creates a new reduced variable neighbourhood search, specifying the problem to solve and the neighbourhoods
     * used to modify the current solution. Neither arguments can be <code>null</code> and the list of neighbourhoods
     * can not be empty. The search name defaults to "ReducedVariableNeighbourhoodSearch".
     * 
     * @throws NullPointerException if <code>problem</code> or <code>neighs</code> are <code>null</code>
     * @throws IllegalArgumentException if <code>neighs</code> is empty
     * @param problem problem to solve
     * @param neighs list of neighbourhoods used to create neighbouring solutions
     */
    public ReducedVariableNeighbourhoodSearch(Problem<SolutionType> problem, List<Neighbourhood<? super SolutionType>> neighs){
        this(null, problem, neighs);
    }
    
    /**
     * Creates a new reduced variable neighbourhood search, specifying the problem to solve, the neighbourhoods used
     * to modify the current solution, and a custom search name. The problem and list of neighbourhoods can not be
     * <code>null</code> and the list of neighbourhoods can not be empty. The search name can be <code>null</code>
     * in which case the default name "ReducedVariableNeighbourhoodSearch" is assigned.
     * 
     * @throws NullPointerException if <code>problem</code> or <code>neighs</code> are <code>null</code>
     * @throws IllegalArgumentException if <code>neighs</code> is empty
     * @param problem problem to solve
     * @param neighs list of neighbourhoods used to create neighbouring solutions
     * @param name custom search name
     */
    public ReducedVariableNeighbourhoodSearch(String name, Problem<SolutionType> problem,
                                            List<Neighbourhood<? super SolutionType>> neighs){
        super(name != null ? name : "ReducedVariableNeighbourhoodSearch", problem, neighs);
        // start with 0th neighbourhood
        k = 0;
    }

    /**
     * Samples a random neighbour of the current solution, using the k-th neighbourhood, and accepts it as the new
     * current solution if it is an improvement. If no improvement is found, k is increased. Upon each improvement,
     * or when k has reached the number of available neighbourhoods, k is reset to 0.
     * <p>
     * If the k-th neighbourhood is unable to generate any move, k is also increased to try the next neighbourhood
     * in the next step.
     * 
     * @throws JamesRuntimeException if depending on malfunctioning components (problem, neighbourhood, ...)
     */
    @Override
    protected void searchStep() {
        // cyclically reset k to zero if no more neighbourhoods are available
        if(k >= getNeighbourhoods().size()){
            k = 0;
        }
        // use k-th neighbourhood to get a random valid move
        Neighbourhood<? super SolutionType> neigh = getNeighbourhoods().get(k);
        Move<? super SolutionType> move = neigh.getRandomMove(getCurrentSolution());
        // check: got move ?
        if(move != null){
            // check: improvement ?
            if(isImprovement(move)){
                // improvement: accept move and reset k
                acceptMove(move);
                k = 0;
            } else {
                rejectMove();
                // switch to next neighbourhood (to be used in next step)
                k++;
            }
        } else {
            // k-th neighbourhood did not produce any random move, try again with next neighbourhood in next step
            k++;
        }
    }
    
}
