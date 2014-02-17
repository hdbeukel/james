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

package org.jamesframework.core.search;

import org.jamesframework.core.search.listeners.SearchListener;
import org.jamesframework.core.search.stopcriteria.StopCriterion;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;
import org.jamesframework.core.exceptions.IncompatibleStopCriterionException;
import org.jamesframework.core.exceptions.JamesRuntimeException;
import org.jamesframework.core.exceptions.SearchException;
import org.jamesframework.core.problems.Problem;
import org.jamesframework.core.problems.solutions.Solution;
import org.jamesframework.core.search.stopcriteria.StopCriterionChecker;
import org.jamesframework.core.util.JamesConstants;

/**
 * <p>
 * General abstract search used to solve a problem with the specified solution type. It provides general methods to
 * start and stop the search, and to access state information and metadata such as the best solution found so far and the
 * runtime of the current run. It also provides methods to add and remove search listeners and stop criteria.
 * </p>
 * <p>
 * A search can have four possible statuses: IDLE, INITIALIZING, RUNNING or TERMINATING (see {@link SearchStatus}). When
 * a search is created, it is IDLE. Upon calling {@link #start()} it first goes to INITIALIZING and then RUNNING, after
 * successful initialization. While the search is running, it iteratively calls {@link #searchStep()} as defined in each
 * specific search implementation.
 * </p>
 * <p>
 * Whenever a search is requested to stop, by calling {@link #stop()}, it goes to status TERMINATING. A terminating
 * search will stop after it has completed its current step, and then its status goes back to IDLE. A search may also
 * terminate itself by calling {@link #stop()} internally, when it has come to its natural end. In particular, a
 * single step algorithm can be implemented by calling {@link #stop()} immediately at the end of this first and
 * only step.
 * </p>
 * <p>
 * An idle search may be restarted at any time. The search state is retained across subsequent runs, including the best
 * solution found so far and any search specific state elements, unless explicitely stated otherwise. On the other hand,
 * the following metadata applies to the current run only:
 * </p>
 * <ul>
 *  <li>current runtime</li>
 *  <li>current number of steps</li>
 *  <li>time without improvement</li>
 *  <li>steps without improvement</li>
 *  <li>minimum delta</li>
 * </ul>
 * <p>
 * This might also be the case for additional metadata in specific searches, which should be clearly indicated in their
 * documentation. Note that stop criteria relying on such metadata will operate on a per-run basis.
 * </p>
 * 
 * @param <SolutionType> solution type of the problems that may be solved using this search, required to extend {@link Solution}
 * @author Herman De Beukelaer <herman.debeukelaer@ugent.be>
 */
public abstract class Search<SolutionType extends Solution> {

    /******************/
    /* PRIVATE FIELDS */
    /******************/
    
    // timestamp indicating when the current (or last) run was started
    private long startTime;
    // timestamp indicating when the last run finished
    private long stopTime;
    
    // number of steps completed in the current (or last) run
    private long currentSteps;
    
    // timestamp indicating when the last improvement was made during the current (or last) run
    private long lastImprovementTime;
    
    // steps completed since last improvement during current (or last) run
    private long stepsSinceLastImprovement;
    // flags improvement during current step, used to update steps since last improvement at the end of each step
    private boolean improvementDuringCurrentStep;
    
    // minimum improvement in evaluation of a newly found best solution
    // over the previously known best solution, during the current (or last) run
    private double minDelta;
    
    // best solution found so far and its corresponding evaluation
    private SolutionType bestSolution;
    private double bestSolutionEvaluation;
    
    // search status
    private SearchStatus status;
    
    /************************/
    /* PRIVATE FINAL FIELDS */
    /************************/
    
    // problem being solved
    private final Problem<SolutionType> problem;
    
    // list containing search listeners attached to this search
    private final List<SearchListener<? super SolutionType>> searchListeners;
    
    // stop criterion checker dedicated to checking the stop criteria attached to this search
    private final StopCriterionChecker stopCriterionChecker;
    
