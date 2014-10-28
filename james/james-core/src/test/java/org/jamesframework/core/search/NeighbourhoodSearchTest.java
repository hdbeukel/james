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

package org.jamesframework.core.search;

import java.util.Collections;
import java.util.Set;
import org.jamesframework.core.problems.Problem;
import org.jamesframework.core.subset.SubsetProblem;
import org.jamesframework.core.problems.Solution;
import org.jamesframework.core.problems.constraints.Constraint;
import org.jamesframework.core.problems.constraints.validations.SimpleValidation;
import org.jamesframework.core.problems.constraints.validations.Validation;
import org.jamesframework.core.problems.objectives.evaluations.Evaluation;
import org.jamesframework.core.search.listeners.SearchListener;
import org.jamesframework.core.subset.SubsetSolution;
import org.jamesframework.core.search.neigh.Move;
import org.jamesframework.core.subset.neigh.SingleDeletionNeighbourhood;
import org.jamesframework.core.subset.neigh.SinglePerturbationNeighbourhood;
import org.jamesframework.core.subset.neigh.moves.AdditionMove;
import org.jamesframework.core.subset.neigh.moves.DeletionMove;
import org.jamesframework.core.subset.neigh.moves.SwapMove;
import org.jamesframework.core.util.SetUtilities;
import org.jamesframework.test.util.DoubleComparatorWithPrecision;
import org.jamesframework.test.stubs.NeverSatisfiedConstraintStub;
import org.jamesframework.test.util.TestConstants;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import static org.junit.Assert.*;
import org.junit.Before;

/**
 * Test abstract neighbourhood search behaviour.
 * 
 * @author <a href="mailto:herman.debeukelaer@ugent.be">Herman De Beukelaer</a>
 */
public class NeighbourhoodSearchTest extends SearchTestTemplate {

    // neighbourhood search stub to work with
    private NeighbourhoodSearch<SubsetSolution> neighSearch;    
    
    /**
     * Print message when starting tests.
     */
    @BeforeClass
    public static void setUpClass() {
        System.out.println("# Testing NeighbourhoodSearch ...");
        SearchTestTemplate.setUpClass();
    }

    /**
     * Print message when tests are complete.
     */
    @AfterClass
    public static void tearDownClass() {
        System.out.println("# Done testing NeighbourhoodSearch!");
    }
    
    /**
     * Create search and components to work with in each test method.
     */
    @Before
    @Override
    public void setUp(){
        super.setUp();
        // for this test, a variable size subset problem is used (+/- 1 allowed)
        problem = new SubsetProblem<>(obj, data, SUBSET_SIZE-1, SUBSET_SIZE+1);
        neighSearch = new NeighbourhoodSearchStub<>(problem);
    }
    
    @After
    public void tearDown(){
        // dispose search
        neighSearch.dispose();
    }

    /**
     * Test of searchStarted method, of class NeighbourhoodSearch.
     */
    @Test
    public void testSearchStarted() {
        
        System.out.println(" - test searchStarted");
        
        // check initial solution: null?
        assertNull(neighSearch.getCurrentSolution());
        // call searchStarted
        neighSearch.searchStarted();
        // check not null anymore
        assertNotNull(neighSearch.getCurrentSolution());
        
    }

    /**
     * Test of addSearchListener method, of class NeighbourhoodSearch.
     */
    @Test
    public void testAddSearchListener() {
        
        System.out.println(" - test addSearchListener");
        
        // add search listener that counts number of current solution updates
        SearchListenerStub<SubsetSolution> l = new SearchListenerStub<>();
        neighSearch.addSearchListener(l);
        
        // initialize (sets random initial solution)
        neighSearch.searchStarted();
        int n = 10;
        for(int i = 0; i < n; i++){
            // accept random move
            neighSearch.acceptMove(neigh.getRandomMove(neighSearch.getCurrentSolution()));
        }
        
        // verify
        assertEquals(n+1, l.getNumCalls());
        
    }

    /**
     * Test of removeSearchListener method, of class NeighbourhoodSearch.
     */
    @Test
    public void testRemoveSearchListener() {
        
        System.out.println(" - test removeSearchListener");
        
        // add search listener
        SearchListenerStub<SubsetSolution> l = new SearchListenerStub<>();
        neighSearch.addSearchListener(l);
        
        // try to remove non added listener
        assertFalse(neighSearch.removeSearchListener(new SearchListenerStub<>()));
        // remove added listener
        assertTrue(neighSearch.removeSearchListener(l));
        
    }

