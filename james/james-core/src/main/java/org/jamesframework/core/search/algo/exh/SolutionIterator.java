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

package org.jamesframework.core.search.algo.exh;

import java.util.NoSuchElementException;
import org.jamesframework.core.problems.solutions.Solution;

/**
 * Interface of a generic solution iterator that iterates through a collection of solutions.
 * The exhaustive search algorithm depends on solution iterators to evaluate all possible solutions. To use
 * this algorithm for a specific problem, a solution iterator has to be created first, which traverses the entire
 * solution space corresponding to this problem.
 * 
 * @param <SolutionType> solution type of generated solutions, required to extends {@link Solution}
 * @author <a href="mailto:herman.debeukelaer@ugent.be">Herman De Beukelaer</a>
 */
public interface SolutionIterator<SolutionType extends Solution> {
    
    /**
     * Indicates whether there are more solutions to be generated.
     * 
     * @return <code>true</code> if solutions remain to be generated
     */
    public boolean hasNext();
    
    /**
     * Generate the next solution. This method should only be called when {@link #hasNext()} returns <code>true</code>,
     * else it will throw an exception.
     * 
     * @throws NoSuchElementException if there are no more solutions to be generated (see {@link #hasNext()})
     * @return next solution
     */
    public SolutionType next();

}
