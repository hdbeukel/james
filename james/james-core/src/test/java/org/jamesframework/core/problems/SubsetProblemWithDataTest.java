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

package org.jamesframework.core.problems;

import java.util.Random;
import org.jamesframework.core.problems.solutions.SubsetSolution;
import org.jamesframework.core.util.SetUtilities;
import org.jamesframework.test.fakes.ScoredFakeSubsetData;
import org.jamesframework.test.fakes.SumOfScoresFakeSubsetObjective;
import org.jamesframework.test.fakes.SumOfIDsFakeSubsetObjective;
import org.jamesframework.test.fakes.MinDiffFakeSubsetPenalizingConstraint;
import org.jamesframework.test.util.TestConstants;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import static org.junit.Assert.*;
import org.junit.Before;

/**
 * Test SubsetProblemWithData.
 * 
 * @author <a href="mailto:herman.debeukelaer@ugent.be">Herman De Beukelaer</a>
 */
public class SubsetProblemWithDataTest {

    // fake subset data
    private ScoredFakeSubsetData fakeData;
    // scores set in fake subset data (10 entities)
    private final double[] SCORES = new double[] {0.76722, 0.1752, 0.134006, 0.680481, 0.0911487,
                                                  0.0185549, 0.270955, 0.126619, 0.18375, 0.850669};
    
    // fake objectives
    private SumOfIDsFakeSubsetObjective fakeObjIgnoringData; 
    private SumOfScoresFakeSubsetObjective fakeObjUsingData;
    
    private SubsetProblemWithData<ScoredFakeSubsetData> problem1, problem2;
    private final int PROBLEM_1_FIXED_SIZE = 5;
    private final int PROBLEM_2_MIN_SIZE = 2;
    private final int PROBLEM_2_MAX_SIZE = 8;
    
    // fake constraint
    private MinDiffFakeSubsetPenalizingConstraint fakeConstraint;
    // minimum score diff imposed by fake constraint
    private final double MIN_SCORE_DIFF = 0.05;
    
    // random generator
    private static final Random RG = new Random();
    
    /**
     * Print message when starting tests.
     */
    @BeforeClass
    public static void setUpClass() {
        System.out.println("# Testing SubsetProblemWithData ...");
    }

    /**
     * Print message when tests are complete.
     */
    @AfterClass
    public static void tearDownClass() {
        System.out.println("# Done testing SubsetProblemWithData!");
    }
    
    /**
     * Create subset problems to work with in each test method.
     */
    @Before
    public void setUp(){
        // create fake subset data
        fakeData = new ScoredFakeSubsetData(SCORES);
        // create fake objectives
        fakeObjIgnoringData = new SumOfIDsFakeSubsetObjective();  // ignores the data
        fakeObjUsingData = new SumOfScoresFakeSubsetObjective();        // actually uses the data
        // create fake constraint
        fakeConstraint = new MinDiffFakeSubsetPenalizingConstraint(MIN_SCORE_DIFF);
        // create subset problems
        problem1 = new SubsetProblemWithData<>(fakeObjIgnoringData, fakeData, PROBLEM_1_FIXED_SIZE);
        problem2 = new SubsetProblemWithData<>(fakeObjUsingData, fakeData, PROBLEM_2_MIN_SIZE, PROBLEM_2_MAX_SIZE);
    }
    
