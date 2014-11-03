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

package org.jamesframework.examples.coresubset3;

import java.io.FileNotFoundException;
import java.text.DecimalFormat;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;
import org.jamesframework.core.problems.objectives.evaluations.Evaluation;
import org.jamesframework.core.search.LocalSearch;
import org.jamesframework.core.search.algo.ParallelTempering;
import org.jamesframework.core.subset.SubsetProblem;
import org.jamesframework.core.subset.SubsetSolution;
import org.jamesframework.core.search.algo.RandomDescent;
import org.jamesframework.core.subset.neigh.SingleSwapNeighbourhood;
import org.jamesframework.core.search.stopcriteria.MaxRuntime;
import org.jamesframework.examples.coresubset.CoreSubsetData;
import org.jamesframework.examples.coresubset.CoreSubsetFileReader;
import org.jamesframework.examples.util.ProgressSearchListener;

/**
 * Main class for the core subset selection example with a different objective function (example 1C).
 * This objective uses a custom evaluation object to track metadata used for efficient delta evaluation.
 * 
 * @author <a href="mailto:herman.debeukelaer@ugent.be">Herman De Beukelaer</a>
 */
public class CoreSubset3 {
    
    /**
     * Runs the core subset selection problem. Expects three parameters: (1) the input file path, (2) the desired
     * core subset size and (3) the runtime limit (in seconds). The input is specified in a CSV file in which the
     * first row (header) lists the N item names and the subsequent N rows describe a symmetric (N x N) distance matrix.
     * The distance matrix indicates the distance between each pair of items, where the rows follow the same order as
     * the columns, as indicated by the header row.
     * 
     * @param args array containing the input file path, subset size and runtime limit
     */
    public static void main(String[] args) {
        System.out.println("###########################################################");
        System.out.println("# CORE SUBSET SELECTION WITH DIFFERENT OBJECTIVE FUNCTION #");
        System.out.println("###########################################################");
        // parse arguments
        if(args.length != 3){
            System.err.println("Usage: java -cp james-examples.jar org.jamesframework.examples.coresubset3.CoreSubset3 <inputfile> <subsetsize> <runtime>");
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
        
            System.out.println("# SAMPLING CORE SUBSET");

            System.out.println("Dataset size: " + data.getIDs().size());
            System.out.println("Subset size: " + subsetSize);
            System.out.println("Time limit: " + timeLimit + " seconds");
            
            /**********************/
            /* INITIALIZE PROBLEM */
            /**********************/
            
            // create objective
            EntryToNearestEntryObjective obj = new EntryToNearestEntryObjective();
            // create subset problem
            SubsetProblem<CoreSubsetData> problem = new SubsetProblem<>(obj, data, subsetSize);
            
            /******************/
            /* RANDOM DESCENT */
            /******************/

            System.out.println("# RANDOM DESCENT");
            
            // create random descent search with single swap neighbourhood
            LocalSearch<SubsetSolution> randomDescent = new RandomDescent<>(problem, new SingleSwapNeighbourhood());
            // set maximum runtime
            randomDescent.addStopCriterion(new MaxRuntime(timeLimit, TimeUnit.SECONDS));
            // attach listener
            randomDescent.addSearchListener(new ProgressSearchListener());

            // start search
            randomDescent.start();
            
            // print results
            Evaluation randomDescentBestEval = null;
            if(randomDescent.getBestSolution() != null){
                System.out.println("Best solution (IDs): "
                                        + randomDescent.getBestSolution().getSelectedIDs());
                System.out.println("Best solution (names): "
                                        + randomDescent.getBestSolution().getSelectedIDs()
                                                                  .stream()
                                                                  .map(data::getName)
                                                                  .collect(Collectors.toSet()));
                randomDescentBestEval = randomDescent.getBestSolutionEvaluation();
                System.out.println("Best solution evaluation: "
                                        + randomDescentBestEval);
            } else {
                System.out.println("No valid solution found...");
            }

            // dispose
            randomDescent.dispose();
            
            /**********************/
            /* PARALLEL TEMPERING */
            /**********************/
            
            System.out.println("# PARALLEL TEMPERING");
            
            // create parallel tempering search with single swap neighbourhood
            double minTemp = 0.00001;
            double maxTemp = 0.01;
            LocalSearch<SubsetSolution> parallelTempering = new ParallelTempering<>(problem,
                                                                        new SingleSwapNeighbourhood(),
                                                                        10, minTemp, maxTemp);
            // set maximum runtime
            parallelTempering.addStopCriterion(new MaxRuntime(timeLimit, TimeUnit.SECONDS));
            // attach listener
            parallelTempering.addSearchListener(new ProgressSearchListener());

            // start search
            parallelTempering.start();
            
            // print results
            Evaluation ptBestEval = null;
            if(parallelTempering.getBestSolution() != null){
                System.out.println("Best solution (IDs): "
                                        + parallelTempering.getBestSolution().getSelectedIDs());
                System.out.println("Best solution (names): "
                                        + parallelTempering.getBestSolution().getSelectedIDs()
                                                                  .stream()
                                                                  .map(data::getName)
                                                                  .collect(Collectors.toSet()));
                ptBestEval = parallelTempering.getBestSolutionEvaluation();
                System.out.println("Best solution evaluation: "
                                        + ptBestEval);
            } else {
                System.out.println("No valid solution found...");
            }

            // dispose
            parallelTempering.dispose();
            
            /***********/
            /* SUMMARY */
            /***********/

            System.out.println("---------------------------------------");
            System.out.println("Summary:");
            System.out.println("---------------------------------------");

            System.out.println("Dataset size: " + data.getIDs().size());
            System.out.println("Subset size: " + subsetSize);
            System.out.println("Time limit: " + timeLimit + " seconds");
            System.out.println("---------------------------------------");

            DecimalFormat df = new DecimalFormat("0.000");
            System.out.format("%20s    %13s \n", "", "Best solution");
            System.out.format("%20s    %13s \n",
                                "Random descent:",
                                randomDescentBestEval != null ? df.format(randomDescentBestEval.getValue()) : "-");
            System.out.format("%20s    %13s \n",
                                "Parallel tempering:",
                                ptBestEval != null ? df.format(ptBestEval.getValue()) : "-");
            System.out.println("---------------------------------------");
            
        } catch (FileNotFoundException ex) {
            System.err.println("Failed to read file: " + filePath);
            System.exit(2);
        }
        
    }
    
}
