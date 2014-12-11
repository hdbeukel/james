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

package org.jamesframework.core.problems.constraints.validations;

import org.jamesframework.test.util.TestConstants;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 * Test simple penalizing validation.
 * 
 * @author <a href="mailto:herman.debeukelaer@ugent.be">Herman De Beukelaer</a>
 */
public class SimplePenalizingValidationTest {

    /**
     * Print message when starting tests.
     */
    @BeforeClass
    public static void setUpClass() {
        System.out.println("# Testing SimplePenalizingValidation ...");
    }

    /**
     * Print message when tests are complete.
     */
    @AfterClass
    public static void tearDownClass() {
        System.out.println("# Done testing SimplePenalizingValidation!");
    }

    /**
     * Test of FAILED method, of class SimplePenalizingValidation.
     */
    @Test
    public void testFAILED() {
       
        System.out.println(" - test static factory method FAILED");
        
        double penalty = 123.456;
        PenalizingValidation v = SimplePenalizingValidation.FAILED(penalty);
        assertEquals(penalty, v.getPenalty(), TestConstants.DOUBLE_COMPARISON_PRECISION);
        
    }
    
    @Test
    public void testConstructor(){
        
        System.out.println(" - test constructor");
        
        boolean thrown;
        
        thrown = false;
        try {
            new SimplePenalizingValidation(false, 0.0);
        } catch (IllegalArgumentException ex){
            thrown = true;
        }
        assertTrue(thrown);
        
        thrown = false;
        try {
            new SimplePenalizingValidation(false, -3.6);
        } catch (IllegalArgumentException ex){
            thrown = true;
        }
        assertTrue(thrown);
        
        new SimplePenalizingValidation(false, 6.3);
        new SimplePenalizingValidation(true, 6.3);
        new SimplePenalizingValidation(true, -1.0);
        new SimplePenalizingValidation(true, 0.0);
        
    }

    /**
     * Test of passed method, of class SimplePenalizingValidation.
     */
    @Test
    public void testPassed() {
       
        System.out.println(" - test passed");
        
        Validation v = new SimplePenalizingValidation(true, 0);
        assertTrue(v.passed());
        
        v = new SimplePenalizingValidation(false, 123);
        assertFalse(v.passed());
        
    }

    /**
     * Test of getPenalty method, of class SimplePenalizingValidation.
     */
    @Test
    public void testGetPenalty() {
        
        System.out.println(" - test getPenalty");
        
        double penalty = 123.456;
        PenalizingValidation v = new SimplePenalizingValidation(false, penalty);
        assertEquals(penalty, v.getPenalty(), TestConstants.DOUBLE_COMPARISON_PRECISION);
        
        v = new SimplePenalizingValidation(true, penalty);
        assertEquals(0.0, v.getPenalty(), TestConstants.DOUBLE_COMPARISON_PRECISION);
        
        v = new SimplePenalizingValidation(true, -1);
        assertEquals(0.0, v.getPenalty(), TestConstants.DOUBLE_COMPARISON_PRECISION);
        
        v = new SimplePenalizingValidation(true, 0);
        assertEquals(0.0, v.getPenalty(), TestConstants.DOUBLE_COMPARISON_PRECISION);
        
    }

    /**
     * Test of toString method, of class SimplePenalizingValidation.
     */
    @Test
    public void testToString() {
        
        System.out.println(" - test toString");
        
        System.out.println("   >>> passed: " + SimplePenalizingValidation.PASSED);
        System.out.println("   >>> penalized: " + SimplePenalizingValidation.FAILED(123.456));
        
    }

}