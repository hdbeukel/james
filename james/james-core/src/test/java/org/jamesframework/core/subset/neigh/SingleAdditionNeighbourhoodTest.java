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
import org.jamesframework.core.subset.neigh.moves.AdditionMove;
import java.util.HashSet;
import java.util.List;
import java.util.Random;
import java.util.Set;
import org.jamesframework.core.subset.SubsetSolution;
import org.jamesframework.core.search.neigh.Move;
import org.jamesframework.core.util.SetUtilities;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import static org.junit.Assert.*;
import org.junit.Before;

/**
 * Test single addition neighbourhood.
 * 
 * @author <a href="mailto:herman.debeukelaer@ugent.be">Herman De Beukelaer</a>
 */
public class SingleAdditionNeighbourhoodTest {
    
    // subset solution to work with
    private SubsetSolution sol;

    // number of IDs
    private final int NUM_IDS = 100;
    
    // addition neighbourhoods to work with
    private SingleAdditionNeighbourhood neighUnlimited = new SingleAdditionNeighbourhood();
    private SingleAdditionNeighbourhood neighLimited = new SingleAdditionNeighbourhood(10);
    
    // random generator
    private final Random RG = new Random();
    
    /**
     * Print message when starting tests.
     */
    @BeforeClass
    public static void setUpClass() {
        System.out.println("# Testing SingleAdditionNeighbourhood ...");
    }

