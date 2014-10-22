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

package org.jamesframework.test.stubs;

import org.jamesframework.core.problems.constraints.Constraint;
import org.jamesframework.core.problems.Solution;
import org.jamesframework.core.problems.constraints.validations.Validation;
import org.jamesframework.core.problems.constraints.validations.SimpleValidation;

/**
 * Constraint stub which is never satisfied for any solution. Data is ignored. Only used for testing.
 * 
 * @author <a href="mailto:herman.debeukelaer@ugent.be">Herman De Beukelaer</a>
 */
public class NeverSatisfiedConstraintStub implements Constraint<Solution, Object>{

    private static final Validation VIOLATED = new SimpleValidation(false);
    
    /**
     * All solutions are invalid.
     * 
     * @param solution ignored
     * @param data ignored
     * @return a simple validation that is never passed
     */
    @Override
    public Validation validate(Solution solution, Object data) {
        return VIOLATED;
    }

}
