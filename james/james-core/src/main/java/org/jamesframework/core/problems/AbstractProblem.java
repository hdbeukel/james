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

package org.jamesframework.core.problems;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import org.jamesframework.core.problems.constraints.Constraint;
import org.jamesframework.core.problems.constraints.PenalizingConstraint;
import org.jamesframework.core.problems.objectives.Objective;
import org.jamesframework.core.problems.solutions.Solution;

/**
 * <p>
 * Represents an abstract problem with generic solution type and data type. The problem manages the underlying data, objective and
 * constraints (if any). When evaluating a solution, the data is used to calculate the objective score and to check the constraints,
 * and the results are combined into an aggregated evaluation. The exact result depends on the type of constraints that were added:
 * </p>
 * <ul>
 *  <li>
 *  <p>
 *      <b>Dominating constraints</b> dominate the entire evaluation, in the sense that for any solution that violates such constraint,
 *      the objective is ignored and the worst possible evaluation is returned. This worst possible evaluation will be equal to
 *      -{@link Double#MAX_VALUE} in case of a maximizing objective, or {@link Double#MAX_VALUE} in case of a minimizing objective.
 *      Any solution that violates a dominating constraint will therefore be considered equally bad, and no solution within the
 *      constraints can ever be worse than a solution outside the constraints.
 *  </p>
 *  </li>
 *  <li>
 *  <p>
 *      <b>Penalizing constraints</b> assign a penalty to the objective score, which is usually designed to reflect the severeness
 *      of the violation. Solutions which are closer to satisfaction will then be favoured over solutions which violate the constraints
 *      more severely. In case of a maximizing objective, penalties are subtracted from the objective score, while they are added to it
 *      in case of a minimizing constraint.
 *  </p>
 *  </li>
 * </ul>
 * <p>
 * In summary: to evaluate a solution, it is first verified whether any dominating constraint is violated. If so, the worst possible
 * evaluation is returned. Else, the objective score is calculated and any penalties resulting from violated penalizing constraints
 * are added/subtracted to obtain the final aggregated evaluation.
 * </p>
 * 
 * @param <SolutionType> solution type corresponding to this problem
 * @param <DataType> type of underlying data
 * 
 * @author Herman De Beukelaer <herman.debeukelaer@ugent.be>
 */
public abstract class AbstractProblem<SolutionType extends Solution, DataType> implements Problem<SolutionType> {
    
    // objective function (can be more general than solution and data types of problem)
    private Objective<? super SolutionType, ? super DataType> objective;
    // underlying data
    private DataType data;
    // dominating and penalizing constraints (may be more general than solution and data types of problem)
    private final List<Constraint<? super SolutionType, ? super DataType>> dominatingConstraints;
    private final List<PenalizingConstraint<? super SolutionType, ? super DataType>> penalizingConstraints;
    
