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

package org.jamesframework.examples.coresubset;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.Scanner;

/**
 * Reads an input file for the core subset selection problem (csv). The first row of such input file lists the N
 * item names and the subsequent N rows describe an (N x N) symmetric distance matrix.
 * 
 * @author <a href="mailto:herman.debeukelaer@ugent.be">Herman De Beukelaer</a>
 */
public class CoreSubsetFileReader {

    /**
     * Reads an input file containing the item names and distance matrix.
     * 
     * @param filePath input file path
     * @return core subset data as read from the input file
     * @throws FileNotFoundException if the file does not exist
     */
    public CoreSubsetData read(String filePath) throws FileNotFoundException{
        Scanner sc = new Scanner(new File(filePath));
        // read names
        String[] names = sc.nextLine().split(",");
        int n = names.length;
        // read distance matrix
        double[][] dist = new double[n][n];
        String[] row;
        for(int r=0; r<n; r++){
            row = sc.nextLine().split(",");
            for(int c=0; c<n; c++){
                dist[r][c] = Double.parseDouble(row[c]);
            }
        }
        // create and return data object
        return new CoreSubsetData(names, dist);
    }
    
}
