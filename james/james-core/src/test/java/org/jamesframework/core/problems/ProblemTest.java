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

package org.jamesframework.core.problems;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;
import org.jamesframework.core.problems.constraints.validations.SimpleValidation;
import org.jamesframework.core.problems.constraints.validations.Validation;
import org.jamesframework.core.problems.objectives.evaluations.Evaluation;
import org.jamesframework.core.problems.objectives.evaluations.SimpleEvaluation;
import org.jamesframework.core.search.neigh.Move;
import org.jamesframework.core.subset.SubsetSolution;
import org.jamesframework.core.subset.neigh.moves.AdditionMove;
import org.jamesframework.test.util.TestConstants;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.Before;
import static org.junit.Assert.*;

/**
 * Test default methods from Problem interface (apply-undo evaluation/validation).
 * 
 * @author <a href="mailto:herman.debeukelaer@ugent.be">Herman De Beukelaer</a>
 */
public class ProblemTest {

    // problem stub to work with
    private Problem<SubsetSolution> problem;
    
    /**
     * Print message when starting tests.
     */
    @BeforeClass
    public static void setUpClass() {
        System.out.println("# Testing Problem ...");
    }

    /**
     * Print message when tests are complete.
     */
    @AfterClass
    public static void tearDownClass() {
        System.out.println("# Done testing Problem!");
    }
    
    /**
     * Create problem stub to work with in each test method.
     */
    @Before
    public void setUp(){
        problem = new ProblemStub();
    }
    
    @Test
    public void testDefaultDeltaEvaluation(){
        
        System.out.println(" - test default delta evaluation");
        
        Set<Integer> IDs = new HashSet<>(Arrays.asList(1,2,3,4,5));
        SubsetSolution sol = new SubsetSolution(IDs); // empty solution: {}
        
        Evaluation eval = problem.evaluate(sol);
        assertEquals(0.0, eval.getValue(), TestConstants.DOUBLE_COMPARISON_PRECISION);
        
        // evaluate addition move
        Move<SubsetSolution> move = new AdditionMove(3);
        Evaluation newEval = problem.evaluate(move, sol, eval);
        assertEquals(3, newEval.getValue(), TestConstants.DOUBLE_COMPARISON_PRECISION);
        // apply move (solution: {3})
        move.apply(sol);
        eval = newEval;
        
        // evaluate second addition move
        move = new AdditionMove(5);
        newEval = problem.evaluate(move, sol, eval);
        assertEquals(8, newEval.getValue(), TestConstants.DOUBLE_COMPARISON_PRECISION);
        // apply move (solution: {3, 5})
        move.apply(sol);
        eval = newEval;
        
        // evaluate third addition move
        move = new AdditionMove(2);
        newEval = problem.evaluate(move, sol, eval);
        assertEquals(10, newEval.getValue(), TestConstants.DOUBLE_COMPARISON_PRECISION);
        // apply move (solution: {2, 3, 5})
        move.apply(sol);
        
    }
    
    @Test
    public void testDefaultDeltaValidation(){
        
        System.out.println(" - test default delta validation");
        
        Set<Integer> IDs = new HashSet<>(Arrays.asList(1,2,3,4,5));
        SubsetSolution sol = new SubsetSolution(IDs); // empty solution: {}
        
        Validation val = problem.validate(sol);
        assertTrue(val.passed());
        
        // validate addition move
        Move<SubsetSolution> move = new AdditionMove(3);
        Validation newVal = problem.validate(move, sol, val);
        assertTrue(newVal.passed());
        // apply move (solution: {3})
        move.apply(sol);
        val = newVal;
        
        // validate second addition move
        move = new AdditionMove(5);
        newVal = problem.validate(move, sol, val);
        assertTrue(newVal.passed());
        // apply move (solution: {3, 5})
        move.apply(sol);
        val = newVal;
        
        // validate second addition move
        move = new AdditionMove(2);
        newVal = problem.validate(move, sol, val);
        assertFalse(newVal.passed());
        // apply move (solution: {2, 3, 5})
        move.apply(sol);
        
    }

    /**
     * Problem stub used for testing. The evaluation of a subset solution corresponds to the sum of the selected IDs.
     * A subset is considered to be valid if it contains odd IDs only.
     */
    private class ProblemStub implements Problem<SubsetSolution> {

        @Override
        public Evaluation evaluate(SubsetSolution solution) {
            return new SimpleEvaluation(solution.getSelectedIDs().stream().mapToInt(Integer::intValue).sum());
        }

        @Override
        public Validation validate(SubsetSolution solution) {
            return new SimpleValidation(solution.getSelectedIDs().stream().allMatch(id -> id%2 == 1));
        }

        @Override
        public boolean isMinimizing() {
            return false;
        }

        @Override
        public SubsetSolution createRandomSolution() {
            return null;
        }
        
    }

}