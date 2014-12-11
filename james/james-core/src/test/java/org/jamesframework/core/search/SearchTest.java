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
import java.util.Collection;
import org.jamesframework.core.exceptions.SearchException;
import org.jamesframework.core.problems.constraints.validations.Validation;
import org.jamesframework.core.problems.objectives.evaluations.Evaluation;
import org.jamesframework.core.search.listeners.SearchListener;
import org.jamesframework.core.search.status.SearchStatus;
import org.jamesframework.core.search.stopcriteria.MaxSteps;
import org.jamesframework.core.search.stopcriteria.MinDelta;
import org.jamesframework.core.search.stopcriteria.StopCriterion;
import org.jamesframework.core.subset.SubsetSolution;
import org.jamesframework.core.util.JamesConstants;
import org.jamesframework.test.util.DoubleComparatorWithPrecision;
import org.jamesframework.test.search.RandomSearchWithInternalMaxSteps;
import org.jamesframework.test.util.DelayedExecution;
import org.jamesframework.test.util.TestConstants;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Test;
import static org.junit.Assert.*;
import org.junit.Before;
import org.junit.BeforeClass;

/**
 * Test general search behaviour using random search dummy.
 * 
 * @author <a href="mailto:herman.debeukelaer@ugent.be">Herman De Beukelaer</a>
 */
public class SearchTest extends SearchTestTemplate {

    // search to work with (random search stub, with internal max steps)
    private Search<SubsetSolution> search;
    // number of random search steps
    private final int NUM_STEPS = 500;
    
    /**
     * Print message when starting tests.
     */
    @BeforeClass
    public static void setUpClass() {
        System.out.println("# Testing Search ...");
        SearchTestTemplate.setUpClass();
    }

    /**
     * Print message when tests are complete.
     */
    @AfterClass
    public static void tearDownClass() {
        System.out.println("# Done testing Search!");
    }
    
    @Override
    @Before
    public void setUp(){
        // call super
        super.setUp();
        // create random search with internal max steps
        search = new RandomSearchWithInternalMaxSteps<>(problem, NUM_STEPS);
    }
    
    @After
    public void tearDown(){
        // dispose search
        search.dispose();
    }
    
    @Test
    public void testDefaultName(){
        System.out.println(" - test default name");
        
        search = new Search<SubsetSolution>(problem) {
            @Override
            protected void searchStep() {
                // do nothing;
            }
        };
        assertEquals("Search", search.getName());
    }
    
    @Test
    public void testUniqueIDs(){
        System.out.println(" - test unique IDs");
        
        Collection<Search> searches = new ArrayList<>();
        int n = 100;
        
        for(int i=0; i<n; i++){
            searches.add(new Search<SubsetSolution>(problem) {
                @Override
                protected void searchStep() {
                    // do nothing;
                }
            });
        }
        
        assertEquals(n, searches.stream().map(Search::getID).distinct().count());
        
        searches.forEach(Search::dispose);
    }

    /**
     * Test of start method, of class Search.
     */
    @Test
    public void testStart() {
        
        System.out.println(" - test start");
        
        // start search to check if it stops
        search.start();
        
    }

    /**
     * Test of stop method, of class Search.
     */
    @Test
    public void testStop() {
        
        System.out.println(" - test stop");
        
        // call stop (should have no effect on idle search)
        search.stop();
        
        // assert that status is still idle
        assertEquals(SearchStatus.IDLE, search.getStatus());
        
    }
    
    @Test
    public void testAssertIdle(){
        System.out.println(" - test assertIdle");
        
        // create never ending search
        search = new Search<SubsetSolution>(problem) {
            @Override
            protected void searchStep() {
                // do nothing;
            }
        };
        search.assertIdle("should pass");
        
        // schedule task to assert search status while running
        DelayedExecution.schedule(() -> {
            boolean thrown = false;
            try {
                search.assertIdle("should throw error");
            } catch (SearchException ex){
                thrown = true;
            } finally {
                search.stop();
                assertTrue(thrown);
            }
        }, 500);
        
        search.start();
        
    }
    
    /**
     * Test with mandatory constraint.
     */
    @Test
    public void testWithConstraint() {
        
        System.out.println(" - test problem with constraint");
        
        // add constraint to problem
        problem.addMandatoryConstraint(constraint);
        
        // run search
        search.start();
        
        // check best solution
        if(search.getBestSolution() != null){
            assertTrue(problem.validate(search.getBestSolution()).passed());
        }
        
    }

