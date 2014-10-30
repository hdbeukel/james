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

package org.jamesframework.core.subset.neigh;

import org.jamesframework.core.subset.neigh.moves.SubsetMove;
import java.util.HashSet;
import java.util.List;
import java.util.Random;
import java.util.Set;
import org.jamesframework.core.subset.SubsetSolution;
import org.jamesframework.core.search.neigh.Move;
import org.jamesframework.core.subset.neigh.moves.DeletionMove;
import org.jamesframework.core.util.SetUtilities;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import static org.junit.Assert.*;
import org.junit.Before;

/**
 * Test single deletion neighbourhood.
 * 
 * @author <a href="mailto:herman.debeukelaer@ugent.be">Herman De Beukelaer</a>
 */
public class SingleDeletionNeighbourhoodTest {
    
    // subset solution to work with
    private SubsetSolution sol;

    // number of IDs
    private final int NUM_IDS = 100;
    
    // deletion neighbourhoods to work with
    private SingleDeletionNeighbourhood neighUnlimited = new SingleDeletionNeighbourhood();
    private SingleDeletionNeighbourhood neighLimited = new SingleDeletionNeighbourhood(10);
    
    // random generator
    private final Random RG = new Random();
    
    /**
     * Print message when starting tests.
     */
    @BeforeClass
    public static void setUpClass() {
        System.out.println("# Testing SingleDeletionNeighbourhood ...");
    }

