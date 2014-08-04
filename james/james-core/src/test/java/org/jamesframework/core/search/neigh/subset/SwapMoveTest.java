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

package org.jamesframework.core.search.neigh.subset;

import java.util.HashSet;
import java.util.Iterator;
import java.util.Random;
import java.util.Set;
import org.jamesframework.core.exceptions.SolutionModificationException;
import org.jamesframework.core.problems.solutions.SubsetSolution;
import org.jamesframework.core.search.neigh.Move;
import org.jamesframework.core.util.SetUtilities;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import static org.junit.Assert.*;
import org.junit.Before;

/**
 * Test SwapMove (subset move).
 * 
 * @author <a href="mailto:herman.debeukelaer@ugent.be">Herman De Beukelaer</a>
 */
public class SwapMoveTest {

    // subset solution to work with
    private SubsetSolution sol;

    // number of IDs
    private final int NUM_IDS = 100;
    
    // random generator
    private final Random RG = new Random();
    
    /**
     * Print message when starting tests.
     */
    @BeforeClass
    public static void setUpClass() {
        System.out.println("# Testing SwapMove ...");
    }

    /**
     * Print message when tests are complete.
     */
    @AfterClass
    public static void tearDownClass() {
        System.out.println("# Done testing SwapMove!");
    }
    
    /**
     * Create subset solution to work with in each test method.
     */
    @Before
    public void setUp(){
        // initialize set of all IDs = {0 ... NUM_IDS-1}
        Set<Integer> IDs = new HashSet<>();
        for(int i=0; i < NUM_IDS; i++){
            IDs.add(i);
        }
        // initialize subset solution with these IDs, none selected
        sol = new SubsetSolution(IDs);
    }

    /**
     * Test of apply method, of class SwapMove.
     */
    @Test
    public void testApply() {
        
        System.out.println(" - test apply");
        
        Move<SubsetSolution> move;
        boolean thrown;
        
        // try to create swap move with added ID = deleted ID
        thrown = false;
        try {
            move = new SwapMove(3, 3);
        } catch (IllegalArgumentException ex) {
            thrown = true;
        }
        assertTrue(thrown);
        // try to apply move with non existing IDs
        move = new SwapMove(NUM_IDS+12, NUM_IDS+123);
        thrown = false;
        try {
            move.apply(sol);
        } catch (SolutionModificationException ex) {
            thrown = true;
        }
        assertTrue(thrown);
        // try to apply swap with existing IDs,
        // but deleted ID is currently not selected
        move = new SwapMove(0, 1);
        thrown = false;
        try {
            move.apply(sol);
        } catch (SolutionModificationException ex) {
            thrown = true;
        }
        assertTrue(thrown);
        
        // select 10 IDs
        Set<Integer> toSelect = SetUtilities.getRandomSubset(sol.getUnselectedIDs(), 10, RG);
        sol.selectAll(toSelect);
        
        // try to swap IDs where added ID is already selected
        Iterator<Integer> it = toSelect.iterator();
        move = new SwapMove(it.next(), it.next());
        thrown = false;
        try {
            move.apply(sol);
        } catch (SolutionModificationException ex) {
            thrown = true;
        }
        assertTrue(thrown);
        
        // apply some valid swap moves
        for(int k=0; k<100; k++){
            int add = SetUtilities.getRandomElement(sol.getUnselectedIDs(), RG);
            int delete = SetUtilities.getRandomElement(sol.getSelectedIDs(), RG);
            move = new SwapMove(add, delete);
            Set<Integer> selected = new HashSet<>(sol.getSelectedIDs());
            Set<Integer> unselected = new HashSet<>(sol.getUnselectedIDs());
            // apply move
            move.apply(sol);
            // verify
            assertTrue(sol.getSelectedIDs().contains(add));
            assertFalse(sol.getSelectedIDs().contains(delete));
            assertTrue(sol.getUnselectedIDs().contains(delete));
            assertFalse(sol.getUnselectedIDs().contains(add));
            selected.add(add);
            selected.remove(delete);
            unselected.add(delete);
            unselected.remove(add);
            assertEquals(selected, sol.getSelectedIDs());
            assertEquals(unselected, sol.getUnselectedIDs());
            // undo move
            move.undo(sol);
        }
        
    }

