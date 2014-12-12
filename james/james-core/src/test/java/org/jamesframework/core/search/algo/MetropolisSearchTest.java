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

import java.util.Collections;
import java.util.List;
import java.util.concurrent.TimeUnit;
import org.jamesframework.core.problems.objectives.evaluations.PenalizedEvaluation;
import org.jamesframework.core.subset.SubsetSolution;
import org.jamesframework.core.search.NeighbourhoodSearch;
import org.jamesframework.core.search.Search;
import org.jamesframework.core.search.SearchTestTemplate;
import org.jamesframework.core.search.listeners.SearchListener;
import org.jamesframework.core.search.neigh.Move;
import org.jamesframework.core.search.neigh.Neighbourhood;
import org.jamesframework.test.stubs.NeverSatisfiedConstraintStub;
import org.jamesframework.test.stubs.NeverSatisfiedPenalizingConstraintStub;
import org.jamesframework.test.util.TestConstants;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import static org.junit.Assert.*;
import org.junit.Before;

/**
 * Test Metropolis search algorithm.
 * 
 * @author <a href="mailto:herman.debeukelaer@ugent.be">Herman De Beukelaer</a>
 */
public class MetropolisSearchTest extends SearchTestTemplate {

    // Metropolis searches
    private MetropolisSearch<SubsetSolution> searchLowTemp;
    private MetropolisSearch<SubsetSolution> searchMedTemp;
    private MetropolisSearch<SubsetSolution> searchHighTemp;
    
    // temperatures
    private final double LOW_TEMP  = 0.0000001;
    private final double MED_TEMP  = 0.001;
    private final double HIGH_TEMP = 10.0;
    
    // maximum runtime
    private final long SINGLE_RUN_RUNTIME = 500;
    private final long MULTI_RUN_RUNTIME = 20;
    private final TimeUnit MAX_RUNTIME_TIME_UNIT = TimeUnit.MILLISECONDS;
    
    // number of runs in multi run tests
    private final int NUM_RUNS = 5;
    
    /**
     * Print message when starting tests.
     */
    @BeforeClass
    public static void setUpClass() {
        System.out.println("# Testing MetropolisSearch ...");
        SearchTestTemplate.setUpClass();
    }

    /**
     * Print message when tests are complete.
     */
    @AfterClass
    public static void tearDownClass() {
        System.out.println("# Done testing MetropolisSearch!");
    }
    
    @Override
    @Before
    public void setUp(){
        // call super
        super.setUp();
        // create Metropolis searches
        searchLowTemp = new MetropolisSearch<>(problem, neigh, LOW_TEMP);
        searchMedTemp = new MetropolisSearch<>(problem, neigh, MED_TEMP);
        searchHighTemp = new MetropolisSearch<>(problem, neigh, HIGH_TEMP);
    }
    
    @After
    public void tearDown(){
        // dispose searches
        searchLowTemp.dispose();
        searchMedTemp.dispose();
        searchHighTemp.dispose();
    }

    @Test
    public void testConstructor(){
        
        System.out.println(" - test constructor");
        
        boolean thrown;
                
        thrown = false;
        try {
            new MetropolisSearch<>(problem, neigh, 0.0);
        } catch (IllegalArgumentException ex){
            thrown = true;
        }
        assertTrue(thrown);
        
        thrown = false;
        try {
            new MetropolisSearch<>(problem, neigh, -1.0);
        } catch (IllegalArgumentException ex){
            thrown = true;
        }
        assertTrue(thrown);
        
    }
    
    @Test
    public void testSetTemperature(){
        System.out.println(" - test setTemperature");
        
        boolean thrown;
        
        thrown = false;
        try {
            searchLowTemp.setTemperature(0.0);
        } catch (IllegalArgumentException ex){
            thrown = true;
        }
        assertTrue(thrown);
        
        thrown = false;
        try {
            searchLowTemp.setTemperature(-1.0);
        } catch (IllegalArgumentException ex){
            thrown = true;
        }
        assertTrue(thrown);
        
        searchLowTemp.setTemperature(123.456);
        assertEquals(123.455, searchLowTemp.getTemperature(), TestConstants.DOUBLE_COMPARISON_PRECISION);
        
    }
    
