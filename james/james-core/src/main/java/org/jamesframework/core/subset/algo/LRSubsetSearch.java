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

package org.jamesframework.core.subset.algo;

import org.jamesframework.core.subset.SubsetProblem;
import org.jamesframework.core.problems.Solution;
import org.jamesframework.core.subset.validations.SubsetValidation;
import org.jamesframework.core.problems.objectives.Evaluation;
import org.jamesframework.core.subset.SubsetSolution;
import org.jamesframework.core.search.LocalSearch;
import org.jamesframework.core.search.neigh.Move;
import org.jamesframework.core.subset.neigh.SingleAdditionNeighbourhood;
import org.jamesframework.core.subset.neigh.SingleDeletionNeighbourhood;
import org.jamesframework.core.subset.neigh.SubsetNeighbourhood;

/**
 * LR subset search is a greedy local search heuristic for subset selection. Its specific execution depends on the
 * value of two parameters \(L \ge 0\) and \(R \ge 0\), \(L \ne R\). If \(L \gt R\), the search first performs the
 * best \(L\) additions followed by the best \(R\) removals of a single item, in every step. Else, if \(R \gt L\),
 * every step consists of performing the \(R\) best removals followed by the \(L\) best additions of a single item.
 * After every step, the subset size has changed with a value of \(\Delta = |L-R|\).
 * <p>
 * The search only considers additions and deletions that yield valid solutions without checking the current subset
 * size as this size is actively brought into the valid range during search. By default, in case of an increasing
 * subset size, the search starts with an empty subset, and in case of a decreasing subset size all items are initially
 * selected. Alternatively, a custom initial solution can be set by calling {@link #setCurrentSolution(Solution)}
 * before starting the search.
 * <p>
 * The search terminates as soon as the entire valid subset size range has been explored.
 * 
 * @author <a href="mailto:herman.debeukelaer@ugent.be">Herman De Beukelaer</a>
 */
public class LRSubsetSearch extends LocalSearch<SubsetSolution> {
    
    // number of additions per search step
    private final int l;
    // number of delections per search step
    private final int r;
    // single addition/deletion neighbourhoods
    private final SubsetNeighbourhood singleAddNeigh;
    private final SubsetNeighbourhood singleDelNeigh;
    
    /**
     * Create an LR subset search, given the subset problem to solve and a value for \(L \ge 0\) and \(R \ge 0\),
     * \(L \ne R\). Note that <code>problem</code> can not be <code>null</code> and that both <code>l</code> and
     * <code>r</code> should be positive and distinct. The search name is set to the default name "LRSubsetSearch".
     * 
     * @param problem subset problem to solve
     * @param l number of additions per search step
     * @param r number of deletions per search step
     * @throws NullPointerException if <code>problem</code> is <code>null</code>
     * @throws IllegalArgumentException if <code>l</code> or <code>r</code> are negative or equal
     */
    public LRSubsetSearch(SubsetProblem<?> problem, int l, int r){
        this(null, problem, l, r);
    }
    
    /**
     * Create an LR subset search, given the subset problem to solve, a value for \(L \ge 0\) and \(R \ge 0\),
     * \(L \ne R\), and a custom search name. Note that <code>problem</code> can not be <code>null</code> and
     * that both <code>l</code> and <code>r</code> should be positive and distinct. The search name can be
     * <code>null</code> in which case it is set to the default name "LRSubsetSearch".
     * 
     * @param name custom search name
     * @param problem subset problem to solve
     * @param l number of additions per search step
     * @param r number of deletions per search step
     * @throws NullPointerException if <code>problem</code> is <code>null</code>
     * @throws IllegalArgumentException if <code>l</code> or <code>r</code> are negative or equal
     */
    public LRSubsetSearch(String name, SubsetProblem<?> problem, int l, int r){
        super(name != null ? name : "LRSubsetSearch", problem);
        // check l and r
        if(l < 0){
            throw new IllegalArgumentException("Can not create LRSubsetSearch: l < 0.");
        }
        if(r < 0){
            throw new IllegalArgumentException("Can not create LRSubsetSearch: r < 0.");
        }
        if(l == r){
            throw new IllegalArgumentException("Can not create LRSubsetSearch: l = r.");
        }
        // store l and r
        this.l = l;
        this.r = r;
        // create single addition/deletion neighbourhoods
        this.singleAddNeigh = new SingleAdditionNeighbourhood();
        this.singleDelNeigh = new SingleDeletionNeighbourhood();
    }
    
    /**
     * Get \(L\): the number of additions performed in every search step.
     * 
     * @return number of additions per step
     */
    public int getL(){
        return l;
    }
    
    /**
     * Get \(R\): the number of deletions performed in every search step.
     * 
     * @return number of deletions per step
     */
    public int getR(){
        return r;
    }
    
    /**
     * Indicates whether the subset size is increasing during search (\(L \gt R\)).
     * 
     * @return <code>true</code> if the subset size is increasing
     */
    private boolean isIncreasing(){
        return l > r;
    }
    
