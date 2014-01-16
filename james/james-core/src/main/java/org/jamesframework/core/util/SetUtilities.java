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

package org.jamesframework.core.util;

import java.util.Iterator;
import java.util.Random;
import java.util.Set;

/**
 * Contains some utility functions for manipulating sets.
 * 
 * @author Herman De Beukelaer <herman.debeukelaer@ugent.be>
 */
public class SetUtilities {
    
    /**
     * Select a random element from a given set.
     * 
     * @param <T> type of randomly selected element
     * @param set set from which to select a random element
     * @param rg random generator
     * @return randomly selected element
     */
    public static final <T> T getRandomElement(Set<? extends T> set, Random rg){
        Iterator<? extends T> it = set.iterator();
        int r = rg.nextInt(set.size());
        T selected = it.next();
        for(int i=0; i<r; i++){
            selected = it.next();
        }
        return selected;
    }

}