    /**
     * Test constructors, of class SubsetProblemWithData.
     */
    @Test
    public void testConstructors(){
        
        System.out.println(" - test constructors");
        
        // try to create a subset problem without objective
        boolean thrown = false;
        try {
            SubsetProblemWithData<?> p = new SubsetProblemWithData<>(null, fakeData, 5);
        } catch (NullPointerException ex) {
            thrown = true;
        }
        assertTrue(thrown);
        
        // try to create a subset problem without data
        thrown = false;
        try {
            SubsetProblemWithData<?> p = new SubsetProblemWithData<>(fakeObjIgnoringData, null, 5);
        } catch (NullPointerException ex) {
            thrown = true;
        }
        assertTrue(thrown);
        
        // try to set invalid subset sizes
        thrown = false;
        try {
            SubsetProblemWithData<?> p = new SubsetProblemWithData<>(fakeObjIgnoringData, fakeData, -1);
        } catch (IllegalArgumentException ex) {
            thrown = true;
        }
        assertTrue(thrown);
        thrown = false;
        try {
            SubsetProblemWithData<?> p = new SubsetProblemWithData<>(fakeObjIgnoringData, fakeData, fakeData.getIDs().size()+1);
        } catch (IllegalArgumentException ex) {
            thrown = true;
        }
        assertTrue(thrown);
        thrown = false;
        try {
            SubsetProblemWithData<?> p = new SubsetProblemWithData<>(fakeObjIgnoringData, fakeData, 4, 2);
        } catch (IllegalArgumentException ex) {
            thrown = true;
        }
        assertTrue(thrown);
        
        // verify that no errors are thrown for valid params
        SubsetProblemWithData<?> p;
        p = new SubsetProblemWithData<>(fakeObjIgnoringData, fakeData, 2, 4);
        p = new SubsetProblemWithData<>(fakeObjIgnoringData, fakeData, 1, fakeData.getIDs().size());
        p = new SubsetProblemWithData<>(fakeObjIgnoringData, fakeData, 1);
        p = new SubsetProblemWithData<>(fakeObjIgnoringData, fakeData, fakeData.getIDs().size());
        
    }

    /**
     * Test of setData method, of class SubsetProblemWithData.
     */
    @Test
    public void testSetData() {
        
        System.out.println(" - test setData");
        
        // try to set data to null
        boolean thrown = false;
        try {
            problem1.setData(null);
        } catch (NullPointerException ex) {
            thrown = true;
        }
        assertTrue(thrown);
        
        // set valid data
        problem1.setData(fakeData);
        
    }

    /**
     * Test of createRandomSolution method, of class SubsetProblemWithData.
     */
    @Test
    public void testCreateRandomSolution() {
        
        System.out.println(" - test createRandomSolution");
        
        // create and verify some random solutions
        final int repeats = 100;
        SubsetSolution sol1, sol2;
        for(int r=0; r<repeats; r++){
            // create random solution for problem 1
            sol1 = problem1.createRandomSolution();
            assertEquals(fakeData.getIDs(), sol1.getAllIDs());
            assertEquals(PROBLEM_1_FIXED_SIZE, sol1.getNumSelectedIDs());
            // create random solution for problem 2
            sol2 = problem2.createRandomSolution();
            assertEquals(fakeData.getIDs(), sol2.getAllIDs());
            assertTrue(sol2.getNumSelectedIDs() >= PROBLEM_2_MIN_SIZE);
            assertTrue(sol2.getNumSelectedIDs() <= PROBLEM_2_MAX_SIZE);
        }
        
    }

    /**
     * Test of copySolution method, of class SubsetProblemWithData.
     */
    @Test
    public void testCopySolution() {
        
        System.out.println(" - test copySolution");
        
        // create some random solutions, copy them, and verify
        final int repeats = 100;
        SubsetSolution sol1, sol2, copy1, copy2;
        for(int r=0; r<repeats; r++){
            // create random solution for problem 1
            sol1 = problem1.createRandomSolution();
            // copy it
            copy1 = problem1.copySolution(sol1);
            // verify
            assertEquals(sol1, copy1);
           // create random solution for problem 2
            sol2 = problem2.createRandomSolution();
            // copy it
            copy2 = problem2.copySolution(sol2);
            // verify
            assertEquals(sol2, copy2);
        }
        
    }

    /**
     * Test of createEmptySubsetSolution method, of class SubsetProblemWithData.
     */
    @Test
    public void testCreateEmptySubsetSolution() {
        
        System.out.println(" - test createEmptySubsetSolution");
        
        // create empty solution
        SubsetSolution sol = problem1.createEmptySubsetSolution();
        // verify
        assertEquals(fakeData.getIDs(), sol.getAllIDs());
        assertEquals(0, sol.getNumSelectedIDs());
        
    }

