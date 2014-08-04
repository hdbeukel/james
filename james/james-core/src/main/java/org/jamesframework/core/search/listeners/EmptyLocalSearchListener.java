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

package org.jamesframework.core.search.listeners;

import org.jamesframework.core.problems.solutions.Solution;
import org.jamesframework.core.search.LocalSearch;

/**
 * An empty local search listener contains empty implementations of all methods in {@link LocalSearchListener},
 * acting as an easy entry point for custom search listeners which desire to listen only to a subset of events fired
 * by a search, by overriding the corresponding empty implementations.
 * 
 * @param <SolutionType> solution type, required to extend {@link Solution}
 * @author <a href="mailto:herman.debeukelaer@ugent.be">Herman De Beukelaer</a>
 */
public class EmptyLocalSearchListener<SolutionType extends Solution>
                                extends EmptySearchListener<SolutionType>
                                implements LocalSearchListener<SolutionType> {

    @Override
    public void modifiedCurrentSolution(LocalSearch<? extends SolutionType> search,
                                        SolutionType newCurrentSolution,
                                        double newCurrentSolutionEvaluation) {
    }
    
}
