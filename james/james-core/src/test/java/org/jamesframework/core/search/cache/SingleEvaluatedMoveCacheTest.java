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

package org.jamesframework.core.search.cache;

import java.util.Collection;
import java.util.HashSet;
import java.util.Random;
import org.jamesframework.core.problems.constraints.validations.Validation;
import org.jamesframework.core.problems.constraints.validations.SimpleValidation;
import org.jamesframework.core.problems.objectives.evaluations.Evaluation;
import org.jamesframework.core.problems.objectives.evaluations.SimpleEvaluation;
import org.jamesframework.core.subset.SubsetSolution;
import org.jamesframework.core.search.neigh.Move;
import org.jamesframework.core.subset.neigh.moves.SwapMove;
import org.jamesframework.test.util.TestConstants;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import static org.junit.Assert.*;
import org.junit.Before;

/**
 * Test single evaluated move cache.
 * 
 * @author <a href="mailto:herman.debeukelaer@ugent.be">Herman De Beukelaer</a>
 */
public class SingleEvaluatedMoveCacheTest {

    // cache to work with in each test method
    private EvaluatedMoveCache cache;
    
    // random generator
    private static final Random RG = new Random();
    
    /**
     * Print message when starting tests.
     */
    @BeforeClass
    public static void setUpClass() {
        System.out.println("# Testing SingleEvaluatedMoveCache ...");
    }

    /**
     * Print message when tests are complete.
     */
    @AfterClass
    public static void tearDownClass() {
        System.out.println("# Done testing SingleEvaluatedMoveCache!");
    }
    
    @Before
    public void setUp(){
        // create empty single evaluated move cache
        cache = new SingleEvaluatedMoveCache();
    }

    /**
     * Test of cacheMoveEvaluation method, of class SingleEvaluatedMoveCache.
     */
    @Test
    public void testCacheMoveEvaluation() {
        
        System.out.println(" - test cacheMoveEvaluation");
        
        // create dummy moves and evaluations
        SwapMove m1 = new SwapMove(-1, -2);
        SwapMove m2 = new SwapMove(-3, -4);
        Evaluation m1eval = new SimpleEvaluation(123.0),
                   m2eval = new SimpleEvaluation(345.0);
        
        // check empty cache
        assertNull(cache.getCachedMoveEvaluation(m1));
        
        // cache an evaluation
        cache.cacheMoveEvaluation(m1, m1eval);
        // verify
        assertNotNull(cache.getCachedMoveEvaluation(m1));
        assertEquals(m1eval.getValue(), cache.getCachedMoveEvaluation(m1).getValue(), TestConstants.DOUBLE_COMPARISON_PRECISION);
        
        // cache an other evaluation
        cache.cacheMoveEvaluation(m2, m2eval);
        // verify: first value overwritten
        assertNull(cache.getCachedMoveEvaluation(m1));
        // verify new value
        assertNotNull(cache.getCachedMoveEvaluation(m2));
        assertEquals(m2eval.getValue(), cache.getCachedMoveEvaluation(m2).getValue(), TestConstants.DOUBLE_COMPARISON_PRECISION);
        
        // test cache hit for equal swap move (other object)
        SwapMove copy = new SwapMove(m2.getAddedID(), m2.getDeletedID());
        // verify
        assertNotNull(cache.getCachedMoveEvaluation(copy));
        assertEquals(m2eval.getValue(), cache.getCachedMoveEvaluation(copy).getValue(), TestConstants.DOUBLE_COMPARISON_PRECISION);
        
    }

    /**
     * Test of cacheMoveValidation method, of class SingleEvaluatedMoveCache.
     */
    @Test
    public void testCacheMoveValidation() {
        
        System.out.println(" - test cacheMoveValidation");
        
        // create dummy moves and validities
        SwapMove m1 = new SwapMove(-1, -2);
        SwapMove m2 = new SwapMove(-3, -4);
        Validation m1validation = new SimpleValidation(true),
                   m2validation = new SimpleValidation(false);
        
        // check empty cache
        assertNull(cache.getCachedMoveValidation(m1));
        
        // cache validation of first move
        cache.cacheMoveValidation(m1, m1validation);
        // verify
        assertNotNull(cache.getCachedMoveValidation(m1));
        assertEquals(m1validation.passed(), cache.getCachedMoveValidation(m1).passed());
        
        // cache validity of second move
        cache.cacheMoveValidation(m2, m2validation);
        // verify: first value overwritten
        assertNull(cache.getCachedMoveValidation(m1));
        // verify new value
        assertNotNull(cache.getCachedMoveValidation(m2));
        assertEquals(m2validation.passed(), cache.getCachedMoveValidation(m2).passed());
        
        // test cache hit for equal swap move (other object)
        SwapMove copy = new SwapMove(m2.getAddedID(), m2.getDeletedID());
        // verify
        assertNotNull(cache.getCachedMoveValidation(copy));
        assertEquals(m2validation.passed(), cache.getCachedMoveValidation(copy).passed());
        
    }

    /**
     * Test of clear method, of class SingleEvaluatedMoveCache.
     */
    @Test
    public void testClear() {
        
        System.out.println(" - test clear");
        
        Collection<Move<SubsetSolution>> moves = new HashSet<>();
        for(int i=0; i<100; i++){
            // create random dummy swap move
            SwapMove m = new SwapMove(RG.nextInt(), RG.nextInt());
            moves.add(m);
            // cache random values
            cache.cacheMoveEvaluation(m, new SimpleEvaluation(RG.nextDouble()));
            cache.cacheMoveValidation(m, new SimpleValidation(RG.nextBoolean()));
        }
        
        // clear cache
        cache.clear();
        
        // verify: no values available after clearing cache
        moves.forEach(m -> {
            assertNull(cache.getCachedMoveEvaluation(m));
            assertNull(cache.getCachedMoveValidation(m));
        });
        
    }

}