    /**
     * Test of getMinSubsetSize method, of class SubsetProblemWithData.
     */
    @Test
    public void testGetMinSubsetSize() {
        
        System.out.println(" - test getMinSubsetSize");
        
        // verify problem 1
        assertEquals(PROBLEM_1_FIXED_SIZE, problem1.getMinSubsetSize());
        // verify problem 2
        assertEquals(PROBLEM_2_MIN_SIZE, problem2.getMinSubsetSize());
        
    }

    /**
     * Test of setMinSubsetSize method, of class SubsetProblemWithData.
     */
    @Test
    public void testSetMinSubsetSize() {
        
        System.out.println(" - test setMinSubsetSize");
        
        SubsetProblemWithData<?>[] problems = new SubsetProblemWithData<?>[] {problem1, problem2};
        
        for(SubsetProblemWithData<?> p : problems){
            // try to set invalid minimum size
            boolean thrown = false;
            try {
                p.setMinSubsetSize(-1);
            } catch (IllegalArgumentException ex){
                thrown = true;
            }
            assertTrue(thrown);
            thrown = false;
            try {
                p.setMinSubsetSize(0);
            } catch (IllegalArgumentException ex){
                thrown = true;
            }
            assertTrue(thrown);
            thrown = false;
            try {
                p.setMinSubsetSize(p.getMaxSubsetSize()+1);
            } catch (IllegalArgumentException ex){
                thrown = true;
            }
            assertTrue(thrown);
            // set valid values
            p.setMinSubsetSize(1);
            p.setMinSubsetSize(p.getMaxSubsetSize());
        }
        
    }

    /**
     * Test of getMaxSubsetSize method, of class SubsetProblemWithData.
     */
    @Test
    public void testGetMaxSubsetSize() {
        
        System.out.println(" - test getMaxSubsetSize");
        
        // verify problem 1
        assertEquals(PROBLEM_1_FIXED_SIZE, problem1.getMaxSubsetSize());
        // verify problem 2
        assertEquals(PROBLEM_2_MAX_SIZE, problem2.getMaxSubsetSize());
        
    }

    /**
     * Test of setMaxSubsetSize method, of class SubsetProblemWithData.
     */
    @Test
    public void testSetMaxSubsetSize() {
        
        System.out.println(" - test setMaxSubsetSize");
        
        SubsetProblemWithData<?>[] problems = new SubsetProblemWithData<?>[] {problem1, problem2};
        
        for(SubsetProblemWithData<?> p : problems){
            // try to set invalid maximum size
            boolean thrown = false;
            try {
                p.setMaxSubsetSize(p.getMinSubsetSize()-1);
            } catch (IllegalArgumentException ex){
                thrown = true;
            }
            assertTrue(thrown);
            thrown = false;
            try {
                p.setMaxSubsetSize(fakeData.getIDs().size()+1);
            } catch (IllegalArgumentException ex){
                thrown = true;
            }
            assertTrue(thrown);
            // set valid values
            p.setMaxSubsetSize(p.getMinSubsetSize());
            p.setMaxSubsetSize(fakeData.getIDs().size());
        }
        
    }
    
