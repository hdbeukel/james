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

import java.util.concurrent.TimeUnit;
import org.jamesframework.core.search.Search;

/**
 * Stop criterion that limits the time without finding any improvement during a search run.
 * 
 * @author Herman De Beukelaer <herman.debeukelaer@ugent.be>
 */
public class MaxTimeWithoutImprovement implements StopCriterion {

    // maximum time without improvement (in milliseconds)
    private final long maxTimeWithoutImprovement;
    
    /**
     * Create a stop criterion to limit the time without finding any improvement during a search run.
     * 
     * @param maxTimeWithoutImprovement maximum time without improvement
     * @param timeUnit corresponding time unit
     */
    public MaxTimeWithoutImprovement(long maxTimeWithoutImprovement, TimeUnit timeUnit){
        this.maxTimeWithoutImprovement = timeUnit.toMillis(maxTimeWithoutImprovement);
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

}
