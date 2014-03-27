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

package org.jamesframework.core.problems.datatypes;

import java.util.Set;
import org.jamesframework.core.problems.solutions.SubsetSolution;

/**
 * Represents data that corresponds to a subset selection problem. The data consists of a set of entities from which a subset
 * is to be selected, where it is required that each entity can be uniquely identified using an integer ID. This is the only
 * requirement, so that the data can be connected with abstract subset solutions, which are fully defined in terms of IDs only
 * (see {@link SubsetSolution}).
 * 
 * @author <a href="mailto:herman.debeukelaer@ugent.be">Herman De Beukelaer</a>
 */
public interface SubsetData {

    /**
     * Get the set of integer IDs corresponding to the underlying entities from which a subset is to be selected.
     * 
     * @return set of integer IDs corresponding to underlying entities
     */
    public Set<Integer> getIDs();
    
}
