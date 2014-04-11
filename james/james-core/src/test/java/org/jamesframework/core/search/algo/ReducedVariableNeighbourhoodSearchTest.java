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

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;
import org.jamesframework.core.problems.solutions.SubsetSolution;
import org.jamesframework.core.search.NeighbourhoodSearch;
import org.jamesframework.core.search.Search;
import org.jamesframework.core.search.SearchTestTemplate;
import org.jamesframework.core.search.listeners.EmptyNeighbourhoodSearchListener;
import org.jamesframework.core.search.neigh.Neighbourhood;
import org.jamesframework.core.search.neigh.subset.adv.DisjointMultiSwapNeighbourhood;
import org.jamesframework.test.util.NeverSatisfiedConstraintStub;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import static org.junit.Assert.*;
import org.junit.Before;

/**
 * Test reduced variable neighbourhood search algorithm.
 * 
 * @author <a href="mailto:herman.debeukelaer@ugent.be">Herman De Beukelaer</a>
 */
public class ReducedVariableNeighbourhoodSearchTest extends SearchTestTemplate {

    // reduced variable neighbourhood search algorithm
    private ReducedVariableNeighbourhoodSearch<SubsetSolution> search;
    
    // maximum runtime
    private final long SINGLE_RUN_RUNTIME = 1000;
    private final long MULTI_RUN_RUNTIME = 50;
    private final TimeUnit MAX_RUNTIME_TIME_UNIT = TimeUnit.MILLISECONDS;
    
    // number of runs in multi run tests
    private final int NUM_RUNS = 5;
    
    // multi swap neighbourhood
    private Neighbourhood<SubsetSolution> multiSwapNeigh;
    
    // rejected moves listener
    private RejectedMovesListener listener;
    
    /**
     * Print message when starting tests.
     */
    @BeforeClass
    public static void setUpClass() {
        System.out.println("# Testing ReducedVariableNeighbourhoodSearch ...");
        // call super
        SearchTestTemplate.setUpClass();
    }

    /**
     * Print message when tests are complete.
     */
    @AfterClass
    public static void tearDownClass() {
        System.out.println("# Done testing ReducedVariableNeighbourhoodSearch!");
    }
    
    @Override
    @Before
    public void setUp(){
        // call super
        super.setUp();
        // create multi swap neighbourhood with 2 swaps
        multiSwapNeigh = new DisjointMultiSwapNeighbourhood(2);
        // create list of neighbourhoods: (1) single swap, (2) dual swap
        List<Neighbourhood<? super SubsetSolution>> neighs = new ArrayList<>();
        neighs.add(neigh);
        neighs.add(multiSwapNeigh);
        // create reduced variable neighbourhood search
        search = new ReducedVariableNeighbourhoodSearch<>(problem, neighs);
        // create and add rejected moves listener
        listener = new RejectedMovesListener();
        search.addSearchListener(listener);
    }
    
    @After
    public void tearDown(){
        // print number of rejected moves during last run
        System.out.println("   >>> Total rejected moves: " + listener.getTotalRejectedMoves());
        // dispose search
        search.dispose();
    }

    /**
     * Test single run.
     */
    @Test
    public void testSingleRun() {
        System.out.println(" - test single run");
        // single run
        singleRunWithMaxRuntime(search, problem, SINGLE_RUN_RUNTIME, MAX_RUNTIME_TIME_UNIT);
    }
    
    /**
     * Test single run with unsatisfiable constraint.
     */
    @Test
    public void testSingleRunWithUnsatisfiableConstraint() {
        System.out.println(" - test single run with unsatisfiable constraint");
        // add constraint
        problem.addRejectingConstraint(new NeverSatisfiedConstraintStub());
        // single run
        singleRunWithMaxRuntime(search, problem, SINGLE_RUN_RUNTIME, MAX_RUNTIME_TIME_UNIT);
        // verify
        assertNull(search.getBestSolution());
    }
    
    /**
     * Test subsequent runs (maximizing).
     */
    @Test
    public void testSubsequentRuns() {
        System.out.println(" - test subsequent runs (maximizing)");
        // perform multiple runs (maximizing objective)
        multiRunWithMaximumRuntime(search, MULTI_RUN_RUNTIME, MAX_RUNTIME_TIME_UNIT, NUM_RUNS, true, true);
    }
    
    /**
     * Test subsequent runs (minimizing).
     */
    @Test
    public void testSubsequentRunsMinimizing() {
        System.out.println(" - test subsequent runs (minimizing)");
        // set minimizing
        obj.setMinimizing();
        // perform multiple runs (maximizing objective)
        multiRunWithMaximumRuntime(search, MULTI_RUN_RUNTIME, MAX_RUNTIME_TIME_UNIT, NUM_RUNS, false, true);
    }
    
    /**
     * Test subsequent runs with unsatisfiable constraint.
     */
    @Test
    public void testSubsequentRunsWithUnsatisfiableConstraint() {
        System.out.println(" - test subsequent runs with unsatisfiable constraint");
        // set constraint
        problem.addRejectingConstraint(new NeverSatisfiedConstraintStub());
        // perform multiple runs (maximizing objective)
        multiRunWithMaximumRuntime(search, MULTI_RUN_RUNTIME, MAX_RUNTIME_TIME_UNIT, NUM_RUNS, true, true);
        // verify
        assertNull(search.getBestSolution());
    }
    
    /**
     * Test subsequent runs with penalizing constraint.
     */
    @Test
    public void testSubsequentRunsWithPenalizingConstraint() {
        System.out.println(" - test subsequent runs with penalizing constraint");
        // set constraint
        problem.addPenalizingConstraint(constraint);
        // perform 3 times as many runs as usual for this harder problem (maximizing objective)
        multiRunWithMaximumRuntime(search, MULTI_RUN_RUNTIME, MAX_RUNTIME_TIME_UNIT, 3*NUM_RUNS, true, false);
        System.out.println("   >>> best: " + search.getBestSolutionEvaluation());
        // constraint satisfied ?
        if(problem.getViolatedConstraints(search.getBestSolution()).isEmpty()){
            System.out.println("   >>> constraint satisfied!");
        } else {
            System.out.println("   >>> constraint not satisfied, penalty " + constraint.computePenalty(search.getBestSolution(), data));
        }
    }
    
    /**
     * Rejected moves listener (private).
     */
    
    private class RejectedMovesListener extends EmptyNeighbourhoodSearchListener<SubsetSolution>{
        // rejected moves counter
        private int rejectedMoves = 0;
        
        @Override
        public void searchStopped(Search<? extends SubsetSolution> search) {
            // increase counter with number of rejected moves during this run
            rejectedMoves += ((NeighbourhoodSearch<?>) search).getNumRejectedMoves();
        }
        
        public int getTotalRejectedMoves(){
            return rejectedMoves;
        }
    }

}