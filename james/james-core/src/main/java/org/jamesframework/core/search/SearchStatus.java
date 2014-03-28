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

/**
 * Enumeration of possible search statuses.
 * 
 * @author <a href="mailto:herman.debeukelaer@ugent.be">Herman De Beukelaer</a>
 */
public enum SearchStatus {
    
    /**
     * Search is idle and may be (re)started.
     */
    IDLE,
    
    /**
     * Search is initializing its current run.
     */
    INITIALIZING,
    
    /**
     * Search is actively running.
     */
    RUNNING,
    
    /**
     * Search is terminating its current run.
     */
    TERMINATING,
    
    /**
     * Search has been disposed and may not be (re)started.
     * All resources have been released.
     */
    DISPOSED;
    ;

}
