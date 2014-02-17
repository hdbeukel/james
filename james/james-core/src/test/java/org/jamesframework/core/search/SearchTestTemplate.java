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

package org.jamesframework.core.search;

import java.util.Random;
import org.jamesframework.core.problems.SubsetProblemWithData;
import org.jamesframework.core.problems.solutions.SubsetSolution;
import org.jamesframework.test.util.FakeSubsetData;
import org.jamesframework.test.util.FakeSubsetObjectiveWithData;
import org.jamesframework.test.util.FakeSubsetPenalizingConstraint;
import org.jamesframework.test.util.RandomSearch;
import org.jamesframework.test.util.RandomSearchWithInternalMaxSteps;
import org.junit.Before;

/**
 * Template for general search tests, using a random search stub to solve a subset problem. Can be extended to use the contained data.
 * 
 * @author Herman De Beukelaer <herman.debeukelaer@ugent.be>
 */
public class SearchTestTemplate {

    // fake subset data (scored entities)
    protected FakeSubsetData data;
    // dataset size
    protected final int DATASET_SIZE = 25;
    
    // fake objective (sum of scores)
    protected FakeSubsetObjectiveWithData obj;

    // subset problem to solve (select SUBSET_SIZE out of DATASET_SIZE)
    protected SubsetProblemWithData<FakeSubsetData> problem;
    protected final int SUBSET_SIZE = 5;
    
    // fake constraint
    protected FakeSubsetPenalizingConstraint constraint;
    // minimum score diff imposed by fake constraint
    protected final double MIN_SCORE_DIFF = 0.05;
    
    // searches to work with (random search stubs)
    protected Search<SubsetSolution> search, searchWithInternalMaxSteps;
    // number of random searchWithInternalMaxSteps steps
    protected final int NUM_STEPS = 500;
    
    // random generator
    protected static final Random RG = new Random();
    
    /**
     * Create search and components to work with in each test method.
     */
    @Before
    public void setUp(){
        double[] scores = new double[DATASET_SIZE];
        for(int i=0; i<DATASET_SIZE; i++){
            scores[i] = RG.nextDouble();
        }
        data = new FakeSubsetData(scores);
        obj = new FakeSubsetObjectiveWithData();
        problem = new SubsetProblemWithData(obj, data, SUBSET_SIZE);
        constraint = new FakeSubsetPenalizingConstraint(MIN_SCORE_DIFF);
        // create two searches
        searchWithInternalMaxSteps = new RandomSearchWithInternalMaxSteps<>(problem, NUM_STEPS);
        search = new RandomSearch<>(problem);
    }
    
}