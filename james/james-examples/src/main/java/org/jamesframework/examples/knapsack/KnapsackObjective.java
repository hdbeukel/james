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

package org.jamesframework.examples.knapsack;

import org.jamesframework.core.exceptions.IncompatibleDeltaEvaluationException;
import org.jamesframework.core.problems.objectives.Evaluation;
import org.jamesframework.core.problems.objectives.Objective;
import org.jamesframework.core.problems.objectives.evaluations.SimpleEvaluation;
import org.jamesframework.core.search.neigh.Move;
import org.jamesframework.core.subset.SubsetSolution;
import org.jamesframework.core.subset.neigh.moves.SubsetMove;

/**
 * Objective for the knapsack problem: maximize the total profit.
 * 
 * @author <a href="mailto:herman.debeukelaer@ugent.be">Herman De Beukelaer</a>
 */
public class KnapsackObjective implements Objective<SubsetSolution, KnapsackData>{

    @Override
    public Evaluation evaluate(SubsetSolution solution, KnapsackData data) {
        // compute sum of profits of selected items
        double value =  solution.getSelectedIDs().stream().mapToDouble(data::getProfit).sum();
        // wrap in simple evaluation object
        return new SimpleEvaluation(value);
    }

    @Override
    public Evaluation evaluate(Move move, SubsetSolution curSolution, Evaluation curEvaluation, KnapsackData data) {
        // check move type
        if(!(move instanceof SubsetMove)){
            throw new IncompatibleDeltaEvaluationException("Knapsack objective should be used in combination "
                                                + "with neighbourhoods that generate moves of type SubsetMove.");
        }
        // cast move
        SubsetMove subsetMove = (SubsetMove) move;
        // get current profit
        double value = curEvaluation.getValue();
        // account for added items
        value += subsetMove.getAddedIDs().stream().mapToDouble(data::getProfit).sum();
        // account for removed items
        value -= subsetMove.getDeletedIDs().stream().mapToDouble(data::getProfit).sum();
        // return updated evaluation
        return new SimpleEvaluation(value);
    }    

    @Override
    public boolean isMinimizing() {
        return false;
    }

}
