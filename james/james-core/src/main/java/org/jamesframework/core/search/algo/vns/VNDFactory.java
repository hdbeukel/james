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

package org.jamesframework.core.search.algo.vns;

import java.util.List;
import org.jamesframework.core.problems.Problem;
import org.jamesframework.core.problems.Solution;
import org.jamesframework.core.search.NeighbourhoodSearch;
import org.jamesframework.core.search.neigh.Neighbourhood;
import org.jamesframework.core.util.LocalSearchFactory;

/**
 * A VND factory creates a variable neighbourhood descent search, where the list of neighbourhoods to apply are
 * set upon construction of the factory. By default, variable neighbourhood search uses a VND factory to generate
 * VND searches that modify the solution obtained by randomly sampling a neighbour of the current solution using
 * one of the shaking neighbourhoods.
 * 
 * @param <SolutionType> solution type of modified solutions, required to extend {@link Solution}
 * @author <a href="mailto:herman.debeukelaer@ugent.be">Herman De Beukelaer</a>
 */
public class VNDFactory<SolutionType extends Solution> implements LocalSearchFactory<SolutionType> {
    
    // applied neighbourhoods
    List<? extends Neighbourhood<? super SolutionType>> neighs;

    /**
     * Create a new VND factory, given the list of neighbourhoods to apply.
     * Note that <code>neighs</code> can not be <code>null</code> nor empty,
     * and can not contain any <code>null</code> elements.
     * 
     * @param neighs neighbourhoods applied by VND
     * @throws NullPointerException if <code>neighs</code> is <code>null</code> or contains any
     *                              <code>null</code> elements
     * @throws IllegalArgumentException if <code>neighs</code> is empty
     */
    public VNDFactory(List<? extends Neighbourhood<? super SolutionType>> neighs){
        // check neighbourhoods
        if(neighs == null){
            throw new NullPointerException("Can not create VND factory: list of neighbourhoods can not be null.");
        }
        for(Neighbourhood<?> n : neighs){
            if(n == null){
                throw new NullPointerException("Can not create VND factory: list of neighbourhoods can not contain"
                                                    + " any null elements.");
            }
        }
        if(neighs.isEmpty()){
            throw new IllegalArgumentException("Can not create VND factory: list of neighbourhoods can not be empty.");
        }
        // ok
        this.neighs = neighs;
    }
    
    /**
     * Create a VND search to solve the given problem, using the list of neighbourhoods
     * set at construction of the factory.
     * 
     * @param problem problem to solve
     * @throws NullPointerException if the given problem is <code>null</code>
     * @return VND search
     */
    @Override
    public VariableNeighbourhoodDescent<SolutionType> create(Problem<SolutionType> problem){
        return new VariableNeighbourhoodDescent<>(problem, neighs);
    }

}
