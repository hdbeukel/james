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
 * Test move that reverses a subsequence of a permutation.
 * 
 * @author <a href="mailto:herman.debeukelaer@ugent.be">Herman De Beukelaer</a>
 */
public class ReverseSubsequenceMoveTest {

    private static final Random RG = new Random();
    
    /**
     * Print message before running tests.
     */
    @BeforeClass
    public static void setUpClass() {
        System.out.println("# Testing ReverseSubsequenceMove ...");
    }

    /**
     * Print message when tests are complete.
     */
    @AfterClass
    public static void tearDownClass() {
        System.out.println("# Done testing ReverseSubsequenceMove!");
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
            // some subsequence reversals
            for(int j=0; j<50; j++){
                int from = RG.nextInt(n);
                int to = RG.nextInt(n-1);
                if(to >= from) to++;
                // create move
                ReverseSubsequenceMove move = new ReverseSubsequenceMove(from, to);
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
        
    }

}