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

package org.jamesframework.core.problems.datatypes;

import java.util.Set;

/**
 * Interface of a data set in which each item is identified using a unique integer ID.
 * The IDs can be freely chosen and are exposed using the defined method {@link #getIDs()}.
 * 
 * @author <a href="mailto:herman.debeukelaer@ugent.be">Herman De Beukelaer</a>
 */
public interface IntegerIdentifiedData {

    /**
     * Get the set of all integer IDs corresponding to an item in the data set.
     * 
     * @return set of all IDs
     */
    public Set<Integer> getIDs();
    
}