    /*********/
    /* LOCKS */
    /*********/
    
    // lock acquired when updating/accessing best solution and its evaluation to ensure consistency
    private final Object bestSolutionLock = new Object();
    
    // lock acquired when updating the search status, when executing a block of code during which
    // the status is not allowed to change, and for consistent updates/access of status dependent data
    private final Object statusLock = new Object();
    
    /***************/
    /* CONSTRUCTOR */
    /***************/
    
    /**
     * Creates a search to solve the given problem.
     * 
     * @param problem problem to solve
     */
    public Search(Problem<SolutionType> problem){
        // store problem reference
        this.problem = problem;
        // initialize search listener list
        searchListeners = new ArrayList<>();
        // create dedicated stop criterion checker
        stopCriterionChecker = new StopCriterionChecker(this);
        // set initial status to idle
        status = SearchStatus.IDLE;
        // initially, best solution is null and its evaluation
        // is arbitrary (as defined in getBestSolutionEvaluation())
        bestSolution = null;
        bestSolutionEvaluation = 0.0; // arbitrary value
        // initialize per-run metadata
        startTime = JamesConstants.INVALID_TIMESTAMP;
        stopTime = JamesConstants.INVALID_TIMESTAMP;
        currentSteps = JamesConstants.INVALID_STEP_COUNT;
        lastImprovementTime = JamesConstants.INVALID_TIMESTAMP;
        stepsSinceLastImprovement = JamesConstants.INVALID_STEP_COUNT;
        minDelta = JamesConstants.INVALID_DELTA;
        // initialize utility variables
        improvementDuringCurrentStep = false;
    }
    
    /***********************/
    /* CONTROLLING METHODS */
    /***********************/
    
    /**
     * <p>
     * Starts a search run and returns when this run has finished. The search run may either complete internally,
     * i.e. come to its natural end, or be terminated by a stop criterion (see {@link #addStopCriterion(StopCriterion)}).
     * This method does not return anything; the best solution found during search can be obtained by calling
     * {@link #getBestSolution()} and its corresponding evaluation with {@link #getBestSolutionEvaluation()}.
     * </p>
     * <p>
     * Note that a search can only be (re)started when it is idle (see {@link #getStatus()}). If attempted to start
     * a search which is already running (or terminating), an exception will be thrown.
     * </p>
     * <p>
     * Before the search is actually started, some initialization may take place. This initialization can also include
     * a verification of the search configuration and in case of an invalid configuration, an exception may be thrown.
     * </p>
     * 
     * @throws SearchException if the search is currently not idle, or if initialization fails because of an invalid
     *                         search configuration
     * @throws JamesRuntimeException in general, any {@link JamesRuntimeException} may be thrown
     *                               in case of a malfunctioning component used during initialization,
     *                               execution or finalization
     */
    public void start(){
        
        // acquire status lock
        synchronized(statusLock) {
            // verify that search is idle
            if(status != SearchStatus.IDLE){
                throw new SearchException("Error when trying to start search: search is not idle.");
            }
            // set status to INITIALIZING
            status = SearchStatus.INITIALIZING;
            // initialize per-run metadata (before releasing lock to
            // ensure consistent values after status transition)
            startTime = System.currentTimeMillis();
            stopTime = JamesConstants.INVALID_TIMESTAMP;
            currentSteps = 0;
            lastImprovementTime = JamesConstants.INVALID_TIMESTAMP;
            stepsSinceLastImprovement = JamesConstants.INVALID_STEP_COUNT;
            minDelta = JamesConstants.INVALID_DELTA;
        }
        
        // fire callback
        fireSearchStarted();
        
        // custom initialization and/or validation
        searchStarted();
        
        // instruct stop criterion checker to start checking
        stopCriterionChecker.startChecking();
        
        // initialization finished: update status
        synchronized(statusLock){
            status = SearchStatus.RUNNING;
        }
        
        // enter search loop
        while(continueSearch()){
            // reset improvement flag (automatically flipped by
            // updateBestSolution if improvement found during step)
            improvementDuringCurrentStep = false;
            // perform search step
            searchStep();
            // update step count
            currentSteps++;
            // update steps since last improvement
            if(improvementDuringCurrentStep){
                // improvement made
                stepsSinceLastImprovement = 0;
            } else if (stepsSinceLastImprovement != JamesConstants.INVALID_STEP_COUNT) {
                // no improvement made now, but found improvement before in current run
                stepsSinceLastImprovement++;
            }
            // fire callback
            fireStepCompleted(currentSteps);
        }
        
        // search was stopped: custom finalization
        searchStopped();
        
        // instruct stop criterion checker to stop checking
        stopCriterionChecker.stopChecking();
        
        // fire callback
        fireSearchStopped();
        
        // search run is complete: update status and set stop time
        synchronized(statusLock){
            status = SearchStatus.IDLE;
            stopTime = System.currentTimeMillis();
        }
        
    }
   
