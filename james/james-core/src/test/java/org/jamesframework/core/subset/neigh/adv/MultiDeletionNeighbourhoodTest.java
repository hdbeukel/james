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
import org.jamesframework.core.subset.neigh.SingleDeletionNeighbourhood;
import org.jamesframework.core.subset.neigh.SingleSwapNeighbourhood;
import org.jamesframework.core.subset.neigh.moves.AdditionMove;
import org.jamesframework.core.subset.neigh.moves.DeletionMove;
import org.jamesframework.core.subset.neigh.moves.SubsetMove;
import org.jamesframework.core.util.SetUtilities;
import org.junit.BeforeClass;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 * Test multi deletion neighbourhood.
 * 
 * @author <a href="mailto:herman.debeukelaer@ugent.be">Herman De Beukelaer</a>
 */
public class MultiDeletionNeighbourhoodTest {

    // random generator
    private static final Random RG = new Random();
    
    // IDs
    private static Set<Integer> IDs;
    private static final int NUM_IDS = 20;
    
    @BeforeClass
    public static void setUpClass() {
        // create set of all IDs
        IDs = new HashSet<>();
        for(int i=0; i<NUM_IDS; i++){
            IDs.add(i);
        }
    }

    /**
     * Test of getRandomMove method, of class MultiDeletionNeighbourhood.
     */
    @Test
    public void testGetRandomMove() {
        
        System.out.println(" - test getRandomMove");
        
        // repeat for maximum of 1 up to 5 deletions
        for(int d=1; d<=5; d++){
            // create multi deletion neighbourhood
            Neighbourhood<SubsetSolution> neigh = new MultiDeletionNeighbourhood(d);

            // create empty subset solution
            SubsetSolution sol = new SubsetSolution(IDs);
            // verify: no move generated
            assertNull(neigh.getRandomMove(sol));

            // select all IDs
            sol.selectAll();

            // apply moves until no IDs are selected
            SubsetMove move;
            while((move = (SubsetMove) neigh.getRandomMove(sol)) != null){
                // verify
                assertTrue(sol.getSelectedIDs().containsAll(move.getDeletedIDs()));
                assertTrue(move.getAddedIDs().isEmpty());
                assertTrue(move.getNumAdded() == 0);
                assertTrue(move.getNumDeleted() >= 1);
                assertTrue(move.getNumDeleted() <= d);
                assertTrue(move.getNumDeleted() <= sol.getNumSelectedIDs());
                // apply move
                move.apply(sol);
            }
            
            // check: no more moves to be generated
            assertNull(neigh.getRandomMove(sol));
            // check: no IDs selected
            assertEquals(0, sol.getNumSelectedIDs());
            
            // select all
            sol.selectAll();
            
            // create new neighbourhood with size limit of 10
            int limit = 10;
            neigh = new MultiDeletionNeighbourhood(d, limit);
            
            // apply moves until minimum size is reached
            while((move = (SubsetMove) neigh.getRandomMove(sol)) != null){
                // verify
                assertTrue(sol.getSelectedIDs().containsAll(move.getDeletedIDs()));
                assertTrue(move.getAddedIDs().isEmpty());
                assertTrue(move.getNumAdded() == 0);
                assertTrue(move.getNumDeleted() >= 1);
                assertTrue(move.getNumDeleted() <= d);
                assertTrue(move.getNumDeleted() <= sol.getNumSelectedIDs());
                // apply move
                move.apply(sol);
            }
            
            // check: no more moves to be generated
            assertNull(neigh.getRandomMove(sol));
            // check: minimum number of IDs selected
            assertEquals(limit, sol.getNumSelectedIDs());
            
        }
        
    }

    /**
     * Test of getAllMoves method, of class MultiDeletionNeighbourhood.
     */
    @Test
    public void testGetAllMoves() {
        
        System.out.println(" - test getAllMoves");
        
        // 1) compare with single deletion neighbourhood by setting maxDeletions to 1
        
        // create neighbourhoods
        SingleDeletionNeighbourhood sdn = new SingleDeletionNeighbourhood();
        MultiDeletionNeighbourhood mdn = new MultiDeletionNeighbourhood(1);

        // create empty subset solution
        SubsetSolution sol = new SubsetSolution(IDs);
        // verify: no moves generated
        assertTrue(sdn.getAllMoves(sol).isEmpty());
        assertTrue(mdn.getAllMoves(sol).isEmpty());

        // select all IDs
        sol.selectAll();

        Set<SubsetMove> moves1, moves2, temp;
        
        moves1 = sdn.getAllMoves(sol);
        moves2 = mdn.getAllMoves(sol);
        // verify
        assertEquals(sol.getNumSelectedIDs(), moves2.size());
        assertEquals(moves1.size(), moves2.size());
        temp = new HashSet<>();
        moves2.forEach(m -> {
            SubsetMove sm = (SubsetMove) m;
            assertEquals(0, sm.getNumAdded());
            assertEquals(1, sm.getNumDeleted());
            temp.add(new DeletionMove(sm.getDeletedIDs().iterator().next()));
        });
        assertEquals(temp, moves1);
        assertEquals(temp, moves2);
        assertEquals(moves1, moves2);
        
        // 2) test with maxDeletions 2 up to 5
        
        for(int d=2; d<=5; d++){
            // create multi deletion neighbourhood
            MultiDeletionNeighbourhood neigh = new MultiDeletionNeighbourhood(d);
            // compute number of expected moves
            int num = 0;
            for(int j=1; j<=d; j++){
                num += numSubsets(sol.getNumSelectedIDs(), j);
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
        
        // randomly fix 5 selected IDs
        Set<Integer> fixedIDs = SetUtilities.getRandomSubset(sol.getSelectedIDs(), 5, RG);
        // create new neighbourhood with fixed IDs (max 2 deletions)
        Neighbourhood<SubsetSolution> neigh = new MultiDeletionNeighbourhood(2, 0, fixedIDs);
        
        // generate random moves and check that fixed IDs are never removed
        SubsetMove move;
        while((move = (SubsetMove) neigh.getRandomMove(sol)) != null){
            // verify
            for(int ID : fixedIDs){
                assertFalse(move.getDeletedIDs().contains(ID));
            }
            // apply move
            move.apply(sol);
        }
        
        // verify: only fixed IDs remain selected
        assertEquals(5, sol.getNumSelectedIDs());
        assertEquals(fixedIDs, sol.getSelectedIDs());
        
        // select 10 IDs
        sol.deselectAll();
        sol.selectAll(SetUtilities.getRandomSubset(IDs, 10, RG));

        // generate all moves and verify that no fixed IDs are removed
        neigh.getAllMoves(sol).stream()
                              .map(m -> (SubsetMove) m)
                              .forEach(m -> {
                                  fixedIDs.forEach(ID -> {
                                    assertFalse(m.getDeletedIDs().contains(ID));
                                  });
                              });
        
        // select all
        sol.selectAll();
        // now fix ALL IDs
        neigh = new MultiDeletionNeighbourhood(2, 0, sol.getAllIDs());
        // check that no move can be generated
        assertNull(neigh.getRandomMove(sol));
        assertTrue(neigh.getAllMoves(sol).isEmpty());
        
    }

}