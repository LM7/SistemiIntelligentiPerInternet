/*
 * @(#)DummyAnalyzer.java	0.9 05/19/10
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
import java.util.Set;
import java.util.prefs.Preferences;

/**
 /**
 * A white space tokenizer that encapsulates Lucene's WhitespaceTokenizer.
 * 
 * @author  Fabio Gasparetti
 * @version 0.9, 05/19/10
 */
public class WhitespaceAnalyzer extends Analyzer {


	/** Builds an analyzer. */
	public WhitespaceAnalyzer() {
	}



	public TokenStream tokenStream(Reader reader) {
		org.apache.lucene.analysis.TokenStream result = new WhitespaceTokenizer(reader);
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

			WhitespaceAnalyzer a = new WhitespaceAnalyzer();
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
