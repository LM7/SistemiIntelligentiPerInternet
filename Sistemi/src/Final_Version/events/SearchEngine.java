/*
 * @(#)UrlNormalizer.java	0.1 12/28/04
 *
 * Copyright 2004 Fabio Gasparetti. All rights reserved.
 */

package Final_Version.events;

import java.io.IOException;
import java.util.Map;



/**
 * Textual search engine.
 */
public interface SearchEngine {
  public void index(String id, String doc) throws IOException;

  /** @todo create customized parsing exceptions */
  public HitsIterator query(String query) throws Exception;

  //public HitsIterator query(String query, int start) throws Exception;

  public void clear() throws IOException;

  public int numDocs() throws IOException;

  /** The TermFreq array is alphabetically sorted. */
  public TermFreq[] docFreqs() throws IOException;
  
  /** Return the maximum number of clauses permitted. */
  public int getMaxClauseCount();
  
  public boolean isIndexed(String id) throws IOException;
  
  public TermWeightVector termFreqs(String id) throws Exception;

  /**
   * List of terms.
   */
  public String[] lexicon() throws Exception;
}