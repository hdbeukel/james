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

package org.jamesframework.core.search.algo;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import org.jamesframework.core.exceptions.SearchException;
import org.jamesframework.core.problems.Problem;
import org.jamesframework.core.problems.Solution;
import org.jamesframework.core.search.Search;
import org.jamesframework.core.search.status.SearchStatus;
import org.jamesframework.core.search.listeners.EmptySearchListener;
import org.jamesframework.core.search.listeners.SearchListener;

/**
 * <p>
 * A basic parallel search runs several (heterogeneous) searches in parallel and keeps track of the best solution found
 * by any of these searches. The parallel search consists of a single step in which (1) all subsearches are executed
 * concurrently using a cached thread pool, (2) the main search waits until termination of all subsearches and (3) the
 * main search itself terminates.
 * </p>
 * <p>
 * When the parallel search is requested to stop (see {@link Search#stop()}) it will propagate this request to its
 * subsearches and will wait for their termination. Similarly, when the parallel search is disposed (see
 * {@link Search#dispose()}), all subsearches will also be disposed.
 * </p>
 * <p>
 * Because searches are executed in separate threads, it is important to ensure that any shared objects (problem,
 * objective, constraints, neighbourhood, ...) are thread-safe.
 * </p>
 *
 * @param <SolutionType> solution type of the problems that may be solved using this search, required to extend
 * {@link Solution}
 * @author <a href="mailto:herman.debeukelaer@ugent.be">Herman De Beukelaer</a>
 */
public class BasicParallelSearch<SolutionType extends Solution> extends Search<SolutionType>{

    // thread pool for concurrent search execution
    private final ExecutorService pool;
    // futures of running searches
    private final Queue<Future<?>> futures;

    // searches to be executed in parallel
    private final List<Search<SolutionType>> searches;
    // subsearch listener
    private final SearchListener<SolutionType> subsearchListener;

    /**
     * Creates a new basic parallel search, specifying the problem to solve. The problem can not be <code>null</code>.
     * The default search name "BasicParallelSearch" is assigned to this search.
     *
     * @throws NullPointerException if <code>problem</code> is <code>null</code>
     * @param problem problem to solve
     */
    public BasicParallelSearch(Problem<SolutionType> problem) {
        this(null, problem);
    }

    /**
     * Creates a new basic parallel search, specifying the problem to solve and a custom search name. The problem can
     * not be <code>null</code>. The search name can be <code>null</code> in which case the default name
     * "BasicParallelSearch" is assigned.
     *
     * @throws NullPointerException if <code>problem</code> is <code>null</code>
     * @param problem problem to solve
     * @param name custom search name
     */
    public BasicParallelSearch(String name, Problem<SolutionType> problem) {
        super(name != null ? name : "BasicParallelSearch", problem);
        // create cached thread pool
        pool = Executors.newCachedThreadPool();
        // initialize futures queue
        futures = new LinkedList<>();
        // initialize search list
        searches = new ArrayList<>();
        // create subsearch listener
        subsearchListener = new SubsearchListener();
    }

    /**
     * <p>
     * Add the given search to the parallel algorithm, to be executed in parallel with the other searches. Only searches
     * that solve the same problem as the one specified when creating the parallel search can be added. An exception
     * will be thrown when attempting to add a search that solves a different problem. The parallel search is attached
     * as a listener to the given search, to keep track of all new best solutions found by this search. Note that this
     * method may only be called when the search is idle.
     * </p>
     * <p>
     * Because searches are executed in separate threads, it is important to ensure that any shared objects (problem,
     * objective, constraints, neighbourhood, ...) are thread-safe.
     * </p>
     *
     * @throws SearchException if the parallel search is not idle, or if the given search does not solve the same
     * problem as the parallel search
     * @param search search to add for parallel execution
     */
    public void addSearch(Search<SolutionType> search) {
        // synchronize with status updates
        synchronized (getStatusLock()) {
            // assert idle
            assertIdle("Cannot add search to basic parallel search algorithm.");
            if (search.getProblem() == getProblem()) {
                // listen to events fired by subsearch
                search.addSearchListener(subsearchListener);
                // add search
                searches.add(search);
            } else {
                throw new SearchException("Cannot add search " + search + " to basic parallel search algorithm " + this
                        + " (does not solve the same problem).");
            }
        }
    }