    /**
     * Print message when tests are complete.
     */
    @AfterClass
    public static void tearDownClass() {
        System.out.println("# Done testing SingleDeletionNeighbourhood!");
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
     * Test of constructor, of class SingleDeletionNeighbourhood.
     */
    @Test
    public void testConstructor() {
                
        System.out.println(" - test constructor");
        
        boolean thrown;
        
        // try to create single deletion neighbourhood with invalid min subset sizes
        thrown = false;
        try {
            new SingleDeletionNeighbourhood(-1);
        } catch(IllegalArgumentException ex) {
            thrown = true;
        }
        assertTrue(thrown);
        thrown = false;
        try {
            new SingleDeletionNeighbourhood(10);
        } catch(IllegalArgumentException ex) {
            thrown = true;
        }
        assertFalse(thrown);
        
    }

    /**
     * Test of getRandomMove method, of class SingleDeletionNeighbourhood.
     */
    @Test
    public void testGetRandomMove() {
        
        System.out.println(" - test getRandomMove");
        
        // 1) generate move for empty solution
        
        // limited size
        assertNull(neighLimited.getRandomMove(sol));
        // unlimited size
        assertNull(neighUnlimited.getRandomMove(sol));

        // 2) generate move for full selection
        sol.selectAll();
        
        // limited size
        assertNotNull(neighLimited.getRandomMove(sol));
        assertTrue(neighLimited.getRandomMove(sol) instanceof DeletionMove);
        // unlimited size
        assertNotNull(neighUnlimited.getRandomMove(sol));
        assertTrue(neighUnlimited.getRandomMove(sol) instanceof DeletionMove);
        
        // 3a) generate moves for solution with 10 selected IDs
        sol.deselectAll();
        sol.selectAll(SetUtilities.getRandomSubset(sol.getUnselectedIDs(), 10, RG));
        
        // limited size
        assertNull(neighLimited.getRandomMove(sol));
        // unlimited size
        assertNotNull(neighUnlimited.getRandomMove(sol));
        assertTrue(neighUnlimited.getRandomMove(sol) instanceof DeletionMove);
        
        // 3b) generate moves for solution with 15 selected IDs
        sol.deselectAll();
        sol.selectAll(SetUtilities.getRandomSubset(sol.getUnselectedIDs(), 15, RG));
        
        // limited size
        assertNotNull(neighLimited.getRandomMove(sol));
        assertTrue(neighLimited.getRandomMove(sol) instanceof DeletionMove);
        // unlimited size
        assertNotNull(neighUnlimited.getRandomMove(sol));
        assertTrue(neighUnlimited.getRandomMove(sol) instanceof DeletionMove);
        
        // generate, apply, undo and reapply 10 moves generated by the neighbourhood with size limit
        Move<SubsetSolution> move;
        SubsetSolution copy;
        for(int i=0; i<10; i++){
            // generate move
            move = neighLimited.getRandomMove(sol);
            if(move != null){
                // verify deleted ID of move
                assertTrue(sol.getSelectedIDs().containsAll(((SubsetMove)move).getDeletedIDs()));
                // copy solution
                copy = new SubsetSolution(sol.getAllIDs());
                copy.selectAll(sol.getSelectedIDs());
                // apply move
                move.apply(sol);
                // undo move
                move.undo(sol);
                // verify
                assertEquals(copy, sol);
                // reapply
                move.apply(sol);
            }
            // verify that solution stays within size limit
            assertTrue(sol.getNumSelectedIDs() >= neighLimited.getMinSubsetSize());
        }
        
        // check: no more moves can be applied
        assertNull(neighLimited.getRandomMove(sol));
        assertTrue(neighLimited.getAllMoves(sol).isEmpty());
        
    }

    /**
     * Test of getAllMoves method, of class SingleDeletionNeighbourhood.
     */
    @Test
    public void testGetAllMoves() {
        
        System.out.println(" - test getAllMoves");
        
        List<? extends Move<SubsetSolution>> moves;
                
        // 1) generate moves for empty solution
        
        // limited size
        moves = neighLimited.getAllMoves(sol);
        assertTrue(moves.isEmpty());
        // unlimited size
        moves = neighUnlimited.getAllMoves(sol);
        assertTrue(moves.isEmpty());

        // 2) generate moves for full selection
        sol.selectAll();
        
        // limited size
        moves = neighLimited.getAllMoves(sol);
        assertEquals(NUM_IDS, moves.size());
        for(Move<SubsetSolution> move : moves){
            assertTrue(move instanceof DeletionMove);
        }
        // unlimited size
        moves = neighUnlimited.getAllMoves(sol);
        assertEquals(NUM_IDS, moves.size());
        for(Move<SubsetSolution> move : moves){
            assertTrue(move instanceof DeletionMove);
        }
        
        // 3a) generate moves for solution with 10 selected IDs
        sol.deselectAll();
        sol.selectAll(SetUtilities.getRandomSubset(sol.getUnselectedIDs(), 10, RG));
        
        // limited size
        moves = neighLimited.getAllMoves(sol);
        assertTrue(moves.isEmpty());
        // unlimited size
        moves = neighUnlimited.getAllMoves(sol);
        assertEquals(10, moves.size());
        for(Move<SubsetSolution> move : moves){
            assertTrue(move instanceof DeletionMove);
        }
        
        // 3b) generate moves for solution with 15 selected IDs
        sol.deselectAll();
        sol.selectAll(SetUtilities.getRandomSubset(sol.getUnselectedIDs(), 15, RG));
        
        // limited size
        moves = neighLimited.getAllMoves(sol);
        assertEquals(15, moves.size());
        for(Move<SubsetSolution> move : moves){
            assertTrue(move instanceof DeletionMove);
        }
        // unlimited size
        moves = neighUnlimited.getAllMoves(sol);
        assertEquals(15, moves.size());
        for(Move<SubsetSolution> move : moves){
            assertTrue(move instanceof DeletionMove);
        }
        
    }

    /**
     * Test of getMinSubsetSize method, of class SingleDeletionNeighbourhood.
     */
    @Test
    public void testGetMinSubsetSize() {
        
        System.out.println(" - test getMinSubsetSize");
        
        assertEquals(10, neighLimited.getMinSubsetSize());
        assertEquals(0, neighUnlimited.getMinSubsetSize());
        
    }
    
    /**
     * Test with fixed IDs.
     */
    @Test
    public void testWithFixedIDs() {
        
        System.out.println(" - test with fixed IDs");
        
        // randomly fix 50% of all IDs
        Set<Integer> fixedIDs = SetUtilities.getRandomSubset(sol.getAllIDs(), (int) (0.5*NUM_IDS), RG);
        // create new neighbourhood with fixed IDs
        neighUnlimited = new SingleDeletionNeighbourhood(0, fixedIDs);
        
        // randomly select 50% of IDs
        sol.selectAll(SetUtilities.getRandomSubset(sol.getAllIDs(), (int) (0.5*NUM_IDS), RG));
        
        // generate random moves and check that fixed IDs are never deleted
        for(int i=0; i<100; i++){
            SubsetMove move = (SubsetMove) neighUnlimited.getRandomMove(sol);
            if(move != null){
                // verify
                fixedIDs.forEach(ID -> assertFalse(move.getDeletedIDs().contains(ID)));
            }
        }
        
        // generate all moves and verify that no fixed IDs are deleted
        neighUnlimited.getAllMoves(sol).stream()
                                       .map(m -> (SubsetMove) m)
                                       .forEach(m -> {
                                            fixedIDs.forEach(ID -> assertFalse(m.getDeletedIDs().contains(ID)));
                                       });
        
        // now fix ALL IDs
        neighUnlimited = new SingleDeletionNeighbourhood(0, sol.getAllIDs());
        // check that no move can be generated
        assertNull(neighUnlimited.getRandomMove(sol));
        assertTrue(neighUnlimited.getAllMoves(sol).isEmpty());
        // ... repeat if solution actually already contains some IDs
        sol.selectAll(SetUtilities.getRandomSubset(sol.getAllIDs(), (int) (0.5*NUM_IDS), RG));
        assertNull(neighUnlimited.getRandomMove(sol));
        assertTrue(neighUnlimited.getAllMoves(sol).isEmpty());
    }

}