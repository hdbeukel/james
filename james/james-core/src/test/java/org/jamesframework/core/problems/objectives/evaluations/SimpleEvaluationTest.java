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

package org.jamesframework.core.problems.objectives.evaluations;

import org.jamesframework.test.util.TestConstants;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 * Test simple evaluation.
 * 
 * @author <a href="mailto:herman.debeukelaer@ugent.be">Herman De Beukelaer</a>
 */
public class SimpleEvaluationTest {

    /**
     * Print message when starting tests.
     */
    @BeforeClass
    public static void setUpClass() {
        System.out.println("# Testing SimpleEvaluation ...");
    }

    /**
     * Print message when tests are complete.
     */
    @AfterClass
    public static void tearDownClass() {
        System.out.println("# Done testing SimpleEvaluation!");
    }

    /**
     * Test of getValue method, of class SimpleEvaluation.
     */
    @Test
    public void testGetValue() {
        
        System.out.println(" - test getValue");
        
        SimpleEvaluation e = new SimpleEvaluation(123.456);
        assertEquals(123.456, e.getValue(), TestConstants.DOUBLE_COMPARISON_PRECISION);
        
    }

    /**
     * Test of toString method, of class SimpleEvaluation.
     */
    @Test
    public void testToString() {
       
        System.out.println(" - test toString");
        
        SimpleEvaluation e = new SimpleEvaluation(123.456);
        System.out.println("   >>> evaluation: " + e);
        
    }

}