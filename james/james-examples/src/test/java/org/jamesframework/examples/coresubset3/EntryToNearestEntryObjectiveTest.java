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

package org.jamesframework.examples.coresubset3;

import java.io.FileNotFoundException;
import java.util.Random;
import java.util.Set;
import org.jamesframework.core.problems.objectives.Objective;
import org.jamesframework.core.problems.objectives.evaluations.Evaluation;
import org.jamesframework.core.subset.SubsetSolution;
import org.jamesframework.core.subset.neigh.moves.AdditionMove;
import org.jamesframework.core.subset.neigh.moves.SubsetMove;
import org.jamesframework.core.util.SetUtilities;
import org.jamesframework.examples.coresubset.CoreSubsetData;
import org.jamesframework.examples.coresubset.CoreSubsetFileReader;
import org.jamesframework.test.util.TestConstants;
import org.junit.AfterClass;
import static org.junit.Assert.*;
import org.junit.BeforeClass;
import org.junit.Test;

/**
 *
 * @author <a href="mailto:herman.debeukelaer@ugent.be">Herman De Beukelaer</a>
 */
public class EntryToNearestEntryObjectiveTest {

    private static final String file = "input/coresubset.csv";
    private static final Random RG = new Random();
    
    private static CoreSubsetData data;
    private static CoreSubsetData dummyData;
    private static Objective<SubsetSolution, CoreSubsetData> obj;
        
    public EntryToNearestEntryObjectiveTest() {
    }

    @BeforeClass
    public static void setUpClass() throws FileNotFoundException {
        // read data
        CoreSubsetFileReader reader = new CoreSubsetFileReader();
        data = reader.read(file);
        // create dummy data
        double[][] dummyDist = {
            {0, 2, 3},
            {2, 0, 1},
            {3, 1, 0}
        };
        dummyData = new CoreSubsetData(new String[]{"A", "B", "C"}, dummyDist);
        // set objective
        obj = new EntryToNearestEntryObjective();
    }

    @AfterClass
    public static void tearDownClass() {
    }

    @Test
    public void testEvaluate(){
        
        SubsetSolution sol;
        SubsetMove move;
        Evaluation eval;
        double exp;
        int toAdd;

        // empty subset solution
        sol = new SubsetSolution(dummyData.getIDs());
        // verify
        eval = obj.evaluate(sol, dummyData);
        exp = 0.0;
        assertEquals(exp, eval.getValue(), TestConstants.DOUBLE_COMPARISON_PRECISION);
        
        // add A (ID 0)
        toAdd = 0;
        move = new AdditionMove(toAdd);
        exp = 0.0;
        assertEquals(exp, obj.evaluate(move, sol, eval, dummyData).getValue(), TestConstants.DOUBLE_COMPARISON_PRECISION);
        move.apply(sol);
        eval = obj.evaluate(sol, dummyData);
        assertEquals(exp, eval.getValue(), TestConstants.DOUBLE_COMPARISON_PRECISION);
        
        // add C (ID 2)
        toAdd = 2;
        move = new AdditionMove(toAdd);
        exp = 3.0;
        assertEquals(exp, obj.evaluate(move, sol, eval, dummyData).getValue(), TestConstants.DOUBLE_COMPARISON_PRECISION);
        move.apply(sol);
        eval = obj.evaluate(sol, dummyData);
        assertEquals(exp, eval.getValue(), TestConstants.DOUBLE_COMPARISON_PRECISION);
        
        // add B (ID 1)
        toAdd = 1;
        move = new AdditionMove(toAdd);
        exp = (2.0 + 1.0 + 1.0) / 3;
        assertEquals(exp, obj.evaluate(move, sol, eval, dummyData).getValue(), TestConstants.DOUBLE_COMPARISON_PRECISION);
        move.apply(sol);
        eval = obj.evaluate(sol, dummyData);
        assertEquals(exp, eval.getValue(), TestConstants.DOUBLE_COMPARISON_PRECISION);
        
    }
    
    @Test
    public void testEvaluateRandom() {
     
        SubsetMove move;
        Evaluation eval;
        double deltaEval, fullEval;
        
        for(int i=0; i<1000; i++){
            // create random subset solution
            SubsetSolution sol = new SubsetSolution(data.getIDs());
            int subsetSize = RG.nextInt(data.getIDs().size()+1);
            Set<Integer> select = SetUtilities.getRandomSubset(data.getIDs(), subsetSize, RG);
            sol.selectAll(select);
            // evaluate
            eval = obj.evaluate(sol, data);
            // create random addition move if possible + check delta evaluation <-> full evaluation
            if(sol.getNumUnselectedIDs() > 0){
                move = new AdditionMove(SetUtilities.getRandomElement(sol.getUnselectedIDs(), RG));
                // delta evaluation
                deltaEval = obj.evaluate(move, sol, eval, data).getValue();
                // full evaluation
                move.apply(sol);
                fullEval = obj.evaluate(sol, data).getValue();
                move.undo(sol);
                // compare
                assertEquals(fullEval, deltaEval, TestConstants.DOUBLE_COMPARISON_PRECISION);
            }
        }
        
    }

}