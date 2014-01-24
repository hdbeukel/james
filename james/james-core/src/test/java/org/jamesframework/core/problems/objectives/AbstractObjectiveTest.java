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

import org.jamesframework.core.problems.ProblemWithData;
import org.jamesframework.core.problems.ProblemWithDataTest;
import org.jamesframework.core.problems.solutions.Solution;
import org.jamesframework.test.util.FixedEvaluationObjectiveStub;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import static org.junit.Assert.*;
import org.junit.Before;

/**
 * Test AbstractObjective. The only thing being tested here is minimization/maximization settings.
 * 
 * @author Herman De Beukelaer <herman.debeukelaer@ugent.be>
 */
public class AbstractObjectiveTest {

    // objective stub to work with
    private AbstractObjectiveStub obj;
    
    /**
     * Print message when starting tests.
     */
    @BeforeClass
    public static void setUpClass() {
        System.out.println("# Testing AbstractObjective ...");
    }

    /**
     * Print message when tests are complete.
     */
    @AfterClass
    public static void tearDownClass() {
        System.out.println("# Done testing AbstractObjective!");
    }
    
    /**
     * Create objective stub to work with in each test method.
     */
    @Before
    public void setUp(){
        obj = new AbstractObjectiveStub();
    }

    /**
     * Test of isMinimizing method, of class AbstractObjective.
     */
    @Test
    public void testIsMinimizing() {
        
        System.out.println(" - test isMinimizing");
        
        // by default objectives are maximizing
        assertFalse(obj.isMinimizing());
        
        // set minimizing
        obj.setMinimizing();
        assertTrue(obj.isMinimizing());
        
        // revert to maximizing
        obj.setMaximizing();
        assertFalse(obj.isMinimizing());
        
    }

    /**
     * AbstractObjective stub to use for testing. 
     */
    private class AbstractObjectiveStub extends AbstractObjective {
        
        /**
         * Always returns zero. Not used for testing here.
         * 
         * @param solution ignored
         * @param data ignored
         * @return 0.0
         */
        @Override
        public double evaluate(Solution solution, Object data) {
            return 0.0;
        }
        
    }

}