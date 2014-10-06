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

package org.jamesframework.core.problems.objectives.evaluations;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import org.jamesframework.core.problems.constraints.PenalizingValidation;
import org.jamesframework.core.problems.objectives.Evaluation;

/**
 * A penalized evaluation consists of an original evaluation and a number of penalizing validations.
 * The final double value is computed by subtracting or adding the assigned penalties from/to the
 * original evaluation, depending on whether evaluations are being maximized or minimized, respectively.
 * 
 * @author <a href="mailto:herman.debeukelaer@ugent.be">Herman De Beukelaer</a>
 */
public class PenalizedEvaluation implements Evaluation {

    // original evaluation
    private final Evaluation evaluation;
    // penalizing validations
    private Map<Object, PenalizingValidation> penalties;
    // indicates whether evaluations are maximized or minimized
    private final boolean minimizing;
    
    // cached value
    private Double cachedValue = null;
    
    /**
     * Create a new penalized evaluation, given the original evaluation. Penalties can be added
     * later by calling {@link #addPenalizingValidation(Object, PenalizingValidation)}. If
     * <code>minimizing</code> is <code>true</code>, penalties are added to the original
     * evaluation, else they are subtracted from it.
     * 
     * @param evaluation original evaluation
     * @param minimizing <code>true</code> if evaluations are minimized
     */
    public PenalizedEvaluation(Evaluation evaluation, boolean minimizing){
        this.evaluation = evaluation;
        this.minimizing = minimizing;
        this.penalties = new HashMap<>();
    }
    
    /**
     * Add a penalty expressed by a penalizing validation object. A key is
     * required that can be used to retrieve the validation object later.
     * 
     * @param key key used to retrieve the validation object later
     * @param penalty penalizing validation that indicates the assigned penalty
     */
    public void addPenalizingValidation(Object key, PenalizingValidation penalty){
        penalties.put(key, penalty);
    }
    
    /**
     * Add a collections of penalties. Each corresponding penalizing
     * validation object is assigned a key as indicated in the provided map.
     * 
     * @param penalties penalizing validations to add
     */
    public void addPenalizingValidations(Map<Object, PenalizingValidation> penalties){
        penalties.keySet().forEach(k -> addPenalizingValidation(k, penalties.get(k)));
    }
    
    /**
     * Retrieve the penalizing validation object corresponding to the given key.
     * 
     * @param key key specified when adding the penalizing validation
     * @return retrieved validation object
     */
    public PenalizingValidation getPenalizingValidation(Object key){
        return penalties.get(key);
    }
    
    /**
     * Get the collection of all keys specified when adding a penalizing validation object.
     * 
     * @return collection of used keys
     */
    public Collection<Object> getKeys(){
        return penalties.keySet();
    }
    
    /**
     * Get a map containing all added penalizing validation objects stored by the assigned keys.
     * 
     * @return map containing all penalizing validations
     */
    public Map<Object, PenalizingValidation> getValidations(){
        return penalties;
    }
    
    /**
     * Set a new penalty map, discarding any previously added penalizing validation objects.
     * 
     * @param penalties map of penalizing validation objects
     */
    public void setValidations(Map<Object, PenalizingValidation> penalties){
        this.penalties = penalties;
    }
    
    /**
     * Get the original unpenalized evaluation.
     * 
     * @return original evaluation
     */
    public Evaluation getEvaluation(){
        return evaluation;
    }

    /**
     * Compute the penalized evaluation. The result is cached so that it only needs
     * to be computed when it is retrieved for the first time.
     * 
     * @return penalized evaluation
     */
    @Override
    public double getValue() {
        if(cachedValue == null){
            double e = evaluation.getValue();
            double p = penalties.values().stream()
                                .mapToDouble(val -> val.getPenalty())
                                .sum();
            if(minimizing){
                e += p;
            } else {
                e -= p;
            }
            cachedValue = e;
        }
        return cachedValue;
    }
    
    /**
     * Get a string representation that contains both the penalized and original unpenalized evaluation.
     * 
     * @return string representation
     */
    @Override
    public String toString(){
        return getValue() + " (unpenalized: " + getEvaluation().getValue() + ")";
    }

}
