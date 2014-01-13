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

package org.jamesframework.core.problems.constraints;

import org.jamesframework.core.problems.solutions.Solution;

/**
 * Interface of a penalizing constraint, which assigns a certain penalty to a solution which does not
 * satisfy the constraint. This penalty is taken into account when evaluating the corresponding solution:
 * in case scores are maximized the penalty is subtracted from the solution's evaluation, in case of
 * minimization the penalty is added to the evaluation. When a solution satisfies the constraint, no
 * penalty (zero) should be assigned.
 * 
 * @author Herman De Beukelaer <herman.debeukelaer@ugent.be>
 * @param <SolutionType> solution type to which the constraint is applied, required to extend {@link Solution}
 * @param <DataType> underlying data type
 */
public interface PenalizingConstraint<SolutionType extends Solution, DataType> extends Constraint<SolutionType, DataType> {

    /**
     * Computes the penalty which is assigned to a solution. The implementation should be consistent with that of
     * {@link #isSatisfied}, meaning that no penalty (zero) should be assigned if the solution
     * satisfies the constraint. If the solution does not satisfy the constraint, the computed penalty should be strictly
     * positive; it will be subtracted from the solution's evaluation in case of maximization, or added in case of minimization.
     * 
     * @param solution solution for which the penalty is computed
     * @param data underlying data
     * @return penalty assigned to the solution (0.0 in case the solution satisfies the constraint, > 0.0 if not)
     */
    public double computePenalty(SolutionType solution, DataType data);
    
}
