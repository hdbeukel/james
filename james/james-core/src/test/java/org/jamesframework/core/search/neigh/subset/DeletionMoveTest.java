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
 * Test DeletionMove (subset move).
 * 
 * @author <a href="mailto:herman.debeukelaer@ugent.be">Herman De Beukelaer</a>
 */
public class DeletionMoveTest {

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
        System.out.println("# Testing DeletionMove ...");
    }

    /**
     * Print message when tests are complete.
     */
    @AfterClass
    public static void tearDownClass() {
        System.out.println("# Done testing DeletionMove!");
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
     * Test of getAddedIDs method, of class DeletionMove.
     */
    @Test
    public void testGetAddedIDs() {
        
        System.out.println(" - test getAddedIDs");
        
        // create arbitrary deletion move
        SubsetMove move = new DeletionMove(0);
        
        // verify
        assertTrue(move.getAddedIDs().isEmpty());
        
    }

    /**
     * Test of getDeletedIDs method, of class DeletionMove.
     */
    @Test
    public void testGetDeletedIDs() {
        
        System.out.println(" - test getDeletedIDs");
        
        // create random deletion move
        Integer del = RG.nextInt();
        SubsetMove move = new DeletionMove(del);
        
        // verify
        assertEquals(del, move.getDeletedIDs().iterator().next());
        assertEquals(1, move.getDeletedIDs().size());
        
    }
    
    /**
     * Test of getDeletedID method, of class DeletionMove.
     */
    @Test
    public void testGetDeletedID() {
        
        System.out.println(" - test getDeletedID");
        
        // create random deletion move
        int del = RG.nextInt();
        DeletionMove move = new DeletionMove(del);
        
        // verify
        assertEquals(del, move.getDeletedID());
        
    }

    /**
     * Test of getNumAdded method, of class DeletionMove.
     */
    @Test
    public void testGetNumAdded() {
        
        System.out.println(" - test getNumAdded");
        
        // create arbitary move
        SubsetMove move = new DeletionMove(0);
        
        // verify
        assertEquals(0, move.getNumAdded());
        
    }

    /**
     * Test of getNumDeleted method, of class DeletionMove.
     */
    @Test
    public void testGetNumDeleted() {
        
        System.out.println(" - test getNumDeleted");
        
        // create arbitrary move
        SubsetMove move = new DeletionMove(0);
        
        // verify
        assertEquals(1, move.getNumDeleted());
        
    }

    /**
     * Test of apply method, of class DeletionMove.
     */
    @Test
    public void testApply() {
        
        System.out.println(" - test apply");
        
        Move<SubsetSolution> move;
        boolean thrown;
        
        // try to delete non existing ID
        move = new DeletionMove(NUM_IDS+123);
        thrown = false;
        try {
            move.apply(sol);
        } catch (SolutionModificationException ex){
            thrown = true;
        }
        assertTrue(thrown);
        
        // try to delete existing but unselected ID
        move = new DeletionMove(SetUtilities.getRandomElement(sol.getUnselectedIDs(), RG));
        thrown = false;
        try {
            move.apply(sol);
        } catch (SolutionModificationException ex){
            thrown = true;
        }
        assertTrue(thrown);
        
        // randomly select 50% of IDs
        sol.selectAll(SetUtilities.getRandomSubset(sol.getUnselectedIDs(), (int) (0.5*sol.getNumUnselectedIDs()), RG));
        
        // apply random deletion move
        int del = SetUtilities.getRandomElement(sol.getSelectedIDs(), RG);
        Set<Integer> selectedCopy = new HashSet<>(sol.getSelectedIDs());
        move = new DeletionMove(del);
        move.apply(sol);
        selectedCopy.remove(del);
        // verify
        assertEquals(selectedCopy, sol.getSelectedIDs());
        
        // apply chain deletion moves until selection is empty again
        while(sol.getNumSelectedIDs() > 0){
            move = new DeletionMove(SetUtilities.getRandomElement(sol.getSelectedIDs(), RG));
            move.apply(sol);
        }
        // verify
        assertTrue(sol.getSelectedIDs().isEmpty());
        
    }

    /**
     * Test of undo method, of class DeletionMove.
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
            move = new DeletionMove(SetUtilities.getRandomElement(sol.getSelectedIDs(), RG));
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
     * Test of equals and hashCode methods, of class DeletionMove.
     */
    @Test
    public void testEqualsAndHashCode() {
        
        System.out.println(" - test equals and hashCode");
        
        // create deletion moves
        SubsetMove move1 = new DeletionMove(123);   // equal
        SubsetMove move2 = new DeletionMove(123);   // equal
        SubsetMove move3 = new DeletionMove(456);   // different
        
        // verify
        assertEquals(move1, move2);
        assertEquals(move1.hashCode(), move2.hashCode());
        assertFalse(move1 == move2);
        
        assertNotEquals(move2, move3);
        assertNotEquals(move1, move3);
        
    }

}