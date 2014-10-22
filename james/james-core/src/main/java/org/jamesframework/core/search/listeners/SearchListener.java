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

package org.jamesframework.core.search.listeners;

import org.jamesframework.core.exceptions.IncompatibleSearchListenerException;
import org.jamesframework.core.problems.Solution;
import org.jamesframework.core.problems.constraints.Validation;
import org.jamesframework.core.problems.objectives.Evaluation;
import org.jamesframework.core.search.LocalSearch;
import org.jamesframework.core.search.Search;
import org.jamesframework.core.search.status.SearchStatus;

/**
 * <p>
 * Interface of a search listener that can be attached to any search with the specified solution type (or a subtype).
 * A listener is informed when certain events occur, e.g. when the search has started, stopped or found a new best
 * solution. Every callback receives a reference to the search that called it, which may be cast to a specific search
 * type if required; if an incompatible search type is received, an {@link IncompatibleSearchListenerException} may
 * be thrown.
 * </p>
 * <p>
 * All callbacks have a default empty implementation. Some callbacks may only be fired by specific search types.
 * </p>
 * 
 * @param <SolutionType> solution type, required to extend {@link Solution}
 * @author <a href="mailto:herman.debeukelaer@ugent.be">Herman De Beukelaer</a>
 */
public interface SearchListener<SolutionType extends Solution> {
    
    /**
     * Fired when the search has started. Called only once during a search run.
     * 
     * @param search search which has started
     * @throws IncompatibleSearchListenerException if the listener is not compatible with the search
     */
    default public void searchStarted(Search<? extends SolutionType> search){}
    
    /**
     * Fired when the search has stopped. Called only once during a search run.
     * 
     * @param search search which has stopped
     * @throws IncompatibleSearchListenerException if the listener is not compatible with the search
     */
    default public void searchStopped(Search<? extends SolutionType> search){}
    
    /**
     * Fired when the search has found a new best solution. Called exactly once for every improvement.
     * 
     * @param search search which has found a new best solution
     * @param newBestSolution new best solution
     * @param newBestSolutionEvaluation evaluation of the new best solution
     * @param newBestSolutionValidation validation of the new best solution
     * @throws IncompatibleSearchListenerException if the listener is not compatible with the search
     */
    default public void newBestSolution(Search<? extends SolutionType> search,
                                        SolutionType newBestSolution,
                                        Evaluation newBestSolutionEvaluation,
                                        Validation newBestSolutionValidation){}
    
    /**
     * Fired by <strong>local searches</strong> only, when a new current solution has been adopted.
     * Called exactly once for every newly adopted current solution.
     * 
     * @param search local search which has updated its current solution
     * @param newCurrentSolution newly adopted current solution
     * @param newCurrentSolutionEvaluation evaluation of new current solution
     * @param newCurrentSolutionValidation validation of new current solution
     * @throws IncompatibleSearchListenerException if the listener is not compatible with the search
     */
    default public void newCurrentSolution(LocalSearch<? extends SolutionType> search,
                                           SolutionType newCurrentSolution,
                                           Evaluation newCurrentSolutionEvaluation,
                                           Validation newCurrentSolutionValidation){}
    
    /**
     * Fired when the search has completed a step. Called exactly once for every completed step.
     * 
     * @param search search which has completed a step
     * @param numSteps number of steps completed so far (during the current search run)
     * @throws IncompatibleSearchListenerException if the listener is not compatible with the search
     */
    default public void stepCompleted(Search<? extends SolutionType> search, long numSteps){}
    
    /**
     * Fired when the search enters a new status. Called exactly once for every status update.
     * 
     * @param search search which has changed status
     * @param newStatus new status of the search
     */
    default public void statusChanged(Search<? extends SolutionType> search, SearchStatus newStatus){}

}
