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

package org.jamesframework.core.problems;

import org.jamesframework.core.exceptions.SolutionCopyException;

/**
 * Represents an abstract solution. Every extending solution type is required to provide appropriate implementations
 * of {@link #equals(Object)} and {@link #hashCode()} that compare solutions by value (instead of reference) and
 * compute consistent hash codes, respectively. The default implementations inherited from {@link Object} have been
 * erased and replaced with abstract methods without implementation.
 * <p>
 * Every solution class should also implement the abstract method {@link #copy()} which is used to create a deep
 * copy of a solution. The returned deep copy should <b>always</b> have the exact same type as the solution on which
 * the method was called. A static method {@link #checkedCopy(Solution)} is provided to create type safe copies of
 * any solution: it returns a deep copy of the exact same type as its argument, given that the general contract
 * of {@link #copy()} is followed in every solution type implementation. If this contract is violated, calling
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
                    throw new SolutionCopyException("Deep copy of solution of type " + origClass.getSimpleName() + " failed. "
                                                            + "Calling copy() yields a solution of type " + copyClass.getSimpleName() + ", not "
                                                            + origClass.getSimpleName() + ". Expected cause of this type mismatch: "
                                                            + origClass.getSimpleName() + " does not directly implement method copy() but "
                                                            + "inherits an undesired implementation from super class "
                                                            + declaringClassOfCopy.getSimpleName() + ".");
                } else {
                    // copy() implemented in T but does not return correct type
                    throw new SolutionCopyException("Deep copy of solution of type " + origClass.getSimpleName() + " failed. "
                                                            + "Calling copy() yields a solution of type " + copyClass.getSimpleName() + ", not "
                                                            + origClass.getSimpleName() + ". Expected cause of this type mismatch: "
                                                            + "faulty implementation of copy() in " + origClass.getSimpleName() + ", "
                                                            + "does not return solution of type " + origClass.getSimpleName() + ".");
                }
            } catch (NoSuchMethodException noSuchMethodEx){
                // this should never happen, all subclasses of Solution have a method copy() somewhere in the class hierarchy
                throw new Error("Solution without method 'copy()': this should never happen; if it does, "
                                + "there is a serious bug in Solution.", noSuchMethodEx);
            }
        }
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
     * Overrides default {@link Object#equals(Object)} to be abstract so that all extending solution types are
     * required to provide an appropriate implemenation that compares solutions by value instead of by reference.
     * A consistent implementation of {@link #hashCode()} should also be provided.
     * 
     * @param other other object to compare for equality
     * @return <code>true</code> if the given object represents the same solution
     */
    @Override
    public abstract boolean equals(Object other);
    
    /**
     * Overrides default {@link Object#hashCode()} to be abstract so that all extending solution types are required
     * to provide a hash code computation that is consistent with the implementation of {@link #equals(Object)}.
     * If two solutions are equal according to {@link #equals(Object)} they should have the same hash code.
     * 
     * @return hash code of this solution
     */
    @Override
    public abstract int hashCode();
    
}