    /**
     * Test of evaluate method, of class SubsetProblemWithData.
     */
    @Test
    public void testEvaluate(){
        
        System.out.println(" - test evaluate");
        
        final int repeats = 100;
        SubsetSolution sol;
        
        /**********************************************************************************/
        /* test with problem 1 (ignoring subset data -- objective is sum of selected IDs) */
        /**********************************************************************************/
        
        // 1) no constraints
        for(int r=0; r<repeats; r++){
            // create random solution
            sol = problem1.createRandomSolution();
            // evaluate and verify
            int expected = 0;
            for(int ID : sol.getSelectedIDs()){
                expected += ID;
            }
            assertEquals((double) expected, problem1.evaluate(sol), TestConstants.DOUBLE_COMPARISON_PRECISION);
        }
        
        // 2) with fake subset constraint (uses underlying subset data)

        problem1.addPenalizingConstraint(fakeConstraint);
        
        // select a solution within the constraint
        sol = problem1.createEmptySubsetSolution();
        sol.select(0);
        sol.select(1);
        sol.select(3);
        sol.select(6);
        sol.select(9);
        // evaluate and verify (constraint should have no impact)
        int expected = 0;
        for(int ID : sol.getSelectedIDs()){
            expected += ID;
        }
        // verify
        assertEquals((double) expected, problem1.evaluate(sol), TestConstants.DOUBLE_COMPARISON_PRECISION);

        // modify solution to violate the constraint (selected = 0 1 3 8 9)
        sol.deselect(6);
        sol.select(8);
        // evaluate and verify with penalizing constraint
        expected = 0;
        for(int ID : sol.getSelectedIDs()){
            expected += ID;
        }
        // account for penalty (1 difference below minimum)
        expected -= 1;
        // verify
        assertEquals((double) expected, problem1.evaluate(sol), TestConstants.DOUBLE_COMPARISON_PRECISION);
        
        // modify solution to violate constraint more severely (selected = 1 2 3 7 8)
        sol.deselect(9);
        sol.deselect(0);
        sol.select(2);
        sol.select(7);
        // evaluate and verify with penalizing constraint
        expected = 0;
        for(int ID : sol.getSelectedIDs()){
            expected += ID;
        }
        // account for penalty (5 differences below minimum)
        expected -= 5;
        // verify
        assertEquals((double) expected, problem1.evaluate(sol), TestConstants.DOUBLE_COMPARISON_PRECISION);
        
        /**********************************************************************************************/
        /* test with problem 2 (using subset data -- objective is sum of scores of selected entities) */
        /**********************************************************************************************/
        
        // 1) no constraints
        for(int r=0; r<repeats; r++){
            // create random solution
            sol = problem2.createRandomSolution();
            // evaluate and verify
            double expected2 = 0;
            for(int ID : sol.getSelectedIDs()){
                expected2 += SCORES[ID];
            }
            assertEquals(expected2, problem2.evaluate(sol), TestConstants.DOUBLE_COMPARISON_PRECISION);
        }
        
        // 2) with fake subset constraint (uses underlying subset data)

        problem2.addPenalizingConstraint(fakeConstraint);
        
        // select a solution within the constraint
        sol = problem1.createEmptySubsetSolution();
        sol.select(0);
        sol.select(1);
        sol.select(3);
        sol.select(6);
        sol.select(9);
        // evaluate and verify (constraint should have no impact)
        double expected2 = 0.0;
        for(int ID : sol.getSelectedIDs()){
            expected2 += SCORES[ID];
        }
        // verify
        assertEquals(expected2, problem2.evaluate(sol), TestConstants.DOUBLE_COMPARISON_PRECISION);

        // modify solution to violate the constraint (selected = 0 1 3 8 9)
        sol.deselect(6);
        sol.select(8);
        // evaluate and verify
        expected2 = 0.0;
        for(int ID : sol.getSelectedIDs()){
            expected2 += SCORES[ID];
        }
        // account for penalty (1 difference below minimum)
        expected2 -= 1.0;
        // verify
        assertEquals(expected2, problem2.evaluate(sol), TestConstants.DOUBLE_COMPARISON_PRECISION);
        
        // modify solution to violate constraint more severely (selected = 1 2 3 7 8)
        sol.deselect(9);
        sol.deselect(0);
        sol.select(2);
        sol.select(7);
        // evaluate and verify
        expected2 = 0.0;
        for(int ID : sol.getSelectedIDs()){
            expected2 += SCORES[ID];
        }
        // account for penalty (5 differences below minimum)
        expected2 -= 5.0;
        // verify
        assertEquals(expected2, problem2.evaluate(sol), TestConstants.DOUBLE_COMPARISON_PRECISION);
        
    }
    
