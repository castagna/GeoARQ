package org.openjena.geoarq.builder;

import java.io.File;
import java.io.IOException;

import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.document.Field;
import org.apache.lucene.index.CorruptIndexException;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.Term;
import org.apache.lucene.index.IndexWriter.MaxFieldLength;
import org.apache.lucene.search.BooleanQuery;
import org.apache.lucene.search.TermQuery;
import org.apache.lucene.search.BooleanClause.Occur;
import org.apache.lucene.spatial.tier.projections.CartesianTierPlotter;
import org.apache.lucene.spatial.tier.projections.IProjector;
import org.apache.lucene.spatial.tier.projections.SinusoidalProjector;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;
import org.apache.lucene.store.LockObtainFailedException;
import org.apache.lucene.util.NumericUtils;
import org.apache.lucene.util.Version;
import org.openjena.geoarq.Document;
import org.openjena.geoarq.GeoARQ;
import org.openjena.geoarq.GeoARQException;
import org.openjena.geoarq.IndexSearcher;
import org.openjena.geoarq.searcher.IndexSearcherFactory;

public class LuceneIndexBuilder extends IndexBuilderBase {

    private String path = null;
	private Directory dir = null;
    private IndexWriter indexWriter = null ;
	
	public LuceneIndexBuilder(String path) {
		super();
		this.path = path;
		try {
			dir = FSDirectory.open(new File(path));
			makeIndexWriter();
		} catch (Exception e) {
			throw new GeoARQException(e.getMessage(), e);
		}
	}
	
    private void makeIndexWriter() throws CorruptIndexException, LockObtainFailedException, IOException {
    	indexWriter = new IndexWriter(dir, new StandardAnalyzer(Version.LUCENE_30), MaxFieldLength.UNLIMITED) ;
    }

    public Directory getDirectory() {
    	return this.dir;
    }
    
	@Override
	public void add(Document doc) {
		double latitude = 0;
		double longitude = 0;

		org.apache.lucene.document.Document luceneDoc = new org.apache.lucene.document.Document();
		for (String name : doc.getNames()) {
			if ( GeoARQ.fURI.equals(name) ) {
				luceneDoc.add( new Field(name, doc.get(name), Field.Store.YES, Field.Index.NOT_ANALYZED_NO_NORMS) ) ;
			} else if ( GeoARQ.fLat.equals(name) ) {
				latitude = Double.parseDouble(doc.get(name));
				String value = NumericUtils.doubleToPrefixCoded(latitude);
				luceneDoc.add( new Field(name, value, Field.Store.YES, Field.Index.NOT_ANALYZED) ) ;
			} else if ( GeoARQ.fLong.equals(name) ) {
				longitude = Double.parseDouble(doc.get(name));
				String value = NumericUtils.doubleToPrefixCoded(longitude);
				luceneDoc.add( new Field(name, value, Field.Store.YES, Field.Index.NOT_ANALYZED) ) ;
			} else {
				luceneDoc.add( new Field(name, doc.get(name), Field.Store.YES, Field.Index.NO) ) ;
			}
		}
		
		IProjector projector = new SinusoidalProjector();
		int startTier = 5;
		int endTier = 15;
		for (; startTier <= endTier; startTier++) {
			CartesianTierPlotter ctp = new CartesianTierPlotter(startTier, projector, GeoARQ.fTierPrefix);
			double boxId = ctp.getTierBoxId(latitude, longitude);
			luceneDoc.add ( new Field(ctp.getTierFieldName(), NumericUtils.doubleToPrefixCoded(boxId), Field.Store.YES, Field.Index.NOT_ANALYZED_NO_NORMS));
		}

		try {
			indexWriter.addDocument(luceneDoc);
		} catch (Exception e) {
			throw new GeoARQException(e.getMessage(), e);
		}
	}

	@Override
	public void delete(String uri) {
		BooleanQuery query = new BooleanQuery();
		query.add(new TermQuery(new Term(GeoARQ.fURI, uri)) , Occur.MUST);
		try {
			indexWriter.deleteDocuments(query);
		} catch (Exception e) {
			throw new GeoARQException(e.getMessage(), e);
		}
	}

	@Override
	public IndexSearcher getIndexSearcher() {
		// TODO: we should flush only if the index have been changed, see index version 
		flushIndexWriter();
		closeIndexWriter(true);
		return IndexSearcherFactory.create(path);
	}
	
	@Override
	public void close() {
		closeIndexWriter(true) ;
	}

	public void deleteAll() {
		try {
			indexWriter.deleteAll();
		} catch (Exception e) {
			throw new GeoARQException(e.getMessage(), e);
		}
	}
	
    private void closeIndexWriter(boolean optimize) {
        if ( optimize ) 
            flushIndexWriter() ;
        try {
            if ( indexWriter != null ) indexWriter.close();
        } catch (IOException e) { 
        	throw new GeoARQException("closeIndex", e) ; 
        }
        indexWriter = null ;
    }
    
    private void flushIndexWriter() { 
        try { 
        	if ( indexWriter != null ) indexWriter.optimize(); 
        } catch (IOException e) { 
        	throw new GeoARQException("flushWriter", e) ; 
        }
    }

}
