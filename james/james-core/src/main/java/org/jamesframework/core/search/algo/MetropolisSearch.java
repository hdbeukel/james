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

package org.jamesframework.core.search.algo;

import java.util.concurrent.ThreadLocalRandom;
import org.jamesframework.core.exceptions.JamesRuntimeException;
import org.jamesframework.core.exceptions.SearchException;
import org.jamesframework.core.problems.Problem;
import org.jamesframework.core.problems.solutions.Solution;
import org.jamesframework.core.search.SearchStatus;
import org.jamesframework.core.search.SingleNeighbourhoodSearch;
import org.jamesframework.core.search.neigh.Move;
import org.jamesframework.core.search.neigh.Neighbourhood;

/**
 * Metropolis search with fixed temperature. Iteratively samples a random neighbour and accepts it based on a criterion that
 * depends on the difference in evaluation (\(\Delta E\)) and the temperature of the system (\(T\)). When \(\Delta E > 0\),
 * which indicates improvement, the neighbour is always accepted as the new current solution. Else, it is accepted if
 * \[
 *      e^{\frac{\Delta E}{kT}} > R(0,1)
 * \]
 * where \(R(0,1)\) is a random number in the interval \([0,1]\) and \(k\) is a constant temperature scale factor (by default, \(k = 1\)).
 * The probability of acceptance increases when the temperature \(T\) is higher or when (the negative) \(\Delta E\) is closer to zero.
 * <p>
 * Note that it is important to carefully choose the temperature, depending on the scale of the evaluations and expected deltas,
 * as well as the landscape of the objective function. Setting a high temperature decreases the probability of ending in a local
 * optimum, but also impedes convergence and generally slows down the search process. Vice versa, a low temperature aids convergence
 * but yields a system that is more sensitive to local optima. It is therefore strongly advised to experiment with different temperatures
 * for each specific problem so that an appropriate temperature can be selected.
 * 
 * @param <SolutionType> solution type of the problems that may be solved using this search, required to extend {@link Solution}
 * @author Herman De Beukelaer <herman.debeukelaer@ugent.be>
 */
public class MetropolisSearch<SolutionType extends Solution> extends SingleNeighbourhoodSearch<SolutionType> {

    // temperature
    private double temperature;
    // temperature scale factor
    private double scale;
    
    /**
     * Creates a new Metropolis search, specifying the problem to solve, the applied neighbourhood and the temperature.
     * The problem and neighbourhood can not be <code>null</code>, and the temperature should be strictly positive.
     * The search name defaults to "MetropolisSearch".
     * 
     * @throws NullPointerException if <code>problem</code> or <code>neighbourhood</code> are <code>null</code>
     * @throws IllegalArgumentException if <code>temperature</code> is not strictly positive
     * @param problem problem to solve
     * @param neighbourhood neighbourhood used to create neighbouring solutions
     * @param temperature temperature of the system
     */
    public MetropolisSearch(Problem<SolutionType> problem, Neighbourhood<? super SolutionType> neighbourhood, double temperature){
        this(null, problem, neighbourhood, temperature);
    }
    
    /**
     * Creates a new Metropolis search, specifying the problem to solve, the applied neighbourhood and temperature,
     * and a custom search name. The problem and neighbourhood can not be <code>null</code>, and the temperature
     * should be strictly positive. The search name can be <code>null</code> in which case the default name
     * "MetropolisSearch" is assigned.
     * 
     * @throws NullPointerException if <code>problem</code> or <code>neighbourhood</code> are <code>null</code>
     * @throws IllegalArgumentException if <code>temperature</code> is not strictly positive
     * @param name custom search name
     * @param problem problem to solve
     * @param neighbourhood neighbourhood used to create neighbouring solutions
     * @param temperature temperature of the system
     */
    public MetropolisSearch(String name, Problem<SolutionType> problem, Neighbourhood<? super SolutionType> neighbourhood, double temperature){
        super(name != null ? name : "MetropolisSearch", problem, neighbourhood);
        // check temperature
        if(temperature <= 0.0){
            throw new IllegalArgumentException("Temperature of Metropolis search should be strictly positive.");
        }
        // set temperature
        this.temperature = temperature;
        // set default temperature scale factor (= 1.0)
        scale = 1.0;
    }
    
