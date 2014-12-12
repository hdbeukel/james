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

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 * Test simple validation.
 * 
 * @author <a href="mailto:herman.debeukelaer@ugent.be">Herman De Beukelaer</a>
 */
public class SimpleValidationTest {

    /**
     * Print message when starting tests.
     */
    @BeforeClass
    public static void setUpClass() {
        System.out.println("# Testing SimpleValidation ...");
    }

    /**
     * Print message when tests are complete.
     */
    @AfterClass
    public static void tearDownClass() {
        System.out.println("# Done testing SimpleValidation!");
    }

    /**
     * Test of passed method, of class SimpleValidation.
     */
    @Test
    public void testPassed() {
        
        System.out.println(" - test passed");
        
        SimpleValidation v = new SimpleValidation(true);
        assertTrue(v.passed());
        
        v = new SimpleValidation(false);
        assertFalse(v.passed());
        
    }

    /**
     * Test of toString method, of class SimpleValidation.
     */
    @Test
    public void testToString() {
       
        System.out.println(" - test toString");
        
        SimpleValidation v = new SimpleValidation(true);
        System.out.println("   >>> passed: " + v);
        
        v = new SimpleValidation(false);
        System.out.println("   >>> did not pass: " + v);
        
    }

}