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

package org.jamesframework.core.subset.neigh.moves;

import java.util.Collections;

/**
 * Simple subset move that adds a single ID to the current selection.
 * 
 * @author <a href="mailto:herman.debeukelaer@ugent.be">Herman De Beukelaer</a>
 */
public class AdditionMove extends GeneralSubsetMove{
    
    /**
     * Create a new addition move that will add the specified ID to the current
     * selection when being applied to a given subset solution.
     * 
     * @param add ID to add to selection
     */
    public AdditionMove(int add){
        super(Collections.singleton(add), Collections.emptySet());
    }
    
    /**
     * Returns the added ID.
     * 
     * @return added ID
     */
    public int getAddedID() {
        return getAddedIDs().iterator().next();
    }
    
}
