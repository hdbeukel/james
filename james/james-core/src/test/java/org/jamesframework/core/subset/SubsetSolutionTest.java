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

package org.jamesframework.core.subset;

import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashSet;
import java.util.Random;
import java.util.Set;
import org.jamesframework.core.exceptions.SolutionModificationException;
import org.jamesframework.core.util.SetUtilities;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import static org.junit.Assert.*;
import org.junit.Before;

/**
 * Test SubsetSolution.
 * 
 * @author <a href="mailto:herman.debeukelaer@ugent.be">Herman De Beukelaer</a>
 */
public class SubsetSolutionTest {

    // subset solution used for testing
    private static SubsetSolution subsetSolution;
    
    // number of IDs
    private static final int NUM_IDS = 100;
    
    // random generator
    private static final Random RG = new Random();
    
    /**
     * Initialize a subset solution.
     */
    @BeforeClass
    public static void setUpClass() {
        System.out.println("# Testing SubsetSolution ...");
        // initialize set of all IDs = {0 ... NUM_IDS-1}
        Set<Integer> IDs = new HashSet<>();
        for(int i=0; i < NUM_IDS; i++){
            IDs.add(i);
        }
        // initialize subset solution with these IDs, none selected
        subsetSolution = new SubsetSolution(IDs);
    }

    /**
     * Print message when tests are complete.
     */
    @AfterClass
    public static void tearDownClass() {
        System.out.println("# Done testing SubsetSolution!");
    }
    
    /**
     * Deselect all IDs before running each test method.
     */
    @Before
    public void setUp() {
        // deselect all IDs so that every test starts with the same clean subset solution
        subsetSolution.deselectAll();
    }
    
    @Test
    public void testConstructor(){
        
        System.out.println(" - test constructor");
        
        boolean thrown;
        
        thrown = false;
        try {
            Set<Integer> ids = null;
            new SubsetSolution(ids);
        } catch (NullPointerException ex) {
            thrown = true;
        }
        assertTrue(thrown);
        
        thrown = false;
        try {
            Set<Integer> ids = new HashSet<>(Arrays.asList(1,null,3));
            new SubsetSolution(ids);
        } catch (NullPointerException ex) {
            thrown = true;
        }
        assertTrue(thrown);
        
        thrown = false;
        try {
            Set<Integer> ids = new HashSet<>(Arrays.asList(1,2,3));
            Set<Integer> sel = null;
            new SubsetSolution(ids, sel);
        } catch (NullPointerException ex) {
            thrown = true;
        }
        assertTrue(thrown);
        
        thrown = false;
        try {
            Set<Integer> ids = new HashSet<>(Arrays.asList(1,2,3));
            Set<Integer> sel = new HashSet<>(Arrays.asList(2,null));
            new SubsetSolution(ids, sel);
        } catch (NullPointerException ex) {
            thrown = true;
        }
        assertTrue(thrown);
        
        thrown = false;
        try {
            Set<Integer> ids = new HashSet<>(Arrays.asList(1,2,3));
            Set<Integer> sel = new HashSet<>(Arrays.asList(2,4));
            new SubsetSolution(ids, sel);
        } catch (IllegalArgumentException ex) {
            thrown = true;
        }
        assertTrue(thrown);
        
    }

    /**
     * Test of toString method, of class SubsetSolution.
     */
    @Test
    public void testToString(){
        
        System.out.println(" - test toString");
        
        // output empty subset solution
        System.out.println("    " + subsetSolution);
        
        // select some IDs and output again
        subsetSolution.selectAll(SetUtilities.getRandomSubset(subsetSolution.getAllIDs(), 10, RG));
        System.out.println("    " + subsetSolution);
        
    }
    
    
    /**
     * Test of select method, of class SubsetSolution.
     */
    @Test
    public void testSelect() {
        
        System.out.println(" - test select");
        
        // try to select non existing ID, should throw error
        boolean thrown = false;
        int nonExistingID = Collections.max(subsetSolution.getAllIDs()) + 7;
        try {
            subsetSolution.select(nonExistingID);
        } catch (SolutionModificationException ex){
            thrown = true;
        }
        assertTrue(thrown);

        Set<Integer> selected = new HashSet<>();
        Set<Integer> unselected = new HashSet<>(subsetSolution.getAllIDs());
        
        // select existing ID, should return true
        int toSelect = subsetSolution.getUnselectedIDs().iterator().next();
        assertTrue(subsetSolution.select(toSelect));
        selected.add(toSelect);
        unselected.remove(toSelect);
        
        // try to select same ID again, should return false
        assertFalse(subsetSolution.select(toSelect));
        
        // select 19 additional IDs
        for(int i=0; i<19; i++){
            // pick random ID to select
            int ID = SetUtilities.getRandomElement(subsetSolution.getUnselectedIDs(), RG);
            // select it
            subsetSolution.select(ID);
            selected.add(ID);
            unselected.remove(ID);
        }
        
        // verify set views and their sizes
        assertEquals(selected, subsetSolution.getSelectedIDs());
        assertEquals(unselected, subsetSolution.getUnselectedIDs());
        assertEquals(20, subsetSolution.getNumSelectedIDs());
        assertEquals(NUM_IDS - 20, subsetSolution.getNumUnselectedIDs());
        
    }

