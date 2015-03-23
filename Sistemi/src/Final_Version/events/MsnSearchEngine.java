package Final_Version.events;

import java.io.BufferedReader;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.apache.commons.codec.binary.Base64;
import org.apache.lucene.document.Document;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;


/**
* Note: sometimes results at 50*n and 50*n+1 positions corresponds.
* Around 231 terms are allowed.
*   
* @author robertofenaroli, fabiogasparetti 
*/
public class MsnSearchEngine implements SearchEngine {
	protected String accountKey = null;
	public static final int MAX_MSN_WEB_RESULTS = 200;

	// Roberto's key
	public static final String DEFAULT_ACCOUNT_KEY = "2O/a7594vhe5kcP7jPt8z5fMzZaR3NB01mT7ivLfSrk";

	public MsnSearchEngine() {
		this(DEFAULT_ACCOUNT_KEY);
	}
	
	public MsnSearchEngine(String accountKey) {
		this.accountKey = accountKey;
	}
	
	public void index(String id, String doc) throws IOException {
		throw new UnsupportedOperationException();
	}

	public HitsIterator query(String query) throws Exception {
		List<String[]> results = retrieveResults(0, MAX_MSN_WEB_RESULTS, query);
		MsnHitsIterator hitsIter = new MsnHitsIterator(this, 0, MAX_MSN_WEB_RESULTS, results, query);
		return hitsIter;
	}
	
	List<String[]> retrieveResults(int from, int n, String query) throws IOException, ParseException {
		List<String[]> results = new ArrayList<String[]>();
		query.replaceAll("%27", "'");
		String bingUrl = "https://api.datamarket.azure.com/Bing/Search/Web?Query=%27"+ java.net.URLEncoder.encode(query, "UTF-8") +"%27&Market=%27en-US%27&$format=json&$top="+n+"&$skip="+from;

		byte[] encoding = Base64.encodeBase64((accountKey + ":" + accountKey).getBytes());
		String accountKeyEnc = new String(encoding);

		URL url = new URL(bingUrl);
		URLConnection urlConnection = url.openConnection();
		urlConnection.setRequestProperty("Authorization", String.format("Basic %s", accountKeyEnc));
		urlConnection.setConnectTimeout(30*1000); // 30 secs
		
		JSONParser parser = new JSONParser();
		InputStream is = urlConnection.getInputStream();
		InputStreamReader isr = new InputStreamReader(is);
		BufferedReader br = new BufferedReader(isr);
		Object obj = parser.parse(br);
		JSONObject jsonObject = (JSONObject) obj;
		
		JSONObject jsonRanking = (JSONObject) jsonObject.get("d");
		JSONArray jsonResults = (JSONArray) jsonRanking.get("results");
		Iterator<JSONObject> resultsIt = jsonResults.iterator();
		//System.out.println("Query: " + query);
		int i = 0;
		while (resultsIt.hasNext() && i < n) {
			JSONObject jsonResult = resultsIt.next();
			String[] resultInfo = new String[3]; 
			resultInfo[0] = (String) jsonResult.get("Title");
			resultInfo[1] = (String) jsonResult.get("Description");
			resultInfo[2] = (String) jsonResult.get("Url");
			results.add(resultInfo);
			i++;
		}
		br.close();
		isr.close();
		is.close();
		return results;
		
	}

	public void clear() throws IOException {
		throw new UnsupportedOperationException();
	}

	public int numDocs() throws IOException {
		throw new UnsupportedOperationException();
	}

	public TermFreq[] docFreqs() throws IOException {
		throw new UnsupportedOperationException();
	}

	@Override
	public int getMaxClauseCount() {
		return Integer.MAX_VALUE;
	}

	@Override
	public boolean isIndexed(String id) throws IOException {
		throw new UnsupportedOperationException();
	}

	@Override
	public TermWeightVector termFreqs(String id) throws Exception {
		throw new UnsupportedOperationException();
	}

	@Override
	public String[] lexicon() throws Exception {
		throw new UnsupportedOperationException();
	}
	
	/*
	public static void main(String[] args) throws Exception {
		MsnSearchEngine se = new MsnSearchEngine();
		HitsIterator iterator = se.query("Roma");
		int i = 0;
		while (iterator.hasNext()) {
			Document d = (Document) iterator.next();
			System.out.println(++i+d.get("idse")+" "+d.get("desc")+" "+d.get("title"));
		}
	}
	*/
	
	public String[] getUrls(String query,int numQueryResults) {
		String[] urls = new String[numQueryResults];
		
		try {
			List<String[]> results = retrieveResults(0, numQueryResults, query);
			int i = 0;
			for(String s[]: results){
				//la posizione 2 contiene l'url
				urls[i] = s[2];
				i++;
			}
		} catch (Exception e) {
			System.out.println(e.getMessage());
		}
		
		return urls;
	}
}