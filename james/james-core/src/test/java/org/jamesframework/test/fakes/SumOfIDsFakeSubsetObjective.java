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

package org.jamesframework.test.fakes;

import org.jamesframework.core.problems.objectives.MinMaxObjective;
import org.jamesframework.core.problems.solutions.SubsetSolution;

/**
 * A fake subset objective that ignores any given data, as it evaluates a subset solution by
 * calculating the sum of the selected IDs. Used for testing purposes only.
 * 
 * @author <a href="mailto:herman.debeukelaer@ugent.be">Herman De Beukelaer</a>
 */
public class SumOfIDsFakeSubsetObjective extends MinMaxObjective<SubsetSolution, Object> {

    /**
     * Evaluate subset solution to sum of selected IDs. Data is ignored.
     * 
     * @param solution subset solution to evaluate
     * @param data ignored
     * @return sum of selected IDs
     */
    @Override
    public double evaluate(SubsetSolution solution, Object data) {
        int idsum = 0;
        for(int id : solution.getSelectedIDs()){
            idsum += id;
        }
        return (double) idsum;
    }

}
