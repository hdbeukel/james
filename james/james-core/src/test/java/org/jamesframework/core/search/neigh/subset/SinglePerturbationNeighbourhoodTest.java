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

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Random;
import java.util.Set;
import org.jamesframework.core.problems.solutions.SubsetSolution;
import org.jamesframework.core.search.neigh.Move;
import org.jamesframework.core.util.SetUtilities;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import static org.junit.Assert.*;
import org.junit.Before;

/**
 * Test single perturbation neighbourhood.
 * 
 * @author <a href="mailto:herman.debeukelaer@ugent.be">Herman De Beukelaer</a>
 */
public class SinglePerturbationNeighbourhoodTest {
    
    // subset solution to work with
    private SubsetSolution sol;

    // number of IDs
    private final int NUM_IDS = 100;
    
    // perturbation neighbourhoods to work with
    private SinglePerturbationNeighbourhood neighVarSize = new SinglePerturbationNeighbourhood(10, 20);
    private SinglePerturbationNeighbourhood neighFixedSize = new SinglePerturbationNeighbourhood(10, 10);
    private SinglePerturbationNeighbourhood neighUnboundedSize = new SinglePerturbationNeighbourhood(0, NUM_IDS);
    
    // random generator
    private final Random RG = new Random();
    
    /**
     * Print message when starting tests.
     */
    @BeforeClass
    public static void setUpClass() {
        System.out.println("# Testing SinglePerturbationNeighbourhood ...");
    }

    /**
     * Print message when tests are complete.
     */
    @AfterClass
    public static void tearDownClass() {
        System.out.println("# Done testing SinglePerturbationNeighbourhood!");
    }
    
    /**
     * Create subset solution and single swap neighbourhood to work with in each test method.
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
     * Test of constructor, of class SinglePerturbationNeighbourhood.
     */
    @Test
    public void testConstructor() {
        
        System.out.println(" - test constructor");
        
        boolean thrown;
        
        // try to create single perturbation neighbourhood with invalid min/max subset sizes
        thrown = false;
        try {
            new SinglePerturbationNeighbourhood(-1, 10);
        } catch(IllegalArgumentException ex) {
            thrown = true;
        }
        assertTrue(thrown);
        thrown = false;
        try {
            new SinglePerturbationNeighbourhood(0, -1);
        } catch(IllegalArgumentException ex) {
            thrown = true;
        }
        assertTrue(thrown);
        thrown = false;
        try {
            new SinglePerturbationNeighbourhood(11, 10);
        } catch(IllegalArgumentException ex) {
            thrown = true;
        }
        assertTrue(thrown);
        
    }

    /**
     * Test of getRandomMove method, of class SinglePerturbationNeighbourhood.
     */
    @Test
    public void testGetRandomMove() {
        
        System.out.println(" - test getRandomMove");
        
        // 1) generate move for empty solution
        
        // var size
        assertNull(neighVarSize.getRandomMove(sol));
        // fixed size
        assertNull(neighFixedSize.getRandomMove(sol));
        // unbounded size
        assertTrue(neighUnboundedSize.getRandomMove(sol) instanceof AdditionMove);

        // 2) generate move for full selection
        sol.selectAll();
        
        // var size
        assertNull(neighVarSize.getRandomMove(sol));
        // fixed size
        assertNull(neighFixedSize.getRandomMove(sol));
        // unbounded size
        assertTrue(neighUnboundedSize.getRandomMove(sol) instanceof DeletionMove);
        
        // 3) generate moves for solution with 10 selected IDs
        sol.deselectAll();
        sol.selectAll(SetUtilities.getRandomSubset(sol.getUnselectedIDs(), 10, RG));
        
        // var size
        assertFalse(neighVarSize.getRandomMove(sol) instanceof DeletionMove);
        // fixed size
        assertTrue(neighFixedSize.getRandomMove(sol) instanceof SwapMove);

        // generate, apply, undo and reapply 100 moves for each neighbourhood, in this order:
        //  - fixed size
        //  - var size
        //  - unbounded size
        List<SinglePerturbationNeighbourhood> neighs = new ArrayList<>();
        neighs.add(neighFixedSize);
        neighs.add(neighVarSize);
        neighs.add(neighUnboundedSize);
        Move<SubsetSolution> move;
        SubsetSolution copy;
        for(SinglePerturbationNeighbourhood neigh : neighs){
            for(int i=0; i<100; i++){
                // generate move
                move = neigh.getRandomMove(sol);
                // verify added/deleted ID of move
                assertTrue(sol.getSelectedIDs().containsAll(((SubsetMove)move).getDeletedIDs()));
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
                // verify that solution stays within size bounds
                assertTrue(sol.getNumSelectedIDs() >= neigh.getMinSubsetSize());
                assertTrue(sol.getNumSelectedIDs() <= neigh.getMaxSubsetSize());
            }
        }
        
    }

