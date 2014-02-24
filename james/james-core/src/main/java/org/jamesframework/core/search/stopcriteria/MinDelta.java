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
 * Stop criterion that imposes a minimum amount of improvement in evaluation
 * when finding a new best solution during a search run.
 * 
 * @author Herman De Beukelaer <herman.debeukelaer@ugent.be>
 */
public class MinDelta implements StopCriterion {

    // minimum delta
    private final double minDelta;
    
    /**
     * Create a stop criterion that imposes a minimum delta in evaluation when
     * finding a new best solution during a search run.
     * 
     * @param minDelta minimum evaluation delta
     */
    public MinDelta(double minDelta){
        this.minDelta = minDelta;
    }
    
    /**
     * Checks whether the minimum delta observed during the current run of the given
     * search is still above the required minimum.
     * 
     * @param search search for which the minimum delta has to be checked
     * @return <code>true</code> in case of a minimum delta below the required minimum
     */
    @Override
    public boolean searchShouldStop(Search<?> search) {
        return search.getMinDelta() < minDelta;
    }

}
