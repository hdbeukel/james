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

package org.jamesframework.ext.permutation;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import org.jamesframework.core.problems.datatypes.IntegerIdentifiedData;
import org.jamesframework.core.problems.objectives.Objective;
import org.jamesframework.core.problems.objectives.evaluations.Evaluation;
import org.jamesframework.core.problems.objectives.evaluations.SimpleEvaluation;
import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;

/**
 * Test permutation problem.
 * 
 * @author <a href="mailto:herman.debeukelaer@ugent.be">Herman De Beukelaer</a>
 */
public class PermutationProblemTest {

    /**
     * Print message before running tests.
     */
    @BeforeClass
    public static void setUpClass() {
        System.out.println("# Testing PermutationProblem ...");
    }

    /**
     * Print message when tests are complete.
     */
    @AfterClass
    public static void tearDownClass() {
        System.out.println("# Done testing PermutationProblem!");
    }
    
    /**
     * Test of createRandomSolution method, of class PermutationProblem.
     */
    @Test
    public void testCreateRandomSolution() {
        
        System.out.println(" - test createRandomSolution");
        
        for(int i=10; i<100; i+=10){
            DummyData data = new DummyData(i);
            DummyObjective obj = new DummyObjective();
            PermutationProblem<DummyData> problem = new PermutationProblem<>(obj, data);
            List<Integer> exp = new ArrayList<>(data.getIDs());
            Collections.sort(exp);
            for(int j=0; j<100; j++){
                PermutationSolution sol = problem.createRandomSolution();
                List<Integer> solOrder = new ArrayList<>(sol.getOrder());
                Collections.sort(solOrder);
                Assert.assertEquals(exp, solOrder);
            }
        }
        
    }
    
    private class DummyData implements IntegerIdentifiedData {

        private final Set<Integer> ids;

        public DummyData(int n) {
            ids = new HashSet<>();
            for(int i=0; i<n; i++){
                ids.add(i);
            }
        }
        
        @Override
        public Set<Integer> getIDs() {
            return ids;
        }
        
    }
    
    private class DummyObjective implements Objective<PermutationSolution, DummyData>{

        @Override
        public Evaluation evaluate(PermutationSolution solution, DummyData data) {
            return new SimpleEvaluation(0.0);
        }

        @Override
        public boolean isMinimizing() {
            return false;
        }
        
    }

}