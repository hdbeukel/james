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

package org.jamesframework.test.util;

import org.jamesframework.core.problems.Problem;
import org.jamesframework.core.problems.solutions.Solution;

/**
 * Search stub that samples a random solution in every step for comparison with the best known solution.
 * Used for testing only.
 * 
 * @author Herman De Beukelaer <herman.debeukelaer@ugent.be>
 */
public class RandomSearchWithInternalMaxSteps<SolutionType extends Solution> extends RandomSearch<SolutionType> {
    
    // search stops internally after this number of steps
    private int steps;
    
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
