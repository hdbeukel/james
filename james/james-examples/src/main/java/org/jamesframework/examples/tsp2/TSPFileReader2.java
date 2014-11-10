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

import java.io.File;
import java.io.FileNotFoundException;
import java.util.Scanner;

/**
 * Reads a (symmetric) distance matrix for TSP from a text file and constructs the corresponding TSP data.
 * 
 * @author <a href="mailto:herman.debeukelaer@ugent.be">Herman De Beukelaer</a>
 */
public class TSPFileReader2 {
    
    /**
     * Read a (symmetric) distance matrix from a text file and create the corresponding TSP data.
     * The first line of the file contains a single integer value (possibly surrounded by whitespace)
     * that indicates the number of cities N. The remainder of the file contains the entries of the
     * lower triangular part of a symmetric distance matrix (row-wise without diagonal entries),
     * separated by whitespace and/or newlines.
     * 
     * @param filePath input file path
     * @return TSP data with distance matrix read from the input file
     * @throws FileNotFoundException if the file does not exist
     */
    public TSPData read(String filePath) throws FileNotFoundException {
        // create scanner
        Scanner sc = new Scanner(new File(filePath));
        // read number of cities
        int n = sc.nextInt();
        // initialize distance matrix
        double[][] dist = new double[n][n];
        // fill distance matrix
        for(int i=0; i<n; i++){
            for(int j=0; j<i; j++){
                dist[i][j] = sc.nextDouble();
                dist[j][i] = dist[i][j];
            }
        }
        // create and return TSP data
        return new TSPData(dist);
    }

}
