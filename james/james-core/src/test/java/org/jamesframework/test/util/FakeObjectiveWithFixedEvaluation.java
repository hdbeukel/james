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

import org.jamesframework.core.problems.objectives.AbstractObjective;
import org.jamesframework.core.problems.solutions.Solution;

/**
 * Fake objective that evaluates every solution (of any type) to a fixed value.
 * Does not use any data. Used only for testing purposes.
 * 
 * @author Herman De Beukelaer <herman.debeukelaer@ugent.be>
 */
public class FakeObjectiveWithFixedEvaluation extends AbstractObjective{

    // fixed evaluation of any solution
    private final double fixedEvaluation;
    
    /**
     * Create a fake objective with fixed evaluation.
     * 
     * @param fixedEvaluation fixed evaluation for any solution
     */
    public FakeObjectiveWithFixedEvaluation(double fixedEvaluation){
        this.fixedEvaluation = fixedEvaluation;
    }
    
    /**
     * Returns a fixed score, ignoring the specific solution and data.
     * 
     * @param solution solution is ignored
     * @param data data is ignored
     * @return returns a fixed score, regardless of the solution and data
     */
    @Override
    public double evaluate(Solution solution, Object data) {
        return fixedEvaluation;
    }

}
