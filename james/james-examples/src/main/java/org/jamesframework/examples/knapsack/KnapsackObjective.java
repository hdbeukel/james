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

package org.jamesframework.examples.knapsack;

import org.jamesframework.core.problems.objectives.Objective;
import org.jamesframework.core.problems.solutions.SubsetSolution;

/**
 * Objective for the knapsack problem: maximize the total profit.
 * 
 * @author <a href="mailto:herman.debeukelaer@ugent.be">Herman De Beukelaer</a>
 */
public class KnapsackObjective implements Objective<SubsetSolution, KnapsackData>{

    @Override
    public double evaluate(SubsetSolution solution, KnapsackData data) {
        // compute sum of profits of selected items
        double profit = 0.0;
        for(int id : solution.getSelectedIDs()){
            profit += data.getProfit(id);
        }
        return profit;
    }

    @Override
    public boolean isMinimizing() {
        return false;
    }

}
