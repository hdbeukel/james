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

package org.jamesframework.ext.problems.objectives;

import org.jamesframework.core.problems.Solution;
import org.jamesframework.test.stubs.EmptySolutionStub;
import org.jamesframework.test.stubs.FixedEvaluationObjectiveStub;
import org.jamesframework.test.util.TestConstants;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 * Test WeightedMultiObjective.
 * 
 * @author <a href="mailto:herman.debeukelaer@ugent.be">Herman De Beukelaer</a>
 */
public class WeightedMultiObjectiveTest {

    /**
     * Print message before running tests.
     */
    @BeforeClass
    public static void setUpClass() {
        System.out.println("# Testing WeightedMultiObjective ...");
    }

    /**
     * Print message when tests are complete.
     */
    @AfterClass
    public static void tearDownClass() {
        System.out.println("# Done testing WeightedMultiObjective!");
    }

    /**
     * Test of addObjective method, of class WeightedMultiObjective.
     */
    @Test
    public void testAddObjective() {
        
        System.out.println(" - test addObjective");
        
        // create weighted objective
        WeightedMultiObjective<Solution, Object> weighted = new WeightedMultiObjective<>();
        // create some fake objective with fixed evaluation
        FixedEvaluationObjectiveStub obj0 = new FixedEvaluationObjectiveStub(0.0);
        
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
        
        // create new weighted objective specific for empty solution
        WeightedMultiObjective<EmptySolutionStub, ?> weighted2 = new WeightedMultiObjective<>();
        
        // try to add a fake objective which can evaluate any solution,
        // should work fine (more general than EmptySolutionStub)
        weighted2.addObjective(obj0, 1.0);
        
    }
    
    /**
     * Test of removeObjective method, of class WeightedMultiObjective.
     */
    @Test
    public void testRemoveObjective() {
        
        System.out.println(" - test removeObjective");
        
        // create weighted objective
        WeightedMultiObjective<Solution, Object> weighted = new WeightedMultiObjective<>();
        // create some fake objectives with fixed evaluation
        FixedEvaluationObjectiveStub obj0 = new FixedEvaluationObjectiveStub(0.0);
        FixedEvaluationObjectiveStub obj1 = new FixedEvaluationObjectiveStub(1.0);
        
        // add one of both objectives
        weighted.addObjective(obj0, 1.0);
        
        // try to remove objective which is not added
        boolean expected = false;
        boolean got = weighted.removeObjective(obj1);
        
        assertEquals(expected, got);
        
        // try to remove objective which was added
        expected = true;
        got = weighted.removeObjective(obj0);
        
        assertEquals(expected, got);
        
    }

    /**
     * Test of isMinimizing method, of class WeightedMultiObjective.
     */
    @Test
    public void testIsMinimizing() {
        
        System.out.println(" - test isMinimizing");        
        
        // create weighted objective
        WeightedMultiObjective obj = new WeightedMultiObjective();
        // weighted objective should never be minimizing
        boolean expResult = false;
        boolean result = obj.isMinimizing();
        assertEquals(expResult, result);
    }

    /**
     * Test of evaluate method, of class WeightedMultiObjective.
     */
    @Test
    public void testEvaluate() {
        
        System.out.println(" - test evaluate");
        
        // create weighted objective
        WeightedMultiObjective<Solution, Object> weighted = new WeightedMultiObjective<>();
        // create some fake objectives with fixed evaluation
        FixedEvaluationObjectiveStub obj0 = new FixedEvaluationObjectiveStub(0.0);
        FixedEvaluationObjectiveStub obj1 = new FixedEvaluationObjectiveStub(1.0);
        FixedEvaluationObjectiveStub obj2 = new FixedEvaluationObjectiveStub(2.0);
        FixedEvaluationObjectiveStub obj3 = new FixedEvaluationObjectiveStub(3.0);
        FixedEvaluationObjectiveStub obj4 = new FixedEvaluationObjectiveStub(4.0);
        
        // add objectives with positive weights
        double weight0 = 1.0;
        double weight1 = 2.0;
        double weight2 = 1.0;
        double weight3 = 3.0;
        double weight4 = 2.0;
        weighted.addObjective(obj0, weight0);
        weighted.addObjective(obj1, weight1);
        weighted.addObjective(obj2, weight2);
        weighted.addObjective(obj3, weight3);
        weighted.addObjective(obj4, weight4);
        
        // create fake empty solution and evaluate
        Solution emptySol = new EmptySolutionStub();
        double expectedEval = 21.0;
        double eval = weighted.evaluate(emptySol, null);
        
        assertEquals(expectedEval, eval, TestConstants.DOUBLE_COMPARISON_PRECISION);
        
        // set objective 3 to minimizing and re-evaluate
        obj3.setMinimizing();
        expectedEval = 3.0;
        eval = weighted.evaluate(emptySol, null);
        
        assertEquals(expectedEval, eval, TestConstants.DOUBLE_COMPARISON_PRECISION);
        
        // set objective 4 to minimizing as well and re-evaluate
        obj4.setMinimizing();
        expectedEval = -13.0;
        eval = weighted.evaluate(emptySol, null);
        
        assertEquals(expectedEval, eval, TestConstants.DOUBLE_COMPARISON_PRECISION);
        
        // set objective 0 to minimizing and re-evaluate (should not make a difference)
        obj0.setMinimizing();
        eval = weighted.evaluate(emptySol, null);

        assertEquals(expectedEval, eval, TestConstants.DOUBLE_COMPARISON_PRECISION);
        
        // remove objective 0 and re-evaluate (should not make a difference)
        weighted.removeObjective(obj0);
        eval = weighted.evaluate(emptySol, null);

        assertEquals(expectedEval, eval, TestConstants.DOUBLE_COMPARISON_PRECISION);
        
        // remove objective 3 and re-evaluate
        weighted.removeObjective(obj3);
        expectedEval = -4.0;
        eval = weighted.evaluate(emptySol, null);

        assertEquals(expectedEval, eval, TestConstants.DOUBLE_COMPARISON_PRECISION);
    
    }

}