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

package org.jamesframework.examples.coresubset3;

import java.util.HashMap;
import java.util.Map;
import org.jamesframework.core.problems.objectives.evaluations.Evaluation;

/**
 * Evaluation object produced by the {@link EntryToNearestEntryObjective}. For every selected item,
 * the closest other selected item is tracked. This information can be retrieved and updated to 
 * perform an efficient delta evaluation. The value corresponds to the average distance from each
 * item to the respective closest item in the selection.
 */
public class EntryToNearestEntryEvaluation implements Evaluation {

    // maps items to closest other items (IDs)
    private final Map<Integer, Integer> closestItemMap;
    // maps items to distance to respective closest item
    private final Map<Integer, Double> minDistMap;
    
    // sum of distances from items to respective closest items
    private double minDistSum;

    public EntryToNearestEntryEvaluation() {
        closestItemMap = new HashMap<>();
        minDistMap = new HashMap<>();
        minDistSum = 0.0;
    }
    
    // deep copy constructor
    public EntryToNearestEntryEvaluation(EntryToNearestEntryEvaluation toCopy){
        closestItemMap = new HashMap<>(toCopy.closestItemMap);
        minDistMap = new HashMap<>(toCopy.minDistMap);
        minDistSum = toCopy.minDistSum;
    }
    
    // add item
    public void add(int itemID, int closestOtherItemID, double distance){
        // update minimum distance sum
        minDistSum += distance;
        // update metadata
        closestItemMap.put(itemID, closestOtherItemID);
        minDistMap.put(itemID, distance);
    }
    
    // remove item
    public boolean remove(int itemID){
        if(closestItemMap.containsKey(itemID)){
            // update minimum distance sum
            minDistSum -= minDistMap.get(itemID);
            // update metadata
            closestItemMap.remove(itemID);
            minDistMap.remove(itemID);
            return true;
        }
        return false;
    }
    
    // update closest item
    public void update(int itemID, int closestOtherItemID, double distance){
        // update minimum distance sum
        minDistSum -= minDistMap.get(itemID);
        minDistSum += distance;
        // update metadata
        closestItemMap.put(itemID, closestOtherItemID);
        minDistMap.put(itemID, distance);
    }
    
    // get closest item (null of no closest item registered)
    public Integer getClosest(int itemID){
        return closestItemMap.get(itemID);
    }

    // return average distance from each item to closest item; 0.0 if no distances
    @Override
    public double getValue() {
        int numDistances = minDistMap.size();
        if(numDistances > 0){
            return minDistSum/numDistances;
        } else {
            return 0.0;
        }
    }
    
}
