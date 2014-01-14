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

package org.jamesframework.core.exceptions;

/**
 * Common super class of all runtime exceptions thrown by the James framework.
 * 
 * @author Herman De Beukelaer <herman.debeukelaer@ugent.be>
 */
public class JamesRuntimeException extends RuntimeException {

    /**
     * Creates a new instance without detail message.
     */
    public JamesRuntimeException() {
    }

    /**
     * Constructs an instance with the specified detail message.
     * @param msg the detail message
     */
    public JamesRuntimeException(String msg) {
        super(msg);
    }
    
    /**
     * Constructs an instance with the specified cause.
     * @param cause other exception that caused this exception
     */
    public JamesRuntimeException(Throwable cause) {
        super(cause);
    }
    
    /**
     * Constructs an instance with the specified detail message and cause.
     * @param msg the detail message
     * @param cause other exception that caused this exception
     */
    public JamesRuntimeException(String msg, Throwable cause) {
        super(msg, cause);
    }
    
}
