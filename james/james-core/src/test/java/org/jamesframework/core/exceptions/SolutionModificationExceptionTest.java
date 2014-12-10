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

package org.jamesframework.core.exceptions;

import java.util.Collections;
import org.jamesframework.core.problems.Solution;
import org.jamesframework.core.subset.SubsetSolution;
import org.junit.AfterClass;
import org.junit.Test;
import static org.junit.Assert.*;
import org.junit.BeforeClass;

/**
 * Test solution modification exception.
 * 
 * @author <a href="mailto:herman.debeukelaer@ugent.be">Herman De Beukelaer</a>
 */
public class SolutionModificationExceptionTest {

    static private final String MSG = "This is all your fault!";
    static private final Exception CAUSE = new RuntimeException("I caused it ...");
    static private final Solution SOL = new SubsetSolution(Collections.EMPTY_SET);

    /**
     * Print message when starting tests.
     */
    @BeforeClass
    public static void setUpClass() {
        System.out.println("# Testing SolutionModificationException ...");
    }

    /**
     * Print message when tests are complete.
     */
    @AfterClass
    public static void tearDownClass() {
        System.out.println("# Done testing SolutionModificationException!");
    }
    
    @Test
    public void testConstructor1() {
        SolutionModificationException ex = new SolutionModificationException(SOL);
        assertNull(ex.getCause());
        assertNull(ex.getMessage());
        assertEquals(SOL, ex.getSolution());
    }
    
    @Test
    public void testConstructor2() {
        SolutionModificationException ex = new SolutionModificationException(MSG, SOL);
        assertNull(ex.getCause());
        assertEquals(MSG, ex.getMessage());
        assertEquals(SOL, ex.getSolution());
    }
    
    @Test
    public void testConstructor3() {
        SolutionModificationException ex = new SolutionModificationException(CAUSE, SOL);
        assertEquals(CAUSE, ex.getCause());
        assertEquals(CAUSE.toString(), ex.getMessage());
        assertEquals(SOL, ex.getSolution());
    }
    
    @Test
    public void testConstructor4() {
        SolutionModificationException ex = new SolutionModificationException(MSG, CAUSE, SOL);
        assertEquals(CAUSE, ex.getCause());
        assertEquals(MSG, ex.getMessage());
        assertEquals(SOL, ex.getSolution());
    }

}