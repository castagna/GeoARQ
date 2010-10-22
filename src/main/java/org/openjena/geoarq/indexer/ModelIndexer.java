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

package org.openjena.geoarq.indexer;

import org.openjena.geoarq.IndexSearcher;

import com.hp.hpl.jena.rdf.model.ModelChangedListener;
import com.hp.hpl.jena.rdf.model.Statement;
import com.hp.hpl.jena.rdf.model.StmtIterator;

public interface ModelIndexer extends ModelChangedListener {

    public void indexStatement(Statement s) ;
    public void unindexStatement(Statement s) ;

    public void indexStatements(StmtIterator sIter) ; 
    public void unindexStatements(StmtIterator sIter) ;

    public IndexSearcher getIndexSearcher();
    public void close();

}
