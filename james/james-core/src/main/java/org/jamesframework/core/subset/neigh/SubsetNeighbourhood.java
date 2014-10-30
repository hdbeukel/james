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

import java.util.LinkedHashSet;
import java.util.Set;
import org.jamesframework.core.search.neigh.Neighbourhood;
import org.jamesframework.core.subset.SubsetSolution;

/**
 * Abstract subset neighbourhood. Provides protected methods to infer the set of candidate IDs to be added to
 * or removed from the selection based on the currently (un)selected IDs and possibly a given set of fixed IDs
 * that are not allowed to be (de)selected.
 * 
 * @author <a href="mailto:herman.debeukelaer@ugent.be">Herman De Beukelaer</a>
 */
public abstract class SubsetNeighbourhood implements Neighbourhood<SubsetSolution>{

    // set of fixed IDs (possibly null)
    private final Set<Integer> fixedIDs;
    
    /**
     * Initialize abstract subset neighbourhood with a given set of fixed IDs that are not allowed to
     * be selected or deselected. None of the generated moves may add nor remove any of these IDs.
     * 
     * @param fixedIDs set of fixed IDs (either <code>null</code> or empty if no IDs are fixed)
     */
    public SubsetNeighbourhood(Set<Integer> fixedIDs){
        this.fixedIDs = fixedIDs;
    }
    
    /**
     * Infer the set of IDs that may be added to the selection in the given subset solution. If no IDs
     * were fixed at construction this method simply returns the set of currently unselected IDs obtained
     * by calling {@link SubsetSolution#getUnselectedIDs()}. Else, it returns a copy of this (immutable)
     * set from which all fixed IDs have been removed.
     * 
     * @param currentSolution current subset solution
     * @return set of IDs that may be added
     */
    protected Set<Integer> getAddCandidates(SubsetSolution currentSolution){
        // get set of candidate IDs for addition
        Set<Integer> addCandidates = currentSolution.getUnselectedIDs();
        // remove fixed IDs, if any, from candidates
        if(fixedIDs != null && !fixedIDs.isEmpty()){
            addCandidates = new LinkedHashSet<>(addCandidates);
            addCandidates.removeAll(fixedIDs);
        }
        return addCandidates;
    }
    
    /**
     * Infer the set of IDs that may be removed from the selection in the given subset solution. If no IDs
     * were fixed at construction this method simply returns the set of currently selected IDs obtained
     * by calling {@link SubsetSolution#getSelectedIDs()}. Else, it returns a copy of this (immutable)
     * set from which all fixed IDs have been removed.
     * 
     * @param currentSolution current subset solution
     * @return set of IDs that may be removed
     */
    protected Set<Integer> getRemoveCandidates(SubsetSolution currentSolution){
        // get set of candidate IDs for removal
        Set<Integer> removeCandidates = currentSolution.getSelectedIDs();
        // remove fixed IDs, if any, from candidates
        if(fixedIDs != null && !fixedIDs.isEmpty()){
            removeCandidates = new LinkedHashSet<>(removeCandidates);
            removeCandidates.removeAll(fixedIDs);
        }
        return removeCandidates;
    }
    
}
