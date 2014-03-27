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

package org.jamesframework.core.search.stopcriteria;

import org.jamesframework.core.search.Search;

/**
 * Stop criterion that limits the number of steps without finding any improvement during a search run.
 * 
 * @author <a href="mailto:herman.debeukelaer@ugent.be">Herman De Beukelaer</a>
 */
public class MaxStepsWithoutImprovement implements StopCriterion {

    // maximum number of steps without improvement
    private final long maxStepsWithoutImprovement;
    
    /**
     * Create a stop criterion to limit the number of steps without finding any improvement during a search run.
     * The given number of steps should be strictly positive.
     * 
     * @param maxStepsWithoutImprovement maximum number of steps without improvement
     * @throws IllegalArgumentException if <code>maxStepsWithoutImprovement</code> is &le; 0
     */
    public MaxStepsWithoutImprovement(long maxStepsWithoutImprovement){
        // check given step count
        if(maxStepsWithoutImprovement <= 0){
            throw new IllegalArgumentException("Error while creating stop criterion: maximum number of "
                                                + "steps without improvement should be > 0.");
        } 
        this.maxStepsWithoutImprovement = maxStepsWithoutImprovement;
    }
    
    /**
     * Checks whether the maximum number of steps without finding improvement has been exceeded for the given search.
     * 
     * @param search search for which the number of performed steps without finding any improvement has to be checked
     * @return <code>true</code> if the given search has exceeded the maximum number of steps without improvement
     */
    @Override
    public boolean searchShouldStop(Search<?> search) {
        return search.getStepsWithoutImprovement()> maxStepsWithoutImprovement;
    }
    
    @Override
    public String toString(){
        return "{max steps without improvement: " + maxStepsWithoutImprovement + "}";
    }

}
