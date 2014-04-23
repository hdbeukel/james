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
import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.TimeUnit;
import org.jamesframework.core.exceptions.IncompatibleStopCriterionException;
import org.jamesframework.core.exceptions.JamesRuntimeException;
import org.jamesframework.core.exceptions.SearchException;
import org.jamesframework.core.problems.Problem;
import org.jamesframework.core.problems.solutions.Solution;
import org.jamesframework.core.search.stopcriteria.StopCriterionChecker;
import org.jamesframework.core.util.JamesConstants;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * <p>
 * General abstract search used to solve a problem with the specified solution type. It provides general methods to
 * start and stop the search, and to access state information and metadata such as the best solution found so far and the
 * runtime of the current run. It also provides methods to add and remove search listeners and stop criteria.
 * </p>
 * <p>
 * A search can have five possible statuses: IDLE, INITIALIZING, RUNNING, TERMINATING or DISPOSED (see {@link SearchStatus}).
 * When a search is created, it is IDLE. Upon calling {@link #start()} it first goes to INITIALIZING and then RUNNING, after
 * successful initialization. While the search is running, it iteratively calls {@link #searchStep()} to perform a search step
 * as defined in each specific search implementation.
 * </p>
 * <p>
 * Whenever a search is requested to stop, by calling {@link #stop()}, it goes to status TERMINATING. A terminating
 * search will stop after it has completed its current step, and then its status goes back to status IDLE. A search
 * may also terminate itself by calling {@link #stop()} internally, when it has come to its natural end. In particular,
 * a single step algorithm can be implemented by calling {@link #stop()} immediately at the end of this first and
 * only step, which guarantees that only one single step will be executed.
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
 * <p>
 * An idle search can also be disposed (see {@link #dispose()}), upon which it will release all of its resources. A disposed
 * search can never be restarted. Note that it is important to always dispose a search after its last run so that it does not
 * hold on to any of its resources. Not disposing a search may prevent termination of the application.
 * </p>
 * 
 * @param <SolutionType> solution type of the problems that may be solved using this search, required to extend {@link Solution}
 * @author <a href="mailto:herman.debeukelaer@ugent.be">Herman De Beukelaer</a>
 */
public abstract class Search<SolutionType extends Solution> implements Runnable {
    
    /*********************/
    /* UNIQUE ID COUNTER */
    /*********************/

    private static int nextID = 0;
    
    /**********/
    /* LOGGER */
    /**********/
    
    private static final Logger logger = LoggerFactory.getLogger(Search.class);

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
    
    // search name & ID
    private final String name;
    private final int id;
    
    // problem being solved
    private final Problem<SolutionType> problem;
    
    // set of search listeners attached to this search
    private final Set<SearchListener<? super SolutionType>> searchListeners;
    
    // stop criterion checker dedicated to checking the stop criteria attached to this search
    private final StopCriterionChecker stopCriterionChecker;
    
    /*********/
    /* LOCKS */
    /*********/
    
    // lock acquired when updating the search status and when executing a block of code during which
    // the status is not allowed to change
    private final Object statusLock = new Object();
    
    /****************/
    /* CONSTRUCTORS */
    /****************/
    
    /**
     * Creates a search to solve the given problem, with default search name "Search".
     * 
     * @throws NullPointerException if <code>problem</code> is <code>null</code>
     * @param problem problem to solve
     */
    public Search(Problem<SolutionType> problem){
        this(null, problem);
    }
    
    /**
     * Creates a search to solve the given problem, with a custom search name. If
     * <code>name</code> is <code>null</code>, a default search name "Search" will
     * be assigned.
     * 
     * @throws NullPointerException if <code>problem</code> is <code>null</code>
     * @param problem problem to solve
     * @param name custom search name
     */
    @SuppressWarnings("LeakingThisInConstructor")
    public Search(String name, Problem<SolutionType> problem){
        // check problem
        if(problem == null){
            throw new NullPointerException("Error while creating search: problem can not be null.");
        }
        // store problem reference
        this.problem = problem;
        // store name
        if(name != null){
            this.name = name;
        } else {
            // no name given: default to "Search"
            this.name = "Search";
        }
        // assign next unique id
        id = getNextUniqueID();
        // initialize search listener set
        searchListeners = new HashSet<>();
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
        // log search creation
        logger.info("Created search {}", this);
    }
    
    /**
     * Get the next unique ID to be assigned to this search. Synchronized to ensure
     * uniqueness of IDs in multi threaded environments.
     * 
     * @return next unique ID
     */
    private synchronized int getNextUniqueID(){
        return nextID++;
    }
    
    /**
     * Get the problem being solved, as specified at construction.
     * 
     * @return problem being solved
     */
    public Problem<SolutionType> getProblem(){
        return problem;
    }
    
    /***************************/
    /* NAME, ID & STRING VALUE */
    /***************************/
    
    /**
     * Get the name that has been assigned to this search.
     * 
     * @return search name
     */
    public String getName(){
        return name;
    }
    
    /**
     * Get the unique ID that has been assigned to this search.
     * 
     * @return unique search ID
     */
    public int getID(){
        return id;
    }
    
    /**
     * Returns a string representation of the search, formatted as "%name(%id)".
     * 
     * @return string representation containing name and id
     */
    @Override
    public String toString(){
        return name + "(" + id + ")";
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
        
        logger.trace("Search {} started", this);
        
        // acquire status lock
        synchronized(statusLock) {
            // verify that search is idle
            assertIdle("Cannot start search.");
            // log
            logger.trace("Search {} changed status: {} --> {}", this, status, SearchStatus.INITIALIZING);
            // set status to INITIALIZING
            status = SearchStatus.INITIALIZING;
            // fire status update
            fireStatusChanged(status);
        }
        
        // fire callback
        fireSearchStarted();
        
        // initialization and/or validation
        searchStarted();
        
        // check if search should be continued (may already
        // have been stopped during initialization)
        if(continueSearch()){

            // instruct stop criterion checker to start checking
            stopCriterionChecker.startChecking();

            // initialization finished: update status
            synchronized(statusLock){
                // log
                logger.trace("Search {} changed status: {} --> {}", this, status, SearchStatus.RUNNING);
                // update
                status = SearchStatus.RUNNING;
                // fire status update
                fireStatusChanged(status);
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
        
            // instruct stop criterion checker to stop checking
            stopCriterionChecker.stopChecking();
            
        }
        
        // finalization
        searchStopped();
                
        // fire callback
        fireSearchStopped();

        logger.trace("Search {} stopped (runtime: {} ms)", this, getRuntime());
        
        // search run is complete: update status
        synchronized(statusLock){
            // log
            logger.trace("Search {} changed status: {} --> {}", this, status, SearchStatus.IDLE);
            // update
            status = SearchStatus.IDLE;
            // fire status update
            fireStatusChanged(status);
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
                // log
                logger.trace("Search {} changed status: {} --> {}", this, status, SearchStatus.TERMINATING);
                // update status
                status = SearchStatus.TERMINATING;
                // fire status update
                fireStatusChanged(status);
            }
        }
    }
    
    /**
     * Dispose this search, upon which all of its resources are released. Note that only idle
     * searches may be disposed and that a disposed search can never be restarted. Sets the search
     * status to DISPOSED.
     * 
     * @throws SearchException if the search is currently not idle
     */
    public void dispose(){
        // acquire status lock
        synchronized(statusLock){
            // assert idle
            assertIdle("Cannot dispose search.");
            // all good, handle disposed
            searchDisposed();
            // log
            logger.trace("Search {} changed status: {} --> {}", this, status, SearchStatus.DISPOSED);
            // update status
            status = SearchStatus.DISPOSED;
            // fire status update
            fireStatusChanged(status);
        }
    }
    
    /***************************/
    /* RUNNABLE IMPLEMENTATION */
    /***************************/
    
    /**
     * Equivalent to calling {@link #start()}. Through this method searches implement the {@link Runnable} interface
     * so that they can easily be executed in a separate thread.
     */
    @Override
    public void run(){
        start();
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
            // assert idle
            assertIdle("Cannot add stop criterion.");
            // check compatibility by performing a dummy call
            try {
                stopCriterion.searchShouldStop(this);
            } catch (IncompatibleStopCriterionException ex){
                // incompatible stop criterion: throw same exception to caller
                throw ex;
            }
            // pass stop criterion to checker
            stopCriterionChecker.add(stopCriterion);
            // log
            logger.info("{}: added stop criterion {}", this, stopCriterion);
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
            // assert idle
            assertIdle("Cannot remove stop criterion.");
            // remove from checker
            if (stopCriterionChecker.remove(stopCriterion)){
                // log
                logger.info("{}: removed stop criterion {}", this, stopCriterion);
                return true;
            } else {
                return false;
            }
        }
    }
    
    /**
     * Instructs the search to check its stop criteria at regular intervals separated by the given period.
     * For the default period, see {@link StopCriterionChecker}, which is used internally for this purpose.
     * The period should be at least 1 millisecond, else the stop criterion checker may thrown an exception
     * when the search is started. Note that this method may only be called when the search is idle.
     * 
     * @param period time between subsequent stop criterion checks (&gt; 0)
     * @param timeUnit corresponding time unit
     * @throws SearchException if the search is not idle
     * @throws IllegalArgumentException if the given period is not strictly positive
     */
    public void setStopCriterionCheckPeriod(long period, TimeUnit timeUnit){
        // acquire status lock
        synchronized(statusLock){
            // assert idle
            assertIdle("Cannot modify stop criterion check period.");
            // pass new settings to checker
            stopCriterionChecker.setPeriod(period, timeUnit);
            // log
            logger.info("{}: set stop criterion check period to {} ms", this, timeUnit.toMillis(period));
        }
    }
    
    /**
     * Add a search listener, if it has not been added before. Any search listener
     * with a matching solution type (or a more general solution type) may be added.
     * Note that this method can only be called when the search is idle.
     * 
     * @param listener search listener to add to the search
     * @throws SearchException if the search is not idle
     * @return <code>true</code> if the search listener had not been added before
     */
    public boolean addSearchListener(SearchListener<? super SolutionType> listener){
        // acquire status lock
        synchronized(statusLock){
            // assert idle
            assertIdle("Cannot add search listener.");
            // add listener
            if(searchListeners.add(listener)){
                // log
                logger.info("{}: added search listener {}", this, listener);
                return true;
            } else {
                return false;
            }
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
            // assert idle
            assertIdle("Cannot remove search listener.");
            // remove listener
            if (searchListeners.remove(listener)){
                // log
                logger.info("{}: removed search listener {}", this, listener);
                return true;
            } else {
                return false;
            }
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
    
    /**
     * Calls {@link SearchListener#statusChanged(Search, SearchStatus)} on every attached search listener.
     * Should only be called exactly once for every status update.
     * 
     * @param newStatus new search status
     */
    private void fireStatusChanged(SearchStatus newStatus){
        for(SearchListener<? super SolutionType> listener : searchListeners){
            listener.statusChanged(this, newStatus);
        }
    }
    
    /*****************/
    /* SEARCH STATUS */
    /*****************/

    /**
     * Get the current search status. The status may be IDLE, INITIALIZING, RUNNING, TERMINATING or DISPOSED.
     * 
     * @return current search status
     */
    public SearchStatus getStatus(){
        // synchronize with status updates
        synchronized(statusLock){
            return status;
        }
    }
    
    /**
     * Returns a lock to be acquired when executing blocks of code that can not be interrupted by a status update.
     * All status updates are synchronized using this lock. Whenever a fixed status is required during execution of
     * a code block, this can be obtained by synchronizing the block using this status lock.
     * 
     * @return status lock used for synchronization with status updates
     */
    protected Object getStatusLock(){
        return statusLock;
    }
    
    /**
     * Asserts that the search is currently idle, more precisely that its status is equal to {@link SearchStatus#IDLE}.
     * If not, a {@link SearchException} is thrown, which includes the given <code>errorMessage</code> and the current
     * search status (different from IDLE).
     * 
     * @throws SearchException if the search is not idle
     * @param errorMessage message to be included in the {@link SearchException} thrown if the search is not idle
     */
    protected void assertIdle(String errorMessage){
        // synchronize with status updates
        synchronized(statusLock){
            if(status != SearchStatus.IDLE){
                // not idle, throw exception
                throw new SearchException(errorMessage + " (current status: " + status  + "; required: IDLE)");
            }
        }
    }
    
    /******************************************/
    /* STATE ACCESSORS (RETAINED ACROSS RUNS) */
    /******************************************/
    
    /**
     * Returns the best solution found so far. It is guaranteed that this solution is valid, in the sense that
     * {@link Problem#rejectSolution(Solution)} returns <code>false</code>. The best solution is <b>retained</b>
     * across subsequent runs of the same search. May return <code>null</code> if no solutions have been evaluated
     * yet, for example when the search has just been created.
     * 
     * @return best solution found so far, if already defined; <code>null</code> otherwise
     */
    public SolutionType getBestSolution(){
        return bestSolution;
    }
    
    /**
     * Get the evaluation of the best solution found so far. The best solution and its evaluation are <b>retained</b>
     * across subsequent runs of the same search. If the best solution is not yet defined, i.e. when {@link #getBestSolution()}
     * return <code>null</code>, the result of this method is undefined; in such case it may return any arbitrary value.
     * 
     * @return evaluation of best solution, if already defined; arbitrary value otherwise
     */
    public double getBestSolutionEvaluation(){
        return bestSolutionEvaluation;
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
     * @return <code>true</code> if the given solution is accepted as the new best solution
     */
    protected boolean updateBestSolution(SolutionType newSolution){
        // check if new solution is not rejected
        if(!problem.rejectSolution(newSolution)){
            // evaluate solution
            double eval = problem.evaluate(newSolution);
            // update, if better
            return updateBestSolution(newSolution, eval);
        }
        // solution is rejected
        return false;
    }
    
    /**
     * <p>
     * Checks whether a new best solution has been found and updates it accordingly,
     * assuming that the given solution is <b>not</b> rejected by the problem (see
     * {@link Problem#rejectSolution(Solution)} and has already been evaluated.
     * This method should only be called for solutions for which it has already been
     * verified that they are not rejected, as this will <b>not</b> be checked here.
     * Else, {@link #updateBestSolution(Solution)} should be used. This alternative
     * method is specifically introduced to avoid unnecessary re-evaluation and
     * re-validation of already evaluated, valid solutions.
     * </p>
     * <p>
     * The best solution is updated if and only if
     * </p>
     * <ul>
     *  <li>no best solution had been set before, or</li>
     *  <li>the new solution has a better evaluation</li>
     * </ul>
     * <p>
     * If the new solution has a worse evaluation than the current best solution, calling this
     * method has no effect. Note that the best solution is <b>retained</b> across subsequent
     * runs of the same search.
     * </p>
     * 
     * @param newSolution newly constructed solution, which is known <b>not</b> to be rejected
     * @param newSolutionEvaluation already computed evaluation of the given solution
     * @return <code>true</code> if the given solution is accepted as the new best solution
     */
    protected boolean updateBestSolution(SolutionType newSolution, double newSolutionEvaluation){
        // check if new solution has better evaluation, or no best solution set
        double delta = computeDelta(newSolutionEvaluation, getBestSolutionEvaluation());
        if(bestSolution == null || delta > 0){
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
            // update best solution: create copy because new solution
            // might be further modified in subsequent search steps
            bestSolution = problem.copySolution(newSolution);
            bestSolutionEvaluation = newSolutionEvaluation;
            // log
            logger.debug("{}: new best solution: {}", this, bestSolutionEvaluation);
            // fire callback
            fireNewBestSolution(bestSolution, bestSolutionEvaluation);
            // found improvement
            return true;
        }
        // no improvement
        return false;
    }

    /*****************************************/
    /* METADATA APPLYING TO CURRENT RUN ONLY */
    /*****************************************/
    
    /**
     * <p>
     * Get the runtime of the <i>current</i> (or last) run, in milliseconds. The precise return value
     * depends on the status of the search:
     * </p>
     * <ul>
     *  <li>
     *   If the search is either RUNNING or TERMINATING, this method returns the time elapsed since
     *   the current run was started.
     *  </li>
     *  <li>
     *   If the search is IDLE or DISPOSED, the total runtime of the last run is returned, if any. Before
     *   the first run, {@link JamesConstants#INVALID_TIME_SPAN} is returned.
     *  </li>
     *  <li>
     *   While INITIALIZING the current run, {@link JamesConstants#INVALID_TIME_SPAN} is returned.
     *  </li>
     * </ul>
     * <p>
     * The return value is always positive, except in those cases when {@link JamesConstants#INVALID_TIME_SPAN} is returned.
     * </p>
     * 
     * @return runtime of the current (or last) run, in milliseconds
     */
    public long getRuntime(){
        // depends on status: synchronize with status updates
        synchronized(statusLock){
            if(status == SearchStatus.INITIALIZING){
                // initializing
                return JamesConstants.INVALID_TIME_SPAN;
            } else if (status == SearchStatus.IDLE || status == SearchStatus.DISPOSED){
                // idle or disposed: check if ran before
                if(stopTime == JamesConstants.INVALID_TIMESTAMP){
                    // did not run before
                    return JamesConstants.INVALID_TIME_SPAN;
                } else {
                    // return total runtime of last run
                    return stopTime - startTime;
                }
            } else {
                // running or terminating
                return System.currentTimeMillis() - startTime;
            }
        }
    }
    
    /**
     * <p>
     * Get the number of completed steps during the <i>current</i> (or last) run. The precise return value
     * depends on the status of the search:
     * </p>
     * <ul>
     *  <li>
     *   If the search is either RUNNING or TERMINATING, this method returns the number of steps completed
     *   since the current run was started.
     *  </li>
     *  <li>
     *   If the search is IDLE or DISPOSED, the total number of steps completed during the last run is returned,
     *   if any. Before the first run, {@link JamesConstants#INVALID_STEP_COUNT}.
     *  </li>
     *  <li>
     *   While INITIALIZING the current run, {@link JamesConstants#INVALID_STEP_COUNT} is returned.
     *  </li>
     * </ul>
     * <p>
     * The return value is always positive, except in those cases when {@link JamesConstants#INVALID_STEP_COUNT}
     * is returned.
     * </p>
     * 
     * @return number of steps completed during the current (or last) run
     */
    public long getSteps(){
        // depends on status: synchronize with status updates
        synchronized(statusLock){
            if(status == SearchStatus.INITIALIZING){
                // initializing
                return JamesConstants.INVALID_STEP_COUNT;
            } else {
                // idle, running, terminating or disposed
                return currentSteps;
            }
        }
    }
    
    /**
     * <p>
     * Get the amount of time elapsed during the <i>current</i> (or last) run, without finding any further improvement
     * (in milliseconds). The precise return value depends on the status of the search:
     * </p>
     * <ul>
     *  <li>
     *   If the search is either RUNNING or TERMINATING, but no improvements have yet been made during the current
     *   run, the returned value is equal to the current runtime; else it reflects the time elapsed since the last
     *   improvement during the current run.
     *  </li>
     *  <li>
     *   If the search is IDLE or DISPOSED, but has been run before, the time without improvement observed during the
     *   last run, up to the point when this run completed, is returned. Before the first run, the return value is
     *   {@link JamesConstants#INVALID_TIME_SPAN}.
     *  </li>
     *  <li>
     *   While INITIALIZING the current run, {@link JamesConstants#INVALID_TIME_SPAN} is returned.
     *  </li>
     * </ul>
     * <p>
     * The return value is always positive, except in those cases when {@link JamesConstants#INVALID_TIME_SPAN}
     * is returned.
     * </p>
     * 
     * @return time without finding improvements during the current (or last) run, in milliseconds
     */
    public long getTimeWithoutImprovement(){
        // depends on status: synchronize with status updates
        synchronized(statusLock){
            if(status == SearchStatus.INITIALIZING){
                // initializing
                return JamesConstants.INVALID_TIME_SPAN;
            } else {
                // idle, running, terminating or disposed: check last improvement time
                if(lastImprovementTime == JamesConstants.INVALID_TIMESTAMP){
                    // no improvement made during current/last run, or did not yet run: equal to total runtime
                    return getRuntime();
                } else {
                    // running or ran before, and improvement made during current/last run
                    if(status == SearchStatus.IDLE || status == SearchStatus.DISPOSED){
                        // idle or disposed: return last time without improvement of previous run
                        return stopTime - lastImprovementTime;
                    } else {
                        // running or terminating: return time elapsed since last improvement
                        return System.currentTimeMillis() - lastImprovementTime;
                    }
                }
            }
        }
    }
    
    /**
     * <p>
     * Get the number of consecutive steps completed during the <i>current</i> (or last) run, without finding
     * any further improvement. The precise return value depends on the status of the search:
     * </p>
     * <ul>
     *  <li>
     *   If the search is either RUNNING or TERMINATING, but no improvements have yet been made during the current
     *   run, the returned value is equal to the total number of steps completed so far; else it reflects the number
     *   of steps completed since the last improvement during the current run.
     *  </li>
     *  <li>
     *   If the search is IDLE or DISPOSED, but has been run before, the number of steps without improvement observed
     *   during the last run, up to the point when this run completed, is returned. Before the first run, the return
     *   value is {@link JamesConstants#INVALID_STEP_COUNT}.
     *  </li>
     *  <li>
     *   While INITIALIZING the current run, {@link JamesConstants#INVALID_STEP_COUNT} is returned.
     *  </li>
     * </ul>
     * <p>
     * The return value is always positive, except in those cases when {@link JamesConstants#INVALID_STEP_COUNT}
     * is returned.
     * </p>
     * 
     * @return number of consecutive completed steps without finding improvements during the current (or last) run
     */
    public long getStepsWithoutImprovement(){
        // depends on status: synchronize with status updates
        synchronized(statusLock){
            if(status == SearchStatus.INITIALIZING){
                // initializing
                return JamesConstants.INVALID_STEP_COUNT;
            } else {
                if(stepsSinceLastImprovement == JamesConstants.INVALID_STEP_COUNT){
                    // no improvement made during current/last run, or not yet run: equal to total step count
                    return getSteps();
                } else {
                    // running or ran before, and improvement made during current/last run
                    return stepsSinceLastImprovement;
                }
            }
        }
    }
    
    /**
     * <p>
     * Get the minimum improvement in evaluation of a new best known solution over the previous best known solution,
     * found during the <i>current</i> (or last) run. The precise return value depends on the status of the search:
     * </p>
     * <ul>
     *  <li>
     *   If the search is either RUNNING or TERMINATING, but no improvements have yet been made during the current
     *   run, {@link JamesConstants#INVALID_DELTA} is returned. Else, the minimum observed delta over all improvements
     *   made during the current run is returned.
     *  </li>
     *  <li>
     *   If the search is IDLE or DISPOSED, but has been run before, the minimum delta observed during the last run is returned.
     *   Before the first run, the return value is {@link JamesConstants#INVALID_DELTA}.
     *  </li>
     *  <li>
     *   While INITIALIZING the current run, {@link JamesConstants#INVALID_DELTA} is returned.
     *  </li>
     * </ul>
     * <p>
     * The return value is always positive, except in those cases when {@link JamesConstants#INVALID_DELTA} is returned.
     * It corresponds to increase when solving a maximization problem, and decrease in case of a minimization problem.
     * </p>
     * 
     * @return minimum delta of improvements observed during current (or last) run
     */
    public double getMinDelta(){
        // depends on status: synchronize with status updates
        synchronized(statusLock){
            if(status == SearchStatus.INITIALIZING){
                // initializing
                return JamesConstants.INVALID_DELTA;
            } else {
                // idle, running or terminating
                return minDelta;
            }
        }
    }
    
    /*********************/
    /* PRIVATE UTILITIES */
    /*********************/
    
    /**
     * Indicates whether the search should continue, by verifying whether its status is not set to {@link SearchStatus#TERMINATING}.
     * Once the search has been started, this method will return <code>true</code> as long as {@link #stop()} has not been called.
     * During that time, {@link #searchStep()} will be repeatedly called from a loop that uses {@link #continueSearch()} as its
     * stop condition.
     * 
     * @return <code>true</code> if the search status is not {@link SearchStatus#TERMINATING}
     */
    private boolean continueSearch(){
        return status != SearchStatus.TERMINATING;
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
        
    /*********************************************************************/
    /* PROTECTED METHODS TO INITIALIZE, FINALIZE AND DISPOSE THE SEARCH  */
    /*********************************************************************/
    
    /**
     * This method is called when a search run is started, to perform initialization and/or validation of the search
     * configuration. It may throw a {@link SearchException} if initialization fails because the search has not been
     * configured validly. Moreover, any {@link JamesRuntimeException} could be thrown when initialization depends on
     * malfunctioning components. The default implementation resets all general per run metadata. Therefore, it is of
     * <b>utmost</b> importance to call <code>super.searchStart()</code> in any overriding implementation.
     * 
     * @throws SearchException if initialization fails, e.g. because the search has not been configured validly
     * @throws JamesRuntimeException in general, any {@link JamesRuntimeException} may be thrown
     *                               in case of a malfunctioning component used during initialization
     */
    protected void searchStarted() {
        startTime = System.currentTimeMillis();
        stopTime = JamesConstants.INVALID_TIMESTAMP;
        currentSteps = 0;
        lastImprovementTime = JamesConstants.INVALID_TIMESTAMP;
        stepsSinceLastImprovement = JamesConstants.INVALID_STEP_COUNT;
        minDelta = JamesConstants.INVALID_DELTA;
    }
    
    /**
     * This method is called when a search run has completed and may be used to perform some finalization. Any
     * {@link JamesRuntimeException} may be thrown when finalization depends on malfunctioning search components.
     * The default implementation ensures that the total runtime of the last run, if applicable, will be returned
     * when calling {@link #getRuntime()} on an idle search. Therefore, it is of <b>utmost</b> importance to call
     * <code>super.searchStopped()</code> in any overriding implementation. 
     * 
     * @throws JamesRuntimeException in general, any {@link JamesRuntimeException} may be thrown
     *                               in case of a malfunctioning component used during finalization
     */
    protected void searchStopped() {
        stopTime = System.currentTimeMillis();
    }
    
    /**
     * This method is called when a search is disposed, immediately before the search status is updated to DISPOSED.
     * The default implementation is empty but should be overridden when a specific search uses resources that have to
     * be released (e.g. an active thread pool) when the search is no longer used. Any {@link JamesRuntimeException} may
     * be thrown when trying to release malfunctioning resources.
     * 
     * @throws JamesRuntimeException in general, any {@link JamesRuntimeException} may be thrown
     *                               when trying to release malfunctioning resources
     */
    protected void searchDisposed(){}
    
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
