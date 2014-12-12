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
import java.util.List;
import java.util.concurrent.TimeUnit;
import org.jamesframework.core.exceptions.SearchException;
import org.jamesframework.core.problems.Problem;
import org.jamesframework.core.problems.AbstractProblem;
import org.jamesframework.core.problems.Solution;
import org.jamesframework.core.problems.objectives.evaluations.PenalizedEvaluation;
import org.jamesframework.core.subset.SubsetSolution;
import org.jamesframework.core.search.Search;
import org.jamesframework.core.search.status.SearchStatus;
import org.jamesframework.core.search.SearchTestTemplate;
import org.jamesframework.core.search.algo.exh.ExhaustiveSearch;
import org.jamesframework.core.search.listeners.SearchListener;
import org.jamesframework.core.subset.algo.exh.SubsetSolutionIterator;
import org.jamesframework.test.util.DoubleComparatorWithPrecision;
import org.jamesframework.test.fakes.ScoredFakeSubsetData;
import org.jamesframework.test.stubs.NeverSatisfiedConstraintStub;
import org.jamesframework.test.stubs.NeverSatisfiedPenalizingConstraintStub;
import org.jamesframework.test.util.DelayedExecution;
import org.jamesframework.test.util.TestConstants;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import static org.junit.Assert.*;
import org.junit.Before;

/**
 * Test basic parallel search algorithm.
 * 
 * @author <a href="mailto:herman.debeukelaer@ugent.be">Herman De Beukelaer</a>
 */
public class BasicParallelSearchTest extends SearchTestTemplate {

    // parallel algorithm
    private BasicParallelSearch<SubsetSolution> parallelSearch;
    // searches added for parallel execution
    private List<Search<SubsetSolution>> subsearches;
    
    // maximum runtime
    private final long SINGLE_RUN_RUNTIME = 1000;
    private final long MULTI_RUN_RUNTIME = 200;
    private final TimeUnit MAX_RUNTIME_TIME_UNIT = TimeUnit.MILLISECONDS;
    
    // number of runs in multi run tests
    private final int NUM_RUNS = 5;
    
    /**
     * Print message when starting tests.
     */
    @BeforeClass
    public static void setUpClass() {
        System.out.println("# Testing BasicParallelSearch ...");
        SearchTestTemplate.setUpClass();
    }

    /**
     * Print message when tests are complete.
     */
    @AfterClass
    public static void tearDownClass() {
        System.out.println("# Done testing BasicParallelSearch!");
    }
    
    @Override
    @Before
    public void setUp(){
        // call super
        super.setUp();
        // create subsearches
        subsearches = new ArrayList<>();
        subsearches.add(new RandomDescent<>(problem, neigh));               // random descent
        subsearches.add(new SteepestDescent<>(problem, neigh));             // steepest descent
        subsearches.add(new RandomSearch<>(problem));                       // random search
        subsearches.add(new MetropolisSearch<>(problem, neigh, 0.001));     // Metropolis search
        subsearches.add(new ExhaustiveSearch<>(problem,
                new SubsetSolutionIterator(data.getIDs(), SUBSET_SIZE)));   // exhaustive search
        subsearches.add(new ParallelTempering<>(problem, neigh,
                                                5, 0.00001, 10.0));         // parallel tempering (parallel inside parallel)
        // create parallel search
        parallelSearch = new BasicParallelSearch<>(problem);
        // add subsearches
        for(Search<SubsetSolution> s : subsearches){
            parallelSearch.addSearch(s);
        }
    }
    
    @After
    public void tearDown(){
        // dispose search
        if(parallelSearch.getStatus() == SearchStatus.IDLE){
            parallelSearch.dispose();
        }
    }
    
    /**
     * Test add search.
     */
    @Test(expected = SearchException.class)
    public void testAddSearch() {
        System.out.println(" - test addSearch");
        // try to add a search that solves a different problem
        Problem<SubsetSolution> p = new AbstractProblem<SubsetSolution, ScoredFakeSubsetData>(obj, data) {
            @Override
            public SubsetSolution createRandomSolution() {return null;}
        };
        parallelSearch.addSearch(new RandomSearch<>(p));
    }
    
