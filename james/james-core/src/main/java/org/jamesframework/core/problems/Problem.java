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

import org.jamesframework.core.exceptions.IncompatibleDeltaEvaluationException;
import org.jamesframework.core.exceptions.IncompatibleDeltaValidationException;
import org.jamesframework.core.problems.constraints.validations.Validation;
import org.jamesframework.core.problems.objectives.evaluations.Evaluation;
import org.jamesframework.core.search.neigh.Move;

/**
 * Interface of a problem with a generic solution type. Contains methods to evaluate or validate a solution
 * or move and to create a random solution. Also indicates whether evaluations are to be maximized or minimized.
 * 
 * @author <a href="mailto:herman.debeukelaer@ugent.be">Herman De Beukelaer</a>
 * @param <SolutionType> the solution type that corresponds to this problem, required to extend {@link Solution}
 */
public interface Problem<SolutionType extends Solution> {
    
    /**
     * Evaluates a given solution. Returns an object of type {@link Evaluation}. The corresponding
     * double value can be obtained by calling {@link Evaluation#getValue()} on the evaluation object.
     * 
     * @param solution the solution to evaluate
     * @return evaluation of the given solution
     */
    public Evaluation evaluate(SolutionType solution);
    
    /**
     * <p>
     * Evaluates a move that will be applied to the current solution of a local search (delta evaluation).
     * The result corresponds to the evaluation of the modified solution that would be obtained by applying
     * the given move to the current solution. A default implementation is provided that (1) applies the move,
     * (2) computes a full evaluation by calling {@link #evaluate(Solution)} and (3) undoes the applied move.
     * </p>
     * <p>
     * It is often possible to provide a custom, much more efficient delta evaluation that computes
     * the modified evaluation based on the current evaluation and the changes that will be made
     * when applying the move to the current solution. This can be done by overriding this method.
     * It is usually required to cast the received move to a specific type so that this problem
     * requires application of neighbourhoods that generate moves of this type (or a subtype).
     * If an incompatible move type is received, an {@link IncompatibleDeltaEvaluationException}
     * may be thrown.
     * </p>
     * <p>
     * Given that both this method and the full evaluation ({@link #evaluate(Solution)}) return
     * evaluations of the same type, it is guaranteed that <code>curEvaluation</code> will also be of
     * this specific type and it is safe to perform a cast, if required.
     * </p>
     * 
     * @param move move to evaluate
     * @param curSolution current solution
     * @param curEvaluation evaluation of current solution
     * @return evaluation of modified solution obtained when applying the move to the current solution
     * @throws IncompatibleDeltaEvaluationException if the provided delta evaluation is not compatible
     *                                              with the received move type
     */
    default public Evaluation evaluate(Move<? super SolutionType> move,
                                       SolutionType curSolution,
                                       Evaluation curEvaluation){
        // apply move
        move.apply(curSolution);
        // full evaluation
        Evaluation e = evaluate(curSolution);
        // undo move
        move.undo(curSolution);
        // return evaluation
        return e;
    }
    
    /**
     * Validates a solution. Returns an object of type {@link Validation}. It can be checked wether the
     * solution passed validation by calling {@link Validation#passed()} on this validation object. When
     * a solution does not pass validation it is discarded by any search so that the best found solution
     * is guaranteed to be valid.
     * 
     * @param solution solution to validate
     * @return a validation object that indicates wether the solution passed validation
     */
    public Validation validate(SolutionType solution);
    
    /**
     * <p>
     * Validates a move that will be applied to the current solution of a local search (delta validation).
     * The result corresponds to the validation of the modified solution that would be obtained by applying
     * the given move to the current solution. A default implementation is provided that (1) applies the move,
     * (2) performs a full validation by calling {@link #validate(Solution)} and (3) undoes the applied move.
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
     * Given that both this method and the full validation ({@link #validate(Solution)}) return
     * validations of the same type, it is guaranteed that <code>curValidation</code> will also
     * be of this specific type and it is safe to perform a cast, if required.
     * </p>
     * 
     * @param move move to validate
     * @param curSolution current solution
     * @param curValidation validation of current solution
     * @return validation of modified solution obtained when applying the move to the current solution
     * @throws IncompatibleDeltaValidationException if the provided delta validation is not compatible
     *                                              with the received move type
     */
    default public Validation validate(Move<? super SolutionType> move,
                                       SolutionType curSolution,
                                       Validation curValidation){
        // apply move
        move.apply(curSolution);
        // full validation
        Validation v = validate(curSolution);
        // undo move
        move.undo(curSolution);
        // return validation
        return v;
    }
    
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
