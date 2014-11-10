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

package org.jamesframework.ext.permutation;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import org.jamesframework.core.problems.AbstractProblem;
import org.jamesframework.core.problems.datatypes.IntegerIdentifiedData;
import org.jamesframework.core.problems.objectives.Objective;

/**
 * Generic permutation problem. Requires that every item in the data set is identified with a unique integer ID.
 * The solution type is fixed to {@link PermutationSolution} so that a permutation is coded as an ordered sequence
 * of these IDs. The data type can be any class or interface that implements/extends the {@link IntegerIdentifiedData}
 * interface which imposes the assignment of IDs to items in the data set.
 * 
 * @param <DataType> data type of the permutation problem, required to implement the {@link IntegerIdentifiedData} interface
 * @author <a href="mailto:herman.debeukelaer@ugent.be">Herman De Beukelaer</a>
 */
public class PermutationProblem<DataType extends IntegerIdentifiedData> extends AbstractProblem<PermutationSolution, DataType>{

    /**
     * Create a permutation problem with given objective and data.
     * 
     * @param objective objective of the problem
     * @param data underlying data
     * @throws NullPointerException if <code>objective</code> is <code>null</code>
     */
    public PermutationProblem(Objective<? super PermutationSolution, ? super DataType> objective, DataType data) {
        super(objective, data);
    }

    /**
     * Create a random permutation solution. The IDs of all items are retrieved from the underlying data
     * specified at construction and shuffled to create a random permutation.
     * 
     * @return randomly generated permutation solution
     */
    @Override
    public PermutationSolution createRandomSolution() {
        // create list with all IDs
        List<Integer> ids = new ArrayList<>(getData().getIDs());
        // shuffle IDs
        Collections.shuffle(ids);
        // create and return permutation solution
        return new PermutationSolution(ids);
    }

}
