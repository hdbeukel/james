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
import org.jamesframework.core.search.NeighbourhoodSearch;
import org.jamesframework.core.search.Search;
import org.jamesframework.core.search.SearchTestTemplate;
import org.jamesframework.core.search.listeners.EmptyNeighbourhoodSearchListener;
import org.jamesframework.test.util.NeverSatisfiedConstraintStub;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import static org.junit.Assert.*;
import org.junit.Before;

/**
 * Test parallel tempering algorithm.
 * 
 * @author Herman De Beukelaer <herman.debeukelaer@ugent.be>
 */
public class ParallelTemperingTest extends SearchTestTemplate {

    // parallel tempering search
    private ParallelTempering<SubsetSolution> search;
    
    // number of replicas
    private final int numReplicas = 10;
    
    // minium and maximum temperatures
    private final double MIN_TEMP = 50.0;
    private final double MAX_TEMP = 200.0;
    // scale factor
    private final double scale = 1e-6;
    
    // maximum runtime
    private final long SINGLE_RUN_RUNTIME = 1000;
    private final long MULTI_RUN_RUNTIME = 100;
    private final TimeUnit MAX_RUNTIME_TIME_UNIT = TimeUnit.MILLISECONDS;
    
    // number of runs in multi run tests
    private final int NUM_RUNS = 10;
    
    /**
     * Print message when starting tests.
     */
    @BeforeClass
    public static void setUpClass() {
        System.out.println("# Testing ParallelTempering ...");
        SearchTestTemplate.setUpClass();
    }

    /**
     * Print message when tests are complete.
     */
    @AfterClass
    public static void tearDownClass() {
        System.out.println("# Done testing ParallelTempering!");
    }
    
    @Override
    @Before
    public void setUp(){
        // call super
        super.setUp();
        // create parallel tempering search
        search = new ParallelTempering<>(problem, neigh, numReplicas, MIN_TEMP, MAX_TEMP);
        // set temperature scale
        search.setTemperatureScaleFactor(scale);
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

}