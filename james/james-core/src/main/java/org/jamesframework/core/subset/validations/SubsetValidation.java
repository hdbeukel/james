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

import org.jamesframework.core.problems.constraints.validations.UnanimousValidation;

/**
 * Represents a validation of a subset solution. Separately indicates whether
 * the subset has a valid size, in addition to the general unanimous constraint
 * validation. It can be checked whether the subset passed validation, possibly
 * ignoring its size.
 * 
 * @author <a href="mailto:herman.debeukelaer@ugent.be">Herman De Beukelaer</a>
 */
public class SubsetValidation extends UnanimousValidation {

    // valid size
    private final boolean validSize;

    /**
     * Create a subset validation.
     * 
     * @param validSize indicates whether the subset has a valid size
     * @param unanimousValidation unanimous validation object produced when checking the general constraints
     */
    public SubsetValidation(boolean validSize, UnanimousValidation unanimousValidation) {
        this.validSize = validSize;
        // set general constraint validations
        setValidations(unanimousValidation.getValidations());
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
     * Check whether the subset solution passed validation. If <code>checkSize</code>
     * is <code>false</code> the size of the subset is ignored and only the general
     * constraints are checked.
     * 
     * @param checkSize indicates whether the size should be validated
     * @return <code>true</code> if the subset solution is valid, possibly ignoring its size
     */
    public boolean passed(boolean checkSize) {
        return (!checkSize || validSize) && super.passed();
    }
    
    /**
     * Check whether the subset solution passed validation, taking into
     * account both its size and the general constraint validations.
     * 
     * @return <code>true</code> if the subset has a valid size and satisfies all constraints
     */
    @Override
    public boolean passed(){
        return passed(true);
    }
    
    /**
     * Get a string representation of the validation object. Indicates whether
     * the general constraints are satisfied and if the subset has a valid size.
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
        str.append(", ");
        if(super.passed()){
            str.append("constraints satisfied");
        } else {
            str.append("constraints not satisfied");
        }
        str.append(")");
        return str.toString();
    }

}