    @Test
    public void testSetTemperatureScaleFactor(){
        System.out.println(" - test setTemperatureScaleFactor");
        
        boolean thrown;
        
        thrown = false;
        try {
            searchLowTemp.setTemperatureScaleFactor(0.0);
        } catch (IllegalArgumentException ex){
            thrown = true;
        }
        assertTrue(thrown);
        
        thrown = false;
        try {
            searchLowTemp.setTemperatureScaleFactor(-1.0);
        } catch (IllegalArgumentException ex){
            thrown = true;
        }
        assertTrue(thrown);
        
        searchLowTemp.setTemperatureScaleFactor(123.456);
        assertEquals(123.455, searchLowTemp.getTemperatureScaleFactor(), TestConstants.DOUBLE_COMPARISON_PRECISION);
        
    }
    
    /**
     * Test single run.
     */
    @Test
    public void testSingleRun() {
        System.out.println(" - test single run");
        // single run
        System.out.format("   - low temperature (T = %.7f)\n", LOW_TEMP);
        singleRunWithMaxRuntime(searchLowTemp, SINGLE_RUN_RUNTIME, MAX_RUNTIME_TIME_UNIT);
        System.out.format("   - medium temperature (T = %.7f)\n", MED_TEMP);
        singleRunWithMaxRuntime(searchMedTemp, SINGLE_RUN_RUNTIME, MAX_RUNTIME_TIME_UNIT);
        System.out.format("   - high temperature (T = %.7f)\n", HIGH_TEMP);
        singleRunWithMaxRuntime(searchHighTemp, SINGLE_RUN_RUNTIME, MAX_RUNTIME_TIME_UNIT);
    }
    
    @Test
    public void testEmptyNeighbourhood() {
        System.out.println(" - test with empty neighbourhood");
        // create empty neighbourhood
        Neighbourhood<SubsetSolution> emptyNeigh = new Neighbourhood<SubsetSolution>() {
            @Override
            public Move<? super SubsetSolution> getRandomMove(SubsetSolution solution) {
                return null;
            }
            @Override
            public List<? extends Move<? super SubsetSolution>> getAllMoves(SubsetSolution solution) {
                return Collections.emptyList();
            }
        };
        // set in searches
        searchLowTemp.setNeighbourhood(emptyNeigh);
        searchMedTemp.setNeighbourhood(emptyNeigh);
        searchHighTemp.setNeighbourhood(emptyNeigh);
        // run searches
        searchLowTemp.start();
        searchMedTemp.start();
        searchHighTemp.start();
        // verify: stopped after first step
        assertEquals(1, searchLowTemp.getSteps());
        assertEquals(1, searchMedTemp.getSteps());
        assertEquals(1, searchHighTemp.getSteps());
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
        System.out.format("   - low temperature (T = %.7f)\n", LOW_TEMP);
        singleRunWithMaxRuntime(searchLowTemp, SINGLE_RUN_RUNTIME, MAX_RUNTIME_TIME_UNIT);
        System.out.format("   - medium temperature (T = %.7f)\n", MED_TEMP);
        singleRunWithMaxRuntime(searchMedTemp, SINGLE_RUN_RUNTIME, MAX_RUNTIME_TIME_UNIT);
        System.out.format("   - high temperature (T = %.7f)\n", HIGH_TEMP);
        singleRunWithMaxRuntime(searchHighTemp, SINGLE_RUN_RUNTIME, MAX_RUNTIME_TIME_UNIT);
        // verify
        assertNull(searchLowTemp.getBestSolution());
        assertNull(searchMedTemp.getBestSolution());
        assertNull(searchHighTemp.getBestSolution());
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
        System.out.format("   - low temperature (T = %.7f)\n", LOW_TEMP);
        singleRunWithMaxRuntime(searchLowTemp, SINGLE_RUN_RUNTIME, MAX_RUNTIME_TIME_UNIT);
        System.out.format("   - medium temperature (T = %.7f)\n", MED_TEMP);
        singleRunWithMaxRuntime(searchMedTemp, SINGLE_RUN_RUNTIME, MAX_RUNTIME_TIME_UNIT);
        System.out.format("   - high temperature (T = %.7f)\n", HIGH_TEMP);
        singleRunWithMaxRuntime(searchHighTemp, SINGLE_RUN_RUNTIME, MAX_RUNTIME_TIME_UNIT);
        // verify
        PenalizedEvaluation penEval;
        penEval = (PenalizedEvaluation) searchLowTemp.getBestSolutionEvaluation();
        assertEquals(penalty, penEval.getEvaluation().getValue() - penEval.getValue(), TestConstants.DOUBLE_COMPARISON_PRECISION);
        penEval = (PenalizedEvaluation) searchMedTemp.getBestSolutionEvaluation();
        assertEquals(penalty, penEval.getEvaluation().getValue() - penEval.getValue(), TestConstants.DOUBLE_COMPARISON_PRECISION);
        penEval = (PenalizedEvaluation) searchHighTemp.getBestSolutionEvaluation();
        assertEquals(penalty, penEval.getEvaluation().getValue() - penEval.getValue(), TestConstants.DOUBLE_COMPARISON_PRECISION);
    }
    