    /**
     * <p>
     * Requests the search to stop. May be called from outside the search, e.g. by a stop criterion, as well as internally,
     * when the search comes to its natural end. In the latter case, it is absolutely guaranteed that the step from which the search
     * was requested to stop will be the last step executed during the current run. If the current search status is not INITIALIZING
     * or RUNNING, calling this method has no effect. Else, it changes the search status to TERMINATING.
     * </p>
     * <p>
     * In case the search is already requested to terminate during initialization, it will complete initialization, but is guaranteed
     * to stop before executing any search steps.
     * </p>
     */
    public void stop(){
        // acquire status lock
        synchronized(statusLock){
            // check current status
            if(status == SearchStatus.INITIALIZING || status == SearchStatus.RUNNING){
                status = SearchStatus.TERMINATING;
            }
        }
    }
    
    /*********************************************************/
    /* METHODS FOR ADDING STOP CRITERIA AND SEARCH LISTENERS */
    /*********************************************************/
    
    /**
     * Adds a stop criterion used to decide when the search should stop running. It might be verified whether the given stop criterion
     * is compatible with the search and if not, an exception may be thrown. Note that this method can only be called when the search
     * is idle.
     * 
     * @param stopCriterion stop criterion used to decide when the search should stop running
     * @throws IncompatibleStopCriterionException when the given stop criterion is incompatible with the search
     * @throws SearchException if the search is not idle
     */
    public void addStopCriterion(StopCriterion stopCriterion){
        // acquire status lock
        synchronized(statusLock){
            // check status
            if(status != SearchStatus.IDLE){
                throw new SearchException("Cannot add stop criterion: search not idle.");
            }
            // check compatibility by performing a dummy call
            try {
                stopCriterion.searchShouldStop(this);
            } catch (IncompatibleStopCriterionException ex){
                // incompatible stop criterion: throw same exception to caller
                throw ex;
            }
            // pass stop criterion to checker
            stopCriterionChecker.add(stopCriterion);
        }
    }
    
    /**
     * Removes a stop criterion. In case this stop criterion had not been added, <code>false</code> is returned.
     * Note that this method may only be called when the search is idle.
     * 
     * @param stopCriterion stop criterion to be removed
     * @throws SearchException if the search is not idle
     * @return <code>true</code> if the stop criterion has been successfully removed
     */
    public boolean removeStopCriterion(StopCriterion stopCriterion){
        // acquire status lock
        synchronized(statusLock){
            // check status
            if(status != SearchStatus.IDLE){
                throw new SearchException("Cannot remove stop criterion: search not idle.");
            }
            // remove from checker
            return stopCriterionChecker.remove(stopCriterion);
        }
    }
    
