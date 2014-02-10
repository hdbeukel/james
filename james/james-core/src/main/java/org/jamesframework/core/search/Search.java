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

import java.util.concurrent.TimeUnit;
import org.jamesframework.core.exceptions.IncompatibleStopCriterionException;
import org.jamesframework.core.exceptions.SearchException;
import org.jamesframework.core.problems.solutions.Solution;

/**
 * <p>
 * Interface of a general search used to solve a problem with the specified solution type. It provides general methods to
 * start and stop the search, and to access state information and metadata such as the best solution found so far and the
 * runtime of the current run. It also specifies methods to add search listeners and stop criteria.
 * </p>
 * <p>
 * A search can have three possible statuses: IDLE, RUNNING or TERMINATING (see {@link SearchStatus}). When a search is
 * created, it is IDLE. Upon calling <code>start()</code> it goes to status RUNNING, after successful initialization.
 * Whenever a search is requested to stop, by calling <code>stop()</code>, it goes to status TERMINATING. The search
 * is then expected to stop a soon as possible, after which it goes back to status IDLE. The decision of when to stop
 * after being requested to terminate is entirely up to the search, as it may need some time before being able to finish
 * gracefully. For example, a neighbourhood search may want to finish its current step first.
 * </p>
 * <p>
 * An idle search may be restarted at any time. The search state is retained across subsequent runs, including the best
 * solution found so far and any search specific state elements, unless explicitely stated otherwise. On the other hand,
 * the runtime applies to the current run only, which might also be the case for other metadata; this should be clearly
 * indicated in the documentation of specific searches. Stop criteria relying on such metadata will therefore operate
 * on a per-run basis.
 * </p>
 * 
 * @param <SolutionType> solution type of the problems that may be solved using this search, required to extend {@link Solution}
 * @author Herman De Beukelaer <herman.debeukelaer@ugent.be>
 */
public interface Search<SolutionType extends Solution> {
    
    /**
     * <p>
     * Starts a search run and returns when this run has finished. The search run may either complete internally,
     * i.e. come to its natural end, or be terminated by a stop criterion (see {@link #addStopCriterion(StopCriterion)}).
     * This method does not return anything; the best solution found during search can be obtained by calling
     * {@link #getBestSolution()}, and its corresponding evaluation with {@link #getBestSolutionEvaluation()}.
     * </p>
     * <p>
     * Note that a search can only be (re)started when it is idle (see {@link #getStatus()}). If attempted to start
     * a search which is already running (or terminating), an exception should be thrown.
     * </p>
     * <p>
     * Before the search is actually started, some initialization may take place. For example, a neighbourhood search
     * may first sample a random initial solution. This initialization can also include a verification of the search
     * configuration and in case of an invalid configuration, an exception may be thrown.
     * </p>
     * 
     * @throws SearchException if the search is currently not idle, or if a fatal error occurs, either
     *                         during initialization or during search
     */
    public void start();
   
    /**
     * Requests the search to stop. If the current search status is different from RUNNING, calling <code>stop()</code> does not have
     * any effect. Else, the search is requested to terminate its current run as soon as possible, but the decision of when to do so
     * remains a responsibility of the search itself.
     */
    public void stop();
    
    /**
     * Adds a stop criterion used to decide when the search should stop running. It might be verified whether the given stop criterion
     * is compatible with the search and if not, an exception may be thrown. Note that this method may only be called when the search
     * is idle.
     * 
     * @param stopCriterion stop criterion used to decide when the search should stop running
     * @throws IncompatibleStopCriterionException when the given stop criterion is incompatible with the search
     * @throws SearchException if the search is not idle
     */
    public void addStopCriterion(StopCriterion stopCriterion);
    
    /**
     * Removes a stop criterion. In case this stop criterion had not been added, <code>false</code> is returned.
     * Note that this method may only be called when the search is idle.
     * 
     * @param stopCriterion stop criterion to be removed
     * @throws SearchException if the search is not idle
     * @return <code>true</code> if the stop criterion has been successfully removed
     */
    public boolean removeStopCriterion(StopCriterion stopCriterion);
    
    /**
     * Instructs the search to check its stop criteria at regular intervals separated by the given period.
     * 
     * @param period time between subsequent stop criterion checks
     * @param timeUnit corresponding time unit
     */
    public void setStopCriterionCheckPeriod(long period, TimeUnit timeUnit);
    
    /**
     * Add a search listener. Any search listener with the same solution type as the search (or a more general solution type)
     * may be added. Note that this method may only be called when the search is idle.
     * 
     * @param listener search listener to add to the search
     * @throws SearchException if the search is not idle
     */
    public void addSearchListener(SearchListener<? super SolutionType> listener);
    
    /**
     * Remove the given search listener. If the search listener had not been added, <code>false</code> is returned.
     * Note that this method may only be called when the search is idle.
     * 
     * @param listener search listener to be removed
     * @throws SearchException if the search is not idle
     * @return <code>true</code> if the listener has been successfully removed
     */
    public boolean removeSearchListener(SearchListener<? super SolutionType> listener);

    /**
     * Get the current search status. The status may either be IDLE, RUNNING or TERMINATING.
     * 
     * @return current search status
     */
    public SearchStatus getStatus();
    
    /**
     * Get the runtime of the <i>current</i> run, in milliseconds. The returned value is <b>not</b> influenced by
     * any possible previous runs of the same search.
     * 
     * @return runtime of the current run, in milliseconds
     */
    public long getCurrentRuntime();
    
    /**
     * Returns the best solution found so far. The best solution is <b>retained</b> across subsequent runs of the
     * same search.
     * 
     * @return best solution found so far
     */
    public SolutionType getBestSolution();
    
    /**
     * Get the evaluation of the best solution found so far. The best solution and its evaluation are <b>retained</b>
     * across subsequent runs of the same search.
     * 
     * @return evaluation of best solution
     */
    public double getBestSolutionEvaluation();

}
