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

package org.jamesframework.core.search.neigh.subset;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Random;
import java.util.Set;
import java.util.concurrent.ThreadLocalRandom;
import org.jamesframework.core.exceptions.JamesRuntimeException;
import org.jamesframework.core.problems.solutions.SubsetSolution;
import org.jamesframework.core.search.neigh.Move;
import org.jamesframework.core.search.neigh.Neighbourhood;
import org.jamesframework.core.util.SetUtilities;

/**
 * <p>
 * A subset neighbourhood that generates swap moves (see {@link SwapMove}), addition moves (see {@link AdditionMove}) and deletion
 * moves (see {@link DeletionMove}). Applying an addition or deletion move to a given subset solution will increase, respectively
 * decrease, the number of selected items. Therefore, this neighbourhood is also suited for variable size subset selection problems,
 * in contrast to {@link SingleSwapNeighbourhood}, which can only handle fixed size subset problems.
 * </p>
 * <p>
 * A single perturbation neighbourhood respects the minimum and maximum subset size specified at construction. When the given
 * subset solution has minimal size, no deletion moves will be generated. Similarly, when the current solution has maximum size, no
 * addition moves will be generated. Swap moves can always be generated for solutions within the desired size bounds, except when the
 * subset solution is empty, i.e. no IDs are selected, or when all IDs are selected. In the former case, only addition moves are
 * considered, in the latter case, the only considered moves are deletions.
 * </p>
 * 
 * @author Herman De Beukelaer <herman.debeukelaer@ugent.be>
 */
public class SinglePerturbationNeighbourhood implements Neighbourhood<SubsetSolution> {

    // move type enum used for selecting random type
    private enum MoveType {
        ADDITION, DELETION, SWAP;
    }
    
    // minimum and maximum subset size
    private final int minSubsetSize;
    private final int maxSubsetSize;
    
    /**
     * Creates a new single perturbation neighbourhood with given minimum and maximum subset size.
     * Only moves that result in a valid solution size after application to the current solution
     * will ever be generated. Positive values are required for the minimum and maximum size,
     * with minimum ≤ maximum; else, an exception is thrown.
     * 
     * @throws IllegalArgumentException if minimum and maximum size are not both positive, or minimum > maximum
     * @param minSubsetSize minimum subset size
     * @param maxSubsetSize maximum subset size
     */
    public SinglePerturbationNeighbourhood(int minSubsetSize, int maxSubsetSize){
        // validate sizes
        if(minSubsetSize < 0){
            throw new IllegalArgumentException("Error while creating single perturbation neighbourhood: minimum subset size should be non-negative.");
        }
        if(maxSubsetSize < 0){
            throw new IllegalArgumentException("Error while creating single perturbation neighbourhood: maximum subset size should be non-negative.");
        }
        if(minSubsetSize > maxSubsetSize){
            throw new IllegalArgumentException("Error while creating single perturbation neighbourhood: "
                                                + "minimum subset size should be smaller than or equal to maximum subset size.");
        }
        this.minSubsetSize = minSubsetSize;
        this.maxSubsetSize = maxSubsetSize;
    }
    
    /**
     * Generates a random swap, deletion or addition move that transforms the given subset solution into
     * a neighbour within the minimum and maximum allowed subset size. If no valid move can be generated,
     * <code>null</code> is returned.
     * 
     * @param solution solution for which a random move is generated
     * @return random move, <code>null</code> if no valid move can be generated
     */
    @Override
    public Move<SubsetSolution> getRandomMove(SubsetSolution solution) {
        // check which moves can be generated
        List<MoveType> validMoveTypes = new ArrayList<>();
        if(genAdditionMovesForSolution(solution)){
            // addition is valid
            validMoveTypes.add(MoveType.ADDITION);
        }
        if(genDeletionMovesForSolution(solution)){
            // deletion is valid
            validMoveTypes.add(MoveType.DELETION);
        }
        if(genSwapMovesForSolution(solution)){
            // swap is valid
            validMoveTypes.add(MoveType.SWAP);
        }
        // in case of no valid moves: return null
        if(validMoveTypes.isEmpty()){
            return null;
        }
        // randomly pick move type (using thread local random for concurrent performance)
        Random rg = ThreadLocalRandom.current();
        MoveType type = validMoveTypes.get(rg.nextInt(validMoveTypes.size()));
        // generate random move of chosen type
        switch(type){
            case ADDITION : return new AdditionMove(SetUtilities.getRandomElement(solution.getUnselectedIDs(), rg));
            case DELETION : return new DeletionMove(SetUtilities.getRandomElement(solution.getSelectedIDs(), rg));
            case SWAP     : return new SwapMove(
                                                SetUtilities.getRandomElement(solution.getUnselectedIDs(), rg),
                                                SetUtilities.getRandomElement(solution.getSelectedIDs(), rg)
                                            );
            default : throw new JamesRuntimeException("This should never happen. If this exception is thrown, "
                                                + "there is a serious bug in SinglePerturbationNeighbourhood.");
        }
    }

