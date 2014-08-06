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

import org.jamesframework.core.problems.constraints.PenalizingConstraint;
import org.jamesframework.core.problems.Solution;

/**
 * Penalizing constraint stub that is satisfied for any solution. Data is ignored. Only used for testing.
 * 
 * @author <a href="mailto:herman.debeukelaer@ugent.be">Herman De Beukelaer</a>
 */
public class AlwaysSatisfiedPenalizingConstraintStub
                                extends AlwaysSatisfiedConstraintStub
                                implements PenalizingConstraint<Solution, Object> {

    /**
     * Always return 0, regardless of the solution or data.
     * 
     * @param solution ignored
     * @param data ignored
     * @return 0.0
     */
    @Override
    public double computePenalty(Solution solution, Object data) {
        return 0.0;
    }

}
