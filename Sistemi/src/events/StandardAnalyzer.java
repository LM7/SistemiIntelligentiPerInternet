/*
 * @(#)StandardAnalyzer.java	1.0 05/01/07
 *
 * Copyright 2007 Fabio Gasparetti. All rights reserved.
 */
package events;

import org.apache.lucene.analysis.*;

import org.apache.lucene.analysis.standard.*;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.StringReader;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;
import java.util.prefs.Preferences;

/**
 /**
 * A text analyzer implementation that encapsulates Lucene's StandardTokenizer.
 * 
 * Filters {@link StandardTokenizer} with {@link StandardFilter}, {@link
 * LowerCaseFilter} and {@link StopFilter}.
 *
 * @author  Fabio Gasparetti
 * @version 1.0, 05/01/07
 */
public class StandardAnalyzer extends Analyzer {
	public static final boolean DEFAULT_PORTER_STEMMER_ON = false;
	public static final boolean DEFAULT_NUMBER_FILTER_ON = true;
	public static final boolean DEFAULT_CASE_LOWER_ON = true;
	public static final boolean DEFAULT_STOP_LIST_ON = true;
	public static final boolean DEFAULT_NO_ALPHA_FILTER_ON = true;
	public static final int DEFAULT_LENGTH_FILTER = 0;
	
	//protected Set stopSet = null;
	
	protected String[] stopSetArray = null;

	protected boolean porterStemmer = DEFAULT_PORTER_STEMMER_ON;

	protected boolean numberFilter = DEFAULT_NUMBER_FILTER_ON;

	protected boolean noAlphaFilter = DEFAULT_NO_ALPHA_FILTER_ON;

	protected boolean lowerCase = DEFAULT_CASE_LOWER_ON;

	protected boolean stopList = DEFAULT_STOP_LIST_ON;

	protected int lengthFilter = DEFAULT_LENGTH_FILTER;

	/** Builds an analyzer. */
	public StandardAnalyzer() {
		
//		stopSet = StopFilter.makeStopSet(StopList.defaultStoplist());
		//stopSet = new HashSet(Arrays.asList(StopList.defaultStoplist()));
		stopSetArray = StopList.defaultStoplist();
	}


	/** Builds an analyzer with the given stop words. */
	public StandardAnalyzer(String[] stopWords) {
		//stopSet = StopFilter.makeStopSet(stopWords);
		//stopSet = new HashSet(Arrays.asList(stopWords));
		stopSetArray = stopWords;
		if (stopSetArray == null) {
			stopSetArray = new String[0];
		}
		
	}

	public void setStopList(String[] words) {
		//stopSet = StopFilter.makeStopSet(words);
		//stopSet = new HashSet(Arrays.asList(words));
		stopSetArray = words;
		if (stopSetArray == null) {
			stopSetArray = new String[0];
		}
	}
	
	/**
	 * Stemming occurs after all other filters 
	 * @param enabled
	 */
	public void enablePorterStemmer(boolean enabled) {
		porterStemmer = enabled;
	}

	public void enableNumberFilter(boolean enabled) {
		numberFilter = enabled;
	}

	public void enableLowerCase(boolean enabled) {
		lowerCase = enabled;
	}

	public void enableStopList(boolean enabled) {
		stopList = enabled;
	}
	
	public void enableNoAlphaFilter(boolean enabled) {
		noAlphaFilter = enabled;
	}

	/**
	 * If <code>len</code> is 0, the filter is disabled.
	 * @param len
	 */
	public void lengthFilter(int len) {
		lengthFilter = len;
	}

	public TokenStream tokenStream(Reader reader) {
		org.apache.lucene.analysis.TokenStream result = new org.apache.lucene.analysis.standard.StandardTokenizer(
				reader);
		// Removes 's from the end of words
		// Removes dots from acronyms
		result = new StandardFilter(result);
		// converts to lower case
		if (lowerCase)
			result = new LowerCaseFilter(result);
		// filters out short tokens
		if (lengthFilter > 0)
			result = new LengthFilter(result, lengthFilter);
		// filters out numbers
		if (numberFilter)
			result = new NumberFilter(result);
		if (noAlphaFilter)
			result = new NoAlphaFilter(result);
		// stop list
		if (stopList)
			result = new StopFilter(result, this.stopSetArray);
		// porter stemmer
		if (porterStemmer)
			result = new PorterStemFilter(result);
		return new TokenStreamLuceneWrapper(result);
	}

//	public void loadPreferences(Preferences prefs) {
//		porterStemmer = prefs.getBoolean("StandardAnalyzerPorterStemmerOn", StandardAnalyzer.DEFAULT_PORTER_STEMMER_ON);
//		numberFilter = prefs.getBoolean("StandardAnalyzerNumberFilterOn", StandardAnalyzer.DEFAULT_NUMBER_FILTER_ON);
//		lowerCase = prefs.getBoolean("StandardAnalyzerCaseLowerOn", StandardAnalyzer.DEFAULT_CASE_LOWER_ON);
//		stopList = prefs.getBoolean("StandardAnalyzerStopListOn", StandardAnalyzer.DEFAULT_STOP_LIST_ON);
//		lengthFilter = prefs.getInt("StandardAnalyzerLengthFilter", StandardAnalyzer.DEFAULT_LENGTH_FILTER);		
//	}

	// for debugging
	public static void main(String[] args) {
		String s1 = null, s2 = null;
		try {
			String s = new String("questa e' una 123 12,5 12.5 12/03/2004 ");
			StringReader sr = new StringReader(s);
			//FileInputStream is = new FileInputStream("1.html");
			BufferedReader reader = new BufferedReader(sr);
			String line;
			StringBuffer buf = new StringBuffer();
			while ((line = reader.readLine()) != null) {
				buf.append(line);
			}
			s1 = buf.toString();

			StandardAnalyzer a = new StandardAnalyzer();
			a.enableLowerCase(true);
			a.enableNumberFilter(true);
			a.enablePorterStemmer(true);
			a.enableStopList(true);
			a.enableNoAlphaFilter(true);
			String[] tkns = a.tokenArray(new java.io.StringReader(s1));
			for (int i = 0; i < tkns.length; i++) {
				System.out.println(tkns[i]);
			}
		} catch (Exception ex) {
			System.out.println(ex.toString());
			ex.printStackTrace();
		}
	}
}