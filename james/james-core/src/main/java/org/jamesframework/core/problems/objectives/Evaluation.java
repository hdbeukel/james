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

import org.jamesframework.core.problems.objectives.evaluations.SimpleEvaluation;

/**
 * <p>
 * Interface of an evaluation produced by an objective function. The only requirement is that the
 * evaluation object can be converted into a double value that expresses the solution quality by
 * calling {@link #getValue()}. A predefined simple implementation is provided that merely wraps
 * a double value (see {@link SimpleEvaluation}).
 * </p>
 * <p>
 * When implementing custom delta evaluations, the evaluation of the current solution of a local search
 * is passed back to the objective to evaluate a move (see {@link Objective}).
 * Knowing only the double value of the current solution's evaluation might not be sufficient to efficiently
 * evaluate the modified solution. In such case, custom evaluation objects can be designed that keep track
 * of any additional metadata used for efficient delta evaluation.
 * </p>
 * 
 * @author <a href="mailto:herman.debeukelaer@ugent.be">Herman De Beukelaer</a>
 */
public interface Evaluation {
    
    /**
     * Get the double value of this evaluation, expressing the solution quality.
     * 
     * @return double value
     */
    public double getValue();

}
