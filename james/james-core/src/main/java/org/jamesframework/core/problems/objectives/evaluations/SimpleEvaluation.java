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

import org.jamesframework.core.problems.objectives.Evaluation;

/**
 * A simple evaluation object that wraps a double value.
 * 
 * @author <a href="mailto:herman.debeukelaer@ugent.be">Herman De Beukelaer</a>
 */
public class SimpleEvaluation implements Evaluation {

    // contained double value
    private final double value;

    /**
     * Create a simple evaluation with given double value.
     * 
     * @param value double value
     */
    public SimpleEvaluation(double value) {
        this.value = value;
    }
    
    /**
     * Get the double value specified at construction.
     * 
     * @return double value
     */
    @Override
    public double getValue() {
        return value;
    }
    
    /**
     * Get a string representation of the assigned value.
     * 
     * @return value converted to a string
     */
    @Override
    public String toString(){
        return value + "";
    }

}
