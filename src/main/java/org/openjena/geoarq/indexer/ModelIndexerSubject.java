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

import java.util.HashMap;
import java.util.Map;

import org.openjena.geoarq.GeoARQException;

import com.hp.hpl.jena.datatypes.RDFDatatype;
import com.hp.hpl.jena.datatypes.xsd.XSDDatatype;
import com.hp.hpl.jena.graph.Node;
import com.hp.hpl.jena.rdf.model.Literal;
import com.hp.hpl.jena.rdf.model.Property;
import com.hp.hpl.jena.rdf.model.ResourceFactory;
import com.hp.hpl.jena.rdf.model.Statement;

public class ModelIndexerSubject extends ModelIndexerBase {

	private final static String namespace = "http://www.w3.org/2003/01/geo/wgs84_pos#";
	private final static Property latitude = ResourceFactory
			.createProperty(namespace + "lat");
	private final static Property longitude = ResourceFactory
			.createProperty(namespace + "long");

	private Map<String, Double> latitudes = new HashMap<String, Double>();
	private Map<String, Double> longitudes = new HashMap<String, Double>();

	public ModelIndexerSubject(String url) {
		super(url);
	}

	@Override
	public void unindexStatement(Statement s) {
		if (!indexThisStatement(s))
			return;

		try {
			Node subject = s.getSubject().asNode();

			if (!s.getObject().isLiteral() || !isDecimal(s.getLiteral())) {
				return;
			}

			builder.unindex(subject.getURI());
		} catch (Exception e) {
			throw new GeoARQException("unindexStatement", e);
		}
	}

	@Override
	public void indexStatement(Statement s) {
		if (!indexThisStatement(s)) {
			return;
		}

		try {
			Node subject = s.getSubject().asNode();

			if (!s.getObject().isLiteral() || !isDecimal(s.getLiteral())) {
				return;
			}

			if (s.getPredicate().equals(latitude)) {
				if (longitudes.containsKey(subject.getURI())) {
					builder.index(subject.getURI(), Double.parseDouble(s
							.getObject().asLiteral().getLexicalForm()),
							longitudes.get(subject.getURI()));
					remove(subject.getURI());
				} else {
					latitudes.put(subject.getURI(), Double.parseDouble(s
							.getObject().asLiteral().getLexicalForm()));
				}
			} else {
				if (latitudes.containsKey(subject.getURI())) {
					builder.index(subject.getURI(), latitudes.get(subject
							.getURI()), Double.parseDouble(s.getObject()
							.asLiteral().getLexicalForm()));
					remove(subject.getURI());
				} else {
					longitudes.put(subject.getURI(), Double.parseDouble(s
							.getObject().asLiteral().getLexicalForm()));
				}
			}
		} catch (Exception e) {
			throw new GeoARQException("indexStatement", e);
		}
	}

	protected boolean indexThisStatement(Statement s) {
		return ((s.getPredicate().equals(latitude)) || (s.getPredicate().equals(longitude)));
	}

	private boolean isDecimal(Literal literal) {
        RDFDatatype dtype = literal.getDatatype() ;
        if ( dtype == null )
            return false ;
        if ( ( dtype.equals(XSDDatatype.XSDfloat) ) ||
             ( dtype.equals(XSDDatatype.XSDdecimal) ) ||  
             ( dtype.equals(XSDDatatype.XSDdouble) ) )
            return true ;
        return false ;
    }

	private void remove(String uri) {
		latitudes.remove(uri);
		longitudes.remove(uri);
	}

}