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

import java.util.HashMap;
import java.util.Map;
import org.jamesframework.core.problems.constraints.Validation;

/**
 * A unanimous validation object is used to combine validations produced by a number of constraints.
 * A solution passes unanimous validation if and only if it passes each contained validation.
 * 
 * @author <a href="mailto:herman.debeukelaer@ugent.be">Herman De Beukelaer</a>
 */
public class UnanimousValidation implements Validation {

    // contained validations
    private final Map<Object, Validation> validations;

    /**
     * Create an empty unanimous validation object. Actual validations can be added
     * later by calling {@link #}
     */
    public UnanimousValidation() {
        validations = new HashMap<>();
    }
    
    /**
     * Add a validation object. A key is required that can be used to retrieve the validation object.
     * 
     * @param key key used to retrieve the validation object
     * @param validation validation object
     */
    public void addValidation(Object key, Validation validation){
        validations.put(key, validation);
    }
    
    /**
     * Retrieve the validation object corresponding to the given key.
     * 
     * @param key key specified when adding the validation
     * @return retrieved validation object
     */
    public Validation getValidation(Object key){
        return validations.get(key);
    }

    /**
     * A unanimous validation passed if and only if all contained validations passed.
     * 
     * @return <code>true</code> if all contained validations passed
     */
    @Override
    public boolean passed() {
        return validations.values().stream().allMatch(Validation::passed);
    }

}
