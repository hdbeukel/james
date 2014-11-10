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

package org.jamesframework.examples.tsp;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import org.jamesframework.core.problems.Problem;
import org.jamesframework.core.problems.constraints.validations.SimpleValidation;
import org.jamesframework.core.problems.constraints.validations.Validation;
import org.jamesframework.core.problems.objectives.evaluations.Evaluation;
import org.jamesframework.core.problems.objectives.evaluations.SimpleEvaluation;

/**
 * Specification of the travelling salesman problem. Each city is identified using a unique integer value.
 * The input consists of a distance matrix that contains the travel distance from each city to each other
 * city. The solution type of the problem is set to {@link TSPSolution}.
 * 
 * @author <a href="mailto:herman.debeukelaer@ugent.be">Herman De Beukelaer</a>
 */
public class TSPProblem implements Problem<TSPSolution>{

    // travel distance matrix
    private double[][] dist;

    public TSPProblem(double[][] dist) {
        this.dist = dist;
    }
    
    public double[][] getDistanceMatrix(){
        return dist;
    }
    
    public int getNumCities(){
        return dist.length;
    }
    
    @Override
    public Evaluation evaluate(TSPSolution solution) {
        // compute sum of travel distances
        List<Integer> cities = solution.getCities();
        int n = cities.size();
        double totalDistance = 0.0;
        for(int i=0; i<n; i++){
            int fromCity = cities.get(i);
            int toCity = cities.get((i+1)%n);
            totalDistance += dist[fromCity][toCity];
        }
        // wrap in simple evaluation
        return new SimpleEvaluation(totalDistance);
    }

    @Override
    public Validation validate(TSPSolution solution) {
        // no constraints
        return SimpleValidation.PASSED;
    }

    @Override
    public boolean isMinimizing() {
        // total travel distance is to be minimized
        return true;
    }

    @Override
    public TSPSolution createRandomSolution() {
        // create random permutation of cities
        List<Integer> cities = new ArrayList<>();
        int n = dist.length;
        for(int i=0; i<n; i++){
            cities.add(i);
        }
        Collections.shuffle(cities);
        // create and return TSP solution
        return new TSPSolution(cities);
    }

}
