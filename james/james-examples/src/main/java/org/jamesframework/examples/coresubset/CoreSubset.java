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

package org.jamesframework.examples.coresubset;

import org.jamesframework.examples.util.ProgressionSearchListener;
import java.io.FileNotFoundException;
import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.TimeUnit;
import org.jamesframework.core.problems.SubsetProblemWithData;
import org.jamesframework.core.problems.solutions.SubsetSolution;
import org.jamesframework.core.search.algo.RandomDescent;
import org.jamesframework.core.search.neigh.subset.SingleSwapNeighbourhood;
import org.jamesframework.core.search.stopcriteria.MaxRuntime;

/**
 * Main class for the core subset selection example.
 * 
 * @author <a href="mailto:herman.debeukelaer@ugent.be">Herman De Beukelaer</a>
 */
public class CoreSubset {
    
    /**
     * Runs the core subset selection problem. Expects three parameters: (1) the input file path, (2) the desired
     * core subset size and (3) the runtime limit (in seconds). The input is specified in a csv file in which the
     * first row (header) lists the N item names and the subsequent N rows describe a symmetric (N x N) distance matrix.
     * The distance matrix indicates the distance between each pair of items, where the rows follow the same order as
     * the columns, as indicated by the header row.
     * 
     * @param args array containing the input file path, subset size and runtime limit
     */
    public static void main(String[] args) {
        System.out.println("#######################");
        System.out.println("# CORE SUBSET PROBLEM #");
        System.out.println("#######################");
        // parse arguments
        if(args.length != 3){
            System.err.println("Usage: java -cp james-examples.jar org.jamesframework.examples.coresubset.CoreSubset <inputfile> <subsetsize> <runtime>");
            System.exit(1);
        }
        String filePath = args[0];
        int subsetSize = Integer.parseInt(args[1]);
        int timeLimit = Integer.parseInt(args[2]);
        run(filePath, subsetSize, timeLimit);
    }
    
    private static void run(String filePath, int subsetSize, int timeLimit){
        
        /***************/
        /* PARSE INPUT */
        /***************/
        
        System.out.println("# PARSING INPUT");
        System.out.println("Reading file: " + filePath);
        
        try {
            
            CoreSubsetData data = new CoreSubsetFileReader().read(filePath);
        
            /**********************/
            /* SAMPLE CORE SUBSET */
            /**********************/

            System.out.println("# SAMPLING CORE SUBSET");

            System.out.println("Dataset size: " + data.getIDs().size());
            System.out.println("Subset size: " + subsetSize);
            System.out.println("Time limit: " + timeLimit + " seconds");

            // create objective
            CoreSubsetObjective obj = new CoreSubsetObjective();
            // create subset problem
            SubsetProblemWithData<CoreSubsetData> problem = new SubsetProblemWithData<>(obj, data, subsetSize);

            // create random descent search with single swap neighbourhood
            RandomDescent<SubsetSolution> search = new RandomDescent<>(problem, new SingleSwapNeighbourhood());
            // set maximum runtime
            search.addStopCriterion(new MaxRuntime(timeLimit, TimeUnit.SECONDS));
            // attach listener
            search.addSearchListener(new ProgressionSearchListener());

            // start search
            search.start();

            // print best solution and evaluation
            System.out.println("Best solution (IDs): " + search.getBestSolution().getSelectedIDs());
            System.out.println("Best solution (names): " + mapNames(search.getBestSolution().getSelectedIDs(), data));
            System.out.println("Best solution evaluation: " + search.getBestSolutionEvaluation());

            // dispose search
            search.dispose();
            
        } catch (FileNotFoundException ex) {
            System.err.println("Failed to read file: " + filePath);
            System.exit(2);
        }
        
    }
    
    private static Set<String> mapNames(Set<Integer> selected, CoreSubsetData data){
        Set<String> names = new HashSet<>();
        for(int ID : selected){
            names.add(data.getName(ID));
        }
        return names;
    }
    
}
