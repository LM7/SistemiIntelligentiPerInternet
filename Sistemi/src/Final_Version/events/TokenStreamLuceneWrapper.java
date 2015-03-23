/*
 * @(#)TokenStream.java	1.0 05/01/07
 *
 * Copyright 2007 Fabio Gasparetti. All rights reserved.
 */
package Final_Version.events;

import java.io.*;
import org.apache.lucene.analysis.*;

/**
 * Just a wrapper of the Lucene's TokenStream.
 * 
 * @author  Fabio Gasparetti
 * @version 1.0, 05/01/07
 */
public class TokenStreamLuceneWrapper extends TokenStream {
	protected org.apache.lucene.analysis.TokenStream tokenStream;
	
	public TokenStreamLuceneWrapper(org.apache.lucene.analysis.TokenStream stream) {
		super();
		this.tokenStream = stream;
	}
	
	/** Returns the next token in the stream, or null at EOS. */
	public String next() throws IOException {
		Token s = tokenStream.next();
		return s != null ? s.termText() : null;
	}


	/** Resets this stream to the beginning. This is an optional operation, so subclasses may or may not implement this method. Reset() is not needed for the standard indexing process. However, if the Tokens of a TokenStream are intended to be consumed more than once, it is neccessary to implement reset(). */
	public void reset() throws IOException {
		/** @todo tokenStream.reset() is not found */
	}


	/** Releases resources associated with this stream */
	public void close() throws IOException {
		tokenStream.close();
	}
}
