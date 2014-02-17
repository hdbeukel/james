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

package org.jamesframework.core.search.listeners;

import org.jamesframework.core.problems.solutions.Solution;
import org.jamesframework.core.search.NeighbourhoodSearch;

/**
 * An empty neighbourhood search listener contains empty implementations of all methods in {@link NeighbourhoodSearchListener},
 * acting as an easy entry point for custom search listeners which desire to listen only to a subset of events fired
 * by a search, by overriding the corresponding empty implementation.
 * 
 * @param <SolutionType> solution type, required to extend {@link Solution}
 * @author Herman De Beukelaer <herman.debeukelaer@ugent.be>
 */
public class EmptyNeighbourhoodSearchListener<SolutionType extends Solution>
                                extends EmptySearchListener<SolutionType>
                                implements NeighbourhoodSearchListener<SolutionType> {

    @Override
    public void modifiedCurrentSolution(NeighbourhoodSearch<? extends SolutionType> search,
                                        SolutionType newCurrentSolution,
                                        double newCurrentSolutionEvaluation) {
    }
    
}