    /**
     * Test subsequent runs (maximizing).
     */
    @Test
    public void testSubsequentRuns() {
        System.out.println(" - test subsequent runs (maximizing)");
        // create and add listeners
        AcceptedMovesListener l1 = new AcceptedMovesListener();
        AcceptedMovesListener l2 = new AcceptedMovesListener();
        AcceptedMovesListener l3 = new AcceptedMovesListener();
        searchLowTemp.addSearchListener(l1);
        searchMedTemp.addSearchListener(l2);
        searchHighTemp.addSearchListener(l3);
        // perform multiple runs (maximizing objective)
        System.out.format("   - low temperature (T = %.7f)\n", LOW_TEMP);
        multiRunWithMaximumRuntime(searchLowTemp, MULTI_RUN_RUNTIME, MAX_RUNTIME_TIME_UNIT, NUM_RUNS, true, true);
        System.out.format("   >>> accepted/rejected moves: %d/%d\n", l1.getTotalAcceptedMoves(), l1.getTotalRejectedMoves());
        System.out.format("   - medium temperature (T = %.7f)\n", MED_TEMP);
        multiRunWithMaximumRuntime(searchMedTemp, MULTI_RUN_RUNTIME, MAX_RUNTIME_TIME_UNIT, NUM_RUNS, true, true);
        System.out.format("   >>> accepted/rejected moves: %d/%d\n", l2.getTotalAcceptedMoves(), l2.getTotalRejectedMoves());
        System.out.format("   - high temperature (T = %.7f)\n", HIGH_TEMP);
        multiRunWithMaximumRuntime(searchHighTemp, MULTI_RUN_RUNTIME, MAX_RUNTIME_TIME_UNIT, NUM_RUNS, true, true);
        System.out.format("   >>> accepted/rejected moves: %d/%d\n", l3.getTotalAcceptedMoves(), l3.getTotalRejectedMoves());
    }
    
    /**
     * Test subsequent runs (minimizing).
     */
    @Test
    public void testSubsequentRunsMinimizing() {
        System.out.println(" - test subsequent runs (minimizing)");
        // set minimizing
        obj.setMinimizing();
        // create and add listeners
        AcceptedMovesListener l1 = new AcceptedMovesListener();
        AcceptedMovesListener l2 = new AcceptedMovesListener();
        AcceptedMovesListener l3 = new AcceptedMovesListener();
        searchLowTemp.addSearchListener(l1);
        searchMedTemp.addSearchListener(l2);
        searchHighTemp.addSearchListener(l3);
        // perform multiple runs
        System.out.format("   - low temperature (T = %.7f)\n", LOW_TEMP);
        multiRunWithMaximumRuntime(searchLowTemp, MULTI_RUN_RUNTIME, MAX_RUNTIME_TIME_UNIT, NUM_RUNS, false, true);
        System.out.format("   >>> accepted/rejected moves: %d/%d\n", l1.getTotalAcceptedMoves(), l1.getTotalRejectedMoves());
        System.out.format("   - medium temperature (T = %.7f)\n", MED_TEMP);
        multiRunWithMaximumRuntime(searchMedTemp, MULTI_RUN_RUNTIME, MAX_RUNTIME_TIME_UNIT, NUM_RUNS, false, true);
        System.out.format("   >>> accepted/rejected moves: %d/%d\n", l2.getTotalAcceptedMoves(), l2.getTotalRejectedMoves());
        System.out.format("   - high temperature (T = %.7f)\n", HIGH_TEMP);
        multiRunWithMaximumRuntime(searchHighTemp, MULTI_RUN_RUNTIME, MAX_RUNTIME_TIME_UNIT, NUM_RUNS, false, true);
        System.out.format("   >>> accepted/rejected moves: %d/%d\n", l3.getTotalAcceptedMoves(), l3.getTotalRejectedMoves());
    }
    