    /**
     * Test of addSearchListener and removeSearchListener method, of class Search.
     */
    @Test
    public void testAddRemoveSearchListener() {
        
        System.out.println(" - test search with listener ");
        
        // add search listener stub
        SearchListenerStub l = new SearchListenerStub();
        search.addSearchListener(l);
        
        // create second listener
        SearchListenerStub l2 = new SearchListenerStub();
        
        // try to remove l2
        assertFalse(search.removeSearchListener(l2));
        
        // remove l
        assertTrue(search.removeSearchListener(l));
        
        // re-add l
        search.addSearchListener(l);
        
        // run search (checks asserts inside listener)
        search.start();
        
    }
    
    @Test
    public void testAddRemoveStopCriterion(){
        
        System.out.println(" - test search with stop criterion");
        
        // use dummy search
        search = new Search<SubsetSolution>(problem) {
            @Override
            protected void searchStep() {
                // do nothing;
            }
        };
        
        // add stop criterion
        StopCriterion sc = new MaxSteps(1000);
        search.addStopCriterion(sc);
        
        // create second stop criterion
        StopCriterion sc2 = new MinDelta(0.0001);
        
        // try to remove second
        assertFalse(search.removeStopCriterion(sc2));
        
        // remove first
        assertTrue(search.removeStopCriterion(sc));
        
        // re-add first
        search.addStopCriterion(sc);
        
        // run search (should stop)
        search.start();
        
    }

    /**
     * Test of getStatus method, of class Search.
     */
    @Test
    public void testGetStatus() {
        
        System.out.println(" - test getStatus");
        
        // check that search is initially idle
        assertEquals(SearchStatus.IDLE, search.getStatus());
        
        // start search
        search.start();
        
        // complete, check that status is back to idle
        assertEquals(SearchStatus.IDLE, search.getStatus());
        
    }

    /**
     * Test of getBestSolution method, of class Search.
     */
    @Test
    public void testGetBestSolution() {
        
        System.out.println(" - test getBestSolution");
        
        // check null before first run
        assertNull(search.getBestSolution());
        
    }

    /**
     * Test of getBestSolutionEvaluation method, of class Search.
     */
    @Test
    public void testGetBestSolutionEvaluation() {
        
        System.out.println(" - test getBestSolutionEvaluation");
        
        // run search
        search.start();
        
        // verify best solution evaluation
        if(search.getBestSolution() != null){
            assertEquals(problem.evaluate(search.getBestSolution()).getValue(),
                         search.getBestSolutionEvaluation().getValue(),
                         TestConstants.DOUBLE_COMPARISON_PRECISION);
        }
        
    }

    /**
     * Test of getRuntime method, of class Search.
     */
    @Test
    public void testGetRuntime() {
        
        System.out.println(" - test getRuntime");
        
        // check value before first run
        assertEquals(JamesConstants.INVALID_TIME_SPAN, search.getRuntime());
        
        // run search
        search.start();
        
        // assert positive runtime
        assertTrue(search.getRuntime() >= 0);
        
    }

    /**
     * Test of getSteps method, of class Search.
     */
    @Test
    public void testGetSteps() {
        
        System.out.println(" - test getSteps (2 runs)");
        
        // check value before first run
        assertEquals(JamesConstants.INVALID_STEP_COUNT, search.getSteps());
        
        // attach listener
        SearchListenerStub2 l = new SearchListenerStub2();
        search.addSearchListener(l);
        
        // run search
        System.out.println("   >>> RUN 1 <<<");
        search.start();
        
        // validate number of steps (1 additional step in which search stopped itself)
        assertEquals(NUM_STEPS+1, search.getSteps());
        // store best solution evaluation
        Evaluation bestSolEval = search.getBestSolutionEvaluation();
        
        // run again
        System.out.println("   >>> RUN 2 <<<");
        search.start();
        
        // validate number of steps (1 additional step in which search stopped itself, per run)
        assertEquals(NUM_STEPS+1, search.getSteps());       // same number in second run
        assertEquals(2*(NUM_STEPS+1), l.getTotalSteps());   // twice that many steps in total (over both runs)
        
        // validate new best solution evaluation
        assertTrue(DoubleComparatorWithPrecision.greaterThanOrEqual(
                    search.getBestSolutionEvaluation().getValue(),
                    bestSolEval.getValue(),
                    TestConstants.DOUBLE_COMPARISON_PRECISION)
                  );
        
    }

