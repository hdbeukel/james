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
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import org.jamesframework.core.problems.constraints.Constraint;
import org.jamesframework.core.problems.constraints.PenalizingConstraint;
import org.jamesframework.core.problems.objectives.Objective;
import org.jamesframework.core.problems.solutions.Solution;

/**
 * <p>
 * Represents an abstract problem that separates data from the objective and constraints (if any). The problem contains
 * data of a specific data type (parameter <code>DataType</code>) and solutions are evaluated and validated based on a
 * combination of an objective and constraints which use the underlying data. Two types of constraints can be specified:
 * </p>
 * <ul>
 *  <li>
 *  <p>
 *      <b>Rejecting constraints</b>: any solution violating a rejecting constraint is immediately rejected, regardless
 *      of its evaluation. More precisely, for such solutions, {@link #rejectSolution(Solution)} returns <code>true</code>.
 *  </p>
 *  </li>
 *  <li>
 *  <p>
 *      <b>Penalizing constraints</b>: assign a penalty to the evaluation calculated by the objective, which is
 *      usually chosen to reflect the severeness of the violation. Solutions which are closer to satisfaction will
 *      then be favoured over solutions which violate the constraints more severely. In case of a maximizing objective,
 *      penalties are subtracted from the objective score, while they are added in case of a minimizing objective.
 *  </p>
 *  </li>
 * </ul>
 * 
 * @param <SolutionType> solution type corresponding to this problem
 * @param <DataType> type of underlying data
 * 
 * @author <a href="mailto:herman.debeukelaer@ugent.be">Herman De Beukelaer</a>
 */
public abstract class ProblemWithData<SolutionType extends Solution, DataType> implements Problem<SolutionType> {
    
    // objective function (can be more general than solution and data types of problem)
    private Objective<? super SolutionType, ? super DataType> objective;
    // underlying data
    private DataType data;
    // rejecting and penalizing constraints (may be more general than solution and data types of problem)
    private final List<Constraint<? super SolutionType, ? super DataType>> rejectingConstraints;
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
    public ProblemWithData(Objective<? super SolutionType, ? super DataType> objective, DataType data) {
        // check that objective is not null
        if(objective == null){
            throw new NullPointerException("Error while creating problem: null not allowed for objective.");
        }
        // set fields
        this.objective = objective;
        this.data = data;
        // initialize constraint lists
        rejectingConstraints = new ArrayList<>();
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
     * Add a rejecting constraint to the problem. For any solution that violates such constraint, <code>rejectSolution(...)</code>
     * will return <code>true</code>. Only constraints designed for the solution and data types of the problem, or more general types,
     * are accepted.
     * 
     * @param constraint rejecting constraint to add
     */
    public void addRejectingConstraint(Constraint<? super SolutionType, ? super DataType> constraint){
        rejectingConstraints.add(constraint);
    }
    
    /**
     * Remove a rejecting constraint. Returns <code>true</code> if the constraint has successfully been removed.
     * 
     * @param constraint rejecting constraint to be removed
     * @return <code>true</code> if the problem contained the specified constraint
     */
    public boolean removeRejectingConstraint(Constraint<? super SolutionType, ? super DataType> constraint){
        return rejectingConstraints.remove(constraint);
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
     * Checks whether any of the specified rejecting constraints are violated. If so, this method returns
     * <code>true</code>.
     * 
     * @param solution solution to check against the rejecting constraints
     * @return <code>true</code> if any rejecting constraint is violated
     */
    @Override
    public boolean rejectSolution(SolutionType solution){
        // check rejecting constraints
        for(Constraint<? super SolutionType, ? super DataType> c : rejectingConstraints){
            if(!c.isSatisfied(solution, data)){
                return true;
            }
        }
        // all satisfied
        return false;
    }
    
    /**
     * Returns a set of all violated constraints (both rejecting and penalizing constraints). If the given solution
     * satisfies all constraints, the returned set will be empty.
     * 
     * @param solution solution for which violated constraints are determined
     * @return set of all violated constraints (rejecting and penalizing)
     */
    public Set<Constraint<? super SolutionType, ? super DataType>> getViolatedConstraints(SolutionType solution){
        // create set with all violated constraints
        Set<Constraint<? super SolutionType, ? super DataType>> violated = new HashSet<>();
        // rejecting constraints
        for(Constraint<? super SolutionType, ? super DataType> c : rejectingConstraints){
            if(!c.isSatisfied(solution, data)){
                violated.add(c);
            }
        }
        // penalizing
        for(Constraint<? super SolutionType, ? super DataType> c : penalizingConstraints){
            if(!c.isSatisfied(solution, data)){
                violated.add(c);
            }
        }
        // return violated constraints
        return violated;
    }

    /**
     * Evaluates a solution by taking into account both the score calculated by the objective function and the
     * penalizing constraints (if any). First, the objective function is calculated for the given solution.
     * Then, penalties are assigned for any violated penalizing constraint. Penalties are subtracted in case
     * of maximization, and added in case of minimization. The resulting score is returned.
     * 
     * @param solution solution to be evaluated
     * @return aggregated evaluation taking into account both the objective function and penalizing constraints
     */
    @Override
    public double evaluate(SolutionType solution) {
        // evaluate objective function
        double eval = objective.evaluate(solution, data);
        // compute penalties
        double penalty = 0.0;
        for(PenalizingConstraint<? super SolutionType, ? super DataType> pc : penalizingConstraints){
            // take into account penalty of penalizing constraint pc -- according to the general contract
            // of a penalizing constraint, no penalty (zero) will be assigned if the constraint is satisfied
            // so this is not explicitely checked to avoid unnecessary computations
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
     * Indicates whether the underlying objective is minimizing.
     * 
     * @return true if the objective is minimizing
     */
    @Override
    public boolean isMinimizing(){
        return objective.isMinimizing();
    }

}
