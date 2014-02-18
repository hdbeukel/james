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

import java.util.concurrent.TimeUnit;
import org.jamesframework.core.problems.solutions.SubsetSolution;
import org.jamesframework.core.search.SearchTestTemplate;
import org.jamesframework.core.search.neigh.Neighbourhood;
import org.jamesframework.core.search.neigh.subset.SingleSwapNeighbourhood;
import org.jamesframework.test.util.NeverSatisfiedConstraintStub;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import static org.junit.Assert.*;
import org.junit.Before;

/**
 * Test random descent algorithm.
 * 
 * @author Herman De Beukelaer <herman.debeukelaer@ugent.be>
 */
public class RandomDescentTest extends SearchTestTemplate {

    // random descent algorithm
    private RandomDescent<SubsetSolution> search;
    // neighbourhood
    private Neighbourhood<SubsetSolution> neigh;
    
    // maximum runtime
    private final long SINGLE_RUN_RUNTIME = 500;
    private final long MULTI_RUN_RUNTIME = 1;
    private final TimeUnit MAX_RUNTIME_TIME_UNIT = TimeUnit.MILLISECONDS;
    
    // number of runs in multi run tests
    private final int NUM_RUNS = 5;
    
    /**
     * Print message when starting tests.
     */
    @BeforeClass
    public static void setUpClass() {
        SearchTestTemplate.setUpClass();
        System.out.println("# Testing RandomDescent ...");
    }

    /**
     * Print message when tests are complete.
     */
    @AfterClass
    public static void tearDownClass() {
        System.out.println("# Done testing RandomDescent!");
    }
    
    @Override
    @Before
    public void setUp(){
        // call super
        super.setUp();
        // create neighbourhood
        neigh = new SingleSwapNeighbourhood();
        // create random search
        search = new RandomDescent<>(problem, neigh);
    }

    /**
     * Test single run.
     */
    @Test
    public void testSingleRun() {
        System.out.println(" - test single run");
        // single run
        singleRunWithMaxRuntime(search, SINGLE_RUN_RUNTIME, MAX_RUNTIME_TIME_UNIT);
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
        singleRunWithMaxRuntime(search, SINGLE_RUN_RUNTIME, MAX_RUNTIME_TIME_UNIT);
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

}