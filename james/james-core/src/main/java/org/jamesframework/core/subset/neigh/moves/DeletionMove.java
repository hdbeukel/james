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
 * Simple subset move that removes a single ID from the current selection.
 * 
 * @author <a href="mailto:herman.debeukelaer@ugent.be">Herman De Beukelaer</a>
 */
public class DeletionMove extends GeneralSubsetMove {
    
    /**
     * Create a new deletion move, specifying the ID that will be removed from the selection
     * when this moves is applied to a given subset solution.
     * 
     * @param delete ID to be removed from the selection
     */
    public DeletionMove(int delete){
        super(Collections.emptySet(), Collections.singleton(delete));
    }
    
    /**
     * Returns the deleted ID.
     * 
     * @return deleted ID
     */
    public int getDeletedID() {
        return getDeletedIDs().iterator().next();
    }
    
}