    /**
     * Test of deselect method, of class SubsetSolution.
     */
    @Test
    public void testDeselect() {
        
        System.out.println(" - test deselect");
        
        // try to deselect non existing ID, should throw error
        boolean thrown = false;
        int nonExistingID = Collections.max(subsetSolution.getAllIDs()) + 7;
        try {
            subsetSolution.deselect(nonExistingID);
        } catch (SolutionModificationException ex){
            thrown = true;
        }
        assertTrue(thrown);
        
        // select 20 IDs
        Set<Integer> selected = new HashSet<>();
        Set<Integer> unselected = new HashSet<>(subsetSolution.getAllIDs());
        for(int i=0; i<20; i++){
            // pick random ID to select
            int ID = SetUtilities.getRandomElement(subsetSolution.getUnselectedIDs(), RG);
            // select it
            subsetSolution.select(ID);
            selected.add(ID);
            unselected.remove(ID);
        }
        
        // deselect 10 random IDs which were just selected
        for(int i=0; i<10; i++){
            // pick random ID to deselect
            int ID = SetUtilities.getRandomElement(subsetSolution.getSelectedIDs(), RG);
            // deselect it
            subsetSolution.deselect(ID);
            selected.remove(ID);
            unselected.add(ID);
        }
        
        // verify set views and their sizes
        assertEquals(selected, subsetSolution.getSelectedIDs());
        assertEquals(unselected, subsetSolution.getUnselectedIDs());
        assertEquals(10, subsetSolution.getNumSelectedIDs());
        assertEquals(NUM_IDS - 10, subsetSolution.getNumUnselectedIDs());
        
        // try to deselect existing but non selected ID, should return false 
        int toDeselect = subsetSolution.getUnselectedIDs().iterator().next();
        assertFalse(subsetSolution.deselect(toDeselect));
        
    }

    /**
     * Test of selectAll method, of class SubsetSolution.
     */
    @Test
    public void testSelectAll_Collection() {
        
        System.out.println(" - test selectAll(Collection)");
        
        // try to select collection of IDs which contains a non existing ID, should throw error
        boolean thrown = false;
        int nonExistingID = Collections.max(subsetSolution.getAllIDs()) + 7;
        Set<Integer> toSelect = new HashSet<>();
        // add non existing ID to collection
        toSelect.add(nonExistingID);
        // add 10 more, existing IDs
        toSelect.addAll(SetUtilities.getRandomSubset(subsetSolution.getUnselectedIDs(), 10, RG));
        try {
            subsetSolution.selectAll(toSelect);
        } catch (SolutionModificationException ex){
            thrown = true;
        }
        assertTrue(thrown);
        // undo any selections performed before exception was thrown
        subsetSolution.deselectAll();
        
        // now try again but without the non existing ID
        toSelect.remove(nonExistingID);
        assertTrue(subsetSolution.selectAll(toSelect));
        assertEquals(10, subsetSolution.getNumSelectedIDs());
        assertEquals(NUM_IDS - 10, subsetSolution.getNumUnselectedIDs());
        
        // randomly remove 5 items from collection toSelect
        toSelect.removeAll(SetUtilities.getRandomSubset(toSelect, 5, RG));
        
        // try to add remaining 5 IDs again, should return false since all of them are already selected
        assertFalse(subsetSolution.selectAll(toSelect));
        
        // add random non selected ID to set toSelect
        toSelect.add(SetUtilities.getRandomElement(subsetSolution.getUnselectedIDs(), RG));
        
        // try to add collection again, should return true because one element was not selected before
        assertTrue(subsetSolution.selectAll(toSelect));
        assertEquals(11, subsetSolution.getNumSelectedIDs());
        assertEquals(NUM_IDS - 11, subsetSolution.getNumUnselectedIDs());
        
    }

