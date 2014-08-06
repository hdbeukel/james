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

package org.jamesframework.core.problems.objectives;

import org.jamesframework.core.problems.Solution;

/**
 * Interface of a general objective used to evaluate solutions, using some underlying data.
 * An objective can be either maximizing or minimizing; in the former case increasing scores
 * indicate improvement, while in the latter case decreasing scores indicate improvement.
 * 
 * @author <a href="mailto:herman.debeukelaer@ugent.be">Herman De Beukelaer</a>
 * @param <SolutionType> solution type to be evaluated, required to extend {@link Solution}
 * @param <DataType> underlying data type
 */
public interface Objective<SolutionType extends Solution, DataType> {
    
    /**
     * Evaluates a given solution, based on some underlying data. Depending on whether the objective
     * is maximizing (versus minimizing), better solutions have higher (respectively lower) scores.
     * 
     * @param solution solution to evaluate
     * @param data underlying data which is used for evaluation
     * @return evaluation of the given solution
     */
    public double evaluate(SolutionType solution, DataType data);
    
    /**
     * Check whether the objective is minimizing.
     * 
     * @return true if the objective is minimizing
     */
    public boolean isMinimizing();

}
