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

package org.jamesframework.examples.clique;

import java.util.HashSet;
import java.util.Set;
import org.jamesframework.core.problems.solutions.SubsetSolution;

/**
 * Extension of subset solution for maximum clique problem. Dynamically keeps track of the set of vertices
 * that are connected to all vertices in the current clique (possible adds) for efficiency of the applied
 * neighbourhoods.
 * 
 * @author <a href="mailto:herman.debeukelaer@ugent.be">Herman De Beukelaer</a>
 */
public class CliqueSolution extends SubsetSolution {

    // possible adds (connected to entire current clique)
    private final Set<Integer> possibleAdds;
    // impossible adds (not connected to at least one vertex in current clique)
    private final Set<Integer> impossibleAdds;
    
    // reference to clique data
    private final CliqueData data;
    
    // single constructor (empty clique)
    public CliqueSolution(Set<Integer> vertexIDs, CliqueData data){
        super(vertexIDs);
        // initialize possible adds (all vertices)
        possibleAdds = new HashSet<>(vertexIDs);
        // initialize impossible adds (empty)
        impossibleAdds = new HashSet<>();
        // store data reference
        this.data = data;
    }
    
    @Override
    public CliqueSolution copy() {
        CliqueSolution copy = new CliqueSolution(getAllIDs(), data);
        copy.selectAll(getSelectedIDs());
        return copy;
    }
    
    @Override
    public boolean select(int vertex){
        if(possibleAdds.contains(vertex) && super.select(vertex)){
            // new vertex included in clique
            possibleAdds.remove(vertex);
            Set<Integer> eliminated = new HashSet<>();
            for(int v : possibleAdds){
                if(!data.connected(v, vertex)){
                    // v is no longer a possible add
                    eliminated.add(v);
                }
            }
            // update (im)possible adds
            possibleAdds.removeAll(eliminated);
            impossibleAdds.addAll(eliminated);
            return true;
        } else {
            return false;
        }
    }
    
    @Override
    public boolean deselect(int vertex){
        if(super.deselect(vertex)){
            // vertex removed from clique (goes back to possible adds)
            possibleAdds.add(vertex);
            // check for new possible adds (connected to remaining clique)
            Set<Integer> newPossibleAdds = new HashSet<>();
            for(int v : impossibleAdds){
                if(connectedToClique(v)){
                    newPossibleAdds.add(v);
                }
            }
            // update (im)possible adds
            possibleAdds.addAll(newPossibleAdds);
            impossibleAdds.removeAll(newPossibleAdds);
            return true;
        } else {
            return false;
        }
    }
    
    public Set<Integer> getPossibleAdds(){
        return possibleAdds;
    }
    
    // checks whether a given vertex is conected to the entire current clique
    private boolean connectedToClique(int vertex){
        for(int v : getSelectedIDs()){
            if(!data.connected(vertex, v)){
                return false;
            }
        }
        return true;
    }
    
}
