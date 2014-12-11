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

package org.jamesframework.core.search;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import org.jamesframework.core.problems.Problem;
import org.jamesframework.core.search.neigh.Neighbourhood;
import org.jamesframework.core.subset.SubsetSolution;
import org.jamesframework.core.subset.neigh.SinglePerturbationNeighbourhood;
import org.jamesframework.core.subset.neigh.SingleSwapNeighbourhood;
import org.jamesframework.test.search.RandomSearchWithInternalMaxSteps;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import static org.junit.Assert.*;
import org.junit.Before;

/**
 * Test abstract multi neighbourhood search.
 * 
 * @author <a href="mailto:herman.debeukelaer@ugent.be">Herman De Beukelaer</a>
 */
public class MultiNeighbourhoodSearchTest extends SearchTestTemplate {

    // dummy search
    private MultiNeighbourhoodSearch<SubsetSolution> search;
    // neighbourhoods
    private List<Neighbourhood<SubsetSolution>> neighs;
    
    /**
     * Print message when starting tests.
     */
    @BeforeClass
    public static void setUpClass() {
        System.out.println("# Testing MultiNeighbourhoodSearch ...");
        SearchTestTemplate.setUpClass();
    }

    /**
     * Print message when tests are complete.
     */
    @AfterClass
    public static void tearDownClass() {
        System.out.println("# Done testing MultiNeighbourhoodSearch!");
    }
    
    @Override
    @Before
    public void setUp(){
        // call super
        super.setUp();
        // create random search with internal max steps
        neighs = Arrays.asList(new SingleSwapNeighbourhood(), new SinglePerturbationNeighbourhood());
        search = new DummyMultiNeighbourhoodSearch(problem, neighs);
    }
    
    @After
    public void tearDown(){
        // dispose search
        search.dispose();
    }

    @Test
    public void testConstructors(){
        System.out.println(" - test constructors");
        
        // test default name
        assertEquals("MultiNeighbourhoodSearch", search.getName());
        
        // test exeptions
        boolean thrown;
        
        thrown = false;
        try{
            search = new DummyMultiNeighbourhoodSearch(problem, null);
        } catch (NullPointerException ex){
            thrown = true;
        }
        assertTrue(thrown);
        
        thrown = false;
        try{
            search = new DummyMultiNeighbourhoodSearch(problem, Arrays.asList(neighs.get(0), null));
        } catch (NullPointerException ex){
            thrown = true;
        }
        assertTrue(thrown);
        
        thrown = false;
        try{
            search = new DummyMultiNeighbourhoodSearch(problem, Collections.emptyList());
        } catch (IllegalArgumentException ex){
            thrown = true;
        }
        assertTrue(thrown);
        
    }
    
    /**
     * Test of setNeighbourhoods method, of class MultiNeighbourhoodSearch.
     */
    @Test
    public void testSetNeighbourhoods() {
        
        System.out.println(" - test setNeighbourhoods");
        
        // test exeptions
        boolean thrown;
        
        thrown = false;
        try{
            search.setNeighbourhoods(null);
        } catch (NullPointerException ex){
            thrown = true;
        }
        assertTrue(thrown);
        
        thrown = false;
        try{
            search.setNeighbourhoods(Arrays.asList(neighs.get(0), null));
        } catch (NullPointerException ex){
            thrown = true;
        }
        assertTrue(thrown);
        
        thrown = false;
        try{
            search.setNeighbourhoods(Collections.emptyList());
        } catch (IllegalArgumentException ex){
            thrown = true;
        }
        assertTrue(thrown);
        
        // set new neighbourhoods
        List<Neighbourhood<SubsetSolution>> neighs2 = Arrays.asList(new SinglePerturbationNeighbourhood(10, 20));
        search.setNeighbourhoods(neighs2);
        // verify
        assertEquals(neighs2, search.getNeighbourhoods());
        
        // test unmodifiable
        thrown = false;
        try {
            search.getNeighbourhoods().clear();
        } catch (UnsupportedOperationException ex){
            thrown = true;
        }
        assertTrue(thrown);
        
    }

    private class DummyMultiNeighbourhoodSearch extends MultiNeighbourhoodSearch<SubsetSolution> {
        public DummyMultiNeighbourhoodSearch(Problem<SubsetSolution> p, List<? extends Neighbourhood<? super SubsetSolution>> n){
            super(p, n);
        }
        @Override
        protected void searchStep() {
            // do nothing at all
        }
    }
    
}