package org.openjena.geoarq;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.io.File;
import java.io.IOException;
import java.util.Iterator;

import org.apache.lucene.search.MatchAllDocsQuery;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.TopDocs;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;
import org.junit.Test;
import org.openjena.geoarq.builder.IndexBuilderFactory;
import org.openjena.geoarq.builder.LuceneIndexBuilder;
import org.openjena.geoarq.searcher.IndexSearcherFactory;

public class LuceneIndexBuilderTest {

	private String location = "target/lucene";
	
	@Test public void test() throws IOException {
    	LuceneIndexBuilder builder = (LuceneIndexBuilder)IndexBuilderFactory.create(location);
    	builder.deleteAll();    	
    	builder.close();
		
		IndexBuilder indexBuilder = IndexBuilderFactory.create(location);
		indexBuilder.index("foo:bar1", 38.9579000, -77.3572000);
		indexBuilder.index("foo:bar2", 38.9510000, -77.4107000);
		indexBuilder.index("foo:bar3", 38.9003000, -77.3829000);
		indexBuilder.close();

		assertEquals(3, count());
		
		IndexSearcher indexSearcher = IndexSearcherFactory.create(location);
		Iterator<Document> hits = indexSearcher.near(38.8725000, -77.3829000, 8);

		assertNotNull(hits);
		
		assertTrue (hits.hasNext());
		Document doc = hits.next();
		assertNotNull(doc);
		assertEquals("foo:bar3", doc.get(GeoARQ.fURI));
		assertEquals(Double.parseDouble("38.9003000"), Double.parseDouble(doc.get(GeoARQ.fLat)), 0.0001);
		assertEquals(Double.parseDouble("-77.3829000"), Double.parseDouble(doc.get(GeoARQ.fLong)), 0.0001);
		
		assertTrue (hits.hasNext());
		doc = hits.next();
		assertNotNull(doc);
		assertEquals("foo:bar2", doc.get(GeoARQ.fURI));
		assertEquals(Double.parseDouble("38.9510000"), Double.parseDouble(doc.get(GeoARQ.fLat)), 0.0001);
		assertEquals(Double.parseDouble("-77.4107000"), Double.parseDouble(doc.get(GeoARQ.fLong)), 0.0001);
		
		assertTrue (hits.hasNext());
		doc = hits.next();
		assertNotNull(doc);
		assertEquals("foo:bar1", doc.get(GeoARQ.fURI));
		assertEquals(Double.parseDouble("38.9579000"), Double.parseDouble(doc.get(GeoARQ.fLat)), 0.0001);
		assertEquals(Double.parseDouble("-77.3572000"), Double.parseDouble(doc.get(GeoARQ.fLong)), 0.0001);

		assertFalse (hits.hasNext());
	}
	
	private int count() throws IOException {
		Query q = new MatchAllDocsQuery();
		Directory dir = FSDirectory.open(new File ("target/lucene"));
		org.apache.lucene.search.IndexSearcher searcher = new org.apache.lucene.search.IndexSearcher(dir);
		TopDocs hits = searcher.search(q, 10);
		return hits.totalHits;
	}
	
}
