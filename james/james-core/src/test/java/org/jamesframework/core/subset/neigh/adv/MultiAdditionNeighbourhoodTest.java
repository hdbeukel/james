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

package org.jamesframework.core.subset.neigh.adv;

import java.util.HashSet;
import java.util.Random;
import java.util.Set;
import java.util.stream.Collectors;
import org.jamesframework.core.subset.SubsetSolution;
import org.jamesframework.core.search.neigh.Neighbourhood;
import org.jamesframework.core.subset.neigh.SingleAdditionNeighbourhood;
import org.jamesframework.core.subset.neigh.moves.AdditionMove;
import org.jamesframework.core.subset.neigh.moves.SubsetMove;
import org.jamesframework.core.util.SetUtilities;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 * Test multi addition neighbourhood.
 * 
 * @author <a href="mailto:herman.debeukelaer@ugent.be">Herman De Beukelaer</a>
 */
public class MultiAdditionNeighbourhoodTest {

    // random generator
    private static final Random RG = new Random();
    
    // IDs
    private static Set<Integer> IDs;
    private static final int NUM_IDS = 20;
    
    @BeforeClass
    public static void setUpClass() {
        System.out.println("# Testing MultiAdditionNeighbourhood ...");
        // create set of all IDs
        IDs = new HashSet<>();
        for(int i=0; i<NUM_IDS; i++){
            IDs.add(i);
        }
    }

    /**
     * Print message when tests are complete.
     */
    @AfterClass
    public static void tearDownClass() {
        System.out.println("# Done testing MultiAdditionNeighbourhood!");
    }

    /**
     * Test of getRandomMove method, of class MultiAdditionNeighbourhood.
     */
    @Test
    public void testGetRandomMove() {
        
        System.out.println(" - test getRandomMove");
        
        // repeat for maximum of 1 up to 5 additions
        for(int a=1; a<=5; a++){
            // create multi addition neighbourhood
            Neighbourhood<SubsetSolution> neigh = new MultiAdditionNeighbourhood(a);

            // create full subset solution
            SubsetSolution sol = new SubsetSolution(IDs);
            sol.selectAll();
            // verify: no move generated
            assertNull(neigh.getRandomMove(sol));

            // deselect all IDs
            sol.deselectAll();

            // apply moves until all IDs are selected
            SubsetMove move;
            while((move = (SubsetMove) neigh.getRandomMove(sol)) != null){
                // verify
                assertTrue(sol.getUnselectedIDs().containsAll(move.getAddedIDs()));
                assertTrue(move.getDeletedIDs().isEmpty());
                assertTrue(move.getNumDeleted() == 0);
                assertTrue(move.getNumAdded() >= 1);
                assertTrue(move.getNumAdded() <= a);
                assertTrue(move.getNumAdded() <= sol.getNumUnselectedIDs());
                // apply move
                move.apply(sol);
            }
            
            // check: no more moves to be generated
            assertNull(neigh.getRandomMove(sol));
            // check: all IDs selected
            assertEquals(NUM_IDS, sol.getNumSelectedIDs());
            
            // deselect all
            sol.deselectAll();
            
            // create new neighbourhood with size limit of 10
            int limit = 10;
            neigh = new MultiAdditionNeighbourhood(a, limit);
            
            // apply moves until maximum size is reached
            while((move = (SubsetMove) neigh.getRandomMove(sol)) != null){
                // verify
                assertTrue(sol.getUnselectedIDs().containsAll(move.getAddedIDs()));
                assertTrue(move.getDeletedIDs().isEmpty());
                assertTrue(move.getNumDeleted() == 0);
                assertTrue(move.getNumAdded() >= 1);
                assertTrue(move.getNumAdded() <= a);
                assertTrue(move.getNumAdded() <= sol.getNumUnselectedIDs());
                // apply move
                move.apply(sol);
            }
            
            // check: no more moves to be generated
            assertNull(neigh.getRandomMove(sol));
            // check: maximum number of IDs selected
            assertEquals(limit, sol.getNumSelectedIDs());
            
        }
        
    }

