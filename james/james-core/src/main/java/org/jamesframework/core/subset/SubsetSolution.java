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

package org.jamesframework.core.subset;

import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.LinkedHashSet;
import java.util.Objects;
import java.util.Set;
import java.util.TreeSet;
import java.util.stream.Collectors;
import org.jamesframework.core.exceptions.SolutionModificationException;
import org.jamesframework.core.problems.Solution;

/**
 * High-level subset solution modeled in terms of IDs of selected items. The subset is sampled from a data set
 * of items which are each assumed to be identified using a unique integer ID.
 * 
 * @author <a href="mailto:herman.debeukelaer@ugent.be">Herman De Beukelaer</a>
 */
public class SubsetSolution extends Solution {
    
    // set of selected IDs
    private final Set<Integer> selected;
    // set of unselected IDs
    private final Set<Integer> unselected;
    // set of all IDs (stored for efficiency, will always
    // be equal to the union of selected and unselected)
    private final Set<Integer> all;
    // comparator according to which IDs are sorted;
    // null in case no order has been imposed
    private Comparator<Integer> orderOfIDs;
    
    /**
     * Creates a new subset solution given the set of all IDs, each corresponding to an underlying entity,
     * from which a subset is to be selected. Initially, no IDs are selected. Note: IDs are copied to the
     * internal data structures of the subset solution; no reference is stored to the set given at construction.
     * IDs are stored in sets that do not impose any order.
     * 
     * @param allIDs set of all IDs from which a subset is to be selected
     * @throws NullPointerException if <code>allIDs</code> is <code>null</code>
     */
    public SubsetSolution(Set<Integer> allIDs){
        this(allIDs, false);
    }
    
    /**
     * Creates a new subset solution given the set of all IDs, and the set of currently selected IDs. Note: IDs
     * are copied to the internal data structures of the subset solution; no reference is stored to the sets given
     * at construction. IDs are stored in sets that do not impose any ordering.
     * 
     * @param allIDs set of all IDs from which a subset is to be selected
     * @param selectedIDs set of currently selected IDs (subset of all IDs)
     * @throws NullPointerException if <code>allIDs</code> or <code>selectedIDs</code> are <code>null</code>,
     *                              of <code>selectedIDs</code> contains any <code>null</code> elements
     * @throws SolutionModificationException if <code>selectedIDs</code> is not a subset of <code>allIDs</code>
     */
    public SubsetSolution(Set<Integer> allIDs, Set<Integer> selectedIDs){
        this(allIDs, selectedIDs, false);
    }
    
    /**
     * Creates a new subset solution given the set of all IDs, each corresponding to an underlying entity,
     * from which a subset is to be selected. Initially, no IDs are selected. Note: IDs are copied to the
     * internal data structures of the subset solution; no reference is stored to the set given at construction.
     * If <code>naturalOrder</code> is <code>true</code>, the set of selected, unselected and all IDs will be
     * ordered according to the natural ordering of integers (ascending); else, no ordering is imposed on the IDs.
     * 
     * @param allIDs set of all IDs from which a subset is to be selected
     * @param naturalOrder if <code>naturalOrder</code> is <code>true</code>, IDs will be ordered according to
     *                     their natural ordering; else, no ordering is imposed on the IDs
     * @throws NullPointerException if <code>allIDs</code> is <code>null</code>
     */
    public SubsetSolution(Set<Integer> allIDs, boolean naturalOrder){
        this(allIDs, naturalOrder ? Comparator.naturalOrder() : null);
    }
    
    /**
     * Creates a new subset solution given the set of all IDs, and the set of currently selected IDs. Note: IDs
     * are copied to the internal data structures of the subset solution; no reference is stored to the sets given
     * at construction. If <code>naturalOrder</code> is <code>true</code>, the set of selected, unselected and all
     * IDs will be ordered according to the natural ordering of integers (ascending); else, no ordering is imposed
     * on the IDs.
     * 
     * @param allIDs set of all IDs from which a subset is to be selected
     * @param selectedIDs set of currently selected IDs (subset of all IDs)
     * @param naturalOrder if <code>naturalOrder</code> is <code>true</code>, IDs will be ordered according to
     *                     their natural ordering; else, no ordering is imposed on the IDs
     * @throws NullPointerException if <code>allIDs</code> or <code>selectedIDs</code> are <code>null</code>,
     *                              or <code>selectedIDs</code> contains any <code>null</code> elements
     * @throws SolutionModificationException if <code>selectedIDs</code> is not a subset of <code>allIDs</code>
     */
    public SubsetSolution(Set<Integer> allIDs, Set<Integer> selectedIDs, boolean naturalOrder){
        this(allIDs, selectedIDs, naturalOrder ? Comparator.naturalOrder() : null);
    }
    
