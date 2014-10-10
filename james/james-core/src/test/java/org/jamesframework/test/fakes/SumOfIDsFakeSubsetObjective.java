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

import java.util.stream.IntStream;
import org.jamesframework.core.exceptions.IncompatibleDeltaValidationException;
import org.jamesframework.core.problems.objectives.Evaluation;
import org.jamesframework.core.problems.objectives.evaluations.SimpleEvaluation;
import org.jamesframework.core.search.neigh.Move;
import org.jamesframework.core.subset.SubsetSolution;
import org.jamesframework.core.subset.neigh.SubsetMove;
import org.jamesframework.test.util.MinMaxObjective;

/**
 * A fake subset objective that ignores any given data, as it evaluates a subset solution by
 * calculating the sum of the selected IDs. Used for testing purposes only.
 * 
 * @author <a href="mailto:herman.debeukelaer@ugent.be">Herman De Beukelaer</a>
 */
public class SumOfIDsFakeSubsetObjective extends MinMaxObjective<SubsetSolution, Object> {

    /**
     * Evaluate subset solution to sum of selected IDs. Data is ignored.
     * 
     * @param solution subset solution to evaluate
     * @param data ignored
     * @return sum of selected IDs
     */
    @Override
    public Evaluation evaluate(SubsetSolution solution, Object data) {
        return new SimpleEvaluation(
                    solution.getSelectedIDs().stream().mapToInt(Integer::intValue).sum()
        );
    }

    /**
     * Delta evaluation. Subtracts removed IDs and adds newly selected IDs.
     * 
     * @param move move to be applied
     * @param curSol current solution
     * @param curEval current evaluation
     * @param data underlying data
     * @return modified evaluation
     */
    @Override
    public Evaluation evaluate(Move move, SubsetSolution curSol, Evaluation curEval, Object data){
        if(!(move instanceof SubsetMove)){
            throw new IncompatibleDeltaValidationException("Expected move of type SubsetMove.");
        }
        SubsetMove sMove = (SubsetMove) move;
        
        // update evaluation
        double e = curEval.getValue();
        e += sMove.getAddedIDs().stream().mapToInt(Integer::intValue).sum();
        e -= sMove.getDeletedIDs().stream().mapToInt(Integer::intValue).sum();
        
        return new SimpleEvaluation(e);
    }

}