    /**
     * Get the change in subset size after each search step: \(\Delta = |L-R|\).
     * 
     * @return change in subset size after each step
     */
    private int getDelta(){
        return Math.abs(l-r);
    }
    
    /**
     * Returns the subset problem that is being solved.
     * 
     * @return subset problem being solved
     */
    @Override
    public SubsetProblem<?> getProblem(){
        return (SubsetProblem<?>) super.getProblem();
    }
    
    /**
     * When the search is started for the first time, and no custom initial solution has been set,
     * an empty or full subset solution is created depending on whether \(L \gt R\) or \(R \gt L\),
     * respectively, to be repeatedly modified during search.
     */
    @Override
    protected void searchStarted(){
        // solution not yet set?
        // NOTE: important to set solution before calling super,
        //       else super sets a random initial solution
        if(getCurrentSolution() == null){
            if(isIncreasing()){
                // increasing: start with empty solution
                SubsetSolution initial = getProblem().createEmptySubsetSolution();
                updateCurrentAndBestSolution(initial);
            } else {
                // decreasing: start with full set
                SubsetSolution initial = getProblem().createEmptySubsetSolution();
                initial.selectAll();
                updateCurrentAndBestSolution(initial);
            }
        }
        // call super
        super.searchStarted();
    }
    
    /**
     * Indicates whether the entire valid subset size range has been explored. When the subset size is increasing
     * (\(L \gt R\)) this happens when the current subset size is larger than or equal to the maximum size. For
     * decreasing size, this happens when the current size is smaller than or equal to the minimum subset size.
     * 
     * @return <code>true</code> if the entire valid subset size range has been explored
     */
    private boolean validSubsetSizeRangeExplored(){
        if(isIncreasing()){
            return getCurrentSolution().getNumSelectedIDs() >= getProblem().getMaxSubsetSize();
        } else {
            return getCurrentSolution().getNumSelectedIDs() <= getProblem().getMinSubsetSize();
        }
    }
    
    /**
     * In every search step, the \(L\) best additions and \(R\) best deletions of a single item are performed.
     * If \(L \gt R\), additions are performed first, else, deletions are performed first.
     */
    @Override
    protected void searchStep() {
        // check: valid subset size range completely explored?
        if(validSubsetSizeRangeExplored()){
            // we are done
            stop();
        } else {
            if(isIncreasing()){
                // perform L additions
                int numAdded = greedyMoves(l, singleAddNeigh);
                // perform number of deletions (<= R) that yields a delta of |L-R|,
                // taking into account the actual number of added items
                int numDelete = numAdded - getDelta();
                greedyMoves(numDelete, singleDelNeigh);
            } else {
                // perform R deletions
                int numDeleted = greedyMoves(r, singleDelNeigh);
                // perform number of additions (<= L) that yields a delta of |L-R|,
                // taking into account the actual number of deleted items
                int numAdd = numDeleted - getDelta();
                greedyMoves(numAdd, singleAddNeigh);
            }
        }
    }
    
    /**
     * Greedily apply a series of n moves generated by the given neighbourhood, where the move yielding the most
     * improvement (or smallest decline) is iteratively selected. Returns the actual number of performed moves,
     * which is always lower than or equal to the requested number of moves. It may be strictly lower in case
     * no more moves can be applied at some point.
     * 
     * @param n number of requested moves
     * @return actual number of applied moves, lower than or equal to requested number of moves
     */
    private int greedyMoves(int n, SubsetNeighbourhood neigh){
        int applied = 0;
        boolean cont = true;
        while(applied < n && cont){
            // go through all moves to find the best one
            Move<? super SubsetSolution> bestMove = null;
            double bestDelta = -Double.MAX_VALUE, delta;
            Evaluation newEvaluation, bestEvaluation = null;
            SubsetValidation newValidation, bestValidation = null;
            for(Move<? super SubsetSolution> move : neigh.getAllMoves(getCurrentSolution())){
                // validate move (IMPORTANT: ignore current subset size)
                newValidation = getProblem().validate(move, getCurrentSolution(), getCurrentSolutionValidation());
                if(newValidation.passed(false)){
                    // evaluate move
                    newEvaluation = getProblem().evaluate(move, getCurrentSolution(), getCurrentSolutionEvaluation());
                    // compute delta
                    delta = computeDelta(newEvaluation, getCurrentSolutionEvaluation());
                    // new best move?
                    if(delta > bestDelta){
                        bestDelta = delta;
                        bestMove = move;
                        bestEvaluation = newEvaluation;
                        bestValidation = newValidation;
                    }
                }
            }
            // apply best move, if any
            if(bestMove != null){
                // apply move
                bestMove.apply(getCurrentSolution());
                // update current and best solution (NOTe: best solution will only be updated
                // if it is fully valid, also taking into account the current subset size)
                updateCurrentAndBestSolution(getCurrentSolution(), bestEvaluation, bestValidation);
                // increase counter
                applied++;
            } else {
                // no valid move found, stop
                cont = false;
            }
        }
        // return actual number of applied moves
        return applied;
    }

}
