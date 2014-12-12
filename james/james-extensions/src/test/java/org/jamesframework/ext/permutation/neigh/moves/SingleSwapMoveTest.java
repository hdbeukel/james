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

package org.jamesframework.ext.permutation.neigh.moves;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;
import org.jamesframework.ext.permutation.PermutationSolution;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 * Test single swap move (for permutation solution).
 * 
 * @author <a href="mailto:herman.debeukelaer@ugent.be">Herman De Beukelaer</a>
 */
public class SingleSwapMoveTest {

    private static final Random RG = new Random();
    
    /**
     * Print message before running tests.
     */
    @BeforeClass
    public static void setUpClass() {
        System.out.println("# Testing SingleSwapMove ...");
    }

    /**
     * Print message when tests are complete.
     */
    @AfterClass
    public static void tearDownClass() {
        System.out.println("# Done testing SingleSwapMove!");
    }

    /**
     * Test of apply and undo.
     */
    @Test
    public void testApplyAndUndo() {
       
        System.out.println(" - test apply and undo");
        
        for(int i=0; i<1000; i++){
            int n = RG.nextInt(499)+2;
            List<Integer> order = new ArrayList<>();
            for(int j=0; j<n; j++){
                order.add(j);
            }
            Collections.shuffle(order);
            PermutationSolution sol = new PermutationSolution(order);
            // some swaps
            for(int j=0; j<50; j++){
                int s1 = RG.nextInt(n);
                int s2 = RG.nextInt(n-1);
                if(s2 >= s1) s2++;
                // create swap move
                SingleSwapMove move = new SingleSwapMove(s1, s2);
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
    
    @Test
    public void testEquals(){
        System.out.println(" - test equals");
        
        // create some moves
        SingleSwapMove m1 = new SingleSwapMove(3, 7);
        SingleSwapMove m2 = new SingleSwapMove(3, 7);
        SingleSwapMove m3 = new SingleSwapMove(7, 3);
        SingleSwapMove m4 = new SingleSwapMove(3, 8);
        SingleSwapMove m5 = new SingleSwapMove(2, 7);
        SingleSwapMove m6 = new SingleSwapMove(1, 6);
        
        // verify
        assertEquals(m1, m2);
        assertEquals(m1, m3);
        assertNotEquals(m1, m4);
        assertNotEquals(m1, m5);
        assertNotEquals(m1, m6);
        
        assertNotEquals(m1, null);
        assertNotEquals(m1, "Trudy");
        
    }

}