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

package org.jamesframework.core.problems;

import org.jamesframework.core.problems.solutions.SubsetSolution;

/**
 * Interface of a subset problem with solution type {@link SubsetSolution}, extending the general {@link Problem} interface.
 * A subset problem contains specific methods to create empty subset solutions in which no IDs are selected, and indicates
 * the desired minimum and maximum subset size.
 * 
 * @author Herman De Beukelaer <herman.debeukelaer@ugent.be>
 */
public interface SubsetProblem extends Problem<SubsetSolution>{
    
    /**
     * Creates an empty subset solution in which no IDs are selected.
     * 
     * @return empty subset solution
     */
    public SubsetSolution createEmptySubsetSolution();
    
    /**
     * The minimum size of the subset to be selected.
     * 
     * @return minimum subset size
     */
    public int getMinSubsetSize();
    
    /**
     * The maximum size of the subset to be selected.
     * 
     * @return maximum subset size
     */
    public int getMaxSubsetSize();

}
