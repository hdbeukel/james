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

package org.jamesframework.core.subset.neigh.moves;

import java.util.Collections;
import java.util.Set;
import org.jamesframework.core.exceptions.SolutionModificationException;
import org.jamesframework.core.subset.SubsetSolution;

/**
 * Simple subset move that removes a single ID from the current selection.
 * 
 * @author <a href="mailto:herman.debeukelaer@ugent.be">Herman De Beukelaer</a>
 */
public class DeletionMove extends AbstractSubsetMove {
    
    // deleted ID
    private final int delete;
    
    /**
     * Create a new deletion move, specifying the ID that will be removed from the selection
     * when this moves is applied to a given subset solution.
     * 
     * @param delete ID to be removed from the selection
     */
    public DeletionMove(int delete){
        this.delete = delete;
    }

    /**
     * Returns an unmodifiable empty set, as no IDs are added.
     * 
     * @return empty set
     */
    @Override
    public Set<Integer> getAddedIDs() {
        return Collections.emptySet();
    }

    /**
     * Returns an unmodifiable singleton, containing the only deleted ID.
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
    public int getDeletedID() {
        return delete;
    }

    /**
     * Always returns 0, as no IDs are added.
     * 
     * @return 0
     */
    @Override
    public int getNumAdded() {
        return 0;
    }

    /**
     * Always returns 1, as a single ID is deleted.
     * 
     * @return 1
     */
    @Override
    public int getNumDeleted() {
        return 1;
    }

    /**
     * Apply this deletion move to the given subset solution. The move can only be applied if the ID to
     * be deleted from the selection is currently indeed selected. This guarantees that calling
     * {@link #undo(SubsetSolution)} will correctly undo the move.
     * 
     * @throws SolutionModificationException if the deleted ID is currently not selected or does not correspond to an entity
     * @param solution solution to which to move is applied
     */
    @Override
    public void apply(SubsetSolution solution) {
        // try to deselect ID
        if(!solution.deselect(delete)){
            // failed: ID currently not selected
            throw new SolutionModificationException("Error while applying deletion move to subset solution: deleted ID currently not selected.", solution);
        }
    }

    /**
     * Undo this deletion move after it has been successfully applied to the given subset solution, by re-adding the
     * deleted ID to the selection.
     * 
     * @param solution solution to which the move has been applied
     */
    @Override
    public void undo(SubsetSolution solution) {
        // re-add deleted ID
        solution.select(delete);
    }
    
}
