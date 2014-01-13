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

package org.jamesframework.core.problems.objectives;

import org.jamesframework.core.problems.solutions.Solution;
import org.jamesframework.test.util.FakeEmptyData;
import org.jamesframework.test.util.FakeEmptySolution;
import org.jamesframework.test.util.FakeObjectiveWithFixedEvaluation;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 *
 * @author Herman De Beukelaer <herman.debeukelaer@ugent.be>
 */
public class WeightedObjectiveTest {
    
    private static final double DOUBLE_PRECISION = 1e-10;

    public WeightedObjectiveTest() {
    }

    @BeforeClass
    public static void setUpClass() {
        System.out.println("# Testing WeightedObjective ...");
    }

    @AfterClass
    public static void tearDownClass() {
        System.out.println("# Done testing WeightedObjective!");
    }

    @Before
    public void setUp() {
    }

    @After
    public void tearDown() {
    }

    /**
     * Test of addObjective method, of class WeightedObjective.
     */
    @Test
    public void testAddObjective() {
        
        System.out.println(" - test addObjective");
        
        // create weighted objective
        WeightedObjective weighted = new WeightedObjective();
        // create some fake objectives with fixed evaluation
        FakeObjectiveWithFixedEvaluation obj0 = new FakeObjectiveWithFixedEvaluation(0.0);
        
        boolean thrown;
        
        // try to add objective with weight 0.0, should throw exception
        thrown = false;
        try{
            weighted.addObjective(obj0, 0.0);
        } catch (IllegalArgumentException ex){
            thrown = true;
        }
        assertTrue(thrown);
        
        // try again with negative weight, should also throw exception
        thrown = false;
        try{
            weighted.addObjective(obj0, -1.5);
        } catch (IllegalArgumentException ex){
            thrown = true;
        }
        assertTrue(thrown);
        
        // now try to add objective with positive weights, should work fine
        weighted.addObjective(obj0, 2.5);
        
        // create new weighted objective specific for empty solution and empty data
        WeightedObjective<FakeEmptySolution, FakeEmptyData> weighted2 = new WeightedObjective<>();
        
        // try to add a fake objective which is more general than specific types set
        // for weighted objective -- can evaluate any solution with any data -- should work fine
        weighted2.addObjective(obj0, 1.0);
        
    }

    /**
     * Test of isMinimizing method, of class WeightedObjective.
     */
    @Test
    public void testIsMinimizing() {
        
        System.out.println(" - test isMinimizing");        
        
        // create weighted objective
        WeightedObjective obj = new WeightedObjective();
        // weighted objective should never be minimizing
        boolean expResult = false;
        boolean result = obj.isMinimizing();
        assertEquals(expResult, result);
    }

    /**
     * Test of evaluate method, of class WeightedObjective.
     */
    @Test
    public void testEvaluate() {
        
        System.out.println(" - test evaluate");
        
        // create weighted objective
        WeightedObjective obj = new WeightedObjective();
        // create some fake objectives with fixed evaluation
        FakeObjectiveWithFixedEvaluation obj0 = new FakeObjectiveWithFixedEvaluation(0.0);
        FakeObjectiveWithFixedEvaluation obj1 = new FakeObjectiveWithFixedEvaluation(1.0);
        FakeObjectiveWithFixedEvaluation obj2 = new FakeObjectiveWithFixedEvaluation(2.0);
        FakeObjectiveWithFixedEvaluation obj3 = new FakeObjectiveWithFixedEvaluation(3.0);
        FakeObjectiveWithFixedEvaluation obj4 = new FakeObjectiveWithFixedEvaluation(4.0);
        
        // add objectives with positive weights
        double weight0 = 1.0;
        double weight1 = 2.0;
        double weight2 = 1.0;
        double weight3 = 3.0;
        double weight4 = 2.0;
        obj.addObjective(obj0, weight0);
        obj.addObjective(obj1, weight1);
        obj.addObjective(obj2, weight2);
        obj.addObjective(obj3, weight3);
        obj.addObjective(obj4, weight4);
        
        // create fake empty data
        FakeEmptyData emptyData = new FakeEmptyData();
        
        // create fake empty solution and evaluate
        Solution emptySol = new FakeEmptySolution();
        double expectedEval = 21.0;
        double eval = obj.evaluate(emptySol, emptyData);
        
        assertEquals(expectedEval, eval, DOUBLE_PRECISION);
        
        // set objective 3 to minimizing and re-evaluate
        obj3.setMinimizing();
        expectedEval = 3.0;
        eval = obj.evaluate(emptySol, emptyData);
        
        assertEquals(expectedEval, eval, DOUBLE_PRECISION);
    }

}