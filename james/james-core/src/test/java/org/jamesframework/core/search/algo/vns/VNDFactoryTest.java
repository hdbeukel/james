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

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import org.jamesframework.core.problems.Problem;
import org.jamesframework.core.problems.constraints.validations.Validation;
import org.jamesframework.core.problems.objectives.evaluations.Evaluation;
import org.jamesframework.core.search.neigh.Neighbourhood;
import org.jamesframework.core.subset.SubsetSolution;
import org.jamesframework.core.subset.neigh.SinglePerturbationNeighbourhood;
import org.jamesframework.core.subset.neigh.SingleSwapNeighbourhood;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 * Test VND factory.
 * 
 * @author <a href="mailto:herman.debeukelaer@ugent.be">Herman De Beukelaer</a>
 */
public class VNDFactoryTest {

    /**
     * Print message when starting tests.
     */
    @BeforeClass
    public static void setUpClass() {
        System.out.println("# Testing VNDFactory ...");
    }

    /**
     * Print message when tests are complete.
     */
    @AfterClass
    public static void tearDownClass() {
        System.out.println("# Done testing VNDFactory!");
    }

    @Test
    public void testConstructor(){
        
        System.out.println(" - test constructor");
        
        boolean thrown;
        
        thrown = false;
        try {
            new VNDFactory<>(null);
        } catch (NullPointerException ex){
            thrown = true;
        }
        assertTrue(thrown);
        
        thrown = false;
        try {
            new VNDFactory<>(Arrays.asList(new SingleSwapNeighbourhood(), null));
        } catch (NullPointerException ex){
            thrown = true;
        }
        assertTrue(thrown);
        
        thrown = false;
        try {
            new VNDFactory<>(Collections.emptyList());
        } catch (IllegalArgumentException ex){
            thrown = true;
        }
        assertTrue(thrown);
        
    }
    
    /**
     * Test of create method, of class VNDFactory.
     */
    @Test
    public void testCreate() {
        
        System.out.println(" - test create");
        
        List<Neighbourhood<SubsetSolution>> neighs = Arrays.asList(new SingleSwapNeighbourhood(), new SinglePerturbationNeighbourhood());
        VNDFactory<SubsetSolution> f = new VNDFactory<>(neighs);
        
        // test with null problem
        boolean thrown = false;
        try {
            f.create(null);
        } catch (NullPointerException ex) {
            thrown = true;
        }
        assertTrue(thrown);
        
        // test with dummy problem
        Problem<SubsetSolution> p = new Problem<SubsetSolution>() {
            @Override
            public Evaluation evaluate(SubsetSolution solution) {
                throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
            }
            @Override
            public Validation validate(SubsetSolution solution) {
                throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
            }
            @Override
            public boolean isMinimizing() {
                throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
            }
            @Override
            public SubsetSolution createRandomSolution() {
                throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
            }
        };
        VariableNeighbourhoodDescent<SubsetSolution> vnd = f.create(p);
        // verify
        assertSame(p, vnd.getProblem());
        assertEquals(neighs, vnd.getNeighbourhoods());
        
    }

}