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

package org.jamesframework.examples.clique;

import org.jamesframework.core.problems.ProblemWithData;
import org.jamesframework.core.problems.objectives.Objective;

/**
 * Custom problem that uses the optimized CliqueSolution as its solution type.
 * 
 * @author <a href="mailto:herman.debeukelaer@ugent.be">Herman De Beukelaer</a>
 */
public class CliqueProblem extends ProblemWithData<CliqueSolution, CliqueData>{

    public CliqueProblem(Objective<? super CliqueSolution, ? super CliqueData> objective, CliqueData data) {
        super(objective, data);
    }

    @Override
    public CliqueSolution createRandomSolution() {
        throw new UnsupportedOperationException("Creating a random clique is not supported.");
    }

}
