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

/**
 * A simple validation object that merely wraps a boolean value
 * indicating whether the corresponding solution passed validation.
 * 
 * @author <a href="mailto:herman.debeukelaer@ugent.be">Herman De Beukelaer</a>
 */
public class SimpleValidation implements Validation {

    /**
     * Constant simple validation indicating that the validation passed successfully
     * ({@link #passed()} returns <code>true</code>).
     */
    public static final SimpleValidation PASSED = new SimpleValidation(true);
    
    /**
     * Constant simple validation indicating that the validation failed
     * ({@link #passed()} returns <code>false</code>).
     */
    public static final SimpleValidation FAILED = new SimpleValidation(false);
    
    // contained boolean value
    private final boolean passed;

    /**
     * Create a simple validation with a given boolean value, indicating
     * whether the corresponding solution passed validation.
     * 
     * @param passed boolean value, <code>true</code> if the corresponding solution is valid
     */
    public SimpleValidation(boolean passed) {
        this.passed = passed;
    }
    
    /**
     * Get the boolean value specified at construction, indicating
     * whether the corresponding solution passed validation.
     * 
     * @return boolean value, <code>true</code> if the corresponding solution is valid
     */
    @Override
    public boolean passed() {
        return passed;
    }
    
    /**
     * Get a string representation of the validation object.
     * If validation passed, a string "valid" is returned,
     * else "invalid" is returned.
     * 
     * @return string representation ("valid"/"invalid")
     */
    @Override
    public String toString(){
        return passed() ? "valid" : "invalid";
    }

}