    /**
     * Instructs the search to check its stop criteria at regular intervals separated by the given period.
     * For the default period, see {@link StopCriterionChecker}, which is used internally for this purpose.
     * Note that this method may only be called when the search is idle.
     * 
     * @param period time between subsequent stop criterion checks (> 0)
     * @param timeUnit corresponding time unit
     * @throws SearchException if the search is not idle
     * @throws IllegalArgumentException if the given period is not strictly positive
     */
    public void setStopCriterionCheckPeriod(long period, TimeUnit timeUnit){
        // acquire status lock
        synchronized(statusLock){
            // check status
            if(status != SearchStatus.IDLE){
                throw new SearchException("Cannot change stop criterion check period: search not idle.");
            }
            // pass new settings to checker
            stopCriterionChecker.setPeriod(period, timeUnit);
        }
    }
    
    /**
     * Add a search listener. Any search listener with a matching solution type (or a more general solution type)
     * may be added. Note that this method may only be called when the search is idle.
     * 
     * @param listener search listener to add to the search
     * @throws SearchException if the search is not idle
     */
    public void addSearchListener(SearchListener<? super SolutionType> listener){
        // acquire status lock
        synchronized(statusLock){
            // check status
            if(status != SearchStatus.IDLE){
                throw new SearchException("Cannot add search listener: search not idle.");
            }
            // add listener
            searchListeners.add(listener);
        }
    }
    
    /**
     * Remove the given search listener. If the search listener had not been added, <code>false</code> is returned.
     * Note that this method may only be called when the search is idle.
     * 
     * @param listener search listener to be removed
     * @throws SearchException if the search is not idle
     * @return <code>true</code> if the listener has been successfully removed
     */
    public boolean removeSearchListener(SearchListener<? super SolutionType> listener){
        // acquire status lock
        synchronized(statusLock){
            // check status
            if(status != SearchStatus.IDLE){
                throw new SearchException("Cannot remove search listener: search not idle.");
            }
            // remove listener
            return searchListeners.remove(listener);
        }
    }
    
    /********************************************************/
    /* PRIVATE METHODS FOR FIRING SEARCH LISTENER CALLBACKS */
    /********************************************************/

    /**
     * Calls {@link SearchListener#searchStarted(Search)} on every attached search listener.
     * Should only be executed when search is active (initializing, running or terminating).
     */
    private void fireSearchStarted(){
        for(SearchListener<? super SolutionType> listener : searchListeners){
            listener.searchStarted(this);
        }
    }
    
    /**
     * Calls {@link SearchListener#searchStopped(Search)} on every attached search listener.
     * Should only be executed when search is active (initializing, running or terminating).
     */
    private void fireSearchStopped(){
        for(SearchListener<? super SolutionType> listener : searchListeners){
            listener.searchStopped(this);
        }
    }
    
    /**
     * Calls {@link SearchListener#newBestSolution(Search, Solution, double)} on every attached search listener.
     * Should only be executed when search is active (initializing, running or terminating).
     * 
     * @param newBestSolution new best solution
     * @param newBestSolutionEvaluation evaluation of new best solution
     */
    private void fireNewBestSolution(SolutionType newBestSolution, double newBestSolutionEvaluation){
        for(SearchListener<? super SolutionType> listener : searchListeners){
            listener.newBestSolution(this, newBestSolution, newBestSolutionEvaluation);
        }
    }
    
    /**
     * Calls {@link SearchListener#stepCompleted(Search, long)} on every attached search listener.
     * Should only be executed when search is active (initializing, running or terminating).
     * 
     * @param numSteps number of steps completed so far (during the current run)
     */
    private void fireStepCompleted(long numSteps){
        for(SearchListener<? super SolutionType> listener : searchListeners){
            listener.stepCompleted(this, numSteps);
        }
    }
    
    /***********************************************/
    /* PROTECTED METHOD FOR FIRING SEARCH MESSAGES */
    /***********************************************/
    
    /**
     * Calls {@link SearchListener#searchMessage(Search, String)} on every attached search listener.
     * Should only be executed when search is active (initializing, running or terminating).
     * 
     * @param message message to be sent to the listener
     */
    protected void fireSearchMessage(String message){
        for(SearchListener<? super SolutionType> listener : searchListeners){
            listener.searchMessage(this, message);
        }
    }
    
