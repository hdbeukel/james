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

package org.jamesframework.core.search;

import java.util.ArrayList;
import java.util.List;
import org.jamesframework.core.exceptions.SearchException;
import org.jamesframework.core.problems.Problem;
import org.jamesframework.core.problems.Solution;
import org.jamesframework.core.problems.constraints.Validation;
import org.jamesframework.core.problems.objectives.Evaluation;
import org.jamesframework.core.search.listeners.LocalSearchListener;
import org.jamesframework.core.search.listeners.SearchListener;

/**
 * Local searches are searches that start from an initial solution and modify this solution in an attempt to improve it.
 * The initial solution can be specified using {@link #setCurrentSolution(Solution)} before running the search, else,
 * a random initial solution is used. The current solution is retained across subsequent runs of a local search.
 * This means that upon restarting a local search, it will continue from where it had arrived in the previous run.
 * 
 * @param <SolutionType> solution type of the problems that may be solved using this search, required to extend {@link Solution}
 * @author <a href="mailto:herman.debeukelaer@ugent.be">Herman De Beukelaer</a>
 */
public abstract class LocalSearch<SolutionType extends Solution> extends Search<SolutionType> {

    /******************/
    /* PRIVATE FIELDS */
    /******************/
    
    // current solution and its corresponding evaluation/validation
    private SolutionType curSolution;
    private Evaluation curSolutionEvaluation;
    private Validation curSolutionValidation;
    
    /************************/
    /* PRIVATE FINAL FIELDS */
    /************************/
    
    // list containing local search listeners attached to this search
    private final List<LocalSearchListener<? super SolutionType>> localSearchListeners;
    
    /***************/
    /* CONSTRUCTOR */
    /***************/
    
    /**
     * Create a new local search to solve the given problem, with default name "LocalSearch".
     * 
     * @throws NullPointerException if <code>problem</code> is <code>null</code>
     * @param problem problem to solve
     */
    public LocalSearch(Problem<SolutionType> problem){
        this(null, problem);
    }
    
    /**
     * Create a new local search to solve the given problem, with a custom name. If <code>name</code>
     * is <code>null</code>, the default name "LocalSearch" will be assigned.
     * 
     * @throws NullPointerException if <code>problem</code> is <code>null</code>
     * @param problem problem to solve
     * @param name custom search name
     */
    public LocalSearch(String name, Problem<SolutionType> problem){
        super(name != null ? name : "LocalSearch", problem);
        // initially, current solution and its evaluation/validation are null
        curSolution = null;
        curSolutionEvaluation = null;
        curSolutionValidation = null;
        // initialize list for local search listeners
        localSearchListeners = new ArrayList<>();
    }
    
    /******************/
    /* INITIALIZATION */
    /******************/
    
    /**
     * When the search is started, a random initial solution is generated if none has been specified.
     */
    @Override
    protected void searchStarted(){
        // call super
        super.searchStarted();
        // create random initial solution if none is set
        if(curSolution == null){
            updateCurrentAndBestSolution(getProblem().createRandomSolution());
        }
    }
    
    /**************************************************/
    /* OVERRIDDEN METHODS FOR ADDING SEARCH LISTENERS */
    /**************************************************/
    
    /**
     * Add a search listener, if not already added before. Passes the listener to its parent (general search),
     * but also stores it locally in case it is a local search listener, to fire local search specific callbacks.
     * Note that this method may only be called when the search is idle.
     * 
     * @param listener search listener to add to the search
     * @throws SearchException if the search is not idle
     * @return <code>true</code> if the search listener is successfully added (not added before)
     */
    @Override
    public boolean addSearchListener(SearchListener<? super SolutionType> listener){
        // acquire status lock
        synchronized(getStatusLock()){
            // pass to super (also checks whether search is idle)
            boolean a = super.addSearchListener(listener);
            // store locally in case of a local search listener
            if(listener instanceof LocalSearchListener){
                localSearchListeners.add((LocalSearchListener<? super SolutionType>) listener);
            }
            return a;
        }
    }
    
    /**
     * Remove the given search listener. If the search listener had not been added, <code>false</code> is returned.
     * Calls its parent (general search) to remove the listener, and also removes it locally in case it is a
     * local search listener. Note that this method may only be called when the search is idle.
     * 
     * @param listener search listener to be removed
     * @throws SearchException if the search is not idle
     * @return <code>true</code> if the listener has been successfully removed
     */
    @Override
    public boolean removeSearchListener(SearchListener<? super SolutionType> listener){
        // acquire status lock
        synchronized(getStatusLock()){
            // call super (also verifies status)
            boolean r = super.removeSearchListener(listener);
            // also remove locally in case of a local search listener
            if(listener instanceof LocalSearchListener){
                localSearchListeners.remove((LocalSearchListener<? super SolutionType>) listener);
            }
            return r;
        }
    }
    
    /**************************************************************/
    /* PRIVATE METHODS FOR FIRING LOCAL SEARCH LISTENER CALLBACKS */
    /**************************************************************/
    
