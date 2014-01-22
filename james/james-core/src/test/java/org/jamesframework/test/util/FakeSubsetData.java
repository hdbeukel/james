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

package org.jamesframework.test.util;

import java.util.HashSet;
import java.util.Set;
import org.jamesframework.core.problems.datatypes.SubsetData;

/**
 * Fake subset data that assigns a score to every ID. Used for testing only.
 * 
 * @author Herman De Beukelaer <herman.debeukelaer@ugent.be>
 */
public class FakeSubsetData implements SubsetData {

    // array of scores
    private final double[] scores;
    
    /**
     * Create a new fake subset data with given scores array. IDs of entities are indices in the array,
     * the values represent the score of each entity.
     * 
     * @param scores array with score per entity
     */
    public FakeSubsetData(double[] scores){
        this.scores = scores;
    }
    
    /**
     * Create and return set with IDs corresponding to indices in scores array.
     * 
     * @return 
     */
    @Override
    public Set<Integer> getIDs() {
        Set<Integer> ids = new HashSet<>();
        for(int i=0; i<scores.length; i++){
            ids.add(i);
        }
        return ids;
    }
    
    /**
     * Get the score of an entity with given ID.
     * 
     * @param ID ID of entity
     * @return score of entity with given ID
     */
    public double getScore(int ID){
        return scores[ID];
    }

}