    /**
     * Test of getTimeWithoutImprovement method, of class Search.
     */
    @Test
    public void testGetTimeWithoutImprovement() {
        
        System.out.println(" - test getTimeWithoutImprovement");
        
        // check value before first run
        assertEquals(JamesConstants.INVALID_TIME_SPAN, search.getTimeWithoutImprovement());
        
        // run search
        search.start();
        
        // check: should return positive value
        assertTrue(search.getTimeWithoutImprovement() >= 0);
        // check: can not be larger than total runtime
        assertTrue(search.getTimeWithoutImprovement() <= search.getRuntime());
        
    }

    /**
     * Test of getStepsWithoutImprovement method, of class Search.
     */
    @Test
    public void testGetStepsWithoutImprovement() {
        
        System.out.println(" - test getStepsWithoutImprovement");
        
        // check value before first run
        assertEquals(JamesConstants.INVALID_STEP_COUNT, search.getStepsWithoutImprovement());
        
        // run search
        search.start();
        
        // check: should return positive value
        assertTrue(search.getStepsWithoutImprovement() >= 0);
        // check: cannot be larger than total steps
        assertTrue(search.getStepsWithoutImprovement() <= search.getSteps());
        
    }
    
    @Test
    public void testGetMinDelta(){
        
        System.out.println(" - test getMinDelta");
        
        // override searchStarted to check min delta during initialization
        search = new RandomSearchWithInternalMaxSteps<SubsetSolution>(problem, NUM_STEPS){
            @Override
            protected void searchStarted(){
                assertTrue(search.getMinDelta() == JamesConstants.INVALID_DELTA);
            }
        };
        
        // check value before first run
        assertTrue(search.getMinDelta() == JamesConstants.INVALID_DELTA);
        
        // run search
        search.start();
        
        // check: should return positive value or invalid constant
        assertTrue(search.getMinDelta() == JamesConstants.INVALID_DELTA || search.getMinDelta() > 0);
        
    }

    /**
     * Search listener stub used for general callback testing. IMPORTANT: suited for single run only (checks assume this).
     */
    private class SearchListenerStub implements SearchListener<SubsetSolution>{

        // flags
        private boolean started = false, stopped = false;
        
        // previous best solution evaluation
        private Evaluation prevBestSolEval = null;
        private double delta = 1e-12;
        
        // number of times stepCompleted() was called
        private long numCallsStepCompleted = 0;
        
        @Override
        public void searchStarted(Search<? extends SubsetSolution> search) {
            // assert that callback is called only once
            assertFalse(started);
            started = true;
        }

        @Override
        public void searchStopped(Search<? extends SubsetSolution> search) {
            // assert that callback is called only once
            assertFalse(stopped);
            stopped = true;
        }

        @Override
        public void newBestSolution(Search<? extends SubsetSolution> search,
                                    SubsetSolution newBestSolution,
                                    Evaluation newBestSolutionEvaluation,
                                    Validation newBestSolutionValidation) {
            // assert that new best solution is valid
            assertTrue(problem.validate(newBestSolution).passed());
            // assert that it is better than the previous best solution
            if(prevBestSolEval != null){
                if(problem.isMinimizing()){
                    assertTrue(DoubleComparatorWithPrecision.smallerThanOrEqual(
                                    newBestSolutionEvaluation.getValue(),
                                    prevBestSolEval.getValue(),
                                    delta));
                } else {
                    assertTrue(DoubleComparatorWithPrecision.greaterThanOrEqual(
                                    newBestSolutionEvaluation.getValue(),
                                    prevBestSolEval.getValue(),
                                    delta));
                }
            }
            // update previous best solution evaluation
            prevBestSolEval = newBestSolutionEvaluation;
            System.out.println("   >>> new best solution: " + newBestSolutionEvaluation);
        }

        @Override
        public void stepCompleted(Search<? extends SubsetSolution> search, long numSteps) {
            // check that this callback is called exactly once for every completed step
            assertEquals(numCallsStepCompleted+1, numSteps);
            // update num calls
            numCallsStepCompleted++;
        }

    }
    
    /**
     * Search listener stub used to track total number of steps over all runs.
     */
    private class SearchListenerStub2 implements SearchListener<SubsetSolution> {
        
        // total steps completed
        private long steps = 0;
        
        @Override
        public void newBestSolution(Search<? extends SubsetSolution> search,
                                    SubsetSolution newBestSolution,
                                    Evaluation newBestSolutionEvaluation,
                                    Validation newBestSolutionValidation) {
            System.out.println("   >>> new best solution: " + newBestSolutionEvaluation);
        }
        
        @Override
        public void stepCompleted(Search<? extends SubsetSolution> search, long numSteps) {
            steps++;
        }
        
        public long getTotalSteps(){
            return steps;
        }
        
    }

}