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

import java.util.Map;
import org.jamesframework.core.problems.constraints.PenalizingConstraint;
import org.jamesframework.core.problems.constraints.PenalizingValidation;
import org.jamesframework.core.problems.objectives.Evaluation;

/**
 * <p>
 * A penalized evaluation consists of an original evaluation and a number of penalizing validations.
 * The final double value is computed by subtracting or adding the assigned penalties from/to the
 * original evaluation, depending on whether evaluations are being maximized or minimized, respectively.
 * </p>
 * <p>
 * The object keeps track of which validations were produced by which penalizing constraints so that
 * these validation objects can be retrieved to perform delta validations.
 * </p>
 * 
 * @author <a href="mailto:herman.debeukelaer@ugent.be">Herman De Beukelaer</a>
 */
public class PenalizedEvaluation implements Evaluation {

    // original evaluation
    private final Evaluation evaluation;
    // penalizing validations
    private final Map<PenalizingConstraint<?,?>, PenalizingValidation> penalties;
    // indicates whether evaluations are maximized or minimized
    private final boolean minimizing;
    
    // cached value
    private Double cachedValue = null;
    
    /**
     * Create a new penalized evaluation. The penalties should be given as a map that maps
     * each penalizing constraint on the produced validation object. If <code>minimizing</code>
     * is <code>true</code>, penalties are added to the original evaluation, else they are
     * subtracted from it.
     * 
     * @param evaluation original evaluation
     * @param penalties validation objects produced by penalizing constraints
     * @param minimizing <code>true</code> if evaluations are minimized
     */
    public PenalizedEvaluation(Evaluation evaluation,
                               Map<PenalizingConstraint<?,?>, PenalizingValidation> penalties,
                               boolean minimizing){
        this.evaluation = evaluation;
        this.penalties = penalties;
        this.minimizing = minimizing;
    }
    
    /**
     * Get the validation object that was produced by a specific penalizing constraint.
     * 
     * @param constraint penalizing constraint
     * @return produced validation object
     */
    public PenalizingValidation getValidation(PenalizingConstraint<?,?> constraint){
        return penalties.get(constraint);
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

}
