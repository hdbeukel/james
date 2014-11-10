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
import org.jamesframework.core.exceptions.IncompatibleDeltaEvaluationException;
import org.jamesframework.core.problems.Problem;
import org.jamesframework.core.problems.constraints.validations.SimpleValidation;
import org.jamesframework.core.problems.constraints.validations.Validation;
import org.jamesframework.core.problems.objectives.evaluations.Evaluation;
import org.jamesframework.core.problems.objectives.evaluations.SimpleEvaluation;
import org.jamesframework.core.search.neigh.Move;

/**
 * Specification of the travelling salesman problem. Each city is identified using a unique integer value.
 * The input consists of a distance matrix that contains the travel distance from each city to each other
 * city. The solution type of the problem is set to {@link TSPSolution}.
 * 
 * @author <a href="mailto:herman.debeukelaer@ugent.be">Herman De Beukelaer</a>
 */
public class TSPProblem implements Problem<TSPSolution>{

    // travel distance matrix
    private final double[][] dist;

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
    public Evaluation evaluate(Move move, TSPSolution curSolution, Evaluation curEvaluation){
        
        // check move type
        if(!(move instanceof TSP2OptMove)){
            throw new IncompatibleDeltaEvaluationException("Delta evaluation in TSP problem expects move of type TSP2OptMove.");
        }
        // cast move
        TSP2OptMove move2opt = (TSP2OptMove) move;
        // get bounds of reversed subsequence
        int i = move2opt.getI();
        int j = move2opt.getJ();
        // get number of cities
        int n = getNumCities();
        
        if((j+1)%n == i){
            // special case: entire sequence reversed
            return curEvaluation;
        } else {
            // get current total travel distance
            double totalDistance = curEvaluation.getValue();
            // get current order of cities
            List<Integer> cities = curSolution.getCities();

            // get crucial cities (at boundary of reversed subsequence)
            int beforeReversed = cities.get((i-1+n)%n);
            int firstReversed = cities.get(i);
            int lastReversed = cities.get(j);
            int afterReversed = cities.get((j+1)%n);

            // account for dropped distances
            totalDistance -= dist[beforeReversed][firstReversed];
            totalDistance -= dist[lastReversed][afterReversed];

            // account for new distances
            totalDistance += dist[beforeReversed][lastReversed];
            totalDistance += dist[firstReversed][afterReversed];

            // return updated travel distance
            return new SimpleEvaluation(totalDistance);
        }
        
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
