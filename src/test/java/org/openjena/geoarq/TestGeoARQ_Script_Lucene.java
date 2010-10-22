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

import org.junit.After;
import org.junit.Before;
import org.junit.BeforeClass;
import org.openjena.geoarq.builder.IndexBuilderFactory;
import org.openjena.geoarq.builder.LuceneIndexBuilder;

public class TestGeoARQ_Script_Lucene extends TestGeoARQ_Script {

    static final String root = "src/test/resources/" ;
    
    @BeforeClass public static void startCluster() {
    	location = "target/lucene";
    }

    @Before public void setUp() {
    	LuceneIndexBuilder builder = (LuceneIndexBuilder)IndexBuilderFactory.create(location);
    	builder.deleteAll();
    	builder.close();
    }
    
    @After public void tearDown() {
    	LuceneIndexBuilder builder = (LuceneIndexBuilder)IndexBuilderFactory.create(location);
    	builder.deleteAll();    	
    	builder.close();
    }
    
}
