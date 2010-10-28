package dev;

import java.io.IOException;

import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.NumericField;
import org.apache.lucene.index.CorruptIndexException;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexWriter.MaxFieldLength;
import org.apache.lucene.search.BooleanQuery;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.NumericRangeQuery;
import org.apache.lucene.search.Searcher;
import org.apache.lucene.search.TopDocs;
import org.apache.lucene.search.BooleanClause.Occur;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.LockObtainFailedException;
import org.apache.lucene.store.RAMDirectory;
import org.apache.lucene.util.Version;
import org.openjena.geoarq.GeoARQ;

public class RangeQueries {

	public static void main(String[] args) throws CorruptIndexException, LockObtainFailedException, IOException {
		Directory dir = new RAMDirectory();
		IndexWriter indexWriter = new IndexWriter(dir, new StandardAnalyzer(Version.LUCENE_30), MaxFieldLength.UNLIMITED) ;
		Document doc = new Document();
		doc.add( new NumericField(GeoARQ.fLat2).setDoubleValue(-0.5) ) ;
		indexWriter.addDocument(doc);
		
		IndexReader indexReader = indexWriter.getReader();
		Searcher indexSearcher = new IndexSearcher(indexReader);
		
		BooleanQuery query = new BooleanQuery();
		query.add(NumericRangeQuery.newDoubleRange(GeoARQ.fLat2, -2.0, 0.0, true, true), Occur.MUST);
		TopDocs docs = indexSearcher.search(query, null, 1000);
		
		System.out.println(docs.totalHits);
	}

}
