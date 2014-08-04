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

package org.jamesframework.examples.knapsack;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.Scanner;

/**
 * Reads an input file for the knapsack problem (plain text). The first row contains a single number N that indicates
 * the number of available knapsack items. The next N rows each contain the profit and weight (in this order) of a
 * single item, separated by one or more spaces.
 * 
 * @author <a href="mailto:herman.debeukelaer@ugent.be">Herman De Beukelaer</a>
 */
public class KnapsackFileReader {
    
    public KnapsackData read(String filePath) throws FileNotFoundException{
        Scanner sc = new Scanner(new File(filePath));
        // read number of available items
        int n = sc.nextInt();
        // read all item profits and weights
        double[] profits = new double[n];
        double[] weights = new double[n];
        int i=0;
        while(sc.hasNext()){
            // read profit
            double profit = sc.nextDouble();
            // read weight
            double weight = sc.nextDouble();
            // store
            profits[i] = profit;
            weights[i] = weight;
            // next item
            i++;
        }
        // create and return data object
        return new KnapsackData(weights, profits);
    }
    
}
