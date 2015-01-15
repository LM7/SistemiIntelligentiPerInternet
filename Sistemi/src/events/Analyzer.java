/*
 * @(#)Analyzer.java	1.0 05/01/07
 *
 * Copyright 2007 Fabio Gasparetti. All rights reserved.
 */
package events;

import java.io.*;
import java.util.ArrayList;

/**
 * An Analyzer builds TokenStreams, which analyze text. It thus represents a
 * policy for extracting index terms from text.
 * 
 * Just a wrapper of Lucene's Analyzer.
 * 
 * @author Fabio Gasparetti
 * @version 1.0, 05/01/07
 */
public abstract class Analyzer {

	/**
	 * Creates a TokenStream which tokenizes all the text in the provided
	 * Reader.
	 * 
	 * @param fieldName
	 * @param reader
	 * @return
	 */
	public abstract TokenStream tokenStream(Reader reader);
	
	/**
	 * Returns an array of tokens from the text in the provided Reader.
	 * 
	 * This default implementation just wraps the streaming process.
	 * Provide an more efficient implementation overriding. 
	 * 
	 * @param reader
	 * @return
	 * @throws IOException
	 */
	public String[] tokenArray(Reader reader) throws IOException {
		TokenStream a1 = tokenStream(reader);
		ArrayList tokens = new ArrayList();
		while (true) {
			String w = a1.next();
			if (w == null) 
				break;
			tokens.add(w);
		}		
		return (String[])tokens.toArray(new String[] {});		
	}
}
