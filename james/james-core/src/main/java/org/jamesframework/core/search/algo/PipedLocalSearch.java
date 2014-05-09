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

package org.jamesframework.core.search.algo;

import java.util.List;
import org.jamesframework.core.problems.Problem;
import org.jamesframework.core.problems.solutions.Solution;
import org.jamesframework.core.search.LocalSearch;
import org.jamesframework.core.search.Search;
import org.jamesframework.core.search.SearchStatus;
import org.jamesframework.core.search.listeners.SearchListener;

/**
 * <p>
 * A piped local search consists of a composition of other local search \(S_1, ..., S_n\). These local searches
 * are consecutively executed in a pipeline where the best solution found by search \(S_i, i \le 1 \lt n,\) is
 * used as initial solution for search \(S_{i+1}\). The initial solution of search \(S_1\) corresponds to the
 * initial solution that has been set for the piped local search, if any; else, \(S_1\) starts from a randomly
 * generated solution. The best solution obtained after executing the entire pipeline is eventually returned as
 * the global best solution of the piped search.
 * </p>
 * <p>
 * This search is a single step search, meaning that execution of the entire pipeline is considered to be one
 * step of the piped search, and that the global algorithm terminates when this step has completed. Moreover,
 * a piped local search can only be started once. After its single step has completed, the search will automatically
 * dispose itself together with all contained local searches, so that it can not be restarted.
 * </p>
 * <p>
 * Only searches solving the same problem as the one specified when creating the piped local search can be included
 * in the pipeline.
 * </p>
 * <p>
 * When a piped local search is requested to stop, this request is propagated to the searches included in the pipeline.
 * Similarly, when a piped local search is disposed, all searches in the pipeline are also disposed. Note that a piped
 * local search is automatically disposed when its single step has completed, i.e. when the entire pipeline has been
 * executed.
 * </p>
 * 
 * @param <SolutionType> solution type of the problems that may be solved using this search, required to extend {@link Solution}
 * @author <a href="mailto:herman.debeukelaer@ugent.be">Herman De Beukelaer</a>
 */
public class PipedLocalSearch<SolutionType extends Solution> extends LocalSearch<SolutionType>{
    
    // pipeline of local searches to be executed
    private List<LocalSearch<SolutionType>> pipeline;
    
    /**
     * Creates a new piped local search, specifying the problem to solve and a list of local searches to
     * be executed in the pipeline, in the given order. Neither arguments can be <code>null</code>, and the
     * list of local searches can not be empty and can not contain any <code>null</code> elements. Moreover,
     * only searches solving the specified problem can be included in the pipeline. The search name defaults
     * to "PipedLocalSearch".
     * 
     * @throws NullPointerException if <code>problem</code> or <code>pipeline</code> are <code>null</code>,
     *                              or if <code>pipeline</code> contains any <code>null</code> elements
     * @throws IllegalArgumentException if <code>pipeline</code> is empty
     * @param problem problem to solve
     * @param pipeline local searches to execute in the pipeline, in the given order
     */
    public PipedLocalSearch(Problem<SolutionType> problem, List<LocalSearch<SolutionType>> pipeline){
        this(null, problem, pipeline);
    }
    
    /**
     * Creates a new piped local search, specifying the problem to solve, a list of local searches to
     * be executed in the pipeline, in the given order, and a custom search name. Only the search name
     * can be <code>null</code>, in which case the default name "PipedLocalSearch" is assigned. The
     * list of local searches can not be empty and can not contain any <code>null</code> elements. Moreover,
     * only searches solving the specified problem can be included in the pipeline.
     * 
     * @throws NullPointerException if <code>problem</code> or <code>pipeline</code> are <code>null</code>,
     *                              or if <code>pipeline</code> contains any <code>null</code> elements
     * @throws IllegalArgumentException if <code>pipeline</code> is empty, or if it contains searches that
     *                                  do not solve the specified problem
     * @param name custom search name
     * @param problem problem to solve
     * @param pipeline local searches to execute in the pipeline, in the given order
     */
    public PipedLocalSearch(String name, Problem<SolutionType> problem, List<LocalSearch<SolutionType>> pipeline){
        super(name != null ? name : "PipedLocalSearch", problem);
        // check arguments
        if(pipeline == null){
            throw new NullPointerException("Error while creating piped local search: pipeline is null.");
        }
        if(pipeline.isEmpty()){
            throw new IllegalArgumentException("Error while creating piped local search: pipeline is empty.");
        }
        for(LocalSearch<?> l : pipeline){
            if(l == null){
                throw new NullPointerException("Error while creating piped local search: pipeline contains null elements.");
            }
            if(l.getProblem() != problem){
                throw new IllegalArgumentException("Error while creating piped local search: pipeline contains searches"
                                                    + " that do not solve the given problem.");
            }
        }
        // store pipeline
        this.pipeline = pipeline;
        // listen to events fired by searches in pipeline (to abort
        // searches starting when piped search is terminating)
        for(LocalSearch<SolutionType> l : pipeline){
            l.addSearchListener(new AbortWhenTerminatingListener());
        }
    }
    
