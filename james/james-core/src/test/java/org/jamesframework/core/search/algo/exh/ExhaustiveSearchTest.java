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

package org.jamesframework.core.search.algo.exh;

import java.util.Arrays;
import java.util.concurrent.TimeUnit;
import org.jamesframework.core.problems.SubsetProblem;
import org.jamesframework.core.problems.solutions.SubsetSolution;
import org.jamesframework.core.search.SearchTestTemplate;
import org.jamesframework.test.fakes.ScoredFakeSubsetData;
import org.jamesframework.test.fakes.SumOfScoresFakeSubsetObjective;
import org.jamesframework.test.stubs.NeverSatisfiedConstraintStub;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import static org.junit.Assert.*;
import org.junit.Before;

/**
 * Test exhaustive search algorithm.
 * 
 * @author <a href="mailto:herman.debeukelaer@ugent.be">Herman De Beukelaer</a>
 */
public class ExhaustiveSearchTest extends SearchTestTemplate {

    // exhaustive algorithms
    private ExhaustiveSearch<SubsetSolution> search;
    private ExhaustiveSearch<SubsetSolution> searchSmall;
    
    // subset solution iterator
    private SubsetSolutionIterator solutionIterator;
    private SubsetSolutionIterator solutionIteratorSmall;
    
    // maximum runtime
    private final long SINGLE_RUN_RUNTIME = 3000;
    private final long MULTI_RUN_RUNTIME = 250;
    private final TimeUnit MAX_RUNTIME_TIME_UNIT = TimeUnit.MILLISECONDS;
    
    // number of runs in multi-run tests
    private static final int NUM_RUNS = 10;
    
    // fake subset data (scored entities)
    protected static ScoredFakeSubsetData dataSmall;
    // dataset size
    protected static final int DATASET_SIZE_SMALL = 20;
    // entity scores
    protected static double[] scoresSmall;
    
    // fake objective (sum of scores)
    protected SumOfScoresFakeSubsetObjective objSmall;

    // subset problem to solve (select SUBSET_SIZE_SMALL out of DATASET_SIZE_SMALL)
    protected SubsetProblem<ScoredFakeSubsetData> problemSmall;
    protected static final int SUBSET_SIZE_SMALL = 10;
    
    /**
     * Print message when starting tests.
     */
    @BeforeClass
    public static void setUpClass() {
        System.out.println("# Testing ExhaustiveSearch ...");
        SearchTestTemplate.setUpClass();
        // create data for small problem
        scoresSmall = new double[DATASET_SIZE_SMALL];
        for(int i=0; i<DATASET_SIZE_SMALL; i++){
            scoresSmall[i] = RG.nextDouble();
        }
        // find best solution ignoring possible constraints (by sorting),
        // both for maximizing and minimizing setting
        double[] sorted = Arrays.copyOf(scoresSmall, scoresSmall.length);
        Arrays.sort(sorted);
        // compute maximum and minimum sum
        double max = 0.0, min = 0.0;
        for(int i=0; i<SUBSET_SIZE_SMALL; i++){
            min += sorted[i];
            max += sorted[sorted.length-i-1];
        }
        System.out.println("# Maximum small subset evaluation: " + max);
        System.out.println("# Minimum small subset evaluation: " + min);
    }

    /**
     * Print message when tests are complete.
     */
    @AfterClass
    public static void tearDownClass() {
        System.out.println("# Done testing ExhaustiveSearch!");
    }
    
    @Override
    @Before
    public void setUp(){
        // call super
        super.setUp();
        // create small problem components
        dataSmall = new ScoredFakeSubsetData(scoresSmall);
        objSmall = new SumOfScoresFakeSubsetObjective();
        problemSmall = new SubsetProblem<>(objSmall, dataSmall, SUBSET_SIZE_SMALL);
        // create solution iterators
        solutionIterator = new SubsetSolutionIterator(data.getIDs(), SUBSET_SIZE);
        solutionIteratorSmall = new SubsetSolutionIterator(dataSmall.getIDs(), SUBSET_SIZE_SMALL);
        // create exhaustive searches
        search = new ExhaustiveSearch<>(problem, solutionIterator);
        searchSmall = new ExhaustiveSearch<>(problemSmall, solutionIteratorSmall);
    }
    
    @After
    public void tearDown(){
        // dispose searches
        search.dispose();
        searchSmall.dispose();
    }

    /**
     * Test single run (small problem).
     */
    @Test
    public void testSingleRunSmall() {
        System.out.println(" - test single run (small problem)");
        // single run
        singleRunWithMaxRuntime(searchSmall, problemSmall, SINGLE_RUN_RUNTIME, MAX_RUNTIME_TIME_UNIT);
        // done ?
        if(!solutionIteratorSmall.hasNext()){
            System.out.print("   >>> evaluated all solutions! ");
        } else {
            System.out.print("   >>> did not evaluate all solutions ");
        }
        System.out.format(" (runtime = %d ms)\n", searchSmall.getRuntime());
    }
    
