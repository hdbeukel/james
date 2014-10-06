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

package org.jamesframework.core.search.algo.vns;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;
import org.jamesframework.core.problems.Problem;
import org.jamesframework.core.subset.SubsetSolution;
import org.jamesframework.core.search.NeighbourhoodSearch;
import org.jamesframework.core.search.Search;
import org.jamesframework.core.search.SearchTestTemplate;
import org.jamesframework.core.search.algo.RandomDescent;
import org.jamesframework.core.search.listeners.SearchListener;
import org.jamesframework.core.search.neigh.Neighbourhood;
import org.jamesframework.core.subset.neigh.SingleSwapNeighbourhood;
import org.jamesframework.core.subset.neigh.adv.DisjointMultiSwapNeighbourhood;
import org.jamesframework.core.search.stopcriteria.MaxRuntime;
import org.jamesframework.core.util.LocalSearchFactory;
import org.jamesframework.test.stubs.NeverSatisfiedConstraintStub;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import static org.junit.Assert.*;
import org.junit.Before;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;

/**
 * Test variable neighbourhood search algorithm.
 * 
 * @author <a href="mailto:herman.debeukelaer@ugent.be">Herman De Beukelaer</a>
 */
@RunWith(Parameterized.class)
public class VariableNeighbourhoodSearchTest extends SearchTestTemplate {

    // repeat all tests with both default VND modification algorithm and a custom Random Descent modifier
    @Parameterized.Parameters
    public static List<Object[]> data(){
        List<Object[]> params = new ArrayList<>();
        // first run: default VND local search algorithm
        Object[] run1 = new Object[2];
        run1[0] = "VND (default)";
        run1[1] = null;
        params.add(run1);
        // second run: custom random descent local search algorithm
        Object[] run2 = new Object[2];
        run2[0] = "Random Descent (custom)";
        run2[1] = (LocalSearchFactory<SubsetSolution>) (p) -> {
            NeighbourhoodSearch<SubsetSolution> ls = new RandomDescent<>(p, new SingleSwapNeighbourhood());
            ls.addStopCriterion(new MaxRuntime(50, TimeUnit.MILLISECONDS));
            ls.setStopCriterionCheckPeriod(50, TimeUnit.MILLISECONDS);
            return ls;
        };
        params.add(run2);
        // return params
        return params;
    }
    
    public VariableNeighbourhoodSearchTest(String modAlgoString, LocalSearchFactory<SubsetSolution> modAlgoFactory){
        this.modAlgoString = modAlgoString;
        this.modAlgoFactory = modAlgoFactory;
    }
    
    // parameters
    private final String modAlgoString;
    private final LocalSearchFactory<SubsetSolution> modAlgoFactory;
    
    // variable neighbourhood search algorithm
    private VariableNeighbourhoodSearch<SubsetSolution> search;
    
    // maximum runtime
    private final long SINGLE_RUN_RUNTIME = 3000;
    private final long MULTI_RUN_RUNTIME = 500;
    private final TimeUnit MAX_RUNTIME_TIME_UNIT = TimeUnit.MILLISECONDS;
    
    // number of runs in multi run tests
    private final int NUM_RUNS = 5;
    
    // rejected moves listener
    private Listener listener;
    
    /**
     * Print message when starting tests.
     */
    @BeforeClass
    public static void setUpClass() {
        System.out.println("# Testing VariableNeighbourhoodSearch ...");
        // reduce dataset size and subset size (to test large neighbourhoods)
        DATASET_SIZE = 50;
        SUBSET_SIZE = 10;
        // call super
        SearchTestTemplate.setUpClass();
    }

    /**
     * Print message when tests are complete.
     */
    @AfterClass
    public static void tearDownClass() {
        System.out.println("# Done testing VariableNeighbourhoodSearch!");
    }
    
    @Override
    @Before
    public void setUp(){
        // call super
        super.setUp();
        // create list of neighbourhoods with 1 up to 5 swaps
        List<Neighbourhood<SubsetSolution>> neighs = new ArrayList<>();
        neighs.add(neigh);
        for(int s=2; s<=5; s++){
            neighs.add(new DisjointMultiSwapNeighbourhood(s));
        }
        // create variable neighbourhood search
        if(modAlgoFactory == null){
            // default VND modifier using only the first 2 neighbourhoods
            search = new VariableNeighbourhoodSearch<>(problem, neighs, neighs.subList(0, 2));
        } else {
            // custom modifier
            search = new VariableNeighbourhoodSearch<>(problem, neighs, modAlgoFactory);
        }
        // create and add listener
        listener = new Listener();
        search.addSearchListener(listener);
        // print information about which modification algorithm is used
        System.out.println(" - MODIFICATION ALGORITHM: " + modAlgoString);
    }
    
    @After
    public void tearDown(){
        // print number of accepted and rejected moves during last run
        System.out.println("   >>> Total accepted moves: " + listener.getTotalAcceptedMoves());
        System.out.println("   >>> Total rejected moves: " + listener.getTotalRejectedMoves());
        System.out.println("   >>> Total runtime: " + listener.getTotalRuntime() + " ms");
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
        problem.addMandatoryConstraint(new NeverSatisfiedConstraintStub());
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
        problem.addMandatoryConstraint(new NeverSatisfiedConstraintStub());
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
        // perform multiple runs (maximizing objective)
        multiRunWithMaximumRuntime(search, MULTI_RUN_RUNTIME, MAX_RUNTIME_TIME_UNIT, NUM_RUNS, true, true);
        System.out.println("   >>> best: " + search.getBestSolutionEvaluation());
        // constraint satisfied ?
        if(problem.getViolatedConstraints(search.getBestSolution()).isEmpty()){
            System.out.println("   >>> constraint satisfied!");
        } else {
            System.out.println("   >>> constraint not satisfied, penalty "
                    + constraint.validate(search.getBestSolution(), data).getPenalty());
        }
    }
    
    /**
     * Listener.
     */
    private class Listener implements SearchListener<SubsetSolution>{
        // accepted moves counter
        private int acceptedMoves = 0;
        // rejected moves counter
        private int rejectedMoves = 0;
        // total runtime counter
        private long totalRuntime = 0;
        
        @Override
        public void searchStopped(Search<? extends SubsetSolution> search) {
            // increase counters
            acceptedMoves += ((NeighbourhoodSearch<?>) search).getNumAcceptedMoves();
            rejectedMoves += ((NeighbourhoodSearch<?>) search).getNumRejectedMoves();
            totalRuntime += search.getRuntime();
        }
        public int getTotalAcceptedMoves(){
            return acceptedMoves;
        }
        public int getTotalRejectedMoves(){
            return rejectedMoves;
        }
        public long getTotalRuntime(){
            return totalRuntime;
        }
    }

}