    /**
     * Test subsequent runs with unsatisfiable constraint.
     */
    @Test
    public void testSubsequentRunsWithUnsatisfiableConstraint() {
        System.out.println(" - test subsequent runs with unsatisfiable constraint");
        // set constraint
        problem.addMandatoryConstraint(new NeverSatisfiedConstraintStub());
        // create and add listeners
        AcceptedMovesListener l1 = new AcceptedMovesListener();
        AcceptedMovesListener l2 = new AcceptedMovesListener();
        AcceptedMovesListener l3 = new AcceptedMovesListener();
        searchLowTemp.addSearchListener(l1);
        searchMedTemp.addSearchListener(l2);
        searchHighTemp.addSearchListener(l3);
        // perform multiple runs (maximizing objective)
        System.out.format("   - low temperature (T = %.7f)\n", LOW_TEMP);
        multiRunWithMaximumRuntime(searchLowTemp, MULTI_RUN_RUNTIME, MAX_RUNTIME_TIME_UNIT, NUM_RUNS, true, true);
        System.out.format("   >>> accepted/rejected moves: %d/%d\n", l1.getTotalAcceptedMoves(), l1.getTotalRejectedMoves());
        System.out.format("   - medium temperature (T = %.7f)\n", MED_TEMP);
        multiRunWithMaximumRuntime(searchMedTemp, MULTI_RUN_RUNTIME, MAX_RUNTIME_TIME_UNIT, NUM_RUNS, true, true);
        System.out.format("   >>> accepted/rejected moves: %d/%d\n", l2.getTotalAcceptedMoves(), l2.getTotalRejectedMoves());
        System.out.format("   - high temperature (T = %.7f)\n", HIGH_TEMP);
        multiRunWithMaximumRuntime(searchHighTemp, MULTI_RUN_RUNTIME, MAX_RUNTIME_TIME_UNIT, NUM_RUNS, true, true);
        System.out.format("   >>> accepted/rejected moves: %d/%d\n", l3.getTotalAcceptedMoves(), l3.getTotalRejectedMoves());
        // verify
        assertNull(searchLowTemp.getBestSolution());
        assertNull(searchMedTemp.getBestSolution());
        assertNull(searchHighTemp.getBestSolution());
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
        // perform multiple runs (maximizing objective)
        System.out.format("   - low temperature (T = %.7f)\n", LOW_TEMP);
        multiRunWithMaximumRuntime(searchLowTemp, MULTI_RUN_RUNTIME, MAX_RUNTIME_TIME_UNIT, NUM_RUNS, true, true);
        System.out.format("   - medium temperature (T = %.7f)\n", MED_TEMP);
        multiRunWithMaximumRuntime(searchMedTemp, MULTI_RUN_RUNTIME, MAX_RUNTIME_TIME_UNIT, NUM_RUNS, true, true);
        System.out.format("   - high temperature (T = %.7f)\n", HIGH_TEMP);
        multiRunWithMaximumRuntime(searchHighTemp, MULTI_RUN_RUNTIME, MAX_RUNTIME_TIME_UNIT, NUM_RUNS, true, true);
        // verify
        PenalizedEvaluation penEval;
        penEval = (PenalizedEvaluation) searchLowTemp.getBestSolutionEvaluation();
        assertEquals(penalty, penEval.getEvaluation().getValue() - penEval.getValue(), TestConstants.DOUBLE_COMPARISON_PRECISION);
        penEval = (PenalizedEvaluation) searchMedTemp.getBestSolutionEvaluation();
        assertEquals(penalty, penEval.getEvaluation().getValue() - penEval.getValue(), TestConstants.DOUBLE_COMPARISON_PRECISION);
        penEval = (PenalizedEvaluation) searchHighTemp.getBestSolutionEvaluation();
        assertEquals(penalty, penEval.getEvaluation().getValue() - penEval.getValue(), TestConstants.DOUBLE_COMPARISON_PRECISION);
    }
    
