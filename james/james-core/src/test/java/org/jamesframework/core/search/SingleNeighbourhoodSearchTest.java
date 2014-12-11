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

import org.jamesframework.core.problems.Problem;
import org.jamesframework.core.search.neigh.Neighbourhood;
import org.jamesframework.core.subset.SubsetSolution;
import org.jamesframework.core.subset.neigh.SinglePerturbationNeighbourhood;
import org.jamesframework.core.subset.neigh.SingleSwapNeighbourhood;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import static org.junit.Assert.*;
import org.junit.Before;

/**
 * Test abstract single neighbourhood search.
 * 
 * @author <a href="mailto:herman.debeukelaer@ugent.be">Herman De Beukelaer</a>
 */
public class SingleNeighbourhoodSearchTest extends SearchTestTemplate {

    // dummy search
    private SingleNeighbourhoodSearch<SubsetSolution> search;
    
    /**
     * Print message when starting tests.
     */
    @BeforeClass
    public static void setUpClass() {
        System.out.println("# Testing SingleNeighbourhoodSearch ...");
        SearchTestTemplate.setUpClass();
    }

    /**
     * Print message when tests are complete.
     */
    @AfterClass
    public static void tearDownClass() {
        System.out.println("# Done testing SingleNeighbourhoodSearch!");
    }
    
    @Override
    @Before
    public void setUp(){
        // call super
        super.setUp();
        // create random search with internal max steps
        neigh = new SingleSwapNeighbourhood();
        search = new DummySingleNeighbourhoodSearch(problem, neigh);
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
        assertEquals("SingleNeighbourhoodSearch", search.getName());
        
        // test exeptions
        boolean thrown;
        
        thrown = false;
        try{
            search = new DummySingleNeighbourhoodSearch(problem, null);
        } catch (NullPointerException ex){
            thrown = true;
        }
        assertTrue(thrown);
        
    }
    
    /**
     * Test of setNeighbourhoods method, of class MultiNeighbourhoodSearch.
     */
    @Test
    public void testSetNeighbourhood() {
        
        System.out.println(" - test setNeighbourhood");
        
        // test exceptions
        boolean thrown;
        
        thrown = false;
        try{
            search.setNeighbourhood(null);
        } catch (NullPointerException ex){
            thrown = true;
        }
        assertTrue(thrown);
        
        // set new neighbourhood
        Neighbourhood<SubsetSolution> neigh2 = new SinglePerturbationNeighbourhood(10, 20);
        search.setNeighbourhood(neigh2);
        // verify
        assertEquals(neigh2, search.getNeighbourhood());
        
    }

    private class DummySingleNeighbourhoodSearch extends SingleNeighbourhoodSearch<SubsetSolution> {
        public DummySingleNeighbourhoodSearch(Problem<SubsetSolution> p, Neighbourhood<? super SubsetSolution> n){
            super(p, n);
        }
        @Override
        protected void searchStep() {
            // do nothing at all
        }
    }
    
}