    /**
     * Remove the given search from the parallel algorithm, if it had been added before. If not, <code>false</code> is
     * returned. The parallel search stops listening to the given search when it is removed. Note that this method may
     * only be called when the search is idle.
     *
     * @throws SearchException if the search is not idle
     * @param search search to be removed from parallel algorithm
     * @return <code>true</code> if search was successfully removed
     */
    public boolean removeSearch(Search<SolutionType> search) {
        // synchronize with status updates
        synchronized (getStatusLock()) {
            // assert idle
            assertIdle("Cannot remove search from basic parallel search algorithm.");
            // stop listening to this search
            search.removeSearchListener(subsearchListener);
            // remove search
            return searches.remove(search);
        }
    }

    /**
     * Get a list of all searches that have been added to this parallel search for concurrent execution.
     *
     * @return list of all contained searches
     */
    public List<Search<SolutionType>> getSearches() {
        return searches;
    }

    /**
     * When the search is started, it is verified whether at least one subsearch has been added.
     * If not, an exception is thrown.
     *
     * @throws SearchException if no searches have been added
     */
    @Override
    protected void searchStarted() {
        super.searchStarted();
        // check: at least one search added
        if (searches.isEmpty()) {
            throw new SearchException("Cannot start basic parallel search: "
                    + "no subsearches added for concurrent execution.");
        }
    }

    /**
     * When requesting to stop a basic parallel search, this request is propagated to each contained search.
     */
    @Override
    public void stop() {
        // stop this search (if running)
        super.stop();
        // propagate request to subsearches
        for (Search<?> s : searches) {
            s.stop();
        }
    }

    /**
     * When disposing a basic parallel search, each of the searches that have been added to the parallel
     * algorithm are disposed and the thread pool used for concurrent search execution is released.
     */
    @Override
    protected void searchDisposed() {
        super.searchDisposed();
        // release thread pool
        pool.shutdown();
        // dispose contained searches
        for (Search<?> s : searches) {
            s.dispose();
        }
    }

    /**
     * This algorithm consists of a single search step only, in which (1) the contained subsearches are executed in
     * parallel, (2) the main search waits until they terminate and (3) the main search stops. A subsearch may terminate
     * because it has come to its natural end, because it has active stop criteria or because the main search was
     * requested to stop and propagated this request to the subsearches.
     */
    @Override
    protected void searchStep() {
        // (1) execute subsearches in parallel
        for (Search<?> s : searches) {
            futures.add(pool.submit(s));
        }
        // (2) wait for termination of subsearches
        while (!futures.isEmpty()) {
            try {
                futures.poll().get();
            } catch (InterruptedException | ExecutionException ex) {
                throw new SearchException("An error occured during concurrent execution of searches "
                        + "in basic parallel search.", ex);
            }
        }
        // (3) stop main search
        stop();
    }

    /**
     * Private listener attached to each contained subsearch, to keep track of the global best solution and to abort
     * a search that attempts to start when the main search is already terminating.
     */
    private class SubsearchListener extends EmptySearchListener<SolutionType> {
    
        /*****************************************/
        /* CALLBACKS FIRED BY CONTAINED SEARCHES */
        /*****************************************/

        /**
         * When a new best solution is found in any concurrently executed subsearch, it is picked up by the main search
         * which updates the global best solution accordingly. This method is synchronized to avoid concurrent updates of
         * the global best solution, as searches are running in separate threads.
         *
         * @param search subsearch that found a new best solution
         * @param newBestSolution new best solution in subsearch
         * @param newBestSolutionEvaluation new best solution evaluation
         */
        @Override
        public synchronized void newBestSolution(Search<? extends SolutionType> search, SolutionType newBestSolution, double newBestSolutionEvaluation) {
            updateBestSolution(newBestSolution, newBestSolutionEvaluation);
        }

        /**
         * When a subsearch has started, the main parallel search verifies that it has not yet been requested
         * to stop in the meantime. Else, the subsearch is stopped before executing any search steps.
         *
         * @param search subsearch which is starting
         */
        @Override
        public void searchStarted(Search<? extends SolutionType> search) {
            if (getStatus() == SearchStatus.TERMINATING) {
                search.stop();
            }
        }
        
    }

}
