/*
 * Copyright © 2010 Talis Systems Ltd.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.openjena.geoarq;

import static org.junit.Assert.assertTrue;

import org.junit.Test;
import org.openjena.geoarq.indexer.ModelIndexer;
import org.openjena.geoarq.indexer.ModelIndexerSubject;

import com.hp.hpl.jena.query.Query;
import com.hp.hpl.jena.query.QueryExecution;
import com.hp.hpl.jena.query.QueryExecutionFactory;
import com.hp.hpl.jena.query.QueryFactory;
import com.hp.hpl.jena.query.ResultSetFactory;
import com.hp.hpl.jena.query.ResultSetFormatter;
import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.ModelFactory;
import com.hp.hpl.jena.sparql.junit.QueryTest;
import com.hp.hpl.jena.sparql.resultset.ResultSetRewindable;
import com.hp.hpl.jena.util.FileManager;

public abstract class TestGeoARQ_Script {

    static final String root = "src/test/resources/" ;
    protected static String location;

    static void runTestScript(String queryFile, String dataFile, String resultsFile, ModelIndexer indexer) {
        Query query = QueryFactory.read(root+queryFile) ;
        Model model = ModelFactory.createDefaultModel() ; 
        model.register(indexer) ;
        FileManager.get().readModel(model, root+dataFile) ;
        model.unregister(indexer) ;
        indexer.close();

        GeoARQ.setDefaultIndex(indexer.getIndexSearcher());

        QueryExecution qe = QueryExecutionFactory.create(query, model) ;
        ResultSetRewindable rsExpected = ResultSetFactory.makeRewindable(ResultSetFactory.load(root+resultsFile)) ;
        ResultSetRewindable rsActual = ResultSetFactory.makeRewindable(qe.execSelect()) ;
        boolean b = QueryTest.resultSetEquivalent(query, rsActual, rsExpected) ;
        if ( ! b ) {
            rsActual.reset() ;
            rsExpected.reset() ;
            System.out.println("==== Different (GeoARQ)") ;
            System.out.println("== Actual") ;
            ResultSetFormatter.out(rsActual) ;
            System.out.println("== Expected") ;
            ResultSetFormatter.out(rsExpected) ;
        }
        
        assertTrue(b) ;
        qe.close() ; 
        GeoARQ.removeDefaultIndex() ;
    }

    @Test public void test_geoarq_1() { 
    	runTestScript("geoarq-query-1.rq", "geoarq-data-1.ttl", "geoarq-results-1.srj", new ModelIndexerSubject(location)) ; 
    }

    @Test public void test_geoarq_2() { 
    	runTestScript("geoarq-query-2.rq", "geoarq-data-1.ttl", "geoarq-results-2.srj", new ModelIndexerSubject(location)) ; 
    }

}
