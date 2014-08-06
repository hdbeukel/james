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

package org.jamesframework.core.subset.neigh;

import java.util.Collections;
import java.util.Set;
import org.jamesframework.core.exceptions.SolutionModificationException;
import org.jamesframework.core.subset.SubsetSolution;

/**
 * Simple subset move that adds a single ID to the current selection.
 * 
 * @author <a href="mailto:herman.debeukelaer@ugent.be">Herman De Beukelaer</a>
 */
public class AdditionMove implements SubsetMove{

    // added ID
    private final int add;
    
    /**
     * Create a new addition move that will add the specified ID to the current
     * selection when being applied to a given subset solution.
     * 
     * @param add ID to add to selection
     */
    public AdditionMove(int add){
        this.add = add;
    }
    
    /**
     * Returns an unmodifiable singleton containing the only added ID.
     * 
     * @return unmodifiable singleton containing added ID
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
     * Returns an unmodifiable empty set, as no IDs are deleted by this move.
     * 
     * @return empty set
     */
    @Override
    public Set<Integer> getDeletedIDs() {
        return Collections.emptySet();
    }

    /**
     * Always return 1, as a single ID is added.
     * 
     * @return 1
     */
    @Override
    public int getNumAdded() {
        return 1;
    }

    /**
     * Always returns 0, as no IDs are deleted.
     * 
     * @return 0
     */
    @Override
    public int getNumDeleted() {
        return 0;
    }

    /**
     * Apply this addition move to the given subset solution. The move can only be applied if the ID that will be added
     * to the selection is currently not already selected. This guarantees that calling {@link #undo(SubsetSolution)} will
     * correctly undo the move.
     * 
     * @throws SolutionModificationException if the added ID is currently already selected or does not correspond to an entity
     * @param solution solution to which to move is applied
     */
    @Override
    public void apply(SubsetSolution solution) {
        // try adding new ID to selection
        if(!solution.select(add)){
            // failed: already selected
            throw new SolutionModificationException("Error while applying addition move: added ID is already selected.", solution);
        }
    }

    /**
     * Undo this addition move after it has been successfully applied to the given subset solution, by removing the newly added ID
     * from the selection.
     * 
     * @param solution solution to which the move has been applied
     */
    @Override
    public void undo(SubsetSolution solution) {
        solution.deselect(add);
    }

    /**
     * Hash code corresponding to implementation of {@link #equals(Object)}.
     * 
     * @return hash code of this addition move
     */
    @Override
    public int hashCode() {
        int hash = 7;
        hash = 79 * hash + add;
        return hash;
    }

    /**
     * Two addition moves are considered equal if they add the same ID.
     * 
     * @param obj object to compare with this addition move for equality
     * @return <code>true</code> if the given object is also an addition move and adds the same ID
     */
    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final AdditionMove other = (AdditionMove) obj;
        return this.add == other.add;
    }
    
}