    /**
     * Test add search.
     */
    @Test
    public void testAddSearch2() {
        System.out.println(" - test addSearch 2");
        Search<SubsetSolution> s = new RandomSearch<>(problem);
        parallelSearch.addSearch(s);
        // verify
        assertTrue(parallelSearch.getSearches().contains(s));
    }
    
    /**
     * Test remove search.
     */
    @Test
    public void testRemoveSearch() {
        System.out.println(" - test removeSearch");
        for(Search<SubsetSolution> s : subsearches){
            parallelSearch.removeSearch(s);
        }
        // verify
        assertTrue(parallelSearch.getSearches().isEmpty());
    }
    
    @Test(expected=SearchException.class)
    public void testNoSearches(){
        System.out.println(" - test without subsearches (initialization should fail)");
        subsearches.forEach(parallelSearch::removeSearch);
        parallelSearch.start();
    }
    
    @Test
    public void testGetSearches(){
        System.out.println(" - test getSearches");
        assertEquals(subsearches, parallelSearch.getSearches());
        boolean thrown = false;
        try {
            parallelSearch.getSearches().clear();
        } catch (UnsupportedOperationException ex) {
            thrown = true;
        }
        assertTrue(thrown);
    }
    
    @Test
    public void testInterruptSubsearchExecution(){
        System.out.println(" - test interrupt subsearch execution");
        
        // schedule task to interrupt the thread that runs the main search
        // while it is waiting for the subsearches to complete
        final Thread thr = Thread.currentThread();
        DelayedExecution.schedule(() -> thr.interrupt(), 500);
        
        boolean thrown = false;
        try{
            parallelSearch.start(); // keeps running (no stop criteria)
        } catch(SearchException ex){
            thrown = true;
        }
        assertTrue(thrown);
    }
    
    @Test
    public void stopDuringInitialization(){
        
        System.out.println(" - test stop during initialization");
       
        SearchListener<Solution> l = new SearchListener<Solution>() {
            @Override
            public void searchStarted(Search s){
                s.stop();
            }
        };
        parallelSearch.addSearchListener(l);
        
        // run without stop criteria: should still stop as subsearches should never start running
        parallelSearch.start();
                
    }
    
    /**
     * Test dispose.
     */
    @Test
    public void testDispose() {
        System.out.println(" - test dispose");
        // dispose main search
        parallelSearch.dispose();
        // check that subsearches have also been disposed
        for(Search<SubsetSolution> s : subsearches){
            assertEquals(SearchStatus.DISPOSED, s.getStatus());
        }
    }

    /**
     * Test single run.
     */
    @Test
    public void testSingleRun() {
        System.out.println(" - test single run");
        // single run
        singleRunWithMaxRuntime(parallelSearch, SINGLE_RUN_RUNTIME, MAX_RUNTIME_TIME_UNIT);
        // verify
        for(Search<SubsetSolution> s : subsearches){
            assertTrue(DoubleComparatorWithPrecision.smallerThanOrEqual(
                    s.getBestSolutionEvaluation().getValue(), 
                    parallelSearch.getBestSolutionEvaluation().getValue(), 
                    TestConstants.DOUBLE_COMPARISON_PRECISION)
            );
        }
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
        singleRunWithMaxRuntime(parallelSearch, SINGLE_RUN_RUNTIME, MAX_RUNTIME_TIME_UNIT);
        // verify
        assertNull(parallelSearch.getBestSolution());
        for(Search<SubsetSolution> s : subsearches){
            assertNull(s.getBestSolution());
        }
    }
    
    /**
     * Test single run with unsatisfiable penalizing constraint.
     */
    @Test
    public void testSingleRunWithUnsatisfiablePenalizingConstraint() {
        System.out.println(" - test single run with unsatisfiable penalizing constraint");
        // set constraint
        final double penalty = 7.8;
        problem.addPenalizingConstraint(new NeverSatisfiedPenalizingConstraintStub(penalty));
        // single run
        singleRunWithMaxRuntime(parallelSearch, SINGLE_RUN_RUNTIME, MAX_RUNTIME_TIME_UNIT);
        // verify
        PenalizedEvaluation penEval = (PenalizedEvaluation) parallelSearch.getBestSolutionEvaluation();
        assertEquals(penalty, penEval.getEvaluation().getValue() - penEval.getValue(), TestConstants.DOUBLE_COMPARISON_PRECISION);
    }
    
