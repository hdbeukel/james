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

import java.util.HashMap;
import java.util.Map;
import org.jamesframework.core.problems.objectives.Evaluation;
import org.jamesframework.core.problems.objectives.Objective;

/**
 * Evaluation produced by a weighted index. Contains evaluations produced by the underlying
 * objectives and the respective weights. The double value is computed as a weighted sum
 * of the contained objectives' double values where the sign of minimizing objectives
 * is flipped because a weighted index is always maximized.
 * 
 * @author <a href="mailto:herman.debeukelaer@ugent.be">Herman De Beukelaer</a>
 */
public class WeightedIndexEvaluation implements Evaluation {
    
    // objectives' evaluations and respective weights
    private final Map<Objective, WeightedEvaluation> evaluations;

    /**
     * Create an empty evaluation.
     */
    public WeightedIndexEvaluation() {
        evaluations = new HashMap<>();
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
    }
    
    /**
     * Get a previously added evaluation produced by a specific objective.
     * 
     * @param obj objective
     * @return evaluation produced by this objective, may be <code>null</code>
     */
    public Evaluation getEvaluation(Objective obj){
        return evaluations.get(obj).getEval();
    }

    /**
     * The value consist of the weighted sum of the values of all added evaluations.
     * 
     * @return weighted sum of values
     */
    @Override
    public double getValue() {
        return evaluations.values().stream().mapToDouble(we -> we.getWeightedValue()).sum();
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
