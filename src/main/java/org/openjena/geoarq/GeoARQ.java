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

import org.openjena.geoarq.pfunction.nearby;
import org.openjena.geoarq.pfunction.within;

import com.hp.hpl.jena.graph.Node;
import com.hp.hpl.jena.query.ARQ;
import com.hp.hpl.jena.sparql.ARQConstants;
import com.hp.hpl.jena.sparql.pfunction.PropertyFunctionRegistry;
import com.hp.hpl.jena.sparql.util.Context;
import com.hp.hpl.jena.sparql.util.Symbol;

public class GeoARQ {

    public static final String GeoARQPropertyFunctionLibraryURI = "http://openjena.org/GeoARQ/property#" ;
	
    public static final String fURI = "uri" ;
    public static final String fBNodeID = "bnode" ;
    public static final String fLat = "lat" ;
    public static final String fLong = "long" ;
    public static final String fLat2 = "lat2" ;
    public static final String fLong2 = "long2" ;
    public static final String fDistance = "distance" ;
    public static final String fScore = "score" ;
    public static final String fTierPrefix = "_localTier";
    
    // The symbol used to register the index in the query context
    public static final Symbol indexKey = ARQConstants.allocSymbol("geoarq") ;

    static {
    	PropertyFunctionRegistry.get().put(GeoARQPropertyFunctionLibraryURI + "nearby", nearby.class);
    	PropertyFunctionRegistry.get().put(GeoARQPropertyFunctionLibraryURI + "within", within.class);
    }
    
    public static void setDefaultIndex(IndexSearcher index) { 
    	setDefaultIndex(ARQ.getContext(), index) ; 
    }
    
    public static void setDefaultIndex(Context context, IndexSearcher index) { 
    	context.set(GeoARQ.indexKey, index) ; 
    }
    
    public static IndexSearcher getDefaultIndex() { 
    	return getDefaultIndex(ARQ.getContext()) ; 
    }
    
    public static IndexSearcher getDefaultIndex(Context context) { 
    	return (IndexSearcher)context.get(GeoARQ.indexKey) ; 
    }
    
    public static void removeDefaultIndex() { 
    	removeDefaultIndex(ARQ.getContext()) ; 
    }
    
    public static void removeDefaultIndex(Context context) { 
    	context.unset(GeoARQ.indexKey) ; 
    }
    
    public static Node build(Document doc) {
        String uri = doc.get(GeoARQ.fURI) ;
        if ( uri != null ) {
            return Node.createURI(uri) ;
        }
        throw new GeoARQException("Can't build: " + doc) ;
    }
    
	
}
