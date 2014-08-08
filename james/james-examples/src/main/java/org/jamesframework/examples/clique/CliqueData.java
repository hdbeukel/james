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

import java.util.Map;
import java.util.Set;
import org.jamesframework.core.problems.datatypes.IntegerIdentifiedData;

/**
 * Graph for the maximum clique problem where vertices are represented by unique integers.
 * 
 * @author <a href="mailto:herman.debeukelaer@ugent.be">Herman De Beukelaer</a>
 */
public class CliqueData implements IntegerIdentifiedData {

    // maps each vertex to set of adjacent vertices
    private final Map<Integer, Set<Integer>> adjacencyMap;
    // number of edges
    private final int numEdges;

    public CliqueData(Map<Integer, Set<Integer>> adjacencyMap) {
        this.adjacencyMap = adjacencyMap;
        // count number of edges
        numEdges = adjacencyMap.values()                
                               .stream()                // stream of neighbour sets
                               .mapToInt(n -> n.size()) // map to number of neighbours
                               .sum()                   // sum neighbour counts of all vertices
                               /2;                      // all edges have been counted twice
    }
    
    @Override
    public Set<Integer> getIDs() {
        return adjacencyMap.keySet();
    }
    
    public Set<Integer> getNeighbours(int vertex){
        return adjacencyMap.get(vertex);
    }
    
    public boolean connected(int v1, int v2){
        return adjacencyMap.get(v1).contains(v2);
    }
    
    public int degree(int v){
        return adjacencyMap.get(v).size();
    }
    
    // computes the degree of a given vertex in a subgraph
    // (edges to vertices outside the subgraph are not counted)
    public long degree(int v, Set<Integer> subGraph){
        // get neighbours of v
        Set<Integer> neighbours = adjacencyMap.get(v);
        // count and return degree within subgraph
        return neighbours.stream()
                         .filter(n -> subGraph.contains(n))
                         .count();
    }
    
    public int numVertices(){
        return adjacencyMap.size();
    }
    
    public int numEdges(){
        return numEdges;
    }

}
