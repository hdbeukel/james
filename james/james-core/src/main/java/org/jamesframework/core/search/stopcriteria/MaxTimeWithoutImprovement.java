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

import java.util.concurrent.TimeUnit;
import org.jamesframework.core.search.Search;

/**
 * Stop criterion that limits the time without finding any improvement during a search run.
 * 
 * @author <a href="mailto:herman.debeukelaer@ugent.be">Herman De Beukelaer</a>
 */
public class MaxTimeWithoutImprovement implements StopCriterion {

    // maximum time without improvement (in milliseconds)
    private final long maxTimeWithoutImprovement;
    
    /**
     * Create a stop criterion to limit the time without finding any improvement during a search run.
     * The given time should be at least 1 millisecond.
     * 
     * @param maxTimeWithoutImprovement maximum time without improvement
     * @param timeUnit corresponding time unit
     * @throws IllegalArgumentException if given time is smaller than 1 millisecond
     */
    public MaxTimeWithoutImprovement(long maxTimeWithoutImprovement, TimeUnit timeUnit){
        this.maxTimeWithoutImprovement = timeUnit.toMillis(maxTimeWithoutImprovement);
        // check at least 1 ms
        if(this.maxTimeWithoutImprovement <= 0){
            throw new IllegalArgumentException("Error while creating stop criterion: maximum time without improvement "
                                                + "should be at least 1 millisecond.");
        }
    }
    
    /**
     * Checks whether the maximum time without finding improvements has been exceeded for the given search.
     * 
     * @param search search for which the time without finding improvements has to be checked
     * @return <code>true</code> if the given search has exceeded the maximum time without improvement
     */
    @Override
    public boolean searchShouldStop(Search<?> search) {
        return search.getTimeWithoutImprovement()> maxTimeWithoutImprovement;
    }
    
    @Override
    public String toString(){
        return "{max time without improvement: " + maxTimeWithoutImprovement + " ms}";
    }

}
