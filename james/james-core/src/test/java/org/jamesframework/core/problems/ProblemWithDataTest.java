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

package org.jamesframework.core.problems;

import org.jamesframework.core.problems.constraints.Constraint;
import org.jamesframework.core.problems.constraints.PenalizingConstraint;
import org.jamesframework.core.problems.objectives.Objective;
import org.jamesframework.core.problems.solutions.Solution;
import org.jamesframework.test.util.AlwaysSatisfiedConstraintStub;
import org.jamesframework.test.util.AlwaysSatisfiedPenalizingConstraintStub;
import org.jamesframework.test.util.EmptySolutionStub;
import org.jamesframework.test.util.FixedEvaluationObjectiveStub;
import org.jamesframework.test.util.NeverSatisfiedConstraintStub;
import org.jamesframework.test.util.NeverSatisfiedPenalizingConstraintStub;
import org.jamesframework.test.util.TestConstants;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import static org.junit.Assert.*;
import org.junit.Before;

/**
 * Test general functionalities of ProblemWithData that do not actually rely on the specific data, using
 * objectives and constraints that ignore the data.
 * 
 * @author Herman De Beukelaer <herman.debeukelaer@ugent.be>
 */
public class ProblemWithDataTest {

    // problem stub to work with
    private ProblemWithData problem;
    
    /**
     * Print message when starting tests.
     */
    @BeforeClass
    public static void setUpClass() {
        System.out.println("# Testing ProblemWithData ...");
    }

    /**
     * Print message when tests are complete.
     */
    @AfterClass
    public static void tearDownClass() {
        System.out.println("# Done testing ProblemWithData!");
    }
    
    /**
     * Create problem stub to work with in each test method.
     */
    @Before
    public void setUp(){
        FixedEvaluationObjectiveStub o = new FixedEvaluationObjectiveStub(10.0);
        problem = new ProblemStub(o);
    }

    /**
     * Test constructor, of class ProblemWithData.
     */
    @Test
    public void testConstructor() {
        
        System.out.println(" - test constructor");
        
        // try to create problem without objective, should result in error
        boolean thrown = false;
        try {
            ProblemWithData p = new ProblemStub(null);
        } catch (NullPointerException ex) {
            thrown = true;
        }
        assertTrue(thrown);
        
    }
    
    /**
     * Test of getObjective method, of class ProblemWithData.
     */
    @Test
    public void testGetObjective() {
        
        System.out.println(" - test getObjective");
        
        FixedEvaluationObjectiveStub o = new FixedEvaluationObjectiveStub(128736.0);
        problem.setObjective(o);
        assertEquals(o, problem.getObjective());
        
    }

    /**
     * Test of setObjective method, of class ProblemWithData.
     */
    @Test
    public void testSetObjective() {
        
        System.out.println(" - test setObjective");
        
        // try to set objective to null, should throw error
        boolean thrown = false;
        try {
            problem.setObjective(null);
        } catch (NullPointerException ex) {
            thrown = true;
        }
        assertTrue(thrown);
        
    }

    /**
     * Test of getData method, of class ProblemWithData.
     */
    @Test
    public void testGetData() {
        
        System.out.println(" - test getData");
        
        // without data
        assertNull(problem.getData());
        
        // set data and get it again
        Object dummyData = "Dummy Data Object";
        problem.setData(dummyData);
        assertEquals(dummyData, problem.getData());
        
    }
    
    /**
     * Test of addDominatingConstraint method, of class ProblemWithData.
     */
    @Test
    public void testAddDominatingConstraint() {
       
        System.out.println(" - test addDominatingConstraint");
        
        // check that both general constraints as well as constraints
        // with penalty definitions can be added as a dominating constraint
        problem.addDominatingConstraint(new AlwaysSatisfiedConstraintStub());
        problem.addDominatingConstraint(new NeverSatisfiedConstraintStub());
        problem.addDominatingConstraint(new AlwaysSatisfiedPenalizingConstraintStub());
        problem.addDominatingConstraint(new NeverSatisfiedPenalizingConstraintStub(123.0));
        
    }

