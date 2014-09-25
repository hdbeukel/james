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

package org.jamesframework.examples.clique;

import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;
import org.jamesframework.core.search.LocalSearch;
import org.jamesframework.core.subset.SubsetSolution;
import org.jamesframework.core.search.algo.RandomDescent;
import org.jamesframework.core.search.algo.vns.VariableNeighbourhoodSearch;
import org.jamesframework.core.search.neigh.Neighbourhood;
import org.jamesframework.core.search.stopcriteria.MaxRuntime;
import org.jamesframework.examples.util.ProgressSearchListener;

/**
 * Main class for the maximum clique example.
 * 
 * @author <a href="mailto:herman.debeukelaer@ugent.be">Herman De Beukelaer</a>
 */
public class MaximumClique {
    
    /**
     * Runs the maximum clique problem. Expects three parameters: (1) the input file path, (2) the maximum number
     * of deleted vertices by shaking in variable neighbourhood search and (3) the runtime limit (in seconds).
     * The input is specified in a text file where every row contains two vertex IDs (edge) separated by one or
     * more spaces.
     * 
     * @param args array containing the input file path, maximum amount of shaking and runtime limit
     */
    public static void main(String[] args) {
        System.out.println("##########################");
        System.out.println("# MAXIMUM CLIQUE PROBLEM #");
        System.out.println("##########################");
        // parse arguments
        if(args.length != 3){
            System.err.println("Usage: java -cp james-examples.jar org.jamesframework.examples.clique.MaximumClique <inputfile> <maxshake> <runtime>");
            System.exit(1);
        }
        String filePath = args[0];
        int maxShake = Integer.parseInt(args[1]);
        int timeLimit = Integer.parseInt(args[2]);
        run(filePath, maxShake, timeLimit);
    }
    
    private static void run(String filePath, int maxShake, int timeLimit){
        
        /***************/
        /* PARSE INPUT */
        /***************/
        
        System.out.println("# PARSING INPUT");
        System.out.println("Reading file: " + filePath);
        
        try {
            
            final CliqueData data = new CliqueFileReader().read(filePath);
        
            /***********************/
            /* FIND MAXIMUM CLIQUE */
            /***********************/

            System.out.println("# SEARCHING FOR MAXIMUM CLIQUE");

            System.out.println("Number of vertices: " + data.numVertices());
            System.out.println("Number of edges: " + data.numEdges());
            System.out.println("Time limit: " + timeLimit + " seconds");

            // create objective
            CliqueObjective obj = new CliqueObjective();
            // create clique problem
            CliqueProblem cliqueProblem = new CliqueProblem(obj, data);
            
            /******************/
            /* RANDOM DESCENT */
            /******************/
            
            System.out.println("# RANDOM DESCENT");
            
            // create random descent with optimized neighbourhood
            LocalSearch<CliqueSolution> randomDescent = new RandomDescent<>(cliqueProblem, new GreedyCliqueNeighbourhood2(data));

            // set maximum runtime
            randomDescent.addStopCriterion(new MaxRuntime(timeLimit, TimeUnit.SECONDS));
            // attach listener
            randomDescent.addSearchListener(new ProgressSearchListener());
            
            // IMPORTANT: start with empty clique
            randomDescent.setCurrentSolution(new CliqueSolution(data.getIDs(), data));

            // start search
            randomDescent.start();
            // print results
            int randomDescentCliqueSize = randomDescent.getBestSolution().getNumSelectedIDs();
            System.out.println("Clique size: " + randomDescentCliqueSize);
            // dispose search
            randomDescent.dispose();
           
            /*********************************/
            /* VARIABLE NEIGHBOURHOOD SEARCH */
            /*********************************/
            
            System.out.println("# VARIABLE NEIGHBOURHOOD SEARCH");
            
            // create shaking neighbourhoods
            List<Neighbourhood<SubsetSolution>> shakingNeighs = new ArrayList<>();
            for(int s=1; s <= maxShake; s++){
                shakingNeighs.add(new ShakingNeighbourhood(s));
            }
            // create variable neighbourhood search
            LocalSearch<CliqueSolution> vns = new VariableNeighbourhoodSearch<>(
                                                    cliqueProblem,
                                                    shakingNeighs,
                                                    // use random descent with optimized clique neighbourhood
                                                    problem -> new RandomDescent<>(problem, new GreedyCliqueNeighbourhood2(data))
                                              );
            // set maximum runtime
            vns.addStopCriterion(new MaxRuntime(timeLimit, TimeUnit.SECONDS));
            // attach listener
            vns.addSearchListener(new ProgressSearchListener());
            // IMPORTANT: start with empty clique
            vns.setCurrentSolution(new CliqueSolution(data.getIDs(), data));

            // start search
            vns.start();
            // print results
            int vnsCliqueSize = vns.getBestSolution().getNumSelectedIDs();
            System.out.println("Clique size: " + vnsCliqueSize);
            // dispose search
            vns.dispose();
            
            /***********/
            /* SUMMARY */
            /***********/
            
            System.out.println("------------------------------------------------------");
            System.out.println("Summary:");
            System.out.println("------------------------------------------------------");

            System.out.println("Number of vertices: " + data.numVertices());
            System.out.println("Number of edges: " + data.numEdges());
            System.out.println("Time limit: " + timeLimit + " seconds");
            
            System.out.println("------------------------------------------------------");

            System.out.format("%35s %15s \n", "", "clique size");
            System.out.format("%35s %15s \n", "Random descent:", randomDescentCliqueSize);
            System.out.format("%35s %15s \n", "Variable neighbourhood search:", vnsCliqueSize);
            
            System.out.println("------------------------------------------------------");
            
        } catch (FileNotFoundException ex) {
            System.err.println("Failed to read file: " + filePath);
            System.exit(2);
        }
        
    }
    
}
