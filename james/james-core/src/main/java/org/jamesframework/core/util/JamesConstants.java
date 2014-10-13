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

package org.jamesframework.core.util;

/**
 * Global container defining constants used across the framework.
 * 
 * @author <a href="mailto:herman.debeukelaer@ugent.be">Herman De Beukelaer</a>
 */
public class JamesConstants {

    /**
     * Used to indicate an invalid timestamp, for example belonging to an event which has not yet occurred.
     */
    public static final long INVALID_TIMESTAMP = -1;
    
    /**
     * Used to indicate an invalid time span.
     */
    public static final long INVALID_TIME_SPAN = -1;
    
    /**
     * Indicates an invalid number of steps.
     */
    public static final long INVALID_STEP_COUNT = -1;
    
    /**
     * Indicates an invalid number of moves.
     */
    public static final long INVALID_MOVE_COUNT = -1;
    
    /**
     * Indicates an invalid delta (amount of improvement).
     */
    public static final double INVALID_DELTA = -1.0;
    
    /**
     * Indicates an unlimited size.
     */
    public static final int UNLIMITED_SIZE = -1;
    
}
