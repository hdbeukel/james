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

package org.jamesframework.core.problems.constraints;

import org.jamesframework.core.problems.solutions.Solution;

/**
 * Interface of a constraint that can be imposed on the solutions, based on underlying data.
 * 
 * @author Herman De Beukelaer <herman.debeukelaer@ugent.be>
 * @param <SolutionType> solution type to which the constraint is applied, required to extend {@link Solution}
 * @param <DataType> underlying data type
 */
public interface Constraint<SolutionType extends Solution, DataType> {

    /**
     * Checks whether a given solution satisfies the imposed constraint, based on
     * the given underlying data.
     * 
     * @param solution solution to check
     * @param data underlying data
     * @return true if the constraint is satisfied for the given solution
     */
    public boolean isSatisfied(SolutionType solution, DataType data);
    
}
