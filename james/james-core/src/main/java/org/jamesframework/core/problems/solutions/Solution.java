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

/**
 *  Represents an abstract solution. Every solution class should provide
 *  methods to compute the hash code of a specific solution, and to check for
 *  conceptual equality with other solutions. The implemented equality
 *  should be consistent with the computed hash codes, i.e. two solutions which
 *  are deemed equal should produce the same hash code, while unequal solutions
 *  may (and preferably should) produce different hash codes.
 * 
 * @author Herman De Beukelaer <herman.debeukelaer@ugent.be>
 */
public abstract class Solution {
    
    /**
     * Checks whether this solution is conceptually equal to an other, given solution.
     * The implementation should be consistent with {@link #computeHashCode()}, i.e. if
     * it returns true, both solutions should always yield the same hash code.
     * 
     * @param sol other solution to check for equality
     * @return true if both solutions are conceptually equal
     */
    public abstract boolean isSameSolution(Solution sol);
    
    /**
     * Compute a hash code for this solution. The implementation should be consistent with
     * {@link #isSameSolution(Solution)}, i.e. it should return exactly the same hash code
     * for any solution which is deemed equal.
     * 
     * @return computed hash code
     */
    public abstract int computeHashCode();

    /**
     * Overrides default equality check by verifying whether the given object is of type
     * {@link Solution} and subsequently calling the specific equality check
     * {@link #isSameSolution(Solution)}.
     * 
     * @param obj object to check for equality
     * @return true in case given object is a solution which is conceptually equal to this
     *  solution
     */
    @Override
    public final boolean equals(Object obj) {
        // check not null
        if (obj == null) {
            return false;
        }
        // check same type
        if (getClass() != obj.getClass()) {
            return false;
        }
        // cast to type Solution
        final Solution other = (Solution) obj;
        // call equality check for specific subtype
        return isSameSolution(other);
    }
    
    /**
     * Overrides default hash code computation by calling {@link #computeHashCode()}.
     * 
     * @return computed hash code
     */
    @Override
    public final int hashCode() {
        // compute and return hash code
        return computeHashCode();
    }
    
}