    /**
     * Set the temperature (\(T > 0\)). Note that this method may only be called when the search is idle.
     * 
     * @param temperature new temperature
     * @throws IllegalArgumentException if <code>temperature</code> is not strictly positive
     * @throws SearchException if the search is not idle
     */
    public void setTemperature(double temperature){
        // synchronize with status updates
        synchronized(getStatusLock()){
            // check if idle
            if(!getStatus().equals(SearchStatus.IDLE)){
                throw new SearchException("Error while setting temperature of Metropolis search: search is not idle.");
            }
            // check temperature
            if(temperature <= 0.0){
                throw new IllegalArgumentException("Temperature of Metropolis search should be strictly positive.");
            }
            // update temperature
            this.temperature = temperature;
        }
    }
    
    /**
     * Get the temperature \(T\) of the system.
     * 
     * @return temperature
     */
    public double getTemperature(){
        return temperature;
    }
    
    /**
     * Set the temperature scale factor \(k > 0\). All temperatures are multiplied with this factor. By default,
     * the scale factor is set to 1. Note that this method may only be called when the search is idle.
     * 
     * @param scale temperature scale factor
     * @throws IllegalArgumentException if <code>scale</code> is not strictly positive
     * @throws SearchException if the search is not idle
     */
    public void setTemperatureScaleFactor(double scale){
        // synchronize with status updates
        synchronized(getStatusLock()){
            // check if idle
            if(!getStatus().equals(SearchStatus.IDLE)){
                throw new SearchException("Error while setting temperature scale factor of Metropolis search: search is not idle.");
            }
            // check scale
            if(scale <= 0.0){
                throw new IllegalArgumentException("Temperature scale factor of Metropolis search should be strictly positive.");
            }
            // update temperature
            this.scale = scale;
        }
    }
    
    /**
     * Get the temperature scale factor \(k\) of the system.
     * 
     * @return temperature scale factor
     */
    public double getTemperatureScaleFactor(){
        return scale;
    }

    /**
     * Creates a random neighbour of the current solution and accepts it if it improves over the current solution,
     * or if
     * \[
     *      e^{\frac{\Delta E}{kT}} > R(0,1)
     * \]
     * where \(\Delta E\) is the difference between the evaluation of the neighbour and that of the current solution,
     * \(T\) is the temperature of the system, \(k\) is a constant scale factor, and \(R(0,1)\) is a random number
     * in the interval \([0,1]\).
     * 
     * @throws JamesRuntimeException if depending on malfunctioning components (problem, neighbourhood, ...)
     */
    @Override
    protected void searchStep() {
        // get random move
        Move<? super SolutionType> move = getNeighbourhood().getRandomMove(getCurrentSolution());
        // got move ?
        if(move != null){
            // valid move ?
            if(validateMove(move)){
                // valid move: improvement ?
                if(isImprovement(move)){
                    // improvement: always accept
                    acceptMove(move);
                } else {
                    // no improvement: accept with probability based on temperature and delta
                    double delta = computeDelta(evaluateMove(move), getCurrentSolutionEvaluation());
                    double r = ThreadLocalRandom.current().nextDouble();
                    if(Math.exp(delta/(scale*temperature)) > r){
                        // accept non-improving move
                        acceptMove(move);
                    } else {
                        // reject non-improving move
                        rejectMove(move);
                    }
                }
            } else {
                // invalid move: reject
                rejectMove(move);
            }
        } else {
            // no move/neighbour found
            stop();            
        }
    }
    
}
