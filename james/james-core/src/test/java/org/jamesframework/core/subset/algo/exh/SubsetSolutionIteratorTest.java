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

package org.jamesframework.core.subset.algo.exh;

import java.util.Collections;
import java.util.HashSet;
import java.util.NoSuchElementException;
import java.util.Set;
import org.jamesframework.core.search.algo.exh.SolutionIterator;
import org.jamesframework.core.subset.SubsetSolution;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import static org.junit.Assert.*;
import org.junit.Before;

/**
 * Test subset solution iterator.
 * 
 * @author <a href="mailto:herman.debeukelaer@ugent.be">Herman De Beukelaer</a>
 */
public class SubsetSolutionIteratorTest {

    // set of IDs to select from
    private Set<Integer> IDs;
    // number of IDs to select from
    private static final int NUM_IDS = 5;
    // IDs to select from = {ID_OFFSET, ID_OFFSET+1, ..., ID_OFFSET+NUM_IDS-1}
    private static final int ID_OFFSET = 1;
    
    /**
     * Print message when starting tests.
     */
    @BeforeClass
    public static void setUpClass() {
        System.out.println("# Testing SubsetSolutionIterator ...");
    }

    /**
     * Print message when tests are complete.
     */
    @AfterClass
    public static void tearDownClass() {
        System.out.println("# Done testing SubsetSolutionIterator!");
    }
    
    /**
     * Create set of IDs to select from.
     */
    @Before
    public void setUp(){
        IDs = new HashSet<>();
        for(int i=0; i<NUM_IDS; i++){
            IDs.add(ID_OFFSET + i);
        }
    }
    
    @Test
    public void testConstructor(){
        System.out.println(" - test constructor");
        
        boolean thrown;
        
        thrown = false;
        try {
            new SubsetSolutionIterator(null, NUM_IDS/2);
        } catch (NullPointerException ex) {
            thrown = true;
        }
        assertTrue(thrown);
        
        thrown = false;
        try {
            new SubsetSolutionIterator(Collections.emptySet(), NUM_IDS/2);
        } catch (IllegalArgumentException ex) {
            thrown = true;
        }
        assertTrue(thrown);
        
        thrown = false;
        try {
            new SubsetSolutionIterator(IDs, -1);
        } catch (IllegalArgumentException ex) {
            thrown = true;
        }
        assertTrue(thrown);
        
        thrown = false;
        try {
            new SubsetSolutionIterator(IDs, NUM_IDS+1);
        } catch (IllegalArgumentException ex) {
            thrown = true;
        }
        assertTrue(thrown);
        
        thrown = false;
        try {
            new SubsetSolutionIterator(IDs, -1, 3);
        } catch (IllegalArgumentException ex) {
            thrown = true;
        }
        assertTrue(thrown);
        
        thrown = false;
        try {
            new SubsetSolutionIterator(IDs, NUM_IDS+1, 3);
        } catch (IllegalArgumentException ex) {
            thrown = true;
        }
        assertTrue(thrown);
        
        thrown = false;
        try {
            new SubsetSolutionIterator(IDs, 4, 3);
        } catch (IllegalArgumentException ex) {
            thrown = true;
        }
        assertTrue(thrown);
        
    }
    
    /**
     * Test SubsetSolutionIterator.
     */
    @Test
    public void testFixedSize2() {
        
        System.out.println(" - test fixed subset size = 2");
        
        // create iterator
        SolutionIterator<SubsetSolution> it = new SubsetSolutionIterator(IDs, 2);
        
        int generated=0;
        while(it.hasNext()){
            System.out.println("   >>> generated: " + it.next());
            generated++;
        }
        // verify number of generated solutions
        assertEquals(10, generated);
        
        boolean thrown = false;
        try {
            it.next();
        } catch (NoSuchElementException ex) {
            thrown = true;
        }
        assertTrue(thrown);
        
    }
    
    /**
     * Test SubsetSolutionIterator.
     */
    @Test
    public void testSizeOne() {
        
        System.out.println(" - test fixed subset size = 1");
        
        // create iterator
        SolutionIterator<SubsetSolution> it = new SubsetSolutionIterator(IDs, 1);
        
        int generated=0;
        while(it.hasNext()){
            System.out.println("   >>> generated: " + it.next());
            generated++;
        }
        // verify number of generated solutions
        assertEquals(NUM_IDS, generated);
        
        boolean thrown = false;
        try {
            it.next();
        } catch (NoSuchElementException ex) {
            thrown = true;
        }
        assertTrue(thrown);
        
    }
    