    /**
     * Test of rejectSolution method, of class SubsetProblemWithData.
     */
    @Test
    public void testRejectSolution(){
        
        System.out.println(" - test rejectSolution");
        
        // check whether solutions of invalid size are rejected
        
        // too small
        SubsetSolution tooSmall = problem2.createEmptySubsetSolution();
        tooSmall.selectAll(SetUtilities.getRandomSubset(tooSmall.getUnselectedIDs(), PROBLEM_2_MIN_SIZE-1, RG));
        // verify
        assertTrue(problem2.rejectSolution(tooSmall));
        
        // too large
        SubsetSolution tooLarge = problem2.createEmptySubsetSolution();
        tooLarge.selectAll(SetUtilities.getRandomSubset(tooLarge.getUnselectedIDs(), PROBLEM_2_MAX_SIZE+1, RG));
        // verify
        assertTrue(problem2.rejectSolution(tooLarge));
        
        // valid size
        SubsetSolution valid = problem2.createEmptySubsetSolution();
        valid.selectAll(SetUtilities.getRandomSubset(valid.getUnselectedIDs(), (PROBLEM_2_MIN_SIZE+PROBLEM_2_MAX_SIZE)/2, RG));
        // verify
        assertFalse(problem2.rejectSolution(valid));
        
    }
    
    /**
     * Test sorted IDs.
     */
    @Test
    public void testSortedIDs(){
        System.out.println(" - test sorted IDs");
        problem1 = new SubsetProblemWithData<>(fakeObjIgnoringData, fakeData, PROBLEM_1_FIXED_SIZE, PROBLEM_1_FIXED_SIZE, true);
        SubsetSolution sol = problem1.createRandomSolution();
        Integer prevID = null;
        for(int ID : sol.getAllIDs()){
            if(prevID != null){
                assertTrue(ID > prevID);
            }
            prevID = ID;
        }
        prevID = null;
        for(int ID : sol.getSelectedIDs()){
            if(prevID != null){
                assertTrue(ID > prevID);
            }
            prevID = ID;
        }
        prevID = null;
        for(int ID : sol.getUnselectedIDs()){
            if(prevID != null){
                assertTrue(ID > prevID);
            }
            prevID = ID;
        }
        // repeat with copy
        sol = problem1.copySolution(sol);
        prevID = null;
        for(int ID : sol.getAllIDs()){
            if(prevID != null){
                assertTrue(ID > prevID);
            }
            prevID = ID;
        }
        prevID = null;
        for(int ID : sol.getSelectedIDs()){
            if(prevID != null){
                assertTrue(ID > prevID);
            }
            prevID = ID;
        }
        prevID = null;
        for(int ID : sol.getUnselectedIDs()){
            if(prevID != null){
                assertTrue(ID > prevID);
            }
            prevID = ID;
        }
        // repeat with empty solution
        sol = problem1.createEmptySubsetSolution();
        prevID = null;
        for(int ID : sol.getAllIDs()){
            if(prevID != null){
                assertTrue(ID > prevID);
            }
            prevID = ID;
        }
        prevID = null;
        for(int ID : sol.getSelectedIDs()){
            if(prevID != null){
                assertTrue(ID > prevID);
            }
            prevID = ID;
        }
        prevID = null;
        for(int ID : sol.getUnselectedIDs()){
            if(prevID != null){
                assertTrue(ID > prevID);
            }
            prevID = ID;
        }
        // select all and repeat
        sol.selectAll();
        prevID = null;
        for(int ID : sol.getAllIDs()){
            if(prevID != null){
                assertTrue(ID > prevID);
            }
            prevID = ID;
        }
        prevID = null;
        for(int ID : sol.getSelectedIDs()){
            if(prevID != null){
                assertTrue(ID > prevID);
            }
            prevID = ID;
        }
        prevID = null;
        for(int ID : sol.getUnselectedIDs()){
            if(prevID != null){
                assertTrue(ID > prevID);
            }
            prevID = ID;
        }
    }

}