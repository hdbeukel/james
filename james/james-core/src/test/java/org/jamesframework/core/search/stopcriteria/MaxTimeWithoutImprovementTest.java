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
import org.jamesframework.core.problems.objectives.evaluations.Evaluation;
import org.jamesframework.core.subset.SubsetSolution;
import org.jamesframework.core.search.Search;
import org.jamesframework.core.search.SearchTestTemplate;
import org.jamesframework.core.search.algo.RandomDescent;
import org.jamesframework.test.util.DoubleComparatorWithPrecision;
import org.jamesframework.test.util.TestConstants;
import org.junit.AfterClass;
import static org.junit.Assert.*;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

/**
 * Test maximum time without improvement stop criterion.
 * 
 * @author <a href="mailto:herman.debeukelaer@ugent.be">Herman De Beukelaer</a>
 */
public class MaxTimeWithoutImprovementTest extends SearchTestTemplate {

    // search to work with (random descent)
    private Search<SubsetSolution> search;
    
    // maximum time without improvement
    private final long MAX_TIME_WITHOUT_IMPR = 2345;
    private final long LOW_MAX_TIME_WITHOUT_IMPR = 20;
    private final TimeUnit TIME_UNIT = TimeUnit.MILLISECONDS;
    
    // coarse check period
    private final long COARSE_CHECK_PERIOD = 5;
    private final TimeUnit CHECK_PERIOD_TIME_UNIT = TimeUnit.SECONDS;
    
    /**
     * Print message when starting tests.
     */
    @BeforeClass
    public static void setUpClass() {
        System.out.println("# Testing MaxTimeWithoutImprovement ...");
        SearchTestTemplate.setUpClass();
    }

    /**
     * Print message when tests are complete.
     */
    @AfterClass
    public static void tearDownClass() {
        System.out.println("# Done testing MaxTimeWithoutImprovement!");
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
    public void testConstructor(){
        System.out.println(" - test constructor");
        new MaxTimeWithoutImprovement(999, TimeUnit.MICROSECONDS);
    }

    /**
     * Test maximum time without improvement stop criterion.
     */
    @Test
    public void testMaxRuntime() {
        
        System.out.println(" - test single run (default check period)");
        System.out.println("   >>> max: " + TIME_UNIT.toMillis(MAX_TIME_WITHOUT_IMPR) + " ms without improvement");
        
        // add stop criterion to search
        search.addStopCriterion(new MaxTimeWithoutImprovement(MAX_TIME_WITHOUT_IMPR, TIME_UNIT));
        
        // run search (should stop)
        search.start();
        
        System.out.println("   >>> run: " + search.getTimeWithoutImprovement()+ " ms without improvement");
        System.out.println("   >>> best: " + search.getBestSolutionEvaluation());
        
    }
    
    /**
     * Test maximum time without improvement stop criterion with coarse check period.
     */
    @Test
    public void testMaxRuntimeWithCoarsePeriod() {
        
        System.out.format(" - test single run (check period = %d ms)\n", CHECK_PERIOD_TIME_UNIT.toMillis(COARSE_CHECK_PERIOD));
        System.out.println("   >>> max: " + TIME_UNIT.toMillis(MAX_TIME_WITHOUT_IMPR) + " ms without improvement");
        
        // add stop criterion to search
        search.addStopCriterion(new MaxTimeWithoutImprovement(MAX_TIME_WITHOUT_IMPR, TIME_UNIT));
        // set coarse check period
        search.setStopCriterionCheckPeriod(COARSE_CHECK_PERIOD, CHECK_PERIOD_TIME_UNIT);
        
        // run search (should stop)
        search.start();
        
        System.out.println("   >>> run: " + search.getTimeWithoutImprovement()+ " ms without improvement");
        System.out.println("   >>> best: " + search.getBestSolutionEvaluation());
        
    }
    
    /**
     * Test subsequent runs.
     */
    @Test
    public void testSubsequentRuns() {
        
        System.out.println(" - test subsequent runs");
        
        // add stop criterion
        search.addStopCriterion(new MaxTimeWithoutImprovement(LOW_MAX_TIME_WITHOUT_IMPR, TIME_UNIT));
        // set check period to same value
        search.setStopCriterionCheckPeriod(LOW_MAX_TIME_WITHOUT_IMPR, TIME_UNIT);
        
        // perform 5 search runs
        Evaluation prevBestSolEval = null;
        for(int i=0; i<5; i++){
            search.start();
            // check best solution evaluation
            Evaluation bestSolEval = search.getBestSolutionEvaluation();
            System.out.println("   >>> best: " + bestSolEval);
            if(prevBestSolEval != null){
                assertTrue(DoubleComparatorWithPrecision.greaterThanOrEqual(
                                bestSolEval.getValue(),
                                prevBestSolEval.getValue(),
                                TestConstants.DOUBLE_COMPARISON_PRECISION));
            }
            prevBestSolEval = bestSolEval;
        }
        
    }
    
    /**
     * Test subsequent runs (minimizing).
     */
    @Test
    public void testSubsequentRunsMinimizing() {
        
        System.out.println(" - test subsequent runs (minimizing)");
        
        // set objective to minimize
        obj.setMinimizing();
        
        // add stop criterion
        search.addStopCriterion(new MaxTimeWithoutImprovement(LOW_MAX_TIME_WITHOUT_IMPR, TIME_UNIT));
        // set check period to same value
        search.setStopCriterionCheckPeriod(LOW_MAX_TIME_WITHOUT_IMPR, TIME_UNIT);
        
        // perform 5 search runs
        Evaluation prevBestSolEval = null;
        for(int i=0; i<5; i++){
            search.start();
            // check best solution evaluation
            Evaluation bestSolEval = search.getBestSolutionEvaluation();
            System.out.println("   >>> best: " + bestSolEval);
            if(prevBestSolEval != null){
                assertTrue(DoubleComparatorWithPrecision.smallerThanOrEqual(
                                bestSolEval.getValue(),
                                prevBestSolEval.getValue(),
                                TestConstants.DOUBLE_COMPARISON_PRECISION));
            }
            prevBestSolEval = bestSolEval;
        }
        
    }

}