    /**
     * Test single run (large problem).
     */
    @Test
    public void testSingleRunLarge() {
        System.out.println(" - test single run (large problem)");
        // single run
        singleRunWithMaxRuntime(search, problem, SINGLE_RUN_RUNTIME, MAX_RUNTIME_TIME_UNIT);
        // done ?
        if(!solutionIterator.hasNext()){
            System.out.print("   >>> evaluated all solutions! ");
        } else {
            System.out.print("   >>> did not evaluate all solutions ");
        }
        System.out.format(" (runtime = %d ms)\n", search.getRuntime());
    }
    
    /**
     * Test single run with unsatisfiable constraint (small problem).
     */
    @Test
    public void testSingleRunWithUnsatisfiableConstraintSmall() {
        System.out.println(" - test single run with unsatisfiable constraint (small problem)");
        // add constraint
        problemSmall.addRejectingConstraint(new NeverSatisfiedConstraintStub());
        // single run
        singleRunWithMaxRuntime(searchSmall, problemSmall, SINGLE_RUN_RUNTIME, MAX_RUNTIME_TIME_UNIT);
        // done ?
        if(!solutionIteratorSmall.hasNext()){
            System.out.print("   >>> evaluated all solutions! ");
        } else {
            System.out.print("   >>> did not evaluate all solutions ");
        }
        System.out.format(" (runtime = %d ms)\n", searchSmall.getRuntime());
        // verify
        assertNull(searchSmall.getBestSolution());
    }
    
    /**
     * Test single run with unsatisfiable constraint (large problem).
     */
    @Test
    public void testSingleRunWithUnsatisfiableConstraintLarge() {
        System.out.println(" - test single run with unsatisfiable constraint (large problem)");
        // add constraint
        problem.addRejectingConstraint(new NeverSatisfiedConstraintStub());
        // single run
        singleRunWithMaxRuntime(search, problem, SINGLE_RUN_RUNTIME, MAX_RUNTIME_TIME_UNIT);
        // done ?
        if(!solutionIterator.hasNext()){
            System.out.print("   >>> evaluated all solutions! ");
        } else {
            System.out.print("   >>> did not evaluate all solutions ");
        }
        System.out.format(" (runtime = %d ms)\n", search.getRuntime());
        // verify
        assertNull(search.getBestSolution());
    }
    
    /**
     * Test subsequent runs (maximizing, small problem).
     */
    @Test
    public void testSubsequentRunsSmall() {
        System.out.println(" - test subsequent runs (maximizing, small problem)");
        // perform multiple runs (maximizing objective)
        multiRunWithMaximumRuntime(searchSmall, MULTI_RUN_RUNTIME, MAX_RUNTIME_TIME_UNIT, NUM_RUNS, true, true);
        // done ?
        if(!solutionIteratorSmall.hasNext()){
            System.out.println("   >>> evaluated all solutions! ");
        } else {
            System.out.println("   >>> did not evaluate all solutions ");
        }
    }
    
    /**
     * Test subsequent runs (maximizing, large problem).
     */
    @Test
    public void testSubsequentRunsLarge() {
        System.out.println(" - test subsequent runs (maximizing, large problem)");
        // perform multiple runs (maximizing objective)
        multiRunWithMaximumRuntime(search, MULTI_RUN_RUNTIME, MAX_RUNTIME_TIME_UNIT, NUM_RUNS, true, true);
        // done ?
        if(!solutionIterator.hasNext()){
            System.out.println("   >>> evaluated all solutions! ");
        } else {
            System.out.println("   >>> did not evaluate all solutions ");
        }
    }
    
    /**
     * Test subsequent runs (minimizing, small problem).
     */
    @Test
    public void testSubsequentRunsSmallMinimizing() {
        System.out.println(" - test subsequent runs (minimizing, small problem)");
        // set minimizing
        objSmall.setMinimizing();
        // perform multiple runs (minimizing objective)
        multiRunWithMaximumRuntime(searchSmall, MULTI_RUN_RUNTIME, MAX_RUNTIME_TIME_UNIT, NUM_RUNS, false, true);
        // done ?
        if(!solutionIteratorSmall.hasNext()){
            System.out.println("   >>> evaluated all solutions! ");
        } else {
            System.out.println("   >>> did not evaluate all solutions ");
        }
    }
    
