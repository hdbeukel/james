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

package org.jamesframework.examples.knapsack2;

import org.jamesframework.examples.knapsack.*;
import java.io.FileNotFoundException;
import java.util.Comparator;
import java.util.Random;
import java.util.concurrent.TimeUnit;
import org.jamesframework.core.problems.Problem;
import org.jamesframework.core.problems.objectives.evaluations.Evaluation;
import org.jamesframework.core.subset.SubsetProblem;
import org.jamesframework.core.subset.SubsetSolution;
import org.jamesframework.core.search.algo.ParallelTempering;
import org.jamesframework.core.search.algo.RandomDescent;
import org.jamesframework.core.subset.neigh.SinglePerturbationNeighbourhood;
import org.jamesframework.core.search.stopcriteria.MaxRuntime;
import org.jamesframework.core.util.SetUtilities;
import org.jamesframework.examples.util.ProgressSearchListener;

/**
 * Main class for the knapsack example (2).
 * 
 * @author <a href="mailto:herman.debeukelaer@ugent.be">Herman De Beukelaer</a>
 */
public class KnapSack2 {
    
    // random generator
    private static final Random RG = new Random();
    
    /**
     * Runs the knapsack problem. Expects three parameters: (1) the input file path, (2) the capacity of the knapsack
     * and (3) the runtime limit (in seconds). The input is specified in a text file in which the first row contains
     * a single number N that indicates number of available knapsack items. The next N rows each contain the profit
     * and weight (in this order) of a single item, separated by one or more spaces.
     * 
     * @param args array containing the input file path, knapsack capacity and runtime limit
     */
    public static void main(String[] args) {
        System.out.println("########################");
        System.out.println("# KNAPSACK PROBLEM (2) #");
        System.out.println("########################");
        // parse arguments
        if(args.length != 3){
            System.err.println("Usage: java -cp james-examples.jar org.jamesframework.examples.knapsack2.KnapSack2 <inputfile> <capacity> <runtime>");
            System.exit(1);
        }
        String filePath = args[0];
        double capacity = Double.parseDouble(args[1]);
        int timeLimit = Integer.parseInt(args[2]);
        run(filePath, capacity, timeLimit);
    }
    
    private static void run(String filePath, double capacity, int timeLimit){
        
        /***************/
        /* PARSE INPUT */
        /***************/
        
        System.out.println("# PARSING INPUT");
        System.out.println("Reading file: " + filePath);
        
        try {
            
            KnapsackData data = new KnapsackFileReader().read(filePath);
        
            /*********************/
            /* OPTIMIZE KNAPSACK */
            /*********************/

            System.out.println("# OPTIMIZING KNAPSACK");

            System.out.println("Dataset size: " + data.getIDs().size());
            System.out.println("Knapsack capacity: " + capacity);
            System.out.println("Time limit: " + timeLimit + " seconds");

            // create objective
            KnapsackObjective obj = new KnapsackObjective();
                        
            // create penalizing constraint
            double highestProfitPerItem = computeHighestProfit(data);
            PenalizingKnapsackConstraint constraint = new PenalizingKnapsackConstraint(capacity, highestProfitPerItem);
            // create subset problem (all sizes allowed)
            // IMPORTANT: IDs are ordered based on the weight of the corresponding item,
            //            as required by the applied penalizing constraint
            SubsetProblem<KnapsackData> problem = new SubsetProblem<>(
                                                        obj, data,
                                                        0, data.getIDs().size(),
                                                        Comparator.comparing(data::getWeight).reversed()
                                                  );
            // add penalizing constraint
            problem.addPenalizingConstraint(constraint);
           
            /**********************/
            /* PARALLEL TEMPERING */
            /**********************/
            
            System.out.println("# PARALLEL TEMPERING");
            
            // create parallel tempering with single perturbation neighbourhood (10 replicas)
            double minTemp = 0.001;
            double maxTemp = 0.1;
            int numReplicas = 10;
            ParallelTempering<SubsetSolution> search = new ParallelTempering<>(problem,
                                                                        new SinglePerturbationNeighbourhood(),
                                                                        numReplicas, minTemp, maxTemp);
            // scale temperatures according to average profit of knapsack items
            double scale = computeAverageProfit(data);
            search.setTemperatureScaleFactor(scale);
            System.out.println("Min. temperature: " + minTemp);
            System.out.println("Max. temperature: " + maxTemp);
            System.out.println("Temperature scale: " + scale);
            
            // set maximum runtime
            search.addStopCriterion(new MaxRuntime(timeLimit, TimeUnit.SECONDS));
            // attach listener
            search.addSearchListener(new ProgressSearchListener());
            // NOTE: not required to set an initial solution within the constraint (<-> mandatory constraint)
            
            // start search
            search.start();
            // print results
            if(search.getBestSolution() != null){
                System.out.println("Items in knapsack: "
                                        + search.getBestSolution().getNumSelectedIDs() + "/" + data.getIDs().size());
                System.out.println("Total profit: "
                                        + search.getBestSolutionEvaluation());
                System.out.println("Total weight: "
                                        + computeSelectionWeight(search.getBestSolution(), data) + "/" + capacity);
                System.out.println("Constraint satisifed: "
                                        + (problem.getViolatedConstraints(search.getBestSolution()).isEmpty() ? "yes" : "no"));
            } else {
                System.out.println("No valid solution found...");
            }
            // dispose search
            search.dispose();
            
        } catch (FileNotFoundException ex) {
            System.err.println("Failed to read file: " + filePath);
            System.exit(2);
        }
        
    }
    
    private static double computeSelectionWeight(SubsetSolution solution, KnapsackData data){
        return solution.getSelectedIDs().stream().mapToDouble(data::getWeight).sum();
    }
    
    private static double computeAverageProfit(KnapsackData data){
        return data.getIDs().stream().mapToDouble(data::getProfit).average().getAsDouble();
    }
    
    private static double computeHighestProfit(KnapsackData data){
        return data.getIDs().stream().mapToDouble(data::getProfit).max().getAsDouble();
    }
    
}