    /**
     * Creates a new subset solution given the set of all IDs, each corresponding to an underlying entity,
     * from which a subset is to be selected. Initially, no IDs are selected. Note: IDs are copied to the
     * internal data structures of the subset solution; no reference is stored to the set given at construction.
     * The order defined by the given <code>comparator</code> is imposed on the set of selected, unselected and
     * all IDs; if this argument is <code>null</code> no order is imposed.
     * 
     * @param allIDs set of all IDs from which a subset is to be selected
     * @param orderOfIDs comparator according to which IDs are ordered, allowed to be
     *                   <code>null</code> in which case no order is imposed
     * @throws NullPointerException if <code>allIDs</code> is <code>null</code>
     */
    public SubsetSolution(Set<Integer> allIDs, Comparator<Integer> orderOfIDs){
        this.orderOfIDs = orderOfIDs;
        if(orderOfIDs == null){
            all = new LinkedHashSet<>(allIDs);         // set with all IDs (copy)
            selected = new LinkedHashSet<>();          // set with selected IDs (empty)
            unselected = new LinkedHashSet<>(allIDs);  // set with unselected IDs (all)
        } else {
            all = new TreeSet<>(orderOfIDs);           // sorted set with all IDs (copy)
            all.addAll(allIDs);
            selected = new TreeSet<>(orderOfIDs);      // sorted set with selected IDs (empty)
            unselected = new TreeSet<>(orderOfIDs);    // sorted set with unselected IDs (all)
            unselected.addAll(allIDs);
        }
    }
    
    /**
     * Creates a new subset solution given the set of all IDs, and the set of currently selected IDs. Note: IDs
     * are copied to the internal data structures of the subset solution; no reference is stored to the sets given
     * at construction. The order defined by the given <code>comparator</code> is imposed on the set of selected,
     * unselected and all IDs; if this argument is <code>null</code> no order is imposed.
     * 
     * @param allIDs set of all IDs from which a subset is to be selected
     * @param selectedIDs set of currently selected IDs (subset of all IDs)
     * @param orderOfIDs comparator according to which IDs are ordered, allowed to be
     *                   <code>null</code> in which case no order is imposed
     * @throws NullPointerException if <code>allIDs</code> or <code>selectedIDs</code> are <code>null</code>,
     *                              or <code>selectedIDs</code> contains any <code>null</code> elements
     * @throws SolutionModificationException if <code>selectedIDs</code> is not a subset of <code>allIDs</code>
     */
    public SubsetSolution(Set<Integer> allIDs, Set<Integer> selectedIDs, Comparator<Integer> orderOfIDs){
        this(allIDs, orderOfIDs);
        for(int ID : selectedIDs){
            if(!allIDs.contains(ID)){
                throw new SolutionModificationException("Error while creating subset solution: "
                                + "set of selected IDs should be a subset of set of all IDs. Got: allIDs = "
                                + allIDs + ", selectedIDs = " + selectedIDs, this);
            }
            selected.add(ID);
            unselected.remove(ID);
        }
    }
    
    /**
     * Copy constructor. Creates a new subset solution which is identical to the given solution, but does not have
     * any reference to any data structures contained within the given solution (deep copy). The obtained subset
     * solution will have exactly the same selected/unselected IDs as the given solution, and will impose the
     * same ordering on the IDs (if any).
     * 
     * @param sol solution to copy
     */
    public SubsetSolution(SubsetSolution sol){
        this(sol.getAllIDs(), sol.getSelectedIDs(), sol.getOrderOfIDs());
    }
    
    /**
     * Create a deep copy of this subset solution, obtained through the copy constructor,
     * passing <code>this</code> as argument.
     * 
     * @return deep copy of this subset solution
     */
    @Override
    public SubsetSolution copy() {
        return new SubsetSolution(this);
    }
    
    /**
     * Get the comparator used to impose an order on the set of selected, unselected and all IDs.
     * May return <code>null</code> which means that no order has been imposed.
     * 
     * @return comparator according to which IDs are ordered, may be <code>null</code>
     */
    public Comparator<Integer> getOrderOfIDs(){
        return orderOfIDs;
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
        selectAll(getAllIDs());
    }
    
    /**
     * Deselect all IDs.
     */
    public void deselectAll(){
        deselectAll(getAllIDs());
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
     * Checks whether the given other object represents the same subset solution.
     * Subset solutions are considered equal if and only if they contain exactly
     * the same selected and unselected IDs.
     * 
     * @param other other object to check for equality
     * @return <code>true</code> if the other object is also a subset solution and contains exactly the same
     *         selected and unselected IDs as this solution
     */
    @Override
    public boolean equals(Object other) {
        // check null
        if (other == null) {
            return false;
        }
        // check type
        if (getClass() != other.getClass()) {
            return false;
        }
        // cast to subset solution
        final SubsetSolution otherSubsetSolution = (SubsetSolution) other;
        // check selected and unselected IDs
        return Objects.equals(getSelectedIDs(), otherSubsetSolution.getSelectedIDs())
                && Objects.equals(getUnselectedIDs(), otherSubsetSolution.getUnselectedIDs());
    }

    /**
     * Computes a hash code that is consistent with the implementation of {@link #equals(Object)} meaning that
     * the same hash code is returned for equal subset solutions. The computed hash code is a linear combination
     * of the hash codes of both the set of selected and unselected IDs, added to a constant.
     * 
     * @return hash code of this subset solution
     */
    @Override
    public int hashCode() {
        int hash = 7;
        // account for selected IDs
        hash = 23 * hash + Objects.hashCode(getSelectedIDs());
        // account for unselected IDs
        hash = 23 * hash + Objects.hashCode(getUnselectedIDs());
        return hash;    
    }
    
    /**
     * Creates a formatted string containing the selected IDs.
     * 
     * @return formatted string
     */
    @Override
    public String toString(){
        return getSelectedIDs().stream()
                               .map(Object::toString)
                               .collect(Collectors.joining(", ", "SubsetSolution: {", "}"));
    }

}
