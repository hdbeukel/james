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
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 * Test fast limited queue.
 * 
 * @author <a href="mailto:herman.debeukelaer@ugent.be">Herman De Beukelaer</a>
 */
public class FastLimitedQueueTest {

    /**
     * Set up test class.
     */
    @BeforeClass
    public static void setUpClass() {
        System.out.println("# Testing FastLimitedQueue ...");
    }

    /**
     * Print message when tests are complete.
     */
    @AfterClass
    public static void tearDownClass() {
        System.out.println("# Done testing FastLimitedQueue!");
    }
    
    @Test(expected = IllegalArgumentException.class)
    public void testConstructor1(){
        System.out.println(" - test constructor (1)");
        new FastLimitedQueue<String>(0);
    }
    
    @Test(expected = IllegalArgumentException.class)
    public void testConstructor2(){
        System.out.println(" - test constructor (2)");
        new FastLimitedQueue<String>(-1);
    }

    @Test
    public void testQueue() {
        System.out.println(" - test queue");
        
        // create a queue with capacity of 3
        int limit = 3;
        FastLimitedQueue<String> queue = new FastLimitedQueue<>(limit);
        
        // check size and limit
        assertEquals(0, queue.size());
        assertEquals(limit, queue.sizeLimit());
        
        // declare some strings
        String s1 = "Once";
        String s2 = "upon";
        String s3 = "a";
        String s4 = "time";
        
        // add first element -- [s1]
        assertTrue(queue.add(s1));
        assertEquals(1, queue.size());
        assertTrue(queue.contains(s1));
        
        // add second and third element -- [s1, s2, s3]
        List<String> batch = new ArrayList<>(Arrays.asList(s2, s3));
        assertTrue(queue.addAll(batch));
        assertEquals(3, queue.size());
        assertTrue(queue.contains(s1));
        assertTrue(queue.contains(s2));
        assertTrue(queue.contains(s3));
        
        // try to add first element again
        assertFalse(queue.add(s1));
        
        // add fourth element -- [s2, s3, s4]
        assertTrue(queue.add(s4));
        assertEquals(3, queue.size());
        assertFalse(queue.contains(s1));
        assertTrue(queue.contains(s2));
        assertTrue(queue.contains(s3));
        assertTrue(queue.contains(s4));
        
        // re-add first element -- [s3, s4, s1]
        assertTrue(queue.add(s1));
        assertEquals(3, queue.size());
        assertFalse(queue.contains(s2));
        assertTrue(queue.contains(s3));
        assertTrue(queue.contains(s4));
        assertTrue(queue.contains(s1));
        
        // remove least recently added item -- [s4, s1]
        assertEquals(s3, queue.remove());
        assertEquals(2, queue.size());
        assertFalse(queue.contains(s2));
        assertFalse(queue.contains(s3));
        assertTrue(queue.contains(s4));
        assertTrue(queue.contains(s1));
        
        // clear queue
        queue.clear();
        assertEquals(0, queue.size());
        assertFalse(queue.contains(s1));
        assertFalse(queue.contains(s2));
        assertFalse(queue.contains(s3));
        assertFalse(queue.contains(s4));
        
    }

}