    /**
     * Test of getAllMoves method, of class MultiAdditionNeighbourhood.
     */
    @Test
    public void testGetAllMoves() {
        
        System.out.println(" - test getAllMoves");
        
        // 1) compare with single addition neighbourhood by setting maxAdditions to 1
        
        // create neighbourhoods
        SingleAdditionNeighbourhood san = new SingleAdditionNeighbourhood();
        MultiAdditionNeighbourhood man = new MultiAdditionNeighbourhood(1);

        // create full subset solution
        SubsetSolution sol = new SubsetSolution(IDs);
        sol.selectAll();
        // verify: no moves generated
        assertTrue(san.getAllMoves(sol).isEmpty());
        assertTrue(man.getAllMoves(sol).isEmpty());

        // deselect all IDs
        sol.deselectAll();

        Set<SubsetMove> moves1, moves2, temp;
        
        moves1 = san.getAllMoves(sol);
        moves2 = man.getAllMoves(sol);
        // verify
        assertEquals(sol.getNumUnselectedIDs(), moves2.size());
        assertEquals(moves1.size(), moves2.size());
        temp = new HashSet<>();
        moves2.forEach(m -> {
            SubsetMove sm = (SubsetMove) m;
            assertEquals(1, sm.getNumAdded());
            assertEquals(0, sm.getNumDeleted());
            temp.add(new AdditionMove(sm.getAddedIDs().iterator().next()));
        });
        assertEquals(temp, moves1);
        assertEquals(temp, moves2);
        assertEquals(moves1, moves2);
        
        // 2) test with maxAdditions 2 up to 5
        
        for(int a=2; a<=5; a++){
            // create multi addition neighbourhood
            MultiAdditionNeighbourhood neigh = new MultiAdditionNeighbourhood(a);
            // compute number of expected moves
            int num = 0;
            for(int j=1; j<=a; j++){
                num += numSubsets(sol.getUnselectedIDs().size(), j);
            }
            // generate all moves
            moves1 = neigh.getAllMoves(sol);
            // verify
            assertEquals(num, moves1.size());
        }
        
    }
    
    // compute number of possible subsets of size subsetSize taken from set of size setSize
    private int numSubsets(int setSize, int subsetSize){
        int num = 1;
        for(int t=setSize; t>=setSize-subsetSize+1; t--){
            num *= t;
        }
        for(int n=2; n<=subsetSize; n++){
            num /= n;
        }
        return num;
    }
    
    /**
     * Test with fixed IDs.
     */
    @Test
    public void testWithFixedIDs() {
        
        System.out.println(" - test with fixed IDs");
                
        // create subset solution with 10 selected IDs
        SubsetSolution sol = new SubsetSolution(IDs);
        sol.selectAll(SetUtilities.getRandomSubset(IDs, 10, RG));
        
        // randomly fix 5 unselected IDs
        Set<Integer> fixedIDs = SetUtilities.getRandomSubset(sol.getUnselectedIDs(), 5, RG);
        // create new neighbourhood with fixed IDs (max 2 additions)
        Neighbourhood<SubsetSolution> neigh = new MultiAdditionNeighbourhood(2, NUM_IDS, fixedIDs);
        
        // generate random moves and check that fixed IDs are never added
        SubsetMove move;
        while((move = (SubsetMove) neigh.getRandomMove(sol)) != null){
            // verify
            for(int ID : fixedIDs){
                assertFalse(move.getAddedIDs().contains(ID));
            }
            // apply move
            move.apply(sol);
        }
        
        // verify: all non-fixed IDs selected
        assertEquals(NUM_IDS-5, sol.getNumSelectedIDs());
        assertEquals(sol.getAllIDs().stream().filter(ID -> !fixedIDs.contains(ID)).collect(Collectors.toSet()), sol.getSelectedIDs());
        
        // select 10 IDs
        sol.deselectAll();
        sol.selectAll(SetUtilities.getRandomSubset(IDs, 10, RG));

        // generate all moves and verify that no fixed IDs are added
        neigh.getAllMoves(sol).stream()
                              .map(m -> (SubsetMove) m)
                              .forEach(m -> {
                                  fixedIDs.forEach(ID -> {
                                    assertFalse(m.getAddedIDs().contains(ID));
                                  });
                              });
        
        // deselect all
        sol.deselectAll();
        // now fix ALL IDs
        neigh = new MultiAdditionNeighbourhood(2, NUM_IDS, sol.getAllIDs());
        // check that no move can be generated
        assertNull(neigh.getRandomMove(sol));
        assertTrue(neigh.getAllMoves(sol).isEmpty());
        
    }

}