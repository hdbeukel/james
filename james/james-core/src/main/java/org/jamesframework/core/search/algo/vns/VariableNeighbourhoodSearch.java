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

package org.jamesframework.core.search.algo.vns;

import java.util.List;
import org.jamesframework.core.exceptions.JamesRuntimeException;
import org.jamesframework.core.problems.Problem;
import org.jamesframework.core.problems.solutions.Solution;
import org.jamesframework.core.search.LocalSearch;
import org.jamesframework.core.search.MultiNeighbourhoodSearch;
import org.jamesframework.core.search.Search;
import org.jamesframework.core.search.SearchStatus;
import org.jamesframework.core.search.neigh.Move;
import org.jamesframework.core.search.neigh.Neighbourhood;
import org.jamesframework.core.search.stopcriteria.StopCriterion;
import org.jamesframework.core.util.LocalSearchFactory;

/**
 * <p>
 * Variable neighbourhood search (VNS) algorithm. This search applies a series of neighbourhoods for random 
 * shaking of the current solution in combination with an arbitrary other local search algorithm L to modify
 * the solution obtained after shaking, in an attempt to find a global improvement. More precisely, given a
 * series of \(n_s\) shaking neighbourhoods \(N_i, i=0, ..., n_s-1\) and an arbitrary other local search
 * algorithm \(L\), each step of VNS consists of:
 * </p>
 * <ol>
 *  <li>
 *   <b>Shaking</b>: sample a random neighbour \(x'\) of the current solution \(x\), using shaking
 *   neighbourhood \(N_s\) (initially, \(s = 0\)).
 *  </li>
 *  <li>
 *   <b>Local search</b>: apply algorithm \(L\) with \(x'\) as its initial solution, until it terminates.
 *   The best solution found by \(L\) is referred to as \(x''\).
 *  </li>
 *  <li>
 *   <b>Acceptance</b>: if \(x''\) is an improvement over \(x\) (i.e. \(x''\) is not rejected and has a better
 *   evaluation than \(x\)), it is accepted as the new current solution and \(s\) is reset to 0. Else, \(x\)
 *   remains the current solution and \(s\) is increased by one, so that the next shaking neighbourhood will be
 *   applied in the next step. If \(s\) becomes equal to the number of shaking neighbourhoods \(n_s\), it is
 *   cyclically reset to 0. Therefore, VNS never terminates internally but continues until a stop criterion is met.
 *  </li>
 * </ol>
 * <p>
 * By default, VNS applies variable neighbourhood descent (VND) as the local search algorithm L used to modify
 * \(x'\). The list of neighbourhoods of VND is not required to be related to the shaking neighbourhoods of VNS. As VND
 * is computationally intensive (it generates all neighbours in the current neighbourhood in every step), smaller
 * neighbourhoods are often applied for VND compared to the shaking neighbourhoods of VNS. The latter may grow larger
 * without computational concerns as they are only used for random sampling.
 * </p>
 * <p>
 * It is possible to use any other local search algorithm \(L\), by specifying a custom neighbourhood search factory.
 * In every step, a fresh instance of \(L\) is created to be applied to solution \(x'\); afterwards, this instance is
 * disposed. Usually, an algorithm is applied that terminates internally at some point in time (VND, steepest descent,
 * ...). Alternatively, a never ending algorithm may be applied in combination with some stop criterion (e.g. random
 * descent with a maximum runtime).
 * </p>
 * <p>
 * The combination of shaking and local search is considered to be one move in the VNS algorithm, which is only accepted
 * if it yields a global improvement. Therefore, {@link #getNumAcceptedMoves()} and {@link #getNumRejectedMoves()}
 * reflect the number of times that such complex move has been accepted and rejected, respectively.
 * </p>
 * <p>
 * Note that {@link #getNeighbourhoods()} returns the list of shaking neighbourhoods applied by VNS, which are, in
 * general, unrelated to the neighbourhoods applied by the default VND local search algorithm. All internals of the
 * applied local search algorithm \(L\) are shielded inside this algorithm, i.e. the algorithm is simply applied as
 * a black box to transform a given solution \(x'\) into another solution \(x''\).
 * </p>
 * 
 * @param <SolutionType> solution type of the problems that may be solved using this search, required to extend {@link Solution}
 * @author <a href="mailto:herman.debeukelaer@ugent.be">Herman De Beukelaer</a>
 */
