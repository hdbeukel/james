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

package org.jamesframework.test.util;

import org.jamesframework.core.problems.constraints.Constraint;
import org.jamesframework.core.problems.solutions.Solution;

/**
 * Constraint stub which is never satisfied for any solution. Data is ignored. Only used for testing.
 * 
 * @author Herman De Beukelaer <herman.debeukelaer@ugent.be>
 */
public class NeverSatisfiedConstraintStub implements Constraint<Solution, Object>{

    /**
     * Always returns false, regardless of the solution and data.
     * 
     * @param solution ignored
     * @param data ignored
     * @return false
     */
    @Override
    public boolean isSatisfied(Solution solution, Object data) {
        return false;
    }

}