    /**
     * Test of deselectAll method, of class SubsetSolution.
     */
    @Test
    public void testDeselectAll_Collection() {
        
        System.out.println(" - test deselectAll(Collection)");
        
        // select 10 randomly chosen IDs
        Set<Integer> s = SetUtilities.getRandomSubset(subsetSolution.getUnselectedIDs(), 10, RG);
        assertTrue(subsetSolution.selectAll(s));
        assertEquals(10, subsetSolution.getNumSelectedIDs());
        assertEquals(NUM_IDS - 10, subsetSolution.getNumUnselectedIDs());
        
        // add non existing ID to collection s
        int nonExisting = Collections.max(subsetSolution.getAllIDs()) + 7;
        s.add(nonExisting);
        
        // try to deselect all previously selected IDs + non existing ID, should throw error
        boolean thrown = false;
        
        try {
            subsetSolution.deselectAll(s);
        } catch (SolutionModificationException ex){
            thrown = true;
        }
        assertTrue(thrown);
        
        // remove non existing ID from s
        s.remove(nonExisting);
        // reselect the other IDs in case any were deselected before error was thrown
        subsetSolution.selectAll(s);
        
        // try to deselect 2 selected IDs and 1 non selected ID, should return true
        Set<Integer> toDeselect = SetUtilities.getRandomSubset(s, 2, RG);
        toDeselect.add(SetUtilities.getRandomElement(subsetSolution.getUnselectedIDs(), RG));
        assertTrue(subsetSolution.deselectAll(toDeselect));
        assertEquals(8, subsetSolution.getNumSelectedIDs());
        assertEquals(NUM_IDS - 8, subsetSolution.getNumUnselectedIDs());
        
        // try to deselect 2 unselected IDs, should return false
        toDeselect = SetUtilities.getRandomSubset(subsetSolution.getUnselectedIDs(), 2, RG);
        assertFalse(subsetSolution.deselectAll(toDeselect));
        
    }

    /**
     * Test of selectAll method, of class SubsetSolution.
     */
    @Test
    public void testSelectAll_0args() {
        
        System.out.println(" - test selectAll");
        
        // select all IDs
        subsetSolution.selectAll();
        assertEquals(NUM_IDS, subsetSolution.getNumSelectedIDs());
        assertEquals(0, subsetSolution.getNumUnselectedIDs());
        
    }

    /**
     * Test of deselectAll method, of class SubsetSolution.
     */
    @Test
    public void testDeselectAll_0args() {
        
        System.out.println(" - test deselectAll");
        
        final int repeats = 100;
        for(int r=0; r<repeats; r++){
            // randomly select some IDs
            int num = RG.nextInt(NUM_IDS) + 1;
            subsetSolution.selectAll(SetUtilities.getRandomSubset(subsetSolution.getAllIDs(), num, RG));
            // deselect all IDs
            subsetSolution.deselectAll();
            assertEquals(0, subsetSolution.getNumSelectedIDs());
            assertEquals(NUM_IDS, subsetSolution.getNumUnselectedIDs());
        }
        
    }

    /**
     * Test of getSelectedIDs method, of class SubsetSolution.
     */
    @Test
    public void testGetSelectedIDs() {
        
        System.out.println(" - test getSelectedIDs");
        
        final int repeats = 100;
        Set<Integer> toSelect;
        for(int r=0; r<repeats; r++){
            // randomly select some IDs
            int num = RG.nextInt(NUM_IDS) + 1;
            toSelect = SetUtilities.getRandomSubset(subsetSolution.getAllIDs(), num, RG);
            subsetSolution.selectAll(toSelect);
            // verify
            assertEquals(toSelect, subsetSolution.getSelectedIDs());
            // deselect all
            subsetSolution.deselectAll();
        }
        
        // try to modify returned set (should result in error; views are unmodifiable)
        boolean thrown = false;
        try {
            subsetSolution.getSelectedIDs().clear();
        } catch (UnsupportedOperationException ex) {
            thrown = true;
        }
        assertTrue(thrown);
        thrown =  false;
        try {
            subsetSolution.getSelectedIDs().add(7);
        } catch (UnsupportedOperationException ex) {
            thrown = true;
        }
        assertTrue(thrown);
        thrown =  false;
        try {
            subsetSolution.getSelectedIDs().addAll(subsetSolution.getAllIDs());
        } catch (UnsupportedOperationException ex) {
            thrown = true;
        }
        assertTrue(thrown);
        thrown =  false;
        try {
            subsetSolution.getSelectedIDs().remove(4);
        } catch (UnsupportedOperationException ex) {
            thrown = true;
        }
        assertTrue(thrown);
        thrown =  false;
        try {
            subsetSolution.getSelectedIDs().removeAll(subsetSolution.getAllIDs());
        } catch (UnsupportedOperationException ex) {
            thrown = true;
        }
        assertTrue(thrown);
        thrown =  false;
        try {
            subsetSolution.getSelectedIDs().retainAll(subsetSolution.getAllIDs());
        } catch (UnsupportedOperationException ex) {
            thrown = true;
        }
        assertTrue(thrown);
        
    }