public class VariableNeighbourhoodSearch<SolutionType extends Solution> extends MultiNeighbourhoodSearch<SolutionType> {

    // index of current shaking neighbourhood
    private int s;
    
    // local search factory used to generate a new instance of the applied local search in every step
    private LocalSearchFactory<SolutionType> localSearchFactory;
    
    /**
     * Creates a new variable neighbourhood search, specifying the problem to solve, the neighbourhoods used for shaking
     * and the neighbourhoods used by the default VND local search algorithm. None of the arguments can be <code>null</code>
     * and the lists of neighbourhoods can not be empty and can not contain any <code>null</code> elements. The search name
     * defaults to "VariableNeighbourhoodSearch".
     * 
     * @throws NullPointerException if <code>problem</code>, <code>shakingNeighs</code> or <code>vndNeighs</code> are <code>null</code>,
     *                              or if <code>shakingNeighs</code> or <code>vndNeighs</code> contain any <code>null</code> elements
     * @throws IllegalArgumentException if <code>shakingNeighs</code> or <code>vndNeighs</code> are empty
     * @param problem problem to solve
     * @param shakingNeighs list of shaking neighbourhoods used by VNS
     * @param vndNeighs list of neighbourhoods applied by VND, which is used as the default local search algorithm
     */
    public VariableNeighbourhoodSearch(Problem<SolutionType> problem, List<? extends Neighbourhood<? super SolutionType>> shakingNeighs,
                                                                      List<? extends Neighbourhood<? super SolutionType>> vndNeighs){
        this(problem, shakingNeighs, new VNDFactory<>(vndNeighs));
    }
    
    /**
     * Creates a new variable neighbourhood search, specifying the problem to solve, the neighbourhoods used for
     * shaking in VNS, and a factory to create instances of a custom local search algorithm \(L\) to be applied to
     * modify solutions obtained after shaking. None of the arguments can be <code>null</code>, and the list of shaking
     * neighbourhoods can not be empty and can not contain any <code>null</code> elements. The search name defaults to
     * "VariableNeighbourhoodSearch".
     * 
     * @throws NullPointerException if <code>problem</code>, <code>shakingNeighs</code> or
     *                              <code>localSearchFactory</code> are <code>null</code>, or if
     *                              <code>shakingNeighs</code> contains any <code>null</code> elements
     * @throws IllegalArgumentException if <code>shakingNeighs</code> is empty
     * @param problem problem to solve
     * @param shakingNeighs list of shaking neighbourhoods used by VNS
     * @param localSearchFactory factory to create instances of the local search algorithm \(L\)
     */
    public VariableNeighbourhoodSearch(Problem<SolutionType> problem, List<? extends Neighbourhood<? super SolutionType>> shakingNeighs,
                                                LocalSearchFactory<SolutionType> localSearchFactory){
        this(null, problem, shakingNeighs, localSearchFactory);
    }
    
    /**
     * Creates a new variable neighbourhood search, specifying the problem to solve, the neighbourhoods used for
     * shaking in VNS, a factory to create instances of a custom local search algorithm \(L\) to be applied to
     * modify solutions obtained after shaking, and a custom search name. Only the search name can be <code>null</code>
     * in which case the default name "VariableNeighbourhoodSearch" is assigned. The list of shaking neighbourhoods
     * can not be empty and can not contain any <code>null</code> elements.
     * 
     * @throws NullPointerException if <code>problem</code>, <code>shakingNeighs</code> or
     *                              <code>localSearchFactory</code> are <code>null</code>, or if
     *                              <code>shakingNeighs</code> contains any <code>null</code> elements
     * @throws IllegalArgumentException if <code>shakingNeighs</code> is empty
     * @param name custom search name
     * @param problem problem to solve
     * @param shakingNeighs list of shaking neighbourhoods used by VNS
     * @param localSearchFactory factory to create instances of the local search algorithm \(L\)
     */
    public VariableNeighbourhoodSearch(String name, Problem<SolutionType> problem, List<? extends Neighbourhood<? super SolutionType>> shakingNeighs,
                                                LocalSearchFactory<SolutionType> localSearchFactory){
        super(name != null ? name : "VariableNeighbourhoodSearch", problem, shakingNeighs);
        // check and store factory
        if(localSearchFactory == null){
            throw new NullPointerException("Can not create variable neighbourhood search: local search factory can not be null.");
        }
        this.localSearchFactory = localSearchFactory;
        // start with 0th shaking neighbourhood
        s = 0;
    }
    