    /**
     * Test of getNumAcceptedMoves method, of class NeighbourhoodSearch.
     */
    @Test
    public void testGetNumAcceptedMoves() {
        
        System.out.println(" - test getNumAcceptedMoves");
        
        // initialize
        neighSearch.searchStarted();
        
        // accept 2 out of 3 moves, 30 total moves --> 20/10
        Move<SubsetSolution> m;
        for(int i=0; i<30; i++){
            m = neigh.getRandomMove(neighSearch.getCurrentSolution());
            if(i%3 == 0){
                neighSearch.rejectMove();
            } else {
                neighSearch.acceptMove(m);
            }
        }
        
        // verify
        assertEquals(20, neighSearch.getNumAcceptedMoves());
        
    }

    /**
     * Test of getNumRejectedMoves method, of class NeighbourhoodSearch.
     */
    @Test
    public void testGetNumRejectedMoves() {
        
        System.out.println(" - test getNumRejectedMoves");
        
        // initialize
        neighSearch.searchStarted();
        
        // accept 2 out of 3 moves, 30 total moves --> 20/10
        Move<SubsetSolution> m;
        for(int i=0; i<30; i++){
            m = neigh.getRandomMove(neighSearch.getCurrentSolution());
            if(i%3 == 0){
                neighSearch.rejectMove();
            } else {
                neighSearch.acceptMove(m);
            }
        }
        
        // verify
        assertEquals(10, neighSearch.getNumRejectedMoves());
        
    }

    /**
     * Test of setCurrentSolution method, of class NeighbourhoodSearch.
     */
    @Test
    public void testSetCurrentSolution() {
        
        System.out.println(" - test setCurrentSolution");
        
        for(int i=0; i<10; i++){
            SubsetSolution sol = problem.createRandomSolution();
            neighSearch.setCurrentSolution(sol);
            // verify
            assertEquals(sol, neighSearch.getCurrentSolution());
        }
        
    }

    /**
     * Test of isImprovement method, of class NeighbourhoodSearch.
     */
    @Test
    public void testIsImprovement() {
        
        System.out.println(" - test isImprovement");
        
        for(int a=0; a<1000; a++){
        
            // set random initial solution of size SUBSET_SIZE
            SubsetSolution initial = new SubsetSolution(data.getIDs());
            initial.selectAll(SetUtilities.getRandomSubset(initial.getUnselectedIDs(), SUBSET_SIZE, RG));
            neighSearch.setCurrentSolution(initial);
            // pick any addition move
            AdditionMove m = new AdditionMove(SetUtilities.getRandomElement(neighSearch.getCurrentSolution().getUnselectedIDs(), RG));
            // verify: addition should increase score
            assertTrue(neighSearch.isImprovement(m));
            // apply move
            neighSearch.acceptMove(m);

            // create corresponding deletion move
            DeletionMove m2 = new DeletionMove(m.getAddedID());
            // verify: deletion yields worse solution
            assertFalse(neighSearch.isImprovement(m2));

            // repeat with minimizing objective
            obj.setMinimizing();
            initial = new SubsetSolution(data.getIDs());
            initial.selectAll(SetUtilities.getRandomSubset(initial.getUnselectedIDs(), SUBSET_SIZE, RG));
            neighSearch.setCurrentSolution(initial);
            // verify: addition is no improvement
            m = new AdditionMove(SetUtilities.getRandomElement(neighSearch.getCurrentSolution().getUnselectedIDs(), RG));
            assertFalse(neighSearch.isImprovement(m));
            // apply addition
            neighSearch.acceptMove(m);
            // verify: deletion now is improvement
            m2 = new DeletionMove(m.getAddedID());
            assertTrue(neighSearch.isImprovement(m2));
            // switch back to maximizing
            obj.setMaximizing();

            // repeat with unsatisfiable constraint and random initial solution
            Constraint<? super SubsetSolution, Object> c = new NeverSatisfiedConstraintStub();
            problem.addMandatoryConstraint(c);
            neighSearch.setCurrentSolution(problem.createRandomSolution());
            // create random addition, deletion and swap move
            m = new AdditionMove(SetUtilities.getRandomElement(neighSearch.getCurrentSolution().getUnselectedIDs(), RG));
            m2 = new DeletionMove(SetUtilities.getRandomElement(neighSearch.getCurrentSolution().getSelectedIDs(), RG));
            SwapMove m3 = new SwapMove(SetUtilities.getRandomElement(neighSearch.getCurrentSolution().getUnselectedIDs(), RG),
                                       SetUtilities.getRandomElement(neighSearch.getCurrentSolution().getSelectedIDs(), RG));
            // verify (no moves are considered improvements because of mandatory constraint)
            assertFalse(neighSearch.isImprovement(m));
            assertFalse(neighSearch.isImprovement(m2));
            assertFalse(neighSearch.isImprovement(m3));
            // remove constraint
            problem.removeMandatoryConstraint(c);
            
            // create random initial solution
            initial = new SubsetSolution(data.getIDs());
            initial.selectAll(SetUtilities.getRandomSubset(initial.getUnselectedIDs(), SUBSET_SIZE, RG));
            // add constraint that invalidates this solution
            c = new InvalidateSelectedSolutionsConstraint(Collections.singleton(Solution.checkedCopy(initial)));
            problem.addMandatoryConstraint(c);
            // set initial solution
            neighSearch.setCurrentSolution(initial);
            // create random addition, deletion and swap move
            m = new AdditionMove(SetUtilities.getRandomElement(neighSearch.getCurrentSolution().getUnselectedIDs(), RG));
            m2 = new DeletionMove(SetUtilities.getRandomElement(neighSearch.getCurrentSolution().getSelectedIDs(), RG));
            m3 = new SwapMove(SetUtilities.getRandomElement(neighSearch.getCurrentSolution().getUnselectedIDs(), RG),
                                       SetUtilities.getRandomElement(neighSearch.getCurrentSolution().getSelectedIDs(), RG));
            // verify that all moves are considered improvements, regardless of their evaluation,
            // as yield valid neighbours of an invalid current solution
            assertTrue(neighSearch.isImprovement(m));
            assertTrue(neighSearch.isImprovement(m2));
            assertTrue(neighSearch.isImprovement(m3));
            // remove constraint
            problem.removeMandatoryConstraint(c);
        
        }
        
    }
    
