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

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import org.jamesframework.core.exceptions.IncompatibleDeltaEvaluationException;
import org.jamesframework.core.exceptions.IncompatibleDeltaValidationException;
import org.jamesframework.core.problems.constraints.Constraint;
import org.jamesframework.core.problems.constraints.PenalizingConstraint;
import org.jamesframework.core.problems.constraints.PenalizingValidation;
import org.jamesframework.core.problems.constraints.Validation;
import org.jamesframework.core.problems.constraints.validations.UnanimousValidation;
import org.jamesframework.core.problems.objectives.Evaluation;
import org.jamesframework.core.problems.objectives.Objective;
import org.jamesframework.core.problems.objectives.evaluations.PenalizedEvaluation;
import org.jamesframework.core.search.neigh.Move;

/**
 * <p>
 * Represents an abstract problem that separates data from the objective and constraints (if any). The problem contains
 * data of a specific data type (parameter <code>DataType</code>) and solutions are evaluated and validated based on a
 * combination of an objective and constraints which use the underlying data. Two types of constraints can be specified:
 * </p>
 * <ul>
 *  <li>
 *  <p>
 *      <b>Mandatory constraints</b>: a solution is valid only if it satisfies all mandatory constraints.
 *      If not, it is discarded regardless of its evaluation. It is guaranteed that the best solution found by
 *      a search will always satisfy all mandatory constraints. In a neighbourhood search, only those neighbours
 *      of the current solution that satisfy all mandatory constraints are considered.
 *  </p>
 *  </li>
 *  <li>
 *  <p>
 *      <b>Penalizing constraints</b>: if a solution does not pass validation by a penalizing constraint, a penalty
 *      is assigned to its evaluation. The solution is not discarded. Penalties are usually chosen to reflect the
 *      severeness of the violation. Solutions closer to satisfaction are then favoured over solutions that violate
 *      the constraints more severely. In case of maximization, penalties are subtracted from the evaluation, while
 *      they are added to it in case of minimization. Depending on the interaction between the evaluation and penalties,
 *      the best found solution might not satisfy all penalizing constraints (which may or may not be desired).
 *  </p>
 *  </li>
 * </ul>
 * 
 * @param <SolutionType> type of solutions to the problem, required to extend {@link Solution}
 * @param <DataType> type of underlying data
 * 
 * @author <a href="mailto:herman.debeukelaer@ugent.be">Herman De Beukelaer</a>
 */
public abstract class AbstractProblem<SolutionType extends Solution, DataType> implements Problem<SolutionType> {
    
    // objective function (can be more general than solution and data types of problem)
    private Objective<? super SolutionType, ? super DataType> objective;
    // underlying data
    private DataType data;
    // mandatory and penalizing constraints (may be more general than solution and data types of problem)
    private final List<Constraint<? super SolutionType, ? super DataType>> mandatoryConstraints;
    private final List<PenalizingConstraint<? super SolutionType, ? super DataType>> penalizingConstraints;
    
    /**
     * Creates a new abstract problem with given objective and data. Any objective designed for the solution and
     * data types of the problem, or more general types, is accepted. The objective can not be <code>null</code>,
     * as it will be called to evaluate solutions.
     * 
     * @param objective objective function
     * @param data underlying data
     * @throws NullPointerException if <code>objective</code> is <code>null</code>
     */
    public AbstractProblem(Objective<? super SolutionType, ? super DataType> objective, DataType data) {
        // check that objective is not null
        if(objective == null){
            throw new NullPointerException("Error while creating problem: null not allowed for objective.");
        }
        // set fields
        this.objective = objective;
        this.data = data;
        // initialize constraint lists
        mandatoryConstraints = new ArrayList<>();
        penalizingConstraints = new ArrayList<>();
    }

    /**
     * Get the objective function.
     * 
     * @return objective function
     */
    public Objective<? super SolutionType, ? super DataType> getObjective() {
        return objective;
    }

