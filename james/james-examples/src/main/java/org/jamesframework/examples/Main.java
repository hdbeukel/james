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

package org.jamesframework.examples;

/**
 * Main class of the James examples module that prints an overview of the
 * implemented examples when running Java on the distrubuted JAR-file.
 * 
 * @author <a href="mailto:herman.debeukelaer@ugent.be">Herman De Beukelaer</a>
 */
public class Main {

    public static void main(String[] args) {
        System.out.println("##################");
        System.out.println("# JAMES EXAMPLES #");
        System.out.println("##################");
        System.out.println("");
        System.out.println("Example 1: core subset selection");
        System.out.println("--------------------------------");
        System.out.println("");
        System.out.println("Given a distance matrix, sample a subset of fixed size with maximum average \n"
                         + "distance between every pair of selected items. The random descent algorithm \n"
                         + "is applied for optimization of the selected core subset.");
        System.out.println("");
        System.out.println("Usage:");
        System.out.println("");
        System.out.println("java -cp james-examples.jar org.jamesframework.examples.coresubset.CoreSubset <inputfile> <subsetsize> <runtime>");
        System.out.println("");
    }
    
}
