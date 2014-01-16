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

package org.jamesframework.core.util;

import java.util.HashSet;
import java.util.Random;
import java.util.Set;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 * Test SetUtilities.
 * 
 * @author Herman De Beukelaer <herman.debeukelaer@ugent.be>
 */
public class SetUtilitiesTest {

    // set used for testing
    private static Set<Integer> set;
    
    // size of set
    private static final int SET_SIZE = 100;
    
    // random generator
    private static final Random RG = new Random();
    
    /**
     * Initialize a set to work with.
     */
    @BeforeClass
    public static void setUpClass() {
        System.out.println("# Testing SetUtilities ...");
        set = new HashSet<>();
        for(int i=0; i<SET_SIZE; i++){
            set.add(i);
        }
    }

    /**
     * Print message when tests are complete.
     */
    @AfterClass
    public static void tearDownClass() {
        System.out.println("# Done testing SetUtilities!");
    }

    /**
     * Test of getRandomElement method, of class SetUtilities.
     */
    @Test
    public void testGetRandomElement() {
        
        System.out.println(" - testing getRandomElement");
        
        final int REPEATS = 1000;
        
        for(int i=0; i<REPEATS; i++){
            int selected = SetUtilities.getRandomElement(set, RG);
            assertTrue(set.contains(selected));
        }
        
    }

    /**
     * Test of getRandomSubset method, of class SetUtilities.
     */
    @Test
    public void testGetRandomSubset() {
        
        System.out.println(" - testing getRandomSubset");
        
        // try with invalid (negative) size parameter, should result in exception
        boolean thrown = false;
        try {
            SetUtilities.getRandomSubset(set, -1, RG);
        } catch (IllegalArgumentException ex) {
            thrown = true;
        }
        assertTrue(thrown);
        
        // try again with size larger than set size
        thrown = false;
        try {
            SetUtilities.getRandomSubset(set, set.size()+1, RG);
        } catch (IllegalArgumentException ex) {
            thrown = true;
        }
        assertTrue(thrown);
        
        // verify that no exceptions are thrown in case of extreme size
        Set<Integer> subset;
        subset = SetUtilities.getRandomSubset(set, 0, RG);
        assertTrue(subset.isEmpty());
        subset = SetUtilities.getRandomSubset(set, set.size(), RG);
        assertEquals(set, subset);
        
        // repeat some random subset samplings
        final int REPEATS = 100;
        
        for(int i=0; i<REPEATS; i++){
            int size = RG.nextInt(set.size()+1);
            subset = SetUtilities.getRandomSubset(set, size, RG);
            assertEquals(size, subset.size());
            assertTrue(set.containsAll(subset));
        }
        
    }

}