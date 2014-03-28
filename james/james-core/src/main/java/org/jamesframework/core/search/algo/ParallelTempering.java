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

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.ThreadLocalRandom;
import org.jamesframework.core.exceptions.JamesRuntimeException;
import org.jamesframework.core.exceptions.SearchException;
import org.jamesframework.core.problems.Problem;
import org.jamesframework.core.problems.solutions.Solution;
import org.jamesframework.core.search.Search;
import org.jamesframework.core.search.SearchStatus;
import org.jamesframework.core.search.listeners.SearchListener;
import org.jamesframework.core.search.neigh.Neighbourhood;
import org.jamesframework.core.search.stopcriteria.MaxSteps;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * <p>
 * Parallel tempering algorithm which uses several Metropolis search replicas with different temperatures in a given range,
 * where good solutions are pushed towards cool replicas for the sake of convergence, while bad solutions are pushed towards
 * hot replicas in an attempt to find further improvements. Each step of parallel tempering consists of the following two actions:
 * </p>
 * <ol>
 *  <li>
 *      Every replica performs a fixed number of steps (defaults to 500) in an attempt to improve its own solution.
 *  </li>
 *  <li>
 *      Solutions of adjacent replica (ordered by temperature) are considered to be swapped. Solutions of replicas
 *      \(R_1\) and \(R_2\) with temperatures \(T_1\) and \(T_2\) (\(T_1 &lt; T_2\)) and current solution evaluation
 *      \(E_1\) and \(E_2\), respectively, are always swapped if \(\Delta E \ge 0\), where \(\Delta E = computeDelta(E_2,E_1)\)
 *      (see {@link #computeDelta(double, double)}). If \(\Delta E &lt; 0\), solutions are swapped with probability
 *      \[
 *          e^{(\frac{1}{k_1T_1}-\frac{1}{k_2T_2})\Delta E},
 *      \]
 *      where \(k_1\) and \(k_2\) are the temperature scale factors of replica \(R_1\) and \(R_2\), respectively
 *      (scale factors default to 1, see {@link MetropolisSearch}).
 *  </li>
 * </ol>
 * <p>
 * The overall best solution found by all replicas is tracked and eventually returned by the parallel tempering algorithm.
 * </p>
 * <p>
 * When creating the parallel tempering algorithm, the number of replicas and a minimum and maximum temperature have to be
 * specified. Temperatures assigned to the replicas are unique and equally spaced in the desired interval. The number of replica
 * steps defaults to 500 but it is strongly advised to tune this parameter for every specific problem, e.g. in case of a computationally
 * intensive objective function a lower number of steps may be more appropriate.
 * </p>
 * <p>
 * Note that every replica runs in a separate thread so that they will be executed in parallel on multi core machines.
 * </p>
 * 
 * @param <SolutionType> solution type of the problems that may be solved using this search, required to extend {@link Solution}
 * @author <a href="mailto:herman.debeukelaer@ugent.be">Herman De Beukelaer</a>
 */
public class ParallelTempering<SolutionType extends Solution> extends Search<SolutionType> implements SearchListener<SolutionType>{

    // logger
    private static final Logger logger = LoggerFactory.getLogger(ParallelTempering.class);
    
    // Metropolis replicas
    private List<MetropolisSearch<SolutionType>> replicas;
    
    // number of steps performed by each replica
    private int replicaSteps;
    
    // thread pool for replica execution and corresponding queue of futures of submitted tasks
    private ExecutorService pool;
    private Queue<Future<?>> futures;
    
    // swap base: flipped (0/1) after every step for fair solution swaps
    private int swapBase;
    
    /**
     * Creates a new parallel tempering algorithm, specifying the problem to solve, the applied neighbourhood, the number of replicas,
     * and the minimum and maximum temperature. The problem and neighbourhood can not be <code>null</code>, the number of replicas and
     * both temperature bounds should be strictly positive, and the minimum temperature should be smaller than the maximum temperature.
     * 
     * @throws NullPointerException if <code>problem</code> or <code>neighbourhood</code> are <code>null</code>
     * @throws IllegalArgumentException if <code>numReplicas</code>, <code>minTemperature</code> or <code>maxTemperature</code>
     *                                  are not strictly positive, or if <code>minTemperature &ge; maxTemperature</code>
     * @param problem problem to solve
     * @param neighbourhood neighbourhood used inside Metropolis search replicas
     * @param numReplicas number of Metropolis replicas
     * @param minTemperature minimum temperature of Metropolis replica
     * @param maxTemperature maximum temperature of Metropolis replica
     */
    public ParallelTempering(Problem<SolutionType> problem, Neighbourhood<? super SolutionType> neighbourhood,
                                int numReplicas, double minTemperature, double maxTemperature){
        this(null, problem, neighbourhood, numReplicas, minTemperature, maxTemperature);
    }
    
    /**
     * Creates a new parallel tempering algorithm, specifying the problem to solve, the applied neighbourhood, the number of replicas,
     * the minimum and maximum temperature, and a custom search name. The problem and neighbourhood can not be <code>null</code>, the
     * number of replicas and both temperature bounds should be strictly positive, and the minimum temperature should be smaller than the
     * maximum temperature. The search name can be <code>null</code> in which case the default name "ParallelTempering" is assigned.
     * 
     * @throws NullPointerException if <code>problem</code> or <code>neighbourhood</code> are <code>null</code>
     * @throws IllegalArgumentException if <code>numReplicas</code>, <code>minTemperature</code> or <code>maxTemperature</code>
     *                                  are not strictly positive, or if <code>minTemperature &ge; maxTemperature</code>
     * @param name custom search name
     * @param problem problem to solve
     * @param neighbourhood neighbourhood used inside Metropolis search replicas
     * @param numReplicas number of Metropolis replicas
     * @param minTemperature minimum temperature of Metropolis replica
     * @param maxTemperature maximum temperature of Metropolis replica
     */
    @SuppressWarnings("LeakingThisInConstructor")
    public ParallelTempering(String name, Problem<SolutionType> problem, Neighbourhood<? super SolutionType> neighbourhood,
                                int numReplicas, double minTemperature, double maxTemperature){
        super(name != null ? name : "ParallelTempering", problem);
        // check number of replicas
        if(numReplicas <= 0){
            throw new IllegalArgumentException("Error while creating parallel tempering algorithm: number of replicas should be > 0.");
        }
        // check minimum and maximum temperature
        if(minTemperature <= 0.0){
            throw new IllegalArgumentException("Error while creating parallel tempering algorithm: minimum temperature should be > 0.0.");
        }
        if(maxTemperature <= 0.0){
            throw new IllegalArgumentException("Error while creating parallel tempering algorithm: maximum temperature should be > 0.0.");
        }
        if(minTemperature >= maxTemperature){
            throw new IllegalArgumentException("Error while creating parallel tempering algorithm: minimum temperature should be smaller than"
                                                + " maximum temperature.");
        }
        // create replicas
        replicas = new ArrayList<>();
        for(int i=0; i<numReplicas; i++){
            double temperature = minTemperature + i*(maxTemperature - minTemperature)/(numReplicas - 1);
            replicas.add(new MetropolisSearch<>(problem, neighbourhood, temperature));
        }
        // set default replica steps
        replicaSteps = 500;
        // create thread pool
        pool = Executors.newFixedThreadPool(numReplicas);
        // initialize (empty) futures queue
        futures = new LinkedList<>();
        // set initial swap base
        swapBase = 0;
        // parallel tempering algorithm listens to replicas
        for(MetropolisSearch<SolutionType> r : replicas){
            r.addSearchListener(this);
        }
    }
    
    /**
     * Sets the number of steps performed by each replica in every iteration of the global parallel tempering
     * algorithm, before considering solution swaps. Defaults to 500. The specified number of steps should
     * be strictly positive. Note that this method may only be called while the search is idle.
     * 
     * @throws SearchException if the search is not idle
     * @throws IllegalArgumentException if <code>steps</code> is not strictly positive
     * @param steps number of steps performed by replicas in each iteration
     */
    public void setReplicaSteps(int steps){
        // synchronize with status updates
        synchronized(getStatusLock()){
            // assert idle
            assertIdle("Cannot change number of replica steps in parallel tempering.");
            // check number of steps
            if(steps <= 0){
                throw new IllegalArgumentException("Number of replica steps in parallel tempering should be strictly positive.");
            }
            // set number
            this.replicaSteps = steps;
        }
    }
    
    /**
     * Get the number of steps performed by each replica in every iteration of the global parallel tempering
     * algorithm, before considering solution swaps. Defaults to 500 and can be changed using {@link #setReplicaSteps(int)}.
     * 
     * @return number of steps performed by replicas in each iteration
     */
    public int getReplicaSteps(){
        return replicaSteps;
    }
    
    /**
     * Get the list of Metropolis replicas used by this parallel tempering algorithm. Replicas are ordered by temperature (ascending).
     * 
     * @return Metropolis replicas
     */
    public List<MetropolisSearch<SolutionType>> getReplicas(){
        return replicas;
    }
    
    /**
     * Set the same temperature scale factor \(k &gt; 0\) for each replica. Temperatures are multiplied with this factor
     * in all computations. By default, the scale factor is set to 1 for every replica, see {@link MetropolisSearch}.
     * Note that this method may only be called when the search is idle.
     * 
     * @param scale temperature scale factor to be set for each replica
     * @throws IllegalArgumentException if <code>scale</code> is not strictly positive
     * @throws SearchException if the search is not idle
     */
    public void setTemperatureScaleFactor(double scale){
        // synchronize with status updates
        synchronized(getStatusLock()){
            // assert idle
            assertIdle("Cannot set temperature scale factor in parallel tempering.");
            // update scale factor in every replica
            for(MetropolisSearch<SolutionType> r : replicas){
                r.setTemperatureScaleFactor(scale);
            }
        }
    }
    
    /**
     * Set the same neighbourhood for each replica. Note that <code>neighbourhood</code> can not be <code>null</code>
     * and that this method may only be called when the search is idle.
     * 
     * @param neighbourhood neighbourhood to be set for each replica
     * @throws NullPointerException if <code>neighbourhood</code> is <code>null</code>
     * @throws SearchException if the search is not idle
     */
    public void setNeighbourhood(Neighbourhood<? super SolutionType> neighbourhood){
        // synchronize with status updates
        synchronized(getStatusLock()){
            // assert idle
            assertIdle("Cannot set neighbourhood in parallel tempering.");
            // set neighbourhood in every replica
            for(MetropolisSearch<SolutionType> r : replicas){
                r.setNeighbourhood(neighbourhood);
            }
        }
    }
    
    /**
     * Set the same current solution for each replica. Note that <code>solution</code> can not be <code>null</code>
     * and that this method may only be called when the search is idle.
     * 
     * @param solution current solution to be set for each replica
     * @throws NullPointerException if <code>solution</code> is <code>null</code>
     * @throws SearchException if the search is not idle
     */
    public void setCurrentSolution(SolutionType solution){
        // synchronize with status updates
        synchronized(getStatusLock()){
            // assert idle
            assertIdle("Cannot set current solution in parallel tempering.");
            // set current solution in every replica
            for(MetropolisSearch<SolutionType> r : replicas){
                r.setCurrentSolution(solution);
            }
        }
    }
    
    /**
     * Perform a search step, in which every replica performs several steps and solutions of adjacent replica may be swapped.
     * 
     * @throws SearchException if an error occurs during concurrent execution of the Metropolis replicas, or if
     *                          it is detected that replicas are not correctly ordered by temperature (ascending)
     * @throws JamesRuntimeException if depending on malfunctioning components (problem, neighbourhood, replicas, ...)
     */
    @Override
    protected void searchStep() {
        // submit replicas for execution in thread pool
        for(MetropolisSearch<SolutionType> r : replicas){
            futures.add(pool.submit(r));
        }
        logger.trace("{}: started {} Metropolis replicas", this, futures.size());
        // wait for completion of all replicas and remove corresponding future
        logger.trace("{}: waiting for replicas to finish", this);
        while(!futures.isEmpty()){
            // remove next future from queue and wait until it has completed
            try{
                futures.poll().get();
                logger.trace("{}: {}/{} replicas finished", this, replicas.size()-futures.size(), replicas.size());
            } catch (InterruptedException | ExecutionException ex){
                throw new SearchException("An error occured during concurrent execution of Metropolis replicas "
                                            + "in the parallel tempering algorithm.", ex);
            }
        }
        logger.trace("{}: swapping solutions", this);
        // consider swapping solutions of adjacent replicas
        for(int i=swapBase; i<replicas.size()-1; i+=2){
            MetropolisSearch<SolutionType> r1 = replicas.get(i);
            MetropolisSearch<SolutionType> r2 = replicas.get(i+1);
            // compute delta
            double delta = computeDelta(r2.getCurrentSolutionEvaluation(), r1.getCurrentSolutionEvaluation());
            // check if solutions should be swapped
            boolean swap = false;
            if(delta >= 0){
                // always swap
                swap = true;
            } else {
                // randomized swap (with probability p)
                double b1 = 1.0 / (r1.getTemperatureScaleFactor() * r1.getTemperature());
                double b2 = 1.0 / (r2.getTemperatureScaleFactor() * r2.getTemperature());
                double p = Math.exp((b1 - b2) * delta);
                // double check: p should be a probability in [0,1], else the replica are not
                // correctly orederd by temperature (ascending)
                if(p > 1.0){
                    throw new SearchException("Error in parallel tempering algorithm: replicas are not correctly ordered by "
                                                + "temperature (ascending).");
                }
                // generate random number
                double r = ThreadLocalRandom.current().nextDouble();
                // swap with probability p
                if(r < p){
                    swap = true;
                }
            }
            // swap solutions
            if(swap){
                SolutionType temp = r1.getCurrentSolution();
                r1.setCurrentSolution(r2.getCurrentSolution());
                r2.setCurrentSolution(temp);
            }
        }
        // flip swap base
        swapBase = 1 - swapBase;
    }

    /*******************************/
    /* CALLBACKS FIRED BY REPLICAS */
    /*******************************/

    /**
     * Parallel tempering algorithm listens to its Metropolis replicas: whenever a new best solution is reported inside a replica, it is
     * verified whether this is also a global improvement. If so, the global best solution is updated.
     * 
     * @param replica Metropolis replica
     * @param newBestSolution new best solution found in replica
     * @param newBestSolutionEvaluation evaluation of new best solution
     */
    @Override
    public void newBestSolution(Search<? extends SolutionType> replica, SolutionType newBestSolution, double newBestSolutionEvaluation) {
        // update global best solution (checks if the new solution is an improvement)
        updateBestSolution(newBestSolution, newBestSolutionEvaluation);
    }

    /**
     * Parallel tempering algorithm listens to its Metropolis replicas: whenever a replica has completed a step, it is verified whether
     * the desired number of steps have been performed and, if so, the replica is stopped. This approach is favoured here over attaching
     * a generic maximum steps stop criterion (see {@link MaxSteps}) to each replica because of its finer granularity, i.e. because it is
     * checked after every single step.
     * 
     * @param replica Metropolis replica
     * @param numSteps number of steps completed so far
     */
    @Override
    public void stepCompleted(Search<? extends SolutionType> replica, long numSteps) {
        if (numSteps >= replicaSteps){
            replica.stop();
        }
    }
    
    /**
     * Empty callback: no action taken here when a replica has started.
     */
    @Override
    public void searchStarted(Search<? extends SolutionType> replica) {}
    /**
     * Empty callback: no action taken here when a replica has stopped.
     */
    @Override
    public void searchStopped(Search<? extends SolutionType> replica) {}

}
