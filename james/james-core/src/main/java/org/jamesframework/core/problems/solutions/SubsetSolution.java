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

package org.jamesframework.core.problems.solutions;

import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;
import org.jamesframework.core.exceptions.SolutionModificationException;

/**
 * Implements a generic subset solution based on IDs of selected entities. The subset is sampled from a set of entities
 * which are each assumed to be uniquely identified using an integer ID, so that a generic subset solution can be modeled
 * using these IDs only, independently of the actual corresponding entities.
 * 
 * @author Herman De Beukelaer <herman.debeukelaer@ugent.be>
 */
public class SubsetSolution extends Solution {
    
    // set of selected IDs
    private final Set<Integer> selected;
    // set of unselected IDs
    private final Set<Integer> unselected;
    // set of all IDs (stored for efficiency, will always
    // be equal to the union of selected and unselected)
    private final Set<Integer> all;
    
    /**
     * Creates a new subset solution given the set of all IDs, each corresponding to an underlying entity,
     * from which a subset is to be selected. Initially, no IDs are selected. Note: IDs are copied to the
     * internal data structures of the subset solution; no reference is stored to the set given at construction.
     * 
     * @param allIDs set of all IDs from which a subset is to be selected
     */
    public SubsetSolution(Set<Integer> allIDs){
        // store set with all IDs
        all = new HashSet<>(allIDs);
        // create empty selected/unselected sets
        selected = new HashSet<>();
        unselected = new HashSet<>();
        // set all IDs as unselected
        unselected.addAll(all);
    }
    
    /**
     * Select the given ID. If there is no entity with the given ID, a {@link SolutionModificationException} is thrown.
     * If the ID is currently already selected, the subset solution is not modified and false is returned. Finally,
     * true is returned if the ID has been successfully selected.
     * 
     * @param ID ID to be selected
     * @throws SolutionModificationException if there is no entity with this ID
     * @return true if the ID has been successfully selected, false if it was already selected
     */
    public boolean select(int ID) {
        // verify that the ID occurs
        if(!all.contains(ID)){
            throw new SolutionModificationException("Error while modifying subset solution: "
                                + "unable to select ID " +  ID + " (no entity with this ID).", this);
        }
        // verify that ID is currently not selected
        if(selected.contains(ID)){
            // already selected: return false
            return false;
        }
        // currently unselected, existing ID: select it
        selected.add(ID);
        unselected.remove(ID);
        return true;
    }
    
    /**
     * Deselect the given ID. If there is no entity with the given ID, a {@link SolutionModificationException} is thrown.
     * If the ID is currently not selected, the subset solution is not modified and false is returned. Finally,
     * true is returned if the ID has been successfully deselected.
     * 
     * @param ID ID to be deselected
     * @throws SolutionModificationException if there is no entity with this ID
     * @return true if the ID has been successfully deselected, false if it is currently not selected
     */
    public boolean deselect(int ID) {
        // verify that the ID occurs
        if(!all.contains(ID)){
            throw new SolutionModificationException("Error while modifying subset solution: "
                                + "unable to deselect ID " +  ID + " (no entity with this ID).", this);
        }
        // verify that ID is currently selected
        if(!selected.contains(ID)){
            // not selected: return false
            return false;
        }
        // currently selected, existing ID: deselect it
        selected.remove(ID);
        unselected.add(ID);
        return true;
    }
    
    /**
     * Select all IDs contained in the given collection. Returns true if the subset solution was modified by this
     * operation, i.e. if at least one previously unselected ID has been selected.
     * 
     * @param IDs collection of IDs to be selected
     * @throws SolutionModificationException if the given collection contains at least one ID which does not correspond to an entity
     * @throws NullPointerException if <code>null</code> is passed or the given collection contains at least one <code>null</code> element
     * @return true if the subset solution was modified
     */
    public boolean selectAll(Collection<Integer> IDs) {
        boolean modified = false;
        for(int ID : IDs){
            if(select(ID)){
                modified = true;
            }
        }
        return modified;
    }
    
