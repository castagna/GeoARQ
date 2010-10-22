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

package org.openjena.geoarq.builder;

import org.openjena.geoarq.Document;
import org.openjena.geoarq.GeoARQ;
import org.openjena.geoarq.GeoARQException;
import org.openjena.geoarq.IndexBuilder;

public abstract class IndexBuilderBase implements IndexBuilder {

	@Override public abstract void add(Document doc);
	@Override public abstract void delete(String id);

    @Override public abstract void close();

	@Override
	public void index(String uri, Double latitude, Double longitude) {
		Document doc = new Document() ;
		doc.set(GeoARQ.fURI, uri);
		if ( latitude != null ) {
			doc.set(GeoARQ.fLat, latitude.toString());
		}
		if ( longitude != null ) {
			doc.set(GeoARQ.fLong, longitude.toString());
		}
	    add(doc) ;
	}

	@Override
	public void unindex(String uri) {
		try {
			delete(uri);
	    } catch (Exception ex) { 
	    	throw new GeoARQException("unindex", ex) ; 
	    } 
	}

}