    /**
     * Test of getUnselectedIDs method, of class SubsetSolution.
     */
    @Test
    public void testGetUnselectedIDs() {
        
        System.out.println(" - test getUnselectedIDs");
        
        final int repeats = 100;
        Set<Integer> toSelect, unselected;
        for(int r=0; r<repeats; r++){
            // randomly select some IDs
            int num = RG.nextInt(NUM_IDS) + 1;
            toSelect = SetUtilities.getRandomSubset(subsetSolution.getAllIDs(), num, RG);
            subsetSolution.selectAll(toSelect);
            // verify
            unselected = new HashSet<>(subsetSolution.getAllIDs());
            unselected.removeAll(toSelect);
            assertEquals(unselected, subsetSolution.getUnselectedIDs());
            // deselect all
            subsetSolution.deselectAll();
        }
        
        // try to modify returned set (should result in error; views are unmodifiable)
        boolean thrown = false;
        try {
            subsetSolution.getUnselectedIDs().clear();
        } catch (UnsupportedOperationException ex) {
            thrown = true;
        }
        assertTrue(thrown);
        thrown =  false;
        try {
            subsetSolution.getUnselectedIDs().add(7);
        } catch (UnsupportedOperationException ex) {
            thrown = true;
        }
        assertTrue(thrown);
        thrown =  false;
        try {
            subsetSolution.getUnselectedIDs().addAll(subsetSolution.getAllIDs());
        } catch (UnsupportedOperationException ex) {
            thrown = true;
        }
        assertTrue(thrown);
        thrown =  false;
        try {
            subsetSolution.getUnselectedIDs().remove(4);
        } catch (UnsupportedOperationException ex) {
            thrown = true;
        }
        assertTrue(thrown);
        thrown =  false;
        try {
            subsetSolution.getUnselectedIDs().removeAll(subsetSolution.getAllIDs());
        } catch (UnsupportedOperationException ex) {
            thrown = true;
        }
        assertTrue(thrown);
        thrown =  false;
        try {
            subsetSolution.getUnselectedIDs().retainAll(subsetSolution.getAllIDs());
        } catch (UnsupportedOperationException ex) {
            thrown = true;
        }
        assertTrue(thrown);
        
    }

    /**
     * Test of getAllIDs method, of class SubsetSolution.
     */
    @Test
    public void testGetAllIDs() {
        
        System.out.println(" - test getAllIDs");
        
        final int repeats = 100;
        Set<Integer> s;
        for(int r=0; r<repeats; r++){
            // randomly select some IDs
            int num = RG.nextInt(NUM_IDS) + 1;
            s = SetUtilities.getRandomSubset(subsetSolution.getAllIDs(), num, RG);
            subsetSolution.selectAll(s);
            // verify
            s.addAll(subsetSolution.getUnselectedIDs());
            assertEquals(s, subsetSolution.getAllIDs());
            // deselect all
            subsetSolution.deselectAll();
        }
        
        // try to modify returned set (should result in error; views are unmodifiable)
        boolean thrown = false;
        try {
            subsetSolution.getAllIDs().clear();
        } catch (UnsupportedOperationException ex) {
            thrown = true;
        }
        assertTrue(thrown);
        thrown =  false;
        try {
            subsetSolution.getAllIDs().add(7);
        } catch (UnsupportedOperationException ex) {
            thrown = true;
        }
        assertTrue(thrown);
        thrown =  false;
        try {
            subsetSolution.getAllIDs().addAll(subsetSolution.getAllIDs());
        } catch (UnsupportedOperationException ex) {
            thrown = true;
        }
        assertTrue(thrown);
        thrown =  false;
        try {
            subsetSolution.getAllIDs().remove(4);
        } catch (UnsupportedOperationException ex) {
            thrown = true;
        }
        assertTrue(thrown);
        thrown =  false;
        try {
            subsetSolution.getAllIDs().removeAll(subsetSolution.getAllIDs());
        } catch (UnsupportedOperationException ex) {
            thrown = true;
        }
        assertTrue(thrown);
        thrown =  false;
        try {
            subsetSolution.getAllIDs().retainAll(subsetSolution.getAllIDs());
        } catch (UnsupportedOperationException ex) {
            thrown = true;
        }
        assertTrue(thrown);
        
    }

