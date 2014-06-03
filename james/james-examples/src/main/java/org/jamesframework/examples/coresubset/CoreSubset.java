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

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.HashSet;
import java.util.Scanner;
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

    // default input file (packed with class files)
    private static final String DEFAULT_INPUT = "/coresubset.csv";
    
    // subset size (relative)
    private static final double RELATIVE_SUBSET_SIZE = 0.1;   // 10% of dataset size
    // runtime
    private static final long RUNTIME = 10;
    private static final TimeUnit RUNTIME_UNIT = TimeUnit.SECONDS;
    
    /**
     * Runs the core subset problem. The input file containing the item names and distance matrix may be specified
     * as a single argument. If no argument is given, a default input file is used. The input consists of a csv
     * file in which the first row (header) contains N item names and the subsequent rows contain a symmetric (N x N)
     * distance matrix. The distance matrix indicates the distance between each pair of items, where the rows follow
     * the same order as the columns, as indicated by the header row.
     * 
     * @param args one argument expected: input file name
     */
    public static void main(String[] args) {
        System.out.println("### CORE SUBSET PROBLEM ###");
        InputStream str;
        if(args.length == 0){
            // default input file
            str = CoreSubset.class.getResourceAsStream(DEFAULT_INPUT);
            System.out.println("[INFO] No input file specified, using default input file:\n"
                             + "[INFO] " + CoreSubset.class.getResource(DEFAULT_INPUT).getPath());
            System.out.println("[INFO] To specify a custom input file, run:\n"
                             + "[INFO] java -cp james-examples.jar org.jamesframework.examples.coresubset.CoreSubset <filename>");
            run(str);
        } else {
            try {
                // get input file path from first argument
                str = new FileInputStream(args[0]);
                run(str);
            } catch (FileNotFoundException ex) {
                System.err.println("Input file " + args[0] + " does not exist");
                System.err.println("Aborting...");
                System.exit(1);
            }
        }
    }
    
    private static void run(InputStream str){
        
        /***************/
        /* PARSE INPUT */
        /***************/
        
        System.out.println("# PARSING INPUT");
        
        Scanner sc = new Scanner(str);
        // read names
        String[] names = sc.nextLine().split(",");
        int n = names.length;
        System.out.println("Read " + n + " item names...");
        // read distance matrix
        double[][] dist = new double[n][n];
        String[] row;
        for(int r=0; r<n; r++){
            row = sc.nextLine().split(",");
            for(int c=0; c<n; c++){
                dist[r][c] = Double.parseDouble(row[c]);
            }
        }
        System.out.println("Read " + n + "x" + n + " distance matrix...");
        
        /**********************/
        /* SAMPLE CORE SUBSET */
        /**********************/
        
        System.out.println("# SAMPLING CORE SUBSET");
        
        int subsetSize = Math.max(1, (int)(RELATIVE_SUBSET_SIZE * n));
        System.out.println("Dataset size: " + n);
        System.out.println("Subset size: " + subsetSize + " (" + RELATIVE_SUBSET_SIZE*100 + "%)");
        
        // create data object
        CoreSubsetData data = new CoreSubsetData(names, dist);
        // create objective
        CoreSubsetObjective obj = new CoreSubsetObjective();
        // create subset problem
        SubsetProblemWithData<CoreSubsetData> problem = new SubsetProblemWithData<>(obj, data, subsetSize);
        
        // create random descent search with single swap neighbourhood
        RandomDescent<SubsetSolution> search = new RandomDescent<>(problem, new SingleSwapNeighbourhood());
        // set maximum runtime
        search.addStopCriterion(new MaxRuntime(RUNTIME, RUNTIME_UNIT));
        // attach listener
        search.addSearchListener(new CoreSubsetSearchListener());
        
        // start search
        search.start();
        
        // print best solution and evaluation
        System.out.println("Best solution (IDs): " + search.getBestSolution().getSelectedIDs());
        System.out.println("Best solution (names): " + mapNames(search.getBestSolution().getSelectedIDs(), data));
        System.out.println("Best solution evaluation: " + search.getBestSolutionEvaluation());
        
    }
    
    private static Set<String> mapNames(Set<Integer> selected, CoreSubsetData data){
        Set<String> names = new HashSet<>();
        for(int ID : selected){
            names.add(data.getName(ID));
        }
        return names;
    }
    
}
