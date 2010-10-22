package org.openjena.geoarq.searcher;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.apache.lucene.document.Fieldable;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.search.MatchAllDocsQuery;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.ScoreDoc;
import org.apache.lucene.search.Searcher;
import org.apache.lucene.search.Sort;
import org.apache.lucene.search.SortField;
import org.apache.lucene.search.TopDocs;
import org.apache.lucene.spatial.tier.DistanceFieldComparatorSource;
import org.apache.lucene.spatial.tier.DistanceQueryBuilder;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;
import org.apache.lucene.util.NumericUtils;
import org.openjena.geoarq.Document;
import org.openjena.geoarq.GeoARQ;
import org.openjena.geoarq.GeoARQException;
import org.openjena.geoarq.IndexSearcher;

public class LuceneIndexSearcher extends IndexSearcherBase implements IndexSearcher {

	private Directory dir = null;
	private IndexReader indexReader = null;
	
	public final static int NUM_RESULTS = 10000;
	
	public LuceneIndexSearcher (String path) {
		super();
        try {
			dir = FSDirectory.open(new File(path));
	        indexReader = IndexReader.open(dir, true) ;
		} catch (IOException e) {
			throw new GeoARQException(e.getMessage(), e);
		}
	}
	
	public LuceneIndexSearcher (Directory dir) {
		this.dir = dir;
        try {
        	// TODO: remove duplication!
	        indexReader = IndexReader.open(dir, true) ;
		} catch (IOException e) {
			throw new GeoARQException(e.getMessage(), e);
		}		
	}

	@Override
	public Iterator<Document> near(double latitude, double longitude, double miles) {
		DistanceQueryBuilder distanceQueryBuilder = new DistanceQueryBuilder(latitude, longitude, miles, GeoARQ.fLat, GeoARQ.fLong, GeoARQ.fTierPrefix, true);
		DistanceFieldComparatorSource dsort = new DistanceFieldComparatorSource(distanceQueryBuilder.getDistanceFilter());
		Sort sort = new Sort(new SortField("unused", dsort)); // field name is not actually used
		Searcher indexSearcher = new org.apache.lucene.search.IndexSearcher(indexReader);
		Query luceneQuery = new MatchAllDocsQuery();
		ArrayList<Document> hits = new ArrayList<Document>();
		try {
			TopDocs docs = indexSearcher.search(luceneQuery, distanceQueryBuilder.getFilter(), 10, sort);
			Map<Integer,Double> distances = distanceQueryBuilder.getDistanceFilter().getDistances();
			for (ScoreDoc sd : docs.scoreDocs) {
				org.apache.lucene.document.Document luceneDocument = indexSearcher.doc(sd.doc);
				Document doc = new Document();
//				List<Fieldable> fields = luceneDocument.getFields();
//				for (Fieldable field : fields) {
//					doc.set(field.name(), field.stringValue());
//				}
				doc.set(GeoARQ.fScore, String.valueOf(sd.score));
				doc.set(GeoARQ.fDistance, distances.get(sd.doc).toString());
				doc.set(GeoARQ.fURI, luceneDocument.get(GeoARQ.fURI));
				doc.set(GeoARQ.fLat, Double.toString(NumericUtils.prefixCodedToDouble(luceneDocument.get(GeoARQ.fLat))));
				doc.set(GeoARQ.fLong, Double.toString(NumericUtils.prefixCodedToDouble(luceneDocument.get(GeoARQ.fLong))));
				hits.add(doc);				
			}
		} catch (Exception e) {
			throw new GeoARQException (e.getMessage(), e);
		} finally {
			try {
				indexSearcher.close();
			} catch (IOException e) { 
				// TODO 
			}
		}
		
		return hits.iterator();
	}

}
