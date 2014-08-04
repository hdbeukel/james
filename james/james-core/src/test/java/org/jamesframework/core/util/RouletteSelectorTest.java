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

package org.jamesframework.core.util;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;
import org.junit.AfterClass;
import static org.junit.Assert.*;
import org.junit.BeforeClass;
import org.junit.Test;

/**
 * Test RouletteSelector.
 * 
 * @author <a href="mailto:herman.debeukelaer@ugent.be">Herman De Beukelaer</a>
 */
public class RouletteSelectorTest {
    
    // roulette selector
    private static final RouletteSelector<String> roulette = new RouletteSelector<>(new Random());
    
    /**
     * Set up test class.
     */
    @BeforeClass
    public static void setUpClass() {
        System.out.println("# Testing RouletteSelector ...");
    }

    /**
     * Print message when tests are complete.
     */
    @AfterClass
    public static void tearDownClass() {
        System.out.println("# Done testing RouletteSelector!");
    }

    /**
     * Test roulette selector.
     */
    @Test
    public void testSelect() {
        
        System.out.println(" - testing select");
        
        // empty lists
        assertNull(roulette.select(new ArrayList<String>(), new ArrayList<Double>()));
        
        // create item list
        List<String> items = Arrays.asList("Banana", "Peach", "Strawberry", "Mango");
        
        // all weights zero
        assertNull(roulette.select(items, Arrays.asList(0.0, 0.0, 0.0, 0.0)));
        
        // all but one weight zero
        assertEquals("Strawberry", roulette.select(items, Arrays.asList(0.0, 0.0, 123.4, 0.0)));
        
        // all but one weight not zero
        List<Double> weights = Arrays.asList(1.2, 0.0, 3.2, 4.0);
        for(int i=0; i<100; i++){
            assertNotEquals("Peach", roulette.select(items, weights));
        }
        
        // no weights zero
        weights = Arrays.asList(1.2, 0.3, 3.2, 4.0);
        for(int i=0; i<100; i++){
            String selected = roulette.select(items, weights);
            assertNotNull(selected);
            assertTrue(items.contains(selected));
        }
        
        // check: item list can contain null elements
        items = Arrays.asList(null, null, null, null);
        for(int i=0; i<100; i++){
            assertNull(roulette.select(items, weights));
        }
        
    }

}