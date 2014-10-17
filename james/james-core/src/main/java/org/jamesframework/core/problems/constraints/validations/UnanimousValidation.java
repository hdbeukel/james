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

import java.util.LinkedHashMap;
import java.util.Map;
import org.jamesframework.core.problems.constraints.Validation;

/**
 * A unanimous validation object is used to combine validations produced by a number of constraints.
 * A solution passes a unanimous validation if and only if it passes each contained validation.
 * 
 * @author <a href="mailto:herman.debeukelaer@ugent.be">Herman De Beukelaer</a>
 */
public class UnanimousValidation implements Validation {

    // contained validations
    private Map<Object, Validation> validations;
    
    // cached value
    private Boolean cachedValue = null;

    /**
     * Create an empty unanimous validation object. Actual underlying validations can be added
     * later by calling {@link #addValidation(Object, Validation)}.
     */
    public UnanimousValidation() {
        validations = null;
    }
    
    /**
     * Private method to initialize the validation map if not yet initialized.
     */
    private void initMapOnce(){
        if(validations == null){
            // use custom initial capacity as map is expected to
            // contain few items (in most cases only a single item)
            validations = new LinkedHashMap<>(1);
        }
    }
    
    /**
     * Add a validation object. A key is required that can be used to retrieve the validation object.
     * 
     * @param key key used to retrieve the validation object
     * @param validation validation object
     */
    public void addValidation(Object key, Validation validation){
        initMapOnce();
        validations.put(key, validation);
        // invalidate cache
        cachedValue = null;
    }
    
    /**
     * Retrieve the validation object corresponding to the given key.
     * If no validation with this key has been added, <code>null</code>
     * is returned.
     * 
     * @param key key specified when adding the validation
     * @return retrieved validation object, <code>null</code> if no validation with this key was added
     */
    public Validation getValidation(Object key){
        return validations == null ? null : validations.get(key);
    }

    /**
     * A unanimous validation passed if and only if all contained validations passed.
     * The result is cached so that it only needs to be computed when it is retrieved
     * for the first time.
     * 
     * @return <code>true</code> if all contained validations passed
     */
    @Override
    public boolean passed() {
        if(cachedValue == null){
            cachedValue = validations == null ? true : validations.values().stream().allMatch(Validation::passed);
        }
        return cachedValue;
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