    /**
     * Test subsequent runs with penalizing constraint.
     */
    @Test
    public void testSubsequentRunsWithPenalizingConstraint() {
        System.out.println(" - test subsequent runs with penalizing constraint");
        // set constraint
        problem.addPenalizingConstraint(constraint);
        // create and add listeners
        AcceptedMovesListener l1 = new AcceptedMovesListener();
        AcceptedMovesListener l2 = new AcceptedMovesListener();
        AcceptedMovesListener l3 = new AcceptedMovesListener();
        searchLowTemp.addSearchListener(l1);
        searchMedTemp.addSearchListener(l2);
        searchHighTemp.addSearchListener(l3);
        // perform 3 times as many runs as usual for this harder problem (maximizing objective)
        System.out.format("   - low temperature (T = %.7f)\n", LOW_TEMP);
        multiRunWithMaximumRuntime(searchLowTemp, MULTI_RUN_RUNTIME, MAX_RUNTIME_TIME_UNIT, 3*NUM_RUNS, true, true);
        System.out.format("   >>> accepted/rejected moves: %d/%d\n", l1.getTotalAcceptedMoves(), l1.getTotalRejectedMoves());
        // constraint satisfied ?
        if(problem.getViolatedConstraints(searchLowTemp.getBestSolution()).isEmpty()){
            System.out.println("   >>> constraint satisfied!");
        } else {
            System.out.println("   >>> constraint not satisfied, penalty "
                    + constraint.validate(searchLowTemp.getBestSolution(), data).getPenalty());
        }
        System.out.format("   - medium temperature (T = %.7f)\n", MED_TEMP);
        multiRunWithMaximumRuntime(searchMedTemp, MULTI_RUN_RUNTIME, MAX_RUNTIME_TIME_UNIT, 3*NUM_RUNS, true, true);
        System.out.format("   >>> accepted/rejected moves: %d/%d\n", l2.getTotalAcceptedMoves(), l2.getTotalRejectedMoves());
        // constraint satisfied ?
        if(problem.getViolatedConstraints(searchMedTemp.getBestSolution()).isEmpty()){
            System.out.println("   >>> constraint satisfied!");
        } else {
            System.out.println("   >>> constraint not satisfied, penalty "
                    + constraint.validate(searchMedTemp.getBestSolution(), data).getPenalty());
        }
        System.out.format("   - high temperature (T = %.7f)\n", HIGH_TEMP);
        multiRunWithMaximumRuntime(searchHighTemp, MULTI_RUN_RUNTIME, MAX_RUNTIME_TIME_UNIT, 3*NUM_RUNS, true, true);
        System.out.format("   >>> accepted/rejected moves: %d/%d\n", l3.getTotalAcceptedMoves(), l3.getTotalRejectedMoves());
        // constraint satisfied ?
        if(problem.getViolatedConstraints(searchHighTemp.getBestSolution()).isEmpty()){
            System.out.println("   >>> constraint satisfied!");
        } else {
            System.out.println("   >>> constraint not satisfied, penalty "
                    + constraint.validate(searchHighTemp.getBestSolution(), data).getPenalty());
        }
    }
    
    private class AcceptedMovesListener implements SearchListener<SubsetSolution>{
        // accepted/rejected moves over ALL runs
        private int accepted = 0, rejected = 0;
        
        @Override
        public void searchStopped(Search<? extends SubsetSolution> search){
            NeighbourhoodSearch<? extends SubsetSolution> neighSearch = (NeighbourhoodSearch<? extends SubsetSolution>) search;
            accepted += neighSearch.getNumAcceptedMoves();
            rejected += neighSearch.getNumRejectedMoves();
        }
        
        public int getTotalAcceptedMoves(){return accepted;}
        public int getTotalRejectedMoves(){return rejected;}
    }

}