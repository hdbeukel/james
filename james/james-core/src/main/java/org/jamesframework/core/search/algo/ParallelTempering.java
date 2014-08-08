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
import org.jamesframework.core.problems.Solution;
import org.jamesframework.core.search.NeighbourhoodSearch;
import org.jamesframework.core.search.Search;
import org.jamesframework.core.search.SingleNeighbourhoodSearch;
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
 * All replicas use the same neighbourhood which is specified when creating the parallel tempering search. If an initial
 * solution is set, a copy of this solution is set as initial solution in each replica. Else, each replica starts with a
 * distinct randomly generated solution.
 * </p>
 * <p>
 * The overall best solution found by all replicas is tracked and eventually returned by the parallel tempering algorithm.
 * The main algorithm does not actively generate nor apply any moves to its current solution, but simply updates it when a
 * replica has found a new global improvement, in which case the best solution is also updated. After setting a current
 * solution using {@link #setCurrentSolution(Solution)}, the main algorithm's current solution may differ from its best
 * solution, until a new global improvement is found and both are again updated.
 * </p>
 * <p>
 * The reported number of accepted and rejected moves (see {@link #getNumAcceptedMoves()} and {@link #getNumRejectedMoves()})
 * corresponds to the sum of the number of accepted and rejected moves in all replicas, during the current run of the
 * parallel tempering search. These values are updated with some delay, whenever a Metropolis replica has completed
 * its current run.
 * </p>
 * <p>
 * When creating the parallel tempering algorithm, the number of replicas and a minimum and maximum temperature have to be
 * specified. Temperatures assigned to the replicas are unique and equally spaced in the desired interval. The number of
 * replica steps defaults to 500 but it is strongly advised to tune this parameter for every specific problem, e.g. in case
 * of a computationally expensive objective function, a lower number of steps may be more appropriate.
 * </p>
 * <p>
 * Note that every replica runs in a separate thread so that they will be executed in parallel on multi core machines.
 * Therefore, it is important that the problem (including all of its components such as the objective, constraints, etc.)
 * and neighbourhood specified at construction are thread-safe.
 * </p>
 * 
 * @param <SolutionType> solution type of the problems that may be solved using this search, required to extend {@link Solution}
 * @author <a href="mailto:herman.debeukelaer@ugent.be">Herman De Beukelaer</a>
 */
public class ParallelTempering<SolutionType extends Solution> extends SingleNeighbourhoodSearch<SolutionType>{

    // logger
    private static final Logger logger = LoggerFactory.getLogger(ParallelTempering.class);
    
    // Metropolis replicas
    private final List<MetropolisSearch<SolutionType>> replicas;
    
    // number of steps performed by each replica
    private int replicaSteps;
    
    // thread pool for replica execution and corresponding queue of futures of submitted tasks
    private final ExecutorService pool;
    private final Queue<Future<?>> futures;
    
    // swap base: flipped (0/1) after every step for fair solution swaps
    private int swapBase;
    
    /**
     * <p>
     * Creates a new parallel tempering algorithm, specifying the problem to solve, the neighbourhood used in each replica, the number
     * of replicas, and the minimum and maximum temperature. The problem and neighbourhood can not be <code>null</code>, the number of
     * replicas and both temperature bounds should be strictly positive, and the minimum temperature should be smaller than the maximum
     * temperature. The default name "ParallelTempering" is assigned to the search.
     * </p>
     * <p>
     * Note that it is important that the given problem (including all of its components such as the objective, constraints, etc.) and
     * neighbourhood are thread-safe, because they will be accessed concurrently from several Metropolis searches running in separate
     * threads.
     * </p>
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
     * <p>
     * Creates a new parallel tempering algorithm, specifying the problem to solve, the neighbourhood used in each replica, the number
     * of replicas, the minimum and maximum temperature, and a custom search name. The problem and neighbourhood can not be <code>null</code>,
     * the number of replicas and both temperature bounds should be strictly positive, and the minimum temperature should be smaller than the
     * maximum temperature. The search name can be <code>null</code> in which case the default name "ParallelTempering" is assigned.
     * </p>
     * <p>
     * Note that it is important that the given problem (including all of its components such as the objective, constraints, etc.) and
     * neighbourhood are thread-safe, because they will be accessed concurrently from several Metropolis searches running in separate
     * threads.
     * </p>
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
    public ParallelTempering(String name, Problem<SolutionType> problem, Neighbourhood<? super SolutionType> neighbourhood,
                                int numReplicas, double minTemperature, double maxTemperature){
        super(name != null ? name : "ParallelTempering", problem, neighbourhood);
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
        // listen to events fired by replicas
        ReplicaListener listener = new ReplicaListener();
        for(MetropolisSearch<SolutionType> r : replicas){
            r.addSearchListener(listener);
        }
    }
    
    /**
     * Sets the number of steps performed by each replica in every iteration of the global parallel tempering
     * algorithm, before considering solution swaps. Defaults to 500. The specified number of steps should
     * be strictly positive.
     * 
     * @throws IllegalArgumentException if <code>steps</code> is not strictly positive
     * @param steps number of steps performed by replicas in each iteration
     */
    public void setReplicaSteps(int steps){
        // check number of steps
        if(steps <= 0){
            throw new IllegalArgumentException("Number of replica steps in parallel tempering should be strictly positive.");
        }
        // set number
        this.replicaSteps = steps;
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
     * Get the list of Metropolis replicas used by this parallel tempering algorithm. Replicas are ordered by temperature
     * (ascending). This method should be used with care, as modifying the parameters or order of replicas might break
     * the execution of the parallel tempering search.
     * 
     * @return Metropolis replicas
     */
    public List<MetropolisSearch<SolutionType>> getReplicas(){
        return replicas;
    }
    
    /**
     * Set the same temperature scale factor \(k &gt; 0\) for each replica. Temperatures are multiplied with this factor
     * in all computations. By default, the scale factor is set to 1 for every replica, see {@link MetropolisSearch}.
     * This method should be used with care when called while the search is running, as the scale factor update is
     * not guaranteed to happen atomically for all replicas.
     * 
     * @param scale temperature scale factor to be set for each replica
     * @throws IllegalArgumentException if <code>scale</code> is not strictly positive
     */
    public void setTemperatureScaleFactor(double scale){
        // update scale factor in every replica
        for(MetropolisSearch<SolutionType> r : replicas){
            r.setTemperatureScaleFactor(scale);
        }
    }
    
    /**
     * Set the same neighbourhood for each replica. Note that <code>neighbourhood</code> can not
     * be <code>null</code> and that this method may only be called when the search is idle.
     * 
     * @param neighbourhood neighbourhood to be set for each replica
     * @throws NullPointerException if <code>neighbourhood</code> is <code>null</code>
     * @throws SearchException if the search is not idle
     */
    @Override
    public void setNeighbourhood(Neighbourhood<? super SolutionType> neighbourhood){
        // synchronize with status updates
        synchronized(getStatusLock()){
            // call super
            super.setNeighbourhood(neighbourhood);
            // set neighbourhood in every replica
            for(MetropolisSearch<SolutionType> r : replicas){
                r.setNeighbourhood(neighbourhood);
            }
        }
    }
    
    /**
     * Set a custom current solution, of which a copy is passed to each replica. Note that <code>solution</code>
     * can not be <code>null</code> and that this method may only be called when the search is idle.
     * 
     * @param solution current solution to be set for each replica
     * @throws NullPointerException if <code>solution</code> is <code>null</code>
     * @throws SearchException if the search is not idle
     */
    @Override
    public void setCurrentSolution(SolutionType solution){
        // synchronize with status updates
        synchronized(getStatusLock()){
            // call super (also verifies status)
            super.setCurrentSolution(solution);
            // pass current solution to every replica (copy!)
            for(MetropolisSearch<SolutionType> r : replicas){
                r.setCurrentSolution(Solution.checkedCopy(solution));
            }
        }
    }
    
    /**
     * Perform a search step, in which every replica performs several steps and solutions of adjacent replicas may be swapped.
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
                // double check: p should be a probability in [0,1], else the replicas are not
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
    
    /**
     * When disposing a parallel tempering search, it will dispose each contained Metropolis replica and will
     * shut down the thread pool used for concurrent execution of replicas.
     */
    @Override
    protected void searchDisposed(){
        super.searchDisposed();
        // dispose replicas
        for(MetropolisSearch<SolutionType> r : replicas){
            r.dispose();
        }
        // shut down thread pool
        pool.shutdown();
    }

    /**
     * Private listener attached to each replica, to keep track of the global best solution and aggregated number of
     * accepted and rejected moves, and to terminate a replica when it has performed the desired number of steps.
     */
    private class ReplicaListener implements SearchListener<SolutionType>{
    
        /*******************************/
        /* CALLBACKS FIRED BY REPLICAS */
        /*******************************/

        /**
         * Parallel tempering algorithm listens to its Metropolis replicas: whenever a new best solution is reported
         * inside a replica, it is verified whether this is also a global improvement. If so, the main algorithm's current
         * and best solution are both updated to refer to this new global best solution. This method is synchronized to
         * avoid conflicting updates by replicas running in separate threads.
         * 
         * @param replica Metropolis replica that has found a (local) best solution
         * @param newBestSolution new best solution found in replica
         * @param newBestSolutionEvaluation evaluation of new best solution
         */
        @Override
        public synchronized void newBestSolution(Search<? extends SolutionType> replica, final SolutionType newBestSolution,
                                                                                     final double newBestSolutionEvaluation) {
            // update main algorithm's current and best solution (skip validation,
            // already known to be valid if reported as best solution by a replica)
            updateCurrentAndBestSolution(newBestSolution, newBestSolutionEvaluation, true);
        }

        /**
         * Parallel tempering algorithm listens to its Metropolis replicas: whenever a replica has completed a step, it is verified whether
         * the desired number of steps have been performed and, if so, the replica is stopped. This approach is favoured here over attaching
         * a generic maximum steps stop criterion (see {@link MaxSteps}) to each replica because of its finer granularity, i.e. because it is
         * checked after every single step.
         * 
         * @param replica Metropolis replica that completed a search step
         * @param numSteps number of steps completed so far
         */
        @Override
        public void stepCompleted(Search<? extends SolutionType> replica, long numSteps) {
            if (numSteps >= replicaSteps){
                replica.stop();
            }
        }

        /**
         * Parallel tempering algorithm listens to its Metropolis replicas: whenever a replica has finished its current run,
         * the number of accepted and rejected moves during this run are accounted for by increase the global counters. This
         * method is synchronized to avoid concurrent updates of the global number of accepted and rejected moves, as the
         * replicas run in separate threads.
         * 
         * @param replica Metropolis replica that has finished its current run
         */
        @Override
        public synchronized void searchStopped(Search<? extends SolutionType> replica) {
            // cast to neighbourhood search (should never fail, as this callback is only fired by Metropolis searches)
            NeighbourhoodSearch<?> nreplica = (NeighbourhoodSearch<?>) replica;
            // update number of accepted moves
            incNumAcceptedMoves(nreplica.getNumAcceptedMoves());
            // update number of rejected moves
            incNumRejectedMoves(nreplica.getNumRejectedMoves());
        }
        
    }

}
