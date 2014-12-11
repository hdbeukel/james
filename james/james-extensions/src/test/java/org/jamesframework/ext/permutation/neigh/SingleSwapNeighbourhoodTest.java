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
import java.util.Collections;
import java.util.List;
import java.util.Random;
import org.jamesframework.ext.permutation.PermutationSolution;
import org.jamesframework.ext.permutation.neigh.moves.SingleSwapMove;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 * Test single swap neighbourhood (for permutation solutions).
 * 
 * @author <a href="mailto:herman.debeukelaer@ugent.be">Herman De Beukelaer</a>
 */
public class SingleSwapNeighbourhoodTest {

    private static final Random RG = new Random();
    
    /**
     * Print message before running tests.
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
     * Test of getRandomMove method, of class SingleSwapNeighbourhood.
     */
    @Test
    public void testGetRandomMove() {
        
        System.out.println(" - test getRandomMove");
        
        // create neighbourhood
        SingleSwapNeighbourhood neigh = new SingleSwapNeighbourhood();
        
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
                // generate random swap move
                SingleSwapMove move = neigh.getRandomMove(sol);
                int s1 = move.getI();
                int s2 = move.getJ();
                // verify
                assertTrue(s1 >= 0);
                assertTrue(s2 >= 0);
                assertTrue(s1 < n);
                assertTrue(s2 < n);
                assertTrue(s1 != s2);
                // apply move
                move.apply(sol);
                // verify
                for(int k=0; k<sol.size(); k++){
                    if(k == s1){
                        assertEquals(order.get(k), sol.getOrder().get(s2));
                    } else if (k == s2){
                        assertEquals(order.get(k), sol.getOrder().get(s1));
                    } else {
                        assertEquals(order.get(k), sol.getOrder().get(k));
                    }
                }
                // undo move
                move.undo(sol);
            }
        }
        
    }

    /**
     * Test of getAllMoves method, of class SingleSwapNeighbourhood.
     */
    @Test
    public void testGetAllMoves() {
        
        System.out.println(" - test getAllMoves");
        
        // create neighbourhood
        SingleSwapNeighbourhood neigh = new SingleSwapNeighbourhood();
        
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
                List<SingleSwapMove> moves = neigh.getAllMoves(sol);
                // verify number
                assertEquals(n*(n-1)/2, moves.size());
                // check duplicates
                assertEquals(n*(n-1)/2, moves.stream().distinct().count());
                // check contained moves
                for(int s1=0; s1<n; s1++){
                    for(int s2=0; s2<n; s2++){
                        if(s1 != s2){
                            assertTrue(moves.contains(new SingleSwapMove(s1, s2)));
                        }
                    }
                }
            }
        }
        
    }

}