    /**
     * Calls {@link LocalSearchListener#modifiedCurrentSolution(LocalSearch, Solution, Evaluation, Validation)} on
     * every attached local search listener. Should only be executed when the search is active (initializing, running
     * or terminating) and only be fired exactly once for each update of the current solution.
     */
    private void fireModifiedCurrentSolution(SolutionType newCurrentSolution,
                                             Evaluation newCurrentSolutionEvaluation,
                                             Validation newCurrentSolutionValidation){
        localSearchListeners.forEach(l -> {
                                l.modifiedCurrentSolution(this,
                                                          newCurrentSolution,
                                                          newCurrentSolutionEvaluation,
                                                          newCurrentSolutionValidation);
                            });
    }
    
    /******************************************/
    /* STATE ACCESSORS (RETAINED ACROSS RUNS) */
    /******************************************/
    
    /**
     * Get the current solution. Might be worse than the best solution found so far. The current solution is
     * <b>retained</b> across subsequent runs of a local search. May return <code>null</code> if no current
     * solution has been set yet, for example when the search has just been created or is still initializing.
     * 
     * @return current solution, if set; <code>null</code> otherwise
     */
    public SolutionType getCurrentSolution(){
        return curSolution;
    }
    
    /**
     * Get the evaluation of the current solution. The current solution is <b>retained</b> across subsequent
     * runs of a local search. May return <code>null</code> if no current solution has been set yet, for example
     * when the search has just been created or is still initializing.
     * 
     * @return evaluation of current solution, if already defined; <code>null</code> otherwise
     */
    public Evaluation getCurrentSolutionEvaluation(){
        return curSolutionEvaluation;
    }
    
    /**
     * Get the validation of the current solution. The current solution is <b>retained</b> across subsequent
     * runs of a local search. May return <code>null</code> if no current solution has been set yet, for example
     * when the search has just been created or is still initializing.
     * 
     * @return validation of current solution, if already defined; <code>null</code> otherwise
     */
    public Validation getCurrentSolutionValidation(){
        return curSolutionValidation;
    }
    
    /*************************/
    /* PUBLIC STATE MUTATORS */
    /*************************/
    
    /**
     * Sets the current solution prior to execution or in between search runs. The given solution is evaluated and
     * validated, and it is checked whether it is a valid first/new best solution. This method may for example be
     * used to specify a custom initial solution before starting the search. Note that it may only be called when
     * the search is idle.
     * 
     * @throws SearchException if the search is not idle
     * @throws NullPointerException if <code>solution</code> is <code>null</code>
     * @param solution current solution to be adopted
     */
    public void setCurrentSolution(SolutionType solution){
        // synchronize with status updates
        synchronized(getStatusLock()){
            // assert idle
            assertIdle("Cannot set current solution.");
            // check not null
            if(solution == null){
                throw new NullPointerException("Cannot set current solution: received null.");
            }
            // go ahead and adjust current solution
            updateCurrentAndBestSolution(solution);
        }
    }
    
    /***********************/
    /* PROTECTED UTILITIES */
    /***********************/
    
    /**
     * Update the current solution during search. The solution is evaluated and validated but the update takes
     * place regardless of the actual obtained evaluation and validation. More precisely, it is <b>not</b> required
     * that the current solution is valid.
     * 
     * @param solution new current solution
     */
    protected void updateCurrentSolution(SolutionType solution){
        updateCurrentSolution(solution, getProblem().evaluate(solution), getProblem().validate(solution));
    }
    
    /**
     * Update the current solution during search, given that it has already been evaluated and validated.
     * The new current solution and its evaluation/validation are stored and attached local search listeners
     * are informed about this update. The update always takes place, it is <b>not</b> required that the
     * current solution is valid.
     * 
     * @param solution new current solution
     * @param evaluation evaluation of new current solution
     * @param validation validation of new current solution
     */
    protected void updateCurrentSolution(SolutionType solution, Evaluation evaluation, Validation validation){
        // store new current solution
        curSolution = solution;
        // store evaluation and validation
        curSolutionEvaluation = evaluation;
        curSolutionValidation = validation;
        // inform listeners
        fireModifiedCurrentSolution(curSolution, curSolutionEvaluation, curSolutionValidation);
    }

    /**
     * Update the current and best solution during search. The given solution is evaluated and validated,
     * followed by an update of the current solution (also if it is invalid). Conversely, the best solution
     * is only updated if the given solution is valid and improves over the best solution found so far.
     * 
     * @param solution new current solution
     * @return <code>true</code> if the best solution has been updated
     */
    protected boolean updateCurrentAndBestSolution(SolutionType solution){
        return updateCurrentAndBestSolution(solution, getProblem().evaluate(solution), getProblem().validate(solution));
    }
    
    /**
     * Update the current and best solution during search, given that the respective solution has already
     * been evaluated and validated. The current solution is always updated, also if it is invalid. Conversely,
     * the best solution is only updated if the given solution is valid and improves over the best solution
     * found so far.
     * 
     * @param solution new current solution
     * @param evaluation evaluation of new current solution
     * @param validation validation of new current solution
     * @return <code>true</code> if the best solution has been updated
     */
    protected boolean updateCurrentAndBestSolution(SolutionType solution, Evaluation evaluation, Validation validation){
        // update current solution
        updateCurrentSolution(solution, evaluation, validation);
        // update best solution (only updates if solution is valid)
        return updateBestSolution(solution, evaluation, validation);
    }
    
}
