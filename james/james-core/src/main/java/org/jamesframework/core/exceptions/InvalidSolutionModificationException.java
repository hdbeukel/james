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
 * Thrown when trying to perform an invalid modification on a solution object.
 * 
 * @author Herman De Beukelaer <herman.debeukelaer@ugent.be>
 */
public class InvalidSolutionModificationException extends JamesRuntimeException {

    public InvalidSolutionModificationException() {
    }

    public InvalidSolutionModificationException(String msg) {
        super(msg);
    }

    public InvalidSolutionModificationException(Throwable cause) {
        super(cause);
    }

    public InvalidSolutionModificationException(String msg, Throwable cause) {
        super(msg, cause);
    }

}
