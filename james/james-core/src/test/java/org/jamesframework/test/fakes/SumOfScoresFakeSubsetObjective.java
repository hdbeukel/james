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

package org.jamesframework.test.fakes;

import org.jamesframework.core.problems.solutions.SubsetSolution;
import org.jamesframework.test.util.MinMaxObjective;

/**
 * A fake subset objective that evaluates a subset solution to the sum of scores corresponding to the selected IDs,
 * where scores are provided by an instance of {@link ScoredFakeSubsetData}. Used for testing only.
 * 
 * @author <a href="mailto:herman.debeukelaer@ugent.be">Herman De Beukelaer</a>
 */
public class SumOfScoresFakeSubsetObjective extends MinMaxObjective<SubsetSolution, ScoredFakeSubsetData>{

    /**
     * Evaluate a subset solution by computing the sum of scores of selected entities.
     * 
     * @param solution solution to evaluate
     * @param data underlying fake subset data
     * @return sum of scores of selected entities
     */
    @Override
    public double evaluate(SubsetSolution solution, ScoredFakeSubsetData data) {
        double sum = 0.0;
        for(int ID : solution.getSelectedIDs()){
            sum += data.getScore(ID);
        }
        return sum;
    }

}
