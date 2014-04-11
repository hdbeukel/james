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

package org.jamesframework.test.search;

import org.jamesframework.core.search.algo.RandomSearch;
import org.jamesframework.core.problems.Problem;
import org.jamesframework.core.problems.solutions.Solution;

/**
 * Extension of random search with an internally limit on the number of performed steps,
 * so that the search functionality can be tested without having to rely on stop criteria.
 * 
 * @author <a href="mailto:herman.debeukelaer@ugent.be">Herman De Beukelaer</a>
 */
public class RandomSearchWithInternalMaxSteps<SolutionType extends Solution> extends RandomSearch<SolutionType> {
    
    // search stops internally after this number of steps
    private int steps;
    
    /**
     * Create a new instance, specifying the problem to solve and the number of steps after which
     * the search internally terminates.
     * 
     * @param problem problem to solve
     * @param steps number of steps to perform
     */
    public RandomSearchWithInternalMaxSteps(Problem<SolutionType> problem, int steps){
        super(problem);
        this.steps = steps;
    }
    
    @Override
    protected void searchStep() {
        if(getSteps() < steps){
            // perform step
            super.searchStep();
        } else {
            // stop search
            stop();
        }
    }

}
