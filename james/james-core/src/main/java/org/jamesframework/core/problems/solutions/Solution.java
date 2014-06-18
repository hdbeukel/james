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

import org.jamesframework.core.exceptions.SolutionCopyException;

/**
 * Represents an abstract solution. Every extending solution class should provide methods to check for conceptual
 * equality with a given other solution and to compute a corresponding hash code. Two solutions which are deemed
 * equal should always produce the same hash code, while different solutions may (and preferably should) produce
 * different hash codes (according to the general contract of {@link Object#hashCode()}).
 * <p>
 * Every solution class should also implement the abstract method {@link #copy()} which is used to create a deep
 * copy of a solution. The returned deep copy should <b>always</b> have the exact same type as the solution on which
 * the method was called. A static method {@link #checkedCopy(Solution)} is provided to create type safe copies of
 * any solution class: it returns a deep copy of the exact same type as its argument, given that the general contract
 * of {@link #copy()} is followed in every solution class implementation. If this contract is violated, calling
 * {@link #checkedCopy(Solution)} will throw a detailed exception that precisely indicates the cause of the
 * occurred type mismatch.
 * 
 * @author <a href="mailto:herman.debeukelaer@ugent.be">Herman De Beukelaer</a>
 */
public abstract class Solution {
    
    /**
     * Creates a checked deep copy of the given solution with specific type <code>T</code> (a subclass of {@link Solution}).
     * Both the given solution and return type are of the same type <code>T</code>. This method calls {@link #copy()} on
     * the given solution and casts the result to the respective type <code>T</code>. If this cast fails, an exception
     * with a detailed error message is thrown, precisely indicating the expected cause of the type mismatch: the method
     * {@link #copy()} does not return a solution of the correct type <code>T</code>, either because an undesired implementation
     * is inherited from a super class or because the direct implementation violates the general contract of {@link #copy()}.
     * 
     * @param <T> solution type, required to extend {@link Solution}
     * @param solution solution to copy, of type <code>T</code>
     * @throws SolutionCopyException if calling {@link #copy()} on the given solution of type <code>T</code>
     *                                       does not yield a copy of the exact same type <code>T</code>, indicating
     *                                       a faulty implementation (contains a detailed error message)
     * @return copy of type <code>T</code>
     */
    @SuppressWarnings("unchecked")
    static public <T extends Solution> T checkedCopy(T solution){
        // copy solution
        Solution copy = solution.copy();
        // verify type of copy
        Class<?> origClass = solution.getClass();
        Class<?> copyClass = copy.getClass();
        if(copyClass == origClass){
            return (T) copy;
        } else {
            // mismatching types: find out why and throw a detailed exception
            try {
                Class<?> declaringClassOfCopy = origClass.getMethod("copy").getDeclaringClass();
                if(declaringClassOfCopy != origClass){
                    // method copy() not directly implemented in T
                    throw new SolutionCopyException("Deep copy of solution of type " + simpleClassName(origClass) + " failed. "
                                                            + "Calling copy() yields a solution of type " + simpleClassName(copyClass) + ", not "
                                                            + simpleClassName(origClass) + ". Expected cause of this type mismatch: "
                                                            + simpleClassName(origClass) + " does not directly implement method copy() but "
                                                            + "inherits an undesired implementation from super class "
                                                            + simpleClassName(declaringClassOfCopy) + ".");
                } else {
                    // copy() implemented in T but does not return correct type
                    throw new SolutionCopyException("Deep copy of solution of type " + simpleClassName(origClass) + " failed. "
                                                            + "Calling copy() yields a solution of type " + simpleClassName(copyClass) + ", not "
                                                            + simpleClassName(origClass) + ". Expected cause of this type mismatch: "
                                                            + "faulty implementation of copy() in " + simpleClassName(origClass) + ", "
                                                            + "does not return solution of type " + simpleClassName(origClass) + ".");
                }
            } catch (NoSuchMethodException noSuchMethodEx){
                // this should never happen, all subclasses of Solution have a method copy() somewhere in the class hierarchy
                throw new Error("Solution without method 'copy()': this should never happen; if it does, "
                                + "there is a serious bug in Solution.", noSuchMethodEx);
            }
        }
    }
    
    /**
     * Get simple class name by stripping anything before the last dot (".") from the canonical class name.
     * 
     * @param clazz class object
     * @return simple class name
     */
    static private String simpleClassName(Class<?> clazz){
        String canonicalClassName = clazz.getCanonicalName();
        int lastDotIndex = canonicalClassName.lastIndexOf(".");
        return canonicalClassName.substring(lastDotIndex+1);
    }
    
    /**
     * Creates a deep copy of this solution. The implementation of this method should <b>always</b> return a solution
     * of the exact same type as the solution on which the method was called. Violating this contract might result in
     * exceptions when copying solutions from within various classes of the framework. It is therefore of <b>utmost</b>
     * importance that every solution class directly implements this method, also when extending an other concrete
     * solution class in which the method has already been implemented, as an inherited implementation will never
     * return a copy of the correct type.
     * 
     * @return deep copy of this solution, which is equal to the original solution but does not share any references
     *         of contained objects with this original solution
     */
    public abstract Solution copy();
    
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