    /**
     * Generate all valid swap, deletion and addition moves that transform the given subset solution into
     * a neighbour within the minimum and maximum allowed subset size. The returned set may be empty,
     * if no valid moves exist.
     * 
     * @param solution solution for which a set of all valid moves is generated
     * @return set of all valid swap, deletion and addition moves
     */
    @Override
    public Set<Move<SubsetSolution>> getAllMoves(SubsetSolution solution) {
        // create empty set
        Set<Move<SubsetSolution>> moves = new HashSet<>();
        // generate all addition moves, if valid
        if(genAdditionMovesForSolution(solution)){
            // go through currently unselected IDs
            for(int ID : solution.getUnselectedIDs()){
                // create addition move that adds this ID
                moves.add(new AdditionMove(ID));
            }
        }
        // generate all deletion moves, if valid
        if(genDeletionMovesForSolution(solution)){
            // go through currently selected IDs
            for(int ID : solution.getSelectedIDs()){
                // create deletion move that deletes this ID
                moves.add(new DeletionMove(ID));
            }
        }
        // generate all swap moves, if valid
        if(genSwapMovesForSolution(solution)){
            // go through candidates for addition
            for(int add : solution.getUnselectedIDs()){
                // go through candidates for deletion
                for(int del : solution.getSelectedIDs()){
                    // create corresponding swap move
                    moves.add(new SwapMove(add, del));
                }
            }
        }
        // return generated moves
        return moves;
    }
    
    /**
     * Verify whether addition moves should be generated for the given solution.
     * 
     * @param solution solution for which moves are generated
     * @return true if addition moves are valid for this solution
     */
    private boolean genAdditionMovesForSolution(SubsetSolution solution){
        return solution.getNumUnselectedIDs() > 0 && validSubsetSize(solution.getNumSelectedIDs()+1);
    }
    
    /**
     * Verify whether deletion moves should be generated for the given solution.
     * 
     * @param solution solution for which moves are generated
     * @return true if deletion moves are valid for this solution
     */
    private boolean genDeletionMovesForSolution(SubsetSolution solution){
        return solution.getNumSelectedIDs() > 0 && validSubsetSize(solution.getNumSelectedIDs()-1);
    }
    
    /**
     * Verify whether swap moves should be generated for the given solution.
     * 
     * @param solution solution for which moves are generated
     * @return true if swap moves are valid for this solution
     */
    private boolean genSwapMovesForSolution(SubsetSolution solution){
        return solution.getNumSelectedIDs() > 0 && solution.getNumUnselectedIDs() > 0
                && validSubsetSize(solution.getNumSelectedIDs());
    }
    
    /**
     * Verifies whether the given subset size is valid, taking into account
     * the minimum and maximum size specified at construction.
     * 
     * @param size size to verify
     * @return true if size falls within bounds
     */
    private boolean validSubsetSize(int size){
        return size >= minSubsetSize && size <= maxSubsetSize;
    }

    /**
     * Get the minimum subset size specified at construction.
     * 
     * @return minimum subset size
     */
    public int getMinSubsetSize() {
        return minSubsetSize;
    }

    /**
     * Get the maximum subset size specified at construction.
     * 
     * @return maximum subset size
     */
    public int getMaxSubsetSize() {
        return maxSubsetSize;
    }

}
