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

package org.jamesframework.core.search.neigh.subset.adv;

import java.util.HashSet;
import java.util.Random;
import java.util.Set;
import org.jamesframework.core.problems.solutions.SubsetSolution;
import org.jamesframework.core.search.neigh.Move;
import org.jamesframework.core.search.neigh.Neighbourhood;
import org.jamesframework.core.search.neigh.subset.SingleSwapNeighbourhood;
import org.jamesframework.core.search.neigh.subset.SubsetMove;
import org.jamesframework.core.search.neigh.subset.SwapMove;
import org.jamesframework.core.util.SetUtilities;
import org.junit.BeforeClass;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 * Test multi swap neighbourhood.
 * 
 * @author <a href="mailto:herman.debeukelaer@ugent.be">Herman De Beukelaer</a>
 */
public class MultiSwapNeighbourhoodTest {

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
     * Test of getRandomMove method, of class MultiSwapNeighbourhood.
     */
    @Test
    public void testGetRandomMove() {
        
        System.out.println(" - test getRandomMove");
        
        // repeat for maximum of 1 up to 5 swaps
        for(int s=1; s<=5; s++){
            // create multi swap neighbourhood
            Neighbourhood<SubsetSolution> neigh = new MultiSwapNeighbourhood(s);

            // create empty subset solution
            SubsetSolution sol = new SubsetSolution(IDs);
            // verify: no move generated
            assertNull(neigh.getRandomMove(sol));

            // randomly select 10 IDs
            sol.selectAll(SetUtilities.getRandomSubset(sol.getUnselectedIDs(), 10, RG));

            SubsetMove move;
            for(int i=0; i<1000; i++){
                move = (SubsetMove) neigh.getRandomMove(sol);
                // verify
                assertTrue(sol.getUnselectedIDs().containsAll(move.getAddedIDs()));
                assertTrue(sol.getSelectedIDs().containsAll(move.getDeletedIDs()));
                assertTrue(move.getNumAdded() >= 1);
                assertTrue(move.getNumAdded() <= s);
                assertTrue(move.getNumDeleted()>= 1);
                assertTrue(move.getNumDeleted() <= s);
                // apply move
                move.apply(sol);
            }
        }
        
    }

    /**
     * Test of getAllMoves method, of class MultiSwapNeighbourhood.
     */
    @Test
    public void testGetAllMoves() {
        
        System.out.println(" - test getAllMoves");
        
        // 1) compare with single swap neighbourhood by setting maxSwaps to 1
        
        // create multi swap neighbourhood
        Neighbourhood<SubsetSolution> ssn = new SingleSwapNeighbourhood();
        Neighbourhood<SubsetSolution> msn = new MultiSwapNeighbourhood(1);

        // create empty subset solution
        SubsetSolution sol = new SubsetSolution(IDs);
        // verify: no moves generated
        assertTrue(msn.getAllMoves(sol).isEmpty());

        // randomly select 10 IDs
        sol.selectAll(SetUtilities.getRandomSubset(sol.getUnselectedIDs(), 10, RG));

        Set<Move<SubsetSolution>> moves1, moves2, temp;
        
        moves1 = ssn.getAllMoves(sol);
        moves2 = msn.getAllMoves(sol);
        // verify
        assertEquals(sol.getNumSelectedIDs()*sol.getNumUnselectedIDs(), moves2.size());
        assertEquals(moves1.size(), moves2.size());
        temp = new HashSet<>();
        for(Move<SubsetSolution> m : moves2){
            SubsetMove sm = (SubsetMove) m;
            assertEquals(1, sm.getNumAdded());
            assertEquals(1, sm.getNumDeleted());
            temp.add(new SwapMove(sm.getAddedIDs().iterator().next(), sm.getDeletedIDs().iterator().next()));
        }
        assertEquals(temp, moves1);
        
        // 2) test with maxSwaps 2 up to 5
        
        for(int s=2; s<=5; s++){
            // create multi swap neighbourhood
            Neighbourhood<SubsetSolution> neigh = new MultiSwapNeighbourhood(s);
            // compute number of expected moves
            int num = 0;
            for(int j=1; j<=s; j++){
                num += numSubsets(sol.getSelectedIDs().size(), j)*numSubsets(sol.getUnselectedIDs().size(), j);
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
        
        // create neighbourhood with max 2 swaps
        
        // create subset solution with 10 selected IDs
        SubsetSolution sol = new SubsetSolution(IDs);
        sol.selectAll(SetUtilities.getRandomSubset(IDs, 10, RG));
        
        // randomly fix 50% of all IDs
        Set<Integer> fixedIDs = SetUtilities.getRandomSubset(sol.getAllIDs(), (int) (0.5*NUM_IDS), RG);
        // create new neighbourhood with fixed IDs (max 2 swaps)
        Neighbourhood<SubsetSolution> neigh = new MultiSwapNeighbourhood(2, fixedIDs);
        
        // generate random moves and check that fixed IDs are never swapped
        for(int i=0; i<1000; i++){
            SubsetMove move = (SubsetMove) neigh.getRandomMove(sol);
            if(move != null){
                // verify
                for(int ID : fixedIDs){
                    assertFalse(move.getAddedIDs().contains(ID));
                    assertFalse(move.getDeletedIDs().contains(ID));
                }
            }
        }
        
        // generate all moves and verify that no fixed IDs are swapped
        for(Move move : neigh.getAllMoves(sol)){
            SubsetMove sm = (SubsetMove) move;
            // verify
            for(int ID : fixedIDs){
                assertFalse(sm.getAddedIDs().contains(ID));
                assertFalse(sm.getDeletedIDs().contains(ID));
            }
        }
        
        // now fix ALL IDs
        neigh = new SingleSwapNeighbourhood(sol.getAllIDs());
        // check that no move can be generated
        assertNull(neigh.getRandomMove(sol));
        assertTrue(neigh.getAllMoves(sol).isEmpty());
        
    }

}