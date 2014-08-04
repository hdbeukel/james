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

package org.jamesframework.core.search.algo;

import org.jamesframework.core.exceptions.JamesRuntimeException;
import org.jamesframework.core.problems.Problem;
import org.jamesframework.core.problems.solutions.Solution;
import org.jamesframework.core.search.SingleNeighbourhoodSearch;
import org.jamesframework.core.search.neigh.Move;
import org.jamesframework.core.search.neigh.Neighbourhood;

/**
 * Basic random descent algorithm. In every search step, a random neighbour is created by applying a random move to the current solution.
 * If this neighbour is an improvement over the current solution, it is accepted as the new current solution; else, it is rejected. The
 * random descent algorithm usually does not come to a natural end, and therefore fully depends on stop criteria for termination, except
 * in the unusual case when the current solution does not have any neighbours.
 * 
 * @param <SolutionType> solution type of the problems that may be solved using this search, required to extend {@link Solution}
 * @author <a href="mailto:herman.debeukelaer@ugent.be">Herman De Beukelaer</a>
 */
public class RandomDescent<SolutionType extends Solution> extends SingleNeighbourhoodSearch<SolutionType> {

    /**
     * Creates a new random descent search, specifying the problem to solve and the neighbourhood used to
     * modify the current solution. Neither arguments can be <code>null</code>. The search name defaults
     * to "RandomDescent".
     * 
     * @throws NullPointerException if <code>problem</code> or <code>neighbourhood</code> are <code>null</code>
     * @param problem problem to solve
     * @param neighbourhood neighbourhood used to create neighbouring solutions
     */
    public RandomDescent(Problem<SolutionType> problem, Neighbourhood<? super SolutionType> neighbourhood){
        this(null, problem, neighbourhood);
    }
    
    /**
     * Creates a new random descent search, specifying the problem to solve, the neighbourhood used to
     * modify the current solution, and a custom search name. The problem and neighbourhood can not be
     * <code>null</code>. The search name can be <code>null</code> in which case the default name
     * "RandomDescent" is assigned.
     * 
     * @throws NullPointerException if <code>problem</code> or <code>neighbourhood</code> are <code>null</code>
     * @param problem problem to solve
     * @param neighbourhood neighbourhood used to create neighbouring solutions
     * @param name custom search name
     */
    public RandomDescent(String name, Problem<SolutionType> problem, Neighbourhood<? super SolutionType> neighbourhood){
        super(name != null ? name : "RandomDescent", problem, neighbourhood);
    }

    /**
     * Creates a random neighbour of the current solution and accepts it if it improves over the current solution.
     * 
     * @throws JamesRuntimeException if depending on malfunctioning components (problem, neighbourhood, ...)
     */
    @Override
    protected void searchStep() {
        // get random move
        Move<? super SolutionType> move = getNeighbourhood().getRandomMove(getCurrentSolution());
        // got move ?
        if(move != null){
            // accept if improvement
            if(isImprovement(move)){
                // accept move
                acceptMove(move);
            } else {
                // no improvement
                rejectMove();
            }
        } else {
            // no move/neighbour reported by neighbourhood
            stop();
        }
    }
    
}
