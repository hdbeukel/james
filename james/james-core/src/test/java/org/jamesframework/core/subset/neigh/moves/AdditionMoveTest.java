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

package org.jamesframework.core.subset.neigh.moves;

import org.jamesframework.core.subset.neigh.moves.SubsetMove;
import org.jamesframework.core.subset.neigh.moves.AdditionMove;
import java.util.HashSet;
import java.util.Random;
import java.util.Set;
import org.jamesframework.core.exceptions.SolutionModificationException;
import org.jamesframework.core.subset.SubsetSolution;
import org.jamesframework.core.search.neigh.Move;
import org.jamesframework.core.util.SetUtilities;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import static org.junit.Assert.*;
import org.junit.Before;

/**
 * Test AdditionMove (subset move).
 * 
 * @author <a href="mailto:herman.debeukelaer@ugent.be">Herman De Beukelaer</a>
 */
public class AdditionMoveTest {

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
        System.out.println("# Testing AdditionMove ...");
    }

    /**
     * Print message when tests are complete.
     */
    @AfterClass
    public static void tearDownClass() {
        System.out.println("# Done testing AdditionMove!");
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
     * Test of getAddedIDs method, of class AdditionMove.
     */
    @Test
    public void testGetAddedIDs() {
        
        System.out.println(" - test getAddedIDs");
        
        // create random move
        Integer add = RG.nextInt();
        SubsetMove move = new AdditionMove(add);
        
        // verify
        assertEquals(add, move.getAddedIDs().iterator().next());
        assertEquals(1, move.getAddedIDs().size());
        
    }

    /**
     * Test of getAddedID method, of class AdditionMove.
     */
    @Test
    public void testGetAddedID() {
       
        System.out.println(" - test getAddedID");
        
        // create random move
        int add = RG.nextInt();
        AdditionMove move = new AdditionMove(add);
        
        // verify
        assertEquals(add, move.getAddedID());
        
    }

    /**
     * Test of getDeletedIDs method, of class AdditionMove.
     */
    @Test
    public void testGetDeletedIDs() {
        
        System.out.println(" - test getDeletedIDs");
        
        // create arbitrary move
        SubsetMove move = new AdditionMove(0);
        
        // verify
        assertTrue(move.getDeletedIDs().isEmpty());
        
    }

    /**
     * Test of getNumAdded method, of class AdditionMove.
     */
    @Test
    public void testGetNumAdded() {
        
        System.out.println(" - test getNumAdded");
        
        // create arbitary addition move
        SubsetMove move = new AdditionMove(0);
        
        // verify
        assertEquals(1, move.getNumAdded());
        
    }

    /**
     * Test of getNumDeleted method, of class AdditionMove.
     */
    @Test
    public void testGetNumDeleted() {
        
        System.out.println(" - test getNumDeleted");
        
        // create arbitary addition move
        SubsetMove move = new AdditionMove(0);
        
        // verify
        assertEquals(0, move.getNumDeleted());
        
    }

    /**
     * Test of apply method, of class AdditionMove.
     */
    @Test
    public void testApply() {
        
        System.out.println(" - test apply");
        
        Move<SubsetSolution> move;
        boolean thrown;
        
        // try to add non existing ID
        move = new AdditionMove(NUM_IDS+123);
        thrown = false;
        try {
            move.apply(sol);
        } catch (SolutionModificationException ex){
            thrown = true;
        }
        assertTrue(thrown);
        
        // try to add already selected ID
        int sel = SetUtilities.getRandomElement(sol.getUnselectedIDs(), RG);
        sol.select(sel);
        move = new AdditionMove(sel);
        thrown = false;
        try {
            move.apply(sol);
        } catch (SolutionModificationException ex){
            thrown = true;
        }
        assertTrue(thrown);
        
        // apply chain of 10 addition moves
        sol.deselectAll();
        Set<Integer> selected = new HashSet<>();
        for(int m=0; m<10; m++){
            sel = SetUtilities.getRandomElement(sol.getUnselectedIDs(), RG);
            move = new AdditionMove(sel);
            move.apply(sol);
            selected.add(sel);
        }
        // verify
        assertEquals(selected, sol.getSelectedIDs());
        
    }

    /**
     * Test of undo method, of class AdditionMove.
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
            move = new AdditionMove(SetUtilities.getRandomElement(sol.getUnselectedIDs(), RG));
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
     * Test of equals and hashCode methods, of class AdditionMove.
     */
    @Test
    public void testEqualsAndHashCode() {
        
        System.out.println(" - test equals and hashCode");
        
        // create addition moves
        SubsetMove move1 = new AdditionMove(123);   // equal
        SubsetMove move2 = new AdditionMove(123);   // equal
        SubsetMove move3 = new AdditionMove(456);   // different
        
        // verify
        assertEquals(move1, move2);
        assertEquals(move1.hashCode(), move2.hashCode());
        assertFalse(move1 == move2);
        
        assertNotEquals(move2, move3);
        assertNotEquals(move1, move3);
        
    }

}