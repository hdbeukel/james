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

package org.jamesframework.core.problems.solutions;

import org.jamesframework.core.problems.Solution;
import org.jamesframework.core.exceptions.SolutionCopyException;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 * Test Solution.
 * 
 * @author <a href="mailto:herman.debeukelaer@ugent.be">Herman De Beukelaer</a>
 */
public class SolutionTest {
    
    /**
     * Print message when test starts.
     */
    @BeforeClass
    public static void setUpClass() {
        System.out.println("# Testing Solution ...");
    }

    /**
     * Print message when test completes.
     */
    @AfterClass
    public static void tearDownClass() {
        System.out.println("# Done testing Solution!");
    }

    /**
     * Test of checkedCopy method, of class Solution.
     */
    @Test
    public void testCheckedCopy(){
        
        System.out.println(" - test checkedCopy");
        
        SolutionDummy sol = new SolutionDummy();
        SolutionDummy copy = Solution.checkedCopy(sol);
        
        boolean thrown = false;
        try {
            WrongTypeCopy sol2 = new WrongTypeCopy();
            WrongTypeCopy copy2 = Solution.checkedCopy(sol2);
        } catch (SolutionCopyException ex){
            thrown = true;
            //System.out.println("   >>> " + ex.getMessage());
        }
        assertTrue(thrown);
        
        thrown = false;
        try {
            InheritedCopy sol3 = new InheritedCopy();
            InheritedCopy copy3 = Solution.checkedCopy(sol3);
        } catch (SolutionCopyException ex){
            thrown = true;
            //System.out.println("   >>> " + ex.getMessage());
        }
        assertTrue(thrown);
        
    }
    
    /* private solution dummies for testing of checked copy */
    
    private class SolutionDummy extends Solution {

        @Override
        public Solution copy() {
            // correct type
            return new SolutionDummy();
        }

        // not used
        @Override
        public boolean equals(Object sol) { return false; }
        @Override
        public int hashCode() { return 0; }
        
    }
    
    private class WrongTypeCopy extends Solution {

        @Override
        public Solution copy() {
            // wrong type
            return new SolutionDummy();
        }
        
        // not used
        @Override
        public boolean equals(Object sol) { return false; }
        @Override
        public int hashCode() { return 0; }
        
    }
    
    private class InheritedCopy extends SolutionDummy {
        // inherits copy (wrong return type)
    }

}