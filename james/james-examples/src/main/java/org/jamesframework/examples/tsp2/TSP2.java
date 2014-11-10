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

package org.jamesframework.examples.tsp2;

import java.io.FileNotFoundException;
import java.text.DecimalFormat;
import java.util.Set;
import java.util.concurrent.TimeUnit;
import org.jamesframework.core.problems.objectives.evaluations.Evaluation;
import org.jamesframework.core.search.LocalSearch;
import org.jamesframework.core.search.algo.ParallelTempering;
import org.jamesframework.core.search.algo.RandomDescent;
import org.jamesframework.core.search.stopcriteria.MaxRuntime;
import org.jamesframework.examples.util.ProgressSearchListener;
import org.jamesframework.ext.permutation.PermutationProblem;
import org.jamesframework.ext.permutation.PermutationSolution;
import org.jamesframework.ext.permutation.neigh.ReverseSubsequenceNeighbourhood;

/**
 * Main class for the travelling salesman example (example 4B).
 * This version uses the predefined components for permutation problems
 * from the James extensions module.
 * 
 * @author <a href="mailto:herman.debeukelaer@ugent.be">Herman De Beukelaer</a>
 */
public class TSP2 {
    
    /**
     * Solves a (symmetric) travelling salesman problem. Expects two parameters: (1) the input file path and
     * (2) the runtime limit (in seconds). The input is specified in a text file in which the first row contains
     * a single integer value indicating the number of cities. The remainder of the file contains the entries of
     * the lower triangular part of a symmetric distance matrix (row-wise without diagonal entries), separated
     * by whitespace and/or newlines.
     * 
     * @param args array containing the input file path and runtime limit
     */
    public static void main(String[] args) {
        System.out.println("###################################");
        System.out.println("# TRAVELLING SALESMAN PROBLEM (2) #");
        System.out.println("###################################");
        // parse arguments
        if(args.length != 2){
            System.err.println("Usage: java -cp james-examples.jar org.jamesframework.examples.tsp2.TSP2 <inputfile> <runtime>");
            System.exit(1);
        }
        String filePath = args[0];
        int timeLimit = Integer.parseInt(args[1]);
        run(filePath, timeLimit);
    }
    
    private static void run(String filePath, int timeLimit){
        
        /***************/
        /* PARSE INPUT */
        /***************/
        
        System.out.println("# PARSING INPUT");
        System.out.println("Reading file: " + filePath);
        
        try {
            
            /*************************/
            /* PROBLEM SPECIFICATION */
            /*************************/
            
            // read TSP data from file
            TSPData data = new TSPFileReader2().read(filePath);
            // create objective
            TSPObjective obj = new TSPObjective();
            
            // wrap data and objective in permutation problem
            PermutationProblem<TSPData> problem = new PermutationProblem<>(obj, data);
                    
            System.out.println("# OPTIMIZING TSP ROUND TRIP");

            System.out.println("Number of cities: " + data.getIDs().size());
            System.out.println("Time limit: " + timeLimit + " seconds");
            
            /******************/
            /* RANDOM DESCENT */
            /******************/

            System.out.println("# RANDOM DESCENT");
            
            // create random descent search with neighbourhood that reverses a subsequence (2-opt move)
            LocalSearch<PermutationSolution> randomDescent = new RandomDescent<>(problem, new ReverseSubsequenceNeighbourhood());
            // set maximum runtime
            randomDescent.addStopCriterion(new MaxRuntime(timeLimit, TimeUnit.SECONDS));
            // attach listener
            randomDescent.addSearchListener(new ProgressSearchListener());

            // start search
            randomDescent.start();
            
            // print results
            Evaluation randomDescentBestEval = null;
            if(randomDescent.getBestSolution() != null){
                System.out.println("Best round trip: "
                                        + randomDescent.getBestSolution().getOrder());
                randomDescentBestEval = randomDescent.getBestSolutionEvaluation();
                System.out.println("Best round trip travel distance: "
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
            
            // create parallel tempering search with with neighbourhood that reverses a subsequence (2-opt move)
            double minTemp = 0.001;
            double maxTemp = 0.1;
            int numReplicas = 10;
            ParallelTempering<PermutationSolution> parallelTempering = new ParallelTempering<>(
                                                                        problem,
                                                                        new ReverseSubsequenceNeighbourhood(),
                                                                        numReplicas, minTemp, maxTemp
                                                                       );
            // scale temperatures according to average travel distance between cities
            double scale = computeAverageTravelDistance(data);
            parallelTempering.setTemperatureScaleFactor(scale);
            
            // set maximum runtime
            parallelTempering.addStopCriterion(new MaxRuntime(timeLimit, TimeUnit.SECONDS));
            // attach listener
            parallelTempering.addSearchListener(new ProgressSearchListener());

            // start search
            parallelTempering.start();
            
            // print results
            Evaluation ptBestEval = null;
            if(parallelTempering.getBestSolution() != null){
                System.out.println("Best round trip: "
                                        + parallelTempering.getBestSolution().getOrder());
                ptBestEval = parallelTempering.getBestSolutionEvaluation();
                System.out.println("Best round trip travel distance: "
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

            System.out.println("Number of cities: " + data.getIDs().size());
            System.out.println("Time limit: " + timeLimit + " seconds");
            System.out.println("---------------------------------------");

            DecimalFormat df = new DecimalFormat("0.0");
            System.out.format("%20s    %15s \n", "", "Travel distance");
            System.out.format("%20s    %15s \n",
                                "Random descent:",
                                randomDescentBestEval != null ? df.format(randomDescentBestEval.getValue()) : "-");
            System.out.format("%20s    %15s \n",
                                "Parallel tempering:",
                                ptBestEval != null ? df.format(ptBestEval.getValue()) : "-");
            System.out.println("---------------------------------------");
            
        } catch (FileNotFoundException ex) {
            System.err.println("Failed to read file: " + filePath);
            System.exit(2);
        }
        
    }
    
    private static double computeAverageTravelDistance(TSPData data){
        Set<Integer> ids = data.getIDs();
        double sum = 0.0;
        for(int id1 : ids){
            for(int id2 : ids) {
                if(id1 != id2){
                    sum += data.getDistance(id1, id2);
                }
            }
        }
        int n = ids.size();
        int numDistances = n*(n-1);
        return sum/numDistances;
    }
    
}