    private class InvalidateSelectedSolutionsConstraint implements Constraint<SubsetSolution, Object>{

        // invalid solutions
        private final Set<SubsetSolution> invalid;

        public InvalidateSelectedSolutionsConstraint(Set<SubsetSolution> invalid) {
            this.invalid = invalid;
        }
        
        @Override
        public Validation validate(SubsetSolution solution, Object data) {
            return new SimpleValidation(!invalid.contains(solution));
        }
        
    }

    /**
     * Test of getBestMove method, of class NeighbourhoodSearch.
     */
    @Test
    public void testGetBestMove() {
        
        System.out.println(" - test getBestMove");
        
        // set random initial solution
        neighSearch.setCurrentSolution(problem.createRandomSolution());
        
        Set<? extends Move<? super SubsetSolution>> moves  = neigh.getAllMoves(neighSearch.getCurrentSolution());
        Move<? super SubsetSolution> bestMove = neighSearch.getBestMove(moves, true);
        Evaluation prevSolutionEvaluation = neighSearch.getCurrentSolutionEvaluation();
        
        // apply best move until no more improvements found (important: only positive deltas allowed)
        while(bestMove != null){
            // apply move
            neighSearch.acceptMove(bestMove);
            // verify: improvement?
            assertTrue(neighSearch.getCurrentSolutionEvaluation().getValue() > prevSolutionEvaluation.getValue());
            prevSolutionEvaluation = neighSearch.getCurrentSolutionEvaluation();
            // get new moves
            moves = neigh.getAllMoves(neighSearch.getCurrentSolution());
            // get move with largest positive delta
            bestMove = neighSearch.getBestMove(moves, true);
        }
        
        // add unsatisfiable constraint
        Constraint<? super SubsetSolution, Object> c = new NeverSatisfiedConstraintStub();
        problem.addMandatoryConstraint(c);
        // set new random current solution
        neighSearch.setCurrentSolution(problem.createRandomSolution());
        // verify that all moves are rejected
        assertNull(neighSearch.getBestMove(neigh.getAllMoves(neighSearch.getCurrentSolution()), false));
        // remove constraint
        problem.removeMandatoryConstraint(c);
        
        // test with initial invalid solution with valid neighbours
        SubsetSolution sol = new SubsetSolution(data.getIDs());
        // select random items
        sol.selectAll(SetUtilities.getRandomSubset(sol.getUnselectedIDs(), SUBSET_SIZE, RG));
        // add constraint that invalidates the initial solution
        c = new InvalidateSelectedSolutionsConstraint(Collections.singleton(Solution.checkedCopy(sol)));
        problem.addMandatoryConstraint(c);
        // set initial solution
        neighSearch.setCurrentSolution(sol);
        // check deletion moves: all have negative delta but the one yielding
        // the smallest decrease in value will still be identified as the best
        // improving move since the current solution is invalid
        moves = new SingleDeletionNeighbourhood().getAllMoves(sol);
        bestMove = neighSearch.getBestMove(moves, true);
        // verify: move with negative delta selected although improvement was required
        assertNotNull(bestMove);
        assertTrue(neighSearch.computeDelta(neighSearch.evaluateMove(bestMove), neighSearch.getCurrentSolutionEvaluation()) < 0);
        assertTrue(neighSearch.validateMove(bestMove).passed());
        // remove constraint
        assertTrue(problem.removeMandatoryConstraint(c));
        
    }