    /**
     * Test of removeDominatingConstraint method, of class ProblemWithData.
     */
    @Test
    public void testRemoveDominatingConstraint() {
        
        System.out.println(" - test removeDominatingConstraint");
        
        // create some constraints
        Constraint<?,?> c0 = new AlwaysSatisfiedConstraintStub();
        Constraint<?,?> c1 = new NeverSatisfiedConstraintStub();
        Constraint<?,?> c2 = new AlwaysSatisfiedPenalizingConstraintStub();
        
        
        // add constraint c0 and c1 as dominating constraint
        problem.addDominatingConstraint(c0);
        problem.addDominatingConstraint(c1);
        // try to remove c2 which was never added
        assertFalse(problem.removeDominatingConstraint(c2));
        // remove constraints that were added
        assertTrue(problem.removeDominatingConstraint(c0));
        assertTrue(problem.removeDominatingConstraint(c1));
        // try to remove again
        assertFalse(problem.removeDominatingConstraint(c0));
        assertFalse(problem.removeDominatingConstraint(c1));
        
    }

    /**
     * Test of addPenalizingConstraint method, of class ProblemWithData.
     */
    @Test
    public void testAddPenalizingConstraint() {
        
        System.out.println(" - test addPenalizingConstraint");
        
        // add some penalizing constraints
        problem.addPenalizingConstraint(new AlwaysSatisfiedPenalizingConstraintStub());
        problem.addPenalizingConstraint(new NeverSatisfiedPenalizingConstraintStub(123.0));
        
    }

    /**
     * Test of removePenalizingConstraint method, of class ProblemWithData.
     */
    @Test
    public void testRemovePenalizingConstraint() {
        
        System.out.println(" - test removePenalizingConstraint");
        
        // create some constraints
        PenalizingConstraint<?,?> c0 = new AlwaysSatisfiedPenalizingConstraintStub();
        PenalizingConstraint<?,?> c1 = new NeverSatisfiedPenalizingConstraintStub(123.0);
        
        
        // add constraint c0 as penalizing constraint
        problem.addPenalizingConstraint(c0);
        // try to remove c1 which was never added
        assertFalse(problem.removePenalizingConstraint(c1));
        // try to remove c0 as DOMINATING constraint -- NEVER added with this role
        assertFalse(problem.removeDominatingConstraint(c0));
        // remove c0 as PENALIZING constraint
        assertTrue(problem.removePenalizingConstraint(c0));
        // try again
        assertFalse(problem.removePenalizingConstraint(c0));
        
    }

    /**
     * Test of areConstraintsSatisfied method, of class ProblemWithData.
     */
    @Test
    public void testAreConstraintsSatisfied() {
        
        System.out.println(" - test areConstraintsSatisfied");
        
        Solution sol = new EmptySolutionStub();
        
        // test without constraints
        assertTrue(problem.areConstraintsSatisfied(sol));
        
        // add constraints which are always satisfied
        problem.addDominatingConstraint(new AlwaysSatisfiedConstraintStub());
        problem.addPenalizingConstraint(new AlwaysSatisfiedPenalizingConstraintStub());
        // verify
        assertTrue(problem.areConstraintsSatisfied(sol));
        
        // add unsatisfiable dominating constraint
        Constraint<?,?> unsatisfiable = new NeverSatisfiedConstraintStub();
        problem.addDominatingConstraint(unsatisfiable);
        assertFalse(problem.areConstraintsSatisfied(sol));
        // remove the constraint
        problem.removeDominatingConstraint(unsatisfiable);
        
        // same thing with unsatisfiable penalizing constraint
        PenalizingConstraint<?,?> unsatisfiable2 = new NeverSatisfiedPenalizingConstraintStub(123.0);
        problem.addPenalizingConstraint(unsatisfiable2);
        assertFalse(problem.areConstraintsSatisfied(sol));
        // remove the constraint
        problem.removePenalizingConstraint(unsatisfiable2);
        
    }

