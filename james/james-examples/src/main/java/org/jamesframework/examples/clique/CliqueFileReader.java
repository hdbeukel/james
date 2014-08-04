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

import java.io.File;
import java.io.FileNotFoundException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Scanner;
import java.util.Set;

/**
 * Reads a graph from a text file. Each line contains two vertex IDs separated by
 * one or more spaces, indicating that these vertices are connected in the graph.
 * 
 * @author <a href="mailto:herman.debeukelaer@ugent.be">Herman De Beukelaer</a>
 */
public class CliqueFileReader {

    public CliqueData read(String filePath) throws FileNotFoundException{
        Scanner sc = new Scanner(new File(filePath));
        Map<Integer, Set<Integer>> adj = new HashMap<>();
        while(sc.hasNext()){
            int v1 = sc.nextInt();
            int v2 = sc.nextInt();
            if(!adj.containsKey(v1)){
                adj.put(v1, new HashSet<Integer>());
            }
            if(!adj.containsKey(v2)){
                adj.put(v2, new HashSet<Integer>());
            }
            adj.get(v1).add(v2);
            adj.get(v2).add(v1);
        }
        return new CliqueData(adj);
    }
    
}
