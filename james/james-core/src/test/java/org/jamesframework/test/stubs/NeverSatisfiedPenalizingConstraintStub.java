//  Copyright 2014 Herman De Beukelaer
//
//  Licensed under the Apache License, Version 2.0 (the "License");
//  you may not use this file except in compliance with the License.
//  You may obtain a copy of the License at
//
//      http://www.apache.org/licenses/LICENSE-2.0
//
//  Unless required by applicable law or agreed to in writing, software
//  distributed under the License is distributed on an "AS IS" BASIS,
//  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
//  See the License for the specific language governing permissions and
//  limitations under the License.

package org.jamesframework.test.stubs;

import org.jamesframework.core.problems.constraints.PenalizingConstraint;
import org.jamesframework.core.problems.solutions.Solution;

/**
 * Penalizing constraint stub that is never satisfied for any solution. A fixed penalty is returned for any solution.
 * Data is ignored. Only used for testing.
 * 
 * @author <a href="mailto:herman.debeukelaer@ugent.be">Herman De Beukelaer</a>
 */
public class NeverSatisfiedPenalizingConstraintStub
                                extends NeverSatisfiedConstraintStub
                                implements PenalizingConstraint<Solution, Object> {

    private final double fixedPenalty;
    
    /**
     * Create a penalizing constraint stub that is never satisfied and always returns the given fixed penalty.
     * 
     * @param fixedPenalty fixed penalty (strictly positive)
     * @throws IllegalArgumentException in case the given penalty is not strictly positive
     */
    public NeverSatisfiedPenalizingConstraintStub(double fixedPenalty) {
        if(fixedPenalty <= 0.0){
            throw new IllegalArgumentException("Fixed penalty should be > 0.0.");
        }
        this.fixedPenalty = fixedPenalty;
    }
    
    /**
     * Always return a fixed positive penalty, regardless of the solution or data.
     * 
     * @param solution ignored
     * @param data ignored
     * @return fixed penalty (strictly positive)
     */
    @Override
    public double computePenalty(Solution solution, Object data) {
        return fixedPenalty;
    }

}
