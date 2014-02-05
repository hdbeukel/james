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

package org.jamesframework.core.search.neigh.subset;

import java.util.HashSet;
import java.util.List;
import java.util.Random;
import java.util.Set;
import org.jamesframework.core.problems.solutions.SubsetSolution;
import org.jamesframework.core.search.neigh.Move;
import org.jamesframework.core.search.neigh.Neighbourhood;
import org.jamesframework.core.util.SetUtilities;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import static org.junit.Assert.*;
import org.junit.Before;

/**
 * Test single swap subset neighbourhood.
 * 
 * @author Herman De Beukelaer <herman.debeukelaer@ugent.be>
 */
public class SingleSwapNeighbourhoodTest {

    // swap neighbourhood
    private Neighbourhood<SubsetSolution> neigh;
    
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
        System.out.println("# Testing SingleSwapNeighbourhood ...");
    }

    /**
     * Print message when tests are complete.
     */
    @AfterClass
    public static void tearDownClass() {
        System.out.println("# Done testing SingleSwapNeighbourhood!");
    }
    
    /**
     * Create subset solution and single swap neighbourhood to work with in each test method.
     */
    @Before
    public void setUp(){
        // initialize single swap neighbourhood
        neigh = new SingleSwapNeighbourhood();
        // initialize set of all IDs = {0 ... NUM_IDS-1}
        Set<Integer> IDs = new HashSet<>();
        for(int i=0; i < NUM_IDS; i++){
            IDs.add(i);
        }
        // initialize subset solution with these IDs, none selected
        sol = new SubsetSolution(IDs);
    }

    /**
     * Test of getRandomMove method, of class SingleSwapNeighbourhood.
     */
    @Test
    public void testGetRandomMove() {
        
        System.out.println(" - test getRandomMove");
        
        // test with empty subset solution
        assertNull(neigh.getRandomMove(sol));
        
        // test when all IDs selected
        sol.selectAll();
        assertNull(neigh.getRandomMove(sol));
        
        // randomly deselect 50% of IDs
        sol.deselectAll(SetUtilities.getRandomSubset(sol.getSelectedIDs(), (int)(0.5*sol.getNumSelectedIDs()), RG));
        
        // generate, apply and undo 50 moves
        Move<SubsetSolution> move;
        SubsetSolution copy;
        for(int i=0; i<50; i++){
            // generate move
            move = neigh.getRandomMove(sol);
            // verify added/deleted ID of move
            assertTrue(sol.getSelectedIDs().contains(((SwapMove)move).getDeletedID()));
            assertTrue(sol.getUnselectedIDs().contains(((SwapMove)move).getAddedID()));
            // copy solution
            copy = new SubsetSolution(sol.getAllIDs());
            copy.selectAll(sol.getSelectedIDs());
            // apply move
            move.apply(sol);
            // undo move
            move.undo(sol);
            // verify
            assertEquals(copy, sol);
        }
        
    }

    /**
     * Test of getAllMoves method, of class SingleSwapNeighbourhood.
     */
    @Test
    public void testGetAllMoves() {
       
        System.out.println(" - test getAllMoves");
        
        // test with empty subset solution
        assertTrue(neigh.getAllMoves(sol).isEmpty());
        
        // test when all IDs selected
        sol.selectAll();
        assertTrue(neigh.getAllMoves(sol).isEmpty());
        
        // randomly deselect 50% of IDs
        sol.deselectAll(SetUtilities.getRandomSubset(sol.getSelectedIDs(), (int)(0.5*sol.getNumSelectedIDs()), RG));
        
        // generate all possible moves
        List<Move<SubsetSolution>> moves = neigh.getAllMoves(sol);
        // verify size
        assertEquals(sol.getNumSelectedIDs()*sol.getNumUnselectedIDs(), moves.size());
        
        // verify that all selected IDs where considered for deletion, and
        // that all unselected IDs where considered for addition
        Set<Integer> delCandidates = new HashSet<>();
        Set<Integer> addCandidates = new HashSet<>();
        for(Move<SubsetSolution> move : moves){
            delCandidates.add(((SwapMove)move).getDeletedID());
            addCandidates.add(((SwapMove)move).getAddedID());
        }
        // verify
        assertEquals(delCandidates, sol.getSelectedIDs());
        assertEquals(addCandidates, sol.getUnselectedIDs());
        
    }

}