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

package org.jamesframework.core.problems;

/**
 * Interface of a problem with a generic solution type. Contains methods to evaluate or validate a solution and
 * to create a random solution. Also indicates whether the evaluations are to be maximized or minimized.
 * 
 * @author <a href="mailto:herman.debeukelaer@ugent.be">Herman De Beukelaer</a>
 * @param <SolutionType> the solution type that corresponds to this problem, required to extend {@link Solution}
 */
public interface Problem<SolutionType extends Solution> {
    
    /**
     * Evaluates a given solution. The computed score reflects the quality of the solution;
     * depending on whether scores are maximized (or minimized), higher (respectively lower)
     * scores indicate improvement.
     * 
     * @see #isMinimizing()
     * @param solution the solution to evaluate
     * @return evaluation of the solution
     */
    public double evaluate(SolutionType solution);
    
    /**
     * Indicates whether the given solution should be rejected during search,
     * regardless of its evaluation. This method may for example be used to
     * discard solutions that do not satisfy imposed constraints.
     * 
     * @param solution solution to verify
     * @return true if the given solution should be rejected
     */
    public boolean rejectSolution(SolutionType solution);
    
    /**
     * Indicates whether scores are being minimized or maximized.
     * 
     * @return true if scores are being minimized
     */
    public boolean isMinimizing();
    
    /**
     * Creates a random solution. Such random solutions can for example be used as initial solution
     * for neighbourhood searches.
     * 
     * @return a random solution
     */
    public SolutionType createRandomSolution();
    
}