    /**
     * Test subsequent runs (minimizing, large problem).
     */
    @Test
    public void testSubsequentRunsLargeMinimizing() {
        System.out.println(" - test subsequent runs (minimizing, large problem)");
        // set minimizing
        obj.setMinimizing();
        // perform multiple runs (minimizing objective)
        multiRunWithMaximumRuntime(search, MULTI_RUN_RUNTIME, MAX_RUNTIME_TIME_UNIT, NUM_RUNS, false, true);
        // done ?
        if(!solutionIterator.hasNext()){
            System.out.println("   >>> evaluated all solutions! ");
        } else {
            System.out.println("   >>> did not evaluate all solutions ");
        }
    }
    
    /**
     * Test subsequent runs with unsatisfiable constraint (small problem).
     */
    @Test
    public void testSubsequentRunsWithUnsatisfiableConstraintSmall() {
        System.out.println(" - test subsequent runs with unsatisfiable constraint (small problem)");
        // set constraint
        problemSmall.addRejectingConstraint(new NeverSatisfiedConstraintStub());
        // perform multiple runs (maximizing objective)
        multiRunWithMaximumRuntime(searchSmall, MULTI_RUN_RUNTIME, MAX_RUNTIME_TIME_UNIT, NUM_RUNS, true, true);
        // verify
        assertNull(searchSmall.getBestSolution());
        // done ?
        if(!solutionIteratorSmall.hasNext()){
            System.out.println("   >>> evaluated all solutions! ");
        } else {
            System.out.println("   >>> did not evaluate all solutions ");
        }
    }
    
    /**
     * Test subsequent runs with unsatisfiable constraint (large problem).
     */
    @Test
    public void testSubsequentRunsWithUnsatisfiableConstraintLarge() {
        System.out.println(" - test subsequent runs with unsatisfiable constraint (large problem)");
        // set constraint
        problem.addRejectingConstraint(new NeverSatisfiedConstraintStub());
        // perform multiple runs (maximizing objective)
        multiRunWithMaximumRuntime(search, MULTI_RUN_RUNTIME, MAX_RUNTIME_TIME_UNIT, NUM_RUNS, true, true);
        // verify
        assertNull(search.getBestSolution());
        // done ?
        if(!solutionIterator.hasNext()){
            System.out.println("   >>> evaluated all solutions! ");
        } else {
            System.out.println("   >>> did not evaluate all solutions ");
        }
    }
    
    /**
     * Test subsequent runs with penalizing constraint (small problem).
     */
    @Test
    public void testSubsequentRunsWithPenalizingConstraintSmall() {
        System.out.println(" - test subsequent runs with penalizing constraint (small problem)");
        // set constraint
        problemSmall.addPenalizingConstraint(constraint);
        // perform 3 times as many runs as usual for this harder problem (maximizing objective)
        multiRunWithMaximumRuntime(searchSmall, MULTI_RUN_RUNTIME, MAX_RUNTIME_TIME_UNIT, 3*NUM_RUNS, true, true);
        System.out.println("   >>> best: " + searchSmall.getBestSolutionEvaluation());
        // constraint satisfied ?
        if(problemSmall.getViolatedConstraints(searchSmall.getBestSolution()).isEmpty()){
            System.out.println("   >>> constraint satisfied!");
        } else {
            System.out.println("   >>> constraint not satisfied, penalty " + constraint.computePenalty(searchSmall.getBestSolution(), dataSmall));
        }
        // done ?
        if(!solutionIteratorSmall.hasNext()){
            System.out.println("   >>> evaluated all solutions! ");
        } else {
            System.out.println("   >>> did not evaluate all solutions ");
        }
    }
    
    /**
     * Test subsequent runs with penalizing constraint (large problem).
     */
    @Test
    public void testSubsequentRunsWithPenalizingConstraintLarge() {
        System.out.println(" - test subsequent runs with penalizing constraint (large problem)");
        // set constraint
        problem.addPenalizingConstraint(constraint);
        // perform 3 times as many runs as usual for this harder problem (maximizing objective)
        multiRunWithMaximumRuntime(search, MULTI_RUN_RUNTIME, MAX_RUNTIME_TIME_UNIT, 3*NUM_RUNS, true, true);
        System.out.println("   >>> best: " + search.getBestSolutionEvaluation());
        // constraint satisfied ?
        if(problem.getViolatedConstraints(search.getBestSolution()).isEmpty()){
            System.out.println("   >>> constraint satisfied!");
        } else {
            System.out.println("   >>> constraint not satisfied, penalty " + constraint.computePenalty(search.getBestSolution(), data));
        }
        // done ?
        if(!solutionIterator.hasNext()){
            System.out.println("   >>> evaluated all solutions! ");
        } else {
            System.out.println("   >>> did not evaluate all solutions ");
        }
    }

}