    /**
     * Test of getNumSelectedIDs method, of class SubsetSolution.
     */
    @Test
    public void testGetNumSelectedIDs() {
        
        System.out.println(" - test numSelectedIDs");
        
        final int repeats = 100;
        Set<Integer> s;
        for(int r=0; r<repeats; r++){
            // randomly select some IDs
            int num = RG.nextInt(NUM_IDS) + 1;
            s = SetUtilities.getRandomSubset(subsetSolution.getAllIDs(), num, RG);
            subsetSolution.selectAll(s);
            // verify
            assertEquals(num, subsetSolution.getNumSelectedIDs());
            // deselect all
            subsetSolution.deselectAll();
        }
        
    }

    /**
     * Test of getNumUnselectedIDs method, of class SubsetSolution.
     */
    @Test
    public void testGetNumUnselectedIDs() {
        
        System.out.println(" - test numUnselectedIDs");
        
        final int repeats = 100;
        Set<Integer> s;
        for(int r=0; r<repeats; r++){
            // randomly select some IDs
            int num = RG.nextInt(NUM_IDS) + 1;
            s = SetUtilities.getRandomSubset(subsetSolution.getAllIDs(), num, RG);
            subsetSolution.selectAll(s);            
            // verify
            assertEquals(NUM_IDS - num, subsetSolution.getNumUnselectedIDs());
            // deselect all
            subsetSolution.deselectAll();
        }
        
    }

    /**
     * Test of getTotalNumIDs method, of class SubsetSolution.
     */
    @Test
    public void testGetTotalNumIDs() {
        
        System.out.println(" - test getTotalNumIDs");
        
        final int repeats = 100;
        Set<Integer> s;
        for(int r=0; r<repeats; r++){
            // randomly select some IDs
            int num = RG.nextInt(NUM_IDS) + 1;
            s = SetUtilities.getRandomSubset(subsetSolution.getAllIDs(), num, RG);
            subsetSolution.selectAll(s);            
            // verify
            int total = subsetSolution.getNumSelectedIDs() + subsetSolution.getNumUnselectedIDs();
            assertEquals(total, subsetSolution.getTotalNumIDs());
            assertEquals(NUM_IDS, subsetSolution.getTotalNumIDs());
            // deselect all
            subsetSolution.deselectAll();
        }
        
    }

    /**
     * Test of equals method, of class SubsetSolution.
     */
    @Test
    public void testEquals() {
        
        System.out.println(" - test equals");
        
        final int repeats = 100;
        Set<Integer> s;
        SubsetSolution other;
        for(int r=0; r<repeats; r++){
            // randomly select some IDs
            int num = RG.nextInt(NUM_IDS) + 1;
            s = SetUtilities.getRandomSubset(subsetSolution.getAllIDs(), num, RG);
            subsetSolution.selectAll(s);            
            // create other subset solution with same selected/unselected IDs
            other = new SubsetSolution(subsetSolution.getAllIDs());
            other.selectAll(subsetSolution.getSelectedIDs());
            // verify equality
            assertTrue(subsetSolution.equals(other));
            assertTrue(other.equals(subsetSolution));
            assertEquals(subsetSolution, other);
            // deselect all
            subsetSolution.deselectAll();
        }
        
        int num=7;
        s = SetUtilities.getRandomSubset(subsetSolution.getAllIDs(), num, RG);
        subsetSolution.selectAll(s);     
        
        assertNotEquals(null, s);
        assertNotEquals("Trudy", s);
        
    }

