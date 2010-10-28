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

package org.openjena.geoarq.searcher;

import java.util.Iterator;

import org.openjena.geoarq.Document;
import org.openjena.geoarq.GeoARQ;
import org.openjena.geoarq.GeoARQException;
import org.openjena.geoarq.IndexSearcher;

import com.hp.hpl.jena.graph.Node;

public abstract class IndexSearcherBase implements IndexSearcher {

	@Override public abstract Iterator<Document> nearby(double latitude, double longitude, double miles);
	
	@Override
    public Document contains(Node node, double latitude, double longitude, double miles) {
        try {
            Iterator<Document> iter = nearby(latitude, longitude, miles) ;
            for ( ; iter.hasNext() ; ) {
                Document x = iter.next();
                if ( x != null && GeoARQ.build(x).equals(node)) {
                    return x ;
                }
            }
            return null ;
        } catch (Exception e) { 
        	throw new GeoARQException("contains", e) ; 
        }
    }

	@Override
	public Document contains(Node node, double latitude1, double longitude1, double latitude2, double longitude2) {
        try {
            Iterator<Document> iter = within(latitude1, longitude1, latitude2, longitude2) ;
            for ( ; iter.hasNext() ; ) {
                Document x = iter.next();
                if ( x != null && GeoARQ.build(x).equals(node)) {
                    return x ;
                }
            }
            return null ;
        } catch (Exception e) { 
        	throw new GeoARQException("contains", e) ; 
        } 
	}

}