    /**
     * Creates a new abstract problem with given objective and data. Any objective designed for the solution and
     * data types of the problem, or more general types, is accepted. The objective can not be null, as it will
     * be called to evaluate solutions. The data may be null in the (unusual) case where no data is required for
     * a specific problem, i.e. if the objective and constraints do not require any data to evaluate/check a solution.
     * 
     * @param objective objective function (null not allowed)
     * @param data underlying data (may be null)
     * @throws NullPointerException if objective is null
     */
    public AbstractProblem(Objective<? super SolutionType, ? super DataType> objective, DataType data) throws NullPointerException {
        // check that objective is not null
        if(objective == null){
            throw new NullPointerException("Error while creating problem: null not allowed for objective.");
        }
        // set fields
        this.objective = objective;
        this.data = data;
        // initialize constraint lists
        dominatingConstraints = new ArrayList<>();
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
     * @throws NullPointerException if the objective is null
     */
    public void setObjective(Objective<? super SolutionType, ? super DataType> objective) throws NullPointerException {
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
     * Add a dominating constraint to the problem. For any solution that violates a dominating constraint, the worst possible
     * evaluation will be returned. Only constraints designed for the solution and data types of the problem, or more general types,
     * are accepted.
     * 
     * @param constraint dominating constraint to add
     */
    public void addDominatingConstraint(Constraint<? super SolutionType, ? super DataType> constraint){
        dominatingConstraints.add(constraint);
    }
    
    /**
     * Remove a dominating constraint. True is returned if the constraint has successfully been removed.
     * 
     * @param constraint dominating constraint to be removed
     * @return true if the problem contained the specified constraint
     */
    public boolean removeDominatingConstraint(Constraint<? super SolutionType, ? super DataType> constraint){
        return dominatingConstraints.remove(constraint);
    }
    
    /**
     * Add a penalizing constraint to the problem. For a solution that violates a penalizing constraint, a penalty will be
     * assigned to the objective score. Only penalizing constraints designed for the solution and data types of the problem, or
     * more general types, are accepted.
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
     * @return true if the problem contained the specified constraint
     */
    public boolean removePenalizingConstraint(PenalizingConstraint<? super SolutionType, ? super DataType> constraint){
        return penalizingConstraints.remove(constraint);
    }
    
    /**
     * Verifies whether a given solution satisfies all constraints (both dominating and penalizing constraints).
     * 
     * @param solution solution to verify
     * @return true if all constraints are satisfied
     */
    public boolean areConstraintsSatisfied(SolutionType solution){
        // check dominating constraints and penalizing constraints
        return checkConstraints(solution, dominatingConstraints) && checkConstraints(solution, penalizingConstraints);
    }
    
    /**
     * Check whether a given solution satisfies all constraints from a given list.
     * 
     * @param solution solution to be verified against list of constraints
     * @param constraints list of constraints to check
     * @return true if solution satisfies all given constraints
     */
    private boolean checkConstraints(SolutionType solution, List<? extends Constraint<? super SolutionType, ? super DataType>> constraints){
        // go through biven list of constraints
        for(Constraint<? super SolutionType,? super DataType> c : constraints){
            if(!c.isSatisfied(solution, data)){
                // not satisfied!
                return false;
            }
        }
        // all satisfied
        return true;
    }

    /**
     * Evaluates a solution by taking into account both the score calculated by the objective function and the constraints (if any).
     * If one or more dominating constraints are violated, the worst possible evaluation is returned. Depending on whether the objective
     * is maximizing or minimizing, this worst evaluation is -{@link Double#MAX_VALUE} or {@link Double#MAX_VALUE}, respectively.
     * Else, the objective function score is calculated, assigning possible penalties for each violated penalizing
     * constraint. Penalties are subtracted in case of maximization, and added in case of minimization.
     * 
     * @param solution solution to be evaluated
     * @return aggregated evaluation taking into account both the objective function score and constraints
     */
    @Override
    public double evaluate(SolutionType solution) {
        // check dominating constraints
        if(!checkConstraints(solution, dominatingConstraints)){
            // at least one dominating constraint violated, return worst evaluation
            return getWorstEvaluation();
        }
        // no violated dominating constraints: evaluate objective function
        double eval = objective.evaluate(solution, data);
        // compute penalties
        double penalty = 0.0;
        for(PenalizingConstraint<? super SolutionType, ? super DataType> pc : penalizingConstraints){
            // take into account penalty of penalizing constraint pc -- according to the general contract
            // of a penalizing constraint, no penalty (zero) will be assigned if the constraint is satisfied
            // so we do not explicitely check this to save computations
            penalty += pc.computePenalty(solution, data);
        }
        // assign penalty to evaluation
        if(objective.isMinimizing()){
            // minimizing: add penalty
            eval += penalty;
        } else {
            // maximizing: subtract penalty
            eval -= penalty;
        }
        // return aggregated evaluation
        return eval;
    }
    
    /**
     * Get worst evaluation. In case of a maximizing objective, the worst evaluation is -{@link Double#MAX_VALUE}, else,
     * it is {@link Double#MAX_VALUE}.
     * 
     * @return worst possible evaluation
     */
    private double getWorstEvaluation(){
        if(objective.isMinimizing()){
            // minimizing
            return Double.MAX_VALUE;
        } else {
            // maximizing
            return -Double.MAX_VALUE;
        }
    }

    /**
     * Computes delta based on settings of objective: if the objective is maximizing, <code>curEvaluation - prevEvaluation</code>
     * is returned, else, <code>prevEvaluation - curEvaluation</code> is returned.
     * 
     * @param prevEvaluation evaluation of previous solution
     * @param curEvaluation evaluation of current solution
     * @return improvement in evaluation, taking into account whether objective is maximizing or minimizing
     */
    @Override
    public double getDelta(double prevEvaluation, double curEvaluation) {
        double increase = curEvaluation - prevEvaluation;
        if(objective.isMinimizing()){
            // minimizing: return decrease
            return -increase;
        } else {
            // maximizing: return increase
            return increase;
        }
    }

}
