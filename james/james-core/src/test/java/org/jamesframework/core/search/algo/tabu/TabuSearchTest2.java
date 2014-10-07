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

package org.jamesframework.core.search.algo.tabu;

import java.util.concurrent.TimeUnit;
import org.jamesframework.core.subset.SubsetSolution;
import org.jamesframework.core.search.SearchTestTemplate;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.Before;

/**
 * Test tabu search when all moves are declared tabu. Should lead to the same behaviour as a basic steepest descent
 * search because of the built-in aspiration criterion that overrides tabu for moves that yield a new best solution.
 * For the considered 1-opt test problem this means that the optimum should still be found.
 * 
 * @author <a href="mailto:herman.debeukelaer@ugent.be">Herman De Beukelaer</a>
 */
public class TabuSearchTest2 extends SearchTestTemplate {
    
    // tabu search
    private TabuSearch<SubsetSolution> search;
    
    // maximum runtime
    private final long SINGLE_RUN_RUNTIME = 1000;
    private final TimeUnit MAX_RUNTIME_TIME_UNIT = TimeUnit.MILLISECONDS;
    
    /**
     * Print message when starting tests.
     */
    @BeforeClass
    public static void setUpClass() {
        System.out.println("# Testing TabuSearch (2) ...");
        SearchTestTemplate.setUpClass();
    }

    /**
     * Print message when tests are complete.
     */
    @AfterClass
    public static void tearDownClass() {
        System.out.println("# Done testing TabuSearch (2)!");
    }
    
    @Override
    @Before
    public void setUp(){
        // call super
        super.setUp();
        // create tabu search with all moves declared tabu
        search = new TabuSearch<>(problem, neigh, new RejectAllTabuMemory<>());
    }
    
    @After
    public void tearDown(){
        // dispose search
        search.dispose();
    }
    
    /**
     * Test with all moves declared tabu.
     */
    @Test
    public void testWithAllMovesTabu() {
        System.out.println(" - test with all moves tabu (~ steepest descent)");
        // single run
        singleRunWithMaxRuntime(search, problem, SINGLE_RUN_RUNTIME, MAX_RUNTIME_TIME_UNIT);
    }
    
    /**
     * Test minimizing with all moves declared tabu.
     */
    @Test
    public void testMinimizingWithAllMovesTabu() {
        System.out.println(" - test with all moves tabu, minimizing (~ steepest descent)");
        // set minimizing
        obj.setMinimizing();
        // single run
        singleRunWithMaxRuntime(search, problem, SINGLE_RUN_RUNTIME, MAX_RUNTIME_TIME_UNIT);
    }

}