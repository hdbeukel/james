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

package org.jamesframework.core.search.stopcriteria;

import java.util.concurrent.TimeUnit;
import org.jamesframework.core.subset.SubsetSolution;
import org.jamesframework.core.search.Search;
import org.jamesframework.core.search.SearchTestTemplate;
import org.jamesframework.core.search.algo.RandomDescent;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

/**
 * Test minimum delta stop criterion.
 * 
 * @author <a href="mailto:herman.debeukelaer@ugent.be">Herman De Beukelaer</a>
 */
public class MinDeltaTest extends SearchTestTemplate {

    // search to work with (random descent)
    private Search<SubsetSolution> search;
    
    // minimum delta
    private final double MIN_DELTA = 0.01;
    // max runtime
    private final long MAX_RUNTIME = 10;
    private final TimeUnit MAX_RUNTIME_TIME_UNIT = TimeUnit.SECONDS;
    
    // short check period
    private final long SHORT_CHECK_PERIOD = 1;
    private final TimeUnit CHECK_PERIOD_TIME_UNIT = TimeUnit.MILLISECONDS;
    
    /**
     * Print message when starting tests.
     */
    @BeforeClass
    public static void setUpClass() {
        System.out.println("# Testing MinDelta ...");
        SearchTestTemplate.setUpClass();
    }

    /**
     * Print message when tests are complete.
     */
    @AfterClass
    public static void tearDownClass() {
        System.out.println("# Done testing MinDelta!");
    }
    
    @Override
    @Before
    public void setUp(){
        // call super
        super.setUp();
        // create random descent search
        search = new RandomDescent<>(problem, neigh);
    }
    
    @Test(expected = IllegalArgumentException.class)
    public void testConstructor1(){
        System.out.println(" - test constructor (1)");
        new MinDelta(0.0);
    }
    
    @Test(expected = IllegalArgumentException.class)
    public void testConstructor2(){
        System.out.println(" - test constructor (2)");
        new MinDelta(-0.0000000000001);
    }

    /**
     * Test minimum delta stop criterion.
     */
    @Test
    public void testMinDelta() {
        
        System.out.println(" - test single run (default check period)");
        System.out.println("   >>> min required delta: " + MIN_DELTA);
        
        // add stop criterion to search
        search.addStopCriterion(new MinDelta(MIN_DELTA));
        // also add maximum runtime stop criterion as min delta might not be obtainable
        search.addStopCriterion(new MaxRuntime(MAX_RUNTIME, MAX_RUNTIME_TIME_UNIT));
        
        // run search (should stop)
        search.start();
        
        System.out.println("   >>> min observed delta: " + search.getMinDelta());
        System.out.println("   >>> best evaluation: " + search.getBestSolutionEvaluation());
        
    }
    
    /**
     * Test minimum delta stop criterion with short check period.
     */
    @Test
    public void testMinDeltaWithShortCheckPeriod() {
        
        System.out.format(" - test single run (check period = %d ms)\n", CHECK_PERIOD_TIME_UNIT.toMillis(SHORT_CHECK_PERIOD));
        System.out.println("   >>> min required delta: " + MIN_DELTA);
        
        // add stop criterion to search
        search.addStopCriterion(new MinDelta(MIN_DELTA));
        // set short check period
        search.setStopCriterionCheckPeriod(SHORT_CHECK_PERIOD, CHECK_PERIOD_TIME_UNIT);
        // also add maximum runtime stop criterion as min delta might not be obtainable
        search.addStopCriterion(new MaxRuntime(MAX_RUNTIME, MAX_RUNTIME_TIME_UNIT));
        
        // run search (should stop)
        search.start();
        
        System.out.println("   >>> min observed delta: " + search.getMinDelta());
        System.out.println("   >>> best evaluation: " + search.getBestSolutionEvaluation());
        
    }
    
    /**
     * Test minimum delta stop criterion (minimizing).
     */
    @Test
    public void testMinDeltaMinimizing() {
        
        System.out.println(" - test single run (default check period, minimizing)");
        System.out.println("   >>> min required delta: " + MIN_DELTA);
        
        // set minimizing
        obj.setMinimizing();
        // add stop criterion to search
        search.addStopCriterion(new MinDelta(MIN_DELTA));
        // also add maximum runtime stop criterion as min delta might not be obtainable
        search.addStopCriterion(new MaxRuntime(MAX_RUNTIME, MAX_RUNTIME_TIME_UNIT));
        
        // run search (should stop)
        search.start();
        
        System.out.println("   >>> min observed delta: " + search.getMinDelta());
        System.out.println("   >>> best evaluation: " + search.getBestSolutionEvaluation());
        
    }
    
    /**
     * Test minimum delta stop criterion with short check period (minimizing).
     */
    @Test
    public void testMinDeltaWithShortCheckPeriodMinimizing() {
        
        System.out.format(" - test single run (check period = %d ms, minimizing)\n", CHECK_PERIOD_TIME_UNIT.toMillis(SHORT_CHECK_PERIOD));
        System.out.println("   >>> min required delta: " + MIN_DELTA);
        
        // set minimizing
        obj.setMinimizing();
        // add stop criterion to search
        search.addStopCriterion(new MinDelta(MIN_DELTA));
        // set short check period
        search.setStopCriterionCheckPeriod(SHORT_CHECK_PERIOD, CHECK_PERIOD_TIME_UNIT);
        // also add maximum runtime stop criterion as min delta might not be obtainable
        search.addStopCriterion(new MaxRuntime(MAX_RUNTIME, MAX_RUNTIME_TIME_UNIT));
        
        // run search (should stop)
        search.start();
        
        System.out.println("   >>> min observed delta: " + search.getMinDelta());
        System.out.println("   >>> best evaluation: " + search.getBestSolutionEvaluation());
        
    }

}