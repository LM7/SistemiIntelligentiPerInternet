package events;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.json.simple.parser.ParseException;

public class MsnHitsIterator implements HitsIterator {
	int currIndex = 0;
	int wholeIndex = 0;
	MsnSearchEngine se;
	int requestedResults;
	String query;
	List<String[]> results;
//	resultInfo[0] = (String) jsonResult.get("Title");
//	resultInfo[1] = (String) jsonResult.get("Description");
//	resultInfo[2] = (String) jsonResult.get("Url");
	
	public MsnHitsIterator(MsnSearchEngine se, int currIndex, int requestedResults, List<String[]> results, String query) {
		this.results = results;
		this.se = se;
		this.requestedResults = requestedResults;
		this.currIndex = currIndex;
		this.wholeIndex = currIndex;
		this.query = query;
	}

	@Override
	public boolean hasNext() {
		boolean hasNext = false;
		if (currIndex >= 0) {
			if (currIndex < results.size())
				hasNext = true;
			else 
				hasNext = retrieveNextResults();			
		}
		return hasNext;
	}
	
	//CAMBIAMENTO DEI PARAMETRI ALL'INTERNO DI FIELD
	static Document toDocument(String[] ss) {
		Document d = new Document();
		d.add(new Field("idse", ss[2], Field.Store.YES, Field.Index.TOKENIZED));
		d.add(new Field("desc", ss[1], Field.Store.YES, Field.Index.TOKENIZED));
		d.add(new Field("title", ss[0], Field.Store.YES, Field.Index.TOKENIZED));	
		return d;
	}
	
	protected boolean retrieveNextResults() {
		if (requestedResults == results.size()) {
			wholeIndex += requestedResults;
			try {
				results = se.retrieveResults(wholeIndex, MsnSearchEngine.MAX_MSN_WEB_RESULTS, query);
				currIndex = 0;
			} catch (Exception e) {
				currIndex = -1;
				requestedResults = 0;
				results = null;
			} 
		} else { // retrieved less than available ones
			currIndex = -1;
			requestedResults = 0;
			results = null;
		}
		return currIndex != -1;
	}

	@Override
	public Object next() {
		String[] ss = null;
		if (currIndex >= 0) {
			if (currIndex == results.size()) {
				if (!retrieveNextResults())
					return null;
			}
			ss = results.get(currIndex);			
			currIndex++;
		}
		return ss != null? toDocument(ss) : null;
	}

	@Override
	public void remove() {
		throw new UnsupportedOperationException();
	}

	@Override
	public String getId() {
		String[] ss = null;
		if (currIndex >= 0) {
			if (currIndex == results.size()) {
				if (!retrieveNextResults())
					return null;
			}
			ss = results.get(currIndex);
			return ss[2];
		}
		return null;
	}

	@Override
	public double getScore() {
		throw new UnsupportedOperationException();
	}

	@Override
	public void interrupt() throws IOException {
		throw new UnsupportedOperationException();
	}

	@Override
	public void forEachRemaining(Consumer arg0) {
		// TODO Auto-generated method stub
		
	}

}
