/*
 * @(#)LuceneHitsIterator.java	0.1 12/28/04
 *
 * Copyright 2004 Fabio Gasparetti. All rights reserved.
 */

package events;

import java.util.Iterator;
import java.io.IOException;

/**
 *
 */
public interface HitsIterator extends Iterator {
  public String getId();

  public double getScore();
  
  /**
   * @todo provide interface to access additional meta-data, e.g., title, summary.
   */

  /**
   * Invoke this method to close the IndexReader associated with the searcher.
   */
  public void interrupt() throws IOException;

}
