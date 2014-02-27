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
package org.jamesframework.core.search.stopcriteria;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.TimeUnit;
import org.jamesframework.core.exceptions.IncompatibleStopCriterionException;
import org.jamesframework.core.search.Search;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * A stop criterion checker is responsible for checking the stop criteria of a given search, while this search is running. At construction,
 * a reference to the search is given. Stop criteria can be added using {@link #add(StopCriterion)}, which usually only happens from within
 * the search, which passes all its stop criteria to its dedicated checker after verifying compatibility.
 * <p>
 * When {@link #startChecking()} is called, a task will be scheduled to periodically check the stop criteria in the background, by default
 * with a period of 1 second, while the search is running. These tasks are executed by a dedicated thread which is shared among all stop
 * criterion checker instances, as checking stop criteria is not an intensive task.
 * <p>
 * As soon as some stop criterion is satisfied, the checker will request the search to stop and will also cancel the scheduled task
 * for further execution.
 *
 * @author Herman De Beukelaer <herman.debeukelaer@ugent.be>
 */
public class StopCriterionChecker {

    // scheduled executor service, shared by all checkers
    // (single thread as stop criterion checking is not intensive)
    private static final ScheduledExecutorService scheduler = Executors.newSingleThreadScheduledExecutor(new ThreadFactory() {
        @Override
        public Thread newThread(Runnable r) {
            return new Thread(r, "stop-crit-checker");
        }
    });
    
    // future of running scheduled task, null if not running
    private ScheduledFuture runningTask;
    
    // logger
    private static final Logger logger = LoggerFactory.getLogger(StopCriterionChecker.class);

    // period between consecutive checks, and corresponding time unit
    private long period;
    private TimeUnit periodTimeUnit;

    // search for which the stop criteria have to be checked
    private final Search<?> search;

    // list of stop criteria
    private final List<StopCriterion> stopCriteria;

    /**
     * Create a stop criterion checker dedicated to checking the stop criteria of the given search. By default, consecutive checks will be
     * separated with a period of 1 second. This can be customized using {@link #setPeriod(long, TimeUnit)}.
     *
     * @param search search for which the stop criteria should be checked while running
     */
    public StopCriterionChecker(Search<?> search) {
        // store search
        this.search = search;
        // initialize stop criterion list
        stopCriteria = new ArrayList<>();
        // set default period to 1 second
        period = 1;
        periodTimeUnit = TimeUnit.SECONDS;
        // not active
        runningTask = null;
    }

    /**
     * Add a stop criterion to check.
     *
     * @param stopCriterion stop criterion to add
     */
    public void add(StopCriterion stopCriterion) {
        stopCriteria.add(stopCriterion);
    }

    /**
     * Remove a stop criterion. Returns <code>false</code> if the given stop criterion had never been added.
     *
     * @param stopCriterion stop criterion to remove
     * @return <code>true</code> if the stop criterion has successfully been removed
     */
    public boolean remove(StopCriterion stopCriterion) {
        return stopCriteria.remove(stopCriterion);
    }

    /**
     * Set the period between consecutive stop criterion checks. By default, this period is set to 1 second. The new settings will apply as
     * from the next call of {@link #startChecking()}.
     *
     * @param period period between two checks
     * @param timeUnit time unit of this period
     */
    public void setPeriod(long period, TimeUnit timeUnit) {
        this.period = period;
        periodTimeUnit = timeUnit;
    }

    /**
     * Start checking the stop criteria, in a separate background thread. If the stop criterion checker is already active,
     * or if no stop criteria have been added, calling this method does not have any effect.
     */
    public synchronized void startChecking() {
        // check if not active and some stop criteria were added
        if (runningTask == null && !stopCriteria.isEmpty()) {
            // schedule periodical check (starting without any delay)
            runningTask = scheduler.scheduleWithFixedDelay(new StopCriterionCheckTask(), 0, period, periodTimeUnit);
            // log
            logger.debug("Stop criterion checker for search {} activated", search);
        }
    }

    /**
     * Instructs the stop criterion checker to stop checking. In case the checker is not active,
     * calling this method does not have any effect.
     */
    public synchronized void stopChecking() {
        if (runningTask != null) {
            // cancel task (let it complete its current run if running)
            runningTask.cancel(false);
            // log
            logger.debug("Stop criterion checker for search {} deactivated", search);
            // discard task
            runningTask = null;
        }
    }

    /**
     * Runnable submitted to scheduler for checking the stop criteria.
     */
    private class StopCriterionCheckTask implements Runnable {

        // only warn once (per run) in case of incompatible stop criterion
        private boolean warned = false;
        
        /**
         * Performs the actual check when being scheduled on the timer thread.
         */
        @Override
        public void run() {
            boolean stopSearch = false;
            int i = 0;
            // check every stop criterion, until one is found to be satisfied or all have been checked
            while (!stopSearch && i < stopCriteria.size()) {
                try {
                    stopSearch = stopCriteria.get(i).searchShouldStop(search);
                } catch (IncompatibleStopCriterionException ex){
                    // incompatible stop criterion; usually should not happen as search verifies compatibility when adding stop criteria
                    if(!warned){
                        // issue a single warning (per run, i.e. once during the lifetime of this specific check task)
                        logger.warn("Stop criterion checker for " + search + " contains an incompatible stop criterion", ex);
                        warned = true;
                    }
                }
                i++;
            }
            // request the search to stop if a stop condition is met
            if (stopSearch) {
                // stop checking
                stopChecking();
                // stop the search
                search.stop();
            }
        }

    }

}
