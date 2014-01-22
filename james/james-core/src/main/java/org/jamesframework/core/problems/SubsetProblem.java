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

package org.jamesframework.core.problems;

import java.util.Random;
import java.util.concurrent.ThreadLocalRandom;
import org.jamesframework.core.problems.datatypes.SubsetData;
import org.jamesframework.core.problems.objectives.Objective;
import org.jamesframework.core.problems.solutions.SubsetSolution;
import org.jamesframework.core.util.SetUtilities;

/**
 * Represents a subset problem, with solution type {@link SubsetSolution} and a given data type which implements the interface
 * {@link SubsetData}. The minimum and maximum allowed subset size are specified in the subset problem. The problem implements
 * methods for creating random subset solutions and copying subset solutions from the {@link Problem} interface, as well as an
 * additional method for creating empty subset solutions in which no entities are selected.
 * 
 * @param <DataType> underlying data type, should implement the interface {@link SubsetData}
 * @author Herman De Beukelaer <herman.debeukelaer@ugent.be>
 */
public class SubsetProblem<DataType extends SubsetData> extends AbstractProblem<SubsetSolution, DataType> {

    // minimum and maximum subset size
    private int minSubsetSize, maxSubsetSize;
        
    /**
     * Creates a new SubsetProblem with given objective, data and minimum/maximum subset size. Both <code>objective</code>
     * and <code>data</code> are not allowed to be <code>null</code>, an exception will be thrown if they are. Any objective
     * designed to evaluate subset solutions (or more general solutions) using subset data (or more general data)
     * is accepted. The minimum and maximum subset size should be contained in <code>[1,n]</code> where <code>n</code>
     * is the number of entities in the given subset data from which a subset is to be selected. Also, the minimum size
     * should be smaller than or equal to the maximum size.
     * 
     * @param objective objective function, can not be <code>null</code>
     * @param data underlying subset data, can not be <code>null</code>
     * @param minSubsetSize minimum subset size (should be > 0 and <= maximum subset size)
     * @param maxSubsetSize maximum subset size (should be >= minimum subset size and <= number of entities in underlying data)
     * @throws NullPointerException if <code>objective</code> or <code>data</code> is <code>null</code>
     * @throws IllegalArgumentException if an invalid minimum or maximum subset size is specified
     */
    public SubsetProblem(Objective<? super SubsetSolution, ? super SubsetData> objective, DataType data,
                            int minSubsetSize, int maxSubsetSize) throws NullPointerException, IllegalArgumentException {
        // call constructor of AbstractProblem (already checks that objective is not null)
        super(objective, data);
        // check that data is not null
        if(data == null){
            throw new NullPointerException("Error while creating subset problem: subset data is required, can not be null.");
        }
        // check constraints on minimum/maximum size
        if(minSubsetSize <= 0){
            throw new IllegalArgumentException("Error while creating subset problem: minimum subset size should be > 0.");
        }
        if(maxSubsetSize > data.getIDs().size()){
            throw new IllegalArgumentException("Error while creating subset problem: maximum subset size can not be larger "
                                                + "than number of entities in underlying subset data.");
        }
        if(minSubsetSize > maxSubsetSize){
            throw new IllegalArgumentException("Error while creating subset problem: minimum subset size should be <= maximum subset size.");
        }
        // store min/max size
        this.minSubsetSize = minSubsetSize;
        this.maxSubsetSize = maxSubsetSize;
    }
    
    /**
     * Creates a subset problem with fixed subset size, equivalent to calling<pre>
     * SubsetProblem p = new SubsetProblem(objective, data, fixedSubsetSize, fixedSubsetSize);</pre>
     * The fixed subset size should be contained in <code>[1,n]</code> where <code>n</code>
     * is the number of entities in the given subset data from which a subset is to be selected.
     * 
     * @param objective objective function, can not be <code>null</code>
     * @param data underlying subset data, can not be <code>null</code>
     * @param fixedSubsetSize fixed subset size
     * @throws NullPointerException if <code>objective</code> or <code>data</code> is <code>null</code>
     * @throws IllegalArgumentException if an invalid fixed subset size is specified 
     */
    public SubsetProblem(Objective<? super SubsetSolution, ? super SubsetData> objective, DataType data,
                            int fixedSubsetSize) throws NullPointerException, IllegalArgumentException {
        this(objective, data, fixedSubsetSize, fixedSubsetSize);
    }
    
