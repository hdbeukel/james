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

import org.jamesframework.core.exceptions.IncompatibleTabuMemoryException;
import org.jamesframework.core.exceptions.JamesRuntimeException;
import org.jamesframework.core.exceptions.SearchException;
import org.jamesframework.core.problems.Problem;
import org.jamesframework.core.problems.solutions.Solution;
import org.jamesframework.core.search.SingleNeighbourhoodSearch;
import org.jamesframework.core.search.neigh.Move;
import org.jamesframework.core.search.neigh.Neighbourhood;

/**
 * Tabu search algorithm. In every search step, all neighbours of the current solution are validated and evaluated.
 * The best valid neighbour is then adopted as the new current solution, even if it is no improvement over the current
 * solution. To avoid repeatedly revisiting the same solutions, moves might be declared tabu based on a tabu memory.
 * This memory dynamically tracks (a limited number of) recently visited solutions, features of these solutions and/or
 * recently applied moves (i.e. recently modified features). If a move is tabu, it is not considered, unless it yields
 * a solution which is better than the best solution found so far (aspiration criterion).
 * <p>
 * If all valid neighbours of the current solution are tabu, the search stops. Note that this may never happen so that
 * a stop criterion should preferably be set to ensure termination.
 * 
 * @param <SolutionType> solution type of the problems that may be solved using this search, required to extend {@link Solution}
 * @author <a href="mailto:herman.debeukelaer@ugent.be">Herman De Beukelaer</a>
 */
public class TabuSearch<SolutionType extends Solution> extends SingleNeighbourhoodSearch<SolutionType> {

    // tabu memory
    private TabuMemory<SolutionType> tabuMemory;
    
    /**
     * Creates a new tabu search, specifying the problem to solve, the neighbourhood used to modify the current
     * solution and the applied tabu memory. None of the arguments can be <code>null</code>. The search name defaults
     * to "TabuSearch".
     * <p>
     * Note that the applied neighbourhood and tabu memory should be compatible in terms of generated and accepted move
     * types, respectively, else an {@link IncompatibleTabuMemoryException} might be thrown during search.
     * 
     * @throws NullPointerException if <code>problem</code>, <code>neighbourhood</code> or <code>tabuMemory</code>
     *                              are <code>null</code>
     * @param problem problem to solve
     * @param neighbourhood neighbourhood used to create neighbouring solutions
     * @param tabuMemory applied tabu memory
     */
    public TabuSearch(Problem<SolutionType> problem, Neighbourhood<? super SolutionType> neighbourhood,
                                                                   TabuMemory<SolutionType> tabuMemory){
        this(null, problem, neighbourhood, tabuMemory);
    }
    
    /**
     * Creates a new tabu search, specifying the problem to solve, the neighbourhood used to modify the current
     * solution, the applied tabu memory and a custom search name. The problem, neighbourhood and tabu memory can
     * not be <code>null</code>. The search name can be <code>null</code> in which case the default name "TabuSearch"
     * is assigned.
     * <p>
     * Note that the applied neighbourhood and tabu memory should be compatible in terms of generated and accepted move
     * types, respectively, else an {@link IncompatibleTabuMemoryException} might be thrown during search.
     * 
     * @throws NullPointerException if <code>problem</code>, <code>neighbourhood</code> or <code>tabyMemory</code>
     *                              are <code>null</code>
     * @param name custom search name
     * @param problem problem to solve
     * @param neighbourhood neighbourhood used to create neighbouring solutions
     * @param tabuMemory applied tabu memory
     */
    public TabuSearch(String name, Problem<SolutionType> problem, Neighbourhood<? super SolutionType> neighbourhood,
                                                                                TabuMemory<SolutionType> tabuMemory){
        super(name != null ? name : "TabuSearch", problem, neighbourhood);
        // validate tabu memory
        if(tabuMemory == null){
            throw new NullPointerException("Error while creating tabu search: tabu memory can not be null.");
        }
        // store memory
        this.tabuMemory = tabuMemory;
    }
    
    /**
     * Clear the tabu memory.
     */
    public void clearTabuMemory(){
        tabuMemory.clear();
    }
    
    /**
     * Set the tabu memory. Can not be <code>null</code>.
     * 
     * @param tabuMemory new tabu memory
     * @throws NullPointerException if <code>tabuMemory</code> is <code>null</code>
     */
    public void setTabuMemory(TabuMemory<SolutionType> tabuMemory){
        // validate
        if(tabuMemory == null){
            throw new NullPointerException("Error while setting tabu memory in tabu search: received null.");
        }
        // store
        this.tabuMemory = tabuMemory;
    }

    /**
     * Overrides validation of moves to verify that the move is not tabu. A basic aspiration criterion
     * is applied so that moves which yield a new best solution are always considered, i.e. never tabu.
     * 
     * @param move applied move
     * @return <code>true</code> if the move is valid and not tabu
     */
    @Override
    protected boolean validateMove(Move<? super SolutionType> move){
        return (!tabuMemory.isTabu(move, getCurrentSolution())  // not tabu (or better than best solution found so far)
                 || computeDelta(evaluateMove(move), getBestSolutionEvaluation()) > 0)
                && super.validateMove(move);                    // call super for usual validation
    }
    
    /**
     * Overrides acceptance of a move to update the tabu memory by registering the newly visited solution.
     * 
     * @param move accepted move
     */
    @Override
    protected void acceptMove(Move<? super SolutionType> move){
        // call super
        super.acceptMove(move);
        // update tabu memory
        tabuMemory.registerVisitedSolution(getCurrentSolution(), move);
    }
    
    /**
     * Updates the tabu memory when a custom current/initial solution is set. Note that this method
     * may only be called when the search is idle.
     * 
     * @param solution manually specified current solution
     * @throws SearchException if the search is not idle
     * @throws NullPointerException if <code>solution</code> is <code>null</code>
     */
    @Override
    public void setCurrentSolution(SolutionType solution){
        // call super (also verifies search status)
        super.setCurrentSolution(solution);
        // update tabu memory (no move has been applied to obtain this solution, pass null as move)
        tabuMemory.registerVisitedSolution(solution, null);
    }
    
    /**
     * In every step, all neighbours of the current solution are inspected and the best valid, non tabu neighbour is
     * adopted as the new current solution, if any. If all valid neighbours are tabu, the search stops.
     * 
     * @throws IncompatibleTabuMemoryException if the applied tabu memory is not compatible with the type of moves
     *                                         generated by the applied neighbourhood
     * @throws JamesRuntimeException if depending on malfunctioning components (problem, neighbourhood, ...)
     */
    @Override
    protected void searchStep() {
        // get best valid, non tabu move (ensured by overriding move validation)
        Move<? super SolutionType> move = getMoveWithLargestDelta(
                                            getNeighbourhood().getAllMoves(getCurrentSolution()), // inspect all moves
                                            false);                                               // not necessarily an improvement
        if(move != null){
            // accept move (also updates tabu memory by overriding move acceptance)
            acceptMove(move);
        } else {
            // no valid, non tabu neighbour found: stop search
            stop();
        }
    }
    
}
