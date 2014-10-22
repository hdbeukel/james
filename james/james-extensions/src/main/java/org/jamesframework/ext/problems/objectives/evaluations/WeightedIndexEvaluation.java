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

package org.jamesframework.ext.problems.objectives.evaluations;

import java.util.LinkedHashMap;
import java.util.Map;
import org.jamesframework.core.problems.objectives.evaluations.Evaluation;
import org.jamesframework.core.problems.objectives.Objective;

/**
 * Evaluation produced by a weighted index. Contains evaluations produced by the underlying
 * objectives and the respective weights. The double value is computed as a weighted sum
 * of the double values of all contained objectives.
 * 
 * @author <a href="mailto:herman.debeukelaer@ugent.be">Herman De Beukelaer</a>
 */
public class WeightedIndexEvaluation implements Evaluation {
    
    // objectives' evaluations and respective weights
    private final Map<Objective, WeightedEvaluation> evaluations;

    // cached value
    private Double cachedValue = null;
    
    /**
     * Create an empty evaluation.
     */
    public WeightedIndexEvaluation() {
        evaluations = new LinkedHashMap<>();
    }
    
    /**
     * Add an evaluation produced by an objective and specify its weight.
     * 
     * @param obj objective
     * @param eval produced evaluation
     * @param weight assigned weight
     */
    public void addEvaluation(Objective obj, Evaluation eval, double weight){
        evaluations.put(obj, new WeightedEvaluation(eval, weight));
        // invalidate cache
        cachedValue = null;
    }
    
    /**
     * Get a previously added evaluation produced by a specific objective.
     * If no evaluation has been added with this objective as its key,
     * <code>null</code> is returned.
     * 
     * @param obj objective
     * @return evaluation produced by this objective, may be <code>null</code>
     */
    public Evaluation getEvaluation(Objective obj){
        return evaluations.get(obj).getEval();
    }

    /**
     * The value consist of the weighted sum of the values of all added evaluations.
     * The result is cached so that it only needs to be computed when it is retrieved
     * for the first time.
     * 
     * @return weighted sum of values
     */
    @Override
    public double getValue() {
        if(cachedValue == null){
            cachedValue = evaluations.values().stream().mapToDouble(WeightedEvaluation::getWeightedValue).sum();
        }
        return cachedValue;
    }
    
    /**
     * Wraps an evaluation and weight.
     */
    private class WeightedEvaluation {
        
        private final Evaluation eval;
        private final double weight;

        public WeightedEvaluation(Evaluation eval, double weight) {
            this.eval = eval;
            this.weight = weight;
        }

        public Evaluation getEval() {
            return eval;
        }

        public double getWeight() {
            return weight;
        }
        
        public double getWeightedValue(){
            return weight * eval.getValue();
        }
        
    }

}