    /**
     * Test subsequent runs (maximizing).
     */
    @Test
    public void testSubsequentRuns() {
        System.out.println(" - test subsequent runs (maximizing)");
        // perform multiple runs (maximizing objective)
        multiRunWithMaximumRuntime(parallelSearch, MULTI_RUN_RUNTIME, MAX_RUNTIME_TIME_UNIT, NUM_RUNS, true, true);
        // verify
        for(Search<SubsetSolution> s : subsearches){
            assertTrue(DoubleComparatorWithPrecision.smallerThanOrEqual(
                    s.getBestSolutionEvaluation().getValue(), 
                    parallelSearch.getBestSolutionEvaluation().getValue(), 
                    TestConstants.DOUBLE_COMPARISON_PRECISION)
            );
        }
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
        multiRunWithMaximumRuntime(parallelSearch, MULTI_RUN_RUNTIME, MAX_RUNTIME_TIME_UNIT, NUM_RUNS, false, true);
        // verify
        for(Search<SubsetSolution> s : subsearches){
            assertTrue(DoubleComparatorWithPrecision.greaterThanOrEqual(
                    s.getBestSolutionEvaluation().getValue(), 
                    parallelSearch.getBestSolutionEvaluation().getValue(), 
                    TestConstants.DOUBLE_COMPARISON_PRECISION)
            );
        }
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
        multiRunWithMaximumRuntime(parallelSearch, MULTI_RUN_RUNTIME, MAX_RUNTIME_TIME_UNIT, NUM_RUNS, true, true);
        // verify
        assertNull(parallelSearch.getBestSolution());
        // verify
        for(Search<SubsetSolution> s : subsearches){
            assertNull(s.getBestSolution());
        }
    }
    
    /**
     * Test subsequent runs with unsatisfiable penalizing constraint.
     */
    @Test
    public void testSubsequentRunsWithUnsatisfiablePenalizingConstraint() {
        System.out.println(" - test subsequent runs with unsatisfiable penalizing constraint");
        // set constraint
        final double penalty = 7.8;
        problem.addPenalizingConstraint(new NeverSatisfiedPenalizingConstraintStub(penalty));
        // perform multiple runs (maximizing objective)
        multiRunWithMaximumRuntime(parallelSearch, MULTI_RUN_RUNTIME, MAX_RUNTIME_TIME_UNIT, NUM_RUNS, true, true);
        // verify
        PenalizedEvaluation penEval = (PenalizedEvaluation) parallelSearch.getBestSolutionEvaluation();
        assertEquals(penalty, penEval.getEvaluation().getValue() - penEval.getValue(), TestConstants.DOUBLE_COMPARISON_PRECISION);
        for(Search<SubsetSolution> s : subsearches){
            penEval = (PenalizedEvaluation) s.getBestSolutionEvaluation();
            assertEquals(penalty, penEval.getEvaluation().getValue() - penEval.getValue(), TestConstants.DOUBLE_COMPARISON_PRECISION);
        }
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
        multiRunWithMaximumRuntime(parallelSearch, MULTI_RUN_RUNTIME, MAX_RUNTIME_TIME_UNIT, 3*NUM_RUNS, true, true);
        System.out.println("   >>> best: " + parallelSearch.getBestSolutionEvaluation());
        // constraint satisfied ?
        if(problem.getViolatedConstraints(parallelSearch.getBestSolution()).isEmpty()){
            System.out.println("   >>> constraint satisfied!");
        } else {
            System.out.println("   >>> constraint not satisfied, penalty "
                    + constraint.validate(parallelSearch.getBestSolution(), data).getPenalty());
        }
        // verify
        for(Search<SubsetSolution> s : subsearches){
            assertTrue(DoubleComparatorWithPrecision.smallerThanOrEqual(
                    s.getBestSolutionEvaluation().getValue(), 
                    parallelSearch.getBestSolutionEvaluation().getValue(), 
                    TestConstants.DOUBLE_COMPARISON_PRECISION)
            );
        }
    }

}