    /*****************/
    /* SEARCH STATUS */
    /*****************/

    /**
     * Get the current search status. The status may either be IDLE, RUNNING or TERMINATING.
     * 
     * @return current search status
     */
    public SearchStatus getStatus(){
        // acquire status lock
        synchronized(statusLock){
            return status;
        }
    }
    
    /******************************************/
    /* STATE ACCESSORS (RETAINED ACROSS RUNS) */
    /******************************************/
    
    /**
     * Returns the best solution found so far. The best solution is <b>retained</b> across subsequent runs of the
     * same search. May return <code>null</code> if no solutions have been evaluated yet, for example when the search
     * has just been created.
     * 
     * @return best solution found so far, if already defined; <code>null</code> otherwise
     */
    public SolutionType getBestSolution(){
        // synchronize with best solution updates
        synchronized(bestSolutionLock){
            return bestSolution;
        }
    }
    
    /**
     * Get the evaluation of the best solution found so far. The best solution and its evaluation are <b>retained</b>
     * across subsequent runs of the same search. If the best solution is not yet defined, i.e. when {@link #getBestSolution()}
     * return <code>null</code>, the result of this method is undefined; in such case it may return any arbitrary value.
     * 
     * @return evaluation of best solution, if already defined; arbitrary value otherwise
     */
    public double getBestSolutionEvaluation(){
        // synchronize with best solution updates
        synchronized(bestSolutionLock){
            return bestSolutionEvaluation;
        }
    }
    
    /****************************/
    /* PROTECTED STATE MUTATORS */
    /****************************/
    
    /**
     * <p>
     * Checks whether a new best solution has been found and updates it accordingly.
     * The best solution is updated only if the new solution is <b>not</b> rejected
     * (see {@link Problem#rejectSolution(Solution)}) and
     * </p>
     * <ul>
     *  <li>no best solution had been set before, or</li>
     *  <li>the new solution has a better evaluation</li>
     * </ul>
     * <p>
     * If the new solution is rejected, or has a worse evaluation than the current best solution, this
     * method has no effect. Note that the best solution is <b>retained</b> across subsequent runs of
     * the same search.
     * </p>
     * 
     * @param newSolution newly constructed solution
     */
    protected void updateBestSolution(SolutionType newSolution){
        // check that (a) new solution is not rejected and (b) it improves over
        // the currently known best solution (or no best solution set before)
        double newSolutionEvaluation = problem.evaluate(newSolution);
        double delta = computeDelta(newSolutionEvaluation, getBestSolutionEvaluation());
        if(!problem.rejectSolution(newSolution) && (bestSolution == null || delta > 0)){
            // flag improvement
            improvementDuringCurrentStep = true;
            // store last improvement time
            lastImprovementTime = System.currentTimeMillis();
            // update minimum delta (only in case previous best solution was set)
            if(bestSolution != null){
                // update if first delta, or smaller than previous minimum
                if(minDelta == JamesConstants.INVALID_DELTA || delta < minDelta){
                    minDelta = delta;
                }
            }
            // update best solution:
            //  - create copy because new solution might be further modified in subsequent search steps
            //  - acquire lock to ensure consistent return values of getBestSolution() and getBestSolutionEvaluation()
            synchronized(bestSolutionLock){
                bestSolution = problem.copySolution(newSolution);
                bestSolutionEvaluation = newSolutionEvaluation;
            }
            // fire callback
            fireNewBestSolution(bestSolution, bestSolutionEvaluation);
        }
    }

    /*****************************************/
    /* METADATA APPLYING TO CURRENT RUN ONLY */
    /*****************************************/
    
