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

package org.jamesframework.core.search.algo.tabu;

import java.util.Arrays;
import java.util.HashSet;
import org.jamesframework.core.problems.solutions.SubsetSolution;
import org.jamesframework.core.search.neigh.subset.AdditionMove;
import org.jamesframework.core.search.neigh.subset.DeletionMove;
import org.jamesframework.core.search.neigh.subset.SubsetMove;
import org.jamesframework.core.search.neigh.subset.SwapMove;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 * Test ID base subset tabu memory.
 * 
 * @author <a href="mailto:herman.debeukelaer@ugent.be">Herman De Beukelaer</a>
 */
public class IDBasedSubsetTabuMemoryTest {

    /**
     * Print message when starting tests.
     */
    @BeforeClass
    public static void setUpClass() {
        System.out.println("# Testing IDBasedSubsetTabuMemory ...");
    }

    /**
     * Print message when tests are complete.
     */
    @AfterClass
    public static void tearDownClass() {
        System.out.println("# Done testing IDBasedSubsetTabuMemory!");
    }

    /**
     * Test of isTabu method, of class IDBasedSubsetTabuMemory.
     */
    @Test
    public void testIDBasedSubsetTabuMemory() {
    
        System.out.println(" - test ID based subset tabu memory");

        // create memory with size 4
        TabuMemory<SubsetSolution> mem = new IDBasedSubsetTabuMemory(4);
        
        // create subset solution {0,1,2} -- rest = {3,4,5}
        SubsetSolution sol = new SubsetSolution(new HashSet<>(Arrays.asList(0,1,2,3,4,5)), new HashSet<>(Arrays.asList(0,1,2)));
        // create swap move: 3 <-> 2
        SubsetMove move = new SwapMove(3, 2);
        
        // check: not tabu (memory is empty)
        assertFalse(mem.isTabu(move, sol));
        
        // register current solution
        mem.registerVisitedSolution(sol, null);

        // CURRENTLY TABU: none
        
        // check again: still not tabu
        assertFalse(mem.isTabu(move, sol));
        
        // apply move to current solution
        move.apply(sol); // sol = {0,1,3} -- rest = {2,4,5}
        // register newly visited solution
        mem.registerVisitedSolution(sol, move);
        
        // CURRENTLY TABU: 3,2
        
        // create inverse swap move: 2 <-> 3
        move = new SwapMove(2, 3);
        // verify: applying this move is tabu (both involved IDs are tabu)
        assertTrue(mem.isTabu(move, sol));
        
        // create addition move: +4
        move = new AdditionMove(4);
        // check: not tabu
        assertFalse(mem.isTabu(move, sol));
        
        // apply move
        move.apply(sol); // sol = {0,1,3,4} -- rest = {2,5}
        // register newly visited solution
        mem.registerVisitedSolution(sol, move);
        
        // CURRENTLY TABU: 3,2,4
        
        // create deletion move: -4
        move = new DeletionMove(4);
        // check: tabu
        assertTrue(mem.isTabu(move, sol));
        
        // other deletion move: -3
        move = new DeletionMove(3);
        // check: also tabu
        assertTrue(mem.isTabu(move, sol));
        
        // other deletion move: -0
        move = new DeletionMove(0);
        // check: not tabu
        assertFalse(mem.isTabu(move, sol));
        
        // apply move
        move.apply(sol); // sol = {1,3,4} -- rest = {0,2,5}
        // register newly visited solution
        mem.registerVisitedSolution(sol, move);
        
        // CURRENTLY TABU: 3,2,4,0
        
        // swap move: 2 <-> 1
        move = new SwapMove(2, 1);
        // check: tabu
        assertTrue(mem.isTabu(move, sol));
        
        // swap move: 2 <-> 3
        move = new SwapMove(2, 3);
        // check: tabu
        assertTrue(mem.isTabu(move, sol));
        
        // swap move: 2 <-> 4
        move = new SwapMove(2,4);
        // check: tabu
        assertTrue(mem.isTabu(move, sol));
        
        // swap move: 0 <-> 1
        move = new SwapMove(0, 1);
        // check: tabu
        assertTrue(mem.isTabu(move, sol));
        
        // swap move: 0 <-> 3
        move = new SwapMove(0, 3);
        // check: tabu
        assertTrue(mem.isTabu(move, sol));
        
        // swap move: 0 <-> 4
        move = new SwapMove(0,4);
        // check: tabu
        assertTrue(mem.isTabu(move, sol));
        
        // swap move: 5 <-> 3
        move = new SwapMove(5, 3);
        // check: tabu
        assertTrue(mem.isTabu(move, sol));
        
        // swap move: 5 <-> 4
        move = new SwapMove(5,4);
        // check: tabu
        assertTrue(mem.isTabu(move, sol));
        
        // swap move: 5 <-> 1
        move = new SwapMove(5,1);
        // check: not tabu
        assertFalse(mem.isTabu(move, sol));
        
        // deletion move: -1
        move = new DeletionMove(1);
        // check: not tabu
        assertFalse(mem.isTabu(move, sol));
        
        // apply move
        move.apply(sol); // sol = {3,4} -- rest = {0,1,2,5}
        // register newly visited solution
        mem.registerVisitedSolution(sol, move);
        
        // CURRENTLY TABU: 2,4,0,1 -- discarded: 3
        
        // deletion move: -3
        move = new DeletionMove(3);
        // check: not tabu
        assertFalse(mem.isTabu(move, sol));
        
        // apply move
        move.apply(sol); // sol = {4} -- rest = {0,1,2,3,5}
        // register newly visited solution
        mem.registerVisitedSolution(sol, move);
        
        // CURRENTLY TABU: 4,0,1,3 -- discarded: 2
        
        for(int i=0; i<=5; i++){
            if(i != 4){
                // swap move
                move = new SwapMove(i, 4);
                // check: tabu
                assertTrue(mem.isTabu(move, sol));
            }
        }
        
        // clear tabu memory
        mem.clear();
        // check: all swaps allowed now
        for(int i=0; i<=5; i++){
            if(i != 4){
                // swap move
                move = new SwapMove(i, 4);
                // check: not tabu
                assertFalse(mem.isTabu(move, sol));
            }
        }
        
    }
    
    /**
     * Test of constructor of class IDBasedSubsetTabuMemory.
     */
    @Test(expected = IllegalArgumentException.class)
    public void testConstructor1() {
    
        System.out.println(" - test constructor (1)");
        
        TabuMemory<?> dummy = new IDBasedSubsetTabuMemory(-1);
        
    }
    
    /**
     * Test of constructor of class FullTabuMemory.
     */
    @Test(expected = IllegalArgumentException.class)
    public void testConstructor2() {
    
        System.out.println(" - test constructor (2)");
        
        TabuMemory<?> dummy = new IDBasedSubsetTabuMemory(0);
        
    }

}