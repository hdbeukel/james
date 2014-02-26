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

package org.jamesframework.core.search;

import org.jamesframework.core.exceptions.SearchException;
import org.jamesframework.core.problems.Problem;
import org.jamesframework.core.problems.solutions.Solution;
import org.jamesframework.core.search.neigh.Neighbourhood;

/**
 * Abstract neighbourhood search that uses a <i>single</i> neighbourhood to modify the current solution. Most local search
 * metaheuristics use a single neighbourhood, including random descent, steepest descent, tabu search, Metropolis search,
 * parallel tempering, etc.
 *
 * @param <SolutionType> solution type of the problems that may be solved using this search, required to extend {@link Solution}
 * @author Herman De Beukelaer <herman.debeukelaer@ugent.be>
 */
public abstract class SingleNeighbourhoodSearch<SolutionType extends Solution> extends NeighbourhoodSearch<SolutionType> {

    // neighbourhood
    private Neighbourhood<? super SolutionType> neighbourhood;
    
    /**
     * Create a new single neighbourhood search, specifying the problem to be solved and the neighbourhood used to
     * modify the current solution. None of both arguments may be <code>null</code>, else, an exception is thrown.
     * The search name is set to the default name "SingleNeighbourhoodSearch".
     * 
     * @throws NullPointerException if <code>problem</code> or <code>neighbourhood</code> are <code>null</code>
     * @param problem problem to be solved
     * @param neighbourhood neighbourhood used to modify the current solution
     */
    public SingleNeighbourhoodSearch(Problem<SolutionType> problem, Neighbourhood<? super SolutionType> neighbourhood){
        this(null, problem, neighbourhood);
    }
    
    /**
     * Create a new single neighbourhood search, specifying the problem to be solved, the neighbourhood used to
     * modify the current solution, and a custom search name. The problem and neighbourhood may be <code>null</code>,
     * else, an exception is thrown. The search name may be <code>null</code> in which case it is set to the default
     * name "SingleNeighbourhoodSearch".
     * 
     * @throws NullPointerException if <code>problem</code> or <code>neighbourhood</code> are <code>null</code>
     * @param name custom search name
     * @param problem problem to be solved
     * @param neighbourhood neighbourhood used to modify the current solution
     */
    public SingleNeighbourhoodSearch(String name, Problem<SolutionType> problem, Neighbourhood<? super SolutionType> neighbourhood){
        // pass problem to super
        super(name != null ? name : "SingleNeighbourhoodSearch", problem);
        // check neighbourhood not null
        if(neighbourhood == null){
            throw new NullPointerException("Error while creating single neighbourhood search: neighbourhood can not be null.");
        }
        // store neighbourhood
        this.neighbourhood = neighbourhood;
    }
    
    /**
     * Get the neighbourhood used to modify the current solution.
     * 
     * @return neighbourhood used to modify the current solution
     */
    public Neighbourhood<? super SolutionType> getNeighbourhood(){
        return neighbourhood;
    }
    
    /**
     * Sets the neighbourhood used to modify the current solution.
     * Note that this method may only be called when the search is idle.
     * 
     * @throws SearchException if the search is currently not idle
     * @param neighbourhood neighbourhood used to modify the current solution
     */
    public void setNeighbourhood(Neighbourhood<? super SolutionType> neighbourhood){
        // synchronize with status updates
        synchronized(getStatusLock()){
            // check: search idle
            if(getStatus() != SearchStatus.IDLE){
                throw new SearchException("Can not set neighbourhood: search not idle.");
            }
            // go ahead
            this.neighbourhood = neighbourhood;
        }
    }
    
}