    /**
     * Get the runtime of the <i>current</i> run, including initialization, in milliseconds. If the search is idle,
     * but has been run before, the total runtime of the last run is returned. Before the first search run, this method
     * returns {@link JamesConstants#INVALID_TIME_SPAN}. Else, the returned value is always positive.
     * 
     * @return runtime of the current (or last) run, in milliseconds
     */
    public long getRuntime(){
        // depends on status: synchronize with status updates
        synchronized(statusLock){
            if(status == SearchStatus.IDLE){
                // search is idle
                if(stopTime == JamesConstants.INVALID_TIMESTAMP){
                    // has not run before
                    return JamesConstants.INVALID_TIME_SPAN;
                } else {
                    // has run before, return runtime of last run
                    return stopTime - startTime;
                }
            } else {
                // search is active: return runtime of current run
                return System.currentTimeMillis() - startTime;
            }
        }
    }
    
    /**
     * Get the number of completed steps in the <i>current</i> run. If the search is idle, but has been run before,
     * the total number of steps executed during the last run is returned. Before the first search run, this method
     * returns {@link JamesConstants#INVALID_STEP_COUNT}. Else, the returned value is always positive.
     * 
     * @return number of completed steps in the current (or last) run
     */
    public long getSteps(){
        // acquire lock to ensure consistency with search status
        synchronized(statusLock){
            return currentSteps;
        }
    }
    
    /**
     * Get the amount of time elapsed during the <i>current</i> run without finding any improvement, in milliseconds.
     * If the search is active, but no improvements have yet been made during the current run, the returned value
     * is equal to the current runtime; else, it reflects the time elapsed since the last improvement during the current
     * run. If the search is idle, but has been run before, the time without improvement observed during the last run,
     * up to the point when this run completed, is returned. Before the first search run, this method returns
     * {@link JamesConstants#INVALID_TIME_SPAN}. Else, the returned value is always positive.
     * 
     * @return time without finding improvements during the current (or last) run, in milliseconds
     */
    public long getTimeWithoutImprovement(){
        // acquire lock to ensure consistency with search status
        synchronized(statusLock){
            if(lastImprovementTime == JamesConstants.INVALID_TIMESTAMP){
                // no improvement made during current/last run, or not yet run: equal to total runtime
                return getRuntime();
            } else {
                // improvement made during current/last run
                if(status == SearchStatus.IDLE){
                    // currently not running
                    return stopTime - lastImprovementTime;
                } else {
                    // running
                    return System.currentTimeMillis() - lastImprovementTime;
                }
            }
        }
    }
    
    /**
     * Get the number of consecutive steps completed during the <i>current</i> run without finding any improvement.
     * If the search is active, but no improvements have yet been made during the current run, the returned value
     * is equal to the current number of completed steps; else, it reflects the number of steps since the last
     * improvement was made during the current run. If the search is idle, but has been run before, the number of steps
     * without improvement observed during the last run, up to the point when this run completed, is returned. Before
     * the first search run, this method returns {@link JamesConstants#INVALID_STEP_COUNT}. Else, the returned value
     * is always positive.
     * 
     * @return number of consecutive completed steps without finding improvements during the current (or last) run
     */
    public long getStepsWithoutImprovement(){
        // acquire lock to ensure consistency with search status
        synchronized(statusLock){
            if(stepsSinceLastImprovement == JamesConstants.INVALID_STEP_COUNT){
                // no improvement made during current/last run, or not yet run: equal to total step count
                return getSteps();
            } else {
                // running or ran before, with improvement in current/last run
                return stepsSinceLastImprovement;
            }
        }
    }
    
    /**
     * Get the minimum improvement in evaluation of a newly found best solution over the previously known best solution,
     * during the <i>current</i> run. The minimum delta is always positive, it corresponds to increase when solving a maximization
     * problem, and decrease in case of a minimization problem. If the search is idle, but has been run before, the minimum delta
     * observed during the last run is returned. Before the first search run, and before the first improvement during an active run,
     * this method returns {@link JamesConstants#INVALID_DELTA}. Else, the returned value is always positive.
     * 
     * @return minimum improvement of newly found best solution over previously known best solution, during current (or last) run
     */
    public double getMinDelta(){
        // acquire lock to ensure consistency with search status
        synchronized(statusLock){
            return minDelta;
        }
    }
    