    /**
     * When requesting to stop a piped local search, this request is propagated to each search in the pipeline.
     */
    @Override
    public void stop() {
        // stop this search (if running)
        super.stop();
        // propagate request to searches in pipeline
        for (Search<?> s : pipeline) {
            s.stop();
        }
    }
    
    /**
     * When disposing a piped local search, all searches in the pipeline are also disposed.
     */
    @Override
    protected void searchDisposed() {
        super.searchDisposed();
        // dispose searches in pipeline
        for (Search<?> s : pipeline) {
            s.dispose();
        }
    }
    
    /**
     * General start method is overriden so that search is automatically disposed when {@link Search#start()} returns.
     */
    @Override
    public void start(){
        // perform search
        super.start();
        // automatical disposal after single run
        dispose();
    }

    /**
     * Executes all local searches in the pipeline, where the best solution of the previous search is used as initial
     * solution for the next search. When the entire pipeline has been executed, the search step is complete and the
     * search terminates.
     */
    @Override
    protected void searchStep() {
        // execute pipeline
        for(LocalSearch<SolutionType> l : pipeline){
            // set initial solution (copy!)
            l.setCurrentSolution(getProblem().copySolution(getCurrentSolution()));
            // run local search
            l.start();
            // get best solution found by search l
            SolutionType bestSol = l.getBestSolution();
            double bestSolEval = l.getBestSolutionEvaluation();
            // if not null and different from current solution: set as new
            // current solution and update global best solution accordingly
            if(bestSol != null && !bestSol.equals(getCurrentSolution())){
                updateCurrentSolution(bestSol, bestSolEval);
                updateBestSolution(bestSol, bestSolEval);
            }
        }
        // pipeline complete: stop search
        stop();
    }

    /**
     * Private listener attached to each search in the pipeline, to abort searches that attempt to start when
     * the main search is already terminating.
     */
    private class AbortWhenTerminatingListener implements SearchListener<SolutionType>{
    
        /*******************************************/
        /* CALLBACKS FIRED BY SEARCHES IN PIPELINE */
        /*******************************************/

        /**
         * When a search from the pipeline has started, the main search verifies that it has not yet been
         * requested to stop in the meantime. Else, the search is stopped before executing any search steps.
         *
         * @param search search from the pipeline which is starting
         */
        @Override
        public void searchStarted(Search<? extends SolutionType> search) {
            if (getStatus() == SearchStatus.TERMINATING) {
                search.stop();
            }
        }

        /**
         * No actions taken here.
         * @param search ignored
         */
        @Override
        public void searchStopped(Search<? extends SolutionType> search) {}
        /**
         * No actions taken here.
         * @param search ignored
         * @param newBestSolution ignored
         * @param newBestSolutionEvaluation ignored
         */
        @Override
        public void newBestSolution(Search<? extends SolutionType> search, SolutionType newBestSolution, double newBestSolutionEvaluation) {}
        /**
         * No actions taken here.
         * @param search ignored
         * @param numSteps ignored
         */
        @Override
        public void stepCompleted(Search<? extends SolutionType> search, long numSteps) {}
        /**
         * No actions taken here.
         * @param search ignored
         * @param newStatus ignored
         */
        @Override
        public void statusChanged(Search<? extends SolutionType> search, SearchStatus newStatus) {}
        
    }
    
}
