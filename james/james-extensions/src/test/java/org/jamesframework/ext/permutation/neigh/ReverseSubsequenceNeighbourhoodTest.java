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

package org.jamesframework.ext.permutation.neigh;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Random;
import org.jamesframework.ext.permutation.PermutationSolution;
import org.jamesframework.ext.permutation.neigh.moves.ReverseSubsequenceMove;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 * Test reverse subsequence neighbourhood (for permutation solutions).
 * 
 * @author <a href="mailto:herman.debeukelaer@ugent.be">Herman De Beukelaer</a>
 */
public class ReverseSubsequenceNeighbourhoodTest {

    private static final Random RG = new Random();
    
    /**
     * Print message before running tests.
     */
    @BeforeClass
    public static void setUpClass() {
        System.out.println("# Testing ReverseSubsequenceNeighbourhood ...");
    }

    /**
     * Print message when tests are complete.
     */
    @AfterClass
    public static void tearDownClass() {
        System.out.println("# Done testing ReverseSubsequenceNeighbourhood!");
    }

    /**
     * Test of getRandomMove method, of class ReverseSubsequenceNeighbourhood.
     */
    @Test
    public void testGetRandomMove() {
        
        System.out.println(" - test getRandomMove");
        
        // create neighbourhood
        ReverseSubsequenceNeighbourhood neigh = new ReverseSubsequenceNeighbourhood();
        
        // test
        for(int i=0; i<100; i++){
            int n = RG.nextInt(499)+2;
            List<Integer> order = new ArrayList<>();
            for(int j=0; j<n; j++){
                order.add(j);
            }
            Collections.shuffle(order);
            PermutationSolution sol = new PermutationSolution(order);
            // some swaps
            for(int j=0; j<50; j++){
                // generate random move
                ReverseSubsequenceMove move = neigh.getRandomMove(sol);
                int from = move.getFrom();
                int to = move.getTo();
                // apply move
                move.apply(sol);
                // verify
                assertEquals(order.get(from), sol.getOrder().get(to));
                assertEquals(order.get(to), sol.getOrder().get(from));
                int r = (to-1+n)%n;
                for(int k=(from+1)%n; k!=to; k=(k+1)%n){
                    assertEquals(order.get(r), sol.getOrder().get(k));
                    r = (r-1+n)%n;
                }
                for(int k=(to+1)%n; k!=from; k=(k+1)%n){
                    assertEquals(order.get(k), sol.getOrder().get(k));
                }
                // undo move
                move.undo(sol);
            }
        }
        
        // test with permuation solution of size 1: no swap possible
        PermutationSolution sol = new PermutationSolution(Arrays.asList(3));
        assertNull(neigh.getRandomMove(sol));
        
    }

    /**
     * Test of getAllMoves method, of class ReverseSubsequenceNeighbourhood.
     */
    @Test
    public void testGetAllMoves() {
        
        System.out.println(" - test getAllMoves");
        
        // create neighbourhood
        ReverseSubsequenceNeighbourhood neigh = new ReverseSubsequenceNeighbourhood();
        
        // test
        for(int i=0; i<10; i++){
            int n = RG.nextInt(99)+2;
            List<Integer> order = new ArrayList<>();
            for(int j=0; j<n; j++){
                order.add(j);
            }
            Collections.shuffle(order);
            PermutationSolution sol = new PermutationSolution(order);
            // some swaps
            for(int j=0; j<10; j++){
                // generate all swap moves
                List<ReverseSubsequenceMove> moves = neigh.getAllMoves(sol);
                // verify number
                assertEquals(n*(n-1), moves.size());
                // check duplicates
                assertEquals(n*(n-1), moves.stream().distinct().count());
                // check contained moves
                for(int s1=0; s1<n; s1++){
                    for(int s2=0; s2<n; s2++){
                        if(s1 != s2){
                            assertTrue(moves.contains(new ReverseSubsequenceMove(s1, s2)));
                        }
                    }
                }
            }
        }
        
        // test with permuation solution of size 1: no swap possible
        PermutationSolution sol = new PermutationSolution(Arrays.asList(3));
        assertTrue(neigh.getAllMoves(sol).isEmpty());
        
    }

}