    /*********************/
    /* PRIVATE UTILITIES */
    /*********************/
    
    /**
     * Indicates whether the search should continue, by verifying whether its status is still set to {@link SearchStatus#RUNNING}.
     * Once the search has been started, this method will return <code>true</code> as long as {@link #stop()} has not been called.
     * During that time, {@link #searchStep()} will be repeatedly called from a loop that uses {@link #continueSearch()} as its
     * stop condition.
     * 
     * @return <code>true</code> if the search status is {@link SearchStatus#RUNNING}
     */
    private boolean continueSearch(){
        return status == SearchStatus.RUNNING;
    }
    
    /***********************/
    /* PROTECTED UTILITIES */
    /***********************/
    
    /**
     * Computes the amount of improvement of <code>currentEvaluation</code> over <code>previousEvaluation</code>, taking into
     * account whether a maximization or minimization problem is being solved, where positive deltas indicate improvement.
     * In case of a maximization problem, the amount of increase is returned, which is equal to
     *  <pre> currentEvaluation - previousEvaluation </pre>
     * while the amount of decrease, equal to
     *  <pre> previousEvaluation - currentEvaluation </pre>
     * is returned when solving a minimization problem.
     * 
     * @param currentEvaluation evaluation to be compared with previous evaluation
     * @param previousEvaluation previous evaluation
     * @return amount of improvement of current evaluation over previous evaluation
     */
    protected double computeDelta(double currentEvaluation, double previousEvaluation){
        if(problem.isMinimizing()){
            // minimization problem: return decrease
            return previousEvaluation - currentEvaluation;
        } else {
            // maximization problem: return increase
            return currentEvaluation - previousEvaluation;
        }
    }
    
    /**
     * Get the problem being solved, as specified at construction.
     * 
     * @return problem being solved
     */
    protected Problem<SolutionType> getProblem(){
        return problem;
    }
    
    /***********************************************************************/
    /* PROTECTED METHODS FOR INITIALIZATION AND FINILIZATION OF SEARCH RUN */
    /***********************************************************************/
    
    /**
     * This method is called when a search run is started, to perform initialization and/or validation of the search
     * configuration. It may throw a {@link SearchException} if initialization fails because the search has not been
     * configured validly. Moreover, any {@link JamesRuntimeException} could be thrown when initialization depends on
     * malfunctioning components. By default, this method has an empty implementation.
     * 
     * @throws SearchException if initialization fails, e.g. because the search has not been configured validly
     * @throws JamesRuntimeException in general, any {@link JamesRuntimeException} may be thrown
     *                               in case of a malfunctioning component used during initialization
     */
    protected void searchStarted() {}
    
    /**
     * This method is called when a search run has completed and may be used to perform some finalization. Any
     * {@link JamesRuntimeException} may be thrown when finalization depends on malfunctioning search components.
     * By default, this method has an empty implementation.
     * 
     * @throws JamesRuntimeException in general, any {@link JamesRuntimeException} may be thrown
     *                               in case of a malfunctioning component used during finalization
     */
    protected void searchStopped() {}
    
    /************************************************************************/
    /* ABSTRACT PROTECTED METHOD TO BE IMPLEMENTED IN EVERY SPECIFIC SEARCH */
    /************************************************************************/

    /**
     * This method is iteratively called while the search is running and should be implemented in every specific
     * search according to the corresponding search strategy. When a search comes to its natural end, it should call
     * {@link #stop()} from within this method, which will cause the search loop to terminate and prevent further
     * steps to be executed. Searches consisting of a single step may simply implement their strategy here and
     * immediately call {@link #stop()} at the end of the execution of the step, so that it will be executed
     * exactly once.
     * 
     * @throws JamesRuntimeException any {@link JamesRuntimeException} may be thrown during a search step, when
     *                               the algorithm has been supplied with a malfunctioning component
     */
    abstract protected void searchStep();
    
}
