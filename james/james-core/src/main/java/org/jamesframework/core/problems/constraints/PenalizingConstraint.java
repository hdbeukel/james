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

package org.jamesframework.core.problems.constraints;

import org.jamesframework.core.problems.constraints.validations.Validation;
import org.jamesframework.core.problems.constraints.validations.PenalizingValidation;
import org.jamesframework.core.exceptions.IncompatibleDeltaValidationException;
import org.jamesframework.core.problems.Solution;
import org.jamesframework.core.search.neigh.Move;

/**
 * <p>
 * Interface of a penalizing constraint that assigns a penalty to a solution's evaluation if it does not
 * satisfy the constraint. This penalty is taken into account when evaluating the corresponding solution:
 * in case evaluations are maximized the penalty is subtracted from the solution's evaluation, in case of
 * minimization the penalty is added to the evaluation. When a solution satisfies the constraint, no
 * penalty (zero) should be assigned.
 * </p>
 * <p>
 * It is required to provide a full validation by implementing {@link #validate(Solution, Object)}.
 * If desired, an efficient delta validation can also be provided by overriding the default behaviour
 * of {@link #validate(Move, Solution, Validation, Object)} which (1) applies the move, (2) performs
 * a full validation and (3) undoes the move.
 * </p>
 * @author <a href="mailto:herman.debeukelaer@ugent.be">Herman De Beukelaer</a>
 * @param <SolutionType> solution type to which the constraint is applied, required to extend {@link Solution}
 * @param <DataType> underlying data type used for validation
 */
public interface PenalizingConstraint<SolutionType extends Solution, DataType> extends Constraint<SolutionType, DataType> {
    
    /**
     * Validates a solution given the underlying data. Returns an object of type {@link PenalizingValidation}.
     * The assigned penalty can be retrieved by calling {@link PenalizingValidation#getPenalty()} on this object.
     * 
     * @param solution solution to validate
     * @param data underlying data used for validation
     * @return penalizing validation that indicates the assigned penalty
     */
    @Override
    public PenalizingValidation validate(SolutionType solution, DataType data);
    
    /**
     * <p>
     * Validates a move that will be applied to the current solution of a local search (delta validation).
     * The result corresponds to the validation of the modified solution that would be obtained by applying
     * the given move to the current solution. A default implementation is provided that (1) applies the move,
     * (2) performs a full validation by calling {@link #validate(Solution, Object)} and (3) undoes the applied move.
     * </p>
     * <p>
     * It is often possible to provide a custom, much more efficient delta validation based on the
     * current validation and the changes that will be made when applying the move to the current
     * solution. This can be done by overriding this method. It is usually required to cast the
     * received move to a specific type so that this constraint can only be used in combination
     * with neighbourhoods that generate moves of this type (or a subtype). If an incompatible
     * move type is received, an {@link IncompatibleDeltaValidationException} may be thrown.
     * </p>
     * <p>
     * Given that both this method and the full validation ({@link #validate(Solution, Object)}) return
     * validations of the same type, it is guaranteed that <code>curValidation</code> will also be of
     * this specific type and it is safe to perform a cast, if required.
     * </p>
     * 
     * @param move move to validate
     * @param curSolution current solution
     * @param curValidation validation of current solution
     * @param data underlying data used for validation
     * @param <ActualSolutionType> the actual solution type of the problem that is being solved;
     *                             a subtype of the solution types of both the constraint and the applied move
     * @return penalizing validation of modified solution obtained when applying the move to the current solution
     * @throws IncompatibleDeltaValidationException if the provided delta validation is not compatible
     *                                              with the received move type
     */
    @Override
    default public <ActualSolutionType extends SolutionType> PenalizingValidation validate(Move<? super ActualSolutionType> move,
                                                                                           ActualSolutionType curSolution,
                                                                                           Validation curValidation,
                                                                                           DataType data){
        return (PenalizingValidation) Constraint.super.validate(move, curSolution, curValidation, data);
    }
    
}
