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

import java.util.List;
import org.jamesframework.core.exceptions.SearchException;
import org.jamesframework.core.problems.Problem;
import org.jamesframework.core.problems.solutions.Solution;
import org.jamesframework.core.search.neigh.Neighbourhood;

/**
 * Abstract neighbourhood search that uses <i>multiple</i> neighbourhoods to modify the current solution. This
 * includes variable neighbourhood descent and variable neighbourhood search.
 *
 * @param <SolutionType> solution type of the problems that may be solved using this search, required to extend {@link Solution}
 * @author <a href="mailto:herman.debeukelaer@ugent.be">Herman De Beukelaer</a>
 */
public abstract class MultiNeighbourhoodSearch<SolutionType extends Solution> extends NeighbourhoodSearch<SolutionType> {

    // neighbourhoods
    private List<? extends Neighbourhood<? super SolutionType>> neighs;
    
    /**
     * Create a new multi neighbourhood search, specifying the problem to be solved and the neighbourhoods used to
     * modify the current solution. None of both arguments may be <code>null</code> and the list of neighbourhoods
     * may not be empty and may not contain any null elements The default search name "MultiNeighbourhoodSearch"
     * is assigned.
     * 
     * @throws NullPointerException if <code>problem</code> or <code>neighs</code> are <code>null</code>, or if
     *                              <code>neighs</code> contains a <code>null</code> element
     * @throws IllegalArgumentException if <code>neighs</code> is empty
     * @param problem problem to be solved
     * @param neighs neighbourhoods used to modify the current solution
     */
    public MultiNeighbourhoodSearch(Problem<SolutionType> problem, List<? extends Neighbourhood<? super SolutionType>> neighs){
        this(null, problem, neighs);
    }
    
    /**
     * Create a new multi neighbourhood search, specifying the problem to be solved, the neighbourhoods used to
     * modify the current solution, and a custom search name. The problem and neighbourhood list may not be
     * <code>null</code> and the neighbourhood list may not be empty and may not contain any <code>null</code>
     * elements. The search name may be <code>null</code> in which case the default name "MultiNeighbourhoodSearch"
     * is assigned.
     * 
     * @throws NullPointerException if <code>problem</code> or <code>neighs</code> are <code>null</code>, or if
     *                              <code>neighs</code> contains a <code>null</code> element
     * @throws IllegalArgumentException if <code>neighs</code> is empty
     * @param name custom search name
     * @param problem problem to be solved
     * @param neighs neighbourhoods used to modify the current solution
     */
    public MultiNeighbourhoodSearch(String name, Problem<SolutionType> problem, List<? extends Neighbourhood<? super SolutionType>> neighs){
        // pass problem to super
        super(name != null ? name : "MultiNeighbourhoodSearch", problem);
        // check neighs not null
        if(neighs == null){
            throw new NullPointerException("Error while creating multi neighbourhood search: neighbourhood list can not be null.");
        }
        // check that neighs does not contain any null elements
        for(Neighbourhood<?> n : neighs){
            if(n == null){
                throw new NullPointerException("Error while creating multi neighbourhood search: neighbourhood list can not"
                                                    + " contain any null elements.");
            }
        }
        // check neighs not empty
        if(neighs.isEmpty()){
            throw new IllegalArgumentException("Error while creating multi neighbourhood search: neighbourhood list can not be empty.");
        }
        // store neighbourhoods
        this.neighs = neighs;
    }
    
    /**
     * Get the list of neighbourhoods used to modify the current solution.
     * 
     * @return list of applied neighbourhoods
     */
    public List<? extends Neighbourhood<? super SolutionType>> getNeighbourhoods(){
        return neighs;
    }
    
    /**
     * Sets the list of neighbourhoods used to modify the current solution. Note that <code>neighs</code>
     * can not be <code>null</code> nor empty and can not contain any <code>null</code> elements. This method
     * may only be called when the search is idle. It should be used with care for searches that have already
     * been run before and will be restarted later, as updating the neighbourhoods might break the execution
     * of a restarted search that tries to continue from where it had arrived.
     * 
     * @throws NullPointerException if <code>neighs</code> is <code>null</code> or contains any <code>null</code> elements
     * @throws IllegalArgumentException if <code>neighs</code> is empty
     * @throws SearchException if the search is currently not idle
     * @param neighs list of neighbourhoods used to modify the current solution
     */
    public void setNeighbourhoods(List<Neighbourhood<? super SolutionType>> neighs){
        // synchronize with status updates
        synchronized(getStatusLock()){
            // assert idle
            assertIdle("Cannot set list of neighbourhoods.");
            // check not null
            if(neighs == null){
                throw new NullPointerException("Can not set neighbourhoods: received null.");
            }
            // check that neighs does not contain any null elements
            for(Neighbourhood<?> n : neighs){
                if(n == null){
                    throw new NullPointerException("Can not set neighbourhoods: neighbourhood list can not"
                                                        + " contain any null elements.");
                }
            }
            // check not empty
            if(neighs.isEmpty()){
                throw new NullPointerException("Can not set neighbourhoods: received empty list.");
            }
            // go ahead
            this.neighs = neighs;
        }
    }
    
}