    /**
     * Test of hashCode method, of class SubsetSolution.
     */
    @Test
    public void testHashCode() {
        
        System.out.println(" - test hashCode");
        
        final int repeats = 100;
        Set<Integer> s;
        SubsetSolution other;
        for(int r=0; r<repeats; r++){
            // randomly select some IDs
            int num = RG.nextInt(NUM_IDS) + 1;
            s = SetUtilities.getRandomSubset(subsetSolution.getAllIDs(), num, RG);
            subsetSolution.selectAll(s);            
            // create other subset solution with same selected/unselected IDs
            other = new SubsetSolution(subsetSolution.getAllIDs());
            other.selectAll(subsetSolution.getSelectedIDs());
            // check same hash code
            assertEquals(subsetSolution.hashCode(), other.hashCode());
            // deselect all
            subsetSolution.deselectAll();
        }
        
        // use in hash-based collections
        Set<SubsetSolution> solutions = new HashSet<>();
        // add subset solution to solutions
        assertTrue(solutions.add(subsetSolution));
        // try to readd, should return false
        assertFalse(solutions.add(subsetSolution));
        
        // create other equal solution
        other = new SubsetSolution(subsetSolution.getAllIDs());
        other.selectAll(subsetSolution.getSelectedIDs());
        // try to add, should return false (already present)
        assertFalse(solutions.add(other));
        // try to remove, should return true and leave solutions set empty
        assertTrue(solutions.remove(other));
        assertTrue(solutions.isEmpty());
        
    }
    
    /**
     * Test constructors of SubsetSolution with selected IDs.
     */
    @Test
    public void testConstructorsWithSelectedIDs(){
        
        System.out.println(" - test constructors with selected IDs");
        
        // take random subset of all IDs
        Set<Integer> random = SetUtilities.getRandomSubset(subsetSolution.getAllIDs(), NUM_IDS/2, RG);
        
        // select these IDs
        subsetSolution.deselectAll();
        subsetSolution.selectAll(random);
        
        // create new subset solution with this selection
        SubsetSolution sol = new SubsetSolution(subsetSolution.getAllIDs(), random);
        
        // verify
        assertEquals(subsetSolution, sol);
        
        // repeat with sorted sets
        SubsetSolution sol2 = new SubsetSolution(subsetSolution.getAllIDs(), random, true);
        
        // verify
        assertEquals(subsetSolution, sol);
        assertEquals(subsetSolution, sol2);
        assertEquals(sol, sol2);
        
    }
    
    /**
     * Test sorted subset solution.
     */
    @Test
    public void testSorted(){
        
        System.out.println(" - test sorted subset solution");
        
        // overwrite subset solution with sorted subset solution
        subsetSolution = new SubsetSolution(subsetSolution.getAllIDs(), true);
        
        // repeat
        for(int i=0; i<100; i++){
            // take random subset of all IDs
            Set<Integer> random = SetUtilities.getRandomSubset(subsetSolution.getAllIDs(), NUM_IDS/2, RG);
            // select them
            subsetSolution.selectAll(random);
            // verify
            Integer prevID = null;
            for(int ID : subsetSolution.getAllIDs()){
                if(prevID != null){
                    assertTrue(ID > prevID);
                }
                prevID = ID;
            }
            prevID = null;
            for(int ID : subsetSolution.getSelectedIDs()){
                if(prevID != null){
                    assertTrue(ID > prevID);
                }
                prevID = ID;
            }
            prevID = null;
            for(int ID : subsetSolution.getUnselectedIDs()){
                if(prevID != null){
                    assertTrue(ID > prevID);
                }
                prevID = ID;
            }
        }
        
        // again with reverse order
        subsetSolution = new SubsetSolution(subsetSolution.getAllIDs(), Comparator.reverseOrder());
        
        // repeat
        for(int i=0; i<100; i++){
            // take random subset of all IDs
            Set<Integer> random = SetUtilities.getRandomSubset(subsetSolution.getAllIDs(), NUM_IDS/2, RG);
            // select them
            subsetSolution.selectAll(random);
            // verify
            Integer prevID = null;
            for(int ID : subsetSolution.getAllIDs()){
                if(prevID != null){
                    assertTrue(ID < prevID);
                }
                prevID = ID;
            }
            prevID = null;
            for(int ID : subsetSolution.getSelectedIDs()){
                if(prevID != null){
                    assertTrue(ID < prevID);
                }
                prevID = ID;
            }
            prevID = null;
            for(int ID : subsetSolution.getUnselectedIDs()){
                if(prevID != null){
                    assertTrue(ID < prevID);
                }
                prevID = ID;
            }
        }
        
    }

}