    /**
     * Test of undo method, of class SwapMove.
     */
    @Test
    public void testUndo() {
        
        System.out.println(" - test undo");
        
        // randomly select 50% of the IDs
        sol.selectAll(SetUtilities.getRandomSubset(sol.getUnselectedIDs(), (int) (0.5*sol.getNumUnselectedIDs()), RG));
        
        // randomly apply moves and undo them
        Move<SubsetSolution> move;
        SubsetSolution copy;
        for(int i=0; i<100; i++){
            // random move
            move = new SwapMove(SetUtilities.getRandomElement(sol.getUnselectedIDs(), RG), SetUtilities.getRandomElement(sol.getSelectedIDs(), RG));
            // copy current solution
            copy = new SubsetSolution(sol.getAllIDs());
            copy.selectAll(sol.getSelectedIDs());
            // apply move to original solution
            move.apply(sol);
            // undo move
            move.undo(sol);
            // check if again equal to copy made before application
            assertEquals(copy, sol);
        }
        
    }

    /**
     * Test of getAddedIDs method, of class SwapMove.
     */
    @Test
    public void testGetAddedIDs() {
        
        System.out.println(" - test getAddedIDs");
        
        // create random move
        Integer add = RG.nextInt();
        SubsetMove move = new SwapMove(add, 0);
        
        // verify
        assertEquals(add, move.getAddedIDs().iterator().next());
        assertEquals(1, move.getAddedIDs().size());
        
    }
    
    /**
     * Test of getAddedID method, of class SwapMove.
     */
    @Test
    public void testGetAddedID() {
        
        System.out.println(" - test getAddedID");
        
        // create random move
        int add = RG.nextInt();
        SwapMove move = new SwapMove(add, 0);
        
        // verify
        assertEquals(add, move.getAddedID());
        
    }

    /**
     * Test of getDeletedIDs method, of class SwapMove.
     */
    @Test
    public void testGetDeletedIDs() {
        
        System.out.println(" - test getDeletedIDs");
        
        // create random move
        Integer delete = RG.nextInt();
        SubsetMove move = new SwapMove(0, delete);
        
        // verify
        assertEquals(delete, move.getDeletedIDs().iterator().next());
        assertEquals(1, move.getDeletedIDs().size());
    }
    
    /**
     * Test of getDeletedID method, of class SwapMove.
     */
    @Test
    public void testGetDeletedID() {
        
        System.out.println(" - test getDeletedID");
        
        // create random move
        int delete = RG.nextInt();
        SwapMove move = new SwapMove(0, delete);
        
        // verify
        assertEquals(delete, move.getDeletedID());

    }

    /**
     * Test of getNumAdded method, of class SwapMove.
     */
    @Test
    public void testGetNumAdded() {
        
        System.out.println(" - test getNumAdded");
        
        // create arbitrary move
        SubsetMove move = new SwapMove(1, 2);
        
        // verify
        assertEquals(1, move.getNumAdded());
        
    }

    /**
     * Test of getNumDeleted method, of class SwapMove.
     */
    @Test
    public void testGetNumDeleted() {
        
        System.out.println(" - test getNumDeleted");
        
        // create arbitrary move
        SubsetMove move = new SwapMove(1, 2);
        
        // verify
        assertEquals(1, move.getNumDeleted());
        
    }
    
    /**
     * Test of equals and hashCode methods, of class SwapMove.
     */
    @Test
    public void testEqualsAndHashCode() {
        
        System.out.println(" - test equals and hashCode");
        
        // create swap moves
        SubsetMove move1 = new SwapMove(1,2);   // equal
        SubsetMove move2 = new SwapMove(1,2);   // equal
        SubsetMove move3 = new SwapMove(3,4);   // different
        SubsetMove move4 = new SwapMove(1,4);   // different
        SubsetMove move5 = new SwapMove(3,2);   // different
        
        // verify
        assertEquals(move1, move2);
        assertEquals(move1.hashCode(), move2.hashCode());
        assertFalse(move1 == move2);
        
        assertNotEquals(move1, move3);
        assertNotEquals(move1, move4);
        assertNotEquals(move1, move5);
        assertNotEquals(move2, move3);
        assertNotEquals(move2, move4);
        assertNotEquals(move2, move5);
        
    }

}