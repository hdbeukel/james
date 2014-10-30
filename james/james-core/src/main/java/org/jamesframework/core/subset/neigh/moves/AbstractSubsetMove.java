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

import java.util.Objects;

/**
 * Abstract subset move that contains general behaviour such as {@link #equals(Object)} and {@link #hashCode()}.
 * 
 * @author <a href="mailto:herman.debeukelaer@ugent.be">Herman De Beukelaer</a>
 */
public abstract class AbstractSubsetMove implements SubsetMove {
    
    /**
     * Two subset moves are considered equal if they respectively remove and add the same sets of IDs.
     * 
     * @param obj object to compare with this move for equality
     * @return <code>true</code> if the given object is also a subset move and respectively removes
     *         and adds the same IDs
     */
    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        if (!(obj instanceof SubsetMove)) {
            return false;
        }
        final SubsetMove other = (SubsetMove) obj;
        return Objects.equals(getAddedIDs(), other.getAddedIDs())
                && Objects.equals(getDeletedIDs(), other.getDeletedIDs());
    }
    
    /**
     * Hash code corresponding to implementation of {@link #equals(Object)}.
     * 
     * @return hash code of this move
     */
    @Override
    public int hashCode() {
        int hash = 7;
        hash = 71 * hash + Objects.hashCode(getAddedIDs());
        hash = 71 * hash + Objects.hashCode(getDeletedIDs());
        return hash;
    }

}
