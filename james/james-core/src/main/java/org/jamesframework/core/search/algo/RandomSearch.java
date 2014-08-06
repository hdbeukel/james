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

import org.jamesframework.core.problems.Problem;
import org.jamesframework.core.problems.Solution;
import org.jamesframework.core.search.Search;

/**
 * The random search algorithm iteratively samples a random solution and checks
 * whether a new best solution has been found, in every search step.
 * 
 * @param <SolutionType> solution type of the problems that may be solved using this search, required to extend {@link Solution}
 * @author <a href="mailto:herman.debeukelaer@ugent.be">Herman De Beukelaer</a>
 */
public class RandomSearch<SolutionType extends Solution> extends Search<SolutionType> {
    
    /**
     * Create a random search, given the problem to solve. Note that <code>problem</code> can not be <code>null</code>.
     * The search name is set to the default name "RandomSearch".
     * 
     * @throws NullPointerException if <code>problem</code> is <code>null</code>
     * @param problem problem to solve
     */
    public RandomSearch(Problem<SolutionType> problem){
        this(null, problem);
    }
    
    /**
     * Create a random search, given the problem to solve and a custom search name. Note that <code>problem</code> can
     * not be <code>null</code>. The search name can be <code>null</code> in which case it is set to the default name
     * "RandomSearch".
     * 
     * @throws NullPointerException if <code>problem</code> is <code>null</code>
     * @param problem problem to solve
     * @param name custom search name
     */
    public RandomSearch(String name, Problem<SolutionType> problem){
        super(name != null ? name : "RandomSearch", problem);
    }
    
    /**
     * In every search step, a random solution is created and the best solution might be updated accordingly.
     */
    @Override
    protected void searchStep() {
        // sample random solution
        SolutionType sol = getProblem().createRandomSolution();
        // check if new best solution found
        updateBestSolution(sol);
    }

}
