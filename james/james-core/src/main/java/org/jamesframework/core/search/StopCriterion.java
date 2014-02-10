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

package org.jamesframework.core.search;

import org.jamesframework.core.exceptions.IncompatibleStopCriterionException;

/**
 * Interface of a stop criterion that may be attached to a search to terminate it when a certain condition is met.
 * A specific stop criterion might be applicable to a certain search type only, therefore it provides a method to
 * check whether it is compatible with a given search. When trying to use it for an incompatible search, an exception
 * should be thrown.
 * 
 * @author Herman De Beukelaer <herman.debeukelaer@ugent.be>
 */
public interface StopCriterion {

    /**
     * Checks whether this stop criterion is compatible with the given search.
     * 
     * @param search search to check for compatibility
     * @return <code>true</code> if the search and stop criterion are compatible
     */
    public boolean isCompatible(Search<?> search);
    
    /**
     * Checks the stop condition for a given search. If the search is not compatible with
     * this stop criterion, i.e. if <code>isCompatible(search)</code> return <code>false</code>,
     * an exception is thrown. Else, <code>true</code> is returned if the stop condition
     * is met for the given search, so that the search should be terminated.
     * 
     * @throws IncompatibleStopCriterionException when the given search is not compatible with the stop criterion
     * @param search search for which the stop criterion should be checked
     * @return <code>true</code> if the stop condition is met
     */
    public boolean searchShouldStop(Search<?> search);
    
}
