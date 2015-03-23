/*
 * @(#)StopList.java	1.0 05/01/07
 *
 * Copyright 2007 Fabio Gasparetti. All rights reserved.
 */
package Final_Version.events;

import java.io.IOException;
import org.apache.lucene.analysis.*;

/**
 * Filters out keywords shorter than a given threshold
 * 
 * @author  Fabio Gasparetti
 * @version 1.0, 05/01/07
 */
public final class LengthFilter extends org.apache.lucene.analysis.TokenFilter {
  protected int minLength;

  /**
   * Every word with <code>length</code> or less characters is filtered out.  
   * @param in
   * @param length
   */
  public LengthFilter(org.apache.lucene.analysis.TokenStream in, int length) {
    super(in);
    minLength = length;
  }

  public final Token next() throws IOException {
    for (Token token = input.next(); token != null; token = input.next())
      if (token.termText().length() > minLength)
        return token;

    return null;
  }  
}