    /**
     * Deselect all IDs contained in the given collection. Returns true if the subset solution was modified by this
     * operation, i.e. if at least one previously selected ID has been deselected.
     * 
     * @param IDs collection of IDs to be deselected
     * @throws SolutionModificationException if the given collection contains at least one ID which does not correspond to an entity
     * @throws NullPointerException if <code>null</code> is passed or the given collection contains at least one <code>null</code> element
     * @return true if the subset solution was modified
     */
    public boolean deselectAll(Collection<Integer> IDs) {
        boolean modified = false;
        for(int ID : IDs){
            if(deselect(ID)){
                modified = true;
            }
        }
        return modified;
    }
    
    /**
     * Select all IDs.
     */
    public void selectAll(){
        selected.addAll(unselected);
        unselected.clear();
    }
    
    /**
     * Deselect all IDs.
     */
    public void deselectAll(){
        unselected.addAll(selected);
        selected.clear();
    }
    
    /**
     * Returns an unmodifiable view of the set of currently selected IDs. Any attempt to modify the returned set
     * will result in an {@link UnsupportedOperationException}.
     * 
     * @return unmodifiable view of currently selected IDs
     */
    public Set<Integer> getSelectedIDs(){
        return Collections.unmodifiableSet(selected);
    }
    
    /**
     * Returns an unmodifiable view of the set of currently non selected IDs. Any attempt to modify the returned set
     * will result in an {@link UnsupportedOperationException}.
     * 
     * @return unmodifiable view of currently non selected IDs
     */
    public Set<Integer> getUnselectedIDs(){
        return Collections.unmodifiableSet(unselected);
    }
    
    /**
     * Returns an unmodifiable view of the set of all IDs. Any attempt to modify the returned set
     * will result in an {@link UnsupportedOperationException}. This set will always be equal to
     * the union of {@link #getSelectedIDs()} and {@link #getUnselectedIDs()}.
     * 
     * @return unmodifiable view of all IDs
     */
    public Set<Integer> getAllIDs(){
        return Collections.unmodifiableSet(all);
    }
    
    /**
     * Get the number of IDs which are currently selected. Corresponds to the size of the selected subset.
     * 
     * @return number of selected IDs
     */
    public int getNumSelectedIDs(){
        return selected.size();
    }
    
    /**
     * Get the number of IDs which are currently unselected.
     * 
     * @return number of unselected IDs
     */
    public int getNumUnselectedIDs(){
        return unselected.size();
    }
    
    /**
     * Get the total number of IDs. The returned number will always be equal to the sum of
     * {@link #getNumSelectedIDs()} and {@link #getNumUnselectedIDs()}.
     * 
     * @return total number of IDs
     */
    public int getTotalNumIDs(){
        return all.size();
    }

    /**
     * Checks if the given solution is also a subset solution which is conceptually equal to this subset solution.
     * Subset solutions are considered equal if and only if they contain exactly the same selected and unselected IDs.
     * 
     * @param sol other solution to check for equality
     * @return true if the other solution is also a subset solution and contains exactly the same selected and unselected IDs as this solution
     */
    @Override
    public boolean isSameSolution(Solution sol) {
        // check null
        if (sol == null) {
            return false;
        }
        // check type
        if (getClass() != sol.getClass()) {
            return false;
        }
        // cast to subset solution
        final SubsetSolution other = (SubsetSolution) sol;
        // check selected IDs
        if (!Objects.equals(selected, other.selected)) {
            return false;
        }
        // check unselected IDs
        if (!Objects.equals(unselected, other.unselected)) {
            return false;
        }
        // all checks passed: equal
        return true;
    }

    /**
     * Computes a hash code in compliance with the implementation of {@link #isSameSolution(Solution)}, meaning that the same hash code
     * is returned for equal subset solutions. The computed hash code is a linear combination of the hash codes of the underlying
     * sets of selected and unselected IDs, added to a constant term.
     * 
     * @return hash code of this subset solution
     */
    @Override
    public int computeHashCode() {
        int hash = 7;
        // account for selected IDs
        hash = 23 * hash + Objects.hashCode(selected);
        // account for unselected IDs
        hash = 23 * hash + Objects.hashCode(unselected);
        return hash;    
    }
    
    @Override
    public String toString(){
        StringBuilder str = new StringBuilder();
        str.append("SubsetSolution: {");
        // add selected IDs
        for(int ID : selected){
            str.append(ID).append(", ");
        }
        // remove final comma and space if nonzero number of selected IDs
        if(getNumSelectedIDs() > 0){
            str.delete(str.length()-2, str.length());
        }
        // close brackets
        str.append("}");
        // return string
        return str.toString();
    }

}
