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

package org.jamesframework.core.problems.constraints.validations;

import org.jamesframework.core.problems.constraints.Validation;

/**
 * Validation of a solution/move in a tabu search. Separately indicates whether
 * the solution/neighbour is invalid and/or tabu where validation is passed
 * for valid non-tabu solutions only.
 * 
 * @author <a href="mailto:herman.debeukelaer@ugent.be">Herman De Beukelaer</a>
 */
public class TabuValidation implements Validation {

    // tabu or not tabu
    private final boolean tabu;
    // solution validation
    private final Validation validation;

    /**
     * Create a tabu validation.
     * 
     * @param tabu indicates whether the solution is tabu
     * @param validation validation of the solution
     */
    public TabuValidation(boolean tabu,  Validation validation) {
        this.tabu = tabu;
        this.validation = validation;
    }
    
    /**
     * Check whether the solution is tabu.
     * 
     * @return <code>true</code> if it is tabu
     */
    public boolean isTabu(){
        return tabu;
    }
    
    /**
     * Get the solution validation.
     * 
     * @return validation object
     */
    public Validation getValidation(){
        return validation;
    }
    
    /**
     * Check wether the solution passed validation and is not tabu.
     * 
     * @return <code>true</code> if the solution is valid an not tabu.
     */
    @Override
    public boolean passed(){
        return !tabu && validation.passed();
    }
    
    /**
     * Get a string representation of the validation object.
     * Indicates whether the solution is invalid and/or tabu.
     * 
     * @return string representation
     */
    @Override
    public String toString(){
        String str = getValidation().passed() ? "valid" : "invalid";
        str += ", ";
        str += isTabu() ? "tabu" : "not tabu";
        return str;
    }

}