    /**
     * Print message when tests are complete.
     */
    @AfterClass
    public static void tearDownClass() {
        System.out.println("# Done testing SingleAdditionNeighbourhood!");
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
     * Test of constructor, of class SingleAdditionNeighbourhood.
     */
    @Test
    public void testConstructor() {
                
        System.out.println(" - test constructor");
        
        boolean thrown;
        
        // try to create single addition neighbourhood with invalid max subset sizes
        thrown = false;
        try {
            new SingleAdditionNeighbourhood(-1);
        } catch(IllegalArgumentException ex) {
            thrown = true;
        }
        assertTrue(thrown);
        thrown = false;
        try {
            new SingleAdditionNeighbourhood(10);
        } catch(IllegalArgumentException ex) {
            thrown = true;
        }
        assertFalse(thrown);
        
    }

    /**
     * Test of getRandomMove method, of class SingleAdditionNeighbourhood.
     */
    @Test
    public void testGetRandomMove() {
        
        System.out.println(" - test getRandomMove");
        
        // 1) generate move for empty solution
        
        // limited size
        assertNotNull(neighLimited.getRandomMove(sol));
        assertTrue(neighLimited.getRandomMove(sol) instanceof AdditionMove);
        // unlimited size
        assertNotNull(neighUnlimited.getRandomMove(sol));
        assertTrue(neighUnlimited.getRandomMove(sol) instanceof AdditionMove);

        // 2) generate move for full selection
        sol.selectAll();
        
        // limited size
        assertNull(neighLimited.getRandomMove(sol));
        // unlimited size
        assertNull(neighUnlimited.getRandomMove(sol));
        
        // 3a) generate moves for solution with 10 selected IDs
        sol.deselectAll();
        sol.selectAll(SetUtilities.getRandomSubset(sol.getUnselectedIDs(), 10, RG));
        
        // limited size
        assertNull(neighLimited.getRandomMove(sol));
        // unlimited size
        assertNotNull(neighUnlimited.getRandomMove(sol));
        assertTrue(neighUnlimited.getRandomMove(sol) instanceof AdditionMove);
        
        // 3b) generate moves for solution with 5 selected IDs
        sol.deselectAll();
        sol.selectAll(SetUtilities.getRandomSubset(sol.getUnselectedIDs(), 5, RG));
        
        // limited size
        assertNotNull(neighLimited.getRandomMove(sol));
        assertTrue(neighLimited.getRandomMove(sol) instanceof AdditionMove);
        // unlimited size
        assertNotNull(neighUnlimited.getRandomMove(sol));
        assertTrue(neighUnlimited.getRandomMove(sol) instanceof AdditionMove);
        
        // generate, apply, undo and reapply 10 moves generated by the neighbourhood with size limit
        sol.deselectAll();
        Move<SubsetSolution> move;
        SubsetSolution copy;
        for(int i=0; i<10; i++){
            // generate move
            move = neighLimited.getRandomMove(sol);
            assertNotNull(move);
            // verify added ID of move
            assertTrue(sol.getUnselectedIDs().containsAll(((SubsetMove)move).getAddedIDs()));
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
            // verify that solution stays within size limit
            assertTrue(sol.getNumSelectedIDs() <= neighLimited.getMaxSubsetSize());
        }
        
        // check: no more moves can be applied
        assertNull(neighLimited.getRandomMove(sol));
        assertTrue(neighLimited.getAllMoves(sol).isEmpty());
        
    }

    /**
     * Test of getAllMoves method, of class SingleAdditionNeighbourhood.
     */
    @Test
    public void testGetAllMoves() {
        
        System.out.println(" - test getAllMoves");
        
        List<? extends Move<SubsetSolution>> moves;
                
        // 1) generate moves for empty solution
        
        // limited size
        moves = neighLimited.getAllMoves(sol);
        assertEquals(NUM_IDS, moves.size());
        for(Move<SubsetSolution> move : moves){
            assertTrue(move instanceof AdditionMove);
        }
        // unlimited size
        moves = neighUnlimited.getAllMoves(sol);
        assertEquals(NUM_IDS, moves.size());
        for(Move<SubsetSolution> move : moves){
            assertTrue(move instanceof AdditionMove);
        }

        // 2) generate moves for full selection
        sol.selectAll();
        
        // limited size
        moves = neighLimited.getAllMoves(sol);
        assertTrue(moves.isEmpty());
        // unlimited size
        moves = neighUnlimited.getAllMoves(sol);
        assertTrue(moves.isEmpty());
        
        // 3a) generate moves for solution with 10 selected IDs
        sol.deselectAll();
        sol.selectAll(SetUtilities.getRandomSubset(sol.getUnselectedIDs(), 10, RG));
        
        // limited size
        moves = neighLimited.getAllMoves(sol);
        assertTrue(moves.isEmpty());
        // unlimited size
        moves = neighUnlimited.getAllMoves(sol);
        assertEquals(NUM_IDS-10, moves.size());
        for(Move<SubsetSolution> move : moves){
            assertTrue(move instanceof AdditionMove);
        }
        
        // 3b) generate moves for solution with 5 selected IDs
        sol.deselectAll();
        sol.selectAll(SetUtilities.getRandomSubset(sol.getUnselectedIDs(), 5, RG));
        
        // limited size
        moves = neighLimited.getAllMoves(sol);
        assertEquals(NUM_IDS-5, moves.size());
        for(Move<SubsetSolution> move : moves){
            assertTrue(move instanceof AdditionMove);
        }
        // unlimited size
        moves = neighUnlimited.getAllMoves(sol);
        assertEquals(NUM_IDS-5, moves.size());
        for(Move<SubsetSolution> move : moves){
            assertTrue(move instanceof AdditionMove);
        }
        
    }

    /**
     * Test of getMaxSubsetSize method, of class SingleAdditionNeighbourhood.
     */
    @Test
    public void testGetMaxSubsetSize() {
        
        System.out.println(" - test getMaxSubsetSize");
        
        assertEquals(10, neighLimited.getMaxSubsetSize());
        assertEquals(Integer.MAX_VALUE, neighUnlimited.getMaxSubsetSize());
        
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
        neighUnlimited = new SingleAdditionNeighbourhood(NUM_IDS, fixedIDs);
        
        // randomly select 50% of IDs
        sol.selectAll(SetUtilities.getRandomSubset(sol.getAllIDs(), (int) (0.5*NUM_IDS), RG));
        
        // generate random moves and check that fixed IDs are never added
        for(int i=0; i<100; i++){
            SubsetMove move = (SubsetMove) neighUnlimited.getRandomMove(sol);
            if(move != null){
                // verify
                fixedIDs.forEach(ID -> assertFalse(move.getAddedIDs().contains(ID)));
            }
        }
        
        // generate all moves and verify that no fixed IDs are added
        neighUnlimited.getAllMoves(sol).stream()
                                       .map(m -> (SubsetMove) m)
                                       .forEach(m -> {
                                            fixedIDs.forEach(ID -> assertFalse(m.getAddedIDs().contains(ID)));
                                       });
        
        // now fix ALL IDs
        neighUnlimited = new SingleAdditionNeighbourhood(NUM_IDS, sol.getAllIDs());
        // check that no move can be generated
        assertNull(neighUnlimited.getRandomMove(sol));
        assertTrue(neighUnlimited.getAllMoves(sol).isEmpty());
        
    }

}