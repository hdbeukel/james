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

package org.jamesframework.core.subset.validations;

import org.jamesframework.core.problems.constraints.validations.Validation;

/**
 * Represents a validation of a subset solution. Separately indicates whether
 * the subset has a valid size, in addition to the general constraint validation.
 * It can be checked whether the subset passed validation, possibly ignoring its size.
 * 
 * @author <a href="mailto:herman.debeukelaer@ugent.be">Herman De Beukelaer</a>
 */
public class SubsetValidation implements Validation {

    // valid size
    private final boolean validSize;
    // constraint validation object (may be null)
    private final Validation constraintValidation;

    /**
     * Create a subset validation.
     * 
     * @param validSize indicates whether the subset has a valid size
     * @param constraintValidation validation object produced when checking the general constraints
     *                             (may be <code>null</code>)
     */
    public SubsetValidation(boolean validSize, Validation constraintValidation) {
        this.validSize = validSize;
        this.constraintValidation = constraintValidation;
    }
    
    /**
     * Check whether the subset solution has a valid size.
     * 
     * @return <code>true</code> if the subset has a valid size
     */
    public boolean validSize(){
        return validSize;
    }
    
    /**
     * Get the validation object produced when checking the general constraints (may be <code>null</code>).
     * 
     * @return constraint validation object
     */
    public Validation getConstraintValidation(){
        return constraintValidation;
    }
    
    /**
     * Check whether the subset solution passed validation. If <code>checkSize</code>
     * is <code>false</code> the size of the subset is ignored and only the general
     * constraints are checked (if not <code>null</code>).
     * 
     * @param checkSize indicates whether the size should be validated
     * @return <code>true</code> if the subset solution is valid, possibly ignoring its size
     */
    public boolean passed(boolean checkSize) {
        return (!checkSize || validSize) && (constraintValidation == null || constraintValidation.passed());
    }
    
    /**
     * Check whether the subset solution passed validation, taking into account both
     * its size and the general constraint validations (if not <code>null</code>).
     * 
     * @return <code>true</code> if the subset has a valid size and satisfies all constraints (if any)
     */
    @Override
    public boolean passed(){
        return passed(true);
    }
    
    /**
     * Get a string representation of the validation object. Indicates whether the
     * general constraints are satisfied (if any) and if the subset has a valid size.
     * 
     * @return string representation
     */
    @Override
    public String toString(){
        StringBuilder str = new StringBuilder(passed() ? "valid" : "invalid");
        str.append(" (");
        if(validSize()){
            str.append("valid size");
        } else {
            str.append("invalid size");
        }
        if(constraintValidation != null){
            str.append(", ");
            if(constraintValidation.passed()){
                str.append("constraints satisfied");
            } else {
                str.append("constraints not satisfied");
            }
        }
        str.append(")");
        return str.toString();
    }

}
