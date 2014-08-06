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
import org.jamesframework.core.search.listeners.LocalSearchListener;
import org.jamesframework.core.search.listeners.SearchListener;

/**
 * Local searches are searches that start with some initial solution and modify this solution in an attempt to improve
 * it. The initial solution can be specified using {@link #setCurrentSolution(Solution)} before running the search, else,
 * a random initial solution will be constructed. The current solution and corresponding evaluation are retained across
 * subsequent runs of a local search. This means that upon restarting a local search, it will continue from where it had
 * arrived in the previous run.
 * 
 * @param <SolutionType> solution type of the problems that may be solved using this search, required to extend {@link Solution}
 * @author <a href="mailto:herman.debeukelaer@ugent.be">Herman De Beukelaer</a>
 */
public abstract class LocalSearch<SolutionType extends Solution> extends Search<SolutionType> {

    /******************/
    /* PRIVATE FIELDS */
    /******************/
    
    // current solution and its corresponding evaluation
    private SolutionType curSolution;
    private double curSolutionEvaluation;
    
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
        // initially, current solution is null and its evaluation
        // is arbitrary (as defined in getCurrentSolutionEvaluation())
        curSolution = null;
        curSolutionEvaluation = 0.0; // arbitrary value
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
     * Calls {@link LocalSearchListener#modifiedCurrentSolution(LocalSearch, Solution, double)} on every attached
     * local search listener. Should only be executed when the search is active (initializing, running or terminating).
     * Also, the callback should only be fired exactly once for each update of the current solution.
     */
    private void fireModifiedCurrentSolution(SolutionType newCurrentSolution, double newCurrentSolutionEvaluation){
        for(LocalSearchListener<? super SolutionType> listener : localSearchListeners){
            listener.modifiedCurrentSolution(this, newCurrentSolution, newCurrentSolutionEvaluation);
        }
    }
    
    /******************************************/
    /* STATE ACCESSORS (RETAINED ACROSS RUNS) */
    /******************************************/
    
    /**
     * Returns the current solution. The current solution might be worse than the best solution found so far.
     * Note that it is <b>retained</b> across subsequent runs of a local search. May return <code>null</code>
     * if no current solution has been set yet, for example when the search has just been created or is still
     * initializing the current run.
     * 
     * @return current solution, if set; <code>null</code> otherwise
     */
    public SolutionType getCurrentSolution(){
        return curSolution;
    }
    
    /**
     * Get the evaluation of the current solution. The current solution and its evaluation are <b>retained</b>
     * across subsequent runs of a local search. If the current solution is not yet defined, i.e. when
     * {@link #getCurrentSolution()} returns <code>null</code>, the result of this method is undefined;
     * in such case it may return any arbitrary value.
     * 
     * @return evaluation of current solution, if already defined; arbitrary value otherwise
     */
    public double getCurrentSolutionEvaluation(){
        return curSolutionEvaluation;
    }
    
    /*************************/
    /* PUBLIC STATE MUTATORS */
    /*************************/
    
    /**
     * Sets the current solution. The given solution is automatically evaluated and compared with the
     * currently known best solution, to check if it improves on this solution. This method may for
     * example be used to specify a custom initial solution before starting the search. Note that it
     * may only be called when the search is idle.
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
     * Update the current solution during search, given that it has already been evaluated. This method stores the
     * new current solution and its evaluation, and informs any local search listeners about this update. It does
     * <b>not</b> validate the new current solution, if it is required that {@link Problem#rejectSolution(Solution)}
     * returns <code>false</code> this must be verified before updating the current solution.
     * 
     * @param solution new current solution
     * @param evaluation evaluation of new current solution
     */
    protected void updateCurrentSolution(SolutionType solution, double evaluation){
        // store new current solution
        curSolution = solution;
        // store evaluation
        curSolutionEvaluation = evaluation;
        // inform listeners
        fireModifiedCurrentSolution(curSolution, curSolutionEvaluation);
    }
    
    /**
     * Update the current and best solution during search. The current solution is first evaluated and then updated,
     * also in case it is an invalid solution (see {@link Problem#rejectSolution(Solution)}). Conversely, the best
     * solution is only updated if the new current solution is not rejected, to ensure that the best solution is
     * always valid.
     * 
     * @param solution new current solution
     */
    protected void updateCurrentAndBestSolution(SolutionType solution){
        updateCurrentAndBestSolution(solution, getProblem().evaluate(solution));
    }
    
    /**
     * Update the current and best solution during search, given that the new current solution has already
     * been evaluated. The current solution is always updated, also in case it is an invalid solution (see
     * {@link Problem#rejectSolution(Solution)}). Conversely, the best solution is only updated if the new
     * current solution is not rejected, to ensure that the best solution is always valid.
     * 
     * @param solution new current solution
     * @param evaluation evaluation of new current solution
     */
    protected void updateCurrentAndBestSolution(SolutionType solution, double evaluation){
        updateCurrentAndBestSolution(solution, evaluation, false);
    }
    
    /**
     * Update the current and best solution during search, given that the new current solution has already
     * been evaluated. The current solution is always updated, also in case it is an invalid solution (see
     * {@link Problem#rejectSolution(Solution)}). Conversely, the best solution is only updated if the new
     * current solution is not rejected, to ensure that the best solution is always valid, unless
     * <code>skipBestSolutionValidation</code> is <code>true</code>. In the latter case, it should
     * have been verified that the given solution is valid before calling this method; revalidation
     * is then ommitted.
     * 
     * @param solution new current solution
     * @param evaluation evaluation of new current solution
     * @param skipBestSolutionValidation if <code>true</code>, the best solution is always updated, without validating
     *                                   the given solution (useful if this solution is already known to be valid)
     */
    protected void updateCurrentAndBestSolution(SolutionType solution, double evaluation, boolean skipBestSolutionValidation){
        // update current solution
        updateCurrentSolution(solution, evaluation);
        // update best solution, if solution is not rejected (or if validation is skipped)
        if(skipBestSolutionValidation || !getProblem().rejectSolution(solution)){
            updateBestSolution(solution, evaluation);
        }
    }
    
}
