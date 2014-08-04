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
 * Stop criterion that limits runtime of a search run.
 * 
 * @author <a href="mailto:herman.debeukelaer@ugent.be">Herman De Beukelaer</a>
 */
public class MaxRuntime implements StopCriterion {

    // maximum runtime in milliseconds
    private final long maxRuntime;
    
    /**
     * Create a stop criterion to limit the runtime of a search run.
     * The maximum runtime should be at least 1 millisecond.
     * 
     * @param maxRuntime maximum runtime
     * @param timeUnit corresponding time unit
     * @throws IllegalArgumentException if a runtime lower than 1 millisecond is given
     */
    public MaxRuntime(long maxRuntime, TimeUnit timeUnit){
        this.maxRuntime = timeUnit.toMillis(maxRuntime);
        // check runtime
        if(this.maxRuntime <= 0){
            throw new IllegalArgumentException("Error while creating stop criterion: maximum runtime should be at least 1 millisecond.");
        }
    }
    
    /**
     * Checks whether the maximum runtime has been exceeded for the given search.
     * 
     * @param search search for which the runtime has to be checked
     * @return <code>true</code> if the given search has exceeded the maximum runtime
     */
    @Override
    public boolean searchShouldStop(Search<?> search) {
        return search.getRuntime() > maxRuntime;
    }
    
    @Override
    public String toString(){
        return "{max runtime: " + maxRuntime + " ms}";
    }

}