    /**
     * Set a new objective function. Any objective designed for the solution and data types of the problem,
     * or more general types, is accepted. The objective can not be null, as it will be called to evaluate solutions.
     * 
     * @param objective new objective function
     * @throws NullPointerException if <code>objective</code> is <code>null</code>
     */
    public void setObjective(Objective<? super SolutionType, ? super DataType> objective) {
        // check not null
        if(objective == null){
            throw new NullPointerException("Error while setting new objective in problem: null is not allowed.");
        }
        this.objective = objective;
    }

    /**
     * Get the underlying data.
     * 
     * @return underlying data
     */
    public DataType getData() {
        return data;
    }

    /**
     * Set new underlying data.
     * 
     * @param data new underlying data
     */
    public void setData(DataType data) {
        this.data = data;
    }
    
    /**
     * Add a mandatory constraint to the problem. Only those solutions that satisfy all mandatory
     * constraints will pass validation (see {@link #validate(Solution)}). Other solutions are discarded
     * regardless of their evaluation.
     * <p>
     * Only constraints designed for the solution and data type of the problem (or more general) are accepted.
     * 
     * @param constraint mandatory constraint to add
     */
    public void addMandatoryConstraint(Constraint<? super SolutionType, ? super DataType> constraint){
        mandatoryConstraints.add(constraint);
    }
    
    /**
     * Remove a mandatory constraint. Returns <code>true</code> if the constraint has been successfully removed.
     * 
     * @param constraint mandatory constraint to be removed
     * @return <code>true</code> if the constraint is successfully removed
     */
    public boolean removeMandatoryConstraint(Constraint<? super SolutionType, ? super DataType> constraint){
        return mandatoryConstraints.remove(constraint);
    }
    
    /**
     * Add a penalizing constraint to the problem. For a solution that violates a penalizing constraint, a penalty will be
     * assigned to the objective score. Only penalizing constraints designed for the solution and data type of the problem
     * (or more general) are accepted.
     * 
     * @param constraint penalizing constraint to add
     */
    public void addPenalizingConstraint(PenalizingConstraint<? super SolutionType, ? super DataType> constraint){
        penalizingConstraints.add(constraint);
    }
    
    /**
     * Remove a penalizing constraint. True is returned if the constraint has successfully been removed.
     * 
     * @param constraint penalizing constraint to be removed
     * @return <code>true</code> if the constraint is successfully removed
     */
    public boolean removePenalizingConstraint(PenalizingConstraint<? super SolutionType, ? super DataType> constraint){
        return penalizingConstraints.remove(constraint);
    }
    
    /**
     * <p>
     * Validate a solution by checking all mandatory constraints. The solution
     * will only pass validation if all mandatory constraints are satisfied.
     * </p>
     * <p>
     * This is a short-circuiting operation: as soon as one violated constraint
     * is found the remaining constraints are not checked.
     * </p>
     * 
     * @param solution solution to validate
     * @return validation indicating whether all mandatory constraints are satisfied
     */
    @Override
    public Validation validate(SolutionType solution){
        UnanimousValidation val = new UnanimousValidation();
        mandatoryConstraints.stream()
                            .allMatch(c -> {
                                // validate solution against constraint c
                                Validation cval = c.validate(solution, data);
                                // add to unanimous validation
                                val.addValidation(c, cval);
                                // continue until one constraint is not satisfied
                                return cval.passed();
                            });
        return val;
    }
    
    /**
     * <p>
     * Validate a move by checking all mandatory constraints. The move will
     * only pass validation if all mandatory constraints are satisfied.
     * </p>
     * <p>
     * This is a short-circuiting operation: as soon as one violated constraint
     * is found the remaining constraints are not checked.
     * </p>
     * 
     * @param move move to validate
     * @param curSolution current solution of a local search
     * @param curValidation validation of current solution
     * @throws IncompatibleDeltaValidationException if the provided delta validation of any mandatory
     *                                              constraint is not compatible with the received move type
     * @return validation indicating whether all mandatory constraints are satisfied
     */
    @Override
    public Validation validate(Move<? super SolutionType> move,
                                        SolutionType curSolution,
                                        Validation curValidation){
        UnanimousValidation curUnanimousVal = (UnanimousValidation) curValidation;
        UnanimousValidation newUnanimousVal = new UnanimousValidation();
        mandatoryConstraints.stream()
                            .allMatch(c -> {
                                // retrieve original validation produced by constraint c
                                Validation curval = curUnanimousVal.getValidation(c);
                                // validate move against constraint c
                                Validation newval;
                                if(curval != null){
                                    // current validation known: delta validation
                                    newval = c.validate(move, curSolution, curval, data);
                                } else {
                                    // current validation unknown: full validation
                                    // (can happen due to short-circuiting behaviour)
                                    newval = c.validate(curSolution, data);
                                }
                                // add to unanimous validation
                                newUnanimousVal.addValidation(c, newval);
                                // continue until one constraint is not satisfied
                                return newval.passed();
                            });
        return newUnanimousVal;
    }
    
