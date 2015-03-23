/*
 * @(#)TokenStream.java	1.0 05/01/07
 *
 * Copyright 2007 Fabio Gasparetti. All rights reserved.
 */
package Final_Version.events;

import java.io.*;

/**
 * A TokenStream enumerates the sequence of tokens, either from fields of a document or from query text.
 * 
 * Just a wrapper of the Lucene's TokenStream.
 * 
 * @author  Fabio Gasparetti
 * @version 1.0, 05/01/07
 */
public abstract class TokenStream {

	/** Returns the next token in the stream, or null at EOS. */
	public abstract String next() throws IOException;


	/** Resets this stream to the beginning. This is an optional operation, so subclasses may or may not implement this method. Reset() is not needed for the standard indexing process. However, if the Tokens of a TokenStream are intended to be consumed more than once, it is neccessary to implement reset(). */
	public abstract void reset() throws IOException;


	/** Releases resources associated with this stream */
	public abstract void close() throws IOException;
}
