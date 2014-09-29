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

package org.jamesframework.core.problems.constraints;

import org.jamesframework.core.problems.constraints.validations.SimpleValidation;

/**
 * <p>
 * Interface of a solution validation produced by a problem definition. The only requirement is that the
 * validation object can be converted into a boolean value that indicates whether the solution is valid
 * (see {@link #passed()}). A predefined simple implementation is provided that merely wraps
 * a boolean value (see {@link SimpleValidation}).
 * </p>
 * <p>
 * When implementing custom delta validations, the validation of the current solution of a neighbourhood
 * search is passed back to the problem to validate a move. Knowing only whether the current solution is
 * valid or not might not be sufficient to efficiently validate the modified solution. In such case,
 * custom validation objects can be designed that keep track of any additional metadata used for
 * efficient delta validation.
 * </p>
 * 
 * @author <a href="mailto:herman.debeukelaer@ugent.be">Herman De Beukelaer</a>
 */
public interface Validation {

    /**
     * Check whether the corresponding solution passed validation.
     * 
     * @return <code>true</code> if the solution is valid
     */
    public boolean passed();
    
}
