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

package org.jamesframework.ext.search.neigh.subset;

import java.util.HashSet;
import java.util.Random;
import java.util.Set;
import org.jamesframework.core.problems.solutions.SubsetSolution;
import org.jamesframework.core.util.SetUtilities;
import org.junit.Test;
import static org.junit.Assert.*;
import org.junit.BeforeClass;

/**
 * Test general subset move.
 * 
 * @author <a href="mailto:herman.debeukelaer@ugent.be">Herman De Beukelaer</a>
 */
public class GeneralSubsetMoveTest {

    // random generator
    private static final Random RG = new Random();
    
    // IDs
    private static Set<Integer> IDs;
    private static final int NUM_IDS = 100;
    
    /**
     * Create data.
     */
    @BeforeClass
    public static void setUpClass() {
        // create set of all IDs
        IDs = new HashSet<>();
        for(int i=0; i<NUM_IDS; i++){
            IDs.add(i);
        }
    }
    
    /**
     * Test of getAddedIDs method, of class GeneralSubsetMove.
     */
    @Test
    public void testGetAddedIDs() {
        
        System.out.println(" - test getAddedIDs");
        
        for(int i=0; i<100; i++){
            // pick random number of IDs to add
            int numadd = RG.nextInt(NUM_IDS);
            // pick random set of IDs
            Set<Integer> add = SetUtilities.getRandomSubset(IDs, numadd, RG);
            GeneralSubsetMove move = new GeneralSubsetMove(add, null);
            // verify
            assertEquals(add, move.getAddedIDs());
            assertEquals(numadd, move.getAddedIDs().size());
        }
        
    }

    /**
     * Test of getDeletedIDs method, of class GeneralSubsetMove.
     */
    @Test
    public void testGetDeletedIDs() {
        
        System.out.println(" - test getDeletedIDs");
        
        for(int i=0; i<100; i++){
            // pick random number of IDs to delete
            int numdel = RG.nextInt(NUM_IDS);
            // pick random set of IDs
            Set<Integer> delete = SetUtilities.getRandomSubset(IDs, numdel, RG);
            GeneralSubsetMove move = new GeneralSubsetMove(null, delete);
            // verify
            assertEquals(delete, move.getDeletedIDs());
            assertEquals(numdel, move.getDeletedIDs().size());
        }
        
    }

    /**
     * Test of getNumAdded method, of class GeneralSubsetMove.
     */
    @Test
    public void testGetNumAdded() {
        
        System.out.println(" - test getNumAdded");
        
        for(int i=0; i<100; i++){
            // pick random number of IDs to add
            int numadd = RG.nextInt(NUM_IDS);
            // pick random set of IDs
            Set<Integer> add = SetUtilities.getRandomSubset(IDs, numadd, RG);
            GeneralSubsetMove move = new GeneralSubsetMove(add, null);
            // verify
            assertEquals(numadd, move.getNumAdded());
        }
        
    }

    /**
     * Test of getNumDeleted method, of class GeneralSubsetMove.
     */
    @Test
    public void testGetNumDeleted() {
        
        System.out.println(" - test getNumDeleted");
        
        for(int i=0; i<100; i++){
            // pick random number of IDs to delete
            int numdel = RG.nextInt(NUM_IDS);
            // pick random set of IDs
            Set<Integer> delete = SetUtilities.getRandomSubset(IDs, numdel, RG);
            GeneralSubsetMove move = new GeneralSubsetMove(null, delete);
            // verify
            assertEquals(numdel, move.getNumDeleted());
        }
        
    }

    /**
     * Test of apply method, of class GeneralSubsetMove.
     */
    @Test
    public void testApply() {
        
        System.out.println(" - test apply");
        
        // create random subset solution (50%) selected
        SubsetSolution sol = new SubsetSolution(IDs);
        sol.selectAll(SetUtilities.getRandomSubset(IDs, NUM_IDS/2, RG));
        
        for(int i=0; i<100; i++){
            // pick random number of IDs to add and remove
            int numadd = RG.nextInt(sol.getNumUnselectedIDs());
            int numdel = RG.nextInt(sol.getNumSelectedIDs());
            // pick random set of IDs to add and remove
            Set<Integer> add = SetUtilities.getRandomSubset(sol.getUnselectedIDs(), numadd, RG);
            Set<Integer> delete = SetUtilities.getRandomSubset(sol.getSelectedIDs(), numdel, RG);
            // create move
            GeneralSubsetMove move = new GeneralSubsetMove(add, delete);
            // apply move
            move.apply(sol);
            // verify
            assertTrue(sol.getSelectedIDs().containsAll(add));
            assertTrue(sol.getUnselectedIDs().containsAll(delete));
        }
        
    }

    /**
     * Test of undo method, of class GeneralSubsetMove.
     */
    @Test
    public void testUndo() {
        
        System.out.println(" - test undo");
        
        // create random subset solution (50%) selected
        SubsetSolution sol = new SubsetSolution(IDs);
        sol.selectAll(SetUtilities.getRandomSubset(IDs, NUM_IDS/2, RG));
        
        // create backup
        SubsetSolution backup = new SubsetSolution(IDs);
        backup.selectAll(sol.getSelectedIDs());
        
        for(int i=0; i<100; i++){
            // pick random number of IDs to add and remove
            int numadd = RG.nextInt(sol.getNumUnselectedIDs());
            int numdel = RG.nextInt(sol.getNumSelectedIDs());
            // pick random set of IDs to add and remove
            Set<Integer> add = SetUtilities.getRandomSubset(sol.getUnselectedIDs(), numadd, RG);
            Set<Integer> delete = SetUtilities.getRandomSubset(sol.getSelectedIDs(), numdel, RG);
            // create move
            GeneralSubsetMove move = new GeneralSubsetMove(add, delete);
            // apply move
            move.apply(sol);
            // verify
            assertTrue(sol.getSelectedIDs().containsAll(add));
            assertTrue(sol.getUnselectedIDs().containsAll(delete));
            // undo move
            move.undo(sol);
            // verify
            assertEquals(backup, sol);
            assertTrue(sol.getUnselectedIDs().containsAll(add));
            assertTrue(sol.getSelectedIDs().containsAll(delete));
        }
        
    }

}