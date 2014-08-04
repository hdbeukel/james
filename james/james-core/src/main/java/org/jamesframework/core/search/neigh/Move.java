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

package org.jamesframework.core.search.neigh;

import org.jamesframework.core.exceptions.SolutionModificationException;
import org.jamesframework.core.problems.solutions.Solution;

/**
 * Interface of a move that can be applied to a generic solution type. Contains methods
 * to apply the move to a given solution and to undo the move after it has been applied.
 * 
 * @author <a href="mailto:herman.debeukelaer@ugent.be">Herman De Beukelaer</a>
 * @param <SolutionType> the solution type to which move can be applied, required to extend {@link Solution}
 */
public interface Move<SolutionType extends Solution> {

    /**
     * Apply this move to the given solution.
     * 
     * @throws SolutionModificationException if the solution can not be modified according to the applied move
     * @param solution solution to which the move is applied
     */
    public void apply(SolutionType solution);
    
    /**
     * Undo this move after it has been applied to the given solution. It is assumed that the
     * solution has not been modified in any way since the move was applied; if so, the behaviour
     * of this method is undefined.
     * 
     * @param solution solution to which the move has been applied
     */
    public void undo(SolutionType solution);
    
}
