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

package org.jamesframework.core.subset.validations;

import org.jamesframework.core.problems.constraints.validations.SimpleValidation;
import org.jamesframework.core.problems.constraints.validations.Validation;
import org.jamesframework.core.search.SearchTestTemplate;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 * Test subset validation.
 * 
 * @author <a href="mailto:herman.debeukelaer@ugent.be">Herman De Beukelaer</a>
 */
public class SubsetValidationTest {

    /**
     * Print message when starting tests.
     */
    @BeforeClass
    public static void setUpClass() {
        System.out.println("# Testing SubsetValidation ...");
        SearchTestTemplate.setUpClass();
    }

    /**
     * Print message when tests are complete.
     */
    @AfterClass
    public static void tearDownClass() {
        System.out.println("# Done testing SubsetValidation!");
    }

    /**
     * Test of validSize method, of class SubsetValidation.
     */
    @Test
    public void testValidSize() {
        
        System.out.println(" - test validSize");
        
        SubsetValidation val;
        
        val = new SubsetValidation(true, null);
        assertTrue(val.validSize());
        
        val = new SubsetValidation(false, null);
        assertFalse(val.validSize());
        
    }

    /**
     * Test of getConstraintValidation method, of class SubsetValidation.
     */
    @Test
    public void testGetConstraintValidation() {
        
        System.out.println(" - test getConstraintValidation");
        
        SubsetValidation val;
        Validation cv;
        
        val = new SubsetValidation(true, null);
        assertNull(val.getConstraintValidation());
        
        cv = new SimpleValidation(true);
        val = new SubsetValidation(true, cv);
        assertEquals(cv, val.getConstraintValidation());
        
        cv = new SimpleValidation(false);
        val = new SubsetValidation(true, cv);
        assertEquals(cv, val.getConstraintValidation());
        
    }

    /**
     * Test of passed method, of class SubsetValidation.
     */
    @Test
    public void testPassed() {
        
        System.out.println(" - test passed");
        
        SubsetValidation val;
        Validation cv;
        
        val = new SubsetValidation(true, null);
        assertTrue(val.passed());
        assertTrue(val.passed(true));
        assertTrue(val.passed(false));
        
        val = new SubsetValidation(false, null);
        assertFalse(val.passed());
        assertFalse(val.passed(true));
        assertTrue(val.passed(false));
        
        cv = new SimpleValidation(true);
        val = new SubsetValidation(true, cv);
        assertTrue(val.passed());
        assertTrue(val.passed(true));
        assertTrue(val.passed(false));
        
        cv = new SimpleValidation(true);
        val = new SubsetValidation(false, cv);
        assertFalse(val.passed());
        assertFalse(val.passed(true));
        assertTrue(val.passed(false));
        
        cv = new SimpleValidation(false);
        val = new SubsetValidation(true, cv);
        assertFalse(val.passed());
        assertFalse(val.passed(true));
        assertFalse(val.passed(false));
        
        cv = new SimpleValidation(false);
        val = new SubsetValidation(false, cv);
        assertFalse(val.passed());
        assertFalse(val.passed(true));
        assertFalse(val.passed(false));
        
    }

}