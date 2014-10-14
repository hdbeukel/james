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
 * Simple subset move that removes a single ID from the current selection
 * and replaces it with a new ID which was previously not selected.
 * 
 * @author <a href="mailto:herman.debeukelaer@ugent.be">Herman De Beukelaer</a>
 */
public class SwapMove extends GeneralSubsetMove{
    
    /**
     * Creates a new swap move with specified IDs to add to and remove from the current selection
     * when being applied to a given subset solution. Both IDs can not be equal.
     * 
     * @throws IllegalArgumentException if <code>add == delete</code>
     * @param add ID to add
     * @param delete ID to delete
     */
    public SwapMove(int add, int delete){
        super(Collections.singleton(add), Collections.singleton(delete));
        // check not equal
        if(add == delete){
            throw new IllegalArgumentException("Error while creating swap move: added and deleted ID can not be equal.");
        }
    }
    
    /**
     * Returns the added ID.
     * 
     * @return added ID
     */
    public int getAddedID() {
        return getAddedIDs().iterator().next();
    }
    
    /**
     * Returns the deleted ID.
     * 
     * @return deleted ID
     */
    public int getDeletedID(){
        return getDeletedIDs().iterator().next();
    }

}