    /**
     * Test of evaluate method, of class ProblemWithData.
     */
    @Test
    public void testEvaluate() {
        
        System.out.println(" - test evaluate");
        
        double fixedEval = 123.0;
        Solution sol = new EmptySolutionStub();
        
        // test with a fixed objective only, no constraints
        FixedEvaluationObjectiveStub o = new FixedEvaluationObjectiveStub(fixedEval);
        problem.setObjective(o);
        assertEquals(fixedEval, problem.evaluate(sol), TestConstants.DOUBLE_COMPARISON_PRECISION);
        
        // throw in a dominating constraint that is always satisfied
        Constraint<?,?> c0 = new AlwaysSatisfiedConstraintStub();
        problem.addDominatingConstraint(c0);
        assertEquals(fixedEval, problem.evaluate(sol), TestConstants.DOUBLE_COMPARISON_PRECISION);
        
        // throw in a penalizing constraint that is always satisfied
        PenalizingConstraint<?,?> c1 = new AlwaysSatisfiedPenalizingConstraintStub();
        problem.addPenalizingConstraint(c1);
        assertEquals(fixedEval, problem.evaluate(sol), TestConstants.DOUBLE_COMPARISON_PRECISION);
        
        // throw in a dominating constraint that is never satisfied
        Constraint<?,?> c2 = new NeverSatisfiedConstraintStub();
        problem.addDominatingConstraint(c2);
        assertEquals(-Double.MAX_VALUE, problem.evaluate(sol), TestConstants.DOUBLE_COMPARISON_PRECISION);
        
        // switch to minimizing and repeat
        o.setMinimizing();
        assertEquals(Double.MAX_VALUE, problem.evaluate(sol), TestConstants.DOUBLE_COMPARISON_PRECISION);
        
        // switch back to maximizing
        o.setMaximizing();
        // remove dominating constraint which is never satisfied
        problem.removeDominatingConstraint(c2);
        
        // add penalizing constraint which is never satisfied
        double c3penalty = 1234.0;
        PenalizingConstraint<?,?> c3 = new NeverSatisfiedPenalizingConstraintStub(c3penalty);
        problem.addPenalizingConstraint(c3);
        assertEquals(fixedEval-c3penalty, problem.evaluate(sol), TestConstants.DOUBLE_COMPARISON_PRECISION);
        
        // switch to minimizing and repeat
        o.setMinimizing();
        assertEquals(fixedEval+c3penalty, problem.evaluate(sol), TestConstants.DOUBLE_COMPARISON_PRECISION);
        
        // switch back to maximizing
        o.setMaximizing();
        
        // add another unsatisfiable penalizing constraint
        double c4penalty = 12345.0;
        PenalizingConstraint<?,?> c4 = new NeverSatisfiedPenalizingConstraintStub(c4penalty);
        problem.addPenalizingConstraint(c4);
        assertEquals(fixedEval-c3penalty-c4penalty, problem.evaluate(sol), TestConstants.DOUBLE_COMPARISON_PRECISION);
        
        // switch to minimizing and repeat
        o.setMinimizing();
        assertEquals(fixedEval+c3penalty+c4penalty, problem.evaluate(sol), TestConstants.DOUBLE_COMPARISON_PRECISION);
        
        // switch back to maximizing
        o.setMaximizing();
        
        // again add a dominating constraint which is never satisfied
        problem.addDominatingConstraint(c2);
        assertEquals(-Double.MAX_VALUE, problem.evaluate(sol), TestConstants.DOUBLE_COMPARISON_PRECISION);
        
    }

    /**
     * Test of isMinimizing method, of class ProblemWithData.
     */
    @Test
    public void testIsMinimizing() {
        
        System.out.println(" - test isMinimizing");
        
        // by default, objectives are maximizing
        assertFalse(problem.isMinimizing());
        
        // now switch to minimizing
        FixedEvaluationObjectiveStub o = new FixedEvaluationObjectiveStub(123.0);
        o.setMinimizing();
        problem.setObjective(o);
        assertTrue(problem.isMinimizing());
        
    }

    /**
     * Problem stub used for testing. Only accepts objectives/constraints that can handle any solution type
     * and that do not use any data.
     */
    private class ProblemStub extends ProblemWithData<Solution, Object> {

        /**
         * Create problem stub with given objective, without specifying the data.
         * 
         * @param obj objective
         */
        public ProblemStub(Objective<Solution, Object> obj) {
            super(obj, null);
        }

        /**
         * Not used here for testing.
         * 
         * @return null
         */
        @Override
        public Solution createRandomSolution() {
            return null;
        }

        /**
         * Not used here for testing.
         * 
         * @param solution any solution
         * @return null
         */
        @Override
        public Solution copySolution(Solution solution) {
            return null;
        }
    }

}