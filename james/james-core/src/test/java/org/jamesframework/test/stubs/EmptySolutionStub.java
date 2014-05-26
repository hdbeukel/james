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

package org.jamesframework.test.stubs;

import org.jamesframework.core.problems.solutions.Solution;

/**
 * Empty solution stub. Used for testing purposes only.
 * 
 * @author <a href="mailto:herman.debeukelaer@ugent.be">Herman De Beukelaer</a>
 */
public class EmptySolutionStub extends Solution {

    @Override
    public boolean isSameSolution(Solution sol) {
        if(sol == null){
            return false;
        }
        // all fake empty solutions are equal
        return sol.getClass() == getClass();
    }

    @Override
    public int computeHashCode() {
        // return fixed hash code
        return 5;
    }

    @Override
    public Solution copy() {
        return new EmptySolutionStub();
    }

}
