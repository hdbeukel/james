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

package org.jamesframework.test.util;

/**
 * Utility for comparing double values with a given precision
 * 
 * @author <a href="mailto:herman.debeukelaer@ugent.be">Herman De Beukelaer</a>
 */
public class DoubleComparatorWithPrecision {
    
    /**
     * Check whether <code>a <= b</code> with the given precision. Returns <code>true</code> if <code>Math.abs(a - b)</code>
     * is smaller than or equal to <code>delta</code>, or if <code>a < b</code>.
     * 
     * @param a first double value
     * @param b second double value
     * @param delta precision used for comparison
     * @return <code>true</code> if <code>a <= b</code> with the given precision
     */
    public static boolean smallerThanOrEqual(double a, double b, double delta){
        return Math.abs(a-b) <= delta || a < b;
    }
    
    /**
     * Check whether <code>a >= b</code> with the given precision. Returns <code>true</code> if <code>Math.abs(a - b)</code>
     * is smaller than or equal to <code>delta</code>, or if <code>a > b</code>.
     * 
     * @param a first double value
     * @param b second double value
     * @param delta precision used for comparison
     * @return <code>true</code> if <code>a >= b</code> with the given precision
     */
    public static boolean greaterThanOrEqual(double a, double b, double delta){
        return Math.abs(a-b) <= delta || a > b;
    }
    
    /**
     * Check whether <code>a < b</code> with the given precision. Returns <code>true</code> if <code>Math.abs(a - b)</code>
     * is greater than <code>delta</code> and <code>a < b</code>.
     * 
     * @param a first double value
     * @param b second double value
     * @param delta precision used for comparison
     * @return <code>true</code> if <code>a < b</code> with the given precision
     */
    public static boolean smallerThan(double a, double b, double delta){
        return Math.abs(a-b) > delta && a < b;
    }
    
    /**
     * Check whether <code>a > b</code> with the given precision. Returns <code>true</code> if <code>Math.abs(a - b)</code>
     * is greater than <code>delta</code> and <code>a > b</code>.
     * 
     * @param a first double value
     * @param b second double value
     * @param delta precision used for comparison
     * @return <code>true</code> if <code>a > b</code> with the given precision
     */
    public static boolean greaterThan(double a, double b, double delta){
        return Math.abs(a-b) > delta && a > b;
    }
    
    /**
     * Check whether <code>a = b</code> with the given precision. Returns <code>true</code> if <code>Math.abs(a - b)</code>
     * is smaller than or equal to <code>delta</code>.
     * 
     * @param a first double value
     * @param b second double value
     * @param delta precision used for comparison
     * @return <code>true</code> if <code>a = b</code> with the given precision
     */
    public static boolean equal(double a, double b, double delta){
        return Math.abs(a-b) <= delta;
    }

}
