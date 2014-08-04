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
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.TimeUnit;
import org.jamesframework.core.problems.Problem;
import org.jamesframework.core.problems.SubsetProblemWithData;
import org.jamesframework.core.problems.solutions.SubsetSolution;
import org.jamesframework.core.search.LocalSearch;
import org.jamesframework.core.search.Search;
import org.jamesframework.core.search.SearchTestTemplate;
import org.jamesframework.core.search.neigh.subset.adv.MultiSwapNeighbourhood;
import org.jamesframework.test.fakes.ScoredFakeSubsetData;
import org.jamesframework.test.stubs.FixedEvaluationObjectiveStub;
import org.jamesframework.test.stubs.NeverSatisfiedConstraintStub;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import static org.junit.Assert.*;
import org.junit.Before;

/**
 * Test piped local search algorithm.
 * 
 * @author <a href="mailto:herman.debeukelaer@ugent.be">Herman De Beukelaer</a>
 */
public class PipedLocalSearchTest extends SearchTestTemplate {

    // piped local search
    private PipedLocalSearch<SubsetSolution> pipedLocalSearch;
    
    // maximum runtime
    private final long SINGLE_RUN_RUNTIME = 500;
    private final TimeUnit MAX_RUNTIME_TIME_UNIT = TimeUnit.MILLISECONDS;
    
    /**
     * Print message when starting tests.
     */
    @BeforeClass
    public static void setUpClass() {
        System.out.println("# Testing PipedLocalSearch ...");
        SearchTestTemplate.setUpClass();
    }

    /**
     * Print message when tests are complete.
     */
    @AfterClass
    public static void tearDownClass() {
        System.out.println("# Done testing PipedLocalSearch!");
    }
    
    @Override
    @Before
    public void setUp(){
        // call super
        super.setUp();
        // create steepest descent with single swap neighbourhood
        LocalSearch<SubsetSolution> sd = new SteepestDescent<>(problem, neigh);
        // create random descent with multi swap neighbourhood
        LocalSearch<SubsetSolution> rd = new RandomDescent<>(problem, new MultiSwapNeighbourhood(10));
        // put both searches in pipeline
        List<LocalSearch<SubsetSolution>> pipeline = new ArrayList<>();
        pipeline.add(sd);
        pipeline.add(rd);
        // create piped local search
        pipedLocalSearch = new PipedLocalSearch<>(problem, pipeline);
    }
    
    @After
    public void tearDown(){
        // dispose search
        pipedLocalSearch.dispose();
    }

    /**
     * Test constructor.
     */
    @Test(expected = NullPointerException.class)
    public void testConstructor1() {
        System.out.println(" - test constructor 1");
        // try with pipeline null
        Search<?> garbage = new PipedLocalSearch<>(problem, null);
    }
    
    /**
     * Test constructor.
     */
    @Test(expected = NullPointerException.class)
    public void testConstructor2() {
        System.out.println(" - test constructor 2");
        // try with problem null
        Search<SubsetSolution> garbage = new PipedLocalSearch<>(null, new ArrayList<LocalSearch<SubsetSolution>>());
    }
    
    /**
     * Test constructor.
     */
    @Test(expected = NullPointerException.class)
    public void testConstructor3() {
        System.out.println(" - test constructor 3");
        // try with pipeline containing null elements
        Search<SubsetSolution> garbage = new PipedLocalSearch<>(problem, Arrays.asList((LocalSearch<SubsetSolution>) null));
    }
    
    /**
     * Test constructor.
     */
    @Test(expected = IllegalArgumentException.class)
    public void testConstructor4() {
        System.out.println(" - test constructor 4");
        // try with empty pipeline
        Search<SubsetSolution> garbage = new PipedLocalSearch<>(problem, new ArrayList<LocalSearch<SubsetSolution>>());
    }
    
    /**
     * Test constructor.
     */
    @Test(expected = IllegalArgumentException.class)
    public void testConstructor5() {
        System.out.println(" - test constructor 5");
        // try with search in pipeline that solves a different problem
        SubsetProblemWithData<ScoredFakeSubsetData> problem2 = new SubsetProblemWithData<>(
                                                                    new FixedEvaluationObjectiveStub(7.0),
                                                                    data,
                                                                    DATASET_SIZE
                                                                );
        List<LocalSearch<SubsetSolution>> pipeline = new ArrayList<>();
        pipeline.add(new RandomDescent<>(problem2, neigh));
        Search<SubsetSolution> garbage = new PipedLocalSearch<>(problem, pipeline);
    }
    
    /**
     * Test single run.
     */
    @Test
    public void testSingleRun() {
        System.out.println(" - test single run");
        // single run
        singleRunWithMaxRuntime(pipedLocalSearch, problem, SINGLE_RUN_RUNTIME, MAX_RUNTIME_TIME_UNIT);
    }
    
    /**
     * Test single run (minimizing).
     */
    @Test
    public void testSingleRunMinimizing() {
        System.out.println(" - test single run (minimizing)");
        // set minimizing
        obj.setMinimizing();
        // single run
        singleRunWithMaxRuntime(pipedLocalSearch, problem, SINGLE_RUN_RUNTIME, MAX_RUNTIME_TIME_UNIT);
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
        singleRunWithMaxRuntime(pipedLocalSearch, problem, SINGLE_RUN_RUNTIME, MAX_RUNTIME_TIME_UNIT);
        // verify
        assertNull(pipedLocalSearch.getBestSolution());
    }
    
    /**
     * Test with dummy local search as first search.
     */
    @Test
    public void testWithDummy() {
        System.out.println(" - test with dummy (NOP) local search as first search");
        // create pipeline with dummy local search as first search and steepest descent as second
        LocalSearch<SubsetSolution> dummy = new DummyLocalSearch(problem);
        LocalSearch<SubsetSolution> sd = new SteepestDescent<>(problem, neigh);
        List<LocalSearch<SubsetSolution>> pipeline = new ArrayList<>();
        pipeline.add(dummy);
        pipeline.add(sd);
        pipedLocalSearch = new PipedLocalSearch<>(problem, pipeline);
        // single run
        singleRunWithMaxRuntime(pipedLocalSearch, problem, SINGLE_RUN_RUNTIME, MAX_RUNTIME_TIME_UNIT);
    }
    
    /**
     * Test with several never ending random descent searches in the pipeline.
     */
    @Test
    public void testNeverEnding() {
        System.out.println(" - test with 10 never ending random descent searches in pipeline");
        // create pipeline with 10 never ending local searches
        List<LocalSearch<SubsetSolution>> pipeline = new ArrayList<>();
        for(int i=0; i<10; i++){
            pipeline.add(new RandomDescent<>(problem, neigh));
        }
        pipedLocalSearch = new PipedLocalSearch<>(problem, pipeline);
        // single run
        singleRunWithMaxRuntime(pipedLocalSearch, problem, SINGLE_RUN_RUNTIME, MAX_RUNTIME_TIME_UNIT);
    }
    
    
    
    /**
     * Dummy local search that does not do anything but immediately stops its current run. This dummy is used to
     * check whether the second search in the pipeline is correctly executed given that the first search did
     * not already find the optimum.
     */
    private class DummyLocalSearch extends LocalSearch<SubsetSolution>{

        public DummyLocalSearch(Problem<SubsetSolution> problem){
            super(problem);
        }
        
        @Override
        protected void searchStep() {
            stop();
        }
    
    }

}