    /**
     * Returns a collection of all violated constraints (both mandatory and penalizing).
     * 
     * @param solution solution for which all violated constraints are determined
     * @return collection of all violated constraints (mandatory and penalizing); possibly empty
     */
    public Collection<Constraint<? super SolutionType, ? super DataType>> getViolatedConstraints(SolutionType solution){
        // return set with all violated constraints
        return Stream.concat(mandatoryConstraints.stream(), penalizingConstraints.stream())
                     .filter(c -> !c.validate(solution, data).passed())
                     .collect(Collectors.toSet());
    }

    /**
     * Evaluates a solution by taking into account both the evaluation calculated by the objective function and the
     * penalizing constraints (if any). Penalties are assigned for any violated penalizing constraint, which are
     * subtracted from the evaluation in case of maximization, and added to it in case of minimization.
     * 
     * @param solution solution to be evaluated
     * @return aggregated evaluation taking into account both the objective function and penalizing constraints
     */
    @Override
    public Evaluation evaluate(SolutionType solution) {
        // evaluate objective function
        Evaluation eval = objective.evaluate(solution, data);
        // initialize penalized evaluation object
        PenalizedEvaluation penEval = new PenalizedEvaluation(eval, isMinimizing());
        // add penalties
        penalizingConstraints.forEach(pc -> penEval.addPenalizingValidation(pc, pc.validate(solution, data)));
        // return aggregated evaluation
        return penEval;
    }
    
    /**
     * Evaluate a move by taking into account both the evaluation of the modified solution and
     * the penalizing constraints (if any). Penalties are assigned for any violated penalizing
     * constraint, which are subtracted from the evaluation in case of maximization, and added
     * to it in case of minimization.
     * 
     * @param move move to evaluate
     * @param curSolution current solution
     * @param curEvaluation current evaluation
     * @throws IncompatibleDeltaEvaluationException if the provided delta evaluation of the objective
     *                                              is not compatible with the received move type
     * @throws IncompatibleDeltaValidationException if the provided delta validation of any penalizing
     *                                              constraint is not compatible with the received move type
     * @return aggregated evaluation of modified solution, taking into account both the objective
     *         function and penalizing constraints
     */
    @Override
    public Evaluation evaluate(Move<? super SolutionType> move, SolutionType curSolution, Evaluation curEvaluation){
        PenalizedEvaluation curPenalizedEval = (PenalizedEvaluation) curEvaluation;
        // retrieve current evaluation without penalties
        Evaluation curEval = curPenalizedEval.getEvaluation();
        // perform delta evaluation
        Evaluation newEval = objective.evaluate(move, curSolution, curEval, data);
        // initialize new penalized evaluation
        PenalizedEvaluation newPenalizedEval = new PenalizedEvaluation(newEval, isMinimizing());
        // perform delta validation for each penalizing constraint
        penalizingConstraints.forEach(pc -> {
            // retrieve current penalizing validation
            PenalizingValidation curVal = curPenalizedEval.getPenalizingValidation(pc);
            // delta validation
            PenalizingValidation newVal = pc.validate(move, curSolution, curVal, data);
            // add penalty
            newPenalizedEval.addPenalizingValidation(pc, newVal);
        });
        return newPenalizedEval;
    }

    /**
     * Indicates whether the underlying objective is minimizing.
     * 
     * @return true if the objective is minimizing
     */
    @Override
    public boolean isMinimizing(){
        return objective.isMinimizing();
    }

}