    /**
     * Set new subset data, verifying that the data is not <code>null</code>.
     * 
     * @param data new subset data, can not be <code>null</code>
     * @throws NullPointerException if <code>data</code> is <code>null</code>
     */
    @Override
    public void setData(DataType data) throws NullPointerException{
        // check not null
        if(data == null){
            throw new NullPointerException("Error while setting data in subset problem: subset data can not be null.");
        }
        // not null: call super
        super.setData(data);
    }
    
    /**
     * Create a random solution within the allowed minimum and maximum subset size. The IDs of all entities are taken from the
     * underlying subset data, and a random subset of IDs is selected.
     * 
     * @return random subset solution within minimum and maximum size
     */
    @Override
    public SubsetSolution createRandomSolution() {
        // get thread local random generator
        final Random rg = ThreadLocalRandom.current();
        // create new subset solution with IDs from underlying data
        final SubsetSolution sol = new SubsetSolution(getData().getIDs());
        // pick random number of initially selected IDs within bounds
        int size = minSubsetSize + rg.nextInt(maxSubsetSize-minSubsetSize+1);
        // randomly select initial IDs
        sol.selectAll(SetUtilities.getRandomSubset(sol.getAllIDs(), size, rg));
        // return random solution
        return sol;
    }

    /**
     * Create a deep copy of the given subset solution. This implementation creates a new subset solution and sets the same IDs,
     * selected IDs and unselected IDs as in the given solution.
     * 
     * @param solution solution to copy
     * @return deep copy with same selected/unselected IDs
     */
    @Override
    public SubsetSolution copySolution(SubsetSolution solution) {
        // create a new subset solution with same IDs
        SubsetSolution copy = new SubsetSolution(solution.getAllIDs());
        // select same IDs as those selected in given solution
        copy.selectAll(solution.getSelectedIDs());
        // return copy
        return copy;
    }
    
    /**
     * Creates an empty subset solution, setting the IDs of the underlying data where no IDs are selected.
     * 
     * @return empty subset solution with no selected IDs
     */
    public SubsetSolution createEmptySubsetSolution(){
        return new SubsetSolution(getData().getIDs());
    }

    /**
     * Get the minimum subset size.
     * 
     * @return minimum subset size
     */
    public int getMinSubsetSize() {
        return minSubsetSize;
    }

    /**
     * Set the minimum subset size. Specified size should be >= 1 and <= the current maximum subset size.
     * 
     * @param minSubsetSize new minimum subset size
     * @throws IllegalArgumentException if an invalid minimum size is given
     */
    public void setMinSubsetSize(int minSubsetSize) throws IllegalArgumentException {
        // check size
        if(minSubsetSize <= 0){
            throw new IllegalArgumentException("Error while setting minimum subset size: should be > 0.");
        }
        if(minSubsetSize > maxSubsetSize){
            throw new IllegalArgumentException("Error while setting minimum subset size: should be <= maximum subset size.");
        }
        this.minSubsetSize = minSubsetSize;
    }

    /**
     * Get the maximum subset size.
     * 
     * @return maximum subset size
     */
    public int getMaxSubsetSize() {
        return maxSubsetSize;
    }

    /**
     * Set the maximum subset size. Specified size should be >= the current minimum subset size
     * and <= the number of entities in the underlying subset data.
     * 
     * @param maxSubsetSize new maximum subset size
     * @throws IllegalArgumentException if an invalid maximum size is given
     */
    public void setMaxSubsetSize(int maxSubsetSize) throws IllegalArgumentException {
        // check size
        if(maxSubsetSize < minSubsetSize){
            throw new IllegalArgumentException("Error while setting maximum subset size: should be >= minimum subset size.");
        }
        if(maxSubsetSize > getData().getIDs().size()){
            throw new IllegalArgumentException("Error while setting maximum subset size: can not be larger "
                                                + "than number of entities in underlying subset data.");
        }
        this.maxSubsetSize = maxSubsetSize;
    }

}
