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

import java.util.Arrays;
import java.util.HashSet;
import org.jamesframework.core.problems.Solution;
import org.jamesframework.core.problems.objectives.evaluations.Evaluation;
import org.jamesframework.core.subset.SubsetSolution;
import org.jamesframework.core.subset.neigh.moves.SubsetMove;
import org.jamesframework.core.subset.neigh.moves.SwapMove;
import org.jamesframework.test.fakes.SumOfIDsFakeSubsetObjective;
import org.jamesframework.test.stubs.EmptySolutionStub;
import org.jamesframework.test.stubs.FixedEvaluationObjectiveStub;
import org.jamesframework.test.util.TestConstants;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 * Test WeightedIndex.
 * 
 * @author <a href="mailto:herman.debeukelaer@ugent.be">Herman De Beukelaer</a>
 */
public class WeightedIndexTest {

    /**
     * Print message before running tests.
     */
    @BeforeClass
    public static void setUpClass() {
        System.out.println("# Testing WeightedIndex ...");
    }

    /**
     * Print message when tests are complete.
     */
    @AfterClass
    public static void tearDownClass() {
        System.out.println("# Done testing WeightedIndex!");
    }

    /**
     * Test of addObjective method, of class WeightedIndex.
     */
    @Test
    public void testAddObjective() {
        
        System.out.println(" - test addObjective");
        
        // create weighted objective
        WeightedIndex<Solution, Object> weighted = new WeightedIndex<>();
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
        WeightedIndex<EmptySolutionStub, ?> weighted2 = new WeightedIndex<>();
        
        // try to add a fake objective which can evaluate any solution,
        // should work fine (more general than EmptySolutionStub)
        weighted2.addObjective(obj0, 1.0);
        
    }
    
    /**
     * Test of removeObjective method, of class WeightedIndex.
     */
    @Test
    public void testRemoveObjective() {
        
        System.out.println(" - test removeObjective");
        
        // create weighted objective
        WeightedIndex<Solution, Object> weighted = new WeightedIndex<>();
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
     * Test of isMinimizing method, of class WeightedIndex.
     */
    @Test
    public void testIsMinimizing() {
        
        System.out.println(" - test isMinimizing");        
        
        // create weighted objective
        WeightedIndex obj = new WeightedIndex();
        // weighted objective should never be minimizing
        boolean expResult = false;
        boolean result = obj.isMinimizing();
        assertEquals(expResult, result);
    }

    /**
     * Test of evaluate method, of class WeightedIndex.
     */
    @Test
    public void testEvaluate() {
        
        System.out.println(" - test evaluate");
        
        // create weighted objective
        WeightedIndex<Solution, Object> weighted = new WeightedIndex<>();
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
        Evaluation eval = weighted.evaluate(emptySol, null);
        
        assertEquals(expectedEval, eval.getValue(), TestConstants.DOUBLE_COMPARISON_PRECISION);
        
        // set objective 3 to minimizing and re-evaluate
        obj3.setMinimizing();
        expectedEval = 3.0;
        eval = weighted.evaluate(emptySol, null);
        
        assertEquals(expectedEval, eval.getValue(), TestConstants.DOUBLE_COMPARISON_PRECISION);
        
        // set objective 4 to minimizing as well and re-evaluate
        obj4.setMinimizing();
        expectedEval = -13.0;
        eval = weighted.evaluate(emptySol, null);
        
        assertEquals(expectedEval, eval.getValue(), TestConstants.DOUBLE_COMPARISON_PRECISION);
        
        // set objective 0 to minimizing and re-evaluate (should not make a difference)
        obj0.setMinimizing();
        eval = weighted.evaluate(emptySol, null);

        assertEquals(expectedEval, eval.getValue(), TestConstants.DOUBLE_COMPARISON_PRECISION);
        
        // remove objective 0 and re-evaluate (should not make a difference)
        weighted.removeObjective(obj0);
        eval = weighted.evaluate(emptySol, null);

        assertEquals(expectedEval, eval.getValue(), TestConstants.DOUBLE_COMPARISON_PRECISION);
        
        // remove objective 3 and re-evaluate
        weighted.removeObjective(obj3);
        expectedEval = -4.0;
        eval = weighted.evaluate(emptySol, null);

        assertEquals(expectedEval, eval.getValue(), TestConstants.DOUBLE_COMPARISON_PRECISION);
    
    }
    
    /**
     * Test delta evaluation.
     */
    @Test
    public void testDeltaEvaluation() {
        
        System.out.println(" - test delta evaluation");
        
        // create weighted objective
        WeightedIndex<SubsetSolution, Object> weighted = new WeightedIndex<>();
        // create some fake objectives with fixed evaluation
        FixedEvaluationObjectiveStub obj0 = new FixedEvaluationObjectiveStub(3.0);
        SumOfIDsFakeSubsetObjective obj1 = new SumOfIDsFakeSubsetObjective();
        
        // add objectives with positive weights
        double weight0 = 2.0;
        double weight1 = 1.5;
        weighted.addObjective(obj0, weight0);
        weighted.addObjective(obj1, weight1);
        
        // create subset solution (ids: 0 - 4)
        SubsetSolution sol = new SubsetSolution(new HashSet<>(Arrays.asList(0,1,2,3,4)));
        // select ids 2 and 4
        sol.select(2);
        sol.select(4);
        
        // evaluate solution
        Evaluation eval = weighted.evaluate(sol, null);
        
        // verify
        double expectedEval = 2.0*3.0 + 1.5*(2 + 4);
        assertEquals(expectedEval, eval.getValue(), TestConstants.DOUBLE_COMPARISON_PRECISION);
        
        // create subset move (drop 2, add 1)
        SubsetMove move = new SwapMove(1, 2);
        
        // evaluate move
        eval = weighted.evaluate(move, sol, eval, null);
        
        // verify
        expectedEval = 2.0*3.0 + 1.5*(1+4);
        assertEquals(expectedEval, eval.getValue(), TestConstants.DOUBLE_COMPARISON_PRECISION);
        
        // full evaluation
        move.apply(sol);
        Evaluation fullEval = weighted.evaluate(sol, null);
        move.undo(sol);
        
        // verify
        assertEquals(eval.getValue(), fullEval.getValue(), TestConstants.DOUBLE_COMPARISON_PRECISION);
        
        // set second objective to minimizing
        obj1.setMinimizing();
        expectedEval = 2.0*3.0 - 1.5*(1 + 4);
        // re-evaluate
        eval = weighted.evaluate(sol, null);
        
        // redo delta evaluation
        eval = weighted.evaluate(move, sol, eval, null);
        // verify
        assertEquals(expectedEval, eval.getValue(), TestConstants.DOUBLE_COMPARISON_PRECISION);
        
        // redo full evaluation
        move.apply(sol);
        fullEval = weighted.evaluate(sol, null);
        move.undo(sol);
        // verify
        assertEquals(eval.getValue(), fullEval.getValue(), TestConstants.DOUBLE_COMPARISON_PRECISION);

    }

}