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

package org.jamesframework.test.util;

import java.util.Timer;
import java.util.TimerTask;

/**
 * Used to schedule tasks for delayed execution in the test.
 * 
 * @author <a href="mailto:herman.debeukelaer@ugent.be">Herman De Beukelaer</a>
 */
public class DelayedExecution {

    /**
     * Schedule a task for execution in an other thread after the given delay (in ms).
     * 
     * @param r runnable to execute
     * @param delay delay (ms)
     */
    public static void schedule(final Runnable r, long delay){
        final Timer timer = new Timer();
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                r.run();
            }
        }, delay);
    }
    
}