    /**
     * Test SubsetSolutionIterator.
     */
    @Test
    public void testSizeZero() {
        
        System.out.println(" - test fixed subset size = 0");
        
        // create iterator
        SolutionIterator<SubsetSolution> it = new SubsetSolutionIterator(IDs, 0);
        
        int generated=0;
        while(it.hasNext()){
            System.out.println("   >>> generated: " + it.next());
            generated++;
        }
        // verify number of generated solutions
        assertEquals(1, generated);
        
        boolean thrown = false;
        try {
            it.next();
        } catch (NoSuchElementException ex) {
            thrown = true;
        }
        assertTrue(thrown);
        
    }
    
    /**
     * Test SubsetSolutionIterator.
     */
    @Test
    public void testFullSize() {
        
        System.out.println(" - test fixed subset size = |IDs|");
        
        // create iterator
        SolutionIterator<SubsetSolution> it = new SubsetSolutionIterator(IDs, NUM_IDS);
        
        int generated=0;
        while(it.hasNext()){
            System.out.println("   >>> generated: " + it.next());
            generated++;
        }
        // verify number of generated solutions
        assertEquals(1, generated);
        
        boolean thrown = false;
        try {
            it.next();
        } catch (NoSuchElementException ex) {
            thrown = true;
        }
        assertTrue(thrown);
        
    }
    
    /**
     * Test SubsetSolutionIterator.
     */
    @Test
    public void testFixedSize3() {
        
        System.out.println(" - test fixed subset size = 3");
        
        // create iterator
        SolutionIterator<SubsetSolution> it = new SubsetSolutionIterator(IDs, 3);
        
        int generated=0;
        while(it.hasNext()){
            System.out.println("   >>> generated: " + it.next());
            generated++;
        }
        // verify number of generated solutions
        assertEquals(10, generated);
        
        boolean thrown = false;
        try {
            it.next();
        } catch (NoSuchElementException ex) {
            thrown = true;
        }
        assertTrue(thrown);
        
    }
    
    /**
     * Test SubsetSolutionIterator.
     */
    @Test
    public void testFixedSize4() {
        
        System.out.println(" - test fixed subset size = 4");
        
        // create iterator
        SolutionIterator<SubsetSolution> it = new SubsetSolutionIterator(IDs, 4);
        
        int generated=0;
        while(it.hasNext()){
            System.out.println("   >>> generated: " + it.next());
            generated++;
        }
        // verify number of generated solutions
        assertEquals(5, generated);
        
        boolean thrown = false;
        try {
            it.next();
        } catch (NoSuchElementException ex) {
            thrown = true;
        }
        assertTrue(thrown);
        
    }
    
    /**
     * Test SubsetSolutionIterator.
     */
    @Test
    public void testVariableSize() {
        
        System.out.println(" - test variable subset size = [1,3]");
        
        // create iterator
        SolutionIterator<SubsetSolution> it = new SubsetSolutionIterator(IDs, 1,3);
        
        int generated=0;
        while(it.hasNext()){
            System.out.println("   >>> generated: " + it.next());
            generated++;
        }
        // verify number of generated solutions
        assertEquals(25, generated);
        
        boolean thrown = false;
        try {
            it.next();
        } catch (NoSuchElementException ex) {
            thrown = true;
        }
        assertTrue(thrown);
        
    }
    
    /**
     * Test SubsetSolutionIterator.
     */
    @Test
    public void testGenerateAllSizes() {
        
        System.out.println(" - test variable subset size = [0,|IDs|]");
        
        // create iterator
        SolutionIterator<SubsetSolution> it = new SubsetSolutionIterator(IDs, 0, NUM_IDS);
        
        int generated=0;
        while(it.hasNext()){
            System.out.println("   >>> generated: " + it.next());
            generated++;
        }
        // verify number of generated solutions
        assertEquals(1 << NUM_IDS, generated);
        
        boolean thrown = false;
        try {
            it.next();
        } catch (NoSuchElementException ex) {
            thrown = true;
        }
        assertTrue(thrown);
        
    }

}