    /**
     * Test of acceptMove method, of class NeighbourhoodSearch.
     */
    @Test
    public void testAcceptMove() {
        
        System.out.println(" - test acceptMove");
        
        // initialize
        neighSearch.searchStarted();
        
        // copy initial solution
        SubsetSolution copy = Solution.checkedCopy(neighSearch.getCurrentSolution());
        Evaluation copyEval;
        Move<SubsetSolution> m;
        for(int i=0; i<1000; i++){
            // generate random move
            m = neigh.getRandomMove(neighSearch.getCurrentSolution());
            // accept it
            neighSearch.acceptMove(m);
            // apply to copy
            m.apply(copy);
            // evaluate copy
            copyEval = problem.evaluate(copy);
            // verify
            assertEquals(copy, neighSearch.getCurrentSolution());
            assertEquals(copyEval.getValue(), neighSearch.getCurrentSolutionEvaluation().getValue(), TestConstants.DOUBLE_COMPARISON_PRECISION);
            assertTrue(DoubleComparatorWithPrecision.greaterThanOrEqual(
                    neighSearch.getBestSolutionEvaluation().getValue(), copyEval.getValue(), TestConstants.DOUBLE_COMPARISON_PRECISION));
        }
        
    }
    
    /**
     * Test of acceptMove method, of class NeighbourhoodSearch, with minimizing objective.
     */
    @Test
    public void testAcceptMoveMinimizing() {
        
        System.out.println(" - test acceptMove with minimizing objective");
        
        // set minimizing
        obj.setMinimizing();
        
        // initialize
        neighSearch.searchStarted();
        
        // copy initial solution
        SubsetSolution copy = Solution.checkedCopy(neighSearch.getCurrentSolution());
        Evaluation copyEval;
        Move<SubsetSolution> m;
        for(int i=0; i<100; i++){
            // generate random move
            m = neigh.getRandomMove(neighSearch.getCurrentSolution());
            // accept it
            neighSearch.acceptMove(m);
            // apply to copy
            m.apply(copy);
            // evaluate
            copyEval = problem.evaluate(copy);
            // verify
            assertEquals(copy, neighSearch.getCurrentSolution());
            assertEquals(copyEval.getValue(), neighSearch.getCurrentSolutionEvaluation().getValue(), TestConstants.DOUBLE_COMPARISON_PRECISION);
            assertTrue(DoubleComparatorWithPrecision.smallerThanOrEqual(
                    neighSearch.getBestSolutionEvaluation().getValue(), copyEval.getValue(), TestConstants.DOUBLE_COMPARISON_PRECISION));
        }
        
    }

    /**
     * Neighbourhood search stub for testing.
     * 
     * @param <SolutionType> solution type
     */
    private class NeighbourhoodSearchStub<SolutionType extends Solution> extends NeighbourhoodSearch<SolutionType> {

        /**
         * Create stub.
         * 
         * @param p problem
         */
        public NeighbourhoodSearchStub(Problem<SolutionType> p) {
            super(p);
        }

        /**
         * Empty search step implementation.
         */
        @Override
        protected void searchStep() {
            // do nothing
        }
    }
    
    /**
     * Search listener stub for testing.
     * 
     * @param <SolutionType> solution type
     */
    private class SearchListenerStub<SolutionType extends Solution> implements SearchListener<SolutionType>{
        
        // number of calls of callback
        private int numCalls = 0;
        
        /**
         * Count number of times fired.
         */
        @Override
        public void newCurrentSolution(LocalSearch<? extends SolutionType> search,
                                       SolutionType newCurrentSolution,
                                       Evaluation newCurrentSolutionEvaluation,
                                       Validation newCurrentSolutionValidation) {
            numCalls++;
        }
        
        /**
         * Get number of calls.
         * 
         * @return  number of calls
         */
        public int getNumCalls(){
            return numCalls;
        }
        
    }

}