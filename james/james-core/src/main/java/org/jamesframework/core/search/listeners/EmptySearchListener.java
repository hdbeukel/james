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

import org.jamesframework.core.problems.Solution;
import org.jamesframework.core.search.Search;
import org.jamesframework.core.search.status.SearchStatus;

/**
 * An empty search listener contains empty implementations of all methods in {@link SearchListener}, acting as
 * an easy entry point for custom search listeners which desire to listen only to a subset of events fired by
 * a search, by overriding the corresponding empty implementation.
 * 
 * @param <SolutionType> solution type, required to extend {@link Solution}
 * @author <a href="mailto:herman.debeukelaer@ugent.be">Herman De Beukelaer</a>
 */
public class EmptySearchListener<SolutionType extends Solution> implements SearchListener<SolutionType> {

    @Override
    public void searchStarted(Search<? extends SolutionType> search) {}

    @Override
    public void searchStopped(Search<? extends SolutionType> search) {}

    @Override
    public void newBestSolution(Search<? extends SolutionType> search, SolutionType newBestSolution, double newBestSolutionEvaluation) {}

    @Override
    public void stepCompleted(Search<? extends SolutionType> search, long numSteps) {}
    
    @Override
    public void statusChanged(Search<? extends SolutionType> search, SearchStatus newStatus) {}

}