    /**
     * Test of getAllMoves method, of class SinglePerturbationNeighbourhood.
     */
    @Test
    public void testGetAllMoves() {
        
        System.out.println(" - test getAllMoves");
        
        Set<Move<SubsetSolution>> moves;
                
        // 1) generate moves for empty solution
        
        // var size
        assertTrue(neighVarSize.getAllMoves(sol).isEmpty());
        // fixed size
        assertTrue(neighFixedSize.getAllMoves(sol).isEmpty());
        // unbounded size
        moves = neighUnboundedSize.getAllMoves(sol);
        assertEquals(sol.getNumUnselectedIDs(), moves.size());
        for(Move<SubsetSolution> move : moves){
            assertTrue(move instanceof AdditionMove);
        }

        // 2) generate moves for full selection
        sol.selectAll();
        
        // var size
        assertTrue(neighVarSize.getAllMoves(sol).isEmpty());
        // fixed size
        assertTrue(neighFixedSize.getAllMoves(sol).isEmpty());
        // unbounded size
        moves = neighUnboundedSize.getAllMoves(sol);
        assertEquals(sol.getNumSelectedIDs(), moves.size());
        for(Move<SubsetSolution> move : moves){
            assertTrue(move instanceof DeletionMove);
        }
        
        // 3) generate moves for solution with 10 selected IDs
        sol.deselectAll();
        sol.selectAll(SetUtilities.getRandomSubset(sol.getUnselectedIDs(), 10, RG));
        
        // var size
        moves = neighVarSize.getAllMoves(sol);
        assertEquals(sol.getNumUnselectedIDs()                              // additions
                    + sol.getNumSelectedIDs()*sol.getNumUnselectedIDs()     // swaps 
                                , moves.size());
        for(Move<SubsetSolution> move : moves){
            assertFalse(move instanceof DeletionMove);
        }
        // fixed size
        moves = neighFixedSize.getAllMoves(sol);
        assertEquals(sol.getNumSelectedIDs()*sol.getNumUnselectedIDs()     // swaps 
                                , moves.size());
        for(Move<SubsetSolution> move : moves){
            assertTrue(move instanceof SwapMove);
        }
        // unbounded size
        moves = neighUnboundedSize.getAllMoves(sol);
        assertEquals(sol.getNumUnselectedIDs()                              // additions
                    + sol.getNumSelectedIDs()*sol.getNumUnselectedIDs()     // swaps 
                    + sol.getNumSelectedIDs()                               // deletions
                                , moves.size());
        
    }

    /**
     * Test of getMinSubsetSize method, of class SinglePerturbationNeighbourhood.
     */
    @Test
    public void testGetMinSubsetSize() {
        
        System.out.println(" - test getMinSubsetSize");
        
        // verify different neighbourhoods
        assertEquals(10, neighFixedSize.getMinSubsetSize());
        assertEquals(10, neighVarSize.getMinSubsetSize());
        assertEquals(0, neighUnboundedSize.getMinSubsetSize());
        
    }

    /**
     * Test of getMaxSubsetSize method, of class SinglePerturbationNeighbourhood.
     */
    @Test
    public void testGetMaxSubsetSize() {
        
        System.out.println(" - test getMaxSubsetSize");
        
        assertEquals(10, neighFixedSize.getMaxSubsetSize());
        assertEquals(20, neighVarSize.getMaxSubsetSize());
        assertEquals(NUM_IDS, neighUnboundedSize.getMaxSubsetSize());
        
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
        neighVarSize = new SinglePerturbationNeighbourhood(10, 20, fixedIDs);
        
        // randomly select 50% of IDs
        sol.selectAll(SetUtilities.getRandomSubset(sol.getAllIDs(), (int) (0.5*NUM_IDS), RG));
        
        // generate random moves and check that fixed IDs are never swapped
        for(int i=0; i<100; i++){
            SubsetMove move = (SubsetMove) neighVarSize.getRandomMove(sol);
            if(move != null){
                // verify
                for(int ID : fixedIDs){
                    assertFalse(move.getAddedIDs().contains(ID));
                    assertFalse(move.getDeletedIDs().contains(ID));
                }
            }
        }
        
        // generate all moves and verify that no fixed IDs are swapped
        for(Move move : neighVarSize.getAllMoves(sol)){
            SubsetMove sm = (SubsetMove) move;
            // verify
            for(int ID : fixedIDs){
                assertFalse(sm.getAddedIDs().contains(ID));
                assertFalse(sm.getDeletedIDs().contains(ID));
            }
        }
        
        // now fix ALL IDs
        neighVarSize = new SinglePerturbationNeighbourhood(10, 20, sol.getAllIDs());
        // check that no move can be generated
        assertNull(neighVarSize.getRandomMove(sol));
        assertTrue(neighVarSize.getAllMoves(sol).isEmpty());
        
    }

}