    /**
     * Set a custom factory to create instances of the local search algorithm to be applied to modify solutions
     * obtained after shaking. The given factory can not be <code>null</code>.
     * 
     * @param localSearchFactory custom local search factory
     * @throws NullPointerException if <code>localSearchFactory</code> is <code>null</code>
     */
    public void setLocalSearchFactory(LocalSearchFactory<SolutionType> localSearchFactory){
        // check not null
        if(localSearchFactory == null){
            throw new NullPointerException("Cannot set local search factory in VNS: received null.");
        }
        // go ahead
        this.localSearchFactory = localSearchFactory;
    }
    
    /**
     * Get the factory used to create instances of the local search algorithm which is applied to modify
     * solutions obtained after shaking. By default, this factory creates variable neighbourhood descent
     * (VND) searches, but a custom factory may have been set.
     * 
     * @return local search factory
     */
    public LocalSearchFactory<SolutionType> getLocalSearchFactory(){
        return localSearchFactory;
    }

    /**
     * Performs a step of VNS. One step consists of:
     * <ol>
     *  <li>Shaking using the current shaking neighbourhood</li>
     *  <li>Modification using a new instance of the local search algorithm</li>
     *  <li>
     *   Acceptance of modified solution if it is a global improvement. In such case, the search continues
     *   with the first shaking neighbourhood; else, the next shaking neighbourhood will be used (cyclically).
     *  </li>
     * </ol>
     * 
     * @throws JamesRuntimeException if depending on malfunctioning components (problem, neighbourhood, ...)
     */
    @Override
    protected void searchStep() {
        
        // cyclically reset s to zero if no more shaking neighbourhoods are available
        if(s >= getNeighbourhoods().size()){
            s = 0;
        }
        
        // create copy of current solution to shake and modify by applying local search procedure
        
        SolutionType shakedSolution = Solution.checkedCopy(getCurrentSolution());
        
        // 1) SHAKING
        
        // get random move from current shaking neighbourhood
        Neighbourhood<? super SolutionType> shakingNeigh = getNeighbourhoods().get(s);
        Move<? super SolutionType> shakeMove = shakingNeigh.getRandomMove(shakedSolution);
        // assert that a shaking move is obtained
        if(shakeMove != null){
            
            // shake
            shakeMove.apply(shakedSolution);

            // 2) LOCAL SEARCH

            // create instance of local search algorithm
            LocalSearch<SolutionType> localSearch = localSearchFactory.create(getProblem());
            // set initial solution to be modified
            localSearch.setCurrentSolution(shakedSolution);
            // interrupt local search algorithm when main VNS search wants to terminate
            localSearch.addStopCriterion(new StopCriterion() {
                @Override
                public boolean searchShouldStop(Search<?> search) {
                    return VariableNeighbourhoodSearch.this.getStatus() == SearchStatus.TERMINATING;
                }
            });
            // run local search
            localSearch.start();
            // dispose local search when completed
            localSearch.dispose();

            // 3) ACCEPTANCE

            SolutionType modifiedSolution = localSearch.getBestSolution();
            double modifiedSolutionEval = localSearch.getBestSolutionEvaluation();
            // check improvement (if best solution is not null, it is guaranteed
            // to be a valid solution, so validation is not required here)
            if(modifiedSolution != null
                    && computeDelta(modifiedSolutionEval, getCurrentSolutionEvaluation()) > 0){
                // improvement: increase number of accepted moves
                incNumAcceptedMoves(1);
                // update current and best solution (skip validation, best solution
                // reported by local search is already guaranteed to be valid)
                updateCurrentAndBestSolution(modifiedSolution, modifiedSolutionEval, true);
                // reset shaking neighbourhood
                s = 0;
            } else {
                // no improvement: stick with current solution, move to next shaking neighbourhood
                incNumRejectedMoves(1);
                s++;
            }
            
        } else {
            // s-th neighbourhood did not produce any random shaking move, try again with next neighbourhood in next step
            s++;
        }
                
    }
    
}
