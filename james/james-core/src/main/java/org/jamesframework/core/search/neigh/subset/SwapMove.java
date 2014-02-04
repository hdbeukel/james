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

import java.util.Collections;
import java.util.Set;
import org.jamesframework.core.exceptions.SolutionModificationException;
import org.jamesframework.core.problems.solutions.SubsetSolution;

/**
 * Simple subset move that removes a single ID from the current selection
 * and replaces it with a new ID which was previously not selected.
 * 
 * @author Herman De Beukelaer <herman.debeukelaer@ugent.be>
 */
public class SwapMove implements SubsetMove{

    // added and deleted ID
    private final int add, delete;
    
    /**
     * Creates a new swap move with specified IDs to add to and remove from the current selection
     * when being applied to a given subset solution. Both IDs can not be equal.
     * 
     * @throws IllegalArgumentException if <code>add == delete</code>
     * @param add ID to add
     * @param delete ID to delete
     */
    public SwapMove(int add, int delete){
        // check not equal
        if(add == delete){
            throw new IllegalArgumentException("Error while creating swap move: added and deleted ID can not be equal.");
        }
        // store IDs
        this.add = add;
        this.delete = delete;
    }
    
    /**
     * Apply this swap move to a given subset solution. The move can only be applied to a solution
     * for which the added ID is currently <b>not selected</b> and the deleted ID is currently
     * <b>selected</b>. This guarantees that calling {@link #undo(SubsetSolution)} will effectively
     * restore the solution state to that before application of the move.
     * 
     * @throws SolutionModificationException if the added ID is already selected, the deleted ID is already unselected,
     *                                       or any of both IDs does not correspond to an underlying entity
     * @param solution solution to which to move will be applied
     */
    @Override
    public void apply(SubsetSolution solution) {
        // add new ID to selection
        if(solution.select(add)){
            // succesfully added new ID, now try to remove deleted ID
            if(!solution.deselect(delete)){
                // deselecting ID failed (currently not selected)
                throw new SolutionModificationException("Error while applying swap move to subset solution: deleted ID currently not selected.", solution);
            }
        } else {
            // selecting new ID failed (already selected)
            throw new SolutionModificationException("Error while applying swap move to subset solution: added ID already selected.", solution);
        }
    }

    /**
     * Undo this swap move after it has been successfully applied to the given subset solution.
     * It is assumed that the solution has not been modified since application of the move,
     * else the behaviour of this method is undefined.
     * 
     * @param solution 
     */
    @Override
    public void undo(SubsetSolution solution) {
        // readd deleted ID
        solution.select(delete);
        // remove newly added ID
        solution.deselect(add);
    }

    /**
     * Returns a singleton containing the only added ID.
     * 
     * @return singleton containing added ID
     */
    @Override
    public Set<Integer> getAddedIDs() {
        return Collections.singleton(add);
    }
    
    /**
     * Returns the added ID.
     * 
     * @return added ID
     */
    public int getAddedID() {
        return add;
    }

    /**
     * Returns a singleton containing the only deleted ID.
     * 
     * @return singleton containing deleted ID
     */
    @Override
    public Set<Integer> getDeletedIDs() {
        return Collections.singleton(delete);
    }
    
    /**
     * Returns the deleted ID.
     * 
     * @return deleted ID
     */
    public int getDeletedID(){
        return delete;
    }

    /**
     * Always returns 1.
     * 
     * @return 1
     */
    @Override
    public int getNumAdded() {
        return 1;
    }

    /**
     * Always return 1.
     * 
     * @return 1
     */
    @Override
    public int getNumDeleted() {
        return 1;
    }

}
