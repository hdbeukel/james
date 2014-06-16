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

import java.util.HashSet;
import java.util.Set;
import org.jamesframework.core.problems.datatypes.SubsetData;

/**
 * Provides the data for the knapsack problem by specifying the weight and profit of each item.
 * 
 * @author <a href="mailto:herman.debeukelaer@ugent.be">Herman De Beukelaer</a>
 */
public class KnapsackData implements SubsetData {

    // weights
    private final double[] weights;
    // profits
    private final double[] profits;
    // IDs (indices in weight and profit arrays)
    private final Set<Integer> ids;
    
    public KnapsackData(double[] weights, double[] profits){
        // store data
        this.weights = weights;
        this.profits = profits;
        // infer IDs: 0..N-1 in case of N items
        // (indices in weight and profit arrays)
        ids = new HashSet<>();
        for(int id=0; id<weights.length; id++){
            ids.add(id);
        }
    }
    
    @Override
    public Set<Integer> getIDs() {
        return ids;
    }
    
    public double getWeight(int id){
        return weights[id];
    }
    
    public double getProfit(int id){
        return profits[id];
    }

}
