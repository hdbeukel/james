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

package org.jamesframework.ext.permutation;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Random;
import org.jamesframework.core.exceptions.SolutionModificationException;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 * Test permutation solution.
 * 
 * @author <a href="mailto:herman.debeukelaer@ugent.be">Herman De Beukelaer</a>
 */
public class PermutationSolutionTest {

    private static final Random RG = new Random();
    
    /**
     * Print message before running tests.
     */
    @BeforeClass
    public static void setUpClass() {
        System.out.println("# Testing PermutationSolution ...");
    }

    /**
     * Print message when tests are complete.
     */
    @AfterClass
    public static void tearDownClass() {
        System.out.println("# Done testing PermutationSolution!");
    }

    /**
     * Test constructor.
     */
    @Test
    public void testConstructor() {
        
        System.out.println(" - test constructor ");
        
        boolean thrown = false;
        try{
            PermutationSolution sol = new PermutationSolution(null);
        } catch(NullPointerException ex){
            thrown = true;
        }
        assertTrue(thrown);
        
        thrown = false;
        try{
            List<Integer> order = Arrays.asList(1, 2, 3, null, 4, 5, 6);
            PermutationSolution sol = new PermutationSolution(order);
        } catch(NullPointerException ex){
            thrown = true;
        }
        assertTrue(thrown);
        
        thrown = false;
        try{
            List<Integer> order = Collections.emptyList();
            PermutationSolution sol = new PermutationSolution(order);
        } catch(IllegalArgumentException ex){
            thrown = true;
        }
        assertTrue(thrown);
        
        thrown = false;
        try{
            List<Integer> order = Arrays.asList(1, 2, 3, 1, 4, 5, 6);
            PermutationSolution sol = new PermutationSolution(order);
        } catch(IllegalArgumentException ex){
            thrown = true;
        }
        assertTrue(thrown);
        
    }
    
    /**
     * Test of getOrder method, of class PermutationSolution.
     */
    @Test
    public void testGetOrder() {
        
        System.out.println(" - test getOrder ");
        
        for(int i=0; i<100; i++){
            int n = RG.nextInt(500)+1;
            List<Integer> order = new ArrayList<>();
            for(int j=0; j<n; j++){
                order.add(j);
            }
            Collections.shuffle(order);
            PermutationSolution sol = new PermutationSolution(order);
            // verify
            assertEquals(order, sol.getOrder());
        }
        
    }

    /**
     * Test of size method, of class PermutationSolution.
     */
    @Test
    public void testSize() {
        
        System.out.println(" - test size ");
        
        for(int i=0; i<100; i++){
            int n = RG.nextInt(500)+1;
            List<Integer> order = new ArrayList<>();
            for(int j=0; j<n; j++){
                order.add(j);
            }
            Collections.shuffle(order);
            PermutationSolution sol = new PermutationSolution(order);
            // verify
            assertEquals(order.size(), sol.size());
        }
        
    }

    /**
     * Test of swap method, of class PermutationSolution.
     */
    @Test
    public void testSwap() {
        
        System.out.println(" - test swap ");
        
        for(int i=0; i<100; i++){
            int n = RG.nextInt(500)+1;
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
                // perform swap
                sol.swap(s1, s2);
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
                // undo swap
                sol.swap(s1, s2);
            }
        }
        
        // test with invalid arguments
        
        List<Integer> order = Arrays.asList(1, 2, 3, 4, 5, 6);
        PermutationSolution sol = new PermutationSolution(order);
        
        boolean thrown = false;
        try{
            sol.swap(3, 3);
        } catch(SolutionModificationException ex){
            thrown = true;
        }
        assertTrue(thrown);
        
        thrown = false;
        try{
            sol.swap(-1, 3);
        } catch(SolutionModificationException ex){
            thrown = true;
        }
        assertTrue(thrown);
        
        thrown = false;
        try{
            sol.swap(3, -1);
        } catch(SolutionModificationException ex){
            thrown = true;
        }
        assertTrue(thrown);
        
        thrown = false;
        try{
            sol.swap(6, 3);
        } catch(SolutionModificationException ex){
            thrown = true;
        }
        assertTrue(thrown);
        
        thrown = false;
        try{
            sol.swap(3, 6);
        } catch(SolutionModificationException ex){
            thrown = true;
        }
        assertTrue(thrown);
        
    }

    /**
     * Test copy, equals and hashCode.
     */
    @Test
    public void testCopyAndEquals() {
        
        System.out.println(" - test copy, equals and hashCode");
        
        for(int i=0; i<100; i++){
            int n = RG.nextInt(500)+1;
            List<Integer> order = new ArrayList<>();
            for(int j=0; j<n; j++){
                order.add(j);
            }
            Collections.shuffle(order);
            PermutationSolution sol = new PermutationSolution(order);
            // copy
            PermutationSolution copy = sol.copy();
            // verify
            assertEquals(copy, sol);
            assertEquals(copy.hashCode(), sol.hashCode());
        }
        
    }

}