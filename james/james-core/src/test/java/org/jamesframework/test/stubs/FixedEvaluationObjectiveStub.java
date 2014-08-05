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

package org.jamesframework.test.stubs;

import org.jamesframework.core.problems.solutions.Solution;
import org.jamesframework.test.util.MinMaxObjective;

/**
 * Objective stub that evaluates every solution (of any type) to a fixed value.
 * Does not use any data. Used only for testing purposes.
 * 
 * @author <a href="mailto:herman.debeukelaer@ugent.be">Herman De Beukelaer</a>
 */
public class FixedEvaluationObjectiveStub extends MinMaxObjective<Solution, Object>{

    // fixed evaluation of any solution
    private final double fixedEvaluation;
    
    /**
     * Create an objective stub with fixed evaluation.
     * 
     * @param fixedEvaluation fixed evaluation for any solution
     */
    public FixedEvaluationObjectiveStub(double fixedEvaluation){
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
