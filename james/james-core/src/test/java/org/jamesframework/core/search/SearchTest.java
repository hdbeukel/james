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

import java.util.Random;
import java.util.concurrent.TimeUnit;
import org.jamesframework.core.problems.SubsetProblemWithData;
import org.jamesframework.core.problems.solutions.Solution;
import org.jamesframework.core.problems.solutions.SubsetSolution;
import org.jamesframework.core.search.stopcriteria.StopCriterion;
import org.jamesframework.core.util.JamesConstants;
import org.jamesframework.test.util.FakeSubsetData;
import org.jamesframework.test.util.FakeSubsetObjectiveWithData;
import org.jamesframework.test.util.FakeSubsetPenalizingConstraint;
import org.jamesframework.test.util.RandomSearch;
import org.jamesframework.test.util.TestConstants;
import org.junit.AfterClass;
import org.junit.Test;
import static org.junit.Assert.*;
import org.junit.Before;
import org.junit.BeforeClass;

/**
 * Test general search behaviour using random search dummy.
 * 
 * @author Herman De Beukelaer <herman.debeukelaer@ugent.be>
 */
public class SearchTest {

    // fake subset data (scored entities)
    private FakeSubsetData data;
    // dataset size
    private final int DATASET_SIZE = 25;
    
    // fake objective (sum of scores)
    private FakeSubsetObjectiveWithData obj;

    // subset problem to solve (select SUBSET_SIZE out of DATASET_SIZE)
    private SubsetProblemWithData<FakeSubsetData> problem;
    private final int SUBSET_SIZE = 5;
    
    // fake constraint
    private FakeSubsetPenalizingConstraint constraint;
    // minimum score diff imposed by fake constraint
    private final double MIN_SCORE_DIFF = 0.05;
    
    // search to work with (random search stub)
    Search<SubsetSolution> search;
    // number of random search steps
    private final int NUM_STEPS = 500;
    
    // random generator
    private static final Random RG = new Random();
    
    /**
     * Print message when starting tests.
     */
    @BeforeClass
    public static void setUpClass() {
        System.out.println("# Testing Search ...");
    }

    /**
     * Print message when tests are complete.
     */
    @AfterClass
    public static void tearDownClass() {
        System.out.println("# Done testing Search!");
    }
    
    /**
     * Create search and components to work with in each test method.
     */
    @Before
    public void setUp(){
        double[] scores = new double[DATASET_SIZE];
        for(int i=0; i<DATASET_SIZE; i++){
            scores[i] = RG.nextDouble();
        }
        data = new FakeSubsetData(scores);
        obj = new FakeSubsetObjectiveWithData();
        problem = new SubsetProblemWithData(obj, data, SUBSET_SIZE);
        constraint = new FakeSubsetPenalizingConstraint(MIN_SCORE_DIFF);
        search = new RandomSearch(problem, NUM_STEPS);
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
        
        // readd l
        search.addSearchListener(l);
        
        // run search (checks asserts inside listener)
        search.start();
        
    }

    /**
     * Test of getStatus method, of class Search.
     */
    @Test
    public void testGetStatus() {
        
        System.out.println(" - test getStatus");
        
        // check that search is initialy idle
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
            assertEquals(problem.evaluate(search.getBestSolution()), search.getBestSolutionEvaluation(), TestConstants.DOUBLE_COMPARISON_PRECISION);
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
        double bestSolEval = search.getBestSolutionEvaluation();
        
        // run again
        System.out.println("   >>> RUN 2 <<<");
        search.start();
        
        // validate number of steps (1 additional step in which search stopped itself, per run)
        assertEquals(NUM_STEPS+1, search.getSteps());       // same number in second run
        assertEquals(2*(NUM_STEPS+1), l.getTotalSteps());   // twice that many steps in total (over both runs)
        
        // valide new best solution evaluation
        assertTrue(search.getBestSolutionEvaluation() >= bestSolEval);
        
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

    /**
     * Search listener stub used for general callback testing. IMPORTANT: suited for single run only (checks assume this).
     */
    private class SearchListenerStub extends EmptySearchListener<SubsetSolution>{

        // flags
        private boolean started = false, stopped = false;
        
        // previous best solution evaluation
        private Double prevBestSolEval = null;
        
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
        public void newBestSolution(Search<? extends SubsetSolution> search, SubsetSolution newBestSolution, double newBestSolutionEvaluation) {
            // assert that new best solution is not rejected by the problem
            assertFalse(problem.rejectSolution(newBestSolution));
            // assert that it is better than the previous best solution
            if(prevBestSolEval != null){
                if(problem.isMinimizing()){
                    // minimizing
                    assertTrue(newBestSolutionEvaluation < prevBestSolEval);
                } else {
                    // maximizing
                    assertTrue(newBestSolutionEvaluation > prevBestSolEval);
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
    private class SearchListenerStub2 extends EmptySearchListener<SubsetSolution> {
        
        // total steps completed
        private long steps = 0;
        
        @Override
        public void newBestSolution(Search<? extends SubsetSolution> search, SubsetSolution newBestSolution, double newBestSolutionEvaluation) {
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