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

package org.jamesframework.core.search.stopcriteria;

import org.jamesframework.core.search.Search;

/**
 * Stop criterion that limits the number of steps of a search run.
 * 
 * @author <a href="mailto:herman.debeukelaer@ugent.be">Herman De Beukelaer</a>
 */
public class MaxSteps implements StopCriterion {

    // maximum number of steps
    private final long maxSteps;
    
    /**
     * Create a stop criterion to limit the number of steps of a search run.
     * The maximum number of steps should be strictly positive.
     * 
     * @param maxSteps maximum number of steps
     * @throws IllegalArgumentException if <code>maxSteps</code> is &le; 0
     */
    public MaxSteps(long maxSteps){
        // check max steps
        if(maxSteps <= 0){
            throw new IllegalArgumentException("Error while creating stop criterion: maximum number of steps should be > 0.");
        }
        this.maxSteps = maxSteps;
    }
    
    /**
     * Checks whether the maximum number of steps has been reached for the given search.
     * 
     * @param search search for which the number of performed steps has to be checked
     * @return <code>true</code> if the given search has reached the maximum number of steps
     */
    @Override
    public boolean searchShouldStop(Search<?> search) {
        return search.getSteps() >= maxSteps;
    }
    
    @Override
    public String toString(){
        return "{max steps: " + maxSteps + "}";
    }

}
